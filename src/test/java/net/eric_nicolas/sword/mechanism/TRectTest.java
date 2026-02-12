package net.eric_nicolas.sword.mechanism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TRect - rectangle geometry.
 */
class TRectTest {

    @Test
    void testDefaultConstructor() {
        TRect r = new TRect();
        assertEquals(0, r.a.x);
        assertEquals(0, r.a.y);
        assertEquals(0, r.b.x);
        assertEquals(0, r.b.y);
    }

    @Test
    void testParameterizedConstructor() {
        TRect r = new TRect(10, 20, 30, 40);
        assertEquals(10, r.a.x);
        assertEquals(20, r.a.y);
        assertEquals(30, r.b.x);
        assertEquals(40, r.b.y);
    }

    @Test
    void testCopyConstructor() {
        TRect r1 = new TRect(5, 10, 15, 20);
        TRect r2 = new TRect(r1);
        assertEquals(5, r2.a.x);
        assertEquals(10, r2.a.y);
        assertEquals(15, r2.b.x);
        assertEquals(20, r2.b.y);
    }

    @Test
    void testWidth() {
        TRect r = new TRect(10, 20, 50, 80);
        assertEquals(40, r.width());
    }

    @Test
    void testHeight() {
        TRect r = new TRect(10, 20, 50, 80);
        assertEquals(60, r.height());
    }

    @Test
    void testContainsPoint() {
        TRect r = new TRect(10, 20, 50, 80);

        assertTrue(r.contains(10, 20));  // Top-left corner
        assertTrue(r.contains(30, 50));  // Inside
        assertTrue(r.contains(49, 79));  // Just inside
        assertFalse(r.contains(50, 80)); // Bottom-right corner (exclusive)
        assertFalse(r.contains(5, 30));  // Left of rect
        assertFalse(r.contains(60, 30)); // Right of rect
    }

    @Test
    void testIsEmpty() {
        TRect r1 = new TRect(10, 20, 10, 20); // Zero width and height
        TRect r2 = new TRect(10, 20, 50, 80); // Non-empty
        TRect r3 = new TRect(50, 80, 10, 20); // Inverted (b < a)

        assertTrue(r1.isEmpty());
        assertFalse(r2.isEmpty());
        assertTrue(r3.isEmpty());
    }

    @Test
    void testOffset() {
        TRect r = new TRect(10, 20, 30, 40);
        r.offset(5, -3);

        assertEquals(15, r.a.x);
        assertEquals(17, r.a.y);
        assertEquals(35, r.b.x);
        assertEquals(37, r.b.y);
    }

    @Test
    void testIntersect() {
        TRect r1 = new TRect(10, 10, 50, 50);
        TRect r2 = new TRect(30, 30, 70, 70);

        r1.intersect(r2);

        assertEquals(30, r1.a.x);
        assertEquals(30, r1.a.y);
        assertEquals(50, r1.b.x);
        assertEquals(50, r1.b.y);
    }

    @Test
    void testIntersectNoOverlap() {
        TRect r1 = new TRect(10, 10, 30, 30);
        TRect r2 = new TRect(40, 40, 60, 60);

        r1.intersect(r2);

        assertTrue(r1.isEmpty());
    }

    @Test
    void testUnion() {
        TRect r1 = new TRect(10, 10, 30, 30);
        TRect r2 = new TRect(20, 20, 50, 50);

        r1.union(r2);

        assertEquals(10, r1.a.x);
        assertEquals(10, r1.a.y);
        assertEquals(50, r1.b.x);
        assertEquals(50, r1.b.y);
    }

    @Test
    void testEquals() {
        TRect r1 = new TRect(10, 20, 30, 40);
        TRect r2 = new TRect(10, 20, 30, 40);
        TRect r3 = new TRect(10, 20, 30, 41);

        assertTrue(r1.equals(r2));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(null));
    }
}
