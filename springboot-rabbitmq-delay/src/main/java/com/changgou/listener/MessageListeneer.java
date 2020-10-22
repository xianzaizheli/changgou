package com.changgou.listener;

import com.changgou.config.QueueConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RabbitListener(queues = QueueConfig.QUEUE_MESSAGE)
public class MessageListeneer {

    @RabbitHandler
    public void msg(@Payload Object msg){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("处理队列...当前时间："+simpleDateFormat.format(new Date()));
        System.out.println("处理队列...收到消息为："+msg);
    }
}
