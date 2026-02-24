package net.eric_nicolas.sword.samples;

import net.eric_nicolas.sword.ui.base.TApp;
import net.eric_nicolas.sword.ui.widgets.*;
import net.eric_nicolas.sword.ui.base.Canvas;

/**
 * Dialog - Sample application demonstrating dialog boxes and controls.
 * Port of DIALOG.CC from C++ S.W.O.R.D
 */
public class Dialog {

    // Command constants
    public static final int CM_DIALOG_TEST = 10000;

    /**
     * DialogApp - Main application.
     */
    static class DialogApp extends TApp {
        private int dialogResult;

        public DialogApp() {
            super("Dialog Sample - S.W.O.R.D", 640, 480);
            dialogResult = 0;
        }

        @Override
        protected void createMenuChoices(Menu menu) {
            Canvas mc = menu.getCanvas();
            mc.add(new MenuChoice("&Test Dialog", 0, CM_DIALOG_TEST));
            mc.add(new MenuChoice());
            mc.add(new MenuChoice("&Quit", 0, CM_QUIT));
        }

        @Override
        protected boolean handleCommand(int commandId) {
            if (commandId == CM_DIALOG_TEST) {
                return doDialogTest();
            }
            return super.handleCommand(commandId);
        }

        protected boolean doDialogTest() {
            int dX = (640 - 296) / 2;
            int dY = (480 - 300) / 2;
            net.eric_nicolas.sword.ui.widgets.Dialog dialog =
                new net.eric_nicolas.sword.ui.widgets.Dialog(dX, dY, 296, 300, "Dialog sample");

            Canvas dc = dialog.getCanvas();

            Button otherBtn = new Button(10, 30, 80, 23, 0, 0, "Other");
            otherBtn.setEnabled(false);
            dc.add(otherBtn);

            Button cancelBtn = StandardButtons.cancelButton(95, 30);
            dc.add(cancelBtn);

            Button okBtn = StandardButtons.okButton(180, 30);
            dc.add(okBtn);

            Label staticText = new Label(10, 60, 250, 20, "Sample application from SWORD package");
            dc.add(staticText);

            GroupBox checkGroup = new GroupBox(10, 85, 250, 65);
            CheckBox checkA = new CheckBox(10, 15, 100, 1, "Check Box &A");
            checkA.setChecked(true);  // default: A checked
            checkGroup.add(checkA);
            checkGroup.add(new CheckBox(10, 40, 100, 2, "Check Box &B"));
            dc.add(checkGroup);

            GroupBox radioGroup = new GroupBox(10, 160, 250, 80, "Radio buttons group");
            radioGroup.add(new RadioBox(10,  25, 100, 1, "Radio &C"));
            RadioBox radioD = new RadioBox(120, 25, 100, 2, "Radio &D");
            radioD.setEnabled(false);
            radioGroup.add(radioD);
            radioGroup.add(new RadioBox(10,  50, 100, 3, "Radio &E"));
            radioGroup.add(new RadioBox(120, 50, 100, 4, "Radio &F"));
            radioGroup.value = 3;  // default: E selected
            dc.add(radioGroup);

            EditLine editLine = new EditLine(10, 265, 250, 60, 10);
            editLine.setText("Sample text edit");
            dc.add(editLine);

            getScreen().add(dialog);
            dialogResult = dialog.execDialog();

            return true;
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

        DialogApp app = new DialogApp();
        app.run();

        System.out.println("\n'Dialog' application finished.");
        System.out.println("You quit the dialog last time by clicking on ");
        switch (app.getDialogResult()) {
            case net.eric_nicolas.sword.ui.widgets.Dialog.CM_OK:
                System.out.println("OK");
                break;
            case net.eric_nicolas.sword.ui.widgets.Dialog.CM_CANCEL:
                System.out.println("Cancel");
                break;
            default:
                System.out.println("(closed)");
                break;
        }
    }
}
