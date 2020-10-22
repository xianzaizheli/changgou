package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.changgou.dao")
@EnableEurekaClient
@SpringBootApplication
public class FescarUserInfoApplication {
    public static void main(String[] args) {
        SpringApplication.run(FescarUserInfoApplication.class,args);
    }
}
