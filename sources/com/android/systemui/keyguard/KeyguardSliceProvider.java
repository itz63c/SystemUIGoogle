package com.android.systemui.keyguard;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.AlarmManager.OnAlarmListener;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Handler;
import android.os.Trace;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.HeaderBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUIAppComponentFactory.ContextAvailableCallback;
import com.android.systemui.SystemUIAppComponentFactory.ContextInitializer;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationMediaManager.MediaListener;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmController.NextAlarmChangeCallback;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeController.Callback;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class KeyguardSliceProvider extends SliceProvider implements NextAlarmChangeCallback, Callback, MediaListener, StateListener, ContextInitializer {
    @VisibleForTesting
    static final int ALARM_VISIBILITY_HOURS = 12;
    private static KeyguardSliceProvider sInstance;
    private static final Object sInstanceLock = new Object();
    public AlarmManager mAlarmManager;
    protected final Uri mAlarmUri = Uri.parse("content://com.android.systemui.keyguard/alarm");
    public ContentResolver mContentResolver;
    private ContextAvailableCallback mContextAvailableCallback;
    private final Date mCurrentTime = new Date();
    private DateFormat mDateFormat;
    private String mDatePattern;
    protected final Uri mDateUri = Uri.parse("content://com.android.systemui.keyguard/date");
    protected final Uri mDndUri = Uri.parse("content://com.android.systemui.keyguard/dnd");
    public DozeParameters mDozeParameters;
    protected boolean mDozing;
    private final Handler mHandler = new Handler();
    protected final Uri mHeaderUri = Uri.parse("content://com.android.systemui.keyguard/header");
    @VisibleForTesting
    final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.DATE_CHANGED".equals(action)) {
                synchronized (this) {
                    KeyguardSliceProvider.this.updateClockLocked();
                }
            } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
                synchronized (this) {
                    KeyguardSliceProvider.this.cleanDateFormatLocked();
                }
            }
        }
    };
    public KeyguardBypassController mKeyguardBypassController;
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onTimeChanged() {
            synchronized (this) {
                KeyguardSliceProvider.this.updateClockLocked();
            }
        }

        public void onTimeZoneChanged(TimeZone timeZone) {
            synchronized (this) {
                KeyguardSliceProvider.this.cleanDateFormatLocked();
            }
        }
    };
    private String mLastText;
    private CharSequence mMediaArtist;
    private final Handler mMediaHandler = new Handler();
    private boolean mMediaIsVisible;
    public NotificationMediaManager mMediaManager;
    private CharSequence mMediaTitle;
    protected final Uri mMediaUri = Uri.parse("content://com.android.systemui.keyguard/media");
    @VisibleForTesting
    protected SettableWakeLock mMediaWakeLock;
    private String mNextAlarm;
    public NextAlarmController mNextAlarmController;
    private AlarmClockInfo mNextAlarmInfo;
    private PendingIntent mPendingIntent;
    private boolean mRegistered;
    protected final Uri mSliceUri = Uri.parse("content://com.android.systemui.keyguard/main");
    private int mStatusBarState;
    public StatusBarStateController mStatusBarStateController;
    private final OnAlarmListener mUpdateNextAlarm = new OnAlarmListener() {
        public final void onAlarm() {
            KeyguardSliceProvider.this.updateNextAlarm();
        }
    };
    public ZenModeController mZenModeController;

    static {
        new StyleSpan(1);
    }

    public static KeyguardSliceProvider getAttachedInstance() {
        return sInstance;
    }

    public Slice onBindSlice(Uri uri) {
        Slice build;
        Trace.beginSection("KeyguardSliceProvider#onBindSlice");
        synchronized (this) {
            ListBuilder listBuilder = new ListBuilder(getContext(), this.mSliceUri, -1);
            if (needsMediaLocked()) {
                addMediaLocked(listBuilder);
            } else {
                RowBuilder rowBuilder = new RowBuilder(this.mDateUri);
                rowBuilder.setTitle(this.mLastText);
                listBuilder.addRow(rowBuilder);
            }
            addNextAlarmLocked(listBuilder);
            addZenModeLocked(listBuilder);
            addPrimaryActionLocked(listBuilder);
            build = listBuilder.build();
        }
        Trace.endSection();
        return build;
    }

    /* access modifiers changed from: protected */
    public boolean needsMediaLocked() {
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        boolean z = keyguardBypassController != null && keyguardBypassController.getBypassEnabled() && this.mDozeParameters.getAlwaysOn();
        boolean z2 = this.mStatusBarState == 0 && this.mMediaIsVisible;
        if (TextUtils.isEmpty(this.mMediaTitle) || !this.mMediaIsVisible || (!this.mDozing && !z && !z2)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void addMediaLocked(ListBuilder listBuilder) {
        if (!TextUtils.isEmpty(this.mMediaTitle)) {
            HeaderBuilder headerBuilder = new HeaderBuilder(this.mHeaderUri);
            headerBuilder.setTitle(this.mMediaTitle);
            listBuilder.setHeader(headerBuilder);
            if (!TextUtils.isEmpty(this.mMediaArtist)) {
                RowBuilder rowBuilder = new RowBuilder(this.mMediaUri);
                rowBuilder.setTitle(this.mMediaArtist);
                NotificationMediaManager notificationMediaManager = this.mMediaManager;
                IconCompat iconCompat = null;
                Icon mediaIcon = notificationMediaManager == null ? null : notificationMediaManager.getMediaIcon();
                if (mediaIcon != null) {
                    iconCompat = IconCompat.createFromIcon(getContext(), mediaIcon);
                }
                if (iconCompat != null) {
                    rowBuilder.addEndItem(iconCompat, 0);
                }
                listBuilder.addRow(rowBuilder);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void addPrimaryActionLocked(ListBuilder listBuilder) {
        SliceAction createDeeplink = SliceAction.createDeeplink(this.mPendingIntent, IconCompat.createWithResource(getContext(), C2010R$drawable.ic_access_alarms_big), 0, this.mLastText);
        RowBuilder rowBuilder = new RowBuilder(Uri.parse("content://com.android.systemui.keyguard/action"));
        rowBuilder.setPrimaryAction(createDeeplink);
        listBuilder.addRow(rowBuilder);
    }

    /* access modifiers changed from: protected */
    public void addNextAlarmLocked(ListBuilder listBuilder) {
        if (!TextUtils.isEmpty(this.mNextAlarm)) {
            IconCompat createWithResource = IconCompat.createWithResource(getContext(), C2010R$drawable.ic_access_alarms_big);
            RowBuilder rowBuilder = new RowBuilder(this.mAlarmUri);
            rowBuilder.setTitle(this.mNextAlarm);
            rowBuilder.addEndItem(createWithResource, 0);
            listBuilder.addRow(rowBuilder);
        }
    }

    /* access modifiers changed from: protected */
    public void addZenModeLocked(ListBuilder listBuilder) {
        if (isDndOn()) {
            RowBuilder rowBuilder = new RowBuilder(this.mDndUri);
            rowBuilder.setContentDescription(getContext().getResources().getString(C2017R$string.accessibility_quick_settings_dnd));
            rowBuilder.addEndItem(IconCompat.createWithResource(getContext(), C2010R$drawable.stat_sys_dnd), 0);
            listBuilder.addRow(rowBuilder);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isDndOn() {
        return this.mZenModeController.getZen() != 0;
    }

    public boolean onCreateSliceProvider() {
        this.mContextAvailableCallback.onContextAvailable(getContext());
        inject();
        synchronized (sInstanceLock) {
            KeyguardSliceProvider keyguardSliceProvider = sInstance;
            if (keyguardSliceProvider != null) {
                keyguardSliceProvider.onDestroy();
            }
            this.mDatePattern = getContext().getString(C2017R$string.system_ui_aod_date_pattern);
            this.mPendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(), 0);
            this.mMediaManager.addCallback(this);
            this.mStatusBarStateController.addCallback(this);
            this.mNextAlarmController.addCallback(this);
            this.mZenModeController.addCallback(this);
            sInstance = this;
            registerClockUpdate();
            updateClockLocked();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void inject() {
        SystemUIFactory.getInstance().getRootComponent().inject(this);
        String str = "media";
        this.mMediaWakeLock = new SettableWakeLock(WakeLock.createPartial(getContext(), str), str);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void onDestroy() {
        synchronized (sInstanceLock) {
            this.mNextAlarmController.removeCallback(this);
            this.mZenModeController.removeCallback(this);
            this.mMediaWakeLock.setAcquired(false);
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            if (this.mRegistered) {
                this.mRegistered = false;
                getKeyguardUpdateMonitor().removeCallback(this.mKeyguardUpdateMonitorCallback);
                getContext().unregisterReceiver(this.mIntentReceiver);
            }
            sInstance = null;
        }
    }

    public void onZenChanged(int i) {
        notifyChange();
    }

    public void onConfigChanged(ZenModeConfig zenModeConfig) {
        notifyChange();
    }

    /* access modifiers changed from: private */
    public void updateNextAlarm() {
        synchronized (this) {
            if (withinNHoursLocked(this.mNextAlarmInfo, 12)) {
                this.mNextAlarm = android.text.format.DateFormat.format(android.text.format.DateFormat.is24HourFormat(getContext(), ActivityManager.getCurrentUser()) ? "HH:mm" : "h:mm", this.mNextAlarmInfo.getTriggerTime()).toString();
            } else {
                this.mNextAlarm = "";
            }
        }
        notifyChange();
    }

    private boolean withinNHoursLocked(AlarmClockInfo alarmClockInfo, int i) {
        boolean z = false;
        if (alarmClockInfo == null) {
            return false;
        }
        if (this.mNextAlarmInfo.getTriggerTime() <= System.currentTimeMillis() + TimeUnit.HOURS.toMillis((long) i)) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void registerClockUpdate() {
        synchronized (this) {
            if (!this.mRegistered) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.DATE_CHANGED");
                intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
                getContext().registerReceiver(this.mIntentReceiver, intentFilter, null, null);
                getKeyguardUpdateMonitor().registerCallback(this.mKeyguardUpdateMonitorCallback);
                this.mRegistered = true;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean isRegistered() {
        boolean z;
        synchronized (this) {
            z = this.mRegistered;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void updateClockLocked() {
        String formattedDateLocked = getFormattedDateLocked();
        if (!formattedDateLocked.equals(this.mLastText)) {
            this.mLastText = formattedDateLocked;
            notifyChange();
        }
    }

    /* access modifiers changed from: protected */
    public String getFormattedDateLocked() {
        if (this.mDateFormat == null) {
            DateFormat instanceForSkeleton = DateFormat.getInstanceForSkeleton(this.mDatePattern, Locale.getDefault());
            instanceForSkeleton.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            this.mDateFormat = instanceForSkeleton;
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        return this.mDateFormat.format(this.mCurrentTime);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void cleanDateFormatLocked() {
        this.mDateFormat = null;
    }

    public void onNextAlarmChanged(AlarmClockInfo alarmClockInfo) {
        long triggerTime;
        synchronized (this) {
            this.mNextAlarmInfo = alarmClockInfo;
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            if (this.mNextAlarmInfo == null) {
                triggerTime = -1;
            } else {
                triggerTime = this.mNextAlarmInfo.getTriggerTime() - TimeUnit.HOURS.toMillis(12);
            }
            long j = triggerTime;
            if (j > 0) {
                this.mAlarmManager.setExact(1, j, "lock_screen_next_alarm", this.mUpdateNextAlarm, this.mHandler);
            }
        }
        updateNextAlarm();
    }

    private KeyguardUpdateMonitor getKeyguardUpdateMonitor() {
        return (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
    }

    public void onMetadataOrStateChanged(MediaMetadata mediaMetadata, int i) {
        synchronized (this) {
            boolean isPlayingState = NotificationMediaManager.isPlayingState(i);
            this.mMediaHandler.removeCallbacksAndMessages(null);
            if (!this.mMediaIsVisible || isPlayingState || this.mStatusBarState == 0) {
                this.mMediaWakeLock.setAcquired(false);
                updateMediaStateLocked(mediaMetadata, i);
            } else {
                this.mMediaWakeLock.setAcquired(true);
                this.mMediaHandler.postDelayed(new Runnable(mediaMetadata, i) {
                    public final /* synthetic */ MediaMetadata f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        KeyguardSliceProvider.this.lambda$onMetadataOrStateChanged$0$KeyguardSliceProvider(this.f$1, this.f$2);
                    }
                }, 2000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMetadataOrStateChanged$0 */
    public /* synthetic */ void lambda$onMetadataOrStateChanged$0$KeyguardSliceProvider(MediaMetadata mediaMetadata, int i) {
        synchronized (this) {
            updateMediaStateLocked(mediaMetadata, i);
            this.mMediaWakeLock.setAcquired(false);
        }
    }

    private void updateMediaStateLocked(MediaMetadata mediaMetadata, int i) {
        CharSequence charSequence;
        boolean isPlayingState = NotificationMediaManager.isPlayingState(i);
        CharSequence charSequence2 = null;
        if (mediaMetadata != null) {
            charSequence = mediaMetadata.getText("android.media.metadata.TITLE");
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = getContext().getResources().getString(C2017R$string.music_controls_no_title);
            }
        } else {
            charSequence = null;
        }
        if (mediaMetadata != null) {
            charSequence2 = mediaMetadata.getText("android.media.metadata.ARTIST");
        }
        if (isPlayingState != this.mMediaIsVisible || !TextUtils.equals(charSequence, this.mMediaTitle) || !TextUtils.equals(charSequence2, this.mMediaArtist)) {
            this.mMediaTitle = charSequence;
            this.mMediaArtist = charSequence2;
            this.mMediaIsVisible = isPlayingState;
            notifyChange();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyChange() {
        this.mContentResolver.notifyChange(this.mSliceUri, null);
    }

    public void onDozingChanged(boolean z) {
        boolean z2;
        synchronized (this) {
            boolean needsMediaLocked = needsMediaLocked();
            this.mDozing = z;
            z2 = needsMediaLocked != needsMediaLocked();
        }
        if (z2) {
            notifyChange();
        }
    }

    public void onStateChanged(int i) {
        boolean z;
        synchronized (this) {
            boolean needsMediaLocked = needsMediaLocked();
            this.mStatusBarState = i;
            z = needsMediaLocked != needsMediaLocked();
        }
        if (z) {
            notifyChange();
        }
    }

    public void setContextAvailableCallback(ContextAvailableCallback contextAvailableCallback) {
        this.mContextAvailableCallback = contextAvailableCallback;
    }
}
