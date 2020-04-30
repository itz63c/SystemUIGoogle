package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.provider.Settings.Global;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2012R$integer;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;

public abstract class HeadsUpManager extends AlertingNotificationManager {
    /* access modifiers changed from: private */
    public final AccessibilityManagerWrapper mAccessibilityMgr;
    protected final Context mContext;
    protected boolean mHasPinnedNotification;
    protected final HashSet<OnHeadsUpChangedListener> mListeners = new HashSet<>();
    protected int mSnoozeLengthMs;
    private final ArrayMap<String, Long> mSnoozedPackages;
    protected int mTouchAcceptanceDelay;
    protected int mUser;

    protected class HeadsUpEntry extends AlertEntry {
        protected boolean expanded;
        public boolean remoteInputActive;

        protected HeadsUpEntry() {
            super();
        }

        /* access modifiers changed from: protected */
        public boolean isSticky() {
            return (this.mEntry.isRowPinned() && this.expanded) || this.remoteInputActive || HeadsUpManager.this.hasFullScreenIntent(this.mEntry);
        }

        public int compareTo(AlertEntry alertEntry) {
            HeadsUpEntry headsUpEntry = (HeadsUpEntry) alertEntry;
            boolean isRowPinned = this.mEntry.isRowPinned();
            boolean isRowPinned2 = headsUpEntry.mEntry.isRowPinned();
            if (isRowPinned && !isRowPinned2) {
                return -1;
            }
            if (!isRowPinned && isRowPinned2) {
                return 1;
            }
            boolean hasFullScreenIntent = HeadsUpManager.this.hasFullScreenIntent(this.mEntry);
            boolean hasFullScreenIntent2 = HeadsUpManager.this.hasFullScreenIntent(headsUpEntry.mEntry);
            if (hasFullScreenIntent && !hasFullScreenIntent2) {
                return -1;
            }
            if (!hasFullScreenIntent && hasFullScreenIntent2) {
                return 1;
            }
            if (this.remoteInputActive && !headsUpEntry.remoteInputActive) {
                return -1;
            }
            if (this.remoteInputActive || !headsUpEntry.remoteInputActive) {
                return super.compareTo((AlertEntry) headsUpEntry);
            }
            return 1;
        }

        public void setExpanded(boolean z) {
            this.expanded = z;
        }

        public void reset() {
            super.reset();
            this.expanded = false;
            this.remoteInputActive = false;
        }

        /* access modifiers changed from: protected */
        public long calculatePostTime() {
            return super.calculatePostTime() + ((long) HeadsUpManager.this.mTouchAcceptanceDelay);
        }

        /* access modifiers changed from: protected */
        public long calculateFinishTime() {
            return this.mPostTime + ((long) getRecommendedHeadsUpTimeoutMs(HeadsUpManager.this.mAutoDismissNotificationDecay));
        }

        /* access modifiers changed from: protected */
        public int getRecommendedHeadsUpTimeoutMs(int i) {
            return HeadsUpManager.this.mAccessibilityMgr.getRecommendedTimeoutMillis(i, 7);
        }
    }

    public int getContentFlag() {
        return 4;
    }

    public boolean isEntryAutoHeadsUpped(String str) {
        return false;
    }

    public boolean isTrackingHeadsUp() {
        return false;
    }

    public void onDensityOrFontScaleChanged() {
    }

