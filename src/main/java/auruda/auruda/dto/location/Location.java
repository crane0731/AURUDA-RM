package auruda.auruda.dto.location;

import lombok.Getter;
import lombok.Setter;

/**
 * 위치를 표현하는 DTO
 */
@Getter
@Setter
public class Location {

    private double latitude;
    private double longitude;
    private String name; //장소 이름


    /**
     * [생성 메서드]
     * @param latitude 위도
     * @param longitude 경도
     * @param name 이름
     * @return Location
     */
    public static Location create(double latitude, double longitude, String name) {
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setName(name);
        return location;
    }

}
