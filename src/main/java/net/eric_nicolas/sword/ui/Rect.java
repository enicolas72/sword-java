package net.eric_nicolas.sword.ui;

/**
 * Rect - Rectangle representation.
 * Uses top-left corner (a) and bottom-right corner (b).
 */
public final class Rect {

    /**
     * Constructor with coordinates.
     *
     * @param x1 Left coordinate
     * @param y1 Top coordinate
     * @param x2 Right coordinate
     * @param y2 Bottom coordinate
     */
    public Rect(int x1, int y1, int x2, int y2) {
        this.a = new Point(x1, y1);
        this.b = new Point(x2, y2);
    }

    /**
     * Constructor with points.
     *
     * @param a Top-left corner
     * @param b Bottom-right corner
     */
    public Rect(Point a, Point b) {
        this.a = new Point(a);
        this.b = new Point(b);
    }

    /**
     * Copy constructor.
     *
     * @param other Rectangle to copy
     */
    public Rect(Rect other) {
        this.a = new Point(other.a);
        this.b = new Point(other.b);
    }

    /**
     * Get width of rectangle.
     *
     * @return Width
     */
    public int width() {
        return b.x() - a.x();
    }

    /**
     * Get height of rectangle.
     *
     * @return Height
     */
    public int height() {
        return b.y() - a.y();
    }

    public Point a() {
        return a;
    }

    public Point b() {
        return b;
    }

    /**
     * Check if rectangle is empty (zero or negative width/height).
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return width() <= 0 || height() <= 0;
    }

    /**
     * Check if point is inside rectangle.
     *
     * @param p Point to test
     * @return true if point is inside
     */
    public boolean contains(Point p) {
        return contains(p.x(), p.y());
    }

    /**
     * Check if point is inside rectangle.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if point is inside
     */
    public boolean contains(int x, int y) {
        return x >= a.x() && x < b.x() && y >= a.y() && y < b.y();
    }

    /**
     * Adds the given amounts to a rectangle coordinates
     *
     * @param a The rectangle to move
     * @param dx X offset
     * @param dy Y offset
     * @return the offsetted rectangle
     */
    public static Rect plus(Rect a, int dx, int dy) {
        return new Rect(
                Point.plus(a.a, dx, dy),
                Point.plus(a.b, dx, dy));
    }

    /**
     * Grow a rectangle by given amounts in all directions.
     *
     * @param a The rectangle to grow
     * @param dx Horizontal growth
     * @param dy Vertical growth
     * @return the grown rectangle
     */
    public static Rect grow(Rect a, int dx, int dy) {
        return new Rect(
                Point.minus(a.a, dx, dy),
                Point.plus(a.b, dx, dy));
    }

    /**
     * Intersect one rectangle with another.
     *
     * @param a First rectangle for the intersection
     * @param b Second rectangle for the intersection
     * @return the rectangle intersection of a and b
     */
    public static Rect intersect(Rect a, Rect b) {
        return new Rect(
                Point.max(a.a, b.a),
                Point.min(a.b, b.b));
    }

    /**
     * Union one rectangle with another.
     *
     * @param a First rectangle to union with
     * @param b Second rectangle to union with
     * @return the rectangle union of a and b
     */
    public static Rect union(Rect a, Rect b) {
        if (b.isEmpty()) return a;
        if (a.isEmpty()) return b;

        return new Rect(
            Point.min(a.a, b.a),
            Point.max(a.b, b.b));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Rect objRect) {
            return a.equals(objRect.a) && b.equals(objRect.b);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * a.hashCode() + b.hashCode();
    }

    @Override
    public String toString() {
        return "TRect(" + a.x() + ", " + a.y() + ", " + b.x() + ", " + b.y() + ")";
    }

    //

    private Point a;  // Top-left corner
    private Point b;  // Bottom-right corner
}
