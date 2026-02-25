package net.eric_nicolas.sword.ui.base;

import java.awt.Color;

/**
 * TColors - Colour palette and management.
 * All colours are defined by explicit RGB values; no standard Java Color
 * constants (Color.BLACK, Color.WHITE, etc.) are used.
 */
public class TColors {

    // Standard colours
    public static final Color BLACK      = new Color(  0,   0,   0);
    public static final Color WHITE      = new Color(255, 255, 255);
    public static final Color RED        = new Color(255,   0,   0);
    public static final Color GREEN      = new Color(  0, 255,   0);
    public static final Color BLUE       = new Color(  0,   0, 255);
    public static final Color YELLOW     = new Color(255, 255,   0);
    public static final Color CYAN       = new Color(  0, 255, 255);
    public static final Color MAGENTA    = new Color(255,   0, 255);
    public static final Color GRAY       = new Color(128, 128, 128);
    public static final Color LIGHT_GRAY = new Color(192, 192, 192);
    public static final Color DARK_GRAY  = new Color( 64,  64,  64);

    // UI colours
    public static final Color DESKTOP_BG   = new Color(  0, 128, 128);
    public static final Color WINDOW_BG    = new Color(192, 192, 192);
    public static final Color WINDOW_FRAME = new Color( 64,  64,  64);
    public static final Color WINDOW_TEXT  = new Color(  0,   0,   0);
    public static final Color FACE_GRAY    = new Color(192, 192, 192);
    public static final Color MEDIUM_GRAY  = new Color(128, 128, 128);

    private TColors() {}
}
