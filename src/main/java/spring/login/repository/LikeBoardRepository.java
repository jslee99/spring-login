package spring.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.login.domain.board.Board;
import spring.login.domain.board.LikeBoard;
import spring.login.domain.member.member.Member;

import java.util.Optional;

public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long> {

    Long countByBoard(Board board);

    Optional<LikeBoard> findByMemberAndBoard(Member member, Board board);
}
