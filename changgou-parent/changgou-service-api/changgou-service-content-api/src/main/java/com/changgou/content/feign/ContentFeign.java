package com.changgou.content.feign;

import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@FeignClient(name="content")
@RequestMapping("/content")
public interface ContentFeign {
    /*
    根据分类的ID 获取到广告列表
     */
    @GetMapping("/list/category/{id}")
    Result<List<Content>> findByCategory(@PathVariable(name="id") Long id);
}