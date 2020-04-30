package com.google.android.systemui.elmyra;

import android.content.Context;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.actions.DismissTimer;
import com.google.android.systemui.elmyra.actions.LaunchOpa;
import com.google.android.systemui.elmyra.actions.LaunchOpa.Builder;
import com.google.android.systemui.elmyra.actions.SettingsAction;
import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.actions.SilenceCall;
import com.google.android.systemui.elmyra.actions.SnoozeAlarm;
import com.google.android.systemui.elmyra.actions.UnpinNotifications;
import com.google.android.systemui.elmyra.feedback.AssistInvocationEffect;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.feedback.HapticClick;
import com.google.android.systemui.elmyra.feedback.NavUndimEffect;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;
import com.google.android.systemui.elmyra.feedback.UserActivity;
import com.google.android.systemui.elmyra.gates.CameraVisibility;
import com.google.android.systemui.elmyra.gates.ChargingState;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
import com.google.android.systemui.elmyra.gates.KeyguardProximity;
import com.google.android.systemui.elmyra.gates.NavigationBarVisibility;
import com.google.android.systemui.elmyra.gates.PowerSaveState;
import com.google.android.systemui.elmyra.gates.SetupWizard;
import com.google.android.systemui.elmyra.gates.SystemKeyPress;
import com.google.android.systemui.elmyra.gates.TelephonyActivity;
import com.google.android.systemui.elmyra.gates.UsbState;
import com.google.android.systemui.elmyra.gates.VrMode;
import com.google.android.systemui.elmyra.gates.WakeMode;
import com.google.android.systemui.elmyra.sensors.CHREGestureSensor;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.sensors.JNIGestureSensor;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.sensors.config.ScreenStateAdjustment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceConfigurationGoogle implements ServiceConfiguration {
    private final List<Action> mActions;
    private final List<FeedbackEffect> mFeedbackEffects;
    private final List<Gate> mGates;
    private final GestureSensor mGestureSensor;

    public ServiceConfigurationGoogle(Context context, AssistInvocationEffect assistInvocationEffect, Builder builder, SettingsAction.Builder builder2, CameraAction.Builder builder3, SetupWizardAction.Builder builder4, SquishyNavigationButtons squishyNavigationButtons, UnpinNotifications unpinNotifications) {
        builder.addFeedbackEffect(assistInvocationEffect);
        LaunchOpa build = builder.build();
        builder2.setLaunchOpa(build);
        SettingsAction build2 = builder2.build();
        List asList = Arrays.asList(new Action[]{new DismissTimer(context), new SnoozeAlarm(context), new SilenceCall(context), build2});
        builder3.addFeedbackEffect(assistInvocationEffect);
        CameraAction build3 = builder3.build();
        ArrayList arrayList = new ArrayList();
        this.mActions = arrayList;
        arrayList.addAll(asList);
        this.mActions.add(unpinNotifications);
        this.mActions.add(build3);
        List<Action> list = this.mActions;
        builder4.setSettingsAction(build2);
        builder4.setLaunchOpa(build);
        list.add(builder4.build());
        this.mActions.add(build);
        ArrayList arrayList2 = new ArrayList();
        this.mFeedbackEffects = arrayList2;
        arrayList2.add(new HapticClick(context));
        this.mFeedbackEffects.add(squishyNavigationButtons);
        this.mFeedbackEffects.add(new NavUndimEffect());
        this.mFeedbackEffects.add(new UserActivity(context));
        ArrayList arrayList3 = new ArrayList();
        this.mGates = arrayList3;
        arrayList3.add(new WakeMode(context));
        this.mGates.add(new ChargingState(context));
        this.mGates.add(new UsbState(context));
        this.mGates.add(new KeyguardProximity(context));
        this.mGates.add(new SetupWizard(context, Arrays.asList(new Action[]{build2})));
        this.mGates.add(new NavigationBarVisibility(context, asList));
        this.mGates.add(new SystemKeyPress(context));
        this.mGates.add(new TelephonyActivity(context));
        this.mGates.add(new VrMode(context));
        this.mGates.add(new KeyguardDeferredSetup(context, asList));
        this.mGates.add(new CameraVisibility(context, build3, asList));
        this.mGates.add(new PowerSaveState(context));
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(new ScreenStateAdjustment(context));
        GestureConfiguration gestureConfiguration = new GestureConfiguration(context, arrayList4);
        if (JNIGestureSensor.isAvailable(context)) {
            this.mGestureSensor = new JNIGestureSensor(context, gestureConfiguration);
        } else {
            this.mGestureSensor = new CHREGestureSensor(context, gestureConfiguration, new SnapshotConfiguration(context));
        }
    }

    public List<Action> getActions() {
        return this.mActions;
    }

    public List<FeedbackEffect> getFeedbackEffects() {
        return this.mFeedbackEffects;
    }

    public List<Gate> getGates() {
        return this.mGates;
    }

    public GestureSensor getGestureSensor() {
        return this.mGestureSensor;
    }
}
