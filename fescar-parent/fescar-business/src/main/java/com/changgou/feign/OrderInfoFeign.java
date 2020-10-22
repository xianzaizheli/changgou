package com.changgou.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fescar-order",url = "127.0.0.1:18083")
@RequestMapping("/fescarOrder")
public interface OrderInfoFeign {

    @PostMapping("/add")
    public String add(@RequestParam(value = "username") String username, @RequestParam(value = "id") int id, @RequestParam(value = "count") int count);

}
