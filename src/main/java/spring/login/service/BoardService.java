package spring.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.controller.dto.board.BoardCreateForm;
import spring.login.controller.dto.board.ThBoardDto;
import spring.login.domain.Board;
import spring.login.domain.Image;
import spring.login.domain.member.Member;
import spring.login.repository.BoardRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public List<ThBoardDto> findRecentBoard(int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Board> boardList = boardRepository.findAllWithMember(pageRequest).getContent();
        log.info("before ThBoardDto::new");
        return boardList.stream()
                .map(ThBoardDto::new)
                .collect(Collectors.toList());
    }

    public void createBoard(Member member, BoardCreateForm boardCreateForm) {
        List<Image> images = imageService.processMultipartFile(boardCreateForm.getImageFiles());
        Board board = new Board(boardCreateForm.getTitle(), boardCreateForm.getContent(), member);
        images.forEach(board::addImage);
        boardRepository.save(board);
    }
}
