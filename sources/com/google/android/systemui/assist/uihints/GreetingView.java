package com.google.android.systemui.assist.uihints;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2009R$dimen;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;

public class GreetingView extends TextView implements TranscriptionSpaceView {
    /* access modifiers changed from: private */
    public final int START_DELTA;
    private final int TEXT_COLOR_DARK;
    private final int TEXT_COLOR_LIGHT;
    private AnimatorSet mAnimatorSet;
    private final SpannableStringBuilder mGreetingBuilder;
    /* access modifiers changed from: private */
    public float mMaxAlpha;
    private final ArrayList<StaggeredSpan> mSpans;

    private class StaggeredSpan extends CharacterStyle {
        private int mAlpha;
        private int mShift;

        private StaggeredSpan() {
            this.mShift = 0;
            this.mAlpha = 0;
        }

        public void updateDrawState(TextPaint textPaint) {
            textPaint.baselineShift -= this.mShift;
            textPaint.setAlpha(this.mAlpha);
            GreetingView.this.invalidate();
        }

        /* access modifiers changed from: 0000 */
        public void initAnimator(long j, OvershootInterpolator overshootInterpolator, AnimatorSet animatorSet) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setInterpolator(overshootInterpolator);
            ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StaggeredSpan.this.lambda$initAnimator$0$GreetingView$StaggeredSpan(valueAnimator);
                }
            });
            ofFloat.setDuration(400);
            ofFloat.setStartDelay(j);
            animatorSet.play(ofFloat);
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat2.addUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StaggeredSpan.this.lambda$initAnimator$1$GreetingView$StaggeredSpan(valueAnimator);
                }
            });
            ofFloat2.setDuration(100);
            ofFloat2.setStartDelay(j);
            animatorSet.play(ofFloat2);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initAnimator$0 */
        public /* synthetic */ void lambda$initAnimator$0$GreetingView$StaggeredSpan(ValueAnimator valueAnimator) {
            this.mShift = (int) (((float) GreetingView.this.START_DELTA) * ((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initAnimator$1 */
        public /* synthetic */ void lambda$initAnimator$1$GreetingView$StaggeredSpan(ValueAnimator valueAnimator) {
            this.mAlpha = (int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * GreetingView.this.mMaxAlpha);
        }
    }

    public GreetingView(Context context) {
        this(context, null);
    }

    public GreetingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GreetingView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public GreetingView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mGreetingBuilder = new SpannableStringBuilder();
        this.mSpans = new ArrayList<>();
        this.mAnimatorSet = new AnimatorSet();
        this.TEXT_COLOR_DARK = getResources().getColor(C2008R$color.transcription_text_dark);
        this.TEXT_COLOR_LIGHT = getResources().getColor(C2008R$color.transcription_text_light);
        this.START_DELTA = (int) getResources().getDimension(C2009R$dimen.assist_greeting_start_delta);
        this.mMaxAlpha = (float) Color.alpha(getCurrentTextColor());
    }

    public void onFontSizeChanged() {
        setTextSize(0, this.mContext.getResources().getDimension(C2009R$dimen.transcription_text_size));
    }

    /* access modifiers changed from: 0000 */
    public void setGreeting(String str) {
        setPadding(0, 0, 0, 0);
        setText(str);
        setVisibility(0);
    }

    /* access modifiers changed from: 0000 */
    public void setGreetingAnimated(String str, float f) {
        setPadding(0, 0, 0, -this.START_DELTA);
        setUpTextSpans(str);
        setText(this.mGreetingBuilder);
        animateIn(Math.abs(f));
    }

    public ListenableFuture<Void> hide(boolean z) {
        if (this.mAnimatorSet.isRunning()) {
            this.mAnimatorSet.cancel();
        }
        setVisibility(8);
        return Futures.immediateFuture(null);
    }

    public void setHasDarkBackground(boolean z) {
        setTextColor(z ? this.TEXT_COLOR_DARK : this.TEXT_COLOR_LIGHT);
        this.mMaxAlpha = (float) Color.alpha(getCurrentTextColor());
    }

    private void animateIn(float f) {
        if (this.mAnimatorSet.isRunning()) {
            Log.w("GreetingView", "Already animating in greeting view; ignoring");
            return;
        }
        this.mAnimatorSet = new AnimatorSet();
        float min = Math.min(10.0f, (f / 1.2f) + 3.0f);
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(min);
        long j = 0;
        Iterator it = this.mSpans.iterator();
        while (it.hasNext()) {
            ((StaggeredSpan) it.next()).initAnimator(j, overshootInterpolator, this.mAnimatorSet);
            j += 8;
        }
        setLayoutParams(min, overshootInterpolator);
        this.mAnimatorSet.start();
    }

    private void setLayoutParams(float f, OvershootInterpolator overshootInterpolator) {
        float convertSpToPx = (float) DisplayUtils.convertSpToPx(getResources().getDimension(C2009R$dimen.transcription_text_size), this.mContext);
        float interpolation = ((float) this.START_DELTA) * overshootInterpolator.getInterpolation(((2.0f * f) + 6.0f) / ((f * 6.0f) + 6.0f));
        LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = (int) (interpolation + convertSpToPx);
        }
        setVisibility(0);
        requestLayout();
    }

    private void setUpTextSpans(String str) {
        String[] split = str.split("\\s+");
        this.mGreetingBuilder.clear();
        this.mSpans.clear();
        this.mGreetingBuilder.append(str);
        int length = split.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            String str2 = split[i];
            StaggeredSpan staggeredSpan = new StaggeredSpan();
            int indexOf = str.indexOf(str2, i2);
            int length2 = str2.length() + indexOf;
            this.mGreetingBuilder.setSpan(staggeredSpan, indexOf, length2, 33);
            this.mSpans.add(staggeredSpan);
            i++;
            i2 = length2;
        }
    }
}
