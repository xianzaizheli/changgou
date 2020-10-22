package com.changgou.token;

import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;


public class ParseJwtTest {
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoi5Zyw5Z2A5YC85rWL6K-VIiwic2l6ZSI6Ny41LCJzZXgiOiJtYW4iLCJhZ2UiOjc3fQ.xMmNVV8CF4gOC6LD2f9Kz02CjeSK8iXJPdc5v5AUwUgdCezUkU9AgXoq4qvuAOorjK4-089YIladVzcZ1CGwJ_4cV6xld08hrM_LCmPjXjULGWR52vrMbkRfpbkUcmox-bY4Imq-jos9sR8TBT-LQo0IsDYPzbczMl1NnYJDLkv3sDstvC97P7pqZuczvhlSs9Lb4WiFQaYjG8aI5o_ksREj8jwVci3K5SgCrZxFSqKrY9K9GokUXOm7rQ7fLyfeBZ4qEVDRK7uvHwjl8gabiVqZAmkk-FyP-u8D1Dy4_lrNYuLcV0neEobrCVIF24csCAEVrYutcCALQG8fCMPdVw";

        //公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7pbxXlUo4z13AR54g6/3r0DaG/sbueua0XFZUfclljK5LRhR95cIjqTYIuh/20Ovly7JmVMfyhxNKCaqcq30WQtOFnbwsK90jQ0lpIhc/VKjlGvB9IwnbKwk+h35j5UJT+NI/lPV9GNrVCL11ZSh7IEyTbdNv6ROSMGvemBVloa3RTnmWJkVwn5hgSqJc1iCG6SVbzIJTfEp6SE0IhYmn7JGPPvAl5rgDf0fb5uf1/0zKKwaDMSGC6ZfkxhNnBRPpL64sTsGrCyD9brsE5g/OpZ/zvHBFbNZJ97r2eYfJBc3gOzhK4zj5XK6Onbl3l4HuN9YfI5LeFA2Yjr6L4wFxQIDAQAB-----END PUBLIC KEY-----";

        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));

        //获取jwt内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //获取jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
