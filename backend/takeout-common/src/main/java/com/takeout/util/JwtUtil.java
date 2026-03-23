package com.takeout.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author 小好
 */
public class JwtUtil {

    /**
     * 密钥（从环境变量读取，生产环境必须修改）
     * 启动时设置：export JWT_SECRET="your-secure-random-string-here"
     */
    private static final String SECRET = System.getenv("JWT_SECRET") != null 
            ? System.getenv("JWT_SECRET") 
            : "takeout-platform-secret-key-2024-change-in-production";

    /**
     * Token 过期时间：7 天
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    /**
     * 创建 Token
     *
     * @param userId 用户 ID
     * @return Token 字符串
     */
    public static String createToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("takeout-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 验证 Token 并解析用户 ID
     *
     * @param token Token 字符串
     * @return 用户 ID
     */
    public static Long verifyToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("userId", Long.class);
        } catch (Exception e) {
            throw new RuntimeException("Token 无效或已过期", e);
        }
    }

    /**
     * 检查 Token 是否过期
     *
     * @param token Token 字符串
     * @return true-未过期，false-已过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
