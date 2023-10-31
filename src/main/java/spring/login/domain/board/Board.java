package spring.login.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.login.domain.BaseTimeEntity;
import spring.login.domain.member.member.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    public Board(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    private List<Image> images = new ArrayList<>();

    public void addImage(Image image) {
        images.add(image);
        image.setBoard(this);
    }

    public Image deleteImage(Long imageId) {
        Image removedImage = images.stream()
                .filter(image -> image.getId().equals(imageId))
                .findAny()
                .orElseThrow();
        images.removeIf(image -> image.getId().equals(imageId));
        return removedImage;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
