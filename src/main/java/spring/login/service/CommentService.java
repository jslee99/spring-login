package spring.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.login.controller.dto.board.ThCommentDto;
import spring.login.domain.Board;
import spring.login.domain.Comment;
import spring.login.domain.member.Member;
import spring.login.repository.BoardRepository;
import spring.login.repository.CommentRepository;
import spring.login.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
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

    public List<ThCommentDto> findCommentsByBoardId(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        List<Comment> comments = commentRepository.findCommentsFetchMemberByBoard(board);
        return comments.stream()
                .map(comment -> new ThCommentDto(comment.getMember().getUsername(), comment.getContent()))
                .collect(Collectors.toList());
    }
}
