package com.android.keyguard;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.ColorExtractor.GradientColors;
import com.android.internal.colorextraction.ColorExtractor.OnColorsChangedListener;
import com.android.keyguard.clock.ClockManager;
import com.android.keyguard.clock.ClockManager.ClockChangedListener;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.Interpolators;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.ClockPlugin;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.util.wakelock.KeepAwakeAnimationListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.TimeZone;

public class KeyguardClockSwitch extends RelativeLayout {
    private ViewGroup mBigClockContainer;
    private final ClockVisibilityTransition mBoldClockTransition;
    private ClockChangedListener mClockChangedListener = new ClockChangedListener() {
        public final void onClockChanged(ClockPlugin clockPlugin) {
            KeyguardClockSwitch.this.setClockPlugin(clockPlugin);
        }
    };
    private final ClockManager mClockManager;
    private ClockPlugin mClockPlugin;
    private final ClockVisibilityTransition mClockTransition;
    /* access modifiers changed from: private */
    public TextClock mClockView;
    private TextClock mClockViewBold;
    private int[] mColorPalette;
    private final OnColorsChangedListener mColorsListener = new OnColorsChangedListener() {
        public final void onColorsChanged(ColorExtractor colorExtractor, int i) {
            KeyguardClockSwitch.this.lambda$new$0$KeyguardClockSwitch(colorExtractor, i);
        }
    };
    private float mDarkAmount;
    private boolean mHasVisibleNotifications;
    private View mKeyguardStatusArea;
    /* access modifiers changed from: private */
    public boolean mShowingHeader;
    private FrameLayout mSmallClockFrame;
    private final StateListener mStateListener = new StateListener() {
        public void onStateChanged(int i) {
            KeyguardClockSwitch.this.mStatusBarState = i;
            KeyguardClockSwitch.this.updateBigClockVisibility();
        }
    };
    /* access modifiers changed from: private */
    public int mStatusBarState;
    private final StatusBarStateController mStatusBarStateController;
    private boolean mSupportsDarkText;
    private final SysuiColorExtractor mSysuiColorExtractor;
    private final Transition mTransition;

    private class ClockVisibilityTransition extends Visibility {
        private float mCutoff;
        private float mScale;

        ClockVisibilityTransition() {
            setCutoff(1.0f);
            setScale(1.0f);
        }

        public ClockVisibilityTransition setCutoff(float f) {
            this.mCutoff = f;
            return this;
        }

        public ClockVisibilityTransition setScale(float f) {
            this.mScale = f;
            return this;
        }

        public void captureStartValues(TransitionValues transitionValues) {
            super.captureStartValues(transitionValues);
            captureVisibility(transitionValues);
        }

        public void captureEndValues(TransitionValues transitionValues) {
            super.captureStartValues(transitionValues);
            captureVisibility(transitionValues);
        }

        private void captureVisibility(TransitionValues transitionValues) {
            transitionValues.values.put("systemui:keyguard:visibility", Integer.valueOf(transitionValues.view.getVisibility()));
        }

        public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
            if (!viewGroup.isShown()) {
                return null;
            }
            return createAnimator(view, this.mCutoff, 4, ((Integer) transitionValues2.values.get("systemui:keyguard:visibility")).intValue(), this.mScale, 1.0f);
        }

