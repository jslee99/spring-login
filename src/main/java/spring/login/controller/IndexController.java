package spring.login.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.login.controller.dto.JoinForm;
import spring.login.controller.dto.UpdateForm;
import spring.login.domain.Member;
import spring.login.domain.Role;
import spring.login.repository.MemberRepository;
import spring.login.security.principal.PrincipalDetail;
import spring.login.service.MemberService;

import java.util.List;
import java.util.NoSuchElementException;
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
        return "index.html";
    }

    @GetMapping("/user")
    public String User(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        Long id = Long.valueOf(principalDetail.getName());
        Member findMember = memberRepository.findById(id).orElseThrow(NoSuchElementException::new);
        model.addAttribute("member", findMember);
        return "userInform";
    }

    @GetMapping("/user/update")
    public String getUserForm(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        Long id = Long.valueOf(principalDetail.getName());
        Member findMember = memberRepository.findById(id).orElseThrow(NoSuchElementException::new);
        model.addAttribute("member", findMember);
        return "userUpdateForm";
    }

    @PostMapping("/user/update")
    public String postUserUpdateForm(@AuthenticationPrincipal PrincipalDetail principalDetail,@Validated @ModelAttribute("member") UpdateForm updateForm, BindingResult bindingResult) {
        String username = updateForm.getUsername();
        String email = updateForm.getEmail();
        Long memberId = Long.valueOf(principalDetail.getName());
        Optional<Member> byUsername = memberRepository.findByUsername(username);
        if (byUsername.isPresent() && byUsername.get().getId() != memberId) {
            bindingResult.rejectValue("username", "duplicatedUsername", new Object[]{updateForm.getUsername()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "userUpdateForm";
        }


        memberService.updateUsername(memberId, username, email);
        return "redirect:/user";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("member", new JoinForm());
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("member") JoinForm joinForm, BindingResult bindingResult) {
        Optional<Member> findMember = memberRepository.findByUsername(joinForm.getUsername());

        if (findMember.isPresent()) {
            bindingResult.reject("duplicatedUsername", new Object[]{joinForm.getUsername()}, null);
            bindingResult.rejectValue("username", "duplicatedUsername", new Object[]{joinForm.getUsername()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "joinForm";
        }

        Member member = new Member(joinForm.getUsername(), bCryptPasswordEncoder.encode(joinForm.getPassword()), null, Role.ROLE_USER);
        memberRepository.save(member);

        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String userList(Model model) {
        PageRequest pageRequest = PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "id"));
        List<Member> memberList = memberRepository.findAll(pageRequest).getContent();
        model.addAttribute("memberList", memberList);
        return "userInformList";
    }

}
