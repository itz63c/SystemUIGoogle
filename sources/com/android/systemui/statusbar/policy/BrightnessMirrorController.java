package com.android.systemui.statusbar.policy;

import android.content.res.Resources;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2013R$layout;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import java.util.Objects;
import java.util.function.Consumer;

public class BrightnessMirrorController implements CallbackController<BrightnessMirrorListener> {
    private View mBrightnessMirror;
    private final ArraySet<BrightnessMirrorListener> mBrightnessMirrorListeners = new ArraySet<>();
    private final int[] mInt2Cache = new int[2];
    private final NotificationPanelViewController mNotificationPanel;
    private final NotificationShadeWindowView mStatusBarWindow;
    private final Consumer<Boolean> mVisibilityCallback;

    public interface BrightnessMirrorListener {
        void onBrightnessMirrorReinflated(View view);
    }

    public BrightnessMirrorController(NotificationShadeWindowView notificationShadeWindowView, NotificationPanelViewController notificationPanelViewController, Consumer<Boolean> consumer) {
        this.mStatusBarWindow = notificationShadeWindowView;
        this.mBrightnessMirror = notificationShadeWindowView.findViewById(C2011R$id.brightness_mirror);
        this.mNotificationPanel = notificationPanelViewController;
        notificationPanelViewController.setPanelAlphaEndAction(new Runnable() {
            public final void run() {
                BrightnessMirrorController.this.lambda$new$0$BrightnessMirrorController();
            }
        });
        this.mVisibilityCallback = consumer;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$BrightnessMirrorController() {
        this.mBrightnessMirror.setVisibility(4);
    }

    public void showMirror() {
        this.mBrightnessMirror.setVisibility(0);
        this.mVisibilityCallback.accept(Boolean.TRUE);
        this.mNotificationPanel.setPanelAlpha(0, true);
    }

    public void hideMirror() {
        this.mVisibilityCallback.accept(Boolean.FALSE);
        this.mNotificationPanel.setPanelAlpha(255, true);
    }

    public void setLocation(View view) {
        view.getLocationInWindow(this.mInt2Cache);
        int width = this.mInt2Cache[0] + (view.getWidth() / 2);
        int height = this.mInt2Cache[1] + (view.getHeight() / 2);
        this.mBrightnessMirror.setTranslationX(0.0f);
        this.mBrightnessMirror.setTranslationY(0.0f);
        this.mBrightnessMirror.getLocationInWindow(this.mInt2Cache);
        int height2 = this.mInt2Cache[1] + (this.mBrightnessMirror.getHeight() / 2);
        this.mBrightnessMirror.setTranslationX((float) (width - (this.mInt2Cache[0] + (this.mBrightnessMirror.getWidth() / 2))));
        this.mBrightnessMirror.setTranslationY((float) (height - height2));
    }

    public View getMirror() {
        return this.mBrightnessMirror;
    }

    public void updateResources() {
        LayoutParams layoutParams = (LayoutParams) this.mBrightnessMirror.getLayoutParams();
        Resources resources = this.mBrightnessMirror.getResources();
        layoutParams.width = resources.getDimensionPixelSize(C2009R$dimen.qs_panel_width);
        layoutParams.height = resources.getDimensionPixelSize(C2009R$dimen.brightness_mirror_height);
        layoutParams.gravity = resources.getInteger(C2012R$integer.notification_panel_layout_gravity);
        this.mBrightnessMirror.setLayoutParams(layoutParams);
    }

    public void onOverlayChanged() {
        reinflate();
    }

    public void onDensityOrFontScaleChanged() {
        reinflate();
    }

    private void reinflate() {
        int indexOfChild = this.mStatusBarWindow.indexOfChild(this.mBrightnessMirror);
        this.mStatusBarWindow.removeView(this.mBrightnessMirror);
        View inflate = LayoutInflater.from(this.mBrightnessMirror.getContext()).inflate(C2013R$layout.brightness_mirror, this.mStatusBarWindow, false);
        this.mBrightnessMirror = inflate;
        this.mStatusBarWindow.addView(inflate, indexOfChild);
        for (int i = 0; i < this.mBrightnessMirrorListeners.size(); i++) {
            ((BrightnessMirrorListener) this.mBrightnessMirrorListeners.valueAt(i)).onBrightnessMirrorReinflated(this.mBrightnessMirror);
        }
    }

    public void addCallback(BrightnessMirrorListener brightnessMirrorListener) {
        Objects.requireNonNull(brightnessMirrorListener);
        this.mBrightnessMirrorListeners.add(brightnessMirrorListener);
    }

    public void removeCallback(BrightnessMirrorListener brightnessMirrorListener) {
        this.mBrightnessMirrorListeners.remove(brightnessMirrorListener);
    }

    public void onUiModeChanged() {
        reinflate();
    }
}
