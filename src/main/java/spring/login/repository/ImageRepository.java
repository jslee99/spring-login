package spring.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.login.domain.board.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
