package spring.login.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.domain.member.member.DefaultMember;
import spring.login.domain.member.member.Member;
import spring.login.repository.MemberRepository;

import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void updateUserInfo(Long id, String username, String email) {//must check duplicate username before
        Member member = memberRepository.findById(id).orElseThrow(NoSuchElementException::new);
        member.setUsernameAndEmail(username, email);
    }

    public void updatePwd(Long id, String afterEncodedPwd) {
        DefaultMember member = (DefaultMember) memberRepository.findById(id).orElseThrow();
        member.setPassword(afterEncodedPwd);
    }
}
