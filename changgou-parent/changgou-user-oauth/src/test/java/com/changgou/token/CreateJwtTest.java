package com.changgou.token;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.Base64Utils;


import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;

public class CreateJwtTest {

    /**
     * 生成令牌
     */
    @Test
    public void TestCreateJwt(){
        //证书位置
        String key_location = "changgou.jks";
        //证书别名
        String alias = "changgou";
        //秘钥库秘钥
        String key_password = "chenxiansheng";
        //秘钥密码
        String keyPwd = "yanyanyan";

        //访问证书路径
        ClassPathResource classPathResource = new ClassPathResource(key_location);
        //创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, key_password.toCharArray());

        //获取一对秘钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keyPwd.toCharArray());

        //获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey)keyPair.getPrivate();

        HashMap<String, Object> map = new HashMap<>();
        map.put("address","地址值测试");
        map.put("age",77);
        map.put("sex","man");
        map.put("size",7.5d);

        //生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(aPrivate));

        //取出令牌
        String encoded = jwt.getEncoded();

        System.out.println(encoded);
    }

    @Test
    public void test77(){
//        byte[] encode = Base64Utils.encode("changgou:changgou".getBytes());
        String heima = new BCryptPasswordEncoder().encode("heima");
//        System.out.println(new String(encode));
        System.out.println(heima);
        System.out.println(new BCryptPasswordEncoder().encode("yanyanyan"));
    }
}
