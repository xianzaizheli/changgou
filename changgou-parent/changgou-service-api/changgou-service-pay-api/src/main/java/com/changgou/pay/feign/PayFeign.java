package com.changgou.pay.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "pay")
@RequestMapping("/pay")
public interface PayFeign {
    @GetMapping("/query/status")
    public Result<Map<String,String>> queryPayStatus(@RequestParam String outTrandNo);

    @GetMapping("/close/pay")
    public Result closePay(@RequestParam Long id);

}
