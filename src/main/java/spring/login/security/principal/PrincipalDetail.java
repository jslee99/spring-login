package spring.login.security.principal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.login.domain.member.member.DefaultMember;
import spring.login.domain.member.member.Member;

import java.util.*;

@Slf4j
public class PrincipalDetail implements UserDetails, OAuth2User {

    private Member member;
    private Map<String, Object> attributes;

    public PrincipalDetail(Member member) {
        this.member = member;
        this.attributes = new LinkedHashMap<>();
    }

    public PrincipalDetail(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public String getName() {
        return member.getId().toString();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> roleList = new ArrayList<>();
        roleList.add(() -> member.getRole().toString());
        return roleList;
    }

    @Override
    public String getPassword() {
        if (member instanceof DefaultMember) {
            return ((DefaultMember)member).getPassword();
        }else{
            return null;
        }
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
