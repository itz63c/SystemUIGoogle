package com.android.systemui.statusbar.notification.row;

import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViews.OnClickHandler;
import android.widget.RemoteViews.OnViewAppliedListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.widget.ImageMessageConsumer;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.MediaNotificationProcessor;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.BindParams;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.InflatedSmartReplies;
import com.android.systemui.statusbar.policy.InflatedSmartReplies.SmartRepliesAndActions;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.util.Assert;
import dagger.Lazy;
import java.util.HashMap;
import java.util.concurrent.Executor;

@VisibleForTesting(visibility = Visibility.PACKAGE)
public class NotificationContentInflater implements NotificationRowContentBinder {
    private final Executor mBgExecutor;
    private final ConversationNotificationProcessor mConversationProcessor;
    private boolean mInflateSynchronously = false;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final NotifRemoteViewCache mRemoteViewCache;
    private final Lazy<SmartReplyConstants> mSmartReplyConstants;
    private final Lazy<SmartReplyController> mSmartReplyController;

    @VisibleForTesting
    static abstract class ApplyCallback {
        public abstract RemoteViews getRemoteView();

        public abstract void setResultView(View view);

        ApplyCallback() {
        }
    }

    public static class AsyncInflationTask extends AsyncTask<Void, Void, InflationProgress> implements InflationCallback, InflationTask {
        private final Executor mBgExecutor;
        private final InflationCallback mCallback;
        private CancellationSignal mCancellationSignal;
        private final Context mContext;
        private final ConversationNotificationProcessor mConversationProcessor;
        private final NotificationEntry mEntry;
        private Exception mError;
        private final boolean mInflateSynchronously;
        private final boolean mIsChildInGroup;
        private final boolean mIsLowPriority;
        private final int mReInflateFlags;
        private final NotifRemoteViewCache mRemoteViewCache;
        private OnClickHandler mRemoteViewClickHandler;
        private ExpandableNotificationRow mRow;
        private final SmartReplyConstants mSmartReplyConstants;
        private final SmartReplyController mSmartReplyController;
        private final boolean mUsesIncreasedHeadsUpHeight;
        private final boolean mUsesIncreasedHeight;

        private class RtlEnabledContext extends ContextWrapper {
            private RtlEnabledContext(AsyncInflationTask asyncInflationTask, Context context) {
                super(context);
            }

            public ApplicationInfo getApplicationInfo() {
                ApplicationInfo applicationInfo = super.getApplicationInfo();
                applicationInfo.flags |= 4194304;
                return applicationInfo;
            }
        }

        private AsyncInflationTask(Executor executor, boolean z, int i, NotifRemoteViewCache notifRemoteViewCache, NotificationEntry notificationEntry, SmartReplyConstants smartReplyConstants, SmartReplyController smartReplyController, ConversationNotificationProcessor conversationNotificationProcessor, ExpandableNotificationRow expandableNotificationRow, boolean z2, boolean z3, boolean z4, boolean z5, InflationCallback inflationCallback, OnClickHandler onClickHandler) {
            this.mEntry = notificationEntry;
            this.mRow = expandableNotificationRow;
            this.mSmartReplyConstants = smartReplyConstants;
            this.mSmartReplyController = smartReplyController;
            this.mBgExecutor = executor;
            this.mInflateSynchronously = z;
            this.mReInflateFlags = i;
            this.mRemoteViewCache = notifRemoteViewCache;
            this.mContext = expandableNotificationRow.getContext();
            this.mIsLowPriority = z2;
            this.mIsChildInGroup = z3;
            this.mUsesIncreasedHeight = z4;
            this.mUsesIncreasedHeadsUpHeight = z5;
            this.mRemoteViewClickHandler = onClickHandler;
            this.mCallback = inflationCallback;
            this.mConversationProcessor = conversationNotificationProcessor;
            notificationEntry.setInflationTask(this);
        }

        @VisibleForTesting
        public int getReInflateFlags() {
            return this.mReInflateFlags;
        }

