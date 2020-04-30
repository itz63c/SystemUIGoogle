package com.android.systemui.statusbar.notification;

import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import androidx.collection.ArraySet;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class VisualStabilityManager implements OnHeadsUpChangedListener, Dumpable {
    private ArraySet<View> mAddedChildren = new ArraySet<>();
    private ArraySet<View> mAllowedReorderViews = new ArraySet<>();
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    private final Handler mHandler;
    private boolean mIsTemporaryReorderingAllowed;
    /* access modifiers changed from: private */
    public ArraySet<NotificationEntry> mLowPriorityReorderingViews = new ArraySet<>();
    private final Runnable mOnTemporaryReorderingExpired = new Runnable() {
        public final void run() {
            VisualStabilityManager.this.lambda$new$0$VisualStabilityManager();
        }
    };
    private boolean mPanelExpanded;
    private boolean mPulsing;
    private boolean mReorderingAllowed;
    private boolean mScreenOn;
    private long mTemporaryReorderingStart;
    private VisibilityLocationProvider mVisibilityLocationProvider;

    public interface Callback {
        void onReorderingAllowed();
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter) {
    }

    public VisualStabilityManager(NotificationEntryManager notificationEntryManager, Handler handler) {
        this.mHandler = handler;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                if (notificationEntry.isAmbient() != notificationEntry.getRow().isLowPriority()) {
                    VisualStabilityManager.this.mLowPriorityReorderingViews.add(notificationEntry);
                }
            }
        });
    }

    public void addReorderingAllowedCallback(Callback callback) {
        if (!this.mCallbacks.contains(callback)) {
            this.mCallbacks.add(callback);
        }
    }

    public void setPanelExpanded(boolean z) {
        this.mPanelExpanded = z;
        updateReorderingAllowed();
    }

    public void setScreenOn(boolean z) {
        this.mScreenOn = z;
        updateReorderingAllowed();
    }

    public void setPulsing(boolean z) {
        if (this.mPulsing != z) {
            this.mPulsing = z;
            updateReorderingAllowed();
        }
    }

    private void updateReorderingAllowed() {
        boolean z = true;
        boolean z2 = (!this.mScreenOn || !this.mPanelExpanded || this.mIsTemporaryReorderingAllowed) && !this.mPulsing;
        if (!z2 || this.mReorderingAllowed) {
            z = false;
        }
        this.mReorderingAllowed = z2;
        if (z) {
            notifyCallbacks();
        }
    }

    private void notifyCallbacks() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            ((Callback) this.mCallbacks.get(i)).onReorderingAllowed();
        }
        this.mCallbacks.clear();
    }

    public boolean isReorderingAllowed() {
        return this.mReorderingAllowed;
    }

    public boolean canReorderNotification(ExpandableNotificationRow expandableNotificationRow) {
        if (this.mReorderingAllowed || this.mAddedChildren.contains(expandableNotificationRow) || this.mLowPriorityReorderingViews.contains(expandableNotificationRow.getEntry())) {
            return true;
        }
        if (!this.mAllowedReorderViews.contains(expandableNotificationRow) || this.mVisibilityLocationProvider.isInVisibleLocation(expandableNotificationRow.getEntry())) {
            return false;
        }
        return true;
    }

    public void setVisibilityLocationProvider(VisibilityLocationProvider visibilityLocationProvider) {
        this.mVisibilityLocationProvider = visibilityLocationProvider;
    }

    public void onReorderingFinished() {
        this.mAllowedReorderViews.clear();
        this.mAddedChildren.clear();
        this.mLowPriorityReorderingViews.clear();
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        if (z) {
            this.mAllowedReorderViews.add(notificationEntry.getRow());
        }
    }

    public void temporarilyAllowReordering() {
        this.mHandler.removeCallbacks(this.mOnTemporaryReorderingExpired);
        this.mHandler.postDelayed(this.mOnTemporaryReorderingExpired, 1000);
        if (!this.mIsTemporaryReorderingAllowed) {
            this.mTemporaryReorderingStart = SystemClock.elapsedRealtime();
        }
        this.mIsTemporaryReorderingAllowed = true;
        updateReorderingAllowed();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VisualStabilityManager() {
        this.mIsTemporaryReorderingAllowed = false;
        updateReorderingAllowed();
    }

    public void notifyViewAddition(View view) {
        this.mAddedChildren.add(view);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("VisualStabilityManager state:");
        printWriter.print("  mIsTemporaryReorderingAllowed=");
        printWriter.println(this.mIsTemporaryReorderingAllowed);
        printWriter.print("  mTemporaryReorderingStart=");
        printWriter.println(this.mTemporaryReorderingStart);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        printWriter.print("    Temporary reordering window has been open for ");
        printWriter.print(elapsedRealtime - (this.mIsTemporaryReorderingAllowed ? this.mTemporaryReorderingStart : elapsedRealtime));
        printWriter.println("ms");
        printWriter.println();
    }
}
