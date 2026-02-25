package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.Widget;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * TexWidget — displays TeX-formatted content rendered by JLaTeXMath.
 *
 * Input is a TeX string written in text mode.  Math content is delimited
 * by {@code \math{...}}.  Newlines ({@code \n}) produce line breaks.
 * Example:
 * <pre>
 *   "Hello World !\n\math{\int_{-\infty}^{+\infty} e^{-x^2}\,dx = \sqrt{\pi}}"
 * </pre>
 *
 * The input is converted to a JLaTeXMath-compatible formula automatically:
 * text segments are wrapped in {@code \text{...}}, {@code \math{...}} blocks
 * are emitted as raw math, and the whole thing is placed inside
 * {@code \begin{array}{l}...\end{array}} so newlines render as row breaks.
 *
 * Rendering is lazy (first paint) and cached in a {@link BufferedImage}.
 * Calling {@link #setTex(String)} or {@link #setFontSize(float)} invalidates
 * the cache and forces a re-render on the next paint.
 *
 * The text colour is taken from {@code ctx.palette().black} so the output
 * automatically adapts to the window's colour scheme.
 */
public class TexWidget extends Widget {

    private String tex;
    private float fontSize;

    // Cached render; invalidated when tex, fontSize, or colour changes
    private BufferedImage cache;
    private Color lastColor;

    /**
     * @param x        position (local to parent)
     * @param y        position (local to parent)
     * @param width    widget width
     * @param height   widget height
     * @param tex      TeX source — text mode with {@code \math{...}} for formulas
     * @param fontSize point size passed to JLaTeXMath
     */
    public TexWidget(int x, int y, int width, int height, String tex, float fontSize) {
        super(x, y, width, height);
        this.tex      = tex;
        this.fontSize = fontSize;
    }

    @Override
    protected void paint(PaintContext ctx) {
        Color textColor = ctx.palette().black;

        // Re-render when content or text colour has changed
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

    // ===== TeX → JLaTeXMath conversion =====

    /**
     * Convert the widget's TeX input into a JLaTeXMath-compatible formula.
     * Each line of the input becomes a row in {@code \begin{array}{l}};
     * within each line, plain text is wrapped in {@code \text{...}} and
     * {@code \math{...}} blocks are emitted verbatim as math.
     */
    private String toLatex(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] lines = input.split("\n", -1);
        StringBuilder sb = new StringBuilder("\\begin{array}{l}");
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) sb.append("\\\\");
            sb.append(convertLine(lines[i]));
        }
        sb.append("\\end{array}");
        return sb.toString();
    }

    /**
     * Convert one line: text segments → {@code \text{...}},
     * {@code \math{...}} blocks → raw math content.
     */
    private String convertLine(String line) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < line.length()) {
            int mathIdx = line.indexOf("\\math{", i);
            if (mathIdx < 0) {
                // No more math blocks — rest is plain text
                appendText(sb, line.substring(i));
                break;
            }
            // Text before \math{
            if (mathIdx > i) appendText(sb, line.substring(i, mathIdx));

            // Find the matching closing brace for \math{
            int start = mathIdx + 6;   // first char of math content
            int depth = 1;
            int j = start;
            while (j < line.length() && depth > 0) {
                char c = line.charAt(j);
                if      (c == '{') depth++;
                else if (c == '}') depth--;
                j++;
            }
            // j is one past the closing '}', or at line.length() if malformed.
            // Math content is line[start .. j-2] inclusive when depth==0
            // (j-1 is the closing '}'); use j if malformed.
            int mathEnd = (depth == 0) ? j - 1 : j;
            sb.append(line, start, mathEnd);
            i = j;
        }
        return sb.toString();
    }

    /** Emit a non-empty plain-text segment as {@code \text{...}}. */
    private void appendText(StringBuilder sb, String text) {
        if (text.isEmpty()) return;
        // Escape braces so they don't confuse the JLaTeXMath parser inside \text{}
        String escaped = text.replace("{", "\\{").replace("}", "\\}");
        sb.append("\\text{").append(escaped).append("}");
    }

    // ===== Rendering =====

    private BufferedImage render(Color color) {
        try {
            String formula = toLatex(tex);
            if (formula.isEmpty()) return null;

            TeXFormula tf   = new TeXFormula(formula);
            TeXIcon    icon = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize);
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

    public String getTex() { return tex; }

    public void setTex(String tex) {
        this.tex = tex;
        cache = null;
    }

    public float getFontSize() { return fontSize; }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        cache = null;
    }
}
