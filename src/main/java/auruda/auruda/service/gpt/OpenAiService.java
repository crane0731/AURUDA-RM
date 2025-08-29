package auruda.auruda.service.gpt;

import auruda.auruda.dto.gpt.GptPlaceRequestDto;
import auruda.auruda.dto.gpt.GptPlaceResponseDto;
import auruda.auruda.dto.location.Location;
import auruda.auruda.dto.trip.CreateItineraryRequestDto;
import auruda.auruda.dto.trip.TouristSpotDto;
import auruda.auruda.service.distance.DistanceMatrixService;
import auruda.auruda.service.distance.NearestNeighborService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * OPEN AI를 통해 여행장소를 생성하고 추천받는 서비스
 */
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate=new RestTemplate();
    private final ObjectMapper objectMapper;
    private final GptPlaceRedisService gptPlaceResponseService;
    private final DistanceMatrixService distanceMatrixService;
    private final NearestNeighborService nearestNeighborService;


    @Value("${openai.api.key}")
    private String apiKey;


    // 여행코스 추천

    /**
     * [서비스 로직]
     * 여행 코스 추천
     * @param dto  여행계획 생성 요청 DTO
     * @param places 장소 리스트
     * @return List<GptPlaceResponseDto>
     * @throws Exception
     */
    public List<GptPlaceResponseDto> getTravelItinerary(CreateItineraryRequestDto dto, List<TouristSpotDto> places) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";

        //Redis 키는 지역 이름으로 지정
        String rediskey = String.format("GptItinerary:%s:%s:%s:%s:%s",
                dto.getCity().trim(),
                dto.getTravelDay(),
                dto.getTheme1().trim(),
                dto.getTheme2().trim(),
                dto.getTheme3().trim());

        // Redis에서 데이터가 존재하는지 확인
        List<GptPlaceResponseDto> cachedData = checkedRedis(places, rediskey);

        //Redis에서 가져온 데이터가 있으면 해당 데이터 반환
        if (cachedData != null) return cachedData;

        List<GptPlaceRequestDto> dtoList = new ArrayList<>();

        for (TouristSpotDto place : places) {
            GptPlaceRequestDto gptPlaceRequestDto = GptPlaceRequestDto.create(place.getName(), place.getCategory());
            dtoList.add(gptPlaceRequestDto);
        }

        JsonNode jsonNode = objectMapper.valueToTree(dtoList);

        //GPT에게 요청하기 위한 바디와 헤더 설정
        HttpEntity<Map<String, Object>> entity = createGptRequest(dto, jsonNode);

        //GPT API에 요청하고 받은 데이터를 JSON으로 파싱
        List<GptPlaceResponseDto> placeResponseDtoList = parseAndValidateTripPlan(dto, places, url, entity);

        if (placeResponseDtoList == null || placeResponseDtoList.isEmpty()) {
            throw new Exception("유효한 여행 코스를 생성하지 못했습니다.");
        }

        //최단 경로 구하기
        List<GptPlaceResponseDto> reorderedPlacesByDay = getNearstNeighborPlace(placeResponseDtoList);

        gptPlaceResponseService.saveData(rediskey, reorderedPlacesByDay);
        return reorderedPlacesByDay;

    }


    //==AI 응답 파서 및 검증==//
    private List<GptPlaceResponseDto> parseAndValidateTripPlan(CreateItineraryRequestDto dto, List<TouristSpotDto> places, String url, HttpEntity<Map<String, Object>> entity) throws JsonProcessingException {
        int retryCount = 0;
        int maxRetries = 3; // 최대 재시도 횟수


        List<GptPlaceResponseDto> placeResponseDtoList = null;

        while (retryCount < maxRetries) {
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

            System.out.println("response = " + response);

            JsonNode contentNode = objectMapper.readTree(response)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("function_call")
                    .path("arguments");

            System.out.println("contentNode = " + contentNode);

            // contentNode를 JSON으로 변환
            String jsonContent = contentNode.asText().replace("\\n", "").replace("\\\"", "\"");

            // JSON 문자열 파싱
            JsonNode locationsArray = objectMapper.readTree(jsonContent).path("itinerary");

            System.out.println("locationsArray = " + locationsArray);

            if (locationsArray.isArray() && locationsArray.size() == 6 * Integer.parseInt(dto.getTravelDay())) {

                placeResponseDtoList = new ArrayList<>();
                for (JsonNode place : locationsArray) {
                    String day = place.path("day").asText("없음");
                    String name = place.path("name").asText("없음");
                    String category = place.path("category").asText("없음");
                    String description = place.path("description").asText("없음");

                    // 특정 이름과 일치하는 PlaceInfoDto 찾기 (존재하지 않을 경우 예외 처리)
                    Optional<TouristSpotDto> touristSpotDto = places.stream()
                            .filter(p -> p.getName().equals(name))
                            .findFirst();

                    if (touristSpotDto.isPresent()) {
                        TouristSpotDto spotDto = touristSpotDto.get();

                        GptPlaceResponseDto gptPlaceResponseDto = GptPlaceResponseDto.create(spotDto.getPlaceId(),
                                spotDto.getVisitedCount(),
                                day,
                                name,
                                spotDto.getCity(),
                                spotDto.getAddress(),
                                spotDto.getLat(),
                                spotDto.getLng(),
                                spotDto.getImageUrl(),
                                description,
                                category
                        );


                        placeResponseDtoList.add(gptPlaceResponseDto);

                    }
                }
                break; // 유효한 데이터가 반환되면 루프 종료
            } else {
                System.out.println("응답 객체 수가 예상치와 다릅니다. 재시도합니다.");
                retryCount++;
            }
        }
        return placeResponseDtoList;
    }

    //==Redis 에서 캐시된 장소데이터가 있는지 확인==//
    private List<GptPlaceResponseDto> checkedRedis(List<TouristSpotDto> places, String rediskey) {
        if (gptPlaceResponseService.exists(rediskey)) {
            System.out.println("Redis에서 데이터 확인...");
            // Redis에서 데이터 가져오기
            List<GptPlaceResponseDto> cachedData = gptPlaceResponseService.getData(rediskey);

            // Redis 데이터와 places 동기화
            syncRedisWithPlaces(cachedData, places);

            // 갱신된 데이터를 Redis에 다시 저장
            gptPlaceResponseService.saveData(rediskey, cachedData);

            // 갱신된 데이터 반환
            return cachedData;
        }
        return null;
    }

    //==요청 생성==//
    private HttpEntity<Map<String, Object>> createGptRequest(CreateItineraryRequestDto dto, JsonNode jsonNode) {
        // System 메시지 생성
        String systemRequest = """
                당신은 여행 코스를 추천하는 AI입니다.
        """;


        // User 메시지 생성
        String userRequest = dto.getStartDate() + "부터 " + dto.getEndDate() + "까지 " + dto.getCity() + "으로 여행을 떠날거야" + jsonNode.toString()+"안에 있는 장소 데이터 안에서(추가 데이터는 허용하지 않음)"+
                dto.getTheme1() + ", " + dto.getTheme2() + ", " + dto.getTheme3() + "의 여행 테마에 알맞게 " + dto.getTravelDay() +
                "일 차 만큼의 여행 코스를 효율적인 순서를 고려해서 추천해줘. `itinerary` 배열은 정확히" +(6 *Integer.parseInt(dto.getTravelDay()) )+"개의 데이터를 포함해야해";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4-turbo");
        requestBody.put("max_tokens", 2500);
        requestBody.put("temperature", 0.2);

        // Functions 정의
        Map<String, Object> functionSchema = createFunctionSchema(Integer.parseInt(dto.getTravelDay()));
        requestBody.put("functions", List.of(functionSchema));
//        requestBody.put("function_call", "auto"); // 함수 자동 호출

        // `function_call` 설정
        requestBody.put("function_call", Map.of("name", "generate_travel_itinerary")); // 특정 함수 호출 강제

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemRequest);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userRequest);


        requestBody.put("messages", new Map[]{systemMessage, userMessage});

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        return entity;
    }

    //==최단 코스를 계산하는 알고리즘 로직==//
    private List<GptPlaceResponseDto> getNearstNeighborPlace(List<GptPlaceResponseDto> placeResponseDtoList) {

        //Day별로 해쉬맵 생성
        assert placeResponseDtoList != null;
        Map<String, List<GptPlaceResponseDto>> stringListMap = groupByDay(placeResponseDtoList);

        //최종 여행코스를 저장할 리스트
        List<GptPlaceResponseDto> reorderedPlacesByDay = new ArrayList<>();

        //Map의 모든 key-value를 순회
        for (Map.Entry<String, List<GptPlaceResponseDto>> entry : stringListMap.entrySet()) {
            String day = entry.getKey(); //현재 day의 값
            List<GptPlaceResponseDto> placesByDay = entry.getValue();//현재 day에 해당하는 장소 리스트

            // List<Location> 생성
            List<Location> locationList = new ArrayList<>();

            //Location 객체 생성
            for (GptPlaceResponseDto gptPlaceResponseDto : placesByDay) {

                Location location = Location.create(gptPlaceResponseDto.getLat(), gptPlaceResponseDto.getLng(), gptPlaceResponseDto.getName());

                // 생성된 Location 객체를 리스트에 추가
                locationList.add(location);
            }

            //거리 행렬 생성
            double[][] distanceMatrix = distanceMatrixService.createDistanceMatrix(locationList);

            // 최단 경로 계산
            List<Integer> path = nearestNeighborService.findShortestPath(distanceMatrix);

            // 경로 반환 (장소 이름으로 변환)
            for (int index : path) {
                reorderedPlacesByDay.add(placesByDay.get(index));
            }
        }
        return reorderedPlacesByDay;
    }

    //==places 리스트에서 Redis 데이터와 이름이 일치하는 항목 찾기==//
    private void syncRedisWithPlaces(List<GptPlaceResponseDto> cachedData, List<TouristSpotDto> places) {
        for (GptPlaceResponseDto cached : cachedData) {
            // places 리스트에서 Redis 데이터와 이름이 일치하는 항목 찾기
            Optional<TouristSpotDto> matchingPlace = places.stream()
                    .filter(p -> p.getName().equals(cached.getName()))
                    .findFirst();

            if (matchingPlace.isPresent()) {
                TouristSpotDto place = matchingPlace.get();

                // Redis 데이터 갱신
                cached.setTripPlaceId(place.getPlaceId());
                cached.setVisitedCount(place.getVisitedCount());

            }
        }
    }

    //==GptPlaceResponseDto를 Day 별로 해쉬맵에 저장==//
    private Map<String,List<GptPlaceResponseDto>> groupByDay(List<GptPlaceResponseDto>places){
        Map<String,List<GptPlaceResponseDto>> groupedByDay = new HashMap<>();
        for (GptPlaceResponseDto place : places) {
            groupedByDay.putIfAbsent(place.getDay(), new ArrayList<>());
            groupedByDay.get(place.getDay()).add(place);
        }
        return groupedByDay;
    }

    //==GPT 명령 함수를 생성하는 로직==//
    private Map<String, Object> createFunctionSchema(int travelDay) {
        return Map.of(
                "name", "generate_travel_itinerary",
                "description", "하루 일정에 반드시 각 객체가 6개 들어가야 하고 그 중 숙소에 대한 장소가 정확히 1개만 있어야 하며, 식당에 대한 장소가 2개 있어야 합니다. 마지막 일차에는 숙소 데이터가 있으면 안됩니다. 하루에 6개씩 총 " + travelDay + "일 일정으로 생성하세요. 총 "+6*travelDay+"개의 데이터가 있어야합니다. 모든 응답은 한국어로 작성하세요.  category는 [카페, 숙소, 식당, 쇼핑몰, 액티비티, 관광지, 행사, 산, 바다, 테마파크] 중 하나여야 합니다. description 에는 여행지에 대한 자세한 설명이 들어가야 합니다. day 에는 여행 일차가 들어가야합니다.한 번 사용한 장소는 재사용해서는 안됩니다.",
                "parameters", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "itinerary", Map.of( // 배열 필드 정의
                                        "type", "array",
                                        "minItems", 6 * travelDay, // 최소 데이터 개수
                                        "maxItems", 6 * travelDay, // 최대 데이터 개수
                                        "items", Map.of( // 배열의 각 요소 정의
                                                "type", "object",
                                                "properties", Map.of(
                                                        "day", Map.of("type", "string"),
                                                        "name", Map.of("type", "string"),
                                                        "category", Map.of("type", "string"),
                                                        "description", Map.of("type", "string")
                                                ),
                                                "required", List.of("day", "name", "category", "description")
                                        )
                                )
                        ),
                        "required", List.of("itinerary"),
                        "examples", List.of( // 예제 데이터 추가
                                Map.of(
                                        "itinerary", List.of(
                                                Map.of(
                                                        "day", "1",
                                                        "name", "경복궁",
                                                        "category", "관광지",
                                                        "description", "조선 시대의 대표적인 궁궐로, 한국 역사와 문화를 체험할 수 있는 장소입니다."
                                                ),
                                                Map.of(
                                                        "day", "1",
                                                        "name", "북촌 한옥마을",
                                                        "category", "관광지",
                                                        "description", "전통 한옥이 모여 있는 아름다운 마을로, 산책과 사진 촬영에 적합합니다."
                                                ),
                                                Map.of(
                                                        "day", "1",
                                                        "name", "광장시장",
                                                        "category", "식당",
                                                        "description", "다양한 한국 전통 음식을 즐길 수 있는 시장입니다."
                                                ),
                                                Map.of(
                                                        "day", "1",
                                                        "name", "익선동 카페거리",
                                                        "category", "카페",
                                                        "description", "독특한 분위기의 카페가 모여 있는 익선동의 숨은 명소입니다."
                                                ),
                                                Map.of(
                                                        "day", "1",
                                                        "name", "롯데월드타워",
                                                        "category", "쇼핑몰",
                                                        "description", "한국에서 가장 높은 건물로, 쇼핑과 전망대 체험을 할 수 있습니다."
                                                ),
                                                Map.of(
                                                        "day", "1",
                                                        "name", "호텔 신라",
                                                        "category", "숙소",
                                                        "description", "서울 중심에 위치한 고급 호텔로 편안한 숙박을 제공합니다."
                                                )
                                        )
                                )
                        )
                )
        );
    }


}
