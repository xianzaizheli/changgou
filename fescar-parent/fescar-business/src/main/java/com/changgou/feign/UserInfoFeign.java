package com.changgou.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fescar-user",url = "127.0.0.1:18084")
@RequestMapping("/fescarUserInfo")
public interface UserInfoFeign {

    @PostMapping("/pay")
    public String decrMoney(@RequestParam(value = "username") String username, @RequestParam(value = "money") int money) ;

}
