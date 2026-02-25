package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.Widget;
import net.eric_nicolas.sword.ui.base.WindowPalette;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.Font;
import java.awt.event.KeyEvent;


/**
 * TEditLine - Single-line text edit control.
 * Supports keyboard input, cursor, and data exchange.
 */
public class EditLine extends Widget {

    protected String text;
    protected int maxLength;
    protected int cursorPos;
    protected boolean hasFocus;
    protected boolean showCursor;
    protected Font editFont;
    protected long lastBlinkTime;
    protected boolean drawBorder;

    /**
     * Constructor with position, size, max length, and initial offset.
     */
    public EditLine(int x, int y, int width, int maxLength, int offset, boolean border) {
        super(x, y, width, 20);
        this.text = "";
        this.maxLength = maxLength;
        this.cursorPos = 0;
        this.hasFocus = false;
        this.showCursor = false;
        this.editFont = new Font("Monospaced", Font.PLAIN, 12);
        this.lastBlinkTime = System.currentTimeMillis();
        this.drawBorder = border;
    }

    /**
     * Constructor with default border.
     */
    public EditLine(int x, int y, int width, int maxLength, int offset) {
        this(x, y, width, maxLength, offset, true);
    }

    @Override
    protected void paint(PaintContext ctx) {
        int x = 0;
        int y = 0;
        int w = bounds.width();
        int h = bounds.height();
        WindowPalette pal = ctx.palette();

        // Draw background
        ctx.setColor(!isEnabled() ? pal.medium : pal.white);
        ctx.fillRect(x, y, w, h);

        // Draw border if enabled
        if (drawBorder) {
            ctx.setColor(hasFocus ? pal.black : pal.dark);
            ctx.drawRect(x, y, w - 1, h - 1);
        }

        // Draw text
        if (text != null && !text.isEmpty()) {
            ctx.setColor(pal.black);
            ctx.setFont(editFont);
            ctx.drawString(x + 4, y + 15, text);
        }

        // Draw cursor
        if (hasFocus && showCursor) {
            // Blink cursor
            long now = System.currentTimeMillis();
            if (now - lastBlinkTime > 500) {
                showCursor = !showCursor;
                lastBlinkTime = now;
            }

            ctx.setColor(pal.black);
            ctx.setFont(editFont);
            int cursorX = x + 4;
            if (cursorPos > 0 && text.length() >= cursorPos) {
                String beforeCursor = text.substring(0, cursorPos);
                cursorX += ctx.getFontMetrics().stringWidth(beforeCursor);
            }
            ctx.drawLine(cursorX, y + 3, cursorX, y + h - 4);
        }
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (contains(event.where)) {
            hasFocus = true;
            showCursor = true;
            lastBlinkTime = System.currentTimeMillis();

            // Position cursor based on click position
            Point absPos = getAbsolutePosition();
            int clickX = event.where.x() - absPos.x() - 4; // Subtract text offset
            if (clickX <= 0) {
                cursorPos = 0;
            } else {
                // Find character position closest to click
                java.awt.image.BufferedImage tempImage = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g = tempImage.createGraphics();
                g.setFont(editFont);
                java.awt.FontMetrics fm = g.getFontMetrics();

                int charPos = 0;
                int totalWidth = 0;
                for (int i = 0; i < text.length(); i++) {
                    int charWidth = fm.charWidth(text.charAt(i));
                    if (totalWidth + charWidth / 2 > clickX) {
                        break;
                    }
                    totalWidth += charWidth;
                    charPos++;
                }
                cursorPos = Math.min(charPos, text.length());
                g.dispose();
            }

            return true;
        } else {
            hasFocus = false;
            return false;
        }
    }

    @Override
    protected boolean keyDown(EventKeyboard event) {
        if (!hasFocus || !isEnabled()) {
            return false;
        }

        boolean handled = false;

        // Check if this is a character input event (keyChar is set)
        if (event.keyChar != 0 && event.keyChar != KeyEvent.CHAR_UNDEFINED) {
            // Handle character input (keyboard layout aware)
            char ch = event.keyChar;

            if (ch == '\b') {
                // Backspace
                if (cursorPos > 0) {
                    text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
                    cursorPos--;
                    handled = true;
                }
            } else if (ch >= 32 && ch < 127 || ch >= 128) {
                // Printable character (ASCII or extended)
                if (text.length() < maxLength) {
                    text = text.substring(0, cursorPos) + ch + text.substring(cursorPos);
                    cursorPos++;
                    handled = true;
                }
            }
        } else {
            // Handle special keys (navigation, etc.) using key codes
            switch (event.keyCode) {
                case KeyEvent.VK_LEFT:
                    if (cursorPos > 0) {
                        cursorPos--;
                        handled = true;
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (cursorPos < text.length()) {
                        cursorPos++;
                        handled = true;
                    }
                    break;

                case KeyEvent.VK_HOME:
                    cursorPos = 0;
                    handled = true;
                    break;

                case KeyEvent.VK_END:
                    cursorPos = text.length();
                    handled = true;
                    break;

                case KeyEvent.VK_DELETE:
                    if (cursorPos < text.length()) {
                        text = text.substring(0, cursorPos) + text.substring(cursorPos + 1);
                        handled = true;
                    }
                    break;
            }
        }

        if (handled) {
            showCursor = true;
            lastBlinkTime = System.currentTimeMillis();
        }

        return handled;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPos = Math.min(cursorPos, this.text.length());
    }
}
