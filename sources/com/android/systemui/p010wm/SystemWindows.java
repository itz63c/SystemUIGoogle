package com.android.systemui.p010wm;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.MergedConfiguration;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayCutout.ParcelableWrapper;
import android.view.DragEvent;
import android.view.IWindow;
import android.view.IWindow.Stub;
import android.view.IWindowManager;
import android.view.IWindowSessionCallback;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.WindowlessWindowManager;
import com.android.internal.os.IResultReceiver;
import com.android.systemui.p010wm.DisplayController.OnDisplaysChangedListener;
import java.util.HashMap;

/* renamed from: com.android.systemui.wm.SystemWindows */
public class SystemWindows {
    Context mContext;
    DisplayController mDisplayController;
    private final OnDisplaysChangedListener mDisplayListener;
    /* access modifiers changed from: private */
    public final SparseArray<PerDisplay> mPerDisplay = new SparseArray<>();
    final HashMap<View, SurfaceControlViewHost> mViewRoots = new HashMap<>();
    IWindowManager mWmService;

    /* renamed from: com.android.systemui.wm.SystemWindows$ContainerWindow */
    class ContainerWindow extends Stub {
        public void closeSystemDialogs(String str) {
        }

        public void dispatchAppVisibility(boolean z) {
        }

        public void dispatchDragEvent(DragEvent dragEvent) {
        }

        public void dispatchGetNewSurface() {
        }

        public void dispatchPointerCaptureChanged(boolean z) {
        }

        public void dispatchSystemUiVisibilityChanged(int i, int i2, int i3, int i4) {
        }

        public void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
        }

        public void dispatchWallpaperOffsets(float f, float f2, float f3, float f4, float f5, boolean z) {
        }

        public void dispatchWindowShown() {
        }

        public void executeCommand(String str, String str2, ParcelFileDescriptor parcelFileDescriptor) {
        }

        public void hideInsets(int i, boolean z) {
        }

        public void insetsChanged(InsetsState insetsState) {
        }

        public void insetsControlChanged(InsetsState insetsState, InsetsSourceControl[] insetsSourceControlArr) {
        }

        public void locationInParentDisplayChanged(Point point) {
        }

        public void moved(int i, int i2) {
        }

        public void requestAppKeyboardShortcuts(IResultReceiver iResultReceiver, int i) {
        }

        public void resized(Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z, MergedConfiguration mergedConfiguration, Rect rect5, boolean z2, boolean z3, int i, ParcelableWrapper parcelableWrapper) {
        }

        public void showInsets(int i, boolean z) {
        }

        public void updatePointerIcon(float f, float f2) {
        }

        public void windowFocusChanged(boolean z, boolean z2) {
        }

