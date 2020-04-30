package com.android.systemui.util.leak;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.android.systemui.Dependency;
import com.android.systemui.util.leak.GarbageMonitor.ProcessMemInfo;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DumpTruck {
    final StringBuilder body = new StringBuilder();
    private final Context context;
    private Uri hprofUri;
    private long rss;

    public DumpTruck(Context context2) {
        this.context = context2;
    }

    public DumpTruck captureHeaps(List<Long> list) {
        String str;
        GarbageMonitor garbageMonitor = (GarbageMonitor) Dependency.get(GarbageMonitor.class);
        File file = new File(this.context.getCacheDir(), "leak");
        file.mkdirs();
        this.hprofUri = null;
        this.body.setLength(0);
        StringBuilder sb = this.body;
        sb.append("Build: ");
        sb.append(Build.DISPLAY);
        sb.append("\n\nProcesses:\n");
        ArrayList arrayList = new ArrayList();
        int myPid = Process.myPid();
        Iterator it = list.iterator();
        while (true) {
            String str2 = "\n";
            str = "DumpTruck";
            if (it.hasNext()) {
                int intValue = ((Long) it.next()).intValue();
                StringBuilder sb2 = this.body;
                sb2.append("  pid ");
                sb2.append(intValue);
                if (garbageMonitor != null) {
                    ProcessMemInfo memInfo = garbageMonitor.getMemInfo(intValue);
                    if (memInfo != null) {
                        StringBuilder sb3 = this.body;
                        sb3.append(":");
                        sb3.append(" up=");
                        sb3.append(memInfo.getUptime());
                        sb3.append(" rss=");
                        sb3.append(memInfo.currentRss);
                        this.rss = memInfo.currentRss;
                    }
                }
                if (intValue == myPid) {
                    String path = new File(file, String.format("heap-%d.ahprof", new Object[]{Integer.valueOf(intValue)})).getPath();
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Dumping memory info for process ");
                    sb4.append(intValue);
                    sb4.append(" to ");
                    sb4.append(path);
                    Log.v(str, sb4.toString());
                    try {
                        Debug.dumpHprofData(path);
                        arrayList.add(path);
                        this.body.append(" (hprof attached)");
                    } catch (IOException e) {
                        Log.e(str, "error dumping memory:", e);
                        StringBuilder sb5 = this.body;
                        sb5.append("\n** Could not dump heap: \n");
                        sb5.append(e.toString());
                        sb5.append(str2);
                    }
                }
                this.body.append(str2);
            } else {
                try {
                    break;
                } catch (IOException e2) {
                    Log.e(str, "unable to zip up heapdumps", e2);
                    StringBuilder sb6 = this.body;
                    sb6.append("\n** Could not zip up files: \n");
                    sb6.append(e2.toString());
                    sb6.append(str2);
                }
            }
        }
        String canonicalPath = new File(file, String.format("hprof-%d.zip", new Object[]{Long.valueOf(System.currentTimeMillis())})).getCanonicalPath();
        if (zipUp(canonicalPath, arrayList)) {
            this.hprofUri = FileProvider.getUriForFile(this.context, "com.android.systemui.fileprovider", new File(canonicalPath));
            StringBuilder sb7 = new StringBuilder();
            sb7.append("Heap dump accessible at URI: ");
            sb7.append(this.hprofUri);
            Log.v(str, sb7.toString());
        }
        return this;
    }

    public Intent createShareIntent() {
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.addFlags(268435456);
        intent.addFlags(1);
        intent.putExtra("android.intent.extra.SUBJECT", String.format("SystemUI memory dump (rss=%dM)", new Object[]{Long.valueOf(this.rss / 1024)}));
        intent.putExtra("android.intent.extra.TEXT", this.body.toString());
        if (this.hprofUri != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.hprofUri);
            intent.setType("application/zip");
            intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
            intent.setClipData(new ClipData(new ClipDescription("content", new String[]{"text/plain"}), new Item(this.hprofUri)));
            intent.addFlags(1);
        }
        return intent;
    }

    private static boolean zipUp(String str, ArrayList<String> arrayList) {
        BufferedInputStream bufferedInputStream;
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(str));
            try {
                byte[] bArr = new byte[1048576];
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(str2));
                    zipOutputStream.putNextEntry(new ZipEntry(str2));
                    while (true) {
                        int read = bufferedInputStream.read(bArr, 0, 1048576);
                        if (read <= 0) {
                            break;
                        }
                        zipOutputStream.write(bArr, 0, read);
                    }
                    zipOutputStream.closeEntry();
                    bufferedInputStream.close();
                }
                zipOutputStream.close();
                return true;
            } catch (Throwable th) {
                zipOutputStream.close();
                throw th;
            }
            throw th;
        } catch (IOException e) {
            Log.e("DumpTruck", "error zipping up profile data", e);
            return false;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }
}
