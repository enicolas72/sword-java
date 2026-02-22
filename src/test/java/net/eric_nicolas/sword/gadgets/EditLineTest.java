package net.eric_nicolas.sword.gadgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TEditLine - text editing control.
 */
class TEditLineTest {

    private TEditLine edit;

    @BeforeEach
    void setUp() {
        edit = new TEditLine(10, 10, 200, 60, 0);
    }

    @Test
    void testInitialState() {
        assertEquals("", edit.getText());
    }

    @Test
    void testSetText() {
        edit.setText("Hello World");
        assertEquals("Hello World", edit.getText());
    }

    @Test
    void testSetTextNull() {
        edit.setText(null);
        assertEquals("", edit.getText());
    }

    @Test
    void testTextWithMaxLength() {
        TEditLine shortEdit = new TEditLine(10, 10, 200, 10, 0);
        shortEdit.setText("This is a very long text");

        // Text should be stored as-is initially
        assertEquals("This is a very long text", shortEdit.getText());
    }

    @Test
    void testEmptyText() {
        edit.setText("Some text");
        edit.setText("");
        assertEquals("", edit.getText());
    }
}
