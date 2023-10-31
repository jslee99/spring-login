package spring.login.controller.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring.login.domain.board.Comment;

@Data
public class ThCommentDto {

    public ThCommentDto(Comment comment) {
        username = comment.getMember().getUsername();
        content = comment.getContent();
    }

    private String username;
    private String content;
}
