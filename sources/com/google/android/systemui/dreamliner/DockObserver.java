package com.google.android.systemui.dreamliner;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamManager.Stub;
import android.util.Log;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManager.AlignmentStateListener;
import com.android.systemui.dock.DockManager.DockEventListener;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptSuppressor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DockObserver extends BroadcastReceiver implements DockManager {
    @VisibleForTesting
    static final String ACTION_ALIGN_STATE_CHANGE = "com.google.android.systemui.dreamliner.ALIGNMENT_CHANGE";
    @VisibleForTesting
    static final String ACTION_CHALLENGE = "com.google.android.systemui.dreamliner.ACTION_CHALLENGE";
    @VisibleForTesting
    static final String ACTION_DOCK_UI_ACTIVE = "com.google.android.systemui.dreamliner.ACTION_DOCK_UI_ACTIVE";
    @VisibleForTesting
    static final String ACTION_DOCK_UI_IDLE = "com.google.android.systemui.dreamliner.ACTION_DOCK_UI_IDLE";
    @VisibleForTesting
    static final String ACTION_GET_DOCK_INFO = "com.google.android.systemui.dreamliner.ACTION_GET_DOCK_INFO";
    @VisibleForTesting
    static final String ACTION_KEY_EXCHANGE = "com.google.android.systemui.dreamliner.ACTION_KEY_EXCHANGE";
    @VisibleForTesting
    static final String ACTION_REBIND_DOCK_SERVICE = "com.google.android.systemui.dreamliner.ACTION_REBIND_DOCK_SERVICE";
    @VisibleForTesting
    static final String ACTION_START_DREAMLINER_CONTROL_SERVICE = "com.google.android.apps.dreamliner.START";
    @VisibleForTesting
    static final String COMPONENTNAME_DREAMLINER_CONTROL_SERVICE = "com.google.android.apps.dreamliner/.DreamlinerControlService";
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("DLObserver", 3);
    @VisibleForTesting
    static final String EXTRA_ALIGN_STATE = "align_state";
    @VisibleForTesting
    static final String EXTRA_CHALLENGE_DATA = "challenge_data";
    @VisibleForTesting
    static final String EXTRA_CHALLENGE_DOCK_ID = "challenge_dock_id";
    @VisibleForTesting
    static final String EXTRA_PUBLIC_KEY = "public_key";
    @VisibleForTesting
    static final String KEY_SHOWING = "showing";
    @VisibleForTesting
    static final int RESULT_NOT_FOUND = 1;
    @VisibleForTesting
    static final int RESULT_OK = 0;
    @VisibleForTesting
    static volatile ExecutorService mSingleThreadExecutor;
    /* access modifiers changed from: private */
    public static boolean sIsDockingUiShowing = DEBUG;
    private final List<AlignmentStateListener> mAlignmentStateListeners;
    private final List<DockEventListener> mClients;
    private final Context mContext;
    private DockAlignmentController mDockAlignmentController;
    @VisibleForTesting
    DockGestureController mDockGestureController;
    @VisibleForTesting
    int mDockState = 0;
    private ImageView mDreamlinerGear;
    @VisibleForTesting
    final DreamlinerBroadcastReceiver mDreamlinerReceiver = new DreamlinerBroadcastReceiver();
    @VisibleForTesting
    DreamlinerServiceConn mDreamlinerServiceConn;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public DockIndicationController mIndicationController;
    private final NotificationInterruptSuppressor mInterruptSuppressor = new NotificationInterruptSuppressor(this) {
        public String getName() {
            return "DLObserver";
        }

        public boolean suppressInterruptions(NotificationEntry notificationEntry) {
            return DockObserver.isDockingUiShowing();
        }
    };
    @VisibleForTesting
    int mLastAlignState = -1;
    private final StatusBarStateController mStatusBarStateController;
    private final CurrentUserTracker mUserTracker;
    /* access modifiers changed from: private */
    public final WirelessCharger mWirelessCharger;

    @VisibleForTesting
    final class ChallengeCallback implements com.google.android.systemui.dreamliner.WirelessCharger.ChallengeCallback {
        private final ResultReceiver mResultReceiver;

        ChallengeCallback(ResultReceiver resultReceiver) {
            this.mResultReceiver = resultReceiver;
        }

        public void onCallback(int i, ArrayList<Byte> arrayList) {
            String str = "DLObserver";
            if (DockObserver.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("challenge() Result: ");
                sb.append(i);
                Log.d(str, sb.toString());
            }
            if (i == 0) {
                if (DockObserver.DEBUG) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("challenge() response: ");
                    sb2.append(arrayList);
                    Log.d(str, sb2.toString());
                }
                this.mResultReceiver.send(0, DockObserver.this.createChallengeResponseBundle(arrayList));
                return;
            }
            this.mResultReceiver.send(1, null);
        }
    }

    private class ChallengeWithDock implements Runnable {
        final byte[] challengeData;
        final byte dockId;
        final ResultReceiver resultReceiver;

        public ChallengeWithDock(ResultReceiver resultReceiver2, byte b, byte[] bArr) {
            this.dockId = b;
            this.challengeData = bArr;
            this.resultReceiver = resultReceiver2;
        }

        public void run() {
            if (DockObserver.this.mWirelessCharger != null) {
                DockObserver.this.mWirelessCharger.challenge(this.dockId, this.challengeData, new ChallengeCallback(this.resultReceiver));
            }
        }
    }

    @VisibleForTesting
    class DreamlinerBroadcastReceiver extends BroadcastReceiver {
        private boolean mListening;

        DreamlinerBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (DockObserver.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Dock Receiver.onReceive(): ");
                    sb.append(intent.getAction());
                    Log.d("DLObserver", sb.toString());
                }
                String action = intent.getAction();
                char c = 65535;
                switch (action.hashCode()) {
                    case -1616532553:
                        if (action.equals(DockObserver.ACTION_GET_DOCK_INFO)) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1598391011:
                        if (action.equals(DockObserver.ACTION_KEY_EXCHANGE)) {
                            c = 3;
                            break;
                        }
                        break;
                    case -1579804275:
                        if (action.equals(DockObserver.ACTION_DOCK_UI_IDLE)) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1573996717:
                        if (action.equals("com.google.android.systemui.dreamliner.photo_promo")) {
                            c = 11;
                            break;
                        }
                        break;
                    case -1458969207:
                        if (action.equals(DockObserver.ACTION_CHALLENGE)) {
                            c = 4;
                            break;
                        }
                        break;
                    case -545730616:
                        if (action.equals("com.google.android.systemui.dreamliner.paired")) {
                            c = 6;
                            break;
                        }
                        break;
                    case -484477188:
                        if (action.equals("com.google.android.systemui.dreamliner.resume")) {
                            c = 7;
                            break;
                        }
                        break;
                    case -390730981:
                        if (action.equals("com.google.android.systemui.dreamliner.undock")) {
                            c = 9;
                            break;
                        }
                        break;
                    case 664552276:
                        if (action.equals("com.google.android.systemui.dreamliner.dream")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 675144007:
                        if (action.equals("com.google.android.systemui.dreamliner.pause")) {
                            c = 8;
                            break;
                        }
                        break;
                    case 717413661:
                        if (action.equals("com.google.android.systemui.dreamliner.assistant_poodle")) {
                            c = 10;
                            break;
                        }
                        break;
                    case 1996802687:
                        if (action.equals(DockObserver.ACTION_DOCK_UI_ACTIVE)) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
                        if (resultReceiver != null) {
                            DockObserver.runOnBackgroundThread(new GetDockInfo(resultReceiver, context));
                            break;
                        }
                        break;
                    case 1:
                        DockObserver.this.sendDockIdleIntent(context);
                        DockObserver.sIsDockingUiShowing = true;
                        break;
                    case 2:
                        DockObserver.this.sendDockActiveIntent(context);
                        DockObserver.sIsDockingUiShowing = DockObserver.DEBUG;
                        break;
                    case 3:
                        DockObserver.this.triggerKeyExchangeWithDock(intent);
                        break;
                    case 4:
                        DockObserver.this.triggerChallengeWithDock(intent);
                        break;
                    case 5:
                        DockObserver.this.tryTurnScreenOff(context);
                        break;
                    case 6:
                    case 7:
                        DockObserver.this.onDockStateChanged(1);
                        DockObserver dockObserver = DockObserver.this;
                        if (dockObserver.assertNotNull(dockObserver.mDockGestureController, DockGestureController.class.getSimpleName())) {
                            DockObserver.this.mDockGestureController.startMonitoring();
                            break;
                        }
                        break;
                    case 8:
                        DockObserver.this.onDockStateChanged(2);
                        DockObserver dockObserver2 = DockObserver.this;
                        if (dockObserver2.assertNotNull(dockObserver2.mDockGestureController, DockGestureController.class.getSimpleName())) {
                            DockObserver.this.mDockGestureController.stopMonitoring();
                            break;
                        }
                        break;
                    case 9:
                        DockObserver.this.onDockStateChanged(0);
                        DockObserver dockObserver3 = DockObserver.this;
                        if (dockObserver3.assertNotNull(dockObserver3.mDockGestureController, DockGestureController.class.getSimpleName())) {
                            DockObserver.this.mDockGestureController.stopMonitoring();
                            break;
                        }
                        break;
                    case 10:
                        if (DockObserver.this.mIndicationController != null) {
                            DockObserver.this.mIndicationController.setShowing(intent.getBooleanExtra(DockObserver.KEY_SHOWING, DockObserver.DEBUG));
                            break;
                        }
                        break;
                    case 11:
                        DockObserver.this.triggerPhotoPromo(intent);
                        break;
                }
            }
        }

        private IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DockObserver.ACTION_GET_DOCK_INFO);
            intentFilter.addAction(DockObserver.ACTION_DOCK_UI_IDLE);
            intentFilter.addAction(DockObserver.ACTION_DOCK_UI_ACTIVE);
            intentFilter.addAction(DockObserver.ACTION_KEY_EXCHANGE);
            intentFilter.addAction(DockObserver.ACTION_CHALLENGE);
            intentFilter.addAction("com.google.android.systemui.dreamliner.dream");
            intentFilter.addAction("com.google.android.systemui.dreamliner.paired");
            intentFilter.addAction("com.google.android.systemui.dreamliner.pause");
            intentFilter.addAction("com.google.android.systemui.dreamliner.resume");
            intentFilter.addAction("com.google.android.systemui.dreamliner.undock");
            intentFilter.addAction("com.google.android.systemui.dreamliner.assistant_poodle");
            intentFilter.addAction("com.google.android.systemui.dreamliner.photo_promo");
            return intentFilter;
        }

        public void registerReceiver(Context context) {
            if (!this.mListening) {
                context.registerReceiverAsUser(this, UserHandle.ALL, getIntentFilter(), "com.google.android.systemui.permission.WIRELESS_CHARGER_STATUS", null);
                this.mListening = true;
            }
        }

        public void unregisterReceiver(Context context) {
            if (this.mListening) {
                context.unregisterReceiver(this);
                this.mListening = DockObserver.DEBUG;
            }
        }
    }

    @VisibleForTesting
    final class DreamlinerServiceConn implements ServiceConnection {
        final Context mContext;

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        public DreamlinerServiceConn(Context context) {
            this.mContext = context;
        }

        public void onServiceDisconnected(ComponentName componentName) {
            DockObserver.this.sendDockActiveIntent(this.mContext);
        }

        public void onBindingDied(ComponentName componentName) {
            DockObserver.this.stopDreamlinerService(this.mContext);
            DockObserver.sIsDockingUiShowing = DockObserver.DEBUG;
        }
    }

    private class GetDockInfo implements Runnable {
        final ResultReceiver resultReceiver;

        public GetDockInfo(ResultReceiver resultReceiver2, Context context) {
            this.resultReceiver = resultReceiver2;
        }

        public void run() {
            if (DockObserver.this.mWirelessCharger != null) {
                DockObserver.this.mWirelessCharger.getInformation(new GetInformationCallback(DockObserver.this, this.resultReceiver));
            }
        }
    }

    @VisibleForTesting
    final class GetInformationCallback implements com.google.android.systemui.dreamliner.WirelessCharger.GetInformationCallback {
        private final ResultReceiver mResultReceiver;

        GetInformationCallback(DockObserver dockObserver, ResultReceiver resultReceiver) {
            this.mResultReceiver = resultReceiver;
        }

        public void onCallback(int i, DockInfo dockInfo) {
            String str = "DLObserver";
            if (DockObserver.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("getInformation() Result: ");
                sb.append(i);
                Log.d(str, sb.toString());
            }
            if (i == 0) {
                if (DockObserver.DEBUG) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("getInformation() DockInfo: ");
                    sb2.append(dockInfo.toString());
                    Log.d(str, sb2.toString());
                }
                this.mResultReceiver.send(0, dockInfo.toBundle());
            } else if (i != 1) {
                this.mResultReceiver.send(1, null);
            }
        }
    }

    private class IsDockPresent implements Runnable {
        final Context context;

        public IsDockPresent(Context context2) {
            this.context = context2;
        }

        public void run() {
            if (DockObserver.this.mWirelessCharger != null) {
                DockObserver.this.mWirelessCharger.asyncIsDockPresent(new IsDockPresentCallback(this.context));
            }
        }
    }

    @VisibleForTesting
    final class IsDockPresentCallback implements com.google.android.systemui.dreamliner.WirelessCharger.IsDockPresentCallback {
        private final Context mContext;

        IsDockPresentCallback(Context context) {
            this.mContext = context;
        }

        public void onCallback(boolean z, byte b, byte b2, boolean z2, int i) {
            if (DockObserver.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("isDockPresent() docked: ");
                sb.append(z);
                sb.append(", id: ");
                sb.append(i);
                Log.i("DLObserver", sb.toString());
            }
            if (z) {
                DockObserver.this.startDreamlinerService(this.mContext, b, b2, i);
            }
        }
    }

    @VisibleForTesting
    final class KeyExchangeCallback implements com.google.android.systemui.dreamliner.WirelessCharger.KeyExchangeCallback {
        private final ResultReceiver mResultReceiver;

        KeyExchangeCallback(ResultReceiver resultReceiver) {
            this.mResultReceiver = resultReceiver;
        }

        public void onCallback(int i, byte b, ArrayList<Byte> arrayList) {
            String str = "DLObserver";
            if (DockObserver.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("keyExchange() Result: ");
                sb.append(i);
                Log.d(str, sb.toString());
            }
            if (i == 0) {
                if (DockObserver.DEBUG) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("keyExchange() key: ");
                    sb2.append(arrayList);
                    Log.d(str, sb2.toString());
                }
                this.mResultReceiver.send(0, DockObserver.this.createKeyExchangeResponseBundle(b, arrayList));
                return;
            }
            this.mResultReceiver.send(1, null);
        }
    }

    private class KeyExchangeWithDock implements Runnable {
        final byte[] publicKey;
        final ResultReceiver resultReceiver;

        public KeyExchangeWithDock(ResultReceiver resultReceiver2, byte[] bArr) {
            this.publicKey = bArr;
            this.resultReceiver = resultReceiver2;
        }

        public void run() {
            if (DockObserver.this.mWirelessCharger != null) {
                DockObserver.this.mWirelessCharger.keyExchange(this.publicKey, new KeyExchangeCallback(this.resultReceiver));
            }
        }
    }

    public DockObserver(final Context context, WirelessCharger wirelessCharger, BroadcastDispatcher broadcastDispatcher, StatusBarStateController statusBarStateController, NotificationInterruptStateProvider notificationInterruptStateProvider) {
        this.mContext = context;
        this.mClients = new ArrayList();
        this.mAlignmentStateListeners = new ArrayList();
        this.mUserTracker = new CurrentUserTracker(broadcastDispatcher) {
            public void onUserSwitched(int i) {
                DockObserver.this.stopDreamlinerService(context);
                DockObserver.this.updateCurrentDockingStatus(context);
            }
        };
        this.mWirelessCharger = wirelessCharger;
        this.mStatusBarStateController = statusBarStateController;
        context.registerReceiver(this, getPowerConnectedIntentFilter());
        this.mDockAlignmentController = new DockAlignmentController(wirelessCharger, this);
        notificationInterruptStateProvider.addSuppressor(this.mInterruptSuppressor);
    }

    public void registerDockAlignInfo() {
        this.mDockAlignmentController.registerAlignInfoListener();
    }

    public void setDreamlinerGear(ImageView imageView) {
        this.mDreamlinerGear = imageView;
    }

    public void setIndicationController(DockIndicationController dockIndicationController) {
        this.mIndicationController = dockIndicationController;
    }

    public void addListener(DockEventListener dockEventListener) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("add listener: ");
            sb.append(dockEventListener);
            Log.d("DLObserver", sb.toString());
        }
        if (!this.mClients.contains(dockEventListener)) {
            this.mClients.add(dockEventListener);
        }
        this.mHandler.post(new Runnable(dockEventListener) {
            public final /* synthetic */ DockEventListener f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DockObserver.this.lambda$addListener$0$DockObserver(this.f$1);
            }
        });
    }

    public void removeListener(DockEventListener dockEventListener) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("remove listener: ");
            sb.append(dockEventListener);
            Log.d("DLObserver", sb.toString());
        }
        this.mClients.remove(dockEventListener);
    }

    public boolean isDocked() {
        int i = this.mDockState;
        if (i == 1 || i == 2) {
            return true;
        }
        return DEBUG;
    }

    public boolean isHidden() {
        if (this.mDockState == 2) {
            return true;
        }
        return DEBUG;
    }

    public void addAlignmentStateListener(AlignmentStateListener alignmentStateListener) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("add alignment listener: ");
            sb.append(alignmentStateListener);
            Log.d("DLObserver", sb.toString());
        }
        if (!this.mAlignmentStateListeners.contains(alignmentStateListener)) {
            this.mAlignmentStateListeners.add(alignmentStateListener);
        }
    }

    /* access modifiers changed from: private */
    public void tryTurnScreenOff(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        if (powerManager.isScreenOn()) {
            powerManager.goToSleep(SystemClock.uptimeMillis());
        }
    }

    /* access modifiers changed from: private */
    public void onDockStateChanged(int i) {
        if (this.mDockState != i) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("dock state changed from ");
                sb.append(this.mDockState);
                sb.append(" to ");
                sb.append(i);
                Log.d("DLObserver", sb.toString());
            }
            int i2 = this.mDockState;
            this.mDockState = i;
            for (int i3 = 0; i3 < this.mClients.size(); i3++) {
                lambda$addListener$0((DockEventListener) this.mClients.get(i3));
            }
            DockIndicationController dockIndicationController = this.mIndicationController;
            if (dockIndicationController != null) {
                dockIndicationController.setDocking(isDocked());
            }
            if (i2 == 0 && i == 1) {
                notifyDreamlinerAlignStateChanged(this.mLastAlignState);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void onAlignStateChanged(int i) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("onAlignStateChanged alignState = ");
            sb.append(i);
            Log.d("DLObserver", sb.toString());
        }
        this.mLastAlignState = i;
        for (AlignmentStateListener onAlignmentStateChanged : this.mAlignmentStateListeners) {
            onAlignmentStateChanged.onAlignmentStateChanged(i);
        }
        notifyDreamlinerAlignStateChanged(i);
    }

    private void notifyDreamlinerAlignStateChanged(int i) {
        if (isDocked()) {
            this.mContext.sendBroadcastAsUser(new Intent(ACTION_ALIGN_STATE_CHANGE).putExtra(EXTRA_ALIGN_STATE, i).addFlags(1073741824), UserHandle.CURRENT);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: dispatchDockEvent */
    public void lambda$addListener$0(DockEventListener dockEventListener) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("onDockEvent mDockState = ");
            sb.append(this.mDockState);
            Log.d("DLObserver", sb.toString());
        }
        dockEventListener.onEvent(this.mDockState);
    }

    private final Intent getBatteryStatus(Context context) {
        return context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    private boolean isChargingOrFull(Intent intent) {
        int intExtra = intent.getIntExtra("status", -1);
        if (intExtra == 2 || intExtra == 5) {
            return true;
        }
        return DEBUG;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public final void updateCurrentDockingStatus(Context context) {
        notifyForceEnabledAmbientDisplay(DEBUG);
        if (isChargingOrFull(getBatteryStatus(context)) && this.mWirelessCharger != null) {
            runOnBackgroundThread(new IsDockPresent(context));
        }
    }

    private IntentFilter getPowerConnectedIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction(ACTION_REBIND_DOCK_SERVICE);
        intentFilter.setPriority(1000);
        return intentFilter;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onReceive(); ");
                sb.append(intent.getAction());
                Log.i("DLObserver", sb.toString());
            }
            String action = intent.getAction();
            char c = 65535;
            switch (action.hashCode()) {
                case -1886648615:
                    if (action.equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
                        c = 1;
                        break;
                    }
                    break;
                case 798292259:
                    if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1019184907:
                    if (action.equals("android.intent.action.ACTION_POWER_CONNECTED")) {
                        c = 0;
                        break;
                    }
                    break;
                case 1318602046:
                    if (action.equals(ACTION_REBIND_DOCK_SERVICE)) {
                        c = 2;
                        break;
                    }
                    break;
            }
            if (c != 0) {
                if (c == 1) {
                    stopDreamlinerService(context);
                    sIsDockingUiShowing = DEBUG;
                } else if (c == 2 || c == 3) {
                    updateCurrentDockingStatus(context);
                }
            } else if (this.mWirelessCharger != null) {
                runOnBackgroundThread(new IsDockPresent(context));
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0091, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void startDreamlinerService(android.content.Context r6, int r7, int r8, int r9) {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 1
            r5.notifyForceEnabledAmbientDisplay(r0)     // Catch:{ all -> 0x0092 }
            com.google.android.systemui.dreamliner.DockObserver$DreamlinerServiceConn r1 = r5.mDreamlinerServiceConn     // Catch:{ all -> 0x0092 }
            if (r1 != 0) goto L_0x0090
            com.google.android.systemui.dreamliner.DockObserver$DreamlinerBroadcastReceiver r1 = r5.mDreamlinerReceiver     // Catch:{ all -> 0x0092 }
            r1.registerReceiver(r6)     // Catch:{ all -> 0x0092 }
            com.google.android.systemui.dreamliner.DockGestureController r1 = new com.google.android.systemui.dreamliner.DockGestureController     // Catch:{ all -> 0x0092 }
            android.widget.ImageView r2 = r5.mDreamlinerGear     // Catch:{ all -> 0x0092 }
            android.widget.ImageView r3 = r5.mDreamlinerGear     // Catch:{ all -> 0x0092 }
            android.view.ViewParent r3 = r3.getParent()     // Catch:{ all -> 0x0092 }
            android.view.View r3 = (android.view.View) r3     // Catch:{ all -> 0x0092 }
            com.android.systemui.plugins.statusbar.StatusBarStateController r4 = r5.mStatusBarStateController     // Catch:{ all -> 0x0092 }
            r1.<init>(r6, r2, r3, r4)     // Catch:{ all -> 0x0092 }
            r5.mDockGestureController = r1     // Catch:{ all -> 0x0092 }
            android.content.Intent r1 = new android.content.Intent     // Catch:{ all -> 0x0092 }
            java.lang.String r2 = "com.google.android.apps.dreamliner.START"
            r1.<init>(r2)     // Catch:{ all -> 0x0092 }
            java.lang.String r2 = "com.google.android.apps.dreamliner/.DreamlinerControlService"
            android.content.ComponentName r2 = android.content.ComponentName.unflattenFromString(r2)     // Catch:{ all -> 0x0092 }
            r1.setComponent(r2)     // Catch:{ all -> 0x0092 }
            java.lang.String r2 = "type"
            r1.putExtra(r2, r7)     // Catch:{ all -> 0x0092 }
            java.lang.String r7 = "orientation"
            r1.putExtra(r7, r8)     // Catch:{ all -> 0x0092 }
            java.lang.String r7 = "id"
            r1.putExtra(r7, r9)     // Catch:{ all -> 0x0092 }
            java.lang.String r7 = "occluded"
            com.google.android.systemui.elmyra.gates.KeyguardVisibility r8 = new com.google.android.systemui.elmyra.gates.KeyguardVisibility     // Catch:{ all -> 0x0092 }
            r8.<init>(r6)     // Catch:{ all -> 0x0092 }
            boolean r8 = r8.isKeyguardOccluded()     // Catch:{ all -> 0x0092 }
            r1.putExtra(r7, r8)     // Catch:{ all -> 0x0092 }
            com.google.android.systemui.dreamliner.DockObserver$DreamlinerServiceConn r7 = new com.google.android.systemui.dreamliner.DockObserver$DreamlinerServiceConn     // Catch:{ SecurityException -> 0x006d }
            r7.<init>(r6)     // Catch:{ SecurityException -> 0x006d }
            r5.mDreamlinerServiceConn = r7     // Catch:{ SecurityException -> 0x006d }
            android.os.UserHandle r8 = new android.os.UserHandle     // Catch:{ SecurityException -> 0x006d }
            com.android.systemui.settings.CurrentUserTracker r9 = r5.mUserTracker     // Catch:{ SecurityException -> 0x006d }
            int r9 = r9.getCurrentUserId()     // Catch:{ SecurityException -> 0x006d }
            r8.<init>(r9)     // Catch:{ SecurityException -> 0x006d }
            boolean r6 = r6.bindServiceAsUser(r1, r7, r0, r8)     // Catch:{ SecurityException -> 0x006d }
            if (r6 == 0) goto L_0x0077
            com.android.systemui.settings.CurrentUserTracker r6 = r5.mUserTracker     // Catch:{ SecurityException -> 0x006d }
            r6.startTracking()     // Catch:{ SecurityException -> 0x006d }
            monitor-exit(r5)
            return
        L_0x006d:
            r6 = move-exception
            java.lang.String r7 = "DLObserver"
            java.lang.String r8 = r6.getMessage()     // Catch:{ all -> 0x0092 }
            android.util.Log.e(r7, r8, r6)     // Catch:{ all -> 0x0092 }
        L_0x0077:
            r6 = 0
            r5.mDreamlinerServiceConn = r6     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = "DLObserver"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r7.<init>()     // Catch:{ all -> 0x0092 }
            java.lang.String r8 = "Unable to bind Dreamliner service: "
            r7.append(r8)     // Catch:{ all -> 0x0092 }
            r7.append(r1)     // Catch:{ all -> 0x0092 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0092 }
            android.util.Log.w(r6, r7)     // Catch:{ all -> 0x0092 }
        L_0x0090:
            monitor-exit(r5)
            return
        L_0x0092:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.dreamliner.DockObserver.startDreamlinerService(android.content.Context, int, int, int):void");
    }

    /* access modifiers changed from: private */
    public void stopDreamlinerService(Context context) {
        notifyForceEnabledAmbientDisplay(DEBUG);
        onDockStateChanged(0);
        try {
            if (this.mDreamlinerServiceConn != null) {
                if (assertNotNull(this.mDockGestureController, DockGestureController.class.getSimpleName())) {
                    this.mDockGestureController.stopMonitoring();
                    this.mDockGestureController = null;
                }
                this.mUserTracker.stopTracking();
                this.mDreamlinerReceiver.unregisterReceiver(context);
                context.unbindService(this.mDreamlinerServiceConn);
                this.mDreamlinerServiceConn = null;
            }
        } catch (IllegalArgumentException e) {
            Log.e("DLObserver", e.getMessage(), e);
        }
    }

    /* access modifiers changed from: private */
    public boolean assertNotNull(Object obj, String str) {
        if (obj != null) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" is null");
        Log.w("DLObserver", sb.toString());
        return DEBUG;
    }

    private void notifyForceEnabledAmbientDisplay(boolean z) {
        IDreamManager dreamManagerInstance = getDreamManagerInstance();
        if (dreamManagerInstance != null) {
            try {
                dreamManagerInstance.forceAmbientDisplayEnabled(z);
            } catch (RemoteException unused) {
            }
        } else {
            Log.e("DLObserver", "DreamManager not found");
        }
    }

    private IDreamManager getDreamManagerInstance() {
        return Stub.asInterface(ServiceManager.checkService("dreams"));
    }

    /* access modifiers changed from: private */
    public void sendDockIdleIntent(Context context) {
        if (DEBUG) {
            Log.d("DLObserver", "sendDockIdleIntent()");
        }
        context.sendBroadcast(new Intent("android.intent.action.DOCK_IDLE").addFlags(1073741824));
    }

    /* access modifiers changed from: private */
    public void sendDockActiveIntent(Context context) {
        if (DEBUG) {
            Log.d("DLObserver", "sendDockActiveIntent()");
        }
        context.sendBroadcast(new Intent("android.intent.action.DOCK_ACTIVE").addFlags(1073741824));
    }

    /* access modifiers changed from: private */
    public void triggerKeyExchangeWithDock(Intent intent) {
        if (DEBUG) {
            Log.d("DLObserver", "triggerKeyExchangeWithDock");
        }
        if (intent != null) {
            ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
            if (resultReceiver != null) {
                byte[] byteArrayExtra = intent.getByteArrayExtra(EXTRA_PUBLIC_KEY);
                if (byteArrayExtra == null || byteArrayExtra.length <= 0) {
                    resultReceiver.send(1, null);
                } else {
                    runOnBackgroundThread(new KeyExchangeWithDock(resultReceiver, byteArrayExtra));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void triggerChallengeWithDock(Intent intent) {
        if (DEBUG) {
            Log.d("DLObserver", "triggerChallengeWithDock");
        }
        if (intent != null) {
            ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
            if (resultReceiver != null) {
                byte byteExtra = intent.getByteExtra(EXTRA_CHALLENGE_DOCK_ID, -1);
                byte[] byteArrayExtra = intent.getByteArrayExtra(EXTRA_CHALLENGE_DATA);
                if (byteArrayExtra == null || byteArrayExtra.length <= 0 || byteExtra < 0) {
                    resultReceiver.send(1, null);
                } else {
                    runOnBackgroundThread(new ChallengeWithDock(resultReceiver, byteExtra, byteArrayExtra));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void triggerPhotoPromo(Intent intent) {
        if (DEBUG) {
            Log.d("DLObserver", "triggerPhotoPromo");
        }
        if (intent != null) {
            ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
            if (resultReceiver != null) {
                DockIndicationController dockIndicationController = this.mIndicationController;
                if (dockIndicationController != null) {
                    dockIndicationController.showPromo(resultReceiver);
                }
            }
        }
    }

    private byte[] convertArrayListToPrimitiveArray(ArrayList<Byte> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        int size = arrayList.size();
        byte[] bArr = new byte[size];
        for (int i = 0; i < size; i++) {
            bArr[i] = ((Byte) arrayList.get(i)).byteValue();
        }
        return bArr;
    }

    /* access modifiers changed from: private */
    public Bundle createKeyExchangeResponseBundle(byte b, ArrayList<Byte> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        byte[] convertArrayListToPrimitiveArray = convertArrayListToPrimitiveArray(arrayList);
        Bundle bundle = new Bundle();
        bundle.putByte("dock_id", b);
        bundle.putByteArray("dock_public_key", convertArrayListToPrimitiveArray);
        return bundle;
    }

    /* access modifiers changed from: private */
    public Bundle createChallengeResponseBundle(ArrayList<Byte> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        byte[] convertArrayListToPrimitiveArray = convertArrayListToPrimitiveArray(arrayList);
        Bundle bundle = new Bundle();
        bundle.putByteArray("challenge_response", convertArrayListToPrimitiveArray);
        return bundle;
    }

    /* access modifiers changed from: private */
    public static void runOnBackgroundThread(Runnable runnable) {
        if (mSingleThreadExecutor == null) {
            mSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        mSingleThreadExecutor.execute(runnable);
    }

    public static boolean isDockingUiShowing() {
        return sIsDockingUiShowing;
    }
}
