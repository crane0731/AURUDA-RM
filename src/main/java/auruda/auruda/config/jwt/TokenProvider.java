package auruda.auruda.config.jwt;

import auruda.auruda.domain.Member;
import auruda.auruda.domain.userdetail.CustomUserDetails;
import auruda.auruda.enums.TokenType;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.JwtCustomException;
import auruda.auruda.service.userdetail.CustomUserDetailService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * 토큰을 생성하고,  올바른 토큰인지 유효성을 확인하고 ,  토큰에서 필요한 정보를 가져오는 기능들을 담은 클래스
 */
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private final CustomUserDetailService customUserDetailService;

    /**
     * 엑세스 토큰의 남은 유효 기간을 확인하기 위한 로직
     * @param token
     * @return
     */
    public long getAccessTokenExpiration(String token) {
        try{
            //클레임 조회
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getAccessSecretKey())
                    .parseClaimsJws(token)
                    .getBody();

            //만료 시각 조회
            Date expiration = claims.getExpiration();

            //현재 시각 조회
            Long now = System.currentTimeMillis();

            //남은 만료 기간
            return expiration.getTime() - now;

        }
        catch (JwtException | IllegalArgumentException e){
            throw new JwtCustomException(ErrorMessage.INVALID_TOKEN,e);
        }
    }


    /**
     * 토큰 생성
     * @param member : 회원
     * @param expiredAt : 만료기간
     */
    public String generateToken(Member member , Duration expiredAt, TokenType tokenType) {

        //현재 시각
        Date now = new Date();

        if(tokenType==TokenType.ACCESS) {
            //now.getTime() -> 현재 시각
            //expiredAt.toMillis() -> 만료 기간
            //now.getTime()+ expiredAt.toMillis() -> 만료 날짜, 토큰 유지 기간
            return makeAccessToken(new Date(now.getTime() + expiredAt.toMillis()), member);
        }else{
            return makeRefreshToken(new Date(now.getTime() + expiredAt.toMillis()), member);
        }

    }


    //==JWT 엑세스 토큰 생성 ==//
    private String makeAccessToken(Date expiry, Member member) {

        //현재 시각
        Date now = new Date();

        //JWT 토큰 생성  -> 헤더 + 내용 + 서명
        return Jwts.builder()
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE) //헤더 타입 : JWT
                .setIssuer(jwtProperties.getIssuer())  // 내용 iss : propertise 파일에서 설정한 값
                .setIssuedAt(now) //내용 iat : 현재시간
                .setExpiration(expiry) //내용 exp : expiry 멤버 변숫값, 만료일
                .setSubject(member.getEmail()) //내용 sub : 회원의 이메일
                .claim("id",member.getId()) // 클레임 id : 유저 id
                .claim("role", member.getRole().name()) // 유저의 권한
                //서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .signWith(SignatureAlgorithm.HS256,jwtProperties.getAccessSecretKey())
                .compact();
    }

    //==JWT 리프레쉬 토큰 생성 ==//
    private String makeRefreshToken(Date expiry, Member member) {

        //현재 시각
        Date now = new Date();

        //JWT 토큰 생성  -> 헤더 + 내용 + 서명
        return Jwts.builder()
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE) //헤더 타입 : JWT
                .setIssuer(jwtProperties.getIssuer())  // 내용 iss : propertise 파일에서 설정한 값
                .setIssuedAt(now) //내용 iat : 현재시간
                .setExpiration(expiry) //내용 exp : expiry 멤버 변숫값, 만료일
                .setSubject(member.getEmail()) //내용 sub : 회원의 이메일
                .claim("id",member.getId()) // 클레임 id : 유저 id
                .claim("role", member.getRole().name()) // 유저의 권한
                //서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .signWith(SignatureAlgorithm.HS256,jwtProperties.getRefreshSecretKey())
                .compact();
    }

    /**
     * JWT 엑세스 토큰 유효성 검증 로직
     * @param token : 토큰
     * @return
     */
    public boolean validAccessToken(String token) {
        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getAccessSecretKey()) //비밀값으로 복호화
                    .parseClaimsJws(token);  // 토큰 파싱 (복호화 + 서명 검증 + 유효성 체크)
            return true;
        } catch (Exception e) { //복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    /**
     * JWT 리프레쉬 토큰 유효성 검증 로직
     * @param token : 토큰
     * @return
     */
    public boolean validRefreshToken(String token) {
        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getRefreshSecretKey()) //비밀값으로 복호화
                    .parseClaimsJws(token);  // 토큰 파싱 (복호화 + 서명 검증 + 유효성 체크)
            return true;
        } catch (Exception e) { //복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    /**
     * 토큰 기반으로 인증 정보를 가져오는 메서드
     * @param token : 토큰
     * @return
     */


    public Authentication getAuthentication(String token) {
        // 클레임 조회
        Claims claims = getClaims(token);

        // DB에서 사용자 정보 로딩 (CustomUserDetails)
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(claims.getSubject());

        // JWT에서 권한 꺼내기 (DB 권한이 아닌 JWT 기준 유지)
        String role = claims.get("role", String.class);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));

        // CustomUserDetails + JWT 권한으로 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    /**
     * 토큰 기반으로 유저 ID를 가져오는 메서드
     * @param token : 토큰
     * @return
     */
    public Long getUserId(String token){

        // 클레임 조회
        Claims claims = getClaims(token);

        // 회원 아이디 반환
        return claims.get("id", Long.class);
    }


    //== 클레임 조회 ==//
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtProperties.getAccessSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new JwtCustomException(ErrorMessage.INVALID_TOKEN, e);
        }
    }


}