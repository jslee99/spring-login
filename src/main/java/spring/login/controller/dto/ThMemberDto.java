package spring.login.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring.login.domain.member.DefaultMember;
import spring.login.domain.member.Member;
import spring.login.domain.member.Oauth2Member;

@Data
public class ThMemberDto {

    public ThMemberDto(Member member) {
        id = member.getId();
        username = member.getUsername();
        email = member.getEmail();
        role = member.getRole().toString();
        if (member instanceof DefaultMember) {
            provider = "";
            providerId = "";
        } else if (member instanceof Oauth2Member) {
            Oauth2Member oauth2Member = (Oauth2Member) member;
            provider = oauth2Member.getProvider();
            providerId = oauth2Member.getProviderId();
        }
    }

    private Long id;
    private String username;
    private String email;
    private String role;
    private String provider;
    private String providerId;
}
