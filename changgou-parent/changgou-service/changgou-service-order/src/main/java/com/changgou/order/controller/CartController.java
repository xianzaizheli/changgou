package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;

    @GetMapping("/add")
    public Result addCart(Integer num, String id){
        String username = tokenDecode.dcodeToken().get("username");
        cartService.add(num,id,username);
        return new Result(true , StatusCode.OK ,"加入购物车成功");
    }

    @GetMapping("/list")
    public Result<List<OrderItem>> list(String username){
        List<OrderItem> list = cartService.list(username);
        return new Result<List<OrderItem>>(true , StatusCode.OK , "查询成功" , list);
    }
}
