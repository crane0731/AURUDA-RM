package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.BackpackCustomException;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.ReviewCustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 배낭
 */
@Entity
@Table(name = "backpack")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Backpack extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backpack_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //회원

    @Column(name = "name", nullable = false)
    private String name; //이름

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    @OneToMany(mappedBy = "backpack",cascade = CascadeType.PERSIST)
    private List<BackpackPlace> backpackPlaces=new ArrayList<>();


    /**
     * [생성 메서드]
     * @param member 회원
     * @param name 배낭 이름
     * @return Backpack
     */
    public static Backpack create(Member member, String name) {
        Backpack backpack = new Backpack();
        backpack.member = member;
        backpack.name = name;
        backpack.deleteStatus = DeleteStatus.ACTIVE;
        return backpack;
    }

    /**
     * [비즈니스 로직]
     * 배낭 이름 수정
     * @param name 배낭 이름
     */
    public void updateName (String name) {

        //이미 삭제된 배낭인지 확인
        validateAlreadyDeletedBackpack();

        this.name = name;
    }

    /**
     * [비즈니스 로직]
     * SOFT DELETE
     */
    public void softDelete(){

        //이미 삭제된 배낭인지 확인
        validateAlreadyDeletedBackpack();

        this.deleteStatus = DeleteStatus.DELETED;

    }


    /**
     * [비즈니스 로직]
     * 배낭에 장소를 추가
     * @param place 장소
     */
    public void addPlace(Place place){

        //이미 삭제된 배낭인지 확인
        validateAlreadyDeletedBackpack();

        //배낭 - 장소 생성
        BackpackPlace backpackPlace = BackpackPlace.create(this, place);

        //연관 관계 세팅
        backpackPlaces.add(backpackPlace);


    }

    //==이미 삭제된 배낭인지 확인 하는 메서드==//
    private void validateAlreadyDeletedBackpack() {
        if(this.deleteStatus.equals(DeleteStatus.DELETED)){
            throw new BackpackCustomException(ErrorMessage.ALREADY_DELETED_BACKPACK);
        }
    }
}
