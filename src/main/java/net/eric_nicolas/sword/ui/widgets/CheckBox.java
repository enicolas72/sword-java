package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.ScreenArea;
import net.eric_nicolas.sword.ui.base.WindowPalette;

import java.awt.Dimension;

/**
 * TCheckBox - Checkbox control with bitmask state.
 */
public class CheckBox extends ItemBox {

    protected int mask;
    protected boolean checked;

    public CheckBox(int x, int y, int width, int mask, String text) {
        super(x, y, width, text);
        this.mask    = mask;
        this.checked = false;
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        int x = 0;
        int y = 0;
        WindowPalette pal = ctx.palette();

        // Draw checkbox box (12 × 12)
        ctx.setColor(pal.white);
        ctx.fillRect(x + 2, y + 4, 12, 12);
        ctx.setColor(pal.dark);
        ctx.drawRect(x + 2, y + 4, 12, 12);

        // Draw check mark if checked
        if (checked) {
            ctx.setColor(pal.black);
            ctx.drawLine(x + 5, y + 7, x + 11, y + 13);
            ctx.drawLine(x + 6, y + 7, x + 12, y + 13);
            ctx.drawLine(x + 11, y + 7, x + 5,  y + 13);
            ctx.drawLine(x + 12, y + 7, x + 6,  y + 13);
        }

        // Draw label text aligned with the centre of the 12-px box
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
            checked = !checked;
            updateParentValue();
        }
    }

    protected void updateParentValue() {
        ScreenArea parentAtom = father();
        if (parentAtom instanceof GroupBox parent) {
            if (checked) {
                parent.value |= mask;
            } else {
                parent.value &= ~mask;
            }
        }
    }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) {
        this.checked = checked;
        updateParentValue();
    }
}
