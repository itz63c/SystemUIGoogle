package com.android.systemui.biometrics;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.TaskStackListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.systemui.SystemUI;
import com.android.systemui.biometrics.AuthContainerView.Builder;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import java.util.List;

public class AuthController extends SystemUI implements Callbacks, AuthDialogCallback {
    @VisibleForTesting
    IActivityTaskManager mActivityTaskManager;
    @VisibleForTesting
    final BroadcastReceiver mBroadcastReceiver;
    private final CommandQueue mCommandQueue;
    @VisibleForTesting
    AuthDialog mCurrentDialog;
    private SomeArgs mCurrentDialogArgs;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private final Injector mInjector;
    @VisibleForTesting
    IBiometricServiceReceiverInternal mReceiver;
    /* access modifiers changed from: private */
    public final Runnable mTaskStackChangedRunnable;
    @VisibleForTesting
    BiometricTaskStackListener mTaskStackListener;
    private WindowManager mWindowManager;

    public class BiometricTaskStackListener extends TaskStackListener {
        public BiometricTaskStackListener() {
        }

        public void onTaskStackChanged() {
            AuthController.this.mHandler.post(AuthController.this.mTaskStackChangedRunnable);
        }
    }

    public static class Injector {
        /* access modifiers changed from: 0000 */
        public IActivityTaskManager getActivityTaskManager() {
            return ActivityTaskManager.getService();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AuthController() {
        String str = "BiometricPrompt/AuthController";
        AuthDialog authDialog = this.mCurrentDialog;
        if (authDialog != null) {
            try {
                String opPackageName = authDialog.getOpPackageName();
                StringBuilder sb = new StringBuilder();
                sb.append("Task stack changed, current client: ");
                sb.append(opPackageName);
                Log.w(str, sb.toString());
                List tasks = this.mActivityTaskManager.getTasks(1);
                if (!tasks.isEmpty()) {
                    String packageName = ((RunningTaskInfo) tasks.get(0)).topActivity.getPackageName();
                    if (!packageName.contentEquals(opPackageName)) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Evicting client due to: ");
                        sb2.append(packageName);
                        Log.w(str, sb2.toString());
                        this.mCurrentDialog.dismissWithoutCallback(true);
                        this.mCurrentDialog = null;
                        if (this.mReceiver != null) {
                            this.mReceiver.onDialogDismissed(3, null);
                            this.mReceiver = null;
                        }
                    }
                }
            } catch (RemoteException e) {
                Log.e(str, "Remote exception", e);
            }
        }
    }

    public void onTryAgainPressed() {
        IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal = this.mReceiver;
        String str = "BiometricPrompt/AuthController";
        if (iBiometricServiceReceiverInternal == null) {
            Log.e(str, "onTryAgainPressed: Receiver is null");
            return;
        }
        try {
            iBiometricServiceReceiverInternal.onTryAgainPressed();
        } catch (RemoteException e) {
            Log.e(str, "RemoteException when handling try again", e);
        }
    }

    public void onDeviceCredentialPressed() {
        IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal = this.mReceiver;
        String str = "BiometricPrompt/AuthController";
        if (iBiometricServiceReceiverInternal == null) {
            Log.e(str, "onDeviceCredentialPressed: Receiver is null");
            return;
        }
        try {
            iBiometricServiceReceiverInternal.onDeviceCredentialPressed();
        } catch (RemoteException e) {
            Log.e(str, "RemoteException when handling credential button", e);
        }
    }

    public void onDismissed(int i, byte[] bArr) {
        switch (i) {
            case 1:
                sendResultAndCleanUp(3, bArr);
                return;
            case 2:
                sendResultAndCleanUp(2, bArr);
                return;
            case 3:
                sendResultAndCleanUp(1, bArr);
                return;
            case 4:
                sendResultAndCleanUp(4, bArr);
                return;
            case 5:
                sendResultAndCleanUp(5, bArr);
                return;
            case 6:
                sendResultAndCleanUp(6, bArr);
                return;
            case 7:
                sendResultAndCleanUp(7, bArr);
                return;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Unhandled reason: ");
                sb.append(i);
                Log.e("BiometricPrompt/AuthController", sb.toString());
                return;
        }
    }

