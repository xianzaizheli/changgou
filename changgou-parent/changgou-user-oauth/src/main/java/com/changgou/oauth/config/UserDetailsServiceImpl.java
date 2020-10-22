package com.changgou.oauth.config;

import com.changgou.entity.Result;
import com.changgou.oauth.util.UserJwt;
import com.changgou.user.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 自定义授权认证类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private UserFeign userFeign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //客户端认证开始
        //取出身份，如果身份为空则说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(null != clientDetails){
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
//                return new User(username ,new BCryptPasswordEncoder().encode(clientSecret) , AuthorityUtils.commaSeparatedStringToAuthorityList(""));
                //使用数据库方式，用户名，秘钥，权限
                return new User(username , clientSecret , AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }

        if(StringUtils.isEmpty(username)){
            return null;
        }

        //根据用户名查询用户信息
        Result<com.changgou.user.pojo.User> userResult = userFeign.findByUsername(username);
        com.changgou.user.pojo.User data = userResult.getData();
        String pwd = data.getPassword();
        //创建User对象  授予权限    USER,VIP
        String permissions = "USER , VIP ";

//        String pwd = new BCryptPasswordEncoder().encode("szitheima");
        UserJwt userDetails = new UserJwt(username, pwd, AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        return userDetails;
    }
}
