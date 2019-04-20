package byow.utils;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.*;

public class NearTree implements PointSet {
    private KDNode rootNode;
    private WeightedQuickUnionUF connections;
    private Map<Point, Integer> pointToIndex;

    public NearTree(Iterable<Point> points) {
        pointToIndex = new HashMap<>();
        int i = 0;
        for (Point p: points) {
            pointToIndex.put(p, i);
            rootNode = addChild(rootNode, new KDNode(p));
            i++;
        }
        connections = new WeightedQuickUnionUF(pointToIndex.size());
    }

    @Override
    public Point nearest(int x, int y) {

        if (rootNode == null) {
            return null;
        }

        Point target = new Point(x, y);
        Point returnPoint;
        if (!rootNode.point.equals(target)) {
            returnPoint = nearest(target, rootNode, rootNode).point;
        } else {
            KDNode node1 = nearest(target, rootNode.leftChild, rootNode.leftChild);
            KDNode node2 = nearest(target, rootNode.rightChild, rootNode.rightChild);
            if (node1 == null) {
                returnPoint = node2.point;
            } else if (node2 == null) {
                returnPoint = node1.point;
            } else if (Point.distance(node1.point, target) < Point.distance(node2.point, target)) {
                returnPoint = node1.point;
            } else {
                returnPoint = node2.point;
            }
        }
        connections.union(pointToIndex.get(target), pointToIndex.get(returnPoint));
        return returnPoint;
    }

    // Private class and methods

    private class KDNode {
        Point point;
        boolean orientation; // vertical split?
        KDNode leftChild; // top or left
        KDNode rightChild; // bottom or right

        KDNode(Point point) {
            this.point = point;
            orientation = true;
        }

    }

    private static KDNode addChild(KDNode parent, KDNode child) {
        if (parent == null) {
            return child;
        }
        if (parent.equals(child)) {
            return parent;
        }

        double coordinateDifference;
        if (parent.orientation) {
            coordinateDifference = child.point.getX() - parent.point.getX(); // left
            child.orientation = false;
        } else {
            coordinateDifference = child.point.getY() - parent.point.getY(); // right
            child.orientation = true;
        }

        if (coordinateDifference < 0) {
            parent.leftChild = addChild(parent.leftChild, child);
        } else {
            parent.rightChild = addChild(parent.rightChild, child);
        }

        return parent;
    }

    private KDNode nearest(Point target, KDNode current, KDNode best) {
        if (current == null) {
            return best;
        }

        if (best != null && target.equals(best.point)) {
            throw new RuntimeException("best = target!");
        }

        int targetIndex = pointToIndex.get(target);
        int currentIndex = pointToIndex.get(current.point);

        if (Point.distance(current.point, target) < Point.distance(best.point, target)
                && !connections.connected(targetIndex, currentIndex)) {
            best = current;
        }

        double coordinateDifference;
        if (current.orientation) { // vertical split
            coordinateDifference = target.getX() - current.point.getX();
        } else { // horizontal split
            coordinateDifference = target.getY() - current.point.getY();
        }

        KDNode favoredChild, otherChild;

        if (coordinateDifference < 0) { // target is on left side
            favoredChild = current.leftChild;
            otherChild = current.rightChild;
        } else {
            favoredChild = current.rightChild;
            otherChild = current.leftChild;
        }

        best = nearest(target, favoredChild, best);

        Point potential;
        if (current.orientation) {
            potential = new Point(current.point.getX(), target.getY());
        } else {
            potential = new Point(target.getX(), current.point.getY());
        }

        if (Point.distance(potential, target) < Point.distance(best.point, target)) {
            best = nearest(target, otherChild, best); // With pruned bounds
        }

        return best;
    }
}
