package spring.login.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.login.domain.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"member","images"})
    @Query("select b from Board b")
    Page<Board> findWithMemberAndImagesAll(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "images"})
    Optional<Board> findWithMemberAndImagesById(Long id);
}
