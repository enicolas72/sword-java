package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.TexHelper;
import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.Widget;
import net.eric_nicolas.sword.ui.base.WindowPalette;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

/**
 * TEditLine - Single-line text edit control.
 */
public class EditLine extends Widget {

    protected String  text;
    protected int     maxLength;
    protected int     cursorPos;
    protected boolean hasFocus;
    protected boolean showCursor;
    protected long    lastBlinkTime;
    protected boolean drawBorder;

    public EditLine(int x, int y, int width, int maxLength, int offset, boolean border) {
        super(x, y, width, 20);
        this.text          = "";
        this.maxLength     = maxLength;
        this.cursorPos     = 0;
        this.hasFocus      = false;
        this.showCursor    = false;
        this.lastBlinkTime = System.currentTimeMillis();
        this.drawBorder    = border;
    }

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

        // Background
        ctx.setColor(!isEnabled() ? pal.medium : pal.white);
        ctx.fillRect(x, y, w, h);

        // Border
        if (drawBorder) {
            ctx.setColor(hasFocus ? pal.black : pal.dark);
            ctx.drawRect(x, y, w - 1, h - 1);
        }

        // Text
        if (text != null && !text.isEmpty()) {
            ctx.setColor(pal.black);
            Dimension sz = ctx.measureText(text);
            int textY = y + (h - sz.height) / 2;
            ctx.drawString(x + 4, textY, text);
        }

        // Cursor
        if (hasFocus && showCursor) {
            long now = System.currentTimeMillis();
            if (now - lastBlinkTime > 500) {
                showCursor    = !showCursor;
                lastBlinkTime = now;
            }

            ctx.setColor(pal.black);
            int cursorX = x + 4;
            if (cursorPos > 0 && text.length() >= cursorPos) {
                String beforeCursor = text.substring(0, cursorPos);
                cursorX += ctx.measureText(beforeCursor).width;
            }
            ctx.drawLine(cursorX, y + 3, cursorX, y + h - 4);
        }
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (contains(event.where)) {
            hasFocus      = true;
            showCursor    = true;
            lastBlinkTime = System.currentTimeMillis();

            // Position cursor at the click position
            Point absPos = getAbsolutePosition();
            int clickX = event.where.x() - absPos.x() - 4;
            if (clickX <= 0) {
                cursorPos = 0;
            } else {
                // Walk prefix widths to find the closest character boundary
                int charPos   = 0;
                int prevWidth = 0;
                for (int i = 0; i < text.length(); i++) {
                    int nextWidth = TexHelper.measure(text.substring(0, i + 1),
                            PaintContext.DEFAULT_FONT_SIZE).width;
                    int charWidth = nextWidth - prevWidth;
                    if (prevWidth + charWidth / 2 > clickX) break;
                    prevWidth = nextWidth;
                    charPos++;
                }
                cursorPos = Math.min(charPos, text.length());
            }
            return true;
        } else {
            hasFocus = false;
            return false;
        }
    }

    @Override
    protected boolean keyDown(EventKeyboard event) {
        if (!hasFocus || !isEnabled()) return false;

        boolean handled = false;

        if (event.keyChar != 0 && event.keyChar != KeyEvent.CHAR_UNDEFINED) {
            char ch = event.keyChar;
            if (ch == '\b') {
                if (cursorPos > 0) {
                    text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
                    cursorPos--;
                    handled = true;
                }
            } else if (ch >= 32 && ch < 127 || ch >= 128) {
                if (text.length() < maxLength) {
                    text = text.substring(0, cursorPos) + ch + text.substring(cursorPos);
                    cursorPos++;
                    handled = true;
                }
            }
        } else {
            switch (event.keyCode) {
                case KeyEvent.VK_LEFT:
                    if (cursorPos > 0) { cursorPos--; handled = true; }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (cursorPos < text.length()) { cursorPos++; handled = true; }
                    break;
                case KeyEvent.VK_HOME:
                    cursorPos = 0; handled = true;
                    break;
                case KeyEvent.VK_END:
                    cursorPos = text.length(); handled = true;
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
            showCursor    = true;
            lastBlinkTime = System.currentTimeMillis();
        }
        return handled;
    }

    public String getText() { return text; }

    public void setText(String text) {
        this.text      = text != null ? text : "";
        this.cursorPos = Math.min(cursorPos, this.text.length());
    }
}
