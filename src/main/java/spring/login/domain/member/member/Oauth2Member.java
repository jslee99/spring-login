package spring.login.domain.member.member;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.login.domain.member.Role;

@Entity
@Getter
@DiscriminatorValue(value = "Provider")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oauth2Member extends Member {

    public Oauth2Member(String username, String email, Role role, String provider, String providerId) {
        super(username, email, role);
        this.provider = provider;
        this.providerId = providerId;
    }

    private String provider;
    private String providerId;
}
