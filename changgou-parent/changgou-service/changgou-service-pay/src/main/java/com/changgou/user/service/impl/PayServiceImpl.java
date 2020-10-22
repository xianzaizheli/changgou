package com.changgou.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.HttpClient;
import com.changgou.entity.IdWorker;
import com.changgou.user.service.PayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;

    /**
     * 关闭支付
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> closePay(Long orderId) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appid",appid);
        hashMap.put("mch_id",partner);
        hashMap.put("out_trade_no",String.valueOf(orderId));
        hashMap.put("nonce_str",WXPayUtil.generateNonceStr());

        String xml = WXPayUtil.generateSignedXml(hashMap, partnerkey);

        String url = "https://api.mch.weixin.qq.com/pay/closeorder";
        HttpClient httpClient = new HttpClient(url);
        httpClient.setHttps(true);
        httpClient.setXmlParam(xml);
        httpClient.post();

        String content = httpClient.getContent();
        Map<String, String> map = WXPayUtil.xmlToMap(content);
        return map;
    }

    @Override
    public Map createNative(Map<String,String> parameter) {
    try {
        //1.封装相关参数
        HashMap<String, String> hashMap = new HashMap<>();
        //设置公众账号ID
        hashMap.put("appid",appid);
        //商户号
        hashMap.put("mch_id",partner);
        hashMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //签名，这里真的需要吗？
        //hashMap.put("sign","playTest");
        hashMap.put("body","购买某商品花了某钱");
        hashMap.put("out_trade_no",parameter.get("outTrandNo"));
        hashMap.put("total_fee",parameter.get("totalFee"));
        //这里是我们的主机ip地址，我变化看看
        hashMap.put("spbill_create_ip","192.168.101.86");
        hashMap.put("notify_url",notifyurl);
        hashMap.put("trade_type","NATIVE");

        HashMap<String, String> attach = new HashMap<>();
        attach.put("username",parameter.get("username"));
        attach.put("queue",parameter.get("queue"));
        attach.put("exchange",parameter.get("exchange"));
        attach.put("routingkey",parameter.get("routingkey"));

        //追加参数，你发送过去原样返回
        hashMap.put("attach", JSON.toJSONString(attach));

        //2.加密xml
        String xml = WXPayUtil.generateSignedXml(hashMap, partnerkey);

        //3.发送请求
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setXmlParam(xml);
        httpClient.setHttps(true);
        httpClient.post();

        //4.获取返回内容
        String content = httpClient.getContent();
        Map<String, String> map = WXPayUtil.xmlToMap(content);
        System.out.println("获取到的内容"+map);

        //5.将成交结果的url返回回来
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("codeUrl", map.get("code_url"));
        dataMap.put("outTrandNo",parameter.get("outTrandNo"));
        dataMap.put("totalFee",parameter.get("totalFee"));
        return dataMap;
    } catch (Exception e) {
        e.printStackTrace();
    }
        return null;
    }

    @Override
    public Map queryPayStatus(String outTrandNo) {
        try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("appid",appid);
            hashMap.put("mch_id",partner);
            hashMap.put("out_trade_no",outTrandNo);
            hashMap.put("nonce_str",WXPayUtil.generateNonceStr());

            //设置签名(不做,转换的时候自动添加签名)
            //3.转成XML 字符串 自动签名
            String xml = WXPayUtil.generateSignedXml(hashMap, partnerkey);
            System.out.println("xml");

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xml);
            httpClient.post();

            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            return map;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
