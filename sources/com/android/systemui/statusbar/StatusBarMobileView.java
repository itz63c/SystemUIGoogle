package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.graph.SignalDrawable;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.DualToneHandler;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.MobileIconState;

public class StatusBarMobileView extends FrameLayout implements DarkReceiver, StatusIconDisplayable {
    private StatusBarIconView mDotView;
    private DualToneHandler mDualToneHandler;
    private ImageView mIn;
    private View mInoutContainer;
    private ImageView mMobile;
    private SignalDrawable mMobileDrawable;
    private LinearLayout mMobileGroup;
    private ImageView mMobileRoaming;
    private View mMobileRoamingSpace;
    private ImageView mMobileType;
    private ImageView mOut;
    private String mSlot;
    private MobileIconState mState;
    private int mVisibleState = -1;

    public static StatusBarMobileView fromContext(Context context, String str) {
        StatusBarMobileView statusBarMobileView = (StatusBarMobileView) LayoutInflater.from(context).inflate(C2013R$layout.status_bar_mobile_signal_group, null);
        statusBarMobileView.setSlot(str);
        statusBarMobileView.init();
        statusBarMobileView.setVisibleState(0);
        return statusBarMobileView;
    }

    public StatusBarMobileView(Context context) {
        super(context);
    }

    public StatusBarMobileView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StatusBarMobileView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public StatusBarMobileView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void getDrawingRect(Rect rect) {
        super.getDrawingRect(rect);
        float translationX = getTranslationX();
        float translationY = getTranslationY();
        rect.left = (int) (((float) rect.left) + translationX);
        rect.right = (int) (((float) rect.right) + translationX);
        rect.top = (int) (((float) rect.top) + translationY);
        rect.bottom = (int) (((float) rect.bottom) + translationY);
    }

    private void init() {
        this.mDualToneHandler = new DualToneHandler(getContext());
        this.mMobileGroup = (LinearLayout) findViewById(C2011R$id.mobile_group);
        this.mMobile = (ImageView) findViewById(C2011R$id.mobile_signal);
        this.mMobileType = (ImageView) findViewById(C2011R$id.mobile_type);
        this.mMobileRoaming = (ImageView) findViewById(C2011R$id.mobile_roaming);
        this.mMobileRoamingSpace = findViewById(C2011R$id.mobile_roaming_space);
        this.mIn = (ImageView) findViewById(C2011R$id.mobile_in);
        this.mOut = (ImageView) findViewById(C2011R$id.mobile_out);
        this.mInoutContainer = findViewById(C2011R$id.inout_container);
        SignalDrawable signalDrawable = new SignalDrawable(getContext());
        this.mMobileDrawable = signalDrawable;
        this.mMobile.setImageDrawable(signalDrawable);
        initDotView();
    }

