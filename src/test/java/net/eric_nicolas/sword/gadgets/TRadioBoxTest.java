package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.mechanism.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TRadioBox - radio button selection.
 */
class TRadioBoxTest {

    private GroupBox group;
    private RadioBox radioA;
    private RadioBox radioB;
    private RadioBox radioC;

    @BeforeEach
    void setUp() {
        group = new GroupBox(10, 10, 200, 100);
        radioA = new RadioBox(10, 10, 100, AbstractButton.BO_NO_CASE, 1, "Radio A");
        radioB = new RadioBox(10, 30, 100, AbstractButton.BO_NO_CASE, 2, "Radio B");
        radioC = new RadioBox(10, 50, 100, AbstractButton.BO_NO_CASE, 3, "Radio C");

        radioA.insertIn(group);
        radioB.insertIn(group);
        radioC.insertIn(group);
    }

    @Test
    void testInitialState() {
        assertEquals(0, group.value);
        assertFalse(radioA.isRadioSelected());
        assertFalse(radioB.isRadioSelected());
        assertFalse(radioC.isRadioSelected());
    }

    @Test
    void testSelectRadio() {
        group.value = 2;

        assertFalse(radioA.isRadioSelected());
        assertTrue(radioB.isRadioSelected());
        assertFalse(radioC.isRadioSelected());
    }

    @Test
    void testOnlyOneSelected() {
        group.value = 1;
        assertTrue(radioA.isRadioSelected());

        group.value = 3;
        assertFalse(radioA.isRadioSelected());
        assertTrue(radioC.isRadioSelected());
    }

    @Test
    void testGetValue() {
        assertEquals(1, radioA.getValue());
        assertEquals(2, radioB.getValue());
        assertEquals(3, radioC.getValue());
    }

    @Test
    void testGetText() {
        assertEquals("Radio A", radioA.getText());
        assertEquals("Radio B", radioB.getText());
        assertEquals("Radio C", radioC.getText());
    }

    @Test
    void testDisabledRadio() {
        RadioBox disabled = new RadioBox(10, 70, 100, AbstractButton.BO_DISABLED, 4, "Disabled");
        disabled.insertIn(group);

        assertTrue(disabled.hasStatus(TObject.SF_DISABLED));
    }
}
