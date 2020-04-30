package com.android.keyguard;

import android.app.admin.IKeyguardCallback;
import android.app.admin.IKeyguardCallback.Stub;
import android.app.admin.IKeyguardClient;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.SurfaceControlViewHost.SurfacePackage;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;

public class AdminSecondaryLockScreenController {
    private final IKeyguardCallback mCallback = new Stub() {
        public void onDismiss() {
            AdminSecondaryLockScreenController.this.dismiss(UserHandle.getCallingUserId());
        }

        public void onRemoteContentReady(SurfacePackage surfacePackage) {
            if (AdminSecondaryLockScreenController.this.mHandler != null) {
                AdminSecondaryLockScreenController.this.mHandler.removeCallbacksAndMessages(null);
            }
            if (surfacePackage != null) {
                AdminSecondaryLockScreenController.this.mView.setChildSurfacePackage(surfacePackage);
            } else {
                AdminSecondaryLockScreenController.this.dismiss(KeyguardUpdateMonitor.getCurrentUser());
            }
        }
    };
    /* access modifiers changed from: private */
    public IKeyguardClient mClient;
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AdminSecondaryLockScreenController.this.mClient = IKeyguardClient.Stub.asInterface(iBinder);
            if (AdminSecondaryLockScreenController.this.mView.isAttachedToWindow() && AdminSecondaryLockScreenController.this.mClient != null) {
                AdminSecondaryLockScreenController.this.onSurfaceReady();
                try {
                    iBinder.linkToDeath(AdminSecondaryLockScreenController.this.mKeyguardClientDeathRecipient, 0);
                } catch (RemoteException e) {
                    Log.e("AdminSecondaryLockScreenController", "Lost connection to secondary lockscreen service", e);
                    AdminSecondaryLockScreenController.this.dismiss(KeyguardUpdateMonitor.getCurrentUser());
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            AdminSecondaryLockScreenController.this.mClient = null;
        }
    };
    private final Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private KeyguardSecurityCallback mKeyguardCallback;
    /* access modifiers changed from: private */
    public final DeathRecipient mKeyguardClientDeathRecipient = new DeathRecipient() {
        public final void binderDied() {
            AdminSecondaryLockScreenController.this.lambda$new$0$AdminSecondaryLockScreenController();
        }
    };
    private final ViewGroup mParent;
    @VisibleForTesting
    protected Callback mSurfaceHolderCallback = new Callback() {
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            AdminSecondaryLockScreenController.this.mUpdateMonitor.registerCallback(AdminSecondaryLockScreenController.this.mUpdateCallback);
            if (AdminSecondaryLockScreenController.this.mClient != null) {
                AdminSecondaryLockScreenController.this.onSurfaceReady();
            }
            AdminSecondaryLockScreenController.this.mHandler.postDelayed(new Runnable(currentUser) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    C05154.this.lambda$surfaceCreated$0$AdminSecondaryLockScreenController$4(this.f$1);
                }
            }, 500);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$surfaceCreated$0 */
        public /* synthetic */ void lambda$surfaceCreated$0$AdminSecondaryLockScreenController$4(int i) {
            AdminSecondaryLockScreenController.this.dismiss(i);
        }

        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            AdminSecondaryLockScreenController.this.mUpdateMonitor.removeCallback(AdminSecondaryLockScreenController.this.mUpdateCallback);
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback() {
        public void onSecondaryLockscreenRequirementChanged(int i) {
            if (AdminSecondaryLockScreenController.this.mUpdateMonitor.getSecondaryLockscreenRequirement(i) == null) {
                AdminSecondaryLockScreenController.this.dismiss(i);
            }
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    /* access modifiers changed from: private */
    public AdminSecurityView mView;

    private class AdminSecurityView extends SurfaceView {
        private Callback mSurfaceHolderCallback;

        AdminSecurityView(AdminSecondaryLockScreenController adminSecondaryLockScreenController, Context context, Callback callback) {
            super(context);
            this.mSurfaceHolderCallback = callback;
            setZOrderOnTop(true);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            getHolder().addCallback(this.mSurfaceHolderCallback);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            getHolder().removeCallback(this.mSurfaceHolderCallback);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AdminSecondaryLockScreenController() {
        hide();
        Log.d("AdminSecondaryLockScreenController", "KeyguardClient service died");
    }

    public AdminSecondaryLockScreenController(Context context, ViewGroup viewGroup, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityCallback keyguardSecurityCallback, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mParent = viewGroup;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mKeyguardCallback = keyguardSecurityCallback;
        this.mView = new AdminSecurityView(this, this.mContext, this.mSurfaceHolderCallback);
    }

    public void show(Intent intent) {
        this.mContext.bindService(intent, this.mConnection, 1);
        this.mParent.addView(this.mView);
    }

    public void hide() {
        if (this.mView.isAttachedToWindow()) {
            this.mParent.removeView(this.mView);
        }
        IKeyguardClient iKeyguardClient = this.mClient;
        if (iKeyguardClient != null) {
            iKeyguardClient.asBinder().unlinkToDeath(this.mKeyguardClientDeathRecipient, 0);
            this.mContext.unbindService(this.mConnection);
            this.mClient = null;
        }
    }

    /* access modifiers changed from: private */
    public void onSurfaceReady() {
        try {
            this.mClient.onSurfaceReady(this.mView.getHostToken(), this.mCallback);
        } catch (RemoteException e) {
            Log.e("AdminSecondaryLockScreenController", "Error in onSurfaceReady", e);
            dismiss(KeyguardUpdateMonitor.getCurrentUser());
        }
    }

    /* access modifiers changed from: private */
    public void dismiss(int i) {
        this.mHandler.removeCallbacksAndMessages(null);
        AdminSecurityView adminSecurityView = this.mView;
        if (adminSecurityView != null && adminSecurityView.isAttachedToWindow() && i == KeyguardUpdateMonitor.getCurrentUser()) {
            hide();
            this.mKeyguardCallback.dismiss(true, i);
        }
    }
}
