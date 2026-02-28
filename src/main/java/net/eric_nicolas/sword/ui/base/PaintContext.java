package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.TexHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * PaintContext - Abstraction layer for painting operations.
 *
 * Wraps AWT {@link Graphics2D} with a cleaner API where coordinates come first.
 * All drawing coordinates are in local (widget-relative) space; the {@code origin}
 * field holds the absolute screen position that is silently added to every
 * coordinate before forwarding to {@link Graphics2D}.
 *
 * <h3>Text rendering</h3>
 * All text is rendered via {@link TexHelper}, which accepts text-mode TeX input
 * (plain text with optional {@code \math{...}} inline math blocks) and returns
 * a {@link java.awt.image.BufferedImage}.  The {@code fontSize} field (default
 * {@value #DEFAULT_FONT_SIZE}) controls the JLaTeXMath point size; override it
 * per-context with {@link #withFontSize(float)}.  {@link #drawString} draws the
 * rendered image top-left at the given coordinates.  {@link #measureText} returns
 * the rendered image dimensions for layout calculations.
 */
public class PaintContext {

    public static final float DEFAULT_FONT_SIZE = 12f;

    public static PaintContext ofAWT(Graphics2D g) {
        return new PaintContext(g, new Point(0, 0), WindowPalette.STANDARD, DEFAULT_FONT_SIZE);
    }

    /**
     * Return a new PaintContext whose origin is this context's origin PLUS
     * the given delta.  The underlying {@link Graphics2D}, palette, and font
     * size are propagated unchanged.
     */
    public PaintContext withOrigin(Point delta) {
        return new PaintContext(g, Point.plus(this.origin, delta), this.palette, this.fontSize);
    }

    /**
     * Return a new PaintContext with a different palette; origin and font size
     * are unchanged.  Used by {@code Window.draw()} to inject the window's own
     * palette at the root of the rendering tree.
     */
    public PaintContext withPalette(WindowPalette p) {
        return new PaintContext(g, this.origin, p, this.fontSize);
    }

    /**
     * Return a new PaintContext with a different font size; origin and palette
     * are unchanged.  Used by widgets (e.g., {@code Label}) that need a
     * non-default text size.
     */
    public PaintContext withFontSize(float size) {
        return new PaintContext(g, this.origin, this.palette, size);
    }

    /** The palette active for this rendering context. */
    public WindowPalette palette() { return palette; }

    /** The JLaTeXMath point size used by {@link #drawString} and {@link #measureText}. */
    public float fontSize() { return fontSize; }

    // ===== Color operations =====

    public void setColor(Color color) {
        g.setColor(color);
    }

    public Color getColor() {
        return g.getColor();
    }

    // ===== Clipping operations =====

    public void setClip(int x, int y, int width, int height) {
        g.setClip(x + origin.x(), y + origin.y(), width, height);
    }

    public void setClip(Point p, int width, int height) {
        setClip(p.x(), p.y(), width, height);
    }

    // ===== Text operations =====

    /**
     * Render {@code text} (text-mode TeX with optional {@code \math{...}} blocks)
     * and draw the result with its top-left corner at {@code (x, y)}.
     * Uses the current colour (set via {@link #setColor}) and this context's
     * {@link #fontSize()}.
     */
    public void drawString(int x, int y, String text) {
        java.awt.image.BufferedImage img = TexHelper.render(text, g.getColor(), fontSize);
        if (img != null) g.drawImage(img, x + origin.x(), y + origin.y(), null);
    }

    /** @see #drawString(int, int, String) */
    public void drawString(Point p, String text) {
        drawString(p.x(), p.y(), text);
    }

    /**
     * Return the pixel dimensions that {@code text} would occupy when rendered
     * at this context's current font size.  Useful for centering text within a
     * bounding box before calling {@link #drawString}.
     */
    public Dimension measureText(String text) {
        return TexHelper.measure(text, fontSize);
    }

    // ===== Drawing operations (coordinates first) =====

    public void drawRect(int x, int y, int width, int height) {
        g.drawRect(x + origin.x(), y + origin.y(), width, height);
    }

    public void drawRect(Point p, int width, int height) {
        drawRect(p.x(), p.y(), width, height);
    }

    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x + origin.x(), y + origin.y(), width, height);
    }

    public void fillRect(Point p, int width, int height) {
        fillRect(p.x(), p.y(), width, height);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        g.drawLine(x1 + origin.x(), y1 + origin.y(), x2 + origin.x(), y2 + origin.y());
    }

    public void drawLine(Point p1, Point p2) {
        drawLine(p1.x(), p1.y(), p2.x(), p2.y());
    }

    public void drawOval(int x, int y, int width, int height) {
        g.drawOval(x + origin.x(), y + origin.y(), width, height);
    }

    public void fillOval(int x, int y, int width, int height) {
        g.fillOval(x + origin.x(), y + origin.y(), width, height);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.drawRoundRect(x + origin.x(), y + origin.y(), width, height, arcWidth, arcHeight);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x + origin.x(), y + origin.y(), width, height, arcWidth, arcHeight);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.drawPolygon(translate(xPoints, nPoints, origin.x()),
                      translate(yPoints, nPoints, origin.y()), nPoints);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.fillPolygon(translate(xPoints, nPoints, origin.x()),
                      translate(yPoints, nPoints, origin.y()), nPoints);
    }

    public void drawImage(java.awt.Image image, int x, int y) {
        g.drawImage(image, x + origin.x(), y + origin.y(), null);
    }

    // ===== Rendering hints =====

    public void setAntialiasing(boolean enabled) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            enabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    //

    private static int[] translate(int[] points, int n, int offset) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = points[i] + offset;
        return result;
    }

    private PaintContext(Graphics2D g, Point origin, WindowPalette palette, float fontSize) {
        this.g        = g;
        this.origin   = origin;
        this.palette  = palette;
        this.fontSize = fontSize;
    }

    private final Graphics2D    g;
    private final Point         origin;
    private final WindowPalette palette;
    private final float         fontSize;
}
