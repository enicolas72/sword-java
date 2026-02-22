package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.TObject;

/**
 * TRadioBox - Radio button control.
 * Works with TGroupBox parent - only one radio button can be selected in a group.
 */
public class RadioBox extends ItemBox {

    protected int value; // Value for this radio button

    /**
     * Constructor with position, size, options, value, and text.
     */
    public RadioBox(int x, int y, int width, int options, int value, String text) {
        super(x, y, width, options, text);
        this.value = value;
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        int x = 0;
        int y = 0;

        // Draw radio button circle (12x12)
        ctx.setColor(TColors.WHITE);
        ctx.fillOval(x + 2, y + 4, 12, 12);
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawOval(x + 2, y + 4, 12, 12);

        // Draw filled circle if selected
        if (isRadioSelected()) {
            ctx.setColor(TColors.BLACK);
            ctx.fillOval(x + 5, y + 7, 6, 6);
        }

        // Draw text
        if (text != null && !text.isEmpty()) {
            if (hasStatus(SF_DISABLED)) {
                ctx.setColor(TColors.DARK_GRAY);
            } else {
                ctx.setColor(TColors.BLACK);
            }
            ctx.setFont(itemFont);
            String displayText = text.replace("&", "");
            ctx.drawString(x + 18, y + 14, displayText);
        }
    }

    @Override
    protected void action() {
        if (!hasStatus(SF_DISABLED)) {
            TObject parentAtom = father();
            if (parentAtom instanceof GroupBox parentGroupBox) {
                parentGroupBox.value = this.value;
            }
        }
    }

    public boolean isRadioSelected() {
        TObject parentAtom = father();
        if (parentAtom instanceof GroupBox parentGroupBox) {
            return parentGroupBox.value == this.value;
        }
        return false;
    }

    public int getValue() {
        return value;
    }
}
