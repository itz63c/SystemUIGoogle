package com.android.systemui.statusbar.notification.row;

import android.app.INotificationManager;
import android.app.Notification.MessagingStyle;
import android.app.Notification.MessagingStyle.Message;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.notification.ConversationIconFactory;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent;
import com.android.systemui.statusbar.phone.ShadeController;

public class NotificationConversationInfo extends LinearLayout implements GutsContent {
    private String mAppName;
    private int mAppUid;
    private BubbleController mBubbleController;
    private String mConversationId;
    private String mDelegatePkg;
    private NotificationEntry mEntry;
    private NotificationGuts mGutsContainer;
    private INotificationManager mINotificationManager;
    private ConversationIconFactory mIconFactory;
    private boolean mIsBubbleable;
    private boolean mIsDeviceProvisioned;
    private NotificationChannel mNotificationChannel;
    private OnClickListener mOnBubbleClick = new OnClickListener() {
        public final void onClick(View view) {
            NotificationConversationInfo.this.lambda$new$0$NotificationConversationInfo(view);
        }
    };
    private OnClickListener mOnFavoriteClick = new OnClickListener() {
        public final void onClick(View view) {
            NotificationConversationInfo.this.lambda$new$2$NotificationConversationInfo(view);
        }
    };
    private OnClickListener mOnHomeClick = new OnClickListener() {
        public final void onClick(View view) {
            NotificationConversationInfo.this.lambda$new$1$NotificationConversationInfo(view);
        }
    };
    private OnClickListener mOnMuteClick = new OnClickListener() {
        public final void onClick(View view) {
            NotificationConversationInfo.this.lambda$new$4$NotificationConversationInfo(view);
        }
    };
    private OnSettingsClickListener mOnSettingsClickListener;
    private OnClickListener mOnSnoozeClick = new OnClickListener() {
        public final void onClick(View view) {
            NotificationConversationInfo.this.lambda$new$3$NotificationConversationInfo(view);
        }
    };
    private OnSnoozeClickListener mOnSnoozeClickListener;
    private String mPackageName;
    private PackageManager mPm;
    private StatusBarNotification mSbn;
    private int mSelectedAction = -1;
    private ShadeController mShadeController;
    private ShortcutInfo mShortcutInfo;
    ShortcutManager mShortcutManager;
    @VisibleForTesting
    boolean mSkipPost = false;
    private boolean mStartedAsBubble;
    private VisualStabilityManager mVisualStabilityManager;

    public interface OnAppSettingsClickListener {
    }

    public interface OnSettingsClickListener {
        void onClick(View view, NotificationChannel notificationChannel, int i);
    }

    public interface OnSnoozeClickListener {
        void onClick(View view, int i);
    }

    class UpdateChannelRunnable implements Runnable {
        private final int mAction;
        private final String mAppPkg;
        private final int mAppUid;
        private NotificationChannel mChannelToUpdate;
        private final INotificationManager mINotificationManager;

        public UpdateChannelRunnable(INotificationManager iNotificationManager, String str, int i, int i2, NotificationChannel notificationChannel) {
            this.mINotificationManager = iNotificationManager;
            this.mAppPkg = str;
            this.mAppUid = i;
            this.mChannelToUpdate = notificationChannel;
            this.mAction = i2;
        }

