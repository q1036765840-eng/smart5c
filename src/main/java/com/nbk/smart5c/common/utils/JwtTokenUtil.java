package com.nbk.smart5c.common.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JwtTokenUtil {

    // TOKEN的有效期一天（S）
    public static final int TOKEN_TIME_OUT = 24 * 60 * 60 * 1000;
    // 加密KEY（由 JwtKeyConfig 从 jwt.secret 配置注入，不再硬编码）
    private static volatile String TOKEN_ENCRY_KEY = "";
    // 最小刷新间隔(S)
    private static final int REFRESH_TIME = 300;

    public static void setTokenEncryptionKey(String key) {
        if (key != null && !key.trim().isEmpty()) {
            TOKEN_ENCRY_KEY = key.trim();
        }
    }

    // 生产ID
    public static String getToken(Map<String, Object> claimMaps) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTime))  //签发时间
                .setSubject("system")  //说明
                .setIssuer("nbk") //签发者信息
                .setAudience("user")  //接收用户
                .compressWith(CompressionCodecs.GZIP)  //数据压缩方式
                .signWith(SignatureAlgorithm.HS512, generalKey())
                .setExpiration(new Date(currentTime + TOKEN_TIME_OUT))  //过期时间戳
                .addClaims(claimMaps) //cla信息
                .compact();
    }

    /**
     * 获取token中的claims信息
     *
     * @param token
     * @return
     */
    private static Jws<Claims> getJws(String token) {
        return Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(token);
    }

    /**
     * 获取payload body信息
     *
     * @param token
     * @return
     */
    public static Claims getClaimsBody(String token) {
        try {
            Jws<Claims> jws = getJws(token);
            if (null == jws) {
                return null;
            }
            return jws.getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    /**
     * 获取hearder body信息
     *
     * @param token
     * @return
     */
    public static JwsHeader getHeaderBody(String token) {
        return getJws(token).getHeader();
    }

    /**
     * 是否过期
     *
     * @param claims
     * @return -1：有效，0：有效，1：过期，2：过期
     */
    public static boolean tokenIsExpired(Claims claims) {
        if (claims == null) {
            return true;
        }
        try {
            return claims.getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException ex) {
            log.error("TOKEN失效校验异常:{}", ex.getLocalizedMessage(),ex);
            return true;
        }
    }

    /**
     * 是否过期
     *
     * @param claims
     * @return -1：有效，0：有效，1：过期，2：过期
     */
    public static boolean tokenNeedRefresh(Claims claims) {
        if (claims == null) {
            return false;
        }
        if(!tokenIsExpired(claims)){
            // 需要自动刷新TOKEN
            return (claims.getExpiration().getTime() - System.currentTimeMillis()) <= REFRESH_TIME * 1000;
        }
        return false;
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(TOKEN_ENCRY_KEY.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

}