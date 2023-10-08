package spring.login.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import spring.login.domain.Member;
import spring.login.domain.Role;
import spring.login.repository.MemberRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitComponent {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void initMember() {
        try {
            Member member = new Member("js", bCryptPasswordEncoder.encode("js"), "junsub_lee@naver.com", Role.ROLE_ADMIN);
            memberRepository.save(member);
        } catch (Exception e) {
            log.error("at init member", e);
        }

    }
}
