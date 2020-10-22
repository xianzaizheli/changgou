package com.changgou.controller;

import com.changgou.service.FescarOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fescarOrder")
@CrossOrigin
public class FescarOrderInfoController {

    @Autowired
    private FescarOrderInfoService fescarOrderInfoService;

    @PostMapping("/add")
    public String add(@RequestParam(value = "username") String username, @RequestParam(value = "id") int id,@RequestParam(value = "count") int count){
        fescarOrderInfoService.add(username, id, count);
        return "success";
    }
}
