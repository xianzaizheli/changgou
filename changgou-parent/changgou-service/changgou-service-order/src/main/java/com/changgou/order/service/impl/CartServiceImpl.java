package com.changgou.order.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(Integer num, String id, String username) {
        if(num <= 0){
            redisTemplate.opsForHash().delete("Cart_"+username , id);
            //redisTemplate.boundHashOps("Cart_"+username).delete(id);
            return;
        }

        //查询SKU
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        if(sku != null){
            Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
            Spu spu = spuResult.getData();
            OrderItem orderItem = skuOrderItem(sku, spu, num);
            redisTemplate.opsForHash().put("Cart_"+username,id,orderItem);
        }

    }

    @Override
    public List<OrderItem> list(String username) {
        if(StringUtils.isEmpty(username)){
            return null;
        }
        List<OrderItem> orderItems = redisTemplate.opsForHash().values("Cart_" + username);
//        List<OrderItem> values = redisTemplate.boundHashOps("Cart_" + username).values();
        return orderItems;
    }

    private OrderItem skuOrderItem(Sku sku , Spu spu , Integer num){
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(sku.getId());
        orderItem.setSpuId(spu.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);

        //设置价格，单价*数量
        orderItem.setMoney(num * orderItem.getPrice());

        //实付金额
        orderItem.setPayMoney(num * orderItem.getPrice());

        orderItem.setImage(sku.getImage());
        //重量=单个重量*数量
        orderItem.setWeight(sku.getWeight()*num);

        //分类ID设置
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        return orderItem;
    }
}
