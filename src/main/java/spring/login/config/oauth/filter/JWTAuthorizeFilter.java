package spring.login.config.oauth.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.login.config.oauth.JwtProperties;
import spring.login.config.oauth.auth.PrincipalDetails;
import spring.login.domain.Member;
import spring.login.repository.MemberRepository;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;

public class JWTAuthorizeFilter extends OncePerRequestFilter {

    private MemberRepository memberRepository;

    public JWTAuthorizeFilter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Cookie authCookie = cookies == null ? null : Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JwtProperties.HEADER_STRING))
                .findAny().orElse(null);
        if(authCookie == null){
            filterChain.doFilter(request,response);
            return;
        }

        String encodedJwtToken = authCookie.getValue();
        String jwtToken = URLDecoder.decode(encodedJwtToken);
        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken)
                .getClaim("username").asString();

        if(username != null){
            Optional<Member> findMember = memberRepository.findByUsername(username);
            if (findMember.isEmpty()) {
                filterChain.doFilter(request,response);
                return;
            }
            PrincipalDetails principalDetails = new PrincipalDetails(findMember.get());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }
}
