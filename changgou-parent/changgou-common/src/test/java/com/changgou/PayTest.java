package com.changgou;

import com.github.wxpay.sdk.WXPayUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PayTest {

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;

    @Test
    public void pay() throws Exception {
        String s = WXPayUtil.generateNonceStr();
        System.out.println("生成随机数："+s);
        HashMap<String, String> map = new HashMap<>();
        map.put("username","名称");
        String s1 = WXPayUtil.mapToXml(map);
        System.out.println("map转换为xml的情况如下："+s1);
        String play = WXPayUtil.generateSignedXml(map, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
        System.out.println("map转xml加盐"+play);

        Map<String, String> map1 = WXPayUtil.xmlToMap(s1);
        System.out.println("xml转化为map");
        for (Map.Entry<String,String> entry : map1.entrySet()){
            System.out.println(entry.getKey()+"::"+entry.getValue());
        }
    }

    @Test
    public void test001() throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("appid ","wx8397f8696b538317");
        hashMap.put("mch_id","1473426802");
//        hashMap.put("out_trade_no","464661564432");
//        hashMap.put("nonce_str",WXPayUtil.generateNonceStr());
        String xml1 = WXPayUtil.mapToXml(hashMap);
        System.out.println("转换测试xml1");

        String xml = WXPayUtil.generateSignedXml(hashMap, partnerkey);
        System.out.println("xml");

    }

    @Test
    public void test0055(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("as ","111");
        System.out.println(hashMap.get("as"));
        System.out.println(hashMap.get("as "));
    }

    @Test
    public void test231(){
        System.out.println(new Date());
    }
}
