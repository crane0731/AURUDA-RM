package auruda.auruda.service.image;

import auruda.auruda.domain.UploadedImage;
import auruda.auruda.dto.image.ImageUrlResponseDto;
import auruda.auruda.enums.DefaultStatus;
import auruda.auruda.enums.ImagePurpose;
import auruda.auruda.enums.IsUsed;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.ImageCustomException;
import auruda.auruda.repository.UploadedImageRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 업로드 게시글 이미지 서비스
 */
@Service
@Transactional(readOnly = true)
public class UploadedArticleImageService {

    private final UploadedImageRepository uploadedImageRepository;//업로드 이미지 레파지토리

    private final StorageService storageService; //스토리지 서비스
    public UploadedArticleImageService(
            UploadedImageRepository uploadedImageRepository,
            @Qualifier("articleImageStorageService") StorageService storageService
    ) {
        this.uploadedImageRepository = uploadedImageRepository;
        this.storageService = storageService;
    }


    /**
     * [서비스 로직]
     * ImageUrl로 업로드 이미지 조회
     * @param imageUrl  이미지 URL
     * @return UploadedImage
     */
    public UploadedImage findByImageUrl(String imageUrl) {
        return uploadedImageRepository.findByImageUrl(imageUrl)
                .orElse(null);
    }

    /**
     * [서비스 로직]
     * 프로필 이미지 업로드
     * @param file 파일
     * @return ImageUrlResponseDto
     */
    @Transactional
    public ImageUrlResponseDto uploadProfileImage(MultipartFile file) {
        //실제 파일 저장
        String imageUrl = storageService.uploadFile(file);
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        //DB 저장
        uploadedImageRepository.save(UploadedImage.create(fileName,imageUrl, ImagePurpose.ARTICLE));

        return ImageUrlResponseDto.create(imageUrl);
    }


    /**
     * [서비스 로직]
     * 이미지 파일 삭제
     * @param imageUrl
     */
    @Transactional
    public void deleteImageFile(String imageUrl) {
        storageService.deleteImageFile(imageUrl);
    }


    /**
     * [서비스 로직]
     * 업로드 취소
     * @param imageUrl 이미지 URL
     */
    @Transactional
    public void cancelUploadImage(String imageUrl) {
        UploadedImage uploadedImage = findByImageUrl(imageUrl);
        deleteImageFile(imageUrl);
        uploadedImageRepository.delete(uploadedImage);
    }

    /**
     * [서비스 로직]
     * 업로드 이미지 사용 처리
     * @param uploadedImage 업로드 이미지
     */
    @Transactional
    public void usedImage(UploadedImage uploadedImage) {
        if(uploadedImage.getIsUsed().equals(IsUsed.UNUSED)) {
            //도메인 상태 처리
            uploadedImage.usedImage();
        }
    }
}
