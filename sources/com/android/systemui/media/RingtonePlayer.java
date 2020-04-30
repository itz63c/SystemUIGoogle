package com.android.systemui.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.IAudioService;
import android.media.IRingtonePlayer;
import android.media.IRingtonePlayer.Stub;
import android.media.Ringtone;
import android.media.VolumeShaper.Configuration;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import com.android.systemui.SystemUI;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class RingtonePlayer extends SystemUI {
    /* access modifiers changed from: private */
    public final NotificationPlayer mAsyncPlayer = new NotificationPlayer("RingtonePlayer");
    private IAudioService mAudioService;
    private IRingtonePlayer mCallback = new Stub() {
        public void play(IBinder iBinder, Uri uri, AudioAttributes audioAttributes, float f, boolean z) throws RemoteException {
            playWithVolumeShaping(iBinder, uri, audioAttributes, f, z, null);
        }

        public void playWithVolumeShaping(IBinder iBinder, Uri uri, AudioAttributes audioAttributes, float f, boolean z, Configuration configuration) throws RemoteException {
            Client client;
            synchronized (RingtonePlayer.this.mClients) {
                client = (Client) RingtonePlayer.this.mClients.get(iBinder);
                if (client == null) {
                    client = new Client(iBinder, uri, Binder.getCallingUserHandle(), audioAttributes, configuration);
                    iBinder.linkToDeath(client, 0);
                    RingtonePlayer.this.mClients.put(iBinder, client);
                }
            }
            client.mRingtone.setLooping(z);
            client.mRingtone.setVolume(f);
            client.mRingtone.play();
        }

        public void stop(IBinder iBinder) {
            Client client;
            synchronized (RingtonePlayer.this.mClients) {
                client = (Client) RingtonePlayer.this.mClients.remove(iBinder);
            }
            if (client != null) {
                client.mToken.unlinkToDeath(client, 0);
                client.mRingtone.stop();
            }
        }

        public boolean isPlaying(IBinder iBinder) {
            Client client;
            synchronized (RingtonePlayer.this.mClients) {
                client = (Client) RingtonePlayer.this.mClients.get(iBinder);
            }
            if (client != null) {
                return client.mRingtone.isPlaying();
            }
            return false;
        }

        public void setPlaybackProperties(IBinder iBinder, float f, boolean z) {
            Client client;
            synchronized (RingtonePlayer.this.mClients) {
                client = (Client) RingtonePlayer.this.mClients.get(iBinder);
            }
            if (client != null) {
                client.mRingtone.setVolume(f);
                client.mRingtone.setLooping(z);
            }
        }

        public void playAsync(Uri uri, UserHandle userHandle, boolean z, AudioAttributes audioAttributes) {
            if (Binder.getCallingUid() == 1000) {
                if (UserHandle.ALL.equals(userHandle)) {
                    userHandle = UserHandle.SYSTEM;
                }
                RingtonePlayer.this.mAsyncPlayer.play(RingtonePlayer.this.getContextForUser(userHandle), uri, z, audioAttributes);
                return;
            }
            throw new SecurityException("Async playback only available from system UID.");
        }

        public void stopAsync() {
            if (Binder.getCallingUid() == 1000) {
                RingtonePlayer.this.mAsyncPlayer.stop();
                return;
            }
            throw new SecurityException("Async playback only available from system UID.");
        }

        public String getTitle(Uri uri) {
            return Ringtone.getTitle(RingtonePlayer.this.getContextForUser(Binder.getCallingUserHandle()), uri, false, false);
        }

        public ParcelFileDescriptor openRingtone(Uri uri) {
            ContentResolver contentResolver = RingtonePlayer.this.getContextForUser(Binder.getCallingUserHandle()).getContentResolver();
            if (uri.toString().startsWith(Media.EXTERNAL_CONTENT_URI.toString())) {
                Cursor query = contentResolver.query(uri, new String[]{"is_ringtone", "is_alarm", "is_notification"}, null, null, null);
                try {
                    if (query.moveToFirst() && (query.getInt(0) != 0 || query.getInt(1) != 0 || query.getInt(2) != 0)) {
                        ParcelFileDescriptor openFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
                        if (query != null) {
                            query.close();
                        }
                        return openFileDescriptor;
                    } else if (query != null) {
                        query.close();
                    }
                } catch (IOException e) {
                    throw new SecurityException(e);
                } catch (Throwable th) {
                    if (query != null) {
                        try {
                            query.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Uri is not ringtone, alarm, or notification: ");
            sb.append(uri);
            throw new SecurityException(sb.toString());
        }
    };
    /* access modifiers changed from: private */
    public final HashMap<IBinder, Client> mClients = new HashMap<>();

    private class Client implements DeathRecipient {
        /* access modifiers changed from: private */
        public final Ringtone mRingtone;
        /* access modifiers changed from: private */
        public final IBinder mToken;

        Client(IBinder iBinder, Uri uri, UserHandle userHandle, AudioAttributes audioAttributes, Configuration configuration) {
            this.mToken = iBinder;
            Ringtone ringtone = new Ringtone(RingtonePlayer.this.getContextForUser(userHandle), false);
            this.mRingtone = ringtone;
            ringtone.setAudioAttributes(audioAttributes);
            this.mRingtone.setUri(uri, configuration);
        }

        public void binderDied() {
            synchronized (RingtonePlayer.this.mClients) {
                RingtonePlayer.this.mClients.remove(this.mToken);
            }
            this.mRingtone.stop();
        }
    }

    public RingtonePlayer(Context context) {
        super(context);
    }

    public void start() {
        this.mAsyncPlayer.setUsesWakeLock(this.mContext);
        IAudioService asInterface = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
        this.mAudioService = asInterface;
        try {
            asInterface.setRingtonePlayer(this.mCallback);
        } catch (RemoteException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Problem registering RingtonePlayer: ");
            sb.append(e);
            Log.e("RingtonePlayer", sb.toString());
        }
    }

    /* access modifiers changed from: private */
    public Context getContextForUser(UserHandle userHandle) {
        try {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, userHandle);
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Clients:");
        synchronized (this.mClients) {
            for (Client client : this.mClients.values()) {
                printWriter.print("  mToken=");
                printWriter.print(client.mToken);
                printWriter.print(" mUri=");
                printWriter.println(client.mRingtone.getUri());
            }
        }
    }
}
