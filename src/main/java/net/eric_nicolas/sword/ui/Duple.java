package net.eric_nicolas.sword.ui;

import java.util.Objects;

/**
 * Duple<X, Y> - An immutable pair of typed values.
 *
 * Suitable for use as a map or cache key: equals() and hashCode() delegate
 * to the stored values, so two Duples with equal components are equal.
 */
public final class Duple<X, Y> {

    private final X x;
    private final Y y;

    public Duple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X x() { return x; }
    public Y y() { return y; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Duple<?, ?> other)) return false;
        return Objects.equals(x, other.x) && Objects.equals(y, other.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Duple(" + x + ", " + y + ")";
    }
}
