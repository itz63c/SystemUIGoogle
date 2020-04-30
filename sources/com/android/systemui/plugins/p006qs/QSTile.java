package com.android.systemui.plugins.p006qs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import java.util.Objects;
import java.util.function.Supplier;

@Dependencies({@DependsOn(target = QSIconView.class), @DependsOn(target = DetailAdapter.class), @DependsOn(target = Callback.class), @DependsOn(target = Icon.class), @DependsOn(target = State.class)})
@ProvidesInterface(version = 1)
/* renamed from: com.android.systemui.plugins.qs.QSTile */
public interface QSTile {
    public static final int VERSION = 1;

    @ProvidesInterface(version = 1)
    /* renamed from: com.android.systemui.plugins.qs.QSTile$BooleanState */
    public static class BooleanState extends State {
        public static final int VERSION = 1;
        public boolean value;

        public boolean copyTo(State state) {
            BooleanState booleanState = (BooleanState) state;
            boolean z = super.copyTo(state) || booleanState.value != this.value;
            booleanState.value = this.value;
            return z;
        }

        /* access modifiers changed from: protected */
        public StringBuilder toStringBuilder() {
            StringBuilder stringBuilder = super.toStringBuilder();
            int length = stringBuilder.length() - 1;
            StringBuilder sb = new StringBuilder();
            sb.append(",value=");
            sb.append(this.value);
            stringBuilder.insert(length, sb.toString());
            return stringBuilder;
        }

        public State copy() {
            BooleanState booleanState = new BooleanState();
            copyTo(booleanState);
            return booleanState;
        }
    }

    @ProvidesInterface(version = 1)
    /* renamed from: com.android.systemui.plugins.qs.QSTile$Callback */
    public interface Callback {
        public static final int VERSION = 1;

        void onAnnouncementRequested(CharSequence charSequence);

        void onScanStateChanged(boolean z);

        void onShowDetail(boolean z);

        void onStateChanged(State state);

        void onToggleStateChanged(boolean z);
    }

    @ProvidesInterface(version = 1)
    /* renamed from: com.android.systemui.plugins.qs.QSTile$Icon */
    public static abstract class Icon {
        public static final int VERSION = 1;

        public abstract Drawable getDrawable(Context context);

        public int getPadding() {
            return 0;
        }

        public String toString() {
            return "Icon";
        }

        public Drawable getInvisibleDrawable(Context context) {
            return getDrawable(context);
        }

        public int hashCode() {
            return Icon.class.hashCode();
        }
    }

    @ProvidesInterface(version = 1)
    /* renamed from: com.android.systemui.plugins.qs.QSTile$SignalState */
    public static final class SignalState extends BooleanState {
        public static final int VERSION = 1;
        public boolean activityIn;
        public boolean activityOut;
        public boolean isOverlayIconWide;
        public int overlayIconId;

        public boolean copyTo(State state) {
            SignalState signalState = (SignalState) state;
            boolean z = (signalState.activityIn == this.activityIn && signalState.activityOut == this.activityOut && signalState.isOverlayIconWide == this.isOverlayIconWide && signalState.overlayIconId == this.overlayIconId) ? false : true;
            signalState.activityIn = this.activityIn;
            signalState.activityOut = this.activityOut;
            signalState.isOverlayIconWide = this.isOverlayIconWide;
            signalState.overlayIconId = this.overlayIconId;
            if (super.copyTo(state) || z) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public StringBuilder toStringBuilder() {
            StringBuilder stringBuilder = super.toStringBuilder();
            int length = stringBuilder.length() - 1;
            StringBuilder sb = new StringBuilder();
            sb.append(",activityIn=");
            sb.append(this.activityIn);
            stringBuilder.insert(length, sb.toString());
            int length2 = stringBuilder.length() - 1;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(",activityOut=");
            sb2.append(this.activityOut);
            stringBuilder.insert(length2, sb2.toString());
            return stringBuilder;
        }

        public State copy() {
            SignalState signalState = new SignalState();
            copyTo(signalState);
            return signalState;
        }
    }

    @ProvidesInterface(version = 2)
    /* renamed from: com.android.systemui.plugins.qs.QSTile$SlashState */
    public static class SlashState {
        public static final int VERSION = 2;
        public boolean isSlashed;
        public float rotation;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("isSlashed=");
            sb.append(this.isSlashed);
            sb.append(",rotation=");
            sb.append(this.rotation);
            return sb.toString();
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (obj == null) {
                return false;
            }
            try {
                if (((SlashState) obj).rotation == this.rotation && ((SlashState) obj).isSlashed == this.isSlashed) {
                    z = true;
                }
            } catch (ClassCastException unused) {
            }
            return z;
        }

        public SlashState copy() {
            SlashState slashState = new SlashState();
            slashState.rotation = this.rotation;
            slashState.isSlashed = this.isSlashed;
            return slashState;
        }
    }

