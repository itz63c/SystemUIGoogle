package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import android.view.IWindowManager;
import android.view.MotionEvent;
import com.android.systemui.statusbar.AutoHideUiElement;
import java.util.Set;

public class AutoHideController {
    private final Runnable mAutoHide = new Runnable() {
        public final void run() {
            AutoHideController.this.lambda$new$0$AutoHideController();
        }
    };
    private boolean mAutoHideSuspended;
    private int mDisplayId;
    private final Set<AutoHideUiElement> mElements;
    private final Handler mHandler;
    private final IWindowManager mWindowManagerService;

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AutoHideController() {
        if (isAnyTransientBarShown()) {
            hideTransientBars();
        }
    }

    public AutoHideController(Context context, Handler handler, IWindowManager iWindowManager) {
        this.mHandler = handler;
        this.mWindowManagerService = iWindowManager;
        this.mElements = new ArraySet();
        this.mDisplayId = context.getDisplayId();
    }

    public void addAutoHideUiElement(AutoHideUiElement autoHideUiElement) {
        if (autoHideUiElement != null) {
            this.mElements.add(autoHideUiElement);
        }
    }

    public void removeAutoHideUiElement(AutoHideUiElement autoHideUiElement) {
        if (autoHideUiElement != null) {
            this.mElements.remove(autoHideUiElement);
        }
    }

    private void hideTransientBars() {
        try {
            this.mWindowManagerService.hideTransientBars(this.mDisplayId);
        } catch (RemoteException unused) {
            Log.w("AutoHideController", "Cannot get WindowManager");
        }
        for (AutoHideUiElement hide : this.mElements) {
            hide.hide();
        }
    }

    /* access modifiers changed from: 0000 */
    public void resumeSuspendedAutoHide() {
        if (this.mAutoHideSuspended) {
            scheduleAutoHide();
            Runnable checkBarModesRunnable = getCheckBarModesRunnable();
            if (checkBarModesRunnable != null) {
                this.mHandler.postDelayed(checkBarModesRunnable, 500);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void suspendAutoHide() {
        this.mHandler.removeCallbacks(this.mAutoHide);
        Runnable checkBarModesRunnable = getCheckBarModesRunnable();
        if (checkBarModesRunnable != null) {
            this.mHandler.removeCallbacks(checkBarModesRunnable);
        }
        this.mAutoHideSuspended = isAnyTransientBarShown();
    }

    public void touchAutoHide() {
        if (isAnyTransientBarShown()) {
            scheduleAutoHide();
        } else {
            cancelAutoHide();
        }
    }

    private Runnable getCheckBarModesRunnable() {
        if (this.mElements.isEmpty()) {
            return null;
        }
        return new Runnable() {
            public final void run() {
                AutoHideController.this.lambda$getCheckBarModesRunnable$1$AutoHideController();
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getCheckBarModesRunnable$1 */
    public /* synthetic */ void lambda$getCheckBarModesRunnable$1$AutoHideController() {
        for (AutoHideUiElement synchronizeState : this.mElements) {
            synchronizeState.synchronizeState();
        }
    }

    private void cancelAutoHide() {
        this.mAutoHideSuspended = false;
        this.mHandler.removeCallbacks(this.mAutoHide);
    }

    private void scheduleAutoHide() {
        cancelAutoHide();
        this.mHandler.postDelayed(this.mAutoHide, 2250);
    }

    /* access modifiers changed from: 0000 */
    public void checkUserAutoHide(MotionEvent motionEvent) {
        boolean z = isAnyTransientBarShown() && motionEvent.getAction() == 4 && motionEvent.getX() == 0.0f && motionEvent.getY() == 0.0f;
        for (AutoHideUiElement shouldHideOnTouch : this.mElements) {
            z &= shouldHideOnTouch.shouldHideOnTouch();
        }
        if (z) {
            userAutoHide();
        }
    }

    private void userAutoHide() {
        cancelAutoHide();
        this.mHandler.postDelayed(this.mAutoHide, 350);
    }

    private boolean isAnyTransientBarShown() {
        for (AutoHideUiElement isVisible : this.mElements) {
            if (isVisible.isVisible()) {
                return true;
            }
        }
        return false;
    }
}
