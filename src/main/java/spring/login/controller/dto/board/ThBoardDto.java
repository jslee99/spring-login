package spring.login.controller.dto.board;

import lombok.Data;
import spring.login.domain.Board;
import spring.login.domain.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ThBoardDto {

    public ThBoardDto(Board board) {
        this.username = board.getMember().getUsername();
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.images = board.getImages().stream()
                .map(Image::getId)
                .collect(Collectors.toList());
        this.comments = board.getComments().stream()
                .map(comment -> new ThCommentDto(comment.getMember().getUsername(), comment.getContent()))
                .collect(Collectors.toList());
    }

    private String username;
    private Long id;
    private String title;
    private String content;
    private List<Long> images;
    private List<ThCommentDto> comments;
}
