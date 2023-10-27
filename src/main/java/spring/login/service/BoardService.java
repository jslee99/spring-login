package spring.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.controller.dto.board.BoardCreateForm;
import spring.login.controller.dto.board.BoardUpdateForm;
import spring.login.controller.dto.board.ThBoardDto;
import spring.login.domain.Board;
import spring.login.domain.Image;
import spring.login.domain.member.Member;
import spring.login.repository.BoardRepository;
import spring.login.repository.ImageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public List<ThBoardDto> findRecentBoard(int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Board> boardList = boardRepository.findWithMemberAndImagesAll(pageRequest).getContent();
        log.info("before ThBoardDto::new");
        return boardList.stream()
                .map(ThBoardDto::new)
                .collect(Collectors.toList());
    }

    public void createBoard(Member member, BoardCreateForm boardCreateForm) {
        List<Image> images = imageService.saveMultipartFile(boardCreateForm.getImageFiles());
        Board board = new Board(boardCreateForm.getTitle(), boardCreateForm.getContent(), member);
        images.forEach(board::addImage);
        boardRepository.save(board);
    }

    public void updateBoard(Long boardId, BoardUpdateForm boardUpdateForm) {
        Board board = boardRepository.findWithMemberAndImagesById(boardId).orElseThrow();
        board.updateTitleAndContent(boardUpdateForm.getTitle(), boardUpdateForm.getContent());
        //board의 images list에서 image를 삭제하는 것은 의미없음 왜냐하면 연관관계 주인이 board가 아니기 때문, 따라서 생략한다. -> cascade persist이므로 삭제해줘야함.
        //delicate
        //그냥 delete할때는 board의 image list에서의 image entity와 image 자체 entity에 대한 데이터를 모두 삭제해주자.
        boardUpdateForm.getDeleteImages().forEach(board::removeImage);
        imageService.remove(boardUpdateForm.getDeleteImages());
        log.info("image size = {}", board.getImages().size());

        List<Image> images = imageService.saveMultipartFile(boardUpdateForm.getAddImages());
        images.forEach(board::addImage);
    }
}
