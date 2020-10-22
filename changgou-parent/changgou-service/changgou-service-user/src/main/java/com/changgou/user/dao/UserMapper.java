package com.changgou.user.dao;
import com.changgou.user.pojo.User;
import tk.mybatis.mapper.common.Mapper;

import javax.jws.soap.SOAPBinding;

/****
 * @Author:admin
 * @Description:User的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface UserMapper extends Mapper<User> {

    /**
     * 增加积分
     */
    public void addUserPoints(User user);
}
