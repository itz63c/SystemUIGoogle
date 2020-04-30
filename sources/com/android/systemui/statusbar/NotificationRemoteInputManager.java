package com.android.systemui.statusbar;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.RemoteInputHistoryItem;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RemoteViews;
import android.widget.RemoteViews.OnClickHandler;
import android.widget.RemoteViews.RemoteResponse;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLifetimeExtender.NotificationSafeToRemoveCallback;
import com.android.systemui.statusbar.RemoteInputController.Delegate;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry.EditedSuggestionInfo;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.policy.RemoteInputView;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class NotificationRemoteInputManager implements Dumpable {
    public static final boolean ENABLE_REMOTE_INPUT = SystemProperties.getBoolean("debug.enable_remote_input", true);
    public static boolean FORCE_REMOTE_INPUT_HISTORY = SystemProperties.getBoolean("debug.force_remoteinput_history", true);
    protected IStatusBarService mBarService;
    protected Callback mCallback;
    protected final Context mContext;
    protected final ArraySet<NotificationEntry> mEntriesKeptForRemoteInputActive = new ArraySet<>();
    /* access modifiers changed from: private */
    public final NotificationEntryManager mEntryManager;
    private final KeyguardManager mKeyguardManager;
    protected final ArraySet<String> mKeysKeptForRemoteInputHistory = new ArraySet<>();
    protected final ArrayList<NotificationLifetimeExtender> mLifetimeExtenders = new ArrayList<>();
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    /* access modifiers changed from: private */
    public final Handler mMainHandler;
    protected NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    private final OnClickHandler mOnClickHandler = new OnClickHandler() {
        public boolean onClickHandler(View view, PendingIntent pendingIntent, RemoteResponse remoteResponse) {
            ((StatusBar) NotificationRemoteInputManager.this.mStatusBarLazy.get()).wakeUpIfDozing(SystemClock.uptimeMillis(), view, "NOTIFICATION_CLICK");
            if (handleRemoteInput(view, pendingIntent)) {
                return true;
            }
            logActionClick(view, pendingIntent);
            try {
                ActivityManager.getService().resumeAppSwitches();
            } catch (RemoteException unused) {
            }
            return NotificationRemoteInputManager.this.mCallback.handleRemoteViewClick(view, pendingIntent, new ClickHandler(remoteResponse, view, pendingIntent) {
                public final /* synthetic */ RemoteResponse f$0;
                public final /* synthetic */ View f$1;
                public final /* synthetic */ PendingIntent f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final boolean handleClick() {
                    return C11511.lambda$onClickHandler$0(this.f$0, this.f$1, this.f$2);
                }
            });
        }

        static /* synthetic */ boolean lambda$onClickHandler$0(RemoteResponse remoteResponse, View view, PendingIntent pendingIntent) {
            Pair launchOptions = remoteResponse.getLaunchOptions(view);
            ((ActivityOptions) launchOptions.second).setLaunchWindowingMode(4);
            return RemoteViews.startPendingIntent(view, pendingIntent, launchOptions);
        }

        private void logActionClick(View view, PendingIntent pendingIntent) {
            Integer num = (Integer) view.getTag(16909198);
            if (num != null) {
                ViewParent parent = view.getParent();
                StatusBarNotification notificationForParent = getNotificationForParent(parent);
                String str = "NotifRemoteInputManager";
                if (notificationForParent == null) {
                    Log.w(str, "Couldn't determine notification for click.");
                    return;
                }
                String key = notificationForParent.getKey();
                int indexOfChild = (view.getId() != 16908691 || parent == null || !(parent instanceof ViewGroup)) ? -1 : ((ViewGroup) parent).indexOfChild(view);
                int activeNotificationsCount = NotificationRemoteInputManager.this.mEntryManager.getActiveNotificationsCount();
                int rank = NotificationRemoteInputManager.this.mEntryManager.getActiveNotificationUnfiltered(key).getRanking().getRank();
                Action[] actionArr = notificationForParent.getNotification().actions;
                if (actionArr == null || num.intValue() >= actionArr.length) {
                    Log.w(str, "statusBarNotification.getNotification().actions is null or invalid");
                    return;
                }
                Action action = notificationForParent.getNotification().actions[num.intValue()];
                if (!Objects.equals(action.actionIntent, pendingIntent)) {
                    Log.w(str, "actionIntent does not match");
                } else {
                    try {
                        NotificationRemoteInputManager.this.mBarService.onNotificationActionClick(key, indexOfChild, action, NotificationVisibility.obtain(key, rank, activeNotificationsCount, true, NotificationLogger.getNotificationLocation(NotificationRemoteInputManager.this.mEntryManager.getActiveNotificationUnfiltered(key))), false);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }

        private StatusBarNotification getNotificationForParent(ViewParent viewParent) {
            while (viewParent != null) {
                if (viewParent instanceof ExpandableNotificationRow) {
                    return ((ExpandableNotificationRow) viewParent).getEntry().getSbn();
                }
                viewParent = viewParent.getParent();
            }
            return null;
        }

        private boolean handleRemoteInput(View view, PendingIntent pendingIntent) {
            if (NotificationRemoteInputManager.this.mCallback.shouldHandleRemoteInput(view, pendingIntent)) {
                return true;
            }
            Object tag = view.getTag(16909318);
            RemoteInput[] remoteInputArr = tag instanceof RemoteInput[] ? (RemoteInput[]) tag : null;
            if (remoteInputArr == null) {
                return false;
            }
            RemoteInput remoteInput = null;
            for (RemoteInput remoteInput2 : remoteInputArr) {
                if (remoteInput2.getAllowFreeFormInput()) {
                    remoteInput = remoteInput2;
                }
            }
            if (remoteInput == null) {
                return false;
            }
            return NotificationRemoteInputManager.this.activateRemoteInput(view, remoteInputArr, remoteInput, pendingIntent, null);
        }
    };
    protected RemoteInputController mRemoteInputController;
    private final RemoteInputUriController mRemoteInputUriController;
    /* access modifiers changed from: private */
    public final SmartReplyController mSmartReplyController;
    /* access modifiers changed from: private */
    public final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    private final UserManager mUserManager;

    public interface Callback {
        boolean handleRemoteViewClick(View view, PendingIntent pendingIntent, ClickHandler clickHandler);

        void onLockedRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view);

        void onLockedWorkRemoteInput(int i, ExpandableNotificationRow expandableNotificationRow, View view);

        void onMakeExpandedVisibleForRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view);

        boolean shouldHandleRemoteInput(View view, PendingIntent pendingIntent);
    }

    public interface ClickHandler {
        boolean handleClick();
    }

    protected class RemoteInputActiveExtender extends RemoteInputExtender {
        protected RemoteInputActiveExtender() {
            super();
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.mRemoteInputController.isRemoteInputActive(notificationEntry);
        }

        public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
            if (z) {
                String str = "NotifRemoteInputManager";
                if (Log.isLoggable(str, 3)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Keeping notification around while remote input active ");
                    sb.append(notificationEntry.getKey());
                    Log.d(str, sb.toString());
                }
                NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.add(notificationEntry);
                return;
            }
            NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.remove(notificationEntry);
        }
    }

    protected abstract class RemoteInputExtender implements NotificationLifetimeExtender {
        protected RemoteInputExtender() {
        }

        public void setCallback(NotificationSafeToRemoveCallback notificationSafeToRemoveCallback) {
            NotificationRemoteInputManager notificationRemoteInputManager = NotificationRemoteInputManager.this;
            if (notificationRemoteInputManager.mNotificationLifetimeFinishedCallback == null) {
                notificationRemoteInputManager.mNotificationLifetimeFinishedCallback = notificationSafeToRemoveCallback;
            }
        }
    }

    protected class RemoteInputHistoryExtender extends RemoteInputExtender {
        protected RemoteInputHistoryExtender() {
            super();
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.shouldKeepForRemoteInputHistory(notificationEntry);
        }

        public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
            if (z) {
                CharSequence charSequence = notificationEntry.remoteInputText;
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = notificationEntry.remoteInputTextWhenReset;
                }
                StatusBarNotification rebuildNotificationWithRemoteInput = NotificationRemoteInputManager.this.rebuildNotificationWithRemoteInput(notificationEntry, charSequence, false, notificationEntry.remoteInputMimeType, notificationEntry.remoteInputUri);
                notificationEntry.onRemoteInputInserted();
                if (rebuildNotificationWithRemoteInput != null) {
                    NotificationRemoteInputManager.this.mEntryManager.updateNotification(rebuildNotificationWithRemoteInput, null);
                    if (!notificationEntry.isRemoved()) {
                        String str = "NotifRemoteInputManager";
                        if (Log.isLoggable(str, 3)) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Keeping notification around after sending remote input ");
                            sb.append(notificationEntry.getKey());
                            Log.d(str, sb.toString());
                        }
                        NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add(notificationEntry.getKey());
                    }
                }
            } else {
                NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove(notificationEntry.getKey());
            }
        }
    }

    protected class SmartReplyHistoryExtender extends RemoteInputExtender {
        protected SmartReplyHistoryExtender() {
            super();
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.shouldKeepForSmartReplyHistory(notificationEntry);
        }

        public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
            if (z) {
                StatusBarNotification rebuildNotificationForCanceledSmartReplies = NotificationRemoteInputManager.this.rebuildNotificationForCanceledSmartReplies(notificationEntry);
                if (rebuildNotificationForCanceledSmartReplies != null) {
                    NotificationRemoteInputManager.this.mEntryManager.updateNotification(rebuildNotificationForCanceledSmartReplies, null);
                    if (!notificationEntry.isRemoved()) {
                        String str = "NotifRemoteInputManager";
                        if (Log.isLoggable(str, 3)) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Keeping notification around after sending smart reply ");
                            sb.append(notificationEntry.getKey());
                            Log.d(str, sb.toString());
                        }
                        NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add(notificationEntry.getKey());
                    }
                }
            } else {
                NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove(notificationEntry.getKey());
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
            }
        }
    }

    public NotificationRemoteInputManager(Context context, NotificationLockscreenUserManager notificationLockscreenUserManager, SmartReplyController smartReplyController, NotificationEntryManager notificationEntryManager, Lazy<StatusBar> lazy, StatusBarStateController statusBarStateController, Handler handler, RemoteInputUriController remoteInputUriController) {
        this.mContext = context;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mSmartReplyController = smartReplyController;
        this.mEntryManager = notificationEntryManager;
        this.mStatusBarLazy = lazy;
        this.mMainHandler = handler;
        this.mBarService = Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        addLifetimeExtenders();
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mStatusBarStateController = statusBarStateController;
        this.mRemoteInputUriController = remoteInputUriController;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
                if (z && notificationEntry != null) {
                    NotificationRemoteInputManager.this.onPerformRemoveNotification(notificationEntry, notificationEntry.getKey());
                }
            }
        });
    }

    public void setUpWithCallback(Callback callback, Delegate delegate) {
        this.mCallback = callback;
        RemoteInputController remoteInputController = new RemoteInputController(delegate, this.mRemoteInputUriController);
        this.mRemoteInputController = remoteInputController;
        remoteInputController.addCallback(new com.android.systemui.statusbar.RemoteInputController.Callback() {
            public void onRemoteInputSent(NotificationEntry notificationEntry) {
                if (NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY && NotificationRemoteInputManager.this.isNotificationKeptForRemoteInputHistory(notificationEntry.getKey())) {
                    NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback.onSafeToRemove(notificationEntry.getKey());
                } else if (NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.contains(notificationEntry)) {
                    NotificationRemoteInputManager.this.mMainHandler.postDelayed(new Runnable(notificationEntry) {
                        public final /* synthetic */ NotificationEntry f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            C11533.this.lambda$onRemoteInputSent$0$NotificationRemoteInputManager$3(this.f$1);
                        }
                    }, 200);
                }
                try {
                    NotificationRemoteInputManager.this.mBarService.onNotificationDirectReplied(notificationEntry.getSbn().getKey());
                    if (notificationEntry.editedSuggestionInfo != null) {
                        NotificationRemoteInputManager.this.mBarService.onNotificationSmartReplySent(notificationEntry.getSbn().getKey(), notificationEntry.editedSuggestionInfo.index, notificationEntry.editedSuggestionInfo.originalText, NotificationLogger.getNotificationLocation(notificationEntry).toMetricsEventEnum(), !TextUtils.equals(notificationEntry.remoteInputText, notificationEntry.editedSuggestionInfo.originalText));
                    }
                } catch (RemoteException unused) {
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onRemoteInputSent$0 */
            public /* synthetic */ void lambda$onRemoteInputSent$0$NotificationRemoteInputManager$3(NotificationEntry notificationEntry) {
                if (NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.remove(notificationEntry)) {
                    NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback.onSafeToRemove(notificationEntry.getKey());
                }
            }
        });
        this.mSmartReplyController.setCallback(new com.android.systemui.statusbar.SmartReplyController.Callback() {
            public final void onSmartReplySent(NotificationEntry notificationEntry, CharSequence charSequence) {
                NotificationRemoteInputManager.this.lambda$setUpWithCallback$0$NotificationRemoteInputManager(notificationEntry, charSequence);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setUpWithCallback$0 */
    public /* synthetic */ void lambda$setUpWithCallback$0$NotificationRemoteInputManager(NotificationEntry notificationEntry, CharSequence charSequence) {
        this.mEntryManager.updateNotification(rebuildNotificationWithRemoteInput(notificationEntry, charSequence, true, null, null), null);
    }

    public boolean activateRemoteInput(View view, RemoteInput[] remoteInputArr, RemoteInput remoteInput, PendingIntent pendingIntent, EditedSuggestionInfo editedSuggestionInfo) {
        RemoteInputView remoteInputView;
        RemoteInputView remoteInputView2;
        ExpandableNotificationRow expandableNotificationRow;
        ViewParent parent = view.getParent();
        while (true) {
            remoteInputView = null;
            if (parent == null) {
                remoteInputView2 = null;
                expandableNotificationRow = null;
                break;
            }
            if (parent instanceof View) {
                View view2 = (View) parent;
                if (view2.isRootNamespace()) {
                    remoteInputView2 = findRemoteInputView(view2);
                    expandableNotificationRow = (ExpandableNotificationRow) view2.getTag(C2011R$id.row_tag_for_content_view);
                    break;
                }
            }
            parent = parent.getParent();
        }
        if (expandableNotificationRow == null) {
            return false;
        }
        expandableNotificationRow.setUserExpanded(true);
        if (!this.mLockscreenUserManager.shouldAllowLockscreenRemoteInput()) {
            int identifier = pendingIntent.getCreatorUserHandle().getIdentifier();
            if (this.mLockscreenUserManager.isLockscreenPublicMode(identifier) || this.mStatusBarStateController.getState() == 1) {
                this.mCallback.onLockedRemoteInput(expandableNotificationRow, view);
                return true;
            } else if (this.mUserManager.getUserInfo(identifier).isManagedProfile() && this.mKeyguardManager.isDeviceLocked(identifier)) {
                this.mCallback.onLockedWorkRemoteInput(identifier, expandableNotificationRow, view);
                return true;
            }
        }
        if (remoteInputView2 == null || remoteInputView2.isAttachedToWindow()) {
            remoteInputView = remoteInputView2;
        }
        if (remoteInputView == null) {
            remoteInputView = findRemoteInputView(expandableNotificationRow.getPrivateLayout().getExpandedChild());
            if (remoteInputView == null) {
                return false;
            }
        }
        if (remoteInputView == expandableNotificationRow.getPrivateLayout().getExpandedRemoteInput() && !expandableNotificationRow.getPrivateLayout().getExpandedChild().isShown()) {
            this.mCallback.onMakeExpandedVisibleForRemoteInput(expandableNotificationRow, view);
            return true;
        } else if (!remoteInputView.isAttachedToWindow()) {
            return false;
        } else {
            int width = view.getWidth();
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (textView.getLayout() != null) {
                    width = Math.min(width, ((int) textView.getLayout().getLineWidth(0)) + textView.getCompoundPaddingLeft() + textView.getCompoundPaddingRight());
                }
            }
            int left = view.getLeft() + (width / 2);
            int top = view.getTop() + (view.getHeight() / 2);
            int height = remoteInputView.getHeight() - top;
            int width2 = remoteInputView.getWidth() - left;
            remoteInputView.setRevealParameters(left, top, Math.max(Math.max(left + top, left + height), Math.max(width2 + top, width2 + height)));
            remoteInputView.setPendingIntent(pendingIntent);
            remoteInputView.setRemoteInput(remoteInputArr, remoteInput, editedSuggestionInfo);
            remoteInputView.focusAnimated();
            return true;
        }
    }

    private RemoteInputView findRemoteInputView(View view) {
        if (view == null) {
            return null;
        }
        return (RemoteInputView) view.findViewWithTag(RemoteInputView.VIEW_TAG);
    }

    /* access modifiers changed from: protected */
    public void addLifetimeExtenders() {
        this.mLifetimeExtenders.add(new RemoteInputHistoryExtender());
        this.mLifetimeExtenders.add(new SmartReplyHistoryExtender());
        this.mLifetimeExtenders.add(new RemoteInputActiveExtender());
    }

    public ArrayList<NotificationLifetimeExtender> getLifetimeExtenders() {
        return this.mLifetimeExtenders;
    }

    public RemoteInputController getController() {
        return this.mRemoteInputController;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void onPerformRemoveNotification(NotificationEntry notificationEntry, String str) {
        if (this.mKeysKeptForRemoteInputHistory.contains(str)) {
            this.mKeysKeptForRemoteInputHistory.remove(str);
        }
        if (this.mRemoteInputController.isRemoteInputActive(notificationEntry)) {
            this.mRemoteInputController.removeRemoteInput(notificationEntry, null);
        }
    }

    public void onPanelCollapsed() {
        for (int i = 0; i < this.mEntriesKeptForRemoteInputActive.size(); i++) {
            NotificationEntry notificationEntry = (NotificationEntry) this.mEntriesKeptForRemoteInputActive.valueAt(i);
            this.mRemoteInputController.removeRemoteInput(notificationEntry, null);
            NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationLifetimeFinishedCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(notificationEntry.getKey());
            }
        }
        this.mEntriesKeptForRemoteInputActive.clear();
    }

    public boolean isNotificationKeptForRemoteInputHistory(String str) {
        return this.mKeysKeptForRemoteInputHistory.contains(str);
    }

    public boolean shouldKeepForRemoteInputHistory(NotificationEntry notificationEntry) {
        boolean z = false;
        if (!FORCE_REMOTE_INPUT_HISTORY) {
            return false;
        }
        if (this.mRemoteInputController.isSpinning(notificationEntry.getKey()) || notificationEntry.hasJustSentRemoteInput()) {
            z = true;
        }
        return z;
    }

    public boolean shouldKeepForSmartReplyHistory(NotificationEntry notificationEntry) {
        if (!FORCE_REMOTE_INPUT_HISTORY) {
            return false;
        }
        return this.mSmartReplyController.isSendingSmartReply(notificationEntry.getKey());
    }

    public void checkRemoteInputOutside(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4 && motionEvent.getX() == 0.0f && motionEvent.getY() == 0.0f && this.mRemoteInputController.isRemoteInputActive()) {
            this.mRemoteInputController.closeRemoteInputs();
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public StatusBarNotification rebuildNotificationForCanceledSmartReplies(NotificationEntry notificationEntry) {
        return rebuildNotificationWithRemoteInput(notificationEntry, null, false, null, null);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public StatusBarNotification rebuildNotificationWithRemoteInput(NotificationEntry notificationEntry, CharSequence charSequence, boolean z, String str, Uri uri) {
        RemoteInputHistoryItem[] remoteInputHistoryItemArr;
        RemoteInputHistoryItem remoteInputHistoryItem;
        CharSequence charSequence2 = charSequence;
        Uri uri2 = uri;
        StatusBarNotification sbn = notificationEntry.getSbn();
        Builder recoverBuilder = Builder.recoverBuilder(this.mContext, sbn.getNotification().clone());
        if (!(charSequence2 == null && uri2 == null)) {
            RemoteInputHistoryItem[] parcelableArray = sbn.getNotification().extras.getParcelableArray("android.remoteInputHistoryItems");
            if (parcelableArray == null) {
                remoteInputHistoryItemArr = new RemoteInputHistoryItem[1];
            } else {
                RemoteInputHistoryItem[] remoteInputHistoryItemArr2 = new RemoteInputHistoryItem[(parcelableArray.length + 1)];
                System.arraycopy(parcelableArray, 0, remoteInputHistoryItemArr2, 1, parcelableArray.length);
                remoteInputHistoryItemArr = remoteInputHistoryItemArr2;
            }
            if (uri2 != null) {
                remoteInputHistoryItem = new RemoteInputHistoryItem(str, uri2, charSequence2);
            } else {
                remoteInputHistoryItem = new RemoteInputHistoryItem(charSequence2);
            }
            remoteInputHistoryItemArr[0] = remoteInputHistoryItem;
            recoverBuilder.setRemoteInputHistory(remoteInputHistoryItemArr);
        }
        recoverBuilder.setShowRemoteInputSpinner(z);
        recoverBuilder.setHideSmartReplies(true);
        Notification build = recoverBuilder.build();
        build.contentView = sbn.getNotification().contentView;
        build.bigContentView = sbn.getNotification().bigContentView;
        build.headsUpContentView = sbn.getNotification().headsUpContentView;
        StatusBarNotification statusBarNotification = new StatusBarNotification(sbn.getPackageName(), sbn.getOpPkg(), sbn.getId(), sbn.getTag(), sbn.getUid(), sbn.getInitialPid(), build, sbn.getUser(), sbn.getOverrideGroupKey(), sbn.getPostTime());
        return statusBarNotification;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationRemoteInputManager state:");
        printWriter.print("  mKeysKeptForRemoteInputHistory: ");
        printWriter.println(this.mKeysKeptForRemoteInputHistory);
        printWriter.print("  mEntriesKeptForRemoteInputActive: ");
        printWriter.println(this.mEntriesKeptForRemoteInputActive);
    }

    public void bindRow(ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setRemoteInputController(this.mRemoteInputController);
    }

    public OnClickHandler getRemoteViewsOnClickHandler() {
        return this.mOnClickHandler;
    }

    @VisibleForTesting
    public Set<NotificationEntry> getEntriesKeptForRemoteInputActive() {
        return this.mEntriesKeptForRemoteInputActive;
    }
}
