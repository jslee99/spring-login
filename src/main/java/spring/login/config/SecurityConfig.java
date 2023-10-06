package spring.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import spring.login.config.oauth.PrincipalOauth2UserService;
import spring.login.config.oauth.filter.JWTAuthorizeFilter;
import spring.login.config.oauth.handler.LoginSuccessHandler;
import spring.login.repository.MemberRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    PrincipalOauth2UserService principalOauth2UserService;
    @Autowired
    MemberRepository memberRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.addFilterBefore(new JWTAuthorizeFilter(memberRepository), UsernamePasswordAuthenticationFilter.class);

        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.authorizeHttpRequests(authorize->authorize.requestMatchers("/user/**").authenticated());
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        http.httpBasic(httpBasic->httpBasic.disable());

        http.formLogin(login->login.loginPage("/login").loginProcessingUrl("/loginProcess").defaultSuccessUrl("/"));

        http.oauth2Login(oauth2 -> oauth2.loginPage("/login").userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService)).successHandler(new LoginSuccessHandler()));

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
