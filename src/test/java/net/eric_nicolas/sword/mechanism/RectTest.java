package net.eric_nicolas.sword.mechanism;

import net.eric_nicolas.sword.ui.Rect;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TRect - rectangle geometry.
 */
class RectTest {

    @Test
    void testDefaultConstructor() {
        Rect r = new Rect();
        assertEquals(0, r.a.x);
        assertEquals(0, r.a.y);
        assertEquals(0, r.b.x);
        assertEquals(0, r.b.y);
    }

    @Test
    void testParameterizedConstructor() {
        Rect r = new Rect(10, 20, 30, 40);
        assertEquals(10, r.a.x);
        assertEquals(20, r.a.y);
        assertEquals(30, r.b.x);
        assertEquals(40, r.b.y);
    }

    @Test
    void testCopyConstructor() {
        Rect r1 = new Rect(5, 10, 15, 20);
        Rect r2 = new Rect(r1);
        assertEquals(5, r2.a.x);
        assertEquals(10, r2.a.y);
        assertEquals(15, r2.b.x);
        assertEquals(20, r2.b.y);
    }

    @Test
    void testWidth() {
        Rect r = new Rect(10, 20, 50, 80);
        assertEquals(40, r.width());
    }

    @Test
    void testHeight() {
        Rect r = new Rect(10, 20, 50, 80);
        assertEquals(60, r.height());
    }

    @Test
    void testContainsPoint() {
        Rect r = new Rect(10, 20, 50, 80);

        assertTrue(r.contains(10, 20));  // Top-left corner
        assertTrue(r.contains(30, 50));  // Inside
        assertTrue(r.contains(49, 79));  // Just inside
        assertFalse(r.contains(50, 80)); // Bottom-right corner (exclusive)
        assertFalse(r.contains(5, 30));  // Left of rect
        assertFalse(r.contains(60, 30)); // Right of rect
    }

    @Test
    void testIsEmpty() {
        Rect r1 = new Rect(10, 20, 10, 20); // Zero width and height
        Rect r2 = new Rect(10, 20, 50, 80); // Non-empty
        Rect r3 = new Rect(50, 80, 10, 20); // Inverted (b < a)

        assertTrue(r1.isEmpty());
        assertFalse(r2.isEmpty());
        assertTrue(r3.isEmpty());
    }

    @Test
    void testOffset() {
        Rect r = new Rect(10, 20, 30, 40);
        r.offset(5, -3);

        assertEquals(15, r.a.x);
        assertEquals(17, r.a.y);
        assertEquals(35, r.b.x);
        assertEquals(37, r.b.y);
    }

    @Test
    void testIntersect() {
        Rect r1 = new Rect(10, 10, 50, 50);
        Rect r2 = new Rect(30, 30, 70, 70);

        r1.intersect(r2);

        assertEquals(30, r1.a.x);
        assertEquals(30, r1.a.y);
        assertEquals(50, r1.b.x);
        assertEquals(50, r1.b.y);
    }

    @Test
    void testIntersectNoOverlap() {
        Rect r1 = new Rect(10, 10, 30, 30);
        Rect r2 = new Rect(40, 40, 60, 60);

        r1.intersect(r2);

        assertTrue(r1.isEmpty());
    }

    @Test
    void testUnion() {
        Rect r1 = new Rect(10, 10, 30, 30);
        Rect r2 = new Rect(20, 20, 50, 50);

        r1.union(r2);

        assertEquals(10, r1.a.x);
        assertEquals(10, r1.a.y);
        assertEquals(50, r1.b.x);
        assertEquals(50, r1.b.y);
    }

    @Test
    void testEquals() {
        Rect r1 = new Rect(10, 20, 30, 40);
        Rect r2 = new Rect(10, 20, 30, 40);
        Rect r3 = new Rect(10, 20, 30, 41);

        assertTrue(r1.equals(r2));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(null));
    }
}
