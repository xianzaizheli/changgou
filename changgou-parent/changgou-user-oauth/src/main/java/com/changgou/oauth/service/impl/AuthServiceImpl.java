package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 授权认证办法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        ServiceInstance choose = loadBalancerClient.choose("user-auth");
        if(choose == null){
            throw new RuntimeException("找不到对应的服务");
        }
        //获取令牌的url
        String path = choose.getUri().toString() + "/oauth/token";
        //定义body
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        //账号
        formData.add("username",username);
        //密码
        formData.add("password",password);
        //授权模式为密码模式
        formData.add("grant_type","password");

        //定义头
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        //将客户端ID跟客户端密码拼接起来
        String s = clientId +":"+clientSecret;
        byte[] encode = Base64Utils.encode(s.getBytes());
        String authorization = "Basic " + new String(encode);
        header.add("Authorization",authorization);

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //就算是401或者是402也要正常响应
                if(401 != response.getRawStatusCode() && 400 != response.getRawStatusCode()){
                    super.handleError(response);
                }
            }
        });

        Map map = null;

        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData, header), Map.class);
            map = exchange.getBody();
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }

        if(map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null){
            //jti是jwt令牌的唯一标志作为用户身份令牌
            throw new RuntimeException("创建令牌失败！");
        }

        //将响应数据封装成AuthToken对象
        AuthToken authToken = new AuthToken();
        //设置用户标识jti
        authToken.setJti(map.get("jti").toString());
        //设置访问令牌（jwt）
        authToken.setAccessToken(map.get("access_token").toString());
        //设置刷新令牌（jwt）
        authToken.setRefreshToken(map.get("refresh_token").toString());

        if(authToken == null){
            throw new RuntimeException("申请令牌失败");
        }
        return authToken;
    }
}
