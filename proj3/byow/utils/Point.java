package byow.utils;

import java.io.Serializable;

public class Point implements Serializable {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Returns the true distance between two points
     */
    private static double distance(int x1, int x2, int y1, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
    }

    /**
     * Returns the euclidean distance (L2 norm) squared between two points.
     * Note: This is the square of the Euclidean distance, i.e.
     * there's no square root.
     */
    public static double distance(Point p1, Point p2) {
        return distance(p1.getX(), p2.getX(), p1.getY(), p2.getY());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        Point otherPoint = (Point) other;
        return getX() == otherPoint.getX() && getY() == otherPoint.getY();
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) ^ Double.hashCode(y);
    }

    @Override
    public String toString() {
        return String.format("Point x: %d, y: %d", x, y);
    }

    public Point left() {
        return new Point(x - 1, y);
    }

    public Point right() {
        return new Point(x + 1, y);
    }

    public Point top() {
        return new Point(x, y + 1);
    }

    public Point bottom() {
        return new Point(x, y - 1);
    }
}
