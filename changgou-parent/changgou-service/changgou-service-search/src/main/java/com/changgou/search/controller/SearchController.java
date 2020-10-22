package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/import")
    public Result importSku(){
        searchService.importSku();
        return new Result(true , StatusCode.OK ,"导入数据成功");
    }

    @PostMapping
    public Result<Map> search(@RequestBody(required = false)Map<String,String> map){
        Map search = searchService.search(map);
        return new Result<Map>(true ,StatusCode.OK ,"查询成功",search);
    }

    @GetMapping
    public Map search1(@RequestParam(required = false)Map<String,String> map){
        Map search = searchService.search(map);
        return search;
    }
}
