package com.changgou.service.impl;

import com.changgou.mapper.TBUserMapper;
import com.changgou.pojo.User;
import com.changgou.service.TBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TBUserServiceImpl implements TBUserService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TBUserMapper tbUserMapper;

    @Override
    public User findOne(String username) {
        System.out.println("名字为："+username);
        User user = (User) redisTemplate.opsForValue().get(username);
        if(redisTemplate.hasKey(username)){
            return user;
        }else {
            User one = tbUserMapper.findOne(username);
            System.out.println("查数据库啊");
            redisTemplate.opsForValue().set(username,one);
            if(one == null){
                redisTemplate.expire(username,30, TimeUnit.SECONDS);
            }
            return one;
        }
    }
}
