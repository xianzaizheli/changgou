package com.changgou.item.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.item.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;

    @RequestMapping("/createHtml/{id}")
    public Result createPageHtml(@PathVariable(value = "id")Long id){
        pageService.createPageHtml(id);
        return new Result(true , StatusCode.OK , "创建静态页成功");
    }

}
