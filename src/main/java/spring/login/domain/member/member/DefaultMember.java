package spring.login.domain.member.member;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.login.domain.member.Role;

@Entity
@Getter
@DiscriminatorValue(value = "Default")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultMember extends Member{

    public DefaultMember(String username, String password, String email, Role role) {
        super(username, email, role);
        this.password = password;
    }

    private String password;

    public void setPassword(String password) {
        this.password = password;
    }
}
