package auruda.auruda.service.image;

import auruda.auruda.domain.UploadedImage;
import auruda.auruda.enums.ImagePurpose;
import auruda.auruda.enums.IsUsed;
import auruda.auruda.repository.UploadedImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 정해진 시간마다 사용되지 않은 업로드 이미지를 삭제하는 스케쥴 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UploadedImageCleanUpService {

    private final UploadedMemberProfileImageService uploadedMemberProfileImageService; //회원 프로필 이미지 서비스
    private final UploadedImageRepository uploadedImageRepository; //업로드 이미지 레파지토리

    /**
     * 매일 새벽 3시에 실행 (초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteUnusedImages() {

        log.info("스케쥴러 시작됨");

        List<UploadedImage> unusedImages = uploadedImageRepository.findByIsUsed(IsUsed.UNUSED);

        for (UploadedImage image : unusedImages) {

            try{
                if(image.getPurpose()== ImagePurpose.PROFILE) {
                    //실제 파일 삭제
                    uploadedMemberProfileImageService.deleteImageFile(image.getImageUrl());
                }

                //DB에서 삭제
                uploadedImageRepository.delete(image);


            }catch (Exception e) {
                log.info("파일삭제 실패 = {}",image.getImageUrl());
            }
        }
    }

}