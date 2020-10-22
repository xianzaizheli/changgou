package com.changgou.controller;


import com.changgou.pojo.User;
import com.changgou.service.TBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/users")
public class TBUserController {
    @Autowired
    private TBUserService tbUserService;

    @RequestMapping("/test/{username}")
    public User findOne(@PathVariable("username")String username){
        return tbUserService.findOne(username);
    }
}
