package com.google.android.systemui.columbus;

import com.android.internal.logging.MetricsLogger;
import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.actions.DismissTimer;
import com.google.android.systemui.columbus.actions.LaunchCamera;
import com.google.android.systemui.columbus.actions.LaunchOpa;
import com.google.android.systemui.columbus.actions.LaunchOverview;
import com.google.android.systemui.columbus.actions.ManageMedia;
import com.google.android.systemui.columbus.actions.SettingsAction;
import com.google.android.systemui.columbus.actions.SetupWizardAction;
import com.google.android.systemui.columbus.actions.SilenceCall;
import com.google.android.systemui.columbus.actions.SnoozeAlarm;
import com.google.android.systemui.columbus.actions.TakeScreenshot;
import com.google.android.systemui.columbus.actions.UnpinNotifications;
import com.google.android.systemui.columbus.actions.UserSelectedAction;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.feedback.HapticClick;
import com.google.android.systemui.columbus.feedback.NavUndimEffect;
import com.google.android.systemui.columbus.feedback.UserActivity;
import com.google.android.systemui.columbus.gates.CameraVisibility;
import com.google.android.systemui.columbus.gates.ChargingState;
import com.google.android.systemui.columbus.gates.FlagEnabled;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;
import com.google.android.systemui.columbus.gates.KeyguardProximity;
import com.google.android.systemui.columbus.gates.NavigationBarVisibility;
import com.google.android.systemui.columbus.gates.PowerSaveState;
import com.google.android.systemui.columbus.gates.SetupWizard;
import com.google.android.systemui.columbus.gates.SystemKeyPress;
import com.google.android.systemui.columbus.gates.TelephonyActivity;
import com.google.android.systemui.columbus.gates.UsbState;
import com.google.android.systemui.columbus.gates.VrMode;
import com.google.android.systemui.columbus.gates.WakeMode;
import com.google.android.systemui.columbus.sensors.config.Adjustment;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SpreadBuilder;

/* compiled from: ColumbusModule.kt */
public abstract class ColumbusModule {
    public static final Companion Companion = new Companion(null);

    /* compiled from: ColumbusModule.kt */
    public static final class Companion {
        public final long provideTransientGateDuration() {
            return 500;
        }

        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final Set<Gate> provideColumbusGates(FlagEnabled flagEnabled, WakeMode wakeMode, ChargingState chargingState, UsbState usbState, KeyguardProximity keyguardProximity, SetupWizard setupWizard, NavigationBarVisibility navigationBarVisibility, SystemKeyPress systemKeyPress, TelephonyActivity telephonyActivity, VrMode vrMode, KeyguardDeferredSetup keyguardDeferredSetup, CameraVisibility cameraVisibility, PowerSaveState powerSaveState) {
            Intrinsics.checkParameterIsNotNull(flagEnabled, "flagEnabled");
            Intrinsics.checkParameterIsNotNull(wakeMode, "wakeMode");
            Intrinsics.checkParameterIsNotNull(chargingState, "chargingState");
            Intrinsics.checkParameterIsNotNull(usbState, "usbState");
            Intrinsics.checkParameterIsNotNull(keyguardProximity, "keyguardProximity");
            Intrinsics.checkParameterIsNotNull(setupWizard, "setupWizard");
            Intrinsics.checkParameterIsNotNull(navigationBarVisibility, "navigationBarVisibility");
            Intrinsics.checkParameterIsNotNull(systemKeyPress, "systemKeyPress");
            Intrinsics.checkParameterIsNotNull(telephonyActivity, "telephonyActivity");
            Intrinsics.checkParameterIsNotNull(vrMode, "vrMode");
            Intrinsics.checkParameterIsNotNull(keyguardDeferredSetup, "keyguardDeferredSetup");
            Intrinsics.checkParameterIsNotNull(cameraVisibility, "cameraVisibility");
            Intrinsics.checkParameterIsNotNull(powerSaveState, "powerSaveState");
            return SetsKt__SetsKt.setOf(flagEnabled, wakeMode, chargingState, usbState, keyguardProximity, setupWizard, navigationBarVisibility, systemKeyPress, telephonyActivity, vrMode, keyguardDeferredSetup, cameraVisibility, powerSaveState);
        }

