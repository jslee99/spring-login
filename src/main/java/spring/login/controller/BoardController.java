package spring.login.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import spring.login.controller.dto.board.*;
import spring.login.controller.dto.member.ThMemberDto;
import spring.login.domain.board.Board;
import spring.login.domain.member.member.Member;
import spring.login.etc.Pair;
import spring.login.security.principal.PrincipalDetail;
import spring.login.service.board.BoardService;
import spring.login.service.board.CommentService;
import spring.login.service.board.LikeBoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final LikeBoardService likeBoardService;

    @GetMapping
    public String getBoardList(@RequestParam(defaultValue = "1") int frontPageNum, Model model) {
        int pageNum = frontPageNum - 1;
        Pair<Page<Board>, List<ThSimpleBoardDto>> pair = boardService.findRecentBoard(pageNum, 10);

        Page<Board> paging = pair.getFirst();
        int totalPages = paging.getTotalPages();

        List<ThSimpleBoardDto> boardList = pair.getSecond();

        model.addAttribute("boardList", boardList);
        model.addAttribute("currentPage", frontPageNum);
        model.addAttribute("lastPage", totalPages);
        model.addAttribute("pageCollectNum", 10);//밑에 pageBox에서 한번에 보여줄 페이지 개수
        return "board/boardHome";
    }

    @GetMapping("/{boardId}")
    public String getBoard(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable("boardId") Long boardId, Model model) {
        ThBoardDto thBoardDto = boardService.findBoard(boardId);
        model.addAttribute("board", thBoardDto);
        List<ThCommentDto> comments = commentService.findComments(boardId);
        model.addAttribute("comments", comments);
        long likeCount = likeBoardService.getLikeCount(boardId);
        model.addAttribute("likeCount", likeCount);
        if (principalDetail != null) {
            model.addAttribute("member", new ThMemberDto(principalDetail.getMember()));
        }
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
        Long boardId = boardService.createBoard(principalDetail.getMember().getId(), boardCreateForm);
        return "redirect:/board/" + boardId;
    }

    @GetMapping("/{boardId}/update")
    public String getUpdateForm(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId, Model model) {
        ThBoardDto thBoardDto = boardService.findBoard(boardId);
        if (!thBoardDto.getMemberId().equals(principalDetail.getMember().getId())) {
            return "redirect:/board/" + boardId;
        }
        model.addAttribute("member", principalDetail.getMember());
        model.addAttribute("board", thBoardDto);
        return "board/updateForm";
    }

    //List<MultipartFile> 사용시 주의점 -> 파일을 하나도 보내지 않으면 빈 껍데기 파일(key-value pair)을 보낸다.
    //https://stackoverflow.com/questions/51323018/thymeleaf-multiple-file-input-sends-empty-file-when-nothing-selected
    @PostMapping("/{boardId}/update")
    public String postUpdateForm(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId, BoardUpdateForm boardUpdateForm) {
        boardUpdateForm.setAddImages(boardUpdateForm.getAddImages().stream().filter(image -> !image.isEmpty()).collect(Collectors.toList()));
        ThBoardDto thBoardDto = boardService.findBoard(boardId);
        if (!thBoardDto.getMemberId().equals(principalDetail.getMember().getId())) {
            return "redirect:/board/" + boardId;
        }
        boardService.updateBoard(boardId, boardUpdateForm);
        return "redirect:/board ";
    }

    @PostMapping("/{boardId}/comment")
    public String addComment(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId, @RequestParam String comment) {
        commentService.addComment(boardId, principalDetail.getMember().getId(), comment);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{boardId}/delete")
    public String deleteBoard(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId) {
        Member member = principalDetail.getMember();
        ThBoardDto board = boardService.findBoard(boardId);
        if (!board.getMemberId().equals(member.getId())) {
            return "redirect:/board/" + boardId;
        }
        commentService.delete(boardId);
        boardService.delete(boardId);
        return "redirect:/board";
    }

    @PostMapping("/{boardId}/add-like")
    public String addLike(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId) {
        if (principalDetail == null || likeBoardService.likeBoardExists(principalDetail.getMember().getId(), boardId)) {
            return "redirect:/board/" + boardId;
        }
        Member member = principalDetail.getMember();
        likeBoardService.addLikeCount(member.getId(), boardId);
        return "redirect:/board/" + boardId;
    }

    @GetMapping("/{boardId}/like-exists")
    @ResponseBody
    public boolean likeExists(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long boardId) {
        if (principalDetail == null || likeBoardService.likeBoardExists(principalDetail.getMember().getId(), boardId)) {
            return true;
        }else{
            return false;
        }
    }
}
//체크박스 : 체크한 상태로 post 전송 -> name : true로 전송 / 체크 하지 않은 상태로 post 전송 -> 아예 보내지 않음
//타임리프의 th:field기능을 쓰면 체크하면 name : true / 체크하지 않으면 _name : on으로 전송하게 된다. 이것을 true/false로 변환하여 컨트롤러에서 작동하게됨