package com.android.keyguard;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;

public class EmergencyButton extends Button {
    private int mDownX;
    private int mDownY;
    private final EmergencyAffordanceManager mEmergencyAffordanceManager;
    private EmergencyButtonCallback mEmergencyButtonCallback;
    private final boolean mEnableEmergencyCallWhileSimLocked;
    KeyguardUpdateMonitorCallback mInfoCallback;
    private final boolean mIsVoiceCapable;
    private LockPatternUtils mLockPatternUtils;
    private boolean mLongPressWasDragged;
    private PowerManager mPowerManager;

    public interface EmergencyButtonCallback {
        void onEmergencyButtonClickedWhenInCall();
    }

    public EmergencyButton(Context context) {
        this(context, null);
    }

    public EmergencyButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            public void onSimStateChanged(int i, int i2, int i3) {
                EmergencyButton.this.updateEmergencyCallButton();
            }

            public void onPhoneStateChanged(int i) {
                EmergencyButton.this.updateEmergencyCallButton();
            }
        };
        this.mIsVoiceCapable = getTelephonyManager().isVoiceCapable();
        this.mEnableEmergencyCallWhileSimLocked = this.mContext.getResources().getBoolean(17891454);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
    }

    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager) this.mContext.getSystemService("phone");
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mInfoCallback);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).removeCallback(this.mInfoCallback);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                EmergencyButton.this.lambda$onFinishInflate$0$EmergencyButton(view);
            }
        });
        if (this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            setOnLongClickListener(new OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return EmergencyButton.this.lambda$onFinishInflate$1$EmergencyButton(view);
                }
            });
        }
        DejankUtils.whitelistIpcs((Runnable) new Runnable() {
            public final void run() {
                EmergencyButton.this.updateEmergencyCallButton();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ void lambda$onFinishInflate$0$EmergencyButton(View view) {
        takeEmergencyCallAction();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$1 */
    public /* synthetic */ boolean lambda$onFinishInflate$1$EmergencyButton(View view) {
        if (this.mLongPressWasDragged || !this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            return false;
        }
        this.mEmergencyAffordanceManager.performEmergencyCall();
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getActionMasked() == 0) {
            this.mDownX = x;
            this.mDownY = y;
            this.mLongPressWasDragged = false;
        } else {
            int abs = Math.abs(x - this.mDownX);
            int abs2 = Math.abs(y - this.mDownY);
            int scaledTouchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
            if (Math.abs(abs2) > scaledTouchSlop || Math.abs(abs) > scaledTouchSlop) {
                this.mLongPressWasDragged = true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public boolean performLongClick() {
        return super.performLongClick();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateEmergencyCallButton();
    }

    public void takeEmergencyCallAction() {
        String str = "EmergencyButton";
        MetricsLogger.action(this.mContext, 200);
        PowerManager powerManager = this.mPowerManager;
        if (powerManager != null) {
            powerManager.userActivity(SystemClock.uptimeMillis(), true);
        }
        try {
            ActivityTaskManager.getService().stopSystemLockTaskMode();
        } catch (RemoteException unused) {
            Slog.w(str, "Failed to stop app pinning");
        }
        if (isInCall()) {
            resumeCall();
            EmergencyButtonCallback emergencyButtonCallback = this.mEmergencyButtonCallback;
            if (emergencyButtonCallback != null) {
                emergencyButtonCallback.onEmergencyButtonClickedWhenInCall();
            }
        } else {
            KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
            if (keyguardUpdateMonitor != null) {
                keyguardUpdateMonitor.reportEmergencyCallAction(true);
            } else {
                Log.w(str, "KeyguardUpdateMonitor was null, launching intent anyway.");
            }
            TelecomManager telecommManager = getTelecommManager();
            if (telecommManager == null) {
                Log.wtf(str, "TelecomManager was null, cannot launch emergency dialer");
            } else {
                getContext().startActivityAsUser(telecommManager.createLaunchEmergencyDialerIntent(null).setFlags(343932928).putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 1), ActivityOptions.makeCustomAnimation(getContext(), 0, 0).toBundle(), new UserHandle(KeyguardUpdateMonitor.getCurrentUser()));
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateEmergencyCallButton() {
        boolean z = this.mIsVoiceCapable ? isInCall() ? true : ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isSimPinVoiceSecure() ? this.mEnableEmergencyCallWhileSimLocked : this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser()) : false;
        if (z) {
            setVisibility(0);
            setText(isInCall() ? 17040363 : 17040336);
            return;
        }
        setVisibility(8);
    }

    public void setCallback(EmergencyButtonCallback emergencyButtonCallback) {
        this.mEmergencyButtonCallback = emergencyButtonCallback;
    }

    private void resumeCall() {
        getTelecommManager().showInCallScreen(false);
    }

    private boolean isInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) this.mContext.getSystemService("telecom");
    }
}
