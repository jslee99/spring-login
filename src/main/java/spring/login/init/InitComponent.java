package spring.login.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import spring.login.domain.Board;
import spring.login.domain.Image;
import spring.login.domain.member.DefaultMember;
import spring.login.domain.member.Member;
import spring.login.domain.member.Role;
import spring.login.repository.BoardRepository;
import spring.login.repository.MemberRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitComponent {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${image.local.storage.baseurl}")
    private String localStorageBaseUrl;

    @PostConstruct
    public void init() {
        Member member = null;
        try {
            member = new DefaultMember("js", bCryptPasswordEncoder.encode("js"), "junsub_lee@naver.com", Role.ROLE_ADMIN);
            memberRepository.save(member);
        } catch (Exception e) {
            log.error("at init member", e);
        }

        try {
            Member member2 = new DefaultMember("js2", bCryptPasswordEncoder.encode("js2"), "junsub_lee@naver.com", Role.ROLE_ADMIN);
            memberRepository.save(member2);
        } catch (Exception e) {
            log.error("at init member", e);
        }

        Board board1 = new Board("board1", "sampletext", member);
        Board board2 = new Board("board2", "sampletext", member);
//        try{
//            File file1 = new File(localStorageBaseUrl + "/sample1.PNG");
//            File file2 = new File(localStorageBaseUrl + "/sample2.PNG");
//        }catch(IOException e){
//
//        }
        Image image1 = new Image("board1", "sample1.PNG");
        Image image2 = new Image("board2", "sample2.PNG");
        board1.addImage(image1);
        board1.addImage(image2);

        boardRepository.save(board1);
        boardRepository.save(board2);

    }

    @PostConstruct
    public void initStorage() {
        File folder = new File("localImageStorage");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
}
