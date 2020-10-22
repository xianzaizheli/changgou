package com.changgou.seckill.task;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.IdWorker;
import com.changgou.seckill.config.SeckillMqConfig;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Async
    public void createOrder(){
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        SeckillStatus seckillStatus = (SeckillStatus)redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
        if(null != seckillStatus){
            String id = seckillStatus.getGoodsId() + "";
            String key = seckillStatus.getTime();
            String username = seckillStatus.getUsername();

            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get("Seckill_"+key , id);
            if(seckillGoods == null){
                clearQueue(seckillStatus);
                throw new RuntimeException("对应商品该时间段并未发售或已售罄");
            }

//            redisTemplate.opsForValue().get("");
            Object i = redisTemplate.boundListOps("SeckillGoodsCountList_"+id).rightPop();
            if(i == null){
                clearQueue(seckillStatus);
                throw new RuntimeException("已经售完");
            }

            Long decrement = redisTemplate.opsForValue().decrement("SeckillGoodsCount_" + seckillStatus.getGoodsId(), 1);
            seckillGoods.setStockCount(decrement.intValue());
            if(decrement<=0){
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                redisTemplate.opsForHash().delete("Seckill_"+key,id);

            }else {
                redisTemplate.opsForHash().put("Seckill_"+key,id,seckillGoods);
            }

            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setSellerId(id);
            seckillOrder.setMoney(seckillGoods.getCostPrice());
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");
            //发送订单信息进入延时队列，直接监听转发队列，假如内容还在，那么就取消订单，如果没有东西就没事了
            sendTimerMessage(seckillStatus);
            //将秒杀订单存入redis中
            redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);

            seckillStatus.setOrderId(seckillOrder.getId()+"");
            seckillStatus.setStatus(2);
            seckillStatus.setMoney(Float.parseFloat(seckillOrder.getMoney()));
            //抢单成功，更新抢单状态
            redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);
        }


    }

    /**
     * 清理排队信息
     * @param seckillStatus
     */
    public void clearQueue(SeckillStatus seckillStatus){
        //清除用户重复登录标志
        redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
        //清除用户购买商品状态？
        redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());

    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    public void sendTimerMessage(SeckillStatus seckillStatus){

        rabbitTemplate.convertAndSend(env.getProperty("mq.pay.exchange.seckillOrder"),env.getProperty("mq.pay.routing.seckillOrderTimerDelayKey"), JSON.toJSONString(seckillStatus) , new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("10000");
                return message;
            }
        });
    }
}
