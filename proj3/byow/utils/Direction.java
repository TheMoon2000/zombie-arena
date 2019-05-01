package byow.utils;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.hw4.AStarSolver;
import byow.hw4.WeightedUndirectedGraph;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

public enum Direction {
    West, East, North, South, Disoriented;

    static WeightedUndirectedGraph arena;
    static TETile[][] tiles;

    public static Direction parse(char c) {
        switch (c) {
            case 'W':
                return North;
            case 'S':
                return South;
            case 'D':
                return East;
            case 'A':
                return West;
            default:
                return Disoriented;
        }
    }

    public boolean vertical() {
        return this.equals(North) || this.equals(South);
    }

    public static void initPathFinder(TETile[][] myTiles, Point s) {
        Direction.tiles = myTiles;
        Set<Point> visited = new HashSet<>();
        WeightedUndirectedGraph graph = new WeightedUndirectedGraph();
        makeGraph(graph, visited, s);

        arena = graph;
    }

    /**
     * Load all the points in the map into an A* object for path computation
     * @param aStarGraph The graph to insert the points
     * @param visited tracker of whether a vertex is already added
     * @param s source point
     */

    private static void makeGraph(WeightedUndirectedGraph aStarGraph,
                           Set<Point> visited, Point s) {
        visited.add(s);
        Set<Point> adjacent = new HashSet<>();
        //check the four adjacent tiles to see if they are floors
        if (tiles[s.getX() + 1][s.getY()].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(s.getX() + 1, s.getY()));
        }
        if (tiles[s.getX() - 1][s.getY()].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(s.getX() - 1, s.getY()));
        }
        if (tiles[s.getX()][s.getY() + 1].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(s.getX(), s.getY() + 1));
        }
        if (tiles[s.getX()][s.getY() - 1].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(s.getX(), s.getY() - 1));
        }
        for (Point p : adjacent) {
            aStarGraph.addEdge(s, p);

            if (!visited.contains(p)) {
                makeGraph(aStarGraph, visited, p);
            }
        }
    }

    public static List<Point> shortestPath(Point s, Point t) {
        AStarSolver<Point> solver = new AStarSolver<>(arena, s, t);
        return solver.solution();
    }
}
