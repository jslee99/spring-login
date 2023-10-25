package spring.login.controller.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PwdUpdateForm {
    @NotBlank
    public String beforePwd;
    @NotBlank
    public String afterPwd;
}
