package com.changgou.service.impl;


import com.changgou.dao.FescarBusinessMapper;
import com.changgou.feign.OrderInfoFeign;
import com.changgou.feign.UserInfoFeign;
import com.changgou.pojo.LogInfo;
import com.changgou.service.FescarBusinessService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FescarBusinessServiceImpl implements FescarBusinessService {
    @Autowired
    private FescarBusinessMapper fescarBusinessMapper;

    @Autowired
    private OrderInfoFeign orderInfoFeign;

    @Autowired
    private UserInfoFeign userInfoFeign;

    /**
     * 下单
     * @param username
     * @param id
     * @param count
     */
    @GlobalTransactional
    @Override
    public void add(String username, int id, int count) {
        //添加订单日志
        LogInfo logInfo = new LogInfo();
        logInfo.setContent("添加订单数据---"+new Date());
        logInfo.setCreatetime(new Date());
        int logCount = fescarBusinessMapper.insertSelective(logInfo);
        System.out.println("添加日志受影响行数："+logCount);

        //添加订单
        orderInfoFeign.add(username, id, count);
        //用户账户余额递减
        userInfoFeign.decrMoney(username,count);
    }
}
