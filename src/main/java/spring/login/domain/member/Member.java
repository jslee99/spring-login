package spring.login.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.login.domain.BaseTimeEntity;
import spring.login.domain.Board;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "mType")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Member extends BaseTimeEntity {

    public Member(String username, String email, Role role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    @Column(unique = true)
    private String username;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    public void setUsernameAndEmail(String username, String email) {
        this.username = username;
        this.email = email;
    }


}
