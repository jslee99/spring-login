package spring.login.security.jwtservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.domain.Member;
import spring.login.repository.MemberRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final MemberRepository memberRepository;

    public String createToken(String memberId, String username) {
        String jwtToken = JWT.create()
                .withSubject(memberId.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim(JwtProperties.CLAIM_ID, memberId)
                .withClaim(JwtProperties.CLAIM_USERNAME, username)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        return jwtToken;
    }

    /**
     * authenticated(가입된) 유저인지 확인
     * @param encodedJwtToken
     * @return 가입된 유저이면 Member return 아니면 null return
     * @throws UnsupportedEncodingException
     */
    @Transactional(readOnly = true)
    public Member verifyToken(String encodedJwtToken) throws UnsupportedEncodingException {
        String jwtToken = URLDecoder.decode(encodedJwtToken, "UTF-8");
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken);
        Long memberId = Long.valueOf(decodedJWT.getClaim(JwtProperties.CLAIM_ID).asString());
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        return optionalMember.orElse(null);
    }
}
