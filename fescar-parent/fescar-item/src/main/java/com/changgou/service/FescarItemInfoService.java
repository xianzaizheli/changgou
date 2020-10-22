package com.changgou.service;

public interface FescarItemInfoService {
    /**
     * 库存递减
     * @param id
     * @param count
     */
    void decrCount(int id ,int count);
}
