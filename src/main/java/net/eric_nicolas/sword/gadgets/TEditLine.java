package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

/**
 * TEditLine - Single-line text edit control.
 * Supports keyboard input, cursor, and data exchange.
 */
public class TEditLine extends TZone {

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
    public TEditLine(int x, int y, int width, int maxLength, int offset, boolean border) {
        super(x, y, width, 20);
        this.text = "";
        this.maxLength = maxLength;
        this.cursorPos = 0;
        this.hasFocus = false;
        this.showCursor = false;
        this.editFont = new Font("Monospaced", Font.PLAIN, 12);
        this.lastBlinkTime = System.currentTimeMillis();
        this.drawBorder = border;
        setBackgroundColor(TColors.WHITE);
    }

    /**
     * Constructor with default border.
     */
    public TEditLine(int x, int y, int width, int maxLength, int offset) {
        this(x, y, width, maxLength, offset, true);
    }

    @Override
    protected void paint(Graphics2D g) {
        int x = bounds.a.x;
        int y = bounds.a.y;
        int w = bounds.width();
        int h = bounds.height();

        // Draw background
        g.setColor(hasStatus(SF_DISABLED) ? TColors.MEDIUM_GRAY : TColors.WHITE);
        g.fillRect(x, y, w, h);

        // Draw border if enabled
        if (drawBorder) {
            g.setColor(hasFocus ? TColors.BLACK : TColors.DARK_GRAY);
            g.drawRect(x, y, w - 1, h - 1);
        }

        // Draw text
        if (text != null && !text.isEmpty()) {
            g.setColor(TColors.BLACK);
            g.setFont(editFont);
            g.drawString(text, x + 4, y + 15);
        }

        // Draw cursor
        if (hasFocus && showCursor) {
            // Blink cursor
            long now = System.currentTimeMillis();
            if (now - lastBlinkTime > 500) {
                showCursor = !showCursor;
                lastBlinkTime = now;
            }

            g.setColor(TColors.BLACK);
            g.setFont(editFont);
            int cursorX = x + 4;
            if (cursorPos > 0 && text.length() >= cursorPos) {
                String beforeCursor = text.substring(0, cursorPos);
                cursorX += g.getFontMetrics().stringWidth(beforeCursor);
            }
            g.drawLine(cursorX, y + 3, cursorX, y + h - 4);
        }
    }

    @Override
    protected boolean mouseLDown(TEvent event) {
        if (contains(event.where.x, event.where.y)) {
            hasFocus = true;
            showCursor = true;
            lastBlinkTime = System.currentTimeMillis();
            return true;
        } else {
            hasFocus = false;
            return false;
        }
    }

    @Override
    protected boolean keyDown(TEvent event) {
        if (!hasFocus || hasStatus(SF_DISABLED)) {
            return false;
        }

        boolean handled = false;

        switch (event.message) {
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

            case KeyEvent.VK_BACK_SPACE:
                if (cursorPos > 0) {
                    text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
                    cursorPos--;
                    handled = true;
                }
                break;

            case KeyEvent.VK_DELETE:
                if (cursorPos < text.length()) {
                    text = text.substring(0, cursorPos) + text.substring(cursorPos + 1);
                    handled = true;
                }
                break;

            default:
                // Handle character input
                if (event.message >= 32 && event.message < 127) {
                    if (text.length() < maxLength) {
                        char ch = (char) event.message;
                        text = text.substring(0, cursorPos) + ch + text.substring(cursorPos);
                        cursorPos++;
                        handled = true;
                    }
                }
                break;
        }

        if (handled) {
            showCursor = true;
            lastBlinkTime = System.currentTimeMillis();
        }

        return handled;
    }

    @Override
    public void setData(Object data) {
        if (data == null) return;

        try {
            // Look for a String field
            Field[] fields = data.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    String value = (String) field.get(data);
                    text = value != null ? value : "";
                    cursorPos = text.length();
                    break;
                } else if (field.getType().isArray() && field.getType().getComponentType() == char.class) {
                    // Handle char[] array
                    field.setAccessible(true);
                    char[] chars = (char[]) field.get(data);
                    if (chars != null) {
                        // Find null terminator
                        int len = 0;
                        while (len < chars.length && chars[len] != 0) {
                            len++;
                        }
                        text = new String(chars, 0, len);
                        cursorPos = text.length();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
    }

    @Override
    public void getData(Object data) {
        if (data == null) return;

        try {
            // Look for a String field
            Field[] fields = data.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    field.set(data, text);
                    break;
                } else if (field.getType().isArray() && field.getType().getComponentType() == char.class) {
                    // Handle char[] array
                    field.setAccessible(true);
                    char[] chars = (char[]) field.get(data);
                    if (chars != null) {
                        int len = Math.min(text.length(), chars.length - 1);
                        for (int i = 0; i < len; i++) {
                            chars[i] = text.charAt(i);
                        }
                        if (len < chars.length) {
                            chars[len] = 0; // Null terminator
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
    }

    @Override
    public long dataSize() {
        return maxLength;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPos = Math.min(cursorPos, this.text.length());
    }
}
