package com.android.systemui.screenshot;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaActionSound;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;

@Deprecated
public class GlobalScreenshotLegacy {
    private ImageView mBackgroundView;
    private float mBgPadding;
    private float mBgPaddingScale;
    private MediaActionSound mCameraSound;
    private Context mContext;
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    private final ScreenshotNotificationsController mNotificationsController;
    private ImageView mScreenshotFlash = ((ImageView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_legacy_flash));
    private View mScreenshotLayout;
    private ScreenshotSelectorView mScreenshotSelectorView = ((ScreenshotSelectorView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_legacy_selector));
    private ImageView mScreenshotView = ((ImageView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_legacy));
    private LayoutParams mWindowLayoutParams;
    private WindowManager mWindowManager;

    static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public GlobalScreenshotLegacy(Context context, Resources resources, LayoutInflater layoutInflater, ScreenshotNotificationsController screenshotNotificationsController) {
        this.mContext = context;
        this.mNotificationsController = screenshotNotificationsController;
        View inflate = layoutInflater.inflate(C2013R$layout.global_screenshot_legacy, null);
        this.mScreenshotLayout = inflate;
        this.mBackgroundView = (ImageView) inflate.findViewById(C2011R$id.global_screenshot_legacy_background);
        this.mScreenshotLayout.setFocusable(true);
        this.mScreenshotSelectorView.setFocusable(true);
        this.mScreenshotSelectorView.setFocusableInTouchMode(true);
        this.mScreenshotLayout.setOnTouchListener($$Lambda$GlobalScreenshotLegacy$qwq1ocOBDT0FoPyQlDCaMU5A1wQ.INSTANCE);
        LayoutParams layoutParams = new LayoutParams(-1, -1, 0, 0, 2036, 525568, -3);
        this.mWindowLayoutParams = layoutParams;
        layoutParams.setTitle("ScreenshotAnimation");
        LayoutParams layoutParams2 = this.mWindowLayoutParams;
        layoutParams2.layoutInDisplayCutoutMode = 3;
        layoutParams2.setFitInsetsTypes(0);
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        this.mWindowManager = windowManager;
        this.mDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mDisplayMetrics = displayMetrics;
        this.mDisplay.getRealMetrics(displayMetrics);
        float dimensionPixelSize = (float) resources.getDimensionPixelSize(C2009R$dimen.global_screenshot_legacy_bg_padding);
        this.mBgPadding = dimensionPixelSize;
        this.mBgPaddingScale = dimensionPixelSize / ((float) this.mDisplayMetrics.widthPixels);
        MediaActionSound mediaActionSound = new MediaActionSound();
        this.mCameraSound = mediaActionSound;
        mediaActionSound.load(0);
    }

    /* access modifiers changed from: 0000 */
    public void stopScreenshot() {
        if (this.mScreenshotSelectorView.getSelectionRect() != null) {
            this.mWindowManager.removeView(this.mScreenshotLayout);
            this.mScreenshotSelectorView.stopSelection();
        }
    }
}
