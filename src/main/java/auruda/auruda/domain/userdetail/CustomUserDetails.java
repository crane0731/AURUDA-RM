package auruda.auruda.domain.userdetail;

import auruda.auruda.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 커스텀 UserDetails ->Spring Security에 필요
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    /**
     * 회원 아이디 반환
     *
     * @return 회원 아이디
     */
    public Long getId() {
        return member.getId();
    }

    /**
     * 권한 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
    }

    /**
     * 사용자의 비밀번호를 반환
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 사용자의 id를 반환 (고유한 값)
     */
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    /**
     * 계정 만료여부 반환
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금여부 반환
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 패드워드 만료 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 사용 여부 반환
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
