package com.android.systemui.statusbar.phone;

import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.DemoMode;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.StatusBarMobileView;
import com.android.systemui.statusbar.StatusBarWifiView;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.MobileIconState;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.WifiIconState;
import java.util.ArrayList;
import java.util.Iterator;

public class DemoStatusIcons extends StatusIconContainer implements DemoMode, DarkReceiver {
    private int mColor;
    private boolean mDemoMode;
    private final int mIconSize;
    private final ArrayList<StatusBarMobileView> mMobileViews = new ArrayList<>();
    private final LinearLayout mStatusIcons;
    private StatusBarWifiView mWifiView;

    public DemoStatusIcons(LinearLayout linearLayout, int i) {
        super(linearLayout.getContext());
        this.mStatusIcons = linearLayout;
        this.mIconSize = i;
        this.mColor = -1;
        if (linearLayout instanceof StatusIconContainer) {
            setShouldRestrictIcons(((StatusIconContainer) linearLayout).isRestrictingIcons());
        } else {
            setShouldRestrictIcons(false);
        }
        setLayoutParams(this.mStatusIcons.getLayoutParams());
        setPadding(this.mStatusIcons.getPaddingLeft(), this.mStatusIcons.getPaddingTop(), this.mStatusIcons.getPaddingRight(), this.mStatusIcons.getPaddingBottom());
        setOrientation(this.mStatusIcons.getOrientation());
        setGravity(16);
        ViewGroup viewGroup = (ViewGroup) this.mStatusIcons.getParent();
        viewGroup.addView(this, viewGroup.indexOfChild(this.mStatusIcons));
    }

    public void remove() {
        this.mMobileViews.clear();
        ((ViewGroup) getParent()).removeView(this);
    }

    public void setColor(int i) {
        this.mColor = i;
        updateColors();
    }

    private void updateColors() {
        for (int i = 0; i < getChildCount(); i++) {
            StatusIconDisplayable statusIconDisplayable = (StatusIconDisplayable) getChildAt(i);
            statusIconDisplayable.setStaticDrawableColor(this.mColor);
            statusIconDisplayable.setDecorColor(this.mColor);
        }
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        int i = 0;
        if (!this.mDemoMode && str.equals("enter")) {
            this.mDemoMode = true;
            this.mStatusIcons.setVisibility(8);
            setVisibility(0);
        } else if (this.mDemoMode && str.equals("exit")) {
            this.mDemoMode = false;
            this.mStatusIcons.setVisibility(0);
            setVisibility(8);
        } else if (this.mDemoMode && str.equals("status")) {
            String str2 = "volume";
            String string = bundle.getString(str2);
            if (string != null) {
                updateSlot(str2, null, string.equals("vibrate") ? C2010R$drawable.stat_sys_ringer_vibrate : 0);
            }
            String str3 = "zen";
            String string2 = bundle.getString(str3);
            if (string2 != null) {
                updateSlot(str3, null, string2.equals("dnd") ? C2010R$drawable.stat_sys_dnd : 0);
            }
            String str4 = "bluetooth";
            String string3 = bundle.getString(str4);
            if (string3 != null) {
                updateSlot(str4, null, string3.equals("connected") ? C2010R$drawable.stat_sys_data_bluetooth_connected : 0);
            }
            String str5 = "location";
            String string4 = bundle.getString(str5);
            String str6 = "show";
            if (string4 != null) {
                updateSlot(str5, null, string4.equals(str6) ? 17303121 : 0);
            }
            String string5 = bundle.getString("alarm");
            if (string5 != null) {
                updateSlot("alarm_clock", null, string5.equals(str6) ? C2010R$drawable.stat_sys_alarm : 0);
            }
            String str7 = "tty";
            String string6 = bundle.getString(str7);
            if (string6 != null) {
                updateSlot(str7, null, string6.equals(str6) ? C2010R$drawable.stat_sys_tty_mode : 0);
            }
            String str8 = "mute";
            String string7 = bundle.getString(str8);
            if (string7 != null) {
                updateSlot(str8, null, string7.equals(str6) ? 17301622 : 0);
            }
            String str9 = "speakerphone";
            String string8 = bundle.getString(str9);
            if (string8 != null) {
                updateSlot(str9, null, string8.equals(str6) ? 17301639 : 0);
            }
            String str10 = "cast";
            String string9 = bundle.getString(str10);
            if (string9 != null) {
                updateSlot(str10, null, string9.equals(str6) ? C2010R$drawable.stat_sys_cast : 0);
            }
            String str11 = "hotspot";
            String string10 = bundle.getString(str11);
            if (string10 != null) {
                if (string10.equals(str6)) {
                    i = C2010R$drawable.stat_sys_hotspot;
                }
                updateSlot(str11, null, i);
            }
        }
    }

