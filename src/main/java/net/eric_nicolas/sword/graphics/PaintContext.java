package net.eric_nicolas.sword.graphics;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

/**
 * PaintContext - Abstraction layer for painting operations.
 * Wraps AWT Graphics2D with a cleaner API where coordinates come first.
 */
public class PaintContext {

    private final Graphics2D g;

    public PaintContext(Graphics2D g) {
        this.g = g;
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
        g.setClip(x, y, width, height);
    }

    // ===== Drawing operations (coordinates first) =====

    /**
     * Draw a string at the specified position.
     * @param x X coordinate
     * @param y Y coordinate (baseline)
     * @param text Text to draw
     */
    public void drawString(int x, int y, String text) {
        g.drawString(text, x, y);
    }

    /**
     * Draw a character at the specified position.
     * @param x X coordinate
     * @param y Y coordinate (baseline)
     * @param ch Character to draw
     */
    public void drawChar(int x, int y, char ch) {
        g.drawString(String.valueOf(ch), x, y);
    }

    /**
     * Draw a rectangle outline.
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     */
    public void drawRect(int x, int y, int width, int height) {
        g.drawRect(x, y, width, height);
    }

    /**
     * Fill a rectangle.
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     */
    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x, y, width, height);
    }

    /**
     * Draw a line.
     * @param x1 Start X coordinate
     * @param y1 Start Y coordinate
     * @param x2 End X coordinate
     * @param y2 End Y coordinate
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }

    /**
     * Draw an oval outline.
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     */
    public void drawOval(int x, int y, int width, int height) {
        g.drawOval(x, y, width, height);
    }

    /**
     * Fill an oval.
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     */
    public void fillOval(int x, int y, int width, int height) {
        g.fillOval(x, y, width, height);
    }

    /**
     * Draw a rounded rectangle outline.
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     * @param arcWidth Arc width
     * @param arcHeight Arc height
     */
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /**
     * Fill a rounded rectangle.
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     * @param arcWidth Arc width
     * @param arcHeight Arc height
     */
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /**
     * Draw a polygon outline.
     * @param xPoints Array of X coordinates
     * @param yPoints Array of Y coordinates
     * @param nPoints Number of points
     */
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.drawPolygon(xPoints, yPoints, nPoints);
    }

    /**
     * Fill a polygon.
     * @param xPoints Array of X coordinates
     * @param yPoints Array of Y coordinates
     * @param nPoints Number of points
     */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.fillPolygon(xPoints, yPoints, nPoints);
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
}
