package com.android.systemui.p007qs;

import android.app.ActivityManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.service.notification.ZenModeConfig;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.DisplayCutout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowInsets;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2018R$style;
import com.android.systemui.DualToneHandler;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.TouchAnimator.Builder;
import com.android.systemui.p007qs.carrier.QSCarrierGroup;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarIconController.TintedIconManager;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import com.android.systemui.statusbar.phone.StatusIconContainer;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.statusbar.policy.DateView;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmController.NextAlarmChangeCallback;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeController.Callback;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/* renamed from: com.android.systemui.qs.QuickStatusBarHeader */
public class QuickStatusBarHeader extends RelativeLayout implements OnClickListener, NextAlarmChangeCallback, Callback {
    private final ActivityStarter mActivityStarter;
    private final NextAlarmController mAlarmController;
    private BatteryMeterView mBatteryRemainingIcon;
    private BroadcastDispatcher mBroadcastDispatcher;
    private Clock mClockView;
    private final CommandQueue mCommandQueue;
    private DualToneHandler mDualToneHandler;
    private boolean mExpanded;
    protected QuickQSPanel mHeaderQsPanel;
    private TouchAnimator mHeaderTextContainerAlphaAnimator;
    private View mHeaderTextContainerView;
    private TintedIconManager mIconManager;
    private boolean mListening;
    private AlarmClockInfo mNextAlarm;
    private View mNextAlarmContainer;
    private ImageView mNextAlarmIcon;
    private TextView mNextAlarmTextView;
    private boolean mQsDisabled;
    private QSPanel mQsPanel;
    private View mQuickQsStatusIcons;
    private View mRingerContainer;
    /* access modifiers changed from: private */
    public int mRingerMode = 2;
    private ImageView mRingerModeIcon;
    private TextView mRingerModeTextView;
    private final BroadcastReceiver mRingerReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            QuickStatusBarHeader.this.mRingerMode = intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
            QuickStatusBarHeader.this.updateStatusText();
        }
    };
    private int mRoundedCornerPadding = 0;
    private final StatusBarIconController mStatusBarIconController;
    private TouchAnimator mStatusIconsAlphaAnimator;
    private View mStatusSeparator;
    private View mSystemIconsView;
    private final ZenModeController mZenController;

    public static float getColorIntensity(int i) {
        return i == -1 ? 0.0f : 1.0f;
    }

    public QuickStatusBarHeader(Context context, AttributeSet attributeSet, NextAlarmController nextAlarmController, ZenModeController zenModeController, StatusBarIconController statusBarIconController, ActivityStarter activityStarter, CommandQueue commandQueue, BroadcastDispatcher broadcastDispatcher) {
        super(context, attributeSet);
        new Handler();
        this.mAlarmController = nextAlarmController;
        this.mZenController = zenModeController;
        this.mStatusBarIconController = statusBarIconController;
        this.mActivityStarter = activityStarter;
        this.mDualToneHandler = new DualToneHandler(new ContextThemeWrapper(context, C2018R$style.QSHeaderTheme));
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mCommandQueue = commandQueue;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderQsPanel = (QuickQSPanel) findViewById(C2011R$id.quick_qs_panel);
        this.mSystemIconsView = findViewById(C2011R$id.quick_status_bar_system_icons);
        this.mQuickQsStatusIcons = findViewById(C2011R$id.quick_qs_status_icons);
        StatusIconContainer statusIconContainer = (StatusIconContainer) findViewById(C2011R$id.statusIcons);
        statusIconContainer.addIgnoredSlots(getIgnoredIconSlots());
        statusIconContainer.setShouldRestrictIcons(false);
        this.mIconManager = new TintedIconManager(statusIconContainer, this.mCommandQueue);
        this.mHeaderTextContainerView = findViewById(C2011R$id.header_text_container);
        this.mStatusSeparator = findViewById(C2011R$id.status_separator);
        this.mNextAlarmIcon = (ImageView) findViewById(C2011R$id.next_alarm_icon);
        this.mNextAlarmTextView = (TextView) findViewById(C2011R$id.next_alarm_text);
        View findViewById = findViewById(C2011R$id.alarm_container);
        this.mNextAlarmContainer = findViewById;
        findViewById.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                QuickStatusBarHeader.this.onClick(view);
            }
        });
        this.mRingerModeIcon = (ImageView) findViewById(C2011R$id.ringer_mode_icon);
        this.mRingerModeTextView = (TextView) findViewById(C2011R$id.ringer_mode_text);
        View findViewById2 = findViewById(C2011R$id.ringer_container);
        this.mRingerContainer = findViewById2;
        findViewById2.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                QuickStatusBarHeader.this.onClick(view);
            }
        });
        QSCarrierGroup qSCarrierGroup = (QSCarrierGroup) findViewById(C2011R$id.carrier_group);
        updateResources();
        Rect rect = new Rect(0, 0, 0, 0);
        int singleColor = this.mDualToneHandler.getSingleColor(getColorIntensity(Utils.getColorAttrDefaultColor(getContext(), 16842800)));
        applyDarkness(C2011R$id.clock, rect, 0.0f, -1);
        this.mIconManager.setTint(singleColor);
        this.mNextAlarmIcon.setImageTintList(ColorStateList.valueOf(singleColor));
        this.mRingerModeIcon.setImageTintList(ColorStateList.valueOf(singleColor));
        Clock clock = (Clock) findViewById(C2011R$id.clock);
        this.mClockView = clock;
        clock.setOnClickListener(this);
        DateView dateView = (DateView) findViewById(C2011R$id.date);
        BatteryMeterView batteryMeterView = (BatteryMeterView) findViewById(C2011R$id.batteryRemainingIcon);
        this.mBatteryRemainingIcon = batteryMeterView;
        batteryMeterView.setIgnoreTunerUpdates(true);
        this.mBatteryRemainingIcon.setPercentShowMode(3);
        this.mRingerModeTextView.setSelected(true);
        this.mNextAlarmTextView.setSelected(true);
    }

    private List<String> getIgnoredIconSlots() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mContext.getResources().getString(17041185));
        arrayList.add(this.mContext.getResources().getString(17041197));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void updateStatusText() {
        boolean z = true;
        int i = 0;
        if (updateRingerStatus() || updateAlarmStatus()) {
            boolean z2 = this.mNextAlarmTextView.getVisibility() == 0;
            if (this.mRingerModeTextView.getVisibility() != 0) {
                z = false;
            }
            View view = this.mStatusSeparator;
            if (!z2 || !z) {
                i = 8;
            }
            view.setVisibility(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0065  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean updateRingerStatus() {
        /*
            r8 = this;
            android.widget.TextView r0 = r8.mRingerModeTextView
            int r0 = r0.getVisibility()
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x000c
            r0 = r1
            goto L_0x000d
        L_0x000c:
            r0 = r2
        L_0x000d:
            android.widget.TextView r3 = r8.mRingerModeTextView
            java.lang.CharSequence r3 = r3.getText()
            com.android.systemui.statusbar.policy.ZenModeController r4 = r8.mZenController
            int r4 = r4.getZen()
            com.android.systemui.statusbar.policy.ZenModeController r5 = r8.mZenController
            android.app.NotificationManager$Policy r5 = r5.getConsolidatedPolicy()
            boolean r4 = android.service.notification.ZenModeConfig.isZenOverridingRinger(r4, r5)
            if (r4 != 0) goto L_0x004a
            int r4 = r8.mRingerMode
            if (r4 != r1) goto L_0x0039
            android.widget.ImageView r4 = r8.mRingerModeIcon
            int r5 = com.android.systemui.C2010R$drawable.ic_volume_ringer_vibrate
            r4.setImageResource(r5)
            android.widget.TextView r4 = r8.mRingerModeTextView
            int r5 = com.android.systemui.C2017R$string.qs_status_phone_vibrate
            r4.setText(r5)
        L_0x0037:
            r4 = r1
            goto L_0x004b
        L_0x0039:
            if (r4 != 0) goto L_0x004a
            android.widget.ImageView r4 = r8.mRingerModeIcon
            int r5 = com.android.systemui.C2010R$drawable.ic_volume_ringer_mute
            r4.setImageResource(r5)
            android.widget.TextView r4 = r8.mRingerModeTextView
            int r5 = com.android.systemui.C2017R$string.qs_status_phone_muted
            r4.setText(r5)
            goto L_0x0037
        L_0x004a:
            r4 = r2
        L_0x004b:
            android.widget.ImageView r5 = r8.mRingerModeIcon
            r6 = 8
            if (r4 == 0) goto L_0x0053
            r7 = r2
            goto L_0x0054
        L_0x0053:
            r7 = r6
        L_0x0054:
            r5.setVisibility(r7)
            android.widget.TextView r5 = r8.mRingerModeTextView
            if (r4 == 0) goto L_0x005d
            r7 = r2
            goto L_0x005e
        L_0x005d:
            r7 = r6
        L_0x005e:
            r5.setVisibility(r7)
            android.view.View r5 = r8.mRingerContainer
            if (r4 == 0) goto L_0x0066
            r6 = r2
        L_0x0066:
            r5.setVisibility(r6)
            if (r0 != r4) goto L_0x0079
            android.widget.TextView r8 = r8.mRingerModeTextView
            java.lang.CharSequence r8 = r8.getText()
            boolean r8 = java.util.Objects.equals(r3, r8)
            if (r8 != 0) goto L_0x0078
            goto L_0x0079
        L_0x0078:
            r1 = r2
        L_0x0079:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.QuickStatusBarHeader.updateRingerStatus():boolean");
    }

    private boolean updateAlarmStatus() {
        boolean z;
        boolean z2 = this.mNextAlarmTextView.getVisibility() == 0;
        CharSequence text = this.mNextAlarmTextView.getText();
        AlarmClockInfo alarmClockInfo = this.mNextAlarm;
        if (alarmClockInfo != null) {
            this.mNextAlarmTextView.setText(formatNextAlarm(alarmClockInfo));
            z = true;
        } else {
            z = false;
        }
        int i = 8;
        this.mNextAlarmIcon.setVisibility(z ? 0 : 8);
        this.mNextAlarmTextView.setVisibility(z ? 0 : 8);
        View view = this.mNextAlarmContainer;
        if (z) {
            i = 0;
        }
        view.setVisibility(i);
        if (z2 != z || !Objects.equals(text, this.mNextAlarmTextView.getText())) {
            return true;
        }
        return false;
    }

    private void applyDarkness(int i, Rect rect, float f, int i2) {
        View findViewById = findViewById(i);
        if (findViewById instanceof DarkReceiver) {
            ((DarkReceiver) findViewById).onDarkChanged(rect, f, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateResources();
        this.mClockView.useWallpaperTextColor(configuration.orientation == 2);
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        updateResources();
    }

    private void updateMinimumHeight() {
        setMinimumHeight(this.mContext.getResources().getDimensionPixelSize(17105462) + this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.qs_quick_header_panel_height));
    }

    private void updateResources() {
        Resources resources = this.mContext.getResources();
        updateMinimumHeight();
        this.mRoundedCornerPadding = resources.getDimensionPixelSize(C2009R$dimen.rounded_corner_content_padding);
        this.mHeaderTextContainerView.getLayoutParams().height = resources.getDimensionPixelSize(C2009R$dimen.qs_header_tooltip_height);
        View view = this.mHeaderTextContainerView;
        view.setLayoutParams(view.getLayoutParams());
        this.mSystemIconsView.getLayoutParams().height = resources.getDimensionPixelSize(17105418);
        View view2 = this.mSystemIconsView;
        view2.setLayoutParams(view2.getLayoutParams());
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (this.mQsDisabled) {
            layoutParams.height = resources.getDimensionPixelSize(17105418);
        } else if (!com.android.systemui.util.Utils.useQsMediaPlayer(this.mContext) || !this.mHeaderQsPanel.hasMediaPlayerSession()) {
            layoutParams.height = Math.max(getMinimumHeight(), resources.getDimensionPixelSize(17105419));
        } else {
            layoutParams.height = Math.max(getMinimumHeight(), resources.getDimensionPixelSize(17105420));
        }
        setLayoutParams(layoutParams);
        updateStatusIconAlphaAnimator();
        updateHeaderTextContainerAlphaAnimator();
    }

    private void updateStatusIconAlphaAnimator() {
        Builder builder = new Builder();
        builder.addFloat(this.mQuickQsStatusIcons, "alpha", 1.0f, 0.0f, 0.0f);
        this.mStatusIconsAlphaAnimator = builder.build();
    }

    private void updateHeaderTextContainerAlphaAnimator() {
        Builder builder = new Builder();
        builder.addFloat(this.mHeaderTextContainerView, "alpha", 0.0f, 0.0f, 1.0f);
        this.mHeaderTextContainerAlphaAnimator = builder.build();
    }

    public void setExpanded(boolean z) {
        if (this.mExpanded != z) {
            this.mExpanded = z;
            this.mHeaderQsPanel.setExpanded(z);
            updateEverything();
        }
    }

    public void setExpansion(boolean z, float f, float f2) {
        float f3 = z ? 1.0f : f;
        TouchAnimator touchAnimator = this.mStatusIconsAlphaAnimator;
        if (touchAnimator != null) {
            touchAnimator.setPosition(f3);
        }
        if (z) {
            this.mHeaderTextContainerView.setTranslationY(f2);
        } else {
            this.mHeaderTextContainerView.setTranslationY(0.0f);
        }
        TouchAnimator touchAnimator2 = this.mHeaderTextContainerAlphaAnimator;
        if (touchAnimator2 != null) {
            touchAnimator2.setPosition(f3);
            if (f3 > 0.0f) {
                this.mHeaderTextContainerView.setVisibility(0);
            } else {
                this.mHeaderTextContainerView.setVisibility(4);
            }
        }
        if (f < 1.0f && ((double) f) > 0.99d && this.mHeaderQsPanel.switchTileLayout()) {
            updateResources();
        }
    }

    public void disable(int i, int i2, boolean z) {
        boolean z2 = true;
        int i3 = 0;
        if ((i2 & 1) == 0) {
            z2 = false;
        }
        if (z2 != this.mQsDisabled) {
            this.mQsDisabled = z2;
            this.mHeaderQsPanel.setDisabledByPolicy(z2);
            this.mHeaderTextContainerView.setVisibility(this.mQsDisabled ? 8 : 0);
            View view = this.mQuickQsStatusIcons;
            if (this.mQsDisabled) {
                i3 = 8;
            }
            view.setVisibility(i3);
            updateResources();
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mStatusBarIconController.addIconGroup(this.mIconManager);
        requestApplyInsets();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        int i;
        int i2;
        int i3;
        setPadding(this.mRoundedCornerPadding, getPaddingTop(), this.mRoundedCornerPadding, getPaddingBottom());
        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        Pair paddingNeededForCutoutAndRoundedCorner = StatusBarWindowView.paddingNeededForCutoutAndRoundedCorner(displayCutout, StatusBarWindowView.cornerCutoutMargins(displayCutout, getDisplay()), this.mRoundedCornerPadding);
        if (displayCutout == null) {
            i = 0;
        } else {
            i = displayCutout.getWaterfallInsets().top;
        }
        if (isLayoutRtl()) {
            i2 = getResources().getDimensionPixelSize(C2009R$dimen.status_bar_padding_end);
        } else {
            i2 = getResources().getDimensionPixelSize(C2009R$dimen.status_bar_padding_start);
        }
        if (isLayoutRtl()) {
            i3 = getResources().getDimensionPixelSize(C2009R$dimen.status_bar_padding_start);
        } else {
            i3 = getResources().getDimensionPixelSize(C2009R$dimen.status_bar_padding_end);
        }
        this.mSystemIconsView.setPadding(Math.max((((Integer) paddingNeededForCutoutAndRoundedCorner.first).intValue() + i2) - this.mRoundedCornerPadding, 0), i, Math.max((((Integer) paddingNeededForCutoutAndRoundedCorner.second).intValue() + i3) - this.mRoundedCornerPadding, 0), 0);
        return super.onApplyWindowInsets(windowInsets);
    }

    public void onDetachedFromWindow() {
        setListening(false);
        this.mStatusBarIconController.removeIconGroup(this.mIconManager);
        super.onDetachedFromWindow();
    }

    public void setListening(boolean z) {
        if (z != this.mListening) {
            this.mHeaderQsPanel.setListening(z);
            if (this.mHeaderQsPanel.switchTileLayout()) {
                updateResources();
            }
            this.mListening = z;
            if (z) {
                this.mZenController.addCallback(this);
                this.mAlarmController.addCallback(this);
                this.mBroadcastDispatcher.registerReceiver(this.mRingerReceiver, new IntentFilter("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION"));
            } else {
                this.mZenController.removeCallback(this);
                this.mAlarmController.removeCallback(this);
                this.mBroadcastDispatcher.unregisterReceiver(this.mRingerReceiver);
            }
        }
    }

    public void onClick(View view) {
        String str = "android.intent.action.SHOW_ALARMS";
        if (view == this.mClockView) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent(str), 0);
            return;
        }
        View view2 = this.mNextAlarmContainer;
        if (view != view2 || !view2.isVisibleToUser()) {
            View view3 = this.mRingerContainer;
            if (view == view3 && view3.isVisibleToUser()) {
                this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.SOUND_SETTINGS"), 0);
            }
        } else if (this.mNextAlarm.getShowIntent() != null) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(this.mNextAlarm.getShowIntent());
        } else {
            Log.d("QuickStatusBarHeader", "No PendingIntent for next alarm. Using default intent");
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent(str), 0);
        }
    }

    public void onNextAlarmChanged(AlarmClockInfo alarmClockInfo) {
        this.mNextAlarm = alarmClockInfo;
        updateStatusText();
    }

    public void onZenChanged(int i) {
        updateStatusText();
    }

    public void onConfigChanged(ZenModeConfig zenModeConfig) {
        updateStatusText();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateEverything$0 */
    public /* synthetic */ void lambda$updateEverything$0$QuickStatusBarHeader() {
        setClickable(!this.mExpanded);
    }

    public void updateEverything() {
        post(new Runnable() {
            public final void run() {
                QuickStatusBarHeader.this.lambda$updateEverything$0$QuickStatusBarHeader();
            }
        });
    }

    public void setQSPanel(QSPanel qSPanel) {
        this.mQsPanel = qSPanel;
        setupHost(qSPanel.getHost());
    }

    public void setupHost(QSTileHost qSTileHost) {
        this.mHeaderQsPanel.setQSPanelAndHeader(this.mQsPanel, this);
        this.mHeaderQsPanel.setHost(qSTileHost, null);
        Rect rect = new Rect(0, 0, 0, 0);
        float colorIntensity = getColorIntensity(Utils.getColorAttrDefaultColor(getContext(), 16842800));
        this.mBatteryRemainingIcon.onDarkChanged(rect, colorIntensity, this.mDualToneHandler.getSingleColor(colorIntensity));
    }

    public void setCallback(QSDetail.Callback callback) {
        this.mHeaderQsPanel.setCallback(callback);
    }

    private String formatNextAlarm(AlarmClockInfo alarmClockInfo) {
        if (alarmClockInfo == null) {
            return "";
        }
        return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser()) ? "EHm" : "Ehma"), alarmClockInfo.getTriggerTime()).toString();
    }

    public void setMargins(int i) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (!(childAt == this.mSystemIconsView || childAt == this.mQuickQsStatusIcons || childAt == this.mHeaderQsPanel || childAt == this.mHeaderTextContainerView)) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) childAt.getLayoutParams();
                layoutParams.leftMargin = i;
                layoutParams.rightMargin = i;
            }
        }
    }
}
