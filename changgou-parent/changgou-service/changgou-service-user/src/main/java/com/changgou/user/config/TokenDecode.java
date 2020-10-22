package com.changgou.user.config;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenDecode {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    /**
     * 获取非对称公钥Key
     * @return  公钥Key
     */
    private String getPublicKey(){
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader isr = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            return br.lines().collect(Collectors.joining("/n"));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 解析令牌
     * @return
     */
    public Map<String,String> dcodeToken(){
        //获取令牌
        String token = getUserInfo();
        //获取公钥
        String publicKey = getPublicKey();
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        String claims = jwt.getClaims();
        return JSON.parseObject(claims , Map.class);
    }

    /**
     * 获取令牌
     * @return
     */
    public String getUserInfo(){
        //获取授权消息
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        return details.getTokenValue();
    }

}
