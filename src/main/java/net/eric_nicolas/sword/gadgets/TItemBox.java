package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Font;

/**
 * TItemBox - Base class for checkbox and radio button items.
 * Provides special handling for item boxes within group boxes.
 */
public class TItemBox extends TButton {

    protected String text;
    protected Font itemFont;

    /**
     * Default constructor.
     */
    public TItemBox() {
        this(0, 0, 100, 0, "");
    }

    /**
     * Constructor with position, size, options, and text.
     */
    public TItemBox(int x, int y, int width, int options, String text) {
        super(x, y, width, 20, 0, 0, options);
        this.text = text != null ? text : "";
        this.itemFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    @Override
    protected void paint(Graphics2D g) {
        // ItemBox doesn't draw button frame - just the content
        // Subclasses override to draw checkbox/radio specific UI
        drawInside(g, 0);
    }

    @Override
    protected void drawInside(Graphics2D g, int offset) {
        // Override in subclasses (TCheckBox, TRadioBox)
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
