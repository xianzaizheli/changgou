package com.changgou.filter;

import com.changgou.entity.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    public static final String POST_URL = "http://127.0.0.1:9001/oauth/login";

    /**
     * 全局过滤器
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取Request、Response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取请求的URI
        String path = request.getURI().getPath();

        //如果是登录、goods等开放的微服务【这里的goods部分开放】，则直接放行，这里不做完整演示，完整演示需要设计一套权限系统
        if(URLFilter.hasAuthorizaion(path)){
            Mono<Void> filter = chain.filter(exchange);
            return filter;
        }

        //1、从请求头中获取
        List<String> list = request.getHeaders().get(AUTHORIZE_TOKEN);
        String authorize_token = "";
        if(null != list){
            authorize_token = list.get(0);
            System.out.println("输一下看看吧："+authorize_token);
        }

        if(StringUtils.isEmpty(authorize_token)){
            //2、从请求参数中获取
            authorize_token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }

        //3、从cookie中获取内容
        List<HttpCookie> httpCookies = request.getCookies().get(AUTHORIZE_TOKEN);
        if(StringUtils.isEmpty(authorize_token) && null != httpCookies){
            authorize_token = httpCookies.get(0).getValue();
        }


        if(StringUtils.isEmpty(authorize_token)){
            //设置方法不允许访问
//            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
//            return response.setComplete();
            return needAuthorize(POST_URL+"?from="+request.getURI(),exchange);
        }


        try {
            //这个内容之前用来做解析jwt令牌，现在解析不在网关在这里做，微服务中做
            //Claims claims = JwtUtil.parseJWT(authorize_token);
            //如果解析成功之后把数据放在请求头中
            request.mutate().header(AUTHORIZE_TOKEN,"Bearer "+authorize_token);
        }catch (Exception e){
            //解析失败，返回401错误
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        Mono<Void> filter = chain.filter(exchange);
        //放行
        return filter;

    }

    /**
     * 设置请求头
     * @param url
     * @param exchange
     * @return
     */
    private Mono<Void> needAuthorize(String url , ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("Location",url);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
