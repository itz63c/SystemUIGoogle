package com.android.systemui.assist;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.metrics.LogMaker;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionListener;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractionSessionShowCallback.Stub;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.C2004R$anim;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2013R$layout;
import com.android.systemui.DejankUtils;
import com.android.systemui.assist.p003ui.DefaultUiController;
import com.android.systemui.model.SysUiState;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.OverviewProxyService.OverviewProxyListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.Lazy;
import java.util.Objects;
import java.util.function.Supplier;

public class AssistManager {
    private final AssistDisclosure mAssistDisclosure;
    protected final AssistUtils mAssistUtils;
    private final CommandQueue mCommandQueue;
    private ConfigurationListener mConfigurationListener = new ConfigurationListener() {
        public void onConfigChanged(Configuration configuration) {
            boolean z;
            if (AssistManager.this.mInterestingConfigChanges.applyNewConfig(AssistManager.this.mContext.getResources())) {
                if (AssistManager.this.mView != null) {
                    z = AssistManager.this.mView.isShowing();
                    AssistManager.this.mWindowManager.removeView(AssistManager.this.mView);
                } else {
                    z = false;
                }
                AssistManager assistManager = AssistManager.this;
                assistManager.mView = (AssistOrbContainer) LayoutInflater.from(assistManager.mContext).inflate(C2013R$layout.assist_orb, null);
                AssistManager.this.mView.setVisibility(8);
                AssistManager.this.mView.setSystemUiVisibility(1792);
                AssistManager.this.mWindowManager.addView(AssistManager.this.mView, AssistManager.this.getLayoutParams());
                if (z) {
                    AssistManager.this.mView.show(true, false);
                }
            }
        }
    };
    protected final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final AssistHandleBehaviorController mHandleController;
    /* access modifiers changed from: private */
    public Runnable mHideRunnable = new Runnable() {
        public void run() {
            AssistManager.this.mView.removeCallbacks(this);
            AssistManager.this.mView.show(false, true);
        }
    };
    /* access modifiers changed from: private */
    public final InterestingConfigChanges mInterestingConfigChanges;
    private final PhoneStateMonitor mPhoneStateMonitor;
    private final boolean mShouldEnableOrb;
    private IVoiceInteractionSessionShowCallback mShowCallback = new Stub() {
        public void onFailed() throws RemoteException {
            AssistManager.this.mView.post(AssistManager.this.mHideRunnable);
        }

        public void onShown() throws RemoteException {
            AssistManager.this.mView.post(AssistManager.this.mHideRunnable);
        }
    };
    protected final Lazy<SysUiState> mSysUiState;
    private final UiController mUiController;
    /* access modifiers changed from: private */
    public AssistOrbContainer mView;
    /* access modifiers changed from: private */
    public final WindowManager mWindowManager;

    public interface UiController {
        void hide();

        void onGestureCompletion(float f);

        void onInvocationProgress(int i, float f);
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowOrb() {
        return false;
    }

