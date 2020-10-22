package com.changgou.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fescart-item",url = "127.0.0.1:18082")
@RequestMapping("/fescarItemInfo")
public interface ItemInfoFeign {

    @PostMapping("/decrCount")
    public String decrCount(@RequestParam(value = "id") int id, @RequestParam(value = "count") int count);

}
