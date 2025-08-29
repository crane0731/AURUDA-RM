package auruda.auruda.service.tourist;

import auruda.auruda.dto.trip.TouristSpotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  여행지점 redis 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TouristSpotRedisService {

    private final RedisTemplate<String, Object> redisTemplate=new RedisTemplate<>(); //redis 템플릿

    /**
     * [저장]
     * @param key redis 키값
     * @param dtos 여행지 DTO
     */
    public void saveData(String key, List<TouristSpotDto> dtos) {

        // Redis에 리스트로 저장
        redisTemplate.opsForValue().set(key, dtos);
    }

    /**
     * [조회]
     * @param key redis 키값
     * @return List<TouristSpotDto>
     */
    public List<TouristSpotDto> getData(String key) {
        return (List<TouristSpotDto>) redisTemplate.opsForValue().get(key);
    }

    /**
     * [존재 여부 확인]
     * @param key redis 키 값
     * @return boolean
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
}
