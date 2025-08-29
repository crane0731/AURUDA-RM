package auruda.auruda.service.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 저장 서비스 인터페이스
 */
@Service
public interface StorageService {

    /**
     * 파일을 업로드 하고 접근 가능한 url 을 반환
     * @param file 업로드 할 파일
     * @return 업로드된 파일의 url
     */
    String uploadFile(MultipartFile file);

    /**
     * 업로드한 파일을 삭제
     * @param imageUrl
     */
    void deleteImageFile(String imageUrl);

}
