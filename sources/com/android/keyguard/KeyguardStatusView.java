package com.android.keyguard;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.shared.system.SurfaceViewRequestReceiver;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.TimeZone;

public class KeyguardStatusView extends GridLayout implements ConfigurationListener {
    private KeyguardClockSwitch mClockView;
    private float mDarkAmount;
    private Handler mHandler;
    private final IActivityManager mIActivityManager;
    private int mIconTopMargin;
    private int mIconTopMarginWithHeader;
    private KeyguardUpdateMonitorCallback mInfoCallback;
    private KeyguardSliceView mKeyguardSlice;
    private final LockPatternUtils mLockPatternUtils;
    private TextView mLogoutView;
    private View mNotificationIcons;
    private TextView mOwnerInfo;
    private Runnable mPendingMarqueeStart;
    private boolean mPulsing;
    private boolean mShowingHeader;
    private int mTextColor;
    private final BroadcastReceiver mUniversalSmartspaceBroadcastReceiver;

    private static final class Patterns {
        static String cacheKey;
        static String clockView12;
        static String clockView24;

        static void update(Context context) {
            Locale locale = Locale.getDefault();
            Resources resources = context.getResources();
            String string = resources.getString(C2017R$string.clock_12hr_format);
            String string2 = resources.getString(C2017R$string.clock_24hr_format);
            StringBuilder sb = new StringBuilder();
            sb.append(locale.toString());
            sb.append(string);
            sb.append(string2);
            String sb2 = sb.toString();
            if (!sb2.equals(cacheKey)) {
                clockView12 = DateFormat.getBestDateTimePattern(locale, string);
                String str = "a";
                if (!string.contains(str)) {
                    clockView12 = clockView12.replaceAll(str, "").trim();
                }
                String bestDateTimePattern = DateFormat.getBestDateTimePattern(locale, string2);
                clockView24 = bestDateTimePattern;
                clockView24 = bestDateTimePattern.replace(':', 60929);
                clockView12 = clockView12.replace(':', 60929);
                cacheKey = sb2;
            }
        }
    }

    public KeyguardStatusView(Context context) {
        this(context, null, 0);
    }

    public KeyguardStatusView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardStatusView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDarkAmount = 0.0f;
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            public void onTimeChanged() {
                KeyguardStatusView.this.refreshTime();
            }

            public void onTimeZoneChanged(TimeZone timeZone) {
                KeyguardStatusView.this.updateTimeZone(timeZone);
            }

            public void onKeyguardVisibilityChanged(boolean z) {
                if (z) {
                    KeyguardStatusView.this.refreshTime();
                    KeyguardStatusView.this.updateOwnerInfo();
                    KeyguardStatusView.this.updateLogoutView();
                }
            }

            public void onStartedWakingUp() {
                KeyguardStatusView.this.setEnableMarquee(true);
            }

            public void onFinishedGoingToSleep(int i) {
                KeyguardStatusView.this.setEnableMarquee(false);
            }

            public void onUserSwitchComplete(int i) {
                KeyguardStatusView.this.refreshFormat();
                KeyguardStatusView.this.updateOwnerInfo();
                KeyguardStatusView.this.updateLogoutView();
            }

