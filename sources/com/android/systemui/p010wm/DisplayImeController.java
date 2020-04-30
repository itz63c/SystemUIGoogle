package com.android.systemui.p010wm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IDisplayWindowInsetsController.Stub;
import android.view.InsetsSource;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.SurfaceControl.Transaction;
import android.view.WindowInsets.Type;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.TransactionPool;
import com.android.systemui.p010wm.DisplayController.OnDisplaysChangedListener;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: com.android.systemui.wm.DisplayImeController */
public class DisplayImeController implements OnDisplaysChangedListener {
    public static final Interpolator INTERPOLATOR = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    final Handler mHandler;
    final SparseArray<PerDisplay> mImePerDisplay = new SparseArray<>();
    final ArrayList<ImePositionProcessor> mPositionProcessors = new ArrayList<>();
    SystemWindows mSystemWindows;
    final TransactionPool mTransactionPool;

    /* renamed from: com.android.systemui.wm.DisplayImeController$ImePositionProcessor */
    public interface ImePositionProcessor {
        void onImeEndPositioning(int i, boolean z, Transaction transaction) {
        }

        void onImePositionChanged(int i, int i2, Transaction transaction) {
        }

        void onImeStartPositioning(int i, int i2, int i3, boolean z, Transaction transaction) {
        }
    }

    /* renamed from: com.android.systemui.wm.DisplayImeController$PerDisplay */
    class PerDisplay extends Stub {
        ValueAnimator mAnimation = null;
        int mAnimationDirection = 0;
        final int mDisplayId;
        boolean mImeShowing = false;
        InsetsSourceControl mImeSourceControl = null;
        final InsetsState mInsetsState = new InsetsState();
        int mRotation = 0;

        PerDisplay(int i, int i2) {
            this.mDisplayId = i;
            this.mRotation = i2;
        }

        public void insetsChanged(InsetsState insetsState) {
            if (!this.mInsetsState.equals(insetsState)) {
                this.mInsetsState.set(insetsState, true);
            }
        }

