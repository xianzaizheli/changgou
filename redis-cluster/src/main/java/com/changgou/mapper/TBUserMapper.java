package com.changgou.mapper;

import com.changgou.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TBUserMapper {

    /**
     * 根据用户名查询用户的信息
     * @param username
     * @return
     */
    public User findOne(String username);
}
