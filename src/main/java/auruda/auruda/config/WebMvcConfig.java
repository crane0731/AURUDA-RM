package auruda.auruda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WEBMVC 설정 클래스
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/profile/**")
                .addResourceLocations("file:///C:/Users/dlwns/crane/PROJECT_AURUDA_RM/BACKEND/ProfileImage/");

        registry.addResourceHandler("/image/article/**")
                .addResourceLocations("file:///C:/Users/dlwns/crane/PROJECT_GMART/BACKEND/ArticleImage/");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해
                .allowedOrigins("http://localhost:5173") // Vue dev 서버 주소 (포트 확인!)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 인증정보(쿠키 등) 허용
    }
}