    private void initDotView() {
        StatusBarIconView statusBarIconView = new StatusBarIconView(this.mContext, this.mSlot, null);
        this.mDotView = statusBarIconView;
        statusBarIconView.setVisibleState(1);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.status_bar_icon_size);
        LayoutParams layoutParams = new LayoutParams(dimensionPixelSize, dimensionPixelSize);
        layoutParams.gravity = 8388627;
        addView(this.mDotView, layoutParams);
    }

    public void applyMobileState(MobileIconState mobileIconState) {
        boolean z = true;
        if (mobileIconState == null) {
            if (getVisibility() == 8) {
                z = false;
            }
            setVisibility(8);
            this.mState = null;
        } else {
            MobileIconState mobileIconState2 = this.mState;
            if (mobileIconState2 == null) {
                this.mState = mobileIconState.copy();
                initViewState();
            } else {
                z = !mobileIconState2.equals(mobileIconState) ? updateState(mobileIconState.copy()) : false;
            }
        }
        if (z) {
            requestLayout();
        }
    }

    private void initViewState() {
        setContentDescription(this.mState.contentDescription);
        int i = 8;
        if (!this.mState.visible) {
            this.mMobileGroup.setVisibility(8);
        } else {
            this.mMobileGroup.setVisibility(0);
        }
        this.mMobileDrawable.setLevel(this.mState.strengthId);
        MobileIconState mobileIconState = this.mState;
        if (mobileIconState.typeId > 0) {
            this.mMobileType.setContentDescription(mobileIconState.typeContentDescription);
            this.mMobileType.setImageResource(this.mState.typeId);
            this.mMobileType.setVisibility(0);
        } else {
            this.mMobileType.setVisibility(8);
        }
        this.mMobileRoaming.setVisibility(this.mState.roaming ? 0 : 8);
        this.mMobileRoamingSpace.setVisibility(this.mState.roaming ? 0 : 8);
        this.mIn.setVisibility(this.mState.activityIn ? 0 : 8);
        this.mOut.setVisibility(this.mState.activityOut ? 0 : 8);
        View view = this.mInoutContainer;
        MobileIconState mobileIconState2 = this.mState;
        if (mobileIconState2.activityIn || mobileIconState2.activityOut) {
            i = 0;
        }
        view.setVisibility(i);
    }

    private boolean updateState(MobileIconState mobileIconState) {
        boolean z;
        setContentDescription(mobileIconState.contentDescription);
        boolean z2 = this.mState.visible;
        boolean z3 = mobileIconState.visible;
        boolean z4 = true;
        int i = 8;
        if (z2 != z3) {
            this.mMobileGroup.setVisibility(z3 ? 0 : 8);
            z = true;
        } else {
            z = false;
        }
        int i2 = this.mState.strengthId;
        int i3 = mobileIconState.strengthId;
        if (i2 != i3) {
            this.mMobileDrawable.setLevel(i3);
        }
        int i4 = this.mState.typeId;
        int i5 = mobileIconState.typeId;
        if (i4 != i5) {
            z |= i5 == 0 || i4 == 0;
            if (mobileIconState.typeId != 0) {
                this.mMobileType.setContentDescription(mobileIconState.typeContentDescription);
                this.mMobileType.setImageResource(mobileIconState.typeId);
                this.mMobileType.setVisibility(0);
            } else {
                this.mMobileType.setVisibility(8);
            }
        }
        this.mMobileRoaming.setVisibility(mobileIconState.roaming ? 0 : 8);
        this.mMobileRoamingSpace.setVisibility(mobileIconState.roaming ? 0 : 8);
        this.mIn.setVisibility(mobileIconState.activityIn ? 0 : 8);
        this.mOut.setVisibility(mobileIconState.activityOut ? 0 : 8);
        View view = this.mInoutContainer;
        if (mobileIconState.activityIn || mobileIconState.activityOut) {
            i = 0;
        }
        view.setVisibility(i);
        boolean z5 = mobileIconState.roaming;
        MobileIconState mobileIconState2 = this.mState;
        if (z5 == mobileIconState2.roaming && mobileIconState.activityIn == mobileIconState2.activityIn && mobileIconState.activityOut == mobileIconState2.activityOut) {
            z4 = false;
        }
        boolean z6 = z | z4;
        this.mState = mobileIconState;
        return z6;
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        if (!DarkIconDispatcher.isInArea(rect, this)) {
            f = 0.0f;
        }
        this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(f)));
        ColorStateList valueOf = ColorStateList.valueOf(DarkIconDispatcher.getTint(rect, this, i));
        this.mIn.setImageTintList(valueOf);
        this.mOut.setImageTintList(valueOf);
        this.mMobileType.setImageTintList(valueOf);
        this.mMobileRoaming.setImageTintList(valueOf);
        this.mDotView.setDecorColor(i);
        this.mDotView.setIconColor(i, false);
    }

    public String getSlot() {
        return this.mSlot;
    }

    public void setSlot(String str) {
        this.mSlot = str;
    }

    public void setStaticDrawableColor(int i) {
        ColorStateList valueOf = ColorStateList.valueOf(i);
        this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(i == -1 ? 0.0f : 1.0f)));
        this.mIn.setImageTintList(valueOf);
        this.mOut.setImageTintList(valueOf);
        this.mMobileType.setImageTintList(valueOf);
        this.mMobileRoaming.setImageTintList(valueOf);
        this.mDotView.setDecorColor(i);
    }

    public void setDecorColor(int i) {
        this.mDotView.setDecorColor(i);
    }

    public boolean isIconVisible() {
        return this.mState.visible;
    }

    public void setVisibleState(int i, boolean z) {
        if (i != this.mVisibleState) {
            this.mVisibleState = i;
            if (i == 0) {
                this.mMobileGroup.setVisibility(0);
                this.mDotView.setVisibility(8);
            } else if (i != 1) {
                this.mMobileGroup.setVisibility(4);
                this.mDotView.setVisibility(4);
            } else {
                this.mMobileGroup.setVisibility(4);
                this.mDotView.setVisibility(0);
            }
        }
    }

    public int getVisibleState() {
        return this.mVisibleState;
    }

    @VisibleForTesting
    public MobileIconState getState() {
        return this.mState;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StatusBarMobileView(slot=");
        sb.append(this.mSlot);
        sb.append(" state=");
        sb.append(this.mState);
        sb.append(")");
        return sb.toString();
    }
}
