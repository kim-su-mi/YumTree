package com.ssafy.yumTree.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
/**
 * JWT 생성하고 검증하는 class 
 */
@Component
public class JWTUtil {

    private SecretKey secretKey;

    /**
     * application.properties에 저장한 JWT암호화 키 가져옴 
     * @param secret
     */
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

    	// secretkey를 객체 변수로 암호화 
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * 토큰에서 Id뽑아서 검증 
     * @param token
     * @return
     */
    public String getUseId(String token) {
    	// 토큰이 우리 서버에서 생성되었는지 확인 후 payLoad에서 Id정보 가져옴 
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }
    /**
     * 토큰에서 role값 뽑아서 검증 
     * @param token
     * @return
     */
    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
    /**
     * 토큰이만료 되었는지  
     * @param token
     * @return
     */
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    /**
     * 카테고리 값 꺼냄 
     * @param token
     * @return
     */
    public String getCategory(String token) {
        
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }
    /**
     * 로그인 성공 시 토큰 생성 
     * @param username
     * @param role
     * @param expiredMs
     * @return
     */
    public String createJwt(String category,String userId, String role, Long expiredMs) {

        return Jwts.builder()
        		.claim("category", category)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 언제 발행되었는지 
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey) //암호화 진행 
                .compact();
    }
}