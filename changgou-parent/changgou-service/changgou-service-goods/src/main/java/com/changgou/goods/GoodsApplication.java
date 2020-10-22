package com.changgou.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient //启用Eureka客户端
/**
 * 这里要注意一下，用的是tkmybatis的包，这样才能够保证导包没错，才能使用通用mapper
 * mapper接口继承了通用的mapper
 * 默认提供一些方法:insert、update、delete、select
 */
@MapperScan(basePackages = {"com.changgou.goods.dao"})
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class,args);
    }

}
