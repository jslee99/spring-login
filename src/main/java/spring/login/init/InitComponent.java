package spring.login.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import spring.login.domain.Board;
import spring.login.domain.member.DefaultMember;
import spring.login.domain.member.Member;
import spring.login.domain.member.Role;
import spring.login.repository.BoardRepository;
import spring.login.repository.MemberRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitComponent {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void initMember() {
        Member member = null;
        try {
            member = new DefaultMember("js", bCryptPasswordEncoder.encode("js"), "junsub_lee@naver.com", Role.ROLE_ADMIN);
            memberRepository.save(member);
        } catch (Exception e) {
            log.error("at init member", e);
        }

        try {
            Member member2 = new DefaultMember("js2", bCryptPasswordEncoder.encode("js2"), "junsub_lee@naver.com", Role.ROLE_ADMIN);
            memberRepository.save(member2);
        } catch (Exception e) {
            log.error("at init member", e);
        }

        Board sample1 = new Board("sample1", "sampletext", member);
        Board sample2 = new Board("sample2", "sampletext", member);
        boardRepository.save(sample1);
        boardRepository.save(sample2);
    }
}
