package net.eric_nicolas.sword.ui;

/**
 * Rect - Rectangle representation.
 * Stored as top-left origin plus width and height.
 */
public final class Rect {

    /**
     * Constructor with origin and size.
     *
     * @param x Left coordinate
     * @param y Top coordinate
     * @param width Width
     * @param height Height
     */
    public Rect(int x, int y, int width, int height) {
        this.origin = new Point(x, y);
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor with origin point and size.
     *
     * @param p Top-left corner
     * @param width Width
     * @param height Height
     */
    public Rect(Point p, int width, int height) {
        this.origin = new Point(p);
        this.width = width;
        this.height = height;
    }

    /**
     * Copy constructor.
     *
     * @param other Rectangle to copy
     */
    public Rect(Rect other) {
        this.origin = new Point(other.origin);
        this.width = other.width;
        this.height = other.height;
    }

    /**
     * Get width of rectangle.
     *
     * @return Width
     */
    public int width() {
        return width;
    }

    /**
     * Get height of rectangle.
     *
     * @return Height
     */
    public int height() {
        return height;
    }

    /** Top-left corner. */
    public Point origin() {
        return origin;
    }

    /**
     * Check if rectangle is empty (zero or negative width/height).
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return width <= 0 || height <= 0;
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
        return x >= origin.x() && x < origin.x() + width &&
               y >= origin.y() && y < origin.y() + height;
    }

    /**
     * Adds the given amounts to a rectangle's origin.
     *
     * @param r The rectangle to move
     * @param dx X offset
     * @param dy Y offset
     * @return the translated rectangle
     */
    public static Rect plus(Rect r, int dx, int dy) {
        return new Rect(Point.plus(r.origin, dx, dy), r.width, r.height);
    }

    /**
     * Grow a rectangle by given amounts in all directions.
     *
     * @param r The rectangle to grow
     * @param dx Horizontal growth (applied to each side)
     * @param dy Vertical growth (applied to each side)
     * @return the grown rectangle
     */
    public static Rect grow(Rect r, int dx, int dy) {
        return new Rect(Point.minus(r.origin, dx, dy), r.width + 2 * dx, r.height + 2 * dy);
    }

    /**
     * Intersect one rectangle with another.
     *
     * @param a First rectangle
     * @param b Second rectangle
     * @return the rectangle intersection of a and b
     */
    public static Rect intersect(Rect a, Rect b) {
        Point newA = Point.max(a.origin, b.origin);
        Point newB = Point.min(Point.plus(a.origin, a.width, a.height),
                               Point.plus(b.origin, b.width, b.height));
        return new Rect(newA, newB.x() - newA.x(), newB.y() - newA.y());
    }

    /**
     * Union one rectangle with another.
     *
     * @param a First rectangle
     * @param b Second rectangle
     * @return the rectangle union of a and b
     */
    public static Rect union(Rect a, Rect b) {
        if (b.isEmpty()) return a;
        if (a.isEmpty()) return b;

        Point newA = Point.min(a.origin, b.origin);
        Point newB = Point.max(Point.plus(a.origin, a.width, a.height),
                               Point.plus(b.origin, b.width, b.height));
        return new Rect(newA, newB.x() - newA.x(), newB.y() - newA.y());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Rect objRect) {
            return origin.equals(objRect.origin) && width == objRect.width && height == objRect.height;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * (31 * origin.hashCode() + width) + height;
    }

    @Override
    public String toString() {
        return "TRect(" + origin.x() + ", " + origin.y() + ", " + width + ", " + height + ")";
    }

    //

    private final Point origin;  // Top-left corner
    private final int width;
    private final int height;
}
