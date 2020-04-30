package com.android.systemui.statusbar.phone;

import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Binder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.Log;
import android.view.Display.Mode;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2012R$integer;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController.Callback;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class NotificationShadeWindowController implements Callback, Dumpable, ConfigurationListener {
    private final IActivityManager mActivityManager;
    private final ArrayList<WeakReference<StatusBarWindowCallback>> mCallbacks = Lists.newArrayList();
    private final SysuiColorExtractor mColorExtractor;
    private final Context mContext;
    private final State mCurrentState = new State();
    private final DozeParameters mDozeParameters;
    private ForcePluginOpenListener mForcePluginOpenListener;
    private boolean mHasTopUi;
    private boolean mHasTopUiChanged;
    private final KeyguardBypassController mKeyguardBypassController;
    private final Mode mKeyguardDisplayMode;
    private final boolean mKeyguardScreenRotation;
    private OtherwisedCollapsedListener mListener;
    private final long mLockScreenDisplayTimeout;
    private LayoutParams mLp;
    private final LayoutParams mLpChanged;
    private ViewGroup mNotificationShadeView;
    private float mScreenBrightnessDoze;
    private final StateListener mStateListener = new StateListener() {
        public void onStateChanged(int i) {
            NotificationShadeWindowController.this.setStatusBarState(i);
        }

        public void onDozingChanged(boolean z) {
            NotificationShadeWindowController.this.setDozing(z);
        }
    };
    private final WindowManager mWindowManager;

    public interface ForcePluginOpenListener {
        void onChange(boolean z);
    }

    public interface OtherwisedCollapsedListener {
        void setWouldOtherwiseCollapse(boolean z);
    }

    private static class State {
        boolean mBackdropShowing;
        int mBackgroundBlurRadius;
        boolean mBouncerShowing;
        boolean mBubbleExpanded;
        boolean mBubblesShowing;
        boolean mDozing;
        boolean mForceCollapsed;
        boolean mForceDozeBrightness;
        boolean mForceHasTopUi;
        boolean mForcePluginOpen;
        boolean mForceUserActivity;
        boolean mHeadsUpShowing;
        boolean mKeyguardFadingAway;
        boolean mKeyguardGoingAway;
        boolean mKeyguardNeedsInput;
        boolean mKeyguardOccluded;
        boolean mKeyguardShowing;
        boolean mNotTouchable;
        boolean mNotificationShadeFocusable;
        boolean mPanelExpanded;
        boolean mPanelVisible;
        boolean mQsExpanded;
        boolean mRemoteInputActive;
        int mScrimsVisibility;
        int mStatusBarState;
        boolean mWallpaperSupportsAmbientMode;

        private State() {
        }

        /* access modifiers changed from: private */
        public boolean isKeyguardShowingAndNotOccluded() {
            return this.mKeyguardShowing && !this.mKeyguardOccluded;
        }

        public String toString() {
            Field[] declaredFields;
            StringBuilder sb = new StringBuilder();
            sb.append("Window State {");
            String str = "\n";
            sb.append(str);
            for (Field field : State.class.getDeclaredFields()) {
                sb.append("  ");
                try {
                    sb.append(field.getName());
                    sb.append(": ");
                    sb.append(field.get(this));
                } catch (IllegalAccessException unused) {
                }
                sb.append(str);
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public NotificationShadeWindowController(Context context, WindowManager windowManager, IActivityManager iActivityManager, DozeParameters dozeParameters, StatusBarStateController statusBarStateController, ConfigurationController configurationController, KeyguardBypassController keyguardBypassController, SysuiColorExtractor sysuiColorExtractor, DumpManager dumpManager) {
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mActivityManager = iActivityManager;
        this.mKeyguardScreenRotation = shouldEnableKeyguardScreenRotation();
        this.mDozeParameters = dozeParameters;
        this.mScreenBrightnessDoze = dozeParameters.getScreenBrightnessDoze();
        this.mLpChanged = new LayoutParams();
        this.mKeyguardBypassController = keyguardBypassController;
        this.mColorExtractor = sysuiColorExtractor;
        dumpManager.registerDumpable(NotificationShadeWindowController.class.getName(), this);
        this.mLockScreenDisplayTimeout = (long) context.getResources().getInteger(C2012R$integer.config_lockScreenDisplayTimeout);
        ((SysuiStatusBarStateController) statusBarStateController).addCallback(this.mStateListener, 1);
        configurationController.addCallback(this);
        Mode[] supportedModes = context.getDisplay().getSupportedModes();
        Mode mode = context.getDisplay().getMode();
        this.mKeyguardDisplayMode = (Mode) Arrays.stream(supportedModes).filter(new Predicate(context.getResources().getInteger(C2012R$integer.config_keyguardRefreshRate), mode) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ Mode f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return NotificationShadeWindowController.lambda$new$0(this.f$0, this.f$1, (Mode) obj);
            }
        }).findFirst().orElse(null);
    }

    static /* synthetic */ boolean lambda$new$0(int i, Mode mode, Mode mode2) {
        return ((int) mode2.getRefreshRate()) == i && mode2.getPhysicalWidth() == mode.getPhysicalWidth() && mode2.getPhysicalHeight() == mode.getPhysicalHeight();
    }

    public void registerCallback(StatusBarWindowCallback statusBarWindowCallback) {
        int i = 0;
        while (i < this.mCallbacks.size()) {
            if (((WeakReference) this.mCallbacks.get(i)).get() != statusBarWindowCallback) {
                i++;
            } else {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference(statusBarWindowCallback));
    }

    private boolean shouldEnableKeyguardScreenRotation() {
        Resources resources = this.mContext.getResources();
        if (SystemProperties.getBoolean("lockscreen.rot_override", false) || resources.getBoolean(C2007R$bool.config_enableLockScreenRotation)) {
            return true;
        }
        return false;
    }

    public void attach() {
        LayoutParams layoutParams = new LayoutParams(-1, -1, 2040, -2138832824, -3);
        this.mLp = layoutParams;
        layoutParams.token = new Binder();
        LayoutParams layoutParams2 = this.mLp;
        layoutParams2.gravity = 48;
        layoutParams2.setFitInsetsTypes(0);
        LayoutParams layoutParams3 = this.mLp;
        layoutParams3.softInputMode = 16;
        layoutParams3.setTitle("NotificationShade");
        this.mLp.packageName = this.mContext.getPackageName();
        LayoutParams layoutParams4 = this.mLp;
        layoutParams4.layoutInDisplayCutoutMode = 3;
        layoutParams4.privateFlags |= 134217728;
        layoutParams4.insetsFlags.behavior = 2;
        this.mWindowManager.addView(this.mNotificationShadeView, layoutParams4);
        this.mLpChanged.copyFrom(this.mLp);
        onThemeChanged();
    }

    public void setNotificationShadeView(ViewGroup viewGroup) {
        this.mNotificationShadeView = viewGroup;
    }

    public ViewGroup getNotificationShadeView() {
        return this.mNotificationShadeView;
    }

    public void setDozeScreenBrightness(int i) {
        this.mScreenBrightnessDoze = ((float) i) / 255.0f;
    }

    private void setKeyguardDark(boolean z) {
        int systemUiVisibility = this.mNotificationShadeView.getSystemUiVisibility();
        this.mNotificationShadeView.setSystemUiVisibility(z ? systemUiVisibility | 16 | 8192 : systemUiVisibility & -17 & -8193);
    }

    private void applyKeyguardFlags(State state) {
        boolean z = true;
        boolean z2 = state.mScrimsVisibility == 2;
        if (!(state.mKeyguardShowing || (state.mDozing && this.mDozeParameters.getAlwaysOn())) || state.mBackdropShowing || z2) {
            this.mLpChanged.flags &= -1048577;
        } else {
            this.mLpChanged.flags |= 1048576;
        }
        if (state.mDozing) {
            this.mLpChanged.privateFlags |= 524288;
        } else {
            this.mLpChanged.privateFlags &= -524289;
        }
        if (this.mKeyguardDisplayMode != null) {
            if (!this.mKeyguardBypassController.getBypassEnabled() || state.mStatusBarState != 1 || state.mKeyguardFadingAway || state.mKeyguardGoingAway) {
                z = false;
            }
            if (state.mDozing || z) {
                this.mLpChanged.preferredDisplayModeId = this.mKeyguardDisplayMode.getModeId();
            } else {
                this.mLpChanged.preferredDisplayModeId = 0;
            }
            Trace.setCounter("display_mode_id", (long) this.mLpChanged.preferredDisplayModeId);
        }
    }

    private void adjustScreenOrientation(State state) {
        if (!state.isKeyguardShowingAndNotOccluded() && !state.mDozing) {
            this.mLpChanged.screenOrientation = -1;
        } else if (this.mKeyguardScreenRotation) {
            this.mLpChanged.screenOrientation = 2;
        } else {
            this.mLpChanged.screenOrientation = 5;
        }
    }

    private void applyFocusableFlag(State state) {
        boolean z = state.mNotificationShadeFocusable && state.mPanelExpanded;
        if ((state.mBouncerShowing && (state.mKeyguardOccluded || state.mKeyguardNeedsInput)) || ((NotificationRemoteInputManager.ENABLE_REMOTE_INPUT && state.mRemoteInputActive) || state.mBubbleExpanded)) {
            LayoutParams layoutParams = this.mLpChanged;
            int i = layoutParams.flags & -9;
            layoutParams.flags = i;
            layoutParams.flags = i & -131073;
        } else if (state.isKeyguardShowingAndNotOccluded() || z) {
            LayoutParams layoutParams2 = this.mLpChanged;
            int i2 = layoutParams2.flags & -9;
            layoutParams2.flags = i2;
            if (state.mKeyguardNeedsInput) {
                layoutParams2.flags = i2 & -131073;
            } else {
                layoutParams2.flags = 131072 | i2;
            }
        } else {
            LayoutParams layoutParams3 = this.mLpChanged;
            int i3 = layoutParams3.flags | 8;
            layoutParams3.flags = i3;
            layoutParams3.flags = i3 & -131073;
        }
        this.mLpChanged.softInputMode = 16;
    }

    private void applyForceShowNavigationFlag(State state) {
        if (state.mPanelExpanded || state.mBouncerShowing || (NotificationRemoteInputManager.ENABLE_REMOTE_INPUT && state.mRemoteInputActive)) {
            this.mLpChanged.privateFlags |= 8388608;
            return;
        }
        this.mLpChanged.privateFlags &= -8388609;
    }

    private void applyVisibility(State state) {
        boolean isExpanded = isExpanded(state);
        if (state.mForcePluginOpen) {
            OtherwisedCollapsedListener otherwisedCollapsedListener = this.mListener;
            if (otherwisedCollapsedListener != null) {
                otherwisedCollapsedListener.setWouldOtherwiseCollapse(isExpanded);
            }
            isExpanded = true;
        }
        if (isExpanded) {
            this.mNotificationShadeView.setVisibility(0);
        } else {
            this.mNotificationShadeView.setVisibility(4);
        }
    }

    private boolean isExpanded(State state) {
        return (!state.mForceCollapsed && (state.isKeyguardShowingAndNotOccluded() || state.mPanelVisible || state.mKeyguardFadingAway || state.mBouncerShowing || state.mHeadsUpShowing || state.mBubblesShowing || state.mScrimsVisibility != 0)) || state.mBackgroundBlurRadius > 0;
    }

    private void applyFitsSystemWindows(State state) {
        boolean z = !state.isKeyguardShowingAndNotOccluded();
        ViewGroup viewGroup = this.mNotificationShadeView;
        if (viewGroup != null && viewGroup.getFitsSystemWindows() != z) {
            this.mNotificationShadeView.setFitsSystemWindows(z);
            this.mNotificationShadeView.requestApplyInsets();
        }
    }

    private void applyUserActivityTimeout(State state) {
        long j;
        if (!state.isKeyguardShowingAndNotOccluded() || state.mStatusBarState != 1 || state.mQsExpanded) {
            this.mLpChanged.userActivityTimeout = -1;
            return;
        }
        LayoutParams layoutParams = this.mLpChanged;
        if (state.mBouncerShowing) {
            j = 10000;
        } else {
            j = this.mLockScreenDisplayTimeout;
        }
        layoutParams.userActivityTimeout = j;
    }

    private void applyInputFeatures(State state) {
        if (!state.isKeyguardShowingAndNotOccluded() || state.mStatusBarState != 1 || state.mQsExpanded || state.mForceUserActivity) {
            this.mLpChanged.inputFeatures &= -5;
            return;
        }
        this.mLpChanged.inputFeatures |= 4;
    }

    private void applyStatusBarColorSpaceAgnosticFlag(State state) {
        if (!isExpanded(state)) {
            this.mLpChanged.privateFlags |= 16777216;
            return;
        }
        this.mLpChanged.privateFlags &= -16777217;
    }

    private void apply(State state) {
        applyKeyguardFlags(state);
        applyFocusableFlag(state);
        applyForceShowNavigationFlag(state);
        adjustScreenOrientation(state);
        applyVisibility(state);
        applyUserActivityTimeout(state);
        applyInputFeatures(state);
        applyFitsSystemWindows(state);
        applyModalFlag(state);
        applyBrightness(state);
        applyHasTopUi(state);
        applyNotTouchable(state);
        applyStatusBarColorSpaceAgnosticFlag(state);
        LayoutParams layoutParams = this.mLp;
        if (!(layoutParams == null || layoutParams.copyFrom(this.mLpChanged) == 0)) {
            this.mWindowManager.updateViewLayout(this.mNotificationShadeView, this.mLp);
        }
        if (this.mHasTopUi != this.mHasTopUiChanged) {
            DejankUtils.whitelistIpcs((Runnable) new Runnable() {
                public final void run() {
                    NotificationShadeWindowController.this.lambda$apply$1$NotificationShadeWindowController();
                }
            });
        }
        notifyStateChangedCallbacks();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$apply$1 */
    public /* synthetic */ void lambda$apply$1$NotificationShadeWindowController() {
        try {
            this.mActivityManager.setHasTopUi(this.mHasTopUiChanged);
        } catch (RemoteException e) {
            Log.e("NotificationShadeWindowController", "Failed to call setHasTopUi", e);
        }
        this.mHasTopUi = this.mHasTopUiChanged;
    }

    public void notifyStateChangedCallbacks() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            StatusBarWindowCallback statusBarWindowCallback = (StatusBarWindowCallback) ((WeakReference) this.mCallbacks.get(i)).get();
            if (statusBarWindowCallback != null) {
                State state = this.mCurrentState;
                statusBarWindowCallback.onStateChanged(state.mKeyguardShowing, state.mKeyguardOccluded, state.mBouncerShowing);
            }
        }
    }

    private void applyModalFlag(State state) {
        if (state.mHeadsUpShowing) {
            this.mLpChanged.flags |= 32;
            return;
        }
        this.mLpChanged.flags &= -33;
    }

    private void applyBrightness(State state) {
        if (state.mForceDozeBrightness) {
            this.mLpChanged.screenBrightness = this.mScreenBrightnessDoze;
            return;
        }
        this.mLpChanged.screenBrightness = -1.0f;
    }

    private void applyHasTopUi(State state) {
        this.mHasTopUiChanged = state.mForceHasTopUi || isExpanded(state);
    }

    private void applyNotTouchable(State state) {
        if (state.mNotTouchable) {
            this.mLpChanged.flags |= 16;
            return;
        }
        this.mLpChanged.flags &= -17;
    }

    public void setKeyguardShowing(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardShowing = z;
        apply(state);
    }

    public void setKeyguardOccluded(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardOccluded = z;
        apply(state);
    }

    public void setKeyguardNeedsInput(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardNeedsInput = z;
        apply(state);
    }

    public void setPanelVisible(boolean z) {
        State state = this.mCurrentState;
        state.mPanelVisible = z;
        state.mNotificationShadeFocusable = z;
        apply(state);
    }

    public void setNotificationShadeFocusable(boolean z) {
        State state = this.mCurrentState;
        state.mNotificationShadeFocusable = z;
        apply(state);
    }

    public void setBouncerShowing(boolean z) {
        State state = this.mCurrentState;
        state.mBouncerShowing = z;
        apply(state);
    }

    public void setBackdropShowing(boolean z) {
        State state = this.mCurrentState;
        state.mBackdropShowing = z;
        apply(state);
    }

    public void setKeyguardFadingAway(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardFadingAway = z;
        apply(state);
    }

    public void setQsExpanded(boolean z) {
        State state = this.mCurrentState;
        state.mQsExpanded = z;
        apply(state);
    }

    public void setScrimsVisibility(int i) {
        State state = this.mCurrentState;
        state.mScrimsVisibility = i;
        apply(state);
    }

    public void setBackgroundBlurRadius(int i) {
        State state = this.mCurrentState;
        if (state.mBackgroundBlurRadius != i) {
            state.mBackgroundBlurRadius = i;
            apply(state);
        }
    }

    public void setHeadsUpShowing(boolean z) {
        State state = this.mCurrentState;
        state.mHeadsUpShowing = z;
        apply(state);
    }

    public void setWallpaperSupportsAmbientMode(boolean z) {
        State state = this.mCurrentState;
        state.mWallpaperSupportsAmbientMode = z;
        apply(state);
    }

    /* access modifiers changed from: private */
    public void setStatusBarState(int i) {
        State state = this.mCurrentState;
        state.mStatusBarState = i;
        apply(state);
    }

    public void setForceWindowCollapsed(boolean z) {
        State state = this.mCurrentState;
        state.mForceCollapsed = z;
        apply(state);
    }

    public void setPanelExpanded(boolean z) {
        State state = this.mCurrentState;
        state.mPanelExpanded = z;
        apply(state);
    }

    public void onRemoteInputActive(boolean z) {
        State state = this.mCurrentState;
        state.mRemoteInputActive = z;
        apply(state);
    }

    public void setForceDozeBrightness(boolean z) {
        State state = this.mCurrentState;
        state.mForceDozeBrightness = z;
        apply(state);
    }

    public void setDozing(boolean z) {
        State state = this.mCurrentState;
        state.mDozing = z;
        apply(state);
    }

    public void setForcePluginOpen(boolean z) {
        State state = this.mCurrentState;
        state.mForcePluginOpen = z;
        apply(state);
        ForcePluginOpenListener forcePluginOpenListener = this.mForcePluginOpenListener;
        if (forcePluginOpenListener != null) {
            forcePluginOpenListener.onChange(z);
        }
    }

    public boolean getForcePluginOpen() {
        return this.mCurrentState.mForcePluginOpen;
    }

    public void setNotTouchable(boolean z) {
        State state = this.mCurrentState;
        state.mNotTouchable = z;
        apply(state);
    }

    public void setBubblesShowing(boolean z) {
        State state = this.mCurrentState;
        state.mBubblesShowing = z;
        apply(state);
    }

    public boolean getBubblesShowing() {
        return this.mCurrentState.mBubblesShowing;
    }

    public void setBubbleExpanded(boolean z) {
        State state = this.mCurrentState;
        state.mBubbleExpanded = z;
        apply(state);
    }

    public boolean getPanelExpanded() {
        return this.mCurrentState.mPanelExpanded;
    }

    public void setStateListener(OtherwisedCollapsedListener otherwisedCollapsedListener) {
        this.mListener = otherwisedCollapsedListener;
    }

    public void setForcePluginOpenListener(ForcePluginOpenListener forcePluginOpenListener) {
        this.mForcePluginOpenListener = forcePluginOpenListener;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationShadeWindowController:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mKeyguardDisplayMode=");
        sb.append(this.mKeyguardDisplayMode);
        printWriter.println(sb.toString());
        printWriter.println(this.mCurrentState);
    }

    public boolean isShowingWallpaper() {
        return !this.mCurrentState.mBackdropShowing;
    }

    public void onThemeChanged() {
        if (this.mNotificationShadeView != null) {
            setKeyguardDark(this.mColorExtractor.getNeutralColors().supportsDarkText());
        }
    }

    public void setKeyguardGoingAway(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardGoingAway = z;
        apply(state);
    }

    public boolean getForceHasTopUi() {
        return this.mCurrentState.mForceHasTopUi;
    }

    public void setForceHasTopUi(boolean z) {
        State state = this.mCurrentState;
        state.mForceHasTopUi = z;
        apply(state);
    }
}
