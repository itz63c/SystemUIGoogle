package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.IWallpaperManager;
import android.app.IWallpaperManagerCallback.Stub;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.IndentingPrintWriter;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import libcore.io.IoUtils;

public class LockscreenWallpaper extends Stub implements Runnable, Dumpable {
    /* access modifiers changed from: private */
    public Bitmap mCache;
    /* access modifiers changed from: private */
    public boolean mCached;
    private int mCurrentUserId = ActivityManager.getCurrentUser();

    /* renamed from: mH */
    private final Handler f75mH;
    /* access modifiers changed from: private */
    public AsyncTask<Void, Void, LoaderResult> mLoader;
    /* access modifiers changed from: private */
    public final NotificationMediaManager mMediaManager;
    private UserHandle mSelectedUser;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    private final WallpaperManager mWallpaperManager;

    private static class LoaderResult {
        public final Bitmap bitmap;
        public final boolean success;

        LoaderResult(boolean z, Bitmap bitmap2) {
            this.success = z;
            this.bitmap = bitmap2;
        }

        static LoaderResult success(Bitmap bitmap2) {
            return new LoaderResult(true, bitmap2);
        }

        static LoaderResult fail() {
            return new LoaderResult(false, null);
        }
    }

    public static class WallpaperDrawable extends DrawableWrapper {
        private final ConstantState mState;
        private final Rect mTmpRect;

        static class ConstantState extends android.graphics.drawable.Drawable.ConstantState {
            /* access modifiers changed from: private */
            public final Bitmap mBackground;

            public int getChangingConfigurations() {
                return 0;
            }

            ConstantState(Bitmap bitmap) {
                this.mBackground = bitmap;
            }

            public Drawable newDrawable() {
                return newDrawable(null);
            }

            public Drawable newDrawable(Resources resources) {
                return new WallpaperDrawable(resources, this);
            }
        }

        public int getIntrinsicHeight() {
            return -1;
        }

        public int getIntrinsicWidth() {
            return -1;
        }

        public WallpaperDrawable(Resources resources, Bitmap bitmap) {
            this(resources, new ConstantState(bitmap));
        }

        private WallpaperDrawable(Resources resources, ConstantState constantState) {
            super(new BitmapDrawable(resources, constantState.mBackground));
            this.mTmpRect = new Rect();
            this.mState = constantState;
        }

        public void setXfermode(Xfermode xfermode) {
            getDrawable().setXfermode(xfermode);
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect rect) {
            float f;
            float f2;
            int width = getBounds().width();
            int height = getBounds().height();
            int width2 = this.mState.mBackground.getWidth();
            int height2 = this.mState.mBackground.getHeight();
            if (width2 * height > width * height2) {
                f2 = (float) height;
                f = (float) height2;
            } else {
                f2 = (float) width;
                f = (float) width2;
            }
            float f3 = f2 / f;
            if (f3 <= 1.0f) {
                f3 = 1.0f;
            }
            float f4 = ((float) height2) * f3;
            float f5 = (((float) height) - f4) * 0.5f;
            this.mTmpRect.set(rect.left, rect.top + Math.round(f5), rect.left + Math.round(((float) width2) * f3), rect.top + Math.round(f4 + f5));
            super.onBoundsChange(this.mTmpRect);
        }

        public ConstantState getConstantState() {
            return this.mState;
        }
    }

    public void onWallpaperColorsChanged(WallpaperColors wallpaperColors, int i, int i2) {
    }

    public LockscreenWallpaper(WallpaperManager wallpaperManager, IWallpaperManager iWallpaperManager, KeyguardUpdateMonitor keyguardUpdateMonitor, DumpManager dumpManager, NotificationMediaManager notificationMediaManager, Handler handler) {
        dumpManager.registerDumpable(LockscreenWallpaper.class.getSimpleName(), this);
        this.mWallpaperManager = wallpaperManager;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mMediaManager = notificationMediaManager;
        this.f75mH = handler;
        if (iWallpaperManager != null) {
            try {
                iWallpaperManager.setLockWallpaperCallback(this);
            } catch (RemoteException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("System dead?");
                sb.append(e);
                Log.e("LockscreenWallpaper", sb.toString());
            }
        }
    }

