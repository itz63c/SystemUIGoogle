package com.android.systemui.glwallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.util.Log;
import android.util.MathUtils;
import android.util.Size;
import android.view.DisplayInfo;
import com.android.systemui.C2016R$raw;
import com.android.systemui.glwallpaper.GLWallpaperRenderer.SurfaceProxy;
import com.android.systemui.glwallpaper.ImageRevealHelper.RevealStateListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class ImageWallpaperRenderer implements GLWallpaperRenderer, RevealStateListener {
    private static final String TAG = "ImageWallpaperRenderer";
    private Bitmap mBitmap;
    private final ImageProcessHelper mImageProcessHelper;
    private final ImageRevealHelper mImageRevealHelper;
    private final ImageGLProgram mProgram;
    private SurfaceProxy mProxy;
    private final Rect mScissor;
    private boolean mScissorMode;
    private final Rect mSurfaceSize = new Rect();
    private final Rect mViewport = new Rect();
    private final ImageGLWallpaper mWallpaper;
    private final WallpaperManager mWallpaperManager;
    private boolean mWcgContent;
    private float mXOffset;
    private float mYOffset;

    public ImageWallpaperRenderer(Context context, SurfaceProxy surfaceProxy) {
        WallpaperManager wallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
        this.mWallpaperManager = wallpaperManager;
        if (wallpaperManager == null) {
            Log.w(TAG, "WallpaperManager not available");
        }
        DisplayInfo displayInfo = new DisplayInfo();
        context.getDisplay().getDisplayInfo(displayInfo);
        if (context.getResources().getConfiguration().orientation == 1) {
            this.mScissor = new Rect(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        } else {
            this.mScissor = new Rect(0, 0, displayInfo.logicalHeight, displayInfo.logicalWidth);
        }
        this.mProxy = surfaceProxy;
        this.mProgram = new ImageGLProgram(context);
        this.mWallpaper = new ImageGLWallpaper(this.mProgram);
        this.mImageProcessHelper = new ImageProcessHelper();
        this.mImageRevealHelper = new ImageRevealHelper(this);
        startProcessingImage();
    }

    /* access modifiers changed from: protected */
    public void startProcessingImage() {
        if (loadBitmap()) {
            this.mImageProcessHelper.start(this.mBitmap);
        }
    }

    public boolean isWcgContent() {
        return this.mWcgContent;
    }

    public void onSurfaceCreated() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.mProgram.useGLProgram(C2016R$raw.image_wallpaper_vertex_shader, C2016R$raw.image_wallpaper_fragment_shader);
        if (!loadBitmap()) {
            Log.w(TAG, "reload bitmap failed!");
        }
        this.mWallpaper.setup(this.mBitmap);
        this.mBitmap = null;
    }

    /* access modifiers changed from: protected */
    public boolean loadBitmap() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("loadBitmap: mBitmap=");
        sb.append(this.mBitmap);
        Log.d(str, sb.toString());
        WallpaperManager wallpaperManager = this.mWallpaperManager;
        if (wallpaperManager != null && this.mBitmap == null) {
            this.mBitmap = wallpaperManager.getBitmap(false);
            this.mWcgContent = this.mWallpaperManager.wallpaperSupportsWcg(1);
            this.mWallpaperManager.forgetLoadedWallpaper();
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null) {
                this.mSurfaceSize.set(0, 0, bitmap.getWidth(), this.mBitmap.getHeight());
            }
        }
        Log.d(TAG, "loadBitmap done");
        if (this.mBitmap != null) {
            return true;
        }
        return false;
    }

    public void onSurfaceChanged(int i, int i2) {
        GLES20.glViewport(0, 0, i, i2);
    }

    public void onDrawFrame() {
        float threshold = this.mImageProcessHelper.getThreshold();
        float reveal = this.mImageRevealHelper.getReveal();
        GLES20.glUniform1f(this.mWallpaper.getHandle("uAod2Opacity"), 1.0f);
        GLES20.glUniform1f(this.mWallpaper.getHandle("uPer85"), threshold);
        GLES20.glUniform1f(this.mWallpaper.getHandle("uReveal"), reveal);
        GLES20.glClear(16384);
        if (this.mScissorMode) {
            scaleViewport(reveal);
        } else {
            GLES20.glViewport(0, 0, this.mSurfaceSize.width(), this.mSurfaceSize.height());
        }
        this.mWallpaper.useTexture();
        this.mWallpaper.draw();
    }

    public void updateAmbientMode(boolean z, long j) {
        this.mImageRevealHelper.updateAwake(!z, j);
    }

    public void updateOffsets(float f, float f2) {
        this.mXOffset = f;
        this.mYOffset = f2;
        int width = (int) (((float) (this.mSurfaceSize.width() - this.mScissor.width())) * f);
        int width2 = this.mScissor.width() + width;
        Rect rect = this.mScissor;
        rect.set(width, rect.top, width2, rect.bottom);
    }

    public Size reportSurfaceSize() {
        return new Size(this.mSurfaceSize.width(), this.mSurfaceSize.height());
    }

    public void finish() {
        this.mProxy = null;
    }

    private void scaleViewport(float f) {
        Rect rect = this.mScissor;
        int i = rect.left;
        int i2 = rect.top;
        int width = rect.width();
        int height = this.mScissor.height();
        float lerp = MathUtils.lerp(1.0f, 1.1f, f);
        float f2 = (1.0f - lerp) / 2.0f;
        float f3 = (float) width;
        float f4 = (float) height;
        this.mViewport.set((int) (((float) i) + (f3 * f2)), (int) (((float) i2) + (f2 * f4)), (int) (f3 * lerp), (int) (f4 * lerp));
        Rect rect2 = this.mViewport;
        GLES20.glViewport(rect2.left, rect2.top, rect2.right, rect2.bottom);
    }

    public void onRevealStateChanged() {
        this.mProxy.requestRender();
    }

    public void onRevealStart(boolean z) {
        if (z) {
            this.mScissorMode = true;
            this.mWallpaper.adjustTextureCoordinates(this.mSurfaceSize, this.mScissor, this.mXOffset, this.mYOffset);
        }
        this.mProxy.preRender();
    }

    public void onRevealEnd() {
        if (this.mScissorMode) {
            this.mScissorMode = false;
            this.mWallpaper.adjustTextureCoordinates(null, null, 0.0f, 0.0f);
            this.mProxy.requestRender();
        }
        this.mProxy.postRender();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mProxy=");
        printWriter.print(this.mProxy);
        printWriter.print(str);
        printWriter.print("mSurfaceSize=");
        printWriter.print(this.mSurfaceSize);
        printWriter.print(str);
        printWriter.print("mScissor=");
        printWriter.print(this.mScissor);
        printWriter.print(str);
        printWriter.print("mViewport=");
        printWriter.print(this.mViewport);
        printWriter.print(str);
        printWriter.print("mScissorMode=");
        printWriter.print(this.mScissorMode);
        printWriter.print(str);
        printWriter.print("mXOffset=");
        printWriter.print(this.mXOffset);
        printWriter.print(str);
        printWriter.print("mYOffset=");
        printWriter.print(this.mYOffset);
        printWriter.print(str);
        printWriter.print("threshold=");
        printWriter.print(this.mImageProcessHelper.getThreshold());
        printWriter.print(str);
        printWriter.print("mReveal=");
        printWriter.print(this.mImageRevealHelper.getReveal());
        printWriter.print(str);
        printWriter.print("mWcgContent=");
        printWriter.print(this.mWcgContent);
        this.mWallpaper.dump(str, fileDescriptor, printWriter, strArr);
    }
}
