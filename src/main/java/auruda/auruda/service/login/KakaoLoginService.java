package auruda.auruda.service.login;

import auruda.auruda.config.kakao.KakaoProperties;
import auruda.auruda.domain.KakaoAccessToken;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.RefreshToken;
import auruda.auruda.domain.userdetail.KakaoUserDetails;
import auruda.auruda.dto.login.LoginRequestDto;
import auruda.auruda.dto.login.SignUpRequestDto;
import auruda.auruda.dto.token.TokenResponseDto;
import auruda.auruda.repository.KakaoAccessTokenRepository;
import auruda.auruda.service.member.MemberService;
import auruda.auruda.service.token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 카카오 로그인 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoLoginService {

    private final RestTemplate restTemplate=new RestTemplate();// REST 템플릿
    private final KakaoProperties kakaoProperties; //카카오 설정 클래스
    private final LoginService loginService; //로그인 서비스
    private final MemberService memberService; //회원 서비스
    private final RefreshTokenService refreshTokenService; //리프레쉬 토큰 서비스
    private final BCryptPasswordEncoder bCryptPasswordEncoder; //패스워드 인코더
    private final KakaoAccessTokenRepository kakaoAccessTokenRepository;//카카오 엑세스 토큰 레파지토리


    /**
     * [서비스 로직]
     * 카카오 로그인 (+회원 가입)
     * @param code 카카오 인증 코드
     * @return
     */
    @Transactional
    public TokenResponseDto kakaoLogin(String code){

        String kakaoToken = getKakaoToken(code);

        KakaoUserDetails userDetail = getKakaoUserDetail(kakaoToken);

        //회원 가입을 요청 DTO 생성
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.createForKakao(userDetail.getEmail(), userDetail.getNickname(), userDetail.getProfileImageUrl());

        //만약 기존 회원 정보가 없다면 회원 가입, 있다면 기존 회원 정보 조회

        //로그인 아이디(이메일)로 회원 조회
        Member member = memberService.findByEmailNullable(signUpRequestDto.getEmail());

        //회원정보가 없다 -> 회원 가입 진행
        if(member==null){
            //회원 가입 진행 로직
            processSignUp(signUpRequestDto);
        }

        //로그인 요청 DTO 생성
        LoginRequestDto loginRequestDto = LoginRequestDto.createForKakao(signUpRequestDto.getEmail(), signUpRequestDto.getPassword());

        //회원 로그인 처리
        TokenResponseDto responseDto = loginService.login(loginRequestDto);


        //카카오 엑세스 토큰 저장
        saveKakaoAccessToken(responseDto,kakaoToken);

        return responseDto;

    }

    //==카카오 엑세스 토큰 저장 로직==//
    private void saveKakaoAccessToken( TokenResponseDto responseDto,String kakaoAccessToken) {
        //리프레쉬 토큰 조회
        RefreshToken refreshToken = refreshTokenService.findByRefreshToken(responseDto.getRefreshToken());

        //카카오 엑세스 토큰 저장
        kakaoAccessTokenRepository.save(KakaoAccessToken.create(refreshToken,kakaoAccessToken));
    }

    //==회원 가입 진행 로직==//
    private void processSignUp(SignUpRequestDto signUpRequestDto) {
        //회원 가입 검증
        loginService.signupcheck(signUpRequestDto);

        //패스워드
        String password = signUpRequestDto.getPassword();

        //패스워드 인코딩
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        //인코딩된 패스워드 설정
        signUpRequestDto.setPassword(encodedPassword);

        //회원 생성
        Member member = Member.create(signUpRequestDto);

        //회원 저장
        memberService.save(member);

        //원래 패스워드로 재설정
        signUpRequestDto.setPassword(password);

    }

    //==카카오 엑세스 토큰 받아오는 로직==//
    public String getKakaoToken(String code) {

        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("client_secret", kakaoProperties.getClientSecret()); // 실제 비밀 키 입력
        params.add("redirect_uri", "http://localhost:8081/api/auth/kakao/callback");

        params.add("code",code);

        //HTTP 요청 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);


        try {
            // 디버깅용 로그
            System.out.println("Request URL: " + tokenUrl);
            System.out.println("Request Headers: " + headers);
            System.out.println("Request Parameters: " + params);

            ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

            // 응답 확인
            System.out.println("Token response: " + response.getBody());

            return (String) response.getBody().get("access_token");

        } catch (HttpClientErrorException e) {

            // 구체적인 오류 로그 출력
            System.out.println("Error during token request: " + e.getMessage());
            System.out.println("Response Body: " + e.getResponseBodyAsString());
            System.out.println("Status Code: " + e.getStatusCode());

            throw e;
        }
    }

    //==카카오 엑세스 토큰을 통해 유저 정보를 가져오는 로직==//
    public KakaoUserDetails getKakaoUserDetail(String accessToken) {

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);
        Map<String, Object> userInfo = response.getBody();

        //유저 아이디
        Long id = ((Number) userInfo.get("id")).longValue();

        //유저 정보
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");

        //유저 닉네임
        String nickname = (String) properties.get("nickname");

        //유저 프로필 이미지
        String profileImageUrl = (String) properties.get("profile_image");

        //카카오 계정
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");

        //유저 이메일
        String email = (String) kakaoAccount.get("email");

        //카카오 유저 디테일 생성 + 반환
        return new KakaoUserDetails(id, nickname, profileImageUrl, email);
    }

}