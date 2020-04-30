package com.android.systemui.pip.phone;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.DisplayInfo;
import android.view.IPinnedStackController;
import android.view.WindowContainerTransaction;
import com.android.systemui.Dependency;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p010wm.DisplayChangeController.OnDisplayChangingListener;
import com.android.systemui.p010wm.DisplayController;
import com.android.systemui.pip.BasePipManager;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.pip.PipTaskOrganizer.PipTransitionCallback;
import com.android.systemui.shared.recents.IPinnedStackAnimationListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.PinnedStackListenerForwarder.PinnedStackListener;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.FloatingContentCoordinator;
import java.io.PrintWriter;

public class PipManager implements BasePipManager, PipTransitionCallback {
    /* access modifiers changed from: private */
    public IActivityManager mActivityManager;
    /* access modifiers changed from: private */
    public PipAppOpsListener mAppOpsListener;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    private InputConsumerController mInputConsumerController;
    /* access modifiers changed from: private */
    public PipMediaController mMediaController;
    /* access modifiers changed from: private */
    public PipMenuActivityController mMenuController;
    private IPinnedStackAnimationListener mPinnedStackAnimationRecentsListener;
    /* access modifiers changed from: private */
    public PipBoundsHandler mPipBoundsHandler;
    protected PipTaskOrganizer mPipTaskOrganizer;
    /* access modifiers changed from: private */
    public final Rect mReentryBounds = new Rect();
    private final OnDisplayChangingListener mRotationController = new OnDisplayChangingListener() {
        public final void onRotateDisplay(int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
            PipManager.this.lambda$new$0$PipManager(i, i2, i3, windowContainerTransaction);
        }
    };
    private final TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() {
        public void onActivityPinned(String str, int i, int i2, int i3) {
            PipManager.this.mTouchHandler.onActivityPinned();
            PipManager.this.mMediaController.onActivityPinned();
            PipManager.this.mMenuController.onActivityPinned();
            PipManager.this.mAppOpsListener.onActivityPinned(str);
            ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).execute($$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU.INSTANCE);
        }

