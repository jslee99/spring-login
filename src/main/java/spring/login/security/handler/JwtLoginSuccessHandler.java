package spring.login.security.handler;
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
import spring.login.security.principal.PrincipalDetail;

import java.io.IOException;

//authentication(로그인) 성공후 jwt토큰을 만들고 쿠키에 저장해줌
@Slf4j
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

        String encodedJwtToken = jwtTokenService.createEncodedToken(principalDetail.getName(), principalDetail.getUsername());

        Cookie cookie = new Cookie(JwtProperties.COOKIE_KEY_AUTHORIZATION, encodedJwtToken);
        cookie.setPath("/");//하지 않으면 /login/oauth...... 즉 이 redirect url에 대해서만 쿠키가 유효하다?\

        response.addCookie(cookie);
        response.sendRedirect("/");
    }
}
