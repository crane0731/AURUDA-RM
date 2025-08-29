package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.Category;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.PlaceCusomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 장소
 */
@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id; // PK

    @Column(name = "name",nullable = false)
    private String name; //이름

    @Column(name = "description",nullable = false)
    private String description; //설명

    @Column(name = "city",nullable = false)
    private String city; //지역

    @Column(name = "address")
    private String address; //주소

    @Column(name = "visited_count",nullable = false)
    private Long visitedCount; //방문자 수

    @Column(name = "image_url")
    private String imageUrl; //이미지 URL

    @Column(name = "lat")
    private Double lat; //위도

    @Column(name = "lng")
    private Double lng; //경도

    @Enumerated(EnumType.STRING)
    @Column(name = "category",nullable = false)
    private Category category; //카테고리

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    @OneToMany(mappedBy = "place")
    private List<Review> reviews= new ArrayList<>();

    /**
     * [생성 메서드]
     * @param name 장소 이름
     * @param description 설명
     * @param city 지역
     * @param address 주소
     * @param lat 위도
     * @param lng 경도
     * @param category 카테고리
     * @return Place
     */
    public static Place create(String name, String description,String city, String address, Double lat, Double lng, Category category){
        Place place = new Place();
        place.name = name;
        place.description = description;
        place.city = city;
        place.address = address;
        place.visitedCount=0L;
        place.lat=lat;
        place.lng=lng;
        place.category = category;
        place.deleteStatus=DeleteStatus.ACTIVE;
        return place;
    }

    /**
     * [비즈니스 로직]
     * 방문수 +1
     */
    public void plusVisitedCount(){
        this.visitedCount++;
    }


    public void recovery(){
        if(deleteStatus.equals(DeleteStatus.ACTIVE)){
            throw new PlaceCusomException(ErrorMessage.ALREADY_EXISTS_PLACE);
        }

        this.deleteStatus=DeleteStatus.ACTIVE;
    }

}
