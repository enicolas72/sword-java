package net.eric_nicolas.sword.ui.widgets;

public class StandardButtons {

    public static Button okButton(int x, int y) {
         return new Button(x, y, 75, 23, Dialog.CM_OK, 0, "OK");
    }

    public static Button cancelButton(int x, int y) {
        return new Button(x, y, 75, 23, Dialog.CM_CANCEL, 0, "Cancel");
    }

    public static Button noButton(int x, int y) {
        return new Button(x, y, 75, 23, Dialog.CM_NO, 0, "No");
    }

    public static Button yesButton(int x, int y) {
        return new Button(x, y, 75, 23, Dialog.CM_YES, 0, "Yes");
    }
}
