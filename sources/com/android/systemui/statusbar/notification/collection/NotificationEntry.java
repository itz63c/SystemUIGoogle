package com.android.systemui.statusbar.notification.collection;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.BubbleMetadata;
import android.app.Notification.MessagingStyle.Message;
import android.app.NotificationChannel;
import android.app.Person;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.icon.IconPack;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NotificationEntry extends ListEntry {
    public EditedSuggestionInfo editedSuggestionInfo;
    private boolean hasSentReply;
    public CharSequence headsUpStatusBarText;
    public CharSequence headsUpStatusBarTextPublic;
    private long initializationTime;
    private boolean interruption;
    private long lastFullScreenIntentLaunchTime;
    public long lastRemoteInputSent;
    public final ArraySet<Integer> mActiveAppOps;
    private boolean mAllowFgsDismissal;
    private boolean mAutoHeadsUp;
    private BubbleMetadata mBubbleMetadata;
    private int mBucket;
    private int mCachedContrastColor;
    private int mCachedContrastColorIsFor;
    int mCancellationReason;
    private Throwable mDebugThrowable;
    final List<NotifDismissInterceptor> mDismissInterceptors;
    private DismissState mDismissState;
    NotifFilter mExcludingFilter;
    private IconPack mIcons;
    public Boolean mIsSystemNotification;
    private final String mKey;
    final List<NotifLifetimeExtender> mLifetimeExtenders;
    NotifPromoter mNotifPromoter;
    private List<OnSensitivityChangedListener> mOnSensitivityChangedListeners;
    private boolean mPulseSupressed;
    private Ranking mRanking;
    private ExpandableNotificationRowController mRowController;
    private InflationTask mRunningTask;
    private StatusBarNotification mSbn;
    private boolean mSensitive;
    private NotificationEntry parent;
    public String remoteInputMimeType;
    public CharSequence remoteInputText;
    public CharSequence remoteInputTextWhenReset;
    public Uri remoteInputUri;
    private ExpandableNotificationRow row;
    public int targetSdk;

    public enum DismissState {
        NOT_DISMISSED,
        DISMISSED,
        PARENT_DISMISSED
    }

    public static class EditedSuggestionInfo {
        public final int index;
        public final CharSequence originalText;

        public EditedSuggestionInfo(CharSequence charSequence, int i) {
            this.originalText = charSequence;
            this.index = i;
        }
    }

    public interface OnSensitivityChangedListener {
        void onSensitivityChanged(NotificationEntry notificationEntry);
    }

    public NotificationEntry getRepresentativeEntry() {
        return this;
    }

    public NotificationEntry(StatusBarNotification statusBarNotification, Ranking ranking) {
        this(statusBarNotification, ranking, false);
    }

    public NotificationEntry(StatusBarNotification statusBarNotification, Ranking ranking, boolean z) {
        Objects.requireNonNull(statusBarNotification);
        String key = statusBarNotification.getKey();
        Objects.requireNonNull(key);
        super(key);
        this.mLifetimeExtenders = new ArrayList();
        this.mDismissInterceptors = new ArrayList();
        this.mCancellationReason = -1;
        this.mDismissState = DismissState.NOT_DISMISSED;
        this.mIcons = IconPack.buildEmptyPack(null);
        this.lastFullScreenIntentLaunchTime = -2000;
        this.mCachedContrastColor = 1;
        this.mCachedContrastColorIsFor = 1;
        this.mRunningTask = null;
        this.lastRemoteInputSent = -2000;
        this.mActiveAppOps = new ArraySet<>(3);
        this.initializationTime = -1;
        this.mSensitive = true;
        this.mOnSensitivityChangedListeners = new ArrayList();
        this.mBucket = 2;
        Objects.requireNonNull(ranking);
        this.mKey = statusBarNotification.getKey();
        setSbn(statusBarNotification);
        setRanking(ranking);
        this.mAllowFgsDismissal = z;
    }

    public String getKey() {
        return this.mKey;
    }

    public StatusBarNotification getSbn() {
        return this.mSbn;
    }

    public void setSbn(StatusBarNotification statusBarNotification) {
        Objects.requireNonNull(statusBarNotification);
        Objects.requireNonNull(statusBarNotification.getKey());
        if (statusBarNotification.getKey().equals(this.mKey)) {
            this.mSbn = statusBarNotification;
            this.mBubbleMetadata = statusBarNotification.getNotification().getBubbleMetadata();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("New key ");
        sb.append(statusBarNotification.getKey());
        sb.append(" doesn't match existing key ");
        sb.append(this.mKey);
        throw new IllegalArgumentException(sb.toString());
    }

    public Ranking getRanking() {
        return this.mRanking;
    }

    public void setRanking(Ranking ranking) {
        Objects.requireNonNull(ranking);
        Objects.requireNonNull(ranking.getKey());
        if (ranking.getKey().equals(this.mKey)) {
            this.mRanking = ranking;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("New key ");
        sb.append(ranking.getKey());
        sb.append(" doesn't match existing key ");
        sb.append(this.mKey);
        throw new IllegalArgumentException(sb.toString());
    }

    public DismissState getDismissState() {
        return this.mDismissState;
    }

    /* access modifiers changed from: 0000 */
    public void setDismissState(DismissState dismissState) {
        Objects.requireNonNull(dismissState);
        this.mDismissState = dismissState;
    }

    public NotificationChannel getChannel() {
        return this.mRanking.getChannel();
    }

    public long getLastAudiblyAlertedMs() {
        return this.mRanking.getLastAudiblyAlertedMillis();
    }

    public boolean isAmbient() {
        return this.mRanking.isAmbient();
    }

    public int getImportance() {
        return this.mRanking.getImportance();
    }

    public List<SnoozeCriterion> getSnoozeCriteria() {
        return this.mRanking.getSnoozeCriteria();
    }

    public int getUserSentiment() {
        return this.mRanking.getUserSentiment();
    }

    public int getSuppressedVisualEffects() {
        return this.mRanking.getSuppressedVisualEffects();
    }

    public boolean canBubble() {
        return this.mRanking.canBubble();
    }

    public List<Action> getSmartActions() {
        return this.mRanking.getSmartActions();
    }

    public List<CharSequence> getSmartReplies() {
        return this.mRanking.getSmartReplies();
    }

    public IconPack getIcons() {
        return this.mIcons;
    }

    public void setIcons(IconPack iconPack) {
        this.mIcons = iconPack;
    }

    public void setInterruption() {
        this.interruption = true;
    }

    public boolean hasInterrupted() {
        return this.interruption;
    }

    public boolean isBubble() {
        return (this.mSbn.getNotification().flags & 4096) != 0;
    }

    public BubbleMetadata getBubbleMetadata() {
        return this.mBubbleMetadata;
    }

    public void setBubbleMetadata(BubbleMetadata bubbleMetadata) {
        this.mBubbleMetadata = bubbleMetadata;
    }

    public boolean setFlagBubble(boolean z) {
        boolean isBubble = isBubble();
        if (!z) {
            Notification notification = this.mSbn.getNotification();
            notification.flags &= -4097;
        } else if (this.mBubbleMetadata != null && canBubble()) {
            Notification notification2 = this.mSbn.getNotification();
            notification2.flags |= 4096;
        }
        return isBubble != isBubble();
    }

    public void reset() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.reset();
        }
    }

    public int getBucket() {
        return this.mBucket;
    }

    public void setBucket(int i) {
        this.mBucket = i;
    }

    public ExpandableNotificationRow getRow() {
        return this.row;
    }

    public void setRow(ExpandableNotificationRow expandableNotificationRow) {
        this.row = expandableNotificationRow;
    }

    public ExpandableNotificationRowController getRowController() {
        return this.mRowController;
    }

    public void setRowController(ExpandableNotificationRowController expandableNotificationRowController) {
        this.mRowController = expandableNotificationRowController;
    }

    public List<NotificationEntry> getChildren() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow == null) {
            return null;
        }
        List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
        if (notificationChildren == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (ExpandableNotificationRow entry : notificationChildren) {
            arrayList.add(entry.getEntry());
        }
        return arrayList;
    }

    public void notifyFullScreenIntentLaunched() {
        setInterruption();
        this.lastFullScreenIntentLaunchTime = SystemClock.elapsedRealtime();
    }

    public boolean hasJustLaunchedFullScreenIntent() {
        return SystemClock.elapsedRealtime() < this.lastFullScreenIntentLaunchTime + 2000;
    }

    public boolean hasJustSentRemoteInput() {
        return SystemClock.elapsedRealtime() < this.lastRemoteInputSent + 500;
    }

    public boolean hasFinishedInitialization() {
        return this.initializationTime == -1 || SystemClock.elapsedRealtime() > this.initializationTime + 400;
    }

    public int getContrastedColor(Context context, boolean z, int i) {
        int i2 = z ? 0 : this.mSbn.getNotification().color;
        if (this.mCachedContrastColorIsFor == i2) {
            int i3 = this.mCachedContrastColor;
            if (i3 != 1) {
                return i3;
            }
        }
        int resolveContrastColor = ContrastColorUtil.resolveContrastColor(context, i2, i);
        this.mCachedContrastColorIsFor = i2;
        this.mCachedContrastColor = resolveContrastColor;
        return resolveContrastColor;
    }

    public void abortTask() {
        InflationTask inflationTask = this.mRunningTask;
        if (inflationTask != null) {
            inflationTask.abort();
            this.mRunningTask = null;
        }
    }

    public void setInflationTask(InflationTask inflationTask) {
        abortTask();
        this.mRunningTask = inflationTask;
    }

    public void onInflationTaskFinished() {
        this.mRunningTask = null;
    }

    @VisibleForTesting
    public InflationTask getRunningTask() {
        return this.mRunningTask;
    }

    public void setDebugThrowable(Throwable th) {
        this.mDebugThrowable = th;
    }

    public Throwable getDebugThrowable() {
        return this.mDebugThrowable;
    }

    public void onRemoteInputInserted() {
        this.lastRemoteInputSent = -2000;
        this.remoteInputTextWhenReset = null;
    }

    public void setHasSentReply() {
        this.hasSentReply = true;
    }

    public boolean isLastMessageFromReply() {
        if (!this.hasSentReply) {
            return false;
        }
        Bundle bundle = this.mSbn.getNotification().extras;
        if (!ArrayUtils.isEmpty(bundle.getParcelableArray("android.remoteInputHistoryItems"))) {
            return true;
        }
        List messagesFromBundleArray = Message.getMessagesFromBundleArray(bundle.getParcelableArray("android.messages"));
        if (messagesFromBundleArray != null && !messagesFromBundleArray.isEmpty()) {
            Message message = (Message) messagesFromBundleArray.get(messagesFromBundleArray.size() - 1);
            if (message != null) {
                Person senderPerson = message.getSenderPerson();
                if (senderPerson == null) {
                    return true;
                }
                return Objects.equals((Person) bundle.getParcelable("android.messagingUser"), senderPerson);
            }
        }
        return false;
    }

    public void setInitializationTime(long j) {
        if (this.initializationTime == -1) {
            this.initializationTime = j;
        }
    }

    public void sendAccessibilityEvent(int i) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.sendAccessibilityEvent(i);
        }
    }

    public boolean isMediaNotification() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow == null) {
            return false;
        }
        return expandableNotificationRow.isMediaRow();
    }

    public boolean isTopLevelChild() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isTopLevelChild();
    }

    public void resetUserExpansion() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.resetUserExpansion();
        }
    }

    public void freeContentViewWhenSafe(int i) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.freeContentViewWhenSafe(i);
        }
    }

    public boolean rowExists() {
        return this.row != null;
    }

    public boolean isRowDismissed() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isDismissed();
    }

    public boolean isRowRemoved() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isRemoved();
    }

    public boolean isRemoved() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow == null || expandableNotificationRow.isRemoved();
    }

    public boolean isRowPinned() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isPinned();
    }

    public void setRowPinned(boolean z) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setPinned(z);
        }
    }

    public boolean isRowHeadsUp() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isHeadsUp();
    }

    public boolean showingPulsing() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.showingPulsing();
    }

    public void setHeadsUp(boolean z) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setHeadsUp(z);
        }
    }

    public void setHeadsUpAnimatingAway(boolean z) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setHeadsUpAnimatingAway(z);
        }
    }

    public void setAutoHeadsUp(boolean z) {
        this.mAutoHeadsUp = z;
    }

    public boolean isAutoHeadsUp() {
        return this.mAutoHeadsUp;
    }

    public boolean mustStayOnScreen() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.mustStayOnScreen();
    }

    public void setHeadsUpIsVisible() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setHeadsUpIsVisible();
        }
    }

    public ExpandableNotificationRow getHeadsUpAnimationView() {
        return this.row;
    }

    public void setUserLocked(boolean z) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setUserLocked(z);
        }
    }

    public void setUserExpanded(boolean z, boolean z2) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setUserExpanded(z, z2);
        }
    }

    public void setGroupExpansionChanging(boolean z) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setGroupExpansionChanging(z);
        }
    }

    public void notifyHeightChanged(boolean z) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.notifyHeightChanged(z);
        }
    }

    public void closeRemoteInput() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.closeRemoteInput();
        }
    }

    public boolean areChildrenExpanded() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.areChildrenExpanded();
    }

    public boolean isGroupNotFullyVisible() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow == null || expandableNotificationRow.isGroupNotFullyVisible();
    }

    public NotificationGuts getGuts() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            return expandableNotificationRow.getGuts();
        }
        return null;
    }

    public void removeRow() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setRemoved();
        }
    }

    public boolean isSummaryWithChildren() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isSummaryWithChildren();
    }

    public void setKeepInParent(boolean z) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setKeepInParent(z);
        }
    }

    public void onDensityOrFontScaleChanged() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.onDensityOrFontScaleChanged();
        }
    }

    public boolean areGutsExposed() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return (expandableNotificationRow == null || expandableNotificationRow.getGuts() == null || !this.row.getGuts().isExposed()) ? false : true;
    }

    public boolean isChildInGroup() {
        return this.parent == null;
    }

    public boolean isClearable() {
        if (!isDismissable()) {
            return false;
        }
        List children = getChildren();
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                if (!((NotificationEntry) children.get(i)).isDismissable()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isDismissable() {
        boolean z = (this.mSbn.getNotification().flags & 2) != 0;
        boolean z2 = (this.mSbn.getNotification().flags & 32) != 0;
        boolean z3 = (this.mSbn.getNotification().flags & 64) != 0;
        if (this.mAllowFgsDismissal) {
            return !z2 || z || z3;
        }
        return this.mSbn.isClearable();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean isExemptFromDndVisualSuppression() {
        if (isNotificationBlockedByPolicy(this.mSbn.getNotification())) {
            return false;
        }
        if ((this.mSbn.getNotification().flags & 64) != 0 || this.mSbn.getNotification().isMediaNotification()) {
            return true;
        }
        Boolean bool = this.mIsSystemNotification;
        if (bool == null || !bool.booleanValue()) {
            return false;
        }
        return true;
    }

    private boolean shouldSuppressVisualEffect(int i) {
        boolean z = false;
        if (isExemptFromDndVisualSuppression()) {
            return false;
        }
        if ((getSuppressedVisualEffects() & i) != 0) {
            z = true;
        }
        return z;
    }

    public boolean shouldSuppressFullScreenIntent() {
        return shouldSuppressVisualEffect(4);
    }

    public boolean shouldSuppressPeek() {
        return shouldSuppressVisualEffect(16);
    }

    public boolean shouldSuppressStatusBar() {
        return shouldSuppressVisualEffect(32);
    }

    public boolean shouldSuppressAmbient() {
        return shouldSuppressVisualEffect(128);
    }

    public boolean shouldSuppressNotificationList() {
        return shouldSuppressVisualEffect(256);
    }

    public boolean shouldSuppressNotificationDot() {
        return shouldSuppressVisualEffect(64);
    }

    private static boolean isNotificationBlockedByPolicy(Notification notification) {
        return isCategory("call", notification) || isCategory("msg", notification) || isCategory("alarm", notification) || isCategory("event", notification) || isCategory("reminder", notification);
    }

    private static boolean isCategory(String str, Notification notification) {
        return Objects.equals(notification.category, str);
    }

    public void setSensitive(boolean z, boolean z2) {
        getRow().setSensitive(z, z2);
        if (z != this.mSensitive) {
            this.mSensitive = z;
            for (int i = 0; i < this.mOnSensitivityChangedListeners.size(); i++) {
                ((OnSensitivityChangedListener) this.mOnSensitivityChangedListeners.get(i)).onSensitivityChanged(this);
            }
        }
    }

    public boolean isSensitive() {
        return this.mSensitive;
    }

    public void addOnSensitivityChangedListener(OnSensitivityChangedListener onSensitivityChangedListener) {
        this.mOnSensitivityChangedListeners.add(onSensitivityChangedListener);
    }

    public void removeOnSensitivityChangedListener(OnSensitivityChangedListener onSensitivityChangedListener) {
        this.mOnSensitivityChangedListeners.remove(onSensitivityChangedListener);
    }

    public boolean isPulseSuppressed() {
        return this.mPulseSupressed;
    }

    public void setPulseSuppressed(boolean z) {
        this.mPulseSupressed = z;
    }
}
