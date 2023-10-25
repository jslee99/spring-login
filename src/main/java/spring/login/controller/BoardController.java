package spring.login.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import spring.login.controller.dto.board.BoardCreateForm;
import spring.login.controller.dto.board.ThBoardDto;
import spring.login.controller.dto.member.ThMemberDto;
import spring.login.domain.Board;
import spring.login.repository.BoardRepository;
import spring.login.security.principal.PrincipalDetail;
import spring.login.service.BoardService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @GetMapping
    public String getBoardList(Model model) {
        List<ThBoardDto> boardList = boardService.findRecentBoard(0, 10);
        model.addAttribute("boardList", boardList);
        return "board/boardList";
    }

    @GetMapping("/{boardId}")
    public String getBoard(@PathVariable("boardId") Long boardId, Model model) {
        ThBoardDto thBoardDto = boardRepository.findWithMemberById(boardId).map(ThBoardDto::new).orElseThrow();
        model.addAttribute("board", thBoardDto);
        return "board/board";
    }

    @GetMapping("/create")
    public String getCreateForm(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        ThMemberDto memberDto = new ThMemberDto(principalDetail.getMember());
        model.addAttribute("member", memberDto);
        model.addAttribute("board", new BoardCreateForm());
        return "board/createForm";
    }

    @PostMapping("/create")
    public String createBoard(@AuthenticationPrincipal PrincipalDetail principalDetail, BoardCreateForm boardCreateForm) {
        Board board = new Board(boardCreateForm.getTitle(), boardCreateForm.getContent(), principalDetail.getMember());
        Board save = boardRepository.save(board);
        return "redirect:/board/" + save.getId();
    }
}
