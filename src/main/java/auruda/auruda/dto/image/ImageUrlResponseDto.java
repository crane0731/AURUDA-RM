package auruda.auruda.dto.image;

import lombok.Getter;
import lombok.Setter;

/**
 * 이미지 URL 응답 DTO
 */
@Getter
@Setter
public class ImageUrlResponseDto {

    private String imageUrl;

    /**
     * [생성 메서드]
     * @param imageUrl 이미지 URL
     * @return ImageUrlResponseDto
     */
    public static ImageUrlResponseDto create(String imageUrl) {
        ImageUrlResponseDto dto = new ImageUrlResponseDto();
        dto.setImageUrl(imageUrl);
        return dto;
    }
}