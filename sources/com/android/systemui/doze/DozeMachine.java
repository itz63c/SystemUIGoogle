package com.android.systemui.doze;

import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Trace;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.Assert;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DozeMachine {
    static final boolean DEBUG = DozeService.DEBUG;
    private final BatteryController mBatteryController;
    private final AmbientDisplayConfiguration mConfig;
    private DockManager mDockManager;
    private final DozeHost mDozeHost;
    private final DozeLog mDozeLog;
    private final Service mDozeService;
    private Part[] mParts;
    private int mPulseReason;
    private final ArrayList<State> mQueuedRequests = new ArrayList<>();
    private State mState = State.UNINITIALIZED;
    private final WakeLock mWakeLock;
    private boolean mWakeLockHeldForCurrentState = false;
    private final WakefulnessLifecycle mWakefulnessLifecycle;

    /* renamed from: com.android.systemui.doze.DozeMachine$1 */
    static /* synthetic */ class C08251 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(26:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|26) */
        /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0078 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0084 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.systemui.doze.DozeMachine$State[] r0 = com.android.systemui.doze.DozeMachine.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$systemui$doze$DozeMachine$State = r0
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_PAUSED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_PAUSING     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_DOCKED     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_REQUEST_PULSE     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING_BRIGHT     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.UNINITIALIZED     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.INITIALIZED     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0084 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.FINISH     // Catch:{ NoSuchFieldError -> 0x0084 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0084 }
                r2 = 11
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0084 }
            L_0x0084:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0090 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSE_DONE     // Catch:{ NoSuchFieldError -> 0x0090 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0090 }
                r2 = 12
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0090 }
            L_0x0090:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeMachine.C08251.<clinit>():void");
        }
    }

    public interface Part {
        void dump(PrintWriter printWriter) {
        }

        void transitionTo(State state, State state2);
    }

    public interface Service {

        public static class Delegate implements Service {
            private final Service mDelegate;

            public Delegate(Service service) {
                this.mDelegate = service;
            }

            public void finish() {
                this.mDelegate.finish();
            }

            public void setDozeScreenState(int i) {
                this.mDelegate.setDozeScreenState(i);
            }

            public void requestWakeUp() {
                this.mDelegate.requestWakeUp();
            }

            public void setDozeScreenBrightness(int i) {
                this.mDelegate.setDozeScreenBrightness(i);
            }
        }

        void finish();

        void requestWakeUp();

        void setDozeScreenBrightness(int i);

        void setDozeScreenState(int i);
    }

    public enum State {
        UNINITIALIZED,
        INITIALIZED,
        DOZE,
        DOZE_AOD,
        DOZE_REQUEST_PULSE,
        DOZE_PULSING,
        DOZE_PULSING_BRIGHT,
        DOZE_PULSE_DONE,
        FINISH,
        DOZE_AOD_PAUSED,
        DOZE_AOD_PAUSING,
        DOZE_AOD_DOCKED;

        /* access modifiers changed from: 0000 */
        public boolean canPulse() {
            int i = C08251.$SwitchMap$com$android$systemui$doze$DozeMachine$State[ordinal()];
            return i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
        }

        /* access modifiers changed from: 0000 */
        public boolean staysAwake() {
            int i = C08251.$SwitchMap$com$android$systemui$doze$DozeMachine$State[ordinal()];
            return i == 5 || i == 6 || i == 7 || i == 8;
        }

        /* access modifiers changed from: 0000 */
        public boolean isAlwaysOn() {
            return this == DOZE_AOD || this == DOZE_AOD_DOCKED;
        }

        /* access modifiers changed from: 0000 */
        public int screenState(DozeParameters dozeParameters) {
            int i = 2;
            switch (C08251.$SwitchMap$com$android$systemui$doze$DozeMachine$State[ordinal()]) {
                case 1:
                case 3:
                    return 1;
                case 2:
                case 4:
                    return 4;
                case 5:
                case 7:
                case 8:
                    break;
                case 6:
                case 9:
                case 10:
                    if (!dozeParameters.shouldControlScreenOff()) {
                        i = 1;
                        break;
                    }
                    break;
                default:
                    return 0;
            }
            return i;
        }
    }

    public DozeMachine(Service service, AmbientDisplayConfiguration ambientDisplayConfiguration, WakeLock wakeLock, WakefulnessLifecycle wakefulnessLifecycle, BatteryController batteryController, DozeLog dozeLog, DockManager dockManager, DozeHost dozeHost) {
        this.mDozeService = service;
        this.mConfig = ambientDisplayConfiguration;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mWakeLock = wakeLock;
        this.mBatteryController = batteryController;
        this.mDozeLog = dozeLog;
        this.mDockManager = dockManager;
        this.mDozeHost = dozeHost;
    }

    public void setParts(Part[] partArr) {
        Preconditions.checkState(this.mParts == null);
        this.mParts = partArr;
    }

    public void requestState(State state) {
        Preconditions.checkArgument(state != State.DOZE_REQUEST_PULSE);
        requestState(state, -1);
    }

    public void requestPulse(int i) {
        Preconditions.checkState(!isExecutingTransition());
        requestState(State.DOZE_REQUEST_PULSE, i);
    }

    private void requestState(State state, int i) {
        Assert.isMainThread();
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("request: current=");
            sb.append(this.mState);
            sb.append(" req=");
            sb.append(state);
            Log.i("DozeMachine", sb.toString(), new Throwable("here"));
        }
        boolean z = !isExecutingTransition();
        this.mQueuedRequests.add(state);
        if (z) {
            String str = "DozeMachine#requestState";
            this.mWakeLock.acquire(str);
            for (int i2 = 0; i2 < this.mQueuedRequests.size(); i2++) {
                transitionTo((State) this.mQueuedRequests.get(i2), i);
            }
            this.mQueuedRequests.clear();
            this.mWakeLock.release(str);
        }
    }

    public State getState() {
        Assert.isMainThread();
        if (!isExecutingTransition()) {
            return this.mState;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot get state because there were pending transitions: ");
        sb.append(this.mQueuedRequests.toString());
        throw new IllegalStateException(sb.toString());
    }

    public int getPulseReason() {
        Assert.isMainThread();
        State state = this.mState;
        boolean z = state == State.DOZE_REQUEST_PULSE || state == State.DOZE_PULSING || state == State.DOZE_PULSING_BRIGHT || state == State.DOZE_PULSE_DONE;
        StringBuilder sb = new StringBuilder();
        sb.append("must be in pulsing state, but is ");
        sb.append(this.mState);
        Preconditions.checkState(z, sb.toString());
        return this.mPulseReason;
    }

    public void wakeUp() {
        this.mDozeService.requestWakeUp();
    }

    public boolean isExecutingTransition() {
        return !this.mQueuedRequests.isEmpty();
    }

    private void transitionTo(State state, int i) {
        State transitionPolicy = transitionPolicy(state);
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("transition: old=");
            sb.append(this.mState);
            sb.append(" req=");
            sb.append(state);
            sb.append(" new=");
            sb.append(transitionPolicy);
            Log.i("DozeMachine", sb.toString());
        }
        if (transitionPolicy != this.mState) {
            validateTransition(transitionPolicy);
            State state2 = this.mState;
            this.mState = transitionPolicy;
            this.mDozeLog.traceState(transitionPolicy);
            Trace.traceCounter(4096, "doze_machine_state", transitionPolicy.ordinal());
            updatePulseReason(transitionPolicy, state2, i);
            performTransitionOnComponents(state2, transitionPolicy);
            updateWakeLockState(transitionPolicy);
            resolveIntermediateState(transitionPolicy);
        }
    }

    private void updatePulseReason(State state, State state2, int i) {
        if (state == State.DOZE_REQUEST_PULSE) {
            this.mPulseReason = i;
        } else if (state2 == State.DOZE_PULSE_DONE) {
            this.mPulseReason = -1;
        }
    }

    private void performTransitionOnComponents(State state, State state2) {
        for (Part transitionTo : this.mParts) {
            transitionTo.transitionTo(state, state2);
        }
        if (C08251.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()] == 11) {
            this.mDozeService.finish();
        }
    }

    private void validateTransition(State state) {
        try {
            int i = C08251.$SwitchMap$com$android$systemui$doze$DozeMachine$State[this.mState.ordinal()];
            boolean z = true;
            if (i == 9) {
                Preconditions.checkState(state == State.INITIALIZED);
            } else if (i == 11) {
                Preconditions.checkState(state == State.FINISH);
            }
            int i2 = C08251.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()];
            if (i2 == 7) {
                if (this.mState != State.DOZE_REQUEST_PULSE) {
                    z = false;
                }
                Preconditions.checkState(z);
            } else if (i2 == 12) {
                if (!(this.mState == State.DOZE_REQUEST_PULSE || this.mState == State.DOZE_PULSING)) {
                    if (this.mState != State.DOZE_PULSING_BRIGHT) {
                        z = false;
                    }
                }
                Preconditions.checkState(z);
            } else if (i2 == 9) {
                throw new IllegalArgumentException("can't transition to UNINITIALIZED");
            } else if (i2 == 10) {
                if (this.mState != State.UNINITIALIZED) {
                    z = false;
                }
                Preconditions.checkState(z);
            }
        } catch (RuntimeException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Illegal Transition: ");
            sb.append(this.mState);
            sb.append(" -> ");
            sb.append(state);
            throw new IllegalStateException(sb.toString(), e);
        }
    }

    private State transitionPolicy(State state) {
        State state2 = this.mState;
        State state3 = State.FINISH;
        if (state2 == state3) {
            return state3;
        }
        String str = "DozeMachine";
        if (!this.mDozeHost.isDozeSuppressed() || !state.isAlwaysOn()) {
            State state4 = this.mState;
            if ((state4 == State.DOZE_AOD_PAUSED || state4 == State.DOZE_AOD_PAUSING || state4 == State.DOZE_AOD || state4 == State.DOZE) && state == State.DOZE_PULSE_DONE) {
                StringBuilder sb = new StringBuilder();
                sb.append("Dropping pulse done because current state is already done: ");
                sb.append(this.mState);
                Log.i(str, sb.toString());
                return this.mState;
            } else if (state == State.DOZE_AOD && this.mBatteryController.isAodPowerSave()) {
                return State.DOZE;
            } else {
                if (state != State.DOZE_REQUEST_PULSE || this.mState.canPulse()) {
                    return state;
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Dropping pulse request because current state can't pulse: ");
                sb2.append(this.mState);
                Log.i(str, sb2.toString());
                return this.mState;
            }
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Doze is suppressed. Suppressing state: ");
            sb3.append(state);
            Log.i(str, sb3.toString());
            this.mDozeLog.traceDozeSuppressed(state);
            return State.DOZE;
        }
    }

    private void updateWakeLockState(State state) {
        boolean staysAwake = state.staysAwake();
        String str = "DozeMachine#heldForState";
        if (this.mWakeLockHeldForCurrentState && !staysAwake) {
            this.mWakeLock.release(str);
            this.mWakeLockHeldForCurrentState = false;
        } else if (!this.mWakeLockHeldForCurrentState && staysAwake) {
            this.mWakeLock.acquire(str);
            this.mWakeLockHeldForCurrentState = true;
        }
    }

    private void resolveIntermediateState(State state) {
        int i = C08251.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()];
        if (i == 10 || i == 12) {
            int wakefulness = this.mWakefulnessLifecycle.getWakefulness();
            State state2 = (wakefulness == 2 || wakefulness == 1) ? State.FINISH : this.mDockManager.isDocked() ? this.mDockManager.isHidden() ? State.DOZE : State.DOZE_AOD_DOCKED : this.mConfig.alwaysOnEnabled(-2) ? State.DOZE_AOD : State.DOZE;
            transitionTo(state2, -1);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print(" state=");
        printWriter.println(this.mState);
        printWriter.print(" wakeLockHeldForCurrentState=");
        printWriter.println(this.mWakeLockHeldForCurrentState);
        printWriter.print(" wakeLock=");
        printWriter.println(this.mWakeLock);
        printWriter.println("Parts:");
        for (Part dump : this.mParts) {
            dump.dump(printWriter);
        }
    }
}
