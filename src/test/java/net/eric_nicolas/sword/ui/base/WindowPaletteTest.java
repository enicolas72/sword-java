package net.eric_nicolas.sword.ui.base;

import java.awt.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WindowPalette — colour scheme structure and pre-built instances.
 */
class WindowPaletteTest {

    // ===== STANDARD palette =====

    @Test
    void testStandardBlack() {
        assertEquals(new Color(0, 0, 0), WindowPalette.STANDARD.black);
    }

    @Test
    void testStandardDark() {
        assertEquals(new Color(64, 64, 64), WindowPalette.STANDARD.dark);
    }

    @Test
    void testStandardMedium() {
        assertEquals(new Color(128, 128, 128), WindowPalette.STANDARD.medium);
    }

    @Test
    void testStandardFace() {
        assertEquals(new Color(192, 192, 192), WindowPalette.STANDARD.face);
    }

    @Test
    void testStandardWhite() {
        assertEquals(new Color(255, 255, 255), WindowPalette.STANDARD.white);
    }

    // ===== GREEN palette =====

    @Test
    void testGreenBlackIsBlack() {
        assertEquals(new Color(0, 0, 0), WindowPalette.GREEN.black);
    }

    @Test
    void testGreenFaceTintedGreen() {
        Color face = WindowPalette.GREEN.face;
        assertTrue(face.getGreen() > face.getRed(), "GREEN face should have higher green channel");
        assertTrue(face.getGreen() > face.getBlue(), "GREEN face should have higher green channel");
    }

    @Test
    void testGreenDarkTintedGreen() {
        Color dark = WindowPalette.GREEN.dark;
        assertTrue(dark.getGreen() > dark.getRed(), "GREEN dark should have higher green channel");
    }

    // ===== BLUE palette =====

    @Test
    void testBlueBlackIsBlack() {
        assertEquals(new Color(0, 0, 0), WindowPalette.BLUE.black);
    }

    @Test
    void testBlueFaceTintedBlue() {
        Color face = WindowPalette.BLUE.face;
        assertTrue(face.getBlue() > face.getRed(), "BLUE face should have higher blue channel");
        assertTrue(face.getBlue() > face.getGreen(), "BLUE face should have higher blue channel");
    }

    @Test
    void testBlueDarkTintedBlue() {
        Color dark = WindowPalette.BLUE.dark;
        assertTrue(dark.getBlue() > dark.getRed(), "BLUE dark should have higher blue channel");
    }

    // ===== Custom palette =====

    @Test
    void testCustomPaletteStoresAllFields() {
        Color black  = new Color(1,  2,  3);
        Color dark   = new Color(4,  5,  6);
        Color medium = new Color(7,  8,  9);
        Color face   = new Color(10, 11, 12);
        Color white  = new Color(13, 14, 15);

        WindowPalette p = new WindowPalette(black, dark, medium, face, white);
        assertSame(black,  p.black);
        assertSame(dark,   p.dark);
        assertSame(medium, p.medium);
        assertSame(face,   p.face);
        assertSame(white,  p.white);
    }

    @Test
    void testThreeInstancesAreDifferent() {
        assertNotSame(WindowPalette.STANDARD, WindowPalette.GREEN);
        assertNotSame(WindowPalette.STANDARD, WindowPalette.BLUE);
        assertNotSame(WindowPalette.GREEN,    WindowPalette.BLUE);
    }
}
