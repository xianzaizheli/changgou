package com.changgou.controller;

import com.changgou.service.FescarBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/business")
@RestController
@CrossOrigin
public class FescarBusinessController {

    @Autowired
    private FescarBusinessService fescarBusinessService;

    @GetMapping("/addOrder")
    public String order(){
        String username = "张三";
        int id = 1;
        int count = 5;
        //下单
        fescarBusinessService.add(username,id,count);
        return "success";
    }
}
