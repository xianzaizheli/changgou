package com.changgou.redis;

import com.changgou.seckill.pojo.SeckillGoods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void test01(){
        redisTemplate.opsForValue().increment("测试",2);
        Object a = redisTemplate.opsForValue().increment("测试",0);
        System.out.println(a);
    }
}
