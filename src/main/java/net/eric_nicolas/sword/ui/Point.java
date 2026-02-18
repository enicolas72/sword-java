package net.eric_nicolas.sword.ui;

/**
 * TPoint - 2D point representation.
 */
public class Point {

    public int x;
    public int y;

    /**
     * Default constructor - creates point at origin (0, 0).
     */
    public Point() {
        this(0, 0);
    }

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
     * Set coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Offset this point by given amounts.
     *
     * @param dx X offset
     * @param dy Y offset
     */
    public void offset(int dx, int dy) {
        this.x += dx;
        this.y += dy;
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
}
