package com.example.vitalic_back.config;


import com.example.vitalic_back.jwt.JwtAuthenticationFilter;
import com.example.vitalic_back.jwt.JwtTokenProvider;
import com.example.vitalic_back.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity
//public class SecurityConfig {
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//    public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//        private final JwtTokenProvider jwtTokenProvider;
//        private CustomUserDetailService customUserDetailService;
//
//        public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
//            this.jwtTokenProvider = jwtTokenProvider;
//        }
//        @Override
//        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//                throws ServletException, IOException {
//            String token = jwtTokenProvider.resolveToken(request);
//            logger.debug("Extracted Token: " + token);
//
//            if (token != null && jwtTokenProvider.validateToken(token)) {
//                Authentication auth = jwtTokenProvider.getAuthentication(token);
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }else {
//                logger.warn("Invalid Token");
//            }
//            filterChain.doFilter(request, response);
//        }
//
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//            http
//                    .formLogin(login -> login.disable())  // Lambda 방식으로 formLogin 비활성화
//                    .httpBasic(basic -> basic.disable())  // httpBasic 비활성화
//                    .cors(cors -> cors.disable())         // CORS 비활성화
//                    .csrf(csrf -> csrf.disable())         // CSRF 비활성화
//                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                    .authorizeHttpRequests(auth -> auth
//                            .requestMatchers("/user/signin", "/user/signup").permitAll()
//                            .requestMatchers("/user/mypage").hasRole("USER")
//                            .anyRequest().authenticated()
//                    )
//
//                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);;
//            return http.build();
//        }
//        @Bean
//        public UserDetailsService userDetailsService() {
//            return customUserDetailService;
//        }
//    }
//
//
//    // 무시할 경로
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(AUTH_WHITELIST);
//    }
//
//    // 화이트리스트
//    private static final String[] AUTH_WHITELIST = {
//            "/v2/api-docs",
//            "/v3/api-docs/**",
//            "/configuration/ui",
//            "/swagger-resources/**",
//            "/configuration/security",
//            "/swagger-ui.html",
//            "/webjars/**",
//            "/file/**",
//            "/image/**",
//            "/swagger/**",
//            "/swagger-ui/**",
//            "/h2/**"
//    };
//
//}

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService customUserDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(withDefaults())  // CORS 설정 활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/signin", "/user/signup").permitAll()
                        .requestMatchers("/user/mypage").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 허용할 Origin 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailService);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailService; // 이 경우 customUserDetailService가 @Autowired로 주입되어야 합니다.
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(AUTH_WHITELIST);
    }

    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/file/**",
            "/image/**",
            "/swagger/**",
            "/swagger-ui/**",
            "/h2/**"
    };
}