    public AssistManager(DeviceProvisionedController deviceProvisionedController, Context context, AssistUtils assistUtils, AssistHandleBehaviorController assistHandleBehaviorController, CommandQueue commandQueue, PhoneStateMonitor phoneStateMonitor, OverviewProxyService overviewProxyService, ConfigurationController configurationController, Lazy<SysUiState> lazy) {
        this.mContext = context;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mCommandQueue = commandQueue;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mAssistUtils = assistUtils;
        this.mAssistDisclosure = new AssistDisclosure(context, new Handler());
        this.mPhoneStateMonitor = phoneStateMonitor;
        this.mHandleController = assistHandleBehaviorController;
        configurationController.addCallback(this.mConfigurationListener);
        registerVoiceInteractionSessionListener();
        this.mInterestingConfigChanges = new InterestingConfigChanges(-2147482748);
        this.mConfigurationListener.onConfigChanged(context.getResources().getConfiguration());
        this.mShouldEnableOrb = !ActivityManager.isLowRamDeviceStatic();
        this.mUiController = new DefaultUiController(this.mContext);
        this.mSysUiState = lazy;
        overviewProxyService.addCallback((OverviewProxyListener) new OverviewProxyListener() {
            public void onAssistantProgress(float f) {
                AssistManager.this.onInvocationProgress(1, f);
            }

            public void onAssistantGestureCompletion(float f) {
                AssistManager.this.onGestureCompletion(f);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void registerVoiceInteractionSessionListener() {
        this.mAssistUtils.registerVoiceInteractionSessionListener(new IVoiceInteractionSessionListener.Stub() {
            public void onVoiceSessionHidden() throws RemoteException {
            }

            public void onVoiceSessionShown() throws RemoteException {
            }

            public void onSetUiHints(Bundle bundle) {
                String string = bundle.getString("action");
                if ("show_assist_handles".equals(string)) {
                    AssistManager.this.requestAssistHandles();
                } else if ("set_assist_gesture_constrained".equals(string)) {
                    SysUiState sysUiState = (SysUiState) AssistManager.this.mSysUiState.get();
                    sysUiState.setFlag(8192, bundle.getBoolean("should_constrain", false));
                    sysUiState.commitUpdate(0);
                }
            }
        });
    }

    public void startAssist(Bundle bundle) {
        ComponentName assistInfo = getAssistInfo();
        if (assistInfo != null) {
            boolean equals = assistInfo.equals(getVoiceInteractorComponentName());
            if (!equals || (!isVoiceSessionRunning() && shouldShowOrb())) {
                showOrb(assistInfo, equals);
                this.mView.postDelayed(this.mHideRunnable, equals ? 2500 : 1000);
            }
            if (bundle == null) {
                bundle = new Bundle();
            }
            int i = bundle.getInt("invocation_type", 0);
            if (i == 1) {
                this.mHandleController.onAssistantGesturePerformed();
            }
            int phoneState = this.mPhoneStateMonitor.getPhoneState();
            bundle.putInt("invocation_phone_state", phoneState);
            bundle.putLong("invocation_time_ms", SystemClock.elapsedRealtime());
            logStartAssist(i, phoneState);
            startAssistInternal(bundle, assistInfo, equals);
        }
    }

    public void onInvocationProgress(int i, float f) {
        this.mUiController.onInvocationProgress(i, f);
    }

    public void onGestureCompletion(float f) {
        this.mUiController.onGestureCompletion(f);
    }

    /* access modifiers changed from: protected */
    public void requestAssistHandles() {
        this.mHandleController.onAssistHandlesRequested();
    }

    public void hideAssist() {
        this.mAssistUtils.hideCurrentSession();
    }

    /* access modifiers changed from: private */
    public LayoutParams getLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(-1, this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.assist_orb_scrim_height), 2033, 280, -3);
        layoutParams.token = new Binder();
        layoutParams.gravity = 8388691;
        layoutParams.setTitle("AssistPreviewPanel");
        layoutParams.softInputMode = 49;
        return layoutParams;
    }

    private void showOrb(ComponentName componentName, boolean z) {
        maybeSwapSearchIcon(componentName, z);
        if (this.mShouldEnableOrb) {
            this.mView.show(true, true);
        }
    }

    private void startAssistInternal(Bundle bundle, ComponentName componentName, boolean z) {
        if (z) {
            startVoiceInteractor(bundle);
        } else {
            startAssistActivity(bundle, componentName);
        }
    }

    private void startAssistActivity(Bundle bundle, ComponentName componentName) {
        if (this.mDeviceProvisionedController.isDeviceProvisioned()) {
            boolean z = false;
            this.mCommandQueue.animateCollapsePanels(3, false);
            if (Secure.getIntForUser(this.mContext.getContentResolver(), "assist_structure_enabled", 1, -2) != 0) {
                z = true;
            }
            SearchManager searchManager = (SearchManager) this.mContext.getSystemService("search");
            if (searchManager != null) {
                final Intent assistIntent = searchManager.getAssistIntent(z);
                if (assistIntent != null) {
                    assistIntent.setComponent(componentName);
                    assistIntent.putExtras(bundle);
                    if (z && AssistUtils.isDisclosureEnabled(this.mContext)) {
                        showDisclosure();
                    }
                    try {
                        final ActivityOptions makeCustomAnimation = ActivityOptions.makeCustomAnimation(this.mContext, C2004R$anim.search_launch_enter, C2004R$anim.search_launch_exit);
                        assistIntent.addFlags(268435456);
                        AsyncTask.execute(new Runnable() {
                            public void run() {
                                AssistManager.this.mContext.startActivityAsUser(assistIntent, makeCustomAnimation.toBundle(), new UserHandle(-2));
                            }
                        });
                    } catch (ActivityNotFoundException unused) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Activity not found for ");
                        sb.append(assistIntent.getAction());
                        Log.w("AssistManager", sb.toString());
                    }
                }
            }
        }
    }

    private void startVoiceInteractor(Bundle bundle) {
        this.mAssistUtils.showSessionForActiveService(bundle, 4, this.mShowCallback, null);
    }

    public void launchVoiceAssistFromKeyguard() {
        this.mAssistUtils.launchVoiceAssistFromKeyguard();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$canVoiceAssistBeLaunchedFromKeyguard$0 */
    public /* synthetic */ Boolean lambda$canVoiceAssistBeLaunchedFromKeyguard$0$AssistManager() {
        return Boolean.valueOf(this.mAssistUtils.activeServiceSupportsLaunchFromKeyguard());
    }

    public boolean canVoiceAssistBeLaunchedFromKeyguard() {
        return ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
            public final Object get() {
                return AssistManager.this.lambda$canVoiceAssistBeLaunchedFromKeyguard$0$AssistManager();
            }
        })).booleanValue();
    }

