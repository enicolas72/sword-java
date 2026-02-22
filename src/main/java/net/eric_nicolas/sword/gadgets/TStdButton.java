package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;

import java.awt.Font;
import java.awt.FontMetrics;

/**
 * TStdButton - Standard button with text label.
 */
public class TStdButton extends TButton {

    protected String text;
    protected Font buttonFont;

    /**
     * Default constructor.
     */
    public TStdButton() {
        this(0, 0, 80, 25, 0, 0, 0, "Button");
    }

    /**
     * Constructor with position, size, command, options, and text.
     */
    public TStdButton(int x, int y, int width, int height, long command, int scanCode, int options, String text) {
        super(x, y, width, height, command, scanCode, options);
        this.text = text != null ? text : "Button";
        this.buttonFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        if (text == null || text.isEmpty()) return;

        // Set text color based on state
        if (hasStatus(SF_DISABLED)) {
            ctx.setColor(TColors.DARK_GRAY);
        } else {
            ctx.setColor(TColors.BLACK);
        }

        // Draw text centered
        ctx.setFont(buttonFont);
        FontMetrics fm = ctx.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int dx = (bounds.width() - textWidth) / 2 + offset;
        int dy = (bounds.height() + textHeight) / 2 - fm.getDescent() + offset;
        ctx.drawString(bounds.a().plus(dx, dy), text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

