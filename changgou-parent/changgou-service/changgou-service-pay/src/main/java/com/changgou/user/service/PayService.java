package com.changgou.user.service;

import java.util.Map;

public interface PayService {

    /**
     * 关闭订单
     * @param orderId
     * @return
     * @throws Exception
     */
    public Map<String,String> closePay(Long orderId) throws Exception;

    /**
     * 调用微信客户端，生成预付款订单
     * @param parameter    包含商户订单号,标价金额
     * @return
     */
    public Map createNative(Map<String,String> parameter);

    /**
     * 查询订单情况
     * @param outTrandNo    商户订单号
     * @return
     */
    public Map queryPayStatus(String outTrandNo);
}
