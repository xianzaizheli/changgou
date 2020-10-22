package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

public interface CartService {

    /**
     * 添加购物车
     * @param num
     * @param id
     * @param username
     */
    void add(Integer num, String id, String username);

    /**
     * 查询购物车详情
     * @param username
     * @return
     */
    List<OrderItem> list(String username);
}
