package com.example.vitalic_back.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtAuthenticationProvider;
//
//    public JwtAuthenticationFilter(JwtTokenProvider provider) {
//        jwtAuthenticationProvider = provider;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = jwtAuthenticationProvider.resolveToken(request);
//
//        if (token != null && jwtAuthenticationProvider.validateToken(token)) {
//            Authentication authentication = jwtAuthenticationProvider.getAuthentication(token);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//
//    }
//    private String extractToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
//        }
//        return null;
//    }
//}

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;

    @Qualifier("userDetailService")
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String header = request.getHeader(tokenProvider.getHeaderString());

        log.debug("JWT Authentication Filter triggered for request: {}", path);
        log.debug("Authorization header: {}", header);

        if (header != null && header.startsWith(tokenProvider.getTokenPrefix())) {
            String token = header.replace(tokenProvider.getTokenPrefix(), "").trim();

            if (tokenProvider.validateToken(token)) {
                String userEmail = tokenProvider.getUserEmailFromJWT(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Spring Security context에 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}