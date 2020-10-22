package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@RabbitListener(queues = {"${mq.pay.queue.order}"})
public class OrderPayMessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * 接收消息
     * @param msg
     */
    @RabbitHandler
    public void consumeMessage(String msg){
        Map<String,String> result = JSON.parseObject(msg, Map.class);
        System.out.println("内容具体为"+result);
        String return_code = result.get("return_code");
        String return_msg = result.get("return_msg");
        System.out.println("监听中");

        //是否成功支付
        if("success".equalsIgnoreCase(return_code)){
            String outTradeNo = result.get("out_trade_no");
            //是否有订单号，有的话就修改
            if(null != outTradeNo){
                try {
                    String timeEnd = result.get("time_end");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date parse = sdf.parse(timeEnd);
                    orderService.updateStatus(outTradeNo , parse,result.get("transaction_id"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                orderService.deleteOrder(outTradeNo);
            }
        }

    }


}
