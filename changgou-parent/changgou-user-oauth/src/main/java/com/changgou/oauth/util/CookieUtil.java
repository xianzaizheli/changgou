package com.changgou.oauth.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieUtil {

    /**
     * 设置cookie
     * @param response
     * @param domain
     * @param path
     * @param name  cookie名字
     * @param value    cookie值
     * @param maxAge    cookie生命周期，以秒为单位
     * @param httpOnly
     */
    public static void addCookie(HttpServletResponse response ,String domain , String path ,String name ,String value ,int maxAge ,Boolean httpOnly){
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }

    /**
     * 根据cookie名称读取cookie
     * @param request
     * @param cookieNames
     * @return
     */
    public static Map<String ,String> readCookie(HttpServletRequest request , String... cookieNames){
        HashMap<String, String> hashMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if(null != cookies){
            for(Cookie cookie : cookies){
                String name = cookie.getName();
                String value = cookie.getValue();
                for (int i = 0 ; i <= cookieNames.length-1 ; i++){
                    if(name.equals(cookieNames[i])){
                        hashMap.put(name,value);
                    }
                }
            }
        }
        return hashMap;
    }

}
