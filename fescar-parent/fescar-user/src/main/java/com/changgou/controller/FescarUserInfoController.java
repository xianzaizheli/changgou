package com.changgou.controller;

import com.changgou.service.FescarUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/fescarUserInfo")
public class FescarUserInfoController {

    @Autowired
    private FescarUserInfoService fescarUserInfoService;

    @PostMapping("/pay")
    public String decrMoney(@RequestParam(value = "username") String username, @RequestParam(value = "money") int money) {
        fescarUserInfoService.decrMoney(username, money);
        return "success";
    }
}
