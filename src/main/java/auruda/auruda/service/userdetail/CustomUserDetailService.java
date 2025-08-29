package auruda.auruda.service.userdetail;

import auruda.auruda.domain.Member;
import auruda.auruda.domain.userdetail.CustomUserDetails;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CustomUserDetails를 생성하는 서비스 -> Spring Security에 필요
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;


    /**
     * 사용자 이름(loginId)으로 사용자의 정보를 가져오는 메서드
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException(ErrorMessage.NOT_FOUND_MEMBER));

        return new CustomUserDetails(member);
    }


}

