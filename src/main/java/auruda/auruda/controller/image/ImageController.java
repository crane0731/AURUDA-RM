package auruda.auruda.controller.image;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.image.ImageUrlResponseDto;
import auruda.auruda.service.image.UploadedArticleImageService;
import auruda.auruda.service.image.UploadedMemberProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 *이미지 관련 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/image")
public class ImageController {

    private final UploadedMemberProfileImageService profileImageService; //회원 프로필 이미지 서비스
    private final UploadedArticleImageService articleImageService;//게시글 이미지 서비스

    /**
     * [컨트롤러]
     * 회원 프로필 이미지 업로드
     * @param file 이미지 파일
     * @return ImageUrlResponseDto
     */
    @PostMapping("/profile")
    public ResponseEntity<ApiResponseDto<?>> profileImageUpload(@RequestParam("file") MultipartFile file) {

        ImageUrlResponseDto responseDto = profileImageService.uploadProfileImage(file);

        return ResponseEntity.ok(ApiResponseDto.success(responseDto));

    }

    /**
     * [컨트롤러]
     * 회원 프로필 이미지 업로드 취소
     * @param imageUrl 업로드 취소할 이미지 URL
     * @return 성공 메시지
     */
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponseDto<?>> cancelProfileImageUpload(@RequestParam("imageUrl") String imageUrl) {

        profileImageService.cancelUploadImage(imageUrl);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "이미지 업로드 취소 성공")));

    }

    /**
     * [컨트롤러]
     * 게시글 이미지 업로드
     * @param file 이미지 파일
     * @return ImageUrlResponseDto
     */
    @PostMapping("/article")
    public ResponseEntity<ApiResponseDto<?>> articleImageUpload(@RequestParam("file") MultipartFile file) {

        ImageUrlResponseDto responseDto = articleImageService.uploadProfileImage(file);

        return ResponseEntity.ok(ApiResponseDto.success(responseDto));

    }

    /**
     * [컨트롤러]
     * 게시글 이미지 업로드 취소
     * @param imageUrl 업로드 취소할 이미지 URL
     * @return 성공 메시지
     */
    @DeleteMapping("/article")
    public ResponseEntity<ApiResponseDto<?>> cancelArticleImageUpload(@RequestParam("imageUrl") String imageUrl) {

        articleImageService.cancelUploadImage(imageUrl);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "이미지 업로드 취소 성공")));

    }

}
