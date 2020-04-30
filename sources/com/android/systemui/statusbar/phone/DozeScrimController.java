package com.android.systemui.statusbar.phone;

import android.os.Handler;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.doze.DozeHost.PulseCallback;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.phone.ScrimController.Callback;

public class DozeScrimController implements StateListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("DozeScrimController", 3);
    private final DozeLog mDozeLog;
    /* access modifiers changed from: private */
    public final DozeParameters mDozeParameters;
    /* access modifiers changed from: private */
    public boolean mDozing;
    /* access modifiers changed from: private */
    public boolean mFullyPulsing;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private PulseCallback mPulseCallback;
    /* access modifiers changed from: private */
    public final Runnable mPulseOut = new Runnable() {
        public void run() {
            DozeScrimController.this.mFullyPulsing = false;
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOutExtended);
            if (DozeScrimController.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pulse out, mDozing=");
                sb.append(DozeScrimController.this.mDozing);
                Log.d("DozeScrimController", sb.toString());
            }
            if (DozeScrimController.this.mDozing) {
                DozeScrimController.this.pulseFinished();
            }
        }
    };
    /* access modifiers changed from: private */
    public final Runnable mPulseOutExtended = new Runnable() {
        public void run() {
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
            DozeScrimController.this.mPulseOut.run();
        }
    };
    /* access modifiers changed from: private */
    public int mPulseReason;
    private final Callback mScrimCallback = new Callback() {
        public void onDisplayBlanked() {
            if (DozeScrimController.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pulse in, mDozing=");
                sb.append(DozeScrimController.this.mDozing);
                sb.append(" mPulseReason=");
                sb.append(DozeLog.reasonToString(DozeScrimController.this.mPulseReason));
                Log.d("DozeScrimController", sb.toString());
            }
            if (DozeScrimController.this.mDozing) {
                DozeScrimController.this.pulseStarted();
            }
        }

        public void onFinished() {
            if (DozeScrimController.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pulse in finished, mDozing=");
                sb.append(DozeScrimController.this.mDozing);
                Log.d("DozeScrimController", sb.toString());
            }
            if (DozeScrimController.this.mDozing) {
                if (!(DozeScrimController.this.mPulseReason == 1 || DozeScrimController.this.mPulseReason == 6)) {
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOut, (long) DozeScrimController.this.mDozeParameters.getPulseVisibleDuration());
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOutExtended, (long) DozeScrimController.this.mDozeParameters.getPulseVisibleDurationExtended());
                }
                DozeScrimController.this.mFullyPulsing = true;
            }
        }

        public void onCancelled() {
            DozeScrimController.this.pulseFinished();
        }
    };

    public void onStateChanged(int i) {
    }

    public DozeScrimController(DozeParameters dozeParameters, DozeLog dozeLog) {
        this.mDozeParameters = dozeParameters;
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        this.mDozeLog = dozeLog;
    }

    @VisibleForTesting
    public void setDozing(boolean z) {
        if (this.mDozing != z) {
            this.mDozing = z;
            if (!z) {
                cancelPulsing();
            }
        }
    }

    public void pulse(PulseCallback pulseCallback, int i) {
        if (pulseCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        } else if (!this.mDozing || this.mPulseCallback != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pulse supressed. Dozing: ");
                sb.append(this.mDozeParameters);
                sb.append(" had callback? ");
                sb.append(this.mPulseCallback != null);
                Log.d("DozeScrimController", sb.toString());
            }
            pulseCallback.onPulseFinished();
        } else {
            this.mPulseCallback = pulseCallback;
            this.mPulseReason = i;
        }
    }

    public void pulseOutNow() {
        if (this.mPulseCallback != null && this.mFullyPulsing) {
            this.mPulseOut.run();
        }
    }

    public boolean isPulsing() {
        return this.mPulseCallback != null;
    }

    public void extendPulse() {
        this.mHandler.removeCallbacks(this.mPulseOut);
    }

    public void cancelPendingPulseTimeout() {
        this.mHandler.removeCallbacks(this.mPulseOut);
        this.mHandler.removeCallbacks(this.mPulseOutExtended);
    }

    private void cancelPulsing() {
        if (this.mPulseCallback != null) {
            if (DEBUG) {
                Log.d("DozeScrimController", "Cancel pulsing");
            }
            this.mFullyPulsing = false;
            this.mHandler.removeCallbacks(this.mPulseOut);
            this.mHandler.removeCallbacks(this.mPulseOutExtended);
            pulseFinished();
        }
    }

    /* access modifiers changed from: private */
    public void pulseStarted() {
        this.mDozeLog.tracePulseStart(this.mPulseReason);
        PulseCallback pulseCallback = this.mPulseCallback;
        if (pulseCallback != null) {
            pulseCallback.onPulseStarted();
        }
    }

    /* access modifiers changed from: private */
    public void pulseFinished() {
        this.mDozeLog.tracePulseFinish();
        PulseCallback pulseCallback = this.mPulseCallback;
        if (pulseCallback != null) {
            pulseCallback.onPulseFinished();
            this.mPulseCallback = null;
        }
    }

    public Callback getScrimCallback() {
        return this.mScrimCallback;
    }

    public void onDozingChanged(boolean z) {
        setDozing(z);
    }
}
