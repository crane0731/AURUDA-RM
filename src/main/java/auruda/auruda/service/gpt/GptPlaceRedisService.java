package auruda.auruda.service.gpt;

import auruda.auruda.dto.gpt.GptPlaceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * GPT 장소 레디스 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GptPlaceRedisService {

    private final RedisTemplate<String, Object> redisTemplate=new RedisTemplate<>();

    /**
     * [저장]
     * @param key  redis 키값
     * @param dtoList DTO 리스트
     */
    public void saveData(String key, List<GptPlaceResponseDto> dtoList) {

        // Redis에 리스트로 저장
        redisTemplate.opsForValue().set(key, dtoList);
    }

    /**
     * [조회]
     * @param key redis 키 값
     * @return List<GptPlaceResponseDto>
     */
    public List<GptPlaceResponseDto> getData(String key) {
        return (List<GptPlaceResponseDto>) redisTemplate.opsForValue().get(key);
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
