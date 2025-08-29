package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.enums.Theme;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 여행 테마
 */
@Entity
@Table(name = "trip_theme")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripTheme extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_plan_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_plan")
    private TripPlan tripPlan;//여행 계획

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false)
    private Theme theme;


    /**
     * [생성 메서드]
     * @param theme 테마
     * @return TripTheme
     */
    public static TripTheme create(Theme theme) {
        TripTheme tripTheme = new TripTheme();
        tripTheme.theme=theme;
        return tripTheme;
    }


    /**
     * [비즈니스 로직]
     * 여행 계획 세팅
     * @param tripPlan 여행 계획
     */
    protected void  setTripPlan(TripPlan tripPlan){
        this.tripPlan=tripPlan;
    }
}
