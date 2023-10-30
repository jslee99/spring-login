package spring.login.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.login.domain.Board;
import spring.login.domain.member.Member;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"member"})
    @Query("select b from Board b")
    Page<Board> findFetchMemberAll(Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Optional<Board> findFetchMemberById(Long id);

    @EntityGraph(attributePaths = "member")
    Page<Board> findFetchMemberByMember(Member member, Pageable pageable);
}
