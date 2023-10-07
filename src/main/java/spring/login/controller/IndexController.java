package spring.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.login.security.principal.PrincipalDetails;

@Slf4j
@Controller
public class IndexController {

    @GetMapping("/")
    @ResponseBody
    public String home(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info(cookie.getValue());
            }
        }
        return "ok";
    }

    @GetMapping("/user")
    @ResponseBody
    public String User(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String username = principalDetails.getUsername();
        return username + "ok";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cookie")
    @ResponseBody
    public String cookie(HttpServletResponse response) {
        response.addCookie(new Cookie("ex", "ex"));
        return "cookie";
    }
}
