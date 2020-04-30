package com.android.systemui.statusbar.phone;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.C2018R$style;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.assist.AssistHandleViewController;
import com.android.systemui.model.SysUiState;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsOnboarding;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.android.systemui.statusbar.policy.DeadZone;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class NavigationBarView extends FrameLayout implements ModeChangedListener {
    private final Region mActiveRegion = new Region();
    private Rect mBackButtonBounds = new Rect();
    private KeyButtonDrawable mBackIcon;
    private final NavigationBarTransitions mBarTransitions;
    private final SparseArray<ButtonDispatcher> mButtonDispatchers = new SparseArray<>();
    private Configuration mConfiguration;
    private final ContextualButtonGroup mContextualButtonGroup;
    private int mCurrentRotation = -1;
    View mCurrentView = null;
    private final DeadZone mDeadZone;
    private boolean mDeadZoneConsuming = false;
    int mDisabledFlags = 0;
    private KeyButtonDrawable mDockedIcon;
    private final Consumer<Boolean> mDockedListener = new Consumer() {
        public final void accept(Object obj) {
            NavigationBarView.this.lambda$new$2$NavigationBarView((Boolean) obj);
        }
    };
    private boolean mDockedStackExists;
    private final EdgeBackGestureHandler mEdgeBackGestureHandler;
    private FloatingRotationButton mFloatingRotationButton;
    private Rect mHomeButtonBounds = new Rect();
    private KeyButtonDrawable mHomeDefaultIcon;
    private View mHorizontal;
    private final OnClickListener mImeSwitcherClickListener = new OnClickListener() {
        public void onClick(View view) {
            ((InputMethodManager) NavigationBarView.this.mContext.getSystemService(InputMethodManager.class)).showInputMethodPickerFromSystem(true, NavigationBarView.this.getContext().getDisplayId());
        }
    };
    private boolean mImeVisible;
    private boolean mInCarMode = false;
    private boolean mIsVertical = false;
    private boolean mLayoutTransitionsEnabled = true;
    /* access modifiers changed from: private */
    public int mNavBarMode;
    private final int mNavColorSampleMargin;
    int mNavigationIconHints = 0;
    private NavigationBarInflaterView mNavigationInflaterView;
    private final OnComputeInternalInsetsListener mOnComputeInternalInsetsListener = new OnComputeInternalInsetsListener() {
        public final void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo) {
            NavigationBarView.this.lambda$new$0$NavigationBarView(internalInsetsInfo);
        }
    };
    private OnVerticalChangedListener mOnVerticalChangedListener;
    private final OverviewProxyService mOverviewProxyService;
    private NotificationPanelViewController mPanelView;
    private final PluginManager mPluginManager;
    private final AccessibilityDelegate mQuickStepAccessibilityDelegate = new AccessibilityDelegate() {
        private AccessibilityAction mToggleOverviewAction;

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            if (this.mToggleOverviewAction == null) {
                this.mToggleOverviewAction = new AccessibilityAction(C2011R$id.action_toggle_overview, NavigationBarView.this.getContext().getString(C2017R$string.quick_step_accessibility_toggle_overview));
            }
            accessibilityNodeInfo.addAction(this.mToggleOverviewAction);
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (i != C2011R$id.action_toggle_overview) {
                return super.performAccessibilityAction(view, i, bundle);
            }
            ((Recents) Dependency.get(Recents.class)).toggleRecentApps();
            return true;
        }
    };
    private KeyButtonDrawable mRecentIcon;
    private Rect mRecentsButtonBounds = new Rect();
    private RecentsOnboarding mRecentsOnboarding;
    private final RegionSamplingHelper mRegionSamplingHelper;
    private Rect mRotationButtonBounds = new Rect();
    private RotationButtonController mRotationButtonController;
    /* access modifiers changed from: private */
    public Rect mSamplingBounds = new Rect();
    private ScreenPinningNotify mScreenPinningNotify;
    private final SysUiState mSysUiFlagContainer;
    private Configuration mTmpLastConfiguration;
    private int[] mTmpPosition = new int[2];
    private final NavTransitionListener mTransitionListener = new NavTransitionListener();
    private boolean mUseCarModeUi = false;
    private View mVertical;
    private boolean mWakeAndUnlocking;

    private class NavTransitionListener implements TransitionListener {
        private boolean mBackTransitioning;
        private long mDuration;
        private boolean mHomeAppearing;
        private TimeInterpolator mInterpolator;
        private long mStartDelay;

        private NavTransitionListener() {
        }

        public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            if (view.getId() == C2011R$id.back) {
                this.mBackTransitioning = true;
            } else if (view.getId() == C2011R$id.home && i == 2) {
                this.mHomeAppearing = true;
                this.mStartDelay = layoutTransition.getStartDelay(i);
                this.mDuration = layoutTransition.getDuration(i);
                this.mInterpolator = layoutTransition.getInterpolator(i);
            }
        }

        public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            if (view.getId() == C2011R$id.back) {
                this.mBackTransitioning = false;
            } else if (view.getId() == C2011R$id.home && i == 2) {
                this.mHomeAppearing = false;
            }
        }

        public void onBackAltCleared() {
            ButtonDispatcher backButton = NavigationBarView.this.getBackButton();
            if (!this.mBackTransitioning && backButton.getVisibility() == 0 && this.mHomeAppearing && NavigationBarView.this.getHomeButton().getAlpha() == 0.0f) {
                NavigationBarView.this.getBackButton().setAlpha(0.0f);
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(backButton, "alpha", new float[]{0.0f, 1.0f});
                ofFloat.setStartDelay(this.mStartDelay);
                ofFloat.setDuration(this.mDuration);
                ofFloat.setInterpolator(this.mInterpolator);
                ofFloat.start();
            }
        }
    }

    public interface OnVerticalChangedListener {
        void onVerticalChanged(boolean z);
    }

    private static String visibilityToString(int i) {
        return i != 4 ? i != 8 ? "VISIBLE" : "GONE" : "INVISIBLE";
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NavigationBarView(InternalInsetsInfo internalInsetsInfo) {
        if (!QuickStepContract.isGesturalMode(this.mNavBarMode) || this.mImeVisible) {
            internalInsetsInfo.setTouchableInsets(0);
            return;
        }
        internalInsetsInfo.setTouchableInsets(3);
        ButtonDispatcher imeSwitchButton = getImeSwitchButton();
        if (imeSwitchButton.getVisibility() == 0) {
            int[] iArr = new int[2];
            View currentView = imeSwitchButton.getCurrentView();
            currentView.getLocationInWindow(iArr);
            internalInsetsInfo.touchableRegion.set(iArr[0], iArr[1], iArr[0] + currentView.getWidth(), iArr[1] + currentView.getHeight());
            return;
        }
        internalInsetsInfo.touchableRegion.setEmpty();
    }

    /* JADX WARNING: type inference failed for: r2v2, types: [com.android.systemui.statusbar.phone.ContextualButton, java.lang.Object, com.android.systemui.statusbar.phone.RotationContextButton] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NavigationBarView(android.content.Context r8, android.util.AttributeSet r9) {
        /*
            r7 = this;
            r7.<init>(r8, r9)
            r9 = 0
            r7.mCurrentView = r9
            r0 = -1
            r7.mCurrentRotation = r0
            r0 = 0
            r7.mDisabledFlags = r0
            r7.mNavigationIconHints = r0
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r7.mHomeButtonBounds = r1
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r7.mBackButtonBounds = r1
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r7.mRecentsButtonBounds = r1
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r7.mRotationButtonBounds = r1
            android.graphics.Region r1 = new android.graphics.Region
            r1.<init>()
            r7.mActiveRegion = r1
            r1 = 2
            int[] r1 = new int[r1]
            r7.mTmpPosition = r1
            r7.mDeadZoneConsuming = r0
            com.android.systemui.statusbar.phone.NavigationBarView$NavTransitionListener r1 = new com.android.systemui.statusbar.phone.NavigationBarView$NavTransitionListener
            r1.<init>()
            r7.mTransitionListener = r1
            r9 = 1
            r7.mLayoutTransitionsEnabled = r9
            r7.mUseCarModeUi = r0
            r7.mInCarMode = r0
            android.util.SparseArray r9 = new android.util.SparseArray
            r9.<init>()
            r7.mButtonDispatchers = r9
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            r7.mSamplingBounds = r9
            com.android.systemui.statusbar.phone.NavigationBarView$1 r9 = new com.android.systemui.statusbar.phone.NavigationBarView$1
            r9.<init>()
            r7.mImeSwitcherClickListener = r9
            com.android.systemui.statusbar.phone.NavigationBarView$2 r9 = new com.android.systemui.statusbar.phone.NavigationBarView$2
            r9.<init>()
            r7.mQuickStepAccessibilityDelegate = r9
            com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarView$khIxhJwBd7pJnFFXnq8zupcHrv8 r9 = new com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarView$khIxhJwBd7pJnFFXnq8zupcHrv8
            r9.<init>()
            r7.mOnComputeInternalInsetsListener = r9
            com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarView$3_rm_LYAhHXvCBhrsX10ry5w8OA r9 = new com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarView$3_rm_LYAhHXvCBhrsX10ry5w8OA
            r9.<init>()
            r7.mDockedListener = r9
            r7.mIsVertical = r0
            java.lang.Class<com.android.systemui.statusbar.phone.NavigationModeController> r9 = com.android.systemui.statusbar.phone.NavigationModeController.class
            java.lang.Object r9 = com.android.systemui.Dependency.get(r9)
            com.android.systemui.statusbar.phone.NavigationModeController r9 = (com.android.systemui.statusbar.phone.NavigationModeController) r9
            int r9 = r9.addListener(r7)
            r7.mNavBarMode = r9
            boolean r9 = com.android.systemui.shared.system.QuickStepContract.isGesturalMode(r9)
            java.lang.Class<com.android.systemui.model.SysUiState> r1 = com.android.systemui.model.SysUiState.class
            java.lang.Object r1 = com.android.systemui.Dependency.get(r1)
            com.android.systemui.model.SysUiState r1 = (com.android.systemui.model.SysUiState) r1
            r7.mSysUiFlagContainer = r1
            java.lang.Class<com.android.systemui.shared.plugins.PluginManager> r1 = com.android.systemui.shared.plugins.PluginManager.class
            java.lang.Object r1 = com.android.systemui.Dependency.get(r1)
            com.android.systemui.shared.plugins.PluginManager r1 = (com.android.systemui.shared.plugins.PluginManager) r1
            r7.mPluginManager = r1
            com.android.systemui.statusbar.phone.ContextualButtonGroup r1 = new com.android.systemui.statusbar.phone.ContextualButtonGroup
            int r2 = com.android.systemui.C2011R$id.menu_container
            r1.<init>(r2)
            r7.mContextualButtonGroup = r1
            com.android.systemui.statusbar.phone.ContextualButton r1 = new com.android.systemui.statusbar.phone.ContextualButton
            int r2 = com.android.systemui.C2011R$id.ime_switcher
            int r3 = com.android.systemui.C2010R$drawable.ic_ime_switcher_default
            r1.<init>(r2, r3)
            com.android.systemui.statusbar.phone.RotationContextButton r2 = new com.android.systemui.statusbar.phone.RotationContextButton
            int r3 = com.android.systemui.C2011R$id.rotate_suggestion
            int r4 = com.android.systemui.C2010R$drawable.ic_sysbar_rotate_button
            r2.<init>(r3, r4)
            com.android.systemui.statusbar.phone.ContextualButton r3 = new com.android.systemui.statusbar.phone.ContextualButton
            int r4 = com.android.systemui.C2011R$id.accessibility_button
            int r5 = com.android.systemui.C2010R$drawable.ic_sysbar_accessibility_button
            r3.<init>(r4, r5)
            com.android.systemui.statusbar.phone.ContextualButtonGroup r4 = r7.mContextualButtonGroup
            r4.addButton(r1)
            if (r9 != 0) goto L_0x00c8
            com.android.systemui.statusbar.phone.ContextualButtonGroup r4 = r7.mContextualButtonGroup
            r4.addButton(r2)
        L_0x00c8:
            com.android.systemui.statusbar.phone.ContextualButtonGroup r4 = r7.mContextualButtonGroup
            r4.addButton(r3)
            java.lang.Class<com.android.systemui.recents.OverviewProxyService> r4 = com.android.systemui.recents.OverviewProxyService.class
            java.lang.Object r4 = com.android.systemui.Dependency.get(r4)
            com.android.systemui.recents.OverviewProxyService r4 = (com.android.systemui.recents.OverviewProxyService) r4
            r7.mOverviewProxyService = r4
            com.android.systemui.recents.RecentsOnboarding r5 = new com.android.systemui.recents.RecentsOnboarding
            r5.<init>(r8, r4)
            r7.mRecentsOnboarding = r5
            com.android.systemui.statusbar.phone.FloatingRotationButton r4 = new com.android.systemui.statusbar.phone.FloatingRotationButton
            r4.<init>(r8)
            r7.mFloatingRotationButton = r4
            com.android.systemui.statusbar.phone.RotationButtonController r5 = new com.android.systemui.statusbar.phone.RotationButtonController
            int r6 = com.android.systemui.C2018R$style.RotateButtonCCWStart90
            if (r9 == 0) goto L_0x00ec
            goto L_0x00ed
        L_0x00ec:
            r4 = r2
        L_0x00ed:
            r5.<init>(r8, r6, r4)
            r7.mRotationButtonController = r5
            com.android.systemui.statusbar.phone.ContextualButton r9 = new com.android.systemui.statusbar.phone.ContextualButton
            int r4 = com.android.systemui.C2011R$id.back
            r9.<init>(r4, r0)
            android.content.res.Configuration r0 = new android.content.res.Configuration
            r0.<init>()
            r7.mConfiguration = r0
            android.content.res.Configuration r0 = new android.content.res.Configuration
            r0.<init>()
            r7.mTmpLastConfiguration = r0
            android.content.res.Configuration r0 = r7.mConfiguration
            android.content.res.Resources r4 = r8.getResources()
            android.content.res.Configuration r4 = r4.getConfiguration()
            r0.updateFrom(r4)
            com.android.systemui.statusbar.phone.ScreenPinningNotify r0 = new com.android.systemui.statusbar.phone.ScreenPinningNotify
            android.content.Context r4 = r7.mContext
            r0.<init>(r4)
            r7.mScreenPinningNotify = r0
            com.android.systemui.statusbar.phone.NavigationBarTransitions r0 = new com.android.systemui.statusbar.phone.NavigationBarTransitions
            java.lang.Class<com.android.systemui.statusbar.CommandQueue> r4 = com.android.systemui.statusbar.CommandQueue.class
            java.lang.Object r4 = com.android.systemui.Dependency.get(r4)
            com.android.systemui.statusbar.CommandQueue r4 = (com.android.systemui.statusbar.CommandQueue) r4
            r0.<init>(r7, r4)
            r7.mBarTransitions = r0
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r0 = r7.mButtonDispatchers
            int r4 = com.android.systemui.C2011R$id.back
            r0.put(r4, r9)
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r9 = r7.mButtonDispatchers
            int r0 = com.android.systemui.C2011R$id.home
            com.android.systemui.statusbar.phone.ButtonDispatcher r4 = new com.android.systemui.statusbar.phone.ButtonDispatcher
            r4.<init>(r0)
            r9.put(r0, r4)
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r9 = r7.mButtonDispatchers
            int r0 = com.android.systemui.C2011R$id.home_handle
            com.android.systemui.statusbar.phone.ButtonDispatcher r4 = new com.android.systemui.statusbar.phone.ButtonDispatcher
            r4.<init>(r0)
            r9.put(r0, r4)
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r9 = r7.mButtonDispatchers
            int r0 = com.android.systemui.C2011R$id.recent_apps
            com.android.systemui.statusbar.phone.ButtonDispatcher r4 = new com.android.systemui.statusbar.phone.ButtonDispatcher
            r4.<init>(r0)
            r9.put(r0, r4)
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r9 = r7.mButtonDispatchers
            int r0 = com.android.systemui.C2011R$id.ime_switcher
            r9.put(r0, r1)
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r9 = r7.mButtonDispatchers
            int r0 = com.android.systemui.C2011R$id.accessibility_button
            r9.put(r0, r3)
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r9 = r7.mButtonDispatchers
            int r0 = com.android.systemui.C2011R$id.rotate_suggestion
            r9.put(r0, r2)
            android.util.SparseArray<com.android.systemui.statusbar.phone.ButtonDispatcher> r9 = r7.mButtonDispatchers
            int r0 = com.android.systemui.C2011R$id.menu_container
            com.android.systemui.statusbar.phone.ContextualButtonGroup r1 = r7.mContextualButtonGroup
            r9.put(r0, r1)
            com.android.systemui.statusbar.policy.DeadZone r9 = new com.android.systemui.statusbar.policy.DeadZone
            r9.<init>(r7)
            r7.mDeadZone = r9
            android.content.res.Resources r9 = r7.getResources()
            int r0 = com.android.systemui.C2009R$dimen.navigation_handle_sample_horizontal_margin
            int r9 = r9.getDimensionPixelSize(r0)
            r7.mNavColorSampleMargin = r9
            com.android.systemui.statusbar.phone.EdgeBackGestureHandler r9 = new com.android.systemui.statusbar.phone.EdgeBackGestureHandler
            com.android.systemui.recents.OverviewProxyService r0 = r7.mOverviewProxyService
            com.android.systemui.model.SysUiState r1 = r7.mSysUiFlagContainer
            com.android.systemui.shared.plugins.PluginManager r2 = r7.mPluginManager
            r9.<init>(r8, r0, r1, r2)
            r7.mEdgeBackGestureHandler = r9
            com.android.systemui.statusbar.phone.RegionSamplingHelper r8 = new com.android.systemui.statusbar.phone.RegionSamplingHelper
            com.android.systemui.statusbar.phone.NavigationBarView$3 r9 = new com.android.systemui.statusbar.phone.NavigationBarView$3
            r9.<init>()
            r8.<init>(r7, r9)
            r7.mRegionSamplingHelper = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NavigationBarView.<init>(android.content.Context, android.util.AttributeSet):void");
    }

    public NavigationBarTransitions getBarTransitions() {
        return this.mBarTransitions;
    }

    public LightBarTransitionsController getLightTransitionsController() {
        return this.mBarTransitions.getLightTransitionsController();
    }

    public void setComponents(NotificationPanelViewController notificationPanelViewController) {
        this.mPanelView = notificationPanelViewController;
        updatePanelSystemUiStateFlags();
    }

    public void setOnVerticalChangedListener(OnVerticalChangedListener onVerticalChangedListener) {
        this.mOnVerticalChangedListener = onVerticalChangedListener;
        notifyVerticalChangedListener(this.mIsVertical);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return shouldDeadZoneConsumeTouchEvents(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        shouldDeadZoneConsumeTouchEvents(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: 0000 */
    public void onTransientStateChanged(boolean z) {
        this.mEdgeBackGestureHandler.onNavBarTransientStateChanged(z);
    }

    /* access modifiers changed from: 0000 */
    public void onBarTransition(int i) {
        if (i == 4) {
            this.mRegionSamplingHelper.stop();
            getLightTransitionsController().setIconsDark(false, true);
            return;
        }
        this.mRegionSamplingHelper.start(this.mSamplingBounds);
    }

    private boolean shouldDeadZoneConsumeTouchEvents(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mDeadZoneConsuming = false;
        }
        if (!this.mDeadZone.onTouchEvent(motionEvent) && !this.mDeadZoneConsuming) {
            return false;
        }
        if (actionMasked == 0) {
            setSlippery(true);
            this.mDeadZoneConsuming = true;
        } else if (actionMasked == 1 || actionMasked == 3) {
            updateSlippery();
            this.mDeadZoneConsuming = false;
        }
        return true;
    }

    public void abortCurrentGesture() {
        getHomeButton().abortCurrentGesture();
    }

    public View getCurrentView() {
        return this.mCurrentView;
    }

    public RotationButtonController getRotationButtonController() {
        return this.mRotationButtonController;
    }

    public ButtonDispatcher getRecentsButton() {
        return (ButtonDispatcher) this.mButtonDispatchers.get(C2011R$id.recent_apps);
    }

    public ButtonDispatcher getBackButton() {
        return (ButtonDispatcher) this.mButtonDispatchers.get(C2011R$id.back);
    }

    public ButtonDispatcher getHomeButton() {
        return (ButtonDispatcher) this.mButtonDispatchers.get(C2011R$id.home);
    }

    public ButtonDispatcher getImeSwitchButton() {
        return (ButtonDispatcher) this.mButtonDispatchers.get(C2011R$id.ime_switcher);
    }

    public ButtonDispatcher getAccessibilityButton() {
        return (ButtonDispatcher) this.mButtonDispatchers.get(C2011R$id.accessibility_button);
    }

    public RotationContextButton getRotateSuggestionButton() {
        return (RotationContextButton) this.mButtonDispatchers.get(C2011R$id.rotate_suggestion);
    }

    public ButtonDispatcher getHomeHandle() {
        return (ButtonDispatcher) this.mButtonDispatchers.get(C2011R$id.home_handle);
    }

    public SparseArray<ButtonDispatcher> getButtonDispatchers() {
        return this.mButtonDispatchers;
    }

    public boolean isRecentsButtonVisible() {
        return getRecentsButton().getVisibility() == 0;
    }

    public boolean isOverviewEnabled() {
        return (this.mDisabledFlags & 16777216) == 0;
    }

    public boolean isQuickStepSwipeUpEnabled() {
        return this.mOverviewProxyService.shouldShowSwipeUpUI() && isOverviewEnabled();
    }

    private void reloadNavIcons() {
        updateIcons(Configuration.EMPTY);
    }

    private void updateIcons(Configuration configuration) {
        boolean z = true;
        boolean z2 = configuration.orientation != this.mConfiguration.orientation;
        boolean z3 = configuration.densityDpi != this.mConfiguration.densityDpi;
        if (configuration.getLayoutDirection() == this.mConfiguration.getLayoutDirection()) {
            z = false;
        }
        if (z2 || z3) {
            this.mDockedIcon = getDrawable(C2010R$drawable.ic_sysbar_docked);
            this.mHomeDefaultIcon = getHomeDrawable();
        }
        if (z3 || z) {
            this.mRecentIcon = getDrawable(C2010R$drawable.ic_sysbar_recent);
            this.mContextualButtonGroup.updateIcons();
        }
        if (z2 || z3 || z) {
            this.mBackIcon = getBackDrawable();
        }
    }

    public KeyButtonDrawable getBackDrawable() {
        KeyButtonDrawable drawable = getDrawable(getBackDrawableRes());
        orientBackButton(drawable);
        return drawable;
    }

    public int getBackDrawableRes() {
        return chooseNavigationIconDrawableRes(C2010R$drawable.ic_sysbar_back, C2010R$drawable.ic_sysbar_back_quick_step);
    }

    public KeyButtonDrawable getHomeDrawable() {
        KeyButtonDrawable keyButtonDrawable;
        if (this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            keyButtonDrawable = getDrawable(C2010R$drawable.ic_sysbar_home_quick_step);
        } else {
            keyButtonDrawable = getDrawable(C2010R$drawable.ic_sysbar_home);
        }
        orientHomeButton(keyButtonDrawable);
        return keyButtonDrawable;
    }

    private void orientBackButton(KeyButtonDrawable keyButtonDrawable) {
        float f;
        boolean z = (this.mNavigationIconHints & 1) != 0;
        boolean z2 = this.mConfiguration.getLayoutDirection() == 1;
        float f2 = 0.0f;
        if (z) {
            f = (float) (z2 ? 90 : -90);
        } else {
            f = 0.0f;
        }
        if (keyButtonDrawable.getRotation() != f) {
            if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
                keyButtonDrawable.setRotation(f);
                return;
            }
            if (!this.mOverviewProxyService.shouldShowSwipeUpUI() && !this.mIsVertical && z) {
                f2 = -getResources().getDimension(C2009R$dimen.navbar_back_button_ime_offset);
            }
            ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(keyButtonDrawable, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_ROTATE, new float[]{f}), PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_TRANSLATE_Y, new float[]{f2})});
            ofPropertyValuesHolder.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            ofPropertyValuesHolder.setDuration(200);
            ofPropertyValuesHolder.start();
        }
    }

    private void orientHomeButton(KeyButtonDrawable keyButtonDrawable) {
        keyButtonDrawable.setRotation(this.mIsVertical ? 90.0f : 0.0f);
    }

    private int chooseNavigationIconDrawableRes(int i, int i2) {
        return this.mOverviewProxyService.shouldShowSwipeUpUI() ? i2 : i;
    }

    private KeyButtonDrawable getDrawable(int i) {
        return KeyButtonDrawable.create(this.mContext, i, true);
    }

    public void onScreenStateChanged(boolean z) {
        if (!z) {
            this.mRegionSamplingHelper.stop();
        } else if (Utils.isGesturalModeOnDefaultDisplay(getContext(), this.mNavBarMode)) {
            this.mRegionSamplingHelper.start(this.mSamplingBounds);
        }
    }

    public void setWindowVisible(boolean z) {
        this.mRegionSamplingHelper.setWindowVisible(z);
        this.mRotationButtonController.onNavigationBarWindowVisibilityChange(z);
    }

    public void setLayoutDirection(int i) {
        reloadNavIcons();
        super.setLayoutDirection(i);
    }

    public void setNavigationIconHints(int i) {
        if (i != this.mNavigationIconHints) {
            boolean z = false;
            boolean z2 = (i & 1) != 0;
            if ((this.mNavigationIconHints & 1) != 0) {
                z = true;
            }
            if (z2 != z) {
                onImeVisibilityChanged(z2);
            }
            this.mNavigationIconHints = i;
            updateNavButtonIcons();
        }
    }

    private void onImeVisibilityChanged(boolean z) {
        if (!z) {
            this.mTransitionListener.onBackAltCleared();
        }
        this.mImeVisible = z;
        this.mRotationButtonController.getRotationButton().setCanShowRotationButton(!this.mImeVisible);
    }

    public void setDisabledFlags(int i) {
        if (this.mDisabledFlags != i) {
            boolean isOverviewEnabled = isOverviewEnabled();
            this.mDisabledFlags = i;
            if (!isOverviewEnabled && isOverviewEnabled()) {
                reloadNavIcons();
            }
            updateNavButtonIcons();
            updateSlippery();
            setUpSwipeUpOnboarding(isQuickStepSwipeUpEnabled());
            updateDisabledSystemUiStateFlags();
        }
    }

    public void updateNavButtonIcons() {
        int i = 0;
        boolean z = (this.mNavigationIconHints & 1) != 0;
        KeyButtonDrawable keyButtonDrawable = this.mBackIcon;
        orientBackButton(keyButtonDrawable);
        KeyButtonDrawable keyButtonDrawable2 = this.mHomeDefaultIcon;
        if (!this.mUseCarModeUi) {
            orientHomeButton(keyButtonDrawable2);
        }
        getHomeButton().setImageDrawable(keyButtonDrawable2);
        getBackButton().setImageDrawable(keyButtonDrawable);
        updateRecentsIcon();
        this.mContextualButtonGroup.setButtonVisibility(C2011R$id.ime_switcher, (this.mNavigationIconHints & 2) != 0);
        this.mBarTransitions.reapplyDarkIntensity();
        boolean z2 = QuickStepContract.isGesturalMode(this.mNavBarMode) || (this.mDisabledFlags & 2097152) != 0;
        boolean isRecentsButtonDisabled = isRecentsButtonDisabled();
        boolean z3 = isRecentsButtonDisabled && (2097152 & this.mDisabledFlags) != 0;
        boolean z4 = !z && (QuickStepContract.isGesturalMode(this.mNavBarMode) || (this.mDisabledFlags & 4194304) != 0);
        boolean isScreenPinningActive = ActivityManagerWrapper.getInstance().isScreenPinningActive();
        if (this.mOverviewProxyService.isEnabled()) {
            isRecentsButtonDisabled |= true ^ QuickStepContract.isLegacyMode(this.mNavBarMode);
            if (isScreenPinningActive && !QuickStepContract.isGesturalMode(this.mNavBarMode)) {
                z4 = false;
                z2 = false;
            }
        } else if (isScreenPinningActive) {
            z4 = false;
            isRecentsButtonDisabled = false;
        }
        ViewGroup viewGroup = (ViewGroup) getCurrentView().findViewById(C2011R$id.nav_buttons);
        if (viewGroup != null) {
            LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
            if (layoutTransition != null && !layoutTransition.getTransitionListeners().contains(this.mTransitionListener)) {
                layoutTransition.addTransitionListener(this.mTransitionListener);
            }
        }
        getBackButton().setVisibility(z4 ? 4 : 0);
        getHomeButton().setVisibility(z2 ? 4 : 0);
        getRecentsButton().setVisibility(isRecentsButtonDisabled ? 4 : 0);
        ButtonDispatcher homeHandle = getHomeHandle();
        if (z3) {
            i = 4;
        }
        homeHandle.setVisibility(i);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean isRecentsButtonDisabled() {
        return this.mUseCarModeUi || !isOverviewEnabled() || getContext().getDisplayId() != 0;
    }

    private Display getContextDisplay() {
        return getContext().getDisplay();
    }

    public void setLayoutTransitionsEnabled(boolean z) {
        this.mLayoutTransitionsEnabled = z;
        updateLayoutTransitionsEnabled();
    }

    public void setWakeAndUnlocking(boolean z) {
        setUseFadingAnimations(z);
        this.mWakeAndUnlocking = z;
        updateLayoutTransitionsEnabled();
    }

    private void updateLayoutTransitionsEnabled() {
        boolean z = !this.mWakeAndUnlocking && this.mLayoutTransitionsEnabled;
        LayoutTransition layoutTransition = ((ViewGroup) getCurrentView().findViewById(C2011R$id.nav_buttons)).getLayoutTransition();
        if (layoutTransition == null) {
            return;
        }
        if (z) {
            layoutTransition.enableTransitionType(2);
            layoutTransition.enableTransitionType(3);
            layoutTransition.enableTransitionType(0);
            layoutTransition.enableTransitionType(1);
            return;
        }
        layoutTransition.disableTransitionType(2);
        layoutTransition.disableTransitionType(3);
        layoutTransition.disableTransitionType(0);
        layoutTransition.disableTransitionType(1);
    }

    private void setUseFadingAnimations(boolean z) {
        LayoutParams layoutParams = (LayoutParams) ((ViewGroup) getParent()).getLayoutParams();
        if (layoutParams != null) {
            boolean z2 = layoutParams.windowAnimations != 0;
            if (!z2 && z) {
                layoutParams.windowAnimations = C2018R$style.Animation_NavigationBarFadeIn;
            } else if (z2 && !z) {
                layoutParams.windowAnimations = 0;
            } else {
                return;
            }
            ((WindowManager) getContext().getSystemService("window")).updateViewLayout((View) getParent(), layoutParams);
        }
    }

    public void onStatusBarPanelStateChanged() {
        updateSlippery();
        updatePanelSystemUiStateFlags();
    }

    public void updateDisabledSystemUiStateFlags() {
        int displayId = this.mContext.getDisplayId();
        SysUiState sysUiState = this.mSysUiFlagContainer;
        boolean z = true;
        sysUiState.setFlag(1, ActivityManagerWrapper.getInstance().isScreenPinningActive());
        sysUiState.setFlag(128, (this.mDisabledFlags & 16777216) != 0);
        sysUiState.setFlag(256, (this.mDisabledFlags & 2097152) != 0);
        if ((this.mDisabledFlags & 33554432) == 0) {
            z = false;
        }
        sysUiState.setFlag(1024, z);
        sysUiState.commitUpdate(displayId);
    }

    public void updatePanelSystemUiStateFlags() {
        int displayId = this.mContext.getDisplayId();
        StringBuilder sb = new StringBuilder();
        sb.append("Updating panel sysui state flags: panelView=");
        sb.append(this.mPanelView);
        String str = "StatusBar/NavBarView";
        Log.d(str, sb.toString());
        if (this.mPanelView != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Updating panel sysui state flags: fullyExpanded=");
            sb2.append(this.mPanelView.isFullyExpanded());
            sb2.append(" inQs=");
            sb2.append(this.mPanelView.isInSettings());
            Log.d(str, sb2.toString());
            SysUiState sysUiState = this.mSysUiFlagContainer;
            sysUiState.setFlag(4, this.mPanelView.isFullyExpanded() && !this.mPanelView.isInSettings());
            sysUiState.setFlag(2048, this.mPanelView.isInSettings());
            sysUiState.commitUpdate(displayId);
        }
    }

    public void updateStates() {
        boolean shouldShowSwipeUpUI = this.mOverviewProxyService.shouldShowSwipeUpUI();
        NavigationBarInflaterView navigationBarInflaterView = this.mNavigationInflaterView;
        if (navigationBarInflaterView != null) {
            navigationBarInflaterView.onLikelyDefaultLayoutChange();
        }
        updateSlippery();
        reloadNavIcons();
        updateNavButtonIcons();
        setUpSwipeUpOnboarding(isQuickStepSwipeUpEnabled());
        WindowManagerWrapper.getInstance().setNavBarVirtualKeyHapticFeedbackEnabled(!shouldShowSwipeUpUI);
        getHomeButton().setAccessibilityDelegate(shouldShowSwipeUpUI ? this.mQuickStepAccessibilityDelegate : null);
    }

    public void updateSlippery() {
        setSlippery(!isQuickStepSwipeUpEnabled() || (this.mPanelView.isFullyExpanded() && !this.mPanelView.isCollapsing()));
    }

    private void setSlippery(boolean z) {
        setWindowFlag(536870912, z);
    }

    private void setWindowFlag(int i, boolean z) {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup != null) {
            LayoutParams layoutParams = (LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams != null) {
                if (z != ((layoutParams.flags & i) != 0)) {
                    if (z) {
                        layoutParams.flags = i | layoutParams.flags;
                    } else {
                        layoutParams.flags = (~i) & layoutParams.flags;
                    }
                    ((WindowManager) getContext().getSystemService("window")).updateViewLayout(viewGroup, layoutParams);
                }
            }
        }
    }

    public void onNavigationModeChanged(int i) {
        Context currentUserContext = ((NavigationModeController) Dependency.get(NavigationModeController.class)).getCurrentUserContext();
        this.mNavBarMode = i;
        this.mBarTransitions.onNavigationModeChanged(i);
        this.mEdgeBackGestureHandler.onNavigationModeChanged(this.mNavBarMode, currentUserContext);
        this.mRecentsOnboarding.onNavigationModeChanged(this.mNavBarMode);
        getRotateSuggestionButton().onNavigationModeChanged(this.mNavBarMode);
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            this.mRegionSamplingHelper.start(this.mSamplingBounds);
        } else {
            this.mRegionSamplingHelper.stop();
        }
    }

    public void setAccessibilityButtonState(boolean z, boolean z2) {
        getAccessibilityButton().setLongClickable(z2);
        this.mContextualButtonGroup.setButtonVisibility(C2011R$id.accessibility_button, z);
    }

    /* access modifiers changed from: 0000 */
    public void hideRecentsOnboarding() {
        this.mRecentsOnboarding.hide(true);
    }

    public void onFinishInflate() {
        NavigationBarInflaterView navigationBarInflaterView = (NavigationBarInflaterView) findViewById(C2011R$id.navigation_inflater);
        this.mNavigationInflaterView = navigationBarInflaterView;
        navigationBarInflaterView.setButtonDispatchers(this.mButtonDispatchers);
        getImeSwitchButton().setOnClickListener(this.mImeSwitcherClickListener);
        ((Divider) Dependency.get(Divider.class)).registerInSplitScreenListener(this.mDockedListener);
        updateOrientationViews();
        reloadNavIcons();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mDeadZone.onDraw(canvas);
        super.onDraw(canvas);
    }

    /* access modifiers changed from: private */
    public void updateSamplingRect() {
        this.mSamplingBounds.setEmpty();
        View currentView = getHomeHandle().getCurrentView();
        if (currentView != null) {
            int[] iArr = new int[2];
            currentView.getLocationOnScreen(iArr);
            Point point = new Point();
            currentView.getContext().getDisplay().getRealSize(point);
            this.mSamplingBounds.set(new Rect(iArr[0] - this.mNavColorSampleMargin, point.y - getNavBarHeight(), iArr[0] + currentView.getWidth() + this.mNavColorSampleMargin, point.y));
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mActiveRegion.setEmpty();
        updateButtonLocation(getBackButton(), this.mBackButtonBounds, true);
        updateButtonLocation(getHomeButton(), this.mHomeButtonBounds, false);
        updateButtonLocation(getRecentsButton(), this.mRecentsButtonBounds, false);
        updateButtonLocation(getRotateSuggestionButton(), this.mRotationButtonBounds, true);
        this.mOverviewProxyService.onActiveNavBarRegionChanges(this.mActiveRegion);
        this.mRecentsOnboarding.setNavBarHeight(getMeasuredHeight());
    }

    private void updateButtonLocation(ButtonDispatcher buttonDispatcher, Rect rect, boolean z) {
        View currentView = buttonDispatcher.getCurrentView();
        if (currentView == null) {
            rect.setEmpty();
            return;
        }
        float translationX = currentView.getTranslationX();
        float translationY = currentView.getTranslationY();
        currentView.setTranslationX(0.0f);
        currentView.setTranslationY(0.0f);
        if (z) {
            currentView.getLocationOnScreen(this.mTmpPosition);
            int[] iArr = this.mTmpPosition;
            rect.set(iArr[0], iArr[1], iArr[0] + currentView.getMeasuredWidth(), this.mTmpPosition[1] + currentView.getMeasuredHeight());
            this.mActiveRegion.op(rect, Op.UNION);
        }
        currentView.getLocationInWindow(this.mTmpPosition);
        int[] iArr2 = this.mTmpPosition;
        rect.set(iArr2[0], iArr2[1], iArr2[0] + currentView.getMeasuredWidth(), this.mTmpPosition[1] + currentView.getMeasuredHeight());
        currentView.setTranslationX(translationX);
        currentView.setTranslationY(translationY);
    }

    private void updateOrientationViews() {
        this.mHorizontal = findViewById(C2011R$id.horizontal);
        this.mVertical = findViewById(C2011R$id.vertical);
        updateCurrentView();
    }

    /* access modifiers changed from: 0000 */
    public boolean needsReorient(int i) {
        return this.mCurrentRotation != i;
    }

    private void updateCurrentView() {
        resetViews();
        View view = this.mIsVertical ? this.mVertical : this.mHorizontal;
        this.mCurrentView = view;
        boolean z = false;
        view.setVisibility(0);
        this.mNavigationInflaterView.setVertical(this.mIsVertical);
        int rotation = getContextDisplay().getRotation();
        this.mCurrentRotation = rotation;
        NavigationBarInflaterView navigationBarInflaterView = this.mNavigationInflaterView;
        if (rotation == 1) {
            z = true;
        }
        navigationBarInflaterView.setAlternativeOrder(z);
        this.mNavigationInflaterView.updateButtonDispatchersCurrentView();
        updateLayoutTransitionsEnabled();
    }

    private void resetViews() {
        this.mHorizontal.setVisibility(8);
        this.mVertical.setVisibility(8);
    }

    private void updateRecentsIcon() {
        this.mDockedIcon.setRotation((!this.mDockedStackExists || !this.mIsVertical) ? 0.0f : 90.0f);
        getRecentsButton().setImageDrawable(this.mDockedStackExists ? this.mDockedIcon : this.mRecentIcon);
        this.mBarTransitions.reapplyDarkIntensity();
    }

    public void showPinningEnterExitToast(boolean z) {
        if (z) {
            this.mScreenPinningNotify.showPinningStartToast();
        } else {
            this.mScreenPinningNotify.showPinningExitToast();
        }
    }

    public void showPinningEscapeToast() {
        this.mScreenPinningNotify.showEscapeToast(this.mNavBarMode == 2, isRecentsButtonVisible());
    }

    public void reorient() {
        updateCurrentView();
        ((NavigationBarFrame) getRootView()).setDeadZone(this.mDeadZone);
        this.mDeadZone.onConfigurationChanged(this.mCurrentRotation);
        this.mBarTransitions.init();
        if (!isLayoutDirectionResolved()) {
            resolveLayoutDirection();
        }
        updateNavButtonIcons();
        getHomeButton().setVertical(this.mIsVertical);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        boolean z = size > 0 && size2 > size && !QuickStepContract.isGesturalMode(this.mNavBarMode);
        if (z != this.mIsVertical) {
            this.mIsVertical = z;
            reorient();
            notifyVerticalChangedListener(z);
        }
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            if (this.mIsVertical) {
                i3 = getResources().getDimensionPixelSize(17105317);
            } else {
                i3 = getResources().getDimensionPixelSize(17105315);
            }
            this.mBarTransitions.setBackgroundFrame(new Rect(0, getResources().getDimensionPixelSize(17105312) - i3, size, size2));
        }
        super.onMeasure(i, i2);
    }

    private int getNavBarHeight() {
        if (this.mIsVertical) {
            return getResources().getDimensionPixelSize(17105317);
        }
        return getResources().getDimensionPixelSize(17105315);
    }

    private void notifyVerticalChangedListener(boolean z) {
        OnVerticalChangedListener onVerticalChangedListener = this.mOnVerticalChangedListener;
        if (onVerticalChangedListener != null) {
            onVerticalChangedListener.onVerticalChanged(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mTmpLastConfiguration.updateFrom(this.mConfiguration);
        this.mConfiguration.updateFrom(configuration);
        boolean updateCarMode = updateCarMode();
        updateIcons(this.mTmpLastConfiguration);
        updateRecentsIcon();
        this.mRecentsOnboarding.onConfigurationChanged(this.mConfiguration);
        if (!updateCarMode) {
            Configuration configuration2 = this.mTmpLastConfiguration;
            if (configuration2.densityDpi == this.mConfiguration.densityDpi && configuration2.getLayoutDirection() == this.mConfiguration.getLayoutDirection()) {
                return;
            }
        }
        updateNavButtonIcons();
    }

    private boolean updateCarMode() {
        Configuration configuration = this.mConfiguration;
        if (configuration != null) {
            boolean z = (configuration.uiMode & 15) == 3;
            if (z != this.mInCarMode) {
                this.mInCarMode = z;
                this.mUseCarModeUi = false;
            }
        }
        return false;
    }

    private String getResourceName(int i) {
        if (i == 0) {
            return "(null)";
        }
        try {
            return getContext().getResources().getResourceName(i);
        } catch (NotFoundException unused) {
            return "(unknown)";
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestApplyInsets();
        reorient();
        onNavigationModeChanged(this.mNavBarMode);
        setUpSwipeUpOnboarding(isQuickStepSwipeUpEnabled());
        RotationButtonController rotationButtonController = this.mRotationButtonController;
        if (rotationButtonController != null) {
            rotationButtonController.registerListeners();
        }
        this.mEdgeBackGestureHandler.onNavBarAttached();
        getViewTreeObserver().addOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).removeListener(this);
        setUpSwipeUpOnboarding(false);
        for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
            ((ButtonDispatcher) this.mButtonDispatchers.valueAt(i)).onDestroy();
        }
        RotationButtonController rotationButtonController = this.mRotationButtonController;
        if (rotationButtonController != null) {
            rotationButtonController.unregisterListeners();
        }
        this.mEdgeBackGestureHandler.onNavBarDetached();
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
    }

    private void setUpSwipeUpOnboarding(boolean z) {
        if (z) {
            this.mRecentsOnboarding.onConnectedToLauncher();
        } else {
            this.mRecentsOnboarding.onDisconnectedFromLauncher();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NavigationBarView {");
        Rect rect = new Rect();
        Point point = new Point();
        getContextDisplay().getRealSize(point);
        StringBuilder sb = new StringBuilder();
        sb.append("      this: ");
        sb.append(StatusBar.viewInfo(this));
        String str = " ";
        sb.append(str);
        sb.append(visibilityToString(getVisibility()));
        printWriter.println(String.format(sb.toString(), new Object[0]));
        getWindowVisibleDisplayFrame(rect);
        boolean z = rect.right > point.x || rect.bottom > point.y;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("      window: ");
        sb2.append(rect.toShortString());
        sb2.append(str);
        sb2.append(visibilityToString(getWindowVisibility()));
        sb2.append(z ? " OFFSCREEN!" : "");
        printWriter.println(sb2.toString());
        printWriter.println(String.format("      mCurrentView: id=%s (%dx%d) %s %f", new Object[]{getResourceName(getCurrentView().getId()), Integer.valueOf(getCurrentView().getWidth()), Integer.valueOf(getCurrentView().getHeight()), visibilityToString(getCurrentView().getVisibility()), Float.valueOf(getCurrentView().getAlpha())}));
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(this.mDisabledFlags);
        objArr[1] = this.mIsVertical ? "true" : "false";
        objArr[2] = Float.valueOf(getLightTransitionsController().getCurrentDarkIntensity());
        printWriter.println(String.format("      disabled=0x%08x vertical=%s darkIntensity=%.2f", objArr));
        dumpButton(printWriter, "back", getBackButton());
        dumpButton(printWriter, "home", getHomeButton());
        dumpButton(printWriter, "rcnt", getRecentsButton());
        dumpButton(printWriter, "rota", getRotateSuggestionButton());
        dumpButton(printWriter, "a11y", getAccessibilityButton());
        printWriter.println("    }");
        this.mContextualButtonGroup.dump(printWriter);
        this.mRecentsOnboarding.dump(printWriter);
        this.mRegionSamplingHelper.dump(printWriter);
        this.mEdgeBackGestureHandler.dump(printWriter);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        AssistHandleViewController assistHandleViewController;
        int systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
        int systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
        setPadding(systemWindowInsetLeft, windowInsets.getSystemWindowInsetTop(), systemWindowInsetRight, windowInsets.getSystemWindowInsetBottom());
        this.mEdgeBackGestureHandler.setInsets(systemWindowInsetLeft, systemWindowInsetRight);
        boolean z = !QuickStepContract.isGesturalMode(this.mNavBarMode) || windowInsets.getSystemWindowInsetBottom() == 0;
        setClipChildren(z);
        setClipToPadding(z);
        NavigationBarController navigationBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);
        if (navigationBarController == null) {
            assistHandleViewController = null;
        } else {
            assistHandleViewController = navigationBarController.getAssistHandlerViewController();
        }
        if (assistHandleViewController != null) {
            assistHandleViewController.setBottomOffset(windowInsets.getSystemWindowInsetBottom());
        }
        return super.onApplyWindowInsets(windowInsets);
    }

    private static void dumpButton(PrintWriter printWriter, String str, ButtonDispatcher buttonDispatcher) {
        StringBuilder sb = new StringBuilder();
        sb.append("      ");
        sb.append(str);
        sb.append(": ");
        printWriter.print(sb.toString());
        if (buttonDispatcher == null) {
            printWriter.print("null");
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(visibilityToString(buttonDispatcher.getVisibility()));
            sb2.append(" alpha=");
            sb2.append(buttonDispatcher.getAlpha());
            printWriter.print(sb2.toString());
        }
        printWriter.println();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$NavigationBarView(Boolean bool) {
        post(new Runnable(bool) {
            public final /* synthetic */ Boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NavigationBarView.this.lambda$new$1$NavigationBarView(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$NavigationBarView(Boolean bool) {
        this.mDockedStackExists = bool.booleanValue();
        updateRecentsIcon();
    }
}
