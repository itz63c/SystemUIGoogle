package com.android.systemui.statusbar;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class NotificationLockscreenUserManagerImpl implements Dumpable, NotificationLockscreenUserManager, StateListener {
    protected final BroadcastReceiver mAllUsersReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(intent.getAction()) && NotificationLockscreenUserManagerImpl.this.isCurrentProfile(getSendingUserId())) {
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED");
            }
        }
    };
    private boolean mAllowLockscreenRemoteInput;
    /* access modifiers changed from: private */
    public final IStatusBarService mBarService;
    protected final BroadcastReceiver mBaseBroadcastReceiver = new BroadcastReceiver() {
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r18, android.content.Intent r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                java.lang.String r2 = r19.getAction()
                int r3 = r2.hashCode()
                r4 = 0
                r5 = 5
                r6 = 4
                r7 = 3
                r8 = 2
                r9 = -1
                r10 = 1
                switch(r3) {
                    case -1238404651: goto L_0x0049;
                    case -864107122: goto L_0x003f;
                    case -598152660: goto L_0x0035;
                    case 833559602: goto L_0x002b;
                    case 959232034: goto L_0x0021;
                    case 1121780209: goto L_0x0017;
                    default: goto L_0x0016;
                }
            L_0x0016:
                goto L_0x0053
            L_0x0017:
                java.lang.String r3 = "android.intent.action.USER_ADDED"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x0053
                r2 = r10
                goto L_0x0054
            L_0x0021:
                java.lang.String r3 = "android.intent.action.USER_SWITCHED"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x0053
                r2 = r4
                goto L_0x0054
            L_0x002b:
                java.lang.String r3 = "android.intent.action.USER_UNLOCKED"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x0053
                r2 = r6
                goto L_0x0054
            L_0x0035:
                java.lang.String r3 = "com.android.systemui.statusbar.work_challenge_unlocked_notification_action"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x0053
                r2 = r5
                goto L_0x0054
            L_0x003f:
                java.lang.String r3 = "android.intent.action.MANAGED_PROFILE_AVAILABLE"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x0053
                r2 = r8
                goto L_0x0054
            L_0x0049:
                java.lang.String r3 = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x0053
                r2 = r7
                goto L_0x0054
            L_0x0053:
                r2 = r9
            L_0x0054:
                if (r2 == 0) goto L_0x00c4
                if (r2 == r10) goto L_0x00be
                if (r2 == r8) goto L_0x00be
                if (r2 == r7) goto L_0x00be
                if (r2 == r6) goto L_0x00b2
                if (r2 == r5) goto L_0x0062
                goto L_0x012e
            L_0x0062:
                java.lang.String r2 = "android.intent.extra.INTENT"
                android.os.Parcelable r2 = r1.getParcelableExtra(r2)
                r12 = r2
                android.content.IntentSender r12 = (android.content.IntentSender) r12
                java.lang.String r2 = "android.intent.extra.INDEX"
                java.lang.String r1 = r1.getStringExtra(r2)
                if (r12 == 0) goto L_0x007f
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r2 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this     // Catch:{ SendIntentException -> 0x007f }
                android.content.Context r11 = r2.mContext     // Catch:{ SendIntentException -> 0x007f }
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 0
                r11.startIntentSender(r12, r13, r14, r15, r16)     // Catch:{ SendIntentException -> 0x007f }
            L_0x007f:
                if (r1 == 0) goto L_0x012e
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r2 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.notification.NotificationEntryManager r2 = r2.getEntryManager()
                com.android.systemui.statusbar.notification.collection.NotificationEntry r2 = r2.getActiveNotificationUnfiltered(r1)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r3 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.notification.NotificationEntryManager r3 = r3.getEntryManager()
                int r3 = r3.getActiveNotificationsCount()
                if (r2 == 0) goto L_0x009f
                android.service.notification.NotificationListenerService$Ranking r4 = r2.getRanking()
                int r4 = r4.getRank()
            L_0x009f:
                com.android.internal.statusbar.NotificationVisibility$NotificationLocation r2 = com.android.systemui.statusbar.notification.logging.NotificationLogger.getNotificationLocation(r2)
                com.android.internal.statusbar.NotificationVisibility r2 = com.android.internal.statusbar.NotificationVisibility.obtain(r1, r4, r3, r10, r2)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r0 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this     // Catch:{ RemoteException -> 0x012e }
                com.android.internal.statusbar.IStatusBarService r0 = r0.mBarService     // Catch:{ RemoteException -> 0x012e }
                r0.onNotificationClick(r1, r2)     // Catch:{ RemoteException -> 0x012e }
                goto L_0x012e
            L_0x00b2:
                java.lang.Class<com.android.systemui.recents.OverviewProxyService> r0 = com.android.systemui.recents.OverviewProxyService.class
                java.lang.Object r0 = com.android.systemui.Dependency.get(r0)
                com.android.systemui.recents.OverviewProxyService r0 = (com.android.systemui.recents.OverviewProxyService) r0
                r0.startConnectionToCurrentUser()
                goto L_0x012e
            L_0x00be:
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r0 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r0.updateCurrentProfilesCache()
                goto L_0x012e
            L_0x00c4:
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r2 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                java.lang.String r3 = "android.intent.extra.user_handle"
                int r1 = r1.getIntExtra(r3, r9)
                r2.mCurrentUserId = r1
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r1 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r1.updateCurrentProfilesCache()
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "userId "
                r1.append(r2)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r2 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                int r2 = r2.mCurrentUserId
                r1.append(r2)
                java.lang.String r2 = " is in the house"
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                java.lang.String r2 = "LockscreenUserManager"
                android.util.Log.v(r2, r1)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r1 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r1.updateLockscreenNotificationSetting()
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r1 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                r1.updatePublicMode()
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r1 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.notification.NotificationEntryManager r1 = r1.getEntryManager()
                java.lang.String r2 = "user switched"
                r1.reapplyFilterAndSort(r2)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r1 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                com.android.systemui.statusbar.NotificationPresenter r2 = r1.mPresenter
                int r1 = r1.mCurrentUserId
                r2.onUserSwitched(r1)
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r1 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                java.util.List r1 = r1.mListeners
                java.util.Iterator r1 = r1.iterator()
            L_0x011a:
                boolean r2 = r1.hasNext()
                if (r2 == 0) goto L_0x012e
                java.lang.Object r2 = r1.next()
                com.android.systemui.statusbar.NotificationLockscreenUserManager$UserChangedListener r2 = (com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener) r2
                com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl r3 = com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.this
                int r3 = r3.mCurrentUserId
                r2.onUserChanged(r3)
                goto L_0x011a
            L_0x012e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl.C11442.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    private final BroadcastDispatcher mBroadcastDispatcher;
    protected final Context mContext;
    protected final ArrayList<UserInfo> mCurrentManagedProfiles = new ArrayList<>();
    protected final SparseArray<UserInfo> mCurrentProfiles = new SparseArray<>();
    protected int mCurrentUserId = 0;
    private final DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public final DeviceProvisionedController mDeviceProvisionedController;
    private NotificationEntryManager mEntryManager;
    protected KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public final List<UserChangedListener> mListeners = new ArrayList();
    private final Object mLock = new Object();
    private LockPatternUtils mLockPatternUtils;
    private final SparseBooleanArray mLockscreenPublicMode = new SparseBooleanArray();
    protected ContentObserver mLockscreenSettingsObserver;
    private final Handler mMainHandler;
    protected NotificationPresenter mPresenter;
    protected ContentObserver mSettingsObserver;
    private boolean mShowLockscreenNotifications;
    private int mState = 0;
    private final UserManager mUserManager;
    /* access modifiers changed from: private */
    public final SparseBooleanArray mUsersAllowingNotifications = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final SparseBooleanArray mUsersAllowingPrivateNotifications = new SparseBooleanArray();
    private final SparseBooleanArray mUsersWithSeperateWorkChallenge = new SparseBooleanArray();

    /* access modifiers changed from: private */
    public NotificationEntryManager getEntryManager() {
        if (this.mEntryManager == null) {
            this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        }
        return this.mEntryManager;
    }

    public NotificationLockscreenUserManagerImpl(Context context, BroadcastDispatcher broadcastDispatcher, DevicePolicyManager devicePolicyManager, UserManager userManager, IStatusBarService iStatusBarService, KeyguardManager keyguardManager, StatusBarStateController statusBarStateController, Handler handler, DeviceProvisionedController deviceProvisionedController, KeyguardStateController keyguardStateController) {
        this.mContext = context;
        this.mMainHandler = handler;
        this.mDevicePolicyManager = devicePolicyManager;
        this.mUserManager = userManager;
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        this.mBarService = iStatusBarService;
        statusBarStateController.addCallback(this);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mKeyguardManager = keyguardManager;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mKeyguardStateController = keyguardStateController;
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter) {
        this.mPresenter = notificationPresenter;
        this.mLockscreenSettingsObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(boolean z) {
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingPrivateNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.mUsersAllowingNotifications.clear();
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("LOCK_SCREEN_SHOW_NOTIFICATIONS, or LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS change");
            }
        };
        this.mSettingsObserver = new ContentObserver(this.mMainHandler) {
            public void onChange(boolean z) {
                NotificationLockscreenUserManagerImpl.this.updateLockscreenNotificationSetting();
                if (NotificationLockscreenUserManagerImpl.this.mDeviceProvisionedController.isDeviceProvisioned()) {
                    NotificationLockscreenUserManagerImpl.this.getEntryManager().updateNotifications("LOCK_SCREEN_ALLOW_REMOTE_INPUT or ZEN_MODE change");
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Secure.getUriFor("lock_screen_show_notifications"), false, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Secure.getUriFor("lock_screen_allow_private_notifications"), true, this.mLockscreenSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Global.getUriFor("zen_mode"), false, this.mSettingsObserver);
        this.mBroadcastDispatcher.registerReceiver(this.mAllUsersReceiver, new IntentFilter("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED"), null, UserHandle.ALL);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        this.mBroadcastDispatcher.registerReceiver(this.mBaseBroadcastReceiver, intentFilter, null, UserHandle.ALL);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
        this.mContext.registerReceiver(this.mBaseBroadcastReceiver, intentFilter2, "com.android.systemui.permission.SELF", null);
        updateCurrentProfilesCache();
        this.mSettingsObserver.onChange(false);
    }

    public boolean shouldShowLockscreenNotifications() {
        return this.mShowLockscreenNotifications;
    }

    public boolean shouldAllowLockscreenRemoteInput() {
        return this.mAllowLockscreenRemoteInput;
    }

    public boolean isCurrentProfile(int i) {
        boolean z;
        synchronized (this.mLock) {
            if (i != -1) {
                try {
                    if (this.mCurrentProfiles.get(i) == null) {
                        z = false;
                    }
                } finally {
                }
            }
            z = true;
        }
        return z;
    }

    private boolean shouldTemporarilyHideNotifications(int i) {
        if (i == -1) {
            i = this.mCurrentUserId;
        }
        return ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isUserInLockdown(i);
    }

    public boolean shouldHideNotifications(int i) {
        if (!isLockscreenPublicMode(i) || userAllowsNotificationsInPublic(i)) {
            int i2 = this.mCurrentUserId;
            if ((i == i2 || !shouldHideNotifications(i2)) && !shouldTemporarilyHideNotifications(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean shouldHideNotifications(String str) {
        boolean z = true;
        if (getEntryManager() == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return true;
        }
        NotificationEntry activeNotificationUnfiltered = getEntryManager().getActiveNotificationUnfiltered(str);
        if (!isLockscreenPublicMode(this.mCurrentUserId) || activeNotificationUnfiltered == null || activeNotificationUnfiltered.getRanking().getVisibilityOverride() != -1) {
            z = false;
        }
        return z;
    }

    public boolean shouldShowOnKeyguard(NotificationEntry notificationEntry) {
        boolean z = false;
        if (getEntryManager() == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return false;
        }
        r4 = (!NotificationUtils.useNewInterruptionModel(this.mContext) || !hideSilentNotificationsOnLockscreen()) ? !notificationEntry.getRanking().isAmbient() : notificationEntry.getBucket() != 3;
        if (this.mShowLockscreenNotifications && r4) {
            z = true;
        }
        return z;
    }

    private boolean hideSilentNotificationsOnLockscreen() {
        return ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
            public final Object get() {
                return NotificationLockscreenUserManagerImpl.this.mo14410x997f5832();
            }
        })).booleanValue();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hideSilentNotificationsOnLockscreen$0 */
    public /* synthetic */ Boolean mo14410x997f5832() {
        boolean z = true;
        if (Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", 1) != 0) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    private void setShowLockscreenNotifications(boolean z) {
        this.mShowLockscreenNotifications = z;
    }

    private void setLockscreenAllowRemoteInput(boolean z) {
        this.mAllowLockscreenRemoteInput = z;
    }

    /* access modifiers changed from: protected */
    public void updateLockscreenNotificationSetting() {
        boolean z = true;
        boolean z2 = Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, this.mCurrentUserId) != 0;
        boolean z3 = (this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mCurrentUserId) & 4) == 0;
        if (!z2 || !z3) {
            z = false;
        }
        setShowLockscreenNotifications(z);
        setLockscreenAllowRemoteInput(false);
    }

    public boolean userAllowsPrivateNotificationsInPublic(int i) {
        boolean z = true;
        if (i == -1) {
            return true;
        }
        if (this.mUsersAllowingPrivateNotifications.indexOfKey(i) >= 0) {
            return this.mUsersAllowingPrivateNotifications.get(i);
        }
        boolean z2 = Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 0, i) != 0;
        boolean adminAllowsKeyguardFeature = adminAllowsKeyguardFeature(i, 8);
        if (!z2 || !adminAllowsKeyguardFeature) {
            z = false;
        }
        this.mUsersAllowingPrivateNotifications.append(i, z);
        return z;
    }

    public boolean allowsManagedPrivateNotificationsInPublic() {
        synchronized (this.mLock) {
            Iterator it = this.mCurrentManagedProfiles.iterator();
            while (it.hasNext()) {
                if (!userAllowsPrivateNotificationsInPublic(((UserInfo) it.next()).id)) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean adminAllowsKeyguardFeature(int i, int i2) {
        boolean z = true;
        if (i == -1) {
            return true;
        }
        if ((this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, i) & i2) != 0) {
            z = false;
        }
        return z;
    }

    public void setLockscreenPublicMode(boolean z, int i) {
        this.mLockscreenPublicMode.put(i, z);
    }

    public boolean isLockscreenPublicMode(int i) {
        if (i == -1) {
            return this.mLockscreenPublicMode.get(this.mCurrentUserId, false);
        }
        return this.mLockscreenPublicMode.get(i, false);
    }

    public boolean needsSeparateWorkChallenge(int i) {
        return this.mUsersWithSeperateWorkChallenge.get(i, false);
    }

    public boolean userAllowsNotificationsInPublic(int i) {
        boolean z = true;
        if (isCurrentProfile(i) && i != this.mCurrentUserId) {
            return true;
        }
        if (this.mUsersAllowingNotifications.indexOfKey(i) >= 0) {
            return this.mUsersAllowingNotifications.get(i);
        }
        boolean z2 = Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 0, i) != 0;
        boolean adminAllowsKeyguardFeature = adminAllowsKeyguardFeature(i, 4);
        boolean privateNotificationsAllowed = this.mKeyguardManager.getPrivateNotificationsAllowed();
        if (!z2 || !adminAllowsKeyguardFeature || !privateNotificationsAllowed) {
            z = false;
        }
        this.mUsersAllowingNotifications.append(i, z);
        return z;
    }

    public boolean needsRedaction(NotificationEntry notificationEntry) {
        boolean z = (userAllowsPrivateNotificationsInPublic(this.mCurrentUserId) ^ true) || (userAllowsPrivateNotificationsInPublic(notificationEntry.getSbn().getUserId()) ^ true);
        boolean z2 = notificationEntry.getSbn().getNotification().visibility == 0;
        if (packageHasVisibilityOverride(notificationEntry.getSbn().getKey())) {
            return true;
        }
        if (!z2 || !z) {
            return false;
        }
        return true;
    }

    private boolean packageHasVisibilityOverride(String str) {
        boolean z = true;
        if (getEntryManager() == null) {
            Log.wtf("LockscreenUserManager", "mEntryManager was null!", new Throwable());
            return true;
        }
        NotificationEntry activeNotificationUnfiltered = getEntryManager().getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered == null || activeNotificationUnfiltered.getRanking().getVisibilityOverride() != 0) {
            z = false;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void updateCurrentProfilesCache() {
        synchronized (this.mLock) {
            this.mCurrentProfiles.clear();
            this.mCurrentManagedProfiles.clear();
            if (this.mUserManager != null) {
                for (UserInfo userInfo : this.mUserManager.getProfiles(this.mCurrentUserId)) {
                    this.mCurrentProfiles.put(userInfo.id, userInfo);
                    if ("android.os.usertype.profile.MANAGED".equals(userInfo.userType)) {
                        this.mCurrentManagedProfiles.add(userInfo);
                    }
                }
            }
        }
        this.mMainHandler.post(new Runnable() {
            public final void run() {
                NotificationLockscreenUserManagerImpl.this.mo14411x298997ce();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateCurrentProfilesCache$1 */
    public /* synthetic */ void mo14411x298997ce() {
        for (UserChangedListener onCurrentProfilesChanged : this.mListeners) {
            onCurrentProfilesChanged.onCurrentProfilesChanged(this.mCurrentProfiles);
        }
    }

    public boolean isAnyProfilePublicMode() {
        synchronized (this.mLock) {
            for (int size = this.mCurrentProfiles.size() - 1; size >= 0; size--) {
                if (isLockscreenPublicMode(((UserInfo) this.mCurrentProfiles.valueAt(size)).id)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isAnyManagedProfilePublicMode() {
        synchronized (this.mLock) {
            for (int size = this.mCurrentManagedProfiles.size() - 1; size >= 0; size--) {
                if (isLockscreenPublicMode(((UserInfo) this.mCurrentManagedProfiles.get(size)).id)) {
                    return true;
                }
            }
            return false;
        }
    }

    public int getCurrentUserId() {
        return this.mCurrentUserId;
    }

    public SparseArray<UserInfo> getCurrentProfiles() {
        return this.mCurrentProfiles;
    }

    public void onStateChanged(int i) {
        this.mState = i;
        updatePublicMode();
    }

    public void updatePublicMode() {
        boolean z = this.mState != 0 || this.mKeyguardStateController.isShowing();
        boolean z2 = z && this.mKeyguardStateController.isMethodSecure();
        SparseArray currentProfiles = getCurrentProfiles();
        this.mUsersWithSeperateWorkChallenge.clear();
        for (int size = currentProfiles.size() - 1; size >= 0; size--) {
            int i = ((UserInfo) currentProfiles.valueAt(size)).id;
            boolean booleanValue = ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final Object get() {
                    return NotificationLockscreenUserManagerImpl.this.lambda$updatePublicMode$2$NotificationLockscreenUserManagerImpl(this.f$1);
                }
            })).booleanValue();
            boolean z3 = (z2 || i == getCurrentUserId() || !booleanValue || !this.mLockPatternUtils.isSecure(i)) ? z2 : z || this.mKeyguardManager.isDeviceLocked(i);
            setLockscreenPublicMode(z3, i);
            this.mUsersWithSeperateWorkChallenge.put(i, booleanValue);
        }
        getEntryManager().updateNotifications("NotificationLockscreenUserManager.updatePublicMode");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePublicMode$2 */
    public /* synthetic */ Boolean lambda$updatePublicMode$2$NotificationLockscreenUserManagerImpl(int i) {
        return Boolean.valueOf(this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i));
    }

    public void addUserChangedListener(UserChangedListener userChangedListener) {
        this.mListeners.add(userChangedListener);
    }

    public void removeUserChangedListener(UserChangedListener userChangedListener) {
        this.mListeners.remove(userChangedListener);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationLockscreenUserManager state:");
        printWriter.print("  mCurrentUserId=");
        printWriter.println(this.mCurrentUserId);
        printWriter.print("  mShowLockscreenNotifications=");
        printWriter.println(this.mShowLockscreenNotifications);
        printWriter.print("  mAllowLockscreenRemoteInput=");
        printWriter.println(this.mAllowLockscreenRemoteInput);
        printWriter.print("  mCurrentProfiles=");
        synchronized (this.mLock) {
            for (int size = this.mCurrentProfiles.size() - 1; size >= 0; size--) {
                int i = ((UserInfo) this.mCurrentProfiles.valueAt(size)).id;
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(i);
                sb.append(" ");
                printWriter.print(sb.toString());
            }
        }
        printWriter.print("  mCurrentManagedProfiles=");
        synchronized (this.mLock) {
            Iterator it = this.mCurrentManagedProfiles.iterator();
            while (it.hasNext()) {
                UserInfo userInfo = (UserInfo) it.next();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("");
                sb2.append(userInfo.id);
                sb2.append(" ");
                printWriter.print(sb2.toString());
            }
        }
        printWriter.println();
    }
}
