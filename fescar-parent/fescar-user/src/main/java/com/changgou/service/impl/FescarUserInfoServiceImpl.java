package com.changgou.service.impl;

import com.changgou.dao.FescarUserInfoMapper;
import com.changgou.pojo.UserInfo;
import com.changgou.service.FescarUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class FescarUserInfoServiceImpl implements FescarUserInfoService {
    @Autowired
    private FescarUserInfoMapper fescarUserInfoMapper;

    @Override
    public void decrMoney(String username, int money) {
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name",username);
        List<UserInfo> userInfos = fescarUserInfoMapper.selectByExample(example);
        UserInfo userInfo = userInfos.get(0);
        userInfo.setMoney(userInfo.getMoney()-money);
        int count = fescarUserInfoMapper.updateByPrimaryKey(userInfo);
        System.out.println("用户账号金额减少受影响行数："+count);
//        int q = 10/ 0;
    }
}
