package com.android.systemui.pip;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityTaskManager;
import android.app.ITaskOrganizerController;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo.WindowLayout;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Size;
import android.view.ITaskOrganizer.Stub;
import android.view.IWindowContainer;
import android.view.SurfaceControl;
import android.view.SurfaceControl.Transaction;
import android.view.WindowContainerTransaction;
import com.android.internal.os.SomeArgs;
import com.android.systemui.C2012R$integer;
import com.android.systemui.pip.PipAnimationController.PipAnimationCallback;
import com.android.systemui.pip.PipAnimationController.PipTransitionAnimator;
import com.android.systemui.pip.phone.PipUpdateThread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class PipTaskOrganizer extends Stub {
    private static final String TAG = PipTaskOrganizer.class.getSimpleName();
    private final Map<IBinder, Rect> mBoundsToRestore = new HashMap();
    private final int mEnterExitAnimationDuration;
    private boolean mInPip;
    private final Rect mLastReportedBounds = new Rect();
    private SurfaceControl mLeash;
    /* access modifiers changed from: private */
    public final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private int mOneShotAnimationType = 0;
    private final PipAnimationCallback mPipAnimationCallback = new PipAnimationCallback() {
        public void onPipAnimationStart(PipTransitionAnimator pipTransitionAnimator) {
            PipTaskOrganizer.this.mMainHandler.post(new Runnable() {
                public final void run() {
                    C08941.this.lambda$onPipAnimationStart$0$PipTaskOrganizer$1();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPipAnimationStart$0 */
        public /* synthetic */ void lambda$onPipAnimationStart$0$PipTaskOrganizer$1() {
            for (int size = PipTaskOrganizer.this.mPipTransitionCallbacks.size() - 1; size >= 0; size--) {
                ((PipTransitionCallback) PipTaskOrganizer.this.mPipTransitionCallbacks.get(size)).onPipTransitionStarted();
            }
        }

        public void onPipAnimationEnd(Transaction transaction, PipTransitionAnimator pipTransitionAnimator) {
            PipTaskOrganizer.this.mMainHandler.post(new Runnable() {
                public final void run() {
                    C08941.this.lambda$onPipAnimationEnd$1$PipTaskOrganizer$1();
                }
            });
            PipTaskOrganizer.this.finishResize(transaction, pipTransitionAnimator.getDestinationBounds(), pipTransitionAnimator.getTransitionDirection());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPipAnimationEnd$1 */
        public /* synthetic */ void lambda$onPipAnimationEnd$1$PipTaskOrganizer$1() {
            for (int size = PipTaskOrganizer.this.mPipTransitionCallbacks.size() - 1; size >= 0; size--) {
                ((PipTransitionCallback) PipTaskOrganizer.this.mPipTransitionCallbacks.get(size)).onPipTransitionFinished();
            }
        }

        public void onPipAnimationCancel(PipTransitionAnimator pipTransitionAnimator) {
            PipTaskOrganizer.this.mMainHandler.post(new Runnable() {
                public final void run() {
                    C08941.this.lambda$onPipAnimationCancel$2$PipTaskOrganizer$1();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPipAnimationCancel$2 */
        public /* synthetic */ void lambda$onPipAnimationCancel$2$PipTaskOrganizer$1() {
            for (int size = PipTaskOrganizer.this.mPipTransitionCallbacks.size() - 1; size >= 0; size--) {
                ((PipTransitionCallback) PipTaskOrganizer.this.mPipTransitionCallbacks.get(size)).onPipTransitionCanceled();
            }
        }
    };
    private final PipAnimationController mPipAnimationController;
    private final PipBoundsHandler mPipBoundsHandler;
    /* access modifiers changed from: private */
    public final List<PipTransitionCallback> mPipTransitionCallbacks = new ArrayList();
    private SurfaceControlTransactionFactory mSurfaceControlTransactionFactory;
    private final PipSurfaceTransactionHelper mSurfaceTransactionHelper;
    private RunningTaskInfo mTaskInfo;
    private final ITaskOrganizerController mTaskOrganizerController = ActivityTaskManager.getTaskOrganizerController();
    private IWindowContainer mToken;
    private Callback mUpdateCallbacks = new Callback() {
        public final boolean handleMessage(Message message) {
            return PipTaskOrganizer.this.lambda$new$0$PipTaskOrganizer(message);
        }
    };
    private final Handler mUpdateHandler = new Handler(PipUpdateThread.get().getLooper(), this.mUpdateCallbacks);

    public interface PipTransitionCallback {
        void onPipTransitionCanceled();

        void onPipTransitionFinished();

        void onPipTransitionStarted();
    }

    public void transactionReady(int i, Transaction transaction) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ boolean lambda$new$0$PipTaskOrganizer(Message message) {
        SomeArgs someArgs = (SomeArgs) message.obj;
        Consumer consumer = (Consumer) someArgs.arg1;
        int i = message.what;
        if (i == 1) {
            Rect rect = (Rect) someArgs.arg2;
            resizePip(rect);
            if (consumer != null) {
                consumer.accept(rect);
            }
        } else if (i == 2) {
            Rect rect2 = (Rect) someArgs.arg3;
            animateResizePip((Rect) someArgs.arg2, rect2, someArgs.argi1, someArgs.argi2);
            if (consumer != null) {
                consumer.accept(rect2);
            }
        } else if (i == 3) {
            Rect rect3 = (Rect) someArgs.arg2;
            int i2 = someArgs.argi1;
            offsetPip(rect3, 0, i2, someArgs.argi2);
            Rect rect4 = new Rect(rect3);
            rect4.offset(0, i2);
            if (consumer != null) {
                consumer.accept(rect4);
            }
        } else if (i == 4) {
            Rect rect5 = (Rect) someArgs.arg3;
            finishResize((Transaction) someArgs.arg2, rect5, someArgs.argi1);
            if (consumer != null) {
                consumer.accept(rect5);
            }
        }
        someArgs.recycle();
        return true;
    }

    public PipTaskOrganizer(Context context, PipBoundsHandler pipBoundsHandler, PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        this.mPipBoundsHandler = pipBoundsHandler;
        this.mEnterExitAnimationDuration = context.getResources().getInteger(C2012R$integer.config_pipResizeAnimationDuration);
        this.mSurfaceTransactionHelper = pipSurfaceTransactionHelper;
        this.mPipAnimationController = new PipAnimationController(context, pipSurfaceTransactionHelper);
        this.mSurfaceControlTransactionFactory = $$Lambda$0FLZQAxNoOm85ohJ3bgjkYQDWsU.INSTANCE;
    }

    public Handler getUpdateHandler() {
        return this.mUpdateHandler;
    }

    public void registerPipTransitionCallback(PipTransitionCallback pipTransitionCallback) {
        this.mPipTransitionCallbacks.add(pipTransitionCallback);
    }

    public void setOneShotAnimationType(int i) {
        this.mOneShotAnimationType = i;
    }

    public void taskAppeared(RunningTaskInfo runningTaskInfo) {
        Objects.requireNonNull(runningTaskInfo, "Requires RunningTaskInfo");
        Rect destinationBounds = this.mPipBoundsHandler.getDestinationBounds(getAspectRatioOrDefault(runningTaskInfo.pictureInPictureParams), null, getMinimalSize(runningTaskInfo.topActivityInfo));
        Objects.requireNonNull(destinationBounds, "Missing destination bounds");
        this.mTaskInfo = runningTaskInfo;
        IWindowContainer iWindowContainer = runningTaskInfo.token;
        this.mToken = iWindowContainer;
        this.mInPip = true;
        try {
            this.mLeash = iWindowContainer.getLeash();
            Rect bounds = this.mTaskInfo.configuration.windowConfiguration.getBounds();
            this.mBoundsToRestore.put(this.mToken.asBinder(), bounds);
            int i = this.mOneShotAnimationType;
            if (i == 0) {
                scheduleAnimateResizePip(bounds, destinationBounds, 2, this.mEnterExitAnimationDuration, null);
            } else if (i == 1) {
                this.mUpdateHandler.post(new Runnable(destinationBounds) {
                    public final /* synthetic */ Rect f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        PipTaskOrganizer.this.lambda$taskAppeared$1$PipTaskOrganizer(this.f$1);
                    }
                });
                this.mOneShotAnimationType = 0;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized animation type: ");
                sb.append(this.mOneShotAnimationType);
                throw new RuntimeException(sb.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException("Unable to get leash", e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$taskAppeared$1 */
    public /* synthetic */ void lambda$taskAppeared$1$PipTaskOrganizer(Rect rect) {
        PipTransitionAnimator animator = this.mPipAnimationController.getAnimator(this.mLeash, rect, 0.0f, 1.0f);
        animator.setTransitionDirection(2);
        animator.setPipAnimationCallback(this.mPipAnimationCallback);
        animator.setDuration((long) this.mEnterExitAnimationDuration).start();
    }

    public void taskVanished(RunningTaskInfo runningTaskInfo) {
        IWindowContainer iWindowContainer = runningTaskInfo.token;
        Objects.requireNonNull(iWindowContainer, "Requires valid IWindowContainer");
        if (iWindowContainer.asBinder() != this.mToken.asBinder()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Unrecognized token: ");
            sb.append(iWindowContainer);
            Log.wtf(str, sb.toString());
            return;
        }
        scheduleAnimateResizePip(this.mLastReportedBounds, (Rect) this.mBoundsToRestore.remove(iWindowContainer.asBinder()), 3, this.mEnterExitAnimationDuration, null);
        this.mInPip = false;
    }

    public void onTaskInfoChanged(RunningTaskInfo runningTaskInfo) {
        PictureInPictureParams pictureInPictureParams = runningTaskInfo.pictureInPictureParams;
        if (!shouldUpdateDestinationBounds(pictureInPictureParams)) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Ignored onTaskInfoChanged with PiP param: ");
            sb.append(pictureInPictureParams);
            Log.d(str, sb.toString());
            return;
        }
        Rect destinationBounds = this.mPipBoundsHandler.getDestinationBounds(getAspectRatioOrDefault(pictureInPictureParams), null, getMinimalSize(runningTaskInfo.topActivityInfo));
        Objects.requireNonNull(destinationBounds, "Missing destination bounds");
        scheduleAnimateResizePip(destinationBounds, this.mEnterExitAnimationDuration, null);
    }

    private boolean shouldUpdateDestinationBounds(PictureInPictureParams pictureInPictureParams) {
        boolean z = true;
        if (pictureInPictureParams != null) {
            PictureInPictureParams pictureInPictureParams2 = this.mTaskInfo.pictureInPictureParams;
            if (pictureInPictureParams2 != null) {
                return !Objects.equals(pictureInPictureParams2.getAspectRatioRational(), pictureInPictureParams.getAspectRatioRational());
            }
        }
        if (pictureInPictureParams == this.mTaskInfo.pictureInPictureParams) {
            z = false;
        }
        return z;
    }

    public void scheduleAnimateResizePip(Rect rect, int i, Consumer<Rect> consumer) {
        scheduleAnimateResizePip(this.mLastReportedBounds, rect, 0, i, consumer);
    }

    private void scheduleAnimateResizePip(Rect rect, Rect rect2, int i, int i2, Consumer<Rect> consumer) {
        if (this.mInPip) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = consumer;
            obtain.arg2 = rect;
            obtain.arg3 = rect2;
            obtain.argi1 = i;
            obtain.argi2 = i2;
            Handler handler = this.mUpdateHandler;
            handler.sendMessage(handler.obtainMessage(2, obtain));
        }
    }

    public void scheduleResizePip(Rect rect, Consumer<Rect> consumer) {
        SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = consumer;
        obtain.arg2 = rect;
        Handler handler = this.mUpdateHandler;
        handler.sendMessage(handler.obtainMessage(1, obtain));
    }

    public void scheduleFinishResizePip(Rect rect) {
        Transaction transaction = this.mSurfaceControlTransactionFactory.getTransaction();
        PipSurfaceTransactionHelper pipSurfaceTransactionHelper = this.mSurfaceTransactionHelper;
        pipSurfaceTransactionHelper.crop(transaction, this.mLeash, rect);
        pipSurfaceTransactionHelper.round(transaction, this.mLeash, this.mInPip);
        scheduleFinishResizePip(transaction, rect, 0, null);
    }

    private void scheduleFinishResizePip(Transaction transaction, Rect rect, int i, Consumer<Rect> consumer) {
        SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = consumer;
        obtain.arg2 = transaction;
        obtain.arg3 = rect;
        obtain.argi1 = i;
        Handler handler = this.mUpdateHandler;
        handler.sendMessage(handler.obtainMessage(4, obtain));
    }

    public void scheduleOffsetPip(Rect rect, int i, int i2, Consumer<Rect> consumer) {
        if (this.mInPip) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = consumer;
            obtain.arg2 = rect;
            obtain.argi1 = i;
            obtain.argi2 = i2;
            Handler handler = this.mUpdateHandler;
            handler.sendMessage(handler.obtainMessage(3, obtain));
        }
    }

    private void offsetPip(Rect rect, int i, int i2, int i3) {
        if (Looper.myLooper() != this.mUpdateHandler.getLooper()) {
            throw new RuntimeException("Callers should call scheduleOffsetPip() instead of this directly");
        } else if (this.mTaskInfo == null) {
            Log.w(TAG, "mTaskInfo is not set");
        } else {
            Rect rect2 = new Rect(rect);
            rect2.offset(i, i2);
            animateResizePip(rect, rect2, 1, i3);
        }
    }

    private void resizePip(Rect rect) {
        if (Looper.myLooper() != this.mUpdateHandler.getLooper()) {
            throw new RuntimeException("Callers should call scheduleResizePip() instead of this directly");
        } else if (this.mToken == null || this.mLeash == null) {
            Log.w(TAG, "Abort animation, invalid leash");
        } else {
            Transaction transaction = this.mSurfaceControlTransactionFactory.getTransaction();
            PipSurfaceTransactionHelper pipSurfaceTransactionHelper = this.mSurfaceTransactionHelper;
            pipSurfaceTransactionHelper.crop(transaction, this.mLeash, rect);
            pipSurfaceTransactionHelper.round(transaction, this.mLeash, this.mInPip);
            transaction.apply();
        }
    }

    /* access modifiers changed from: private */
    public void finishResize(Transaction transaction, Rect rect, int i) {
        if (Looper.myLooper() == this.mUpdateHandler.getLooper()) {
            this.mLastReportedBounds.set(rect);
            if (i == 3) {
                rect = null;
            }
            try {
                WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
                if (i == 2) {
                    windowContainerTransaction.scheduleFinishEnterPip(this.mToken, rect);
                } else {
                    windowContainerTransaction.setBounds(this.mToken, rect);
                }
                windowContainerTransaction.setBoundsChangeTransaction(this.mToken, transaction);
                this.mTaskOrganizerController.applyContainerTransaction(windowContainerTransaction, null);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to apply container transaction", e);
            }
        } else {
            throw new RuntimeException("Callers should call scheduleResizePip() instead of this directly");
        }
    }

    private void animateResizePip(Rect rect, Rect rect2, int i, int i2) {
        if (Looper.myLooper() != this.mUpdateHandler.getLooper()) {
            throw new RuntimeException("Callers should call scheduleAnimateResizePip() instead of this directly");
        } else if (this.mToken == null || this.mLeash == null) {
            Log.w(TAG, "Abort animation, invalid leash");
        } else {
            Handler handler = this.mUpdateHandler;
            $$Lambda$PipTaskOrganizer$UThb1C4CG3JKRXSQ8cKQ1U8MqUE r1 = new Runnable(rect, rect2, i, i2) {
                public final /* synthetic */ Rect f$1;
                public final /* synthetic */ Rect f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    PipTaskOrganizer.this.lambda$animateResizePip$2$PipTaskOrganizer(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            };
            handler.post(r1);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateResizePip$2 */
    public /* synthetic */ void lambda$animateResizePip$2$PipTaskOrganizer(Rect rect, Rect rect2, int i, int i2) {
        PipTransitionAnimator animator = this.mPipAnimationController.getAnimator(this.mLeash, rect, rect2);
        animator.setTransitionDirection(i);
        animator.setPipAnimationCallback(this.mPipAnimationCallback);
        animator.setDuration((long) i2).start();
    }

    private Size getMinimalSize(ActivityInfo activityInfo) {
        if (activityInfo != null) {
            WindowLayout windowLayout = activityInfo.windowLayout;
            if (windowLayout != null) {
                return new Size(windowLayout.minWidth, windowLayout.minHeight);
            }
        }
        return null;
    }

    private float getAspectRatioOrDefault(PictureInPictureParams pictureInPictureParams) {
        if (pictureInPictureParams == null) {
            return this.mPipBoundsHandler.getDefaultAspectRatio();
        }
        return pictureInPictureParams.getAspectRatio();
    }
}
