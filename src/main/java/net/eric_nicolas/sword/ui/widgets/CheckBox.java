package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.TColors;
import net.eric_nicolas.sword.ui.base.TObject;

/**
 * TCheckBox - Checkbox control with bitmask state.
 * Works with TGroupBox parent to exchange data.
 */
public class CheckBox extends ItemBox {

    protected int mask; // Bit mask for this checkbox
    protected boolean checked;

    /**
     * Constructor with position, size, options, mask, and text.
     */
    public CheckBox(int x, int y, int width, int mask, String text) {
        super(x, y, width, text);
        this.mask = mask;
        this.checked = false;
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        int x = 0;
        int y = 0;

        // Draw checkbox box (12x12)
        ctx.setColor(TColors.WHITE);
        ctx.fillRect(x + 2, y + 4, 12, 12);
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawRect(x + 2, y + 4, 12, 12);

        // Draw check mark if checked
        if (checked) {
            ctx.setColor(TColors.BLACK);
            // Draw X
            ctx.drawLine(x + 5, y + 7, x + 11, y + 13);
            ctx.drawLine(x + 6, y + 7, x + 12, y + 13);
            ctx.drawLine(x + 11, y + 7, x + 5, y + 13);
            ctx.drawLine(x + 12, y + 7, x + 6, y + 13);
        }

        // Draw text
        if (text != null && !text.isEmpty()) {
            if (!isEnabled()) {
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
        if (isEnabled()) {
            checked = !checked;
            // Update parent group box value
            updateParentValue();
        }
    }

    protected void updateParentValue() {
        TObject parentAtom = father();
        if (parentAtom instanceof GroupBox parent) {
            if (checked) {
                parent.value |= mask; // Set bit
            } else {
                parent.value &= ~mask; // Clear bit
            }
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        updateParentValue();
    }
}
