package net.eric_nicolas.sword.mechanism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TPoint - point geometry.
 */
class TPointTest {

    @Test
    void testDefaultConstructor() {
        TPoint p = new TPoint();
        assertEquals(0, p.x);
        assertEquals(0, p.y);
    }

    @Test
    void testParameterizedConstructor() {
        TPoint p = new TPoint(10, 20);
        assertEquals(10, p.x);
        assertEquals(20, p.y);
    }

    @Test
    void testCopyConstructor() {
        TPoint p1 = new TPoint(15, 25);
        TPoint p2 = new TPoint(p1);
        assertEquals(15, p2.x);
        assertEquals(25, p2.y);
    }

    @Test
    void testSet() {
        TPoint p = new TPoint();
        p.set(30, 40);
        assertEquals(30, p.x);
        assertEquals(40, p.y);
    }

    @Test
    void testOffset() {
        TPoint p = new TPoint(10, 20);
        p.offset(5, -3);
        assertEquals(15, p.x);
        assertEquals(17, p.y);
    }

    @Test
    void testEquals() {
        TPoint p1 = new TPoint(10, 20);
        TPoint p2 = new TPoint(10, 20);
        TPoint p3 = new TPoint(10, 21);

        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(null));
    }
}