        /* access modifiers changed from: protected */
        public InflationProgress doInBackground(Void... voidArr) {
            try {
                StatusBarNotification sbn = this.mEntry.getSbn();
                Builder recoverBuilder = Builder.recoverBuilder(this.mContext, sbn.getNotification());
                Context packageContext = sbn.getPackageContext(this.mContext);
                Context rtlEnabledContext = recoverBuilder.usesTemplate() ? new RtlEnabledContext(packageContext) : packageContext;
                Notification notification = sbn.getNotification();
                if (notification.isMediaNotification()) {
                    new MediaNotificationProcessor(this.mContext, rtlEnabledContext).processNotification(notification, recoverBuilder);
                }
                if (this.mEntry.getRanking().isConversation()) {
                    this.mConversationProcessor.processNotification(this.mEntry, recoverBuilder);
                }
                InflationProgress access$1600 = NotificationContentInflater.createRemoteViews(this.mReInflateFlags, recoverBuilder, this.mIsLowPriority, this.mIsChildInGroup, this.mUsesIncreasedHeight, this.mUsesIncreasedHeadsUpHeight, rtlEnabledContext);
                NotificationContentInflater.inflateSmartReplyViews(access$1600, this.mReInflateFlags, this.mEntry, this.mRow.getContext(), rtlEnabledContext, this.mRow.getHeadsUpManager(), this.mSmartReplyConstants, this.mSmartReplyController, this.mRow.getExistingSmartRepliesAndActions());
                return access$1600;
            } catch (Exception e) {
                this.mError = e;
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(InflationProgress inflationProgress) {
            Exception exc = this.mError;
            if (exc == null) {
                this.mCancellationSignal = NotificationContentInflater.apply(this.mBgExecutor, this.mInflateSynchronously, inflationProgress, this.mReInflateFlags, this.mRemoteViewCache, this.mEntry, this.mRow, this.mRemoteViewClickHandler, this);
                return;
            }
            handleError(exc);
        }

        private void handleError(Exception exc) {
            this.mEntry.onInflationTaskFinished();
            StatusBarNotification sbn = this.mEntry.getSbn();
            StringBuilder sb = new StringBuilder();
            sb.append(sbn.getPackageName());
            sb.append("/0x");
            sb.append(Integer.toHexString(sbn.getId()));
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append("couldn't inflate view for notification ");
            sb3.append(sb2);
            Log.e("StatusBar", sb3.toString(), exc);
            InflationCallback inflationCallback = this.mCallback;
            if (inflationCallback != null) {
                NotificationEntry entry = this.mRow.getEntry();
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Couldn't inflate contentViews");
                sb4.append(exc);
                inflationCallback.handleInflationException(entry, new InflationException(sb4.toString()));
            }
        }

        public void abort() {
            cancel(true);
            CancellationSignal cancellationSignal = this.mCancellationSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
        }

        public void handleInflationException(NotificationEntry notificationEntry, Exception exc) {
            handleError(exc);
        }

        public void onAsyncInflationFinished(NotificationEntry notificationEntry) {
            this.mEntry.onInflationTaskFinished();
            this.mRow.onNotificationUpdated();
            InflationCallback inflationCallback = this.mCallback;
            if (inflationCallback != null) {
                inflationCallback.onAsyncInflationFinished(this.mEntry);
            }
            this.mRow.getImageResolver().purgeCache();
        }
    }

    @VisibleForTesting
    static class InflationProgress {
        /* access modifiers changed from: private */
        public InflatedSmartReplies expandedInflatedSmartReplies;
        /* access modifiers changed from: private */
        public InflatedSmartReplies headsUpInflatedSmartReplies;
        /* access modifiers changed from: private */
        public CharSequence headsUpStatusBarText;
        /* access modifiers changed from: private */
        public CharSequence headsUpStatusBarTextPublic;
        /* access modifiers changed from: private */
        public View inflatedContentView;
        /* access modifiers changed from: private */
        public View inflatedExpandedView;
        /* access modifiers changed from: private */
        public View inflatedHeadsUpView;
        /* access modifiers changed from: private */
        public View inflatedPublicView;
        /* access modifiers changed from: private */
        public RemoteViews newContentView;
        /* access modifiers changed from: private */
        public RemoteViews newExpandedView;
        /* access modifiers changed from: private */
        public RemoteViews newHeadsUpView;
        /* access modifiers changed from: private */
        public RemoteViews newPublicView;
        @VisibleForTesting
        Context packageContext;

        InflationProgress() {
        }
    }

    NotificationContentInflater(NotifRemoteViewCache notifRemoteViewCache, NotificationRemoteInputManager notificationRemoteInputManager, Lazy<SmartReplyConstants> lazy, Lazy<SmartReplyController> lazy2, ConversationNotificationProcessor conversationNotificationProcessor, Executor executor) {
        this.mRemoteViewCache = notifRemoteViewCache;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mSmartReplyConstants = lazy;
        this.mSmartReplyController = lazy2;
        this.mConversationProcessor = conversationNotificationProcessor;
        this.mBgExecutor = executor;
    }

    public void bindContent(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, int i, BindParams bindParams, boolean z, InflationCallback inflationCallback) {
        BindParams bindParams2 = bindParams;
        if (!expandableNotificationRow.isRemoved()) {
            expandableNotificationRow.getImageResolver().preloadImages(notificationEntry.getSbn().getNotification());
            if (z) {
                this.mRemoteViewCache.clearCache(notificationEntry);
            } else {
                NotificationEntry notificationEntry2 = notificationEntry;
            }
            AsyncInflationTask asyncInflationTask = new AsyncInflationTask(this.mBgExecutor, this.mInflateSynchronously, i, this.mRemoteViewCache, notificationEntry, (SmartReplyConstants) this.mSmartReplyConstants.get(), (SmartReplyController) this.mSmartReplyController.get(), this.mConversationProcessor, expandableNotificationRow, bindParams2.isLowPriority, bindParams2.isChildInGroup, bindParams2.usesIncreasedHeight, bindParams2.usesIncreasedHeadsUpHeight, inflationCallback, this.mRemoteInputManager.getRemoteViewsOnClickHandler());
            if (this.mInflateSynchronously) {
                asyncInflationTask.onPostExecute(asyncInflationTask.doInBackground(new Void[0]));
            } else {
                asyncInflationTask.executeOnExecutor(this.mBgExecutor, new Void[0]);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public InflationProgress inflateNotificationViews(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, BindParams bindParams, boolean z, int i, Builder builder, Context context) {
        BindParams bindParams2 = bindParams;
        InflationProgress createRemoteViews = createRemoteViews(i, builder, bindParams2.isLowPriority, bindParams2.isChildInGroup, bindParams2.usesIncreasedHeight, bindParams2.usesIncreasedHeadsUpHeight, context);
        inflateSmartReplyViews(createRemoteViews, i, notificationEntry, expandableNotificationRow.getContext(), context, expandableNotificationRow.getHeadsUpManager(), (SmartReplyConstants) this.mSmartReplyConstants.get(), (SmartReplyController) this.mSmartReplyController.get(), expandableNotificationRow.getExistingSmartRepliesAndActions());
        apply(this.mBgExecutor, z, createRemoteViews, i, this.mRemoteViewCache, notificationEntry, expandableNotificationRow, this.mRemoteInputManager.getRemoteViewsOnClickHandler(), null);
        return createRemoteViews;
    }

    public void cancelBind(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow) {
        notificationEntry.abortTask();
    }

    public void unbindContent(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, int i) {
        int i2 = 1;
        while (i != 0) {
            if ((i & i2) != 0) {
                freeNotificationView(notificationEntry, expandableNotificationRow, i2);
            }
            i &= ~i2;
            i2 <<= 1;
        }
    }

    private void freeNotificationView(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 4) {
                    if (i == 8 && expandableNotificationRow.getPublicLayout().isContentViewInactive(0)) {
                        expandableNotificationRow.getPublicLayout().setContractedChild(null);
                        this.mRemoteViewCache.removeCachedView(notificationEntry, 8);
                    }
                } else if (expandableNotificationRow.getPrivateLayout().isContentViewInactive(2)) {
                    expandableNotificationRow.getPrivateLayout().setHeadsUpChild(null);
                    this.mRemoteViewCache.removeCachedView(notificationEntry, 4);
                    expandableNotificationRow.getPrivateLayout().setHeadsUpInflatedSmartReplies(null);
                }
            } else if (expandableNotificationRow.getPrivateLayout().isContentViewInactive(1)) {
                expandableNotificationRow.getPrivateLayout().setExpandedChild(null);
                this.mRemoteViewCache.removeCachedView(notificationEntry, 2);
            }
        } else if (expandableNotificationRow.getPrivateLayout().isContentViewInactive(0)) {
            expandableNotificationRow.getPrivateLayout().setContractedChild(null);
            this.mRemoteViewCache.removeCachedView(notificationEntry, 1);
        }
    }

    /* access modifiers changed from: private */
    public static InflationProgress inflateSmartReplyViews(InflationProgress inflationProgress, int i, NotificationEntry notificationEntry, Context context, Context context2, HeadsUpManager headsUpManager, SmartReplyConstants smartReplyConstants, SmartReplyController smartReplyController, SmartRepliesAndActions smartRepliesAndActions) {
        if (!((i & 2) == 0 || inflationProgress.newExpandedView == null)) {
            inflationProgress.expandedInflatedSmartReplies = InflatedSmartReplies.inflate(context, context2, notificationEntry, smartReplyConstants, smartReplyController, headsUpManager, smartRepliesAndActions);
        }
        if (!((i & 4) == 0 || inflationProgress.newHeadsUpView == null)) {
            inflationProgress.headsUpInflatedSmartReplies = InflatedSmartReplies.inflate(context, context2, notificationEntry, smartReplyConstants, smartReplyController, headsUpManager, smartRepliesAndActions);
        }
        return inflationProgress;
    }

    /* access modifiers changed from: private */
    public static InflationProgress createRemoteViews(int i, Builder builder, boolean z, boolean z2, boolean z3, boolean z4, Context context) {
        InflationProgress inflationProgress = new InflationProgress();
        boolean z5 = z && !z2;
        if ((i & 1) != 0) {
            inflationProgress.newContentView = createContentView(builder, z5, z3);
        }
        if ((i & 2) != 0) {
            inflationProgress.newExpandedView = createExpandedView(builder, z5);
        }
        if ((i & 4) != 0) {
            inflationProgress.newHeadsUpView = builder.createHeadsUpContentView(z4);
        }
        if ((i & 8) != 0) {
            inflationProgress.newPublicView = builder.makePublicContentView(z5);
        }
        inflationProgress.packageContext = context;
        inflationProgress.headsUpStatusBarText = builder.getHeadsUpStatusBarText(false);
        inflationProgress.headsUpStatusBarTextPublic = builder.getHeadsUpStatusBarText(true);
        return inflationProgress;
    }

    /* access modifiers changed from: private */
    public static CancellationSignal apply(Executor executor, boolean z, InflationProgress inflationProgress, int i, NotifRemoteViewCache notifRemoteViewCache, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, OnClickHandler onClickHandler, InflationCallback inflationCallback) {
        NotificationContentView notificationContentView;
        NotificationContentView notificationContentView2;
        HashMap hashMap;
        final InflationProgress inflationProgress2 = inflationProgress;
        NotifRemoteViewCache notifRemoteViewCache2 = notifRemoteViewCache;
        NotificationEntry notificationEntry2 = notificationEntry;
        NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
        NotificationContentView publicLayout = expandableNotificationRow.getPublicLayout();
        HashMap hashMap2 = new HashMap();
        if ((i & 1) != 0) {
            boolean z2 = !canReapplyRemoteView(inflationProgress.newContentView, notifRemoteViewCache2.getCachedView(notificationEntry2, 1));
            C12781 r7 = new ApplyCallback() {
                public void setResultView(View view) {
                    InflationProgress.this.inflatedContentView = view;
                }

                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newContentView;
                }
            };
            hashMap = hashMap2;
            notificationContentView2 = publicLayout;
            notificationContentView = privateLayout;
            applyRemoteView(executor, z, inflationProgress, i, 1, notifRemoteViewCache, notificationEntry, expandableNotificationRow, z2, onClickHandler, inflationCallback, privateLayout, privateLayout.getContractedChild(), privateLayout.getVisibleWrapper(0), hashMap, r7);
        } else {
            hashMap = hashMap2;
            notificationContentView2 = publicLayout;
            notificationContentView = privateLayout;
        }
        if ((i & 2) != 0 && inflationProgress.newExpandedView != null) {
            boolean z3 = !canReapplyRemoteView(inflationProgress.newExpandedView, notifRemoteViewCache.getCachedView(notificationEntry, 2));
            final InflationProgress inflationProgress3 = inflationProgress;
            C12792 r11 = new ApplyCallback() {
                public void setResultView(View view) {
                    InflationProgress.this.inflatedExpandedView = view;
                }

                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newExpandedView;
                }
            };
            applyRemoteView(executor, z, inflationProgress, i, 2, notifRemoteViewCache, notificationEntry, expandableNotificationRow, z3, onClickHandler, inflationCallback, notificationContentView, notificationContentView.getExpandedChild(), notificationContentView.getVisibleWrapper(1), hashMap, r11);
        }
        if (!((i & 4) == 0 || inflationProgress.newHeadsUpView == null)) {
            boolean z4 = !canReapplyRemoteView(inflationProgress.newHeadsUpView, notifRemoteViewCache.getCachedView(notificationEntry, 4));
            final InflationProgress inflationProgress4 = inflationProgress;
            C12803 r13 = new ApplyCallback() {
                public void setResultView(View view) {
                    InflationProgress.this.inflatedHeadsUpView = view;
                }

                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newHeadsUpView;
                }
            };
            NotificationContentView notificationContentView3 = notificationContentView;
            applyRemoteView(executor, z, inflationProgress, i, 4, notifRemoteViewCache, notificationEntry, expandableNotificationRow, z4, onClickHandler, inflationCallback, notificationContentView3, notificationContentView.getHeadsUpChild(), notificationContentView3.getVisibleWrapper(2), hashMap, r13);
        }
        if ((i & 8) != 0) {
            boolean z5 = !canReapplyRemoteView(inflationProgress.newPublicView, notifRemoteViewCache.getCachedView(notificationEntry, 8));
            final InflationProgress inflationProgress5 = inflationProgress;
            C12814 r132 = new ApplyCallback() {
                public void setResultView(View view) {
                    InflationProgress.this.inflatedPublicView = view;
                }

                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newPublicView;
                }
            };
            NotificationContentView notificationContentView4 = notificationContentView2;
            applyRemoteView(executor, z, inflationProgress, i, 8, notifRemoteViewCache, notificationEntry, expandableNotificationRow, z5, onClickHandler, inflationCallback, notificationContentView4, notificationContentView2.getContractedChild(), notificationContentView4.getVisibleWrapper(0), hashMap, r132);
        }
        finishIfDone(inflationProgress, i, notifRemoteViewCache, hashMap, inflationCallback, notificationEntry, expandableNotificationRow);
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new OnCancelListener(hashMap) {
            public final /* synthetic */ HashMap f$0;

            {
                this.f$0 = r1;
            }

            public final void onCancel() {
                this.f$0.values().forEach($$Lambda$POlPJz26zF5Nt5Z2kVGSqFxN8Co.INSTANCE);
            }
        });
        return cancellationSignal;
    }

    @VisibleForTesting
    static void applyRemoteView(Executor executor, boolean z, InflationProgress inflationProgress, int i, int i2, NotifRemoteViewCache notifRemoteViewCache, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, boolean z2, OnClickHandler onClickHandler, InflationCallback inflationCallback, NotificationContentView notificationContentView, View view, NotificationViewWrapper notificationViewWrapper, HashMap<Integer, CancellationSignal> hashMap, ApplyCallback applyCallback) {
        CancellationSignal cancellationSignal;
        InflationProgress inflationProgress2 = inflationProgress;
        OnClickHandler onClickHandler2 = onClickHandler;
        HashMap<Integer, CancellationSignal> hashMap2 = hashMap;
        RemoteViews remoteView = applyCallback.getRemoteView();
        if (z) {
            if (z2) {
                try {
                    View apply = remoteView.apply(inflationProgress2.packageContext, notificationContentView, onClickHandler2);
                    apply.setIsRootNamespace(true);
                    applyCallback.setResultView(apply);
                } catch (Exception e) {
                    handleInflationError(hashMap2, e, expandableNotificationRow.getEntry(), inflationCallback);
                    hashMap2.put(Integer.valueOf(i2), new CancellationSignal());
                }
            } else {
                remoteView.reapply(inflationProgress2.packageContext, view, onClickHandler2);
                notificationViewWrapper.onReinflated();
            }
            return;
        }
        InflationCallback inflationCallback2 = inflationCallback;
        NotificationContentView notificationContentView2 = notificationContentView;
        View view2 = view;
        final ApplyCallback applyCallback2 = applyCallback;
        final ExpandableNotificationRow expandableNotificationRow2 = expandableNotificationRow;
        final boolean z3 = z2;
        final NotificationViewWrapper notificationViewWrapper2 = notificationViewWrapper;
        final HashMap<Integer, CancellationSignal> hashMap3 = hashMap;
        final int i3 = i2;
        final InflationProgress inflationProgress3 = inflationProgress;
        final int i4 = i;
        final NotifRemoteViewCache notifRemoteViewCache2 = notifRemoteViewCache;
        final InflationCallback inflationCallback3 = inflationCallback;
        final NotificationEntry notificationEntry2 = notificationEntry;
        RemoteViews remoteViews = remoteView;
        final View view3 = view;
        final RemoteViews remoteViews2 = remoteViews;
        final NotificationContentView notificationContentView3 = notificationContentView;
        final OnClickHandler onClickHandler3 = onClickHandler;
        C12825 r1 = new OnViewAppliedListener() {
            public void onViewInflated(View view) {
                if (view instanceof ImageMessageConsumer) {
                    ((ImageMessageConsumer) view).setImageResolver(ExpandableNotificationRow.this.getImageResolver());
                }
            }

            public void onViewApplied(View view) {
                if (z3) {
                    view.setIsRootNamespace(true);
                    applyCallback2.setResultView(view);
                } else {
                    NotificationViewWrapper notificationViewWrapper = notificationViewWrapper2;
                    if (notificationViewWrapper != null) {
                        notificationViewWrapper.onReinflated();
                    }
                }
                hashMap3.remove(Integer.valueOf(i3));
                NotificationContentInflater.finishIfDone(inflationProgress3, i4, notifRemoteViewCache2, hashMap3, inflationCallback3, notificationEntry2, ExpandableNotificationRow.this);
            }

            public void onError(Exception exc) {
                try {
                    View view = view3;
                    if (z3) {
                        view = remoteViews2.apply(inflationProgress3.packageContext, notificationContentView3, onClickHandler3);
                    } else {
                        remoteViews2.reapply(inflationProgress3.packageContext, view3, onClickHandler3);
                    }
                    Log.wtf("NotifContentInflater", "Async Inflation failed but normal inflation finished normally.", exc);
                    onViewApplied(view);
                } catch (Exception unused) {
                    hashMap3.remove(Integer.valueOf(i3));
                    NotificationContentInflater.handleInflationError(hashMap3, exc, ExpandableNotificationRow.this.getEntry(), inflationCallback3);
                }
            }
        };
        if (z2) {
            cancellationSignal = remoteViews.applyAsync(inflationProgress2.packageContext, notificationContentView, executor, r1, onClickHandler);
        } else {
            cancellationSignal = remoteViews.reapplyAsync(inflationProgress2.packageContext, view, executor, r1, onClickHandler);
        }
        hashMap.put(Integer.valueOf(i2), cancellationSignal);
    }

    /* access modifiers changed from: private */
    public static void handleInflationError(HashMap<Integer, CancellationSignal> hashMap, Exception exc, NotificationEntry notificationEntry, InflationCallback inflationCallback) {
        Assert.isMainThread();
        hashMap.values().forEach($$Lambda$POlPJz26zF5Nt5Z2kVGSqFxN8Co.INSTANCE);
        if (inflationCallback != null) {
            inflationCallback.handleInflationException(notificationEntry, exc);
        }
    }

    /* access modifiers changed from: private */
    public static boolean finishIfDone(InflationProgress inflationProgress, int i, NotifRemoteViewCache notifRemoteViewCache, HashMap<Integer, CancellationSignal> hashMap, InflationCallback inflationCallback, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow) {
        Assert.isMainThread();
        NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
        NotificationContentView publicLayout = expandableNotificationRow.getPublicLayout();
        boolean z = false;
        if (!hashMap.isEmpty()) {
            return false;
        }
        if ((i & 1) != 0) {
            if (inflationProgress.inflatedContentView != null) {
                privateLayout.setContractedChild(inflationProgress.inflatedContentView);
                notifRemoteViewCache.putCachedView(notificationEntry, 1, inflationProgress.newContentView);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 1)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 1, inflationProgress.newContentView);
            }
        }
        if ((i & 2) != 0) {
            if (inflationProgress.inflatedExpandedView != null) {
                privateLayout.setExpandedChild(inflationProgress.inflatedExpandedView);
                notifRemoteViewCache.putCachedView(notificationEntry, 2, inflationProgress.newExpandedView);
            } else if (inflationProgress.newExpandedView == null) {
                privateLayout.setExpandedChild(null);
                notifRemoteViewCache.removeCachedView(notificationEntry, 2);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 2)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 2, inflationProgress.newExpandedView);
            }
            if (inflationProgress.newExpandedView != null) {
                privateLayout.setExpandedInflatedSmartReplies(inflationProgress.expandedInflatedSmartReplies);
            } else {
                privateLayout.setExpandedInflatedSmartReplies(null);
            }
            if (inflationProgress.newExpandedView != null) {
                z = true;
            }
            expandableNotificationRow.setExpandable(z);
        }
        if ((i & 4) != 0) {
            if (inflationProgress.inflatedHeadsUpView != null) {
                privateLayout.setHeadsUpChild(inflationProgress.inflatedHeadsUpView);
                notifRemoteViewCache.putCachedView(notificationEntry, 4, inflationProgress.newHeadsUpView);
            } else if (inflationProgress.newHeadsUpView == null) {
                privateLayout.setHeadsUpChild(null);
                notifRemoteViewCache.removeCachedView(notificationEntry, 4);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 4)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 4, inflationProgress.newHeadsUpView);
            }
            if (inflationProgress.newHeadsUpView != null) {
                privateLayout.setHeadsUpInflatedSmartReplies(inflationProgress.headsUpInflatedSmartReplies);
            } else {
                privateLayout.setHeadsUpInflatedSmartReplies(null);
            }
        }
        if ((i & 8) != 0) {
            if (inflationProgress.inflatedPublicView != null) {
                publicLayout.setContractedChild(inflationProgress.inflatedPublicView);
                notifRemoteViewCache.putCachedView(notificationEntry, 8, inflationProgress.newPublicView);
            } else if (notifRemoteViewCache.hasCachedView(notificationEntry, 8)) {
                notifRemoteViewCache.putCachedView(notificationEntry, 8, inflationProgress.newPublicView);
            }
        }
        notificationEntry.headsUpStatusBarText = inflationProgress.headsUpStatusBarText;
        notificationEntry.headsUpStatusBarTextPublic = inflationProgress.headsUpStatusBarTextPublic;
        if (inflationCallback != null) {
            inflationCallback.onAsyncInflationFinished(notificationEntry);
        }
        return true;
    }

    private static RemoteViews createExpandedView(Builder builder, boolean z) {
        RemoteViews createBigContentView = builder.createBigContentView();
        if (createBigContentView != null) {
            return createBigContentView;
        }
        if (!z) {
            return null;
        }
        RemoteViews createContentView = builder.createContentView();
        Builder.makeHeaderExpanded(createContentView);
        return createContentView;
    }

    private static RemoteViews createContentView(Builder builder, boolean z, boolean z2) {
        if (z) {
            return builder.makeLowPriorityContentView(false);
        }
        return builder.createContentView(z2);
    }

    @VisibleForTesting
    static boolean canReapplyRemoteView(RemoteViews remoteViews, RemoteViews remoteViews2) {
        if (remoteViews == null && remoteViews2 == null) {
            return true;
        }
        if (remoteViews == null || remoteViews2 == null || remoteViews2.getPackage() == null || remoteViews.getPackage() == null || !remoteViews.getPackage().equals(remoteViews2.getPackage()) || remoteViews.getLayoutId() != remoteViews2.getLayoutId() || remoteViews2.hasFlags(1)) {
            return false;
        }
        return true;
    }

    @VisibleForTesting
    public void setInflateSynchronously(boolean z) {
        this.mInflateSynchronously = z;
    }
}
