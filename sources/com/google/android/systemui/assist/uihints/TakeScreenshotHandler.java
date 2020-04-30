package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.internal.util.ScreenshotHelper;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.TakeScreenshotListener;
import java.util.function.Consumer;

final class TakeScreenshotHandler implements TakeScreenshotListener {
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ScreenshotHelper mScreenshotHelper;

    TakeScreenshotHandler(Context context) {
        this.mContext = context;
        this.mScreenshotHelper = new ScreenshotHelper(context);
    }

    public void onTakeScreenshot(PendingIntent pendingIntent) {
        this.mScreenshotHelper.takeScreenshot(1, true, true, this.mHandler, new Consumer(pendingIntent) {
            public final /* synthetic */ PendingIntent f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                TakeScreenshotHandler.this.lambda$onTakeScreenshot$0$TakeScreenshotHandler(this.f$1, (Uri) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTakeScreenshot$0 */
    public /* synthetic */ void lambda$onTakeScreenshot$0$TakeScreenshotHandler(PendingIntent pendingIntent, Uri uri) {
        if (pendingIntent != null) {
            try {
                Intent intent = new Intent();
                intent.putExtra("success", uri != null);
                intent.putExtra("uri", uri);
                pendingIntent.send(this.mContext, 0, intent);
            } catch (CanceledException unused) {
                Log.w("TakeScreenshotHandler", "Intent was cancelled");
            }
        }
    }
}
