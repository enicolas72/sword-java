package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.ScreenArea;
import net.eric_nicolas.sword.ui.base.WindowPalette;

/**
 * TRadioBox - Radio button control.
 * Works with TGroupBox parent - only one radio button can be selected in a group.
 */
public class RadioBox extends ItemBox {

    protected int value; // Value for this radio button

    /**
     * Constructor with position, size, options, value, and text.
     */
    public RadioBox(int x, int y, int width, int value, String text) {
        super(x, y, width, text);
        this.value = value;
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        int x = 0;
        int y = 0;
        WindowPalette pal = ctx.palette();

        // Draw radio button circle (12x12)
        ctx.setColor(pal.white);
        ctx.fillOval(x + 2, y + 4, 12, 12);
        ctx.setColor(pal.dark);
        ctx.drawOval(x + 2, y + 4, 12, 12);

        // Draw filled circle if selected
        if (isRadioSelected()) {
            ctx.setColor(pal.black);
            ctx.fillOval(x + 5, y + 7, 6, 6);
        }

        // Draw text
        if (text != null && !text.isEmpty()) {
            ctx.setColor(!isEnabled() ? pal.dark : pal.black);
            ctx.setFont(itemFont);
            String displayText = text.replace("&", "");
            ctx.drawString(x + 18, y + 14, displayText);
        }
    }

    @Override
    protected void action() {
        if (isEnabled()) {
            ScreenArea parentAtom = father();
            if (parentAtom instanceof GroupBox parentGroupBox) {
                parentGroupBox.value = this.value;
            }
        }
    }

    public boolean isRadioSelected() {
        ScreenArea parentAtom = father();
        if (parentAtom instanceof GroupBox parentGroupBox) {
            return parentGroupBox.value == this.value;
        }
        return false;
    }

    public int getValue() {
        return value;
    }
}
