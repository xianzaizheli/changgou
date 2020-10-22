package com.changgou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisClusterTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test01(){
        redisTemplate.opsForValue().set("sdadasdasda","12331313");
        redisTemplate.opsForValue().set("sddsadaadasdasda","12331313");
        redisTemplate.opsForValue().set("dsadzcxzzceqw","12331313");
        redisTemplate.opsForValue().set("cfdsdfqwwqw","12331313");
    }
}
