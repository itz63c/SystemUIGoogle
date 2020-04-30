package com.android.systemui.statusbar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.format.DateFormat;
import android.util.FloatProperty;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.SysuiStatusBarStateController.RankedListener;
import com.android.systemui.statusbar.policy.CallbackController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

public class StatusBarStateControllerImpl implements SysuiStatusBarStateController, CallbackController<StateListener>, Dumpable {
    private static final FloatProperty<StatusBarStateControllerImpl> SET_DARK_AMOUNT_PROPERTY = new FloatProperty<StatusBarStateControllerImpl>("mDozeAmount") {
        public void setValue(StatusBarStateControllerImpl statusBarStateControllerImpl, float f) {
            statusBarStateControllerImpl.setDozeAmountInternal(f);
        }

        public Float get(StatusBarStateControllerImpl statusBarStateControllerImpl) {
            return Float.valueOf(statusBarStateControllerImpl.mDozeAmount);
        }
    };
    private static final Comparator<RankedListener> sComparator = Comparator.comparingInt(C1111xf20ff57f.INSTANCE);
    private ValueAnimator mDarkAnimator;
    /* access modifiers changed from: private */
    public float mDozeAmount;
    private float mDozeAmountTarget;
    private Interpolator mDozeInterpolator;
    private HistoricalState[] mHistoricalRecords;
    private int mHistoryIndex;
    private boolean mIsDozing;
    private boolean mIsFullscreen;
    private boolean mIsImmersive;
    private boolean mKeyguardRequested;
    private int mLastState;
    private boolean mLeaveOpenOnKeyguardHide;
    private final ArrayList<RankedListener> mListeners = new ArrayList<>();
    private boolean mPulsing;
    private int mState;

    private static class HistoricalState {
        int mLastState;
        int mState;
        long mTimestamp;

        private HistoricalState() {
        }

        public String toString() {
            if (this.mTimestamp != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("state=");
                sb.append(this.mState);
                String str = " (";
                sb.append(str);
                sb.append(StatusBarStateControllerImpl.describe(this.mState));
                String str2 = ")";
                sb.append(str2);
                sb.append("lastState=");
                sb.append(this.mLastState);
                sb.append(str);
                sb.append(StatusBarStateControllerImpl.describe(this.mLastState));
                sb.append(str2);
                sb.append("timestamp=");
                sb.append(DateFormat.format("MM-dd HH:mm:ss", this.mTimestamp));
                return sb.toString();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Empty ");
            sb2.append(HistoricalState.class.getSimpleName());
            return sb2.toString();
        }
    }

    public StatusBarStateControllerImpl() {
        this.mHistoryIndex = 0;
        this.mHistoricalRecords = new HistoricalState[32];
        this.mIsFullscreen = false;
        this.mIsImmersive = false;
        this.mDozeInterpolator = Interpolators.FAST_OUT_SLOW_IN;
        for (int i = 0; i < 32; i++) {
            this.mHistoricalRecords[i] = new HistoricalState();
        }
    }

    public int getState() {
        return this.mState;
    }

