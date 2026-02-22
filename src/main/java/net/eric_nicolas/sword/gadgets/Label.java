package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;

import java.awt.Font;
import java.awt.FontMetrics;

/**
 * TStaticText - Non-interactive text label.
 */
public class Label extends TZone {

    protected String text;
    protected Font textFont;

    /**
     * Default constructor.
     */
    public Label() {
        this(0, 0, 100, 20, "Text");
    }

    /**
     * Constructor with position, size, and text.
     */
    public Label(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text != null ? text : "";
        this.textFont = new Font("SansSerif", Font.PLAIN, 12);
        setBackgroundColor(TColors.FACE_GRAY);
    }

    @Override
    protected void paint(PaintContext ctx) {
        if (text == null || text.isEmpty()) return;

        // Draw text
        ctx.setColor(TColors.BLACK);
        ctx.setFont(textFont);
        FontMetrics fm = ctx.getFontMetrics();

        // Center vertically
        int dx = 2;
        int dy = (bounds.height() + fm.getHeight()) / 2 - fm.getDescent();
        ctx.drawString(dx, dy, text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
