package com.android.systemui.doze;

import android.hardware.display.AmbientDisplayConfiguration;
import android.util.Log;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.DozeMachine.Part;
import com.android.systemui.doze.DozeMachine.State;
import java.io.PrintWriter;

public class DozeDockHandler implements Part {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = DozeService.DEBUG;
    /* access modifiers changed from: private */
    public final AmbientDisplayConfiguration mConfig;
    private final DockEventListener mDockEventListener;
    /* access modifiers changed from: private */
    public final DockManager mDockManager;
    /* access modifiers changed from: private */
    public int mDockState = 0;
    /* access modifiers changed from: private */
    public final DozeMachine mMachine;

    /* renamed from: com.android.systemui.doze.DozeDockHandler$1 */
    static /* synthetic */ class C08221 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                com.android.systemui.doze.DozeMachine$State[] r0 = com.android.systemui.doze.DozeMachine.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$systemui$doze$DozeMachine$State = r0
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.INITIALIZED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.FINISH     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeDockHandler.C08221.<clinit>():void");
        }
    }

    private class DockEventListener implements com.android.systemui.dock.DockManager.DockEventListener {
        private boolean mRegistered;

        private DockEventListener() {
        }

        /* synthetic */ DockEventListener(DozeDockHandler dozeDockHandler, C08221 r2) {
            this();
        }

        public void onEvent(int i) {
            State state;
            if (DozeDockHandler.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("dock event = ");
                sb.append(i);
                Log.d("DozeDockHandler", sb.toString());
            }
            DozeDockHandler.this.mDockState = i;
            int access$200 = DozeDockHandler.this.mDockState;
            if (access$200 != 0) {
                if (access$200 == 1) {
                    state = State.DOZE_AOD_DOCKED;
                } else if (access$200 == 2) {
                    state = State.DOZE;
                } else {
                    return;
                }
            } else if (DozeDockHandler.this.mConfig.alwaysOnEnabled(-2)) {
                state = State.DOZE_AOD;
            } else {
                state = State.DOZE;
            }
            DozeDockHandler.this.mMachine.requestState(state);
        }

        /* access modifiers changed from: 0000 */
        public void register() {
            if (!this.mRegistered) {
                if (DozeDockHandler.this.mDockManager != null) {
                    DozeDockHandler.this.mDockManager.addListener(this);
                }
                this.mRegistered = true;
            }
        }

        /* access modifiers changed from: 0000 */
        public void unregister() {
            if (this.mRegistered) {
                if (DozeDockHandler.this.mDockManager != null) {
                    DozeDockHandler.this.mDockManager.removeListener(this);
                }
                this.mRegistered = false;
            }
        }
    }

    public DozeDockHandler(AmbientDisplayConfiguration ambientDisplayConfiguration, DozeMachine dozeMachine, DockManager dockManager) {
        this.mMachine = dozeMachine;
        this.mConfig = ambientDisplayConfiguration;
        this.mDockManager = dockManager;
        this.mDockEventListener = new DockEventListener(this, null);
    }

    public void transitionTo(State state, State state2) {
        int i = C08221.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()];
        if (i == 1) {
            this.mDockEventListener.register();
        } else if (i == 2) {
            this.mDockEventListener.unregister();
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("DozeDockHandler:");
        StringBuilder sb = new StringBuilder();
        sb.append(" dockState=");
        sb.append(this.mDockState);
        printWriter.println(sb.toString());
    }
}
