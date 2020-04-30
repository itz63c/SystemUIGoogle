package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.hardware.display.NightDisplayListener.Callback;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Switch;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimeZone;

/* renamed from: com.android.systemui.qs.tiles.NightDisplayTile */
public class NightDisplayTile extends QSTileImpl<BooleanState> implements Callback {
    private boolean mIsListening;
    private NightDisplayListener mListener = new NightDisplayListener(this.mContext, new Handler(Looper.myLooper()));
    private final ColorDisplayManager mManager = ((ColorDisplayManager) this.mContext.getSystemService(ColorDisplayManager.class));

    public int getMetricsCategory() {
        return 491;
    }

    public NightDisplayTile(QSHost qSHost) {
        super(qSHost);
    }

    public boolean isAvailable() {
        return ColorDisplayManager.isNightDisplayAvailable(this.mContext);
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        if ("1".equals(Global.getString(this.mContext.getContentResolver(), "night_display_forced_auto_mode_available")) && this.mManager.getNightDisplayAutoModeRaw() == -1) {
            this.mManager.setNightDisplayAutoMode(1);
            Log.i("NightDisplayTile", "Enrolled in forced night display auto mode");
        }
        this.mManager.setNightDisplayActivated(!((BooleanState) this.mState).value);
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
        if (this.mIsListening) {
            this.mListener.setCallback(null);
        }
        NightDisplayListener nightDisplayListener = new NightDisplayListener(this.mContext, i, new Handler(Looper.myLooper()));
        this.mListener = nightDisplayListener;
        if (this.mIsListening) {
            nightDisplayListener.setCallback(this);
        }
        super.handleUserSwitch(i);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        CharSequence charSequence;
        booleanState.value = this.mManager.isNightDisplayActivated();
        booleanState.label = this.mContext.getString(C2017R$string.quick_settings_night_display_label);
        booleanState.icon = ResourceIcon.get(17302797);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.state = booleanState.value ? 2 : 1;
        String secondaryLabel = getSecondaryLabel(booleanState.value);
        booleanState.secondaryLabel = secondaryLabel;
        if (TextUtils.isEmpty(secondaryLabel)) {
            charSequence = booleanState.label;
        } else {
            charSequence = TextUtils.concat(new CharSequence[]{booleanState.label, ", ", booleanState.secondaryLabel});
        }
        booleanState.contentDescription = charSequence;
    }

    private String getSecondaryLabel(boolean z) {
        LocalTime localTime;
        int i;
        String str;
        int nightDisplayAutoMode = this.mManager.getNightDisplayAutoMode();
        if (nightDisplayAutoMode == 1) {
            if (z) {
                localTime = this.mManager.getNightDisplayCustomEndTime();
                i = C2017R$string.quick_settings_secondary_label_until;
            } else {
                localTime = this.mManager.getNightDisplayCustomStartTime();
                i = C2017R$string.quick_settings_night_secondary_label_on_at;
            }
            Calendar instance = Calendar.getInstance();
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this.mContext);
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            instance.setTimeZone(timeFormat.getTimeZone());
            instance.set(11, localTime.getHour());
            instance.set(12, localTime.getMinute());
            instance.set(13, 0);
            instance.set(14, 0);
            return this.mContext.getString(i, new Object[]{timeFormat.format(instance.getTime())});
        } else if (nightDisplayAutoMode != 2) {
            return null;
        } else {
            if (z) {
                str = this.mContext.getString(C2017R$string.quick_settings_night_secondary_label_until_sunrise);
            } else {
                str = this.mContext.getString(C2017R$string.quick_settings_night_secondary_label_on_at_sunset);
            }
            return str;
        }
    }

    public LogMaker populate(LogMaker logMaker) {
        return super.populate(logMaker).addTaggedData(1311, Integer.valueOf(this.mManager.getNightDisplayAutoModeRaw()));
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.NIGHT_DISPLAY_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        this.mIsListening = z;
        if (z) {
            this.mListener.setCallback(this);
            refreshState();
            return;
        }
        this.mListener.setCallback(null);
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_night_display_label);
    }

    public void onActivated(boolean z) {
        refreshState();
    }
}
