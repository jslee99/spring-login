package spring.login.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.login.security.jwtservice.JwtProperties;
import spring.login.security.jwtservice.JwtTokenService;
import spring.login.security.oauth2.PrincipalOauth2UserService;
import spring.login.security.filter.JwtVerifyFilter;
import spring.login.security.handler.JwtLoginSuccessHandler;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final JwtTokenService jwtTokenService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.addFilterBefore(new JwtVerifyFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class);

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authorize->authorize.requestMatchers("/user/**").authenticated());
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        http.httpBasic(httpBasic->httpBasic.disable());

        http.formLogin(login->login.loginPage("/login").loginProcessingUrl("/loginProcess").defaultSuccessUrl("/"));

        http.oauth2Login(oauth2 -> oauth2.loginPage("/login").userInfoEndpoint(endpoint -> endpoint.userService(principalOauth2UserService)).successHandler(new JwtLoginSuccessHandler(jwtTokenService)));

        http.logout(logout -> logout.logoutUrl("/logout").deleteCookies(JwtProperties.COOKIE_KEY_STRING).logoutSuccessUrl("/"));
        //logout controller

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //spring security는 기본적으로 session을 이용한 stateful 상태이를이용하는데 이를 쿠키를 이용한 stateless 로 바꾼다.
        return http.build();
    }
}