        public void onActivityUnpinned() {
            ComponentName componentName = (ComponentName) PipUtils.getTopPipActivity(PipManager.this.mContext, PipManager.this.mActivityManager).first;
            PipManager.this.mMenuController.onActivityUnpinned();
            PipManager.this.mTouchHandler.onActivityUnpinned(componentName);
            PipManager.this.mAppOpsListener.onActivityUnpinned();
            ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).execute(new Runnable(componentName) {
                public final /* synthetic */ ComponentName f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    C09121.lambda$onActivityUnpinned$1(this.f$0);
                }
            });
        }

        static /* synthetic */ void lambda$onActivityUnpinned$1(ComponentName componentName) {
            WindowManagerWrapper.getInstance().setPipVisibility(componentName != null);
        }

        public void onActivityRestartAttempt(RunningTaskInfo runningTaskInfo, boolean z, boolean z2) {
            if (runningTaskInfo.configuration.windowConfiguration.getWindowingMode() == 2) {
                PipManager.this.mTouchHandler.getMotionHelper().expandPip(z2);
            }
        }
    };
    private final DisplayInfo mTmpDisplayInfo = new DisplayInfo();
    private final Rect mTmpInsetBounds = new Rect();
    private final Rect mTmpNormalBounds = new Rect();
    /* access modifiers changed from: private */
    public PipTouchHandler mTouchHandler;

    private class PipManagerPinnedStackListener extends PinnedStackListener {
        private PipManagerPinnedStackListener() {
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onListenerRegistered$0 */
        public /* synthetic */ void mo12164x41eed0e6(IPinnedStackController iPinnedStackController) {
            PipManager.this.mTouchHandler.setPinnedStackController(iPinnedStackController);
        }

        public void onListenerRegistered(IPinnedStackController iPinnedStackController) {
            PipManager.this.mHandler.post(new Runnable(iPinnedStackController) {
                public final /* synthetic */ IPinnedStackController f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12164x41eed0e6(this.f$1);
                }
            });
        }

        public void onImeVisibilityChanged(boolean z, int i) {
            PipManager.this.mHandler.post(new Runnable(z, i) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12163xe87294fa(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onImeVisibilityChanged$1 */
        public /* synthetic */ void mo12163xe87294fa(boolean z, int i) {
            PipManager.this.mPipBoundsHandler.onImeVisibilityChanged(z, i);
            PipManager.this.mTouchHandler.onImeVisibilityChanged(z, i);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onMovementBoundsChanged$2 */
        public /* synthetic */ void mo12165x453515e2(Rect rect, boolean z) {
            PipManager.this.updateMovementBounds(rect, z, false);
        }

        public void onMovementBoundsChanged(Rect rect, boolean z) {
            PipManager.this.mHandler.post(new Runnable(rect, z) {
                public final /* synthetic */ Rect f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12165x453515e2(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onActionsChanged$3 */
        public /* synthetic */ void mo12159x73f0fb02(ParceledListSlice parceledListSlice) {
            PipManager.this.mMenuController.setAppActions(parceledListSlice);
        }

        public void onActionsChanged(ParceledListSlice parceledListSlice) {
            PipManager.this.mHandler.post(new Runnable(parceledListSlice) {
                public final /* synthetic */ ParceledListSlice f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12159x73f0fb02(this.f$1);
                }
            });
        }

        public void onSaveReentryBounds(ComponentName componentName, Rect rect) {
            PipManager.this.mHandler.post(new Runnable(rect, componentName) {
                public final /* synthetic */ Rect f$1;
                public final /* synthetic */ ComponentName f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12167x5a1eb259(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSaveReentryBounds$4 */
        public /* synthetic */ void mo12167x5a1eb259(Rect rect, ComponentName componentName) {
            PipManager.this.mReentryBounds.set(PipManager.this.mTouchHandler.getNormalBounds());
            PipManager.this.mPipBoundsHandler.applySnapFraction(PipManager.this.mReentryBounds, PipManager.this.mPipBoundsHandler.getSnapFraction(rect));
            PipManager.this.mPipBoundsHandler.onSaveReentryBounds(componentName, PipManager.this.mReentryBounds);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onResetReentryBounds$5 */
        public /* synthetic */ void mo12166x867030d2(ComponentName componentName) {
            PipManager.this.mPipBoundsHandler.onResetReentryBounds(componentName);
        }

        public void onResetReentryBounds(ComponentName componentName) {
            PipManager.this.mHandler.post(new Runnable(componentName) {
                public final /* synthetic */ ComponentName f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12166x867030d2(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onDisplayInfoChanged$6 */
        public /* synthetic */ void mo12162x2c090532(DisplayInfo displayInfo) {
            PipManager.this.mPipBoundsHandler.onDisplayInfoChanged(displayInfo);
        }

        public void onDisplayInfoChanged(DisplayInfo displayInfo) {
            PipManager.this.mHandler.post(new Runnable(displayInfo) {
                public final /* synthetic */ DisplayInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12162x2c090532(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onConfigurationChanged$7 */
        public /* synthetic */ void mo12161x6b8d7837() {
            PipManager.this.mPipBoundsHandler.onConfigurationChanged();
        }

        public void onConfigurationChanged() {
            PipManager.this.mHandler.post(new Runnable() {
                public final void run() {
                    PipManagerPinnedStackListener.this.mo12161x6b8d7837();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onAspectRatioChanged$8 */
        public /* synthetic */ void mo12160xcbbfcab3(float f) {
            PipManager.this.mPipBoundsHandler.onAspectRatioChanged(f);
        }

        public void onAspectRatioChanged(float f) {
            PipManager.this.mHandler.post(new Runnable(f) {
                public final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12160xcbbfcab3(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PipManager(int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
        if (this.mPipBoundsHandler.onDisplayRotationChanged(this.mTmpNormalBounds, i, i2, i3, windowContainerTransaction)) {
            updateMovementBounds(this.mTmpNormalBounds, false, false);
        }
    }

    public PipManager(Context context, BroadcastDispatcher broadcastDispatcher, DisplayController displayController, FloatingContentCoordinator floatingContentCoordinator, DeviceConfigProxy deviceConfigProxy, PipBoundsHandler pipBoundsHandler, PipSnapAlgorithm pipSnapAlgorithm, PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        Context context2 = context;
        PipBoundsHandler pipBoundsHandler2 = pipBoundsHandler;
        this.mContext = context2;
        this.mActivityManager = ActivityManager.getService();
        try {
            WindowManagerWrapper.getInstance().addPinnedStackListener(new PipManagerPinnedStackListener());
        } catch (RemoteException e) {
            Log.e("PipManager", "Failed to register pinned stack listener", e);
        }
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        IActivityTaskManager service = ActivityTaskManager.getService();
        this.mPipBoundsHandler = pipBoundsHandler2;
        PipTaskOrganizer pipTaskOrganizer = new PipTaskOrganizer(context2, pipBoundsHandler2, pipSurfaceTransactionHelper);
        this.mPipTaskOrganizer = pipTaskOrganizer;
        pipTaskOrganizer.registerPipTransitionCallback(this);
        this.mInputConsumerController = InputConsumerController.getPipInputConsumer();
        PipMediaController pipMediaController = new PipMediaController(context2, this.mActivityManager, broadcastDispatcher);
        this.mMediaController = pipMediaController;
        PipMenuActivityController pipMenuActivityController = new PipMenuActivityController(context2, pipMediaController, this.mInputConsumerController);
        this.mMenuController = pipMenuActivityController;
        PipTouchHandler pipTouchHandler = new PipTouchHandler(context, this.mActivityManager, service, pipMenuActivityController, this.mInputConsumerController, this.mPipBoundsHandler, this.mPipTaskOrganizer, floatingContentCoordinator, deviceConfigProxy, pipSnapAlgorithm);
        this.mTouchHandler = pipTouchHandler;
        this.mAppOpsListener = new PipAppOpsListener(context2, this.mActivityManager, this.mTouchHandler.getMotionHelper());
        displayController.addDisplayChangingController(this.mRotationController);
        DisplayInfo displayInfo = new DisplayInfo();
        context.getDisplay().getDisplayInfo(displayInfo);
        this.mPipBoundsHandler.onDisplayInfoChanged(displayInfo);
        try {
            ActivityTaskManager.getTaskOrganizerController().registerTaskOrganizer(this.mPipTaskOrganizer, 2);
            if (service.getStackInfo(2, 0) != null) {
                this.mInputConsumerController.registerInputConsumer();
            }
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        this.mTouchHandler.onConfigurationChanged();
    }

    public void showPictureInPictureMenu() {
        this.mTouchHandler.showPictureInPictureMenu();
    }

    public void setShelfHeight(boolean z, int i) {
        this.mHandler.post(new Runnable(z, i) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PipManager.this.lambda$setShelfHeight$1$PipManager(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setShelfHeight$1 */
    public /* synthetic */ void lambda$setShelfHeight$1$PipManager(boolean z, int i) {
        if (this.mPipBoundsHandler.setShelfHeight(z, i)) {
            this.mTouchHandler.onShelfVisibilityChanged(z, i);
            updateMovementBounds(this.mPipBoundsHandler.getLastDestinationBounds(), false, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setPinnedStackAnimationType$2 */
    public /* synthetic */ void lambda$setPinnedStackAnimationType$2$PipManager(int i) {
        this.mPipTaskOrganizer.setOneShotAnimationType(i);
    }

    public void setPinnedStackAnimationType(int i) {
        this.mHandler.post(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PipManager.this.lambda$setPinnedStackAnimationType$2$PipManager(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setPinnedStackAnimationListener$3 */
    public /* synthetic */ void lambda$setPinnedStackAnimationListener$3$PipManager(IPinnedStackAnimationListener iPinnedStackAnimationListener) {
        this.mPinnedStackAnimationRecentsListener = iPinnedStackAnimationListener;
    }

    public void setPinnedStackAnimationListener(IPinnedStackAnimationListener iPinnedStackAnimationListener) {
        this.mHandler.post(new Runnable(iPinnedStackAnimationListener) {
            public final /* synthetic */ IPinnedStackAnimationListener f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PipManager.this.lambda$setPinnedStackAnimationListener$3$PipManager(this.f$1);
            }
        });
    }

    public void onPipTransitionStarted() {
        this.mTouchHandler.setTouchEnabled(false);
        IPinnedStackAnimationListener iPinnedStackAnimationListener = this.mPinnedStackAnimationRecentsListener;
        if (iPinnedStackAnimationListener != null) {
            try {
                iPinnedStackAnimationListener.onPinnedStackAnimationStarted();
            } catch (RemoteException e) {
                Log.e("PipManager", "Failed to callback recents", e);
            }
        }
    }

    public void onPipTransitionFinished() {
        onPipTransitionFinishedOrCanceled();
    }

    public void onPipTransitionCanceled() {
        onPipTransitionFinishedOrCanceled();
    }

    private void onPipTransitionFinishedOrCanceled() {
        this.mTouchHandler.setTouchEnabled(true);
        this.mTouchHandler.onPinnedStackAnimationEnded();
        this.mMenuController.onPinnedStackAnimationEnded();
    }

    /* access modifiers changed from: private */
    public void updateMovementBounds(Rect rect, boolean z, boolean z2) {
        this.mPipBoundsHandler.onMovementBoundsChanged(this.mTmpInsetBounds, this.mTmpNormalBounds, rect, this.mTmpDisplayInfo);
        this.mTouchHandler.onMovementBoundsChanged(this.mTmpInsetBounds, this.mTmpNormalBounds, rect, z, z2, this.mTmpDisplayInfo.rotation);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("PipManager");
        String str = "  ";
        this.mInputConsumerController.dump(printWriter, str);
        this.mMenuController.dump(printWriter, str);
        this.mTouchHandler.dump(printWriter, str);
        this.mPipBoundsHandler.dump(printWriter, str);
    }
}
