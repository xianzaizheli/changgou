package com.changgou.user.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

//@EnableRabbit
@Configuration
public class PayRabbitConfig {

    @Autowired
    private Environment env;

    @Bean(name = "orderExchange")
    public DirectExchange orderExchange(){
//        System.out.println("注入路由："+env.getProperty("mq.pay.exchange.order"));
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"),true,false);
    }

    @Bean(name = "orderQueue")
    public Queue orderQueue(){
//        System.out.println("注入队列："+env.getProperty("mq.pay.queue.order"));
        return new Queue(env.getProperty("mq.pay.queue.order"),true);
    }

    @Bean(name = "orderBinding")
    public Binding orderBinding(){
//        System.out.println("注入绑定"+env.getProperty("mq.pay.routing.key"));
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(env.getProperty("mq.pay.routing.key"));
    }

    @Bean(name = "seckillOrderQueue")
    public Queue seckillOrderQueue(){
        return new Queue(env.getProperty("mq.pay.queue.seckillOrder"),true);
    }

    @Bean(name = "seckillOrderExchange")
    public DirectExchange seckillOrderExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillOrder"),true,false);
    }

    @Bean(name = "seckillOrderBinding")
    public Binding seckillOrderBinding(){
        return BindingBuilder.bind(seckillOrderQueue()).to(seckillOrderExchange()).with(env.getProperty("mq.pay.routing.seckillOrderKey"));
    }

}
