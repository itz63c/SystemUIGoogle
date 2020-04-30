package com.google.android.systemui.assist.uihints;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.metrics.LogMaker;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.DejankUtils;
import com.android.systemui.assist.AssistHandleViewController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistManager.UiController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController.ModeChangeThrottler;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;
import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import com.google.android.systemui.assist.uihints.edgelights.mode.Gone;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public class NgaUiController implements UiController, OnComputeInternalInsetsListener, StateListener {
    private static final boolean VERBOSE = (Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng"));
    private static final PathInterpolator mProgressInterpolator = new PathInterpolator(0.83f, 0.0f, 0.84f, 1.0f);
    private final Lazy<AssistManager> mAssistManager;
    private final AssistantPresenceHandler mAssistantPresenceHandler;
    private final AssistantWarmer mAssistantWarmer;
    private final ColorChangeHandler mColorChangeHandler;
    private long mColorMonitoringStart = 0;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final EdgeLightsController mEdgeLightsController;
    private final FlingVelocityWrapper mFlingVelocity;
    private final GlowController mGlowController;
    private boolean mHasDarkBackground = false;
    private final IconController mIconController;
    private ValueAnimator mInvocationAnimator;
    private boolean mInvocationInProgress = false;
    private AssistantInvocationLightsView mInvocationLightsView;
    private boolean mIsMonitoringColor = false;
    private float mLastInvocationProgress = 0.0f;
    private long mLastInvocationStartTime = 0;
    private final LightnessProvider mLightnessProvider;
    private ValueAnimator mNavBarAlphaAnimator;
    private float mNavBarDestinationAlpha = -1.0f;
    private final Lazy<NavigationBarController> mNavigationBarController;
    /* access modifiers changed from: private */
    public Runnable mPendingEdgeLightsModeChange;
    private PromptView mPromptView;
    private final ScrimController mScrimController;
    private boolean mShouldKeepWakeLock = false;
    private boolean mShowingAssistUi = false;
    private final TimeoutManager mTimeoutManager;
    private final TouchInsideHandler mTouchInsideHandler;
    private final TranscriptionController mTranscriptionController;
    /* access modifiers changed from: private */
    public final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private final OverlayUiHost mUiHost;
    private WakeLock mWakeLock;

    static {
        Class<NgaUiController> cls = NgaUiController.class;
    }

    public NgaUiController(Context context, TimeoutManager timeoutManager, AssistantPresenceHandler assistantPresenceHandler, TouchInsideHandler touchInsideHandler, ColorChangeHandler colorChangeHandler, OverlayUiHost overlayUiHost, EdgeLightsController edgeLightsController, GlowController glowController, ScrimController scrimController, TranscriptionController transcriptionController, IconController iconController, LightnessProvider lightnessProvider, StatusBarStateController statusBarStateController, Lazy<AssistManager> lazy, Lazy<NavigationBarController> lazy2, FlingVelocityWrapper flingVelocityWrapper, AssistantWarmer assistantWarmer) {
        Context context2 = context;
        ColorChangeHandler colorChangeHandler2 = colorChangeHandler;
        LightnessProvider lightnessProvider2 = lightnessProvider;
        this.mContext = context2;
        this.mColorChangeHandler = colorChangeHandler2;
        colorChangeHandler.onColorChange(false);
        this.mTimeoutManager = timeoutManager;
        this.mAssistantPresenceHandler = assistantPresenceHandler;
        this.mTouchInsideHandler = touchInsideHandler;
        this.mUiHost = overlayUiHost;
        this.mEdgeLightsController = edgeLightsController;
        this.mGlowController = glowController;
        this.mScrimController = scrimController;
        this.mTranscriptionController = transcriptionController;
        this.mIconController = iconController;
        this.mLightnessProvider = lightnessProvider2;
        this.mAssistManager = lazy;
        this.mNavigationBarController = lazy2;
        this.mFlingVelocity = flingVelocityWrapper;
        this.mAssistantWarmer = assistantWarmer;
        lightnessProvider2.setListener(new LightnessListener() {
            public final void onLightnessUpdate(float f) {
                NgaUiController.this.lambda$new$0$NgaUiController(f);
            }
        });
        this.mAssistantPresenceHandler.registerSysUiIsNgaUiChangeListener(new SysUiIsNgaUiChangeListener() {
            public final void onSysUiIsNgaUiChanged(boolean z) {
                NgaUiController.this.lambda$new$1$NgaUiController(z);
            }
        });
        this.mTouchInsideHandler.setFallback(new Runnable() {
            public final void run() {
                NgaUiController.this.closeNgaUi();
            }
        });
        this.mEdgeLightsController.setModeChangeThrottler(new ModeChangeThrottler() {
            public final void runWhenReady(String str, Runnable runnable) {
                NgaUiController.this.lambda$new$2$NgaUiController(str, runnable);
            }
        });
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(805306378, "Assist (NGA)");
        $$Lambda$NgaUiController$4GhIoaWGm6twYJc1tT2hhB1Tms r1 = new VisibilityListener() {
            public final void onVisibilityChanged(int i) {
                NgaUiController.this.lambda$new$3$NgaUiController(i);
            }
        };
        this.mGlowController.setVisibilityListener(r1);
        this.mScrimController.setVisibilityListener(r1);
        ViewGroup parent = this.mUiHost.getParent();
        AssistantInvocationLightsView assistantInvocationLightsView = (AssistantInvocationLightsView) parent.findViewById(C2011R$id.invocation_lights);
        this.mInvocationLightsView = assistantInvocationLightsView;
        assistantInvocationLightsView.setGoogleAssistant(true);
        this.mEdgeLightsController.addListener(this.mGlowController);
        this.mEdgeLightsController.addListener(this.mScrimController);
        this.mTranscriptionController.setListener(this.mScrimController);
        this.mPromptView = (PromptView) parent.findViewById(C2011R$id.prompt);
        dispatchHasDarkBackground();
        statusBarStateController.addCallback(this);
        refresh();
        this.mTimeoutManager.setTimeoutCallback(new TimeoutCallback(new Runnable() {
            public final void run() {
                NgaUiController.this.lambda$new$4$NgaUiController();
            }
        }) {
            public final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onTimeout() {
                this.f$0.run();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NgaUiController(float f) {
        if (this.mColorMonitoringStart > 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.mColorMonitoringStart;
            if (VERBOSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("Got lightness update (");
                sb.append(f);
                sb.append(") after ");
                sb.append(elapsedRealtime);
                sb.append(" ms");
                Log.d("NgaUiController", sb.toString());
            }
            this.mColorMonitoringStart = 0;
        }
        boolean z = true;
        this.mIconController.setHasAccurateLuma(true);
        this.mGlowController.setMedianLightness(f);
        this.mScrimController.setHasMedianLightness(f);
        this.mTranscriptionController.setHasAccurateBackground(true);
        if (f > 0.4f) {
            z = false;
        }
        setHasDarkBackground(z);
        refresh();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$NgaUiController(boolean z) {
        if (!z) {
            hide();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$NgaUiController(String str, Runnable runnable) {
        ValueAnimator valueAnimator = this.mInvocationAnimator;
        if (valueAnimator != null && valueAnimator.isStarted()) {
            this.mPendingEdgeLightsModeChange = runnable;
        } else if (this.mShowingAssistUi || !"FULL_LISTENING".equals(str)) {
            this.mPendingEdgeLightsModeChange = null;
            runnable.run();
        } else {
            this.mInvocationInProgress = true;
            onInvocationProgress(0, 1.0f);
            this.mPendingEdgeLightsModeChange = runnable;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$NgaUiController(int i) {
        refresh();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$NgaUiController() {
        if (this.mShowingAssistUi) {
            Log.e("NgaUiController", "Timed out");
            closeNgaUi();
            MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(4));
        }
    }

    /* access modifiers changed from: 0000 */
    public void onUiMessageReceived() {
        refresh();
    }

    private void refresh() {
        updateShowingAssistUi();
        updateShowingNavBar();
    }

    private void setHasDarkBackground(boolean z) {
        String str = "dark";
        String str2 = "light";
        String str3 = "NgaUiController";
        if (this.mHasDarkBackground == z) {
            if (VERBOSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("not switching; already ");
                if (!z) {
                    str = str2;
                }
                sb.append(str);
                Log.v(str3, sb.toString());
            }
            return;
        }
        this.mHasDarkBackground = z;
        this.mColorChangeHandler.onColorChange(z);
        if (VERBOSE) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("switching to ");
            if (!this.mHasDarkBackground) {
                str = str2;
            }
            sb2.append(str);
            Log.v(str3, sb2.toString());
        }
        dispatchHasDarkBackground();
    }

    private void dispatchHasDarkBackground() {
        this.mTranscriptionController.setHasDarkBackground(this.mHasDarkBackground);
        this.mIconController.setHasDarkBackground(this.mHasDarkBackground);
        this.mPromptView.setHasDarkBackground(this.mHasDarkBackground);
    }

    /* access modifiers changed from: private */
    public void closeNgaUi() {
        ((AssistManager) this.mAssistManager.get()).hideAssist();
        hide();
    }

    public void hide() {
        ValueAnimator valueAnimator = this.mInvocationAnimator;
        if (valueAnimator != null && valueAnimator.isStarted()) {
            this.mInvocationAnimator.cancel();
        }
        this.mInvocationInProgress = false;
        this.mTranscriptionController.onClear(false);
        this.mEdgeLightsController.setGone();
        this.mPendingEdgeLightsModeChange = null;
        this.mPromptView.disable();
        this.mIconController.onHideKeyboard();
        this.mIconController.onHideZerostate();
        refresh();
    }

    private void setColorMonitoringState(boolean z) {
        if (this.mIsMonitoringColor != z) {
            if (!z || !this.mScrimController.isVisible() || this.mScrimController.getSurfaceControllerHandle() != null) {
                this.mIsMonitoringColor = z;
                if (z) {
                    int rotatedHeight = (DisplayUtils.getRotatedHeight(this.mContext) - ((int) this.mContext.getResources().getDimension(C2009R$dimen.transcription_space_bottom_margin))) - DisplayUtils.convertSpToPx(20.0f, this.mContext);
                    Rect rect = new Rect(0, rotatedHeight - DisplayUtils.convertDpToPx(160.0f, this.mContext), DisplayUtils.getRotatedWidth(this.mContext), rotatedHeight);
                    this.mColorMonitoringStart = SystemClock.elapsedRealtime();
                    this.mLightnessProvider.enableColorMonitoring(true, rect, this.mScrimController.getSurfaceControllerHandle());
                } else {
                    this.mLightnessProvider.enableColorMonitoring(false, null, null);
                    this.mIconController.setHasAccurateLuma(false);
                    this.mScrimController.onLightnessInvalidated();
                    this.mTranscriptionController.setHasAccurateBackground(false);
                }
            }
        }
    }

    private void updateShowingAssistUi() {
        boolean z = false;
        boolean z2 = !(this.mEdgeLightsController.getMode() instanceof Gone) || this.mGlowController.isVisible() || this.mScrimController.isVisible() || this.mInvocationInProgress;
        if (z2 || this.mIconController.isVisible() || this.mIconController.isRequested()) {
            z = true;
        }
        setColorMonitoringState(z);
        if (this.mShowingAssistUi != z) {
            this.mShowingAssistUi = z;
            AssistHandleViewController assistHandlerViewController = ((NavigationBarController) this.mNavigationBarController.get()).getAssistHandlerViewController();
            if (assistHandlerViewController != null) {
                assistHandlerViewController.lambda$setAssistHintBlocked$1(z);
            }
            this.mUiHost.setAssistState(z, this.mEdgeLightsController.getMode() instanceof FullListening);
            if (z) {
                this.mUiHost.getParent().getViewTreeObserver().addOnComputeInternalInsetsListener(this);
            } else {
                this.mUiHost.getParent().getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
                ValueAnimator valueAnimator = this.mInvocationAnimator;
                if (valueAnimator != null && valueAnimator.isStarted()) {
                    this.mInvocationAnimator.cancel();
                }
            }
        }
        if (this.mShouldKeepWakeLock != z2) {
            this.mShouldKeepWakeLock = z2;
            if (z2) {
                this.mWakeLock.acquire();
            } else {
                this.mWakeLock.release();
            }
        }
    }

    private void updateShowingNavBar() {
        boolean z = !this.mInvocationInProgress && (this.mEdgeLightsController.getMode() instanceof Gone);
        float f = z ? 1.0f : 0.0f;
        NavigationBarView defaultNavigationBarView = ((NavigationBarController) this.mNavigationBarController.get()).getDefaultNavigationBarView();
        if (defaultNavigationBarView != null) {
            float alpha = defaultNavigationBarView.getAlpha();
            if (!(f == alpha || f == this.mNavBarDestinationAlpha)) {
                this.mNavBarDestinationAlpha = f;
                ValueAnimator valueAnimator = this.mNavBarAlphaAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                ObjectAnimator duration = ObjectAnimator.ofFloat(defaultNavigationBarView, View.ALPHA, new float[]{alpha, f}).setDuration((long) Math.abs((f - alpha) * 80.0f));
                this.mNavBarAlphaAnimator = duration;
                if (z) {
                    duration.setStartDelay(80);
                }
                this.mNavBarAlphaAnimator.start();
            }
        }
    }

    private float getAnimationProgress(int i, float f) {
        return i == 2 ? f * 0.95f : mProgressInterpolator.getInterpolation(f * 0.8f);
    }

    public void onInvocationProgress(int i, float f) {
        ValueAnimator valueAnimator = this.mInvocationAnimator;
        String str = "NgaUiController";
        if (valueAnimator != null && valueAnimator.isStarted()) {
            Log.w(str, "Already animating; ignoring invocation progress");
        } else if (this.mEdgeLightsController.getMode().preventsInvocations()) {
            if (VERBOSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("ignoring invocation; mode is ");
                sb.append(this.mEdgeLightsController.getMode().getClass().getSimpleName());
                Log.v(str, sb.toString());
            }
        } else {
            boolean z = this.mInvocationInProgress;
            int i2 = (f > 1.0f ? 1 : (f == 1.0f ? 0 : -1));
            if (i2 < 0) {
                this.mLastInvocationProgress = f;
                if (!z && f > 0.0f) {
                    this.mLastInvocationStartTime = SystemClock.uptimeMillis();
                }
                boolean z2 = f > 0.0f && i2 < 0;
                this.mInvocationInProgress = z2;
                if (!z2) {
                    this.mPromptView.disable();
                } else if (f < 0.9f && SystemClock.uptimeMillis() - this.mLastInvocationStartTime > 200) {
                    this.mPromptView.enable();
                }
                setProgress(i, getAnimationProgress(i, f));
            } else {
                ValueAnimator valueAnimator2 = this.mInvocationAnimator;
                if (valueAnimator2 == null || !valueAnimator2.isStarted()) {
                    this.mFlingVelocity.setVelocity(0.0f);
                    completeInvocation(i);
                }
            }
            this.mAssistantWarmer.onInvocationProgress(f);
            logInvocationProgressMetrics(i, f, z);
        }
    }

    private void logInvocationProgressMetrics(int i, float f, boolean z) {
        String str = "NgaUiController";
        if (f == 1.0f && VERBOSE) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invocation complete: type=");
            sb.append(i);
            Log.v(str, sb.toString());
        }
        if (!z && f > 0.0f) {
            if (VERBOSE) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Invocation started: type=");
                sb2.append(i);
                Log.v(str, sb2.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(4).setSubtype(((AssistManager) this.mAssistManager.get()).toLoggingSubType(i)));
        }
        ValueAnimator valueAnimator = this.mInvocationAnimator;
        if ((valueAnimator == null || !valueAnimator.isRunning()) && z && f == 0.0f) {
            if (VERBOSE) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Invocation cancelled: type=");
                sb3.append(i);
                Log.v(str, sb3.toString());
            }
            MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(1));
        }
    }

    public void onGestureCompletion(float f) {
        if (this.mEdgeLightsController.getMode().preventsInvocations()) {
            if (VERBOSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("ignoring invocation; mode is ");
                sb.append(this.mEdgeLightsController.getMode().getClass().getSimpleName());
                Log.v("NgaUiController", sb.toString());
            }
            return;
        }
        this.mFlingVelocity.setVelocity(f);
        completeInvocation(1);
        logInvocationProgressMetrics(1, 1.0f, this.mInvocationInProgress);
    }

    private void setProgress(int i, float f) {
        this.mInvocationLightsView.onInvocationProgress(f);
        this.mGlowController.setInvocationProgress(f);
        this.mScrimController.setInvocationProgress(f);
        this.mPromptView.onInvocationProgress(i, f);
        refresh();
    }

    private void completeInvocation(int i) {
        if (!this.mAssistantPresenceHandler.isSysUiNgaUi()) {
            setProgress(i, 0.0f);
            resetInvocationProgress();
            return;
        }
        this.mTouchInsideHandler.maybeSetGuarded();
        this.mTimeoutManager.resetTimeout();
        this.mPromptView.disable();
        ValueAnimator valueAnimator = this.mInvocationAnimator;
        if (valueAnimator != null && valueAnimator.isStarted()) {
            this.mInvocationAnimator.cancel();
        }
        float velocity = this.mFlingVelocity.getVelocity();
        float f = 3.0f;
        if (velocity != 0.0f) {
            f = MathUtils.constrain((-velocity) / 1.45f, 3.0f, 12.0f);
        }
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(f);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{approximateInverse(Float.valueOf(getAnimationProgress(i, this.mLastInvocationProgress)), new Function(overshootInterpolator) {
            public final /* synthetic */ OvershootInterpolator f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return Float.valueOf(Math.min(1.0f, this.f$0.getInterpolation(((Float) obj).floatValue())));
            }
        }), 1.0f});
        ofFloat.setDuration(600);
        ofFloat.setStartDelay(1);
        ofFloat.addUpdateListener(new AnimatorUpdateListener(i, overshootInterpolator) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ OvershootInterpolator f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NgaUiController.this.lambda$completeInvocation$6$NgaUiController(this.f$1, this.f$2, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled = false;

            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (!this.mCancelled) {
                    if (NgaUiController.this.mPendingEdgeLightsModeChange == null) {
                        NgaUiController.this.mEdgeLightsController.setFullListening();
                    } else {
                        NgaUiController.this.mPendingEdgeLightsModeChange.run();
                        NgaUiController.this.mPendingEdgeLightsModeChange = null;
                    }
                }
                NgaUiController.this.mUiHandler.post(new Runnable() {
                    public final void run() {
                        C18761.this.lambda$onAnimationEnd$0$NgaUiController$1();
                    }
                });
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onAnimationEnd$0 */
            public /* synthetic */ void lambda$onAnimationEnd$0$NgaUiController$1() {
                NgaUiController.this.resetInvocationProgress();
            }
        });
        this.mInvocationAnimator = ofFloat;
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$completeInvocation$6 */
    public /* synthetic */ void lambda$completeInvocation$6$NgaUiController(int i, OvershootInterpolator overshootInterpolator, ValueAnimator valueAnimator) {
        setProgress(i, overshootInterpolator.getInterpolation(((Float) valueAnimator.getAnimatedValue()).floatValue()));
    }

    /* access modifiers changed from: private */
    public void resetInvocationProgress() {
        this.mInvocationInProgress = false;
        this.mInvocationLightsView.hide();
        this.mLastInvocationProgress = 0.0f;
        this.mScrimController.setInvocationProgress(0.0f);
        refresh();
    }

    public void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo) {
        internalInsetsInfo.setTouchableInsets(3);
        Region region = new Region();
        this.mIconController.getTouchActionRegion().ifPresent(new Consumer(region) {
            public final /* synthetic */ Region f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.op((Region) obj, Op.UNION);
            }
        });
        Region region2 = new Region();
        Mode mode = this.mEdgeLightsController.getMode();
        if (!((mode instanceof FullListening) && ((FullListening) mode).isFakeForHalfListening())) {
            this.mGlowController.getTouchInsideRegion().ifPresent(new Consumer(region2) {
                public final /* synthetic */ Region f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.op((Region) obj, Op.UNION);
                }
            });
        }
        this.mScrimController.getTouchInsideRegion().ifPresent(new Consumer(region2) {
            public final /* synthetic */ Region f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.op((Region) obj, Op.UNION);
            }
        });
        $$Lambda$NgaUiController$LqQG_DaL0eMcmw4B_N5N3flgBSY r2 = new Consumer(region2) {
            public final /* synthetic */ Region f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                NgaUiController.lambda$onComputeInternalInsets$10(this.f$0, (Region) obj);
            }
        };
        this.mTranscriptionController.getTouchInsideRegion().ifPresent(r2);
        this.mTranscriptionController.getTouchActionRegion().ifPresent(r2);
        region.op(region2, Op.UNION);
        internalInsetsInfo.touchableRegion.set(region);
    }

    static /* synthetic */ void lambda$onComputeInternalInsets$10(Region region, Region region2) {
        if (region.isEmpty()) {
            region.op(region2, Op.UNION);
        } else if (region.quickReject(region2)) {
            Rect bounds = region.getBounds();
            bounds.top = region2.getBounds().top;
            region.set(bounds);
        } else {
            region.op(region2, Op.UNION);
        }
    }

    private float approximateInverse(Float f, Function<Float, Float> function) {
        ArrayList arrayList = new ArrayList((int) 200.0f);
        for (float f2 = 0.0f; f2 < 1.0f; f2 += 0.005f) {
            arrayList.add((Float) function.apply(Float.valueOf(f2)));
        }
        int binarySearch = Collections.binarySearch(arrayList, f);
        if (binarySearch < 0) {
            binarySearch = (binarySearch + 1) * -1;
        }
        return ((float) binarySearch) * 0.005f;
    }

    /* renamed from: onDozingChanged */
    public void lambda$onDozingChanged$11(boolean z) {
        if (Looper.myLooper() != this.mUiHandler.getLooper()) {
            this.mUiHandler.post(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NgaUiController.this.lambda$onDozingChanged$11$NgaUiController(this.f$1);
                }
            });
            return;
        }
        this.mScrimController.setIsDozing(z);
        if (z && this.mShowingAssistUi) {
            DejankUtils.whitelistIpcs((Runnable) new Runnable() {
                public final void run() {
                    NgaUiController.this.closeNgaUi();
                }
            });
        }
    }
}
