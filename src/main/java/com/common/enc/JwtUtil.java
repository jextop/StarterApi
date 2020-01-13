package com.common.enc;

import com.common.util.LogUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "a506b111-6e52-442d-854b-63c19bc16560";

    public static String generate(String str) {
        return generate(str, "Jwt", "token", 1000 * 3600 * 24 * 30L);
    }

    public static String generate(String str, String issuer, String subject, long msExpiration) {
        final long msNow = System.currentTimeMillis();

        // Set claims
        JwtBuilder builder = Jwts.builder()
                .setId(str)
                .setIssuedAt(new Date(msNow))
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS256, SECRET);

        // Add expiration
        if (msExpiration >= 0) {
            builder.setExpiration(new Date(msNow + msExpiration));
        }

        // Builds JWT and serializes it to a compacted, URL-safe string
        return builder.compact();
    }

    public static String parse(String token) {
        return parse(token, true);
    }

    public static String parse(String token, boolean checkExpiration) {
        final Claims claims = parseClaims(token);
        if (claims == null) {
            return null;
        }

        // Check if it expires
        if (checkExpiration) {
            final Date dateExpiration = claims.getExpiration();
            if (dateExpiration == null || dateExpiration.getTime() < System.currentTimeMillis()) {
                return null;
            }
        }

        // Return string
        return claims.getId();
    }

    public static Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LogUtil.info("Error when parseClaims", e.getMessage());
        }
        return null;
    }
}
