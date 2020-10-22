package com.changgou.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@Configuration
@EnableResourceServer       //开启资源服务器（标志它是一个oauth2中的资源服务器）
@EnableGlobalMethodSecurity(prePostEnabled = true , securedEnabled = true)  //激活方法上的PreAuthorize注解
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    /**
     * 定义JwtTokenStore
     * @param jwtAccessTokenConverter
     * @return
     */
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter){
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /**
     * 定义JwtAccessTokenConverter
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPublicKey());
        return converter;
    }

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
     * Http安全配置，对每个到达系统的http请求连接进行校验
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //请求认证处理
        http.authorizeRequests()
                //下面的路径放行
                .antMatchers("/user/load/*")   //配置地址放行
                .permitAll()
                .anyRequest()
                .authenticated();   //其他地址需要认证授权
    }
}
