package net.eric_nicolas.sword.ui;

/**
 * Rect - Rectangle representation.
 * Uses top-left corner (a) and bottom-right corner (b).
 */
public class Rect {

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
     * Offset rectangle by given amounts.
     *
     * @param dx X offset
     * @param dy Y offset
     * @return the offsetted rectangle
     */
    public Rect offset(int dx, int dy) {
        return new Rect(
                a.plus(dx, dy),
                b.plus(dx, dy));
    }

    /**
     * Grow rectangle by given amounts in all directions.
     *
     * @param dx Horizontal growth
     * @param dy Vertical growth
     * @return the grown rectangle
     */
    public Rect grow(int dx, int dy) {
        return new Rect(
                a.plus(-dx, -dy),
                b.plus(dx, dy));
    }

    /**
     * Intersect this rectangle with another.
     *
     * @param r Rectangle to intersect with
     * @return the intersected rectangle
     */
    public Rect intersect(Rect r) {
        return new Rect(
                Point.max(a, r.a),
                Point.min(b, r.b));
    }

    /**
     * Union this rectangle with another.
     *
     * @param r Rectangle to union with
     */
    public void union(Rect r) {
        if (r.isEmpty()) return;
        if (isEmpty()) {
            a = new Point(r.a);
            b = new Point(r.b);
            return;
        }
        a = Point.min(a, r.a);
        b = Point.max(b, r.b);
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
