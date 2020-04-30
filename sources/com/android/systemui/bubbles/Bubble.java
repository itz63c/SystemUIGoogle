package com.android.systemui.bubbles;

import android.app.Notification.BubbleMetadata;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask.Status;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.systemui.bubbles.BubbleController.NotificationSuppressionChangedListener;
import com.android.systemui.bubbles.BubbleViewInfoTask.Callback;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;

class Bubble implements BubbleViewProvider {
    private String mAppName;
    private Bitmap mBadgedImage;
    private int mDotColor;
    private Path mDotPath;
    private NotificationEntry mEntry;
    private BubbleExpandedView mExpandedView;
    private FlyoutMessage mFlyoutMessage;
    private final String mGroupId;
    private BadgedImageView mIconView;
    private boolean mInflateSynchronously;
    private boolean mInflated;
    private BubbleViewInfoTask mInflationTask;
    private final String mKey;
    private long mLastAccessed;
    private long mLastUpdated;
    private ShortcutInfo mShortcutInfo;
    private boolean mShowBubbleUpdateDot = true;
    private boolean mSuppressFlyout;
    private NotificationSuppressionChangedListener mSuppressionListener;

    public static class FlyoutMessage {
        public boolean isGroupChat;
        public CharSequence message;
        public Drawable senderAvatar;
        public CharSequence senderName;
    }

    public static String groupId(NotificationEntry notificationEntry) {
        UserHandle user = notificationEntry.getSbn().getUser();
        StringBuilder sb = new StringBuilder();
        sb.append(user.getIdentifier());
        sb.append("|");
        sb.append(notificationEntry.getSbn().getPackageName());
        return sb.toString();
    }

    @VisibleForTesting(visibility = Visibility.PRIVATE)
    Bubble(NotificationEntry notificationEntry, NotificationSuppressionChangedListener notificationSuppressionChangedListener) {
        this.mEntry = notificationEntry;
        this.mKey = notificationEntry.getKey();
        this.mLastUpdated = notificationEntry.getSbn().getPostTime();
        this.mGroupId = groupId(notificationEntry);
        this.mSuppressionListener = notificationSuppressionChangedListener;
    }

    public String getKey() {
        return this.mKey;
    }

    public NotificationEntry getEntry() {
        return this.mEntry;
    }

    public String getGroupId() {
        return this.mGroupId;
    }

    public String getPackageName() {
        return this.mEntry.getSbn().getPackageName();
    }

    public Bitmap getBadgedImage() {
        return this.mBadgedImage;
    }

    public int getDotColor() {
        return this.mDotColor;
    }

