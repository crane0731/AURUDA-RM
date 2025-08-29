package auruda.auruda.service.distance;

import org.springframework.stereotype.Service;

/**
 * Haversine 공식을 사용해 두 좌표 사이의 거리 계산하는 클래스
 */
@Service
public class DistanceCalculator {

    //지구 반지름
    private static final double EARTH_RADIUS = 6371.0;


    /**
     * [서비스 로직]
     * Haversine 공식을 사용해 두 좌표 사이의 거리 계산
     * @param lat1 위도1
     * @param lng1 경도1
     * @param lat2 위도2
     * @param lng2 경도2
     * @return double
     */
    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {

        //위도 , 경도의 거리 차이
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        //Haversine 공식
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
