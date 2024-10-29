package com.example.vitalic_back.jwt;

import com.example.vitalic_back.entity.Role;
import com.example.vitalic_back.entity.User;
import com.example.vitalic_back.service.CustomUserDetailService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

//@RequiredArgsConstructor
//@Component
//public class JwtTokenProvider {
//
//    private final CustomUserDetailService userDetailsService;
//    private String secretKey =
//            "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";
//    Key key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
//
//    // 토큰 유효시간 168 시간(7일)
//    private long tokenValidTime = 1440 * 60 * 7 * 1000L;
//    private final CustomUserDetailService userDetailService;
//    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
//
//
//    // 객체 초기화, secretKey 를 Base64로 인코딩
//    @PostConstruct
//    protected void init() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//    }
//
//    // JWT 토큰 생성
//    public String createToken(String userPk, List<String> roles) {
//        Claims claims = Jwts.claims().setSubject(userPk);
//        claims.put("roles", roles); // key/value 쌍으로 저장
//        Date now = new Date();
//        String token = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + tokenValidTime))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//
//        logger.debug("Generated Token: " + token);
//        return token;
//    }
//
//
//    // JWT 토큰에서 인증 정보 조회
//    public Authentication getAuthentication(String token) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }
//
//    public String getUserPk(String token) {
//        String userPk = Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//        logger.debug("Parsed UserPK: " + userPk);
//        return userPk;
//    }
//
//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
//
//    // 토큰의 유효성 + 만료일자 확인
//    public boolean validateToken(String jwtToken) {
//        try {
//            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
//            boolean isValid = !claims.getBody().getExpiration().before(new Date());
//            logger.debug("Token Valid: " + isValid);
//            return isValid;
//        } catch (Exception e) {
//            logger.debug("Token Validation Error: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public Long extractUserNo(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get("userNo", Long.class);
//    }
//}
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @Getter
    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Getter
    @Value("${jwt.header-string}")
    private String headerString;

    public String generateToken(String userEmail, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime); // expirationTime은 밀리초 단위

        // 비밀 키 설정
        Key key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
                .setSubject(userEmail) // JWT의 주체 설정
                .claim("auth", String.join(",", roles)) // 역할을 claim으로 추가
                .setIssuedAt(now) // 발급 시간 설정
                .setExpiration(expiryDate) // 만료 시간 설정
                .signWith(key) // 서명
                .compact(); // JWT 생성
    }

    public String getUserEmailFromJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    //.setSigningKey(secretKey)
                    .setSigningKey(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            // 예외 발생 시 로그를 남김
            log.error("Error parsing JWT: {}", e.getMessage());
            throw new RuntimeException("JWT 파싱 오류: " + e.getMessage(), e); // 예외를 던져 호출한 쪽에서 처리하게 함
        }
    }
    public Long getUserNoFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userNo", Long.class); // userNo 추출
    }
//    public boolean validateToken(String token) {
//        try {
//            Key key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            log.error("Invalid JWT token: {}", e.getMessage());
//            return false;
//        }
//    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName())) // 같은 비밀 키 사용
                    .build()
                    .parseClaimsJws(token); // JWT 파싱
            return true; // 유효한 경우
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 유효성 검사 실패: {}", e.getMessage());
            return false; // 유효하지 않은 경우
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(headerString);
        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length()).trim();
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get("auth") == null) {
            return null;
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = User.builder()
                .userEmail(claims.getSubject())
                .role(Role.valueOf(claims.get("auth").toString()))
                .build();

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}