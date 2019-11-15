package top.liujingyanghui.assignmentupload.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    // 秘钥
    private static final String SECRET = "sds15644er1rr2gcge3r4ytxgwetwets";
    // 过期时间
    private static final int TIMEOUT = 7200000;//一天86400000  一周604800000   2小时 7200000

    /**
     * 根据token验证获取信息
     */
    public static Long getSubject(String token) {
        Claims claim = getClaim(token);
        String subject = claim.getSubject();
        return Long.parseLong(subject);
    }

    /**
     * 根据token验证获取信息
     */
    public static Claims getClaim(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建token
     *
     * @return token
     */
    public static String setClaim(Map<String, Object> claim, String subject) {
        return Jwts.builder().setClaims(claim).setSubject(subject) // 设置token主题
                .setIssuedAt(new Date()) // 设置token发布时间
                .setExpiration(new Date(System.currentTimeMillis() + TIMEOUT)) // 设置token过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    /**
     * 解密token
     *
     */
    public static Claims getClaimsFromToken(String token) throws Exception {
        Claims claims = Jwts.parser() // 得到DefaultJwtParser
                .setSigningKey(SECRET) // 设置签名的秘钥
                .parseClaimsJws(token).getBody(); // 设置需要解析的jwt
        return claims;
    }
}