        public final List<Action> provideColumbusActions(List<Action> list, UnpinNotifications unpinNotifications, SetupWizardAction setupWizardAction, UserSelectedAction userSelectedAction) {
            Intrinsics.checkParameterIsNotNull(list, "fullscreenActions");
            Intrinsics.checkParameterIsNotNull(unpinNotifications, "unpinNotifications");
            Intrinsics.checkParameterIsNotNull(setupWizardAction, "setupWizardAction");
            Intrinsics.checkParameterIsNotNull(userSelectedAction, "userSelectedAction");
            SpreadBuilder spreadBuilder = new SpreadBuilder(4);
            Object[] array = list.toArray(new Action[0]);
            if (array != null) {
                spreadBuilder.addSpread(array);
                spreadBuilder.add(unpinNotifications);
                spreadBuilder.add(setupWizardAction);
                spreadBuilder.add(userSelectedAction);
                return CollectionsKt__CollectionsKt.listOf((Action[]) spreadBuilder.toArray(new Action[spreadBuilder.size()]));
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
        }

        public final List<Action> provideFullscreenActions(DismissTimer dismissTimer, SnoozeAlarm snoozeAlarm, SilenceCall silenceCall, SettingsAction settingsAction) {
            Intrinsics.checkParameterIsNotNull(dismissTimer, "dismissTimer");
            Intrinsics.checkParameterIsNotNull(snoozeAlarm, "snoozeAlarm");
            Intrinsics.checkParameterIsNotNull(silenceCall, "silenceCall");
            Intrinsics.checkParameterIsNotNull(settingsAction, "settingsAction");
            return CollectionsKt__CollectionsKt.listOf(dismissTimer, snoozeAlarm, silenceCall, settingsAction);
        }

        public final Set<FeedbackEffect> provideColumbusEffects(HapticClick hapticClick, NavUndimEffect navUndimEffect, UserActivity userActivity) {
            Intrinsics.checkParameterIsNotNull(hapticClick, "hapticClick");
            Intrinsics.checkParameterIsNotNull(navUndimEffect, "navUndimEffect");
            Intrinsics.checkParameterIsNotNull(userActivity, "userActivity");
            return SetsKt__SetsKt.setOf(hapticClick, navUndimEffect, userActivity);
        }

        public final Set<Adjustment> provideGestureAdjustments() {
            return SetsKt__SetsKt.emptySet();
        }

        public final Set<Integer> provideBlockingSystemKeys() {
            return SetsKt__SetsKt.setOf(Integer.valueOf(24), Integer.valueOf(25), Integer.valueOf(26));
        }

        public final MetricsLogger provideColumbusLogger() {
            return new MetricsLogger();
        }

        public final Map<String, Action> provideUserSelectedActions(LaunchOpa launchOpa, LaunchCamera launchCamera, ManageMedia manageMedia, TakeScreenshot takeScreenshot, LaunchOverview launchOverview) {
            Intrinsics.checkParameterIsNotNull(launchOpa, "launchOpa");
            Intrinsics.checkParameterIsNotNull(launchCamera, "launchCamera");
            Intrinsics.checkParameterIsNotNull(manageMedia, "manageMedia");
            Intrinsics.checkParameterIsNotNull(takeScreenshot, "takeScreenshot");
            Intrinsics.checkParameterIsNotNull(launchOverview, "launchOverview");
            return MapsKt__MapsKt.mapOf(new Pair("assistant", launchOpa), new Pair("camera", launchCamera), new Pair("media", manageMedia), new Pair("screenshot", takeScreenshot), new Pair("overview", launchOverview));
        }
    }

    public static final Set<Integer> provideBlockingSystemKeys() {
        return Companion.provideBlockingSystemKeys();
    }

    public static final List<Action> provideColumbusActions(List<Action> list, UnpinNotifications unpinNotifications, SetupWizardAction setupWizardAction, UserSelectedAction userSelectedAction) {
        return Companion.provideColumbusActions(list, unpinNotifications, setupWizardAction, userSelectedAction);
    }

    public static final Set<FeedbackEffect> provideColumbusEffects(HapticClick hapticClick, NavUndimEffect navUndimEffect, UserActivity userActivity) {
        return Companion.provideColumbusEffects(hapticClick, navUndimEffect, userActivity);
    }

    public static final Set<Gate> provideColumbusGates(FlagEnabled flagEnabled, WakeMode wakeMode, ChargingState chargingState, UsbState usbState, KeyguardProximity keyguardProximity, SetupWizard setupWizard, NavigationBarVisibility navigationBarVisibility, SystemKeyPress systemKeyPress, TelephonyActivity telephonyActivity, VrMode vrMode, KeyguardDeferredSetup keyguardDeferredSetup, CameraVisibility cameraVisibility, PowerSaveState powerSaveState) {
        return Companion.provideColumbusGates(flagEnabled, wakeMode, chargingState, usbState, keyguardProximity, setupWizard, navigationBarVisibility, systemKeyPress, telephonyActivity, vrMode, keyguardDeferredSetup, cameraVisibility, powerSaveState);
    }

    public static final MetricsLogger provideColumbusLogger() {
        return Companion.provideColumbusLogger();
    }

    public static final List<Action> provideFullscreenActions(DismissTimer dismissTimer, SnoozeAlarm snoozeAlarm, SilenceCall silenceCall, SettingsAction settingsAction) {
        return Companion.provideFullscreenActions(dismissTimer, snoozeAlarm, silenceCall, settingsAction);
    }

    public static final Set<Adjustment> provideGestureAdjustments() {
        return Companion.provideGestureAdjustments();
    }

    public static final long provideTransientGateDuration() {
        return Companion.provideTransientGateDuration();
    }

    public static final Map<String, Action> provideUserSelectedActions(LaunchOpa launchOpa, LaunchCamera launchCamera, ManageMedia manageMedia, TakeScreenshot takeScreenshot, LaunchOverview launchOverview) {
        return Companion.provideUserSelectedActions(launchOpa, launchCamera, manageMedia, takeScreenshot, launchOverview);
    }
}
