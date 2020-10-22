package com.changgou.service.impl;

import com.changgou.dao.FescarItemInfoMapper;
import com.changgou.pojo.ItemInfo;
import com.changgou.service.FescarItemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FescarItemInfoServiceImpl implements FescarItemInfoService {

    @Autowired
    private FescarItemInfoMapper fescarItemInfoMapper;

    @Override
    public void decrCount(int id, int count) {
        ItemInfo itemInfo = fescarItemInfoMapper.selectByPrimaryKey(id);
        itemInfo.setCount(itemInfo.getCount() - count);
        int dcount = fescarItemInfoMapper.updateByPrimaryKey(itemInfo);
        System.out.println("库存递减受影响行数："+dcount);
        //异常准备回滚
//        int q = 10/0;
    }
}