        public void insetsControlChanged(InsetsState insetsState, InsetsSourceControl[] insetsSourceControlArr) {
            insetsChanged(insetsState);
            if (insetsSourceControlArr != null) {
                for (InsetsSourceControl insetsSourceControl : insetsSourceControlArr) {
                    if (insetsSourceControl != null && insetsSourceControl.getType() == 13) {
                        DisplayImeController.this.mHandler.post(new Runnable(insetsSourceControl) {
                            public final /* synthetic */ InsetsSourceControl f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PerDisplay.this.lambda$insetsControlChanged$0$DisplayImeController$PerDisplay(this.f$1);
                            }
                        });
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$insetsControlChanged$0 */
        public /* synthetic */ void lambda$insetsControlChanged$0$DisplayImeController$PerDisplay(InsetsSourceControl insetsSourceControl) {
            InsetsSourceControl insetsSourceControl2 = this.mImeSourceControl;
            Point surfacePosition = insetsSourceControl2 != null ? insetsSourceControl2.getSurfacePosition() : null;
            this.mImeSourceControl = insetsSourceControl;
            if (!insetsSourceControl.getSurfacePosition().equals(surfacePosition) && this.mAnimation != null) {
                startAnimation(this.mImeShowing, true);
            }
        }

        public void showInsets(int i, boolean z) {
            if ((i & Type.ime()) != 0) {
                startAnimation(true, false);
            }
        }

        public void hideInsets(int i, boolean z) {
            if ((i & Type.ime()) != 0) {
                startAnimation(false, false);
            }
        }

        private void setVisibleDirectly(boolean z) {
            this.mInsetsState.getSource(13).setVisible(z);
            try {
                DisplayImeController.this.mSystemWindows.mWmService.modifyDisplayWindowInsets(this.mDisplayId, this.mInsetsState);
            } catch (RemoteException unused) {
            }
        }

        /* access modifiers changed from: private */
        public int imeTop(InsetsSource insetsSource, float f) {
            return insetsSource.getFrame().top + ((int) f);
        }

        /* access modifiers changed from: private */
        public void startAnimation(boolean z, boolean z2) {
            InsetsSource source = this.mInsetsState.getSource(13);
            if (source != null && this.mImeSourceControl != null) {
                DisplayImeController.this.mHandler.post(new Runnable(z, z2, source) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ boolean f$2;
                    public final /* synthetic */ InsetsSource f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        PerDisplay.this.lambda$startAnimation$2$DisplayImeController$PerDisplay(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$startAnimation$2 */
        public /* synthetic */ void lambda$startAnimation$2$DisplayImeController$PerDisplay(boolean z, boolean z2, InsetsSource insetsSource) {
            boolean z3;
            boolean z4 = z;
            if ((z2 || this.mAnimationDirection != 1 || !z4) && (this.mAnimationDirection != 2 || z4)) {
                float f = 0.0f;
                ValueAnimator valueAnimator = this.mAnimation;
                if (valueAnimator != null) {
                    if (valueAnimator.isRunning()) {
                        f = ((Float) this.mAnimation.getAnimatedValue()).floatValue();
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    this.mAnimation.cancel();
                } else {
                    z3 = false;
                }
                this.mAnimationDirection = z4 ? 1 : 2;
                final float f2 = (float) this.mImeSourceControl.getSurfacePosition().y;
                float f3 = (float) this.mImeSourceControl.getSurfacePosition().x;
                final float height = f2 + ((float) insetsSource.getFrame().height());
                float f4 = z4 ? height : f2;
                final float f5 = z4 ? f2 : height;
                this.mImeShowing = z4;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f4, f5});
                this.mAnimation = ofFloat;
                ofFloat.setDuration(z4 ? 275 : 340);
                if (z3) {
                    this.mAnimation.setCurrentFraction((f - f4) / (f5 - f4));
                }
                this.mAnimation.addUpdateListener(new AnimatorUpdateListener(f3, insetsSource) {
                    public final /* synthetic */ float f$1;
                    public final /* synthetic */ InsetsSource f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PerDisplay.this.lambda$startAnimation$1$DisplayImeController$PerDisplay(this.f$1, this.f$2, valueAnimator);
                    }
                });
                this.mAnimation.setInterpolator(DisplayImeController.INTERPOLATOR);
                ValueAnimator valueAnimator2 = this.mAnimation;
                final float f6 = f3;
                final float f7 = f4;
                final InsetsSource insetsSource2 = insetsSource;
                C17931 r0 = new AnimatorListenerAdapter() {
                    private boolean mCancelled = false;

                    public void onAnimationStart(Animator animator) {
                        Transaction acquire = DisplayImeController.this.mTransactionPool.acquire();
                        acquire.setPosition(PerDisplay.this.mImeSourceControl.getLeash(), f6, f7);
                        PerDisplay perDisplay = PerDisplay.this;
                        DisplayImeController.this.dispatchStartPositioning(perDisplay.mDisplayId, perDisplay.imeTop(insetsSource2, height), PerDisplay.this.imeTop(insetsSource2, f2), PerDisplay.this.mAnimationDirection == 1, acquire);
                        PerDisplay perDisplay2 = PerDisplay.this;
                        if (perDisplay2.mAnimationDirection == 1) {
                            acquire.show(perDisplay2.mImeSourceControl.getLeash());
                        }
                        acquire.apply();
                        DisplayImeController.this.mTransactionPool.release(acquire);
                    }

                    public void onAnimationCancel(Animator animator) {
                        this.mCancelled = true;
                    }

                    public void onAnimationEnd(Animator animator) {
                        Transaction acquire = DisplayImeController.this.mTransactionPool.acquire();
                        if (!this.mCancelled) {
                            acquire.setPosition(PerDisplay.this.mImeSourceControl.getLeash(), f6, f5);
                        }
                        PerDisplay perDisplay = PerDisplay.this;
                        DisplayImeController.this.dispatchEndPositioning(perDisplay.mDisplayId, this.mCancelled, acquire);
                        PerDisplay perDisplay2 = PerDisplay.this;
                        if (perDisplay2.mAnimationDirection == 2 && !this.mCancelled) {
                            acquire.hide(perDisplay2.mImeSourceControl.getLeash());
                        }
                        acquire.apply();
                        DisplayImeController.this.mTransactionPool.release(acquire);
                        PerDisplay perDisplay3 = PerDisplay.this;
                        perDisplay3.mAnimationDirection = 0;
                        perDisplay3.mAnimation = null;
                    }
                };
                valueAnimator2.addListener(r0);
                if (!z4) {
                    setVisibleDirectly(false);
                }
                this.mAnimation.start();
                if (z4) {
                    setVisibleDirectly(true);
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$startAnimation$1 */
        public /* synthetic */ void lambda$startAnimation$1$DisplayImeController$PerDisplay(float f, InsetsSource insetsSource, ValueAnimator valueAnimator) {
            Transaction acquire = DisplayImeController.this.mTransactionPool.acquire();
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            acquire.setPosition(this.mImeSourceControl.getLeash(), f, floatValue);
            DisplayImeController.this.dispatchPositionChanged(this.mDisplayId, imeTop(insetsSource, floatValue), acquire);
            acquire.apply();
            DisplayImeController.this.mTransactionPool.release(acquire);
        }
    }

    public DisplayImeController(SystemWindows systemWindows, DisplayController displayController, Handler handler, TransactionPool transactionPool) {
        this.mHandler = handler;
        this.mSystemWindows = systemWindows;
        this.mTransactionPool = transactionPool;
        displayController.addDisplayWindowListener(this);
    }

    public void onDisplayAdded(int i) {
        PerDisplay perDisplay = new PerDisplay(i, this.mSystemWindows.mDisplayController.getDisplayLayout(i).rotation());
        try {
            this.mSystemWindows.mWmService.setDisplayWindowInsetsController(i, perDisplay);
        } catch (RemoteException unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to set insets controller on display ");
            sb.append(i);
            Slog.w("DisplayImeController", sb.toString());
        }
        this.mImePerDisplay.put(i, perDisplay);
    }

    public void onDisplayConfigurationChanged(int i, Configuration configuration) {
        PerDisplay perDisplay = (PerDisplay) this.mImePerDisplay.get(i);
        if (!(perDisplay == null || this.mSystemWindows.mDisplayController.getDisplayLayout(i).rotation() == perDisplay.mRotation || !isImeShowing(i))) {
            perDisplay.startAnimation(true, false);
        }
    }

    public void onDisplayRemoved(int i) {
        try {
            this.mSystemWindows.mWmService.setDisplayWindowInsetsController(i, null);
        } catch (RemoteException unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to remove insets controller on display ");
            sb.append(i);
            Slog.w("DisplayImeController", sb.toString());
        }
        this.mImePerDisplay.remove(i);
    }

    private boolean isImeShowing(int i) {
        PerDisplay perDisplay = (PerDisplay) this.mImePerDisplay.get(i);
        boolean z = false;
        if (perDisplay == null) {
            return false;
        }
        InsetsSource source = perDisplay.mInsetsState.getSource(13);
        if (!(source == null || perDisplay.mImeSourceControl == null || !source.isVisible())) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void dispatchPositionChanged(int i, int i2, Transaction transaction) {
        synchronized (this.mPositionProcessors) {
            Iterator it = this.mPositionProcessors.iterator();
            while (it.hasNext()) {
                ((ImePositionProcessor) it.next()).onImePositionChanged(i, i2, transaction);
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchStartPositioning(int i, int i2, int i3, boolean z, Transaction transaction) {
        synchronized (this.mPositionProcessors) {
            Iterator it = this.mPositionProcessors.iterator();
            while (it.hasNext()) {
                ((ImePositionProcessor) it.next()).onImeStartPositioning(i, i2, i3, z, transaction);
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchEndPositioning(int i, boolean z, Transaction transaction) {
        synchronized (this.mPositionProcessors) {
            Iterator it = this.mPositionProcessors.iterator();
            while (it.hasNext()) {
                ((ImePositionProcessor) it.next()).onImeEndPositioning(i, z, transaction);
            }
        }
    }

    public void addPositionProcessor(ImePositionProcessor imePositionProcessor) {
        synchronized (this.mPositionProcessors) {
            if (!this.mPositionProcessors.contains(imePositionProcessor)) {
                this.mPositionProcessors.add(imePositionProcessor);
            }
        }
    }
}
