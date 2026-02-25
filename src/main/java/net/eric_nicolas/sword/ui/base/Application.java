package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.driver.LwjglDriver;
import net.eric_nicolas.sword.ui.widgets.Menu;
import net.eric_nicolas.sword.ui.widgets.MenuChoice;

/**
 * Application - Main application shell.
 * Creates the Screen and LwjglDriver (OpenGL/GLFW), registers the command
 * handler and menu hotkey handler, then delegates the event loop to the driver.
 * Subclasses override createMenuChoices() to populate the menu bar and
 * handleCommand() to respond to application-level commands.
 */
public class Application {

    public static final int CM_QUIT = 100;

    protected Screen screen;
    protected LwjglDriver driver;
    protected Menu mainMenu;

    public Application(String title, int width, int height) {
        screen = new Screen(width, height);
        screen.setCommandHandler(this::handleCommand);
        driver = new LwjglDriver(title, width, height, screen, this::processHotKey);
        initializeMenu();
    }

    private boolean processHotKey(int keyCode) {
        return mainMenu != null && mainMenu.processHotKey(keyCode);
    }

    protected void initializeMenu() {
        mainMenu = new Menu("Menu", false);
        createMenuChoices(mainMenu);
        mainMenu.initChoices();
        screen.add(mainMenu);
    }

    /**
     * Override to populate the menu bar with MenuChoice items.
     * Default: adds a single Quit entry.
     */
    protected void createMenuChoices(Menu menu) {
        menu.getCanvas().add(new MenuChoice("&Quit", 0, CM_QUIT));
    }

    /**
     * Override to handle application commands.
     * Call super.handleCommand(commandId) to keep the default CM_QUIT handling.
     */
    protected boolean handleCommand(int commandId) {
        if (commandId == CM_QUIT) return doQuit();
        return false;
    }

    protected boolean doQuit() {
        if (canClose()) {
            driver.quit();
            return true;
        }
        return false;
    }

    protected boolean canClose() {
        return true;
    }

    public Screen getScreen() {
        return screen;
    }

    public void run() {
        driver.run();
    }
}
