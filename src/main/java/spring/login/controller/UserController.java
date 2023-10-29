package spring.login.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.login.controller.dto.member.PwdUpdateForm;
import spring.login.controller.dto.member.ThMemberDto;
import spring.login.controller.dto.member.UpdateForm;
import spring.login.domain.member.DefaultMember;
import spring.login.domain.member.Member;
import spring.login.repository.MemberRepository;
import spring.login.security.principal.PrincipalDetail;
import spring.login.service.MemberService;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping
    public String User(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        Long id = Long.valueOf(principalDetail.getName());
        ThMemberDto findMember = memberRepository.findById(id).map(ThMemberDto::new).orElseThrow();
        model.addAttribute("member", findMember);
        return "user/userInform";
    }

    @GetMapping("/update")
    public String getUserForm(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        Long id = principalDetail.getMember().getId();
        ThMemberDto findMember = memberRepository.findById(id).map(ThMemberDto::new).orElseThrow();
        model.addAttribute("member", findMember);
        return "user/userUpdateForm";
    }

    @PostMapping("/update")
    public String postUserUpdateForm(@AuthenticationPrincipal PrincipalDetail principalDetail, @Validated @ModelAttribute("member") UpdateForm updateForm, BindingResult bindingResult) {
        String username = updateForm.getUsername();
        String email = updateForm.getEmail();
        Long memberId = principalDetail.getMember().getId();
        Optional<Member> byUsername = memberRepository.findByUsername(username);
        if (byUsername.isPresent() && !byUsername.get().getId().equals(memberId)) {
            bindingResult.rejectValue("username", "duplicatedUsername", new Object[]{updateForm.getUsername()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "user/userUpdateForm";
        }

        memberService.updateUserInfo(memberId, username, email);
        return "redirect:/user";
    }

    @GetMapping("/update/pwd")
    public String getPwdForm(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        if (!(principalDetail.getMember() instanceof DefaultMember)) {
            return "redirect:/";
        }
        model.addAttribute("member", new ThMemberDto(principalDetail.getMember()));
        model.addAttribute("pwdForm", new PwdUpdateForm());
        return "user/pwdUpdateForm";
    }

    @PostMapping("/update/pwd")
    public String updatePwd(
            @AuthenticationPrincipal PrincipalDetail principalDetail,
            @Validated @ModelAttribute("pwdForm") PwdUpdateForm pwdUpdateForm,
            BindingResult bindingResult,
            Model model) {
        Member findMember = principalDetail.getMember();
        if (!(findMember instanceof DefaultMember member)) {
            return "redirect:/";
        }

        String beforePwd = pwdUpdateForm.getBeforePwd();

        if (!bCryptPasswordEncoder.matches(beforePwd, member.getPassword())) {
            bindingResult.rejectValue("beforePwd", "incorrectBeforePwd");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("member", member);
            return "user/pwdUpdateForm";
        }

        memberService.updatePwd(member.getId(), bCryptPasswordEncoder.encode(pwdUpdateForm.getAfterPwd()));
        return "redirect:/";
    }
}
