package spring.login.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.login.domain.board.Board;
import spring.login.domain.board.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

//    @EntityGraph(attributePaths = "member")
//    @Query("select c from Comment c left join c.board b on b.id = :boardId")
//    List<Comment> findCommentsFetchMemberByBoard(@Param("boardId") Long boardId);

    @EntityGraph(attributePaths = "member")
    List<Comment> findCommentsByBoard(Board board);

    void deleteByBoard(Board board);
}
