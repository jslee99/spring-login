package spring.login.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spring.login.controller.dto.board.BoardCreateForm;
import spring.login.domain.board.Board;
import spring.login.domain.board.Image;
import spring.login.domain.member.member.DefaultMember;
import spring.login.domain.member.member.Member;
import spring.login.domain.member.Role;
import spring.login.repository.BoardRepository;
import spring.login.repository.MemberRepository;
import spring.login.service.board.BoardService;
import spring.login.service.board.CommentService;
import spring.login.service.member.FollowService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitComponent {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final CommentService commentService;
    private final FollowService followService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${image.local.storage.baseurl}")
    private String localStorageBaseUrl;

    @PostConstruct
    public void initStorage() {
        File folder = new File("localImageStorage");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    @PostConstruct
    @Transactional
    public void init() {
        Member member = new DefaultMember("js", bCryptPasswordEncoder.encode("js"), "junsub_lee@naver.com", Role.ROLE_ADMIN);
        memberRepository.save(member);
        Member member2 = new DefaultMember("js2", bCryptPasswordEncoder.encode("js2"), "junsub_lee@naver.com", Role.ROLE_ADMIN);
        memberRepository.save(member2);

        Board board1 = new Board("board1", "sampletext", member);
        Board board2 = new Board("board2", "sampletext", member2);
        Board board3 = new Board("board3", "sampletxt", member);
        File copyFile = null;
        try{
            copyFile = new File(localStorageBaseUrl + '/' + UUID.randomUUID() + ".PNG");
            copyFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(copyFile);
            FileInputStream fis = new FileInputStream(new File(localStorageBaseUrl + "/sample1.PNG"));
            fos.write(fis.readAllBytes());
            fos.close();
            fis.close();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        Image image1 = new Image("sample1.PNG", copyFile.getName());
        board1.addImage(image1);
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);

        for (int i = 1; i <= 210; i++) {
            Board board = new Board("title" + i, "content" + i, member);
            boardRepository.save(board);
        }

        commentService.addComment(board1.getId(), member.getId(), "sample comment");
        commentService.addComment(board1.getId(), member2.getId(), "sample comment");

        followService.follow(member.getId(), member2.getId());
    }


}
