package com.android.systemui.pip.p005tv;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.StackInfo;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.os.Debug;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.DisplayInfo;
import com.android.systemui.C2005R$array;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.pip.BasePipManager;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.pip.PipTaskOrganizer.PipTransitionCallback;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PinnedStackListenerForwarder.PinnedStackListener;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.WindowManagerWrapper;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.systemui.pip.tv.PipManager */
public class PipManager implements BasePipManager, PipTransitionCallback {
    static final boolean DEBUG = Log.isLoggable("PipManager", 3);
    private static List<Pair<String, String>> sSettingsPackageAndClassNamePairList;
    /* access modifiers changed from: private */
    public final OnActiveSessionsChangedListener mActiveMediaSessionListener = new OnActiveSessionsChangedListener() {
        public void onActiveSessionsChanged(List<MediaController> list) {
            PipManager.this.updateMediaController(list);
        }
    };
    private IActivityTaskManager mActivityTaskManager;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.MEDIA_RESOURCE_GRANTED".equals(intent.getAction())) {
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
                int intExtra = intent.getIntExtra("android.intent.extra.MEDIA_RESOURCE_TYPE", -1);
                if (stringArrayExtra != null && stringArrayExtra.length > 0 && intExtra == 0) {
                    PipManager.this.handleMediaResourceGranted(stringArrayExtra);
                }
            }
        }
    };
    private final Runnable mClosePipRunnable = new Runnable() {
        public void run() {
            PipManager.this.closePip();
        }
    };
    private Context mContext;
    /* access modifiers changed from: private */
    public Rect mCurrentPipBounds;
    /* access modifiers changed from: private */
    public ParceledListSlice mCustomActions;
    /* access modifiers changed from: private */
    public Rect mDefaultPipBounds = new Rect();
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public int mImeHeightAdjustment;
    /* access modifiers changed from: private */
    public boolean mImeVisible;
    private boolean mInitialized;
    private int mLastOrientation = 0;
    private String[] mLastPackagesResourceGranted;
    /* access modifiers changed from: private */
    public List<Listener> mListeners = new ArrayList();
    private List<MediaListener> mMediaListeners = new ArrayList();
    /* access modifiers changed from: private */
    public MediaSessionManager mMediaSessionManager;
    private Rect mMenuModePipBounds;
    /* access modifiers changed from: private */
    public int mPinnedStackId = -1;
    private final PinnedStackListener mPinnedStackListener = new PipManagerPinnedStackListener();
    /* access modifiers changed from: private */
    public Rect mPipBounds;
    /* access modifiers changed from: private */
    public PipBoundsHandler mPipBoundsHandler;
    /* access modifiers changed from: private */
    public ComponentName mPipComponentName;
    private MediaController mPipMediaController;
    private PipNotification mPipNotification;
    /* access modifiers changed from: private */
    public int mPipTaskId = -1;
    private PipTaskOrganizer mPipTaskOrganizer;
    private int mResizeAnimationDuration;
    private final Runnable mResizePinnedStackRunnable = new Runnable() {
        public void run() {
            PipManager pipManager = PipManager.this;
            pipManager.resizePinnedStack(pipManager.mResumeResizePinnedStackRunnableState);
        }
    };
    /* access modifiers changed from: private */
    public int mResumeResizePinnedStackRunnableState = 0;
    /* access modifiers changed from: private */
    public Rect mSettingsPipBounds;
    /* access modifiers changed from: private */
    public int mState = 0;
    private int mSuspendPipResizingReason;
    private TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() {
        public void onTaskStackChanged() {
            String str = "PipManager";
            if (PipManager.DEBUG) {
                Log.d(str, "onTaskStackChanged()");
            }
            if (PipManager.this.getState() != 0) {
                StackInfo access$1700 = PipManager.this.getPinnedStackInfo();
                boolean z = false;
                if (access$1700 != null) {
                    int[] iArr = access$1700.taskIds;
                    if (iArr != null) {
                        int length = iArr.length - 1;
                        while (true) {
                            if (length < 0) {
                                break;
                            } else if (access$1700.taskIds[length] == PipManager.this.mPipTaskId) {
                                z = true;
                                break;
                            } else {
                                length--;
                            }
                        }
                        if (!z) {
                            PipManager.this.closePipInternal(true);
                            return;
                        }
                    }
                }
                Log.w(str, "There is nothing in pinned stack");
                PipManager.this.closePipInternal(false);
                return;
            }
            if (PipManager.this.getState() == 1) {
                Rect access$2100 = PipManager.this.isSettingsShown() ? PipManager.this.mSettingsPipBounds : PipManager.this.mDefaultPipBounds;
                if (PipManager.this.mPipBounds != access$2100) {
                    PipManager.this.mPipBounds = access$2100;
                    PipManager.this.resizePinnedStack(1);
                }
            }
        }

        public void onActivityPinned(String str, int i, int i2, int i3) {
            String str2 = "PipManager";
            if (PipManager.DEBUG) {
                Log.d(str2, "onActivityPinned()");
            }
            StackInfo access$1700 = PipManager.this.getPinnedStackInfo();
            if (access$1700 == null) {
                Log.w(str2, "Cannot find pinned stack");
                return;
            }
            if (PipManager.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("PINNED_STACK:");
                sb.append(access$1700);
                Log.d(str2, sb.toString());
            }
            PipManager.this.mPinnedStackId = access$1700.stackId;
            PipManager pipManager = PipManager.this;
            int[] iArr = access$1700.taskIds;
            pipManager.mPipTaskId = iArr[iArr.length - 1];
            PipManager pipManager2 = PipManager.this;
            String[] strArr = access$1700.taskNames;
            pipManager2.mPipComponentName = ComponentName.unflattenFromString(strArr[strArr.length - 1]);
            PipManager.this.mState = 1;
            PipManager pipManager3 = PipManager.this;
            pipManager3.mCurrentPipBounds = pipManager3.mPipBounds;
            PipManager.this.mMediaSessionManager.addOnActiveSessionsChangedListener(PipManager.this.mActiveMediaSessionListener, null);
            PipManager pipManager4 = PipManager.this;
            pipManager4.updateMediaController(pipManager4.mMediaSessionManager.getActiveSessions(null));
            for (int size = PipManager.this.mListeners.size() - 1; size >= 0; size--) {
                ((Listener) PipManager.this.mListeners.get(size)).onPipEntered();
            }
            PipManager.this.updatePipVisibility(true);
        }

        public void onActivityRestartAttempt(RunningTaskInfo runningTaskInfo, boolean z, boolean z2) {
            if (runningTaskInfo.configuration.windowConfiguration.getWindowingMode() == 2) {
                if (PipManager.DEBUG) {
                    Log.d("PipManager", "onPinnedActivityRestartAttempt()");
                }
                PipManager.this.movePipToFullscreen();
            }
        }
    };
    /* access modifiers changed from: private */
    public final DisplayInfo mTmpDisplayInfo = new DisplayInfo();
    /* access modifiers changed from: private */
    public final Rect mTmpInsetBounds = new Rect();
    /* access modifiers changed from: private */
    public final Rect mTmpNormalBounds = new Rect();

    /* renamed from: com.android.systemui.pip.tv.PipManager$Listener */
    public interface Listener {
        void onMoveToFullscreen();

        void onPipActivityClosed();

        void onPipEntered();

        void onPipMenuActionsChanged(ParceledListSlice parceledListSlice);

        void onPipResizeAboutToStart();

        void onShowPipMenu();
    }

    /* renamed from: com.android.systemui.pip.tv.PipManager$MediaListener */
    public interface MediaListener {
        void onMediaControllerChanged();
    }

    /* renamed from: com.android.systemui.pip.tv.PipManager$PipManagerPinnedStackListener */
    private class PipManagerPinnedStackListener extends PinnedStackListener {
        private PipManagerPinnedStackListener() {
        }

        public void onImeVisibilityChanged(boolean z, int i) {
            if (PipManager.this.mState == 1 && PipManager.this.mImeVisible != z) {
                if (z) {
                    PipManager.this.mPipBounds.offset(0, -i);
                    PipManager.this.mImeHeightAdjustment = i;
                } else {
                    PipManager.this.mPipBounds.offset(0, PipManager.this.mImeHeightAdjustment);
                }
                PipManager.this.mImeVisible = z;
                PipManager.this.resizePinnedStack(1);
            }
        }

        public void onMovementBoundsChanged(Rect rect, boolean z) {
            PipManager.this.mHandler.post(new Runnable(rect) {
                public final /* synthetic */ Rect f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PipManagerPinnedStackListener.this.mo12344x366aaba4(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onMovementBoundsChanged$0 */
        public /* synthetic */ void mo12344x366aaba4(Rect rect) {
            PipManager.this.mPipBoundsHandler.onMovementBoundsChanged(PipManager.this.mTmpInsetBounds, PipManager.this.mTmpNormalBounds, rect, PipManager.this.mTmpDisplayInfo);
            PipManager.this.mDefaultPipBounds.set(rect);
        }

        public void onActionsChanged(ParceledListSlice parceledListSlice) {
            PipManager.this.mCustomActions = parceledListSlice;
            PipManager.this.mHandler.post(new Runnable() {
                public final void run() {
                    PipManagerPinnedStackListener.this.mo12343x652690c4();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onActionsChanged$1 */
        public /* synthetic */ void mo12343x652690c4() {
            for (int size = PipManager.this.mListeners.size() - 1; size >= 0; size--) {
                ((Listener) PipManager.this.mListeners.get(size)).onPipMenuActionsChanged(PipManager.this.mCustomActions);
            }
        }
    }

    public void onPipTransitionStarted() {
    }

    public PipManager(Context context, BroadcastDispatcher broadcastDispatcher, PipBoundsHandler pipBoundsHandler, PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        Object obj;
        String str;
        if (!this.mInitialized) {
            this.mInitialized = true;
            this.mContext = context;
            this.mPipBoundsHandler = pipBoundsHandler;
            this.mResizeAnimationDuration = context.getResources().getInteger(C2012R$integer.config_pipResizeAnimationDuration);
            PipTaskOrganizer pipTaskOrganizer = new PipTaskOrganizer(this.mContext, this.mPipBoundsHandler, pipSurfaceTransactionHelper);
            this.mPipTaskOrganizer = pipTaskOrganizer;
            pipTaskOrganizer.registerPipTransitionCallback(this);
            this.mActivityTaskManager = ActivityTaskManager.getService();
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_RESOURCE_GRANTED");
            broadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter, null, UserHandle.ALL);
            String str2 = "PipManager";
            if (sSettingsPackageAndClassNamePairList == null) {
                String[] stringArray = this.mContext.getResources().getStringArray(C2005R$array.tv_pip_settings_class_name);
                sSettingsPackageAndClassNamePairList = new ArrayList();
                if (stringArray != null) {
                    for (int i = 0; i < stringArray.length; i++) {
                        String[] split = stringArray[i].split("/");
                        int length = split.length;
                        if (length == 1) {
                            obj = Pair.create(split[0], null);
                        } else if (length == 2 && split[1] != null) {
                            String str3 = split[0];
                            if (split[1].startsWith(".")) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(split[0]);
                                sb.append(split[1]);
                                str = sb.toString();
                            } else {
                                str = split[1];
                            }
                            obj = Pair.create(str3, str);
                        } else {
                            obj = null;
                        }
                        if (obj != null) {
                            sSettingsPackageAndClassNamePairList.add(obj);
                        } else {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Ignoring malformed settings name ");
                            sb2.append(stringArray[i]);
                            Log.w(str2, sb2.toString());
                        }
                    }
                }
            }
            Configuration configuration = this.mContext.getResources().getConfiguration();
            this.mLastOrientation = configuration.orientation;
            loadConfigurationsAndApply(configuration);
            this.mMediaSessionManager = (MediaSessionManager) this.mContext.getSystemService("media_session");
            this.mPipTaskOrganizer.registerPipTransitionCallback(this);
            try {
                WindowManagerWrapper.getInstance().addPinnedStackListener(this.mPinnedStackListener);
                ActivityTaskManager.getTaskOrganizerController().registerTaskOrganizer(this.mPipTaskOrganizer, 2);
            } catch (RemoteException e) {
                Log.e(str2, "Failed to register pinned stack listener", e);
            }
            this.mPipNotification = new PipNotification(context, broadcastDispatcher, this);
        }
    }

    private void loadConfigurationsAndApply(Configuration configuration) {
        int i = this.mLastOrientation;
        int i2 = configuration.orientation;
        if (i != i2) {
            this.mLastOrientation = i2;
            return;
        }
        Resources resources = this.mContext.getResources();
        this.mSettingsPipBounds = Rect.unflattenFromString(resources.getString(C2017R$string.pip_settings_bounds));
        this.mMenuModePipBounds = Rect.unflattenFromString(resources.getString(C2017R$string.pip_menu_bounds));
        this.mPipBounds = isSettingsShown() ? this.mSettingsPipBounds : this.mDefaultPipBounds;
        resizePinnedStack(getPinnedStackInfo() == null ? 0 : 1);
    }

    public void onConfigurationChanged(Configuration configuration) {
        loadConfigurationsAndApply(configuration);
        this.mPipNotification.onConfigurationChanged(this.mContext);
    }

    public void showPictureInPictureMenu() {
        if (getState() == 1) {
            resizePinnedStack(2);
        }
    }

    public void closePip() {
        closePipInternal(true);
    }

    /* access modifiers changed from: private */
    public void closePipInternal(boolean z) {
        this.mState = 0;
        this.mPipTaskId = -1;
        this.mPipMediaController = null;
        this.mMediaSessionManager.removeOnActiveSessionsChangedListener(this.mActiveMediaSessionListener);
        if (z) {
            try {
                this.mActivityTaskManager.removeStack(this.mPinnedStackId);
            } catch (RemoteException e) {
                Log.e("PipManager", "removeStack failed", e);
            } catch (Throwable th) {
                this.mPinnedStackId = -1;
                throw th;
            }
            this.mPinnedStackId = -1;
        }
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            ((Listener) this.mListeners.get(size)).onPipActivityClosed();
        }
        this.mHandler.removeCallbacks(this.mClosePipRunnable);
        updatePipVisibility(false);
    }

    /* access modifiers changed from: 0000 */
    public void movePipToFullscreen() {
        this.mPipTaskId = -1;
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            ((Listener) this.mListeners.get(size)).onMoveToFullscreen();
        }
        resizePinnedStack(0);
        updatePipVisibility(false);
    }

    public void suspendPipResizing(int i) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("suspendPipResizing() reason=");
            sb.append(i);
            sb.append(" callers=");
            sb.append(Debug.getCallers(2));
            Log.d("PipManager", sb.toString());
        }
        this.mSuspendPipResizingReason = i | this.mSuspendPipResizingReason;
    }

    public void resumePipResizing(int i) {
        if ((this.mSuspendPipResizingReason & i) != 0) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("resumePipResizing() reason=");
                sb.append(i);
                sb.append(" callers=");
                sb.append(Debug.getCallers(2));
                Log.d("PipManager", sb.toString());
            }
            this.mSuspendPipResizingReason = (~i) & this.mSuspendPipResizingReason;
            this.mHandler.post(this.mResizePinnedStackRunnable);
        }
    }

    /* access modifiers changed from: 0000 */
    public void resizePinnedStack(int i) {
        String str = "PipManager";
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("resizePinnedStack() state=");
            sb.append(i);
            Log.d(str, sb.toString(), new Exception());
        }
        boolean z = this.mState == 0;
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            ((Listener) this.mListeners.get(size)).onPipResizeAboutToStart();
        }
        if (this.mSuspendPipResizingReason != 0) {
            this.mResumeResizePinnedStackRunnableState = i;
            if (DEBUG) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("resizePinnedStack() deferring mSuspendPipResizingReason=");
                sb2.append(this.mSuspendPipResizingReason);
                sb2.append(" mResumeResizePinnedStackRunnableState=");
                sb2.append(this.mResumeResizePinnedStackRunnableState);
                Log.d(str, sb2.toString());
            }
            return;
        }
        this.mState = i;
        if (i == 0) {
            this.mCurrentPipBounds = null;
            if (z) {
                return;
            }
        } else if (i != 2) {
            this.mCurrentPipBounds = this.mPipBounds;
        } else {
            this.mCurrentPipBounds = this.mMenuModePipBounds;
        }
        this.mPipTaskOrganizer.scheduleAnimateResizePip(this.mCurrentPipBounds, this.mResizeAnimationDuration, null);
    }

    /* access modifiers changed from: private */
    public int getState() {
        if (this.mSuspendPipResizingReason != 0) {
            return this.mResumeResizePinnedStackRunnableState;
        }
        return this.mState;
    }

    private void showPipMenu() {
        if (DEBUG) {
            Log.d("PipManager", "showPipMenu()");
        }
        this.mState = 2;
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            ((Listener) this.mListeners.get(size)).onShowPipMenu();
        }
        Intent intent = new Intent(this.mContext, PipMenuActivity.class);
        intent.setFlags(268435456);
        intent.putExtra("custom_actions", this.mCustomActions);
        this.mContext.startActivity(intent);
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    public void addMediaListener(MediaListener mediaListener) {
        this.mMediaListeners.add(mediaListener);
    }

    public void removeMediaListener(MediaListener mediaListener) {
        this.mMediaListeners.remove(mediaListener);
    }

    public boolean isPipShown() {
        return this.mState != 0;
    }

    /* access modifiers changed from: private */
    public StackInfo getPinnedStackInfo() {
        try {
            return ActivityTaskManager.getService().getStackInfo(2, 0);
        } catch (RemoteException e) {
            Log.e("PipManager", "getStackInfo failed", e);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void handleMediaResourceGranted(String[] strArr) {
        if (getState() == 0) {
            this.mLastPackagesResourceGranted = strArr;
            return;
        }
        String[] strArr2 = this.mLastPackagesResourceGranted;
        boolean z = false;
        if (strArr2 != null) {
            boolean z2 = false;
            for (String str : strArr2) {
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    } else if (TextUtils.equals(strArr[i], str)) {
                        z2 = true;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            z = z2;
        }
        this.mLastPackagesResourceGranted = strArr;
        if (!z) {
            closePip();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateMediaController(java.util.List<android.media.session.MediaController> r5) {
        /*
            r4 = this;
            if (r5 == 0) goto L_0x002e
            int r0 = r4.getState()
            if (r0 == 0) goto L_0x002e
            android.content.ComponentName r0 = r4.mPipComponentName
            if (r0 == 0) goto L_0x002e
            int r0 = r5.size()
            int r0 = r0 + -1
        L_0x0012:
            if (r0 < 0) goto L_0x002e
            java.lang.Object r1 = r5.get(r0)
            android.media.session.MediaController r1 = (android.media.session.MediaController) r1
            java.lang.String r2 = r1.getPackageName()
            android.content.ComponentName r3 = r4.mPipComponentName
            java.lang.String r3 = r3.getPackageName()
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x002b
            goto L_0x002f
        L_0x002b:
            int r0 = r0 + -1
            goto L_0x0012
        L_0x002e:
            r1 = 0
        L_0x002f:
            android.media.session.MediaController r5 = r4.mPipMediaController
            if (r5 == r1) goto L_0x0062
            r4.mPipMediaController = r1
            java.util.List<com.android.systemui.pip.tv.PipManager$MediaListener> r5 = r4.mMediaListeners
            int r5 = r5.size()
            int r5 = r5 + -1
        L_0x003d:
            if (r5 < 0) goto L_0x004d
            java.util.List<com.android.systemui.pip.tv.PipManager$MediaListener> r0 = r4.mMediaListeners
            java.lang.Object r0 = r0.get(r5)
            com.android.systemui.pip.tv.PipManager$MediaListener r0 = (com.android.systemui.pip.p005tv.PipManager.MediaListener) r0
            r0.onMediaControllerChanged()
            int r5 = r5 + -1
            goto L_0x003d
        L_0x004d:
            android.media.session.MediaController r5 = r4.mPipMediaController
            if (r5 != 0) goto L_0x005b
            android.os.Handler r5 = r4.mHandler
            java.lang.Runnable r4 = r4.mClosePipRunnable
            r0 = 3000(0xbb8, double:1.482E-320)
            r5.postDelayed(r4, r0)
            goto L_0x0062
        L_0x005b:
            android.os.Handler r5 = r4.mHandler
            java.lang.Runnable r4 = r4.mClosePipRunnable
            r5.removeCallbacks(r4)
        L_0x0062:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.pip.p005tv.PipManager.updateMediaController(java.util.List):void");
    }

    /* access modifiers changed from: 0000 */
    public MediaController getMediaController() {
        return this.mPipMediaController;
    }

    /* access modifiers changed from: 0000 */
    public int getPlaybackState() {
        MediaController mediaController = this.mPipMediaController;
        if (!(mediaController == null || mediaController.getPlaybackState() == null)) {
            int state = this.mPipMediaController.getPlaybackState().getState();
            boolean z = state == 6 || state == 8 || state == 3 || state == 4 || state == 5 || state == 9 || state == 10;
            long actions = this.mPipMediaController.getPlaybackState().getActions();
            if (!z && (4 & actions) != 0) {
                return 1;
            }
            if (!z || (actions & 2) == 0) {
                return 2;
            }
            return 0;
        }
        return 2;
    }

    /* access modifiers changed from: private */
    public boolean isSettingsShown() {
        try {
            List tasks = this.mActivityTaskManager.getTasks(1);
            if (tasks.isEmpty()) {
                return false;
            }
            ComponentName componentName = ((RunningTaskInfo) tasks.get(0)).topActivity;
            for (Pair pair : sSettingsPackageAndClassNamePairList) {
                if (componentName.getPackageName().equals((String) pair.first)) {
                    String str = (String) pair.second;
                    if (str == null || componentName.getClassName().equals(str)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (RemoteException e) {
            Log.d("PipManager", "Failed to detect top activity", e);
            return false;
        }
    }

    public void onPipTransitionFinished() {
        onPipTransitionFinishedOrCanceled();
    }

    public void onPipTransitionCanceled() {
        onPipTransitionFinishedOrCanceled();
    }

    private void onPipTransitionFinishedOrCanceled() {
        if (DEBUG) {
            Log.d("PipManager", "onPipTransitionFinishedOrCanceled()");
        }
        if (getState() == 2) {
            showPipMenu();
        }
    }

    /* access modifiers changed from: private */
    public void updatePipVisibility(boolean z) {
        ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).execute(new Runnable(z) {
            public final /* synthetic */ boolean f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                WindowManagerWrapper.getInstance().setPipVisibility(this.f$0);
            }
        });
    }
}
