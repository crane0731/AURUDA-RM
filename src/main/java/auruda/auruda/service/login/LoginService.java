package auruda.auruda.service.login;

import auruda.auruda.domain.KakaoAccessToken;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.RefreshToken;
import auruda.auruda.domain.UploadedImage;
import auruda.auruda.domain.userdetail.CustomUserDetails;
import auruda.auruda.dto.login.LoginRequestDto;
import auruda.auruda.dto.login.SignUpRequestDto;
import auruda.auruda.dto.token.TokenResponseDto;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.MemberCustomException;
import auruda.auruda.repository.KakaoAccessTokenRepository;
import auruda.auruda.service.image.UploadedMemberProfileImageService;
import auruda.auruda.service.member.MemberService;
import auruda.auruda.service.token.RefreshTokenService;
import auruda.auruda.service.token.TokenBlackListService;
import auruda.auruda.service.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 로그인 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    private final UploadedMemberProfileImageService profileImageService; //회원 프로필 이미지 업로드 서비스
    private final MemberService memberService; //회원 서비스
    private final TokenService tokenService; //토큰 서비스
    private final RefreshTokenService refreshTokenService; //리프레쉬 토큰 서비스
    private final TokenBlackListService tokenBlackListService; //토큰 블랙리스트 서비스

    private final AuthenticationManager authenticationManager; //시큐리티 인증 매니저
    private final BCryptPasswordEncoder bCryptPasswordEncoder; //패스워드 인코더

    private final KakaoAccessTokenRepository kakaoAccessTokenRepository; //카카오 엑세스 토큰 레파지토리

    /**
     * [서비스 로직]
     * 회원 가입
     * @param dto 요청 DTO
     */
    @Transactional
    public void signUp(SignUpRequestDto dto){
        //회원 가입을 위한 검증
        signupcheck(dto);

        //업로드된 이미지 조회
        UploadedImage uploadedImage = getUploadedImage(dto.getProfileImageUrl());

        //회원 등록
        saveMember(dto, uploadedImage);

    }

    /**
     * [서비스 로직]
     * 회원 로그인
     * @param dto 로그인 요청 DTO
     * @return TokenResponseDto
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {

        //스프링 시큐리티 수동 로그인
        Member member = securityLogin(dto);

        //JWT 토큰 생성 , 반환
        return makeToken(member);
    }

    /**
     * [서비스 로직]
     * 로그아웃
     * @param request HTTP 요청
     */
    @Transactional
    public void logout(HttpServletRequest request) {

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //현재 로그인한 회원의 모든 리프레쉬 토큰 조회
        List<RefreshToken> refreshTokenList = refreshTokenService.findByMember(member);

        //모든 리프레쉬 토큰 로그아웃 처리
        for (RefreshToken refreshToken : refreshTokenList) {

            //카카오 로그아웃 API 호출(카카오 토큰이 있는경우)
            kakaoAccessTokenRepository.findByRefreshToken(refreshToken).ifPresent(this::logoutFromKakao);
            break;
        }

        //리프레쉬 토큰 삭제
        deleteToken(request, member);

    }


    //== 카카오 로그아웃 API 호출 메서드==//
    private void logoutFromKakao(KakaoAccessToken kakaoAccessToken) {
        String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken.getAccessToken());  // Access Token 설정
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoUnlinkUrl, request, String.class);

            System.out.println("카카오 계정 연결 해제 성공: " + response.getBody());
        } catch (Exception e) {
            System.err.println("카카오 계정 연결 해제 실패: " + e.getMessage());
        }
    }

    //==토큰 삭제 처리 로직==//
    private void deleteToken(HttpServletRequest request, Member member) {

        //리프레쉬 토큰 삭제
        refreshTokenService.delete(member);

        //엑세스 토큰 블랙리스트 등록
        setBlackListAccessToken(request);
    }

    //==엑세스 토큰 블랙리스트 등록==//
    private void setBlackListAccessToken(HttpServletRequest request) {
        //요청에서 인증 헤더 추출
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);

            //블랙리스트에 등록
            tokenBlackListService.blackList(accessToken);
        }
    }

    //==리프레쉬 토큰 + 엑세스 토큰 생성==//
    private TokenResponseDto makeToken(Member member) {
        //리프레시 토큰 생성
        String refreshToken = refreshTokenService.createRefreshToken(member);

        //엑세스 토큰 생성
        String accessToken = tokenService.createNewAccessToken(refreshToken);

        //응답 DTO 반환
        return TokenResponseDto.create(accessToken, refreshToken);
    }

    //==스프링 시큐리티 수동 로그인==//
    private Member securityLogin(LoginRequestDto dto) {
        //UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());


        //AuthenticationManager로 인증 시도 (loadUser + password 체크 내부 수행)
        Authentication authentication = authenticationManager.authenticate(authToken);


        //인증 정보 SecurityContext에 저장 (로그인 처리)
        SecurityContextHolder.getContext().setAuthentication(authentication);


        //로그인된 회원 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        return memberService.findByEmail(userDetails.getUsername());
    }


    //==업로드된 이미지 조회 ==//
    private UploadedImage getUploadedImage(String profileImageUrl) {

        if(profileImageUrl!=null) {
            //업로드 이미지 조회
            UploadedImage uploadedImage = profileImageService.findByImageUrl(profileImageUrl);

            //이미지 사용 처리
            profileImageService.usedImage(uploadedImage);


            return uploadedImage;
        }else {
            //프로필 이미지가 null 일 경우 기본 프로필 이미지 사용
            return profileImageService.findDefaultProfileImage();
        }

    }

    //==회원 생성 + 프로필 이미지 세팅 + 저장 ==//
    public void saveMember(SignUpRequestDto signUpRequestDto, UploadedImage uploadedImage) {

        //프로필 이미지 세팅
        signUpRequestDto.setProfileImageUrl(uploadedImage.getImageUrl());

        //패스워드 인코딩
        String encodedPassword = bCryptPasswordEncoder.encode(signUpRequestDto.getPassword());

        //인코딩한 패스워드 세팅
        signUpRequestDto.setPassword(encodedPassword);

        //회원 도메인 생성 + 저장
        memberService.save(Member.create(signUpRequestDto));


    }


    //==회원가입 검증 로직==//
    public void signupcheck(SignUpRequestDto signUpRequestDto) {
        //이메일 중복 검사
        emailDuplicatedCheck(signUpRequestDto.getEmail());

        //비밀번호 일치 검사
        passwordCheck(signUpRequestDto.getPassword(), signUpRequestDto.getPasswordCheck());

        //닉네임 중복 검사
        nicknameDuplicatedCheck(signUpRequestDto.getNickname());
    }

    //==닉네임 중복 검사 로직 ==//
    private void nicknameDuplicatedCheck(String nickname) {
        if(memberService.existsByNickname(nickname)) {
            throw new MemberCustomException(ErrorMessage.DUPLICATED_NICKNAME);
        }
    }

    //==아이디 중복 검사 로직 ==//
    private void emailDuplicatedCheck(String email) {
        if(memberService.existsByEmail(email)) {
            throw new MemberCustomException(ErrorMessage.DUPLICATED_EMAIL);
        }
    }

    //==비밀번호 일치 검사 로직==//
    private void passwordCheck(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new MemberCustomException(ErrorMessage.PASSWORD_MISMATCH);
        }
    }

    //로그인

    //로그 아웃




}
