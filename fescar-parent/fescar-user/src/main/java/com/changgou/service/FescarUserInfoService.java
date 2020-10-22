package com.changgou.service;

public interface FescarUserInfoService {
    /**
     * 账户金额递减
     * @param username
     * @param money
     */
    void decrMoney(String username ,int money);
}
