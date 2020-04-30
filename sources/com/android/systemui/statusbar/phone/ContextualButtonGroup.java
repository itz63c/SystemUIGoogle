package com.android.systemui.statusbar.phone;

import android.view.View;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ContextualButtonGroup extends ButtonDispatcher {
    private final List<ButtonData> mButtonData = new ArrayList();

    private static final class ButtonData {
        ContextualButton button;
        boolean markedVisible = false;

        ButtonData(ContextualButton contextualButton) {
            this.button = contextualButton;
        }

        /* access modifiers changed from: 0000 */
        public void setVisibility(int i) {
            this.button.setVisibility(i);
        }
    }

    public ContextualButtonGroup(int i) {
        super(i);
    }

    public void addButton(ContextualButton contextualButton) {
        contextualButton.attachToGroup(this);
        this.mButtonData.add(new ButtonData(contextualButton));
    }

    public ContextualButton getVisibleContextButton() {
        for (int size = this.mButtonData.size() - 1; size >= 0; size--) {
            if (((ButtonData) this.mButtonData.get(size)).markedVisible) {
                return ((ButtonData) this.mButtonData.get(size)).button;
            }
        }
        return null;
    }

    public int setButtonVisibility(int i, boolean z) {
        int contextButtonIndex = getContextButtonIndex(i);
        if (contextButtonIndex != -1) {
            setVisibility(4);
            ((ButtonData) this.mButtonData.get(contextButtonIndex)).markedVisible = z;
            boolean z2 = false;
            for (int size = this.mButtonData.size() - 1; size >= 0; size--) {
                ButtonData buttonData = (ButtonData) this.mButtonData.get(size);
                if (z2 || !buttonData.markedVisible) {
                    buttonData.setVisibility(4);
                } else {
                    buttonData.setVisibility(0);
                    setVisibility(0);
                    z2 = true;
                }
            }
            return ((ButtonData) this.mButtonData.get(contextButtonIndex)).button.getVisibility();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot find the button id of ");
        sb.append(i);
        sb.append(" in context group");
        throw new RuntimeException(sb.toString());
    }

    public void updateIcons() {
        for (ButtonData buttonData : this.mButtonData) {
            buttonData.button.updateIcon();
        }
    }

    public void dump(PrintWriter printWriter) {
        View currentView = getCurrentView();
        printWriter.println("ContextualButtonGroup {");
        StringBuilder sb = new StringBuilder();
        sb.append("      getVisibleContextButton(): ");
        sb.append(getVisibleContextButton());
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("      isVisible(): ");
        sb2.append(isVisible());
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("      attached(): ");
        sb3.append(currentView != null && currentView.isAttachedToWindow());
        printWriter.println(sb3.toString());
        printWriter.println("      mButtonData [ ");
        for (int size = this.mButtonData.size() - 1; size >= 0; size--) {
            ButtonData buttonData = (ButtonData) this.mButtonData.get(size);
            View currentView2 = buttonData.button.getCurrentView();
            StringBuilder sb4 = new StringBuilder();
            sb4.append("            ");
            sb4.append(size);
            sb4.append(": markedVisible=");
            sb4.append(buttonData.markedVisible);
            sb4.append(" visible=");
            sb4.append(buttonData.button.getVisibility());
            sb4.append(" attached=");
            sb4.append(currentView2 != null && currentView2.isAttachedToWindow());
            sb4.append(" alpha=");
            sb4.append(buttonData.button.getAlpha());
            printWriter.println(sb4.toString());
        }
        printWriter.println("      ]");
        printWriter.println("    }");
    }

    private int getContextButtonIndex(int i) {
        for (int i2 = 0; i2 < this.mButtonData.size(); i2++) {
            if (((ButtonData) this.mButtonData.get(i2)).button.getId() == i) {
                return i2;
            }
        }
        return -1;
    }
}
