package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.Widget;
import net.eric_nicolas.sword.ui.base.WindowPalette;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * LatexWidget — displays a LaTeX formula string rendered by JLaTeXMath.
 *
 * The formula is rendered lazily on first paint and cached.  Calling
 * {@link #setLatex(String)} or changing the font size invalidates the cache
 * and forces a re-render on the next paint.
 *
 * The text colour is taken from {@code ctx.palette().black} so the formula
 * automatically adapts to the window's colour scheme.
 */
public class LatexWidget extends Widget {

    private String latex;
    private float fontSize;

    // Cached render; invalidated when latex, fontSize, or colour changes
    private BufferedImage cache;
    private Color lastColor;

    /**
     * @param x        position (local to parent)
     * @param y        position (local to parent)
     * @param width    widget width
     * @param height   widget height
     * @param latex    LaTeX source string (e.g. {@code "E = mc^2"})
     * @param fontSize point size passed to JLaTeXMath
     */
    public LatexWidget(int x, int y, int width, int height, String latex, float fontSize) {
        super(x, y, width, height);
        this.latex    = latex;
        this.fontSize = fontSize;
    }

    @Override
    protected void paint(PaintContext ctx) {
        Color textColor = ctx.palette().black;

        // Re-render when the formula or the text colour has changed
        if (cache == null || !textColor.equals(lastColor)) {
            cache     = render(textColor);
            lastColor = textColor;
        }

        if (cache != null) {
            // Centre the rendered image within the widget bounds
            int dx = (bounds.width()  - cache.getWidth())  / 2;
            int dy = (bounds.height() - cache.getHeight()) / 2;
            ctx.drawImage(cache, Math.max(0, dx), Math.max(0, dy));
        }
    }

    // ===== Private helpers =====

    private BufferedImage render(Color color) {
        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon    icon    = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize);
            icon.setForeground(color);

            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();
            return img;
        } catch (Exception e) {
            return null;
        }
    }

    // ===== Public API =====

    public String getLatex() { return latex; }

    public void setLatex(String latex) {
        this.latex = latex;
        cache = null;
    }

    public float getFontSize() { return fontSize; }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        cache = null;
    }
}
