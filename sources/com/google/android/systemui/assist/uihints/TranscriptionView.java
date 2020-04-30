package com.google.android.systemui.assist.uihints;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Shader.TileMode;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.PathInterpolator;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import androidx.core.math.MathUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.assist.DeviceConfigHelper;
import com.google.android.systemui.assist.uihints.StringUtils.StringStabilityInfo;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TranscriptionView extends TextView implements TranscriptionSpaceView {
    /* access modifiers changed from: private */
    public static final PathInterpolator INTERPOLATOR_SCROLL = new PathInterpolator(0.17f, 0.17f, 0.67f, 1.0f);
    private final float BUMPER_DISTANCE_END_PX;
    private final float BUMPER_DISTANCE_START_PX;
    private final float FADE_DISTANCE_END_PX;
    private final float FADE_DISTANCE_START_PX;
    private final int TEXT_COLOR_DARK;
    private final int TEXT_COLOR_LIGHT;
    private boolean mCardVisible;
    private DeviceConfigHelper mDeviceConfigHelper;
    /* access modifiers changed from: private */
    public int mDisplayWidthPx;
    private boolean mHasDarkBackground;
    private SettableFuture<Void> mHideFuture;
    private Matrix mMatrix;
    private int mRequestedTextColor;
    private float[] mStops;
    private ValueAnimator mTranscriptionAnimation;
    private TranscriptionAnimator mTranscriptionAnimator;
    /* access modifiers changed from: private */
    public SpannableStringBuilder mTranscriptionBuilder;
    private AnimatorSet mVisibilityAnimators;

    private class TranscriptionAnimator implements AnimatorUpdateListener {
        private float mDistance;
        private List<TranscriptionSpan> mSpans;
        private float mStartX;

        private TranscriptionAnimator() {
            this.mSpans = new ArrayList();
        }

        /* access modifiers changed from: 0000 */
        public void addSpan(TranscriptionSpan transcriptionSpan) {
            this.mSpans.add(transcriptionSpan);
        }

        /* access modifiers changed from: 0000 */
        public List<TranscriptionSpan> getSpans() {
            return this.mSpans;
        }

        /* access modifiers changed from: 0000 */
        public void clearSpans() {
            this.mSpans.clear();
        }

        /* access modifiers changed from: 0000 */
        public ValueAnimator createAnimator() {
            float measureText = TranscriptionView.this.getPaint().measureText(TranscriptionView.this.mTranscriptionBuilder.toString());
            this.mStartX = TranscriptionView.this.getX();
            this.mDistance = TranscriptionView.this.getFullyVisibleDistance(measureText) - this.mStartX;
            TranscriptionView.this.updateColor();
            long adaptiveDuration = TranscriptionView.this.getAdaptiveDuration(Math.abs(this.mDistance), (float) TranscriptionView.this.mDisplayWidthPx);
            long access$500 = measureText > ((float) TranscriptionView.this.mDisplayWidthPx) - TranscriptionView.this.getX() ? TranscriptionView.this.getFadeInDurationMs() + adaptiveDuration : adaptiveDuration;
            float f = this.mDistance * (((float) access$500) / ((float) adaptiveDuration));
            float f2 = this.mStartX;
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{f2, f2 + f}).setDuration(access$500);
            duration.setInterpolator(TranscriptionView.INTERPOLATOR_SCROLL);
            duration.addUpdateListener(this);
            return duration;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (Math.abs(floatValue - this.mStartX) < Math.abs(this.mDistance)) {
                TranscriptionView.this.setX(floatValue);
                TranscriptionView.this.updateColor();
            }
            this.mSpans.forEach(new Consumer(valueAnimator) {
                public final /* synthetic */ ValueAnimator f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((TranscriptionSpan) obj).setCurrentFraction(this.f$0.getAnimatedFraction());
                }
            });
            TranscriptionView.this.invalidate();
        }
    }

    private class TranscriptionSpan extends ReplacementSpan {
        private float mCurrentFraction = 0.0f;
        private float mStartFraction = 0.0f;

        TranscriptionSpan() {
        }

        TranscriptionSpan(TranscriptionSpan transcriptionSpan) {
            this.mStartFraction = MathUtils.clamp(transcriptionSpan.getCurrentFraction(), 0.0f, 1.0f);
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
            return (int) Math.ceil((double) TranscriptionView.this.getPaint().measureText(charSequence, 0, charSequence.length()));
        }

        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            Paint paint2 = paint;
            paint2.setAlpha((int) Math.ceil((double) (getAlpha() * 255.0f)));
            canvas.drawText(charSequence, i, i2, f, (float) i4, paint2);
        }

        private float getAlpha() {
            float f = this.mStartFraction;
            if (f == 1.0f) {
                return 1.0f;
            }
            return MathUtils.clamp((((1.0f - f) / 1.0f) * this.mCurrentFraction) + f, 0.0f, 1.0f);
        }

        /* access modifiers changed from: 0000 */
        public float getCurrentFraction() {
            return this.mCurrentFraction;
        }

        /* access modifiers changed from: 0000 */
        public void setCurrentFraction(float f) {
            this.mCurrentFraction = f;
        }
    }

    @VisibleForTesting
    static float interpolate(long j, long j2, float f) {
        return (((float) (j2 - j)) * f) + ((float) j);
    }

    public TranscriptionView(Context context) {
        this(context, null);
    }

    public TranscriptionView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TranscriptionView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TranscriptionView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTranscriptionBuilder = new SpannableStringBuilder();
        this.mVisibilityAnimators = new AnimatorSet();
        this.mHideFuture = null;
        this.mHasDarkBackground = false;
        this.mCardVisible = false;
        this.mRequestedTextColor = 0;
        this.mMatrix = new Matrix();
        this.mDisplayWidthPx = 0;
        this.mTranscriptionAnimator = new TranscriptionAnimator();
        initializeDeviceConfigHelper(new DeviceConfigHelper());
        this.BUMPER_DISTANCE_START_PX = context.getResources().getDimension(C2009R$dimen.zerostate_icon_left_margin) + context.getResources().getDimension(C2009R$dimen.zerostate_icon_tap_padding);
        this.BUMPER_DISTANCE_END_PX = context.getResources().getDimension(C2009R$dimen.keyboard_icon_right_margin) + context.getResources().getDimension(C2009R$dimen.keyboard_icon_tap_padding);
        this.FADE_DISTANCE_START_PX = context.getResources().getDimension(C2009R$dimen.zerostate_icon_size);
        this.FADE_DISTANCE_END_PX = context.getResources().getDimension(C2009R$dimen.keyboard_icon_size) / 2.0f;
        this.TEXT_COLOR_DARK = context.getResources().getColor(C2008R$color.transcription_text_dark);
        this.TEXT_COLOR_LIGHT = context.getResources().getColor(C2008R$color.transcription_text_light);
        updateDisplayWidth();
        setHasDarkBackground(!this.mHasDarkBackground);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public long getAdaptiveDuration(float f, float f2) {
        return Math.min(getDurationMaxMs(), Math.max(getDurationMinMs(), (long) (f * interpolate(getDurationRegularMs(), getDurationFastMs(), f / f2))));
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void initializeDeviceConfigHelper(DeviceConfigHelper deviceConfigHelper) {
        this.mDeviceConfigHelper = deviceConfigHelper;
    }

    private void updateDisplayWidth() {
        int rotatedWidth = DisplayUtils.getRotatedWidth(this.mContext);
        this.mDisplayWidthPx = rotatedWidth;
        float f = this.BUMPER_DISTANCE_START_PX;
        this.mStops = new float[]{f / ((float) rotatedWidth), (f + this.FADE_DISTANCE_START_PX) / ((float) rotatedWidth), ((((float) rotatedWidth) - this.FADE_DISTANCE_END_PX) - this.BUMPER_DISTANCE_END_PX) / ((float) rotatedWidth), 1.0f};
        updateColor();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        String spannableStringBuilder = this.mTranscriptionBuilder.toString();
        resetTranscription();
        setTranscription(spannableStringBuilder);
    }

    public ListenableFuture<Void> hide(boolean z) {
        SettableFuture<Void> settableFuture = this.mHideFuture;
        if (settableFuture != null && !settableFuture.isDone()) {
            return this.mHideFuture;
        }
        this.mHideFuture = SettableFuture.create();
        final $$Lambda$TranscriptionView$Qv69LoHEhmJSkqbPe36IZfPgiA r0 = new Runnable() {
            public final void run() {
                TranscriptionView.this.lambda$hide$0$TranscriptionView();
            }
        };
        if (!z) {
            if (this.mVisibilityAnimators.isRunning()) {
                this.mVisibilityAnimators.end();
            } else {
                r0.run();
            }
            return Futures.immediateFuture(null);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.mVisibilityAnimators = animatorSet;
        animatorSet.play(ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{getAlpha(), 0.0f}).setDuration(400));
        if (!this.mCardVisible) {
            this.mVisibilityAnimators.play(ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{getTranslationY(), (float) (getHeight() * -1)}).setDuration(700));
        }
        this.mVisibilityAnimators.addListener(new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                r0.run();
            }
        });
        this.mVisibilityAnimators.start();
        return this.mHideFuture;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hide$0 */
    public /* synthetic */ void lambda$hide$0$TranscriptionView() {
        setVisibility(8);
        setAlpha(1.0f);
        setTranslationY(0.0f);
        resetTranscription();
        this.mHideFuture.set(null);
    }

    public void setHasDarkBackground(boolean z) {
        if (z != this.mHasDarkBackground) {
            this.mHasDarkBackground = z;
            updateColor();
        }
    }

    public void setCardVisible(boolean z) {
        this.mCardVisible = z;
    }

    public void onFontSizeChanged() {
        setTextSize(0, this.mContext.getResources().getDimension(C2009R$dimen.transcription_text_size));
    }

    /* access modifiers changed from: 0000 */
    public void setTranscription(String str) {
        boolean z = false;
        setVisibility(0);
        updateDisplayWidth();
        ValueAnimator valueAnimator = this.mTranscriptionAnimation;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            z = true;
        }
        if (z) {
            this.mTranscriptionAnimation.cancel();
        }
        boolean isEmpty = this.mTranscriptionBuilder.toString().isEmpty();
        StringStabilityInfo calculateStringStabilityInfo = StringUtils.calculateStringStabilityInfo(this.mTranscriptionBuilder.toString(), str);
        this.mTranscriptionBuilder.clear();
        this.mTranscriptionBuilder.append(calculateStringStabilityInfo.stable);
        this.mTranscriptionBuilder.append(calculateStringStabilityInfo.unstable);
        int ceil = (int) Math.ceil((double) getPaint().measureText(this.mTranscriptionBuilder.toString()));
        LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = ceil;
            setLayoutParams(layoutParams);
        }
        updateColor();
        TranscriptionSpan transcriptionSpan = null;
        if (isEmpty || calculateStringStabilityInfo.stable.isEmpty()) {
            setUpSpans(calculateStringStabilityInfo.stable.length() + calculateStringStabilityInfo.unstable.length(), null);
            setX(getFullyVisibleDistance((float) ceil));
            updateColor();
            return;
        }
        int length = calculateStringStabilityInfo.stable.length();
        if (z) {
            String str2 = " ";
            if (!calculateStringStabilityInfo.stable.endsWith(str2) && !calculateStringStabilityInfo.unstable.startsWith(str2)) {
                String[] split = calculateStringStabilityInfo.stable.split("\\s+");
                if (split.length > 0) {
                    length -= split[split.length - 1].length();
                }
                List spans = this.mTranscriptionAnimator.getSpans();
                if (!spans.isEmpty()) {
                    transcriptionSpan = (TranscriptionSpan) spans.get(spans.size() - 1);
                }
            }
        }
        setUpSpans(length, transcriptionSpan);
        ValueAnimator createAnimator = this.mTranscriptionAnimator.createAnimator();
        this.mTranscriptionAnimation = createAnimator;
        createAnimator.start();
    }

    private void setUpSpans(int i, TranscriptionSpan transcriptionSpan) {
        TranscriptionSpan transcriptionSpan2;
        this.mTranscriptionAnimator.clearSpans();
        String spannableStringBuilder = this.mTranscriptionBuilder.toString();
        String substring = spannableStringBuilder.substring(i);
        if (substring.length() > 0) {
            int indexOf = spannableStringBuilder.indexOf(substring, i);
            int length = substring.length() + indexOf;
            if (transcriptionSpan == null) {
                transcriptionSpan2 = new TranscriptionSpan();
            } else {
                transcriptionSpan2 = new TranscriptionSpan(transcriptionSpan);
            }
            this.mTranscriptionBuilder.setSpan(transcriptionSpan2, indexOf, length, 33);
            this.mTranscriptionAnimator.addSpan(transcriptionSpan2);
        }
        setText(this.mTranscriptionBuilder, BufferType.SPANNABLE);
        updateColor();
    }

    /* access modifiers changed from: 0000 */
    public void setTranscriptionColor(int i) {
        this.mRequestedTextColor = i;
        updateColor();
    }

    /* access modifiers changed from: private */
    public float getFullyVisibleDistance(float f) {
        int i = this.mDisplayWidthPx;
        float f2 = (float) i;
        float f3 = this.BUMPER_DISTANCE_END_PX;
        float f4 = this.BUMPER_DISTANCE_START_PX + f3;
        float f5 = this.FADE_DISTANCE_END_PX;
        return f < f2 - ((f4 + f5) + this.FADE_DISTANCE_START_PX) ? (((float) i) - f) / 2.0f : ((((float) i) - f) - f5) - f3;
    }

    /* access modifiers changed from: private */
    public void updateColor() {
        int i = this.mRequestedTextColor;
        if (i == 0) {
            i = this.mHasDarkBackground ? this.TEXT_COLOR_DARK : this.TEXT_COLOR_LIGHT;
        }
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, (float) this.mDisplayWidthPx, 0.0f, new int[]{0, i, i, 0}, this.mStops, TileMode.CLAMP);
        this.mMatrix.setTranslate(-getTranslationX(), 0.0f);
        linearGradient.setLocalMatrix(this.mMatrix);
        getPaint().setShader(linearGradient);
        invalidate();
    }

    private void resetTranscription() {
        setTranscription("");
        this.mTranscriptionAnimator = new TranscriptionAnimator();
    }

    private long getDurationRegularMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_duration_per_px_regular", 4);
    }

    private long getDurationFastMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_duration_per_px_fast", 3);
    }

    private long getDurationMaxMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_max_duration", 400);
    }

    private long getDurationMinMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_min_duration", 20);
    }

    /* access modifiers changed from: private */
    public long getFadeInDurationMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_fade_in_duration", 50);
    }
}
