package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.Widget;

import java.awt.Dimension;

/**
 * TStaticText - Non-interactive text label.
 *
 * Text is drawn via {@link net.eric_nicolas.sword.ui.base.PaintContext#drawString},
 * which renders through {@link net.eric_nicolas.sword.ui.TexHelper}.  Plain text
 * is displayed as-is; {@code \math{...}} blocks produce inline math.  The font
 * size can be changed with {@link #setFontSize(float)}.
 */
public class Label extends Widget {

    protected String text;
    private float fontSize = PaintContext.DEFAULT_FONT_SIZE;

    public Label() {
        this(0, 0, 100, 20, "Text");
    }

    public Label(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text != null ? text : "";
    }

    @Override
    protected void paint(PaintContext ctx) {
        if (text == null || text.isEmpty()) return;

        ctx.setColor(ctx.palette().black);
        PaintContext pc = ctx.withFontSize(fontSize);
        Dimension sz = pc.measureText(text);
        int dx = 2;
        int dy = Math.max(0, (bounds.height() - sz.height) / 2);
        pc.drawString(dx, dy, text);
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public float getFontSize() { return fontSize; }
    public void setFontSize(float size) { this.fontSize = size; }
}
