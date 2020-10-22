package com.changgou;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

public class JwtTest {

    /**
     * 加密
     */
    @Test
    public void testCreateJwt(){
        JwtBuilder jwtBuilder = Jwts.builder()
//                .setExpiration(new Date())  //设置过期时间
                .setId("888")   //设置主键
                .setSubject("内容测试") //设置主题
                .setIssuedAt(new Date())  //设置签发时间
                .signWith(SignatureAlgorithm.HS256,"PLAY");//设置签名，并设置秘钥（字符串）

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("age",48);
        hashMap.put("name","陈某");
        hashMap.put("address","上海");
        //这里不要用set那个方法，set是全部改了的，用add加入
        jwtBuilder.addClaims(hashMap);
        System.out.println("输出看一下结果："+jwtBuilder.compact());
    }

    /**
     * 解析Jwt令牌数据
     */
    @Test
    public void testParseJwt(){
        String compactJwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlhoXlrrnmtYvor5UiLCJpYXQiOjE1OTg3NjU3NTUsImFkZHJlc3MiOiLkuIrmtbciLCJuYW1lIjoi6ZmI5p-QIiwiYWdlIjo0OH0.jw1gLbm20TOkL_9GFknrUK6IyrczusJukHprRs7mn8A";
        Claims play = Jwts.parser().setSigningKey("PLAY").parseClaimsJws(compactJwt).getBody();
        System.out.println("输出结果："+play);
    }
}
