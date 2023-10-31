package spring.login.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.login.domain.member.Follow;
import spring.login.domain.member.member.Member;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFromAndTo(Member from, Member to);

    @EntityGraph(attributePaths = "to")
    List<Follow> findByFrom(Member from);

    @EntityGraph(attributePaths = "from")
    List<Follow> findByTo(Member to);
}
