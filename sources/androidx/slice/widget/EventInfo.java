package androidx.slice.widget;

import com.android.systemui.plugins.DarkIconDispatcher;

public class EventInfo {
    public int actionCount = -1;
    public int actionIndex = -1;
    public int actionPosition = -1;
    public int actionType;
    public int rowIndex;
    public int rowTemplateType;
    public int sliceMode;
    public int state = -1;

    public EventInfo(int i, int i2, int i3, int i4) {
        this.sliceMode = i;
        this.actionType = i2;
        this.rowTemplateType = i3;
        this.rowIndex = i4;
    }

    public void setPosition(int i, int i2, int i3) {
        this.actionPosition = i;
        this.actionIndex = i2;
        this.actionCount = i3;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mode=");
        sb.append(SliceView.modeToString(this.sliceMode));
        sb.append(", actionType=");
        sb.append(actionToString(this.actionType));
        sb.append(", rowTemplateType=");
        sb.append(rowTypeToString(this.rowTemplateType));
        sb.append(", rowIndex=");
        sb.append(this.rowIndex);
        sb.append(", actionPosition=");
        sb.append(positionToString(this.actionPosition));
        sb.append(", actionIndex=");
        sb.append(this.actionIndex);
        sb.append(", actionCount=");
        sb.append(this.actionCount);
        sb.append(", state=");
        sb.append(this.state);
        return sb.toString();
    }

    private static String positionToString(int i) {
        if (i == 0) {
            return "START";
        }
        if (i == 1) {
            return "END";
        }
        if (i == 2) {
            return "CELL";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown position: ");
        sb.append(i);
        return sb.toString();
    }

    private static String actionToString(int i) {
        if (i == 0) {
            return "TOGGLE";
        }
        if (i == 1) {
            return "BUTTON";
        }
        if (i == 2) {
            return "SLIDER";
        }
        if (i == 3) {
            return "CONTENT";
        }
        if (i == 4) {
            return "SEE MORE";
        }
        if (i == 5) {
            return "SELECTION";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown action: ");
        sb.append(i);
        return sb.toString();
    }

    private static String rowTypeToString(int i) {
        switch (i) {
            case DarkIconDispatcher.DEFAULT_ICON_TINT /*-1*/:
                return "SHORTCUT";
            case 0:
                return "LIST";
            case 1:
                return "GRID";
            case 2:
                return "MESSAGING";
            case 3:
                return "TOGGLE";
            case 4:
                return "SLIDER";
            case 5:
                return "PROGRESS";
            case 6:
                return "SELECTION";
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("unknown row type: ");
                sb.append(i);
                return sb.toString();
        }
    }
}
