package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.biometrics.BiometricSourceType;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.KeyguardStatusView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.DejankUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.fragments.FragmentHostManager.FragmentListener;
import com.android.systemui.p007qs.QSFragment;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p006qs.C0940QS;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.FlingAnimationUtils.Builder;
import com.android.systemui.statusbar.GestureRecorder;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.RemoteInputController.Delegate;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator.ExpandAnimationParameters;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController.Listener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback;
import com.android.systemui.statusbar.phone.KeyguardClockPositionAlgorithm.Result;
import com.android.systemui.statusbar.phone.PanelViewController.TouchHandler;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.InjectionInflationController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class NotificationPanelViewController extends PanelViewController {
    private static final AnimationProperties CLOCK_ANIMATION_PROPERTIES;
    private static final Rect EMPTY_RECT = new Rect();
    private static final AnimationProperties KEYGUARD_HUN_PROPERTIES;
    /* access modifiers changed from: private */
    public static final Rect M_DUMMY_DIRTY_RECT = new Rect(0, 0, 1, 1);
    private final AnimatableProperty KEYGUARD_HEADS_UP_SHOWING_AMOUNT = AnimatableProperty.from("KEYGUARD_HEADS_UP_SHOWING_AMOUNT", new BiConsumer() {
        public final void accept(Object obj, Object obj2) {
            NotificationPanelViewController.this.lambda$new$0$NotificationPanelViewController((NotificationPanelView) obj, (Float) obj2);
        }
    }, new Function() {
        public final Object apply(Object obj) {
            return NotificationPanelViewController.this.lambda$new$1$NotificationPanelViewController((NotificationPanelView) obj);
        }
    }, C2011R$id.keyguard_hun_animator_tag, C2011R$id.keyguard_hun_animator_end_tag, C2011R$id.keyguard_hun_animator_start_tag);
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final ActivityManager mActivityManager;
    private boolean mAffordanceHasPreview;
    /* access modifiers changed from: private */
    public KeyguardAffordanceHelper mAffordanceHelper;
    private Consumer<Boolean> mAffordanceLaunchListener;
    private boolean mAllowExpandForSmallExpansion;
    private int mAmbientIndicationBottomPadding;
    private final Runnable mAnimateKeyguardBottomAreaInvisibleEndRunnable;
    /* access modifiers changed from: private */
    public final Runnable mAnimateKeyguardStatusBarInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewGoneEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewVisibleEndRunnable;
    /* access modifiers changed from: private */
    public boolean mAnimateNextPositionUpdate;
    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            if (NotificationPanelViewController.this.mPanelAlphaEndAction != null) {
                NotificationPanelViewController.this.mPanelAlphaEndAction.run();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mBarState;
    private ViewGroup mBigClockContainer;
    /* access modifiers changed from: private */
    public boolean mBlockTouches;
    /* access modifiers changed from: private */
    public boolean mBlockingExpansionForCurrentTouch;
    private float mBottomAreaShadeAlpha;
    private final ValueAnimator mBottomAreaShadeAlphaAnimator;
    private final KeyguardClockPositionAlgorithm mClockPositionAlgorithm = new KeyguardClockPositionAlgorithm();
    private final Result mClockPositionResult = new Result();
    private boolean mClosingWithAlphaFadeOut;
    private boolean mCollapsedOnDown;
    private final CommandQueue mCommandQueue;
    /* access modifiers changed from: private */
    public final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public final ConfigurationListener mConfigurationListener = new ConfigurationListener();
    private boolean mConflictingQsExpansionGesture;
    private int mDarkIconSize;
    /* access modifiers changed from: private */
    public boolean mDelayShowingKeyguardStatusBar;
    private int mDisplayId;
    /* access modifiers changed from: private */
    public float mDownX;
    /* access modifiers changed from: private */
    public float mDownY;
    private final DozeParameters mDozeParameters;
    /* access modifiers changed from: private */
    public boolean mDozing;
    private boolean mDozingOnDown;
    /* access modifiers changed from: private */
    public float mEmptyDragAmount;
    private final NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public Runnable mExpandAfterLayoutRunnable;
    private float mExpandOffset;
    private boolean mExpandingFromHeadsUp;
    private final ExpansionCallback mExpansionCallback = new ExpansionCallback();
    private boolean mExpectingSynthesizedDown;
    /* access modifiers changed from: private */
    public FalsingManager mFalsingManager;
    /* access modifiers changed from: private */
    public boolean mFirstBypassAttempt;
    private FlingAnimationUtils mFlingAnimationUtils;
    private final Builder mFlingAnimationUtilsBuilder;
    /* access modifiers changed from: private */
    public final FragmentListener mFragmentListener;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpAnimatingAway;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    /* access modifiers changed from: private */
    public Runnable mHeadsUpExistenceChangedRunnable = new Runnable() {
        public final void run() {
            NotificationPanelViewController.this.lambda$new$2$NotificationPanelViewController();
        }
    };
    private int mHeadsUpInset;
    /* access modifiers changed from: private */
    public boolean mHeadsUpPinnedMode;
    /* access modifiers changed from: private */
    public HeadsUpTouchHelper mHeadsUpTouchHelper;
    /* access modifiers changed from: private */
    public final HeightListener mHeightListener = new HeightListener();
    private boolean mHideIconsDuringNotificationLaunch = true;
    private int mIndicationBottomPadding;
    private float mInitialHeightOnTouch;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private final InjectionInflationController mInjectionInflationController;
    /* access modifiers changed from: private */
    public float mInterpolatedDarkAmount;
    /* access modifiers changed from: private */
    public boolean mIsExpanding;
    private boolean mIsFullWidth;
    /* access modifiers changed from: private */
    public boolean mIsLaunchTransitionFinished;
    /* access modifiers changed from: private */
    public boolean mIsLaunchTransitionRunning;
    private final KeyguardAffordanceHelperCallback mKeyguardAffordanceHelperCallback = new KeyguardAffordanceHelperCallback();
    /* access modifiers changed from: private */
    public final KeyguardBypassController mKeyguardBypassController;
    private float mKeyguardHeadsUpShowingAmount;
    private KeyguardIndicationController mKeyguardIndicationController;
    /* access modifiers changed from: private */
    public boolean mKeyguardShowing;
    /* access modifiers changed from: private */
    public KeyguardStatusBarView mKeyguardStatusBar;
    /* access modifiers changed from: private */
    public float mKeyguardStatusBarAnimateAlpha = 1.0f;
    /* access modifiers changed from: private */
    public KeyguardStatusView mKeyguardStatusView;
    /* access modifiers changed from: private */
    public boolean mKeyguardStatusViewAnimating;
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            if (NotificationPanelViewController.this.mFirstBypassAttempt && NotificationPanelViewController.this.mUpdateMonitor.isUnlockingWithBiometricAllowed(z)) {
                NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = true;
            }
        }

        public void onBiometricRunningStateChanged(boolean z, BiometricSourceType biometricSourceType) {
            boolean z2 = true;
            if (!(NotificationPanelViewController.this.mBarState == 1 || NotificationPanelViewController.this.mBarState == 2)) {
                z2 = false;
            }
            if (!z && NotificationPanelViewController.this.mFirstBypassAttempt && z2 && !NotificationPanelViewController.this.mDozing && !NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar) {
                NotificationPanelViewController.this.mFirstBypassAttempt = false;
                NotificationPanelViewController.this.animateKeyguardStatusBarIn(360);
            }
        }

        public void onFinishedGoingToSleep(int i) {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mFirstBypassAttempt = notificationPanelViewController.mKeyguardBypassController.getBypassEnabled();
            NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = false;
        }
    };
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    /* access modifiers changed from: private */
    public String mLastCameraLaunchSource = "lockscreen_affordance";
    /* access modifiers changed from: private */
    public boolean mLastEventSynthesizedDown;
    /* access modifiers changed from: private */
    public int mLastOrientation = -1;
    /* access modifiers changed from: private */
    public float mLastOverscroll;
    /* access modifiers changed from: private */
    public Runnable mLaunchAnimationEndRunnable;
    private boolean mLaunchingAffordance;
    /* access modifiers changed from: private */
    public float mLinearDarkAmount;
    /* access modifiers changed from: private */
    public boolean mListenForHeadsUp;
    /* access modifiers changed from: private */
    public LockscreenGestureLogger mLockscreenGestureLogger = new LockscreenGestureLogger();
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public int mNavigationBarBottomHeight;
    private NotificationsQuickSettingsContainer mNotificationContainerParent;
    /* access modifiers changed from: private */
    public NotificationStackScrollLayout mNotificationStackScroller;
    private int mNotificationsHeaderCollideDistance;
    private int mOldLayoutDirection;
    /* access modifiers changed from: private */
    public final OnClickListener mOnClickListener = new OnClickListener();
    private final OnEmptySpaceClickListener mOnEmptySpaceClickListener = new OnEmptySpaceClickListener();
    private final MyOnHeadsUpChangedListener mOnHeadsUpChangedListener = new MyOnHeadsUpChangedListener();
    private final OnHeightChangedListener mOnHeightChangedListener = new OnHeightChangedListener();
    private final OnOverscrollTopChangedListener mOnOverscrollTopChangedListener = new OnOverscrollTopChangedListener();
    private Runnable mOnReinflationListener;
    /* access modifiers changed from: private */
    public boolean mOnlyAffordanceInThisMotion;
    private int mPanelAlpha;
    private final AnimatableProperty mPanelAlphaAnimator = AnimatableProperty.from("panelAlpha", $$Lambda$aKsp0zdf_wKFZXD1TonJ2cFEsN4.INSTANCE, $$Lambda$SmdYpsZqQm1fpR9OgK3SiEL3pJQ.INSTANCE, C2011R$id.panel_alpha_animator_tag, C2011R$id.panel_alpha_animator_start_tag, C2011R$id.panel_alpha_animator_end_tag);
    /* access modifiers changed from: private */
    public Runnable mPanelAlphaEndAction;
    private final AnimationProperties mPanelAlphaInPropertiesAnimator;
    private final AnimationProperties mPanelAlphaOutPropertiesAnimator;
    private boolean mPanelExpanded;
    private int mPositionMinSideMargin;
    private final PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public final PulseExpansionHandler mPulseExpansionHandler;
    /* access modifiers changed from: private */
    public boolean mPulsing;
    /* access modifiers changed from: private */
    public C0940QS mQs;
    private boolean mQsAnimatorExpand;
    private boolean mQsExpandImmediate;
    /* access modifiers changed from: private */
    public boolean mQsExpanded;
    private boolean mQsExpandedWhenExpandingStarted;
    /* access modifiers changed from: private */
    public ValueAnimator mQsExpansionAnimator;
    /* access modifiers changed from: private */
    public boolean mQsExpansionEnabled = true;
    /* access modifiers changed from: private */
    public boolean mQsExpansionFromOverscroll;
    /* access modifiers changed from: private */
    public float mQsExpansionHeight;
    private int mQsFalsingThreshold;
    private FrameLayout mQsFrame;
    /* access modifiers changed from: private */
    public boolean mQsFullyExpanded;
    /* access modifiers changed from: private */
    public int mQsMaxExpansionHeight;
    /* access modifiers changed from: private */
    public int mQsMinExpansionHeight;
    private View mQsNavbarScrim;
    /* access modifiers changed from: private */
    public int mQsNotificationTopPadding;
    private int mQsPeekHeight;
    private boolean mQsScrimEnabled = true;
    /* access modifiers changed from: private */
    public ValueAnimator mQsSizeChangeAnimator;
    private boolean mQsTouchAboveFalsingThreshold;
    /* access modifiers changed from: private */
    public boolean mQsTracking;
    private VelocityTracker mQsVelocityTracker;
    private final ShadeController mShadeController;
    private int mShelfHeight;
    private boolean mShowEmptyShadeView;
    private boolean mShowIconsWhenExpanded;
    private boolean mShowingKeyguardHeadsUp;
    private int mStackScrollerMeasuringPass;
    /* access modifiers changed from: private */
    public boolean mStackScrollerOverscrolling;
    private final AnimatorUpdateListener mStatusBarAnimateAlphaListener;
    private int mStatusBarMinHeight;
    /* access modifiers changed from: private */
    public final StatusBarStateListener mStatusBarStateListener = new StatusBarStateListener();
    /* access modifiers changed from: private */
    public int mThemeResId;
    private ArrayList<Consumer<ExpandableNotificationRow>> mTrackingHeadsUpListeners = new ArrayList<>();
    private int mTrackingPointer;
    private boolean mTwoFingerQsExpandPossible;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    private boolean mUserSetupComplete;
    private ArrayList<Runnable> mVerticalTranslationListener = new ArrayList<>();
    /* access modifiers changed from: private */
    public final NotificationPanelView mView;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;
    /* access modifiers changed from: private */
    public final ZenModeController mZenModeController;
    /* access modifiers changed from: private */
    public final ZenModeControllerCallback mZenModeControllerCallback = new ZenModeControllerCallback();

    private class ConfigurationListener implements com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener {
        public void onUiModeChanged() {
        }

        private ConfigurationListener() {
        }

        public void onDensityOrFontScaleChanged() {
            NotificationPanelViewController.this.updateShowEmptyShadeView();
        }

        public void onThemeChanged() {
            int themeResId = NotificationPanelViewController.this.mView.getContext().getThemeResId();
            if (NotificationPanelViewController.this.mThemeResId != themeResId) {
                NotificationPanelViewController.this.mThemeResId = themeResId;
                NotificationPanelViewController.this.reInflateViews();
            }
        }

        public void onOverlayChanged() {
            NotificationPanelViewController.this.reInflateViews();
        }
    }

    private class DynamicPrivacyControlListener implements Listener {
        private DynamicPrivacyControlListener() {
        }

        public void onDynamicPrivacyChanged() {
            if (NotificationPanelViewController.this.mLinearDarkAmount == 0.0f) {
                NotificationPanelViewController.this.mAnimateNextPositionUpdate = true;
            }
        }
    }

    private class ExpansionCallback implements com.android.systemui.statusbar.PulseExpansionHandler.ExpansionCallback {
        private ExpansionCallback() {
        }

        public void setEmptyDragAmount(float f) {
            NotificationPanelViewController.this.mEmptyDragAmount = f * 0.2f;
            NotificationPanelViewController.this.positionClockAndNotifications();
        }
    }

    private class HeightListener implements com.android.systemui.plugins.p006qs.C0940QS.HeightListener {
        private HeightListener() {
        }

        public void onQsHeightChanged() {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mQsMaxExpansionHeight = notificationPanelViewController.mQs != null ? NotificationPanelViewController.this.mQs.getDesiredHeight() : 0;
            if (NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mQsFullyExpanded) {
                NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                notificationPanelViewController2.mQsExpansionHeight = (float) notificationPanelViewController2.mQsMaxExpansionHeight;
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
            }
            if (NotificationPanelViewController.this.mAccessibilityManager.isEnabled()) {
                NotificationPanelViewController.this.mView.setAccessibilityPaneTitle(NotificationPanelViewController.this.determineAccessibilityPaneTitle());
            }
            NotificationPanelViewController.this.mNotificationStackScroller.setMaxTopPadding(NotificationPanelViewController.this.mQsMaxExpansionHeight + NotificationPanelViewController.this.mQsNotificationTopPadding);
        }
    }

    private class KeyguardAffordanceHelperCallback implements Callback {
        private KeyguardAffordanceHelperCallback() {
        }

        public void onAnimationToSideStarted(boolean z, float f, float f2) {
            if (NotificationPanelViewController.this.mView.getLayoutDirection() != 1) {
                z = !z;
            }
            NotificationPanelViewController.this.mIsLaunchTransitionRunning = true;
            NotificationPanelViewController.this.mLaunchAnimationEndRunnable = null;
            float displayDensity = NotificationPanelViewController.this.mStatusBar.getDisplayDensity();
            int abs = Math.abs((int) (f / displayDensity));
            int abs2 = Math.abs((int) (f2 / displayDensity));
            if (z) {
                NotificationPanelViewController.this.mLockscreenGestureLogger.write(190, abs, abs2);
                NotificationPanelViewController.this.mFalsingManager.onLeftAffordanceOn();
                if (NotificationPanelViewController.this.mFalsingManager.shouldEnforceBouncer()) {
                    NotificationPanelViewController.this.mStatusBar.executeRunnableDismissingKeyguard(new Runnable() {
                        public final void run() {
                            KeyguardAffordanceHelperCallback.this.mo17789x99d0e4e3();
                        }
                    }, null, true, false, true);
                } else {
                    NotificationPanelViewController.this.mKeyguardBottomArea.launchLeftAffordance();
                }
            } else {
                if ("lockscreen_affordance".equals(NotificationPanelViewController.this.mLastCameraLaunchSource)) {
                    NotificationPanelViewController.this.mLockscreenGestureLogger.write(189, abs, abs2);
                }
                NotificationPanelViewController.this.mFalsingManager.onCameraOn();
                if (NotificationPanelViewController.this.mFalsingManager.shouldEnforceBouncer()) {
                    NotificationPanelViewController.this.mStatusBar.executeRunnableDismissingKeyguard(new Runnable() {
                        public final void run() {
                            KeyguardAffordanceHelperCallback.this.mo17790x4006d02();
                        }
                    }, null, true, false, true);
                } else {
                    NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                    notificationPanelViewController.mKeyguardBottomArea.launchCamera(notificationPanelViewController.mLastCameraLaunchSource);
                }
            }
            NotificationPanelViewController.this.mStatusBar.startLaunchTransitionTimeout();
            NotificationPanelViewController.this.mBlockTouches = true;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onAnimationToSideStarted$0 */
        public /* synthetic */ void mo17789x99d0e4e3() {
            NotificationPanelViewController.this.mKeyguardBottomArea.launchLeftAffordance();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onAnimationToSideStarted$1 */
        public /* synthetic */ void mo17790x4006d02() {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mKeyguardBottomArea.launchCamera(notificationPanelViewController.mLastCameraLaunchSource);
        }

        public void onAnimationToSideEnded() {
            NotificationPanelViewController.this.mIsLaunchTransitionRunning = false;
            NotificationPanelViewController.this.mIsLaunchTransitionFinished = true;
            if (NotificationPanelViewController.this.mLaunchAnimationEndRunnable != null) {
                NotificationPanelViewController.this.mLaunchAnimationEndRunnable.run();
                NotificationPanelViewController.this.mLaunchAnimationEndRunnable = null;
            }
            NotificationPanelViewController.this.mStatusBar.readyForKeyguardDone();
        }

        public float getMaxTranslationDistance() {
            return (float) Math.hypot((double) NotificationPanelViewController.this.mView.getWidth(), (double) NotificationPanelViewController.this.getHeight());
        }

        public void onSwipingStarted(boolean z) {
            NotificationPanelViewController.this.mFalsingManager.onAffordanceSwipingStarted(z);
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                z = !z;
            }
            if (z) {
                NotificationPanelViewController.this.mKeyguardBottomArea.bindCameraPrewarmService();
            }
            NotificationPanelViewController.this.mView.requestDisallowInterceptTouchEvent(true);
            NotificationPanelViewController.this.mOnlyAffordanceInThisMotion = true;
            NotificationPanelViewController.this.mQsTracking = false;
        }

        public void onSwipingAborted() {
            NotificationPanelViewController.this.mFalsingManager.onAffordanceSwipingAborted();
            NotificationPanelViewController.this.mKeyguardBottomArea.unbindCameraPrewarmService(false);
        }

        public void onIconClicked(boolean z) {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            if (!notificationPanelViewController.mHintAnimationRunning) {
                notificationPanelViewController.mHintAnimationRunning = true;
                notificationPanelViewController.mAffordanceHelper.startHintAnimation(z, new Runnable() {
                    public final void run() {
                        KeyguardAffordanceHelperCallback.this.mo17791xac558cd6();
                    }
                });
                if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                    z = !z;
                }
                if (z) {
                    NotificationPanelViewController.this.mStatusBar.onCameraHintStarted();
                } else if (NotificationPanelViewController.this.mKeyguardBottomArea.isLeftVoiceAssist()) {
                    NotificationPanelViewController.this.mStatusBar.onVoiceAssistHintStarted();
                } else {
                    NotificationPanelViewController.this.mStatusBar.onPhoneHintStarted();
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onIconClicked$2 */
        public /* synthetic */ void mo17791xac558cd6() {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mHintAnimationRunning = false;
            notificationPanelViewController.mStatusBar.onHintFinished();
        }

        public KeyguardAffordanceView getLeftIcon() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getRightView() : NotificationPanelViewController.this.mKeyguardBottomArea.getLeftView();
        }

        public KeyguardAffordanceView getRightIcon() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getLeftView() : NotificationPanelViewController.this.mKeyguardBottomArea.getRightView();
        }

        public View getLeftPreview() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getRightPreview() : NotificationPanelViewController.this.mKeyguardBottomArea.getLeftPreview();
        }

        public View getRightPreview() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getLeftPreview() : NotificationPanelViewController.this.mKeyguardBottomArea.getRightPreview();
        }

        public float getAffordanceFalsingFactor() {
            return NotificationPanelViewController.this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f;
        }

        public boolean needsAntiFalsing() {
            return NotificationPanelViewController.this.mBarState == 1;
        }
    }

    private class MyOnHeadsUpChangedListener implements OnHeadsUpChangedListener {
        private MyOnHeadsUpChangedListener() {
        }

        public void onHeadsUpPinnedModeChanged(boolean z) {
            NotificationPanelViewController.this.mNotificationStackScroller.setInHeadsUpPinnedMode(z);
            if (z) {
                NotificationPanelViewController.this.mHeadsUpExistenceChangedRunnable.run();
                NotificationPanelViewController.this.updateNotificationTranslucency();
            } else {
                NotificationPanelViewController.this.setHeadsUpAnimatingAway(true);
                NotificationPanelViewController.this.mNotificationStackScroller.runAfterAnimationFinished(NotificationPanelViewController.this.mHeadsUpExistenceChangedRunnable);
            }
            NotificationPanelViewController.this.updateGestureExclusionRect();
            NotificationPanelViewController.this.mHeadsUpPinnedMode = z;
            NotificationPanelViewController.this.updateHeadsUpVisibility();
            NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
        }

        public void onHeadsUpPinned(NotificationEntry notificationEntry) {
            if (!NotificationPanelViewController.this.isOnKeyguard()) {
                NotificationPanelViewController.this.mNotificationStackScroller.generateHeadsUpAnimation(notificationEntry.getHeadsUpAnimationView(), true);
            }
        }

        public void onHeadsUpUnPinned(NotificationEntry notificationEntry) {
            if (NotificationPanelViewController.this.isFullyCollapsed() && notificationEntry.isRowHeadsUp() && !NotificationPanelViewController.this.isOnKeyguard()) {
                NotificationPanelViewController.this.mNotificationStackScroller.generateHeadsUpAnimation(notificationEntry.getHeadsUpAnimationView(), false);
                notificationEntry.setHeadsUpIsVisible();
            }
        }

        public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
            NotificationPanelViewController.this.mNotificationStackScroller.generateHeadsUpAnimation(notificationEntry, z);
        }
    }

    private class OnApplyWindowInsetsListener implements android.view.View.OnApplyWindowInsetsListener {
        private OnApplyWindowInsetsListener() {
        }

        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            NotificationPanelViewController.this.mNavigationBarBottomHeight = windowInsets.getStableInsetBottom();
            NotificationPanelViewController.this.updateMaxHeadsUpTranslation();
            return windowInsets;
        }
    }

    private class OnAttachStateChangeListener implements android.view.View.OnAttachStateChangeListener {
        private OnAttachStateChangeListener() {
        }

        public void onViewAttachedToWindow(View view) {
            FragmentHostManager.get(NotificationPanelViewController.this.mView).addTagListener(C0940QS.TAG, NotificationPanelViewController.this.mFragmentListener);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mStatusBarStateController.addCallback(notificationPanelViewController.mStatusBarStateListener);
            NotificationPanelViewController.this.mZenModeController.addCallback(NotificationPanelViewController.this.mZenModeControllerCallback);
            NotificationPanelViewController.this.mConfigurationController.addCallback(NotificationPanelViewController.this.mConfigurationListener);
            NotificationPanelViewController.this.mUpdateMonitor.registerCallback(NotificationPanelViewController.this.mKeyguardUpdateCallback);
            NotificationPanelViewController.this.mConfigurationListener.onThemeChanged();
        }

        public void onViewDetachedFromWindow(View view) {
            FragmentHostManager.get(NotificationPanelViewController.this.mView).removeTagListener(C0940QS.TAG, NotificationPanelViewController.this.mFragmentListener);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mStatusBarStateController.removeCallback(notificationPanelViewController.mStatusBarStateListener);
            NotificationPanelViewController.this.mZenModeController.removeCallback(NotificationPanelViewController.this.mZenModeControllerCallback);
            NotificationPanelViewController.this.mConfigurationController.removeCallback(NotificationPanelViewController.this.mConfigurationListener);
            NotificationPanelViewController.this.mUpdateMonitor.removeCallback(NotificationPanelViewController.this.mKeyguardUpdateCallback);
        }
    }

    private class OnClickListener implements android.view.View.OnClickListener {
        private OnClickListener() {
        }

        public void onClick(View view) {
            NotificationPanelViewController.this.onQsExpansionStarted();
            if (NotificationPanelViewController.this.mQsExpanded) {
                NotificationPanelViewController.this.flingSettings(0.0f, 1, null, true);
            } else if (NotificationPanelViewController.this.mQsExpansionEnabled) {
                NotificationPanelViewController.this.mLockscreenGestureLogger.write(195, 0, 0);
                NotificationPanelViewController.this.flingSettings(0.0f, 0, null, true);
            }
        }
    }

    private class OnConfigurationChangedListener extends com.android.systemui.statusbar.phone.PanelViewController.OnConfigurationChangedListener {
        private OnConfigurationChangedListener() {
            super();
        }

        public void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            NotificationPanelViewController.this.mAffordanceHelper.onConfigurationChanged();
            if (configuration.orientation != NotificationPanelViewController.this.mLastOrientation) {
                NotificationPanelViewController.this.resetHorizontalPanelPosition();
            }
            NotificationPanelViewController.this.mLastOrientation = configuration.orientation;
        }
    }

    private class OnEmptySpaceClickListener implements com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.OnEmptySpaceClickListener {
        private OnEmptySpaceClickListener() {
        }

        public void onEmptySpaceClicked(float f, float f2) {
            NotificationPanelViewController.this.onEmptySpaceClick(f);
        }
    }

    private class OnHeightChangedListener implements com.android.systemui.statusbar.notification.row.ExpandableView.OnHeightChangedListener {
        public void onReset(ExpandableView expandableView) {
        }

        private OnHeightChangedListener() {
        }

        public void onHeightChanged(ExpandableView expandableView, boolean z) {
            if (expandableView != null || !NotificationPanelViewController.this.mQsExpanded) {
                if (z && NotificationPanelViewController.this.mInterpolatedDarkAmount == 0.0f) {
                    NotificationPanelViewController.this.mAnimateNextPositionUpdate = true;
                }
                ExpandableView firstChildNotGone = NotificationPanelViewController.this.mNotificationStackScroller.getFirstChildNotGone();
                ExpandableNotificationRow expandableNotificationRow = firstChildNotGone instanceof ExpandableNotificationRow ? (ExpandableNotificationRow) firstChildNotGone : null;
                if (expandableNotificationRow != null && (expandableView == expandableNotificationRow || expandableNotificationRow.getNotificationParent() == expandableNotificationRow)) {
                    NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                }
                NotificationPanelViewController.this.requestPanelHeightUpdate();
            }
        }
    }

    private class OnLayoutChangeListener extends com.android.systemui.statusbar.phone.PanelViewController.OnLayoutChangeListener {
        private OnLayoutChangeListener() {
            super();
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            String str = "NVP#onLayout";
            DejankUtils.startDetectingBlockingIpcs(str);
            super.onLayoutChange(view, i, i2, i3, i4, i5, i6, i7, i8);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.setIsFullWidth(notificationPanelViewController.mNotificationStackScroller.getWidth() == NotificationPanelViewController.this.mView.getWidth());
            NotificationPanelViewController.this.mKeyguardStatusView.setPivotX((float) (NotificationPanelViewController.this.mView.getWidth() / 2));
            NotificationPanelViewController.this.mKeyguardStatusView.setPivotY(NotificationPanelViewController.this.mKeyguardStatusView.getClockTextSize() * 0.34521484f);
            int access$8700 = NotificationPanelViewController.this.mQsMaxExpansionHeight;
            if (NotificationPanelViewController.this.mQs != null) {
                NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                notificationPanelViewController2.mQsMinExpansionHeight = notificationPanelViewController2.mKeyguardShowing ? 0 : NotificationPanelViewController.this.mQs.getQsMinExpansionHeight();
                NotificationPanelViewController notificationPanelViewController3 = NotificationPanelViewController.this;
                notificationPanelViewController3.mQsMaxExpansionHeight = notificationPanelViewController3.mQs.getDesiredHeight();
                NotificationPanelViewController.this.mNotificationStackScroller.setMaxTopPadding(NotificationPanelViewController.this.mQsMaxExpansionHeight + NotificationPanelViewController.this.mQsNotificationTopPadding);
            }
            NotificationPanelViewController.this.positionClockAndNotifications();
            if (NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mQsFullyExpanded) {
                NotificationPanelViewController notificationPanelViewController4 = NotificationPanelViewController.this;
                notificationPanelViewController4.mQsExpansionHeight = (float) notificationPanelViewController4.mQsMaxExpansionHeight;
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
                if (NotificationPanelViewController.this.mQsMaxExpansionHeight != access$8700) {
                    NotificationPanelViewController notificationPanelViewController5 = NotificationPanelViewController.this;
                    notificationPanelViewController5.startQsSizeChangeAnimation(access$8700, notificationPanelViewController5.mQsMaxExpansionHeight);
                }
            } else if (!NotificationPanelViewController.this.mQsExpanded) {
                NotificationPanelViewController notificationPanelViewController6 = NotificationPanelViewController.this;
                notificationPanelViewController6.setQsExpansion(((float) notificationPanelViewController6.mQsMinExpansionHeight) + NotificationPanelViewController.this.mLastOverscroll);
            }
            NotificationPanelViewController notificationPanelViewController7 = NotificationPanelViewController.this;
            notificationPanelViewController7.updateExpandedHeight(notificationPanelViewController7.getExpandedHeight());
            NotificationPanelViewController.this.updateHeader();
            if (NotificationPanelViewController.this.mQsSizeChangeAnimator == null && NotificationPanelViewController.this.mQs != null) {
                NotificationPanelViewController.this.mQs.setHeightOverride(NotificationPanelViewController.this.mQs.getDesiredHeight());
            }
            NotificationPanelViewController.this.updateMaxHeadsUpTranslation();
            NotificationPanelViewController.this.updateGestureExclusionRect();
            if (NotificationPanelViewController.this.mExpandAfterLayoutRunnable != null) {
                NotificationPanelViewController.this.mExpandAfterLayoutRunnable.run();
                NotificationPanelViewController.this.mExpandAfterLayoutRunnable = null;
            }
            DejankUtils.stopDetectingBlockingIpcs(str);
        }
    }

    private class OnOverscrollTopChangedListener implements com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.OnOverscrollTopChangedListener {
        private OnOverscrollTopChangedListener() {
        }

        public void onOverscrollTopChanged(float f, boolean z) {
            NotificationPanelViewController.this.cancelQsAnimation();
            if (!NotificationPanelViewController.this.mQsExpansionEnabled) {
                f = 0.0f;
            }
            if (f < 1.0f) {
                f = 0.0f;
            }
            int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
            boolean z2 = true;
            NotificationPanelViewController.this.setOverScrolling(i != 0 && z);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            if (i == 0) {
                z2 = false;
            }
            notificationPanelViewController.mQsExpansionFromOverscroll = z2;
            NotificationPanelViewController.this.mLastOverscroll = f;
            NotificationPanelViewController.this.updateQsState();
            NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
            notificationPanelViewController2.setQsExpansion(((float) notificationPanelViewController2.mQsMinExpansionHeight) + f);
        }

        public void flingTopOverscroll(float f, boolean z) {
            NotificationPanelViewController.this.mLastOverscroll = 0.0f;
            NotificationPanelViewController.this.mQsExpansionFromOverscroll = false;
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.setQsExpansion(notificationPanelViewController.mQsExpansionHeight);
            NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
            if (!notificationPanelViewController2.mQsExpansionEnabled && z) {
                f = 0.0f;
            }
            notificationPanelViewController2.flingSettings(f, (!z || !NotificationPanelViewController.this.mQsExpansionEnabled) ? 1 : 0, new Runnable() {
                public final void run() {
                    OnOverscrollTopChangedListener.this.mo17799x848b415e();
                }
            }, false);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$flingTopOverscroll$0 */
        public /* synthetic */ void mo17799x848b415e() {
            NotificationPanelViewController.this.mStackScrollerOverscrolling = false;
            NotificationPanelViewController.this.setOverScrolling(false);
            NotificationPanelViewController.this.updateQsState();
        }
    }

    private class StatusBarStateListener implements StateListener {
        private StatusBarStateListener() {
        }

        public void onStateChanged(int i) {
            long j;
            boolean goingToFullShade = NotificationPanelViewController.this.mStatusBarStateController.goingToFullShade();
            boolean isKeyguardFadingAway = NotificationPanelViewController.this.mKeyguardStateController.isKeyguardFadingAway();
            int access$1400 = NotificationPanelViewController.this.mBarState;
            boolean z = i == 1;
            NotificationPanelViewController.this.setKeyguardStatusViewVisibility(i, isKeyguardFadingAway, goingToFullShade);
            NotificationPanelViewController.this.setKeyguardBottomAreaVisibility(i, goingToFullShade);
            NotificationPanelViewController.this.mBarState = i;
            NotificationPanelViewController.this.mKeyguardShowing = z;
            if (access$1400 == 1 && (goingToFullShade || i == 2)) {
                NotificationPanelViewController.this.animateKeyguardStatusBarOut();
                if (NotificationPanelViewController.this.mBarState == 2) {
                    j = 0;
                } else {
                    j = NotificationPanelViewController.this.mKeyguardStateController.calculateGoingToFullShadeDelay();
                }
                NotificationPanelViewController.this.mQs.animateHeaderSlidingIn(j);
            } else if (access$1400 == 2 && i == 1) {
                NotificationPanelViewController.this.animateKeyguardStatusBarIn(360);
                NotificationPanelViewController.this.mNotificationStackScroller.resetScrollPosition();
                if (!NotificationPanelViewController.this.mQsExpanded) {
                    NotificationPanelViewController.this.mQs.animateHeaderSlidingOut();
                }
            } else {
                NotificationPanelViewController.this.mKeyguardStatusBar.setAlpha(1.0f);
                NotificationPanelViewController.this.mKeyguardStatusBar.setVisibility(z ? 0 : 4);
                if (!(!z || access$1400 == NotificationPanelViewController.this.mBarState || NotificationPanelViewController.this.mQs == null)) {
                    NotificationPanelViewController.this.mQs.hideImmediately();
                }
            }
            NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
            if (z) {
                NotificationPanelViewController.this.updateDozingVisibilities(false);
            }
            NotificationPanelViewController.this.updateQSPulseExpansion();
            NotificationPanelViewController.this.maybeAnimateBottomAreaAlpha();
            NotificationPanelViewController.this.resetHorizontalPanelPosition();
            NotificationPanelViewController.this.updateQsState();
        }

        public void onDozeAmountChanged(float f, float f2) {
            NotificationPanelViewController.this.mInterpolatedDarkAmount = f2;
            NotificationPanelViewController.this.mLinearDarkAmount = f;
            NotificationPanelViewController.this.mKeyguardStatusView.setDarkAmount(NotificationPanelViewController.this.mInterpolatedDarkAmount);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mKeyguardBottomArea.setDarkAmount(notificationPanelViewController.mInterpolatedDarkAmount);
            NotificationPanelViewController.this.positionClockAndNotifications();
        }
    }

    private class ZenModeControllerCallback implements ZenModeController.Callback {
        private ZenModeControllerCallback() {
        }

        public void onZenChanged(int i) {
            NotificationPanelViewController.this.updateShowEmptyShadeView();
        }
    }

    static {
        AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.setDuration(360);
        CLOCK_ANIMATION_PROPERTIES = animationProperties;
        AnimationProperties animationProperties2 = new AnimationProperties();
        animationProperties2.setDuration(360);
        KEYGUARD_HUN_PROPERTIES = animationProperties2;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NotificationPanelViewController(NotificationPanelView notificationPanelView, Float f) {
        setKeyguardHeadsUpShowingAmount(f.floatValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ Float lambda$new$1$NotificationPanelViewController(NotificationPanelView notificationPanelView) {
        return Float.valueOf(getKeyguardHeadsUpShowingAmount());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$NotificationPanelViewController() {
        setHeadsUpAnimatingAway(false);
        notifyBarPanelExpansionChanged();
    }

    public NotificationPanelViewController(NotificationPanelView notificationPanelView, InjectionInflationController injectionInflationController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, PulseExpansionHandler pulseExpansionHandler, DynamicPrivacyController dynamicPrivacyController, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager, ShadeController shadeController, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationEntryManager notificationEntryManager, KeyguardStateController keyguardStateController, StatusBarStateController statusBarStateController, DozeLog dozeLog, DozeParameters dozeParameters, CommandQueue commandQueue, VibratorHelper vibratorHelper, LatencyTracker latencyTracker, PowerManager powerManager, AccessibilityManager accessibilityManager, int i, KeyguardUpdateMonitor keyguardUpdateMonitor, MetricsLogger metricsLogger, ActivityManager activityManager, ZenModeController zenModeController, ConfigurationController configurationController, Builder builder, StatusBarTouchableRegionManager statusBarTouchableRegionManager) {
        NotificationPanelView notificationPanelView2 = notificationPanelView;
        PulseExpansionHandler pulseExpansionHandler2 = pulseExpansionHandler;
        super(notificationPanelView, falsingManager, dozeLog, keyguardStateController, (SysuiStatusBarStateController) statusBarStateController, vibratorHelper, latencyTracker, builder, statusBarTouchableRegionManager);
        AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.setDuration(150);
        animationProperties.setCustomInterpolator(this.mPanelAlphaAnimator.getProperty(), Interpolators.ALPHA_OUT);
        this.mPanelAlphaOutPropertiesAnimator = animationProperties;
        AnimationProperties animationProperties2 = new AnimationProperties();
        animationProperties2.setDuration(200);
        animationProperties2.setAnimationFinishListener(this.mAnimatorListenerAdapter);
        animationProperties2.setCustomInterpolator(this.mPanelAlphaAnimator.getProperty(), Interpolators.ALPHA_IN);
        this.mPanelAlphaInPropertiesAnimator = animationProperties2;
        this.mKeyguardHeadsUpShowingAmount = 0.0f;
        this.mAnimateKeyguardStatusViewInvisibleEndRunnable = new Runnable() {
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusViewAnimating = false;
                NotificationPanelViewController.this.mKeyguardStatusView.setVisibility(4);
            }
        };
        this.mAnimateKeyguardStatusViewGoneEndRunnable = new Runnable() {
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusViewAnimating = false;
                NotificationPanelViewController.this.mKeyguardStatusView.setVisibility(8);
            }
        };
        this.mAnimateKeyguardStatusViewVisibleEndRunnable = new Runnable() {
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusViewAnimating = false;
            }
        };
        this.mAnimateKeyguardStatusBarInvisibleEndRunnable = new Runnable() {
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusBar.setVisibility(4);
                NotificationPanelViewController.this.mKeyguardStatusBar.setAlpha(1.0f);
                NotificationPanelViewController.this.mKeyguardStatusBarAnimateAlpha = 1.0f;
            }
        };
        this.mStatusBarAnimateAlphaListener = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationPanelViewController.this.mKeyguardStatusBarAnimateAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                NotificationPanelViewController.this.updateHeaderKeyguardAlpha();
            }
        };
        this.mAnimateKeyguardBottomAreaInvisibleEndRunnable = new Runnable() {
            public void run() {
                NotificationPanelViewController.this.mKeyguardBottomArea.setVisibility(8);
            }
        };
        this.mFragmentListener = new FragmentListener() {
            public void onFragmentViewCreated(String str, Fragment fragment) {
                NotificationPanelViewController.this.mQs = (C0940QS) fragment;
                NotificationPanelViewController.this.mQs.setPanelView(NotificationPanelViewController.this.mHeightListener);
                NotificationPanelViewController.this.mQs.setExpandClickListener(NotificationPanelViewController.this.mOnClickListener);
                NotificationPanelViewController.this.mQs.setHeaderClickable(NotificationPanelViewController.this.mQsExpansionEnabled);
                NotificationPanelViewController.this.updateQSPulseExpansion();
                NotificationPanelViewController.this.mQs.setOverscrolling(NotificationPanelViewController.this.mStackScrollerOverscrolling);
                NotificationPanelViewController.this.mQs.getView().addOnLayoutChangeListener(new android.view.View.OnLayoutChangeListener() {
                    public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                        C153417.this.mo17780xa4469db1(view, i, i2, i3, i4, i5, i6, i7, i8);
                    }
                });
                NotificationPanelViewController.this.mNotificationStackScroller.setQsContainer((ViewGroup) NotificationPanelViewController.this.mQs.getView());
                if (NotificationPanelViewController.this.mQs instanceof QSFragment) {
                    NotificationPanelViewController.this.mKeyguardStatusBar.setQSPanel(((QSFragment) NotificationPanelViewController.this.mQs).getQsPanel());
                }
                NotificationPanelViewController.this.updateQsExpansion();
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onFragmentViewCreated$0 */
            public /* synthetic */ void mo17780xa4469db1(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (i4 - i2 != i8 - i6) {
                    NotificationPanelViewController.this.mHeightListener.onQsHeightChanged();
                }
            }

            public void onFragmentViewDestroyed(String str, Fragment fragment) {
                if (fragment == NotificationPanelViewController.this.mQs) {
                    NotificationPanelViewController.this.mQs = null;
                }
            }
        };
        this.mView = notificationPanelView2;
        this.mMetricsLogger = metricsLogger;
        this.mActivityManager = activityManager;
        this.mZenModeController = zenModeController;
        this.mConfigurationController = configurationController;
        this.mFlingAnimationUtilsBuilder = builder;
        notificationPanelView.setWillNotDraw(true);
        this.mInjectionInflationController = injectionInflationController;
        this.mFalsingManager = falsingManager;
        this.mPowerManager = powerManager;
        this.mWakeUpCoordinator = notificationWakeUpCoordinator;
        this.mAccessibilityManager = accessibilityManager;
        this.mView.setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        setPanelAlpha(255, false);
        this.mCommandQueue = commandQueue;
        this.mDisplayId = i;
        this.mPulseExpansionHandler = pulseExpansionHandler2;
        this.mDozeParameters = dozeParameters;
        pulseExpansionHandler2.setPulseExpandAbortListener(new Runnable() {
            public final void run() {
                NotificationPanelViewController.this.lambda$new$3$NotificationPanelViewController();
            }
        });
        this.mThemeResId = this.mView.getContext().getThemeResId();
        this.mKeyguardBypassController = keyguardBypassController;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mFirstBypassAttempt = keyguardBypassController.getBypassEnabled();
        this.mKeyguardStateController.addCallback(new KeyguardStateController.Callback() {
            public void onKeyguardFadingAwayChanged() {
                if (!NotificationPanelViewController.this.mKeyguardStateController.isKeyguardFadingAway()) {
                    NotificationPanelViewController.this.mFirstBypassAttempt = false;
                    NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = false;
                }
            }
        });
        dynamicPrivacyController.addListener(new DynamicPrivacyControlListener());
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        this.mBottomAreaShadeAlphaAnimator = ofFloat;
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationPanelViewController.this.lambda$new$4$NotificationPanelViewController(valueAnimator);
            }
        });
        this.mBottomAreaShadeAlphaAnimator.setDuration(160);
        this.mBottomAreaShadeAlphaAnimator.setInterpolator(Interpolators.ALPHA_OUT);
        this.mShadeController = shadeController;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mEntryManager = notificationEntryManager;
        this.mView.setBackgroundColor(0);
        OnAttachStateChangeListener onAttachStateChangeListener = new OnAttachStateChangeListener();
        this.mView.addOnAttachStateChangeListener(onAttachStateChangeListener);
        if (this.mView.isAttachedToWindow()) {
            onAttachStateChangeListener.onViewAttachedToWindow(this.mView);
        }
        this.mView.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener());
        onFinishInflate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$NotificationPanelViewController() {
        C0940QS qs = this.mQs;
        if (qs != null) {
            qs.animateHeaderSlidingOut();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$NotificationPanelViewController(ValueAnimator valueAnimator) {
        this.mBottomAreaShadeAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateKeyguardBottomAreaAlpha();
    }

    private void onFinishInflate() {
        loadDimens();
        this.mKeyguardStatusBar = (KeyguardStatusBarView) this.mView.findViewById(C2011R$id.keyguard_header);
        this.mKeyguardStatusView = (KeyguardStatusView) this.mView.findViewById(C2011R$id.keyguard_status_view);
        KeyguardClockSwitch keyguardClockSwitch = (KeyguardClockSwitch) this.mView.findViewById(C2011R$id.keyguard_clock_container);
        ViewGroup viewGroup = (ViewGroup) this.mView.findViewById(C2011R$id.big_clock_container);
        this.mBigClockContainer = viewGroup;
        keyguardClockSwitch.setBigClockContainer(viewGroup);
        this.mNotificationContainerParent = (NotificationsQuickSettingsContainer) this.mView.findViewById(C2011R$id.notification_container_parent);
        NotificationStackScrollLayout notificationStackScrollLayout = (NotificationStackScrollLayout) this.mView.findViewById(C2011R$id.notification_stack_scroller);
        this.mNotificationStackScroller = notificationStackScrollLayout;
        notificationStackScrollLayout.setOnHeightChangedListener(this.mOnHeightChangedListener);
        this.mNotificationStackScroller.setOverscrollTopChangedListener(this.mOnOverscrollTopChangedListener);
        this.mNotificationStackScroller.setOnEmptySpaceClickListener(this.mOnEmptySpaceClickListener);
        NotificationStackScrollLayout notificationStackScrollLayout2 = this.mNotificationStackScroller;
        Objects.requireNonNull(notificationStackScrollLayout2);
        addTrackingHeadsUpListener(new Consumer() {
            public final void accept(Object obj) {
                NotificationStackScrollLayout.this.setTrackingHeadsUp((ExpandableNotificationRow) obj);
            }
        });
        this.mKeyguardBottomArea = (KeyguardBottomAreaView) this.mView.findViewById(C2011R$id.keyguard_bottom_area);
        this.mQsNavbarScrim = this.mView.findViewById(C2011R$id.qs_navbar_scrim);
        this.mLastOrientation = this.mResources.getConfiguration().orientation;
        initBottomArea();
        this.mWakeUpCoordinator.setStackScroller(this.mNotificationStackScroller);
        this.mQsFrame = (FrameLayout) this.mView.findViewById(C2011R$id.qs_frame);
        this.mPulseExpansionHandler.setUp(this.mNotificationStackScroller, this.mExpansionCallback, this.mShadeController);
        this.mWakeUpCoordinator.addListener(new WakeUpListener() {
            public void onFullyHiddenChanged(boolean z) {
                NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
            }

            public void onPulseExpansionChanged(boolean z) {
                if (NotificationPanelViewController.this.mKeyguardBypassController.getBypassEnabled()) {
                    NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                    NotificationPanelViewController.this.updateQSPulseExpansion();
                }
            }
        });
        this.mView.setRtlChangeListener(new RtlChangeListener() {
            public final void onRtlPropertielsChanged(int i) {
                NotificationPanelViewController.this.lambda$onFinishInflate$5$NotificationPanelViewController(i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$5 */
    public /* synthetic */ void lambda$onFinishInflate$5$NotificationPanelViewController(int i) {
        if (i != this.mOldLayoutDirection) {
            this.mAffordanceHelper.onRtlPropertiesChanged();
            this.mOldLayoutDirection = i;
        }
    }

    /* access modifiers changed from: protected */
    public void loadDimens() {
        super.loadDimens();
        Builder builder = this.mFlingAnimationUtilsBuilder;
        builder.reset();
        builder.setMaxLengthSeconds(0.4f);
        this.mFlingAnimationUtils = builder.build();
        this.mStatusBarMinHeight = this.mResources.getDimensionPixelSize(17105462);
        this.mQsPeekHeight = this.mResources.getDimensionPixelSize(C2009R$dimen.qs_peek_height);
        this.mNotificationsHeaderCollideDistance = this.mResources.getDimensionPixelSize(C2009R$dimen.header_notifications_collide_distance);
        this.mClockPositionAlgorithm.loadDimens(this.mResources);
        this.mQsFalsingThreshold = this.mResources.getDimensionPixelSize(C2009R$dimen.qs_falsing_threshold);
        this.mPositionMinSideMargin = this.mResources.getDimensionPixelSize(C2009R$dimen.notification_panel_min_side_margin);
        this.mIndicationBottomPadding = this.mResources.getDimensionPixelSize(C2009R$dimen.keyguard_indication_bottom_padding);
        this.mQsNotificationTopPadding = this.mResources.getDimensionPixelSize(C2009R$dimen.qs_notification_padding);
        this.mShelfHeight = this.mResources.getDimensionPixelSize(C2009R$dimen.notification_shelf_height);
        this.mDarkIconSize = this.mResources.getDimensionPixelSize(C2009R$dimen.status_bar_icon_drawing_size_dark);
        this.mHeadsUpInset = this.mResources.getDimensionPixelSize(17105462) + this.mResources.getDimensionPixelSize(C2009R$dimen.heads_up_status_bar_padding);
    }

    public boolean hasCustomClock() {
        return this.mKeyguardStatusView.hasCustomClock();
    }

    private void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
        this.mKeyguardBottomArea.setStatusBar(statusBar);
    }

    public void setLaunchAffordanceListener(Consumer<Boolean> consumer) {
        this.mAffordanceLaunchListener = consumer;
    }

    public void updateResources() {
        int dimensionPixelSize = this.mResources.getDimensionPixelSize(C2009R$dimen.qs_panel_width);
        int integer = this.mResources.getInteger(C2012R$integer.notification_panel_layout_gravity);
        LayoutParams layoutParams = (LayoutParams) this.mQsFrame.getLayoutParams();
        if (!(layoutParams.width == dimensionPixelSize && layoutParams.gravity == integer)) {
            layoutParams.width = dimensionPixelSize;
            layoutParams.gravity = integer;
            this.mQsFrame.setLayoutParams(layoutParams);
        }
        int dimensionPixelSize2 = this.mResources.getDimensionPixelSize(C2009R$dimen.notification_panel_width);
        LayoutParams layoutParams2 = (LayoutParams) this.mNotificationStackScroller.getLayoutParams();
        if (layoutParams2.width != dimensionPixelSize2 || layoutParams2.gravity != integer) {
            layoutParams2.width = dimensionPixelSize2;
            layoutParams2.gravity = integer;
            this.mNotificationStackScroller.setLayoutParams(layoutParams2);
        }
    }

    /* access modifiers changed from: private */
    public void reInflateViews() {
        updateShowEmptyShadeView();
        int indexOfChild = this.mView.indexOfChild(this.mKeyguardStatusView);
        this.mView.removeView(this.mKeyguardStatusView);
        KeyguardStatusView keyguardStatusView = (KeyguardStatusView) this.mInjectionInflationController.injectable(LayoutInflater.from(this.mView.getContext())).inflate(C2013R$layout.keyguard_status_view, this.mView, false);
        this.mKeyguardStatusView = keyguardStatusView;
        this.mView.addView(keyguardStatusView, indexOfChild);
        this.mBigClockContainer.removeAllViews();
        ((KeyguardClockSwitch) this.mView.findViewById(C2011R$id.keyguard_clock_container)).setBigClockContainer(this.mBigClockContainer);
        int indexOfChild2 = this.mView.indexOfChild(this.mKeyguardBottomArea);
        this.mView.removeView(this.mKeyguardBottomArea);
        KeyguardBottomAreaView keyguardBottomAreaView = this.mKeyguardBottomArea;
        KeyguardBottomAreaView keyguardBottomAreaView2 = (KeyguardBottomAreaView) this.mInjectionInflationController.injectable(LayoutInflater.from(this.mView.getContext())).inflate(C2013R$layout.keyguard_bottom_area, this.mView, false);
        this.mKeyguardBottomArea = keyguardBottomAreaView2;
        keyguardBottomAreaView2.initFrom(keyguardBottomAreaView);
        this.mView.addView(this.mKeyguardBottomArea, indexOfChild2);
        initBottomArea();
        this.mKeyguardIndicationController.setIndicationArea(this.mKeyguardBottomArea);
        this.mStatusBarStateListener.onDozeAmountChanged(this.mStatusBarStateController.getDozeAmount(), this.mStatusBarStateController.getInterpolatedDozeAmount());
        KeyguardStatusBarView keyguardStatusBarView = this.mKeyguardStatusBar;
        if (keyguardStatusBarView != null) {
            keyguardStatusBarView.onThemeChanged();
        }
        setKeyguardStatusViewVisibility(this.mBarState, false, false);
        setKeyguardBottomAreaVisibility(this.mBarState, false);
        Runnable runnable = this.mOnReinflationListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    private void initBottomArea() {
        KeyguardAffordanceHelper keyguardAffordanceHelper = new KeyguardAffordanceHelper(this.mKeyguardAffordanceHelperCallback, this.mView.getContext(), this.mFalsingManager);
        this.mAffordanceHelper = keyguardAffordanceHelper;
        this.mKeyguardBottomArea.setAffordanceHelper(keyguardAffordanceHelper);
        this.mKeyguardBottomArea.setStatusBar(this.mStatusBar);
        this.mKeyguardBottomArea.setUserSetupComplete(this.mUserSetupComplete);
    }

    public void setKeyguardIndicationController(KeyguardIndicationController keyguardIndicationController) {
        this.mKeyguardIndicationController = keyguardIndicationController;
        keyguardIndicationController.setIndicationArea(this.mKeyguardBottomArea);
    }

    /* access modifiers changed from: private */
    public void updateGestureExclusionRect() {
        List list;
        Rect calculateGestureExclusionRect = calculateGestureExclusionRect();
        NotificationPanelView notificationPanelView = this.mView;
        if (calculateGestureExclusionRect.isEmpty()) {
            list = Collections.EMPTY_LIST;
        } else {
            list = Collections.singletonList(calculateGestureExclusionRect);
        }
        notificationPanelView.setSystemGestureExclusionRects(list);
    }

    private Rect calculateGestureExclusionRect() {
        Region calculateTouchableRegion = this.mStatusBarTouchableRegionManager.calculateTouchableRegion();
        Rect bounds = (!isFullyCollapsed() || calculateTouchableRegion == null) ? null : calculateTouchableRegion.getBounds();
        return bounds != null ? bounds : EMPTY_RECT;
    }

    /* access modifiers changed from: private */
    public void setIsFullWidth(boolean z) {
        this.mIsFullWidth = z;
        this.mNotificationStackScroller.setIsFullWidth(z);
    }

    /* access modifiers changed from: private */
    public void startQsSizeChangeAnimation(int i, int i2) {
        ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
        if (valueAnimator != null) {
            i = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            this.mQsSizeChangeAnimator.cancel();
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i, i2});
        this.mQsSizeChangeAnimator = ofInt;
        ofInt.setDuration(300);
        this.mQsSizeChangeAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mQsSizeChangeAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
                NotificationPanelViewController.this.mQs.setHeightOverride(((Integer) NotificationPanelViewController.this.mQsSizeChangeAnimator.getAnimatedValue()).intValue());
            }
        });
        this.mQsSizeChangeAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                NotificationPanelViewController.this.mQsSizeChangeAnimator = null;
            }
        });
        this.mQsSizeChangeAnimator.start();
    }

    /* access modifiers changed from: private */
    public void positionClockAndNotifications() {
        int i;
        boolean isAddOrRemoveAnimationPending = this.mNotificationStackScroller.isAddOrRemoveAnimationPending();
        boolean z = isAddOrRemoveAnimationPending || this.mAnimateNextPositionUpdate;
        if (this.mBarState != 1) {
            i = getUnlockedStackScrollerPadding();
        } else {
            int height = this.mView.getHeight();
            int max = Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding);
            int clockPreferredY = this.mKeyguardStatusView.getClockPreferredY(height);
            boolean bypassEnabled = this.mKeyguardBypassController.getBypassEnabled();
            boolean z2 = !bypassEnabled && this.mNotificationStackScroller.getVisibleNotificationCount() != 0;
            this.mKeyguardStatusView.setHasVisibleNotifications(z2);
            this.mClockPositionAlgorithm.setup(this.mStatusBarMinHeight, height - max, this.mNotificationStackScroller.getIntrinsicContentHeight(), getExpandedFraction(), height, (int) ((((float) this.mKeyguardStatusView.getHeight()) - (((float) this.mShelfHeight) / 2.0f)) - (((float) this.mDarkIconSize) / 2.0f)), clockPreferredY, hasCustomClock(), z2, this.mInterpolatedDarkAmount, this.mEmptyDragAmount, bypassEnabled, getUnlockedStackScrollerPadding());
            this.mClockPositionAlgorithm.run(this.mClockPositionResult);
            PropertyAnimator.setProperty(this.mKeyguardStatusView, AnimatableProperty.f69X, (float) this.mClockPositionResult.clockX, CLOCK_ANIMATION_PROPERTIES, z);
            PropertyAnimator.setProperty(this.mKeyguardStatusView, AnimatableProperty.f70Y, (float) this.mClockPositionResult.clockY, CLOCK_ANIMATION_PROPERTIES, z);
            updateNotificationTranslucency();
            updateClock();
            i = this.mClockPositionResult.stackScrollerPaddingExpanded;
        }
        this.mNotificationStackScroller.setIntrinsicPadding(i);
        this.mKeyguardBottomArea.setAntiBurnInOffsetX(this.mClockPositionResult.clockX);
        this.mStackScrollerMeasuringPass++;
        requestScrollerTopPaddingUpdate(isAddOrRemoveAnimationPending);
        this.mStackScrollerMeasuringPass = 0;
        this.mAnimateNextPositionUpdate = false;
    }

    private int getUnlockedStackScrollerPadding() {
        C0940QS qs = this.mQs;
        return (qs != null ? qs.getHeader().getHeight() : 0) + this.mQsPeekHeight + this.mQsNotificationTopPadding;
    }

    public int computeMaxKeyguardNotifications(int i) {
        float f;
        float minStackScrollerPadding = this.mClockPositionAlgorithm.getMinStackScrollerPadding();
        int max = Math.max(1, this.mResources.getDimensionPixelSize(C2009R$dimen.notification_divider_height));
        NotificationShelf notificationShelf = this.mNotificationStackScroller.getNotificationShelf();
        if (notificationShelf.getVisibility() == 8) {
            f = 0.0f;
        } else {
            f = (float) (notificationShelf.getIntrinsicHeight() + max);
        }
        float height = (((((float) this.mNotificationStackScroller.getHeight()) - minStackScrollerPadding) - f) - ((float) Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding))) - ((float) this.mKeyguardStatusView.getLogoutButtonHeight());
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i2 >= this.mNotificationStackScroller.getChildCount()) {
                break;
            }
            ExpandableView expandableView = (ExpandableView) this.mNotificationStackScroller.getChildAt(i2);
            if (expandableView instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) expandableView;
                NotificationGroupManager notificationGroupManager = this.mGroupManager;
                if (!(notificationGroupManager != null && notificationGroupManager.isSummaryOfSuppressedGroup(expandableNotificationRow.getEntry().getSbn())) && this.mLockscreenUserManager.shouldShowOnKeyguard(expandableNotificationRow.getEntry()) && !expandableNotificationRow.isRemoved()) {
                    height -= (float) (expandableView.getMinHeight(true) + max);
                    if (height >= 0.0f && i3 < i) {
                        i3++;
                    }
                }
            }
            i2++;
        }
        if (height > (-f)) {
            for (int i4 = i2 + 1; i4 < this.mNotificationStackScroller.getChildCount(); i4++) {
                if (this.mNotificationStackScroller.getChildAt(i4) instanceof ExpandableNotificationRow) {
                    return i3;
                }
            }
            i3++;
        }
        return i3;
    }

    private void updateClock() {
        if (!this.mKeyguardStatusViewAnimating) {
            this.mKeyguardStatusView.setAlpha(this.mClockPositionResult.clockAlpha);
        }
    }

    public void animateToFullShade(long j) {
        this.mNotificationStackScroller.goToFullShade(j);
        this.mView.requestLayout();
        this.mAnimateNextPositionUpdate = true;
    }

    public void setQsExpansionEnabled(boolean z) {
        this.mQsExpansionEnabled = z;
        C0940QS qs = this.mQs;
        if (qs != null) {
            qs.setHeaderClickable(z);
        }
    }

    public void resetViews(boolean z) {
        this.mIsLaunchTransitionFinished = false;
        this.mBlockTouches = false;
        if (!this.mLaunchingAffordance) {
            this.mAffordanceHelper.reset(false);
            this.mLastCameraLaunchSource = "lockscreen_affordance";
        }
        this.mStatusBar.getGutsManager().closeAndSaveGuts(true, true, true, -1, -1, true);
        if (z) {
            animateCloseQs(true);
        } else {
            closeQs();
        }
        this.mNotificationStackScroller.setOverScrollAmount(0.0f, true, z, !z);
        this.mNotificationStackScroller.resetScrollPosition();
    }

    public void collapse(boolean z, float f) {
        if (canPanelBeCollapsed()) {
            if (this.mQsExpanded) {
                this.mQsExpandImmediate = true;
                this.mNotificationStackScroller.setShouldShowShelfOnly(true);
            }
            super.collapse(z, f);
        }
    }

    public void closeQs() {
        cancelQsAnimation();
        setQsExpansion((float) this.mQsMinExpansionHeight);
    }

    public void cancelAnimation() {
        this.mView.animate().cancel();
    }

    public void animateCloseQs(boolean z) {
        ValueAnimator valueAnimator = this.mQsExpansionAnimator;
        if (valueAnimator != null) {
            if (this.mQsAnimatorExpand) {
                float f = this.mQsExpansionHeight;
                valueAnimator.cancel();
                setQsExpansion(f);
            } else {
                return;
            }
        }
        flingSettings(0.0f, z ? 2 : 1);
    }

    public void expandWithQs() {
        if (this.mQsExpansionEnabled) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        if (isFullyCollapsed()) {
            expand(true);
        } else {
            flingSettings(0.0f, 0);
        }
    }

    public void expandWithoutQs() {
        if (isQsExpanded()) {
            flingSettings(0.0f, 1);
        } else {
            expand(true);
        }
    }

    public void fling(float f, boolean z) {
        GestureRecorder gestureRecorder = ((PhoneStatusBarView) this.mBar).mBar.getGestureRecorder();
        if (gestureRecorder != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("fling ");
            sb.append(f > 0.0f ? "open" : "closed");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append("notifications,v=");
            sb3.append(f);
            gestureRecorder.tag(sb2, sb3.toString());
        }
        super.fling(f, z);
    }

    /* access modifiers changed from: protected */
    public void flingToHeight(float f, boolean z, float f2, float f3, boolean z2) {
        this.mHeadsUpTouchHelper.notifyFling(!z);
        setClosingWithAlphaFadeout(!z && !isOnKeyguard() && getFadeoutAlpha() == 1.0f);
        super.flingToHeight(f, z, f2, f3, z2);
    }

    /* access modifiers changed from: private */
    public boolean onQsIntercept(MotionEvent motionEvent) {
        int findPointerIndex = motionEvent.findPointerIndex(this.mTrackingPointer);
        if (findPointerIndex < 0) {
            this.mTrackingPointer = motionEvent.getPointerId(0);
            findPointerIndex = 0;
        }
        float x = motionEvent.getX(findPointerIndex);
        float y = motionEvent.getY(findPointerIndex);
        int actionMasked = motionEvent.getActionMasked();
        boolean z = true;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    float f = y - this.mInitialTouchY;
                    trackMovement(motionEvent);
                    if (this.mQsTracking) {
                        setQsExpansion(f + this.mInitialHeightOnTouch);
                        trackMovement(motionEvent);
                        return true;
                    } else if (Math.abs(f) > ((float) this.mTouchSlop) && Math.abs(f) > Math.abs(x - this.mInitialTouchX) && shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, f)) {
                        this.mQsTracking = true;
                        onQsExpansionStarted();
                        notifyExpandingFinished();
                        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                        this.mInitialTouchY = y;
                        this.mInitialTouchX = x;
                        this.mNotificationStackScroller.cancelLongPress();
                        return true;
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked == 6) {
                        int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                        if (this.mTrackingPointer == pointerId) {
                            if (motionEvent.getPointerId(0) != pointerId) {
                                z = false;
                            }
                            this.mTrackingPointer = motionEvent.getPointerId(z ? 1 : 0);
                            this.mInitialTouchX = motionEvent.getX(z);
                            this.mInitialTouchY = motionEvent.getY(z);
                        }
                    }
                }
            }
            trackMovement(motionEvent);
            if (this.mQsTracking) {
                if (motionEvent.getActionMasked() != 3) {
                    z = false;
                }
                flingQsWithCurrentVelocity(y, z);
                this.mQsTracking = false;
            }
        } else {
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            initVelocityTracker();
            trackMovement(motionEvent);
            if (shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, 0.0f)) {
                this.mView.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.mQsExpansionAnimator != null) {
                onQsExpansionStarted();
                this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                this.mQsTracking = true;
                this.mNotificationStackScroller.cancelLongPress();
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isInContentBounds(float f, float f2) {
        float x = this.mNotificationStackScroller.getX();
        return !this.mNotificationStackScroller.isBelowLastNotification(f - x, f2) && x < f && f < x + ((float) this.mNotificationStackScroller.getWidth());
    }

    /* access modifiers changed from: private */
    public void initDownStates(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mOnlyAffordanceInThisMotion = false;
            this.mQsTouchAboveFalsingThreshold = this.mQsFullyExpanded;
            this.mDozingOnDown = isDozing();
            this.mDownX = motionEvent.getX();
            this.mDownY = motionEvent.getY();
            boolean isFullyCollapsed = isFullyCollapsed();
            this.mCollapsedOnDown = isFullyCollapsed;
            this.mListenForHeadsUp = isFullyCollapsed && this.mHeadsUpManager.hasPinnedHeadsUp();
            boolean z = this.mExpectingSynthesizedDown;
            this.mAllowExpandForSmallExpansion = z;
            this.mTouchSlopExceededBeforeDown = z;
            if (z) {
                this.mLastEventSynthesizedDown = true;
            } else {
                this.mLastEventSynthesizedDown = false;
            }
        } else {
            this.mLastEventSynthesizedDown = false;
        }
    }

    private void flingQsWithCurrentVelocity(float f, boolean z) {
        float currentQSVelocity = getCurrentQSVelocity();
        boolean flingExpandsQs = flingExpandsQs(currentQSVelocity);
        if (flingExpandsQs) {
            logQsSwipeDown(f);
        }
        flingSettings(currentQSVelocity, (!flingExpandsQs || z) ? 1 : 0);
    }

    private void logQsSwipeDown(float f) {
        this.mLockscreenGestureLogger.write(this.mBarState == 1 ? 193 : 194, (int) ((f - this.mInitialTouchY) / this.mStatusBar.getDisplayDensity()), (int) (getCurrentQSVelocity() / this.mStatusBar.getDisplayDensity()));
    }

    private boolean flingExpandsQs(float f) {
        boolean z = false;
        if (!this.mFalsingManager.isUnlockingDisabled() && !isFalseTouch()) {
            if (Math.abs(f) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
                if (getQsExpansionFraction() > 0.5f) {
                    z = true;
                }
                return z;
            } else if (f > 0.0f) {
                z = true;
            }
        }
        return z;
    }

    private boolean isFalseTouch() {
        if (!this.mKeyguardAffordanceHelperCallback.needsAntiFalsing()) {
            return false;
        }
        if (this.mFalsingManager.isClassifierEnabled()) {
            return this.mFalsingManager.isFalseTouch();
        }
        return !this.mQsTouchAboveFalsingThreshold;
    }

    private float getQsExpansionFraction() {
        float f = this.mQsExpansionHeight;
        int i = this.mQsMinExpansionHeight;
        return Math.min(1.0f, (f - ((float) i)) / ((float) (this.mQsMaxExpansionHeight - i)));
    }

    /* access modifiers changed from: protected */
    public boolean shouldExpandWhenNotFlinging() {
        boolean z = true;
        if (super.shouldExpandWhenNotFlinging()) {
            return true;
        }
        if (!this.mAllowExpandForSmallExpansion) {
            return false;
        }
        if (SystemClock.uptimeMillis() - this.mDownTime > 300) {
            z = false;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public float getOpeningHeight() {
        return this.mNotificationStackScroller.getOpeningHeight();
    }

    /* access modifiers changed from: private */
    public boolean handleQsTouch(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0 && getExpandedFraction() == 1.0f && this.mBarState != 1 && !this.mQsExpanded && this.mQsExpansionEnabled) {
            this.mQsTracking = true;
            this.mConflictingQsExpansionGesture = true;
            onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = motionEvent.getX();
            this.mInitialTouchX = motionEvent.getY();
        }
        if (!isFullyCollapsed()) {
            handleQsDown(motionEvent);
        }
        if (!this.mQsExpandImmediate && this.mQsTracking) {
            onQsTouch(motionEvent);
            if (!this.mConflictingQsExpansionGesture) {
                return true;
            }
        }
        if (actionMasked == 3 || actionMasked == 1) {
            this.mConflictingQsExpansionGesture = false;
        }
        if (actionMasked == 0 && isFullyCollapsed() && this.mQsExpansionEnabled) {
            this.mTwoFingerQsExpandPossible = true;
        }
        if (this.mTwoFingerQsExpandPossible && isOpenQsEvent(motionEvent) && motionEvent.getY(motionEvent.getActionIndex()) < ((float) this.mStatusBarMinHeight)) {
            this.mMetricsLogger.count("panel_open_qs", 1);
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
            requestPanelHeightUpdate();
            setListening(true);
        }
        return false;
    }

    private boolean isInQsArea(float f, float f2) {
        return f >= this.mQsFrame.getX() && f <= this.mQsFrame.getX() + ((float) this.mQsFrame.getWidth()) && (f2 <= this.mNotificationStackScroller.getBottomMostNotificationBottom() || f2 <= this.mQs.getView().getY() + ((float) this.mQs.getView().getHeight()));
    }

    private boolean isOpenQsEvent(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        int actionMasked = motionEvent.getActionMasked();
        boolean z = actionMasked == 5 && pointerCount == 2;
        boolean z2 = actionMasked == 0 && (motionEvent.isButtonPressed(32) || motionEvent.isButtonPressed(64));
        boolean z3 = actionMasked == 0 && (motionEvent.isButtonPressed(2) || motionEvent.isButtonPressed(4));
        if (z || z2 || z3) {
            return true;
        }
        return false;
    }

    private void handleQsDown(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && shouldQuickSettingsIntercept(motionEvent.getX(), motionEvent.getY(), -1.0f)) {
            this.mFalsingManager.onQsDown();
            this.mQsTracking = true;
            onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = motionEvent.getX();
            this.mInitialTouchX = motionEvent.getY();
            notifyExpandingFinished();
        }
    }

    public void startWaitingForOpenPanelGesture() {
        if (isFullyCollapsed()) {
            this.mExpectingSynthesizedDown = true;
            onTrackingStarted();
            updatePanelExpanded();
        }
    }

    public void stopWaitingForOpenPanelGesture(float f) {
        if (this.mExpectingSynthesizedDown) {
            this.mExpectingSynthesizedDown = false;
            maybeVibrateOnOpening();
            fling(f > 1.0f ? f * 1000.0f : 0.0f, true);
            onTrackingStopped(false);
        }
    }

    /* access modifiers changed from: protected */
    public boolean flingExpands(float f, float f2, float f3, float f4) {
        boolean flingExpands = super.flingExpands(f, f2, f3, f4);
        if (this.mQsExpansionAnimator != null) {
            return true;
        }
        return flingExpands;
    }

    /* access modifiers changed from: protected */
    public boolean shouldGestureWaitForTouchSlop() {
        boolean z = false;
        if (this.mExpectingSynthesizedDown) {
            this.mExpectingSynthesizedDown = false;
            return false;
        }
        if (isFullyCollapsed() || this.mBarState != 0) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public boolean shouldGestureIgnoreXTouchSlop(float f, float f2) {
        return !this.mAffordanceHelper.isOnAffordanceIcon(f, f2);
    }

    private void onQsTouch(MotionEvent motionEvent) {
        int findPointerIndex = motionEvent.findPointerIndex(this.mTrackingPointer);
        boolean z = false;
        if (findPointerIndex < 0) {
            this.mTrackingPointer = motionEvent.getPointerId(0);
            findPointerIndex = 0;
        }
        float y = motionEvent.getY(findPointerIndex);
        float x = motionEvent.getX(findPointerIndex);
        float f = y - this.mInitialTouchY;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    setQsExpansion(this.mInitialHeightOnTouch + f);
                    if (f >= ((float) getFalsingThreshold())) {
                        this.mQsTouchAboveFalsingThreshold = true;
                    }
                    trackMovement(motionEvent);
                    return;
                } else if (actionMasked != 3) {
                    if (actionMasked == 6) {
                        int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                        if (this.mTrackingPointer == pointerId) {
                            if (motionEvent.getPointerId(0) == pointerId) {
                                z = true;
                            }
                            float y2 = motionEvent.getY(z ? 1 : 0);
                            float x2 = motionEvent.getX(z);
                            this.mTrackingPointer = motionEvent.getPointerId(z);
                            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                            this.mInitialTouchY = y2;
                            this.mInitialTouchX = x2;
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
            this.mQsTracking = false;
            this.mTrackingPointer = -1;
            trackMovement(motionEvent);
            if (getQsExpansionFraction() != 0.0f || y >= this.mInitialTouchY) {
                if (motionEvent.getActionMasked() == 3) {
                    z = true;
                }
                flingQsWithCurrentVelocity(y, z);
            }
            VelocityTracker velocityTracker = this.mQsVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.recycle();
                this.mQsVelocityTracker = null;
                return;
            }
            return;
        }
        this.mQsTracking = true;
        this.mInitialTouchY = y;
        this.mInitialTouchX = x;
        onQsExpansionStarted();
        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
        initVelocityTracker();
        trackMovement(motionEvent);
    }

    private int getFalsingThreshold() {
        return (int) (((float) this.mQsFalsingThreshold) * (this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f));
    }

    /* access modifiers changed from: private */
    public void setOverScrolling(boolean z) {
        this.mStackScrollerOverscrolling = z;
        C0940QS qs = this.mQs;
        if (qs != null) {
            qs.setOverscrolling(z);
        }
    }

    /* access modifiers changed from: private */
    public void onQsExpansionStarted() {
        onQsExpansionStarted(0);
    }

    /* access modifiers changed from: protected */
    public void onQsExpansionStarted(int i) {
        cancelQsAnimation();
        cancelHeightAnimator();
        float f = this.mQsExpansionHeight - ((float) i);
        setQsExpansion(f);
        requestPanelHeightUpdate();
        this.mNotificationStackScroller.checkSnoozeLeavebehind();
        if (f == 0.0f) {
            this.mStatusBar.requestFaceAuth();
        }
    }

    private void setQsExpanded(boolean z) {
        if (this.mQsExpanded != z) {
            this.mQsExpanded = z;
            updateQsState();
            requestPanelHeightUpdate();
            this.mFalsingManager.setQsExpanded(z);
            this.mStatusBar.setQsExpanded(z);
            this.mNotificationContainerParent.setQsExpanded(z);
            this.mPulseExpansionHandler.setQsExpanded(z);
            this.mKeyguardBypassController.setQSExpanded(z);
        }
    }

    /* access modifiers changed from: private */
    public void maybeAnimateBottomAreaAlpha() {
        this.mBottomAreaShadeAlphaAnimator.cancel();
        if (this.mBarState == 2) {
            this.mBottomAreaShadeAlphaAnimator.start();
        } else {
            this.mBottomAreaShadeAlpha = 1.0f;
        }
    }

    /* access modifiers changed from: private */
    public void animateKeyguardStatusBarOut() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mKeyguardStatusBar.getAlpha(), 0.0f});
        ofFloat.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        ofFloat.setStartDelay(this.mKeyguardStateController.isKeyguardFadingAway() ? this.mKeyguardStateController.getKeyguardFadingAwayDelay() : 0);
        ofFloat.setDuration(this.mKeyguardStateController.isKeyguardFadingAway() ? this.mKeyguardStateController.getShortenedFadingAwayDuration() : 360);
        ofFloat.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                NotificationPanelViewController.this.mAnimateKeyguardStatusBarInvisibleEndRunnable.run();
            }
        });
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public void animateKeyguardStatusBarIn(long j) {
        this.mKeyguardStatusBar.setVisibility(0);
        this.mKeyguardStatusBar.setAlpha(0.0f);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        ofFloat.setDuration(j);
        ofFloat.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public void setKeyguardBottomAreaVisibility(int i, boolean z) {
        this.mKeyguardBottomArea.animate().cancel();
        if (z) {
            this.mKeyguardBottomArea.animate().alpha(0.0f).setStartDelay(this.mKeyguardStateController.getKeyguardFadingAwayDelay()).setDuration(this.mKeyguardStateController.getShortenedFadingAwayDuration()).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardBottomAreaInvisibleEndRunnable).start();
        } else if (i == 1 || i == 2) {
            this.mKeyguardBottomArea.setVisibility(0);
            this.mKeyguardBottomArea.setAlpha(1.0f);
        } else {
            this.mKeyguardBottomArea.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void setKeyguardStatusViewVisibility(int i, boolean z, boolean z2) {
        this.mKeyguardStatusView.animate().cancel();
        this.mKeyguardStatusViewAnimating = false;
        if ((!z && this.mBarState == 1 && i != 1) || z2) {
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.animate().alpha(0.0f).setStartDelay(0).setDuration(160).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardStatusViewGoneEndRunnable);
            if (z) {
                this.mKeyguardStatusView.animate().setStartDelay(this.mKeyguardStateController.getKeyguardFadingAwayDelay()).setDuration(this.mKeyguardStateController.getShortenedFadingAwayDuration()).start();
            }
        } else if (this.mBarState == 2 && i == 1) {
            this.mKeyguardStatusView.setVisibility(0);
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.setAlpha(0.0f);
            this.mKeyguardStatusView.animate().alpha(1.0f).setStartDelay(0).setDuration(320).setInterpolator(Interpolators.ALPHA_IN).withEndAction(this.mAnimateKeyguardStatusViewVisibleEndRunnable);
        } else if (i != 1) {
            this.mKeyguardStatusView.setVisibility(8);
            this.mKeyguardStatusView.setAlpha(1.0f);
        } else if (z) {
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.animate().alpha(0.0f).translationYBy(((float) (-getHeight())) * 0.05f).setInterpolator(Interpolators.FAST_OUT_LINEAR_IN).setDuration(125).setStartDelay(0).withEndAction(this.mAnimateKeyguardStatusViewInvisibleEndRunnable).start();
        } else {
            this.mKeyguardStatusView.setVisibility(0);
            this.mKeyguardStatusView.setAlpha(1.0f);
        }
    }

    /* access modifiers changed from: private */
    public void updateQsState() {
        this.mNotificationStackScroller.setQsExpanded(this.mQsExpanded);
        int i = 0;
        this.mNotificationStackScroller.setScrollingEnabled(this.mBarState != 1 && (!this.mQsExpanded || this.mQsExpansionFromOverscroll));
        updateEmptyShadeView();
        View view = this.mQsNavbarScrim;
        if (this.mBarState != 0 || !this.mQsExpanded || this.mStackScrollerOverscrolling || !this.mQsScrimEnabled) {
            i = 4;
        }
        view.setVisibility(i);
        KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
        if (keyguardUserSwitcher != null && this.mQsExpanded && !this.mStackScrollerOverscrolling) {
            keyguardUserSwitcher.hideIfNotSimple(true);
        }
        C0940QS qs = this.mQs;
        if (qs != null) {
            qs.setExpanded(this.mQsExpanded);
        }
    }

    /* access modifiers changed from: private */
    public void setQsExpansion(float f) {
        float min = Math.min(Math.max(f, (float) this.mQsMinExpansionHeight), (float) this.mQsMaxExpansionHeight);
        int i = this.mQsMaxExpansionHeight;
        this.mQsFullyExpanded = min == ((float) i) && i != 0;
        if (min > ((float) this.mQsMinExpansionHeight) && !this.mQsExpanded && !this.mStackScrollerOverscrolling && !this.mDozing) {
            setQsExpanded(true);
        } else if (min <= ((float) this.mQsMinExpansionHeight) && this.mQsExpanded) {
            setQsExpanded(false);
        }
        this.mQsExpansionHeight = min;
        updateQsExpansion();
        requestScrollerTopPaddingUpdate(false);
        updateHeaderKeyguardAlpha();
        int i2 = this.mBarState;
        if (i2 == 2 || i2 == 1) {
            updateKeyguardBottomAreaAlpha();
            updateBigClockAlpha();
        }
        if (this.mBarState == 0 && this.mQsExpanded && !this.mStackScrollerOverscrolling && this.mQsScrimEnabled) {
            this.mQsNavbarScrim.setAlpha(getQsExpansionFraction());
        }
        if (this.mAccessibilityManager.isEnabled()) {
            this.mView.setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        }
        if (!this.mFalsingManager.isUnlockingDisabled() && this.mQsFullyExpanded && this.mFalsingManager.shouldEnforceBouncer()) {
            this.mStatusBar.executeRunnableDismissingKeyguard(null, null, false, true, false);
        }
        for (int i3 = 0; i3 < this.mExpansionListeners.size(); i3++) {
            PanelExpansionListener panelExpansionListener = (PanelExpansionListener) this.mExpansionListeners.get(i3);
            int i4 = this.mQsMaxExpansionHeight;
            panelExpansionListener.onQsExpansionChanged(i4 != 0 ? this.mQsExpansionHeight / ((float) i4) : 0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void updateQsExpansion() {
        if (this.mQs != null) {
            float qsExpansionFraction = getQsExpansionFraction();
            this.mQs.setQsExpansion(qsExpansionFraction, getHeaderTranslation());
            this.mQs.getDesiredHeight();
            this.mQs.getQsMinExpansionHeight();
            this.mNotificationStackScroller.setQsExpansionFraction(qsExpansionFraction);
        }
    }

    /* access modifiers changed from: private */
    public String determineAccessibilityPaneTitle() {
        C0940QS qs = this.mQs;
        if (qs != null && qs.isCustomizing()) {
            return this.mResources.getString(C2017R$string.accessibility_desc_quick_settings_edit);
        }
        if (this.mQsExpansionHeight != 0.0f && this.mQsFullyExpanded) {
            return this.mResources.getString(C2017R$string.accessibility_desc_quick_settings);
        }
        if (this.mBarState == 1) {
            return this.mResources.getString(C2017R$string.accessibility_desc_lock_screen);
        }
        return this.mResources.getString(C2017R$string.accessibility_desc_notification_shade);
    }

    private float calculateQsTopPadding() {
        if (!this.mKeyguardShowing || (!this.mQsExpandImmediate && (!this.mIsExpanding || !this.mQsExpandedWhenExpandingStarted))) {
            ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
            if (valueAnimator != null) {
                return (float) Math.max(((Integer) valueAnimator.getAnimatedValue()).intValue(), getKeyguardNotificationStaticPadding());
            }
            if (this.mKeyguardShowing) {
                return MathUtils.lerp((float) getKeyguardNotificationStaticPadding(), (float) (this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding), getQsExpansionFraction());
            }
            return this.mQsExpansionHeight + ((float) this.mQsNotificationTopPadding);
        }
        int keyguardNotificationStaticPadding = getKeyguardNotificationStaticPadding();
        int i = this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding;
        if (this.mBarState == 1) {
            i = Math.max(keyguardNotificationStaticPadding, i);
        }
        return (float) ((int) MathUtils.lerp((float) this.mQsMinExpansionHeight, (float) i, getExpandedFraction()));
    }

    private int getKeyguardNotificationStaticPadding() {
        if (!this.mKeyguardShowing) {
            return 0;
        }
        if (!this.mKeyguardBypassController.getBypassEnabled()) {
            return this.mClockPositionResult.stackScrollerPadding;
        }
        int i = this.mHeadsUpInset;
        if (!this.mNotificationStackScroller.isPulseExpanding()) {
            return i;
        }
        return (int) MathUtils.lerp((float) i, (float) this.mClockPositionResult.stackScrollerPadding, this.mNotificationStackScroller.calculateAppearFractionBypass());
    }

    /* access modifiers changed from: protected */
    public void requestScrollerTopPaddingUpdate(boolean z) {
        this.mNotificationStackScroller.updateTopPadding(calculateQsTopPadding(), z);
        if (this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled()) {
            updateQsExpansion();
        }
    }

    /* access modifiers changed from: private */
    public void updateQSPulseExpansion() {
        C0940QS qs = this.mQs;
        if (qs != null) {
            qs.setShowCollapsedOnKeyguard(this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled() && this.mNotificationStackScroller.isPulseExpanding());
        }
    }

    private void trackMovement(MotionEvent motionEvent) {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
        }
    }

    private void initVelocityTracker() {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mQsVelocityTracker = VelocityTracker.obtain();
    }

    private float getCurrentQSVelocity() {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker == null) {
            return 0.0f;
        }
        velocityTracker.computeCurrentVelocity(1000);
        return this.mQsVelocityTracker.getYVelocity();
    }

    /* access modifiers changed from: private */
    public void cancelQsAnimation() {
        ValueAnimator valueAnimator = this.mQsExpansionAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void flingSettings(float f, int i) {
        flingSettings(f, i, null, false);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x001a  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0014  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void flingSettings(float r7, int r8, final java.lang.Runnable r9, boolean r10) {
        /*
            r6 = this;
            r0 = 0
            r1 = 1
            if (r8 == 0) goto L_0x000b
            if (r8 == r1) goto L_0x0008
            r2 = r0
            goto L_0x000e
        L_0x0008:
            int r2 = r6.mQsMinExpansionHeight
            goto L_0x000d
        L_0x000b:
            int r2 = r6.mQsMaxExpansionHeight
        L_0x000d:
            float r2 = (float) r2
        L_0x000e:
            float r3 = r6.mQsExpansionHeight
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x001a
            if (r9 == 0) goto L_0x0019
            r9.run()
        L_0x0019:
            return
        L_0x001a:
            r3 = 0
            if (r8 != 0) goto L_0x001f
            r8 = r1
            goto L_0x0020
        L_0x001f:
            r8 = r3
        L_0x0020:
            int r4 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r4 <= 0) goto L_0x0026
            if (r8 == 0) goto L_0x002c
        L_0x0026:
            int r4 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r4 >= 0) goto L_0x002f
            if (r8 == 0) goto L_0x002f
        L_0x002c:
            r7 = r0
            r0 = r1
            goto L_0x0030
        L_0x002f:
            r0 = r3
        L_0x0030:
            r4 = 2
            float[] r4 = new float[r4]
            float r5 = r6.mQsExpansionHeight
            r4[r3] = r5
            r4[r1] = r2
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r4)
            if (r10 == 0) goto L_0x004a
            android.view.animation.Interpolator r7 = com.android.systemui.Interpolators.TOUCH_RESPONSE
            r1.setInterpolator(r7)
            r2 = 368(0x170, double:1.82E-321)
            r1.setDuration(r2)
            goto L_0x0051
        L_0x004a:
            com.android.systemui.statusbar.FlingAnimationUtils r10 = r6.mFlingAnimationUtils
            float r3 = r6.mQsExpansionHeight
            r10.apply(r1, r3, r2, r7)
        L_0x0051:
            if (r0 == 0) goto L_0x0058
            r2 = 350(0x15e, double:1.73E-321)
            r1.setDuration(r2)
        L_0x0058:
            com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelViewController$9yAiDU7Cy0TecFB7S-umCCXv53w r7 = new com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelViewController$9yAiDU7Cy0TecFB7S-umCCXv53w
            r7.<init>()
            r1.addUpdateListener(r7)
            com.android.systemui.statusbar.phone.NotificationPanelViewController$14 r7 = new com.android.systemui.statusbar.phone.NotificationPanelViewController$14
            r7.<init>(r9)
            r1.addListener(r7)
            r1.start()
            r6.mQsExpansionAnimator = r1
            r6.mQsAnimatorExpand = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationPanelViewController.flingSettings(float, int, java.lang.Runnable, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$flingSettings$6 */
    public /* synthetic */ void lambda$flingSettings$6$NotificationPanelViewController(ValueAnimator valueAnimator) {
        setQsExpansion(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x005c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x006b A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldQuickSettingsIntercept(float r6, float r7, float r8) {
        /*
            r5 = this;
            boolean r0 = r5.mQsExpansionEnabled
            r1 = 0
            if (r0 == 0) goto L_0x006c
            boolean r0 = r5.mCollapsedOnDown
            if (r0 != 0) goto L_0x006c
            boolean r0 = r5.mKeyguardShowing
            if (r0 == 0) goto L_0x0016
            com.android.systemui.statusbar.phone.KeyguardBypassController r0 = r5.mKeyguardBypassController
            boolean r0 = r0.getBypassEnabled()
            if (r0 == 0) goto L_0x0016
            goto L_0x006c
        L_0x0016:
            boolean r0 = r5.mKeyguardShowing
            if (r0 != 0) goto L_0x0024
            com.android.systemui.plugins.qs.QS r0 = r5.mQs
            if (r0 != 0) goto L_0x001f
            goto L_0x0024
        L_0x001f:
            android.view.View r0 = r0.getHeader()
            goto L_0x0026
        L_0x0024:
            com.android.systemui.statusbar.phone.KeyguardStatusBarView r0 = r5.mKeyguardStatusBar
        L_0x0026:
            android.widget.FrameLayout r2 = r5.mQsFrame
            float r2 = r2.getX()
            int r2 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            r3 = 1
            if (r2 < 0) goto L_0x0057
            android.widget.FrameLayout r2 = r5.mQsFrame
            float r2 = r2.getX()
            android.widget.FrameLayout r4 = r5.mQsFrame
            int r4 = r4.getWidth()
            float r4 = (float) r4
            float r2 = r2 + r4
            int r2 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0057
            int r2 = r0.getTop()
            float r2 = (float) r2
            int r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0057
            int r0 = r0.getBottom()
            float r0 = (float) r0
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x0057
            r0 = r3
            goto L_0x0058
        L_0x0057:
            r0 = r1
        L_0x0058:
            boolean r2 = r5.mQsExpanded
            if (r2 == 0) goto L_0x006b
            if (r0 != 0) goto L_0x0069
            r0 = 0
            int r8 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r8 >= 0) goto L_0x006a
            boolean r5 = r5.isInQsArea(r6, r7)
            if (r5 == 0) goto L_0x006a
        L_0x0069:
            r1 = r3
        L_0x006a:
            return r1
        L_0x006b:
            return r0
        L_0x006c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationPanelViewController.shouldQuickSettingsIntercept(float, float, float):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean isScrolledToBottom() {
        if (isInSettings() || this.mBarState == 1 || this.mNotificationStackScroller.isScrolledToBottom()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public int getMaxPanelHeight() {
        if (!this.mKeyguardBypassController.getBypassEnabled() || this.mBarState != 1) {
            return getMaxPanelHeightNonBypass();
        }
        return getMaxPanelHeightBypass();
    }

    private int getMaxPanelHeightNonBypass() {
        int i;
        int i2 = this.mStatusBarMinHeight;
        if (this.mBarState != 1 && this.mNotificationStackScroller.getNotGoneChildCount() == 0) {
            i2 = Math.max(i2, (int) (((float) this.mQsMinExpansionHeight) + getOverExpansionAmount()));
        }
        if (this.mQsExpandImmediate || this.mQsExpanded || ((this.mIsExpanding && this.mQsExpandedWhenExpandingStarted) || this.mPulsing)) {
            i = calculatePanelHeightQsExpanded();
        } else {
            i = calculatePanelHeightShade();
        }
        int max = Math.max(i2, i);
        if (max == 0) {
            String str = PanelViewController.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("maxPanelHeight is 0. getOverExpansionAmount(): ");
            sb.append(getOverExpansionAmount());
            sb.append(", calculatePanelHeightQsExpanded: ");
            sb.append(calculatePanelHeightQsExpanded());
            sb.append(", calculatePanelHeightShade: ");
            sb.append(calculatePanelHeightShade());
            sb.append(", mStatusBarMinHeight = ");
            sb.append(this.mStatusBarMinHeight);
            sb.append(", mQsMinExpansionHeight = ");
            sb.append(this.mQsMinExpansionHeight);
            Log.wtf(str, sb.toString());
        }
        return max;
    }

    private int getMaxPanelHeightBypass() {
        int expandedClockPosition = this.mClockPositionAlgorithm.getExpandedClockPosition() + this.mKeyguardStatusView.getHeight();
        return this.mNotificationStackScroller.getVisibleNotificationCount() != 0 ? (int) (((float) expandedClockPosition) + (((float) this.mShelfHeight) / 2.0f) + (((float) this.mDarkIconSize) / 2.0f)) : expandedClockPosition;
    }

    public boolean isInSettings() {
        return this.mQsExpanded;
    }

    public boolean isExpanding() {
        return this.mIsExpanding;
    }

    /* access modifiers changed from: protected */
    public void onHeightUpdated(float f) {
        float f2;
        if ((!this.mQsExpanded || this.mQsExpandImmediate || (this.mIsExpanding && this.mQsExpandedWhenExpandingStarted)) && this.mStackScrollerMeasuringPass <= 2) {
            positionClockAndNotifications();
        }
        if (this.mQsExpandImmediate || (this.mQsExpanded && !this.mQsTracking && this.mQsExpansionAnimator == null && !this.mQsExpansionFromOverscroll)) {
            if (this.mKeyguardShowing) {
                f2 = f / ((float) getMaxPanelHeight());
            } else {
                float intrinsicPadding = (float) (this.mNotificationStackScroller.getIntrinsicPadding() + this.mNotificationStackScroller.getLayoutMinHeight());
                f2 = (f - intrinsicPadding) / (((float) calculatePanelHeightQsExpanded()) - intrinsicPadding);
            }
            int i = this.mQsMinExpansionHeight;
            setQsExpansion(((float) i) + (f2 * ((float) (this.mQsMaxExpansionHeight - i))));
        }
        updateExpandedHeight(f);
        updateHeader();
        updateNotificationTranslucency();
        updatePanelExpanded();
        updateGestureExclusionRect();
    }

    private void updatePanelExpanded() {
        boolean z = !isFullyCollapsed() || this.mExpectingSynthesizedDown;
        if (this.mPanelExpanded != z) {
            this.mHeadsUpManager.setIsPanelExpanded(z);
            this.mStatusBarTouchableRegionManager.setPanelExpanded(z);
            this.mStatusBar.setPanelExpanded(z);
            this.mPanelExpanded = z;
        }
    }

    private int calculatePanelHeightShade() {
        int height = (int) (((float) (this.mNotificationStackScroller.getHeight() - this.mNotificationStackScroller.getEmptyBottomMargin())) + this.mNotificationStackScroller.getTopPaddingOverflow());
        return this.mBarState == 1 ? Math.max(height, this.mClockPositionAlgorithm.getExpandedClockPosition() + this.mKeyguardStatusView.getHeight() + this.mNotificationStackScroller.getIntrinsicContentHeight()) : height;
    }

    private int calculatePanelHeightQsExpanded() {
        float height = (float) ((this.mNotificationStackScroller.getHeight() - this.mNotificationStackScroller.getEmptyBottomMargin()) - this.mNotificationStackScroller.getTopPadding());
        if (this.mNotificationStackScroller.getNotGoneChildCount() == 0 && this.mShowEmptyShadeView) {
            height = (float) this.mNotificationStackScroller.getEmptyShadeViewHeight();
        }
        int i = this.mQsMaxExpansionHeight;
        if (this.mKeyguardShowing) {
            i += this.mQsNotificationTopPadding;
        }
        ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
        if (valueAnimator != null) {
            i = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        }
        float max = ((float) Math.max(i, this.mBarState == 1 ? this.mClockPositionResult.stackScrollerPadding : 0)) + height + this.mNotificationStackScroller.getTopPaddingOverflow();
        if (max > ((float) this.mNotificationStackScroller.getHeight())) {
            max = Math.max((float) (i + this.mNotificationStackScroller.getLayoutMinHeight()), (float) this.mNotificationStackScroller.getHeight());
        }
        return (int) max;
    }

    /* access modifiers changed from: private */
    public void updateNotificationTranslucency() {
        float fadeoutAlpha = (!this.mClosingWithAlphaFadeOut || this.mExpandingFromHeadsUp || this.mHeadsUpManager.hasPinnedHeadsUp()) ? 1.0f : getFadeoutAlpha();
        if (this.mBarState == 1 && !this.mHintAnimationRunning && !this.mKeyguardBypassController.getBypassEnabled()) {
            fadeoutAlpha *= this.mClockPositionResult.clockAlpha;
        }
        this.mNotificationStackScroller.setAlpha(fadeoutAlpha);
    }

    private float getFadeoutAlpha() {
        if (this.mQsMinExpansionHeight == 0) {
            return 1.0f;
        }
        return (float) Math.pow((double) Math.max(0.0f, Math.min(getExpandedHeight() / ((float) this.mQsMinExpansionHeight), 1.0f)), 0.75d);
    }

    /* access modifiers changed from: protected */
    public float getOverExpansionAmount() {
        return this.mNotificationStackScroller.getCurrentOverScrollAmount(true);
    }

    /* access modifiers changed from: protected */
    public float getOverExpansionPixels() {
        return this.mNotificationStackScroller.getCurrentOverScrolledPixels(true);
    }

    /* access modifiers changed from: private */
    public void updateHeader() {
        if (this.mBarState == 1) {
            updateHeaderKeyguardAlpha();
        }
        updateQsExpansion();
    }

    /* access modifiers changed from: protected */
    public float getHeaderTranslation() {
        if (this.mBarState == 1 && !this.mKeyguardBypassController.getBypassEnabled()) {
            return (float) (-this.mQs.getQsMinExpansionHeight());
        }
        float calculateAppearFraction = this.mNotificationStackScroller.calculateAppearFraction(this.mExpandedHeight);
        float f = -this.mQsExpansionHeight;
        if (this.mKeyguardBypassController.getBypassEnabled() && isOnKeyguard() && this.mNotificationStackScroller.isPulseExpanding()) {
            if (this.mPulseExpansionHandler.isExpanding() || this.mPulseExpansionHandler.getLeavingLockscreen()) {
                calculateAppearFraction = this.mNotificationStackScroller.calculateAppearFractionBypass();
            } else {
                calculateAppearFraction = 0.0f;
            }
            f = (float) (-this.mQs.getQsMinExpansionHeight());
        }
        return Math.min(0.0f, MathUtils.lerp(f, 0.0f, Math.min(1.0f, calculateAppearFraction)) + this.mExpandOffset);
    }

    private float getKeyguardContentsAlpha() {
        float f;
        float f2;
        if (this.mBarState == 1) {
            f2 = getExpandedHeight();
            f = (float) (this.mKeyguardStatusBar.getHeight() + this.mNotificationsHeaderCollideDistance);
        } else {
            f2 = getExpandedHeight();
            f = (float) this.mKeyguardStatusBar.getHeight();
        }
        return (float) Math.pow((double) MathUtils.saturate(f2 / f), 0.75d);
    }

    /* access modifiers changed from: private */
    public void updateHeaderKeyguardAlpha() {
        if (this.mKeyguardShowing) {
            float min = Math.min(getKeyguardContentsAlpha(), 1.0f - Math.min(1.0f, getQsExpansionFraction() * 2.0f)) * this.mKeyguardStatusBarAnimateAlpha * (1.0f - this.mKeyguardHeadsUpShowingAmount);
            this.mKeyguardStatusBar.setAlpha(min);
            int i = 0;
            boolean z = (this.mFirstBypassAttempt && this.mUpdateMonitor.shouldListenForFace()) || this.mDelayShowingKeyguardStatusBar;
            KeyguardStatusBarView keyguardStatusBarView = this.mKeyguardStatusBar;
            if (min == 0.0f || this.mDozing || z) {
                i = 4;
            }
            keyguardStatusBarView.setVisibility(i);
        }
    }

    private void updateKeyguardBottomAreaAlpha() {
        float min = Math.min(MathUtils.map(isUnlockHintRunning() ? 0.0f : 0.95f, 1.0f, 0.0f, 1.0f, getExpandedFraction()), 1.0f - getQsExpansionFraction()) * this.mBottomAreaShadeAlpha;
        this.mKeyguardBottomArea.setAffordanceAlpha(min);
        this.mKeyguardBottomArea.setImportantForAccessibility(min == 0.0f ? 4 : 0);
        View ambientIndicationContainer = this.mStatusBar.getAmbientIndicationContainer();
        if (ambientIndicationContainer != null) {
            ambientIndicationContainer.setAlpha(min);
        }
    }

    private void updateBigClockAlpha() {
        this.mBigClockContainer.setAlpha(Math.min(MathUtils.map(isUnlockHintRunning() ? 0.0f : 0.95f, 1.0f, 0.0f, 1.0f, getExpandedFraction()), 1.0f - getQsExpansionFraction()));
    }

    /* access modifiers changed from: protected */
    public void onExpandingStarted() {
        super.onExpandingStarted();
        this.mNotificationStackScroller.onExpansionStarted();
        this.mIsExpanding = true;
        this.mQsExpandedWhenExpandingStarted = this.mQsFullyExpanded;
        if (this.mQsExpanded) {
            onQsExpansionStarted();
        }
        C0940QS qs = this.mQs;
        if (qs != null) {
            qs.setHeaderListening(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onExpandingFinished() {
        super.onExpandingFinished();
        this.mNotificationStackScroller.onExpansionStopped();
        this.mHeadsUpManager.onExpandingFinished();
        this.mIsExpanding = false;
        if (isFullyCollapsed()) {
            DejankUtils.postAfterTraversal(new Runnable() {
                public void run() {
                    NotificationPanelViewController.this.setListening(false);
                }
            });
            this.mView.postOnAnimation(new Runnable() {
                public void run() {
                    NotificationPanelViewController.this.mView.getParent().invalidateChild(NotificationPanelViewController.this.mView, NotificationPanelViewController.M_DUMMY_DIRTY_RECT);
                }
            });
        } else {
            setListening(true);
        }
        this.mQsExpandImmediate = false;
        this.mNotificationStackScroller.setShouldShowShelfOnly(false);
        this.mTwoFingerQsExpandPossible = false;
        notifyListenersTrackingHeadsUp(null);
        this.mExpandingFromHeadsUp = false;
        setPanelScrimMinFraction(0.0f);
    }

    private void notifyListenersTrackingHeadsUp(ExpandableNotificationRow expandableNotificationRow) {
        for (int i = 0; i < this.mTrackingHeadsUpListeners.size(); i++) {
            ((Consumer) this.mTrackingHeadsUpListeners.get(i)).accept(expandableNotificationRow);
        }
    }

    /* access modifiers changed from: private */
    public void setListening(boolean z) {
        this.mKeyguardStatusBar.setListening(z);
        C0940QS qs = this.mQs;
        if (qs != null) {
            qs.setListening(z);
        }
    }

    public void expand(boolean z) {
        super.expand(z);
        setListening(true);
    }

    /* access modifiers changed from: protected */
    public void setOverExpansion(float f, boolean z) {
        if (!this.mConflictingQsExpansionGesture && !this.mQsExpandImmediate && this.mBarState != 1) {
            this.mNotificationStackScroller.setOnHeightChangedListener(null);
            if (z) {
                this.mNotificationStackScroller.setOverScrolledPixels(f, true, false);
            } else {
                this.mNotificationStackScroller.setOverScrollAmount(f, true, false);
            }
            this.mNotificationStackScroller.setOnHeightChangedListener(this.mOnHeightChangedListener);
        }
    }

    /* access modifiers changed from: protected */
    public void onTrackingStarted() {
        this.mFalsingManager.onTrackingStarted(!this.mKeyguardStateController.canDismissLockScreen());
        super.onTrackingStarted();
        if (this.mQsFullyExpanded) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        int i = this.mBarState;
        if (i == 1 || i == 2) {
            this.mAffordanceHelper.animateHideLeftRightIcon();
        }
        this.mNotificationStackScroller.onPanelTrackingStarted();
    }

    /* access modifiers changed from: protected */
    public void onTrackingStopped(boolean z) {
        this.mFalsingManager.onTrackingStopped();
        super.onTrackingStopped(z);
        if (z) {
            this.mNotificationStackScroller.setOverScrolledPixels(0.0f, true, true);
        }
        this.mNotificationStackScroller.onPanelTrackingStopped();
        if (z) {
            int i = this.mBarState;
            if ((i == 1 || i == 2) && !this.mHintAnimationRunning) {
                this.mAffordanceHelper.reset(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateMaxHeadsUpTranslation() {
        this.mNotificationStackScroller.setHeadsUpBoundaries(getHeight(), this.mNavigationBarBottomHeight);
    }

    /* access modifiers changed from: protected */
    public void startUnlockHintAnimation() {
        if (this.mPowerManager.isPowerSaveMode()) {
            onUnlockHintStarted();
            onUnlockHintFinished();
            return;
        }
        super.startUnlockHintAnimation();
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintFinished() {
        super.onUnlockHintFinished();
        this.mNotificationStackScroller.setUnlockHintRunning(false);
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintStarted() {
        super.onUnlockHintStarted();
        this.mNotificationStackScroller.setUnlockHintRunning(true);
    }

    /* access modifiers changed from: protected */
    public float getPeekHeight() {
        int i;
        if (this.mNotificationStackScroller.getNotGoneChildCount() > 0) {
            i = this.mNotificationStackScroller.getPeekHeight();
        } else {
            i = this.mQsMinExpansionHeight;
        }
        return (float) i;
    }

    /* access modifiers changed from: protected */
    public boolean shouldUseDismissingAnimation() {
        return this.mBarState != 0 && (this.mKeyguardStateController.canDismissLockScreen() || !isTracking());
    }

    /* access modifiers changed from: protected */
    public boolean fullyExpandedClearAllVisible() {
        return this.mNotificationStackScroller.isFooterViewNotGone() && this.mNotificationStackScroller.isScrolledToBottom() && !this.mQsExpandImmediate;
    }

    /* access modifiers changed from: protected */
    public boolean isClearAllVisible() {
        return this.mNotificationStackScroller.isFooterViewContentVisible();
    }

    /* access modifiers changed from: protected */
    public int getClearAllHeight() {
        return this.mNotificationStackScroller.getFooterViewHeight();
    }

    /* access modifiers changed from: protected */
    public boolean isTrackingBlocked() {
        return (this.mConflictingQsExpansionGesture && this.mQsExpanded) || this.mBlockingExpansionForCurrentTouch;
    }

    public boolean isQsExpanded() {
        return this.mQsExpanded;
    }

    public boolean isQsDetailShowing() {
        return this.mQs.isShowingDetail();
    }

    public void closeQsDetail() {
        this.mQs.closeDetail();
    }

    public boolean isLaunchTransitionFinished() {
        return this.mIsLaunchTransitionFinished;
    }

    public boolean isLaunchTransitionRunning() {
        return this.mIsLaunchTransitionRunning;
    }

    public void setLaunchTransitionEndRunnable(Runnable runnable) {
        this.mLaunchAnimationEndRunnable = runnable;
    }

    /* access modifiers changed from: private */
    public void updateDozingVisibilities(boolean z) {
        this.mKeyguardBottomArea.setDozing(this.mDozing, z);
        if (!this.mDozing && z) {
            animateKeyguardStatusBarIn(360);
        }
    }

    public boolean isDozing() {
        return this.mDozing;
    }

    public void showEmptyShadeView(boolean z) {
        this.mShowEmptyShadeView = z;
        updateEmptyShadeView();
    }

    private void updateEmptyShadeView() {
        this.mNotificationStackScroller.updateEmptyShadeView(this.mShowEmptyShadeView && !this.mQsExpanded);
    }

    public void setQsScrimEnabled(boolean z) {
        boolean z2 = this.mQsScrimEnabled != z;
        this.mQsScrimEnabled = z;
        if (z2) {
            updateQsState();
        }
    }

    public void setKeyguardUserSwitcher(KeyguardUserSwitcher keyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = keyguardUserSwitcher;
    }

    public void onScreenTurningOn() {
        this.mKeyguardStatusView.dozeTimeTick();
    }

    /* access modifiers changed from: protected */
    public boolean onMiddleClicked() {
        int i = this.mBarState;
        if (i == 0) {
            this.mView.post(this.mPostCollapseRunnable);
            return false;
        } else if (i != 1) {
            if (i == 2 && !this.mQsExpanded) {
                this.mStatusBarStateController.setState(1);
            }
            return true;
        } else {
            if (!this.mDozingOnDown) {
                if (this.mKeyguardBypassController.getBypassEnabled()) {
                    this.mUpdateMonitor.requestFaceAuth();
                } else {
                    this.mLockscreenGestureLogger.write(188, 0, 0);
                    startUnlockHintAnimation();
                }
            }
            return true;
        }
    }

    public void setPanelAlpha(int i, boolean z) {
        if (this.mPanelAlpha != i) {
            this.mPanelAlpha = i;
            PropertyAnimator.setProperty(this.mView, this.mPanelAlphaAnimator, (float) i, i == 255 ? this.mPanelAlphaInPropertiesAnimator : this.mPanelAlphaOutPropertiesAnimator, z);
        }
    }

    public void setPanelAlphaEndAction(Runnable runnable) {
        this.mPanelAlphaEndAction = runnable;
    }

    /* access modifiers changed from: private */
    public void updateKeyguardStatusBarForHeadsUp() {
        boolean z = this.mKeyguardShowing && this.mHeadsUpAppearanceController.shouldBeVisible();
        if (this.mShowingKeyguardHeadsUp != z) {
            this.mShowingKeyguardHeadsUp = z;
            float f = 0.0f;
            if (this.mKeyguardShowing) {
                NotificationPanelView notificationPanelView = this.mView;
                AnimatableProperty animatableProperty = this.KEYGUARD_HEADS_UP_SHOWING_AMOUNT;
                if (z) {
                    f = 1.0f;
                }
                PropertyAnimator.setProperty(notificationPanelView, animatableProperty, f, KEYGUARD_HUN_PROPERTIES, true);
                return;
            }
            PropertyAnimator.applyImmediately(this.mView, this.KEYGUARD_HEADS_UP_SHOWING_AMOUNT, 0.0f);
        }
    }

    private void setKeyguardHeadsUpShowingAmount(float f) {
        this.mKeyguardHeadsUpShowingAmount = f;
        updateHeaderKeyguardAlpha();
    }

    private float getKeyguardHeadsUpShowingAmount() {
        return this.mKeyguardHeadsUpShowingAmount;
    }

    public void setHeadsUpAnimatingAway(boolean z) {
        this.mHeadsUpAnimatingAway = z;
        this.mNotificationStackScroller.setHeadsUpAnimatingAway(z);
        updateHeadsUpVisibility();
    }

    /* access modifiers changed from: private */
    public void updateHeadsUpVisibility() {
        ((PhoneStatusBarView) this.mBar).setHeadsUpVisible(this.mHeadsUpAnimatingAway || this.mHeadsUpPinnedMode);
    }

    public void setHeadsUpManager(HeadsUpManagerPhone headsUpManagerPhone) {
        super.setHeadsUpManager(headsUpManagerPhone);
        this.mHeadsUpTouchHelper = new HeadsUpTouchHelper(headsUpManagerPhone, this.mNotificationStackScroller.getHeadsUpCallback(), this);
    }

    public void setTrackedHeadsUp(ExpandableNotificationRow expandableNotificationRow) {
        if (expandableNotificationRow != null) {
            notifyListenersTrackingHeadsUp(expandableNotificationRow);
            this.mExpandingFromHeadsUp = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onClosingFinished() {
        super.onClosingFinished();
        resetHorizontalPanelPosition();
        setClosingWithAlphaFadeout(false);
    }

    private void setClosingWithAlphaFadeout(boolean z) {
        this.mClosingWithAlphaFadeOut = z;
        this.mNotificationStackScroller.forceNoOverlappingRendering(z);
    }

    /* access modifiers changed from: protected */
    public void updateVerticalPanelPosition(float f) {
        if (((float) this.mNotificationStackScroller.getWidth()) * 1.75f > ((float) this.mView.getWidth())) {
            resetHorizontalPanelPosition();
            return;
        }
        float width = (float) (this.mPositionMinSideMargin + (this.mNotificationStackScroller.getWidth() / 2));
        float width2 = (float) ((this.mView.getWidth() - this.mPositionMinSideMargin) - (this.mNotificationStackScroller.getWidth() / 2));
        if (Math.abs(f - ((float) (this.mView.getWidth() / 2))) < ((float) (this.mNotificationStackScroller.getWidth() / 4))) {
            f = (float) (this.mView.getWidth() / 2);
        }
        setHorizontalPanelTranslation(Math.min(width2, Math.max(width, f)) - ((float) (this.mNotificationStackScroller.getLeft() + (this.mNotificationStackScroller.getWidth() / 2))));
    }

    /* access modifiers changed from: private */
    public void resetHorizontalPanelPosition() {
        setHorizontalPanelTranslation(0.0f);
    }

    /* access modifiers changed from: protected */
    public void setHorizontalPanelTranslation(float f) {
        this.mNotificationStackScroller.setTranslationX(f);
        this.mQsFrame.setTranslationX(f);
        int size = this.mVerticalTranslationListener.size();
        for (int i = 0; i < size; i++) {
            ((Runnable) this.mVerticalTranslationListener.get(i)).run();
        }
    }

    /* access modifiers changed from: protected */
    public void updateExpandedHeight(float f) {
        if (this.mTracking) {
            this.mNotificationStackScroller.setExpandingVelocity(getCurrentExpandVelocity());
        }
        if (this.mKeyguardBypassController.getBypassEnabled() && isOnKeyguard()) {
            f = (float) getMaxPanelHeightNonBypass();
        }
        this.mNotificationStackScroller.setExpandedHeight(f);
        updateKeyguardBottomAreaAlpha();
        updateBigClockAlpha();
        updateStatusBarIcons();
    }

    public boolean isFullWidth() {
        return this.mIsFullWidth;
    }

    private void updateStatusBarIcons() {
        boolean z = (isPanelVisibleBecauseOfHeadsUp() || isFullWidth()) && getExpandedHeight() < getOpeningHeight();
        if (z && isOnKeyguard()) {
            z = false;
        }
        if (z != this.mShowIconsWhenExpanded) {
            this.mShowIconsWhenExpanded = z;
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
        }
    }

    /* access modifiers changed from: private */
    public boolean isOnKeyguard() {
        return this.mBarState == 1;
    }

    public void setPanelScrimMinFraction(float f) {
        this.mBar.panelScrimMinFractionChanged(f);
    }

    public void clearNotificationEffects() {
        this.mStatusBar.clearNotificationEffects();
    }

    /* access modifiers changed from: protected */
    public boolean isPanelVisibleBecauseOfHeadsUp() {
        return (this.mHeadsUpManager.hasPinnedHeadsUp() || this.mHeadsUpAnimatingAway) && this.mBarState == 0;
    }

    public void launchCamera(boolean z, int i) {
        boolean z2 = true;
        if (i == 1) {
            this.mLastCameraLaunchSource = "power_double_tap";
        } else if (i == 0) {
            this.mLastCameraLaunchSource = "wiggle_gesture";
        } else if (i == 2) {
            this.mLastCameraLaunchSource = "lift_to_launch_ml";
        } else {
            this.mLastCameraLaunchSource = "lockscreen_affordance";
        }
        if (!isFullyCollapsed()) {
            setLaunchingAffordance(true);
        } else {
            z = false;
        }
        this.mAffordanceHasPreview = this.mKeyguardBottomArea.getRightPreview() != null;
        KeyguardAffordanceHelper keyguardAffordanceHelper = this.mAffordanceHelper;
        if (this.mView.getLayoutDirection() != 1) {
            z2 = false;
        }
        keyguardAffordanceHelper.launchAffordance(z, z2);
    }

    public void onAffordanceLaunchEnded() {
        setLaunchingAffordance(false);
    }

    private void setLaunchingAffordance(boolean z) {
        this.mLaunchingAffordance = z;
        this.mKeyguardAffordanceHelperCallback.getLeftIcon().setLaunchingAffordance(z);
        this.mKeyguardAffordanceHelperCallback.getRightIcon().setLaunchingAffordance(z);
        this.mKeyguardBypassController.setLaunchingAffordance(z);
        Consumer<Boolean> consumer = this.mAffordanceLaunchListener;
        if (consumer != null) {
            consumer.accept(Boolean.valueOf(z));
        }
    }

    public boolean isLaunchingAffordanceWithPreview() {
        return this.mLaunchingAffordance && this.mAffordanceHasPreview;
    }

    public boolean canCameraGestureBeLaunched() {
        String str;
        boolean z = false;
        if (!this.mStatusBar.isCameraAllowedByAdmin()) {
            return false;
        }
        ResolveInfo resolveCameraIntent = this.mKeyguardBottomArea.resolveCameraIntent();
        if (resolveCameraIntent != null) {
            ActivityInfo activityInfo = resolveCameraIntent.activityInfo;
            if (activityInfo != null) {
                str = activityInfo.packageName;
                if (str != null && ((this.mBarState != 0 || !isForegroundApp(str)) && !this.mAffordanceHelper.isSwipingInProgress())) {
                    z = true;
                }
                return z;
            }
        }
        str = null;
        z = true;
        return z;
    }

    private boolean isForegroundApp(String str) {
        List runningTasks = this.mActivityManager.getRunningTasks(1);
        if (runningTasks.isEmpty() || !str.equals(((RunningTaskInfo) runningTasks.get(0)).topActivity.getPackageName())) {
            return false;
        }
        return true;
    }

    private void setGroupManager(NotificationGroupManager notificationGroupManager) {
        this.mGroupManager = notificationGroupManager;
    }

    public boolean hideStatusBarIconsWhenExpanded() {
        if (this.mLaunchingNotification) {
            return this.mHideIconsDuringNotificationLaunch;
        }
        HeadsUpAppearanceController headsUpAppearanceController = this.mHeadsUpAppearanceController;
        boolean z = false;
        if (headsUpAppearanceController != null && headsUpAppearanceController.shouldBeVisible()) {
            return false;
        }
        if (!isFullWidth() || !this.mShowIconsWhenExpanded) {
            z = true;
        }
        return z;
    }

    public void setTouchAndAnimationDisabled(boolean z) {
        super.setTouchAndAnimationDisabled(z);
        if (z && this.mAffordanceHelper.isSwipingInProgress() && !this.mIsLaunchTransitionRunning) {
            this.mAffordanceHelper.reset(false);
        }
        this.mNotificationStackScroller.setAnimationsEnabled(!z);
    }

    public void setDozing(boolean z, boolean z2, PointF pointF) {
        if (z != this.mDozing) {
            this.mView.setDozing(z);
            this.mDozing = z;
            this.mNotificationStackScroller.setDozing(z, z2, pointF);
            this.mKeyguardBottomArea.setDozing(this.mDozing, z2);
            if (z) {
                this.mBottomAreaShadeAlphaAnimator.cancel();
            }
            int i = this.mBarState;
            if (i == 1 || i == 2) {
                updateDozingVisibilities(z2);
            }
            this.mStatusBarStateController.setDozeAmount(z ? 1.0f : 0.0f, z2);
        }
    }

    public void setPulsing(boolean z) {
        this.mPulsing = z;
        boolean z2 = !this.mDozeParameters.getDisplayNeedsBlanking() && this.mDozeParameters.getAlwaysOn();
        if (z2) {
            this.mAnimateNextPositionUpdate = true;
        }
        if (!this.mPulsing && !this.mDozing) {
            this.mAnimateNextPositionUpdate = false;
        }
        this.mNotificationStackScroller.setPulsing(z, z2);
        this.mKeyguardStatusView.setPulsing(z);
    }

    public void setAmbientIndicationBottomPadding(int i) {
        if (this.mAmbientIndicationBottomPadding != i) {
            this.mAmbientIndicationBottomPadding = i;
            this.mStatusBar.updateKeyguardMaxNotifications();
        }
    }

    public void dozeTimeTick() {
        this.mKeyguardBottomArea.dozeTimeTick();
        this.mKeyguardStatusView.dozeTimeTick();
        if (this.mInterpolatedDarkAmount > 0.0f) {
            positionClockAndNotifications();
        }
    }

    public void setStatusAccessibilityImportance(int i) {
        this.mKeyguardStatusView.setImportantForAccessibility(i);
    }

    public KeyguardBottomAreaView getKeyguardBottomAreaView() {
        return this.mKeyguardBottomArea;
    }

    public void setUserSetupComplete(boolean z) {
        this.mUserSetupComplete = z;
        this.mKeyguardBottomArea.setUserSetupComplete(z);
    }

    public void applyExpandAnimationParams(ExpandAnimationParameters expandAnimationParameters) {
        this.mExpandOffset = expandAnimationParameters != null ? (float) expandAnimationParameters.getTopChange() : 0.0f;
        updateQsExpansion();
        if (expandAnimationParameters != null) {
            boolean z = expandAnimationParameters.getProgress(14, 100) == 0.0f;
            if (z != this.mHideIconsDuringNotificationLaunch) {
                this.mHideIconsDuringNotificationLaunch = z;
                if (!z) {
                    this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
                }
            }
        }
    }

    public void addTrackingHeadsUpListener(Consumer<ExpandableNotificationRow> consumer) {
        this.mTrackingHeadsUpListeners.add(consumer);
    }

    public void removeTrackingHeadsUpListener(Consumer<ExpandableNotificationRow> consumer) {
        this.mTrackingHeadsUpListeners.remove(consumer);
    }

    public void addVerticalTranslationListener(Runnable runnable) {
        this.mVerticalTranslationListener.add(runnable);
    }

    public void removeVerticalTranslationListener(Runnable runnable) {
        this.mVerticalTranslationListener.remove(runnable);
    }

    public void setHeadsUpAppearanceController(HeadsUpAppearanceController headsUpAppearanceController) {
        this.mHeadsUpAppearanceController = headsUpAppearanceController;
    }

    public void onBouncerPreHideAnimation() {
        setKeyguardStatusViewVisibility(this.mBarState, true, false);
    }

    public void blockExpansionForCurrentTouch() {
        this.mBlockingExpansionForCurrentTouch = this.mTracking;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        StringBuilder sb = new StringBuilder();
        sb.append("    gestureExclusionRect: ");
        sb.append(calculateGestureExclusionRect());
        printWriter.println(sb.toString());
        KeyguardStatusBarView keyguardStatusBarView = this.mKeyguardStatusBar;
        if (keyguardStatusBarView != null) {
            keyguardStatusBarView.dump(fileDescriptor, printWriter, strArr);
        }
        KeyguardStatusView keyguardStatusView = this.mKeyguardStatusView;
        if (keyguardStatusView != null) {
            keyguardStatusView.dump(fileDescriptor, printWriter, strArr);
        }
    }

    /* access modifiers changed from: private */
    public void updateShowEmptyShadeView() {
        boolean z = true;
        if (this.mBarState == 1 || this.mEntryManager.hasActiveNotifications()) {
            z = false;
        }
        showEmptyShadeView(z);
    }

    public Delegate createRemoteInputDelegate() {
        return this.mNotificationStackScroller.createDelegate();
    }

    public void updateNotificationViews() {
        this.mNotificationStackScroller.updateSectionBoundaries();
        this.mNotificationStackScroller.updateSpeedBumpIndex();
        this.mNotificationStackScroller.updateFooter();
        updateShowEmptyShadeView();
        this.mNotificationStackScroller.updateIconAreaViews();
    }

    public void onUpdateRowStates() {
        this.mNotificationStackScroller.onUpdateRowStates();
    }

    public boolean hasPulsingNotifications() {
        return this.mNotificationStackScroller.hasPulsingNotifications();
    }

    public ActivatableNotificationView getActivatedChild() {
        return this.mNotificationStackScroller.getActivatedChild();
    }

    public void setActivatedChild(ActivatableNotificationView activatableNotificationView) {
        this.mNotificationStackScroller.setActivatedChild(activatableNotificationView);
    }

    public void runAfterAnimationFinished(Runnable runnable) {
        this.mNotificationStackScroller.runAfterAnimationFinished(runnable);
    }

    public void initDependencies(StatusBar statusBar, NotificationGroupManager notificationGroupManager, NotificationShelf notificationShelf, NotificationIconAreaController notificationIconAreaController, ScrimController scrimController) {
        setStatusBar(statusBar);
        setGroupManager(this.mGroupManager);
        this.mNotificationStackScroller.setNotificationPanelController(this);
        this.mNotificationStackScroller.setIconAreaController(notificationIconAreaController);
        this.mNotificationStackScroller.setStatusBar(statusBar);
        this.mNotificationStackScroller.setGroupManager(notificationGroupManager);
        this.mNotificationStackScroller.setShelf(notificationShelf);
        this.mNotificationStackScroller.setScrimController(scrimController);
        updateShowEmptyShadeView();
    }

    public void showTransientIndication(int i) {
        this.mKeyguardIndicationController.showTransientIndication(i);
    }

    public void setOnReinflationListener(Runnable runnable) {
        this.mOnReinflationListener = runnable;
    }

    public void setAlpha(float f) {
        this.mView.setAlpha(f);
    }

    public ViewPropertyAnimator fadeOut(long j, long j2, Runnable runnable) {
        return this.mView.animate().alpha(0.0f).setStartDelay(j).setDuration(j2).setInterpolator(Interpolators.ALPHA_OUT).withLayer().withEndAction(runnable);
    }

    public void resetViewGroupFade() {
        ViewGroupFadeHelper.reset(this.mView);
    }

    public void addOnGlobalLayoutListener(OnGlobalLayoutListener onGlobalLayoutListener) {
        this.mView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public void removeOnGlobalLayoutListener(OnGlobalLayoutListener onGlobalLayoutListener) {
        this.mView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public MyOnHeadsUpChangedListener getOnHeadsUpChangedListener() {
        return this.mOnHeadsUpChangedListener;
    }

    public int getHeight() {
        return this.mView.getHeight();
    }

    public void onThemeChanged() {
        this.mConfigurationListener.onThemeChanged();
    }

    public OnLayoutChangeListener createLayoutChangeListener() {
        return new OnLayoutChangeListener();
    }

    public void setEmptyDragAmount(float f) {
        this.mExpansionCallback.setEmptyDragAmount(f);
    }

    /* access modifiers changed from: protected */
    public TouchHandler createTouchHandler() {
        return new TouchHandler() {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (NotificationPanelViewController.this.mBlockTouches || (NotificationPanelViewController.this.mQsFullyExpanded && NotificationPanelViewController.this.mQs.onInterceptTouchEvent(motionEvent))) {
                    return false;
                }
                NotificationPanelViewController.this.initDownStates(motionEvent);
                if (NotificationPanelViewController.this.mStatusBar.isBouncerShowing()) {
                    return true;
                }
                if (!NotificationPanelViewController.this.mBar.panelEnabled() || !NotificationPanelViewController.this.mHeadsUpTouchHelper.onInterceptTouchEvent(motionEvent)) {
                    NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                    if (!notificationPanelViewController.shouldQuickSettingsIntercept(notificationPanelViewController.mDownX, NotificationPanelViewController.this.mDownY, 0.0f) && NotificationPanelViewController.this.mPulseExpansionHandler.onInterceptTouchEvent(motionEvent)) {
                        return true;
                    }
                    if (NotificationPanelViewController.this.isFullyCollapsed() || !NotificationPanelViewController.this.onQsIntercept(motionEvent)) {
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                    return true;
                }
                NotificationPanelViewController.this.mMetricsLogger.count("panel_open", 1);
                NotificationPanelViewController.this.mMetricsLogger.count("panel_open_peek", 1);
                return true;
            }

            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean z = false;
                if (!NotificationPanelViewController.this.mBlockTouches && (NotificationPanelViewController.this.mQs == null || !NotificationPanelViewController.this.mQs.isCustomizing())) {
                    if (NotificationPanelViewController.this.mStatusBar.isBouncerShowingScrimmed()) {
                        return false;
                    }
                    if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        NotificationPanelViewController.this.mBlockingExpansionForCurrentTouch = false;
                    }
                    if (NotificationPanelViewController.this.mLastEventSynthesizedDown && motionEvent.getAction() == 1) {
                        NotificationPanelViewController.this.expand(true);
                    }
                    NotificationPanelViewController.this.initDownStates(motionEvent);
                    if (!NotificationPanelViewController.this.mIsExpanding) {
                        NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                        if (!notificationPanelViewController.shouldQuickSettingsIntercept(notificationPanelViewController.mDownX, NotificationPanelViewController.this.mDownY, 0.0f) && NotificationPanelViewController.this.mPulseExpansionHandler.onTouchEvent(motionEvent)) {
                            return true;
                        }
                    }
                    if (NotificationPanelViewController.this.mListenForHeadsUp && !NotificationPanelViewController.this.mHeadsUpTouchHelper.isTrackingHeadsUp() && NotificationPanelViewController.this.mHeadsUpTouchHelper.onInterceptTouchEvent(motionEvent)) {
                        NotificationPanelViewController.this.mMetricsLogger.count("panel_open_peek", 1);
                    }
                    boolean onTouchEvent = ((!NotificationPanelViewController.this.mIsExpanding || NotificationPanelViewController.this.mHintAnimationRunning) && !NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mBarState != 0 && !NotificationPanelViewController.this.mDozing) ? NotificationPanelViewController.this.mAffordanceHelper.onTouchEvent(motionEvent) | false : false;
                    if (NotificationPanelViewController.this.mOnlyAffordanceInThisMotion) {
                        return true;
                    }
                    boolean onTouchEvent2 = onTouchEvent | NotificationPanelViewController.this.mHeadsUpTouchHelper.onTouchEvent(motionEvent);
                    if (!NotificationPanelViewController.this.mHeadsUpTouchHelper.isTrackingHeadsUp() && NotificationPanelViewController.this.handleQsTouch(motionEvent)) {
                        return true;
                    }
                    if (motionEvent.getActionMasked() == 0 && NotificationPanelViewController.this.isFullyCollapsed()) {
                        NotificationPanelViewController.this.mMetricsLogger.count("panel_open", 1);
                        NotificationPanelViewController.this.updateVerticalPanelPosition(motionEvent.getX());
                        onTouchEvent2 = true;
                    }
                    boolean onTouch = super.onTouch(view, motionEvent) | onTouchEvent2;
                    if (!NotificationPanelViewController.this.mDozing || NotificationPanelViewController.this.mPulsing || onTouch) {
                        z = true;
                    }
                }
                return z;
            }
        };
    }

    /* access modifiers changed from: protected */
    public com.android.systemui.statusbar.phone.PanelViewController.OnConfigurationChangedListener createOnConfigurationChangedListener() {
        return new OnConfigurationChangedListener();
    }
}
