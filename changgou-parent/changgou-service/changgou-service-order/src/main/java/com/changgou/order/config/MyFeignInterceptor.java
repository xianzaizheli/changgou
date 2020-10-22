package com.changgou.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class MyFeignInterceptor implements RequestInterceptor {
//public class MyFeignInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        //使用RequestContextHolder获取request相关变量
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(sra != null){
            //取出request
            HttpServletRequest request = sra.getRequest();
            //获取所有头文件的key
            Enumeration<String> headerNames = request.getHeaderNames();
            if(null != headerNames){
                while (headerNames.hasMoreElements()){
                    //头文件的key值
                    String key = headerNames.nextElement();
                    //头文件的value值
                    String value = request.getHeader(key);
                    System.out.println("name:"+key +"    value:"+value);
                    //将头信息传递给feign（restTemplate）
                    requestTemplate.header(key , value);
                }

            }
        }
    }

}
