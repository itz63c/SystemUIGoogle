package com.android.systemui.doze;

import android.app.IWallpaperManager;
import android.os.RemoteException;
import android.util.Log;
import com.android.systemui.doze.DozeMachine.Part;
import com.android.systemui.doze.DozeMachine.State;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import java.io.PrintWriter;

public class DozeWallpaperState implements Part {
    private static final boolean DEBUG = Log.isLoggable("DozeWallpaperState", 3);
    private final BiometricUnlockController mBiometricUnlockController;
    private final DozeParameters mDozeParameters;
    private boolean mIsAmbientMode;
    private final IWallpaperManager mWallpaperManagerService;

    /* renamed from: com.android.systemui.doze.DozeWallpaperState$1 */
    static /* synthetic */ class C08341 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|(3:17|18|20)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
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
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_DOCKED     // Catch:{ NoSuchFieldError -> 0x0028 }
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
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_PAUSED     // Catch:{ NoSuchFieldError -> 0x003e }
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
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSE_DONE     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING_BRIGHT     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeWallpaperState.C08341.<clinit>():void");
        }
    }

    public DozeWallpaperState(IWallpaperManager iWallpaperManager, BiometricUnlockController biometricUnlockController, DozeParameters dozeParameters) {
        this.mWallpaperManagerService = iWallpaperManager;
        this.mBiometricUnlockController = biometricUnlockController;
        this.mDozeParameters = dozeParameters;
    }

    public void transitionTo(State state, State state2) {
        boolean z;
        boolean z2;
        String str = "DozeWallpaperState";
        boolean z3 = false;
        switch (C08341.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                z = true;
                break;
            default:
                z = false;
                break;
        }
        if (z) {
            z2 = this.mDozeParameters.shouldControlScreenOff();
        } else {
            boolean z4 = state == State.DOZE_PULSING && state2 == State.FINISH;
            if (((!this.mDozeParameters.getDisplayNeedsBlanking()) && !this.mBiometricUnlockController.unlockedByWakeAndUnlock()) || z4) {
                z3 = true;
            }
            z2 = z3;
        }
        if (z != this.mIsAmbientMode) {
            this.mIsAmbientMode = z;
            if (this.mWallpaperManagerService != null) {
                long j = z2 ? 500 : 0;
                try {
                    if (DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("AOD wallpaper state changed to: ");
                        sb.append(this.mIsAmbientMode);
                        sb.append(", animationDuration: ");
                        sb.append(j);
                        Log.i(str, sb.toString());
                    }
                    this.mWallpaperManagerService.setInAmbientMode(this.mIsAmbientMode, j);
                } catch (RemoteException unused) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Cannot notify state to WallpaperManagerService: ");
                    sb2.append(this.mIsAmbientMode);
                    Log.w(str, sb2.toString());
                }
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("DozeWallpaperState:");
        StringBuilder sb = new StringBuilder();
        sb.append(" isAmbientMode: ");
        sb.append(this.mIsAmbientMode);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" hasWallpaperService: ");
        sb2.append(this.mWallpaperManagerService != null);
        printWriter.println(sb2.toString());
    }
}
