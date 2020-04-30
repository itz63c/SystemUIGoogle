package com.android.systemui.statusbar.notification.collection.inflation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.ViewGroup;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.NotificationClicker;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.icon.IconManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline.BindCallback;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.notification.row.RowInflaterTask;
import com.android.systemui.statusbar.notification.row.RowInflaterTask.RowInflationFinishedListener;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Objects;
import javax.inject.Provider;

public class NotificationRowBinderImpl implements NotificationRowBinder {
    private BindRowCallback mBindRowCallback;
    private final Context mContext;
    private final Builder mExpandableNotificationRowComponentBuilder;
    private final IconManager mIconManager;
    private InflationCallback mInflationCallback;
    private NotificationListContainer mListContainer;
    private final NotificationMessagingUtil mMessagingUtil;
    private final NotifBindPipeline mNotifBindPipeline;
    private NotificationClicker mNotificationClicker;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationLockscreenUserManager mNotificationLockscreenUserManager;
    private final NotificationRemoteInputManager mNotificationRemoteInputManager;
    private NotificationPresenter mPresenter;
    private final RowContentBindStage mRowContentBindStage;
    private final Provider<RowInflaterTask> mRowInflaterTaskProvider;

    public interface BindRowCallback {
        void onBindRow(NotificationEntry notificationEntry, PackageManager packageManager, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow);
    }

    public NotificationRowBinderImpl(Context context, NotificationMessagingUtil notificationMessagingUtil, NotificationRemoteInputManager notificationRemoteInputManager, NotificationLockscreenUserManager notificationLockscreenUserManager, NotifBindPipeline notifBindPipeline, RowContentBindStage rowContentBindStage, NotificationInterruptStateProvider notificationInterruptStateProvider, Provider<RowInflaterTask> provider, Builder builder, IconManager iconManager) {
        this.mContext = context;
        this.mNotifBindPipeline = notifBindPipeline;
        this.mRowContentBindStage = rowContentBindStage;
        this.mMessagingUtil = notificationMessagingUtil;
        this.mNotificationRemoteInputManager = notificationRemoteInputManager;
        this.mNotificationLockscreenUserManager = notificationLockscreenUserManager;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mRowInflaterTaskProvider = provider;
        this.mExpandableNotificationRowComponentBuilder = builder;
        this.mIconManager = iconManager;
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter, NotificationListContainer notificationListContainer, BindRowCallback bindRowCallback) {
        this.mPresenter = notificationPresenter;
        this.mListContainer = notificationListContainer;
        this.mBindRowCallback = bindRowCallback;
        this.mIconManager.attach();
    }

    public void setInflationCallback(InflationCallback inflationCallback) {
        this.mInflationCallback = inflationCallback;
    }

    public void setNotificationClicker(NotificationClicker notificationClicker) {
        this.mNotificationClicker = notificationClicker;
    }

    public void inflateViews(NotificationEntry notificationEntry, Runnable runnable) throws InflationException {
        ViewGroup viewParentForNotification = this.mListContainer.getViewParentForNotification(notificationEntry);
        PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, notificationEntry.getSbn().getUser().getIdentifier());
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (notificationEntry.rowExists()) {
            this.mIconManager.updateIcons(notificationEntry);
            notificationEntry.reset();
            updateNotification(notificationEntry, packageManagerForUser, sbn, notificationEntry.getRow());
            notificationEntry.getRowController().setOnDismissRunnable(runnable);
            return;
        }
        this.mIconManager.createIcons(notificationEntry);
        RowInflaterTask rowInflaterTask = (RowInflaterTask) this.mRowInflaterTaskProvider.get();
        Context context = this.mContext;
        $$Lambda$NotificationRowBinderImpl$02ioNJJPa5d3UCwTG0KVIOT4blk r3 = new RowInflationFinishedListener(notificationEntry, runnable, packageManagerForUser, sbn) {
            public final /* synthetic */ NotificationEntry f$1;
            public final /* synthetic */ Runnable f$2;
            public final /* synthetic */ PackageManager f$3;
            public final /* synthetic */ StatusBarNotification f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onInflationFinished(ExpandableNotificationRow expandableNotificationRow) {
                NotificationRowBinderImpl.this.lambda$inflateViews$0$NotificationRowBinderImpl(this.f$1, this.f$2, this.f$3, this.f$4, expandableNotificationRow);
            }
        };
        rowInflaterTask.inflate(context, viewParentForNotification, notificationEntry, r3);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inflateViews$0 */
    public /* synthetic */ void lambda$inflateViews$0$NotificationRowBinderImpl(NotificationEntry notificationEntry, Runnable runnable, PackageManager packageManager, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        ExpandableNotificationRowController expandableNotificationRowController = this.mExpandableNotificationRowComponentBuilder.expandableNotificationRow(expandableNotificationRow).notificationEntry(notificationEntry).onDismissRunnable(runnable).inflationCallback(this.mInflationCallback).rowContentBindStage(this.mRowContentBindStage).onExpandClickListener(this.mPresenter).build().getExpandableNotificationRowController();
        expandableNotificationRowController.init();
        notificationEntry.setRowController(expandableNotificationRowController);
        bindRow(notificationEntry, packageManager, statusBarNotification, expandableNotificationRow);
        updateNotification(notificationEntry, packageManager, statusBarNotification, expandableNotificationRow);
    }

