package com.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@Component
@RabbitListener(queues = "${mq.pay.queue.seckillOrder}")
public class SeckillOrderPayResultListener {

    @Autowired
    private Environment env;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener
    public void consumer(@Payload String message) throws ParseException {
        Map<String,String> map = JSON.parseObject(message, Map.class);
        if(map != null){
            String returnCode = map.get("return_code");
            if("SUCCESS".equalsIgnoreCase(returnCode)){
//                String returnMsg = map.get("return_msg");
                String attach = map.get("attach");
                Map<String,String> attachMap = JSON.parseObject(attach, Map.class);
                String username = attachMap.get("username");

                if("SUCCESS".equalsIgnoreCase(returnCode)){
                    SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
                    redisTemplate.boundHashOps("SeckillOrder").delete(username);

                    seckillOrder.setStatus("1");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date timeEnd = simpleDateFormat.parse(map.get("time_end"));
                    seckillOrder.setPayTime(timeEnd);
                    seckillOrderMapper.insertSelective(seckillOrder);

                    //5、清理排队标志
                    redisTemplate.boundHashOps("UserQueueCount").delete(username);
                    //6、清理抢单标志
                    redisTemplate.boundHashOps("UserQueueStatus").delete(username);
                }else {
                    seckillOrderService.closeOrder(username);
                }
            }
        }
    }
}
