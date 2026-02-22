package net.eric_nicolas.sword.samples;

import net.eric_nicolas.sword.tools.TApp;
import net.eric_nicolas.sword.gadgets.*;
import net.eric_nicolas.sword.graphics.Canvas;

/**
 * Dialog - Sample application demonstrating dialog boxes and controls.
 * Port of DIALOG.CC from C++ S.W.O.R.D
 */
public class Dialog {

    // Command constants
    public static final int CM_DIALOG_TEST = 10000;

    /**
     * Dialog data structure for data exchange.
     */
    static class DialogData {
        public int checks;      // Bitmask for checkboxes
        public int radios;      // Value for radio buttons
        public String string;   // Text from edit line

        public DialogData() {
            checks = 1;  // Check box A is checked
            radios = 3;  // Radio button E is active
            string = "Sample text edit";
        }
    }

    /**
     * DialogApp - Main application.
     */
    static class DialogApp extends TApp {
        private DialogData dialogData;
        private int dialogResult;

        public DialogApp() {
            super("Dialog Sample - S.W.O.R.D", 640, 480);
            dialogData = new DialogData();
            dialogResult = 0;
        }

        @Override
        protected void createMenuChoices(Menu menu) {
            // Add Test Dialog menu item
            Canvas mc = menu.getCanvas();
            mc.add(new MenuChoice("&Test Dialog", 0, CM_DIALOG_TEST));
            mc.add(new MenuChoice());
            mc.add(new MenuChoice("&Quit", 0, CM_QUIT));
        }

        @Override
        protected boolean command(int commandId) {
            if (commandId == CM_DIALOG_TEST) {
                return doDialogTest();
            }
            return super.command(commandId);
        }

        protected boolean doDialogTest() {
            // Build TDialog object
            // Size is 270x300, compute position to center it on the screen
            int dX = (640 - 270) / 2;
            int dY = (480 - 300) / 2;
            net.eric_nicolas.sword.gadgets.Dialog dialog = new net.eric_nicolas.sword.gadgets.Dialog(dX, dY, 270, 300, "Dialog sample");

            // Insert controls in the dialog
            // One "Other Button" button, disabled for interactions
            Canvas dc = dialog.getCanvas();

            Button otherBtn = new Button(10, 30, 80, 23, 0, 0, AbstractButton.BO_DISABLED, "Other");
            dc.add(otherBtn);

            // One Cancel Button
            Button cancelBtn = StandardButtons.cancelButton(95, 30);
            dc.add(cancelBtn);

            // One OK button
            Button okBtn = StandardButtons.okButton(180, 30);
            dc.add(okBtn);

            // One Static text
            Label staticText = new Label(10, 60, 250, 20, "Sample application from SWORD package");
            dc.add(staticText);

            // Check boxes
            GroupBox checkGroup = new GroupBox(10, 85, 250, 65);
            CheckBox checkA = new CheckBox(10, 15, 100, AbstractButton.BO_NO_CASE, 1, "Check Box &A");
            checkGroup.add(checkA);
            CheckBox checkB = new CheckBox(10, 40, 100, AbstractButton.BO_NO_CASE, 2, "Check Box &B");
            checkGroup.add(checkB);
            dc.add(checkGroup);

            // Radio buttons
            GroupBox radioGroup = new GroupBox(10, 160, 250, 80, "Radio buttons group");
            RadioBox radioC = new RadioBox(10, 25, 100, AbstractButton.BO_NO_CASE, 1, "Radio &C");
            radioGroup.add(radioC);
            RadioBox radioD = new RadioBox(120, 25, 100, AbstractButton.BO_NO_CASE | AbstractButton.BO_DISABLED, 2, "Radio &D");
            radioGroup.add(radioD);
            RadioBox radioE = new RadioBox(10, 50, 100, AbstractButton.BO_NO_CASE, 3, "Radio &E");
            radioGroup.add(radioE);
            RadioBox radioF = new RadioBox(120, 50, 100, AbstractButton.BO_NO_CASE, 4, "Radio &F");
            radioGroup.add(radioF);
            dc.add(radioGroup);

            // Edit Line
            EditLine editLine = new EditLine(10, 265, 250, 60, 10);
            dc.add(editLine);

            // Insert dialog in desktop
            dialog.insertIn(getDesktop());

            // Execute the dialog modally
            dialogResult = dialog.execDialog(dialogData);

            return true;
        }

        public DialogData getDialogData() {
            return dialogData;
        }

        public int getDialogResult() {
            return dialogResult;
        }
    }

    public static void main(String[] args) {
        System.out.println("S.W.O.R.D - Dialog Sample");
        System.out.println("Copyright (C) 1993-1996 The SWORD Group");
        System.out.println("Java Port 2026");
        System.out.println();

        // Execute Application
        DialogApp app = new DialogApp();
        app.run();

        // Print results after the application finishes
        System.out.println("\n'Dialog' application finished.");
        System.out.println("You quit the dialog last time by clicking on ");
        switch (app.getDialogResult()) {
            case net.eric_nicolas.sword.gadgets.Dialog.CM_OK:
                System.out.println("OK");
                break;
            case net.eric_nicolas.sword.gadgets.Dialog.CM_CANCEL:
                System.out.println("Cancel");
                break;
            default:
                System.out.println("(closed)");
                break;
        }

        DialogData data = app.getDialogData();
        System.out.print("Check box selected    : ");
        if ((data.checks & 1) != 0) System.out.print("A ");
        if ((data.checks & 2) != 0) System.out.print("B ");
        System.out.println();

        System.out.print("Radio button selected : ");
        switch (data.radios) {
            case 1: System.out.println("C"); break;
            case 2: System.out.println("D"); break;
            case 3: System.out.println("E"); break;
            case 4: System.out.println("F"); break;
            default: System.out.println("(none)"); break;
        }

        System.out.println("Line typed            : " + data.string);
    }
}
