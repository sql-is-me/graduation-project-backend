package com.sql.common.jwt.service;

import java.util.Map;

import com.sql.common.constants.JWTConstants;
import com.sql.common.constants.TokenConstants;
import com.sql.utils.Convert;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Jwt工具类
 *
 */
public class JWTService {
    public static String secret = TokenConstants.SECRET;

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims) {
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return claim 数据声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 根据claims获取UUIDtoken
     * 
     * @param claims
     * @return UUIDtoken
     */
    public static String getToken(Claims claims) {
        return getValue(claims, JWTConstants.DETAILS_TOKEN);
    }

    /**
     * 根据claims获取SessionKey
     * 
     * @param claims
     * @return SessionKey
     */
    public static String getSessionKey(Claims claims) {
        return getValue(claims, JWTConstants.DETAILS_SESSION_KEY);
    }

    /**
     * 根据claims获取获取用户ID
     * 
     * @param claims
     * @return ID
     */
    public static String getId(Claims claims) {
        return getValue(claims, JWTConstants.DETAILS_ID);
    }

    /**
     * 根据身份信息获取用户名
     * 
     * @param claims
     * @return username
     */
    public static String getUsername(Claims claims) {
        return getValue(claims, JWTConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取用户类别
     * 
     * @param claims
     * @return 0:管理员 1:用户
     */
    public static String getType(Claims claims) {
        return getValue(claims, JWTConstants.DETAILS_TYPE);
    }

    /**
     * 根据身份信息获取键值
     * 
     * @param claims
     * @param key    键
     * @return 值
     */
    public static String getValue(Claims claims, String key) {
        return Convert.toStr(claims.get(key), "");
    }
}
