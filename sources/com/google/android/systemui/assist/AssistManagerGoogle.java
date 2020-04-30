package com.google.android.systemui.assist;

import android.content.Context;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionListener.Stub;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistManager.UiController;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.model.SysUiState;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler.AssistantPresenceChangeListener;
import com.google.android.systemui.assist.uihints.GoogleDefaultUiController;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.google.android.systemui.assist.uihints.NgaUiController;
import dagger.Lazy;
import java.util.Objects;

public class AssistManagerGoogle extends AssistManager {
    private final AssistantPresenceHandler mAssistantPresenceHandler;
    private boolean mCheckAssistantStatus = true;
    private final GoogleDefaultUiController mDefaultUiController;
    private boolean mGoogleIsAssistant;
    private int mNavigationMode;
    private boolean mNgaIsAssistant;
    /* access modifiers changed from: private */
    public final NgaMessageHandler mNgaMessageHandler;
    private final NgaUiController mNgaUiController;
    /* access modifiers changed from: private */
    public final Runnable mOnProcessBundle;
    /* access modifiers changed from: private */
    public final OpaEnabledReceiver mOpaEnabledReceiver;
    private boolean mSqueezeSetUp;
    private UiController mUiController;
    private final Handler mUiHandler;

    public boolean shouldShowOrb() {
        return false;
    }