        /* JADX WARNING: Removed duplicated region for block: B:40:0x0081 A[Catch:{ RemoteException -> 0x008d }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r7 = this;
                int r0 = r7.mAction     // Catch:{ RemoteException -> 0x008d }
                r1 = 3
                r2 = 0
                r3 = 1
                if (r0 == r3) goto L_0x000d
                int r0 = r7.mAction     // Catch:{ RemoteException -> 0x008d }
                if (r0 == r1) goto L_0x000d
                r0 = r3
                goto L_0x000e
            L_0x000d:
                r0 = r2
            L_0x000e:
                int r4 = r7.mAction     // Catch:{ RemoteException -> 0x008d }
                if (r4 == 0) goto L_0x006a
                r5 = 2
                if (r4 == r5) goto L_0x0045
                r6 = 4
                if (r4 == r6) goto L_0x001c
                r1 = 6
                if (r4 == r1) goto L_0x006a
                goto L_0x0068
            L_0x001c:
                android.app.NotificationChannel r2 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                int r2 = r2.getImportance()     // Catch:{ RemoteException -> 0x008d }
                r3 = -1000(0xfffffffffffffc18, float:NaN)
                if (r2 == r3) goto L_0x003f
                android.app.NotificationChannel r2 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                int r2 = r2.getImportance()     // Catch:{ RemoteException -> 0x008d }
                if (r2 < r1) goto L_0x002f
                goto L_0x003f
            L_0x002f:
                android.app.NotificationChannel r2 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                android.app.NotificationChannel r3 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                int r3 = r3.getOriginalImportance()     // Catch:{ RemoteException -> 0x008d }
                int r1 = java.lang.Math.max(r3, r1)     // Catch:{ RemoteException -> 0x008d }
                r2.setImportance(r1)     // Catch:{ RemoteException -> 0x008d }
                goto L_0x0068
            L_0x003f:
                android.app.NotificationChannel r1 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                r1.setImportance(r5)     // Catch:{ RemoteException -> 0x008d }
                goto L_0x0068
            L_0x0045:
                android.app.NotificationChannel r1 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                android.app.NotificationChannel r4 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                boolean r4 = r4.isImportantConversation()     // Catch:{ RemoteException -> 0x008d }
                if (r4 != 0) goto L_0x0050
                r2 = r3
            L_0x0050:
                r1.setImportantConversation(r2)     // Catch:{ RemoteException -> 0x008d }
                android.app.NotificationChannel r1 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                boolean r1 = r1.isImportantConversation()     // Catch:{ RemoteException -> 0x008d }
                if (r1 == 0) goto L_0x0068
                com.android.systemui.statusbar.notification.row.NotificationConversationInfo r1 = com.android.systemui.statusbar.notification.row.NotificationConversationInfo.this     // Catch:{ RemoteException -> 0x008d }
                boolean r1 = r1.bubbleImportantConversations()     // Catch:{ RemoteException -> 0x008d }
                if (r1 == 0) goto L_0x0068
                android.app.NotificationChannel r1 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                r1.setAllowBubbles(r3)     // Catch:{ RemoteException -> 0x008d }
            L_0x0068:
                r2 = r0
                goto L_0x007f
            L_0x006a:
                int r0 = r7.mAction     // Catch:{ RemoteException -> 0x008d }
                if (r0 != 0) goto L_0x0070
                r0 = r3
                goto L_0x0071
            L_0x0070:
                r0 = r2
            L_0x0071:
                android.app.NotificationChannel r1 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                boolean r1 = r1.canBubble()     // Catch:{ RemoteException -> 0x008d }
                if (r1 == r0) goto L_0x007f
                android.app.NotificationChannel r1 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                r1.setAllowBubbles(r0)     // Catch:{ RemoteException -> 0x008d }
                r2 = r3
            L_0x007f:
                if (r2 == 0) goto L_0x0095
                android.app.INotificationManager r0 = r7.mINotificationManager     // Catch:{ RemoteException -> 0x008d }
                java.lang.String r1 = r7.mAppPkg     // Catch:{ RemoteException -> 0x008d }
                int r2 = r7.mAppUid     // Catch:{ RemoteException -> 0x008d }
                android.app.NotificationChannel r3 = r7.mChannelToUpdate     // Catch:{ RemoteException -> 0x008d }
                r0.updateNotificationChannelForPackage(r1, r2, r3)     // Catch:{ RemoteException -> 0x008d }
                goto L_0x0095
            L_0x008d:
                r0 = move-exception
                java.lang.String r1 = "ConversationGuts"
                java.lang.String r2 = "Unable to update notification channel"
                android.util.Log.e(r1, r2, r0)
            L_0x0095:
                com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationConversationInfo$UpdateChannelRunnable$_TdB-ndU_iWORDLe32ALCWoJYfU r0 = new com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationConversationInfo$UpdateChannelRunnable$_TdB-ndU_iWORDLe32ALCWoJYfU
                r0.<init>()
                com.android.settingslib.utils.ThreadUtils.postOnMainThread(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.NotificationConversationInfo.UpdateChannelRunnable.run():void");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$run$0 */
        public /* synthetic */ void lambda$run$0$NotificationConversationInfo$UpdateChannelRunnable() {
            NotificationConversationInfo.this.updateToggleActions();
        }
    }

    public View getContentView() {
        return this;
    }

    @VisibleForTesting
    public boolean isAnimating() {
        return false;
    }

