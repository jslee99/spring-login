package spring.login.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import spring.login.controller.dto.JoinForm;
import spring.login.domain.member.DefaultMember;
import spring.login.domain.member.Member;
import spring.login.domain.member.Role;
import spring.login.repository.MemberRepository;
import spring.login.security.principal.PrincipalDetail;
import spring.login.service.MemberService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        log.info("principal detail = {}", principalDetail);
        Member member = null;
        if (principalDetail != null) {
            member = principalDetail.getMember();
            log.info("member = {}", member);
        }
        model.addAttribute("member", member);
        return "index/index";
    }

    @GetMapping("/login")
    public String login() {
        return "index/login";
    }

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("member", new JoinForm());
        return "index/joinForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("member") JoinForm joinForm, BindingResult bindingResult) {
        Optional<Member> findMember = memberRepository.findByUsername(joinForm.getUsername());

        if (findMember.isPresent()) {
            bindingResult.reject("duplicatedUsername", new Object[]{joinForm.getUsername()}, null);
            bindingResult.rejectValue("username", "duplicatedUsername", new Object[]{joinForm.getUsername()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "index/joinForm";
        }

        Member member = new DefaultMember(joinForm.getUsername(), bCryptPasswordEncoder.encode(joinForm.getPassword()), null, Role.ROLE_USER);
        memberRepository.save(member);

        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String userList(Model model) {
        PageRequest pageRequest = PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "id"));
        List<Member> memberList = memberRepository.findAll(pageRequest).getContent();
        model.addAttribute("memberList", memberList);
        return "admin/userInformList";
    }

}
