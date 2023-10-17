package spring.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.domain.Member;
import spring.login.repository.MemberRepository;

import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void updateUsername(Long id, String username, String email) {
        Member member = memberRepository.findById(id).orElseThrow(NoSuchElementException::new);
        member.setUsernameAndEmail(username, email);
    }
}
