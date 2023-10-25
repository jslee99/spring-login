package spring.login.controller.dto.board;

import lombok.Data;
import spring.login.domain.Board;

@Data
public class ThBoardDto {

    public ThBoardDto(Board board) {
        this.username = board.getMember().getUsername();
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
    }

    private String username;
    private Long id;
    private String title;
    private String content;
}
