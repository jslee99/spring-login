package spring.login.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    public Member(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isOauth2Member = false;
    }

    public Member(String username, String email, Role role, String provider, String providerId) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.isOauth2Member = true;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean isOauth2Member;
    private String provider;
    private String providerId;
}
