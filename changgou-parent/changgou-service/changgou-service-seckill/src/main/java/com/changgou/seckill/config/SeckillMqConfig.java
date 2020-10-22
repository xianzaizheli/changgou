package com.changgou.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SeckillMqConfig {

    @Value("${mq.pay.exchange.seckillOrder}")
    private String seckillOrderExchange;

    @Value("${mq.pay.queue.seckillOrder}")
    private String seckillOrderQueue;

    @Value("${mq.pay.queue.seckillOrderTimer}")
    private String seckillOrderTimerQueue;

    @Value("${mq.pay.queue.seckillOrderTimerDelay}")
    private String seckillOrderTimerDelayQueue;

    @Value("${mq.pay.routing.seckillOrderKey}")
    private String seckillOrderKey;

    @Value("${mq.pay.routing.seckillOrderTimerDelayKey}")
    private String seckillOrderTimerDelayKey;

    @Value("${mq.pay.routing.seckillOrderTimerKey}")
    private String seckillOrderTimerKey;

    @Bean
    public Queue seckillOrderQueue(){
        return new Queue(seckillOrderQueue);
    }

    @Bean
    public Queue seckillOrderTimerQueue(){
        return new Queue(seckillOrderTimerQueue);
    }

    @Bean
    public Queue seckillOrderTimerDelay(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("x-dead-letter-exchange",seckillOrderExchange);
        hashMap.put("x-dead-letter-routing-key",seckillOrderTimerKey);
        return new Queue(seckillOrderTimerDelayQueue,true,true,false,hashMap);
    }

    @Bean
    public DirectExchange seckillOrderExchange(){
        return new DirectExchange(seckillOrderExchange);
    }

    @Bean
    public Binding seckillOrderTimerDelayBinding(){
        return new Binding(seckillOrderTimerDelayQueue, Binding.DestinationType.QUEUE ,seckillOrderExchange ,seckillOrderTimerDelayKey , null);
    }

    @Bean
    public Binding seckillOrderBinding(){
        return BindingBuilder.bind(seckillOrderQueue())
                .to(seckillOrderExchange())
                .with(seckillOrderKey);
    }

    @Bean
    public Binding seckillOrderTimerBinding(){
        return BindingBuilder.bind(seckillOrderTimerQueue())
                .to(seckillOrderExchange())
                .with(seckillOrderTimerKey);
    }
}
