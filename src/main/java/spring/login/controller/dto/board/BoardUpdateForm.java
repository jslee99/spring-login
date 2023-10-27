package spring.login.controller.dto.board;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardUpdateForm {
    private String title;
    private String content;
    private List<Long> deleteImages = new ArrayList<>();
    private List<MultipartFile> addImages = new ArrayList<>();
}
