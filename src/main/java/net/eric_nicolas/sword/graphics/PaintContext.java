package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.ui.Point;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

/**
 * PaintContext - Abstraction layer for painting operations.
 * Wraps AWT Graphics2D with a cleaner API where coordinates come first.
 * All drawing coordinates are in local (zone-relative) space; the origin
 * field holds the absolute screen position that is silently added to every
 * coordinate before forwarding to Graphics2D.
 */
public class PaintContext {

    public static PaintContext ofAWT(Graphics2D g) {
        return new PaintContext(g, new Point(0, 0));
    }

    /**
     * Return a new PaintContext with the given absolute origin.
     * The underlying Graphics2D is shared; only the translation changes.
     */
    public PaintContext withOrigin(Point origin) {
        return new PaintContext(g, origin);
    }

    /**
     * Get the underlying Graphics2D object (for advanced operations).
     */
    public Graphics2D getGraphics2D() {
        return g;
    }

    // ===== Color operations =====

    public void setColor(Color color) {
        g.setColor(color);
    }

    public Color getColor() {
        return g.getColor();
    }

    // ===== Font operations =====

    public void setFont(Font font) {
        g.setFont(font);
    }

    public Font getFont() {
        return g.getFont();
    }

    public FontMetrics getFontMetrics() {
        return g.getFontMetrics();
    }

    public FontMetrics getFontMetrics(Font font) {
        return g.getFontMetrics(font);
    }

    // ===== Clipping operations =====

    public void setClip(int x, int y, int width, int height) {
        g.setClip(x + origin.x(), y + origin.y(), width, height);
    }

    public void setClip(Point p, int width, int height) {
        setClip(p.x(), p.y(), width, height);
    }

    // ===== Drawing operations (coordinates first) =====

    /**
     * Draw a string at the specified position.
     * @param x X coordinate (local)
     * @param y Y coordinate (baseline, local)
     * @param text Text to draw
     */
    public void drawString(int x, int y, String text) {
        g.drawString(text, x + origin.x(), y + origin.y());
    }

    /**
     * Draw a string at the specified position.
     * @param p X,Y coordinates (local)
     * @param text Text to draw
     */
    public void drawString(Point p, String text) {
        drawString(p.x(), p.y(), text);
    }

    /**
     * Draw a character at the specified position.
     * @param x X coordinate (local)
     * @param y Y coordinate (baseline, local)
     * @param ch Character to draw
     */
    public void drawChar(int x, int y, char ch) {
        g.drawString(String.valueOf(ch), x + origin.x(), y + origin.y());
    }

    /**
     * Draw a rectangle outline.
     * @param x X coordinate (local)
     * @param y Y coordinate (local)
     * @param width Width
     * @param height Height
     */
    public void drawRect(int x, int y, int width, int height) {
        g.drawRect(x + origin.x(), y + origin.y(), width, height);
    }

    /**
     * Draw a rectangle outline.
     * @param p X,Y coordinate (local)
     * @param width Width
     * @param height Height
     */
    public void drawRect(Point p, int width, int height) {
        drawRect(p.x(), p.y(), width, height);
    }

    /**
     * Fill a rectangle.
     * @param x X coordinate (local)
     * @param y Y coordinate (local)
     * @param width Width
     * @param height Height
     */
    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x + origin.x(), y + origin.y(), width, height);
    }

    /**
     * Fill a rectangle.
     * @param p the top left corner coordinates (local)
     * @param width Width
     * @param height Height
     */
    public void fillRect(Point p, int width, int height) {
        fillRect(p.x(), p.y(), width, height);
    }

    /**
     * Draw a line.
     * @param x1 Start X coordinate (local)
     * @param y1 Start Y coordinate (local)
     * @param x2 End X coordinate (local)
     * @param y2 End Y coordinate (local)
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        g.drawLine(x1 + origin.x(), y1 + origin.y(), x2 + origin.x(), y2 + origin.y());
    }

    /**
     * Draw a line.
     * @param p1 Start X,Y coordinate (local)
     * @param p2 End X,Y coordinate (local)
     */
    public void drawLine(Point p1, Point p2) {
        drawLine(p1.x(), p1.y(), p2.x(), p2.y());
    }

    /**
     * Draw an oval outline.
     * @param x X coordinate (local)
     * @param y Y coordinate (local)
     * @param width Width
     * @param height Height
     */
    public void drawOval(int x, int y, int width, int height) {
        g.drawOval(x + origin.x(), y + origin.y(), width, height);
    }

    /**
     * Fill an oval.
     * @param x X coordinate (local)
     * @param y Y coordinate (local)
     * @param width Width
     * @param height Height
     */
    public void fillOval(int x, int y, int width, int height) {
        g.fillOval(x + origin.x(), y + origin.y(), width, height);
    }

    /**
     * Draw a rounded rectangle outline.
     * @param x X coordinate (local)
     * @param y Y coordinate (local)
     * @param width Width
     * @param height Height
     * @param arcWidth Arc width
     * @param arcHeight Arc height
     */
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.drawRoundRect(x + origin.x(), y + origin.y(), width, height, arcWidth, arcHeight);
    }

    /**
     * Fill a rounded rectangle.
     * @param x X coordinate (local)
     * @param y Y coordinate (local)
     * @param width Width
     * @param height Height
     * @param arcWidth Arc width
     * @param arcHeight Arc height
     */
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x + origin.x(), y + origin.y(), width, height, arcWidth, arcHeight);
    }

    /**
     * Draw a polygon outline.
     * @param xPoints Array of X coordinates (local)
     * @param yPoints Array of Y coordinates (local)
     * @param nPoints Number of points
     */
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.drawPolygon(translate(xPoints, nPoints, origin.x()), translate(yPoints, nPoints, origin.y()), nPoints);
    }

    /**
     * Fill a polygon.
     * @param xPoints Array of X coordinates (local)
     * @param yPoints Array of Y coordinates (local)
     * @param nPoints Number of points
     */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.fillPolygon(translate(xPoints, nPoints, origin.x()), translate(yPoints, nPoints, origin.y()), nPoints);
    }

    // ===== Rendering hints =====

    public void setAntialiasing(boolean enabled) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            enabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void setTextAntialiasing(boolean enabled) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            enabled ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    //

    private static int[] translate(int[] points, int n, int offset) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = points[i] + offset;
        return result;
    }

    private PaintContext(Graphics2D g, Point origin) {
        this.g = g;
        this.origin = origin;
    }

    private final Graphics2D g;
    private final Point origin;
}
