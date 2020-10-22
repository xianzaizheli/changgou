package com.changgou.controller;


import com.changgou.service.FescarItemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/fescarItemInfo")
public class FescarItemInfoController {

    @Autowired
    private FescarItemInfoService fescarItemInfoService;

    @PostMapping("/decrCount")
    public String decrCount(@RequestParam(value = "id")int id ,@RequestParam(value = "count")int count){
        fescarItemInfoService.decrCount(id, count);
        return "success";
    }
}
