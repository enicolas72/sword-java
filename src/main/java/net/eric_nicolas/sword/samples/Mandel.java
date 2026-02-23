package net.eric_nicolas.sword.samples;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.base.*;
import net.eric_nicolas.sword.ui.events.EventMouse;
import net.eric_nicolas.sword.ui.widgets.Menu;
import net.eric_nicolas.sword.ui.widgets.MenuChoice;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Mandel - Mandelbrot fractal viewer sample.
 * Port of MANDEL.CC / MANVIEW.CC from C++ S.W.O.R.D.
 *
 * Left-click zooms in 2x around the clicked point.
 * Right-click restores the previous zoom (or zooms out 2x if no history).
 */
public class Mandel {

    static final int CM_NEW_VIEWER = 10001;

    // -------------------------------------------------------------------
    // MandelWidget - renders the Mandelbrot set and handles zoom gestures
    // -------------------------------------------------------------------

    static class MandelWidget extends Widget {

        private static final int MAX_ITER = 128;
        private static final int MAX_ZOOM_HISTORY = 50;

        private double xMin = -2.5, xMax = 1.0;
        private double yMin = -1.25, yMax = 1.25;

        private final Deque<double[]> zoomHistory = new ArrayDeque<>();
        private BufferedImage rendered;

        public MandelWidget(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        // Render the Mandelbrot set into a BufferedImage.
        private void render() {
            int w = bounds.width();
            int h = bounds.height();
            rendered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int py = 0; py < h; py++) {
                double ci = yMin + py * (yMax - yMin) / h;
                for (int px = 0; px < w; px++) {
                    double cr = xMin + px * (xMax - xMin) / w;

                    double zr = 0, zi = 0;
                    int iter = 0;
                    while (zr * zr + zi * zi <= 4.0 && iter < MAX_ITER) {
                        double tmp = zr * zr - zi * zi + cr;
                        zi = 2.0 * zr * zi + ci;
                        zr = tmp;
                        iter++;
                    }

                    int color = (iter == MAX_ITER)
                            ? 0x000000
                            : Color.HSBtoRGB(iter / (float) MAX_ITER, 0.8f, 1.0f);
                    rendered.setRGB(px, py, color);
                }
            }
        }

        @Override
        protected void paint(PaintContext ctx) {
            if (rendered == null) {
                render();
            }
            ctx.drawImage(rendered, 0, 0);
        }

        // Left-click: zoom in 2x around the clicked point.
        @Override
        protected boolean mouseLDown(EventMouse event) {
            if (!contains(event.where)) return false;

            Point absPos = getAbsolutePosition();
            int px = event.where.x() - absPos.x();
            int py = event.where.y() - absPos.y();

            if (zoomHistory.size() < MAX_ZOOM_HISTORY) {
                zoomHistory.push(new double[]{xMin, xMax, yMin, yMax});
            }

            double cx = xMin + px * (xMax - xMin) / bounds.width();
            double cy = yMin + py * (yMax - yMin) / bounds.height();
            double hw = (xMax - xMin) / 4.0;
            double hh = (yMax - yMin) / 4.0;
            xMin = cx - hw;  xMax = cx + hw;
            yMin = cy - hh;  yMax = cy + hh;

            rendered = null;
            return true;
        }

        // Right-click: restore previous zoom, or zoom out 2x if no history.
        @Override
        protected boolean mouseRDown(EventMouse event) {
            if (!contains(event.where)) return false;

            if (!zoomHistory.isEmpty()) {
                double[] prev = zoomHistory.pop();
                xMin = prev[0];  xMax = prev[1];
                yMin = prev[2];  yMax = prev[3];
            } else {
                double cx = (xMin + xMax) / 2.0;
                double cy = (yMin + yMax) / 2.0;
                double hw = (xMax - xMin);
                double hh = (yMax - yMin);
                xMin = cx - hw;  xMax = cx + hw;
                yMin = cy - hh;  yMax = cy + hh;
            }

            rendered = null;
            return true;
        }
    }

    // -------------------------------------------------------------------
    // MandelApp - application shell
    // -------------------------------------------------------------------

    static class MandelApp extends TApp {

        private int viewerCount = 0;

        public MandelApp() {
            super("Mandel Sample - S.W.O.R.D", 640, 480);
        }

        @Override
        protected void createMenuChoices(Menu menu) {
            Canvas mc = menu.getCanvas();
            mc.add(new MenuChoice("&New viewer", 0, CM_NEW_VIEWER));
            mc.add(new MenuChoice());
            mc.add(new MenuChoice("&Quit", 0, CM_QUIT));
        }

        @Override
        protected boolean command(int commandId) {
            if (commandId == CM_NEW_VIEWER) return doNewViewer();
            return super.command(commandId);
        }

        private boolean doNewViewer() {
            viewerCount++;
            int offset = ((viewerCount - 1) % 6) * 25;
            Window win = new Window(
                    30 + offset, 50 + offset, 400, 350,
                    "Mandelbrot #" + viewerCount);
            MandelWidget widget = new MandelWidget(
                    2, 22,
                    win.getBounds().width() - 4,
                    win.getBounds().height() - 24);
            win.getCanvas().add(widget);
            getDesktop().add(win);
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("S.W.O.R.D - Mandel Sample");
        System.out.println("Copyright (C) 1993-1996 The SWORD Group");
        System.out.println("Java Port 2026");
        new MandelApp().run();
    }
}
