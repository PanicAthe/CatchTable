package panicathe.catchtable.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import panicathe.catchtable.model.Partner;
import panicathe.catchtable.model.User;
import panicathe.catchtable.repository.PartnerRepository;
import panicathe.catchtable.repository.UserRepository;

/**
 * 요청마다 한 번씩 실행되는 필터로, JWT를 검증하고 인증 정보를 SecurityContext에 설정한다.
 * 이 클래스는 Spring Security의 OncePerRequestFilter를 확장하여 구현되었다.
 */
@Component
@RequiredArgsConstructor // final이나 @NonNull인 필드에 대한 생성자를 자동으로 생성
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 요청 헤더에서 JWT 토큰을 추출한다.
            String token = parseBearerToken(request);

            // 토큰이 없는 경우, 다음 필터로 요청을 넘긴다.
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에서 사용자 이메일을 검증하고 추출한다.
            String userEmail = jwtProvider.getEmail(token);
            String role = jwtProvider.getRole(token);

            System.out.println("이메일" + userEmail);
            System.out.println("롤" + role); //디버깅
            ;
            // 검증 실패 시, 다음 필터로 요청을 넘긴다.
            if (userEmail == null || role == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 권한 설정
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));

            // 인증 정보 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());


        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // 모든 검증을 마치면, 다음 필터로 요청을 넘겨 처리한다.
        filterChain.doFilter(request, response);
    }

    /**
     * 'Authorization' 헤더에서 'Bearer '로 시작하는 토큰을 추출하는 메소드.
     *
     * @param request HTTP 요청 객체
     * @return 추출된 토큰 문자열, 없거나 잘못된 형식이면 null 반환
     */
    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7); // 'Bearer ' 다음부터 토큰 추출
    }
}
