package net.eric_nicolas.sword.samples;

import net.eric_nicolas.sword.ui.base.*;
import net.eric_nicolas.sword.ui.widgets.Menu;
import net.eric_nicolas.sword.ui.widgets.MenuChoice;

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
    static class THello extends Widget {
        private Font bigFont;

        public THello(int x, int y, int width, int height) {
            super(x, y, width, height);
            bigFont = new Font("Serif", Font.BOLD, 32);
            setBackgroundColor(TColors.LIGHT_GRAY);
        }

        @Override
        protected void paint(PaintContext ctx) {
            // Draw text in local coordinates
            ctx.setColor(TColors.BLACK);
            ctx.setFont(bigFont);
            ctx.drawString(20, 40, "Hello World !");
            ctx.drawString(20, 80, "S. W. O. R. D.");
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
        protected void createMenuChoices(Menu menu) {
            // Add Hello Window menu item
            Canvas mc = menu.getCanvas();
            mc.add(new MenuChoice("&Hello Window", 0, CM_HELLO_WINDOW));
            mc.add(new MenuChoice());
            mc.add(new MenuChoice("&Quit", 0, CM_QUIT));
        }

        @Override
        protected boolean handleCommand(int commandId) {
            if (commandId == CM_HELLO_WINDOW) {
                return doHelloWindow();
            }
            return super.handleCommand(commandId);
        }

        protected boolean doHelloWindow() {
            String title = "Hello World #" + helloCounter + " !";

            // Create window with THello inside
            Window window = new Window(
                100 + (helloCounter - 1) * 30,
                100 + (helloCounter - 1) * 30,
                300, 200, title
            );

            // Coordinates are relative to the window's content canvas origin
            THello hello = new THello(
                0, 0,
                window.getContentWidth(),
                window.getContentHeight()
            );
            window.getCanvas().add(hello);

            // Keep THello filling the content area when the window is resized
            window.setOnResize(() ->
                hello.setBounds(new net.eric_nicolas.sword.ui.Rect(
                    0, 0, window.getContentWidth(), window.getContentHeight())));

            // Insert window in desktop
            getScreen().add(window);

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