    private void sendResultAndCleanUp(int i, byte[] bArr) {
        IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal = this.mReceiver;
        String str = "BiometricPrompt/AuthController";
        if (iBiometricServiceReceiverInternal == null) {
            Log.e(str, "sendResultAndCleanUp: Receiver is null");
            return;
        }
        try {
            iBiometricServiceReceiverInternal.onDialogDismissed(i, bArr);
        } catch (RemoteException e) {
            Log.w(str, "Remote exception", e);
        }
        onDialogDismissed(i);
    }

    public AuthController(Context context, CommandQueue commandQueue) {
        this(context, commandQueue, new Injector());
    }

    @VisibleForTesting
    AuthController(Context context, CommandQueue commandQueue, Injector injector) {
        super(context);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (AuthController.this.mCurrentDialog != null) {
                    if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                        String str = "BiometricPrompt/AuthController";
                        Log.w(str, "ACTION_CLOSE_SYSTEM_DIALOGS received");
                        AuthController.this.mCurrentDialog.dismissWithoutCallback(true);
                        AuthController authController = AuthController.this;
                        authController.mCurrentDialog = null;
                        try {
                            if (authController.mReceiver != null) {
                                authController.mReceiver.onDialogDismissed(3, null);
                                AuthController.this.mReceiver = null;
                            }
                        } catch (RemoteException e) {
                            Log.e(str, "Remote exception", e);
                        }
                    }
                }
            }
        };
        this.mTaskStackChangedRunnable = new Runnable() {
            public final void run() {
                AuthController.this.lambda$new$0$AuthController();
            }
        };
        this.mCommandQueue = commandQueue;
        this.mInjector = injector;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        context.registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    public void start() {
        this.mCommandQueue.addCallback((Callbacks) this);
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mActivityTaskManager = this.mInjector.getActivityTaskManager();
        try {
            BiometricTaskStackListener biometricTaskStackListener = new BiometricTaskStackListener();
            this.mTaskStackListener = biometricTaskStackListener;
            this.mActivityTaskManager.registerTaskStackListener(biometricTaskStackListener);
        } catch (RemoteException e) {
            Log.w("BiometricPrompt/AuthController", "Unable to register task stack listener", e);
        }
    }

    public void showAuthenticationDialog(Bundle bundle, IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal, int i, boolean z, int i2, String str, long j) {
        boolean z2;
        int authenticators = Utils.getAuthenticators(bundle);
        StringBuilder sb = new StringBuilder();
        sb.append("showAuthenticationDialog, authenticators: ");
        sb.append(authenticators);
        sb.append(", biometricModality: ");
        sb.append(i);
        sb.append(", requireConfirmation: ");
        sb.append(z);
        sb.append(", operationId: ");
        sb.append(j);
        String sb2 = sb.toString();
        String str2 = "BiometricPrompt/AuthController";
        Log.d(str2, sb2);
        SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = bundle;
        obtain.arg2 = iBiometricServiceReceiverInternal;
        obtain.argi1 = i;
        obtain.arg3 = Boolean.valueOf(z);
        obtain.argi2 = i2;
        obtain.arg4 = str;
        obtain.arg5 = Long.valueOf(j);
        if (this.mCurrentDialog != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("mCurrentDialog: ");
            sb3.append(this.mCurrentDialog);
            Log.w(str2, sb3.toString());
            z2 = true;
        } else {
            z2 = false;
        }
        showDialog(obtain, z2, null);
    }

    public void onBiometricAuthenticated() {
        this.mCurrentDialog.onAuthenticationSucceeded();
    }

    public void onBiometricHelp(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("onBiometricHelp: ");
        sb.append(str);
        Log.d("BiometricPrompt/AuthController", sb.toString());
        this.mCurrentDialog.onHelp(str);
    }

    private String getErrorString(int i, int i2, int i3) {
        if (i != 2) {
            return i != 8 ? "" : FaceManager.getErrorString(this.mContext, i2, i3);
        }
        return FingerprintManager.getErrorString(this.mContext, i2, i3);
    }

    public void onBiometricError(int i, int i2, int i3) {
        String str;
        boolean z = false;
        String str2 = "BiometricPrompt/AuthController";
        Log.d(str2, String.format("onBiometricError(%d, %d, %d)", new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)}));
        boolean z2 = i2 == 7 || i2 == 9;
        if (i2 == 100 || i2 == 3) {
            z = true;
        }
        if (this.mCurrentDialog.isAllowDeviceCredentials() && z2) {
            Log.d(str2, "onBiometricError, lockout");
            this.mCurrentDialog.animateToCredentialUI();
        } else if (z) {
            if (i2 == 100) {
                str = this.mContext.getString(17039656);
            } else {
                str = getErrorString(i, i2, i3);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("onBiometricError, soft error: ");
            sb.append(str);
            Log.d(str2, sb.toString());
            this.mCurrentDialog.onAuthenticationFailed(str);
        } else {
            String errorString = getErrorString(i, i2, i3);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("onBiometricError, hard error: ");
            sb2.append(errorString);
            Log.d(str2, sb2.toString());
            this.mCurrentDialog.onError(errorString);
        }
    }

    public void hideAuthenticationDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("hideAuthenticationDialog: ");
        sb.append(this.mCurrentDialog);
        Log.d("BiometricPrompt/AuthController", sb.toString());
        AuthDialog authDialog = this.mCurrentDialog;
        if (authDialog != null) {
            authDialog.dismissFromSystemServer();
            this.mCurrentDialog = null;
        }
    }

    private void showDialog(SomeArgs someArgs, boolean z, Bundle bundle) {
        this.mCurrentDialogArgs = someArgs;
        int i = someArgs.argi1;
        Bundle bundle2 = (Bundle) someArgs.arg1;
        boolean booleanValue = ((Boolean) someArgs.arg3).booleanValue();
        int i2 = someArgs.argi2;
        AuthDialog buildDialog = buildDialog(bundle2, booleanValue, i2, i, (String) someArgs.arg4, z, ((Long) someArgs.arg5).longValue());
        String str = "BiometricPrompt/AuthController";
        if (buildDialog == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unsupported type: ");
            sb.append(i);
            Log.e(str, sb.toString());
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("userId: ");
        sb2.append(i2);
        sb2.append(" savedState: ");
        sb2.append(bundle);
        sb2.append(" mCurrentDialog: ");
        sb2.append(this.mCurrentDialog);
        sb2.append(" newDialog: ");
        sb2.append(buildDialog);
        sb2.append(" type: ");
        sb2.append(i);
        Log.d(str, sb2.toString());
        AuthDialog authDialog = this.mCurrentDialog;
        if (authDialog != null) {
            authDialog.dismissWithoutCallback(false);
        }
        this.mReceiver = (IBiometricServiceReceiverInternal) someArgs.arg2;
        this.mCurrentDialog = buildDialog;
        buildDialog.show(this.mWindowManager, bundle);
    }

    private void onDialogDismissed(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("onDialogDismissed: ");
        sb.append(i);
        String sb2 = sb.toString();
        String str = "BiometricPrompt/AuthController";
        Log.d(str, sb2);
        if (this.mCurrentDialog == null) {
            Log.w(str, "Dialog already dismissed");
        }
        this.mReceiver = null;
        this.mCurrentDialog = null;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mCurrentDialog != null) {
            Bundle bundle = new Bundle();
            this.mCurrentDialog.onSaveState(bundle);
            this.mCurrentDialog.dismissWithoutCallback(false);
            this.mCurrentDialog = null;
            if (bundle.getInt("container_state") != 4) {
                if (bundle.getBoolean("credential_showing")) {
                    ((Bundle) this.mCurrentDialogArgs.arg1).putInt("authenticators_allowed", 32768);
                }
                showDialog(this.mCurrentDialogArgs, true, bundle);
            }
        }
    }

    /* access modifiers changed from: protected */
    public AuthDialog buildDialog(Bundle bundle, boolean z, int i, int i2, String str, boolean z2, long j) {
        Builder builder = new Builder(this.mContext);
        builder.setCallback(this);
        builder.setBiometricPromptBundle(bundle);
        builder.setRequireConfirmation(z);
        builder.setUserId(i);
        builder.setOpPackageName(str);
        builder.setSkipIntro(z2);
        builder.setOperationId(j);
        return builder.build(i2);
    }
}
