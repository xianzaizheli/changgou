package com.changgou.user.service.impl;

import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;

@Service
public class PayTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void test01(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg","这是第一个消息");
        map.put("data", Arrays.asList("hello",1.23d,6,true));
        rabbitTemplate.convertAndSend("exchange.order","queue.order",map);
    }
}
