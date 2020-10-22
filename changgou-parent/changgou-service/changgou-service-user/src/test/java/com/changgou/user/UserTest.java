package com.changgou.user;


import com.changgou.entity.BCrypt;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

public class UserTest {

    @Test
    public void test01(){
        //获取盐
        String gensalt = BCrypt.gensalt();
        System.out.println("盐的内容："+gensalt);
        //密码加密
        String heima = BCrypt.hashpw("heima", "$2a$10$9hTVhnom1gvLK7geG4nbVO");
        System.out.println(heima);
        //密码对比处理
        boolean heima1 = BCrypt.checkpw("heima", "$2a$10$9hTVhnom1gvLK7geG4nbVOHhhSCjz5lh.KrVCTs70xt1z5oYrAFuW");
        System.out.println(heima1);
    }

    @Test
    public void test02(){
        String pwd = new BCryptPasswordEncoder().encode("yanyanyan");
        System.out.println(pwd);
    }
}
