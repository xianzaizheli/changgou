package com.changgou.oauth.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class AuthLoginController {

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieMaxAge}")
    private Integer cookieMaxAge;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result login(String username ,String password ,HttpServletResponse response){
        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("用户名不能为空");
        }
        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不能为空");
        }

        AuthToken authToken = authService.login(username, password, clientId , clientSecret);
        String accessToken = authToken.getAccessToken();
        saveCookie(accessToken);

        return new Result<AuthToken>(true, StatusCode.OK,"登录成功",authToken);
    }

    public void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response , cookieDomain , "/" ,"Authorization",token,cookieMaxAge,false);
    }
}
