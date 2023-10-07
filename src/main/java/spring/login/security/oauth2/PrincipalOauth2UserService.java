package spring.login.security.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.login.security.principal.PrincipalDetails;
import spring.login.security.oauth2.provider.GoogleUserInfo;
import spring.login.security.oauth2.provider.OAuth2UserInfo;
import spring.login.domain.Member;
import spring.login.domain.Role;
import spring.login.repository.MemberRepository;

import java.util.Optional;

@Service
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Autowired
    public PrincipalOauth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //user request : accessToken 이를 이용해서 user의 정보를 가지고오면 그게 oAuth2User
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("userRequest registration = {}", userRequest.getClientRegistration());
        log.info("oAuth2User = {}", oAuth2User);

        PrincipalDetails principalDetails = Oauth2UserToPrincipalDetails(userRequest, oAuth2User);
        return principalDetails;
    }

    private PrincipalDetails Oauth2UserToPrincipalDetails(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = null;
        String registration = userRequest.getClientRegistration().getRegistrationId();
        if (registration.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            log.info("google 소셜 로그인");
        }else{
            log.info("지원되지 않는 소셜 로그인");
        }

        Optional<Member> memberOptional = memberRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());

        Member member;
        if (memberOptional.isPresent()) {
            //이미 회원가입 되어 있음
            member = memberOptional.get();
        }else{
            Member newMember = new Member(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId(), oAuth2UserInfo.getEmail(), Role.ROLE_USER, oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
            member = memberRepository.save(newMember);
        }

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