    public ComponentName getVoiceInteractorComponentName() {
        return this.mAssistUtils.getActiveServiceComponentName();
    }

    private boolean isVoiceSessionRunning() {
        return this.mAssistUtils.isSessionRunning();
    }

    private void maybeSwapSearchIcon(ComponentName componentName, boolean z) {
        replaceDrawable(this.mView.getOrb().getLogo(), componentName, "com.android.systemui.action_assist_icon", z);
    }

    public void replaceDrawable(ImageView imageView, ComponentName componentName, String str, boolean z) {
        Bundle bundle;
        if (componentName != null) {
            try {
                PackageManager packageManager = this.mContext.getPackageManager();
                if (z) {
                    bundle = packageManager.getServiceInfo(componentName, 128).metaData;
                } else {
                    bundle = packageManager.getActivityInfo(componentName, 128).metaData;
                }
                if (bundle != null) {
                    int i = bundle.getInt(str);
                    if (i != 0) {
                        imageView.setImageDrawable(packageManager.getResourcesForApplication(componentName.getPackageName()).getDrawable(i));
                        return;
                    }
                }
            } catch (NameNotFoundException unused) {
            } catch (NotFoundException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to swap drawable from ");
                sb.append(componentName.flattenToShortString());
                Log.w("AssistManager", sb.toString(), e);
            }
        }
        imageView.setImageDrawable(null);
    }

    public ComponentName getAssistInfoForUser(int i) {
        return this.mAssistUtils.getAssistComponentForUser(i);
    }

    private ComponentName getAssistInfo() {
        return getAssistInfoForUser(KeyguardUpdateMonitor.getCurrentUser());
    }

    public void showDisclosure() {
        this.mAssistDisclosure.postShow();
    }

    public void onLockscreenShown() {
        AssistUtils assistUtils = this.mAssistUtils;
        Objects.requireNonNull(assistUtils);
        DejankUtils.whitelistIpcs((Runnable) new Runnable(assistUtils) {
            public final /* synthetic */ AssistUtils f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.onLockscreenShown();
            }
        });
    }

    public long getAssistHandleShowAndGoRemainingDurationMs() {
        return this.mHandleController.getShowAndGoRemainingTimeMs();
    }

    public int toLoggingSubType(int i) {
        return toLoggingSubType(i, this.mPhoneStateMonitor.getPhoneState());
    }

    /* access modifiers changed from: protected */
    public void logStartAssist(int i, int i2) {
        MetricsLogger.action(new LogMaker(1716).setType(1).setSubtype(toLoggingSubType(i, i2)));
    }

    /* access modifiers changed from: protected */
    public final int toLoggingSubType(int i, int i2) {
        return ((this.mHandleController.areHandlesShowing() ^ true) | (i << 1)) | (i2 << 4) ? 1 : 0;
    }
}