    private void updateSlot(String str, String str2, int i) {
        if (this.mDemoMode) {
            if (str2 == null) {
                str2 = this.mContext.getPackageName();
            }
            String str3 = str2;
            int i2 = 0;
            while (true) {
                if (i2 >= getChildCount()) {
                    i2 = -1;
                    break;
                }
                View childAt = getChildAt(i2);
                if (childAt instanceof StatusBarIconView) {
                    StatusBarIconView statusBarIconView = (StatusBarIconView) childAt;
                    if (str.equals(statusBarIconView.getTag())) {
                        if (i != 0) {
                            StatusBarIcon statusBarIcon = statusBarIconView.getStatusBarIcon();
                            statusBarIcon.visible = true;
                            statusBarIcon.icon = Icon.createWithResource(statusBarIcon.icon.getResPackage(), i);
                            statusBarIconView.set(statusBarIcon);
                            statusBarIconView.updateDrawable();
                            return;
                        }
                    }
                }
                i2++;
            }
            if (i == 0) {
                if (i2 != -1) {
                    removeViewAt(i2);
                }
                return;
            }
            StatusBarIcon statusBarIcon2 = new StatusBarIcon(str3, UserHandle.SYSTEM, i, 0, 0, "Demo");
            statusBarIcon2.visible = true;
            StatusBarIconView statusBarIconView2 = new StatusBarIconView(getContext(), str, null, false);
            statusBarIconView2.setTag(str);
            statusBarIconView2.set(statusBarIcon2);
            statusBarIconView2.setStaticDrawableColor(this.mColor);
            statusBarIconView2.setDecorColor(this.mColor);
            addView(statusBarIconView2, 0, createLayoutParams());
        }
    }

    public void addDemoWifiView(WifiIconState wifiIconState) {
        Log.d("DemoStatusIcons", "addDemoWifiView: ");
        StatusBarWifiView fromContext = StatusBarWifiView.fromContext(this.mContext, wifiIconState.slot);
        int childCount = getChildCount();
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            } else if (getChildAt(i) instanceof StatusBarMobileView) {
                childCount = i;
                break;
            } else {
                i++;
            }
        }
        this.mWifiView = fromContext;
        fromContext.applyWifiState(wifiIconState);
        this.mWifiView.setStaticDrawableColor(this.mColor);
        addView(fromContext, childCount, createLayoutParams());
    }

    public void updateWifiState(WifiIconState wifiIconState) {
        Log.d("DemoStatusIcons", "updateWifiState: ");
        StatusBarWifiView statusBarWifiView = this.mWifiView;
        if (statusBarWifiView == null) {
            addDemoWifiView(wifiIconState);
        } else {
            statusBarWifiView.applyWifiState(wifiIconState);
        }
    }

    public void addMobileView(MobileIconState mobileIconState) {
        Log.d("DemoStatusIcons", "addMobileView: ");
        StatusBarMobileView fromContext = StatusBarMobileView.fromContext(this.mContext, mobileIconState.slot);
        fromContext.applyMobileState(mobileIconState);
        fromContext.setStaticDrawableColor(this.mColor);
        this.mMobileViews.add(fromContext);
        addView(fromContext, getChildCount(), createLayoutParams());
    }

    public void updateMobileState(MobileIconState mobileIconState) {
        Log.d("DemoStatusIcons", "updateMobileState: ");
        for (int i = 0; i < this.mMobileViews.size(); i++) {
            StatusBarMobileView statusBarMobileView = (StatusBarMobileView) this.mMobileViews.get(i);
            if (statusBarMobileView.getState().subId == mobileIconState.subId) {
                statusBarMobileView.applyMobileState(mobileIconState);
                return;
            }
        }
        addMobileView(mobileIconState);
    }

    public void onRemoveIcon(StatusIconDisplayable statusIconDisplayable) {
        if (statusIconDisplayable.getSlot().equals("wifi")) {
            removeView(this.mWifiView);
            this.mWifiView = null;
            return;
        }
        StatusBarMobileView matchingMobileView = matchingMobileView(statusIconDisplayable);
        if (matchingMobileView != null) {
            removeView(matchingMobileView);
            this.mMobileViews.remove(matchingMobileView);
        }
    }

    private StatusBarMobileView matchingMobileView(StatusIconDisplayable statusIconDisplayable) {
        if (!(statusIconDisplayable instanceof StatusBarMobileView)) {
            return null;
        }
        StatusBarMobileView statusBarMobileView = (StatusBarMobileView) statusIconDisplayable;
        Iterator it = this.mMobileViews.iterator();
        while (it.hasNext()) {
            StatusBarMobileView statusBarMobileView2 = (StatusBarMobileView) it.next();
            if (statusBarMobileView2.getState().subId == statusBarMobileView.getState().subId) {
                return statusBarMobileView2;
            }
        }
        return null;
    }

    private LayoutParams createLayoutParams() {
        return new LayoutParams(-2, this.mIconSize);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        setColor(DarkIconDispatcher.getTint(rect, this.mStatusIcons, i));
        StatusBarWifiView statusBarWifiView = this.mWifiView;
        if (statusBarWifiView != null) {
            statusBarWifiView.onDarkChanged(rect, f, i);
        }
        Iterator it = this.mMobileViews.iterator();
        while (it.hasNext()) {
            ((StatusBarMobileView) it.next()).onDarkChanged(rect, f, i);
        }
    }
}
