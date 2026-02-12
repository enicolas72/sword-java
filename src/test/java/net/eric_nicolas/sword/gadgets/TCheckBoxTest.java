package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.mechanism.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TCheckBox - checkbox state management.
 */
class TCheckBoxTest {

    private TGroupBox group;
    private TCheckBox checkA;
    private TCheckBox checkB;

    @BeforeEach
    void setUp() {
        group = new TGroupBox(10, 10, 200, 100);
        checkA = new TCheckBox(10, 10, 100, TButton.BO_NO_CASE, 1, "Check A");
        checkB = new TCheckBox(10, 30, 100, TButton.BO_NO_CASE, 2, "Check B");

        checkA.insertIn(group);
        checkB.insertIn(group);
    }

    @Test
    void testInitialState() {
        assertFalse(checkA.isChecked());
        assertFalse(checkB.isChecked());
        assertEquals(0, group.value);
    }

    @Test
    void testSetChecked() {
        checkA.setChecked(true);
        assertTrue(checkA.isChecked());
        assertEquals(1, group.value);
    }

    @Test
    void testMultipleChecksWithBitmask() {
        checkA.setChecked(true);
        checkB.setChecked(true);

        assertTrue(checkA.isChecked());
        assertTrue(checkB.isChecked());
        assertEquals(3, group.value); // 1 | 2 = 3
    }

    @Test
    void testUncheck() {
        checkA.setChecked(true);
        checkB.setChecked(true);
        checkA.setChecked(false);

        assertFalse(checkA.isChecked());
        assertTrue(checkB.isChecked());
        assertEquals(2, group.value);
    }

    @Test
    void testDisabled() {
        TCheckBox disabled = new TCheckBox(10, 50, 100, TButton.BO_DISABLED, 4, "Disabled");
        disabled.insertIn(group);

        assertTrue(disabled.hasStatus(TObject.SF_DISABLED));
    }

    @Test
    void testGetText() {
        assertEquals("Check A", checkA.getText());
        assertEquals("Check B", checkB.getText());
    }
}
