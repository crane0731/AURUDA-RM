package auruda.auruda.repository;

import auruda.auruda.domain.UploadedImage;
import auruda.auruda.enums.DefaultStatus;
import auruda.auruda.enums.ImagePurpose;
import auruda.auruda.enums.IsUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 업로드 이미지 레파지토리
 */
public interface UploadedImageRepository extends JpaRepository<UploadedImage,Long> {

    @Query("SELECT ui " +
            "FROM UploadedImage ui " +
            "WHERE ui.imageUrl = :imageUrl")
    Optional<UploadedImage>findByImageUrl(@Param("imageUrl") String imageUrl);

    @Query("SELECT ui " +
            "FROM UploadedImage ui " +
            "WHERE ui.defaultStatus= :defaultStatus AND ui.purpose= :imagePurpose")
    Optional<UploadedImage>findDefaultMemberProfileImage(@Param("defaultStatus")DefaultStatus defaultStatus, @Param("imagePurpose")ImagePurpose imagePurpose);

    @Query("SELECT ui " +
            "FROM UploadedImage ui " +
            "WHERE ui.isUsed= :isUsed")
    List<UploadedImage> findByIsUsed(@Param("isUsed") IsUsed isUsed);

}
