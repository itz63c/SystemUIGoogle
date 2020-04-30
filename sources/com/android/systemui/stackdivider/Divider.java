package com.android.systemui.stackdivider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings.Global;
import android.util.Slog;
import android.view.IWindowContainer;
import android.view.LayoutInflater;
import android.view.SurfaceControl.Transaction;
import android.view.SurfaceSession;
import android.view.WindowContainerTransaction;
import com.android.systemui.C2013R$layout;
import com.android.systemui.SystemUI;
import com.android.systemui.TransactionPool;
import com.android.systemui.p010wm.DisplayChangeController.OnDisplayChangingListener;
import com.android.systemui.p010wm.DisplayController;
import com.android.systemui.p010wm.DisplayController.OnDisplaysChangedListener;
import com.android.systemui.p010wm.DisplayImeController;
import com.android.systemui.p010wm.DisplayImeController.ImePositionProcessor;
import com.android.systemui.p010wm.DisplayLayout;
import com.android.systemui.p010wm.SystemWindows;
import com.android.systemui.recents.Recents;
import com.android.systemui.stackdivider.DividerView.DividerCallbacks;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Divider extends SystemUI implements DividerCallbacks, OnDisplaysChangedListener {
    private boolean mAdjustedForIme = false;
    private DisplayController mDisplayController;
    private final DividerState mDividerState = new DividerState();
    private final ArrayList<WeakReference<Consumer<Boolean>>> mDockedStackExistsListeners = new ArrayList<>();
    private ForcedResizableInfoActivityController mForcedResizableController;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private boolean mHomeStackResizable = false;
    private DisplayImeController mImeController;
    private final DividerImeController mImePositionProcessor = new DividerImeController();
    /* access modifiers changed from: private */
    public KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public boolean mMinimized = false;
    private final Optional<Lazy<Recents>> mRecentsOptionalLazy;
    private SplitDisplayLayout mRotateSplitLayout;
    private OnDisplayChangingListener mRotationController = new OnDisplayChangingListener() {
        public final void onRotateDisplay(int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
            Divider.this.lambda$new$0$Divider(i, i2, i3, windowContainerTransaction);
        }
    };
    /* access modifiers changed from: private */
    public SplitDisplayLayout mSplitLayout;
    /* access modifiers changed from: private */
    public SplitScreenTaskOrganizer mSplits = new SplitScreenTaskOrganizer(this);
    final SurfaceSession mSurfaceSession = new SurfaceSession();
    private SystemWindows mSystemWindows;
    final TransactionPool mTransactionPool;
    /* access modifiers changed from: private */
    public DividerView mView;
    private boolean mVisible = false;
    private DividerWindowManager mWindowManager;

    private class DividerImeController implements ImePositionProcessor {
        private boolean mAdjusted;
        /* access modifiers changed from: private */
        public ValueAnimator mAnimation;
        private int mHiddenTop;
        private boolean mImeWasShown;
        private int mLastAdjustTop;
        private float mLastPrimaryDim;
        private float mLastSecondaryDim;
        private boolean mPaused;
        private boolean mPausedTargetAdjusted;
        private boolean mSecondaryHasFocus;
        private int mShownTop;
        private boolean mTargetAdjusted;
        private float mTargetPrimaryDim;
        private float mTargetSecondaryDim;
        private boolean mTargetShown;

        private DividerImeController() {
            this.mHiddenTop = 0;
            this.mShownTop = 0;
            this.mTargetAdjusted = false;
            this.mTargetShown = false;
            this.mTargetPrimaryDim = 0.0f;
            this.mTargetSecondaryDim = 0.0f;
            this.mSecondaryHasFocus = false;
            this.mLastPrimaryDim = 0.0f;
            this.mLastSecondaryDim = 0.0f;
            this.mLastAdjustTop = -1;
            this.mImeWasShown = false;
            this.mAdjusted = false;
            this.mAnimation = null;
            this.mPaused = true;
            this.mPausedTargetAdjusted = false;
        }

        private boolean getSecondaryHasFocus(int i) {
            boolean z = false;
            try {
                IWindowContainer imeTarget = ActivityTaskManager.getTaskOrganizerController().getImeTarget(i);
                if (imeTarget != null && imeTarget.asBinder() == Divider.this.mSplits.mSecondary.token.asBinder()) {
                    z = true;
                }
                return z;
            } catch (RemoteException e) {
                Slog.w("Divider", "Failed to get IME target", e);
                return false;
            }
        }

        private void updateDimTargets() {
            boolean z = !Divider.this.mView.isHidden();
            float f = 0.3f;
            this.mTargetPrimaryDim = (!this.mSecondaryHasFocus || !this.mTargetShown || !z) ? 0.0f : 0.3f;
            if (this.mSecondaryHasFocus || !this.mTargetShown || !z) {
                f = 0.0f;
            }
            this.mTargetSecondaryDim = f;
        }

        public void onImeStartPositioning(int i, int i2, int i3, boolean z, Transaction transaction) {
            if (Divider.this.inSplitMode()) {
                boolean z2 = true;
                boolean z3 = !Divider.this.mView.isHidden();
                boolean secondaryHasFocus = getSecondaryHasFocus(i);
                this.mSecondaryHasFocus = secondaryHasFocus;
                if (!z3 || !z || !secondaryHasFocus || Divider.this.mSplitLayout.mDisplayLayout.isLandscape()) {
                    z2 = false;
                }
                this.mHiddenTop = i2;
                this.mShownTop = i3;
                this.mTargetShown = z;
                int i4 = this.mLastAdjustTop;
                if (i4 < 0) {
                    if (!z) {
                        i2 = i3;
                    }
                    this.mLastAdjustTop = i2;
                } else if (this.mTargetAdjusted != z2 && z2 == this.mAdjusted) {
                    if (z) {
                        i2 = i3;
                    }
                    if (i4 != i2) {
                        this.mAdjusted = this.mTargetAdjusted;
                    }
                }
                if (this.mPaused) {
                    this.mPausedTargetAdjusted = z2;
                    return;
                }
                this.mTargetAdjusted = z2;
                updateDimTargets();
                if (this.mAnimation != null || (this.mImeWasShown && z && this.mTargetAdjusted != this.mAdjusted)) {
                    startAsyncAnimation();
                }
                if (z3) {
                    updateImeAdjustState();
                }
            }
        }

        private void updateImeAdjustState() {
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            if (this.mTargetAdjusted) {
                SplitDisplayLayout access$200 = Divider.this.mSplitLayout;
                int i = this.mShownTop;
                access$200.updateAdjustedBounds(i, this.mHiddenTop, i);
                windowContainerTransaction.setBounds(Divider.this.mSplits.mSecondary.token, Divider.this.mSplitLayout.mAdjustedSecondary);
                Rect rect = new Rect(Divider.this.mSplits.mSecondary.configuration.windowConfiguration.getAppBounds());
                rect.offset(0, Divider.this.mSplitLayout.mAdjustedSecondary.top - Divider.this.mSplitLayout.mSecondary.top);
                windowContainerTransaction.setAppBounds(Divider.this.mSplits.mSecondary.token, rect);
                windowContainerTransaction.setScreenSizeDp(Divider.this.mSplits.mSecondary.token, Divider.this.mSplits.mSecondary.configuration.screenWidthDp, Divider.this.mSplits.mSecondary.configuration.screenHeightDp);
            } else {
                windowContainerTransaction.setBounds(Divider.this.mSplits.mSecondary.token, Divider.this.mSplitLayout.mSecondary);
                windowContainerTransaction.setAppBounds(Divider.this.mSplits.mSecondary.token, null);
                windowContainerTransaction.setScreenSizeDp(Divider.this.mSplits.mSecondary.token, 0, 0);
            }
            try {
                ActivityTaskManager.getTaskOrganizerController().applyContainerTransaction(windowContainerTransaction, null);
            } catch (RemoteException unused) {
            }
            DividerView access$100 = Divider.this.mView;
            boolean z = this.mTargetShown;
            access$100.setAdjustedForIme(z, z ? 275 : 340);
            Divider.this.setAdjustedForIme(this.mTargetShown);
        }

        public void onImePositionChanged(int i, int i2, Transaction transaction) {
            if (this.mAnimation == null && Divider.this.inSplitMode() && !this.mPaused) {
                float f = (float) i2;
                int i3 = this.mHiddenTop;
                float f2 = (f - ((float) i3)) / ((float) (this.mShownTop - i3));
                if (!this.mTargetShown) {
                    f2 = 1.0f - f2;
                }
                onProgress(f2, transaction);
            }
        }

        public void onImeEndPositioning(int i, boolean z, Transaction transaction) {
            if (this.mAnimation == null && Divider.this.inSplitMode() && !this.mPaused) {
                onEnd(z, transaction);
            }
        }

        private void onProgress(float f, Transaction transaction) {
            boolean z = this.mTargetAdjusted;
            if (z != this.mAdjusted && !this.mPaused) {
                float f2 = z ? f : 1.0f - f;
                this.mLastAdjustTop = (int) ((((float) this.mShownTop) * f2) + ((1.0f - f2) * ((float) this.mHiddenTop)));
                Divider.this.mSplitLayout.updateAdjustedBounds(this.mLastAdjustTop, this.mHiddenTop, this.mShownTop);
                Divider.this.mView.resizeSplitSurfaces(transaction, Divider.this.mSplitLayout.mAdjustedPrimary, Divider.this.mSplitLayout.mAdjustedSecondary);
            }
            float f3 = 1.0f - f;
            Divider.this.mView.setResizeDimLayer(transaction, true, (this.mLastPrimaryDim * f3) + (this.mTargetPrimaryDim * f));
            Divider.this.mView.setResizeDimLayer(transaction, false, (this.mLastSecondaryDim * f3) + (f * this.mTargetSecondaryDim));
        }

        /* access modifiers changed from: private */
        public void onEnd(boolean z, Transaction transaction) {
            if (!z) {
                onProgress(1.0f, transaction);
                boolean z2 = this.mTargetAdjusted;
                this.mAdjusted = z2;
                this.mImeWasShown = this.mTargetShown;
                this.mLastAdjustTop = z2 ? this.mShownTop : this.mHiddenTop;
                this.mLastPrimaryDim = this.mTargetPrimaryDim;
                this.mLastSecondaryDim = this.mTargetSecondaryDim;
            }
        }

        private void startAsyncAnimation() {
            ValueAnimator valueAnimator = this.mAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.mAnimation = ofFloat;
            ofFloat.setDuration(275);
            boolean z = this.mTargetAdjusted;
            if (z != this.mAdjusted) {
                float f = (float) this.mLastAdjustTop;
                int i = this.mHiddenTop;
                float f2 = (f - ((float) i)) / ((float) (this.mShownTop - i));
                if (!z) {
                    f2 = 1.0f - f2;
                }
                this.mAnimation.setCurrentFraction(f2);
            }
            this.mAnimation.addUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DividerImeController.this.lambda$startAsyncAnimation$0$Divider$DividerImeController(valueAnimator);
                }
            });
            this.mAnimation.setInterpolator(DisplayImeController.INTERPOLATOR);
            this.mAnimation.addListener(new AnimatorListenerAdapter() {
                private boolean mCancel = false;

                public void onAnimationCancel(Animator animator) {
                    this.mCancel = true;
                }

                public void onAnimationEnd(Animator animator) {
                    Transaction acquire = Divider.this.mTransactionPool.acquire();
                    DividerImeController.this.onEnd(this.mCancel, acquire);
                    acquire.apply();
                    Divider.this.mTransactionPool.release(acquire);
                    DividerImeController.this.mAnimation = null;
                }
            });
            this.mAnimation.start();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$startAsyncAnimation$0 */
        public /* synthetic */ void lambda$startAsyncAnimation$0$Divider$DividerImeController(ValueAnimator valueAnimator) {
            Transaction acquire = Divider.this.mTransactionPool.acquire();
            onProgress(((Float) valueAnimator.getAnimatedValue()).floatValue(), acquire);
            acquire.apply();
            Divider.this.mTransactionPool.release(acquire);
        }

        public void pause(int i) {
            Divider.this.mHandler.post(new Runnable() {
                public final void run() {
                    DividerImeController.this.lambda$pause$1$Divider$DividerImeController();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$pause$1 */
        public /* synthetic */ void lambda$pause$1$Divider$DividerImeController() {
            if (!this.mPaused) {
                this.mPaused = true;
                this.mPausedTargetAdjusted = this.mTargetAdjusted;
                this.mTargetAdjusted = false;
                this.mTargetSecondaryDim = 0.0f;
                this.mTargetPrimaryDim = 0.0f;
                updateImeAdjustState();
                startAsyncAnimation();
            }
        }

        public void resume(int i) {
            Divider.this.mHandler.post(new Runnable() {
                public final void run() {
                    DividerImeController.this.lambda$resume$2$Divider$DividerImeController();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$resume$2 */
        public /* synthetic */ void lambda$resume$2$Divider$DividerImeController() {
            if (this.mPaused) {
                this.mPaused = false;
                this.mTargetAdjusted = this.mPausedTargetAdjusted;
                updateDimTargets();
                if (!(this.mTargetAdjusted == this.mAdjusted || Divider.this.mMinimized || Divider.this.mView == null)) {
                    Divider.this.mView.finishAnimations();
                }
                updateImeAdjustState();
                startAsyncAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$Divider(int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
        int i4;
        SplitDisplayLayout splitDisplayLayout = new SplitDisplayLayout(this.mContext, new DisplayLayout(this.mDisplayController.getDisplayLayout(i)), this.mSplits);
        splitDisplayLayout.rotateTo(i3);
        this.mRotateSplitLayout = splitDisplayLayout;
        if (this.mMinimized) {
            i4 = this.mView.mSnapTargetBeforeMinimized.position;
        } else {
            i4 = this.mView.getCurrentPosition();
        }
        splitDisplayLayout.resizeSplits(splitDisplayLayout.getSnapAlgorithm().calculateNonDismissingSnapTarget(i4).position, windowContainerTransaction);
        if (inSplitMode()) {
            WindowManagerProxy.applyHomeTasksMinimized(splitDisplayLayout, this.mSplits.mSecondary.token, windowContainerTransaction);
        }
    }

    public Divider(Context context, Optional<Lazy<Recents>> optional, DisplayController displayController, SystemWindows systemWindows, DisplayImeController displayImeController, Handler handler, KeyguardStateController keyguardStateController, TransactionPool transactionPool) {
        super(context);
        this.mDisplayController = displayController;
        this.mSystemWindows = systemWindows;
        this.mImeController = displayImeController;
        this.mHandler = handler;
        this.mKeyguardStateController = keyguardStateController;
        this.mRecentsOptionalLazy = optional;
        this.mForcedResizableController = new ForcedResizableInfoActivityController(context, this);
        this.mTransactionPool = transactionPool;
    }

    public void start() {
        this.mWindowManager = new DividerWindowManager(this.mSystemWindows);
        this.mDisplayController.addDisplayWindowListener(this);
        this.mKeyguardStateController.addCallback(new Callback() {
            public void onKeyguardFadingAwayChanged() {
            }

            public void onUnlockedChanged() {
            }

            public void onKeyguardShowingChanged() {
                if (Divider.this.inSplitMode() && Divider.this.mView != null) {
                    Divider.this.mView.setHidden(Divider.this.mKeyguardStateController.isShowing());
                }
            }
        });
    }

    public void onDisplayAdded(int i) {
        if (i == 0) {
            this.mSplitLayout = new SplitDisplayLayout(this.mDisplayController.getDisplayContext(i), this.mDisplayController.getDisplayLayout(i), this.mSplits);
            this.mImeController.addPositionProcessor(this.mImePositionProcessor);
            this.mDisplayController.addDisplayChangingController(this.mRotationController);
            try {
                this.mSplits.init(ActivityTaskManager.getTaskOrganizerController(), this.mSurfaceSession);
                WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
                this.mSplitLayout.resizeSplits(this.mSplitLayout.getSnapAlgorithm().getMiddleTarget().position, windowContainerTransaction);
                ActivityTaskManager.getTaskOrganizerController().applyContainerTransaction(windowContainerTransaction, null);
            } catch (Exception e) {
                Slog.e("Divider", "Failed to register docked stack listener", e);
            }
            update(this.mDisplayController.getDisplayContext(i).getResources().getConfiguration());
        }
    }

    public void onDisplayConfigurationChanged(int i, Configuration configuration) {
        if (i == 0) {
            SplitDisplayLayout splitDisplayLayout = new SplitDisplayLayout(this.mDisplayController.getDisplayContext(i), this.mDisplayController.getDisplayLayout(i), this.mSplits);
            this.mSplitLayout = splitDisplayLayout;
            SplitDisplayLayout splitDisplayLayout2 = this.mRotateSplitLayout;
            if (splitDisplayLayout2 == null) {
                int i2 = splitDisplayLayout.getSnapAlgorithm().getMiddleTarget().position;
                WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
                this.mSplitLayout.resizeSplits(i2, windowContainerTransaction);
                try {
                    ActivityTaskManager.getTaskOrganizerController().applyContainerTransaction(windowContainerTransaction, null);
                } catch (RemoteException unused) {
                }
            } else if (splitDisplayLayout2 != null && splitDisplayLayout.mDisplayLayout.rotation() == this.mRotateSplitLayout.mDisplayLayout.rotation()) {
                this.mSplitLayout.mPrimary = new Rect(this.mRotateSplitLayout.mPrimary);
                this.mSplitLayout.mSecondary = new Rect(this.mRotateSplitLayout.mSecondary);
                this.mRotateSplitLayout = null;
            }
            update(configuration);
        }
    }

    /* access modifiers changed from: 0000 */
    public Handler getHandler() {
        return this.mHandler;
    }

    public DividerView getView() {
        return this.mView;
    }

    public boolean isMinimized() {
        return this.mMinimized;
    }

    public boolean isHomeStackResizable() {
        return this.mHomeStackResizable;
    }

    public boolean inSplitMode() {
        DividerView dividerView = this.mView;
        return dividerView != null && dividerView.getVisibility() == 0;
    }

    private void addDivider(Configuration configuration) {
        int i;
        Context displayContext = this.mDisplayController.getDisplayContext(this.mContext.getDisplayId());
        this.mView = (DividerView) LayoutInflater.from(displayContext).inflate(C2013R$layout.docked_stack_divider, null);
        DisplayLayout displayLayout = this.mDisplayController.getDisplayLayout(this.mContext.getDisplayId());
        this.mView.injectDependencies(this.mWindowManager, this.mDividerState, this, this.mSplits, this.mSplitLayout);
        boolean z = false;
        this.mView.setVisibility(this.mVisible ? 0 : 4);
        this.mView.setMinimizedDockStack(this.mMinimized, this.mHomeStackResizable);
        int dimensionPixelSize = displayContext.getResources().getDimensionPixelSize(17105167);
        if (configuration.orientation == 2) {
            z = true;
        }
        if (z) {
            i = dimensionPixelSize;
        } else {
            i = displayLayout.width();
        }
        if (z) {
            dimensionPixelSize = displayLayout.height();
        }
        this.mWindowManager.add(this.mView, i, dimensionPixelSize, this.mContext.getDisplayId());
    }

    private void removeDivider() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onDividerRemoved();
        }
        this.mWindowManager.remove();
    }

    private void update(Configuration configuration) {
        removeDivider();
        addDivider(configuration);
        if (this.mMinimized) {
            DividerView dividerView = this.mView;
            if (dividerView != null) {
                dividerView.setMinimizedDockStack(true, this.mHomeStackResizable);
                updateTouchable();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateVisibility(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            this.mView.setVisibility(z ? 0 : 4);
            if (z) {
                this.mView.enterSplitMode(this.mHomeStackResizable);
                this.mView.setMinimizedDockStack(this.mMinimized, this.mHomeStackResizable);
            } else {
                this.mView.exitSplitMode();
                this.mView.setMinimizedDockStack(false, this.mHomeStackResizable);
            }
            synchronized (this.mDockedStackExistsListeners) {
                this.mDockedStackExistsListeners.removeIf(new Predicate(z) {
                    public final /* synthetic */ boolean f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final boolean test(Object obj) {
                        return Divider.lambda$updateVisibility$1(this.f$0, (WeakReference) obj);
                    }
                });
            }
        }
    }

    static /* synthetic */ boolean lambda$updateVisibility$1(boolean z, WeakReference weakReference) {
        Consumer consumer = (Consumer) weakReference.get();
        if (consumer != null) {
            consumer.accept(Boolean.valueOf(z));
        }
        return consumer == null;
    }

    public void setMinimized(boolean z) {
        this.mHandler.post(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Divider.this.lambda$setMinimized$2$Divider(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMinimized$2 */
    public /* synthetic */ void lambda$setMinimized$2$Divider(boolean z) {
        if (this.mVisible) {
            setHomeMinimized(z, this.mHomeStackResizable);
        }
    }

    private void setHomeMinimized(boolean z, boolean z2) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        if (this.mMinimized != z) {
            this.mMinimized = z;
        }
        windowContainerTransaction.setFocusable(this.mSplits.mPrimary.token, !this.mMinimized);
        if (this.mHomeStackResizable != z2) {
            this.mHomeStackResizable = z2;
            if (inSplitMode()) {
                WindowManagerProxy.applyHomeTasksMinimized(this.mSplitLayout, this.mSplits.mSecondary.token, windowContainerTransaction);
            }
        }
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            int displayId = dividerView.getDisplay() != null ? this.mView.getDisplay().getDisplayId() : 0;
            if (this.mMinimized) {
                this.mImePositionProcessor.pause(displayId);
            }
            this.mView.setMinimizedDockStack(z, getAnimDuration(), z2);
            if (!this.mMinimized) {
                this.mImePositionProcessor.resume(displayId);
            }
        }
        updateTouchable();
        WindowManagerProxy.applyContainerTransaction(windowContainerTransaction);
    }

    /* access modifiers changed from: 0000 */
    public void setAdjustedForIme(boolean z) {
        if (this.mAdjustedForIme != z) {
            this.mAdjustedForIme = z;
            updateTouchable();
        }
    }

    private void updateTouchable() {
        this.mWindowManager.setTouchable((this.mHomeStackResizable || !this.mMinimized) && !this.mAdjustedForIme);
    }

    public void onRecentsDrawn() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onRecentsDrawn();
        }
    }

    public void onUndockingTask() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onUndockingTask();
        }
    }

    public void onDockedFirstAnimationFrame() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onDockedFirstAnimationFrame();
        }
    }

    public void onDockedTopTask() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onDockedTopTask();
        }
    }

    public void onAppTransitionFinished() {
        if (this.mView != null) {
            this.mForcedResizableController.onAppTransitionFinished();
        }
    }

    public void onDraggingStart() {
        this.mForcedResizableController.onDraggingStart();
    }

    public void onDraggingEnd() {
        this.mForcedResizableController.onDraggingEnd();
    }

    public void growRecents() {
        this.mRecentsOptionalLazy.ifPresent($$Lambda$Divider$khi_jE4xcxq3HoOT3yA0PCT5IE.INSTANCE);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("  mVisible=");
        printWriter.println(this.mVisible);
        printWriter.print("  mMinimized=");
        printWriter.println(this.mMinimized);
        printWriter.print("  mAdjustedForIme=");
        printWriter.println(this.mAdjustedForIme);
    }

    /* access modifiers changed from: 0000 */
    public long getAnimDuration() {
        return (long) (Global.getFloat(this.mContext.getContentResolver(), "transition_animation_scale", this.mContext.getResources().getFloat(17105052)) * 336.0f);
    }

    public void registerInSplitScreenListener(Consumer<Boolean> consumer) {
        consumer.accept(Boolean.valueOf(inSplitMode()));
        synchronized (this.mDockedStackExistsListeners) {
            this.mDockedStackExistsListeners.add(new WeakReference(consumer));
        }
    }

    /* access modifiers changed from: 0000 */
    public void startEnterSplit() {
        this.mHomeStackResizable = WindowManagerProxy.applyEnterSplit(this.mSplits, this.mSplitLayout);
    }

    /* access modifiers changed from: 0000 */
    public void ensureMinimizedSplit() {
        setHomeMinimized(true, this.mSplits.mSecondary.isResizable());
        if (!inSplitMode()) {
            updateVisibility(true);
        }
    }

    /* access modifiers changed from: 0000 */
    public void ensureNormalSplit() {
        setHomeMinimized(false, this.mHomeStackResizable);
        if (!inSplitMode()) {
            updateVisibility(true);
        }
    }
}
