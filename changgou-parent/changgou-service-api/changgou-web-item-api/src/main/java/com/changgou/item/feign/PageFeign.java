package com.changgou.item.feign;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "item")
@RequestMapping("/page")
public interface PageFeign {

    /**
     * 根据spuId生成静态页
     * @param id
     * @return
     */
    @RequestMapping("/createHtml/{id}")
    public Result createPageHtml(@PathVariable(value = "id")Long id);
}
