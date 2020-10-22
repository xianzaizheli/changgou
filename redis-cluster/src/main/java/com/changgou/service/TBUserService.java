package com.changgou.service;

import com.changgou.pojo.User;

public interface TBUserService {

    /**
     * 根据用户名查询用户的信息
     * @param username
     * @return
     */
    public User findOne(String username);
}
