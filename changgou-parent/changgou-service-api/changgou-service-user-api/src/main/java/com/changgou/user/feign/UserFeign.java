package com.changgou.user.feign;

import com.changgou.entity.Result;
import com.changgou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;

@FeignClient(name = "user")
@RequestMapping("/user")
public interface UserFeign {

    @GetMapping("/load/{id}")
    public Result<User> findByUsername(@PathVariable(name = "id") String id) ;

    @GetMapping(value = "/points/add")
    public Result addUserPoints(@RequestParam(value = "points") Integer points);
}
