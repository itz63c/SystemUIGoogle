package com.android.systemui;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.graph.ThemedBatteryDrawable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.statusbar.policy.BatteryController.EstimateFetchCompletion;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.SysuiLifecycle;
import com.android.systemui.util.Utils.DisableStateTracker;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.function.Supplier;

public class BatteryMeterView extends LinearLayout implements BatteryStateChangeCallback, Tunable, DarkReceiver, ConfigurationListener {
    private BatteryController mBatteryController;
    private final ImageView mBatteryIconView;
    private TextView mBatteryPercentView;
    private boolean mCharging;
    private final ThemedBatteryDrawable mDrawable;
    private DualToneHandler mDualToneHandler;
    private boolean mForceShowPercent;
    private boolean mIgnoreTunerUpdates;
    private boolean mIsSubscribedForTunerUpdates;
    private int mLevel;
    private int mNonAdaptedBackgroundColor;
    private int mNonAdaptedForegroundColor;
    private int mNonAdaptedSingleToneColor;
    private final int mPercentageStyleId;
    /* access modifiers changed from: private */
    public SettingObserver mSettingObserver;
    private boolean mShowPercentAvailable;
    private int mShowPercentMode;
    private final String mSlotBattery;
    private int mTextColor;
    private boolean mUseWallpaperTextColors;
    /* access modifiers changed from: private */
    public int mUser;
    private final CurrentUserTracker mUserTracker;

