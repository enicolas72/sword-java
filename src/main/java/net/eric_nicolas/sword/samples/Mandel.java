package net.eric_nicolas.sword.samples;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.base.*;
import net.eric_nicolas.sword.ui.events.EventMouse;
import net.eric_nicolas.sword.ui.widgets.Menu;
import net.eric_nicolas.sword.ui.widgets.MenuChoice;
import net.eric_nicolas.sword.ui.widgets.Scrollbar;
import net.eric_nicolas.sword.ui.widgets.Scroller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Mandel - Mandelbrot fractal viewer sample.
 * Port of MANDEL.CC / MANVIEW.CC from C++ S.W.O.R.D.
 *
 * Left-click zooms in 2x around the clicked point.
 * Right-click restores the previous zoom (or resets to full view if no history).
 *
 * The virtual content size grows with each zoom level (doubles each step),
 * so the scrollbar thumb shrinks and you can pan to see the rest of the
 * fractal at the current zoom level.
 */
public class Mandel {

    static final int CM_NEW_VIEWER = 10001;

    // Full Mandelbrot extent in the complex plane (zoom level 1)
    static final double WORLD_XMIN = -2.5, WORLD_XMAX = 1.0;
    static final double WORLD_YMIN = -1.25, WORLD_YMAX = 1.25;

    // -------------------------------------------------------------------
    // MandelWidget - renders one viewport-sized slice of the fractal
    // -------------------------------------------------------------------

    static class MandelWidget extends Widget {

        private static final int MAX_ITER = 128;
        private static final int MAX_HISTORY = 50;

        // Zoom state
        private int zoom    = 1;    // current zoom factor (1, 2, 4, 8, …)
        private int offsetX = 0;    // view origin in virtual pixels (X)
        private int offsetY = 0;    // view origin in virtual pixels (Y)

        // Undo history: each entry stores [zoom, offsetX, offsetY]
        private final Deque<int[]> history = new ArrayDeque<>();

        private BufferedImage rendered;

        // Called after each zoom change so the Scroller can update its range
        private Runnable onZoomChange;