    public AssistManagerGoogle(DeviceProvisionedController deviceProvisionedController, Context context, AssistUtils assistUtils, AssistHandleBehaviorController assistHandleBehaviorController, NgaUiController ngaUiController, CommandQueue commandQueue, BroadcastDispatcher broadcastDispatcher, PhoneStateMonitor phoneStateMonitor, OverviewProxyService overviewProxyService, OpaEnabledDispatcher opaEnabledDispatcher, KeyguardUpdateMonitor keyguardUpdateMonitor, NavigationModeController navigationModeController, ConfigurationController configurationController, AssistantPresenceHandler assistantPresenceHandler, NgaMessageHandler ngaMessageHandler, Lazy<SysUiState> lazy, Handler handler) {
        AssistantPresenceHandler assistantPresenceHandler2 = assistantPresenceHandler;
        super(deviceProvisionedController, context, assistUtils, assistHandleBehaviorController, commandQueue, phoneStateMonitor, overviewProxyService, configurationController, lazy);
        this.mUiHandler = handler;
        this.mOpaEnabledReceiver = new OpaEnabledReceiver(this.mContext, broadcastDispatcher);
        addOpaEnabledListener(opaEnabledDispatcher);
        keyguardUpdateMonitor.registerCallback(new KeyguardUpdateMonitorCallback() {
            public void onUserSwitching(int i) {
                AssistManagerGoogle.this.mOpaEnabledReceiver.onUserSwitching(i);
            }
        });
        this.mNgaUiController = ngaUiController;
        Context context2 = context;
        GoogleDefaultUiController googleDefaultUiController = new GoogleDefaultUiController(context);
        this.mDefaultUiController = googleDefaultUiController;
        this.mUiController = googleDefaultUiController;
        this.mNavigationMode = navigationModeController.addListener(new ModeChangedListener() {
            public final void onNavigationModeChanged(int i) {
                AssistManagerGoogle.this.lambda$new$0$AssistManagerGoogle(i);
            }
        });
        this.mAssistantPresenceHandler = assistantPresenceHandler2;
        assistantPresenceHandler2.registerAssistantPresenceChangeListener(new AssistantPresenceChangeListener() {
            public final void onAssistantPresenceChanged(boolean z, boolean z2) {
                AssistManagerGoogle.this.lambda$new$1$AssistManagerGoogle(z, z2);
            }
        });
        this.mNgaMessageHandler = ngaMessageHandler;
        this.mOnProcessBundle = new Runnable() {
            public final void run() {
                AssistManagerGoogle.this.lambda$new$2$AssistManagerGoogle();
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AssistManagerGoogle(int i) {
        this.mNavigationMode = i;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$AssistManagerGoogle(boolean z, boolean z2) {
        if (!(this.mGoogleIsAssistant == z && this.mNgaIsAssistant == z2)) {
            if (!z2) {
                if (!this.mUiController.equals(this.mDefaultUiController)) {
                    UiController uiController = this.mUiController;
                    this.mUiController = this.mDefaultUiController;
                    Handler handler = this.mUiHandler;
                    Objects.requireNonNull(uiController);
                    handler.post(new Runnable() {
                        public final void run() {
                            UiController.this.hide();
                        }
                    });
                }
                this.mDefaultUiController.setGoogleAssistant(z);
            } else if (!this.mUiController.equals(this.mNgaUiController)) {
                UiController uiController2 = this.mUiController;
                this.mUiController = this.mNgaUiController;
                Handler handler2 = this.mUiHandler;
                Objects.requireNonNull(uiController2);
                handler2.post(new Runnable() {
                    public final void run() {
                        UiController.this.hide();
                    }
                });
            }
            this.mGoogleIsAssistant = z;
            this.mNgaIsAssistant = z2;
        }
        this.mCheckAssistantStatus = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$AssistManagerGoogle() {
        this.mAssistantPresenceHandler.requestAssistantPresenceUpdate();
        this.mCheckAssistantStatus = false;
    }

    public boolean shouldUseHomeButtonAnimations() {
        return !QuickStepContract.isGesturalMode(this.mNavigationMode);
    }

    /* access modifiers changed from: protected */
    public void registerVoiceInteractionSessionListener() {
        this.mAssistUtils.registerVoiceInteractionSessionListener(new Stub() {
            public void onVoiceSessionHidden() throws RemoteException {
            }

            public void onVoiceSessionShown() throws RemoteException {
            }

            public void onSetUiHints(Bundle bundle) {
                String string = bundle.getString("action");
                if ("show_assist_handles".equals(string)) {
                    AssistManagerGoogle.this.requestAssistHandles();
                } else if ("set_assist_gesture_constrained".equals(string)) {
                    SysUiState sysUiState = (SysUiState) AssistManagerGoogle.this.mSysUiState.get();
                    sysUiState.setFlag(8192, bundle.getBoolean("should_constrain", false));
                    sysUiState.commitUpdate(0);
                } else {
                    AssistManagerGoogle.this.mNgaMessageHandler.lambda$processBundle$0(bundle, AssistManagerGoogle.this.mOnProcessBundle);
                }
            }
        });
    }

    public void onInvocationProgress(int i, float f) {
        if (f == 0.0f || f == 1.0f) {
            this.mCheckAssistantStatus = true;
            if (i == 2) {
                checkSqueezeGestureStatus();
            }
        }
        if (this.mCheckAssistantStatus) {
            this.mAssistantPresenceHandler.requestAssistantPresenceUpdate();
            this.mCheckAssistantStatus = false;
        }
        if (i != 2 || this.mSqueezeSetUp) {
            this.mUiController.onInvocationProgress(i, f);
        }
    }

    public void onGestureCompletion(float f) {
        this.mCheckAssistantStatus = true;
        this.mUiController.onGestureCompletion(f / this.mContext.getResources().getDisplayMetrics().density);
    }

    public void logStartAssist(int i, int i2) {
        this.mAssistantPresenceHandler.requestAssistantPresenceUpdate();
        this.mCheckAssistantStatus = false;
        MetricsLogger.action(new LogMaker(1716).setType(1).setSubtype(((this.mAssistantPresenceHandler.isNgaAssistant() ? 1 : 0) << true) | toLoggingSubType(i, i2)));
    }

    public void addOpaEnabledListener(OpaEnabledListener opaEnabledListener) {
        this.mOpaEnabledReceiver.addOpaEnabledListener(opaEnabledListener);
    }

    public boolean isActiveAssistantNga() {
        return this.mNgaIsAssistant;
    }

    public void dispatchOpaEnabledState() {
        this.mOpaEnabledReceiver.dispatchOpaEnabledState();
    }

    private void checkSqueezeGestureStatus() {
        boolean z = false;
        if (Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_setup_complete", 0) == 1) {
            z = true;
        }
        this.mSqueezeSetUp = z;
    }
}
