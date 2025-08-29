package auruda.auruda.service.member;

import auruda.auruda.domain.Member;
import auruda.auruda.dto.member.MemberInfoResponseDto;
import auruda.auruda.dto.member.SearchMemberCondDto;
import auruda.auruda.dto.member.UpdateMemberNicknameRequestDto;
import auruda.auruda.dto.member.UpdateMemberPasswordRequestDto;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.MemberCustomException;
import auruda.auruda.repository.member.MemberRepository;
import auruda.auruda.service.token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final RefreshTokenService refreshTokenService; //리프레쉬 토큰 서비스

    private final MemberRepository memberRepository;//회원 레파지토리

    private final BCryptPasswordEncoder bCryptPasswordEncoder; //패스워드 인코더

    /**
     * [서비스 로직]
     * 회원 자신의 정보 상세 조회
     * @return MemberInfoResponseDto
     */
    public MemberInfoResponseDto findMyInfo(){

        //현재 로그인한 회원 조회
        Member member = findLoginMember();

        //응답 DTO 생성 + 반환
        return MemberInfoResponseDto.create(member);
    }

    /**
     * [서비스 로직]
     * 회원 자신의 닉네임 업데이트
     * @param dto 닉네임 업데이트 요청 DTO
     */
    @Transactional
    public void updateMyNickname(UpdateMemberNicknameRequestDto dto){

        //현재 로그인한 회원 조회
        Member member = findLoginMember();

        //변경할 새로운 닉네임
        String newNickname = dto.getNickname();

        // 현재 닉네임과 동일하면 변경 불필요 → 그냥 리턴
        if (member.getNickname().equals(newNickname)) {
            return;
        }

        //닉네임 중복 검증
        if(existsByNickname(newNickname)){
           throw new MemberCustomException(ErrorMessage.DUPLICATED_NICKNAME);
        }

        //닉네임 업데이트
        member.updateNickname(newNickname);
    }

    /**
     * [서비스 로직]
     * 회원 자신의 패스워드 업데이트
     * @param dto 패스워드 업데이트 요청 DTO
     */
    @Transactional
    public void updateMyPassword(UpdateMemberPasswordRequestDto dto){

        //현재 로그인한 회원 조회
        Member member = findLoginMember();

        //패스워드 & 패스워드 체크 검증
        if(!dto.getPassword().equals(dto.getPasswordCheck())){
            throw new MemberCustomException(ErrorMessage.PASSWORD_MISMATCH);
        }

        //변경할 패스워드 인코딩 + 업데이트
        member.updatePassword(bCryptPasswordEncoder.encode(dto.getPassword()));

    }

    /**
     * [서비스 로직]
     * 현재 스프링시큐리티에 로그인된 회원 정보 가져오기
     * @return Member
     */
    public Member findLoginMember(){

        //시큐리티 컨텍스트 홀더에서 인증 객체 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //설정한 username (email) 조회
        String email = authentication.getName();

        // Member 반환
        return findByEmail(email);
    }


    /**
     * [서비스 로직]
     * 회원 탈퇴(SOFT DELETE)
     */
    @Transactional
    public void withdraw(){

        //현재 로그인한 회원
        Member member = findLoginMember();

        //SOFT DELETE
        member.softDelete();
    }


    /**
     * [저장]
     * @param member 회원
     */
    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }


    /**
     * [삭제]
     * @param member 회원
     */
    @Transactional
    public void delete(Member member) {
        memberRepository.delete(member);
    }


    /**
     * [조회]
     * 검색조건에 따라 리스트 조회
     * @param cond 검색 조건
     * @param page 페이지 번호
     * @return Page<Member>
     */
    public Page<Member> findAllByCond(SearchMemberCondDto cond, int page){

        return memberRepository.findAllByCond(cond,createPageable(page));
    }

    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return Member
     */
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(()->new MemberCustomException(ErrorMessage.NOT_FOUND_MEMBER));
    }

    /**
     * [조회]
     * 이메일로 조회
     * @param email 이메일
     * @return Member
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(()->new MemberCustomException(ErrorMessage.NOT_FOUND_MEMBER));
    }

    /**
     * [조회]
     * 이메일로 조회 + NULL 허용
     * @param email 이메일
     * @return Member
     */
    public Member findByEmailNullable(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    /**
     * [존재 여부 확인]
     * 닉네임으로 회원이 존재하는지 확인
     * @param nickname 닉네임
     * @return existsByNickName
     */
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    /**
     * [존재 여부 확인]
     * 이메일로 회원이 존재하는지 확인
     * @param email 이메일
     * @return boolean
     */
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }


    //==페이징 객체 생성==//
    private Pageable createPageable(int page) {
        return PageRequest.of(page,10);
    }

}
