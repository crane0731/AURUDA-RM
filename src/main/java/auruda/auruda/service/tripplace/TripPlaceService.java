package auruda.auruda.service.tripplace;

import auruda.auruda.domain.TripPlace;
import auruda.auruda.domain.TripPlan;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.TripPlaceCustomException;
import auruda.auruda.repository.tripplace.TripPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 여행 장소 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripPlaceService{

    private final TripPlaceRepository tripPlaceRepository;//여행 장소 레파지토리

    /**
     * [저장]
     * @param tripPlace 여행 장소
     */
    @Transactional
    public void save(TripPlace tripPlace){
        tripPlaceRepository.save(tripPlace);
    }


    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return TripPlace
     */
    public TripPlace findById(Long id){
        return tripPlaceRepository.findById(id).orElseThrow(()->new TripPlaceCustomException(ErrorMessage.NOT_FOUND_TRIP_PLACE));
    }

    /**
     * [삭제]
     * TripPlan 으로 삭제
     * @param tripPlan 여행 계획
     * @return List<TripPlace>
     */
    @Transactional
    public void deleteByTripPlan(TripPlan tripPlan){
        tripPlaceRepository.deleteByTripPlan(tripPlan);
    }




}