            public void onLogoutEnabledChanged() {
                KeyguardStatusView.this.updateLogoutView();
            }
        };
        this.mUniversalSmartspaceBroadcastReceiver = new BroadcastReceiver() {
            private final SurfaceViewRequestReceiver mReceiver = new SurfaceViewRequestReceiver();

            public void onReceive(Context context, Intent intent) {
                if ("com.android.systemui.REQUEST_SMARTSPACE_VIEW".equals(intent.getAction())) {
                    this.mReceiver.onReceive(context, intent.getBundleExtra("bundle_key"), View.inflate(KeyguardStatusView.this.mContext, C2013R$layout.keyguard_status_area, null));
                }
            }
        };
        this.mIActivityManager = ActivityManager.getService();
        this.mLockPatternUtils = new LockPatternUtils(getContext());
        this.mHandler = new Handler();
        onDensityOrFontScaleChanged();
    }

    public boolean hasCustomClock() {
        return this.mClockView.hasCustomClock();
    }

    public void setHasVisibleNotifications(boolean z) {
        this.mClockView.setHasVisibleNotifications(z);
    }

    /* access modifiers changed from: private */
    public void setEnableMarquee(boolean z) {
        if (!z) {
            Runnable runnable = this.mPendingMarqueeStart;
            if (runnable != null) {
                this.mHandler.removeCallbacks(runnable);
                this.mPendingMarqueeStart = null;
            }
            setEnableMarqueeImpl(false);
        } else if (this.mPendingMarqueeStart == null) {
            $$Lambda$KeyguardStatusView$ps9yj97ShIVR2u2hJB8SKuKkkQ r3 = new Runnable() {
                public final void run() {
                    KeyguardStatusView.this.lambda$setEnableMarquee$0$KeyguardStatusView();
                }
            };
            this.mPendingMarqueeStart = r3;
            this.mHandler.postDelayed(r3, 2000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setEnableMarquee$0 */
    public /* synthetic */ void lambda$setEnableMarquee$0$KeyguardStatusView() {
        setEnableMarqueeImpl(true);
        this.mPendingMarqueeStart = null;
    }

    private void setEnableMarqueeImpl(boolean z) {
        TextView textView = this.mOwnerInfo;
        if (textView != null) {
            textView.setSelected(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        LinearLayout linearLayout = (LinearLayout) findViewById(C2011R$id.status_view_container);
        this.mLogoutView = (TextView) findViewById(C2011R$id.logout);
        this.mNotificationIcons = findViewById(C2011R$id.clock_notification_icon_container);
        TextView textView = this.mLogoutView;
        if (textView != null) {
            textView.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    KeyguardStatusView.this.onLogoutClicked(view);
                }
            });
        }
        KeyguardClockSwitch keyguardClockSwitch = (KeyguardClockSwitch) findViewById(C2011R$id.keyguard_clock_container);
        this.mClockView = keyguardClockSwitch;
        keyguardClockSwitch.setShowCurrentUserTime(true);
        if (KeyguardClockAccessibilityDelegate.isNeeded(this.mContext)) {
            this.mClockView.setAccessibilityDelegate(new KeyguardClockAccessibilityDelegate(this.mContext));
        }
        this.mOwnerInfo = (TextView) findViewById(C2011R$id.owner_info);
        this.mKeyguardSlice = (KeyguardSliceView) findViewById(C2011R$id.keyguard_status_area);
        this.mTextColor = this.mClockView.getCurrentTextColor();
        this.mKeyguardSlice.setContentChangeListener(new Runnable() {
            public final void run() {
                KeyguardStatusView.this.onSliceContentChanged();
            }
        });
        onSliceContentChanged();
        setEnableMarquee(((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isDeviceInteractive());
        refreshFormat();
        updateOwnerInfo();
        updateLogoutView();
        updateDark();
    }

    /* access modifiers changed from: private */
    public void onSliceContentChanged() {
        boolean hasHeader = this.mKeyguardSlice.hasHeader();
        this.mClockView.setKeyguardShowingHeader(hasHeader);
        if (this.mShowingHeader != hasHeader) {
            this.mShowingHeader = hasHeader;
            View view = this.mNotificationIcons;
            if (view != null) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                marginLayoutParams.setMargins(marginLayoutParams.leftMargin, hasHeader ? this.mIconTopMarginWithHeader : this.mIconTopMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
                this.mNotificationIcons.setLayoutParams(marginLayoutParams);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        layoutOwnerInfo();
    }

    public void onDensityOrFontScaleChanged() {
        KeyguardClockSwitch keyguardClockSwitch = this.mClockView;
        if (keyguardClockSwitch != null) {
            keyguardClockSwitch.setTextSize(0, (float) getResources().getDimensionPixelSize(C2009R$dimen.widget_big_font_size));
        }
        TextView textView = this.mOwnerInfo;
        if (textView != null) {
            textView.setTextSize(0, (float) getResources().getDimensionPixelSize(C2009R$dimen.widget_label_font_size));
        }
        loadBottomMargin();
    }

    public void dozeTimeTick() {
        refreshTime();
        this.mKeyguardSlice.refresh();
    }

    /* access modifiers changed from: private */
    public void refreshTime() {
        this.mClockView.refresh();
    }

    /* access modifiers changed from: private */
    public void updateTimeZone(TimeZone timeZone) {
        this.mClockView.onTimeZoneChanged(timeZone);
    }

    /* access modifiers changed from: private */
    public void refreshFormat() {
        Patterns.update(this.mContext);
        this.mClockView.setFormat12Hour(Patterns.clockView12);
        this.mClockView.setFormat24Hour(Patterns.clockView24);
    }

    public int getLogoutButtonHeight() {
        TextView textView = this.mLogoutView;
        int i = 0;
        if (textView == null) {
            return 0;
        }
        if (textView.getVisibility() == 0) {
            i = this.mLogoutView.getHeight();
        }
        return i;
    }

    public float getClockTextSize() {
        return this.mClockView.getTextSize();
    }

    public int getClockPreferredY(int i) {
        return this.mClockView.getPreferredY(i);
    }

    /* access modifiers changed from: private */
    public void updateLogoutView() {
        TextView textView = this.mLogoutView;
        if (textView != null) {
            textView.setVisibility(shouldShowLogout() ? 0 : 8);
            this.mLogoutView.setText(this.mContext.getResources().getString(17040140));
        }
    }

    /* access modifiers changed from: private */
    public void updateOwnerInfo() {
        if (this.mOwnerInfo != null) {
            String deviceOwnerInfo = this.mLockPatternUtils.getDeviceOwnerInfo();
            if (deviceOwnerInfo == null && this.mLockPatternUtils.isOwnerInfoEnabled(KeyguardUpdateMonitor.getCurrentUser())) {
                deviceOwnerInfo = this.mLockPatternUtils.getOwnerInfo(KeyguardUpdateMonitor.getCurrentUser());
            }
            this.mOwnerInfo.setText(deviceOwnerInfo);
            updateDark();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mInfoCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        getContext().registerReceiver(this.mUniversalSmartspaceBroadcastReceiver, new IntentFilter("com.android.systemui.REQUEST_SMARTSPACE_VIEW"));
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).removeCallback(this.mInfoCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
        getContext().unregisterReceiver(this.mUniversalSmartspaceBroadcastReceiver);
    }

    public void onLocaleListChanged() {
        refreshFormat();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Object obj;
        printWriter.println("KeyguardStatusView:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mOwnerInfo: ");
        TextView textView = this.mOwnerInfo;
        boolean z = true;
        if (textView == null) {
            obj = "null";
        } else {
            obj = Boolean.valueOf(textView.getVisibility() == 0);
        }
        sb.append(obj);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mPulsing: ");
        sb2.append(this.mPulsing);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mDarkAmount: ");
        sb3.append(this.mDarkAmount);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mTextColor: ");
        sb4.append(Integer.toHexString(this.mTextColor));
        printWriter.println(sb4.toString());
        if (this.mLogoutView != null) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("  logout visible: ");
            if (this.mLogoutView.getVisibility() != 0) {
                z = false;
            }
            sb5.append(z);
            printWriter.println(sb5.toString());
        }
        KeyguardClockSwitch keyguardClockSwitch = this.mClockView;
        if (keyguardClockSwitch != null) {
            keyguardClockSwitch.dump(fileDescriptor, printWriter, strArr);
        }
        KeyguardSliceView keyguardSliceView = this.mKeyguardSlice;
        if (keyguardSliceView != null) {
            keyguardSliceView.dump(fileDescriptor, printWriter, strArr);
        }
    }

    private void loadBottomMargin() {
        this.mIconTopMargin = getResources().getDimensionPixelSize(C2009R$dimen.widget_vertical_padding);
        this.mIconTopMarginWithHeader = getResources().getDimensionPixelSize(C2009R$dimen.widget_vertical_padding_with_header);
    }

    public void setDarkAmount(float f) {
        if (this.mDarkAmount != f) {
            this.mDarkAmount = f;
            this.mClockView.setDarkAmount(f);
            updateDark();
        }
    }

    private void updateDark() {
        float f = 1.0f;
        int i = 0;
        boolean z = this.mDarkAmount == 1.0f;
        TextView textView = this.mLogoutView;
        if (textView != null) {
            if (z) {
                f = 0.0f;
            }
            textView.setAlpha(f);
        }
        TextView textView2 = this.mOwnerInfo;
        if (textView2 != null) {
            boolean z2 = !TextUtils.isEmpty(textView2.getText());
            TextView textView3 = this.mOwnerInfo;
            if (!z2) {
                i = 8;
            }
            textView3.setVisibility(i);
            layoutOwnerInfo();
        }
        int blendARGB = ColorUtils.blendARGB(this.mTextColor, -1, this.mDarkAmount);
        this.mKeyguardSlice.setDarkAmount(this.mDarkAmount);
        this.mClockView.setTextColor(blendARGB);
    }

    private void layoutOwnerInfo() {
        TextView textView = this.mOwnerInfo;
        if (textView == null || textView.getVisibility() == 8) {
            View view = this.mNotificationIcons;
            if (view != null) {
                view.setScrollY(0);
                return;
            }
            return;
        }
        this.mOwnerInfo.setAlpha(1.0f - this.mDarkAmount);
        int bottom = (int) (((float) ((this.mOwnerInfo.getBottom() + this.mOwnerInfo.getPaddingBottom()) - (this.mOwnerInfo.getTop() - this.mOwnerInfo.getPaddingTop()))) * this.mDarkAmount);
        setBottom(getMeasuredHeight() - bottom);
        View view2 = this.mNotificationIcons;
        if (view2 != null) {
            view2.setScrollY(bottom);
        }
    }

    public void setPulsing(boolean z) {
        if (this.mPulsing != z) {
            this.mPulsing = z;
        }
    }

    private boolean shouldShowLogout() {
        return ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isLogoutEnabled() && KeyguardUpdateMonitor.getCurrentUser() != 0;
    }

    /* access modifiers changed from: private */
    public void onLogoutClicked(View view) {
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        try {
            this.mIActivityManager.switchUser(0);
            this.mIActivityManager.stopUser(currentUser, true, null);
        } catch (RemoteException e) {
            Log.e("KeyguardStatusView", "Failed to logout user", e);
        }
    }
}
