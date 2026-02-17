package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Font;
import java.lang.reflect.Field;

/**
 * TCheckBox - Checkbox control with bitmask state.
 * Works with TGroupBox parent to exchange data.
 */
public class TCheckBox extends TItemBox {

    protected int mask; // Bit mask for this checkbox
    protected boolean checked;

    /**
     * Constructor with position, size, options, mask, and text.
     */
    public TCheckBox(int x, int y, int width, int options, int mask, String text) {
        super(x, y, width, options, text);
        this.mask = mask;
        this.checked = false;
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        int x = bounds.a.x;
        int y = bounds.a.y;

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
            checked = !checked;
            // Update parent group box value
            updateParentValue();
        }
    }

    protected void updateParentValue() {
        TAtom parentAtom = father();
        if (parentAtom instanceof TGroupBox) {
            TGroupBox parent = (TGroupBox) parentAtom;
            if (checked) {
                parent.value |= mask; // Set bit
            } else {
                parent.value &= ~mask; // Clear bit
            }
        }
    }

    @Override
    public void setData(Object data) {
        // Read checked state from parent group box value
        TAtom parentAtom = father();
        if (parentAtom instanceof TGroupBox) {
            TGroupBox parent = (TGroupBox) parentAtom;

            // If data is provided and has a field matching the data structure
            if (data != null) {
                try {
                    // Look for a field named "Checks" (or similar pattern)
                    Field[] fields = data.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equalsIgnoreCase("checks") ||
                            field.getName().toLowerCase().contains("check")) {
                            field.setAccessible(true);
                            parent.value = field.getInt(data);
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Ignore reflection errors
                }
            }

            checked = (parent.value & mask) != 0;
        }
    }

    @Override
    public void getData(Object data) {
        // Write checked state to parent group box value
        updateParentValue();

        TAtom parentAtom = father();
        if (parentAtom instanceof TGroupBox && data != null) {
            TGroupBox parent = (TGroupBox) parentAtom;

            try {
                // Look for a field named "Checks" (or similar pattern)
                Field[] fields = data.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase("checks") ||
                        field.getName().toLowerCase().contains("check")) {
                        field.setAccessible(true);
                        field.setInt(data, parent.value);
                        break;
                    }
                }
            } catch (Exception e) {
                // Ignore reflection errors
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
