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
import spring.login.controller.dto.board.BoardUpdateForm;
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
        ThBoardDto thBoardDto = boardRepository.findWithMemberAndImagesById(boardId).map(ThBoardDto::new).orElseThrow();
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
    public String createBoard(@AuthenticationPrincipal PrincipalDetail principalDetail, @ModelAttribute BoardCreateForm boardCreateForm) {
        boardService.createBoard(principalDetail.getMember(), boardCreateForm);
        return "redirect:/board";
    }

    @GetMapping("/{boardId}/update")
    public String getUpdateForm(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId, Model model) {
        Board board = boardRepository.findWithMemberAndImagesById(boardId).orElseThrow();
        if (board.getMember().getId() != principalDetail.getMember().getId()) {
            return "redirect:/board/" + boardId;
        }
        ThBoardDto thBoardDto = new ThBoardDto(board);
        model.addAttribute("member", principalDetail.getMember());
        model.addAttribute("board", thBoardDto);
        return "board/updateForm";
    }

    @PostMapping("/{boardId}/update")
    public String postUpdateForm(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId, BoardUpdateForm boardUpdateForm) {
        Board board = boardRepository.findWithMemberAndImagesById(boardId).orElseThrow();
        if (board.getMember().getId() != principalDetail.getMember().getId()) {
            return "redirect:/board/" + boardId;
        }
        boardService.updateBoard(boardId, boardUpdateForm);
        return "redirect:/board/" + boardId;
    }
}
//체크박스 : 체크한 상태로 post 전송 -> name : true로 전송 / 체크 하지 않은 상태로 post 전송 -> 아예 보내지 않음
//타임리프의 th:field기능을 쓰면 체크하면 name : true / 체크하지 않으면 _name : on으로 전송하게 된다. 이것을 true/false로 변환하여 컨트롤러에서 작동하게됨