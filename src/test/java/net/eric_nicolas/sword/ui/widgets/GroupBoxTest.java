package net.eric_nicolas.sword.ui.widgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GroupBox — title text and value field.
 */
class GroupBoxTest {

    private GroupBox group;

    @BeforeEach
    void setUp() {
        group = new GroupBox(0, 0, 150, 100, "Options");
    }

    // ===== Text =====

    @Test
    void testInitialText() {
        assertEquals("Options", group.getText());
    }

    @Test
    void testSetText() {
        group.setText("Settings");
        assertEquals("Settings", group.getText());
    }

    @Test
    void testSetTextToNull() {
        group.setText(null);
        assertNull(group.getText());
    }

    // ===== Value =====

    @Test
    void testInitialValue() {
        assertEquals(0, group.value);
    }

    @Test
    void testSetValue() {
        group.value = 3;
        assertEquals(3, group.value);
    }

    // ===== Alternative constructors =====

    @Test
    void testNullTitleConstructor() {
        GroupBox g = new GroupBox(0, 0, 100, 100, null);
        assertNull(g.getText());
    }

    @Test
    void testNoTitleConstructor() {
        GroupBox g = new GroupBox(0, 0, 100, 100);
        assertNull(g.getText());
        assertEquals(0, g.value);
    }

    @Test
    void testDefaultConstructor() {
        GroupBox g = new GroupBox();
        assertNull(g.getText());
        assertEquals(0, g.value);
    }

    // ===== Bounds =====

    @Test
    void testBoundsSetCorrectly() {
        assertEquals(150, group.getBounds().width());
        assertEquals(100, group.getBounds().height());
    }
}
