package com.android.systemui.statusbar.policy;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.GlobalSetting;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.policy.ZenModeController.Callback;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class ZenModeControllerImpl extends CurrentUserTracker implements ZenModeController, Dumpable {
    private final AlarmManager mAlarmManager;
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    private final Object mCallbacksLock = new Object();
    private ZenModeConfig mConfig;
    private final GlobalSetting mConfigSetting;
    private Policy mConsolidatedNotificationPolicy;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final GlobalSetting mModeSetting;
    private final NotificationManager mNoMan;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(intent.getAction())) {
                ZenModeControllerImpl.this.fireNextAlarmChanged();
            }
            if ("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED".equals(intent.getAction())) {
                ZenModeControllerImpl.this.fireEffectsSuppressorChanged();
            }
        }
    };
    private boolean mRegistered;
    private final SetupObserver mSetupObserver;
    /* access modifiers changed from: private */
    public int mUserId;
    private final UserManager mUserManager;
    private int mZenMode;
    private long mZenUpdateTime;

    private final class SetupObserver extends ContentObserver {
        private boolean mRegistered;
        private final ContentResolver mResolver;

        public SetupObserver(Handler handler) {
            super(handler);
            this.mResolver = ZenModeControllerImpl.this.mContext.getContentResolver();
        }

        public boolean isUserSetup() {
            return Secure.getIntForUser(this.mResolver, "user_setup_complete", 0, ZenModeControllerImpl.this.mUserId) != 0;
        }

        public boolean isDeviceProvisioned() {
            return Global.getInt(this.mResolver, "device_provisioned", 0) != 0;
        }

        public void register() {
            if (this.mRegistered) {
                this.mResolver.unregisterContentObserver(this);
            }
            this.mResolver.registerContentObserver(Global.getUriFor("device_provisioned"), false, this);
            this.mResolver.registerContentObserver(Secure.getUriFor("user_setup_complete"), false, this, ZenModeControllerImpl.this.mUserId);
            this.mRegistered = true;
            ZenModeControllerImpl zenModeControllerImpl = ZenModeControllerImpl.this;
            zenModeControllerImpl.fireZenAvailableChanged(zenModeControllerImpl.isZenAvailable());
        }

        public void onChange(boolean z, Uri uri) {
            if (Global.getUriFor("device_provisioned").equals(uri) || Secure.getUriFor("user_setup_complete").equals(uri)) {
                ZenModeControllerImpl zenModeControllerImpl = ZenModeControllerImpl.this;
                zenModeControllerImpl.fireZenAvailableChanged(zenModeControllerImpl.isZenAvailable());
            }
        }
    }

    static {
        Log.isLoggable("ZenModeController", 3);
    }

    public ZenModeControllerImpl(Context context, Handler handler, BroadcastDispatcher broadcastDispatcher) {
        super(broadcastDispatcher);
        this.mContext = context;
        this.mModeSetting = new GlobalSetting(this.mContext, handler, "zen_mode") {
            /* access modifiers changed from: protected */
            public void handleValueChanged(int i) {
                ZenModeControllerImpl.this.updateZenMode(i);
                ZenModeControllerImpl.this.fireZenChanged(i);
            }
        };
        this.mConfigSetting = new GlobalSetting(this.mContext, handler, "zen_mode_config_etag") {
            /* access modifiers changed from: protected */
            public void handleValueChanged(int i) {
                ZenModeControllerImpl.this.updateZenModeConfig();
            }
        };
        this.mNoMan = (NotificationManager) context.getSystemService("notification");
        this.mModeSetting.setListening(true);
        updateZenMode(this.mModeSetting.getValue());
        this.mConfigSetting.setListening(true);
        updateZenModeConfig();
        updateConsolidatedNotificationPolicy();
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        SetupObserver setupObserver = new SetupObserver(handler);
        this.mSetupObserver = setupObserver;
        setupObserver.register();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        startTracking();
    }

    public boolean isVolumeRestricted() {
        return this.mUserManager.hasUserRestriction("no_adjust_volume", new UserHandle(this.mUserId));
    }

    public boolean areNotificationsHiddenInShade() {
        if (this.mZenMode == 0 || (this.mConsolidatedNotificationPolicy.suppressedVisualEffects & 256) == 0) {
            return false;
        }
        return true;
    }

    public void addCallback(Callback callback) {
        synchronized (this.mCallbacksLock) {
            this.mCallbacks.add(callback);
        }
    }

    public void removeCallback(Callback callback) {
        synchronized (this.mCallbacksLock) {
            this.mCallbacks.remove(callback);
        }
    }

    public int getZen() {
        return this.mZenMode;
    }

    public void setZen(int i, Uri uri, String str) {
        this.mNoMan.setZenMode(i, uri, str);
    }

    public boolean isZenAvailable() {
        return this.mSetupObserver.isDeviceProvisioned() && this.mSetupObserver.isUserSetup();
    }

    public ZenRule getManualRule() {
        ZenModeConfig zenModeConfig = this.mConfig;
        if (zenModeConfig == null) {
            return null;
        }
        return zenModeConfig.manualRule;
    }

    public ZenModeConfig getConfig() {
        return this.mConfig;
    }

    public Policy getConsolidatedPolicy() {
        return this.mConsolidatedNotificationPolicy;
    }

    public long getNextAlarm() {
        AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(this.mUserId);
        if (nextAlarmClock != null) {
            return nextAlarmClock.getTriggerTime();
        }
        return 0;
    }

    public void onUserSwitched(int i) {
        this.mUserId = i;
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        IntentFilter intentFilter = new IntentFilter("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        intentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
        this.mContext.registerReceiverAsUser(this.mReceiver, new UserHandle(this.mUserId), intentFilter, null, null);
        this.mRegistered = true;
        this.mSetupObserver.register();
    }

    /* access modifiers changed from: private */
    public void fireNextAlarmChanged() {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, $$Lambda$ZenModeControllerImpl$6_S_aAoRd9fsiJr9D0TIwCJGb6M.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    public void fireEffectsSuppressorChanged() {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, $$Lambda$ZenModeControllerImpl$SV0AVEr3ZD6I5F0ZOAtC6EOynk.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    public void fireZenChanged(int i) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer(i) {
                public final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((Callback) obj).onZenChanged(this.f$0);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void fireZenAvailableChanged(boolean z) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer(z) {
                public final /* synthetic */ boolean f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((Callback) obj).onZenAvailableChanged(this.f$0);
                }
            });
        }
    }

    private void fireManualRuleChanged(ZenRule zenRule) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer(zenRule) {
                public final /* synthetic */ ZenRule f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((Callback) obj).onManualRuleChanged(this.f$0);
                }
            });
        }
    }

    private void fireConsolidatedPolicyChanged(Policy policy) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer(policy) {
                public final /* synthetic */ Policy f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((Callback) obj).onConsolidatedPolicyChanged(this.f$0);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void fireConfigChanged(ZenModeConfig zenModeConfig) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer(zenModeConfig) {
                public final /* synthetic */ ZenModeConfig f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((Callback) obj).onConfigChanged(this.f$0);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void updateZenMode(int i) {
        this.mZenMode = i;
        this.mZenUpdateTime = System.currentTimeMillis();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void updateConsolidatedNotificationPolicy() {
        Policy consolidatedNotificationPolicy = this.mNoMan.getConsolidatedNotificationPolicy();
        if (!Objects.equals(consolidatedNotificationPolicy, this.mConsolidatedNotificationPolicy)) {
            this.mConsolidatedNotificationPolicy = consolidatedNotificationPolicy;
            fireConsolidatedPolicyChanged(consolidatedNotificationPolicy);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void updateZenModeConfig() {
        ZenModeConfig zenModeConfig = this.mNoMan.getZenModeConfig();
        if (!Objects.equals(zenModeConfig, this.mConfig)) {
            ZenModeConfig zenModeConfig2 = this.mConfig;
            ZenRule zenRule = null;
            Object obj = zenModeConfig2 != null ? zenModeConfig2.manualRule : null;
            this.mConfig = zenModeConfig;
            this.mZenUpdateTime = System.currentTimeMillis();
            fireConfigChanged(zenModeConfig);
            if (zenModeConfig != null) {
                zenRule = zenModeConfig.manualRule;
            }
            if (!Objects.equals(obj, zenRule)) {
                fireManualRuleChanged(zenRule);
            }
            Policy consolidatedNotificationPolicy = this.mNoMan.getConsolidatedNotificationPolicy();
            if (!Objects.equals(consolidatedNotificationPolicy, this.mConsolidatedNotificationPolicy)) {
                this.mConsolidatedNotificationPolicy = consolidatedNotificationPolicy;
                fireConsolidatedPolicyChanged(consolidatedNotificationPolicy);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("ZenModeControllerImpl:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mZenMode=");
        sb.append(this.mZenMode);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mConfig=");
        sb2.append(this.mConfig);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mConsolidatedNotificationPolicy=");
        sb3.append(this.mConsolidatedNotificationPolicy);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mZenUpdateTime=");
        sb4.append(DateFormat.format("MM-dd HH:mm:ss", this.mZenUpdateTime));
        printWriter.println(sb4.toString());
    }
}
