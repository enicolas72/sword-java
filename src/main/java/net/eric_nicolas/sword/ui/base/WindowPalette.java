package net.eric_nicolas.sword.ui.base;

import java.awt.Color;

/**
 * WindowPalette — A set of five coordinated colours used to draw windows and
 * all the widgets they contain.  All colours are defined by explicit RGB
 * values; no standard Java Color constants are used.
 *
 * Three pre-built palettes:
 *   STANDARD — neutral grays (default for all regular windows)
 *   GREEN    — slightly green-tinted grays (used by Menu / MenuChoice)
 *   BLUE     — slightly blue-tinted grays  (used by Dialog windows)
 *
 * The palette is carried by PaintContext and propagated automatically via
 * withOrigin(), so every widget in a window's hierarchy paints with the same
 * palette without any extra wiring.
 */
public final class WindowPalette {

    /** Deepest colour: text, corner ticks, arrow fills, check marks. */
    public final Color black;

    /** Dark accent: borders, shadows, pressed button backgrounds, separator tops. */
    public final Color dark;

    /** Mid-tone fill: sidebar, scrollbar track groove, disabled text. */
    public final Color medium;

    /** Main surface colour: button faces, window chrome, normal menu items. */
    public final Color face;

    /** Lightest colour: 3-D bevel highlights, selected-item text, edit-area background. */
    public final Color white;

    public WindowPalette(Color black, Color dark, Color medium, Color face, Color white) {
        this.black  = black;
        this.dark   = dark;
        this.medium = medium;
        this.face   = face;
        this.white  = white;
    }

    /** Neutral grey — default for all regular windows. */
    public static final WindowPalette STANDARD = new WindowPalette(
        new Color(  0,   0,   0),   // black
        new Color( 64,  64,  64),   // dark
        new Color(128, 128, 128),   // medium
        new Color(192, 192, 192),   // face
        new Color(255, 255, 255)    // white
    );

    /** Slightly green tint — used by Menu and MenuChoice windows. */
    public static final WindowPalette GREEN = new WindowPalette(
        new Color(  0,   0,   0),   // black
        new Color( 52,  76,  52),   // dark   (green-tinted dark grey)
        new Color(112, 140, 112),   // medium (green-tinted mid grey)
        new Color(180, 204, 180),   // face   (green-tinted light grey)
        new Color(255, 255, 255)    // white
    );

    /** Slightly blue tint — used by Dialog windows. */
    public static final WindowPalette BLUE = new WindowPalette(
        new Color(  0,   0,   0),   // black
        new Color( 52,  52,  76),   // dark   (blue-tinted dark grey)
        new Color(112, 112, 140),   // medium (blue-tinted mid grey)
        new Color(180, 180, 204),   // face   (blue-tinted light grey)
        new Color(255, 255, 255)    // white
    );
}
