package spring.login.controller.dto.board;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardCreateForm {
    private String title;
    private String content;
    private List<MultipartFile> imageFiles = new ArrayList<>();
}
