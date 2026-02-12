package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;

/**
 * TStaticText - Non-interactive text label.
 */
public class TStaticText extends TZone {

    protected String text;
    protected Font textFont;

    /**
     * Default constructor.
     */
    public TStaticText() {
        this(0, 0, 100, 20, "Text");
    }

    /**
     * Constructor with position, size, and text.
     */
    public TStaticText(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text != null ? text : "";
        this.textFont = new Font("SansSerif", Font.PLAIN, 12);
        setBackgroundColor(TColors.FACE_GRAY);
    }

    @Override
    protected void paint(Graphics2D g) {
        if (text == null || text.isEmpty()) return;

        // Draw text
        g.setColor(TColors.BLACK);
        g.setFont(textFont);
        FontMetrics fm = g.getFontMetrics();

        // Center vertically
        int y = bounds.a.y + (bounds.height() + fm.getHeight()) / 2 - fm.getDescent();

        g.drawString(text, bounds.a.x + 2, y);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
