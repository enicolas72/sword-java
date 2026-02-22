package net.eric_nicolas.sword.ui;

/**
 * Point - 2D point representation.
 */
public class Point {

    /**
     * Constructor with coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     *
     * @param other Point to copy
     */
    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Adds the given amounts to this point, returns a new Point
     *
     * @param dx X offset
     * @param dy Y offset
     */
    public Point plus(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    /**
     * Adds the given point.x, point.y to this point, returns a new Point
     *
     * @param dp X,Y offset
     */
    public Point plus(Point dp) {
        return new Point(x + dp.x, y + dp.y);
    }

    /**
     * Subtracts the given amounts to this point, returns a new Point
     *
     * @param dx X offset
     * @param dy Y offset
     */
    public Point minus(int dx, int dy) {
        return new Point(x - dx, y - dy);
    }

    /**
     * Subtracts the given point.x, point.y to this point, returns a new Point
     *
     * @param dp X,Y offset
     */
    public Point minus(Point dp) {
        return new Point(x - dp.x, y - dp.y);
    }

    /**
     * Returns a point that has the minimum coordinate of the two provided
     * @param a first point
     * @param b second point
     * @return the new point with minimum coordinates
     */
    public static Point min(Point a, Point b) {
        int x = Math.min(a.x(), b.x());
        int y = Math.min(a.y(), b.y());
        return new Point(x, y);
    }

    /**
     * Returns a point that has the maximum coordinate of the two provided
     * @param a first point
     * @param b second point
     * @return the new point with maximum coordinates
     */
    public static Point max(Point a, Point b) {
        int x = Math.max(a.x(), b.x());
        int y = Math.max(a.y(), b.y());
        return new Point(x, y);
    }

    /**
     * @return the x position of the point
     */
    public int x() {
        return x;
    }

    /**
     * @return the y position of the point
     */
    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Point pointObj) {
            return x == pointObj.x && y == pointObj.y;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "TPoint(" + x + ", " + y + ")";
    }

    //

    private int x;
    private int y;
}
