package com.changgou.service.impl;

import com.changgou.dao.FescarOrderInfoMapper;
import com.changgou.feign.ItemInfoFeign;
import com.changgou.pojo.OrderInfo;
import com.changgou.service.FescarOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FescarOrderInfoServiceImpl implements FescarOrderInfoService {
    @Autowired
    private FescarOrderInfoMapper fescarOrderInfoMapper;

    @Autowired
    private ItemInfoFeign itemInfoFeign;

    @Override
    public void add(String username, int id, int count) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setMessage("生成订单");
        orderInfo.setMoney(50);
        int icount = fescarOrderInfoMapper.insertSelective(orderInfo);
        System.out.println("添加订单受影响行数："+icount);
        //递减库存
        itemInfoFeign.decrCount(id, count);
    }
}
