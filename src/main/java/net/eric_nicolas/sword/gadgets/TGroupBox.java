package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Font;

/**
 * TGroupBox - Container for grouping related controls (checkboxes, radio buttons).
 * Handles data exchange for child ItemBox controls.
 */
public class TGroupBox extends TZone {

    protected String text;
    protected Font groupFont;
    public int value; // For radio button groups

    /**
     * Default constructor.
     */
    public TGroupBox() {
        this(0, 0, 150, 100, null);
    }

    /**
     * Constructor with position, size, and optional title.
     */
    public TGroupBox(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text;
        this.value = 0;
        this.groupFont = new Font("SansSerif", Font.PLAIN, 12);
        setBackgroundColor(TColors.FACE_GRAY);
    }

    /**
     * Constructor without title.
     */
    public TGroupBox(int x, int y, int width, int height) {
        this(x, y, width, height, null);
    }

    @Override
    protected void paint(PaintContext ctx) {
        int x = bounds.a.x;
        int y = bounds.a.y;
        int w = bounds.width();
        int h = bounds.height();

        if (text != null && !text.isEmpty()) {
            // Draw titled group box with frame
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawRect(x, y + 8, w - 1, h - 9);

            // Draw title background
            ctx.setColor(TColors.FACE_GRAY);
            ctx.setFont(groupFont);
            int textWidth = ctx.getFontMetrics().stringWidth(text);
            ctx.fillRect(x + 10, y, textWidth + 6, 16);

            // Draw title text
            ctx.setColor(TColors.BLACK);
            ctx.drawString(x + 13, y + 12, text);
        } else {
            // Draw simple frame
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawRect(x, y, w - 1, h - 1);
        }
    }

    @Override
    public void setData(Object data) {
        // For checkboxes: read bits from integer at data pointer
        // For radio buttons: read value from integer at data pointer
        TAtom child = son();
        while (child != null) {
            if (child instanceof TZone) {
                ((TZone) child).setData(data);
            }
            child = child.next();
        }
    }

    @Override
    public void getData(Object data) {
        // For checkboxes: write bits to integer at data pointer
        // For radio buttons: write value to integer at data pointer
        TAtom child = son();
        while (child != null) {
            if (child instanceof TZone) {
                ((TZone) child).getData(data);
            }
            child = child.next();
        }
    }

    @Override
    public long dataSize() {
        // Group box typically handles one integer (4 bytes)
        return 4;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
