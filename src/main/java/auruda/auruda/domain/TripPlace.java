package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.TripPlaceCustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 여행 장소
 */
@Entity
@Table(name = "trip_place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripPlace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_place_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_plan_id")
    private TripPlan tripPlan;//여행 계획

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place; //장소

    @Column(name = "day", nullable = false)
    private Long day; //일 차

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    /**
     * [생성 메서드]
     * @param tripPlan 여행 계획
     * @param place 장소
     * @param day 일차
     * @return TripPlace
     */
    public static TripPlace create(TripPlan tripPlan, Place place, Long day) {
        TripPlace tripPlace = new TripPlace();
        tripPlace.settingTripPlan(tripPlan);
        tripPlace.place = place;
        tripPlace.day = day;
        tripPlace.deleteStatus = DeleteStatus.ACTIVE;
        return tripPlace;
    }

    /**
     * [연관관계 편의 메서드]
     * TripPlace - TripPlan
     * @param tripPlan 여행 계획
     */
    public void settingTripPlan(TripPlan tripPlan) {
        this.tripPlan = tripPlan;
        tripPlan.getTripPlaces().add(this);
    }

    /**
     * [비즈니스 로직]
     * SOFT DELETE
     */
    public void softDelete(){
        if(this.deleteStatus.equals(DeleteStatus.DELETED)){
            throw new TripPlaceCustomException(ErrorMessage.ALREADY_DELETED_TRIP_PLACE);
        }

        this.deleteStatus = DeleteStatus.DELETED;
    }


}
