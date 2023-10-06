package spring.login.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import spring.login.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    Optional<Member> findByUsername(String username);
}
