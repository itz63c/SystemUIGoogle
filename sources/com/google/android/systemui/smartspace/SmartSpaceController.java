package com.google.android.systemui.smartspace;

import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.util.KeyValueListParser;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.smartspace.nano.SmartspaceProto$CardWrapper;
import com.android.systemui.util.Assert;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SmartSpaceController implements Dumpable {
    static final boolean DEBUG = Log.isLoggable("SmartSpaceController", 3);
    private final AlarmManager mAlarmManager;
    private boolean mAlarmRegistered;
    private final Context mAppContext;
    private final Handler mBackgroundHandler;
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUserId;
    /* access modifiers changed from: private */
    public final SmartSpaceData mData;
    private final OnAlarmListener mExpireAlarmAction = new OnAlarmListener() {
        public final void onAlarm() {
            SmartSpaceController.this.lambda$new$0$SmartSpaceController();
        }
    };
    private boolean mHidePrivateData;
    private boolean mHideWorkData;
    private final KeyguardUpdateMonitorCallback mKeyguardMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onTimeChanged() {
            if (SmartSpaceController.this.mData != null && SmartSpaceController.this.mData.hasCurrent() && SmartSpaceController.this.mData.getExpirationRemainingMillis() > 0) {
                SmartSpaceController.this.update();
            }
        }
    };
    private final ArrayList<SmartSpaceUpdateListener> mListeners = new ArrayList<>();
    private boolean mSmartSpaceEnabledBroadcastSent;
    private final ProtoStore mStore;
    private final Handler mUiHandler;

    private class UserSwitchReceiver extends BroadcastReceiver {
        private UserSwitchReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (SmartSpaceController.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Switching user: ");
                sb.append(intent.getAction());
                sb.append(" uid: ");
                sb.append(UserHandle.myUserId());
                Log.d("SmartSpaceController", sb.toString());
            }
            if (intent.getAction().equals("android.intent.action.USER_SWITCHED")) {
                SmartSpaceController.this.mCurrentUserId = intent.getIntExtra("android.intent.extra.user_handle", -1);
                SmartSpaceController.this.mData.clear();
                SmartSpaceController.this.onExpire(true);
            }
            SmartSpaceController.this.onExpire(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$SmartSpaceController() {
        onExpire(false);
    }

    public SmartSpaceController(Context context, KeyguardUpdateMonitor keyguardUpdateMonitor, Handler handler, AlarmManager alarmManager, DumpManager dumpManager) {
        this.mContext = context;
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mStore = new ProtoStore(this.mContext);
        new HandlerThread("smartspace-background").start();
        this.mBackgroundHandler = handler;
        this.mCurrentUserId = UserHandle.myUserId();
        this.mAppContext = context;
        this.mAlarmManager = alarmManager;
        this.mData = new SmartSpaceData();
        if (!isSmartSpaceDisabledByExperiments()) {
            keyguardUpdateMonitor.registerCallback(this.mKeyguardMonitorCallback);
            reloadData();
            onGsaChanged();
            context.registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    SmartSpaceController.this.onGsaChanged();
                }
            }, GSAIntents.getGsaPackageFilter("android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED", "android.intent.action.PACKAGE_DATA_CLEARED"));
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.USER_UNLOCKED");
            context.registerReceiver(new UserSwitchReceiver(), intentFilter);
            context.registerReceiver(new SmartSpaceBroadcastReceiver(this), new IntentFilter("com.google.android.apps.nexuslauncher.UPDATE_SMARTSPACE"));
            dumpManager.registerDumpable(SmartSpaceController.class.getName(), this);
        }
    }

    private SmartSpaceCard loadSmartSpaceData(boolean z) {
        SmartspaceProto$CardWrapper smartspaceProto$CardWrapper = new SmartspaceProto$CardWrapper();
        ProtoStore protoStore = this.mStore;
        StringBuilder sb = new StringBuilder();
        sb.append("smartspace_");
        sb.append(this.mCurrentUserId);
        sb.append("_");
        sb.append(z);
        if (protoStore.load(sb.toString(), smartspaceProto$CardWrapper)) {
            return SmartSpaceCard.fromWrapper(this.mContext, smartspaceProto$CardWrapper, !z);
        }
        return null;
    }

    public void onNewCard(NewCardInfo newCardInfo) {
        String str = "SmartSpaceController";
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("onNewCard: ");
            sb.append(newCardInfo);
            Log.d(str, sb.toString());
        }
        if (newCardInfo != null) {
            if (newCardInfo.getUserId() != this.mCurrentUserId) {
                if (DEBUG) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Ignore card that belongs to another user target: ");
                    sb2.append(this.mCurrentUserId);
                    sb2.append(" current: ");
                    sb2.append(this.mCurrentUserId);
                    Log.d(str, sb2.toString());
                }
                return;
            }
            this.mBackgroundHandler.post(new Runnable(newCardInfo) {
                public final /* synthetic */ NewCardInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SmartSpaceController.this.lambda$onNewCard$2$SmartSpaceController(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNewCard$2 */
    public /* synthetic */ void lambda$onNewCard$2$SmartSpaceController(NewCardInfo newCardInfo) {
        SmartspaceProto$CardWrapper wrapper = newCardInfo.toWrapper(this.mContext);
        if (!this.mHidePrivateData || !this.mHideWorkData) {
            ProtoStore protoStore = this.mStore;
            StringBuilder sb = new StringBuilder();
            sb.append("smartspace_");
            sb.append(this.mCurrentUserId);
            sb.append("_");
            sb.append(newCardInfo.isPrimary());
            protoStore.store(wrapper, sb.toString());
        }
        this.mUiHandler.post(new Runnable(newCardInfo, newCardInfo.shouldDiscard() ? null : SmartSpaceCard.fromWrapper(this.mContext, wrapper, newCardInfo.isPrimary())) {
            public final /* synthetic */ NewCardInfo f$1;
            public final /* synthetic */ SmartSpaceCard f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SmartSpaceController.this.lambda$onNewCard$1$SmartSpaceController(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNewCard$1 */
    public /* synthetic */ void lambda$onNewCard$1$SmartSpaceController(NewCardInfo newCardInfo, SmartSpaceCard smartSpaceCard) {
        if (newCardInfo.isPrimary()) {
            this.mData.mCurrentCard = smartSpaceCard;
        } else {
            this.mData.mWeatherCard = smartSpaceCard;
        }
        this.mData.handleExpire();
        update();
    }

    private void clearStore() {
        ProtoStore protoStore = this.mStore;
        StringBuilder sb = new StringBuilder();
        String str = "smartspace_";
        sb.append(str);
        sb.append(this.mCurrentUserId);
        sb.append("_true");
        protoStore.store(null, sb.toString());
        ProtoStore protoStore2 = this.mStore;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(this.mCurrentUserId);
        sb2.append("_false");
        protoStore2.store(null, sb2.toString());
    }

    /* access modifiers changed from: private */
    public void update() {
        Assert.isMainThread();
        String str = "SmartSpaceController";
        if (DEBUG) {
            Log.d(str, "update");
        }
        if (this.mAlarmRegistered) {
            this.mAlarmManager.cancel(this.mExpireAlarmAction);
            this.mAlarmRegistered = false;
        }
        long expiresAtMillis = this.mData.getExpiresAtMillis();
        if (expiresAtMillis > 0) {
            this.mAlarmManager.set(0, expiresAtMillis, "SmartSpace", this.mExpireAlarmAction, this.mUiHandler);
            this.mAlarmRegistered = true;
        }
        if (this.mListeners != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("notifying listeners data=");
                sb.append(this.mData);
                Log.d(str, sb.toString());
            }
            ArrayList arrayList = new ArrayList(this.mListeners);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((SmartSpaceUpdateListener) arrayList.get(i)).onSmartSpaceUpdated(this.mData);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onExpire(boolean z) {
        Assert.isMainThread();
        this.mAlarmRegistered = false;
        if (this.mData.handleExpire() || z) {
            update();
        } else if (DEBUG) {
            Log.d("SmartSpaceController", "onExpire - cancelled");
        }
    }

    public void setHideSensitiveData(boolean z, boolean z2) {
        if (this.mHidePrivateData != z || this.mHideWorkData != z2) {
            this.mHidePrivateData = z;
            this.mHideWorkData = z2;
            ArrayList arrayList = new ArrayList(this.mListeners);
            boolean z3 = false;
            for (int i = 0; i < arrayList.size(); i++) {
                ((SmartSpaceUpdateListener) arrayList.get(i)).onSensitiveModeChanged(z, z2);
            }
            if (this.mData.getCurrentCard() != null) {
                boolean z4 = this.mHidePrivateData && !this.mData.getCurrentCard().isWorkProfile();
                if (this.mHideWorkData && this.mData.getCurrentCard().isWorkProfile()) {
                    z3 = true;
                }
                if (z4 || z3) {
                    clearStore();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onGsaChanged() {
        if (DEBUG) {
            Log.d("SmartSpaceController", "onGsaChanged");
        }
        if (UserHandle.myUserId() == 0) {
            this.mAppContext.sendBroadcast(new Intent("com.google.android.systemui.smartspace.ENABLE_UPDATE").setPackage("com.google.android.googlequicksearchbox").addFlags(268435456));
            this.mSmartSpaceEnabledBroadcastSent = true;
        }
        ArrayList arrayList = new ArrayList(this.mListeners);
        for (int i = 0; i < arrayList.size(); i++) {
            ((SmartSpaceUpdateListener) arrayList.get(i)).onGsaChanged();
        }
    }

    public void reloadData() {
        this.mData.mCurrentCard = loadSmartSpaceData(true);
        this.mData.mWeatherCard = loadSmartSpaceData(false);
        update();
    }

    private boolean isSmartSpaceDisabledByExperiments() {
        boolean z;
        String string = Global.getString(this.mContext.getContentResolver(), "always_on_display_constants");
        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        try {
            keyValueListParser.setString(string);
            z = keyValueListParser.getBoolean("smart_space_enabled", true);
        } catch (IllegalArgumentException unused) {
            Log.e("SmartSpaceController", "Bad AOD constants");
            z = true;
        }
        return !z;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println();
        printWriter.println("SmartspaceController");
        StringBuilder sb = new StringBuilder();
        sb.append("  initial broadcast: ");
        sb.append(this.mSmartSpaceEnabledBroadcastSent);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        String str = "  weather ";
        sb2.append(str);
        sb2.append(this.mData.mWeatherCard);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        String str2 = "  current ";
        sb3.append(str2);
        sb3.append(this.mData.mCurrentCard);
        printWriter.println(sb3.toString());
        printWriter.println("serialized:");
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append(loadSmartSpaceData(false));
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(str2);
        sb5.append(loadSmartSpaceData(true));
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("disabled by experiment: ");
        sb6.append(isSmartSpaceDisabledByExperiments());
        printWriter.println(sb6.toString());
    }

    public void addListener(SmartSpaceUpdateListener smartSpaceUpdateListener) {
        Assert.isMainThread();
        this.mListeners.add(smartSpaceUpdateListener);
        SmartSpaceData smartSpaceData = this.mData;
        if (!(smartSpaceData == null || smartSpaceUpdateListener == null)) {
            smartSpaceUpdateListener.onSmartSpaceUpdated(smartSpaceData);
        }
        if (smartSpaceUpdateListener != null) {
            smartSpaceUpdateListener.onSensitiveModeChanged(this.mHidePrivateData, this.mHideWorkData);
        }
    }

    public void removeListener(SmartSpaceUpdateListener smartSpaceUpdateListener) {
        Assert.isMainThread();
        this.mListeners.remove(smartSpaceUpdateListener);
    }
}
