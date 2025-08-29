package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.DefaultStatus;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.enums.ImagePurpose;
import auruda.auruda.enums.IsUsed;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 업로드 이미지
 */
@Entity
@Table(name = "uploaded_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadedImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uploaded_image_id")
    private Long id; // PK

    @Column(name = "file_name", nullable = false)
    private String fileName; //파일 이름

    @Column(name = "image_url", nullable = false)
    private String imageUrl; //이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(name = "is_used", nullable = false)
    private IsUsed isUsed; //사용 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    private ImagePurpose purpose;//목적

    @Enumerated(EnumType.STRING)
    @Column(name = "default_status", nullable = false)
    private DefaultStatus defaultStatus; //기본 이미지 여부


    /**
     * [생성 메서드]
     * @param fileName 파일 이름
     * @param imageUrl 이미지 URL
     * @param purpose 이미지 목적
     * @return UploadedImage
     */
    public static UploadedImage create(String fileName, String imageUrl, ImagePurpose purpose) {
        UploadedImage uploadedImage = new UploadedImage();
        uploadedImage.fileName = fileName;
        uploadedImage.imageUrl = imageUrl;
        uploadedImage.purpose = purpose;
        uploadedImage.isUsed=IsUsed.UNUSED;
        uploadedImage.defaultStatus=DefaultStatus.NOT_DEFAULT;
        return uploadedImage;
    }

    /**
     * [비즈니스 로직]
     * 이미지 사용 처리
     */
    public void usedImage(){
        this.isUsed=IsUsed.USED;
    }

    /**
     * [비즈니스 로직]
     * 이미지 비사용 처리
     */
    public void unusedImage(){
        this.isUsed=IsUsed.UNUSED;
    }

}