    public Bitmap getBitmap() {
        if (this.mCached) {
            return this.mCache;
        }
        boolean z = true;
        if (!this.mWallpaperManager.isWallpaperSupported()) {
            this.mCached = true;
            this.mCache = null;
            return null;
        }
        LoaderResult loadBitmap = loadBitmap(this.mCurrentUserId, this.mSelectedUser);
        if (loadBitmap.success) {
            this.mCached = true;
            KeyguardUpdateMonitor keyguardUpdateMonitor = this.mUpdateMonitor;
            if (loadBitmap.bitmap == null) {
                z = false;
            }
            keyguardUpdateMonitor.setHasLockscreenWallpaper(z);
            this.mCache = loadBitmap.bitmap;
        }
        return this.mCache;
    }

    public LoaderResult loadBitmap(int i, UserHandle userHandle) {
        if (!this.mWallpaperManager.isWallpaperSupported()) {
            return LoaderResult.success(null);
        }
        if (userHandle != null) {
            i = userHandle.getIdentifier();
        }
        ParcelFileDescriptor wallpaperFile = this.mWallpaperManager.getWallpaperFile(2, i);
        if (wallpaperFile != null) {
            try {
                Options options = new Options();
                options.inPreferredConfig = Config.HARDWARE;
                return LoaderResult.success(BitmapFactory.decodeFileDescriptor(wallpaperFile.getFileDescriptor(), null, options));
            } catch (OutOfMemoryError e) {
                Log.w("LockscreenWallpaper", "Can't decode file", e);
                return LoaderResult.fail();
            } finally {
                IoUtils.closeQuietly(wallpaperFile);
            }
        } else if (userHandle != null) {
            return LoaderResult.success(this.mWallpaperManager.getBitmapAsUser(userHandle.getIdentifier(), true));
        } else {
            return LoaderResult.success(null);
        }
    }

    public void setCurrentUser(int i) {
        if (i != this.mCurrentUserId) {
            UserHandle userHandle = this.mSelectedUser;
            if (userHandle == null || i != userHandle.getIdentifier()) {
                this.mCached = false;
            }
            this.mCurrentUserId = i;
        }
    }

    public void onWallpaperChanged() {
        postUpdateWallpaper();
    }

    private void postUpdateWallpaper() {
        Handler handler = this.f75mH;
        if (handler == null) {
            Log.wtfStack("LockscreenWallpaper", "Trying to use LockscreenWallpaper before initialization.");
            return;
        }
        handler.removeCallbacks(this);
        this.f75mH.post(this);
    }

    public void run() {
        AsyncTask<Void, Void, LoaderResult> asyncTask = this.mLoader;
        if (asyncTask != null) {
            asyncTask.cancel(false);
        }
        final int i = this.mCurrentUserId;
        final UserHandle userHandle = this.mSelectedUser;
        this.mLoader = new AsyncTask<Void, Void, LoaderResult>() {
            /* access modifiers changed from: protected */
            public LoaderResult doInBackground(Void... voidArr) {
                return LockscreenWallpaper.this.loadBitmap(i, userHandle);
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(LoaderResult loaderResult) {
                super.onPostExecute(loaderResult);
                if (!isCancelled()) {
                    if (loaderResult.success) {
                        LockscreenWallpaper.this.mCached = true;
                        LockscreenWallpaper.this.mCache = loaderResult.bitmap;
                        LockscreenWallpaper.this.mUpdateMonitor.setHasLockscreenWallpaper(loaderResult.bitmap != null);
                        LockscreenWallpaper.this.mMediaManager.updateMediaMetaData(true, true);
                    }
                    LockscreenWallpaper.this.mLoader = null;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(LockscreenWallpaper.class.getSimpleName());
        sb.append(":");
        printWriter.println(sb.toString());
        IndentingPrintWriter increaseIndent = new IndentingPrintWriter(printWriter, "  ").increaseIndent();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("mCached=");
        sb2.append(this.mCached);
        increaseIndent.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("mCache=");
        sb3.append(this.mCache);
        increaseIndent.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("mCurrentUserId=");
        sb4.append(this.mCurrentUserId);
        increaseIndent.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("mSelectedUser=");
        sb5.append(this.mSelectedUser);
        increaseIndent.println(sb5.toString());
    }
}
