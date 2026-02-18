package net.eric_nicolas.sword.ui;

/**
 * TRect - Rectangle representation.
 * Uses top-left corner (a) and bottom-right corner (b).
 */
public class Rect {

    public Point a;  // Top-left corner
    public Point b;  // Bottom-right corner

    /**
     * Default constructor - creates empty rectangle at origin.
     */
    public Rect() {
        this.a = new Point(0, 0);
        this.b = new Point(0, 0);
    }

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
        return b.x - a.x;
    }

    /**
     * Get height of rectangle.
     *
     * @return Height
     */
    public int height() {
        return b.y - a.y;
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
        return p.x >= a.x && p.x < b.x && p.y >= a.y && p.y < b.y;
    }

    /**
     * Check if point is inside rectangle.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if point is inside
     */
    public boolean contains(int x, int y) {
        return x >= a.x && x < b.x && y >= a.y && y < b.y;
    }

    /**
     * Offset rectangle by given amounts.
     *
     * @param dx X offset
     * @param dy Y offset
     */
    public void offset(int dx, int dy) {
        a.offset(dx, dy);
        b.offset(dx, dy);
    }

    /**
     * Grow rectangle by given amounts in all directions.
     *
     * @param dx Horizontal growth
     * @param dy Vertical growth
     */
    public void grow(int dx, int dy) {
        a.x -= dx;
        a.y -= dy;
        b.x += dx;
        b.y += dy;
    }

    /**
     * Intersect this rectangle with another.
     *
     * @param r Rectangle to intersect with
     * @return true if rectangles intersect
     */
    public boolean intersect(Rect r) {
        if (a.x < r.a.x) a.x = r.a.x;
        if (a.y < r.a.y) a.y = r.a.y;
        if (b.x > r.b.x) b.x = r.b.x;
        if (b.y > r.b.y) b.y = r.b.y;
        return !isEmpty();
    }

    /**
     * Union this rectangle with another.
     *
     * @param r Rectangle to union with
     */
    public void union(Rect r) {
        if (r.isEmpty()) return;
        if (isEmpty()) {
            a.set(r.a.x, r.a.y);
            b.set(r.b.x, r.b.y);
            return;
        }
        if (r.a.x < a.x) a.x = r.a.x;
        if (r.a.y < a.y) a.y = r.a.y;
        if (r.b.x > b.x) b.x = r.b.x;
        if (r.b.y > b.y) b.y = r.b.y;
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
        return "TRect(" + a.x + ", " + a.y + ", " + b.x + ", " + b.y + ")";
    }
}
