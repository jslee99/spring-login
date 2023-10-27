package spring.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.login.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
