package spring.login.controller.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThCommentDto {
    private String username;
    private String content;
}
