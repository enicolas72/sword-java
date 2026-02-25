package net.eric_nicolas.sword.ui.widgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Button — text, enabled state, bounds.
 */
class ButtonTest {

    private Button button;

    @BeforeEach
    void setUp() {
        button = new Button(10, 20, 80, 25, 1001, 0, "OK");
    }

    // ===== Text =====

    @Test
    void testInitialText() {
        assertEquals("OK", button.getText());
    }

    @Test
    void testSetText() {
        button.setText("Cancel");
        assertEquals("Cancel", button.getText());
    }

    @Test
    void testNullTextDefaultsToButton() {
        Button b = new Button(0, 0, 80, 25, 0, 0, null);
        assertEquals("Button", b.getText());
    }

    @Test
    void testDefaultConstructorText() {
        Button b = new Button();
        assertEquals("Button", b.getText());
    }

    // ===== Enabled =====

    @Test
    void testInitiallyEnabled() {
        assertTrue(button.isEnabled());
    }

    @Test
    void testDisable() {
        button.setEnabled(false);
        assertFalse(button.isEnabled());
    }

    @Test
    void testReEnable() {
        button.setEnabled(false);
        button.setEnabled(true);
        assertTrue(button.isEnabled());
    }

    // ===== Bounds =====

    @Test
    void testBoundsWidth() {
        assertEquals(80, button.getBounds().width());
    }

    @Test
    void testBoundsHeight() {
        assertEquals(25, button.getBounds().height());
    }

    @Test
    void testBoundsOrigin() {
        assertEquals(10, button.getBounds().origin().x());
        assertEquals(20, button.getBounds().origin().y());
    }
}
