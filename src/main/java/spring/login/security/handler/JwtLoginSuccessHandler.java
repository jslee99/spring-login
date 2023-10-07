package spring.login.security.handler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import spring.login.security.jwtservice.JwtProperties;
import spring.login.security.jwtservice.JwtTokenService;
import spring.login.security.principal.PrincipalDetails;

import java.io.IOException;
import java.net.URLEncoder;

//authentication(로그인) 성공후 jwt토큰을 만들고 쿠키에 저장해줌
@Slf4j
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String jwtToken = jwtTokenService.createToken(principalDetails.getName(), principalDetails.getUsername());

        String encode = URLEncoder.encode(jwtToken, "UTF-8");
        Cookie cookie = new Cookie(JwtProperties.COOKIE_KEY_STRING, encode);
        cookie.setPath("/");//하지 않으면 /login/oauth...... 즉 이 redirect url에 대해서만 쿠키가 유효하다?

        response.addCookie(cookie);
        response.sendRedirect("/");
    }
}
