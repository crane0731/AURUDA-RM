package auruda.auruda.service.image;

import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.ImageCustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 이미지를 로컬 디렉토리어 저장, 삭제하기 위한 서비스
 */
@Slf4j
public class LocalStorageService implements StorageService {

    //파일이 저장될 디렉토리를 나타내는 Path 객체
    public final Path fileStorageLocation;

    //생성자 : 로컬 저장소의 디렉토리를 설정
    public LocalStorageService(String uploadDir) {
        //업로드 디렉토리를 절대 경로로 반환하고 정규화
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            //지정된 디렉토리가 없으면 생성
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            //디렉토리 생성에 실패하면 런타임 예외를 발생
            throw new ImageCustomException(ErrorMessage.FAILED_CREATED_DIRECTORY);
        }
    }

    /**
     * 파일을 업로드 하고 접근 가능한 url 을 반환
     * @param file 업로드 할 파일
     * @return 업로드된 파일의 url
     */
    @Override
    public String uploadFile(MultipartFile file) {

        //업로드된 파일의 원래 이름을 가져와 깨끗한 경로로 변환
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        String fileExtension="";

        try{
            //파일 이름에 "."이 포함되어 있다면 예외 발생(보안 검증)
            if(originalFileName.contains("..")){
                throw new ImageCustomException(ErrorMessage.INCORRECT_FILE_NAME);
            }

            //업로드된 파일의 MIME 타입 확인
            String contentType = file.getContentType();
            if (!isImage(contentType)) {
                //이미지 파일이 아닌 경우 예외 발생
                throw new ImageCustomException(ErrorMessage.NOT_IMAGE_FILE);
            }

            //UUID를 생성하여 고유한 파일 이름 생성
            String uuid = UUID.randomUUID().toString();

            //파일 이름에서 확장자 추출
            int dotIndex = originalFileName.lastIndexOf(".");
            if(dotIndex > 0){
                fileExtension = originalFileName.substring(dotIndex);
            }

            //최종 파일 이름 (UUID + 확장자)
            String fileName = uuid + fileExtension;

            //파일 저장 경로 생성
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            //파일을 지정된 경로에 저장(기준 파일이 있다면 덮어씀)
            Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);

            //저장된 파일의 URL 반환
            return "/"+fileName;

        } catch (IOException e) {
            //파일 저장 중 오류가 발생하면 예외 발생
            throw new ImageCustomException(ErrorMessage.FAILED_IMAGE_UPLOAD);
        }
    }

    @Override
    public void deleteImageFile(String imageUrl) {
        // URL에서 파일 이름만 추출
        if (imageUrl.contains("/")) {
            imageUrl = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        }

        // 삭제할 파일의 전체 경로 생성
        Path filePath = fileStorageLocation.resolve(imageUrl);
        File file = filePath.toFile();

        // 파일이 존재하면 삭제
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (isDeleted) {
                log.info("파일이 성공적으로 삭제되었습니다.");
            } else {
                throw new ImageCustomException(ErrorMessage.FAILED_DELETE_FILE);
            }
        } else {
            throw new ImageCustomException(ErrorMessage.NOT_FOUND_FILE);
        }
    }

    /**
     * MIME 타입이 이미지인지 확인하는 메서드
     * @param contentType
     * @return
     */
    private boolean isImage(String contentType) {
        //MIME 타입이 null이 아니면서 허용된 이미지 포맷인지 확인
        return contentType != null &&(
                contentType.equals("image/jpeg") ||  // JPG
                        contentType.equals("image/png") ||   // PNG
                        contentType.equals("image/gif") ||   // GIF
                        contentType.equals("image/bmp") ||   // BMP
                        contentType.equals("image/webp")     // WEBP
        );
    }
}
