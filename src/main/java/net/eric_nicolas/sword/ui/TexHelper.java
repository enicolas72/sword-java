package net.eric_nicolas.sword.ui;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.Dimension;

/**
 * TexHelper — parses and lays out text-mode TeX strings as cached
 * {@link TeXIcon} objects via JLaTeXMath.  Used by
 * {@link net.eric_nicolas.sword.ui.base.PaintContext} to draw all text
 * in the S.W.O.R.D widget hierarchy.
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
 * {@link TeXIcon} objects (the result of the expensive parse-and-layout step,
 * ≈100–320 µs) are cached by {@code (text, fontSize)} — colour-independent,
 * because colour is applied via {@link TeXIcon#setForeground} at paint time.
 * The cache holds up to {@value #CACHE_SIZE} entries with FIFO eviction.
 * Call {@link #clearCache()} to flush all entries (e.g. after a global
 * font-size change).
 */
public final class TexHelper {

    private static final int CACHE_SIZE = 32;

    /** (text, fontSize) → parsed-and-laid-out icon; colour-independent. */
    private static final Cache<Duple<String, Float>, TeXIcon> ICON_CACHE =
            new Cache<>(CACHE_SIZE);

    private TexHelper() {}   // static utility class

    // ===== Public API =====

    /**
     * Return the cached {@link TeXIcon} for {@code (text, fontSize)}, creating
     * and caching it on the first call.  Returns {@code null} for null/empty
     * input or if JLaTeXMath cannot parse the formula.
     *
     * <p>Callers are responsible for calling {@link TeXIcon#setForeground}
     * before painting to apply the desired colour.
     */
    public static TeXIcon getOrCreateIcon(String text, float fontSize) {
        if (text == null || text.isEmpty()) return null;
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

    /**
     * Return the pixel dimensions that {@code text} would occupy at the given
     * font size, using the cached {@link TeXIcon} directly.
     *
     * @return dimensions, or {@code (0, 0)} for null/empty input or parse failure
     */
    public static Dimension measure(String text, float fontSize) {
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
}
