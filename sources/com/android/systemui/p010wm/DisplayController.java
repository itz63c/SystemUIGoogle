package com.android.systemui.p010wm;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.IDisplayWindowListener;
import android.view.IDisplayWindowListener.Stub;
import android.view.IWindowManager;
import com.android.systemui.p010wm.DisplayChangeController.OnDisplayChangingListener;
import java.util.ArrayList;

/* renamed from: com.android.systemui.wm.DisplayController */
public class DisplayController {
    private final DisplayChangeController mChangeController;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final ArrayList<OnDisplaysChangedListener> mDisplayChangedListeners = new ArrayList<>();
    private final IDisplayWindowListener mDisplayContainerListener = new Stub() {
        public void onDisplayAdded(int i) {
            DisplayController.this.mHandler.post(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    C17921.this.lambda$onDisplayAdded$0$DisplayController$1(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onDisplayAdded$0 */
        public /* synthetic */ void lambda$onDisplayAdded$0$DisplayController$1(int i) {
            Context context;
            synchronized (DisplayController.this.mDisplays) {
                if (DisplayController.this.mDisplays.get(i) == null) {
                    Display display = DisplayController.this.getDisplay(i);
                    if (display != null) {
                        DisplayRecord displayRecord = new DisplayRecord();
                        if (i == 0) {
                            context = DisplayController.this.mContext;
                        } else {
                            context = DisplayController.this.mContext.createDisplayContext(display);
                        }
                        displayRecord.mContext = context;
                        displayRecord.mDisplayLayout = new DisplayLayout(context, display);
                        DisplayController.this.mDisplays.put(i, displayRecord);
                        for (int i2 = 0; i2 < DisplayController.this.mDisplayChangedListeners.size(); i2++) {
                            ((OnDisplaysChangedListener) DisplayController.this.mDisplayChangedListeners.get(i2)).onDisplayAdded(i);
                        }
                    }
                }
            }
        }

        public void onDisplayConfigurationChanged(int i, Configuration configuration) {
            DisplayController.this.mHandler.post(new Runnable(i, configuration) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ Configuration f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    C17921.this.lambda$onDisplayConfigurationChanged$1$DisplayController$1(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onDisplayConfigurationChanged$1 */
        public /* synthetic */ void lambda$onDisplayConfigurationChanged$1$DisplayController$1(int i, Configuration configuration) {
            synchronized (DisplayController.this.mDisplays) {
                DisplayRecord displayRecord = (DisplayRecord) DisplayController.this.mDisplays.get(i);
                if (displayRecord == null) {
                    Slog.w("DisplayController", "Skipping Display Configuration change on non-added display.");
                    return;
                }
                Display display = DisplayController.this.getDisplay(i);
                if (display == null) {
                    Slog.w("DisplayController", "Skipping Display Configuration change on invalid display. It may have been removed.");
                    return;
                }
                Context access$300 = DisplayController.this.mContext;
                if (i != 0) {
                    access$300 = DisplayController.this.mContext.createDisplayContext(display);
                }
                Context createConfigurationContext = access$300.createConfigurationContext(configuration);
                displayRecord.mContext = createConfigurationContext;
                displayRecord.mDisplayLayout = new DisplayLayout(createConfigurationContext, display);
                for (int i2 = 0; i2 < DisplayController.this.mDisplayChangedListeners.size(); i2++) {
                    ((OnDisplaysChangedListener) DisplayController.this.mDisplayChangedListeners.get(i2)).onDisplayConfigurationChanged(i, configuration);
                }
            }
        }

        public void onDisplayRemoved(int i) {
            DisplayController.this.mHandler.post(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    C17921.this.lambda$onDisplayRemoved$2$DisplayController$1(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onDisplayRemoved$2 */
        public /* synthetic */ void lambda$onDisplayRemoved$2$DisplayController$1(int i) {
            synchronized (DisplayController.this.mDisplays) {
                if (DisplayController.this.mDisplays.get(i) != null) {
                    for (int size = DisplayController.this.mDisplayChangedListeners.size() - 1; size >= 0; size--) {
                        ((OnDisplaysChangedListener) DisplayController.this.mDisplayChangedListeners.get(size)).onDisplayRemoved(i);
                    }
                    DisplayController.this.mDisplays.remove(i);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final SparseArray<DisplayRecord> mDisplays = new SparseArray<>();
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private final IWindowManager mWmService;

    /* renamed from: com.android.systemui.wm.DisplayController$DisplayRecord */
    private static class DisplayRecord {
        Context mContext;
        DisplayLayout mDisplayLayout;

        private DisplayRecord() {
        }
    }

    /* renamed from: com.android.systemui.wm.DisplayController$OnDisplaysChangedListener */
    public interface OnDisplaysChangedListener {
        void onDisplayAdded(int i) {
        }

        void onDisplayConfigurationChanged(int i, Configuration configuration) {
        }

        void onDisplayRemoved(int i) {
        }
    }

    public Display getDisplay(int i) {
        return ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(i);
    }

    public DisplayController(Context context, Handler handler, IWindowManager iWindowManager) {
        this.mHandler = handler;
        this.mContext = context;
        this.mWmService = iWindowManager;
        this.mChangeController = new DisplayChangeController(handler, iWindowManager);
        try {
            this.mWmService.registerDisplayWindowListener(this.mDisplayContainerListener);
        } catch (RemoteException unused) {
            throw new RuntimeException("Unable to register hierarchy listener");
        }
    }

    public DisplayLayout getDisplayLayout(int i) {
        DisplayRecord displayRecord = (DisplayRecord) this.mDisplays.get(i);
        if (displayRecord != null) {
            return displayRecord.mDisplayLayout;
        }
        return null;
    }

    public Context getDisplayContext(int i) {
        DisplayRecord displayRecord = (DisplayRecord) this.mDisplays.get(i);
        if (displayRecord != null) {
            return displayRecord.mContext;
        }
        return null;
    }

    public void addDisplayWindowListener(OnDisplaysChangedListener onDisplaysChangedListener) {
        synchronized (this.mDisplays) {
            if (!this.mDisplayChangedListeners.contains(onDisplaysChangedListener)) {
                this.mDisplayChangedListeners.add(onDisplaysChangedListener);
                for (int i = 0; i < this.mDisplays.size(); i++) {
                    onDisplaysChangedListener.onDisplayAdded(this.mDisplays.keyAt(i));
                }
            }
        }
    }

    public void addDisplayChangingController(OnDisplayChangingListener onDisplayChangingListener) {
        this.mChangeController.addRotationListener(onDisplayChangingListener);
    }
}
