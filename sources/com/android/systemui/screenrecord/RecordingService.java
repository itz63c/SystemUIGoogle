package com.android.systemui.screenrecord;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionManager.Stub;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.MediaStore.Video.Media;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingService extends Service implements OnInfoListener {
    private final RecordingController mController;
    private Surface mInputSurface;
    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private boolean mOriginalShowTaps;
    private Builder mRecordingNotificationBuilder;
    private boolean mShowTaps;
    private File mTempFile;
    private boolean mUseAudio;
    private VirtualDisplay mVirtualDisplay;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public RecordingService(RecordingController recordingController) {
        this.mController = recordingController;
    }

    public static Intent getStartIntent(Context context, int i, Intent intent, boolean z, boolean z2) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.START").putExtra("extra_resultCode", i).putExtra("extra_data", intent).putExtra("extra_useAudio", z).putExtra("extra_showTaps", z2);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            return 2;
        }
        String action = intent.getAction();
        StringBuilder sb = new StringBuilder();
        sb.append("onStartCommand ");
        sb.append(action);
        String str = "RecordingService";
        Log.d(str, sb.toString());
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        char c = 65535;
        switch (action.hashCode()) {
            case -1688140755:
                if (action.equals("com.android.systemui.screenrecord.SHARE")) {
                    c = 2;
                    break;
                }
                break;
            case -1687783248:
                if (action.equals("com.android.systemui.screenrecord.START")) {
                    c = 0;
                    break;
                }
                break;
            case -1224647939:
                if (action.equals("com.android.systemui.screenrecord.DELETE")) {
                    c = 3;
                    break;
                }
                break;
            case -470086188:
                if (action.equals("com.android.systemui.screenrecord.STOP")) {
                    c = 1;
                    break;
                }
                break;
        }
        if (c == 0) {
            this.mUseAudio = intent.getBooleanExtra("extra_useAudio", false);
            this.mShowTaps = intent.getBooleanExtra("extra_showTaps", false);
            try {
                IBinder asBinder = Stub.asInterface(ServiceManager.getService("media_projection")).createProjection(getUserId(), getPackageName(), 0, false).asBinder();
                if (asBinder == null) {
                    Log.e(str, "Projection was null");
                    Toast.makeText(this, C2017R$string.screenrecord_start_error, 1).show();
                    return 2;
                }
                this.mMediaProjection = new MediaProjection(getApplicationContext(), IMediaProjection.Stub.asInterface(asBinder));
                startRecording();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, C2017R$string.screenrecord_start_error, 1).show();
                return 2;
            }
        } else if (c != 1) {
            String str2 = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
            String str3 = "extra_path";
            if (c == 2) {
                Intent putExtra = new Intent("android.intent.action.SEND").setType("video/mp4").putExtra("android.intent.extra.STREAM", Uri.parse(intent.getStringExtra(str3)));
                String string = getResources().getString(C2017R$string.screenrecord_share_label);
                sendBroadcast(new Intent(str2));
                notificationManager.cancel(1);
                startActivity(Intent.createChooser(putExtra, string).setFlags(268435456));
            } else if (c == 3) {
                sendBroadcast(new Intent(str2));
                ContentResolver contentResolver = getContentResolver();
                Uri parse = Uri.parse(intent.getStringExtra(str3));
                contentResolver.delete(parse, null, null);
                Toast.makeText(this, C2017R$string.screenrecord_delete_description, 1).show();
                notificationManager.cancel(1);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Deleted recording ");
                sb2.append(parse);
                Log.d(str, sb2.toString());
            }
        } else {
            stopRecording();
            saveRecording(notificationManager);
        }
        return 1;
    }

    public void onCreate() {
        super.onCreate();
    }

    private void startRecording() {
        String str = "RecordingService";
        try {
            File cacheDir = getCacheDir();
            cacheDir.mkdirs();
            this.mTempFile = File.createTempFile("temp", ".mp4", cacheDir);
            StringBuilder sb = new StringBuilder();
            sb.append("Writing video output to: ");
            sb.append(this.mTempFile.getAbsolutePath());
            Log.d(str, sb.toString());
            boolean z = false;
            if (1 == System.getInt(getApplicationContext().getContentResolver(), "show_touches", 0)) {
                z = true;
            }
            this.mOriginalShowTaps = z;
            setTapsVisible(this.mShowTaps);
            MediaRecorder mediaRecorder = new MediaRecorder();
            this.mMediaRecorder = mediaRecorder;
            if (this.mUseAudio) {
                mediaRecorder.setAudioSource(1);
            }
            this.mMediaRecorder.setVideoSource(2);
            this.mMediaRecorder.setOutputFormat(2);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) getSystemService("window")).getDefaultDisplay().getRealMetrics(displayMetrics);
            int i = displayMetrics.widthPixels;
            int i2 = displayMetrics.heightPixels;
            this.mMediaRecorder.setVideoEncoder(2);
            this.mMediaRecorder.setVideoSize(i, i2);
            this.mMediaRecorder.setVideoFrameRate(30);
            this.mMediaRecorder.setVideoEncodingBitRate(10000000);
            this.mMediaRecorder.setMaxDuration(3600000);
            this.mMediaRecorder.setMaxFileSize(5000000000L);
            if (this.mUseAudio) {
                this.mMediaRecorder.setAudioEncoder(1);
                this.mMediaRecorder.setAudioChannels(1);
                this.mMediaRecorder.setAudioEncodingBitRate(16);
                this.mMediaRecorder.setAudioSamplingRate(44100);
            }
            this.mMediaRecorder.setOutputFile(this.mTempFile);
            this.mMediaRecorder.prepare();
            Surface surface = this.mMediaRecorder.getSurface();
            this.mInputSurface = surface;
            this.mVirtualDisplay = this.mMediaProjection.createVirtualDisplay("Recording Display", i, i2, displayMetrics.densityDpi, 16, surface, null, null);
            this.mMediaRecorder.setOnInfoListener(this);
            this.mMediaRecorder.start();
            this.mController.updateState(true);
            createRecordingNotification();
        } catch (IOException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Error starting screen recording: ");
            sb2.append(e.getMessage());
            Log.e(str, sb2.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void createRecordingNotification() {
        String str;
        Resources resources = getResources();
        String str2 = "screen_record";
        NotificationChannel notificationChannel = new NotificationChannel(str2, getString(C2017R$string.screenrecord_name), 3);
        notificationChannel.setDescription(getString(C2017R$string.screenrecord_channel_description));
        notificationChannel.enableVibration(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        notificationManager.createNotificationChannel(notificationChannel);
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", resources.getString(C2017R$string.screenrecord_name));
        if (this.mUseAudio) {
            str = resources.getString(C2017R$string.screenrecord_ongoing_screen_and_audio);
        } else {
            str = resources.getString(C2017R$string.screenrecord_ongoing_screen_only);
        }
        Builder addExtras = new Builder(this, str2).setSmallIcon(C2010R$drawable.ic_screenrecord).setContentTitle(str).setContentText(getResources().getString(C2017R$string.screenrecord_stop_text)).setUsesChronometer(true).setColorized(true).setColor(getResources().getColor(C2008R$color.GM2_red_700)).setOngoing(true).setContentIntent(PendingIntent.getService(this, 2, getStopIntent(this), 134217728)).addExtras(bundle);
        this.mRecordingNotificationBuilder = addExtras;
        notificationManager.notify(1, addExtras.build());
        startForeground(1, this.mRecordingNotificationBuilder.build());
    }

    private Notification createSaveNotification(Uri uri) {
        Bitmap bitmap;
        Intent dataAndType = new Intent("android.intent.action.VIEW").setFlags(268435457).setDataAndType(uri, "video/mp4");
        Action build = new Action.Builder(Icon.createWithResource(this, C2010R$drawable.ic_screenrecord), getResources().getString(C2017R$string.screenrecord_share_label), PendingIntent.getService(this, 2, getShareIntent(this, uri.toString()), 134217728)).build();
        Action build2 = new Action.Builder(Icon.createWithResource(this, C2010R$drawable.ic_screenrecord), getResources().getString(C2017R$string.screenrecord_delete_label), PendingIntent.getService(this, 2, getDeleteIntent(this, uri.toString()), 134217728)).build();
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", getResources().getString(C2017R$string.screenrecord_name));
        Builder addExtras = new Builder(this, "screen_record").setSmallIcon(C2010R$drawable.ic_screenrecord).setContentTitle(getResources().getString(C2017R$string.screenrecord_save_message)).setContentIntent(PendingIntent.getActivity(this, 2, dataAndType, 1)).addAction(build).addAction(build2).setAutoCancel(true).addExtras(bundle);
        try {
            ContentResolver contentResolver = getContentResolver();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            bitmap = contentResolver.loadThumbnail(uri, new Size(displayMetrics.widthPixels, displayMetrics.heightPixels / 2), null);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error creating thumbnail: ");
            sb.append(e.getMessage());
            Log.e("RecordingService", sb.toString());
            e.printStackTrace();
            bitmap = null;
        }
        if (bitmap != null) {
            addExtras.setLargeIcon(bitmap).setStyle(new BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null));
        }
        return addExtras.build();
    }

    private void stopRecording() {
        setTapsVisible(this.mOriginalShowTaps);
        this.mMediaRecorder.stop();
        this.mMediaRecorder.release();
        this.mMediaRecorder = null;
        this.mMediaProjection.stop();
        this.mMediaProjection = null;
        this.mInputSurface.release();
        this.mVirtualDisplay.release();
        stopSelf();
        this.mController.updateState(false);
    }

    private void saveRecording(NotificationManager notificationManager) {
        String format = new SimpleDateFormat("'screen-'yyyyMMdd-HHmmss'.mp4'").format(new Date());
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", format);
        contentValues.put("mime_type", "video/mp4");
        contentValues.put("date_added", Long.valueOf(System.currentTimeMillis()));
        contentValues.put("datetaken", Long.valueOf(System.currentTimeMillis()));
        ContentResolver contentResolver = getContentResolver();
        Uri insert = contentResolver.insert(Media.getContentUri("external_primary"), contentValues);
        try {
            OutputStream openOutputStream = contentResolver.openOutputStream(insert, "w");
            Files.copy(this.mTempFile.toPath(), openOutputStream);
            openOutputStream.close();
            notificationManager.notify(1, createSaveNotification(insert));
            this.mTempFile.delete();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error saving screen recording: ");
            sb.append(e.getMessage());
            Log.e("RecordingService", sb.toString());
            Toast.makeText(this, C2017R$string.screenrecord_delete_error, 1).show();
        }
    }

    private void setTapsVisible(boolean z) {
        System.putInt(getApplicationContext().getContentResolver(), "show_touches", z ? 1 : 0);
    }

    public static Intent getStopIntent(Context context) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.STOP");
    }

    private static Intent getShareIntent(Context context, String str) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.SHARE").putExtra("extra_path", str);
    }

    private static Intent getDeleteIntent(Context context, String str) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.DELETE").putExtra("extra_path", str);
    }

    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        StringBuilder sb = new StringBuilder();
        sb.append("Media recorder info: ");
        sb.append(i);
        Log.d("RecordingService", sb.toString());
        onStartCommand(getStopIntent(this), 0, 0);
    }
}
