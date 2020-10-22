package com.changgou.goods.service;

import com.changgou.goods.pojo.Sku;
import com.changgou.order.pojo.OrderItem;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Sku业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SkuService {

    /**
     * 回滚库存
     * @param orderItem
     */
    void rollBackCount(OrderItem orderItem);

    /**
     * 减少库存
     * @param orderItem
     * @return
     */
    void decrCount(OrderItem orderItem);

    /**
     * 根据上架状态查询数据
     * @param status
     * @return
     */
    List<Sku> findByStatus(String status);

    /***
     * Sku多条件分页查询
     * @param sku
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(Sku sku, int page, int size);

    /***
     * Sku分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(int page, int size);

    /***
     * Sku多条件搜索方法
     * @param sku
     * @return
     */
    List<Sku> findList(Sku sku);

    /***
     * 删除Sku
     * @param id
     */
    void delete(String id);

    /***
     * 修改Sku数据
     * @param sku
     */
    void update(Sku sku);

    /***
     * 新增Sku
     * @param sku
     */
    void add(Sku sku);

    /**
     * 根据ID查询Sku
     * @param id
     * @return
     */
     Sku findById(String id);

    /***
     * 查询所有Sku
     * @return
     */
    List<Sku> findAll();
}
