package spring.login.controller.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThSimpleBoardDto {
    private Long id;
    private String username;
    private String title;
}
