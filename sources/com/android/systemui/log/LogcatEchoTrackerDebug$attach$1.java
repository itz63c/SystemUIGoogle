package com.android.systemui.log;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LogcatEchoTrackerDebug.kt */
public final class LogcatEchoTrackerDebug$attach$1 extends ContentObserver {
    final /* synthetic */ LogcatEchoTrackerDebug this$0;

    LogcatEchoTrackerDebug$attach$1(LogcatEchoTrackerDebug logcatEchoTrackerDebug, Looper looper, Handler handler) {
        this.this$0 = logcatEchoTrackerDebug;
        super(handler);
    }

    public void onChange(boolean z, Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "uri");
        super.onChange(z, uri);
        this.this$0.cachedBufferLevels.clear();
    }
}