    public Path getDotPath() {
        return this.mDotPath;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public ShortcutInfo getShortcutInfo() {
        return this.mShortcutInfo;
    }

    public BadgedImageView getIconView() {
        return this.mIconView;
    }

    public BubbleExpandedView getExpandedView() {
        return this.mExpandedView;
    }

    /* access modifiers changed from: 0000 */
    public void cleanupExpandedState() {
        BubbleExpandedView bubbleExpandedView = this.mExpandedView;
        if (bubbleExpandedView != null) {
            bubbleExpandedView.cleanUpExpandedState();
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setInflateSynchronously(boolean z) {
        this.mInflateSynchronously = z;
    }

    /* access modifiers changed from: 0000 */
    public void inflate(Callback callback, Context context, BubbleStackView bubbleStackView, BubbleIconFactory bubbleIconFactory) {
        if (isBubbleLoading()) {
            this.mInflationTask.cancel(true);
        }
        BubbleViewInfoTask bubbleViewInfoTask = new BubbleViewInfoTask(this, context, bubbleStackView, bubbleIconFactory, callback);
        this.mInflationTask = bubbleViewInfoTask;
        if (this.mInflateSynchronously) {
            bubbleViewInfoTask.onPostExecute(bubbleViewInfoTask.doInBackground(new Void[0]));
        } else {
            bubbleViewInfoTask.execute(new Void[0]);
        }
    }

    private boolean isBubbleLoading() {
        BubbleViewInfoTask bubbleViewInfoTask = this.mInflationTask;
        return (bubbleViewInfoTask == null || bubbleViewInfoTask.getStatus() == Status.FINISHED) ? false : true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isInflated() {
        return this.mInflated;
    }

    /* access modifiers changed from: 0000 */
    public void setViewInfo(BubbleViewInfo bubbleViewInfo) {
        if (!isInflated()) {
            this.mIconView = bubbleViewInfo.imageView;
            this.mExpandedView = bubbleViewInfo.expandedView;
            this.mInflated = true;
        }
        this.mShortcutInfo = bubbleViewInfo.shortcutInfo;
        this.mAppName = bubbleViewInfo.appName;
        this.mFlyoutMessage = bubbleViewInfo.flyoutMessage;
        this.mBadgedImage = bubbleViewInfo.badgedBubbleImage;
        this.mDotColor = bubbleViewInfo.dotColor;
        this.mDotPath = bubbleViewInfo.dotPath;
        this.mExpandedView.update(this);
        this.mIconView.update(this);
    }

    /* access modifiers changed from: 0000 */
    public void setInflated(boolean z) {
        this.mInflated = z;
    }

    public void setContentVisibility(boolean z) {
        BubbleExpandedView bubbleExpandedView = this.mExpandedView;
        if (bubbleExpandedView != null) {
            bubbleExpandedView.setContentVisibility(z);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setEntry(NotificationEntry notificationEntry) {
        this.mEntry = notificationEntry;
        this.mLastUpdated = notificationEntry.getSbn().getPostTime();
    }

    /* access modifiers changed from: 0000 */
    public long getLastActivity() {
        return Math.max(this.mLastUpdated, this.mLastAccessed);
    }

    /* access modifiers changed from: 0000 */
    public long getLastUpdateTime() {
        return this.mLastUpdated;
    }

    public int getDisplayId() {
        BubbleExpandedView bubbleExpandedView = this.mExpandedView;
        if (bubbleExpandedView != null) {
            return bubbleExpandedView.getVirtualDisplayId();
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public void markAsAccessedAt(long j) {
        this.mLastAccessed = j;
        setSuppressNotification(true);
        setShowDot(false, true);
    }

    /* access modifiers changed from: 0000 */
    public void markUpdatedAt(long j) {
        this.mLastUpdated = j;
    }

    /* access modifiers changed from: 0000 */
    public boolean showInShade() {
        return !shouldSuppressNotification() || !this.mEntry.isClearable();
    }

    /* access modifiers changed from: 0000 */
    public void setSuppressNotification(boolean z) {
        boolean showInShade = showInShade();
        BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        int flags = bubbleMetadata.getFlags();
        bubbleMetadata.setFlags(z ? flags | 2 : flags & -3);
        if (showInShade() != showInShade) {
            NotificationSuppressionChangedListener notificationSuppressionChangedListener = this.mSuppressionListener;
            if (notificationSuppressionChangedListener != null) {
                notificationSuppressionChangedListener.onBubbleNotificationSuppressionChange(this);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setShowDot(boolean z, boolean z2) {
        this.mShowBubbleUpdateDot = z;
        if (z2) {
            BadgedImageView badgedImageView = this.mIconView;
            if (badgedImageView != null) {
                badgedImageView.animateDot();
                return;
            }
        }
        BadgedImageView badgedImageView2 = this.mIconView;
        if (badgedImageView2 != null) {
            badgedImageView2.invalidate();
        }
    }

    public boolean showDot() {
        return this.mShowBubbleUpdateDot && !this.mEntry.shouldSuppressNotificationDot() && !shouldSuppressNotification();
    }

    /* access modifiers changed from: 0000 */
    public boolean showFlyout() {
        return !this.mSuppressFlyout && !this.mEntry.shouldSuppressPeek() && !shouldSuppressNotification() && !this.mEntry.shouldSuppressNotificationList();
    }

    /* access modifiers changed from: 0000 */
    public void setSuppressFlyout(boolean z) {
        this.mSuppressFlyout = z;
    }

    /* access modifiers changed from: 0000 */
    public FlyoutMessage getFlyoutMessage() {
        return this.mFlyoutMessage;
    }

    /* access modifiers changed from: 0000 */
    public boolean isOngoing() {
        return (this.mEntry.getSbn().getNotification().flags & 64) != 0;
    }

    /* access modifiers changed from: 0000 */
    public float getDesiredHeight(Context context) {
        BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        if (bubbleMetadata.getDesiredHeightResId() != 0) {
            return (float) getDimenForPackageUser(context, bubbleMetadata.getDesiredHeightResId(), this.mEntry.getSbn().getPackageName(), this.mEntry.getSbn().getUser().getIdentifier());
        }
        return ((float) bubbleMetadata.getDesiredHeight()) * context.getResources().getDisplayMetrics().density;
    }

    /* access modifiers changed from: 0000 */
    public String getDesiredHeightString() {
        BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        if (bubbleMetadata.getDesiredHeightResId() != 0) {
            return String.valueOf(bubbleMetadata.getDesiredHeightResId());
        }
        return String.valueOf(bubbleMetadata.getDesiredHeight());
    }

    /* access modifiers changed from: 0000 */
    public boolean usingShortcutInfo() {
        return this.mEntry.getBubbleMetadata().getShortcutId() != null;
    }

    /* access modifiers changed from: 0000 */
    public PendingIntent getBubbleIntent() {
        BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        if (bubbleMetadata != null) {
            return bubbleMetadata.getBubbleIntent();
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public Intent getSettingsIntent() {
        Intent intent = new Intent("android.settings.APP_NOTIFICATION_BUBBLE_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        intent.putExtra("app_uid", this.mEntry.getSbn().getUid());
        intent.addFlags(134217728);
        intent.addFlags(268435456);
        intent.addFlags(536870912);
        return intent;
    }

    private int getDimenForPackageUser(Context context, int i, String str, int i2) {
        PackageManager packageManager = context.getPackageManager();
        if (str != null) {
            if (i2 == -1) {
                i2 = 0;
            }
            try {
                return packageManager.getResourcesForApplicationAsUser(str, i2).getDimensionPixelSize(i);
            } catch (NameNotFoundException unused) {
            } catch (NotFoundException e) {
                Log.e("Bubble", "Couldn't find desired height res id", e);
            }
        }
        return 0;
    }

    private boolean shouldSuppressNotification() {
        return this.mEntry.getBubbleMetadata() != null && this.mEntry.getBubbleMetadata().isNotificationSuppressed();
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldAutoExpand() {
        BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        return bubbleMetadata != null && bubbleMetadata.getAutoExpandBubble();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bubble{");
        sb.append(this.mKey);
        sb.append('}');
        return sb.toString();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("key: ");
        printWriter.println(this.mKey);
        printWriter.print("  showInShade:   ");
        printWriter.println(showInShade());
        printWriter.print("  showDot:       ");
        printWriter.println(showDot());
        printWriter.print("  showFlyout:    ");
        printWriter.println(showFlyout());
        printWriter.print("  desiredHeight: ");
        printWriter.println(getDesiredHeightString());
        printWriter.print("  suppressNotif: ");
        printWriter.println(shouldSuppressNotification());
        printWriter.print("  autoExpand:    ");
        printWriter.println(shouldAutoExpand());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Bubble)) {
            return false;
        }
        return Objects.equals(this.mKey, ((Bubble) obj).mKey);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.mKey});
    }

    public void logUIEvent(int i, int i2, float f, float f2, int i3) {
        if (getEntry() == null || getEntry().getSbn() == null) {
            SysUiStatsLog.write(149, null, null, 0, 0, i, i2, f, f2, false, false, false);
            return;
        }
        StatusBarNotification sbn = getEntry().getSbn();
        SysUiStatsLog.write(149, sbn.getPackageName(), sbn.getNotification().getChannelId(), sbn.getId(), i3, i, i2, f, f2, showInShade(), isOngoing(), false);
    }
}
