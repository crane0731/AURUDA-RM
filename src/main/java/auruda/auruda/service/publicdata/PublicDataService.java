package auruda.auruda.service.publicdata;

import auruda.auruda.domain.Place;
import auruda.auruda.dto.trip.TouristSpotDto;
import auruda.auruda.enums.Category;
import auruda.auruda.service.place.PlaceService;
import auruda.auruda.service.tourist.TouristSpotRedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 공공데이터 포탈에서 여행지 정보를 가져오는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicDataService {


    private final RestTemplate restTemplate=new RestTemplate(); //redis template
    private final ObjectMapper objectMapper; //오브젝트 매퍼
    private final TouristSpotRedisService touristSpotRedisService; //여행지 redis 서비스
    private final PlaceService placeService;//장소 서비스

    @Value("${data.api.key}")
    private String apiKey;
    private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService1/areaBasedList1";



    public List<TouristSpotDto> getTouristSpots(String city, Integer contentTypeId, Integer areaCode, Integer sigunguCode , Integer pageNo) {

        //Redis 키는 지역 이름으로 지정
        String rediskey = String.format("TouristSpot:%s:%s:%s:%s",
                city != null ? city : "",
                areaCode != 0 ? String.valueOf(areaCode).trim() : "unknown",
                contentTypeId != 0 ? String.valueOf(contentTypeId).trim() : "unknown",
                pageNo != 0 ? String.valueOf(pageNo).trim():"unknown");




        if (touristSpotRedisService.exists(rediskey)) {
            System.out.println("Redis에서 데이터 확인...");
            List<TouristSpotDto> cachedData = touristSpotRedisService.getData(rediskey);

            // Redis에 있는 데이터를 데이터베이스와 동기화 (travelPlaceId 확인 및 갱신)
            for (TouristSpotDto spot : cachedData) {
                Place place = placeService.findPlaceByNameAndAddress(spot.getName(), spot.getAddress());
                Long paceId = (place != null) ? place.getId() : null;
                Long visitedCount=(place != null) ? place.getVisitedCount() : null;

                // travelPlaceId를 Redis 데이터에도 반영
                spot.setPlaceId(paceId);
                spot.setVisitedCount(visitedCount);
            }

            // Redis에 갱신된 데이터를 다시 저장
            touristSpotRedisService.saveData(rediskey, cachedData);

            // 저장된 데이터를 바로 반환 (다시 조회할 필요 없음)
            return cachedData;
        }

        String url=null;
        if(contentTypeId==1){
            if (sigunguCode==null){
                url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                        .queryParam("serviceKey", apiKey)
                        .queryParam("numOfRows", 10)
                        .queryParam("pageNo",pageNo)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "AURUDA")
                        .queryParam("_type", "json")
                        .queryParam("listYN", "Y")
                        .queryParam("arrange", "A")
                        .queryParam("contentTypeId", 39)
                        .queryParam("cat1","A05")
                        .queryParam("cat2","A0502")
                        .queryParam("cat3","A05020900")
                        .queryParam("areaCode", areaCode)
                        .build()
                        .toUriString();
            }else {
                url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                        .queryParam("serviceKey", apiKey)
                        .queryParam("numOfRows", 10)
                        .queryParam("pageNo", pageNo)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "AURUDA")
                        .queryParam("_type", "json")
                        .queryParam("listYN", "Y")
                        .queryParam("arrange", "A")
                        .queryParam("contentTypeId", 39)
                        .queryParam("cat1", "A05")
                        .queryParam("cat2", "A0502")
                        .queryParam("cat3", "A05020900")
                        .queryParam("areaCode", areaCode)
                        .queryParam("sigunguCode", sigunguCode)
                        .build()
                        .toUriString();
            }
        }
        else{
            if(sigunguCode==null){
                url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                        .queryParam("serviceKey", apiKey)
                        .queryParam("numOfRows", 10)
                        .queryParam("pageNo",pageNo)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "AURUDA")
                        .queryParam("_type", "json")
                        .queryParam("listYN", "Y")
                        .queryParam("arrange", "A")
                        .queryParam("contentTypeId", contentTypeId)
                        .queryParam("areaCode", areaCode)
                        .build()
                        .toUriString();
            }else {
                //요청 url
                url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                        .queryParam("serviceKey", apiKey)
                        .queryParam("numOfRows", 10)
                        .queryParam("pageNo", pageNo)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "AURUDA")
                        .queryParam("_type", "json")
                        .queryParam("listYN", "Y")
                        .queryParam("arrange", "A")
                        .queryParam("contentTypeId", contentTypeId)
                        .queryParam("areaCode", areaCode)
                        .queryParam("sigunguCode", sigunguCode)
                        .build()
                        .toUriString();
            }
        }


        //요청 보내기
        String response = restTemplate.getForObject(url, String.class);

        System.out.println("response = " + response);


        // JSON 파싱 및 필드 추출
        List<TouristSpotDto> touristSpotList = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items").path("item");

            for (JsonNode item : items) {

                String addr1 = item.path("addr1").asText();
                //String addr2 = item.path("addr2").asText();
                String areacode = item.path("areacode").asText();
                String sigungucode = item.path("sigungucode").asText();
                String cat1 = item.path("cat1").asText();
                String cat2 = item.path("cat2").asText();
                String cat3 = item.path("cat3").asText();
                String firstimage = item.path("firstimage").asText();
                String title = item.path("title").asText();
                double mapx = item.path("mapx").asDouble();
                double mapy = item.path("mapy").asDouble();

                String category="";

                if(cat1.equals("A01")){
                    category= Category.TOURIST_ATTRACTION.name();
                }
                else if(cat1.equals("A02")){
                    if (cat2.equals("A0206")){
                        category=Category.CULTURAL_FACILITIES.name();
                    } else if (cat2.equals("A0201")||cat2.equals("A0202")||cat2.equals("A0203")||cat2.equals("A0204")||cat2.equals("A0205")) {
                        category=Category.TOURIST_ATTRACTION.name();
                    } else {
                        category=Category.EVENT.name();
                    }
                }

                else if(cat1.equals("A03")){
                    category=Category.ACTIVITY.name();
                } else if (cat1.equals("B02")) {
                    category=Category.ACCOMMODATION.name();
                } else if (cat1.equals("A04")) {
                    category= Category.SHOPPING_MALL.name();
                } else if (cat1.equals("A05") && cat2.equals("A0502")) {
                    if (cat3.equals("A05020900")){
                        category=Category.CAFE.name();
                    }else {
                        category=Category.RESTAURANT.name();
                    }
                }

                //데이터베이스에 이미 장소 데이터가 있다면 id값을 넘겨줌
                Place place = placeService.findPlaceByNameAndAddress(title, addr1);
                Long placeId = (place != null) ? place.getId() : null;

                //TouristSpotDto 객체 생성
                TouristSpotDto spot = TouristSpotDto.create(placeId, 0L, city, addr1, areacode, sigungucode, category, firstimage, title, mapx, mapy);

                touristSpotList.add(spot);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        touristSpotRedisService.saveData(rediskey, touristSpotList);

        return touristSpotList;

    }
}
