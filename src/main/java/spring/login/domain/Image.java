package spring.login.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

    public Image(String originalName, String storedName) {
        this.originalName = originalName;
        this.storedName = storedName;
    }

    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;
    private String originalName;
    private String storedName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }
}
