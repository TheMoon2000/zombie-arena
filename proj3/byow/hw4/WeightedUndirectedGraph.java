package byow.hw4;

import byow.utils.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeightedUndirectedGraph implements AStarGraph<Point>, Serializable {

    private HashMap<Point, List<WeightedEdge<Point>>> edgesMap = new HashMap<>();

    public List<WeightedEdge<Point>> neighbors(Point v) {
        return edgesMap.get(v);
    }

    public double estimatedDistanceToGoal(Point s, Point goal) {
        return Math.sqrt(Math.pow(s.getX() - goal.getX(), 2.0)
                + Math.pow(s.getY() - goal.getY(), 2.0));
    }

    public void addEdge(Point point1, Point point2) {
        WeightedEdge<Point> edge1 = new WeightedEdge<>(point1, point2, 1);
        WeightedEdge<Point> edge2 = new WeightedEdge<>(point2, point1, 1);
        if (edgesMap.containsKey(point1)) {
            edgesMap.get(point1).add(edge1);
        } else {
            edgesMap.put(point1, new ArrayList<>());
            edgesMap.get(point1).add(edge1);
        }
        if (edgesMap.containsKey(point2)) {
            edgesMap.get(point2).add(edge2);
        } else {
            edgesMap.put(point2, new ArrayList<>());
            edgesMap.get(point2).add(edge2);
        }
    }
}
