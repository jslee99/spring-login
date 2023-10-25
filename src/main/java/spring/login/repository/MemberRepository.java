package spring.login.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.login.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m where m.provider = :provider and m.providerId = :providerId")
    Optional<Member> findByProviderAndProviderId(@Param(value = "provider")String provider,@Param(value = "providerId") String providerId);
    //단순한 function name은 x, @Query를 통해 child property에 접근해 줘야함,
    // 위 쿼리는 inheritance type 이 single이든 join이든 상관없이 기능한다.

    Optional<Member> findByUsername(String username);
}
