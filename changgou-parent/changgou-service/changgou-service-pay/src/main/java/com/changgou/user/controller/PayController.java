package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.user.service.PayService;
import com.changgou.user.service.impl.PayTest;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
@CrossOrigin
public class PayController {
    @Autowired
    private PayService payService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.pay.exchange.order}")
    String orderExchange;

    @Value("${mq.pay.queue.order}")
    String orderQueue;

    @Value("${mq.pay.routing.key}")
    String orderKey;

    @Value("${mq.pay.exchange.seckillOrder}")
    String seckillOrderExchange;

    @Value("${mq.pay.queue.seckillOrder}")
    String seckillOrderQueue;

    @Value("${mq.pay.routing.seckillOrderKey}")
    String seckillOrderKey;

    @Autowired
    private PayTest payTest;

    @GetMapping
    public void rabConfTest(){
        payTest.test01();
    }

    @GetMapping("/close/pay")
    public Result closePay(Long id){
        try {
            Map<String, String> map = payService.closePay(id);
            return new Result(true,StatusCode.OK,"关闭订单成功",map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(true,StatusCode.OK,"关闭订单失败");
    }

    @RequestMapping(value = "/notify/url")
    public String notifyUrl(HttpServletRequest request){
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes =new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1){
                baos.write(bytes , 0 ,len);
            }
            baos.close();
            baos.close();

            //将支付回调数据转换成xml字符串
            String result = new String(baos.toByteArray() , "UTF-8");
            //将xml字符串转换成Map结构
            Map<String, String> map = WXPayUtil.xmlToMap(result);

            String attach = map.get("attach");
            Map<String,String> attachMap = JSON.parseObject(attach, Map.class);
            System.out.println("打印一下看看:"+attachMap);

            //将微信客户端执行结果存入rabbitMq中去
            if(seckillOrderQueue.equalsIgnoreCase(attachMap.get("queue"))){
                rabbitTemplate.convertAndSend(seckillOrderExchange , seckillOrderKey , JSON.toJSONString(map) );
            }else {
                rabbitTemplate.convertAndSend(orderExchange , orderKey , JSON.toJSONString(map));
            }

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("return_code","SUCCESS");
            hashMap.put("return_msg","OK");

            return WXPayUtil.mapToXml(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/create/native")
    public Result<Map<String,String>> createNative(@RequestParam Map<String,String> parameter){
//        @RequestParam String outTrandNo,@RequestParam String totalFee
        Map<String,String> map = payService.createNative(parameter);
        return new Result<Map<String,String>>(true , StatusCode.OK , "生成订单成功" , map);
    }

    @GetMapping("/query/status")
    public Result<Map<String,String>> queryPayStatus(String outTrandNo){
        Map<String,String> map = payService.queryPayStatus(outTrandNo);
        return new Result<Map<String,String>>(true , StatusCode.OK , "查看订单成功" , map);
    }
}
