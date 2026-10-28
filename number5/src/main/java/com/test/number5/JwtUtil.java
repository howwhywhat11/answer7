package com.test.number5;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    //生成密钥，这里采用的是随机的临时密钥，重启IDEA后会重置
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //门票的有效期（毫秒）。这里设置 1 小时 = 3600000 毫秒
    private final long EXPIRATION_TIME = 3600000;

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)  // 需要区分的内容：角色（USER/ADMIN）
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
