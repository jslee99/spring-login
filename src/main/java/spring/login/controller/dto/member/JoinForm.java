package spring.login.controller.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinForm {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
