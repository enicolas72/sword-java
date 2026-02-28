package net.eric_nicolas.sword.ui;

import java.util.Objects;

/**
 * Triple&lt;X, Y, Z&gt; - An immutable triplet of typed values.
 *
 * Suitable for use as a map or cache key: equals() and hashCode() delegate
 * to the stored values, so two Triples with equal components are equal.
 */
public final class Triple<X, Y, Z> {

    private final X x;
    private final Y y;
    private final Z z;

    public Triple(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X x() { return x; }
    public Y y() { return y; }
    public Z z() { return z; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Triple<?, ?, ?> other)) return false;
        return Objects.equals(x, other.x)
            && Objects.equals(y, other.y)
            && Objects.equals(z, other.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Triple(" + x + ", " + y + ", " + z + ")";
    }
}
