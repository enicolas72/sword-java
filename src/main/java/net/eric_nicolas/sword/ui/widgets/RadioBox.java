package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.ScreenArea;
import net.eric_nicolas.sword.ui.base.WindowPalette;

import java.awt.Dimension;

/**
 * TRadioBox - Radio button control.
 */
public class RadioBox extends ItemBox {

    protected int value;

    public RadioBox(int x, int y, int width, int value, String text) {
        super(x, y, width, text);
        this.value = value;
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        int x = 0;
        int y = 0;
        WindowPalette pal = ctx.palette();

        // Draw radio button circle (12 × 12)
        ctx.setColor(pal.white);
        ctx.fillOval(x + 2, y + 4, 12, 12);
        ctx.setColor(pal.dark);
        ctx.drawOval(x + 2, y + 4, 12, 12);

        // Draw filled centre if selected
        if (isRadioSelected()) {
            ctx.setColor(pal.black);
            ctx.fillOval(x + 5, y + 7, 6, 6);
        }

        // Draw label text aligned with the centre of the 12-px circle
        if (text != null && !text.isEmpty()) {
            ctx.setColor(!isEnabled() ? pal.dark : pal.black);
            String displayText = text.replace("&", "");
            Dimension sz = ctx.measureText(displayText);
            int dy = y + 4 + (12 - sz.height) / 2;
            ctx.drawString(x + 18, dy, displayText);
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

    public int getValue() { return value; }
}
