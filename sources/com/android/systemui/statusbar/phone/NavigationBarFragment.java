package com.android.systemui.statusbar.phone;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import android.provider.Settings.Secure;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.InsetsState;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityServicesStateChangeListener;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.LatencyTracker;
import com.android.internal.view.AppearanceRegion;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.assist.AssistHandleViewController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.fragments.FragmentHostManager.FragmentListener;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.OverviewProxyService.OverviewProxyListener;
import com.android.systemui.recents.Recents;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.AutoHideUiElement;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.phone.ContextualButton.ContextButtonListener;
import com.android.systemui.statusbar.phone.NavigationBarView.OnVerticalChangedListener;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyButtonView;
import com.android.systemui.util.LifecycleFragment;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class NavigationBarFragment extends LifecycleFragment implements Callbacks, ModeChangedListener {
    private final AccessibilityServicesStateChangeListener mAccessibilityListener;
    /* access modifiers changed from: private */
    public AccessibilityManager mAccessibilityManager;
    private final AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    private int mAppearance;
    private final ContentObserver mAssistContentObserver;
    private AssistHandleViewController mAssistHandlerViewController;
    protected final AssistManager mAssistManager;
    /* access modifiers changed from: private */
    public boolean mAssistantAvailable;
    private final Runnable mAutoDim;
    private AutoHideController mAutoHideController;
    private final AutoHideUiElement mAutoHideUiElement;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver;
    private final CommandQueue mCommandQueue;
    private ContentResolver mContentResolver;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private int mDisabledFlags1;
    private int mDisabledFlags2;
    public int mDisplayId;
    private final Divider mDivider;
    /* access modifiers changed from: private */
    public boolean mForceNavBarHandleOpaque;
    private final Handler mHandler;
    public boolean mHomeBlockedThisTouch;
    private boolean mIsOnDefaultDisplay;
    private long mLastLockToAppLongPress;
    private int mLayoutDirection;
    private LightBarController mLightBarController;
    private Locale mLocale;
    private final MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public int mNavBarMode;
    private int mNavigationBarMode;
    protected NavigationBarView mNavigationBarView = null;
    private int mNavigationBarWindowState;
    private int mNavigationIconHints;
    private final NavigationModeController mNavigationModeController;
    /* access modifiers changed from: private */
    public final NotificationRemoteInputManager mNotificationRemoteInputManager;
    private final OnPropertiesChangedListener mOnPropertiesChangedListener;
    private final OverviewProxyListener mOverviewProxyListener;
    private OverviewProxyService mOverviewProxyService;
    private final Optional<Recents> mRecentsOptional;
    private final ContextButtonListener mRotationButtonListener;
    private final Consumer<Integer> mRotationWatcher;
    private final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    private SysUiState mSysUiFlagsContainer;
    private boolean mTransientShown;
    private WindowManager mWindowManager;

    private static int barMode(boolean z, int i) {
        if (z) {
            return 1;
        }
        if ((i & 6) == 6) {
            return 3;
        }
        if ((i & 4) != 0) {
            return 6;
        }
        return (i & 2) != 0 ? 4 : 0;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NavigationBarFragment(ContextualButton contextualButton, boolean z) {
        if (z) {
            this.mAutoHideController.touchAutoHide();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$NavigationBarFragment() {
        getBarTransitions().setAutoDim(true);
    }

    public NavigationBarFragment(AccessibilityManagerWrapper accessibilityManagerWrapper, DeviceProvisionedController deviceProvisionedController, MetricsLogger metricsLogger, AssistManager assistManager, OverviewProxyService overviewProxyService, NavigationModeController navigationModeController, StatusBarStateController statusBarStateController, SysUiState sysUiState, BroadcastDispatcher broadcastDispatcher, CommandQueue commandQueue, Divider divider, Optional<Recents> optional, Lazy<StatusBar> lazy, ShadeController shadeController, NotificationRemoteInputManager notificationRemoteInputManager, Handler handler) {
        AssistManager assistManager2 = assistManager;
        NavigationModeController navigationModeController2 = navigationModeController;
        boolean z = false;
        this.mNavigationBarWindowState = 0;
        this.mNavigationIconHints = 0;
        this.mNavBarMode = 0;
        this.mAutoHideUiElement = new AutoHideUiElement() {
            public void synchronizeState() {
                NavigationBarFragment.this.checkNavBarModes();
            }

            public boolean shouldHideOnTouch() {
                return !NavigationBarFragment.this.mNotificationRemoteInputManager.getController().isRemoteInputActive();
            }

            public boolean isVisible() {
                return NavigationBarFragment.this.isTransientShown();
            }

            public void hide() {
                NavigationBarFragment.this.clearTransient();
            }
        };
        this.mOverviewProxyListener = new OverviewProxyListener() {
            public void onConnectionChanged(boolean z) {
                NavigationBarFragment.this.mNavigationBarView.updateStates();
                NavigationBarFragment.this.updateScreenPinningGestures();
                if (z) {
                    NavigationBarFragment navigationBarFragment = NavigationBarFragment.this;
                    navigationBarFragment.sendAssistantAvailability(navigationBarFragment.mAssistantAvailable);
                }
            }

            public void startAssistant(Bundle bundle) {
                NavigationBarFragment.this.mAssistManager.startAssist(bundle);
            }

            /* JADX WARNING: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
            /* JADX WARNING: Removed duplicated region for block: B:9:0x0035 A[ADDED_TO_REGION] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onNavBarButtonAlphaChanged(float r4, boolean r5) {
                /*
                    r3 = this;
                    com.android.systemui.statusbar.phone.NavigationBarFragment r0 = com.android.systemui.statusbar.phone.NavigationBarFragment.this
                    int r0 = r0.mNavBarMode
                    boolean r0 = com.android.systemui.shared.system.QuickStepContract.isSwipeUpMode(r0)
                    r1 = 0
                    if (r0 == 0) goto L_0x0016
                    com.android.systemui.statusbar.phone.NavigationBarFragment r3 = com.android.systemui.statusbar.phone.NavigationBarFragment.this
                    com.android.systemui.statusbar.phone.NavigationBarView r3 = r3.mNavigationBarView
                    com.android.systemui.statusbar.phone.ButtonDispatcher r3 = r3.getBackButton()
                    goto L_0x0032
                L_0x0016:
                    com.android.systemui.statusbar.phone.NavigationBarFragment r0 = com.android.systemui.statusbar.phone.NavigationBarFragment.this
                    int r0 = r0.mNavBarMode
                    boolean r0 = com.android.systemui.shared.system.QuickStepContract.isGesturalMode(r0)
                    if (r0 == 0) goto L_0x0031
                    com.android.systemui.statusbar.phone.NavigationBarFragment r0 = com.android.systemui.statusbar.phone.NavigationBarFragment.this
                    boolean r0 = r0.mForceNavBarHandleOpaque
                    com.android.systemui.statusbar.phone.NavigationBarFragment r3 = com.android.systemui.statusbar.phone.NavigationBarFragment.this
                    com.android.systemui.statusbar.phone.NavigationBarView r3 = r3.mNavigationBarView
                    com.android.systemui.statusbar.phone.ButtonDispatcher r3 = r3.getHomeHandle()
                    goto L_0x0033
                L_0x0031:
                    r3 = 0
                L_0x0032:
                    r0 = r1
                L_0x0033:
                    if (r3 == 0) goto L_0x0048
                    if (r0 != 0) goto L_0x003e
                    r2 = 0
                    int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
                    if (r2 <= 0) goto L_0x003d
                    goto L_0x003e
                L_0x003d:
                    r1 = 4
                L_0x003e:
                    r3.setVisibility(r1)
                    if (r0 == 0) goto L_0x0045
                    r4 = 1065353216(0x3f800000, float:1.0)
                L_0x0045:
                    r3.setAlpha(r4, r5)
                L_0x0048:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NavigationBarFragment.C15052.onNavBarButtonAlphaChanged(float, boolean):void");
            }
        };
        this.mRotationButtonListener = new ContextButtonListener() {
            public final void onVisibilityChanged(ContextualButton contextualButton, boolean z) {
                NavigationBarFragment.this.lambda$new$0$NavigationBarFragment(contextualButton, z);
            }
        };
        this.mAutoDim = new Runnable() {
            public final void run() {
                NavigationBarFragment.this.lambda$new$1$NavigationBarFragment();
            }
        };
        this.mAssistContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean z, Uri uri) {
                boolean z2 = NavigationBarFragment.this.mAssistManager.getAssistInfoForUser(-2) != null;
                if (NavigationBarFragment.this.mAssistantAvailable != z2) {
                    NavigationBarFragment.this.sendAssistantAvailability(z2);
                    NavigationBarFragment.this.mAssistantAvailable = z2;
                }
            }
        };
        this.mOnPropertiesChangedListener = new OnPropertiesChangedListener() {
            public void onPropertiesChanged(Properties properties) {
                String str = "nav_bar_handle_force_opaque";
                if (properties.getKeyset().contains(str)) {
                    NavigationBarFragment.this.mForceNavBarHandleOpaque = properties.getBoolean(str, true);
                }
            }
        };
        this.mAccessibilityListener = new AccessibilityServicesStateChangeListener() {
            public final void onAccessibilityServicesStateChanged(AccessibilityManager accessibilityManager) {
                NavigationBarFragment.this.updateAccessibilityServicesState(accessibilityManager);
            }
        };
        this.mRotationWatcher = new Consumer() {
            public final void accept(Object obj) {
                NavigationBarFragment.this.lambda$new$4$NavigationBarFragment((Integer) obj);
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String str = "android.intent.action.SCREEN_ON";
                if ("android.intent.action.SCREEN_OFF".equals(action) || str.equals(action)) {
                    NavigationBarFragment.this.notifyNavigationBarScreenOn();
                    NavigationBarFragment.this.mNavigationBarView.onScreenStateChanged(str.equals(action));
                }
                if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    NavigationBarFragment navigationBarFragment = NavigationBarFragment.this;
                    navigationBarFragment.updateAccessibilityServicesState(navigationBarFragment.mAccessibilityManager);
                }
            }
        };
        this.mAccessibilityManagerWrapper = accessibilityManagerWrapper;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mStatusBarStateController = statusBarStateController;
        this.mMetricsLogger = metricsLogger;
        this.mAssistManager = assistManager2;
        this.mSysUiFlagsContainer = sysUiState;
        this.mStatusBarLazy = lazy;
        this.mNotificationRemoteInputManager = notificationRemoteInputManager;
        if (assistManager.getAssistInfoForUser(-2) != null) {
            z = true;
        }
        this.mAssistantAvailable = z;
        this.mOverviewProxyService = overviewProxyService;
        this.mNavigationModeController = navigationModeController2;
        this.mNavBarMode = navigationModeController.addListener(this);
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mCommandQueue = commandQueue;
        this.mDivider = divider;
        this.mRecentsOptional = optional;
        this.mHandler = handler;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mCommandQueue.observe(getLifecycle(), this);
        this.mWindowManager = (WindowManager) getContext().getSystemService(WindowManager.class);
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService(AccessibilityManager.class);
        ContentResolver contentResolver = getContext().getContentResolver();
        this.mContentResolver = contentResolver;
        contentResolver.registerContentObserver(Secure.getUriFor("assistant"), false, this.mAssistContentObserver, -1);
        if (bundle != null) {
            this.mDisabledFlags1 = bundle.getInt("disabled_state", 0);
            this.mDisabledFlags2 = bundle.getInt("disabled2_state", 0);
            this.mAppearance = bundle.getInt("appearance", 0);
            this.mTransientShown = bundle.getBoolean("transient_state", false);
        }
        this.mAccessibilityManagerWrapper.addCallback(this.mAccessibilityListener);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
        String str = "systemui";
        this.mForceNavBarHandleOpaque = DeviceConfig.getBoolean(str, "nav_bar_handle_force_opaque", true);
        Handler handler = this.mHandler;
        Objects.requireNonNull(handler);
        DeviceConfig.addOnPropertiesChangedListener(str, new Executor(handler) {
            public final /* synthetic */ Handler f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.f$0.post(runnable);
            }
        }, this.mOnPropertiesChangedListener);
    }

    public void onDestroy() {
        super.onDestroy();
        this.mNavigationModeController.removeListener(this);
        this.mAccessibilityManagerWrapper.removeCallback(this.mAccessibilityListener);
        this.mContentResolver.unregisterContentObserver(this.mAssistContentObserver);
        DeviceConfig.removeOnPropertiesChangedListener(this.mOnPropertiesChangedListener);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C2013R$layout.navigation_bar, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mNavigationBarView = (NavigationBarView) view;
        Display display = view.getDisplay();
        if (display != null) {
            int displayId = display.getDisplayId();
            this.mDisplayId = displayId;
            this.mIsOnDefaultDisplay = displayId == 0;
        }
        this.mNavigationBarView.setComponents(((StatusBar) this.mStatusBarLazy.get()).getPanelController());
        this.mNavigationBarView.setDisabledFlags(this.mDisabledFlags1);
        this.mNavigationBarView.setOnVerticalChangedListener(new OnVerticalChangedListener() {
            public final void onVerticalChanged(boolean z) {
                NavigationBarFragment.this.onVerticalChanged(z);
            }
        });
        this.mNavigationBarView.setOnTouchListener(new OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return NavigationBarFragment.this.onNavigationTouch(view, motionEvent);
            }
        });
        if (bundle != null) {
            this.mNavigationBarView.getLightTransitionsController().restoreState(bundle);
        }
        this.mNavigationBarView.setNavigationIconHints(this.mNavigationIconHints);
        this.mNavigationBarView.setWindowVisible(isNavBarWindowVisible());
        prepareNavigationBarView();
        checkNavBarModes();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastReceiver, intentFilter, Handler.getMain(), UserHandle.ALL);
        notifyNavigationBarScreenOn();
        this.mOverviewProxyService.addCallback(this.mOverviewProxyListener);
        updateSystemUiStateFlags(-1);
        if (this.mIsOnDefaultDisplay) {
            this.mNavigationBarView.getRotateSuggestionButton().setListener(this.mRotationButtonListener);
            RotationButtonController rotationButtonController = this.mNavigationBarView.getRotationButtonController();
            rotationButtonController.addRotationCallback(this.mRotationWatcher);
            if (display != null && rotationButtonController.isRotationLocked()) {
                rotationButtonController.setRotationLockedAtAngle(display.getRotation());
            }
        } else {
            this.mDisabledFlags2 |= 16;
        }
        setDisabled2Flags(this.mDisabledFlags2);
        if (this.mIsOnDefaultDisplay) {
            this.mAssistHandlerViewController = new AssistHandleViewController(this.mHandler, this.mNavigationBarView);
            getBarTransitions().addDarkIntensityListener(this.mAssistHandlerViewController);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            if (this.mIsOnDefaultDisplay) {
                navigationBarView.getBarTransitions().removeDarkIntensityListener(this.mAssistHandlerViewController);
                this.mAssistHandlerViewController = null;
            }
            this.mNavigationBarView.getBarTransitions().destroy();
            this.mNavigationBarView.getLightTransitionsController().destroy(getContext());
        }
        this.mOverviewProxyService.removeCallback(this.mOverviewProxyListener);
        this.mBroadcastDispatcher.unregisterReceiver(this.mBroadcastReceiver);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("disabled_state", this.mDisabledFlags1);
        bundle.putInt("disabled2_state", this.mDisabledFlags2);
        bundle.putInt("appearance", this.mAppearance);
        bundle.putBoolean("transient_state", this.mTransientShown);
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.getLightTransitionsController().saveState(bundle);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Locale locale = getContext().getResources().getConfiguration().locale;
        int layoutDirectionFromLocale = TextUtils.getLayoutDirectionFromLocale(locale);
        if (!locale.equals(this.mLocale) || layoutDirectionFromLocale != this.mLayoutDirection) {
            this.mLocale = locale;
            this.mLayoutDirection = layoutDirectionFromLocale;
            refreshLayout(layoutDirectionFromLocale);
        }
        repositionNavigationBar();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (this.mNavigationBarView != null) {
            printWriter.print("  mNavigationBarWindowState=");
            printWriter.println(StatusBarManager.windowStateToString(this.mNavigationBarWindowState));
            printWriter.print("  mNavigationBarMode=");
            printWriter.println(BarTransitions.modeToString(this.mNavigationBarMode));
            StatusBar.dumpBarTransitions(printWriter, "mNavigationBarView", this.mNavigationBarView.getBarTransitions());
        }
        printWriter.print("  mNavigationBarView=");
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView == null) {
            printWriter.println("null");
        } else {
            navigationBarView.dump(fileDescriptor, printWriter, strArr);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0017, code lost:
        if (r5 != 3) goto L_0x0021;
     */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0023  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x002b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x002c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setImeWindowStatus(int r2, android.os.IBinder r3, int r4, int r5, boolean r6) {
        /*
            r1 = this;
            int r3 = r1.mDisplayId
            if (r2 == r3) goto L_0x0005
            return
        L_0x0005:
            r2 = 2
            r3 = r4 & 2
            r4 = 1
            if (r3 == 0) goto L_0x000d
            r3 = r4
            goto L_0x000e
        L_0x000d:
            r3 = 0
        L_0x000e:
            int r0 = r1.mNavigationIconHints
            if (r5 == 0) goto L_0x001d
            if (r5 == r4) goto L_0x001d
            if (r5 == r2) goto L_0x001d
            r3 = 3
            if (r5 == r3) goto L_0x001a
            goto L_0x0021
        L_0x001a:
            r0 = r0 & -2
            goto L_0x0021
        L_0x001d:
            if (r3 == 0) goto L_0x001a
            r0 = r0 | 1
        L_0x0021:
            if (r6 == 0) goto L_0x0025
            r2 = r2 | r0
            goto L_0x0027
        L_0x0025:
            r2 = r0 & -3
        L_0x0027:
            int r3 = r1.mNavigationIconHints
            if (r2 != r3) goto L_0x002c
            return
        L_0x002c:
            r1.mNavigationIconHints = r2
            com.android.systemui.statusbar.phone.NavigationBarView r3 = r1.mNavigationBarView
            if (r3 == 0) goto L_0x0035
            r3.setNavigationIconHints(r2)
        L_0x0035:
            r1.checkBarModes()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NavigationBarFragment.setImeWindowStatus(int, android.os.IBinder, int, int, boolean):void");
    }

    public void setWindowState(int i, int i2, int i3) {
        if (i == this.mDisplayId && i2 == 2 && this.mNavigationBarWindowState != i3) {
            this.mNavigationBarWindowState = i3;
            updateSystemUiStateFlags(-1);
            NavigationBarView navigationBarView = this.mNavigationBarView;
            if (navigationBarView != null) {
                navigationBarView.setWindowVisible(isNavBarWindowVisible());
            }
        }
    }

    public void onRotationProposal(int i, boolean z) {
        int rotation = this.mNavigationBarView.getDisplay().getRotation();
        boolean hasDisable2RotateSuggestionFlag = RotationButtonController.hasDisable2RotateSuggestionFlag(this.mDisabledFlags2);
        RotationButtonController rotationButtonController = this.mNavigationBarView.getRotationButtonController();
        rotationButtonController.getRotationButton();
        if (!hasDisable2RotateSuggestionFlag) {
            rotationButtonController.onRotationProposal(i, rotation, z);
        }
    }

    public void restoreAppearanceAndTransientState() {
        int barMode = barMode(this.mTransientShown, this.mAppearance);
        this.mNavigationBarMode = barMode;
        checkNavBarModes();
        this.mAutoHideController.touchAutoHide();
        this.mLightBarController.onNavigationBarAppearanceChanged(this.mAppearance, true, barMode, false);
    }

    public void onSystemBarAppearanceChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z) {
        if (i == this.mDisplayId) {
            boolean z2 = false;
            if (this.mAppearance != i2) {
                this.mAppearance = i2;
                if (getView() != null) {
                    z2 = updateBarMode(barMode(this.mTransientShown, i2));
                } else {
                    return;
                }
            }
            this.mLightBarController.onNavigationBarAppearanceChanged(i2, z2, this.mNavigationBarMode, z);
        }
    }

    public void showTransient(int i, int[] iArr) {
        if (i == this.mDisplayId && InsetsState.containsType(iArr, 1) && !this.mTransientShown) {
            this.mTransientShown = true;
            handleTransientChanged();
        }
    }

    public void abortTransient(int i, int[] iArr) {
        if (i == this.mDisplayId && InsetsState.containsType(iArr, 1)) {
            clearTransient();
        }
    }

    /* access modifiers changed from: private */
    public void clearTransient() {
        if (this.mTransientShown) {
            this.mTransientShown = false;
            handleTransientChanged();
        }
    }

    private void handleTransientChanged() {
        if (getView() != null) {
            NavigationBarView navigationBarView = this.mNavigationBarView;
            if (navigationBarView != null) {
                navigationBarView.onTransientStateChanged(this.mTransientShown);
            }
            int barMode = barMode(this.mTransientShown, this.mAppearance);
            if (updateBarMode(barMode)) {
                this.mLightBarController.onNavigationBarModeChanged(barMode);
            }
        }
    }

    private boolean updateBarMode(int i) {
        int i2 = this.mNavigationBarMode;
        if (i2 == i) {
            return false;
        }
        if (i2 == 0 || i2 == 6) {
            this.mNavigationBarView.hideRecentsOnboarding();
        }
        this.mNavigationBarMode = i;
        checkNavBarModes();
        this.mAutoHideController.touchAutoHide();
        return true;
    }

    public void disable(int i, int i2, int i3, boolean z) {
        if (i == this.mDisplayId) {
            int i4 = 56623104 & i2;
            if (i4 != this.mDisabledFlags1) {
                this.mDisabledFlags1 = i4;
                NavigationBarView navigationBarView = this.mNavigationBarView;
                if (navigationBarView != null) {
                    navigationBarView.setDisabledFlags(i2);
                }
                updateScreenPinningGestures();
            }
            if (this.mIsOnDefaultDisplay) {
                int i5 = i3 & 16;
                if (i5 != this.mDisabledFlags2) {
                    this.mDisabledFlags2 = i5;
                    setDisabled2Flags(i5);
                }
            }
        }
    }

    private void setDisabled2Flags(int i) {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.getRotationButtonController().onDisable2FlagChanged(i);
        }
    }

    private void refreshLayout(int i) {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.setLayoutDirection(i);
        }
    }

    private boolean shouldDisableNavbarGestures() {
        return !this.mDeviceProvisionedController.isDeviceProvisioned() || (this.mDisabledFlags1 & 33554432) != 0;
    }

    private void repositionNavigationBar() {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null && navigationBarView.isAttachedToWindow()) {
            prepareNavigationBarView();
            this.mWindowManager.updateViewLayout((View) this.mNavigationBarView.getParent(), ((View) this.mNavigationBarView.getParent()).getLayoutParams());
        }
    }

    /* access modifiers changed from: private */
    public void updateScreenPinningGestures() {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            boolean isRecentsButtonVisible = navigationBarView.isRecentsButtonVisible();
            ButtonDispatcher backButton = this.mNavigationBarView.getBackButton();
            if (isRecentsButtonVisible) {
                backButton.setOnLongClickListener(new OnLongClickListener() {
                    public final boolean onLongClick(View view) {
                        return NavigationBarFragment.this.onLongPressBackRecents(view);
                    }
                });
            } else {
                backButton.setOnLongClickListener(new OnLongClickListener() {
                    public final boolean onLongClick(View view) {
                        return NavigationBarFragment.this.onLongPressBackHome(view);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyNavigationBarScreenOn() {
        this.mNavigationBarView.updateNavButtonIcons();
    }

    private void prepareNavigationBarView() {
        this.mNavigationBarView.reorient();
        ButtonDispatcher recentsButton = this.mNavigationBarView.getRecentsButton();
        recentsButton.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                NavigationBarFragment.this.onRecentsClick(view);
            }
        });
        recentsButton.setOnTouchListener(new OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return NavigationBarFragment.this.onRecentsTouch(view, motionEvent);
            }
        });
        recentsButton.setLongClickable(true);
        recentsButton.setOnLongClickListener(new OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return NavigationBarFragment.this.onLongPressBackRecents(view);
            }
        });
        this.mNavigationBarView.getBackButton().setLongClickable(true);
        ButtonDispatcher homeButton = this.mNavigationBarView.getHomeButton();
        homeButton.setOnTouchListener(new OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return NavigationBarFragment.this.onHomeTouch(view, motionEvent);
            }
        });
        homeButton.setOnLongClickListener(new OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return NavigationBarFragment.this.onHomeLongClick(view);
            }
        });
        ButtonDispatcher accessibilityButton = this.mNavigationBarView.getAccessibilityButton();
        accessibilityButton.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                NavigationBarFragment.this.onAccessibilityClick(view);
            }
        });
        accessibilityButton.setOnLongClickListener(new OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return NavigationBarFragment.this.onAccessibilityLongClick(view);
            }
        });
        updateAccessibilityServicesState(this.mAccessibilityManager);
        updateScreenPinningGestures();
    }

    /* access modifiers changed from: private */
    public boolean onHomeTouch(View view, MotionEvent motionEvent) {
        if (this.mHomeBlockedThisTouch && motionEvent.getActionMasked() != 0) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mHomeBlockedThisTouch = false;
            TelecomManager telecomManager = (TelecomManager) getContext().getSystemService(TelecomManager.class);
            if (telecomManager != null && telecomManager.isRinging() && ((StatusBar) this.mStatusBarLazy.get()).isKeyguardShowing()) {
                Log.i("NavigationBar", "Ignoring HOME; there's a ringing incoming call. No heads up");
                this.mHomeBlockedThisTouch = true;
                return true;
            }
        } else if (action == 1 || action == 3) {
            ((StatusBar) this.mStatusBarLazy.get()).awakenDreams();
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void onVerticalChanged(boolean z) {
        ((StatusBar) this.mStatusBarLazy.get()).setQsScrimEnabled(!z);
    }

    /* access modifiers changed from: private */
    public boolean onNavigationTouch(View view, MotionEvent motionEvent) {
        this.mAutoHideController.checkUserAutoHide(motionEvent);
        return false;
    }

    /* access modifiers changed from: 0000 */
    public boolean onHomeLongClick(View view) {
        if (!this.mNavigationBarView.isRecentsButtonVisible() && ActivityManagerWrapper.getInstance().isScreenPinningActive()) {
            return onLongPressBackHome(view);
        }
        if (shouldDisableNavbarGestures()) {
            return false;
        }
        this.mMetricsLogger.action(239);
        Bundle bundle = new Bundle();
        bundle.putInt("invocation_type", 5);
        this.mAssistManager.startAssist(bundle);
        ((StatusBar) this.mStatusBarLazy.get()).awakenDreams();
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.abortCurrentGesture();
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean onRecentsTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            this.mCommandQueue.preloadRecentApps();
        } else if (action == 3) {
            this.mCommandQueue.cancelPreloadRecentApps();
        } else if (action == 1 && !view.isPressed()) {
            this.mCommandQueue.cancelPreloadRecentApps();
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void onRecentsClick(View view) {
        if (LatencyTracker.isEnabled(getContext())) {
            LatencyTracker.getInstance(getContext()).onActionStart(1);
        }
        ((StatusBar) this.mStatusBarLazy.get()).awakenDreams();
        this.mCommandQueue.toggleRecentApps();
    }

    /* access modifiers changed from: private */
    public boolean onLongPressBackHome(View view) {
        return onLongPressNavigationButtons(view, C2011R$id.back, C2011R$id.home);
    }

    /* access modifiers changed from: private */
    public boolean onLongPressBackRecents(View view) {
        return onLongPressNavigationButtons(view, C2011R$id.back, C2011R$id.recent_apps);
    }

    private boolean onLongPressNavigationButtons(View view, int i, int i2) {
        boolean z;
        boolean z2;
        ButtonDispatcher buttonDispatcher;
        try {
            IActivityTaskManager service = ActivityTaskManager.getService();
            boolean isTouchExplorationEnabled = this.mAccessibilityManager.isTouchExplorationEnabled();
            boolean isInLockTaskMode = service.isInLockTaskMode();
            if (isInLockTaskMode && !isTouchExplorationEnabled) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - this.mLastLockToAppLongPress < 200) {
                    service.stopSystemLockTaskMode();
                    this.mNavigationBarView.updateNavButtonIcons();
                    return true;
                }
                if (view.getId() == i) {
                    if (i2 == C2011R$id.recent_apps) {
                        buttonDispatcher = this.mNavigationBarView.getRecentsButton();
                    } else {
                        buttonDispatcher = this.mNavigationBarView.getHomeButton();
                    }
                    if (!buttonDispatcher.getCurrentView().isPressed()) {
                        z = true;
                        this.mLastLockToAppLongPress = currentTimeMillis;
                    }
                }
                z = false;
                this.mLastLockToAppLongPress = currentTimeMillis;
            } else if (view.getId() == i) {
                z = true;
            } else if (isTouchExplorationEnabled && isInLockTaskMode) {
                service.stopSystemLockTaskMode();
                this.mNavigationBarView.updateNavButtonIcons();
                return true;
            } else if (view.getId() == i2) {
                if (i2 == C2011R$id.recent_apps) {
                    z2 = onLongPressRecents();
                } else {
                    z2 = onHomeLongClick(this.mNavigationBarView.getHomeButton().getCurrentView());
                }
                return z2;
            } else {
                z = false;
            }
            if (z) {
                KeyButtonView keyButtonView = (KeyButtonView) view;
                keyButtonView.sendEvent(0, 128);
                keyButtonView.sendAccessibilityEvent(2);
                return true;
            }
        } catch (RemoteException e) {
            Log.d("NavigationBar", "Unable to reach activity manager", e);
        }
        return false;
    }

    private boolean onLongPressRecents() {
        if (this.mRecentsOptional.isPresent() || !ActivityTaskManager.supportsMultiWindow(getContext()) || !this.mDivider.getView().getSnapAlgorithm().isSplitScreenFeasible() || ActivityManager.isLowRamDeviceStatic() || this.mOverviewProxyService.getProxy() != null) {
            return false;
        }
        return ((StatusBar) this.mStatusBarLazy.get()).toggleSplitScreenMode(271, 286);
    }

    /* access modifiers changed from: private */
    public void onAccessibilityClick(View view) {
        Display display = view.getDisplay();
        this.mAccessibilityManager.notifyAccessibilityButtonClicked(display != null ? display.getDisplayId() : 0);
    }

    /* access modifiers changed from: private */
    public boolean onAccessibilityLongClick(View view) {
        Intent intent = new Intent("com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON");
        intent.addFlags(268468224);
        intent.putExtra("com.android.internal.intent.extra.SHORTCUT_TYPE", 0);
        view.getContext().startActivityAsUser(intent, UserHandle.CURRENT);
        return true;
    }

    /* access modifiers changed from: private */
    public void updateAccessibilityServicesState(AccessibilityManager accessibilityManager) {
        boolean z = true;
        int a11yButtonState = getA11yButtonState(new boolean[1]);
        boolean z2 = (a11yButtonState & 16) != 0;
        if ((a11yButtonState & 32) == 0) {
            z = false;
        }
        this.mNavigationBarView.setAccessibilityButtonState(z2, z);
        updateSystemUiStateFlags(a11yButtonState);
    }

    public void updateSystemUiStateFlags(int i) {
        if (i < 0) {
            i = getA11yButtonState(null);
        }
        boolean z = false;
        boolean z2 = (i & 16) != 0;
        if ((i & 32) != 0) {
            z = true;
        }
        SysUiState sysUiState = this.mSysUiFlagsContainer;
        sysUiState.setFlag(16, z2);
        sysUiState.setFlag(32, z);
        sysUiState.setFlag(2, !isNavBarWindowVisible());
        sysUiState.commitUpdate(this.mDisplayId);
    }

    public int getA11yButtonState(boolean[] zArr) {
        int i;
        List enabledAccessibilityServiceList = this.mAccessibilityManager.getEnabledAccessibilityServiceList(-1);
        int i2 = 0;
        int size = this.mAccessibilityManager.getAccessibilityShortcutTargets(0).size();
        int size2 = enabledAccessibilityServiceList.size() - 1;
        boolean z = false;
        while (true) {
            i = 16;
            if (size2 < 0) {
                break;
            }
            int i3 = ((AccessibilityServiceInfo) enabledAccessibilityServiceList.get(size2)).feedbackType;
            if (!(i3 == 0 || i3 == 16)) {
                z = true;
            }
            size2--;
        }
        if (zArr != null) {
            zArr[0] = z;
        }
        if (size < 1) {
            i = 0;
        }
        if (size >= 2) {
            i2 = 32;
        }
        return i | i2;
    }

    /* access modifiers changed from: private */
    public void sendAssistantAvailability(boolean z) {
        if (this.mOverviewProxyService.getProxy() != null) {
            try {
                this.mOverviewProxyService.getProxy().onAssistantAvailable(z && QuickStepContract.isGesturalMode(this.mNavBarMode));
            } catch (RemoteException unused) {
                Log.w("NavigationBar", "Unable to send assistant availability data to launcher");
            }
        }
    }

    public void touchAutoDim() {
        getBarTransitions().setAutoDim(false);
        this.mHandler.removeCallbacks(this.mAutoDim);
        int state = this.mStatusBarStateController.getState();
        if (state != 1 && state != 2) {
            this.mHandler.postDelayed(this.mAutoDim, 2250);
        }
    }

    public void setLightBarController(LightBarController lightBarController) {
        this.mLightBarController = lightBarController;
        lightBarController.setNavigationBar(this.mNavigationBarView.getLightTransitionsController());
    }

    public void setAutoHideController(AutoHideController autoHideController) {
        AutoHideController autoHideController2 = this.mAutoHideController;
        if (autoHideController2 != null) {
            autoHideController2.removeAutoHideUiElement(this.mAutoHideUiElement);
        }
        this.mAutoHideController = autoHideController;
        if (autoHideController != null) {
            autoHideController.addAutoHideUiElement(this.mAutoHideUiElement);
        }
    }

    /* access modifiers changed from: private */
    public boolean isTransientShown() {
        return this.mTransientShown;
    }

    private void checkBarModes() {
        if (this.mIsOnDefaultDisplay) {
            ((StatusBar) this.mStatusBarLazy.get()).checkBarModes();
        } else {
            checkNavBarModes();
        }
    }

    public boolean isNavBarWindowVisible() {
        return this.mNavigationBarWindowState == 0;
    }

    public void checkNavBarModes() {
        this.mNavigationBarView.getBarTransitions().transitionTo(this.mNavigationBarMode, ((StatusBar) this.mStatusBarLazy.get()).isDeviceInteractive() && this.mNavigationBarWindowState != 2);
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
        updateScreenPinningGestures();
        if (ActivityManagerWrapper.getInstance().getCurrentUserId() != 0) {
            this.mHandler.post(new Runnable() {
                public final void run() {
                    NavigationBarFragment.this.lambda$onNavigationModeChanged$2$NavigationBarFragment();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNavigationModeChanged$2 */
    public /* synthetic */ void lambda$onNavigationModeChanged$2$NavigationBarFragment() {
        FragmentHostManager.get(this.mNavigationBarView).reloadFragments();
    }

    public void disableAnimationsDuringHide(long j) {
        this.mNavigationBarView.setLayoutTransitionsEnabled(false);
        this.mNavigationBarView.postDelayed(new Runnable() {
            public final void run() {
                NavigationBarFragment.this.lambda$disableAnimationsDuringHide$3$NavigationBarFragment();
            }
        }, j + 448);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$disableAnimationsDuringHide$3 */
    public /* synthetic */ void lambda$disableAnimationsDuringHide$3$NavigationBarFragment() {
        this.mNavigationBarView.setLayoutTransitionsEnabled(true);
    }

    public AssistHandleViewController getAssistHandlerViewController() {
        return this.mAssistHandlerViewController;
    }

    public void transitionTo(int i, boolean z) {
        getBarTransitions().transitionTo(i, z);
    }

    public NavigationBarTransitions getBarTransitions() {
        return this.mNavigationBarView.getBarTransitions();
    }

    public void finishBarAnimations() {
        this.mNavigationBarView.getBarTransitions().finishAnimations();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$NavigationBarFragment(Integer num) {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null && navigationBarView.needsReorient(num.intValue())) {
            repositionNavigationBar();
        }
    }

    public static View create(Context context, final FragmentListener fragmentListener) {
        LayoutParams layoutParams = new LayoutParams(-1, -1, 2019, 545521768, -3);
        layoutParams.token = new Binder();
        StringBuilder sb = new StringBuilder();
        sb.append("NavigationBar");
        sb.append(context.getDisplayId());
        layoutParams.setTitle(sb.toString());
        layoutParams.accessibilityTitle = context.getString(C2017R$string.nav_bar);
        layoutParams.windowAnimations = 0;
        layoutParams.privateFlags |= 16777216;
        final View inflate = LayoutInflater.from(context).inflate(C2013R$layout.navigation_bar_window, null);
        if (inflate == null) {
            return null;
        }
        inflate.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View view) {
                FragmentHostManager fragmentHostManager = FragmentHostManager.get(view);
                String str = "NavigationBar";
                fragmentHostManager.getFragmentManager().beginTransaction().replace(C2011R$id.navigation_bar_frame, NavigationBarFragment.this, str).commit();
                fragmentHostManager.addTagListener(str, fragmentListener);
            }

            public void onViewDetachedFromWindow(View view) {
                FragmentHostManager.removeAndDestroy(view);
                inflate.removeOnAttachStateChangeListener(this);
            }
        });
        ((WindowManager) context.getSystemService(WindowManager.class)).addView(inflate, layoutParams);
        return inflate;
    }

    /* access modifiers changed from: 0000 */
    public int getNavigationIconHints() {
        return this.mNavigationIconHints;
    }
}
