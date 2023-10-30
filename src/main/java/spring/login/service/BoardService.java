package spring.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.controller.dto.board.*;
import spring.login.domain.Board;
import spring.login.domain.Image;
import spring.login.domain.member.Member;
import spring.login.repository.BoardRepository;
import spring.login.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final ImageService imageService;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ThSimpleBoardDto> findRecentBoard(int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Board> boardList = boardRepository.findFetchMemberAll(pageRequest).getContent();
        log.info("before ThBoardDto::new");
        return boardList.stream()
                .map(board -> new ThSimpleBoardDto(board.getId(),board.getMember().getUsername(), board.getTitle()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ThBoardDto findBoard(Long boardId) {
        Board board = boardRepository.findFetchMemberImagesById(boardId).orElseThrow();
        return new ThBoardDto(board);
    }

    public Long createBoard(Long memberId, BoardCreateForm boardCreateForm) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Image> images = imageService.saveMultipartFile(boardCreateForm.getImageFiles());
        Board board = new Board(boardCreateForm.getTitle(), boardCreateForm.getContent(), member);
        images.forEach(board::addImage);
        Board saveBoard = boardRepository.save(board);
        return saveBoard.getId();
    }

    public void updateBoard(Long boardId, BoardUpdateForm boardUpdateForm) {
        log.info("add image size = {}", boardUpdateForm.getAddImages().size());
        Board board = boardRepository.findFetchMemberImagesById(boardId).orElseThrow();
        board.updateTitleAndContent(boardUpdateForm.getTitle(), boardUpdateForm.getContent());
        //board의 images list에서 image를 삭제하는 것은 의미없음 왜냐하면 연관관계 주인이 board가 아니기 때문, 따라서 생략한다. -> cascade persist이므로 삭제해줘야함.
        //delicate
        //그냥 delete할때는 board의 image list에서의 image entity와 image 자체 entity에 대한 데이터를 모두 삭제해주자.
        List<Image> deleteImageList = boardUpdateForm.getDeleteImages().
                stream()
                .map(board::deleteImage)
                .collect(Collectors.toList());
        imageService.delete(deleteImageList);
        log.info("image size = {}", board.getImages().size());

        List<Image> images = imageService.saveMultipartFile(boardUpdateForm.getAddImages());
        images.forEach(board::addImage);
    }


    public void delete(Long boardId) {
        Board board = boardRepository.findFetchMemberImagesById(boardId).orElseThrow();
        imageService.delete(board.getImages());
        boardRepository.delete(board);
    }
}
