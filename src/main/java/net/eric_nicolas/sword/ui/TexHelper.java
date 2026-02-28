package net.eric_nicolas.sword.ui;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * TexHelper — converts text-mode TeX strings to rendered {@link BufferedImage}s
 * via JLaTeXMath.  Used by {@link net.eric_nicolas.sword.ui.base.PaintContext}
 * to draw all text in the S.W.O.R.D widget hierarchy.
 *
 * <h3>Input format</h3>
 * Input is written in <em>text mode</em>: plain text is emitted as-is; inline
 * math is wrapped in {@code \math{...}} blocks.  Example:
 * <pre>
 *   "Hello World !\n\math{\int_{-\infty}^{+\infty} e^{-x^2}\,dx = \sqrt{\pi}}"
 * </pre>
 * Newlines ({@code \n}) produce line breaks via {@code \begin{array}{l}...}.
 *
 * <h3>Caching</h3>
 * Rendered images are stored in a static {@link Cache} keyed by
 * {@code (text, color, fontSize)}.  The cache holds up to {@value #CACHE_SIZE}
 * entries with FIFO eviction.  Call {@link #clearCache()} to flush all entries
 * (e.g., after a global font-size change).
 */
public final class TexHelper {

    private static final int CACHE_SIZE = 32;

    /** (text, color, fontSize) → rendered image. */
    private static final Cache<Triple<String, Color, Float>, BufferedImage> CACHE =
            new Cache<>(CACHE_SIZE);

    /** (text, fontSize) → pixel dimensions; color-independent so kept separately. */
    private static final Cache<Duple<String, Float>, Dimension> DIM_CACHE =
            new Cache<>(CACHE_SIZE);

    private TexHelper() {}   // static utility class

    // ===== Public API =====

    /**
     * Render {@code text} (text-mode TeX with optional {@code \math{...}} blocks)
     * to a {@link BufferedImage} at the given font size and colour.
     *
     * @return the rendered image, or {@code null} if the input is empty or
     *         JLaTeXMath throws an exception
     */
    public static BufferedImage render(String text, Color color, float fontSize) {
        if (text == null || text.isEmpty()) return null;

        Triple<String, Color, Float> key = new Triple<>(text, color, fontSize);
        BufferedImage img = CACHE.get(key);
        if (img != null) return img;

        img = doRender(toLatex(text), color, fontSize);
        if (img != null) CACHE.put(key, img);
        return img;
    }

    /**
     * Return the pixel dimensions that {@code text} would occupy at the given
     * font size.  Dimensions are colour-independent and cached separately from
     * rendered images so repeated layout passes cost only a single map lookup.
     *
     * @return dimensions, or {@code (0, 0)} if the text is empty or rendering fails
     */
    public static Dimension measure(String text, float fontSize) {
        if (text == null || text.isEmpty()) return new Dimension(0, 0);

        Duple<String, Float> dimKey = new Duple<>(text, fontSize);
        Dimension cached = DIM_CACHE.get(dimKey);
        if (cached != null) return cached;

        BufferedImage img = render(text, Color.BLACK, fontSize);
        Dimension dim = img != null
                ? new Dimension(img.getWidth(), img.getHeight())
                : new Dimension(0, 0);
        DIM_CACHE.put(dimKey, dim);
        return dim;
    }

    /** Remove all cached images and dimensions. */
    public static void clearCache() {
        CACHE.clear();
        DIM_CACHE.clear();
    }

    // ===== TeX → JLaTeXMath conversion =====

    /**
     * Convert text-mode TeX input into a JLaTeXMath-compatible formula string.
     * Each line becomes a row in {@code \begin{array}{l}}; within each line
     * plain text is wrapped in {@code \text{...}} and {@code \math{...}} blocks
     * are emitted verbatim as math.
     */
    static String toLatex(String input) {
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
    private static String convertLine(String line) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < line.length()) {
            int mathIdx = line.indexOf("\\math{", i);
            if (mathIdx < 0) {
                appendText(sb, line.substring(i));
                break;
            }
            if (mathIdx > i) appendText(sb, line.substring(i, mathIdx));

            int start = mathIdx + 6;   // first char of math content
            int depth = 1;
            int j = start;
            while (j < line.length() && depth > 0) {
                char c = line.charAt(j);
                if      (c == '{') depth++;
                else if (c == '}') depth--;
                j++;
            }
            // j is one past the closing '}' (or at line.length() if malformed)
            int mathEnd = (depth == 0) ? j - 1 : j;
            sb.append(line, start, mathEnd);
            i = j;
        }
        return sb.toString();
    }

    /** Emit a non-empty plain-text segment as {@code \text{...}}. */
    private static void appendText(StringBuilder sb, String text) {
        if (text.isEmpty()) return;
        String escaped = text.replace("{", "\\{").replace("}", "\\}");
        sb.append("\\text{").append(escaped).append("}");
    }

    // ===== Rendering =====

    private static BufferedImage doRender(String formula, Color color, float fontSize) {
        if (formula.isEmpty()) return null;
        try {
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
}
