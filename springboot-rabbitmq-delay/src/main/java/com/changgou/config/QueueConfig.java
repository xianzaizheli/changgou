package com.changgou.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    /**
     * 短信发送队列
     */
    public static final String QUEUE_MESSAGE ="queue.message";

    /**
     * 正常队列的key值
     */
    public static final String NORMAL_QUEUE_KEY = "queue.normal.key";

    /**
     * 延时/死信队列的key值
     */
    public static final String DELAY_QUEUE_KEY = "queue.delay.key";

    /**
     * 交换机
     */
    public static final String DLX_EXCHANGE ="dlx.exchange";

    /**
     * 短信发送队列   延迟缓存，死信队列
     */
    public static final String QUEUE_MESSAGE_DELAY="queue.message.delay";

    /**
     * 短信发送队列
     * @return
     */
    @Bean
    public Queue messageQueue(){
        return new Queue(QUEUE_MESSAGE);
    }

    /**
     * 设置队列过期之后会怎么处理
     * @return
     */
    @Bean
    public Queue delayMessageQueue(){
        return QueueBuilder.durable(QUEUE_MESSAGE_DELAY)
                //消息超时进入死信队列，绑定死信队列交换机
                .withArgument("x-dead-letter-exchange",DLX_EXCHANGE)
                //绑定制定的routing-key
                .withArgument("x-dead-letter-routing-key",NORMAL_QUEUE_KEY)
                .build();
    }

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(DLX_EXCHANGE);
    }

    /**
     * 交换机与队列绑定
     * @param messageQueue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding basicBinding(Queue messageQueue ,DirectExchange directExchange){
        return BindingBuilder.bind(messageQueue)
                .to(directExchange)
                .with(NORMAL_QUEUE_KEY);
    }

    /**
     * 交换机与队列绑定
     * @param delayMessageQueue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding normalBasicBinding(Queue delayMessageQueue ,DirectExchange directExchange){
        return BindingBuilder.bind(delayMessageQueue)
                .to(directExchange)
                .with(DELAY_QUEUE_KEY);
    }

}
