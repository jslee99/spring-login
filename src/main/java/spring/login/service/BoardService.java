package spring.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.controller.dto.board.ThBoardDto;
import spring.login.domain.Board;
import spring.login.repository.BoardRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<ThBoardDto> findRecentBoard(int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Board> boardList = boardRepository.findAll(pageRequest).getContent();
        return boardList.stream()
                .map(ThBoardDto::new)
                .collect(Collectors.toList());
    }
}
