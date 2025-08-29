package auruda.auruda.config.jwt;

import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.service.token.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰 필터
 */
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenBlackListService tokenBlackListService;
    private final TokenProvider tokenProvider;

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";


    /**
     * 필터 설정
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT 필터링 시작");

        //요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        //가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        //가져온 토큰이 유효한지 확인하고 , 유효한 때는 인증 정보 설정
        if(token!=null & tokenProvider.validAccessToken(token)) {

            log.info("JWT 토큰 유효");

            //블랙 리스트 확인
            if(tokenBlackListService.isBlacklisted(token)) {

                log.warn("블랙리스트에 등록된 토큰");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorMessage.LOGOUT_TOKEN);
            }

            //인증 정보 가져오기
            Authentication authentication = tokenProvider.getAuthentication(token);

            //시큐리티 컨텍스트에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        log.info("JWT 필터링 성공");

        // 다음 필터 실행
        filterChain.doFilter(request, response);

    }


    //==엑세스 토큰 조회 + 접두사 제거 ==//
    private String getAccessToken(String authorizationHeader) {

        //만약 헤더가 "Bearer " 로시작한다면 접두사 제거
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