        public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
            if (!viewGroup.isShown()) {
                return null;
            }
            return createAnimator(view, 1.0f - this.mCutoff, 0, ((Integer) transitionValues2.values.get("systemui:keyguard:visibility")).intValue(), 1.0f, this.mScale);
        }

        private Animator createAnimator(final View view, float f, final int i, final int i2, float f2, float f3) {
            view.setPivotY((float) (view.getHeight() - view.getPaddingBottom()));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            C0510x69bf1505 r1 = new AnimatorUpdateListener(f, view, i2, f2, f3) {
                public final /* synthetic */ float f$0;
                public final /* synthetic */ View f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ float f$3;
                public final /* synthetic */ float f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ClockVisibilityTransition.lambda$createAnimator$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
                }
            };
            ofFloat.addUpdateListener(r1);
            ofFloat.addListener(new KeepAwakeAnimationListener(this, KeyguardClockSwitch.this.getContext()) {
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    view.setVisibility(i);
                }

                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    animator.removeListener(this);
                }
            });
            addListener(new TransitionListenerAdapter(this) {
                public void onTransitionEnd(Transition transition) {
                    view.setVisibility(i2);
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                    transition.removeListener(this);
                }
            });
            return ofFloat;
        }

        static /* synthetic */ void lambda$createAnimator$0(float f, View view, int i, float f2, float f3, ValueAnimator valueAnimator) {
            float animatedFraction = valueAnimator.getAnimatedFraction();
            if (animatedFraction > f) {
                view.setVisibility(i);
            }
            float lerp = MathUtils.lerp(f2, f3, animatedFraction);
            view.setScaleX(lerp);
            view.setScaleY(lerp);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$KeyguardClockSwitch(ColorExtractor colorExtractor, int i) {
        if ((i & 2) != 0) {
            updateColors();
        }
    }

    public KeyguardClockSwitch(Context context, AttributeSet attributeSet, StatusBarStateController statusBarStateController, SysuiColorExtractor sysuiColorExtractor, ClockManager clockManager) {
        super(context, attributeSet);
        this.mStatusBarStateController = statusBarStateController;
        this.mStatusBarState = statusBarStateController.getState();
        this.mSysuiColorExtractor = sysuiColorExtractor;
        this.mClockManager = clockManager;
        ClockVisibilityTransition clockVisibilityTransition = new ClockVisibilityTransition();
        clockVisibilityTransition.setCutoff(0.3f);
        this.mClockTransition = clockVisibilityTransition;
        clockVisibilityTransition.addTarget(C2011R$id.default_clock_view);
        ClockVisibilityTransition clockVisibilityTransition2 = new ClockVisibilityTransition();
        clockVisibilityTransition2.setCutoff(0.7f);
        this.mBoldClockTransition = clockVisibilityTransition2;
        clockVisibilityTransition2.addTarget(C2011R$id.default_clock_view_bold);
        this.mTransition = new TransitionSet().setOrdering(0).addTransition(this.mClockTransition).addTransition(this.mBoldClockTransition).setDuration(275).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
    }

    public boolean hasCustomClock() {
        return this.mClockPlugin != null;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mClockView = (TextClock) findViewById(C2011R$id.default_clock_view);
        this.mClockViewBold = (TextClock) findViewById(C2011R$id.default_clock_view_bold);
        this.mSmallClockFrame = (FrameLayout) findViewById(C2011R$id.clock_view);
        this.mKeyguardStatusArea = findViewById(C2011R$id.keyguard_status_area);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mClockManager.addOnClockChangedListener(this.mClockChangedListener);
        this.mStatusBarStateController.addCallback(this.mStateListener);
        this.mSysuiColorExtractor.addOnColorsChangedListener(this.mColorsListener);
        updateColors();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mClockManager.removeOnClockChangedListener(this.mClockChangedListener);
        this.mStatusBarStateController.removeCallback(this.mStateListener);
        this.mSysuiColorExtractor.removeOnColorsChangedListener(this.mColorsListener);
        setClockPlugin(null);
    }

    /* access modifiers changed from: private */
    public void setClockPlugin(ClockPlugin clockPlugin) {
        ClockPlugin clockPlugin2 = this.mClockPlugin;
        if (clockPlugin2 != null) {
            View view = clockPlugin2.getView();
            if (view != null) {
                ViewParent parent = view.getParent();
                FrameLayout frameLayout = this.mSmallClockFrame;
                if (parent == frameLayout) {
                    frameLayout.removeView(view);
                }
            }
            ViewGroup viewGroup = this.mBigClockContainer;
            if (viewGroup != null) {
                viewGroup.removeAllViews();
                updateBigClockVisibility();
            }
            this.mClockPlugin.onDestroyView();
            this.mClockPlugin = null;
        }
        if (clockPlugin == null) {
            if (this.mShowingHeader) {
                this.mClockView.setVisibility(8);
                this.mClockViewBold.setVisibility(0);
            } else {
                this.mClockView.setVisibility(0);
                this.mClockViewBold.setVisibility(4);
            }
            this.mKeyguardStatusArea.setVisibility(0);
            return;
        }
        View view2 = clockPlugin.getView();
        if (view2 != null) {
            this.mSmallClockFrame.addView(view2, -1, new LayoutParams(-1, -2));
            this.mClockView.setVisibility(8);
            this.mClockViewBold.setVisibility(8);
        }
        View bigClockView = clockPlugin.getBigClockView();
        if (bigClockView != null) {
            ViewGroup viewGroup2 = this.mBigClockContainer;
            if (viewGroup2 != null) {
                viewGroup2.addView(bigClockView);
                updateBigClockVisibility();
            }
        }
        if (!clockPlugin.shouldShowStatusArea()) {
            this.mKeyguardStatusArea.setVisibility(8);
        }
        this.mClockPlugin = clockPlugin;
        clockPlugin.setStyle(getPaint().getStyle());
        this.mClockPlugin.setTextColor(getCurrentTextColor());
        this.mClockPlugin.setDarkAmount(this.mDarkAmount);
        int[] iArr = this.mColorPalette;
        if (iArr != null) {
            this.mClockPlugin.setColorPalette(this.mSupportsDarkText, iArr);
        }
    }

    public void setBigClockContainer(ViewGroup viewGroup) {
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (!(clockPlugin == null || viewGroup == null)) {
            View bigClockView = clockPlugin.getBigClockView();
            if (bigClockView != null) {
                viewGroup.addView(bigClockView);
            }
        }
        this.mBigClockContainer = viewGroup;
        updateBigClockVisibility();
    }

    public void setTextColor(int i) {
        this.mClockView.setTextColor(i);
        this.mClockViewBold.setTextColor(i);
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.setTextColor(i);
        }
    }

    public void setShowCurrentUserTime(boolean z) {
        this.mClockView.setShowCurrentUserTime(z);
        this.mClockViewBold.setShowCurrentUserTime(z);
    }

    public void setTextSize(int i, float f) {
        this.mClockView.setTextSize(i, f);
    }

    public void setFormat12Hour(CharSequence charSequence) {
        this.mClockView.setFormat12Hour(charSequence);
        this.mClockViewBold.setFormat12Hour(charSequence);
    }

    public void setFormat24Hour(CharSequence charSequence) {
        this.mClockView.setFormat24Hour(charSequence);
        this.mClockViewBold.setFormat24Hour(charSequence);
    }

    public void setDarkAmount(float f) {
        this.mDarkAmount = f;
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.setDarkAmount(f);
        }
        updateBigClockAlpha();
    }

    /* access modifiers changed from: 0000 */
    public void setHasVisibleNotifications(boolean z) {
        if (z != this.mHasVisibleNotifications) {
            this.mHasVisibleNotifications = z;
            if (this.mDarkAmount == 0.0f) {
                ViewGroup viewGroup = this.mBigClockContainer;
                if (viewGroup != null) {
                    TransitionManager.beginDelayedTransition(viewGroup, new Fade().setDuration(275).addTarget(this.mBigClockContainer));
                }
            }
            updateBigClockAlpha();
        }
    }

    public Paint getPaint() {
        return this.mClockView.getPaint();
    }

    public int getCurrentTextColor() {
        return this.mClockView.getCurrentTextColor();
    }

    public float getTextSize() {
        return this.mClockView.getTextSize();
    }

    /* access modifiers changed from: 0000 */
    public int getPreferredY(int i) {
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            return clockPlugin.getPreferredY(i);
        }
        return i / 2;
    }

    public void refresh() {
        this.mClockView.refreshTime();
        this.mClockViewBold.refreshTime();
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.onTimeTick();
        }
        if (Build.IS_DEBUGGABLE) {
            StringBuilder sb = new StringBuilder();
            sb.append("Updating clock: ");
            sb.append(this.mClockView.getText());
            Log.d("KeyguardClockSwitch", sb.toString());
        }
    }

    public void onTimeZoneChanged(TimeZone timeZone) {
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.onTimeZoneChanged(timeZone);
        }
    }

    private void updateColors() {
        GradientColors colors = this.mSysuiColorExtractor.getColors(2);
        this.mSupportsDarkText = colors.supportsDarkText();
        int[] colorPalette = colors.getColorPalette();
        this.mColorPalette = colorPalette;
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.setColorPalette(this.mSupportsDarkText, colorPalette);
        }
    }

    /* access modifiers changed from: private */
    public void updateBigClockVisibility() {
        if (this.mBigClockContainer != null) {
            int i = this.mStatusBarState;
            int i2 = 0;
            boolean z = true;
            if (!(i == 1 || i == 2)) {
                z = false;
            }
            if (!z || this.mBigClockContainer.getChildCount() == 0) {
                i2 = 8;
            }
            if (this.mBigClockContainer.getVisibility() != i2) {
                this.mBigClockContainer.setVisibility(i2);
            }
        }
    }

    private void updateBigClockAlpha() {
        if (this.mBigClockContainer != null) {
            float f = this.mHasVisibleNotifications ? this.mDarkAmount : 1.0f;
            this.mBigClockContainer.setAlpha(f);
            if (f == 0.0f) {
                this.mBigClockContainer.setVisibility(4);
            } else if (this.mBigClockContainer.getVisibility() == 4) {
                this.mBigClockContainer.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setKeyguardShowingHeader(boolean z) {
        if (this.mShowingHeader != z) {
            this.mShowingHeader = z;
            if (!hasCustomClock()) {
                float dimensionPixelSize = (float) this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.widget_small_font_size);
                float dimensionPixelSize2 = (float) this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.widget_big_font_size);
                this.mClockTransition.setScale(dimensionPixelSize / dimensionPixelSize2);
                this.mBoldClockTransition.setScale(dimensionPixelSize2 / dimensionPixelSize);
                TransitionManager.endTransitions((ViewGroup) this.mClockView.getParent());
                if (z) {
                    this.mTransition.addListener(new TransitionListenerAdapter() {
                        public void onTransitionEnd(Transition transition) {
                            super.onTransitionEnd(transition);
                            if (KeyguardClockSwitch.this.mShowingHeader) {
                                KeyguardClockSwitch.this.mClockView.setVisibility(8);
                            }
                            transition.removeListener(this);
                        }
                    });
                }
                TransitionManager.beginDelayedTransition((ViewGroup) this.mClockView.getParent(), this.mTransition);
                int i = 4;
                this.mClockView.setVisibility(z ? 4 : 0);
                TextClock textClock = this.mClockViewBold;
                if (z) {
                    i = 0;
                }
                textClock.setVisibility(i);
                int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(z ? C2009R$dimen.widget_vertical_padding_clock : C2009R$dimen.title_clock_padding);
                TextClock textClock2 = this.mClockView;
                textClock2.setPadding(textClock2.getPaddingLeft(), this.mClockView.getPaddingTop(), this.mClockView.getPaddingRight(), dimensionPixelSize3);
                TextClock textClock3 = this.mClockViewBold;
                textClock3.setPadding(textClock3.getPaddingLeft(), this.mClockViewBold.getPaddingTop(), this.mClockViewBold.getPaddingRight(), dimensionPixelSize3);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public ClockChangedListener getClockChangedListener() {
        return this.mClockChangedListener;
    }

    /* access modifiers changed from: 0000 */
    public StateListener getStateListener() {
        return this.mStateListener;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("KeyguardClockSwitch:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mClockPlugin: ");
        sb.append(this.mClockPlugin);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mClockView: ");
        sb2.append(this.mClockView);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mClockViewBold: ");
        sb3.append(this.mClockViewBold);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mSmallClockFrame: ");
        sb4.append(this.mSmallClockFrame);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  mBigClockContainer: ");
        sb5.append(this.mBigClockContainer);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  mKeyguardStatusArea: ");
        sb6.append(this.mKeyguardStatusArea);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("  mDarkAmount: ");
        sb7.append(this.mDarkAmount);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append("  mShowingHeader: ");
        sb8.append(this.mShowingHeader);
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append("  mSupportsDarkText: ");
        sb9.append(this.mSupportsDarkText);
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append("  mColorPalette: ");
        sb10.append(Arrays.toString(this.mColorPalette));
        printWriter.println(sb10.toString());
    }
}
