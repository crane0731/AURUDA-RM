package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.enums.Theme;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.TripPlanCustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 여행 계획
 */
@Entity
@Table(name = "trip_plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_plan_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //회원

    @Column(name = "title" , nullable = false)
    private String title; //제목

    @Column(name = "region", nullable = false)
    private String region; //지역

    @Column(name = "day", nullable = false)
    private Long day; //여행 일

    @Column(name = "start_date", nullable = false)
    private String startDate; //여행 시작 일

    @Column(name = "end_date", nullable = false)
    private String endDate; //여행 종료 일

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    @OneToMany(mappedBy = "tripPlan",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TripTheme> tripThemes= new ArrayList<>();

    @OneToMany(mappedBy = "tripPlan" )
    private List<TripPlace> tripPlaces = new ArrayList<>();

    /**
     * [생성 메서드]
     * @param member 회원
     * @param title 제목
     * @param region 지역
     * @param day 날짜
     * @param startDate 여행 시작일
     * @param endDate 여행 종료일
     * @param theme1 테마1
     * @param theme2 테마2
     * @param theme3 테마3
     * @return TripPlan
     */
    public static TripPlan create(Member member, String title, String region, Long day, String startDate, String endDate, Theme theme1, Theme theme2, Theme theme3) {
        TripPlan tripPlan = new TripPlan();
        tripPlan.member = member;
        tripPlan.title = title;
        tripPlan.region = region;
        tripPlan.day = day;
        tripPlan.startDate = startDate;
        tripPlan.endDate = endDate;
        tripPlan.deleteStatus = DeleteStatus.ACTIVE;

        TripTheme tripTheme1 = TripTheme.create(theme1);
        tripPlan.addTripTheme(tripTheme1);

        TripTheme tripTheme2 = TripTheme.create(theme2);
        tripPlan.addTripTheme(tripTheme2);

        TripTheme tripTheme3 = TripTheme.create(theme3);
        tripPlan.addTripTheme(tripTheme3);

        return tripPlan;
    }

    /**
     * [비즈니스 로직]
     * 업데이트
     * @param title 제목
     * @param region 지역
     * @param day 여행 일
     * @param startDate 시작 일
     * @param endDate 종료 일
     * @param theme1 테마1
     * @param theme2 테마2
     * @param theme3 테마3
     */
    public void update(String title, String region, Long day, String startDate, String endDate, Theme theme1, Theme theme2, Theme theme3){
        this.title = title;
        this.region = region;
        this.day = day;
        this.startDate = startDate;
        this.endDate = endDate;

        //여행 테마리스트 업데이트
        updateTripThemes(theme1, theme2, theme3);
    }

    /**
     * [연관관계 편의 메서드]
     * @param tripTheme 여행 테마
     */
    public void addTripTheme(TripTheme tripTheme) {
        tripThemes.add(tripTheme);
        tripTheme.setTripPlan(this);
    }

    /**
     * [비즈니스 로직]
     * SOFT DELETE
     */
    public void softDelete(){
        if(this.deleteStatus.equals(DeleteStatus.DELETED)){
            throw new TripPlanCustomException(ErrorMessage.ALREADY_DELETED_TRIP_PLAN);
        }

        //여행 장소들 논리적 삭제 처리
        List<TripPlace> tripPlaces = this.tripPlaces;
        for (TripPlace tripPlace : tripPlaces) {
            tripPlace.softDelete();
        }

        this.deleteStatus = DeleteStatus.DELETED;
    }

    //==여행 테마리스트 업데이트==//
    private void updateTripThemes(Theme theme1, Theme theme2, Theme theme3) {
        this.getTripThemes().clear();
        addTripTheme(TripTheme.create(theme1));

        addTripTheme(TripTheme.create(theme2));

        addTripTheme(TripTheme.create(theme3));
    }

}
