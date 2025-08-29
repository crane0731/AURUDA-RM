package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.BackpackCustomException;
import auruda.auruda.exception.ErrorMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배낭 장소
 */
@Entity
@Table(name = "backpack_place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackpackPlace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backpack_place_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backpack_id")
    private Backpack backpack;// 배낭

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place; //장소

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    /**
     * [생성 메서드]
     * @param backpack 배낭
     * @param place 장소
     * @return BackpackPlace
     */
    public static BackpackPlace create(Backpack backpack, Place place) {
        BackpackPlace backpackPlace = new BackpackPlace();
        backpackPlace.backpack = backpack;
        backpackPlace.place = place;
        backpackPlace.deleteStatus = DeleteStatus.ACTIVE;
        return backpackPlace;
    }

    /**
     * [비즈니스 로직]
     * RECOVERY
     */
    public void recovery(){
        if(deleteStatus == DeleteStatus.ACTIVE){
            throw new BackpackCustomException(ErrorMessage.ALREADY_EXISTS_BACKPACK_PLACE);
        }

        deleteStatus = DeleteStatus.ACTIVE;
    }

    /**
     * [비즈니스 로직]
     * SOFT DELETE
     */
    public void softDelete(){

        //이미 삭제된 배낭인지 확인
        if(this.deleteStatus.equals(DeleteStatus.DELETED)){
            throw new BackpackCustomException(ErrorMessage.ALREADY_DELETED_BACKPACK_PLACE);
        }

        this.deleteStatus = DeleteStatus.DELETED;

    }



}