        ContainerWindow(SystemWindows systemWindows) {
        }
    }

    /* renamed from: com.android.systemui.wm.SystemWindows$PerDisplay */
    private class PerDisplay {
        final int mDisplayId;
        /* access modifiers changed from: private */
        public final SparseArray<SysUiWindowManager> mWwms = new SparseArray<>();

        PerDisplay(int i) {
            this.mDisplayId = i;
        }

        public void addView(View view, LayoutParams layoutParams, int i) {
            SysUiWindowManager addRoot = addRoot(i);
            if (addRoot == null) {
                Slog.e("SystemWindows", "Unable to create systemui root");
                return;
            }
            SurfaceControlViewHost surfaceControlViewHost = new SurfaceControlViewHost(SystemWindows.this.mContext, SystemWindows.this.mDisplayController.getDisplay(this.mDisplayId), addRoot);
            layoutParams.flags |= 16777216;
            surfaceControlViewHost.setView(view, layoutParams);
            SystemWindows.this.mViewRoots.put(view, surfaceControlViewHost);
        }

        /* access modifiers changed from: 0000 */
        public SysUiWindowManager addRoot(int i) {
            SurfaceControl surfaceControl;
            SysUiWindowManager sysUiWindowManager = (SysUiWindowManager) this.mWwms.get(i);
            if (sysUiWindowManager != null) {
                return sysUiWindowManager;
            }
            ContainerWindow containerWindow = new ContainerWindow(SystemWindows.this);
            try {
                surfaceControl = SystemWindows.this.mWmService.addShellRoot(this.mDisplayId, containerWindow, i);
            } catch (RemoteException unused) {
                surfaceControl = null;
            }
            if (surfaceControl == null) {
                Slog.e("SystemWindows", "Unable to get root surfacecontrol for systemui");
                return null;
            }
            SysUiWindowManager sysUiWindowManager2 = new SysUiWindowManager(this.mDisplayId, SystemWindows.this.mDisplayController.getDisplayContext(this.mDisplayId), surfaceControl, containerWindow);
            this.mWwms.put(i, sysUiWindowManager2);
            return sysUiWindowManager2;
        }

        /* access modifiers changed from: 0000 */
        public void updateConfiguration(Configuration configuration) {
            for (int i = 0; i < this.mWwms.size(); i++) {
                ((SysUiWindowManager) this.mWwms.valueAt(i)).updateConfiguration(configuration);
            }
        }
    }

    /* renamed from: com.android.systemui.wm.SystemWindows$SysUiWindowManager */
    public class SysUiWindowManager extends WindowlessWindowManager {
        final int mDisplayId;

        public SysUiWindowManager(int i, Context context, SurfaceControl surfaceControl, ContainerWindow containerWindow) {
            super(context.getResources().getConfiguration(), surfaceControl, null);
            this.mDisplayId = i;
        }

        public int relayout(IWindow iWindow, int i, LayoutParams layoutParams, int i2, int i3, int i4, int i5, long j, Rect rect, Rect rect2, Rect rect3, Rect rect4, Rect rect5, ParcelableWrapper parcelableWrapper, MergedConfiguration mergedConfiguration, SurfaceControl surfaceControl, InsetsState insetsState, Point point, SurfaceControl surfaceControl2) {
            int relayout = SystemWindows.super.relayout(iWindow, i, layoutParams, i2, i3, i4, i5, j, rect, rect2, rect3, rect4, rect5, parcelableWrapper, mergedConfiguration, surfaceControl, insetsState, point, surfaceControl2);
            if (relayout != 0) {
                return relayout;
            }
            rect5.set(SystemWindows.this.mDisplayController.getDisplayLayout(this.mDisplayId).stableInsets());
            return 0;
        }

        /* access modifiers changed from: 0000 */
        public void updateConfiguration(Configuration configuration) {
            setConfiguration(configuration);
        }

        /* access modifiers changed from: 0000 */
        public SurfaceControl getSurfaceControlForWindow(View view) {
            return getSurfaceControl(view);
        }
    }

    public SystemWindows(Context context, DisplayController displayController, IWindowManager iWindowManager) {
        C17941 r0 = new OnDisplaysChangedListener() {
            public void onDisplayAdded(int i) {
            }

            public void onDisplayRemoved(int i) {
            }

            public void onDisplayConfigurationChanged(int i, Configuration configuration) {
                PerDisplay perDisplay = (PerDisplay) SystemWindows.this.mPerDisplay.get(i);
                if (perDisplay != null) {
                    perDisplay.updateConfiguration(configuration);
                }
            }
        };
        this.mDisplayListener = r0;
        this.mContext = context;
        this.mWmService = iWindowManager;
        this.mDisplayController = displayController;
        displayController.addDisplayWindowListener(r0);
        try {
            iWindowManager.openSession(new IWindowSessionCallback.Stub(this) {
                public void onAnimatorScaleChanged(float f) {
                }
            });
        } catch (RemoteException e) {
            Slog.e("SystemWindows", "Unable to create layer", e);
        }
    }

    public void addView(View view, LayoutParams layoutParams, int i, int i2) {
        PerDisplay perDisplay = (PerDisplay) this.mPerDisplay.get(i);
        if (perDisplay == null) {
            perDisplay = new PerDisplay(i);
            this.mPerDisplay.put(i, perDisplay);
        }
        perDisplay.addView(view, layoutParams, i2);
    }

    public void removeView(View view) {
        ((SurfaceControlViewHost) this.mViewRoots.remove(view)).die();
    }

    public void updateViewLayout(View view, ViewGroup.LayoutParams layoutParams) {
        SurfaceControlViewHost surfaceControlViewHost = (SurfaceControlViewHost) this.mViewRoots.get(view);
        if (surfaceControlViewHost != null && (layoutParams instanceof LayoutParams)) {
            view.setLayoutParams(layoutParams);
            surfaceControlViewHost.relayout((LayoutParams) layoutParams);
        }
    }

    public SurfaceControl getViewSurface(View view) {
        for (int i = 0; i < this.mPerDisplay.size(); i++) {
            for (int i2 = 0; i2 < ((PerDisplay) this.mPerDisplay.valueAt(i)).mWwms.size(); i2++) {
                SurfaceControl surfaceControlForWindow = ((SysUiWindowManager) ((PerDisplay) this.mPerDisplay.valueAt(i)).mWwms.get(i2)).getSurfaceControlForWindow(view);
                if (surfaceControlForWindow != null) {
                    return surfaceControlForWindow;
                }
            }
        }
        return null;
    }
}