    private void bindRow(NotificationEntry notificationEntry, PackageManager packageManager, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        this.mListContainer.bindRow(expandableNotificationRow);
        this.mNotificationRemoteInputManager.bindRow(expandableNotificationRow);
        notificationEntry.setRow(expandableNotificationRow);
        expandableNotificationRow.setEntry(notificationEntry);
        this.mNotifBindPipeline.manageRow(notificationEntry, expandableNotificationRow);
        this.mBindRowCallback.onBindRow(notificationEntry, packageManager, statusBarNotification, expandableNotificationRow);
    }

    public void onNotificationRankingUpdated(NotificationEntry notificationEntry, Integer num, NotificationUiAdjustment notificationUiAdjustment, NotificationUiAdjustment notificationUiAdjustment2) {
        if (NotificationUiAdjustment.needReinflate(notificationUiAdjustment, notificationUiAdjustment2)) {
            if (notificationEntry.rowExists()) {
                notificationEntry.reset();
                updateNotification(notificationEntry, StatusBar.getPackageManagerForUser(this.mContext, notificationEntry.getSbn().getUser().getIdentifier()), notificationEntry.getSbn(), notificationEntry.getRow());
            }
        } else if (num != null && notificationEntry.getImportance() != num.intValue() && notificationEntry.rowExists()) {
            notificationEntry.getRow().onNotificationRankingUpdated();
        }
    }

    private void updateNotification(NotificationEntry notificationEntry, PackageManager packageManager, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        try {
            notificationEntry.targetSdk = packageManager.getApplicationInfo(statusBarNotification.getPackageName(), 0).targetSdkVersion;
        } catch (NameNotFoundException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed looking up ApplicationInfo for ");
            sb.append(statusBarNotification.getPackageName());
            Log.e("NotificationViewManager", sb.toString(), e);
        }
        int i = notificationEntry.targetSdk;
        expandableNotificationRow.setLegacy(i >= 9 && i < 21);
        this.mIconManager.updateIconTags(notificationEntry, notificationEntry.targetSdk);
        expandableNotificationRow.setOnActivatedListener(this.mPresenter);
        boolean isImportantMessaging = this.mMessagingUtil.isImportantMessaging(statusBarNotification, notificationEntry.getImportance());
        boolean z = isImportantMessaging && !this.mPresenter.isPresenterFullyCollapsed();
        boolean isAmbient = notificationEntry.isAmbient();
        RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mRowContentBindStage.getStageParams(notificationEntry);
        rowContentBindParams.setUseIncreasedCollapsedHeight(isImportantMessaging);
        rowContentBindParams.setUseIncreasedHeadsUpHeight(z);
        rowContentBindParams.setUseLowPriority(notificationEntry.isAmbient());
        if (this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
            rowContentBindParams.requireContentViews(4);
        }
        expandableNotificationRow.setNeedsRedaction(this.mNotificationLockscreenUserManager.needsRedaction(notificationEntry));
        rowContentBindParams.rebindAllContentViews();
        RowContentBindStage rowContentBindStage = this.mRowContentBindStage;
        $$Lambda$NotificationRowBinderImpl$6gwRMIPuV8Kz6rP5X5oGH5EYd3E r3 = new BindCallback(expandableNotificationRow, isImportantMessaging, z, isAmbient) {
            public final /* synthetic */ ExpandableNotificationRow f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ boolean f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onBindFinished(NotificationEntry notificationEntry) {
                NotificationRowBinderImpl.this.lambda$updateNotification$1$NotificationRowBinderImpl(this.f$1, this.f$2, this.f$3, this.f$4, notificationEntry);
            }
        };
        rowContentBindStage.requestRebind(notificationEntry, r3);
        NotificationClicker notificationClicker = this.mNotificationClicker;
        Objects.requireNonNull(notificationClicker);
        notificationClicker.register(expandableNotificationRow, statusBarNotification);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateNotification$1 */
    public /* synthetic */ void lambda$updateNotification$1$NotificationRowBinderImpl(ExpandableNotificationRow expandableNotificationRow, boolean z, boolean z2, boolean z3, NotificationEntry notificationEntry) {
        expandableNotificationRow.setUsesIncreasedCollapsedHeight(z);
        expandableNotificationRow.setUsesIncreasedHeadsUpHeight(z2);
        expandableNotificationRow.setIsLowPriority(z3);
        this.mInflationCallback.onAsyncInflationFinished(notificationEntry);
    }
}
