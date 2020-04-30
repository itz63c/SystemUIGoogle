package com.android.systemui.volume;

import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.IVolumeController.Stub;
import android.media.VolumePolicy;
import android.media.session.MediaController.PlaybackInfo;
import android.media.session.MediaSession.Token;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.GuardedBy;
import com.android.settingslib.volume.MediaSessions;
import com.android.settingslib.volume.Util;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.tiles.DndTile;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.plugins.VolumeDialogController.Callbacks;
import com.android.systemui.plugins.VolumeDialogController.State;
import com.android.systemui.plugins.VolumeDialogController.StreamState;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class VolumeDialogControllerImpl implements VolumeDialogController, Dumpable {
    private static final AudioAttributes SONIFICIATION_VIBRATION_ATTRIBUTES = new Builder().setContentType(4).setUsage(13).build();
    static final ArrayMap<Integer, Integer> STREAMS;
    /* access modifiers changed from: private */
    public static final String TAG = Util.logTag(VolumeDialogControllerImpl.class);
    private AudioManager mAudio;
    private IAudioService mAudioService;
    protected final BroadcastDispatcher mBroadcastDispatcher;
    protected C1761C mCallbacks = new C1761C(this);
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public boolean mDestroyed;
    private final boolean mHasVibrator;
    private long mLastToggledRingerOn;
    /* access modifiers changed from: private */
    public final MediaSessions mMediaSessions;
    protected final MediaSessionsCallbacks mMediaSessionsCallbacksW = new MediaSessionsCallbacks();
    /* access modifiers changed from: private */
    public final NotificationManager mNoMan;
    private final NotificationManager mNotificationManager;
    private final SettingObserver mObserver;
    private final Receiver mReceiver = new Receiver();
    /* access modifiers changed from: private */
    public boolean mShowA11yStream;
    private boolean mShowDndTile;
    private boolean mShowSafetyWarning;
    private boolean mShowVolumeDialog;
    /* access modifiers changed from: private */
    public final State mState = new State();
    private final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy;
    @GuardedBy({"this"})
    private UserActivityListener mUserActivityListener;
    private final Vibrator mVibrator;
    protected final C1772VC mVolumeController;
    private VolumePolicy mVolumePolicy;
    /* access modifiers changed from: private */
    public final C1773W mWorker;
    private final HandlerThread mWorkerThread;

    /* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$C */
    class C1761C implements Callbacks {
        private final HashMap<Callbacks, Handler> mCallbackMap = new HashMap<>();

        C1761C(VolumeDialogControllerImpl volumeDialogControllerImpl) {
        }

        public void add(Callbacks callbacks, Handler handler) {
            if (callbacks == null || handler == null) {
                throw new IllegalArgumentException();
            }
            this.mCallbackMap.put(callbacks, handler);
        }

        public void remove(Callbacks callbacks) {
            this.mCallbackMap.remove(callbacks);
        }

        public void onShowRequested(final int i) {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onShowRequested(i);
                    }
                });
            }
        }

        public void onDismissRequested(final int i) {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onDismissRequested(i);
                    }
                });
            }
        }

        public void onStateChanged(State state) {
            long currentTimeMillis = System.currentTimeMillis();
            final State copy = state.copy();
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onStateChanged(copy);
                    }
                });
            }
            Events.writeState(currentTimeMillis, copy);
        }

        public void onLayoutDirectionChanged(final int i) {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onLayoutDirectionChanged(i);
                    }
                });
            }
        }

        public void onConfigurationChanged() {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onConfigurationChanged();
                    }
                });
            }
        }

        public void onShowVibrateHint() {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onShowVibrateHint();
                    }
                });
            }
        }

        public void onShowSilentHint() {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onShowSilentHint();
                    }
                });
            }
        }

        public void onScreenOff() {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onScreenOff();
                    }
                });
            }
        }

        public void onShowSafetyWarning(final int i) {
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onShowSafetyWarning(i);
                    }
                });
            }
        }

        public void onAccessibilityModeChanged(Boolean bool) {
            final boolean booleanValue = bool == null ? false : bool.booleanValue();
            for (final Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(this) {
                    public void run() {
                        ((Callbacks) entry.getKey()).onAccessibilityModeChanged(Boolean.valueOf(booleanValue));
                    }
                });
            }
        }

        public void onCaptionComponentStateChanged(Boolean bool, Boolean bool2) {
            boolean booleanValue = bool == null ? false : bool.booleanValue();
            for (Entry entry : this.mCallbackMap.entrySet()) {
                ((Handler) entry.getValue()).post(new Runnable(entry, booleanValue, bool2) {
                    public final /* synthetic */ Entry f$0;
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ Boolean f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ((Callbacks) this.f$0.getKey()).onCaptionComponentStateChanged(Boolean.valueOf(this.f$1), this.f$2);
                    }
                });
            }
        }
    }

    protected final class MediaSessionsCallbacks implements MediaSessions.Callbacks {
        private int mNextStream = 100;
        /* access modifiers changed from: private */
        public final HashMap<Token, Integer> mRemoteStreams = new HashMap<>();

        protected MediaSessionsCallbacks() {
        }

        public void onRemoteUpdate(Token token, String str, PlaybackInfo playbackInfo) {
            int intValue;
            addStream(token, "onRemoteUpdate");
            synchronized (this.mRemoteStreams) {
                intValue = ((Integer) this.mRemoteStreams.get(token)).intValue();
            }
            String access$300 = VolumeDialogControllerImpl.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onRemoteUpdate: stream: ");
            sb.append(intValue);
            sb.append(" volume: ");
            sb.append(playbackInfo.getCurrentVolume());
            Slog.d(access$300, sb.toString());
            boolean z = true;
            boolean z2 = VolumeDialogControllerImpl.this.mState.states.indexOfKey(intValue) < 0;
            StreamState access$3100 = VolumeDialogControllerImpl.this.streamStateW(intValue);
            access$3100.dynamic = true;
            access$3100.levelMin = 0;
            access$3100.levelMax = playbackInfo.getMaxVolume();
            if (access$3100.level != playbackInfo.getCurrentVolume()) {
                access$3100.level = playbackInfo.getCurrentVolume();
                z2 = true;
            }
            if (!Objects.equals(access$3100.remoteLabel, str)) {
                access$3100.name = -1;
                access$3100.remoteLabel = str;
            } else {
                z = z2;
            }
            if (z) {
                String access$3002 = VolumeDialogControllerImpl.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("onRemoteUpdate: ");
                sb2.append(str);
                sb2.append(": ");
                sb2.append(access$3100.level);
                sb2.append(" of ");
                sb2.append(access$3100.levelMax);
                Log.d(access$3002, sb2.toString());
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
            }
        }

        public void onRemoteVolumeChanged(Token token, int i) {
            int intValue;
            addStream(token, "onRemoteVolumeChanged");
            synchronized (this.mRemoteStreams) {
                intValue = ((Integer) this.mRemoteStreams.get(token)).intValue();
            }
            boolean access$3200 = VolumeDialogControllerImpl.this.shouldShowUI(i);
            String access$300 = VolumeDialogControllerImpl.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onRemoteVolumeChanged: stream: ");
            sb.append(intValue);
            sb.append(" showui? ");
            sb.append(access$3200);
            Slog.d(access$300, sb.toString());
            boolean access$3300 = VolumeDialogControllerImpl.this.updateActiveStreamW(intValue);
            if (access$3200) {
                access$3300 |= VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(3);
            }
            if (access$3300) {
                Slog.d(VolumeDialogControllerImpl.TAG, "onRemoteChanged: updatingState");
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
            }
            if (access$3200) {
                VolumeDialogControllerImpl.this.mCallbacks.onShowRequested(2);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0036, code lost:
            com.android.systemui.volume.VolumeDialogControllerImpl.access$2300(r3.this$0).states.remove(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0049, code lost:
            if (com.android.systemui.volume.VolumeDialogControllerImpl.access$2300(r3.this$0).activeStream != r4) goto L_0x0051;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x004b, code lost:
            com.android.systemui.volume.VolumeDialogControllerImpl.access$3300(r3.this$0, -1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0051, code lost:
            r3 = r3.this$0;
            r3.mCallbacks.onStateChanged(com.android.systemui.volume.VolumeDialogControllerImpl.access$2300(r3));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x005c, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onRemoteRemoved(android.media.session.MediaSession.Token r4) {
            /*
                r3 = this;
                java.util.HashMap<android.media.session.MediaSession$Token, java.lang.Integer> r0 = r3.mRemoteStreams
                monitor-enter(r0)
                java.util.HashMap<android.media.session.MediaSession$Token, java.lang.Integer> r1 = r3.mRemoteStreams     // Catch:{ all -> 0x005d }
                boolean r1 = r1.containsKey(r4)     // Catch:{ all -> 0x005d }
                if (r1 != 0) goto L_0x0029
                java.lang.String r3 = com.android.systemui.volume.VolumeDialogControllerImpl.TAG     // Catch:{ all -> 0x005d }
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x005d }
                r1.<init>()     // Catch:{ all -> 0x005d }
                java.lang.String r2 = "onRemoteRemoved: stream doesn't exist, aborting remote removed for token:"
                r1.append(r2)     // Catch:{ all -> 0x005d }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x005d }
                r1.append(r4)     // Catch:{ all -> 0x005d }
                java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x005d }
                android.util.Log.d(r3, r4)     // Catch:{ all -> 0x005d }
                monitor-exit(r0)     // Catch:{ all -> 0x005d }
                return
            L_0x0029:
                java.util.HashMap<android.media.session.MediaSession$Token, java.lang.Integer> r1 = r3.mRemoteStreams     // Catch:{ all -> 0x005d }
                java.lang.Object r4 = r1.get(r4)     // Catch:{ all -> 0x005d }
                java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x005d }
                int r4 = r4.intValue()     // Catch:{ all -> 0x005d }
                monitor-exit(r0)     // Catch:{ all -> 0x005d }
                com.android.systemui.volume.VolumeDialogControllerImpl r0 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                com.android.systemui.plugins.VolumeDialogController$State r0 = r0.mState
                android.util.SparseArray<com.android.systemui.plugins.VolumeDialogController$StreamState> r0 = r0.states
                r0.remove(r4)
                com.android.systemui.volume.VolumeDialogControllerImpl r0 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                com.android.systemui.plugins.VolumeDialogController$State r0 = r0.mState
                int r0 = r0.activeStream
                if (r0 != r4) goto L_0x0051
                com.android.systemui.volume.VolumeDialogControllerImpl r4 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                r0 = -1
                r4.updateActiveStreamW(r0)
            L_0x0051:
                com.android.systemui.volume.VolumeDialogControllerImpl r3 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                com.android.systemui.volume.VolumeDialogControllerImpl$C r4 = r3.mCallbacks
                com.android.systemui.plugins.VolumeDialogController$State r3 = r3.mState
                r4.onStateChanged(r3)
                return
            L_0x005d:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x005d }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.VolumeDialogControllerImpl.MediaSessionsCallbacks.onRemoteRemoved(android.media.session.MediaSession$Token):void");
        }

        public void setStreamVolume(int i, int i2) {
            Token findToken = findToken(i);
            if (findToken == null) {
                String access$300 = VolumeDialogControllerImpl.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("setStreamVolume: No token found for stream: ");
                sb.append(i);
                Log.w(access$300, sb.toString());
                return;
            }
            VolumeDialogControllerImpl.this.mMediaSessions.setVolume(findToken, i2);
        }

        private Token findToken(int i) {
            synchronized (this.mRemoteStreams) {
                for (Entry entry : this.mRemoteStreams.entrySet()) {
                    if (((Integer) entry.getValue()).equals(Integer.valueOf(i))) {
                        Token token = (Token) entry.getKey();
                        return token;
                    }
                }
                return null;
            }
        }

        private void addStream(Token token, String str) {
            synchronized (this.mRemoteStreams) {
                if (!this.mRemoteStreams.containsKey(token)) {
                    this.mRemoteStreams.put(token, Integer.valueOf(this.mNextStream));
                    String access$300 = VolumeDialogControllerImpl.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(": added stream ");
                    sb.append(this.mNextStream);
                    sb.append(" from token + ");
                    sb.append(token.toString());
                    Log.d(access$300, sb.toString());
                    this.mNextStream++;
                }
            }
        }
    }

    private final class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        public void init() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            intentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
            intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
            intentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
            intentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
            intentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
            volumeDialogControllerImpl.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, volumeDialogControllerImpl.mWorker);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String str = "android.media.EXTRA_VOLUME_STREAM_TYPE";
            boolean z = false;
            if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                int intExtra = intent.getIntExtra(str, -1);
                int intExtra2 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
                int intExtra3 = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1);
                if (C1753D.BUG) {
                    String access$300 = VolumeDialogControllerImpl.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onReceive VOLUME_CHANGED_ACTION stream=");
                    sb.append(intExtra);
                    sb.append(" level=");
                    sb.append(intExtra2);
                    sb.append(" oldLevel=");
                    sb.append(intExtra3);
                    Log.d(access$300, sb.toString());
                }
                z = VolumeDialogControllerImpl.this.updateStreamLevelW(intExtra, intExtra2);
            } else if (action.equals("android.media.STREAM_DEVICES_CHANGED_ACTION")) {
                int intExtra4 = intent.getIntExtra(str, -1);
                int intExtra5 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", -1);
                int intExtra6 = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", -1);
                if (C1753D.BUG) {
                    String access$3002 = VolumeDialogControllerImpl.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("onReceive STREAM_DEVICES_CHANGED_ACTION stream=");
                    sb2.append(intExtra4);
                    sb2.append(" devices=");
                    sb2.append(intExtra5);
                    sb2.append(" oldDevices=");
                    sb2.append(intExtra6);
                    Log.d(access$3002, sb2.toString());
                }
                z = VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(intExtra4) | VolumeDialogControllerImpl.this.onVolumeChangedW(intExtra4, 0);
            } else {
                String str2 = "android.media.EXTRA_RINGER_MODE";
                if (action.equals("android.media.RINGER_MODE_CHANGED")) {
                    int intExtra7 = intent.getIntExtra(str2, -1);
                    if (isInitialStickyBroadcast()) {
                        VolumeDialogControllerImpl.this.mState.ringerModeExternal = intExtra7;
                    }
                    if (C1753D.BUG) {
                        String access$3003 = VolumeDialogControllerImpl.TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("onReceive RINGER_MODE_CHANGED_ACTION rm=");
                        sb3.append(Util.ringerModeToString(intExtra7));
                        Log.d(access$3003, sb3.toString());
                    }
                    z = VolumeDialogControllerImpl.this.updateRingerModeExternalW(intExtra7);
                } else if (action.equals("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION")) {
                    int intExtra8 = intent.getIntExtra(str2, -1);
                    if (isInitialStickyBroadcast()) {
                        VolumeDialogControllerImpl.this.mState.ringerModeInternal = intExtra8;
                    }
                    if (C1753D.BUG) {
                        String access$3004 = VolumeDialogControllerImpl.TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("onReceive INTERNAL_RINGER_MODE_CHANGED_ACTION rm=");
                        sb4.append(Util.ringerModeToString(intExtra8));
                        Log.d(access$3004, sb4.toString());
                    }
                    z = VolumeDialogControllerImpl.this.updateRingerModeInternalW(intExtra8);
                } else if (action.equals("android.media.STREAM_MUTE_CHANGED_ACTION")) {
                    int intExtra9 = intent.getIntExtra(str, -1);
                    boolean booleanExtra = intent.getBooleanExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", false);
                    if (C1753D.BUG) {
                        String access$3005 = VolumeDialogControllerImpl.TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("onReceive STREAM_MUTE_CHANGED_ACTION stream=");
                        sb5.append(intExtra9);
                        sb5.append(" muted=");
                        sb5.append(booleanExtra);
                        Log.d(access$3005, sb5.toString());
                    }
                    z = VolumeDialogControllerImpl.this.updateStreamMuteW(intExtra9, booleanExtra);
                } else if (action.equals("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED")) {
                    if (C1753D.BUG) {
                        Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_EFFECTS_SUPPRESSOR_CHANGED");
                    }
                    VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                    z = volumeDialogControllerImpl.updateEffectsSuppressorW(volumeDialogControllerImpl.mNoMan.getEffectsSuppressor());
                } else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                    if (C1753D.BUG) {
                        Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CONFIGURATION_CHANGED");
                    }
                    VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
                } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                    if (C1753D.BUG) {
                        Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_SCREEN_OFF");
                    }
                    VolumeDialogControllerImpl.this.mCallbacks.onScreenOff();
                } else if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                    if (C1753D.BUG) {
                        Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CLOSE_SYSTEM_DIALOGS");
                    }
                    VolumeDialogControllerImpl.this.dismiss();
                }
            }
            if (z) {
                VolumeDialogControllerImpl volumeDialogControllerImpl2 = VolumeDialogControllerImpl.this;
                volumeDialogControllerImpl2.mCallbacks.onStateChanged(volumeDialogControllerImpl2.mState);
            }
        }
    }

    private final class SettingObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_URI = Global.getUriFor("zen_mode_config_etag");
        private final Uri ZEN_MODE_URI = Global.getUriFor("zen_mode");

        public SettingObserver(Handler handler) {
            super(handler);
        }

        public void init() {
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this);
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_URI, false, this);
        }

        public void onChange(boolean z, Uri uri) {
            boolean access$2100 = this.ZEN_MODE_URI.equals(uri) ? VolumeDialogControllerImpl.this.updateZenModeW() : false;
            if (this.ZEN_MODE_CONFIG_URI.equals(uri)) {
                access$2100 |= VolumeDialogControllerImpl.this.updateZenConfig();
            }
            if (access$2100) {
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
            }
        }
    }

    public interface UserActivityListener {
        void onUserActivity();
    }

    /* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$VC */
    private final class C1772VC extends Stub {
        private final String TAG;

        private C1772VC() {
            StringBuilder sb = new StringBuilder();
            sb.append(VolumeDialogControllerImpl.TAG);
            sb.append(".VC");
            this.TAG = sb.toString();
        }

        public void displaySafeVolumeWarning(int i) throws RemoteException {
            if (C1753D.BUG) {
                String str = this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("displaySafeVolumeWarning ");
                sb.append(Util.audioManagerFlagsToString(i));
                Log.d(str, sb.toString());
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(14, i, 0).sendToTarget();
            }
        }

        public void volumeChanged(int i, int i2) throws RemoteException {
            if (C1753D.BUG) {
                String str = this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("volumeChanged ");
                sb.append(AudioSystem.streamToString(i));
                sb.append(" ");
                sb.append(Util.audioManagerFlagsToString(i2));
                Log.d(str, sb.toString());
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(1, i, i2).sendToTarget();
            }
        }

        public void masterMuteChanged(int i) throws RemoteException {
            if (C1753D.BUG) {
                Log.d(this.TAG, "masterMuteChanged");
            }
        }

        public void setLayoutDirection(int i) throws RemoteException {
            if (C1753D.BUG) {
                Log.d(this.TAG, "setLayoutDirection");
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(8, i, 0).sendToTarget();
            }
        }

        public void dismiss() throws RemoteException {
            if (C1753D.BUG) {
                Log.d(this.TAG, "dismiss requested");
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(2, 2, 0).sendToTarget();
                VolumeDialogControllerImpl.this.mWorker.sendEmptyMessage(2);
            }
        }

        public void setA11yMode(int i) {
            if (C1753D.BUG) {
                String str = this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("setA11yMode to ");
                sb.append(i);
                Log.d(str, sb.toString());
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                if (i == 0) {
                    VolumeDialogControllerImpl.this.mShowA11yStream = false;
                } else if (i != 1) {
                    String str2 = this.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Invalid accessibility mode ");
                    sb2.append(i);
                    Log.e(str2, sb2.toString());
                } else {
                    VolumeDialogControllerImpl.this.mShowA11yStream = true;
                }
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(15, Boolean.valueOf(VolumeDialogControllerImpl.this.mShowA11yStream)).sendToTarget();
            }
        }
    }

    /* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$W */
    private final class C1773W extends Handler {
        C1773W(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            boolean z = true;
            switch (message.what) {
                case 1:
                    VolumeDialogControllerImpl.this.onVolumeChangedW(message.arg1, message.arg2);
                    return;
                case 2:
                    VolumeDialogControllerImpl.this.onDismissRequestedW(message.arg1);
                    return;
                case 3:
                    VolumeDialogControllerImpl.this.onGetStateW();
                    return;
                case 4:
                    VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                    int i = message.arg1;
                    if (message.arg2 == 0) {
                        z = false;
                    }
                    volumeDialogControllerImpl.onSetRingerModeW(i, z);
                    return;
                case 5:
                    VolumeDialogControllerImpl.this.onSetZenModeW(message.arg1);
                    return;
                case 6:
                    VolumeDialogControllerImpl.this.onSetExitConditionW((Condition) message.obj);
                    return;
                case 7:
                    VolumeDialogControllerImpl volumeDialogControllerImpl2 = VolumeDialogControllerImpl.this;
                    int i2 = message.arg1;
                    if (message.arg2 == 0) {
                        z = false;
                    }
                    volumeDialogControllerImpl2.onSetStreamMuteW(i2, z);
                    return;
                case 8:
                    VolumeDialogControllerImpl.this.mCallbacks.onLayoutDirectionChanged(message.arg1);
                    return;
                case 9:
                    VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
                    return;
                case 10:
                    VolumeDialogControllerImpl.this.onSetStreamVolumeW(message.arg1, message.arg2);
                    return;
                case 11:
                    VolumeDialogControllerImpl.this.onSetActiveStreamW(message.arg1);
                    return;
                case 12:
                    VolumeDialogControllerImpl volumeDialogControllerImpl3 = VolumeDialogControllerImpl.this;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    volumeDialogControllerImpl3.onNotifyVisibleW(z);
                    return;
                case 13:
                    VolumeDialogControllerImpl.this.onUserActivityW();
                    return;
                case 14:
                    VolumeDialogControllerImpl.this.onShowSafetyWarningW(message.arg1);
                    return;
                case 15:
                    VolumeDialogControllerImpl.this.onAccessibilityModeChanged((Boolean) message.obj);
                    return;
                case 16:
                    VolumeDialogControllerImpl.this.onGetCaptionsComponentStateW(((Boolean) message.obj).booleanValue());
                    return;
                default:
                    return;
            }
        }
    }

    private static boolean isLogWorthy(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 6;
    }

    private static boolean isRinger(int i) {
        return i == 2 || i == 5;
    }

    public boolean isCaptionStreamOptedOut() {
        return false;
    }

    static {
        ArrayMap<Integer, Integer> arrayMap = new ArrayMap<>();
        STREAMS = arrayMap;
        arrayMap.put(Integer.valueOf(4), Integer.valueOf(C2017R$string.stream_alarm));
        STREAMS.put(Integer.valueOf(6), Integer.valueOf(C2017R$string.stream_bluetooth_sco));
        STREAMS.put(Integer.valueOf(8), Integer.valueOf(C2017R$string.stream_dtmf));
        STREAMS.put(Integer.valueOf(3), Integer.valueOf(C2017R$string.stream_music));
        STREAMS.put(Integer.valueOf(10), Integer.valueOf(C2017R$string.stream_accessibility));
        STREAMS.put(Integer.valueOf(5), Integer.valueOf(C2017R$string.stream_notification));
        STREAMS.put(Integer.valueOf(2), Integer.valueOf(C2017R$string.stream_ring));
        STREAMS.put(Integer.valueOf(1), Integer.valueOf(C2017R$string.stream_system));
        STREAMS.put(Integer.valueOf(7), Integer.valueOf(C2017R$string.stream_system_enforced));
        STREAMS.put(Integer.valueOf(9), Integer.valueOf(C2017R$string.stream_tts));
        STREAMS.put(Integer.valueOf(0), Integer.valueOf(C2017R$string.stream_voice_call));
    }

    public VolumeDialogControllerImpl(Context context, BroadcastDispatcher broadcastDispatcher, Optional<Lazy<StatusBar>> optional) {
        boolean z = true;
        this.mShowDndTile = true;
        this.mVolumeController = new C1772VC();
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        if (applicationContext.getPackageManager().hasSystemFeature("android.software.leanback")) {
            this.mStatusBarOptionalLazy = Optional.empty();
        } else {
            this.mStatusBarOptionalLazy = optional;
        }
        String str = "notification";
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(str);
        Events.writeEvent(5, new Object[0]);
        HandlerThread handlerThread = new HandlerThread(VolumeDialogControllerImpl.class.getSimpleName());
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWorker = new C1773W(this.mWorkerThread.getLooper());
        this.mMediaSessions = createMediaSessions(this.mContext, this.mWorkerThread.getLooper(), this.mMediaSessionsCallbacksW);
        String str2 = "audio";
        this.mAudio = (AudioManager) this.mContext.getSystemService(str2);
        this.mNoMan = (NotificationManager) this.mContext.getSystemService(str);
        SettingObserver settingObserver = new SettingObserver(this.mWorker);
        this.mObserver = settingObserver;
        this.mBroadcastDispatcher = broadcastDispatcher;
        settingObserver.init();
        this.mReceiver.init();
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        this.mVibrator = vibrator;
        if (vibrator == null || !vibrator.hasVibrator()) {
            z = false;
        }
        this.mHasVibrator = z;
        this.mAudioService = IAudioService.Stub.asInterface(ServiceManager.getService(str2));
        this.mVolumeController.setA11yMode(((AccessibilityManager) context.getSystemService(AccessibilityManager.class)).isAccessibilityVolumeStreamActive() ? 1 : 0);
    }

    public AudioManager getAudioManager() {
        return this.mAudio;
    }

    public void dismiss() {
        this.mCallbacks.onDismissRequested(2);
    }

    /* access modifiers changed from: protected */
    public void setVolumeController() {
        try {
            this.mAudio.setVolumeController(this.mVolumeController);
        } catch (SecurityException e) {
            Log.w(TAG, "Unable to set the volume controller", e);
        }
    }

    /* access modifiers changed from: protected */
    public void setAudioManagerStreamVolume(int i, int i2, int i3) {
        this.mAudio.setStreamVolume(i, i2, i3);
    }

    /* access modifiers changed from: protected */
    public int getAudioManagerStreamVolume(int i) {
        return this.mAudio.getLastAudibleStreamVolume(i);
    }

    /* access modifiers changed from: protected */
    public int getAudioManagerStreamMaxVolume(int i) {
        return this.mAudio.getStreamMaxVolume(i);
    }

    /* access modifiers changed from: protected */
    public int getAudioManagerStreamMinVolume(int i) {
        return this.mAudio.getStreamMinVolumeInt(i);
    }

    public void register() {
        setVolumeController();
        setVolumePolicy(this.mVolumePolicy);
        showDndTile(this.mShowDndTile);
        try {
            this.mMediaSessions.init();
        } catch (SecurityException e) {
            Log.w(TAG, "No access to media sessions", e);
        }
    }

    public void setVolumePolicy(VolumePolicy volumePolicy) {
        this.mVolumePolicy = volumePolicy;
        if (volumePolicy != null) {
            try {
                this.mAudio.setVolumePolicy(volumePolicy);
            } catch (NoSuchMethodError unused) {
                Log.w(TAG, "No volume policy api");
            }
        }
    }

    /* access modifiers changed from: protected */
    public MediaSessions createMediaSessions(Context context, Looper looper, MediaSessions.Callbacks callbacks) {
        return new MediaSessions(context, looper, callbacks);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(VolumeDialogControllerImpl.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.print("  mDestroyed: ");
        printWriter.println(this.mDestroyed);
        printWriter.print("  mVolumePolicy: ");
        printWriter.println(this.mVolumePolicy);
        printWriter.print("  mState: ");
        printWriter.println(this.mState.toString(4));
        printWriter.print("  mShowDndTile: ");
        printWriter.println(this.mShowDndTile);
        printWriter.print("  mHasVibrator: ");
        printWriter.println(this.mHasVibrator);
        synchronized (this.mMediaSessionsCallbacksW.mRemoteStreams) {
            printWriter.print("  mRemoteStreams: ");
            printWriter.println(this.mMediaSessionsCallbacksW.mRemoteStreams.values());
        }
        printWriter.print("  mShowA11yStream: ");
        printWriter.println(this.mShowA11yStream);
        printWriter.println();
        this.mMediaSessions.dump(printWriter);
    }

    public void addCallback(Callbacks callbacks, Handler handler) {
        this.mCallbacks.add(callbacks, handler);
        callbacks.onAccessibilityModeChanged(Boolean.valueOf(this.mShowA11yStream));
    }

    public void setUserActivityListener(UserActivityListener userActivityListener) {
        if (!this.mDestroyed) {
            synchronized (this) {
                this.mUserActivityListener = userActivityListener;
            }
        }
    }

    public void removeCallback(Callbacks callbacks) {
        this.mCallbacks.remove(callbacks);
    }

    public void getState() {
        if (!this.mDestroyed) {
            this.mWorker.sendEmptyMessage(3);
        }
    }

    public boolean areCaptionsEnabled() {
        return Secure.getIntForUser(this.mContext.getContentResolver(), "odi_captions_enabled", 0, -2) == 1;
    }

    public void setCaptionsEnabled(boolean z) {
        Secure.putIntForUser(this.mContext.getContentResolver(), "odi_captions_enabled", z ? 1 : 0, -2);
    }

    public void getCaptionsComponentState(boolean z) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(16, Boolean.valueOf(z)).sendToTarget();
        }
    }

    public void notifyVisible(boolean z) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(12, z ? 1 : 0, 0).sendToTarget();
        }
    }

    public void userActivity() {
        if (!this.mDestroyed) {
            this.mWorker.removeMessages(13);
            this.mWorker.sendEmptyMessage(13);
        }
    }

    public void setRingerMode(int i, boolean z) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(4, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void setStreamVolume(int i, int i2) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(10, i, i2).sendToTarget();
        }
    }

    public void setActiveStream(int i) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(11, i, 0).sendToTarget();
        }
    }

    public void setEnableDialogs(boolean z, boolean z2) {
        this.mShowVolumeDialog = z;
        this.mShowSafetyWarning = z2;
    }

    public void scheduleTouchFeedback() {
        this.mLastToggledRingerOn = System.currentTimeMillis();
    }

    private void playTouchFeedback() {
        if (System.currentTimeMillis() - this.mLastToggledRingerOn < 1000) {
            try {
                this.mAudioService.playSoundEffect(5);
            } catch (RemoteException unused) {
            }
        }
    }

    public void vibrate(VibrationEffect vibrationEffect) {
        if (this.mHasVibrator) {
            this.mVibrator.vibrate(vibrationEffect, SONIFICIATION_VIBRATION_ATTRIBUTES);
        }
    }

    public boolean hasVibrator() {
        return this.mHasVibrator;
    }

    /* access modifiers changed from: private */
    public void onNotifyVisibleW(boolean z) {
        if (!this.mDestroyed) {
            this.mAudio.notifyVolumeControllerVisible(this.mVolumeController, z);
            if (!z && updateActiveStreamW(-1)) {
                this.mCallbacks.onStateChanged(this.mState);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onUserActivityW() {
        synchronized (this) {
            if (this.mUserActivityListener != null) {
                this.mUserActivityListener.onUserActivity();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onShowSafetyWarningW(int i) {
        if (this.mShowSafetyWarning) {
            this.mCallbacks.onShowSafetyWarning(i);
        }
    }

    /* access modifiers changed from: private */
    public void onGetCaptionsComponentStateW(boolean z) {
        Boolean bool = Boolean.FALSE;
        try {
            String string = this.mContext.getString(17039769);
            if (TextUtils.isEmpty(string)) {
                this.mCallbacks.onCaptionComponentStateChanged(bool, Boolean.valueOf(z));
                return;
            }
            boolean z2 = false;
            if (C1753D.BUG) {
                Log.i(TAG, String.format("isCaptionsServiceEnabled componentNameString=%s", new Object[]{string}));
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
            if (unflattenFromString == null) {
                this.mCallbacks.onCaptionComponentStateChanged(bool, Boolean.valueOf(z));
                return;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            C1761C c = this.mCallbacks;
            if (packageManager.getComponentEnabledSetting(unflattenFromString) == 1) {
                z2 = true;
            }
            c.onCaptionComponentStateChanged(Boolean.valueOf(z2), Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "isCaptionsServiceEnabled failed to check for captions component", e);
            this.mCallbacks.onCaptionComponentStateChanged(bool, Boolean.valueOf(z));
        }
    }

    /* access modifiers changed from: private */
    public void onAccessibilityModeChanged(Boolean bool) {
        this.mCallbacks.onAccessibilityModeChanged(bool);
    }

    /* access modifiers changed from: private */
    public boolean checkRoutedToBluetoothW(int i) {
        if (i != 3) {
            return false;
        }
        return false | updateStreamRoutedToBluetoothW(i, (this.mAudio.getDevicesForStream(3) & 896) != 0);
    }

    /* access modifiers changed from: private */
    public boolean shouldShowUI(int i) {
        Optional map = this.mStatusBarOptionalLazy.map(new Function(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final Object apply(Object obj) {
                return VolumeDialogControllerImpl.this.lambda$shouldShowUI$0$VolumeDialogControllerImpl(this.f$1, (Lazy) obj);
            }
        });
        boolean z = true;
        if (!this.mShowVolumeDialog || (i & 1) == 0) {
            z = false;
        }
        return ((Boolean) map.orElse(Boolean.valueOf(z))).booleanValue();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$shouldShowUI$0 */
    public /* synthetic */ Boolean lambda$shouldShowUI$0$VolumeDialogControllerImpl(int i, Lazy lazy) {
        StatusBar statusBar = (StatusBar) lazy.get();
        boolean z = true;
        if (statusBar.getWakefulnessState() == 0 || statusBar.getWakefulnessState() == 3 || !statusBar.isDeviceInteractive() || (i & 1) == 0 || !this.mShowVolumeDialog) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    /* access modifiers changed from: 0000 */
    public boolean onVolumeChangedW(int i, int i2) {
        boolean shouldShowUI = shouldShowUI(i2);
        boolean z = (i2 & 4096) != 0;
        boolean z2 = (i2 & 2048) != 0;
        boolean z3 = (i2 & 128) != 0;
        boolean updateActiveStreamW = shouldShowUI ? updateActiveStreamW(i) | false : false;
        int audioManagerStreamVolume = getAudioManagerStreamVolume(i);
        boolean updateStreamLevelW = updateActiveStreamW | updateStreamLevelW(i, audioManagerStreamVolume) | checkRoutedToBluetoothW(shouldShowUI ? 3 : i);
        if (updateStreamLevelW) {
            this.mCallbacks.onStateChanged(this.mState);
        }
        if (shouldShowUI) {
            this.mCallbacks.onShowRequested(1);
        }
        if (z2) {
            this.mCallbacks.onShowVibrateHint();
        }
        if (z3) {
            this.mCallbacks.onShowSilentHint();
        }
        if (updateStreamLevelW && z) {
            Events.writeEvent(4, Integer.valueOf(i), Integer.valueOf(audioManagerStreamVolume));
        }
        return updateStreamLevelW;
    }

    /* access modifiers changed from: private */
    public boolean updateActiveStreamW(int i) {
        State state = this.mState;
        if (i == state.activeStream) {
            return false;
        }
        state.activeStream = i;
        Events.writeEvent(2, Integer.valueOf(i));
        if (C1753D.BUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("updateActiveStreamW ");
            sb.append(i);
            Log.d(str, sb.toString());
        }
        if (i >= 100) {
            i = -1;
        }
        if (C1753D.BUG) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("forceVolumeControlStream ");
            sb2.append(i);
            Log.d(str2, sb2.toString());
        }
        this.mAudio.forceVolumeControlStream(i);
        return true;
    }

    /* access modifiers changed from: private */
    public StreamState streamStateW(int i) {
        StreamState streamState = (StreamState) this.mState.states.get(i);
        if (streamState != null) {
            return streamState;
        }
        StreamState streamState2 = new StreamState();
        this.mState.states.put(i, streamState2);
        return streamState2;
    }

    /* access modifiers changed from: private */
    public void onGetStateW() {
        for (Integer intValue : STREAMS.keySet()) {
            int intValue2 = intValue.intValue();
            updateStreamLevelW(intValue2, getAudioManagerStreamVolume(intValue2));
            streamStateW(intValue2).levelMin = getAudioManagerStreamMinVolume(intValue2);
            streamStateW(intValue2).levelMax = Math.max(1, getAudioManagerStreamMaxVolume(intValue2));
            updateStreamMuteW(intValue2, this.mAudio.isStreamMute(intValue2));
            StreamState streamStateW = streamStateW(intValue2);
            streamStateW.muteSupported = this.mAudio.isStreamAffectedByMute(intValue2);
            streamStateW.name = ((Integer) STREAMS.get(Integer.valueOf(intValue2))).intValue();
            checkRoutedToBluetoothW(intValue2);
        }
        updateRingerModeExternalW(this.mAudio.getRingerMode());
        updateZenModeW();
        updateZenConfig();
        updateEffectsSuppressorW(this.mNoMan.getEffectsSuppressor());
        this.mCallbacks.onStateChanged(this.mState);
    }

    private boolean updateStreamRoutedToBluetoothW(int i, boolean z) {
        StreamState streamStateW = streamStateW(i);
        if (streamStateW.routedToBluetooth == z) {
            return false;
        }
        streamStateW.routedToBluetooth = z;
        if (C1753D.BUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("updateStreamRoutedToBluetoothW stream=");
            sb.append(i);
            sb.append(" routedToBluetooth=");
            sb.append(z);
            Log.d(str, sb.toString());
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateStreamLevelW(int i, int i2) {
        StreamState streamStateW = streamStateW(i);
        if (streamStateW.level == i2) {
            return false;
        }
        streamStateW.level = i2;
        if (isLogWorthy(i)) {
            Events.writeEvent(10, Integer.valueOf(i), Integer.valueOf(i2));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateStreamMuteW(int i, boolean z) {
        StreamState streamStateW = streamStateW(i);
        if (streamStateW.muted == z) {
            return false;
        }
        streamStateW.muted = z;
        if (isLogWorthy(i)) {
            Events.writeEvent(15, Integer.valueOf(i), Boolean.valueOf(z));
        }
        if (z && isRinger(i)) {
            updateRingerModeInternalW(this.mAudio.getRingerModeInternal());
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateEffectsSuppressorW(ComponentName componentName) {
        if (Objects.equals(this.mState.effectsSuppressor, componentName)) {
            return false;
        }
        State state = this.mState;
        state.effectsSuppressor = componentName;
        state.effectsSuppressorName = getApplicationName(this.mContext, componentName);
        State state2 = this.mState;
        Events.writeEvent(14, state2.effectsSuppressor, state2.effectsSuppressorName);
        return true;
    }

    private static String getApplicationName(Context context, ComponentName componentName) {
        if (componentName == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        String packageName = componentName.getPackageName();
        try {
            String trim = Objects.toString(packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager), "").trim();
            return trim.length() > 0 ? trim : packageName;
        } catch (NameNotFoundException unused) {
        }
    }

    /* access modifiers changed from: private */
    public boolean updateZenModeW() {
        int i = Global.getInt(this.mContext.getContentResolver(), "zen_mode", 0);
        State state = this.mState;
        if (state.zenMode == i) {
            return false;
        }
        state.zenMode = i;
        Events.writeEvent(13, Integer.valueOf(i));
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateZenConfig() {
        Policy consolidatedNotificationPolicy = this.mNotificationManager.getConsolidatedNotificationPolicy();
        boolean z = (consolidatedNotificationPolicy.priorityCategories & 32) == 0;
        boolean z2 = (consolidatedNotificationPolicy.priorityCategories & 64) == 0;
        boolean z3 = (consolidatedNotificationPolicy.priorityCategories & 128) == 0;
        boolean areAllPriorityOnlyRingerSoundsMuted = ZenModeConfig.areAllPriorityOnlyRingerSoundsMuted(consolidatedNotificationPolicy);
        State state = this.mState;
        if (state.disallowAlarms == z && state.disallowMedia == z2 && state.disallowRinger == areAllPriorityOnlyRingerSoundsMuted && state.disallowSystem == z3) {
            return false;
        }
        State state2 = this.mState;
        state2.disallowAlarms = z;
        state2.disallowMedia = z2;
        state2.disallowSystem = z3;
        state2.disallowRinger = areAllPriorityOnlyRingerSoundsMuted;
        StringBuilder sb = new StringBuilder();
        sb.append("disallowAlarms=");
        sb.append(z);
        sb.append(" disallowMedia=");
        sb.append(z2);
        sb.append(" disallowSystem=");
        sb.append(z3);
        sb.append(" disallowRinger=");
        sb.append(areAllPriorityOnlyRingerSoundsMuted);
        Events.writeEvent(17, sb.toString());
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateRingerModeExternalW(int i) {
        State state = this.mState;
        if (i == state.ringerModeExternal) {
            return false;
        }
        state.ringerModeExternal = i;
        Events.writeEvent(12, Integer.valueOf(i));
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateRingerModeInternalW(int i) {
        State state = this.mState;
        if (i == state.ringerModeInternal) {
            return false;
        }
        state.ringerModeInternal = i;
        Events.writeEvent(11, Integer.valueOf(i));
        if (this.mState.ringerModeInternal == 2) {
            playTouchFeedback();
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void onSetRingerModeW(int i, boolean z) {
        if (z) {
            this.mAudio.setRingerMode(i);
        } else {
            this.mAudio.setRingerModeInternal(i);
        }
    }

    /* access modifiers changed from: private */
    public void onSetStreamMuteW(int i, boolean z) {
        this.mAudio.adjustStreamVolume(i, z ? -100 : 100, 0);
    }

    /* access modifiers changed from: private */
    public void onSetStreamVolumeW(int i, int i2) {
        if (C1753D.BUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onSetStreamVolume ");
            sb.append(i);
            sb.append(" level=");
            sb.append(i2);
            Log.d(str, sb.toString());
        }
        if (i >= 100) {
            this.mMediaSessionsCallbacksW.setStreamVolume(i, i2);
        } else {
            setAudioManagerStreamVolume(i, i2, 0);
        }
    }

    /* access modifiers changed from: private */
    public void onSetActiveStreamW(int i) {
        if (updateActiveStreamW(i)) {
            this.mCallbacks.onStateChanged(this.mState);
        }
    }

    /* access modifiers changed from: private */
    public void onSetExitConditionW(Condition condition) {
        this.mNoMan.setZenMode(this.mState.zenMode, condition != null ? condition.id : null, TAG);
    }

    /* access modifiers changed from: private */
    public void onSetZenModeW(int i) {
        if (C1753D.BUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onSetZenModeW ");
            sb.append(i);
            Log.d(str, sb.toString());
        }
        this.mNoMan.setZenMode(i, null, TAG);
    }

    /* access modifiers changed from: private */
    public void onDismissRequestedW(int i) {
        this.mCallbacks.onDismissRequested(i);
    }

    public void showDndTile(boolean z) {
        if (C1753D.BUG) {
            Log.d(TAG, "showDndTile");
        }
        DndTile.setVisible(this.mContext, z);
    }
}
