package net.eric_nicolas.sword.mechanism;

import net.eric_nicolas.sword.ui.Rect;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TRect - rectangle geometry.
 */
class RectTest {

    @Test
    void testParameterizedConstructor() {
        Rect r = new Rect(10, 20, 30, 40);
        assertEquals(10, r.origin().x());
        assertEquals(20, r.origin().y());
        assertEquals(30, r.width());
        assertEquals(40, r.height());
    }

    @Test
    void testCopyConstructor() {
        Rect r1 = new Rect(5, 10, 15, 20);
        Rect r2 = new Rect(r1);
        assertEquals(5, r2.origin().x());
        assertEquals(10, r2.origin().y());
        assertEquals(15, r2.width());
        assertEquals(20, r2.height());
    }

    @Test
    void testWidth() {
        Rect r = new Rect(10, 20, 40, 60);
        assertEquals(40, r.width());
    }

    @Test
    void testHeight() {
        Rect r = new Rect(10, 20, 40, 60);
        assertEquals(60, r.height());
    }

    @Test
    void testContainsPoint() {
        // origin=(10,20), size=40x60 → bottom-right exclusive at (50,80)
        Rect r = new Rect(10, 20, 40, 60);

        assertTrue(r.contains(10, 20));  // Top-left corner
        assertTrue(r.contains(30, 50));  // Inside
        assertTrue(r.contains(49, 79));  // Just inside
        assertFalse(r.contains(50, 80)); // Bottom-right corner (exclusive)
        assertFalse(r.contains(5, 30));  // Left of rect
        assertFalse(r.contains(60, 30)); // Right of rect
    }

    @Test
    void testIsEmpty() {
        Rect r1 = new Rect(10, 20, 0, 0);  // Zero width and height
        Rect r2 = new Rect(10, 20, 40, 60); // Non-empty
        Rect r3 = new Rect(10, 20, -1, -1); // Negative size

        assertTrue(r1.isEmpty());
        assertFalse(r2.isEmpty());
        assertTrue(r3.isEmpty());
    }

    @Test
    void testPlus() {
        // origin=(10,20), size=20x20 → b=(30,40)
        Rect r = new Rect(10, 20, 20, 20);
        r = Rect.plus(r, 5, -3);

        assertEquals(15, r.origin().x());
        assertEquals(17, r.origin().y());
        assertEquals(20, r.width());
        assertEquals(20, r.height());
    }

    @Test
    void testIntersect() {
        Rect r1 = new Rect(10, 10, 40, 40); // b=(50,50)
        Rect r2 = new Rect(30, 30, 40, 40); // b=(70,70)

        r1 = Rect.intersect(r1, r2);

        assertEquals(30, r1.origin().x());
        assertEquals(30, r1.origin().y());
        assertEquals(20, r1.width());
        assertEquals(20, r1.height());
    }

    @Test
    void testIntersectNoOverlap() {
        Rect r1 = new Rect(10, 10, 20, 20); // b=(30,30)
        Rect r2 = new Rect(40, 40, 20, 20); // b=(60,60)

        r1 = Rect.intersect(r1, r2);

        assertTrue(r1.isEmpty());
    }

    @Test
    void testUnion() {
        Rect r1 = new Rect(10, 10, 20, 20); // b=(30,30)
        Rect r2 = new Rect(20, 20, 30, 30); // b=(50,50)

        r1 = Rect.union(r1, r2);

        assertEquals(10, r1.origin().x());
        assertEquals(10, r1.origin().y());
        assertEquals(40, r1.width());
        assertEquals(40, r1.height());
    }

    @Test
    void testEquals() {
        Rect r1 = new Rect(10, 20, 20, 20);
        Rect r2 = new Rect(10, 20, 20, 20);
        Rect r3 = new Rect(10, 20, 20, 21);

        assertTrue(r1.equals(r2));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(null));
    }
}
