package byow.hw4;

import byow.proj2ab.ArrayHeapMinPQ;
import java.util.HashMap;
import java.util.List;
import byow.proj2ab.ExtrinsicMinPQ;
import java.util.Map;
import java.util.LinkedList;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {

    private SolverOutcome solverOutcome;
    private LinkedList<Vertex> solution;
    private double solutionWeight;
    private int statesExplored;

    private AStarGraph<Vertex> inputGraph;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end) {
        this.inputGraph = input;

        ExtrinsicMinPQ<Vertex> pq = new ArrayHeapMinPQ<>();
        Map<Vertex, Double> distTo = new HashMap<>();
        Map<Vertex, Vertex> edgeTo = new HashMap<>();

        pq.add(start, h(start, end));
        distTo.put(start, 0.0);
        solverOutcome = SolverOutcome.UNSOLVABLE;

        while (pq.size() > 0) {

            Vertex current = pq.removeSmallest();

            if (current.equals(end)) {
                solverOutcome = SolverOutcome.SOLVED;
                solutionWeight = distTo.get(current);
                break;
            }

            statesExplored += 1;

            double d = distTo.get(current);
            for (WeightedEdge<Vertex> E: input.neighbors(current)) {
                if (d + E.weight() < distTo.getOrDefault(E.to(), Double.POSITIVE_INFINITY)) {
                    distTo.put(E.to(), d + E.weight());
                    edgeTo.put(E.to(), current);
                    if (pq.contains(E.to())) {
                        pq.changePriority(E.to(), d + E.weight() + h(E.to(), end));
                    } else {
                        pq.add(E.to(), d + E.weight() + h(E.to(), end));
                    }
                }
            }
        }

        solution = new LinkedList<>();

        if (solverOutcome == SolverOutcome.SOLVED) {
            Vertex v = end;
            while (edgeTo.getOrDefault(v, null) != null) {
                solution.addFirst(v);
                v = edgeTo.get(v);
            }
//            solution.addFirst(start);
        }
    }

    @Override
    public SolverOutcome outcome() {
        return solverOutcome;
    }

    @Override
    public List<Vertex> solution() {
        return solution;
    }

    @Override
    public double solutionWeight() {
        return solutionWeight;
    }

    @Override
    public int numStatesExplored() {
        return statesExplored;
    }

    @Override
    public double explorationTime() {
        return 100;
    }

    // Simplification

    private double h(Vertex s, Vertex t) {
        return inputGraph.estimatedDistanceToGoal(s, t);
    }
}
