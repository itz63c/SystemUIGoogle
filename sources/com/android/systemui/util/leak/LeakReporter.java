package com.android.systemui.util.leak;

import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Debug;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LeakReporter {
    private final Context mContext;
    private final LeakDetector mLeakDetector;
    private final String mLeakReportEmail;

    public LeakReporter(Context context, LeakDetector leakDetector, String str) {
        this.mContext = context;
        this.mLeakDetector = leakDetector;
        this.mLeakReportEmail = str;
    }

    public void dumpLeak(int i) {
        FileOutputStream fileOutputStream;
        Throwable th;
        String str = "leak";
        String str2 = "LeakReporter";
        try {
            File file = new File(this.mContext.getCacheDir(), str);
            file.mkdir();
            File file2 = new File(file, "leak.hprof");
            Debug.dumpHprofData(file2.getAbsolutePath());
            File file3 = new File(file, "leak.dump");
            fileOutputStream = new FileOutputStream(file3);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.print("Build: ");
            printWriter.println(SystemProperties.get("ro.build.description"));
            printWriter.println();
            printWriter.flush();
            this.mLeakDetector.dump(fileOutputStream.getFD(), printWriter, new String[0]);
            printWriter.close();
            fileOutputStream.close();
            NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = new NotificationChannel(str, "Leak Alerts", 4);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(str2, 0, new Builder(this.mContext, notificationChannel.getId()).setAutoCancel(true).setShowWhen(true).setContentTitle("Memory Leak Detected").setContentText(String.format("SystemUI has detected %d leaked objects. Tap to send", new Object[]{Integer.valueOf(i)})).setSmallIcon(17303537).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, getIntent(file2, file3), 134217728, null, UserHandle.CURRENT)).build());
            return;
        } catch (IOException e) {
            Log.e(str2, "Couldn't dump heap for leak", e);
            return;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    private Intent getIntent(File file, File file2) {
        String str = "com.android.systemui.fileprovider";
        Uri uriForFile = FileProvider.getUriForFile(this.mContext, str, file2);
        Uri uriForFile2 = FileProvider.getUriForFile(this.mContext, str, file);
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.addFlags(1);
        intent.addCategory("android.intent.category.DEFAULT");
        String str2 = "application/vnd.android.leakreport";
        intent.setType(str2);
        intent.putExtra("android.intent.extra.SUBJECT", "SystemUI leak report");
        StringBuilder sb = new StringBuilder("Build info: ");
        sb.append(SystemProperties.get("ro.build.description"));
        intent.putExtra("android.intent.extra.TEXT", sb.toString());
        ClipData clipData = new ClipData(null, new String[]{str2}, new Item(null, null, null, uriForFile));
        ArrayList newArrayList = Lists.newArrayList(new Uri[]{uriForFile});
        clipData.addItem(new Item(null, null, null, uriForFile2));
        newArrayList.add(uriForFile2);
        intent.setClipData(clipData);
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", newArrayList);
        String str3 = this.mLeakReportEmail;
        if (str3 != null) {
            intent.putExtra("android.intent.extra.EMAIL", new String[]{str3});
        }
        return intent;
    }
}
