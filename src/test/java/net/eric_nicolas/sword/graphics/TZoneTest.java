package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TZone - coordinate system and geometry.
 */
class TZoneTest {

    private TZone parent;
    private TZone child;
    private TZone grandchild;

    @BeforeEach
    void setUp() {
        parent = new TZone(100, 100, 200, 150);
        child = new TZone(10, 20, 50, 40);
        grandchild = new TZone(5, 5, 20, 15);
    }

    @Test
    void testInitialBounds() {
        TZone zone = new TZone(10, 20, 100, 80);
        Rect bounds = zone.getBounds();

        assertEquals(10, bounds.a.x);
        assertEquals(20, bounds.a.y);
        assertEquals(110, bounds.b.x);  // x + width
        assertEquals(100, bounds.b.y);  // y + height
    }

    @Test
    void testAbsolutePositionWithNoParent() {
        Point abs = child.getAbsolutePosition();
        assertEquals(10, abs.x);
        assertEquals(20, abs.y);
    }

    @Test
    void testAbsolutePositionWithOneParent() {
        child.insertIn(parent);
        Point abs = child.getAbsolutePosition();

        // Child at (10, 20) + parent at (100, 100) = (110, 120)
        assertEquals(110, abs.x);
        assertEquals(120, abs.y);
    }

    @Test
    void testAbsolutePositionWithGrandparent() {
        child.insertIn(parent);
        grandchild.insertIn(child);
        Point abs = grandchild.getAbsolutePosition();

        // Grandchild at (5, 5) + child at (10, 20) + parent at (100, 100) = (115, 125)
        assertEquals(115, abs.x);
        assertEquals(125, abs.y);
    }

    @Test
    void testContains() {
        child.insertIn(parent);

        // Child is at relative (10, 20), absolute (110, 120)
        // Size is 50x40, so absolute bounds are (110, 120) to (160, 160)
        assertTrue(child.contains(110, 120));  // Top-left
        assertTrue(child.contains(135, 140));  // Center
        assertTrue(child.contains(159, 159));  // Just inside
        assertFalse(child.contains(160, 160)); // Bottom-right (exclusive)
        assertFalse(child.contains(100, 120)); // Left of zone
    }

    @Test
    void testIsVisible() {
        assertTrue(parent.isVisible());
        parent.setVisible(false);
        assertFalse(parent.isVisible());
    }

    @Test
    void testSetBackgroundColor() {
        parent.setBackgroundColor(TColors.RED);
        // Just verify no exception thrown
        assertDoesNotThrow(() -> parent.setBackgroundColor(TColors.BLUE));
    }

    @Test
    void testBoundsWidth() {
        assertEquals(200, parent.getBounds().width());
    }

    @Test
    void testBoundsHeight() {
        assertEquals(150, parent.getBounds().height());
    }

    @Test
    void testSetBounds() {
        Rect newBounds = new Rect(50, 60, 150, 160);
        parent.setBounds(newBounds);

        Rect bounds = parent.getBounds();
        assertEquals(50, bounds.a.x);
        assertEquals(60, bounds.a.y);
        assertEquals(150, bounds.b.x);
        assertEquals(160, bounds.b.y);
    }
}
