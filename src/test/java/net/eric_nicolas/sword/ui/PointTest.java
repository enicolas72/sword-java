package net.eric_nicolas.sword.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TPoint - point geometry.
 */
class PointTest {

    @Test
    void testParameterizedConstructor() {
        Point p = new Point(10, 20);
        assertEquals(10, p.x());
        assertEquals(20, p.y());
    }

    @Test
    void testCopyConstructor() {
        Point p1 = new Point(15, 25);
        Point p2 = new Point(p1);
        assertEquals(15, p2.x());
        assertEquals(25, p2.y());
    }

    @Test
    void testSet() {
        Point p = new Point(30, 40);
        assertEquals(30, p.x());
        assertEquals(40, p.y());
    }

    @Test
    void testPlus() {
        Point p = new Point(10, 20);
        p = Point.plus(p, 5, -3);
        assertEquals(15, p.x());
        assertEquals(17, p.y());
    }

    @Test
    void testEquals() {
        Point p1 = new Point(10, 20);
        Point p2 = new Point(10, 20);
        Point p3 = new Point(10, 21);

        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(null));
    }
}
