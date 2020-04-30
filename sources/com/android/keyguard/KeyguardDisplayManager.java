package com.android.keyguard;

import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.InvalidDisplayException;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2018R$style;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.util.InjectionInflationController;

public class KeyguardDisplayManager {
    /* access modifiers changed from: private */
    public static boolean DEBUG = false;
    private final Context mContext;
    private final DisplayListener mDisplayListener = new DisplayListener() {
        public void onDisplayAdded(int i) {
            Display display = KeyguardDisplayManager.this.mDisplayService.getDisplay(i);
            if (KeyguardDisplayManager.this.mShowing) {
                KeyguardDisplayManager.this.updateNavigationBarVisibility(i, false);
                KeyguardDisplayManager.this.showPresentation(display);
            }
        }

        public void onDisplayChanged(int i) {
            if (!(i == 0 || ((Presentation) KeyguardDisplayManager.this.mPresentations.get(i)) == null || !KeyguardDisplayManager.this.mShowing)) {
                KeyguardDisplayManager.this.hidePresentation(i);
                Display display = KeyguardDisplayManager.this.mDisplayService.getDisplay(i);
                if (display != null) {
                    KeyguardDisplayManager.this.showPresentation(display);
                }
            }
        }

        public void onDisplayRemoved(int i) {
            KeyguardDisplayManager.this.hidePresentation(i);
        }
    };
    /* access modifiers changed from: private */
    public final DisplayManager mDisplayService;
    private final InjectionInflationController mInjectableInflater;
    private final MediaRouter mMediaRouter;
    private final SimpleCallback mMediaRouterCallback = new SimpleCallback() {
        public void onRouteSelected(MediaRouter mediaRouter, int i, RouteInfo routeInfo) {
            if (KeyguardDisplayManager.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteSelected: type=");
                sb.append(i);
                sb.append(", info=");
                sb.append(routeInfo);
                Log.d("KeyguardDisplayManager", sb.toString());
            }
            KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
            keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
        }

        public void onRouteUnselected(MediaRouter mediaRouter, int i, RouteInfo routeInfo) {
            if (KeyguardDisplayManager.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteUnselected: type=");
                sb.append(i);
                sb.append(", info=");
                sb.append(routeInfo);
                Log.d("KeyguardDisplayManager", sb.toString());
            }
            KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
            keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
        }

        public void onRoutePresentationDisplayChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            if (KeyguardDisplayManager.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRoutePresentationDisplayChanged: info=");
                sb.append(routeInfo);
                Log.d("KeyguardDisplayManager", sb.toString());
            }
            KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
            keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
        }
    };
    private final NavigationBarController mNavBarController = ((NavigationBarController) Dependency.get(NavigationBarController.class));
    /* access modifiers changed from: private */
    public final SparseArray<Presentation> mPresentations = new SparseArray<>();
    /* access modifiers changed from: private */
    public boolean mShowing;
    private final DisplayInfo mTmpDisplayInfo = new DisplayInfo();

    @VisibleForTesting
    static final class KeyguardPresentation extends Presentation {
        /* access modifiers changed from: private */
        public View mClock;
        private final InjectionInflationController mInjectableInflater;
        /* access modifiers changed from: private */
        public int mMarginLeft;
        /* access modifiers changed from: private */
        public int mMarginTop;
        Runnable mMoveTextRunnable = new Runnable() {
            public void run() {
                int access$1000 = KeyguardPresentation.this.mMarginTop + ((int) (Math.random() * ((double) (KeyguardPresentation.this.mUsableHeight - KeyguardPresentation.this.mClock.getHeight()))));
                KeyguardPresentation.this.mClock.setTranslationX((float) (KeyguardPresentation.this.mMarginLeft + ((int) (Math.random() * ((double) (KeyguardPresentation.this.mUsableWidth - KeyguardPresentation.this.mClock.getWidth()))))));
                KeyguardPresentation.this.mClock.setTranslationY((float) access$1000);
                KeyguardPresentation.this.mClock.postDelayed(KeyguardPresentation.this.mMoveTextRunnable, 10000);
            }
        };
        /* access modifiers changed from: private */
        public int mUsableHeight;
        /* access modifiers changed from: private */
        public int mUsableWidth;

        public void cancel() {
        }

        KeyguardPresentation(Context context, Display display, InjectionInflationController injectionInflationController) {
            super(context, display, C2018R$style.Theme_SystemUI_KeyguardPresentation);
            this.mInjectableInflater = injectionInflationController;
            getWindow().setType(2009);
            setCancelable(false);
        }

        public void onDetachedFromWindow() {
            this.mClock.removeCallbacks(this.mMoveTextRunnable);
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            Point point = new Point();
            getDisplay().getSize(point);
            int i = point.x;
            this.mUsableWidth = (i * 80) / 100;
            int i2 = point.y;
            this.mUsableHeight = (i2 * 80) / 100;
            this.mMarginLeft = (i * 20) / 200;
            this.mMarginTop = (i2 * 20) / 200;
            setContentView(this.mInjectableInflater.injectable(LayoutInflater.from(getContext())).inflate(C2013R$layout.keyguard_presentation, null));
            getWindow().getDecorView().setSystemUiVisibility(1792);
            getWindow().getAttributes().setFitInsetsTypes(0);
            getWindow().setNavigationBarContrastEnforced(false);
            getWindow().setNavigationBarColor(0);
            View findViewById = findViewById(C2011R$id.clock);
            this.mClock = findViewById;
            findViewById.post(this.mMoveTextRunnable);
        }
    }

    public KeyguardDisplayManager(Context context, InjectionInflationController injectionInflationController) {
        this.mContext = context;
        this.mInjectableInflater = injectionInflationController;
        this.mMediaRouter = (MediaRouter) context.getSystemService(MediaRouter.class);
        DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mDisplayService = displayManager;
        displayManager.registerDisplayListener(this.mDisplayListener, null);
    }

    private boolean isKeyguardShowable(Display display) {
        String str = "KeyguardDisplayManager";
        if (display == null) {
            if (DEBUG) {
                Log.i(str, "Cannot show Keyguard on null display");
            }
            return false;
        } else if (display.getDisplayId() == 0) {
            if (DEBUG) {
                Log.i(str, "Do not show KeyguardPresentation on the default display");
            }
            return false;
        } else {
            display.getDisplayInfo(this.mTmpDisplayInfo);
            if ((this.mTmpDisplayInfo.flags & 4) == 0) {
                return true;
            }
            if (DEBUG) {
                Log.i(str, "Do not show KeyguardPresentation on a private display");
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean showPresentation(Display display) {
        if (!isKeyguardShowable(display)) {
            return false;
        }
        String str = "KeyguardDisplayManager";
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Keyguard enabled on display: ");
            sb.append(display);
            Log.i(str, sb.toString());
        }
        int displayId = display.getDisplayId();
        if (((Presentation) this.mPresentations.get(displayId)) == null) {
            KeyguardPresentation keyguardPresentation = new KeyguardPresentation(this.mContext, display, this.mInjectableInflater);
            keyguardPresentation.setOnDismissListener(new OnDismissListener(displayId) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    KeyguardDisplayManager.this.lambda$showPresentation$0$KeyguardDisplayManager(this.f$1, dialogInterface);
                }
            });
            try {
                keyguardPresentation.show();
            } catch (InvalidDisplayException e) {
                Log.w(str, "Invalid display:", e);
                keyguardPresentation = null;
            }
            if (keyguardPresentation != null) {
                this.mPresentations.append(displayId, keyguardPresentation);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPresentation$0 */
    public /* synthetic */ void lambda$showPresentation$0$KeyguardDisplayManager(int i, DialogInterface dialogInterface) {
        if (this.mPresentations.get(i) != null) {
            this.mPresentations.remove(i);
        }
    }

    /* access modifiers changed from: private */
    public void hidePresentation(int i) {
        Presentation presentation = (Presentation) this.mPresentations.get(i);
        if (presentation != null) {
            presentation.dismiss();
            this.mPresentations.remove(i);
        }
    }

    public void show() {
        if (!this.mShowing) {
            if (DEBUG) {
                Log.v("KeyguardDisplayManager", "show");
            }
            this.mMediaRouter.addCallback(4, this.mMediaRouterCallback, 8);
            updateDisplays(true);
        }
        this.mShowing = true;
    }

    public void hide() {
        if (this.mShowing) {
            if (DEBUG) {
                Log.v("KeyguardDisplayManager", "hide");
            }
            this.mMediaRouter.removeCallback(this.mMediaRouterCallback);
            updateDisplays(false);
        }
        this.mShowing = false;
    }

    /* access modifiers changed from: protected */
    public boolean updateDisplays(boolean z) {
        Display[] displays;
        boolean z2 = false;
        if (z) {
            boolean z3 = false;
            for (Display display : this.mDisplayService.getDisplays()) {
                updateNavigationBarVisibility(display.getDisplayId(), false);
                z3 |= showPresentation(display);
            }
            return z3;
        }
        if (this.mPresentations.size() > 0) {
            z2 = true;
        }
        for (int size = this.mPresentations.size() - 1; size >= 0; size--) {
            updateNavigationBarVisibility(this.mPresentations.keyAt(size), true);
            ((Presentation) this.mPresentations.valueAt(size)).dismiss();
        }
        this.mPresentations.clear();
        return z2;
    }

    /* access modifiers changed from: private */
    public void updateNavigationBarVisibility(int i, boolean z) {
        if (i != 0) {
            NavigationBarView navigationBarView = this.mNavBarController.getNavigationBarView(i);
            if (navigationBarView != null) {
                if (z) {
                    navigationBarView.getRootView().setVisibility(0);
                } else {
                    navigationBarView.getRootView().setVisibility(8);
                }
            }
        }
    }
}
