package spring.login.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.login.domain.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"member"})
    Optional<Board> findWithMemberById(Long id);
}
