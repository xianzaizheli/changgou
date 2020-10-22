package com.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.pay.feign.PayFeign;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听不支付的吊人，如果支付了的话redis应该没值了
 */
@Component
@RabbitListener(queues = "queue.seckillOrderTimer")
public class SeckillOrderNotPayListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PayFeign payFeign;

    @RabbitListener
    public void consumer(@Payload String message){
        SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);
        String username = seckillStatus.getUsername();
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
        //判断订单是否已经存在
        if(null != seckillOrder){
            System.out.println("开始回滚");
            Result result = payFeign.closePay(seckillOrder.getId());
            Map<String,String> data = (Map<String,String>)result.getData();
            if(data != null && data.get("return_code").equalsIgnoreCase("SUCCESS") && data.get("return_msg").equalsIgnoreCase("OK")){
                seckillOrderService.closeOrder(username);
            }
        }
    }
}
