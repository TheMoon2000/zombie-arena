package byow.hw4;

import byow.proj2ab.ArrayHeapMinPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {

    private SolverOutcome outcome = SolverOutcome.SOLVED;
    private List<Vertex> solution = new ArrayList<>();
    private double solutionWeight = 0;
    private int numStatesExplored = 1;
    private double explorationTime;

    private ArrayHeapMinPQ<Vertex> minPQ = new ArrayHeapMinPQ<>();
    private HashMap<Vertex, Item> map = new HashMap<>();

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch s = new Stopwatch();
        boolean out = false;
        minPQ.add(start, input.estimatedDistanceToGoal(start, end) + 0);
        Item i = new Item(0, null, 0);
        map.put(start, i);
        Vertex result = minPQ.getSmallest();
        while (!result.equals(end)) {
            if (s.elapsedTime() > timeout) {
                out = true;
                break;
            }
            List<WeightedEdge<Vertex>> edges = input.neighbors(result);
            for (WeightedEdge<Vertex> e : edges) {
                relax(input, end, e, result, minPQ, map);
            }
            if (minPQ.size() == 0) {
                outcome = SolverOutcome.UNSOLVABLE;
                break;
            }
            result = minPQ.removeSmallest();
            numStatesExplored++;
        }
        if (out) {
            outcome = SolverOutcome.TIMEOUT;
            solutionWeight = 0;
            solution = new ArrayList<>();
        }
        if (outcome.equals(SolverOutcome.SOLVED)) {
            listOfVertice(map, start, end, solution);
        }
        explorationTime = s.elapsedTime();
    }

    public SolverOutcome outcome() {
        return outcome;
    }

    public List<Vertex> solution() {
        return solution;
    }

    public double solutionWeight() {
        return solutionWeight;
    }

    public int numStatesExplored() {
        return numStatesExplored;
    }

    public double explorationTime() {
        return explorationTime;
    }


    private void relax(AStarGraph<Vertex> input, Vertex end, WeightedEdge<Vertex> e, Vertex v,
                       ArrayHeapMinPQ<Vertex> minPQLocal, HashMap<Vertex, Item> mapLocal) {
        Vertex w = e.to();
        double oldScore;
        if (mapLocal.containsKey(w)) {
            oldScore = mapLocal.get(w).distTo;
        } else {
            oldScore = Double.POSITIVE_INFINITY;
        }
        double newScore = mapLocal.get(v).distTo + e.weight();
        if (newScore < oldScore) {
            if (minPQLocal.contains(w)) {
                minPQLocal.changePriority(w, newScore + input.estimatedDistanceToGoal(w, end));
            } else {
                minPQLocal.add(w, newScore + input.estimatedDistanceToGoal(w, end));
            }
            if (mapLocal.containsKey(w)) {
                mapLocal.replace(w, new Item(newScore, v, e.weight()));
            } else {
                mapLocal.put(w, new Item(newScore, v, e.weight()));
            }
        }
    }

    private void listOfVertice(HashMap<Vertex, Item> mapLocal,
                               Vertex start, Vertex end, List<Vertex> l) {
        if (end.equals(start)) {
            l.add(start);
        } else {
            Vertex v = mapLocal.get(end).edgeTo;
            solutionWeight += mapLocal.get(end).weight;
            listOfVertice(mapLocal, start, v, l);
            l.add(end);
        }
    }

    private class Item {
        double distTo;
        Vertex edgeTo;
        double weight;

        Item(double d, Vertex e, double w) {
            distTo = d;
            edgeTo = e;
            weight = w;
        }
    }
}
