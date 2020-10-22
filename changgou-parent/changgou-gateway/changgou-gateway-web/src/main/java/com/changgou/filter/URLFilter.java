package com.changgou.filter;

import org.springframework.util.StringUtils;

public class URLFilter {

    /**
     * 要过滤
     */
    private static String noAuthorization = "/api/user/add,/api/user/login";


    public static boolean hasAuthorizaion(String url){
        if(StringUtils.isEmpty(url)){
            return false;
        }
        String[] split = noAuthorization.split(",");
        for(String s : split){
            if(s.equals(url)){
                return true;
            }
        }
        return false;
    }
}