    public HeadsUpManager(final Context context) {
        this.mContext = context;
        this.mAccessibilityMgr = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);
        Resources resources = context.getResources();
        this.mMinimumDisplayTime = resources.getInteger(C2012R$integer.heads_up_notification_minimum_time);
        this.mAutoDismissNotificationDecay = resources.getInteger(C2012R$integer.heads_up_notification_decay);
        this.mTouchAcceptanceDelay = resources.getInteger(C2012R$integer.touch_acceptance_delay);
        this.mSnoozedPackages = new ArrayMap<>();
        String str = "heads_up_snooze_length_ms";
        this.mSnoozeLengthMs = Global.getInt(context.getContentResolver(), str, resources.getInteger(C2012R$integer.heads_up_default_snooze_length_ms));
        context.getContentResolver().registerContentObserver(Global.getUriFor(str), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                int i = Global.getInt(context.getContentResolver(), "heads_up_snooze_length_ms", -1);
                if (i > -1) {
                    HeadsUpManager headsUpManager = HeadsUpManager.this;
                    if (i != headsUpManager.mSnoozeLengthMs) {
                        headsUpManager.mSnoozeLengthMs = i;
                        String str = "HeadsUpManager";
                        if (Log.isLoggable(str, 2)) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("mSnoozeLengthMs = ");
                            sb.append(HeadsUpManager.this.mSnoozeLengthMs);
                            Log.v(str, sb.toString());
                        }
                    }
                }
            }
        });
    }

    public void addListener(OnHeadsUpChangedListener onHeadsUpChangedListener) {
        this.mListeners.add(onHeadsUpChangedListener);
    }

    public void removeListener(OnHeadsUpChangedListener onHeadsUpChangedListener) {
        this.mListeners.remove(onHeadsUpChangedListener);
    }

    public void updateNotification(String str, boolean z) {
        super.updateNotification(str, z);
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(str);
        if (z && headsUpEntry != null) {
            setEntryPinned(headsUpEntry, shouldHeadsUpBecomePinned(headsUpEntry.mEntry));
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldHeadsUpBecomePinned(NotificationEntry notificationEntry) {
        return hasFullScreenIntent(notificationEntry);
    }

    /* access modifiers changed from: protected */
    public boolean hasFullScreenIntent(NotificationEntry notificationEntry) {
        return notificationEntry.getSbn().getNotification().fullScreenIntent != null;
    }

    /* access modifiers changed from: protected */
    public void setEntryPinned(HeadsUpEntry headsUpEntry, boolean z) {
        String str = "HeadsUpManager";
        if (Log.isLoggable(str, 2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("setEntryPinned: ");
            sb.append(z);
            Log.v(str, sb.toString());
        }
        NotificationEntry notificationEntry = headsUpEntry.mEntry;
        if (notificationEntry.isRowPinned() != z) {
            notificationEntry.setRowPinned(z);
            updatePinnedMode();
            Iterator it = this.mListeners.iterator();
            while (it.hasNext()) {
                OnHeadsUpChangedListener onHeadsUpChangedListener = (OnHeadsUpChangedListener) it.next();
                if (z) {
                    onHeadsUpChangedListener.onHeadsUpPinned(notificationEntry);
                } else {
                    onHeadsUpChangedListener.onHeadsUpUnPinned(notificationEntry);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAlertEntryAdded(AlertEntry alertEntry) {
        NotificationEntry notificationEntry = alertEntry.mEntry;
        notificationEntry.setHeadsUp(true);
        setEntryPinned((HeadsUpEntry) alertEntry, shouldHeadsUpBecomePinned(notificationEntry));
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((OnHeadsUpChangedListener) it.next()).onHeadsUpStateChanged(notificationEntry, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onAlertEntryRemoved(AlertEntry alertEntry) {
        NotificationEntry notificationEntry = alertEntry.mEntry;
        notificationEntry.setHeadsUp(false);
        setEntryPinned((HeadsUpEntry) alertEntry, false);
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((OnHeadsUpChangedListener) it.next()).onHeadsUpStateChanged(notificationEntry, false);
        }
        notificationEntry.freeContentViewWhenSafe(4);
    }

    /* access modifiers changed from: protected */
    public void updatePinnedMode() {
        boolean hasPinnedNotificationInternal = hasPinnedNotificationInternal();
        if (hasPinnedNotificationInternal != this.mHasPinnedNotification) {
            String str = "HeadsUpManager";
            if (Log.isLoggable(str, 2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pinned mode changed: ");
                sb.append(this.mHasPinnedNotification);
                sb.append(" -> ");
                sb.append(hasPinnedNotificationInternal);
                Log.v(str, sb.toString());
            }
            this.mHasPinnedNotification = hasPinnedNotificationInternal;
            if (hasPinnedNotificationInternal) {
                MetricsLogger.count(this.mContext, "note_peek", 1);
            }
            Iterator it = this.mListeners.iterator();
            while (it.hasNext()) {
                ((OnHeadsUpChangedListener) it.next()).onHeadsUpPinnedModeChanged(hasPinnedNotificationInternal);
            }
        }
    }

    public boolean isSnoozed(String str) {
        String snoozeKey = snoozeKey(str, this.mUser);
        Long l = (Long) this.mSnoozedPackages.get(snoozeKey);
        if (l != null) {
            if (l.longValue() > this.mClock.currentTimeMillis()) {
                String str2 = "HeadsUpManager";
                if (Log.isLoggable(str2, 2)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(snoozeKey);
                    sb.append(" snoozed");
                    Log.v(str2, sb.toString());
                }
                return true;
            }
            this.mSnoozedPackages.remove(str);
        }
        return false;
    }

    public void snooze() {
        for (String headsUpEntry : this.mAlertEntries.keySet()) {
            this.mSnoozedPackages.put(snoozeKey(getHeadsUpEntry(headsUpEntry).mEntry.getSbn().getPackageName(), this.mUser), Long.valueOf(this.mClock.currentTimeMillis() + ((long) this.mSnoozeLengthMs)));
        }
    }

    private static String snoozeKey(String str, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append(",");
        sb.append(str);
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public HeadsUpEntry getHeadsUpEntry(String str) {
        return (HeadsUpEntry) this.mAlertEntries.get(str);
    }

    public NotificationEntry getTopEntry() {
        HeadsUpEntry topHeadsUpEntry = getTopHeadsUpEntry();
        if (topHeadsUpEntry != null) {
            return topHeadsUpEntry.mEntry;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public HeadsUpEntry getTopHeadsUpEntry() {
        HeadsUpEntry headsUpEntry = null;
        if (this.mAlertEntries.isEmpty()) {
            return null;
        }
        for (AlertEntry alertEntry : this.mAlertEntries.values()) {
            if (headsUpEntry == null || alertEntry.compareTo((AlertEntry) headsUpEntry) < 0) {
                headsUpEntry = (HeadsUpEntry) alertEntry;
            }
        }
        return headsUpEntry;
    }

    public void setUser(int i) {
        this.mUser = i;
    }

    /* access modifiers changed from: protected */
    public void dumpInternal(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("  mTouchAcceptanceDelay=");
        printWriter.println(this.mTouchAcceptanceDelay);
        printWriter.print("  mSnoozeLengthMs=");
        printWriter.println(this.mSnoozeLengthMs);
        printWriter.print("  now=");
        printWriter.println(this.mClock.currentTimeMillis());
        printWriter.print("  mUser=");
        printWriter.println(this.mUser);
        for (AlertEntry alertEntry : this.mAlertEntries.values()) {
            printWriter.print("  HeadsUpEntry=");
            printWriter.println(alertEntry.mEntry);
        }
        int size = this.mSnoozedPackages.size();
        StringBuilder sb = new StringBuilder();
        sb.append("  snoozed packages: ");
        sb.append(size);
        printWriter.println(sb.toString());
        for (int i = 0; i < size; i++) {
            printWriter.print("    ");
            printWriter.print(this.mSnoozedPackages.valueAt(i));
            printWriter.print(", ");
            printWriter.println((String) this.mSnoozedPackages.keyAt(i));
        }
    }

    public boolean hasPinnedHeadsUp() {
        return this.mHasPinnedNotification;
    }

    private boolean hasPinnedNotificationInternal() {
        for (String headsUpEntry : this.mAlertEntries.keySet()) {
            if (getHeadsUpEntry(headsUpEntry).mEntry.isRowPinned()) {
                return true;
            }
        }
        return false;
    }

    public void unpinAll(boolean z) {
        for (String headsUpEntry : this.mAlertEntries.keySet()) {
            HeadsUpEntry headsUpEntry2 = getHeadsUpEntry(headsUpEntry);
            setEntryPinned(headsUpEntry2, false);
            headsUpEntry2.updateEntry(false);
            if (z) {
                NotificationEntry notificationEntry = headsUpEntry2.mEntry;
                if (notificationEntry != null && notificationEntry.mustStayOnScreen()) {
                    headsUpEntry2.mEntry.setHeadsUpIsVisible();
                }
            }
        }
    }

    public int compare(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(notificationEntry.getKey());
        HeadsUpEntry headsUpEntry2 = getHeadsUpEntry(notificationEntry2.getKey());
        if (headsUpEntry != null && headsUpEntry2 != null) {
            return headsUpEntry.compareTo((AlertEntry) headsUpEntry2);
        }
        return headsUpEntry == null ? 1 : -1;
    }

    public void setExpanded(NotificationEntry notificationEntry, boolean z) {
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(notificationEntry.getKey());
        if (headsUpEntry != null && notificationEntry.isRowPinned()) {
            headsUpEntry.setExpanded(z);
        }
    }

    /* access modifiers changed from: protected */
    public HeadsUpEntry createAlertEntry() {
        return new HeadsUpEntry();
    }
}
