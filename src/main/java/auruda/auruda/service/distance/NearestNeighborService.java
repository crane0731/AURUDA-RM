package auruda.auruda.service.distance;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 최단 거리를 계산하는 서비스
 */
@Service
public class NearestNeighborService {

    /**
     * [서비스 로직]
     * 최단거리 계산
     * @param distanceMatrix 거리 행렬
     * @return List<Integer>
     */
    public List<Integer> findShortestPath(double[][] distanceMatrix) {
        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();

        int current =0; //출발지
        visited[current] = true;
        path.add(current);
        while (path.size() < n) {
            double minDistance = Double.MAX_VALUE;
            int nextCity=-1;

            for (int i = 0; i < n; i++) {
                if(!visited[i] && distanceMatrix[current][i] < minDistance) {
                    minDistance = distanceMatrix[current][i];
                    nextCity = i;
                }
            }
            path.add(nextCity);
            visited[nextCity] = true;
            current = nextCity;
        }
        return path;
    }


}
