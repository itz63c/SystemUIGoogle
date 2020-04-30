package com.android.systemui.statusbar.notification.logging;

import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.statusbar.NotificationVisibility.NotificationLocation;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger.ExpansionStateLogger;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class NotificationLogger implements StateListener {
    protected IStatusBarService mBarService;
    /* access modifiers changed from: private */
    public final ArraySet<NotificationVisibility> mCurrentlyVisibleNotifications = new ArraySet<>();
    private boolean mDozing;
    private final Object mDozingLock = new Object();
    /* access modifiers changed from: private */
    public final NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public final ExpansionStateLogger mExpansionStateLogger;
    protected Handler mHandler = new Handler();
    private HeadsUpManager mHeadsUpManager;
    /* access modifiers changed from: private */
    public long mLastVisibilityReportUptimeMs;
    /* access modifiers changed from: private */
    public NotificationListContainer mListContainer;
    private final NotificationListenerService mNotificationListener;
    protected final OnChildLocationsChangedListener mNotificationLocationsChangedListener = new OnChildLocationsChangedListener() {
        public void onChildLocationsChanged() {
            NotificationLogger notificationLogger = NotificationLogger.this;
            if (!notificationLogger.mHandler.hasCallbacks(notificationLogger.mVisibilityReporter)) {
                long access$000 = NotificationLogger.this.mLastVisibilityReportUptimeMs + 500;
                NotificationLogger notificationLogger2 = NotificationLogger.this;
                notificationLogger2.mHandler.postAtTime(notificationLogger2.mVisibilityReporter, access$000);
            }
        }
    };
    private final NotificationPanelLogger mNotificationPanelLogger;
    private final Executor mUiBgExecutor;
    protected Runnable mVisibilityReporter = new Runnable() {
        private final ArraySet<NotificationVisibility> mTmpCurrentlyVisibleNotifications = new ArraySet<>();
        private final ArraySet<NotificationVisibility> mTmpNewlyVisibleNotifications = new ArraySet<>();
        private final ArraySet<NotificationVisibility> mTmpNoLongerVisibleNotifications = new ArraySet<>();

        public void run() {
            NotificationLogger.this.mLastVisibilityReportUptimeMs = SystemClock.uptimeMillis();
            List visibleNotifications = NotificationLogger.this.mEntryManager.getVisibleNotifications();
            int size = visibleNotifications.size();
            for (int i = 0; i < size; i++) {
                NotificationEntry notificationEntry = (NotificationEntry) visibleNotifications.get(i);
                String key = notificationEntry.getSbn().getKey();
                boolean isInVisibleLocation = NotificationLogger.this.mListContainer.isInVisibleLocation(notificationEntry);
                NotificationVisibility obtain = NotificationVisibility.obtain(key, i, size, isInVisibleLocation, NotificationLogger.getNotificationLocation(notificationEntry));
                boolean contains = NotificationLogger.this.mCurrentlyVisibleNotifications.contains(obtain);
                if (isInVisibleLocation) {
                    this.mTmpCurrentlyVisibleNotifications.add(obtain);
                    if (!contains) {
                        this.mTmpNewlyVisibleNotifications.add(obtain);
                    }
                } else {
                    obtain.recycle();
                }
            }
            this.mTmpNoLongerVisibleNotifications.addAll(NotificationLogger.this.mCurrentlyVisibleNotifications);
            this.mTmpNoLongerVisibleNotifications.removeAll(this.mTmpCurrentlyVisibleNotifications);
            NotificationLogger.this.logNotificationVisibilityChanges(this.mTmpNewlyVisibleNotifications, this.mTmpNoLongerVisibleNotifications);
            NotificationLogger notificationLogger = NotificationLogger.this;
            notificationLogger.recycleAllVisibilityObjects(notificationLogger.mCurrentlyVisibleNotifications);
            NotificationLogger.this.mCurrentlyVisibleNotifications.addAll(this.mTmpCurrentlyVisibleNotifications);
            ExpansionStateLogger access$600 = NotificationLogger.this.mExpansionStateLogger;
            ArraySet<NotificationVisibility> arraySet = this.mTmpCurrentlyVisibleNotifications;
            access$600.onVisibilityChanged(arraySet, arraySet);
            NotificationLogger.this.recycleAllVisibilityObjects(this.mTmpNoLongerVisibleNotifications);
            this.mTmpCurrentlyVisibleNotifications.clear();
            this.mTmpNewlyVisibleNotifications.clear();
            this.mTmpNoLongerVisibleNotifications.clear();
        }
    };

    public static class ExpansionStateLogger {
        @VisibleForTesting
        IStatusBarService mBarService;
        private final Map<String, State> mExpansionStates = new ArrayMap();
        private final Map<String, Boolean> mLoggedExpansionState = new ArrayMap();
        private final Executor mUiBgExecutor;

        private static class State {
            Boolean mIsExpanded;
            Boolean mIsUserAction;
            Boolean mIsVisible;
            NotificationLocation mLocation;

            private State() {
            }

            private State(State state) {
                this.mIsUserAction = state.mIsUserAction;
                this.mIsExpanded = state.mIsExpanded;
                this.mIsVisible = state.mIsVisible;
                this.mLocation = state.mLocation;
            }

            /* access modifiers changed from: private */
            public boolean isFullySet() {
                return (this.mIsUserAction == null || this.mIsExpanded == null || this.mIsVisible == null || this.mLocation == null) ? false : true;
            }
        }

        public ExpansionStateLogger(Executor executor) {
            this.mUiBgExecutor = executor;
            this.mBarService = Stub.asInterface(ServiceManager.getService("statusbar"));
        }

        /* access modifiers changed from: 0000 */
        @VisibleForTesting
        public void onExpansionChanged(String str, boolean z, boolean z2, NotificationLocation notificationLocation) {
            State state = getState(str);
            state.mIsUserAction = Boolean.valueOf(z);
            state.mIsExpanded = Boolean.valueOf(z2);
            state.mLocation = notificationLocation;
            maybeNotifyOnNotificationExpansionChanged(str, state);
        }

        /* access modifiers changed from: 0000 */
        @VisibleForTesting
        public void onVisibilityChanged(Collection<NotificationVisibility> collection, Collection<NotificationVisibility> collection2) {
            NotificationVisibility[] access$900 = NotificationLogger.cloneVisibilitiesAsArr(collection);
            NotificationVisibility[] access$9002 = NotificationLogger.cloneVisibilitiesAsArr(collection2);
            for (NotificationVisibility notificationVisibility : access$900) {
                State state = getState(notificationVisibility.key);
                state.mIsVisible = Boolean.TRUE;
                state.mLocation = notificationVisibility.location;
                maybeNotifyOnNotificationExpansionChanged(notificationVisibility.key, state);
            }
            for (NotificationVisibility notificationVisibility2 : access$9002) {
                getState(notificationVisibility2.key).mIsVisible = Boolean.FALSE;
            }
        }

        /* access modifiers changed from: 0000 */
        @VisibleForTesting
        public void onEntryRemoved(String str) {
            this.mExpansionStates.remove(str);
            this.mLoggedExpansionState.remove(str);
        }

        /* access modifiers changed from: 0000 */
        @VisibleForTesting
        public void onEntryUpdated(String str) {
            this.mLoggedExpansionState.remove(str);
        }

        private State getState(String str) {
            State state = (State) this.mExpansionStates.get(str);
            if (state != null) {
                return state;
            }
            State state2 = new State();
            this.mExpansionStates.put(str, state2);
            return state2;
        }

        private void maybeNotifyOnNotificationExpansionChanged(String str, State state) {
            if (state.isFullySet() && state.mIsVisible.booleanValue()) {
                Boolean bool = (Boolean) this.mLoggedExpansionState.get(str);
                if (bool == null && !state.mIsExpanded.booleanValue()) {
                    return;
                }
                if (bool == null || state.mIsExpanded != bool) {
                    this.mLoggedExpansionState.put(str, state.mIsExpanded);
                    this.mUiBgExecutor.execute(new Runnable(str, new State(state)) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ State f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            ExpansionStateLogger.this.mo15308x167b4926(this.f$1, this.f$2);
                        }
                    });
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$maybeNotifyOnNotificationExpansionChanged$0 */
        public /* synthetic */ void mo15308x167b4926(String str, State state) {
            try {
                this.mBarService.onNotificationExpansionChanged(str, state.mIsUserAction.booleanValue(), state.mIsExpanded.booleanValue(), state.mLocation.ordinal());
            } catch (RemoteException e) {
                Log.e("NotificationLogger", "Failed to call onNotificationExpansionChanged: ", e);
            }
        }
    }

    public interface OnChildLocationsChangedListener {
        void onChildLocationsChanged();
    }

    public void onStateChanged(int i) {
    }

    public static NotificationLocation getNotificationLocation(NotificationEntry notificationEntry) {
        if (notificationEntry == null || notificationEntry.getRow() == null || notificationEntry.getRow().getViewState() == null) {
            return NotificationLocation.LOCATION_UNKNOWN;
        }
        return convertNotificationLocation(notificationEntry.getRow().getViewState().location);
    }

    private static NotificationLocation convertNotificationLocation(int i) {
        if (i == 1) {
            return NotificationLocation.LOCATION_FIRST_HEADS_UP;
        }
        if (i == 2) {
            return NotificationLocation.LOCATION_HIDDEN_TOP;
        }
        if (i == 4) {
            return NotificationLocation.LOCATION_MAIN_AREA;
        }
        if (i == 8) {
            return NotificationLocation.LOCATION_BOTTOM_STACK_PEEKING;
        }
        if (i == 16) {
            return NotificationLocation.LOCATION_BOTTOM_STACK_HIDDEN;
        }
        if (i != 64) {
            return NotificationLocation.LOCATION_UNKNOWN;
        }
        return NotificationLocation.LOCATION_GONE;
    }

    public NotificationLogger(NotificationListener notificationListener, Executor executor, NotificationEntryManager notificationEntryManager, StatusBarStateController statusBarStateController, ExpansionStateLogger expansionStateLogger, NotificationPanelLogger notificationPanelLogger) {
        this.mNotificationListener = notificationListener;
        this.mUiBgExecutor = executor;
        this.mEntryManager = notificationEntryManager;
        this.mBarService = Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mExpansionStateLogger = expansionStateLogger;
        this.mNotificationPanelLogger = notificationPanelLogger;
        statusBarStateController.addCallback(this);
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                if (z && notificationVisibility != null) {
                    NotificationLogger.this.logNotificationClear(notificationEntry.getKey(), notificationEntry.getSbn(), notificationVisibility);
                }
                NotificationLogger.this.mExpansionStateLogger.onEntryRemoved(notificationEntry.getKey());
            }

            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                NotificationLogger.this.mExpansionStateLogger.onEntryUpdated(notificationEntry.getKey());
            }

            public void onInflationError(StatusBarNotification statusBarNotification, Exception exc) {
                NotificationLogger.this.logNotificationError(statusBarNotification, exc);
            }
        });
    }

    public void setUpWithContainer(NotificationListContainer notificationListContainer) {
        this.mListContainer = notificationListContainer;
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void stopNotificationLogging() {
        if (!this.mCurrentlyVisibleNotifications.isEmpty()) {
            logNotificationVisibilityChanges(Collections.emptyList(), this.mCurrentlyVisibleNotifications);
            recycleAllVisibilityObjects(this.mCurrentlyVisibleNotifications);
        }
        this.mHandler.removeCallbacks(this.mVisibilityReporter);
        this.mListContainer.setChildLocationsChangedListener(null);
    }

    public void startNotificationLogging() {
        this.mListContainer.setChildLocationsChangedListener(this.mNotificationLocationsChangedListener);
        this.mNotificationLocationsChangedListener.onChildLocationsChanged();
        this.mNotificationPanelLogger.logPanelShown(this.mListContainer.hasPulsingNotifications(), this.mEntryManager.getVisibleNotifications());
    }

    private void setDozing(boolean z) {
        synchronized (this.mDozingLock) {
            this.mDozing = z;
        }
    }

    /* access modifiers changed from: private */
    public void logNotificationClear(String str, StatusBarNotification statusBarNotification, NotificationVisibility notificationVisibility) {
        int i;
        int i2;
        String packageName = statusBarNotification.getPackageName();
        String tag = statusBarNotification.getTag();
        int id = statusBarNotification.getId();
        int userId = statusBarNotification.getUserId();
        try {
            if (this.mHeadsUpManager.isAlerting(str)) {
                i2 = 1;
            } else if (this.mListContainer.hasPulsingNotifications()) {
                i2 = 2;
            } else {
                i = 3;
                this.mBarService.onNotificationClear(packageName, tag, id, userId, statusBarNotification.getKey(), i, 1, notificationVisibility);
            }
            i = i2;
            this.mBarService.onNotificationClear(packageName, tag, id, userId, statusBarNotification.getKey(), i, 1, notificationVisibility);
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: private */
    public void logNotificationError(StatusBarNotification statusBarNotification, Exception exc) {
        try {
            this.mBarService.onNotificationError(statusBarNotification.getPackageName(), statusBarNotification.getTag(), statusBarNotification.getId(), statusBarNotification.getUid(), statusBarNotification.getInitialPid(), exc.getMessage(), statusBarNotification.getUserId());
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: private */
    public void logNotificationVisibilityChanges(Collection<NotificationVisibility> collection, Collection<NotificationVisibility> collection2) {
        if (!collection.isEmpty() || !collection2.isEmpty()) {
            this.mUiBgExecutor.execute(new Runnable(cloneVisibilitiesAsArr(collection), cloneVisibilitiesAsArr(collection2)) {
                public final /* synthetic */ NotificationVisibility[] f$1;
                public final /* synthetic */ NotificationVisibility[] f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationLogger.this.lambda$logNotificationVisibilityChanges$0$NotificationLogger(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$logNotificationVisibilityChanges$0 */
    public /* synthetic */ void lambda$logNotificationVisibilityChanges$0$NotificationLogger(NotificationVisibility[] notificationVisibilityArr, NotificationVisibility[] notificationVisibilityArr2) {
        try {
            this.mBarService.onNotificationVisibilityChanged(notificationVisibilityArr, notificationVisibilityArr2);
        } catch (RemoteException unused) {
        }
        int length = notificationVisibilityArr.length;
        if (length > 0) {
            String[] strArr = new String[length];
            for (int i = 0; i < length; i++) {
                strArr[i] = notificationVisibilityArr[i].key;
            }
            synchronized (this.mDozingLock) {
                if (!this.mDozing) {
                    try {
                        this.mNotificationListener.setNotificationsShown(strArr);
                    } catch (RuntimeException e) {
                        Log.d("NotificationLogger", "failed setNotificationsShown: ", e);
                    }
                }
            }
        }
        recycleAllVisibilityObjects(notificationVisibilityArr);
        recycleAllVisibilityObjects(notificationVisibilityArr2);
    }

    /* access modifiers changed from: private */
    public void recycleAllVisibilityObjects(ArraySet<NotificationVisibility> arraySet) {
        int size = arraySet.size();
        for (int i = 0; i < size; i++) {
            ((NotificationVisibility) arraySet.valueAt(i)).recycle();
        }
        arraySet.clear();
    }

    private void recycleAllVisibilityObjects(NotificationVisibility[] notificationVisibilityArr) {
        int length = notificationVisibilityArr.length;
        for (int i = 0; i < length; i++) {
            if (notificationVisibilityArr[i] != null) {
                notificationVisibilityArr[i].recycle();
            }
        }
    }

    /* access modifiers changed from: private */
    public static NotificationVisibility[] cloneVisibilitiesAsArr(Collection<NotificationVisibility> collection) {
        NotificationVisibility[] notificationVisibilityArr = new NotificationVisibility[collection.size()];
        int i = 0;
        for (NotificationVisibility notificationVisibility : collection) {
            if (notificationVisibility != null) {
                notificationVisibilityArr[i] = notificationVisibility.clone();
            }
            i++;
        }
        return notificationVisibilityArr;
    }

    @VisibleForTesting
    public Runnable getVisibilityReporter() {
        return this.mVisibilityReporter;
    }

    public void onDozingChanged(boolean z) {
        setDozing(z);
    }

    public void onExpansionChanged(String str, boolean z, boolean z2) {
        this.mExpansionStateLogger.onExpansionChanged(str, z, z2, getNotificationLocation(this.mEntryManager.getActiveNotificationUnfiltered(str)));
    }

    @VisibleForTesting
    public void setVisibilityReporter(Runnable runnable) {
        this.mVisibilityReporter = runnable;
    }
}
