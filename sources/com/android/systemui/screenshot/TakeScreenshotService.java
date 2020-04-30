package com.android.systemui.screenshot;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import java.util.function.Consumer;

public class TakeScreenshotService extends Service {
    private Handler mHandler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message message) {
            $$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g r6 = new Consumer(message.replyTo) {
                public final /* synthetic */ Messenger f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    C10591.lambda$handleMessage$0(this.f$0, (Uri) obj);
                }
            };
            String str = "TakeScreenshotService";
            if (!TakeScreenshotService.this.mUserManager.isUserUnlocked()) {
                Log.w(str, "Skipping screenshot because storage is locked!");
                post(new Runnable(r6) {
                    public final /* synthetic */ Consumer f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.accept(null);
                    }
                });
                return;
            }
            int i = message.what;
            if (i == 1) {
                TakeScreenshotService.this.mScreenshot.takeScreenshot(r6);
            } else if (i == 2) {
                TakeScreenshotService.this.mScreenshot.takeScreenshotPartial(r6);
            } else if (i != 3) {
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid screenshot option: ");
                sb.append(message.what);
                Log.d(str, sb.toString());
            } else {
                TakeScreenshotService.this.mScreenshot.handleImageAsScreenshot((Bitmap) message.getData().getParcelable("screenshot_screen_bitmap"), (Rect) message.getData().getParcelable("screenshot_screen_bounds"), (Insets) message.getData().getParcelable("screenshot_insets"), message.getData().getInt("screenshot_task_id"), r6);
            }
        }

        static /* synthetic */ void lambda$handleMessage$0(Messenger messenger, Uri uri) {
            try {
                messenger.send(Message.obtain(null, 1, uri));
            } catch (RemoteException unused) {
            }
        }
    };
    /* access modifiers changed from: private */
    public final GlobalScreenshot mScreenshot;
    private final GlobalScreenshotLegacy mScreenshotLegacy;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;

    public TakeScreenshotService(GlobalScreenshot globalScreenshot, GlobalScreenshotLegacy globalScreenshotLegacy, UserManager userManager) {
        this.mScreenshot = globalScreenshot;
        this.mScreenshotLegacy = globalScreenshotLegacy;
        this.mUserManager = userManager;
    }

    public IBinder onBind(Intent intent) {
        return new Messenger(this.mHandler).getBinder();
    }

    public boolean onUnbind(Intent intent) {
        GlobalScreenshot globalScreenshot = this.mScreenshot;
        if (globalScreenshot != null) {
            globalScreenshot.stopScreenshot();
        }
        GlobalScreenshotLegacy globalScreenshotLegacy = this.mScreenshotLegacy;
        if (globalScreenshotLegacy != null) {
            globalScreenshotLegacy.stopScreenshot();
        }
        return true;
    }
}
