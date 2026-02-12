package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Font;
import java.lang.reflect.Field;

/**
 * TRadioBox - Radio button control.
 * Works with TGroupBox parent - only one radio button can be selected in a group.
 */
public class TRadioBox extends TButton {

    protected String text;
    protected int value; // Value for this radio button
    protected Font radioFont;

    /**
     * Constructor with position, size, options, value, and text.
     */
    public TRadioBox(int x, int y, int width, int options, int value, String text) {
        super(x, y, width, 20, 0, 0, options);
        this.text = text != null ? text : "";
        this.value = value;
        this.radioFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    @Override
    protected void paint(Graphics2D g) {
        int x = bounds.a.x;
        int y = bounds.a.y;

        // Draw radio button circle (12x12)
        g.setColor(TColors.WHITE);
        g.fillOval(x + 2, y + 4, 12, 12);
        g.setColor(TColors.DARK_GRAY);
        g.drawOval(x + 2, y + 4, 12, 12);

        // Draw filled circle if selected
        if (isRadioSelected()) {
            g.setColor(TColors.BLACK);
            g.fillOval(x + 5, y + 7, 6, 6);
        }

        // Draw text
        if (text != null && !text.isEmpty()) {
            if (hasStatus(SF_DISABLED)) {
                g.setColor(TColors.DARK_GRAY);
            } else {
                g.setColor(TColors.BLACK);
            }
            g.setFont(radioFont);
            String displayText = text.replace("&", "");
            g.drawString(displayText, x + 18, y + 14);
        }
    }

    @Override
    protected void action() {
        if (!hasStatus(SF_DISABLED)) {
            // Deselect all other radio buttons in group
            TAtom parentAtom = father();
            if (parentAtom instanceof TGroupBox) {
                TGroupBox parent = (TGroupBox) parentAtom;

                // Deselect all siblings
                TAtom sibling = parent.son();
                while (sibling != null) {
                    if (sibling instanceof TRadioBox && sibling != this) {
                        // Just update parent value, no need to clear individual state
                    }
                    sibling = sibling.next();
                }

                // Select this radio button
                parent.value = this.value;
            }
        }
    }

    public boolean isRadioSelected() {
        TAtom parentAtom = father();
        if (parentAtom instanceof TGroupBox) {
            TGroupBox parent = (TGroupBox) parentAtom;
            return parent.value == this.value;
        }
        return false;
    }

    @Override
    public void setData(Object data) {
        // Read selected value from parent group box
        TAtom parentAtom = father();
        if (parentAtom instanceof TGroupBox) {
            TGroupBox parent = (TGroupBox) parentAtom;

            // If data is provided, read from appropriate field
            if (data != null) {
                try {
                    // Look for a field named "Radios" (or similar pattern)
                    Field[] fields = data.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equalsIgnoreCase("radios") ||
                            field.getName().toLowerCase().contains("radio")) {
                            field.setAccessible(true);
                            parent.value = field.getInt(data);
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Ignore reflection errors
                }
            }
        }
    }

    @Override
    public void getData(Object data) {
        // Write selected value to data structure
        TAtom parentAtom = father();
        if (parentAtom instanceof TGroupBox && data != null) {
            TGroupBox parent = (TGroupBox) parentAtom;

            try {
                // Look for a field named "Radios" (or similar pattern)
                Field[] fields = data.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase("radios") ||
                        field.getName().toLowerCase().contains("radio")) {
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

    public int getValue() {
        return value;
    }
}
