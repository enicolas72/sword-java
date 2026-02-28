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
 * {@link TeXIcon} objects (the result of the expensive parse-and-layout step)
 * are cached by {@code (text, fontSize)} — colour-independent, because colour
 * is applied via {@link TeXIcon#setForeground} just before each paint call.
 * This means a cache hit saves {@code createTeXIcon} time (≈100–320 µs) for
 * any colour variant of a previously seen string.  The cache holds up to
 * {@value #CACHE_SIZE} entries with FIFO eviction.  Call {@link #clearCache()}
 * to flush all entries (e.g. after a global font-size change).
 */
public final class TexHelper {

    private static final int CACHE_SIZE = 32;

    /** (text, fontSize) → parsed-and-laid-out icon; colour-independent. */
    private static final Cache<Duple<String, Float>, TeXIcon> ICON_CACHE =
            new Cache<>(CACHE_SIZE);

    private TexHelper() {}   // static utility class

    // ===== Public API =====

    /**
     * Render {@code text} (text-mode TeX with optional {@code \math{...}} blocks)
     * to a {@link BufferedImage} at the given font size and colour.
     * The expensive parse-and-layout step ({@code createTeXIcon}) is cached by
     * {@code (text, fontSize)}; only the rasterisation ({@code paintIcon}) runs
     * on every call.
     *
     * @return the rendered image, or {@code null} if the input is empty or
     *         JLaTeXMath throws an exception
     */
    public static BufferedImage render(String text, Color color, float fontSize) {
        if (text == null || text.isEmpty()) return null;

        TeXIcon icon = getOrCreateIcon(text, fontSize);
        if (icon == null) return null;

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
    }

    /**
     * Return the pixel dimensions that {@code text} would occupy at the given
     * font size.  Uses the cached {@link TeXIcon} directly — no extra render needed.
     *
     * @return dimensions, or {@code (0, 0)} if the text is empty or parsing fails
     */
    public static Dimension measure(String text, float fontSize) {
        if (text == null || text.isEmpty()) return new Dimension(0, 0);
        TeXIcon icon = getOrCreateIcon(text, fontSize);
        return icon != null
                ? new Dimension(icon.getIconWidth(), icon.getIconHeight())
                : new Dimension(0, 0);
    }

    /** Remove all cached icons. */
    public static void clearCache() {
        ICON_CACHE.clear();
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

    // ===== Icon cache =====

    /**
     * Return the cached {@link TeXIcon} for {@code (text, fontSize)}, creating
     * and caching it if not already present.  This is the only place
     * {@code createTeXIcon} is called.
     */
    private static TeXIcon getOrCreateIcon(String text, float fontSize) {
        Duple<String, Float> key = new Duple<>(text, fontSize);
        TeXIcon icon = ICON_CACHE.get(key);
        if (icon != null) return icon;
        try {
            TeXFormula tf = new TeXFormula(toLatex(text));
            icon = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize);
            ICON_CACHE.put(key, icon);
            return icon;
        } catch (Exception e) {
            return null;
        }
    }
}
