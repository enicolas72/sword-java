package net.eric_nicolas.sword.ui.driver;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.Screen;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventKeyboard;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.IntPredicate;

/**
 * AwtDriver - AWT event loop, window management, and double-buffered rendering.
 *
 * Owns the AWT Frame, Canvas, and back buffer. Translates AWT events into
 * S.W.O.R.D events and forwards them to the Screen. All AWT coupling is
 * confined to this class and EventAwtAdapter.
 *
 * @param hotKeyHandler  Called with the AWT key code before desktop dispatch;
 *                       return true to consume the event (e.g. global menu hotkeys).
 */
public class AwtDriver {

    private final Frame frame;
    private final java.awt.Canvas canvas;
    private final BufferedImage backBuffer;
    private final Graphics2D backGraphics;
    private final Screen screen;
    private final IntPredicate hotKeyHandler;
    private boolean running;

    public AwtDriver(String title, int width, int height,
                     Screen screen, IntPredicate hotKeyHandler) {
        this.screen = screen;
        this.hotKeyHandler = hotKeyHandler;

        frame = new Frame(title);
        frame.setSize(width, height);
        frame.setResizable(false);

        canvas = new java.awt.Canvas() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(backBuffer, 0, 0, null);
            }
        };
        canvas.setSize(width, height);
        frame.add(canvas);

        backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        backGraphics = backBuffer.createGraphics();
        backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Event event = EventAwtAdapter.ofMousePressedEvent(e);
                if (screen.handleEvent(event)) forceRepaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Event event = EventAwtAdapter.ofMouseReleasedEvent(e);
                if (screen.handleEvent(event)) forceRepaint();
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Event event = EventAwtAdapter.ofMouseMouseEvent(e);
                if (screen.handleEvent(event)) forceRepaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Event event = EventAwtAdapter.ofMouseDraggedEvent(e);
                if (screen.handleEvent(event)) forceRepaint();
            }
        });

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                EventKeyboard event = EventAwtAdapter.ofKeyPressedEvent(e, '\0');
                if (hotKeyHandler != null && hotKeyHandler.test(event.keyCode)) {
                    forceRepaint();
                    return;
                }
                if (screen.handleEvent(event)) forceRepaint();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (!Character.isISOControl(ch) || ch == '\b' || ch == '\n') {
                    Event event = EventAwtAdapter.ofKeyPressedEvent(e, ch);
                    if (screen.handleEvent(event)) forceRepaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Event event = EventAwtAdapter.ofKeyReleasedEvent(e);
                screen.handleEvent(event);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });

        running = false;
    }

    public void run() {
        running = true;
        forceRepaint();
        frame.setVisible(true);

        while (running) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void quit() {
        running = false;
        backGraphics.dispose();
        frame.dispose();
        System.exit(0);
    }

    public void forceRepaint() {
        PaintContext ctx = PaintContext.ofAWT(backGraphics);
        screen.draw(ctx);

        Graphics g = canvas.getGraphics();
        if (g != null) {
            g.drawImage(backBuffer, 0, 0, null);
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        }
    }
}
