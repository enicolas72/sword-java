package net.eric_nicolas.sword.samples;

import net.eric_nicolas.sword.tools.TApp;
import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.gadgets.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Font;

/**
 * Hello - Simple Hello World application demonstrating S.W.O.R.D framework.
 */
public class Hello {

    // Command constants
    public static final int CM_HELLO_WINDOW = 10000;

    /**
     * THello - Custom zone that draws "Hello World!" text.
     */
    static class THello extends TZone {
        private Font bigFont;

        public THello(int x, int y, int width, int height) {
            super(x, y, width, height);
            bigFont = new Font("Serif", Font.BOLD, 32);
            setBackgroundColor(TColors.LIGHT_GRAY);
        }

        @Override
        protected void paint(PaintContext ctx) {
            // Draw text (relative to this zone's bounds)
            ctx.setColor(TColors.BLACK);
            ctx.setFont(bigFont);
            ctx.drawString(bounds.a.x + 20, bounds.a.y + 40, "Hello World !");
            ctx.drawString(bounds.a.x + 20, bounds.a.y + 80, "S. W. O. R. D.");
        }
    }

    /**
     * HelloApp - Main application.
     */
    static class HelloApp extends TApp {
        private int helloCounter = 1;

        public HelloApp() {
            super("Hello Sample - S.W.O.R.D", 640, 480);
        }

        @Override
        protected void createMenuChoices(TMenu menu) {
            // Add Hello Window menu item
            new TMenuChoice("&Hello Window", 0, CM_HELLO_WINDOW).insertIn(menu);
            // Add separator
            new TMenuChoice().insertIn(menu);
            // Add Quit menu item
            new TMenuChoice("&Quit", 0, CM_QUIT).insertIn(menu);
        }

        @Override
        protected boolean command(int commandId) {
            if (commandId == CM_HELLO_WINDOW) {
                return doHelloWindow();
            }
            return super.command(commandId);
        }

        protected boolean doHelloWindow() {
            String title = "Hello World #" + helloCounter + " !";

            // Create window with THello inside
            TStdWindow window = new TStdWindow(
                100 + (helloCounter - 1) * 30,
                100 + (helloCounter - 1) * 30,
                300, 200, title,
                TObject.OP_WIN_SIZEABLE | TObject.OP_WIN_CLOSEBOX
            );

            // Create THello zone inside window (with padding for title bar)
            // Coordinates are relative to parent window
            THello hello = new THello(
                2,   // 2 pixels from window's left edge
                22,  // 22 pixels from window's top edge (below title bar)
                window.getBounds().width() - 4,
                window.getBounds().height() - 24
            );
            hello.insertIn(window);

            // Insert window in desktop
            window.insertIn(getDesktop());

            helloCounter++;
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("S.W.O.R.D - Hello Sample");
        System.out.println("Copyright (C) 1993-1996 The SWORD Group");
        System.out.println("Java Port 2026");

        HelloApp app = new HelloApp();
        app.run();
    }
}
