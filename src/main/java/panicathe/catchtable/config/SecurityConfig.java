package panicathe.catchtable.config;

import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import panicathe.catchtable.jwt.JwtAuthenticationFilter;

@Configurable
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 인증 필터를 주입받습니다.

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())) // CORS 설정을 정의합니다.

                .csrf(CsrfConfigurer::disable) // csrf disable -> JWT를 사용하는 Stateless 인증 방식에서는 CSRF 공격 위험 적음

                .httpBasic(HttpBasicConfigurer::disable)  //http basic 인증 방식 disable

                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 STATELESS로 설정하여 세션을 사용하지 않게

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**", "/auth/**").permitAll()
                        .requestMatchers("/partner/**").hasAuthority("ROLE_PARTNER")
                        .requestMatchers("/user/**").hasAuthority("ROLE_USER")
                        .anyRequest().authenticated())

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new FailedAuthenticationEntryPoint())) // 인증 실패 시 처리할 엔트리 포인트를 설정

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 커스텀 JWT 인증 필터

        return httpSecurity.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedOrigin("*"); // 모든 도메인에서의 요청을 허용합니다.
        corsConfiguration.addAllowedMethod("*"); // 모든 HTTP 메소드를 허용합니다.
        corsConfiguration.addAllowedHeader("*"); // 모든 헤더를 허용합니다.
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 모든 경로에 대해 CORS 설정을 적용합니다.

        return source;
    }
}

class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 인증 실패 시 클라이언트에게 응답할 로직
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"code\":\"NP\", \"message\": \"No Permission\"}");
    }
}