    public void onFinishedClosing() {
    }

    public boolean willBeRemoved() {
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NotificationConversationInfo(View view) {
        this.mSelectedAction = this.mStartedAsBubble ? 6 : 0;
        if (this.mStartedAsBubble) {
            this.mBubbleController.onUserDemotedBubbleFromNotification(this.mEntry);
        } else {
            this.mBubbleController.onUserCreatedBubbleFromNotification(this.mEntry);
        }
        closeControls(view, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$NotificationConversationInfo(View view) {
        this.mSelectedAction = 1;
        this.mShortcutManager.requestPinShortcut(this.mShortcutInfo, null);
        this.mShadeController.animateCollapsePanels();
        closeControls(view, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$NotificationConversationInfo(View view) {
        this.mSelectedAction = 2;
        updateChannel();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$NotificationConversationInfo(View view) {
        this.mSelectedAction = 3;
        this.mOnSnoozeClickListener.onClick(view, 1);
        closeControls(view, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$NotificationConversationInfo(View view) {
        this.mSelectedAction = 4;
        updateChannel();
    }

    public NotificationConversationInfo(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void bindNotification(ShortcutManager shortcutManager, LauncherApps launcherApps, PackageManager packageManager, INotificationManager iNotificationManager, VisualStabilityManager visualStabilityManager, String str, NotificationChannel notificationChannel, NotificationEntry notificationEntry, OnSettingsClickListener onSettingsClickListener, OnAppSettingsClickListener onAppSettingsClickListener, OnSnoozeClickListener onSnoozeClickListener, ConversationIconFactory conversationIconFactory, boolean z) {
        this.mSelectedAction = -1;
        this.mINotificationManager = iNotificationManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mBubbleController = (BubbleController) Dependency.get(BubbleController.class);
        this.mPackageName = str;
        this.mEntry = notificationEntry;
        StatusBarNotification sbn = notificationEntry.getSbn();
        this.mSbn = sbn;
        this.mPm = packageManager;
        this.mAppName = this.mPackageName;
        this.mOnSettingsClickListener = onSettingsClickListener;
        this.mNotificationChannel = notificationChannel;
        this.mAppUid = sbn.getUid();
        this.mDelegatePkg = this.mSbn.getOpPkg();
        this.mIsDeviceProvisioned = z;
        this.mOnSnoozeClickListener = onSnoozeClickListener;
        this.mShadeController = (ShadeController) Dependency.get(ShadeController.class);
        this.mIconFactory = conversationIconFactory;
        this.mShortcutManager = shortcutManager;
        this.mConversationId = this.mNotificationChannel.getConversationId();
        if (TextUtils.isEmpty(this.mNotificationChannel.getConversationId())) {
            this.mConversationId = this.mSbn.getShortcutId(this.mContext);
        }
        if (!TextUtils.isEmpty(this.mConversationId)) {
            this.mShortcutInfo = notificationEntry.getRanking().getShortcutInfo();
            boolean z2 = true;
            if (this.mEntry.getBubbleMetadata() == null || Global.getInt(this.mContext.getContentResolver(), "notification_bubbles", 0) != 1) {
                z2 = false;
            }
            this.mIsBubbleable = z2;
            this.mStartedAsBubble = this.mEntry.isBubble();
            createConversationChannelIfNeeded();
            bindHeader();
            bindActions();
            return;
        }
        throw new IllegalArgumentException("Does not have required information");
    }

    /* access modifiers changed from: 0000 */
    public void createConversationChannelIfNeeded() {
        if (TextUtils.isEmpty(this.mNotificationChannel.getConversationId())) {
            try {
                this.mNotificationChannel.setName(this.mContext.getString(C2017R$string.notification_summary_message_format, new Object[]{getName(), this.mNotificationChannel.getName()}));
                this.mINotificationManager.createConversationNotificationChannelForPackage(this.mPackageName, this.mAppUid, this.mSbn.getKey(), this.mNotificationChannel, this.mConversationId);
                this.mNotificationChannel = this.mINotificationManager.getConversationNotificationChannel(this.mContext.getOpPackageName(), UserHandle.getUserId(this.mAppUid), this.mPackageName, this.mNotificationChannel.getId(), false, this.mConversationId);
            } catch (RemoteException e) {
                Slog.e("ConversationGuts", "Could not create conversation channel", e);
            }
        }
    }

    private void bindActions() {
        Button button = (Button) findViewById(C2011R$id.bubble);
        int i = 0;
        button.setVisibility(this.mIsBubbleable ? 0 : 8);
        button.setOnClickListener(this.mOnBubbleClick);
        if (this.mStartedAsBubble) {
            button.setText(C2017R$string.notification_conversation_unbubble);
        } else {
            button.setText(C2017R$string.notification_conversation_bubble);
        }
        Button button2 = (Button) findViewById(C2011R$id.home);
        button2.setOnClickListener(this.mOnHomeClick);
        button2.setVisibility((this.mShortcutInfo == null || !this.mShortcutManager.isRequestPinShortcutSupported()) ? 8 : 0);
        findViewById(C2011R$id.fave).setOnClickListener(this.mOnFavoriteClick);
        ((Button) findViewById(C2011R$id.snooze)).setOnClickListener(this.mOnSnoozeClick);
        findViewById(C2011R$id.mute).setOnClickListener(this.mOnMuteClick);
        View findViewById = findViewById(C2011R$id.info);
        findViewById.setOnClickListener(getSettingsOnClickListener());
        if (!findViewById.hasOnClickListeners()) {
            i = 8;
        }
        findViewById.setVisibility(i);
        updateToggleActions();
    }

    private void bindHeader() {
        bindConversationDetails();
        bindDelegate();
    }

    private OnClickListener getSettingsOnClickListener() {
        int i = this.mAppUid;
        if (i < 0 || this.mOnSettingsClickListener == null || !this.mIsDeviceProvisioned) {
            return null;
        }
        return new OnClickListener(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                NotificationConversationInfo.this.lambda$getSettingsOnClickListener$5$NotificationConversationInfo(this.f$1, view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getSettingsOnClickListener$5 */
    public /* synthetic */ void lambda$getSettingsOnClickListener$5$NotificationConversationInfo(int i, View view) {
        this.mOnSettingsClickListener.onClick(view, this.mNotificationChannel, i);
    }

    private void bindConversationDetails() {
        ((TextView) findViewById(C2011R$id.parent_channel_name)).setText(this.mNotificationChannel.getName());
        bindGroup();
        bindPackage();
        bindIcon();
    }

    private void bindIcon() {
        ImageView imageView = (ImageView) findViewById(C2011R$id.conversation_icon);
        ShortcutInfo shortcutInfo = this.mShortcutInfo;
        if (shortcutInfo != null) {
            imageView.setImageDrawable(this.mIconFactory.getConversationDrawable(shortcutInfo, this.mPackageName, this.mAppUid, this.mNotificationChannel.isImportantConversation()));
        } else if (this.mSbn.getNotification().extras.getBoolean("android.isGroupConversation", false)) {
            imageView.setImageDrawable(this.mPm.getDefaultActivityIcon());
        } else {
            Message findLatestIncomingMessage = MessagingStyle.findLatestIncomingMessage(Message.getMessagesFromBundleArray((Parcelable[]) this.mSbn.getNotification().extras.get("android.messages")));
            if (findLatestIncomingMessage.getSenderPerson().getIcon() != null) {
                imageView.setImageIcon(findLatestIncomingMessage.getSenderPerson().getIcon());
            } else {
                imageView.setImageDrawable(this.mPm.getDefaultActivityIcon());
            }
        }
    }

    private String getName() {
        ShortcutInfo shortcutInfo = this.mShortcutInfo;
        if (shortcutInfo != null) {
            return shortcutInfo.getShortLabel().toString();
        }
        Bundle bundle = this.mSbn.getNotification().extras;
        String string = bundle.getString("android.conversationTitle");
        if (TextUtils.isEmpty(string)) {
            string = bundle.getString("android.title");
        }
        return string;
    }

    private void bindPackage() {
        try {
            ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(this.mPackageName, 795136);
            if (applicationInfo != null) {
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(applicationInfo));
            }
        } catch (NameNotFoundException unused) {
        }
        ((TextView) findViewById(C2011R$id.pkg_name)).setText(this.mAppName);
    }

    /* access modifiers changed from: private */
    public boolean bubbleImportantConversations() {
        return Secure.getInt(this.mContext.getContentResolver(), "bubble_important_conversations", 1) == 1;
    }

    private void bindDelegate() {
        TextView textView = (TextView) findViewById(C2011R$id.delegate_name);
        if (!TextUtils.equals(this.mPackageName, this.mDelegatePkg)) {
            textView.setVisibility(0);
        } else {
            textView.setVisibility(8);
        }
    }

    private void bindGroup() {
        NotificationChannel notificationChannel = this.mNotificationChannel;
        CharSequence charSequence = null;
        if (!(notificationChannel == null || notificationChannel.getGroup() == null)) {
            try {
                NotificationChannelGroup notificationChannelGroupForPackage = this.mINotificationManager.getNotificationChannelGroupForPackage(this.mNotificationChannel.getGroup(), this.mPackageName, this.mAppUid);
                if (notificationChannelGroupForPackage != null) {
                    charSequence = notificationChannelGroupForPackage.getName();
                }
            } catch (RemoteException unused) {
            }
        }
        TextView textView = (TextView) findViewById(C2011R$id.group_name);
        View findViewById = findViewById(C2011R$id.group_divider);
        if (charSequence != null) {
            textView.setText(charSequence);
            textView.setVisibility(0);
            findViewById.setVisibility(0);
            return;
        }
        textView.setVisibility(8);
        findViewById.setVisibility(8);
    }

    public boolean post(Runnable runnable) {
        if (!this.mSkipPost) {
            return super.post(runnable);
        }
        runnable.run();
        return true;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (this.mGutsContainer != null && accessibilityEvent.getEventType() == 32) {
            if (this.mGutsContainer.isExposed()) {
                accessibilityEvent.getText().add(this.mContext.getString(C2017R$string.notification_channel_controls_opened_accessibility, new Object[]{this.mAppName}));
                return;
            }
            accessibilityEvent.getText().add(this.mContext.getString(C2017R$string.notification_channel_controls_closed_accessibility, new Object[]{this.mAppName}));
        }
    }

    /* access modifiers changed from: private */
    public void updateToggleActions() {
        ImageButton imageButton = (ImageButton) findViewById(C2011R$id.fave);
        if (this.mNotificationChannel.isImportantConversation()) {
            imageButton.setContentDescription(this.mContext.getString(C2017R$string.notification_conversation_favorite));
            imageButton.setImageResource(C2010R$drawable.ic_important);
        } else {
            imageButton.setContentDescription(this.mContext.getString(C2017R$string.notification_conversation_unfavorite));
            imageButton.setImageResource(C2010R$drawable.ic_important_outline);
        }
        ImageButton imageButton2 = (ImageButton) findViewById(C2011R$id.mute);
        if (this.mNotificationChannel.getImportance() >= 3 || this.mNotificationChannel.getImportance() == -1000) {
            imageButton2.setContentDescription(this.mContext.getString(C2017R$string.notification_conversation_unmute));
            imageButton2.setImageResource(C2010R$drawable.ic_notifications_alert);
        } else {
            imageButton2.setContentDescription(this.mContext.getString(C2017R$string.notification_conversation_mute));
            imageButton2.setImageResource(C2010R$drawable.ic_notifications_silence);
        }
        bindIcon();
    }

    private void updateChannel() {
        Handler handler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
        UpdateChannelRunnable updateChannelRunnable = new UpdateChannelRunnable(this.mINotificationManager, this.mPackageName, this.mAppUid, this.mSelectedAction, this.mNotificationChannel);
        handler.post(updateChannelRunnable);
        this.mVisualStabilityManager.temporarilyAllowReordering();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void closeControls(View view, boolean z) {
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        this.mGutsContainer.getLocationOnScreen(iArr);
        view.getLocationOnScreen(iArr2);
        int width = view.getWidth() / 2;
        this.mGutsContainer.closeControls((iArr2[0] - iArr[0]) + width, (iArr2[1] - iArr[1]) + (view.getHeight() / 2), z, false);
    }

    public void setGutsParent(NotificationGuts notificationGuts) {
        this.mGutsContainer = notificationGuts;
    }

    public boolean shouldBeSaved() {
        int i = this.mSelectedAction;
        return (i <= -1 || i == 2 || i == 4) ? false : true;
    }

    public boolean handleCloseControls(boolean z, boolean z2) {
        if (z && this.mSelectedAction > -1) {
            updateChannel();
        }
        return false;
    }

    public int getActualHeight() {
        return getHeight();
    }
}
