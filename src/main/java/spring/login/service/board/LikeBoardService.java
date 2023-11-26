package spring.login.service.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.domain.board.Board;
import spring.login.domain.board.LikeBoard;
import spring.login.domain.member.member.Member;
import spring.login.repository.LikeBoardRepository;
import spring.login.repository.BoardRepository;
import spring.login.repository.MemberRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LikeBoardService {

    private final BoardRepository boardRepository;
    private final LikeBoardRepository likeBoardRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public long getLikeCount(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        return likeBoardRepository.countByBoard(board);
    }

    public void addLikeCount(Long memberId, Long boardId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Board board = boardRepository.findById(boardId).orElseThrow();
        LikeBoard likeBoard = new LikeBoard(member, board);
        likeBoardRepository.save(likeBoard);
    }

    public boolean likeBoardExists(Long memberId, Long boardId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Board board = boardRepository.findById(boardId).orElseThrow();
        return likeBoardRepository.findByMemberAndBoard(member, board).isPresent();
    }
}
