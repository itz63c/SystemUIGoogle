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
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.WifiIconState;

public class StatusBarWifiView extends FrameLayout implements DarkReceiver, StatusIconDisplayable {
    private View mAirplaneSpacer;
    private StatusBarIconView mDotView;
    private ImageView mIn;
    private View mInoutContainer;
    private ImageView mOut;
    private View mSignalSpacer;
    private String mSlot;
    private WifiIconState mState;
    private int mVisibleState = -1;
    private LinearLayout mWifiGroup;
    private ImageView mWifiIcon;

    public static StatusBarWifiView fromContext(Context context, String str) {
        StatusBarWifiView statusBarWifiView = (StatusBarWifiView) LayoutInflater.from(context).inflate(C2013R$layout.status_bar_wifi_group, null);
        statusBarWifiView.setSlot(str);
        statusBarWifiView.init();
        statusBarWifiView.setVisibleState(0);
        return statusBarWifiView;
    }

    public StatusBarWifiView(Context context) {
        super(context);
    }

    public StatusBarWifiView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StatusBarWifiView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public StatusBarWifiView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setSlot(String str) {
        this.mSlot = str;
    }

    public void setStaticDrawableColor(int i) {
        ColorStateList valueOf = ColorStateList.valueOf(i);
        this.mWifiIcon.setImageTintList(valueOf);
        this.mIn.setImageTintList(valueOf);
        this.mOut.setImageTintList(valueOf);
        this.mDotView.setDecorColor(i);
    }

    public void setDecorColor(int i) {
        this.mDotView.setDecorColor(i);
    }

    public String getSlot() {
        return this.mSlot;
    }

    public boolean isIconVisible() {
        WifiIconState wifiIconState = this.mState;
        return wifiIconState != null && wifiIconState.visible;
    }

    public void setVisibleState(int i, boolean z) {
        if (i != this.mVisibleState) {
            this.mVisibleState = i;
            if (i == 0) {
                this.mWifiGroup.setVisibility(0);
                this.mDotView.setVisibility(8);
            } else if (i != 1) {
                this.mWifiGroup.setVisibility(8);
                this.mDotView.setVisibility(8);
            } else {
                this.mWifiGroup.setVisibility(8);
                this.mDotView.setVisibility(0);
            }
        }
    }

    public int getVisibleState() {
        return this.mVisibleState;
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
        this.mWifiGroup = (LinearLayout) findViewById(C2011R$id.wifi_group);
        this.mWifiIcon = (ImageView) findViewById(C2011R$id.wifi_signal);
        this.mIn = (ImageView) findViewById(C2011R$id.wifi_in);
        this.mOut = (ImageView) findViewById(C2011R$id.wifi_out);
        this.mSignalSpacer = findViewById(C2011R$id.wifi_signal_spacer);
        this.mAirplaneSpacer = findViewById(C2011R$id.wifi_airplane_spacer);
        this.mInoutContainer = findViewById(C2011R$id.inout_container);
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

    public void applyWifiState(WifiIconState wifiIconState) {
        boolean z = true;
        if (wifiIconState == null) {
            if (getVisibility() == 8) {
                z = false;
            }
            setVisibility(8);
            this.mState = null;
        } else {
            WifiIconState wifiIconState2 = this.mState;
            if (wifiIconState2 == null) {
                this.mState = wifiIconState.copy();
                initViewState();
            } else {
                z = !wifiIconState2.equals(wifiIconState) ? updateState(wifiIconState.copy()) : false;
            }
        }
        if (z) {
            requestLayout();
        }
    }

    private boolean updateState(WifiIconState wifiIconState) {
        setContentDescription(wifiIconState.contentDescription);
        int i = this.mState.resId;
        int i2 = wifiIconState.resId;
        if (i != i2 && i2 >= 0) {
            this.mWifiIcon.setImageDrawable(this.mContext.getDrawable(i2));
        }
        int i3 = 8;
        this.mIn.setVisibility(wifiIconState.activityIn ? 0 : 8);
        this.mOut.setVisibility(wifiIconState.activityOut ? 0 : 8);
        this.mInoutContainer.setVisibility((wifiIconState.activityIn || wifiIconState.activityOut) ? 0 : 8);
        this.mAirplaneSpacer.setVisibility(wifiIconState.airplaneSpacerVisible ? 0 : 8);
        this.mSignalSpacer.setVisibility(wifiIconState.signalSpacerVisible ? 0 : 8);
        boolean z = wifiIconState.activityIn;
        WifiIconState wifiIconState2 = this.mState;
        boolean z2 = (z == wifiIconState2.activityIn && wifiIconState.activityOut == wifiIconState2.activityOut) ? false : true;
        boolean z3 = this.mState.visible;
        boolean z4 = wifiIconState.visible;
        if (z3 != z4) {
            z2 |= true;
            if (z4) {
                i3 = 0;
            }
            setVisibility(i3);
        }
        this.mState = wifiIconState;
        return z2;
    }

    private void initViewState() {
        setContentDescription(this.mState.contentDescription);
        int i = this.mState.resId;
        if (i >= 0) {
            this.mWifiIcon.setImageDrawable(this.mContext.getDrawable(i));
        }
        int i2 = 0;
        this.mIn.setVisibility(this.mState.activityIn ? 0 : 8);
        this.mOut.setVisibility(this.mState.activityOut ? 0 : 8);
        View view = this.mInoutContainer;
        WifiIconState wifiIconState = this.mState;
        view.setVisibility((wifiIconState.activityIn || wifiIconState.activityOut) ? 0 : 8);
        this.mAirplaneSpacer.setVisibility(this.mState.airplaneSpacerVisible ? 0 : 8);
        this.mSignalSpacer.setVisibility(this.mState.signalSpacerVisible ? 0 : 8);
        if (!this.mState.visible) {
            i2 = 8;
        }
        setVisibility(i2);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        int tint = DarkIconDispatcher.getTint(rect, this, i);
        ColorStateList valueOf = ColorStateList.valueOf(tint);
        this.mWifiIcon.setImageTintList(valueOf);
        this.mIn.setImageTintList(valueOf);
        this.mOut.setImageTintList(valueOf);
        this.mDotView.setDecorColor(tint);
        this.mDotView.setIconColor(tint, false);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StatusBarWifiView(slot=");
        sb.append(this.mSlot);
        sb.append(" state=");
        sb.append(this.mState);
        sb.append(")");
        return sb.toString();
    }
}
