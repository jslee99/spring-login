package spring.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.login.domain.Member;
import spring.login.security.jwtservice.JwtProperties;
import spring.login.security.principal.PrincipalDetail;

import java.util.Arrays;

@Slf4j
@Controller
public class IndexController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        log.info("principal detail = {}", principalDetail);
        Member member = null;
        if (principalDetail != null) {
            member = principalDetail.getMember();
            log.info("member = {}", member);
        }
        model.addAttribute("member", member);
        return "index.html";
    }

    @GetMapping("/user")
    @ResponseBody
    public String User(Authentication authentication) {
        PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();
        String username = principalDetail.getUsername();
        return username + "ok";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
