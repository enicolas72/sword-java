package net.eric_nicolas.sword.mechanism;

/**
 * TPoint - 2D point representation.
 */
public class TPoint {

    public int x;
    public int y;

    /**
     * Default constructor - creates point at origin (0, 0).
     */
    public TPoint() {
        this(0, 0);
    }

    /**
     * Constructor with coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public TPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     *
     * @param other Point to copy
     */
    public TPoint(TPoint other) {
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
        if (!(obj instanceof TPoint)) return false;
        TPoint other = (TPoint) obj;
        return x == other.x && y == other.y;
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
