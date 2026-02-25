package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.WindowPalette;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Menu — palette, window options, choices.
 */
class MenuTest {

    private Menu dropDown;
    private Menu mainMenu;

    @BeforeEach
    void setUp() {
        dropDown = new Menu("File", false);
        mainMenu = new Menu("Main", true);
    }

    // ===== Palette =====

    @Test
    void testDropDownPaletteIsGreen() {
        assertSame(WindowPalette.GREEN, dropDown.getPalette());
    }

    @Test
    void testMainMenuPaletteIsGreen() {
        assertSame(WindowPalette.GREEN, mainMenu.getPalette());
    }

    // ===== Window options =====

    @Test
    void testDropDownNotClosable() {
        assertFalse(dropDown.isClosable());
    }

    @Test
    void testDropDownNotResizable() {
        assertFalse(dropDown.isResizable());
    }

    @Test
    void testMainMenuNotClosable() {
        assertFalse(mainMenu.isClosable());
    }

    @Test
    void testMainMenuNotResizable() {
        assertFalse(mainMenu.isResizable());
    }

    // ===== Choices =====

    @Test
    void testInitiallyNoChoices() {
        assertTrue(dropDown.getChoices().isEmpty());
    }

    @Test
    void testAddOneChoice() {
        dropDown.getCanvas().add(new MenuChoice("&Open", 0, 1001));
        assertEquals(1, dropDown.getChoices().size());
    }

    @Test
    void testSeparatorCountedAsChoice() {
        dropDown.getCanvas().add(new MenuChoice("&Open", 0, 1001));
        dropDown.getCanvas().add(new MenuChoice());         // separator
        dropDown.getCanvas().add(new MenuChoice("&Quit", 0, 9999));
        assertEquals(3, dropDown.getChoices().size());
    }
}
