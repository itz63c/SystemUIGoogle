package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Color;
import android.view.InsetsFlags;
import android.view.ViewDebug;
import com.android.internal.colorextraction.ColorExtractor.GradientColors;
import com.android.internal.view.AppearanceRegion;
import com.android.systemui.C2008R$color;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class LightBarController implements BatteryStateChangeCallback, Dumpable {
    private int mAppearance;
    private AppearanceRegion[] mAppearanceRegions = new AppearanceRegion[0];
    private final BatteryController mBatteryController;
    private BiometricUnlockController mBiometricUnlockController;
    private boolean mDirectReplying;
    private boolean mForceDarkForScrim;
    private boolean mHasLightNavigationBar;
    private boolean mNavbarColorManagedByIme;
    private LightBarTransitionsController mNavigationBarController;
    private int mNavigationBarMode;
    private boolean mNavigationLight;
    private boolean mQsCustomizing;
    private final SysuiDarkIconDispatcher mStatusBarIconController;
    private int mStatusBarMode;

    private static boolean isLight(int i, int i2, int i3) {
        return (i2 == 0 || i2 == 6) && ((i & i3) != 0);
    }

    public LightBarController(Context context, DarkIconDispatcher darkIconDispatcher, BatteryController batteryController) {
        Color.valueOf(context.getColor(C2008R$color.dark_mode_icon_color_single_tone));
        this.mStatusBarIconController = (SysuiDarkIconDispatcher) darkIconDispatcher;
        this.mBatteryController = batteryController;
        batteryController.addCallback(this);
    }

    public void setNavigationBar(LightBarTransitionsController lightBarTransitionsController) {
        this.mNavigationBarController = lightBarTransitionsController;
        updateNavigation();
    }

    public void setBiometricUnlockController(BiometricUnlockController biometricUnlockController) {
        this.mBiometricUnlockController = biometricUnlockController;
    }

    /* access modifiers changed from: 0000 */
    public void onStatusBarAppearanceChanged(AppearanceRegion[] appearanceRegionArr, boolean z, int i, boolean z2) {
        int length = appearanceRegionArr.length;
        boolean z3 = this.mAppearanceRegions.length != length;
        for (int i2 = 0; i2 < length && !z3; i2++) {
            z3 |= !appearanceRegionArr[i2].equals(this.mAppearanceRegions[i2]);
        }
        if (z3 || z) {
            this.mAppearanceRegions = appearanceRegionArr;
            onStatusBarModeChanged(i);
        }
        this.mNavbarColorManagedByIme = z2;
    }

    /* access modifiers changed from: 0000 */
    public void onStatusBarModeChanged(int i) {
        this.mStatusBarMode = i;
        updateStatus();
    }

    /* access modifiers changed from: 0000 */
    public void onNavigationBarAppearanceChanged(int i, boolean z, int i2, boolean z2) {
        if (((this.mAppearance ^ i) & 16) != 0 || z) {
            boolean z3 = this.mNavigationLight;
            boolean isLight = isLight(i, i2, 16);
            this.mHasLightNavigationBar = isLight;
            boolean z4 = isLight && ((this.mDirectReplying && this.mNavbarColorManagedByIme) || !this.mForceDarkForScrim) && !this.mQsCustomizing;
            this.mNavigationLight = z4;
            if (z4 != z3) {
                updateNavigation();
            }
        }
        this.mAppearance = i;
        this.mNavigationBarMode = i2;
        this.mNavbarColorManagedByIme = z2;
    }

    /* access modifiers changed from: 0000 */
    public void onNavigationBarModeChanged(int i) {
        this.mHasLightNavigationBar = isLight(this.mAppearance, i, 16);
    }

    private void reevaluate() {
        onStatusBarAppearanceChanged(this.mAppearanceRegions, true, this.mStatusBarMode, this.mNavbarColorManagedByIme);
        onNavigationBarAppearanceChanged(this.mAppearance, true, this.mNavigationBarMode, this.mNavbarColorManagedByIme);
    }

    public void setQsCustomizing(boolean z) {
        if (this.mQsCustomizing != z) {
            this.mQsCustomizing = z;
            reevaluate();
        }
    }

    public void setDirectReplying(boolean z) {
        if (this.mDirectReplying != z) {
            this.mDirectReplying = z;
            reevaluate();
        }
    }

    public void setScrimState(ScrimState scrimState, float f, GradientColors gradientColors) {
        boolean z = this.mForceDarkForScrim;
        boolean z2 = scrimState != ScrimState.BOUNCER && scrimState != ScrimState.BOUNCER_SCRIMMED && f >= 0.1f && !gradientColors.supportsDarkText();
        this.mForceDarkForScrim = z2;
        if (this.mHasLightNavigationBar && z2 != z) {
            reevaluate();
        }
    }

    private boolean animateChange() {
        BiometricUnlockController biometricUnlockController = this.mBiometricUnlockController;
        boolean z = false;
        if (biometricUnlockController == null) {
            return false;
        }
        int mode = biometricUnlockController.getMode();
        if (!(mode == 2 || mode == 1)) {
            z = true;
        }
        return z;
    }

    private void updateStatus() {
        int length = this.mAppearanceRegions.length;
        int i = -1;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            if (isLight(this.mAppearanceRegions[i3].getAppearance(), this.mStatusBarMode, 8)) {
                i2++;
                i = i3;
            }
        }
        if (i2 == length) {
            this.mStatusBarIconController.setIconsDarkArea(null);
            this.mStatusBarIconController.getTransitionsController().setIconsDark(true, animateChange());
        } else if (i2 == 0) {
            this.mStatusBarIconController.getTransitionsController().setIconsDark(false, animateChange());
        } else {
            this.mStatusBarIconController.setIconsDarkArea(this.mAppearanceRegions[i].getBounds());
            this.mStatusBarIconController.getTransitionsController().setIconsDark(true, animateChange());
        }
    }

    private void updateNavigation() {
        LightBarTransitionsController lightBarTransitionsController = this.mNavigationBarController;
        if (lightBarTransitionsController != null) {
            lightBarTransitionsController.setIconsDark(this.mNavigationLight, animateChange());
        }
    }

    public void onPowerSaveChanged(boolean z) {
        reevaluate();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("LightBarController: ");
        printWriter.print(" mAppearance=");
        printWriter.println(ViewDebug.flagsToString(InsetsFlags.class, "appearance", this.mAppearance));
        int length = this.mAppearanceRegions.length;
        for (int i = 0; i < length; i++) {
            boolean isLight = isLight(this.mAppearanceRegions[i].getAppearance(), this.mStatusBarMode, 8);
            printWriter.print(" stack #");
            printWriter.print(i);
            printWriter.print(": ");
            printWriter.print(this.mAppearanceRegions[i].toString());
            printWriter.print(" isLight=");
            printWriter.println(isLight);
        }
        printWriter.print(" mNavigationLight=");
        printWriter.print(this.mNavigationLight);
        printWriter.print(" mHasLightNavigationBar=");
        printWriter.println(this.mHasLightNavigationBar);
        printWriter.print(" mStatusBarMode=");
        printWriter.print(this.mStatusBarMode);
        printWriter.print(" mNavigationBarMode=");
        printWriter.println(this.mNavigationBarMode);
        printWriter.print(" mForceDarkForScrim=");
        printWriter.print(this.mForceDarkForScrim);
        printWriter.print(" mQsCustomizing=");
        printWriter.print(this.mQsCustomizing);
        printWriter.print(" mDirectReplying=");
        printWriter.println(this.mDirectReplying);
        printWriter.print(" mNavbarColorManagedByIme=");
        printWriter.println(this.mNavbarColorManagedByIme);
        printWriter.println();
        LightBarTransitionsController transitionsController = this.mStatusBarIconController.getTransitionsController();
        if (transitionsController != null) {
            printWriter.println(" StatusBarTransitionsController:");
            transitionsController.dump(fileDescriptor, printWriter, strArr);
            printWriter.println();
        }
        if (this.mNavigationBarController != null) {
            printWriter.println(" NavigationBarTransitionsController:");
            this.mNavigationBarController.dump(fileDescriptor, printWriter, strArr);
            printWriter.println();
        }
    }
}
