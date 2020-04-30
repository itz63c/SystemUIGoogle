package com.android.systemui.statusbar.notification.row;

import android.util.ArrayMap;
import android.util.ArraySet;
import androidx.core.p002os.CancellationSignal;
import androidx.core.p002os.CancellationSignal.OnCancelListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.row.BindRequester.BindRequestListener;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline.BindCallback;
import java.util.Map;
import java.util.Set;

public final class NotifBindPipeline {
    /* access modifiers changed from: private */
    public final Map<NotificationEntry, BindEntry> mBindEntries = new ArrayMap();
    private final NotifCollectionListener mCollectionListener;
    private final NotifBindPipelineLogger mLogger;
    /* access modifiers changed from: private */
    public BindStage mStage;

    public interface BindCallback {
        void onBindFinished(NotificationEntry notificationEntry);
    }

    private class BindEntry {
        public final Set<BindCallback> callbacks;
        public boolean invalidated;
        public ExpandableNotificationRow row;

        private BindEntry(NotifBindPipeline notifBindPipeline) {
            this.callbacks = new ArraySet();
        }
    }

    NotifBindPipeline(CommonNotifCollection commonNotifCollection, NotifBindPipelineLogger notifBindPipelineLogger) {
        C12761 r0 = new NotifCollectionListener() {
            public void onEntryInit(NotificationEntry notificationEntry) {
                NotifBindPipeline.this.mBindEntries.put(notificationEntry, new BindEntry());
                NotifBindPipeline.this.mStage.createStageParams(notificationEntry);
            }

            public void onEntryCleanUp(NotificationEntry notificationEntry) {
                ExpandableNotificationRow expandableNotificationRow = ((BindEntry) NotifBindPipeline.this.mBindEntries.remove(notificationEntry)).row;
                if (expandableNotificationRow != null) {
                    NotifBindPipeline.this.mStage.abortStage(notificationEntry, expandableNotificationRow);
                }
                NotifBindPipeline.this.mStage.deleteStageParams(notificationEntry);
            }
        };
        this.mCollectionListener = r0;
        commonNotifCollection.addCollectionListener(r0);
        this.mLogger = notifBindPipelineLogger;
    }

    public void setStage(BindStage bindStage) {
        this.mLogger.logStageSet(bindStage.getClass().getName());
        this.mStage = bindStage;
        bindStage.setBindRequestListener(new BindRequestListener() {
            public final void onBindRequest(NotificationEntry notificationEntry, CancellationSignal cancellationSignal, BindCallback bindCallback) {
                NotifBindPipeline.this.onBindRequested(notificationEntry, cancellationSignal, bindCallback);
            }
        });
    }

    public void manageRow(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow) {
        this.mLogger.logManagedRow(notificationEntry.getKey());
        BindEntry bindEntry = getBindEntry(notificationEntry);
        bindEntry.row = expandableNotificationRow;
        if (bindEntry.invalidated) {
            startPipeline(notificationEntry);
        }
    }

    /* access modifiers changed from: private */
    public void onBindRequested(NotificationEntry notificationEntry, CancellationSignal cancellationSignal, BindCallback bindCallback) {
        BindEntry bindEntry = getBindEntry(notificationEntry);
        if (bindEntry != null) {
            bindEntry.invalidated = true;
            if (bindCallback != null) {
                Set<BindCallback> set = bindEntry.callbacks;
                set.add(bindCallback);
                cancellationSignal.setOnCancelListener(new OnCancelListener(set, bindCallback) {
                    public final /* synthetic */ Set f$0;
                    public final /* synthetic */ BindCallback f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void onCancel() {
                        this.f$0.remove(this.f$1);
                    }
                });
            }
            startPipeline(notificationEntry);
        }
    }

    private void startPipeline(NotificationEntry notificationEntry) {
        this.mLogger.logStartPipeline(notificationEntry.getKey());
        if (this.mStage != null) {
            ExpandableNotificationRow expandableNotificationRow = ((BindEntry) this.mBindEntries.get(notificationEntry)).row;
            if (expandableNotificationRow != null) {
                this.mStage.abortStage(notificationEntry, expandableNotificationRow);
                this.mStage.executeStage(notificationEntry, expandableNotificationRow, new StageCallback() {
                    public final void onStageFinished(NotificationEntry notificationEntry) {
                        NotifBindPipeline.this.lambda$startPipeline$1$NotifBindPipeline(notificationEntry);
                    }
                });
                return;
            }
            return;
        }
        throw new IllegalStateException("No stage was ever set on the pipeline");
    }

    /* access modifiers changed from: private */
    /* renamed from: onPipelineComplete */
    public void lambda$startPipeline$1(NotificationEntry notificationEntry) {
        BindEntry bindEntry = getBindEntry(notificationEntry);
        Set<BindCallback> set = bindEntry.callbacks;
        this.mLogger.logFinishedPipeline(notificationEntry.getKey(), set.size());
        bindEntry.invalidated = false;
        for (BindCallback onBindFinished : set) {
            onBindFinished.onBindFinished(notificationEntry);
        }
        set.clear();
    }

    private BindEntry getBindEntry(NotificationEntry notificationEntry) {
        BindEntry bindEntry = (BindEntry) this.mBindEntries.get(notificationEntry);
        if (bindEntry != null) {
            return bindEntry;
        }
        throw new IllegalStateException(String.format("Attempting bind on an inactive notification. key: %s", new Object[]{notificationEntry.getKey()}));
    }
}
