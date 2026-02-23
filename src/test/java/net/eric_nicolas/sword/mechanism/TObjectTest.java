package net.eric_nicolas.sword.mechanism;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TObject - options and status management.
 */
class TObjectTest {

    private TObject obj;

    @BeforeEach
    void setUp() {
        obj = new TObject();
    }

    @Test
    void testInitialStatus() {
        assertTrue(obj.hasStatus(TObject.SF_VISIBLE));
        assertFalse(obj.hasStatus(TObject.SF_SELECTED));
    }

    @Test
    void testSetStatus() {
        obj.setStatus(TObject.SF_SELECTED);
        assertTrue(obj.hasStatus(TObject.SF_SELECTED));
        assertTrue(obj.hasStatus(TObject.SF_VISIBLE)); // Should still be visible
    }

    @Test
    void testClearStatus() {
        obj.setStatus(TObject.SF_SELECTED);
        obj.clearStatus(TObject.SF_SELECTED);
        assertFalse(obj.hasStatus(TObject.SF_SELECTED));
    }

    @Test
    void testMultipleStatusFlags() {
        obj.setStatus(TObject.SF_SELECTED);
        obj.setStatus(TObject.SF_FOCUSED);

        assertTrue(obj.hasStatus(TObject.SF_SELECTED));
        assertTrue(obj.hasStatus(TObject.SF_FOCUSED));
        assertTrue(obj.hasStatus(TObject.SF_VISIBLE));
    }

    @Test
    void testIsVisible() {
        assertTrue(obj.isVisible());
        obj.clearStatus(TObject.SF_VISIBLE);
        assertFalse(obj.isVisible());
    }

    @Test
    void testSetVisible() {
        obj.setVisible(false);
        assertFalse(obj.isVisible());
        obj.setVisible(true);
        assertTrue(obj.isVisible());
    }

    @Test
    void testIsSelected() {
        assertFalse(obj.isSelected());
        obj.setSelected(true);
        assertTrue(obj.isSelected());
    }

    @Test
    void testSetSelected() {
        obj.setSelected(true);
        assertTrue(obj.isSelected());
        obj.setSelected(false);
        assertFalse(obj.isSelected());
    }
}
