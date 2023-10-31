package spring.login.service.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.controller.dto.board.ThCommentDto;
import spring.login.domain.board.Board;
import spring.login.domain.board.Comment;
import spring.login.domain.member.member.Member;
import spring.login.repository.BoardRepository;
import spring.login.repository.CommentRepository;
import spring.login.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public void addComment(Long boardId, Long memberId, String content) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Member member = memberRepository.findById(memberId).orElseThrow();
        Comment comment = new Comment(content, board, member);
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<ThCommentDto> findComments(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        List<Comment> comments = commentRepository.findCommentsByBoard(board);
        return comments.stream()
                .map(ThCommentDto::new)
                .collect(Collectors.toList());
    }

    public void delete(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        commentRepository.deleteByBoard(board);
    }
}
