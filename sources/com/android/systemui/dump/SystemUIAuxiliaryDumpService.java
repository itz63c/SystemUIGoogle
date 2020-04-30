package com.android.systemui.dump;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SystemUIAuxiliaryDumpService extends Service {
    private final DumpManager mDumpManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public SystemUIAuxiliaryDumpService(DumpManager dumpManager) {
        this.mDumpManager = dumpManager;
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mDumpManager.dump(fileDescriptor, printWriter, new String[]{"--dump-priority", "NORMAL"});
    }
}