    public boolean setState(int i) {
        if (i > 3 || i < 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid state ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        int i2 = this.mState;
        if (i == i2) {
            return false;
        }
        recordHistoricalState(i, i2);
        if (this.mState == 0 && i == 2) {
            Log.e("SbStateController", "Invalid state transition: SHADE -> SHADE_LOCKED", new Throwable());
        }
        synchronized (this.mListeners) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getClass().getSimpleName());
            sb2.append("#setState(");
            sb2.append(i);
            sb2.append(")");
            String sb3 = sb2.toString();
            DejankUtils.startDetectingBlockingIpcs(sb3);
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                ((RankedListener) it.next()).mListener.onStatePreChange(this.mState, i);
            }
            this.mLastState = this.mState;
            this.mState = i;
            Iterator it2 = new ArrayList(this.mListeners).iterator();
            while (it2.hasNext()) {
                ((RankedListener) it2.next()).mListener.onStateChanged(this.mState);
            }
            Iterator it3 = new ArrayList(this.mListeners).iterator();
            while (it3.hasNext()) {
                ((RankedListener) it3.next()).mListener.onStatePostChange();
            }
            DejankUtils.stopDetectingBlockingIpcs(sb3);
        }
        return true;
    }

    public boolean isDozing() {
        return this.mIsDozing;
    }

    public float getDozeAmount() {
        return this.mDozeAmount;
    }

    public float getInterpolatedDozeAmount() {
        return this.mDozeInterpolator.getInterpolation(this.mDozeAmount);
    }

    public boolean setIsDozing(boolean z) {
        if (this.mIsDozing == z) {
            return false;
        }
        this.mIsDozing = z;
        synchronized (this.mListeners) {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append("#setIsDozing");
            String sb2 = sb.toString();
            DejankUtils.startDetectingBlockingIpcs(sb2);
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                ((RankedListener) it.next()).mListener.onDozingChanged(z);
            }
            DejankUtils.stopDetectingBlockingIpcs(sb2);
        }
        return true;
    }

    public void setDozeAmount(float f, boolean z) {
        ValueAnimator valueAnimator = this.mDarkAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            if (!z || this.mDozeAmountTarget != f) {
                this.mDarkAnimator.cancel();
            } else {
                return;
            }
        }
        this.mDozeAmountTarget = f;
        if (z) {
            startDozeAnimation();
        } else {
            setDozeAmountInternal(f);
        }
    }

    private void startDozeAnimation() {
        Interpolator interpolator;
        float f = this.mDozeAmount;
        if (f == 0.0f || f == 1.0f) {
            if (this.mIsDozing) {
                interpolator = Interpolators.FAST_OUT_SLOW_IN;
            } else {
                interpolator = Interpolators.TOUCH_RESPONSE_REVERSE;
            }
            this.mDozeInterpolator = interpolator;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, SET_DARK_AMOUNT_PROPERTY, new float[]{this.mDozeAmountTarget});
        this.mDarkAnimator = ofFloat;
        ofFloat.setInterpolator(Interpolators.LINEAR);
        this.mDarkAnimator.setDuration(500);
        this.mDarkAnimator.start();
    }

    /* access modifiers changed from: private */
    public void setDozeAmountInternal(float f) {
        this.mDozeAmount = f;
        float interpolation = this.mDozeInterpolator.getInterpolation(f);
        synchronized (this.mListeners) {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append("#setDozeAmount");
            String sb2 = sb.toString();
            DejankUtils.startDetectingBlockingIpcs(sb2);
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                ((RankedListener) it.next()).mListener.onDozeAmountChanged(this.mDozeAmount, interpolation);
            }
            DejankUtils.stopDetectingBlockingIpcs(sb2);
        }
    }

    public boolean goingToFullShade() {
        return this.mState == 0 && this.mLeaveOpenOnKeyguardHide;
    }

    public void setLeaveOpenOnKeyguardHide(boolean z) {
        this.mLeaveOpenOnKeyguardHide = z;
    }

    public boolean leaveOpenOnKeyguardHide() {
        return this.mLeaveOpenOnKeyguardHide;
    }

    public boolean fromShadeLocked() {
        return this.mLastState == 2;
    }

    public void addCallback(StateListener stateListener) {
        synchronized (this.mListeners) {
            addListenerInternalLocked(stateListener, Integer.MAX_VALUE);
        }
    }

    @Deprecated
    public void addCallback(StateListener stateListener, int i) {
        synchronized (this.mListeners) {
            addListenerInternalLocked(stateListener, i);
        }
    }

    @GuardedBy({"mListeners"})
    private void addListenerInternalLocked(StateListener stateListener, int i) {
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            if (((RankedListener) it.next()).mListener.equals(stateListener)) {
                return;
            }
        }
        this.mListeners.add(new RankedListener(stateListener, i));
        this.mListeners.sort(sComparator);
    }

    public void removeCallback(StateListener stateListener) {
        synchronized (this.mListeners) {
            this.mListeners.removeIf(new Predicate() {
                public final boolean test(Object obj) {
                    return ((RankedListener) obj).mListener.equals(StateListener.this);
                }
            });
        }
    }

    public void setKeyguardRequested(boolean z) {
        this.mKeyguardRequested = z;
    }

    public boolean isKeyguardRequested() {
        return this.mKeyguardRequested;
    }

    public void setFullscreenState(boolean z, boolean z2) {
        if (this.mIsFullscreen != z || this.mIsImmersive != z2) {
            this.mIsFullscreen = z;
            this.mIsImmersive = z2;
            synchronized (this.mListeners) {
                Iterator it = new ArrayList(this.mListeners).iterator();
                while (it.hasNext()) {
                    ((RankedListener) it.next()).mListener.onFullscreenStateChanged(z, z2);
                }
            }
        }
    }

    public void setPulsing(boolean z) {
        if (this.mPulsing != z) {
            this.mPulsing = z;
            synchronized (this.mListeners) {
                Iterator it = new ArrayList(this.mListeners).iterator();
                while (it.hasNext()) {
                    ((RankedListener) it.next()).mListener.onPulsingChanged(z);
                }
            }
        }
    }

    public static String describe(int i) {
        return StatusBarState.toShortString(i);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("StatusBarStateController: ");
        StringBuilder sb = new StringBuilder();
        sb.append(" mState=");
        sb.append(this.mState);
        String str = " (";
        sb.append(str);
        sb.append(describe(this.mState));
        String str2 = ")";
        sb.append(str2);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" mLastState=");
        sb2.append(this.mLastState);
        sb2.append(str);
        sb2.append(describe(this.mLastState));
        sb2.append(str2);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(" mLeaveOpenOnKeyguardHide=");
        sb3.append(this.mLeaveOpenOnKeyguardHide);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(" mKeyguardRequested=");
        sb4.append(this.mKeyguardRequested);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(" mIsDozing=");
        sb5.append(this.mIsDozing);
        printWriter.println(sb5.toString());
        printWriter.println(" Historical states:");
        int i = 0;
        for (int i2 = 0; i2 < 32; i2++) {
            if (this.mHistoricalRecords[i2].mTimestamp != 0) {
                i++;
            }
        }
        for (int i3 = this.mHistoryIndex + 32; i3 >= ((this.mHistoryIndex + 32) - i) + 1; i3--) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append("  (");
            sb6.append(((this.mHistoryIndex + 32) - i3) + 1);
            sb6.append(str2);
            sb6.append(this.mHistoricalRecords[i3 & 31]);
            printWriter.println(sb6.toString());
        }
    }

    private void recordHistoricalState(int i, int i2) {
        int i3 = (this.mHistoryIndex + 1) % 32;
        this.mHistoryIndex = i3;
        HistoricalState historicalState = this.mHistoricalRecords[i3];
        historicalState.mState = i;
        historicalState.mLastState = i2;
        historicalState.mTimestamp = System.currentTimeMillis();
    }
}
