package net.eric_nicolas.sword.tools;

import net.eric_nicolas.sword.mechanism.*;
import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.graphics.PaintContext;
import net.eric_nicolas.sword.gadgets.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class TApp extends TShell implements Runnable {

    // Command constants
    public static final int CM_QUIT = 100;

    protected Frame frame;
    protected TDesktop desktop;
    protected Canvas canvas;
    protected BufferedImage backBuffer;
    protected Graphics2D backGraphics;
    protected boolean running;
    protected TMenu mainMenu;

    public TApp(String title, int width, int height) {
        super();
        frame = new Frame(title);
        desktop = new TDesktop(width, height);
        desktop.setApplication(this); // Set this as the application for command routing
        frame.setSize(width, height);
        frame.setResizable(false);

        canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                if (backBuffer != null) {
                    g.drawImage(backBuffer, 0, 0, null);
                }
            }
        };
        canvas.setSize(width, height);
        frame.add(canvas);

        backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        backGraphics = backBuffer.createGraphics();
        backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TEvent event = TEventAdapter.ofMousePressedEvent(e);
                if (desktop.handleEvent(event)) {
                    forceRepaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                TEvent event = TEventAdapter.ofMouseReleasedEvent(e);
                if (desktop.handleEvent(event)) {
                    forceRepaint();
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                TEvent event = TEventAdapter.ofMouseMouseEvent(e);
                if (desktop.handleEvent(event)) {
                    forceRepaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                TEvent event = TEventAdapter.ofMouseDraggedEvent(e);
                if (desktop.handleEvent(event)) {
                    forceRepaint();
                }
            }
        });

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                TKeyEvent event = TEventAdapter.ofKeyPressedEvent(e, '\0');
                // First try to handle as hotkey
                if (keyDown(event)) {
                    forceRepaint();
                    return;
                }
                // Then pass to desktop
                if (desktop.handleEvent(event)) {
                    forceRepaint();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Handle actual character input (keyboard layout aware)
                char ch = e.getKeyChar();
                if (!Character.isISOControl(ch) || ch == '\b' || ch == '\n') {
                    TEvent event = TEventAdapter.ofKeyPressedEvent(e, ch);
                    if (desktop.handleEvent(event)) {
                        forceRepaint();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                TEvent event = TEventAdapter.ofKeyReleasedEvent(e);
                desktop.handleEvent(event);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });

        running = false;

        // Create menu after initialization
        initializeMenu();
    }

    protected void initializeMenu() {
        // Create main menu (regular window, stays visible)
        mainMenu = new TMenu("Menu", 0);
        createMenuChoices(mainMenu);
        mainMenu.initChoices();
        mainMenu.insertIn(desktop);
    }

    /**
     * Override this method to create menu choices.
     * Default implementation adds just a Quit option.
     */
    protected void createMenuChoices(TMenu menu) {
        new TMenuChoice("&Quit", 0, CM_QUIT).insertIn(menu);
    }

    @Override
    protected boolean keyDown(TKeyEvent event) {
        // Process menu hotkeys
        if (mainMenu != null) {
            return processMenuHotKey(event.keyCode, mainMenu);
        }
        return false;
    }

    /**
     * Process menu hotkeys recursively.
     */
    protected boolean processMenuHotKey(int keyCode, TMenu menu) {
        TMenuChoice choice = menu.firstChoice();
        while (choice != null) {
            if (choice.getSubMenu() != null) {
                // Check submenu hotkeys
                if (processMenuHotKey(keyCode, choice.getSubMenu())) {
                    return true;
                }
            } else {
                // Check this choice's hotkey
                if (keyCode == choice.getGlobalScanCode()) {
                    // Send command
                    handleEvent(new TCmdEvent(choice.getCommand()));
                    return true;
                }
            }
            choice = choice.nextChoice();
        }
        return false;
    }

    @Override
    protected boolean command(int commandId) {
        if (commandId == CM_QUIT) {
            return doQuit();
        }
        return false;
    }

    protected boolean doQuit() {
        if (canClose()) {
            quit();
            return true;
        }
        return false;
    }

    protected boolean canClose() {
        return true;
    }

    public TDesktop getDesktop() {
        return desktop;
    }

    public void run() {
        running = true;

        // Initial draw
        forceRepaint();

        frame.setVisible(true);

        // Event loop - just keep running, actual redraws happen in forceRepaint()
        while (running) {
            try {
                Thread.sleep(50); // Just keep alive, don't redraw continuously
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void quit() {
        running = false;
        if (backGraphics != null) backGraphics.dispose();
        frame.dispose();
        System.exit(0);
    }

    public void repaint() {
        forceRepaint();
    }

    public void forceRepaint() {
        if (backBuffer != null && backGraphics != null) {
            // Create paint context and draw to back buffer
            PaintContext ctx = PaintContext.ofAWT(backGraphics);
            desktop.draw(ctx);

            // Copy back buffer to screen immediately (synchronously)
            Graphics g = canvas.getGraphics();
            if (g != null) {
                g.drawImage(backBuffer, 0, 0, null);
                Toolkit.getDefaultToolkit().sync(); // Force synchronization
                g.dispose();
            }
        }
    }
}