        public MandelWidget(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        // ===== Public state accessors =====

        /**
         * Set by the Scroller's onScroll callback whenever the user moves
         * a scrollbar. Invalidates the cached render.
         */
        public void setOffset(int x, int y) {
            int maxX = Math.max(0, virtualW() - bounds.width());
            int maxY = Math.max(0, virtualH() - bounds.height());
            offsetX = Math.max(0, Math.min(x, maxX));
            offsetY = Math.max(0, Math.min(y, maxY));
            rendered = null;
        }

        /** Virtual content width at the current zoom level. */
        public int virtualW() { return bounds.width()  * zoom; }

        /** Virtual content height at the current zoom level. */
        public int virtualH() { return bounds.height() * zoom; }

        public int getOffsetX() { return offsetX; }
        public int getOffsetY() { return offsetY; }

        /**
         * Callback invoked after each zoom change.
         * The callback should read virtualW/H and offsetX/Y to update the Scroller.
         */
        public void setOnZoomChange(Runnable r) { this.onZoomChange = r; }

        // ===== Rendering =====

        /**
         * Render the current viewport into a BufferedImage.
         *
         * The viewport covers virtual pixels [offsetX, offsetX+viewW) in X and
         * [offsetY, offsetY+viewH) in Y. The virtual world has size virtualW × virtualH.
         * Complex-plane coordinates map linearly from [WORLD_XMIN, WORLD_XMAX] over
         * the full virtual width.
         */
        private void render() {
            int w = bounds.width();
            int h = bounds.height();
            double vw = virtualW();
            double vh = virtualH();

            // Complex-plane origin of this viewport
            double cxMin = WORLD_XMIN + offsetX * (WORLD_XMAX - WORLD_XMIN) / vw;
            double cyMin = WORLD_YMIN + offsetY * (WORLD_YMAX - WORLD_YMIN) / vh;
            // Complex units per pixel
            double pixW = (WORLD_XMAX - WORLD_XMIN) / vw;
            double pixH = (WORLD_YMAX - WORLD_YMIN) / vh;

            rendered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int py = 0; py < h; py++) {
                double ci = cyMin + py * pixH;
                for (int px = 0; px < w; px++) {
                    double cr = cxMin + px * pixW;
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
            if (rendered == null) render();
            ctx.drawImage(rendered, 0, 0);
        }

        // ===== Event handling =====

        /**
         * Left-click: zoom in 2× around the clicked point.
         *
         * Coordinates arrive as viewport-local pixels (0..viewW-1, 0..viewH-1)
         * thanks to the Scroller's event translation.
         */
        @Override
        protected boolean mouseLDown(EventMouse event) {
            if (!contains(event.where)) return false;

            // viewport-local pixel of the click
            Point absPos = getAbsolutePosition();  // (0,0) — father is null
            int px = event.where.x() - absPos.x();
            int py = event.where.y() - absPos.y();

            if (history.size() < MAX_HISTORY) {
                history.push(new int[]{zoom, offsetX, offsetY});
            }

            // Virtual pixel of the click in the current zoom space
            int vx = offsetX + px;
            int vy = offsetY + py;

            // Double the zoom: the virtual world grows 2× in each dimension.
            // In the new space the click point is at (2*vx, 2*vy).
            // Centre the viewport on that point.
            int newZoom = zoom * 2;
            int viewW   = bounds.width();
            int viewH   = bounds.height();
            int newOffX = 2 * vx - viewW / 2;
            int newOffY = 2 * vy - viewH / 2;
            newOffX = Math.max(0, Math.min(newOffX, viewW * newZoom - viewW));
            newOffY = Math.max(0, Math.min(newOffY, viewH * newZoom - viewH));

            zoom    = newZoom;
            offsetX = newOffX;
            offsetY = newOffY;
            rendered = null;
            if (onZoomChange != null) onZoomChange.run();
            return true;
        }

        /**
         * Right-click: pop zoom history, or reset to full view if empty.
         */
        @Override
        protected boolean mouseRDown(EventMouse event) {
            if (!contains(event.where)) return false;

            if (!history.isEmpty()) {
                int[] prev = history.pop();
                zoom    = prev[0];
                offsetX = prev[1];
                offsetY = prev[2];
            } else {
                // Already at base level — nothing to undo
                return false;
            }

            rendered = null;
            if (onZoomChange != null) onZoomChange.run();
            return true;
        }
    }

    // -------------------------------------------------------------------
    // MandelApp - application shell
    // -------------------------------------------------------------------

    static class MandelApp extends TApp {

        private int viewerCount = 0;

        public MandelApp() {
            super("Mandel Sample - S.W.O.R.D", 700, 540);
        }

        @Override
        protected void createMenuChoices(Menu menu) {
            Canvas mc = menu.getCanvas();
            mc.add(new MenuChoice("&New viewer", 0, CM_NEW_VIEWER));
            mc.add(new MenuChoice());
            mc.add(new MenuChoice("&Quit", 0, CM_QUIT));
        }

        @Override
        protected boolean handleCommand(int commandId) {
            if (commandId == CM_NEW_VIEWER) return doNewViewer();
            return super.handleCommand(commandId);
        }

        private boolean doNewViewer() {
            viewerCount++;
            int offset = ((viewerCount - 1) % 6) * 25;

            int winW = 460, winH = 410;
            Window win = new Window(
                    30 + offset, 50 + offset, winW, winH,
                    "Mandelbrot #" + viewerCount);

            // Available area inside the window frame + title bar
            int contentAreaW = winW - 4;   // 456
            int contentAreaH = winH - 24;  // 386

            // Viewport = content area minus the two scrollbar strips
            int viewW = contentAreaW - Scrollbar.THICKNESS;  // 440
            int viewH = contentAreaH - Scrollbar.THICKNESS;  // 370

            // MandelWidget renders exactly the viewport; its virtual world
            // starts at viewW × viewH (= no scrolling at zoom 1) and doubles
            // with each zoom step.
            MandelWidget widget = new MandelWidget(0, 0, viewW, viewH);
            Scroller scroller = new Scroller(
                    2, 22, viewW, viewH,
                    widget, viewW, viewH,   // initial virtual size = viewport (zoom 1)
                    true, true);

            // When scrollbars move: update the widget's render offset
            scroller.setOnScroll(() ->
                    widget.setOffset(scroller.getScrollX(), scroller.getScrollY()));

            // When widget zooms: expand the virtual world and reposition
            widget.setOnZoomChange(() -> {
                scroller.setContentSize(widget.virtualW(), widget.virtualH());
                scroller.setScrollPosition(widget.getOffsetX(), widget.getOffsetY());
            });

            win.getCanvas().add(scroller);
            getScreen().add(win);
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