    @ProvidesInterface(version = 1)
    /* renamed from: com.android.systemui.plugins.qs.QSTile$State */
    public static class State {
        public static final int VERSION = 1;
        public CharSequence contentDescription;
        public boolean disabledByPolicy;
        public CharSequence dualLabelContentDescription;
        public boolean dualTarget = false;
        public String expandedAccessibilityClassName;
        public boolean handlesLongClick = true;
        public Icon icon;
        public Supplier<Icon> iconSupplier;
        public boolean isTransient = false;
        public CharSequence label;
        public CharSequence secondaryLabel;
        public boolean showRippleEffect = true;
        public SlashState slash;
        public int state = 2;
        public CharSequence stateDescription;

        public boolean copyTo(State state2) {
            if (state2 == null) {
                throw new IllegalArgumentException();
            } else if (state2.getClass().equals(getClass())) {
                boolean z = !Objects.equals(state2.icon, this.icon) || !Objects.equals(state2.iconSupplier, this.iconSupplier) || !Objects.equals(state2.label, this.label) || !Objects.equals(state2.secondaryLabel, this.secondaryLabel) || !Objects.equals(state2.contentDescription, this.contentDescription) || !Objects.equals(state2.stateDescription, this.stateDescription) || !Objects.equals(state2.dualLabelContentDescription, this.dualLabelContentDescription) || !Objects.equals(state2.expandedAccessibilityClassName, this.expandedAccessibilityClassName) || !Objects.equals(Boolean.valueOf(state2.disabledByPolicy), Boolean.valueOf(this.disabledByPolicy)) || !Objects.equals(Integer.valueOf(state2.state), Integer.valueOf(this.state)) || !Objects.equals(Boolean.valueOf(state2.isTransient), Boolean.valueOf(this.isTransient)) || !Objects.equals(Boolean.valueOf(state2.dualTarget), Boolean.valueOf(this.dualTarget)) || !Objects.equals(state2.slash, this.slash) || !Objects.equals(Boolean.valueOf(state2.handlesLongClick), Boolean.valueOf(this.handlesLongClick)) || !Objects.equals(Boolean.valueOf(state2.showRippleEffect), Boolean.valueOf(this.showRippleEffect));
                state2.icon = this.icon;
                state2.iconSupplier = this.iconSupplier;
                state2.label = this.label;
                state2.secondaryLabel = this.secondaryLabel;
                state2.contentDescription = this.contentDescription;
                state2.stateDescription = this.stateDescription;
                state2.dualLabelContentDescription = this.dualLabelContentDescription;
                state2.expandedAccessibilityClassName = this.expandedAccessibilityClassName;
                state2.disabledByPolicy = this.disabledByPolicy;
                state2.state = this.state;
                state2.dualTarget = this.dualTarget;
                state2.isTransient = this.isTransient;
                SlashState slashState = this.slash;
                state2.slash = slashState != null ? slashState.copy() : null;
                state2.handlesLongClick = this.handlesLongClick;
                state2.showRippleEffect = this.showRippleEffect;
                return z;
            } else {
                throw new IllegalArgumentException();
            }
        }

        public String toString() {
            return toStringBuilder().toString();
        }

        /* access modifiers changed from: protected */
        public StringBuilder toStringBuilder() {
            StringBuilder sb = new StringBuilder(getClass().getSimpleName());
            sb.append('[');
            sb.append(",icon=");
            sb.append(this.icon);
            sb.append(",iconSupplier=");
            sb.append(this.iconSupplier);
            sb.append(",label=");
            sb.append(this.label);
            sb.append(",secondaryLabel=");
            sb.append(this.secondaryLabel);
            sb.append(",contentDescription=");
            sb.append(this.contentDescription);
            sb.append(",stateDescription=");
            sb.append(this.stateDescription);
            sb.append(",dualLabelContentDescription=");
            sb.append(this.dualLabelContentDescription);
            sb.append(",expandedAccessibilityClassName=");
            sb.append(this.expandedAccessibilityClassName);
            sb.append(",disabledByPolicy=");
            sb.append(this.disabledByPolicy);
            sb.append(",dualTarget=");
            sb.append(this.dualTarget);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(",state=");
            sb.append(this.state);
            sb.append(",slash=\"");
            sb.append(this.slash);
            sb.append("\"");
            sb.append(']');
            return sb;
        }

        public State copy() {
            State state2 = new State();
            copyTo(state2);
            return state2;
        }
    }

    void addCallback(Callback callback);

    @Deprecated
    void clearState() {
    }

    void click();

    QSIconView createTileView(Context context);

    void destroy();

    DetailAdapter getDetailAdapter();

    int getMetricsCategory();

    State getState();

    CharSequence getTileLabel();

    String getTileSpec();

    boolean isAvailable();

    void longClick();

    LogMaker populate(LogMaker logMaker) {
        return logMaker;
    }

    void refreshState();

    void removeCallback(Callback callback);

    void removeCallbacks();

    void secondaryClick();

    void setDetailListening(boolean z);

    void setListening(Object obj, boolean z);

    void setTileSpec(String str);

    boolean supportsDetailView() {
        return false;
    }

    void userSwitch(int i);
}
