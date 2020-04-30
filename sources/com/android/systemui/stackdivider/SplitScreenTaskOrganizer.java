package com.android.systemui.stackdivider;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ITaskOrganizerController;
import android.os.RemoteException;
import android.view.ITaskOrganizer.Stub;
import android.view.SurfaceControl;
import android.view.SurfaceControl.Builder;
import android.view.SurfaceControl.Transaction;
import android.view.SurfaceSession;

class SplitScreenTaskOrganizer extends Stub {
    final Divider mDivider;
    RunningTaskInfo mPrimary;
    SurfaceControl mPrimaryDim;
    SurfaceControl mPrimarySurface;
    RunningTaskInfo mSecondary;
    SurfaceControl mSecondaryDim;
    SurfaceControl mSecondarySurface;

    public void taskAppeared(RunningTaskInfo runningTaskInfo) {
    }

    public void taskVanished(RunningTaskInfo runningTaskInfo) {
    }

    public void transactionReady(int i, Transaction transaction) {
    }

    SplitScreenTaskOrganizer(Divider divider) {
        this.mDivider = divider;
    }

    /* access modifiers changed from: 0000 */
    public void init(ITaskOrganizerController iTaskOrganizerController, SurfaceSession surfaceSession) throws RemoteException {
        iTaskOrganizerController.registerTaskOrganizer(this, 3);
        iTaskOrganizerController.registerTaskOrganizer(this, 4);
        this.mPrimary = iTaskOrganizerController.createRootTask(0, 3);
        this.mSecondary = iTaskOrganizerController.createRootTask(0, 4);
        this.mPrimarySurface = this.mPrimary.token.getLeash();
        this.mSecondarySurface = this.mSecondary.token.getLeash();
        this.mPrimaryDim = new Builder(surfaceSession).setParent(this.mPrimarySurface).setColorLayer().setName("Primary Divider Dim").build();
        this.mSecondaryDim = new Builder(surfaceSession).setParent(this.mSecondarySurface).setColorLayer().setName("Secondary Divider Dim").build();
        Transaction transaction = getTransaction();
        transaction.setLayer(this.mPrimaryDim, Integer.MAX_VALUE);
        transaction.setColor(this.mPrimaryDim, new float[]{0.0f, 0.0f, 0.0f});
        transaction.setLayer(this.mSecondaryDim, Integer.MAX_VALUE);
        transaction.setColor(this.mSecondaryDim, new float[]{0.0f, 0.0f, 0.0f});
        transaction.apply();
        releaseTransaction(transaction);
    }

    /* access modifiers changed from: 0000 */
    public Transaction getTransaction() {
        return this.mDivider.mTransactionPool.acquire();
    }

    /* access modifiers changed from: 0000 */
    public void releaseTransaction(Transaction transaction) {
        this.mDivider.mTransactionPool.release(transaction);
    }

    public void onTaskInfoChanged(RunningTaskInfo runningTaskInfo) {
        if (runningTaskInfo.displayId == 0) {
            this.mDivider.getHandler().post(new Runnable(runningTaskInfo) {
                public final /* synthetic */ RunningTaskInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SplitScreenTaskOrganizer.this.lambda$onTaskInfoChanged$0$SplitScreenTaskOrganizer(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: handleTaskInfoChanged */
    public void lambda$onTaskInfoChanged$0(RunningTaskInfo runningTaskInfo) {
        boolean z = this.mPrimary.topActivityType == 0;
        boolean z2 = this.mSecondary.topActivityType == 0;
        if (runningTaskInfo.token.asBinder() == this.mPrimary.token.asBinder()) {
            this.mPrimary = runningTaskInfo;
        } else if (runningTaskInfo.token.asBinder() == this.mSecondary.token.asBinder()) {
            this.mSecondary = runningTaskInfo;
        }
        boolean z3 = this.mPrimary.topActivityType == 0;
        boolean z4 = this.mSecondary.topActivityType == 0;
        if (!z3 && !z4) {
            int i = this.mSecondary.topActivityType;
            if (i == 2 || i == 3) {
                this.mDivider.ensureMinimizedSplit();
            } else {
                this.mDivider.ensureNormalSplit();
            }
        } else if (this.mDivider.inSplitMode()) {
            WindowManagerProxy.applyDismissSplit(this, true);
            this.mDivider.updateVisibility(false);
        } else if (!z3 && z && z2) {
            this.mDivider.startEnterSplit();
        }
    }
}
