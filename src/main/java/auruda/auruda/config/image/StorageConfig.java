package auruda.auruda.config.image;

import auruda.auruda.service.image.LocalStorageService;
import auruda.auruda.service.image.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 이미지 저장 관련 설정
 */
@Configuration
public class StorageConfig {

    @Value("${file.upload-profile-dir}")
    private String profileDir;

    @Value("${file.upload-article-dir}")
    private String articleDir;


    @Bean
    public StorageService profileImageStorageService() {
        return new LocalStorageService(profileDir);
    }

    @Bean
    public StorageService articleImageStorageService() {
        return new LocalStorageService(articleDir);
    }


}