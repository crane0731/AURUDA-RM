package auruda.auruda.config;

import auruda.auruda.config.jwt.TokenAuthenticationFilter;
import auruda.auruda.config.jwt.TokenProvider;
import auruda.auruda.service.token.TokenBlackListService;
import auruda.auruda.service.userdetail.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 스프링 시큐리티 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final TokenBlackListService tokenBlackListService;
    private final CustomUserDetailService customUserDetailService;
    private final TokenProvider tokenProvider;


    /**
     *  패스워드 인코더로 사용할 Bean 등록
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 인증 매니저 빈 등록 ,  Spring Security에서 커스텀 인증을 처리
     */
    @Bean
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    /**
     * 특정 HTTP 요쳥에 대한 웹 기반 보안 구성
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // TokenAuthenticationFilter 생성 및 Security 필터 체인에 추가

        //토큰 인증 필터
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter(tokenBlackListService,tokenProvider);


        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless 모드
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auruda/auth/signup","/api/auruda/auth/login","/api/auruda/auth/token","/api/auruda/image/**",
                                "/static/***","/image/profile/**","/image/article/**","/test.html","/api/auth/**","/api/auth/kakao/callback",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/v3/api-docs/**",
//                                "/swagger-resources/**",
//                                "/webjars/**",
                                "/api/auruda/latest-festivals",
                                "/api/auruda/latest-concerts",
                                "/api/auruda/kakao/**",
                                "/api/auruda/place/**"
                        )
                        .permitAll()
                        .requestMatchers("/api/auruda/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가


        return http.build();

    }
}