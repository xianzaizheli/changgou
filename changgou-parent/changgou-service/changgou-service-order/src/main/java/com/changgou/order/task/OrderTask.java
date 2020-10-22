package com.changgou.order.task;

import com.changgou.entity.Result;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderService;
import com.changgou.pay.feign.PayFeign;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableScheduling
//@SpringBootTest
//@RunWith(SpringRunner.class)
public class OrderTask {

    @Autowired
    private PayFeign payFeign;

    @Autowired
    private OrderService orderService;

    @Resource
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/5 * * * * *")
    public void orderCheck(){
        Boolean order = redisTemplate.hasKey("Order");
        if(order){
            Set<String> orderIds = redisTemplate.boundHashOps("Order").keys();
            for(String o : orderIds){
                Result<Map<String, String>> mapResult = payFeign.queryPayStatus(o);
                Map<String, String> data = mapResult.getData();
                if(StringUtils.isEmpty(data.get("return_code")) || data.get("return_code").equalsIgnoreCase("fail")){
                    orderService.deleteOrder(o);
                }else {
                    try {
                        String timeEnd = data.get("time_end");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date parse = sdf.parse(timeEnd);
                        orderService.updateStatus(o , parse,data.get("transaction_id"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Test
    public void test01(){

        Boolean order = redisTemplate.hasKey("Cart_changgou");
        if(order){
            //获取全内容，结果为{100000030326=com.changgou.order.pojo.OrderItem@408d12fc, 100000006163=com.changgou.order.pojo.OrderItem@69d61a6f}
            Map<String, OrderItem> cart_changgou = redisTemplate.opsForHash().entries("Cart_changgou");
            System.out.println(cart_changgou);
            //获取值，结果为[com.changgou.order.pojo.OrderItem@69d1ad64, com.changgou.order.pojo.OrderItem@458b4487]
            List<OrderItem> cart_changgou1 = redisTemplate.opsForHash().values("Cart_changgou");
            System.out.println(cart_changgou1);
            Set<String> orderIds = redisTemplate.boundHashOps("Cart_changgou").keys();
            for(String o : orderIds){
                //输出结果为
                //100000030326
                //100000006163
                System.out.println(o);
            }
        }
    }
}
