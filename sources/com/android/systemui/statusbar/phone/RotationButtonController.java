package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.view.IRotationWatcher.Stub;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.WindowManagerGlobal;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2018R$style;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import com.android.systemui.statusbar.policy.RotationLockController;
import java.util.Optional;
import java.util.function.Consumer;

public class RotationButtonController {
    private AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    private final Runnable mCancelPendingRotationProposal = new Runnable() {
        public final void run() {
            RotationButtonController.this.lambda$new$1$RotationButtonController();
        }
    };
    private final Context mContext;
    private boolean mHoveringRotationSuggestion;
    private boolean mIsNavigationBarShowing;
    private int mLastRotationSuggestion;
    private boolean mListenersRegistered = false;
    /* access modifiers changed from: private */
    public final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private final MetricsLogger mMetricsLogger = ((MetricsLogger) Dependency.get(MetricsLogger.class));
    private boolean mPendingRotationSuggestion;
    private final Runnable mRemoveRotationProposal = new Runnable() {
        public final void run() {
            RotationButtonController.this.lambda$new$0$RotationButtonController();
        }
    };
    /* access modifiers changed from: private */
    public Consumer<Integer> mRotWatcherListener;
    private Animator mRotateHideAnimator;
    /* access modifiers changed from: private */
    public final RotationButton mRotationButton;
    /* access modifiers changed from: private */
    public RotationLockController mRotationLockController;
    private final Stub mRotationWatcher = new Stub() {
        public void onRotationChanged(int i) throws RemoteException {
            RotationButtonController.this.mMainThreadHandler.postAtFrontOfQueue(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    C15691.this.lambda$onRotationChanged$0$RotationButtonController$1(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onRotationChanged$0 */
        public /* synthetic */ void lambda$onRotationChanged$0$RotationButtonController$1(int i) {
            if (RotationButtonController.this.mRotationLockController.isRotationLocked()) {
                if (RotationButtonController.this.shouldOverrideUserLockPrefs(i)) {
                    RotationButtonController.this.setRotationLockedAtAngle(i);
                }
                RotationButtonController.this.setRotateSuggestionButtonState(false, true);
            }
            if (RotationButtonController.this.mRotWatcherListener != null) {
                RotationButtonController.this.mRotWatcherListener.accept(Integer.valueOf(i));
            }
        }
    };
    private int mStyleRes;
    private TaskStackListenerImpl mTaskStackListener;
    private final ViewRippler mViewRippler = new ViewRippler();

    private class TaskStackListenerImpl extends TaskStackChangeListener {
        private TaskStackListenerImpl() {
        }

        public void onTaskStackChanged() {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        public void onTaskRemoved(int i) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        public void onTaskMovedToFront(int i) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        public void onActivityRequestedOrientationChanged(int i, int i2) {
            Optional.ofNullable(ActivityManagerWrapper.getInstance()).map($$Lambda$Zm3Yj0EQnVWvu_ZksQOsrTwJ3k.INSTANCE).ifPresent(new Consumer(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    TaskStackListenerImpl.this.mo18095x156bd330(this.f$1, (RunningTaskInfo) obj);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onActivityRequestedOrientationChanged$0 */
        public /* synthetic */ void mo18095x156bd330(int i, RunningTaskInfo runningTaskInfo) {
            if (runningTaskInfo.id == i) {
                RotationButtonController.this.setRotateSuggestionButtonState(false);
            }
        }
    }

    private class ViewRippler {
        private final Runnable mRipple;
        /* access modifiers changed from: private */
        public View mRoot;

        private ViewRippler(RotationButtonController rotationButtonController) {
            this.mRipple = new Runnable() {
                public void run() {
                    if (ViewRippler.this.mRoot.isAttachedToWindow()) {
                        ViewRippler.this.mRoot.setPressed(true);
                        ViewRippler.this.mRoot.setPressed(false);
                    }
                }
            };
        }

        public void start(View view) {
            stop();
            this.mRoot = view;
            view.postOnAnimationDelayed(this.mRipple, 50);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 2000);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 4000);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 6000);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 8000);
        }

        public void stop() {
            View view = this.mRoot;
            if (view != null) {
                view.removeCallbacks(this.mRipple);
            }
        }
    }

    static boolean hasDisable2RotateSuggestionFlag(int i) {
        return (i & 16) != 0;
    }

    private boolean isRotationAnimationCCW(int i, int i2) {
        if (i == 0 && i2 == 1) {
            return false;
        }
        if (i == 0 && i2 == 2) {
            return true;
        }
        if (i == 0 && i2 == 3) {
            return true;
        }
        if (i == 1 && i2 == 0) {
            return true;
        }
        if (i == 1 && i2 == 2) {
            return false;
        }
        if (i == 1 && i2 == 3) {
            return true;
        }
        if (i == 2 && i2 == 0) {
            return true;
        }
        if (i == 2 && i2 == 1) {
            return true;
        }
        if (i == 2 && i2 == 3) {
            return false;
        }
        if (i == 3 && i2 == 0) {
            return false;
        }
        if (i == 3 && i2 == 1) {
            return true;
        }
        return i == 3 && i2 == 2;
    }

    /* access modifiers changed from: private */
    public boolean shouldOverrideUserLockPrefs(int i) {
        return i == 0;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$RotationButtonController() {
        setRotateSuggestionButtonState(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$RotationButtonController() {
        this.mPendingRotationSuggestion = false;
    }

    RotationButtonController(Context context, int i, RotationButton rotationButton) {
        this.mContext = context;
        this.mRotationButton = rotationButton;
        rotationButton.setRotationButtonController(this);
        this.mStyleRes = i;
        this.mIsNavigationBarShowing = true;
        this.mRotationLockController = (RotationLockController) Dependency.get(RotationLockController.class);
        this.mAccessibilityManagerWrapper = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);
        this.mTaskStackListener = new TaskStackListenerImpl();
        this.mRotationButton.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                RotationButtonController.this.onRotateSuggestionClick(view);
            }
        });
        this.mRotationButton.setOnHoverListener(new OnHoverListener() {
            public final boolean onHover(View view, MotionEvent motionEvent) {
                return RotationButtonController.this.onRotateSuggestionHover(view, motionEvent);
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void registerListeners() {
        if (!this.mListenersRegistered) {
            this.mListenersRegistered = true;
            try {
                WindowManagerGlobal.getWindowManagerService().watchRotation(this.mRotationWatcher, this.mContext.getDisplay().getDisplayId());
                ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void unregisterListeners() {
        if (this.mListenersRegistered) {
            this.mListenersRegistered = false;
            try {
                WindowManagerGlobal.getWindowManagerService().removeRotationWatcher(this.mRotationWatcher);
                ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void addRotationCallback(Consumer<Integer> consumer) {
        this.mRotWatcherListener = consumer;
    }

    /* access modifiers changed from: 0000 */
    public void setRotationLockedAtAngle(int i) {
        this.mRotationLockController.setRotationLockedAtAngle(true, i);
    }

    public boolean isRotationLocked() {
        return this.mRotationLockController.isRotationLocked();
    }

    /* access modifiers changed from: 0000 */
    public void setRotateSuggestionButtonState(boolean z) {
        setRotateSuggestionButtonState(z, false);
    }

    /* access modifiers changed from: 0000 */
    public void setRotateSuggestionButtonState(boolean z, boolean z2) {
        if (z || this.mRotationButton.isVisible()) {
            View currentView = this.mRotationButton.getCurrentView();
            if (currentView != null) {
                KeyButtonDrawable imageDrawable = this.mRotationButton.getImageDrawable();
                if (imageDrawable != null) {
                    this.mPendingRotationSuggestion = false;
                    this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
                    if (z) {
                        Animator animator = this.mRotateHideAnimator;
                        if (animator != null && animator.isRunning()) {
                            this.mRotateHideAnimator.cancel();
                        }
                        this.mRotateHideAnimator = null;
                        currentView.setAlpha(1.0f);
                        if (imageDrawable.canAnimate()) {
                            imageDrawable.resetAnimation();
                            imageDrawable.startAnimation();
                        }
                        if (!isRotateSuggestionIntroduced()) {
                            this.mViewRippler.start(currentView);
                        }
                        this.mRotationButton.show();
                    } else {
                        this.mViewRippler.stop();
                        if (z2) {
                            Animator animator2 = this.mRotateHideAnimator;
                            if (animator2 != null && animator2.isRunning()) {
                                this.mRotateHideAnimator.pause();
                            }
                            this.mRotationButton.hide();
                            return;
                        }
                        Animator animator3 = this.mRotateHideAnimator;
                        if (animator3 == null || !animator3.isRunning()) {
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(currentView, "alpha", new float[]{0.0f});
                            ofFloat.setDuration(100);
                            ofFloat.setInterpolator(Interpolators.LINEAR);
                            ofFloat.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    RotationButtonController.this.mRotationButton.hide();
                                }
                            });
                            this.mRotateHideAnimator = ofFloat;
                            ofFloat.start();
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setDarkIntensity(float f) {
        this.mRotationButton.setDarkIntensity(f);
    }

    /* access modifiers changed from: 0000 */
    public void onRotationProposal(int i, int i2, boolean z) {
        if (this.mRotationButton.acceptRotationProposal()) {
            if (!z) {
                setRotateSuggestionButtonState(false);
            } else if (i == i2) {
                this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
                setRotateSuggestionButtonState(false);
            } else {
                this.mLastRotationSuggestion = i;
                boolean isRotationAnimationCCW = isRotationAnimationCCW(i2, i);
                int i3 = (i2 == 0 || i2 == 2) ? isRotationAnimationCCW ? C2018R$style.RotateButtonCCWStart90 : C2018R$style.RotateButtonCWStart90 : isRotationAnimationCCW ? C2018R$style.RotateButtonCCWStart0 : C2018R$style.RotateButtonCWStart0;
                this.mStyleRes = i3;
                this.mRotationButton.updateIcon();
                if (this.mIsNavigationBarShowing) {
                    showAndLogRotationSuggestion();
                } else {
                    this.mPendingRotationSuggestion = true;
                    this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
                    this.mMainThreadHandler.postDelayed(this.mCancelPendingRotationProposal, 20000);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDisable2FlagChanged(int i) {
        if (hasDisable2RotateSuggestionFlag(i)) {
            onRotationSuggestionsDisabled();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onNavigationBarWindowVisibilityChange(boolean z) {
        if (this.mIsNavigationBarShowing != z) {
            this.mIsNavigationBarShowing = z;
            if (z && this.mPendingRotationSuggestion) {
                showAndLogRotationSuggestion();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int getStyleRes() {
        return this.mStyleRes;
    }

    /* access modifiers changed from: 0000 */
    public RotationButton getRotationButton() {
        return this.mRotationButton;
    }

    /* access modifiers changed from: private */
    public void onRotateSuggestionClick(View view) {
        this.mMetricsLogger.action(1287);
        incrementNumAcceptedRotationSuggestionsIfNeeded();
        setRotationLockedAtAngle(this.mLastRotationSuggestion);
    }

    /* access modifiers changed from: private */
    public boolean onRotateSuggestionHover(View view, MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        this.mHoveringRotationSuggestion = actionMasked == 9 || actionMasked == 7;
        rescheduleRotationTimeout(true);
        return false;
    }

    private void onRotationSuggestionsDisabled() {
        setRotateSuggestionButtonState(false, true);
        this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
    }

    private void showAndLogRotationSuggestion() {
        setRotateSuggestionButtonState(true);
        rescheduleRotationTimeout(false);
        this.mMetricsLogger.visible(1288);
    }

    private void rescheduleRotationTimeout(boolean z) {
        if (z) {
            Animator animator = this.mRotateHideAnimator;
            if ((animator != null && animator.isRunning()) || !this.mRotationButton.isVisible()) {
                return;
            }
        }
        this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
        this.mMainThreadHandler.postDelayed(this.mRemoveRotationProposal, (long) computeRotationProposalTimeout());
    }

    private int computeRotationProposalTimeout() {
        return this.mAccessibilityManagerWrapper.getRecommendedTimeoutMillis(this.mHoveringRotationSuggestion ? 16000 : 5000, 4);
    }

    private boolean isRotateSuggestionIntroduced() {
        if (Secure.getInt(this.mContext.getContentResolver(), "num_rotation_suggestions_accepted", 0) >= 3) {
            return true;
        }
        return false;
    }

    private void incrementNumAcceptedRotationSuggestionsIfNeeded() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String str = "num_rotation_suggestions_accepted";
        int i = Secure.getInt(contentResolver, str, 0);
        if (i < 3) {
            Secure.putInt(contentResolver, str, i + 1);
        }
    }
}
