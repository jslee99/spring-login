package spring.login.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.login.domain.BaseTimeEntity;
import spring.login.domain.member.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "fromToUnique",
                        columnNames = {"from_member_id", "to_member_id"}
                )
        }
)
public class Follow extends BaseTimeEntity {

    public Follow(Member from, Member to) {
        this.from = from;
        this.to = to;
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_member_id")
    private Member from;

    @ManyToOne
    @JoinColumn(name = "to_member_id")
    private Member to;
}
