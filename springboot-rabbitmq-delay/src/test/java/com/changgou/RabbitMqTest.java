package com.changgou;

import com.changgou.config.QueueConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitMqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("测试用方法时间："+simpleDateFormat.format(new Date()));
//        rabbitTemplate.convertAndSend(QueueConfig.DLX_EXCHANGE,QueueConfig.QUEUE_MESSAGE,"发送的消息");
        rabbitTemplate.convertAndSend(QueueConfig.DLX_EXCHANGE, QueueConfig.DELAY_QUEUE_KEY, "你真棒", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("10000");  //10s

                return message;
            }
        });

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}
