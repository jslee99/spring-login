package spring.login.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.login.security.jwtservice.JwtProperties;
import spring.login.security.jwtservice.JwtTokenService;
import spring.login.security.principal.PrincipalDetail;
import spring.login.domain.Member;

import java.io.IOException;
import java.util.Arrays;

/**
 * cookie jwt token이 존재하는지 확인하고, 해당 jwt token이 유효한 토큰인지 확인한다.
 */
@RequiredArgsConstructor
@Slf4j
public class JwtVerifyFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Cookie encodedJwtTokenCookie = cookies == null ? null : Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JwtProperties.COOKIE_KEY_AUTHORIZATION))
                .findAny().orElse(null);

        Member findMember;
        if (encodedJwtTokenCookie != null && (findMember = jwtTokenService.verifyEncodedToken(encodedJwtTokenCookie.getValue())) != null) {
            log.info("verified user ok");
            PrincipalDetail principalDetail = new PrincipalDetail(findMember);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetail, principalDetail.getPassword(), principalDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
