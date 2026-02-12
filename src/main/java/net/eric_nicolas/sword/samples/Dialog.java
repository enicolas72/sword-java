package net.eric_nicolas.sword.samples;

import net.eric_nicolas.sword.tools.TApp;
import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.gadgets.*;
import net.eric_nicolas.sword.mechanism.*;

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
        protected void createMenuChoices(TMenu menu) {
            // Add Test Dialog menu item
            new TMenuChoice("&Test Dialog", 0, CM_DIALOG_TEST).insertIn(menu);
            // Add separator
            new TMenuChoice().insertIn(menu);
            // Add Quit menu item
            new TMenuChoice("&Quit", 0, CM_QUIT).insertIn(menu);
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
            TDialog dialog = new TDialog(dX, dY, 270, 300, "Dialog sample");

            // Insert controls in the dialog
            // One "Other Button" button, disabled for interactions
            TStdButton otherBtn = new TStdButton(10, 30, 80, 23, 0, 0, TButton.BO_DISABLED, "Other");
            otherBtn.insertIn(dialog);

            // One Cancel Button
            TCancelButton cancelBtn = new TCancelButton(95, 30);
            cancelBtn.insertIn(dialog);

            // One OK button
            TOKButton okBtn = new TOKButton(180, 30);
            okBtn.insertIn(dialog);

            // One Static text
            TStaticText staticText = new TStaticText(10, 60, 250, 20, "Sample application from SWORD package");
            staticText.insertIn(dialog);

            // Check boxes
            TGroupBox checkGroup = new TGroupBox(10, 85, 250, 65);
            TCheckBox checkA = new TCheckBox(10, 15, 100, TButton.BO_NO_CASE, 1, "Check Box &A");
            checkA.insertIn(checkGroup);
            TCheckBox checkB = new TCheckBox(10, 40, 100, TButton.BO_NO_CASE, 2, "Check Box &B");
            checkB.insertIn(checkGroup);
            checkGroup.insertIn(dialog);

            // Radio buttons
            TGroupBox radioGroup = new TGroupBox(10, 160, 250, 80, "Radio buttons group");
            TRadioBox radioC = new TRadioBox(10, 25, 100, TButton.BO_NO_CASE, 1, "Radio &C");
            radioC.insertIn(radioGroup);
            TRadioBox radioD = new TRadioBox(120, 25, 100, TButton.BO_NO_CASE | TButton.BO_DISABLED, 2, "Radio &D");
            radioD.insertIn(radioGroup);
            TRadioBox radioE = new TRadioBox(10, 50, 100, TButton.BO_NO_CASE, 3, "Radio &E");
            radioE.insertIn(radioGroup);
            TRadioBox radioF = new TRadioBox(120, 50, 100, TButton.BO_NO_CASE, 4, "Radio &F");
            radioF.insertIn(radioGroup);
            radioGroup.insertIn(dialog);

            // Edit Line
            TEditLine editLine = new TEditLine(10, 265, 250, 60, 10);
            editLine.insertIn(dialog);

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
            case TDialog.CM_OK:
                System.out.println("OK");
                break;
            case TDialog.CM_CANCEL:
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
