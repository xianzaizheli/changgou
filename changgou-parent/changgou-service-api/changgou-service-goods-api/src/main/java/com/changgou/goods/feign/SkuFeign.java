package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Sku;
import com.changgou.order.pojo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/sku")
@FeignClient(name = "goods")
public interface SkuFeign {

    /**
     * 回滚库存
     * @param orderItem
     * @return
     */
    @PostMapping("/roll/count")
    public Result rollBackCount(@RequestBody OrderItem orderItem);

    /**
     * 库存递减
     * @param orderItem
     * @return
     */
    @PostMapping("/decr/count")
    public Result decrCount(@RequestBody OrderItem orderItem);

    /***
     * 多条件搜索品牌数据
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);

    /***
     * 根据ID查询Sku数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(name = "id") String id);

}
