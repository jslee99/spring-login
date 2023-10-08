package spring.login.security.jwtservice;

public interface JwtProperties {
	String SECRET = "cos"; // 우리 서버만 알고 있는 비밀값
	int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
	String TOKEN_PREFIX = "Bearer ";
	String COOKIE_KEY_AUTHORIZATION = "Authorization";
	String CLAIM_ID = "id";
	String CLAIM_USERNAME = "username";
}
