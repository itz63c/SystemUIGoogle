package com.android.settingslib.volume;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.IRemoteVolumeController;
import android.media.IRemoteVolumeController.Stub;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.MediaController.PlaybackInfo;
import android.media.session.MediaSession.QueueItem;
import android.media.session.MediaSession.Token;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MediaSessions {
    /* access modifiers changed from: private */
    public static final String TAG = Util.logTag(MediaSessions.class);
    /* access modifiers changed from: private */
    public final Callbacks mCallbacks;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final C0644H mHandler;
    private boolean mInit;
    /* access modifiers changed from: private */
    public final MediaSessionManager mMgr;
    private final Map<Token, MediaControllerRecord> mRecords = new HashMap();
    private final IRemoteVolumeController mRvc = new Stub() {
        public void remoteVolumeChanged(Token token, int i) throws RemoteException {
            MediaSessions.this.mHandler.obtainMessage(2, i, 0, token).sendToTarget();
        }

        public void updateRemoteController(Token token) throws RemoteException {
            MediaSessions.this.mHandler.obtainMessage(3, token).sendToTarget();
        }
    };
    private final OnActiveSessionsChangedListener mSessionsListener = new OnActiveSessionsChangedListener() {
        public void onActiveSessionsChanged(List<MediaController> list) {
            MediaSessions.this.onActiveSessionsUpdatedH(list);
        }
    };

    public interface Callbacks {
        void onRemoteRemoved(Token token);

        void onRemoteUpdate(Token token, String str, PlaybackInfo playbackInfo);

        void onRemoteVolumeChanged(Token token, int i);
    }

    /* renamed from: com.android.settingslib.volume.MediaSessions$H */
    private final class C0644H extends Handler {
        private C0644H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                MediaSessions mediaSessions = MediaSessions.this;
                mediaSessions.onActiveSessionsUpdatedH(mediaSessions.mMgr.getActiveSessions(null));
            } else if (i == 2) {
                MediaSessions.this.onRemoteVolumeChangedH((Token) message.obj, message.arg1);
            } else if (i == 3) {
                MediaSessions.this.onUpdateRemoteControllerH((Token) message.obj);
            }
        }
    }

    private final class MediaControllerRecord extends Callback {
        public final MediaController controller;
        public String name;
        public boolean sentRemote;

        private MediaControllerRecord(MediaController mediaController) {
            this.controller = mediaController;
        }

        /* renamed from: cb */
        private String m5cb(String str) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            String str2 = " ";
            sb.append(str2);
            sb.append(this.controller.getPackageName());
            sb.append(str2);
            return sb.toString();
        }

        public void onAudioInfoChanged(PlaybackInfo playbackInfo) {
            if (C0641D.BUG) {
                String access$200 = MediaSessions.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(m5cb("onAudioInfoChanged"));
                sb.append(Util.playbackInfoToString(playbackInfo));
                sb.append(" sentRemote=");
                sb.append(this.sentRemote);
                Log.d(access$200, sb.toString());
            }
            boolean access$300 = MediaSessions.isRemote(playbackInfo);
            if (!access$300 && this.sentRemote) {
                MediaSessions.this.mCallbacks.onRemoteRemoved(this.controller.getSessionToken());
                this.sentRemote = false;
            } else if (access$300) {
                MediaSessions.this.updateRemoteH(this.controller.getSessionToken(), this.name, playbackInfo);
                this.sentRemote = true;
            }
        }

        public void onExtrasChanged(Bundle bundle) {
            if (C0641D.BUG) {
                String access$200 = MediaSessions.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(m5cb("onExtrasChanged"));
                sb.append(bundle);
                Log.d(access$200, sb.toString());
            }
        }

        public void onMetadataChanged(MediaMetadata mediaMetadata) {
            if (C0641D.BUG) {
                String access$200 = MediaSessions.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(m5cb("onMetadataChanged"));
                sb.append(Util.mediaMetadataToString(mediaMetadata));
                Log.d(access$200, sb.toString());
            }
        }

        public void onPlaybackStateChanged(PlaybackState playbackState) {
            if (C0641D.BUG) {
                String access$200 = MediaSessions.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(m5cb("onPlaybackStateChanged"));
                sb.append(Util.playbackStateToString(playbackState));
                Log.d(access$200, sb.toString());
            }
        }

        public void onQueueChanged(List<QueueItem> list) {
            if (C0641D.BUG) {
                String access$200 = MediaSessions.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(m5cb("onQueueChanged"));
                sb.append(list);
                Log.d(access$200, sb.toString());
            }
        }

        public void onQueueTitleChanged(CharSequence charSequence) {
            if (C0641D.BUG) {
                String access$200 = MediaSessions.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(m5cb("onQueueTitleChanged"));
                sb.append(charSequence);
                Log.d(access$200, sb.toString());
            }
        }

        public void onSessionDestroyed() {
            if (C0641D.BUG) {
                Log.d(MediaSessions.TAG, m5cb("onSessionDestroyed"));
            }
        }

        public void onSessionEvent(String str, Bundle bundle) {
            if (C0641D.BUG) {
                String access$200 = MediaSessions.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(m5cb("onSessionEvent"));
                sb.append("event=");
                sb.append(str);
                sb.append(" extras=");
                sb.append(bundle);
                Log.d(access$200, sb.toString());
            }
        }
    }

    public MediaSessions(Context context, Looper looper, Callbacks callbacks) {
        this.mContext = context;
        this.mHandler = new C0644H(looper);
        this.mMgr = (MediaSessionManager) context.getSystemService("media_session");
        this.mCallbacks = callbacks;
    }

    public void dump(PrintWriter printWriter) {
        StringBuilder sb = new StringBuilder();
        sb.append(MediaSessions.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.print("  mInit: ");
        printWriter.println(this.mInit);
        printWriter.print("  mRecords.size: ");
        printWriter.println(this.mRecords.size());
        int i = 0;
        for (MediaControllerRecord mediaControllerRecord : this.mRecords.values()) {
            i++;
            dump(i, printWriter, mediaControllerRecord.controller);
        }
    }

    public void init() {
        if (C0641D.BUG) {
            Log.d(TAG, "init");
        }
        this.mMgr.addOnActiveSessionsChangedListener(this.mSessionsListener, null, this.mHandler);
        this.mInit = true;
        postUpdateSessions();
        this.mMgr.registerRemoteVolumeController(this.mRvc);
    }

    /* access modifiers changed from: protected */
    public void postUpdateSessions() {
        if (this.mInit) {
            this.mHandler.sendEmptyMessage(1);
        }
    }

    public void setVolume(Token token, int i) {
        MediaControllerRecord mediaControllerRecord = (MediaControllerRecord) this.mRecords.get(token);
        if (mediaControllerRecord == null) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("setVolume: No record found for token ");
            sb.append(token);
            Log.w(str, sb.toString());
            return;
        }
        if (C0641D.BUG) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Setting level to ");
            sb2.append(i);
            Log.d(str2, sb2.toString());
        }
        mediaControllerRecord.controller.setVolumeTo(i, 0);
    }

    /* access modifiers changed from: private */
    public void onRemoteVolumeChangedH(Token token, int i) {
        MediaController mediaController = new MediaController(this.mContext, token);
        if (C0641D.BUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("remoteVolumeChangedH ");
            sb.append(mediaController.getPackageName());
            sb.append(" ");
            sb.append(Util.audioManagerFlagsToString(i));
            Log.d(str, sb.toString());
        }
        this.mCallbacks.onRemoteVolumeChanged(mediaController.getSessionToken(), i);
    }

    /* access modifiers changed from: private */
    public void onUpdateRemoteControllerH(Token token) {
        String str = null;
        MediaController mediaController = token != null ? new MediaController(this.mContext, token) : null;
        if (mediaController != null) {
            str = mediaController.getPackageName();
        }
        if (C0641D.BUG) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("updateRemoteControllerH ");
            sb.append(str);
            Log.d(str2, sb.toString());
        }
        postUpdateSessions();
    }

    /* access modifiers changed from: protected */
    public void onActiveSessionsUpdatedH(List<MediaController> list) {
        if (C0641D.BUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onActiveSessionsUpdatedH n=");
            sb.append(list.size());
            Log.d(str, sb.toString());
        }
        HashSet<Token> hashSet = new HashSet<>(this.mRecords.keySet());
        for (MediaController mediaController : list) {
            Token sessionToken = mediaController.getSessionToken();
            PlaybackInfo playbackInfo = mediaController.getPlaybackInfo();
            hashSet.remove(sessionToken);
            if (!this.mRecords.containsKey(sessionToken)) {
                MediaControllerRecord mediaControllerRecord = new MediaControllerRecord(mediaController);
                mediaControllerRecord.name = getControllerName(mediaController);
                this.mRecords.put(sessionToken, mediaControllerRecord);
                mediaController.registerCallback(mediaControllerRecord, this.mHandler);
            }
            MediaControllerRecord mediaControllerRecord2 = (MediaControllerRecord) this.mRecords.get(sessionToken);
            if (isRemote(playbackInfo)) {
                updateRemoteH(sessionToken, mediaControllerRecord2.name, playbackInfo);
                mediaControllerRecord2.sentRemote = true;
            }
        }
        for (Token token : hashSet) {
            MediaControllerRecord mediaControllerRecord3 = (MediaControllerRecord) this.mRecords.get(token);
            mediaControllerRecord3.controller.unregisterCallback(mediaControllerRecord3);
            this.mRecords.remove(token);
            if (C0641D.BUG) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Removing ");
                sb2.append(mediaControllerRecord3.name);
                sb2.append(" sentRemote=");
                sb2.append(mediaControllerRecord3.sentRemote);
                Log.d(str2, sb2.toString());
            }
            if (mediaControllerRecord3.sentRemote) {
                this.mCallbacks.onRemoteRemoved(token);
                mediaControllerRecord3.sentRemote = false;
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean isRemote(PlaybackInfo playbackInfo) {
        return playbackInfo != null && playbackInfo.getPlaybackType() == 2;
    }

    /* access modifiers changed from: protected */
    public String getControllerName(MediaController mediaController) {
        PackageManager packageManager = this.mContext.getPackageManager();
        String packageName = mediaController.getPackageName();
        try {
            String trim = Objects.toString(packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager), "").trim();
            return trim.length() > 0 ? trim : packageName;
        } catch (NameNotFoundException unused) {
        }
    }

    /* access modifiers changed from: private */
    public void updateRemoteH(Token token, String str, PlaybackInfo playbackInfo) {
        Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.onRemoteUpdate(token, str, playbackInfo);
        }
    }

    private static void dump(int i, PrintWriter printWriter, MediaController mediaController) {
        StringBuilder sb = new StringBuilder();
        sb.append("  Controller ");
        sb.append(i);
        sb.append(": ");
        sb.append(mediaController.getPackageName());
        printWriter.println(sb.toString());
        Bundle extras = mediaController.getExtras();
        long flags = mediaController.getFlags();
        MediaMetadata metadata = mediaController.getMetadata();
        PlaybackInfo playbackInfo = mediaController.getPlaybackInfo();
        PlaybackState playbackState = mediaController.getPlaybackState();
        List<QueueItem> queue = mediaController.getQueue();
        CharSequence queueTitle = mediaController.getQueueTitle();
        int ratingType = mediaController.getRatingType();
        PendingIntent sessionActivity = mediaController.getSessionActivity();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    PlaybackState: ");
        sb2.append(Util.playbackStateToString(playbackState));
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("    PlaybackInfo: ");
        sb3.append(Util.playbackInfoToString(playbackInfo));
        printWriter.println(sb3.toString());
        if (metadata != null) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("  MediaMetadata.desc=");
            sb4.append(metadata.getDescription());
            printWriter.println(sb4.toString());
        }
        StringBuilder sb5 = new StringBuilder();
        sb5.append("    RatingType: ");
        sb5.append(ratingType);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("    Flags: ");
        sb6.append(flags);
        printWriter.println(sb6.toString());
        String str = "      ";
        if (extras != null) {
            printWriter.println("    Extras:");
            for (String str2 : extras.keySet()) {
                StringBuilder sb7 = new StringBuilder();
                sb7.append(str);
                sb7.append(str2);
                sb7.append("=");
                sb7.append(extras.get(str2));
                printWriter.println(sb7.toString());
            }
        }
        if (queueTitle != null) {
            StringBuilder sb8 = new StringBuilder();
            sb8.append("    QueueTitle: ");
            sb8.append(queueTitle);
            printWriter.println(sb8.toString());
        }
        if (queue != null && !queue.isEmpty()) {
            printWriter.println("    Queue:");
            for (QueueItem queueItem : queue) {
                StringBuilder sb9 = new StringBuilder();
                sb9.append(str);
                sb9.append(queueItem);
                printWriter.println(sb9.toString());
            }
        }
        if (playbackInfo != null) {
            StringBuilder sb10 = new StringBuilder();
            sb10.append("    sessionActivity: ");
            sb10.append(sessionActivity);
            printWriter.println(sb10.toString());
        }
    }
}