    private final class SettingObserver extends ContentObserver {
        public SettingObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            BatteryMeterView.this.updateShowPercent();
            if (TextUtils.equals(uri.getLastPathSegment(), "battery_estimates_last_update_time")) {
                BatteryMeterView.this.updatePercentText();
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowPercentMode = 0;
        BroadcastDispatcher broadcastDispatcher = (BroadcastDispatcher) Dependency.get(BroadcastDispatcher.class);
        setOrientation(0);
        setGravity(8388627);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.BatteryMeterView, i, 0);
        int color = obtainStyledAttributes.getColor(R$styleable.BatteryMeterView_frameColor, context.getColor(C2008R$color.meter_background_color));
        this.mPercentageStyleId = obtainStyledAttributes.getResourceId(R$styleable.BatteryMeterView_textAppearance, 0);
        this.mDrawable = new ThemedBatteryDrawable(context, color);
        obtainStyledAttributes.recycle();
        this.mSettingObserver = new SettingObserver(new Handler(context.getMainLooper()));
        this.mShowPercentAvailable = context.getResources().getBoolean(17891374);
        addOnAttachStateChangeListener(new DisableStateTracker(0, 2, (CommandQueue) Dependency.get(CommandQueue.class)));
        setupLayoutTransition();
        this.mSlotBattery = context.getString(17041183);
        ImageView imageView = new ImageView(context);
        this.mBatteryIconView = imageView;
        imageView.setImageDrawable(this.mDrawable);
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(getResources().getDimensionPixelSize(C2009R$dimen.status_bar_battery_icon_width), getResources().getDimensionPixelSize(C2009R$dimen.status_bar_battery_icon_height));
        marginLayoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelOffset(C2009R$dimen.battery_margin_bottom));
        addView(this.mBatteryIconView, marginLayoutParams);
        updateShowPercent();
        this.mDualToneHandler = new DualToneHandler(context);
        onDarkChanged(new Rect(), 0.0f, -1);
        this.mUserTracker = new CurrentUserTracker(broadcastDispatcher) {
            public void onUserSwitched(int i) {
                BatteryMeterView.this.mUser = i;
                BatteryMeterView.this.getContext().getContentResolver().unregisterContentObserver(BatteryMeterView.this.mSettingObserver);
                BatteryMeterView.this.getContext().getContentResolver().registerContentObserver(System.getUriFor("status_bar_show_battery_percent"), false, BatteryMeterView.this.mSettingObserver, i);
                BatteryMeterView.this.updateShowPercent();
            }
        };
        setClipChildren(false);
        setClipToPadding(false);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).observe(SysuiLifecycle.viewAttachLifecycle(this), this);
    }

    private void setupLayoutTransition() {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(200);
        String str = "alpha";
        layoutTransition.setAnimator(2, ObjectAnimator.ofFloat(null, str, new float[]{0.0f, 1.0f}));
        layoutTransition.setInterpolator(2, Interpolators.ALPHA_IN);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(null, str, new float[]{1.0f, 0.0f});
        layoutTransition.setInterpolator(3, Interpolators.ALPHA_OUT);
        layoutTransition.setAnimator(3, ofFloat);
        setLayoutTransition(layoutTransition);
    }

    public void setForceShowPercent(boolean z) {
        setPercentShowMode(z ? 1 : 0);
    }

    public void setPercentShowMode(int i) {
        this.mShowPercentMode = i;
        updateShowPercent();
    }

    public void setIgnoreTunerUpdates(boolean z) {
        this.mIgnoreTunerUpdates = z;
        updateTunerSubscription();
    }

    private void updateTunerSubscription() {
        if (this.mIgnoreTunerUpdates) {
            unsubscribeFromTunerUpdates();
        } else {
            subscribeForTunerUpdates();
        }
    }

    private void subscribeForTunerUpdates() {
        if (!this.mIsSubscribedForTunerUpdates && !this.mIgnoreTunerUpdates) {
            ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "icon_blacklist");
            this.mIsSubscribedForTunerUpdates = true;
        }
    }

    private void unsubscribeFromTunerUpdates() {
        if (this.mIsSubscribedForTunerUpdates) {
            ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
            this.mIsSubscribedForTunerUpdates = false;
        }
    }

    public void setColorsFromContext(Context context) {
        if (context != null) {
            this.mDualToneHandler.setColorsFromContext(context);
        }
    }

    public void onTuningChanged(String str, String str2) {
        if ("icon_blacklist".equals(str)) {
            setVisibility(StatusBarIconController.getIconBlacklist(getContext(), str2).contains(this.mSlotBattery) ? 8 : 0);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        BatteryController batteryController = (BatteryController) Dependency.get(BatteryController.class);
        this.mBatteryController = batteryController;
        batteryController.addCallback(this);
        this.mUser = ActivityManager.getCurrentUser();
        getContext().getContentResolver().registerContentObserver(System.getUriFor("status_bar_show_battery_percent"), false, this.mSettingObserver, this.mUser);
        getContext().getContentResolver().registerContentObserver(Global.getUriFor("battery_estimates_last_update_time"), false, this.mSettingObserver);
        updateShowPercent();
        subscribeForTunerUpdates();
        this.mUserTracker.startTracking();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mUserTracker.stopTracking();
        this.mBatteryController.removeCallback(this);
        getContext().getContentResolver().unregisterContentObserver(this.mSettingObserver);
        unsubscribeFromTunerUpdates();
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        this.mDrawable.setCharging(z);
        this.mDrawable.setBatteryLevel(i);
        this.mCharging = z;
        this.mLevel = i;
        updatePercentText();
    }

    public void onPowerSaveChanged(boolean z) {
        this.mDrawable.setPowerSaveEnabled(z);
    }

    private TextView loadPercentView() {
        return (TextView) LayoutInflater.from(getContext()).inflate(C2013R$layout.battery_percentage_view, null);
    }

    public void updatePercentView() {
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            removeView(textView);
            this.mBatteryPercentView = null;
        }
        updateShowPercent();
    }

    /* access modifiers changed from: private */
    public void updatePercentText() {
        int i;
        BatteryController batteryController = this.mBatteryController;
        if (batteryController != null) {
            if (this.mBatteryPercentView == null) {
                Context context = getContext();
                if (this.mCharging) {
                    i = C2017R$string.accessibility_battery_level_charging;
                } else {
                    i = C2017R$string.accessibility_battery_level;
                }
                setContentDescription(context.getString(i, new Object[]{Integer.valueOf(this.mLevel)}));
            } else if (this.mShowPercentMode != 3 || this.mCharging) {
                setPercentTextAtCurrentLevel();
            } else {
                batteryController.getEstimatedTimeRemainingString(new EstimateFetchCompletion() {
                    public final void onBatteryRemainingEstimateRetrieved(String str) {
                        BatteryMeterView.this.lambda$updatePercentText$0$BatteryMeterView(str);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePercentText$0 */
    public /* synthetic */ void lambda$updatePercentText$0$BatteryMeterView(String str) {
        if (str != null) {
            this.mBatteryPercentView.setText(str);
            setContentDescription(getContext().getString(C2017R$string.accessibility_battery_level_with_estimate, new Object[]{Integer.valueOf(this.mLevel), str}));
            return;
        }
        setPercentTextAtCurrentLevel();
    }

    private void setPercentTextAtCurrentLevel() {
        int i;
        this.mBatteryPercentView.setText(NumberFormat.getPercentInstance().format((double) (((float) this.mLevel) / 100.0f)));
        Context context = getContext();
        if (this.mCharging) {
            i = C2017R$string.accessibility_battery_level_charging;
        } else {
            i = C2017R$string.accessibility_battery_level;
        }
        setContentDescription(context.getString(i, new Object[]{Integer.valueOf(this.mLevel)}));
    }

    /* access modifiers changed from: private */
    public void updateShowPercent() {
        boolean z = false;
        boolean z2 = this.mBatteryPercentView != null;
        if (((Integer) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
            public final Object get() {
                return BatteryMeterView.this.lambda$updateShowPercent$1$BatteryMeterView();
            }
        })).intValue() != 0) {
            z = true;
        }
        if (!this.mShowPercentAvailable || !z || this.mShowPercentMode == 2) {
            int i = this.mShowPercentMode;
            if (!(i == 1 || i == 3)) {
                if (z2) {
                    removeView(this.mBatteryPercentView);
                    this.mBatteryPercentView = null;
                    return;
                }
                return;
            }
        }
        if (!z2) {
            TextView loadPercentView = loadPercentView();
            this.mBatteryPercentView = loadPercentView;
            int i2 = this.mPercentageStyleId;
            if (i2 != 0) {
                loadPercentView.setTextAppearance(i2);
            }
            int i3 = this.mTextColor;
            if (i3 != 0) {
                this.mBatteryPercentView.setTextColor(i3);
            }
            updatePercentText();
            addView(this.mBatteryPercentView, new LayoutParams(-2, -1));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateShowPercent$1 */
    public /* synthetic */ Integer lambda$updateShowPercent$1$BatteryMeterView() {
        return Integer.valueOf(System.getIntForUser(getContext().getContentResolver(), "status_bar_show_battery_percent", 0, this.mUser));
    }

    public void onDensityOrFontScaleChanged() {
        scaleBatteryMeterViews();
    }

    private void scaleBatteryMeterViews() {
        Resources resources = getContext().getResources();
        TypedValue typedValue = new TypedValue();
        resources.getValue(C2009R$dimen.status_bar_icon_scale_factor, typedValue, true);
        float f = typedValue.getFloat();
        int dimensionPixelSize = resources.getDimensionPixelSize(C2009R$dimen.status_bar_battery_icon_height);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(C2009R$dimen.status_bar_battery_icon_width);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(C2009R$dimen.battery_margin_bottom);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (((float) dimensionPixelSize2) * f), (int) (((float) dimensionPixelSize) * f));
        layoutParams.setMargins(0, 0, 0, dimensionPixelSize3);
        this.mBatteryIconView.setLayoutParams(layoutParams);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        if (!DarkIconDispatcher.isInArea(rect, this)) {
            f = 0.0f;
        }
        this.mNonAdaptedSingleToneColor = this.mDualToneHandler.getSingleColor(f);
        this.mNonAdaptedForegroundColor = this.mDualToneHandler.getFillColor(f);
        int backgroundColor = this.mDualToneHandler.getBackgroundColor(f);
        this.mNonAdaptedBackgroundColor = backgroundColor;
        if (!this.mUseWallpaperTextColors) {
            updateColors(this.mNonAdaptedForegroundColor, backgroundColor, this.mNonAdaptedSingleToneColor);
        }
    }

    private void updateColors(int i, int i2, int i3) {
        this.mDrawable.setColors(i, i2, i3);
        this.mTextColor = i3;
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            textView.setTextColor(i3);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        CharSequence charSequence = null;
        if (this.mDrawable == null) {
            str = null;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mDrawable.getPowerSaveEnabled());
            sb.append("");
            str = sb.toString();
        }
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            charSequence = textView.getText();
        }
        printWriter.println("  BatteryMeterView:");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    mDrawable.getPowerSave: ");
        sb2.append(str);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("    mBatteryPercentView.getText(): ");
        sb3.append(charSequence);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("    mTextColor: #");
        sb4.append(Integer.toHexString(this.mTextColor));
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("    mLevel: ");
        sb5.append(this.mLevel);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("    mForceShowPercent: ");
        sb6.append(this.mForceShowPercent);
        printWriter.println(sb6.toString());
    }
}
