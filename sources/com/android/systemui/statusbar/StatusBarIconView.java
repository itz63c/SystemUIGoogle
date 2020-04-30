package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.util.TypedValue;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.core.graphics.ColorUtils;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.notification.NotificationIconDozeHelper;
import com.android.systemui.statusbar.notification.NotificationUtils;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.function.Consumer;

public class StatusBarIconView extends AnimatedImageView implements StatusIconDisplayable {
    private static final Property<StatusBarIconView, Float> DOT_APPEAR_AMOUNT = new FloatProperty<StatusBarIconView>("dot_appear_amount") {
        public void setValue(StatusBarIconView statusBarIconView, float f) {
            statusBarIconView.setDotAppearAmount(f);
        }

        public Float get(StatusBarIconView statusBarIconView) {
            return Float.valueOf(statusBarIconView.getDotAppearAmount());
        }
    };
    private static final Property<StatusBarIconView, Float> ICON_APPEAR_AMOUNT = new FloatProperty<StatusBarIconView>("iconAppearAmount") {
        public void setValue(StatusBarIconView statusBarIconView, float f) {
            statusBarIconView.setIconAppearAmount(f);
        }

        public Float get(StatusBarIconView statusBarIconView) {
            return Float.valueOf(statusBarIconView.getIconAppearAmount());
        }
    };
    private boolean mAlwaysScaleIcon;
    /* access modifiers changed from: private */
    public int mAnimationStartColor;
    private final boolean mBlocked;
    private int mCachedContrastBackgroundColor;
    /* access modifiers changed from: private */
    public ValueAnimator mColorAnimator;
    private final AnimatorUpdateListener mColorUpdater;
    private int mContrastedDrawableColor;
    private int mCurrentSetColor;
    private int mDecorColor;
    private int mDensity;
    /* access modifiers changed from: private */
    public ObjectAnimator mDotAnimator;
    private float mDotAppearAmount;
    private final Paint mDotPaint;
    private float mDotRadius;
    private float mDozeAmount;
    private final NotificationIconDozeHelper mDozer;
    private int mDrawableColor;
    private StatusBarIcon mIcon;
    private float mIconAppearAmount;
    /* access modifiers changed from: private */
    public ObjectAnimator mIconAppearAnimator;
    private int mIconColor;
    private float mIconScale;
    private boolean mIncreasedSize;
    private Runnable mLayoutRunnable;
    private float[] mMatrix;
    private ColorMatrixColorFilter mMatrixColorFilter;
    private boolean mNightMode;
    private StatusBarNotification mNotification;
    private Drawable mNumberBackground;
    private Paint mNumberPain;
    private String mNumberText;
    private int mNumberX;
    private int mNumberY;
    private Runnable mOnDismissListener;
    private OnVisibilityChangedListener mOnVisibilityChangedListener;
    @ExportedProperty
    private String mSlot;
    private int mStaticDotRadius;
    private int mStatusBarIconDrawingSize;
    private int mStatusBarIconDrawingSizeIncreased;
    private int mStatusBarIconSize;
    private float mSystemIconDefaultScale;
    private float mSystemIconDesiredHeight;
    private float mSystemIconIntrinsicHeight;
    private boolean mTintIcons;
    private int mVisibleState;

    public interface OnVisibilityChangedListener {
        void onVisibilityChanged(int i);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setIsInShelf(boolean z) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$StatusBarIconView(ValueAnimator valueAnimator) {
        setColorInternal(NotificationUtils.interpolateColors(this.mAnimationStartColor, this.mIconColor, valueAnimator.getAnimatedFraction()));
    }

    public StatusBarIconView(Context context, String str, StatusBarNotification statusBarNotification) {
        this(context, str, statusBarNotification, false);
    }

    public StatusBarIconView(Context context, String str, StatusBarNotification statusBarNotification, boolean z) {
        super(context);
        this.mSystemIconDesiredHeight = 15.0f;
        this.mSystemIconIntrinsicHeight = 17.0f;
        this.mSystemIconDefaultScale = 15.0f / 17.0f;
        boolean z2 = true;
        this.mStatusBarIconDrawingSizeIncreased = 1;
        this.mStatusBarIconDrawingSize = 1;
        this.mStatusBarIconSize = 1;
        this.mIconScale = 1.0f;
        this.mDotPaint = new Paint(1);
        this.mVisibleState = 0;
        this.mIconAppearAmount = 1.0f;
        this.mCurrentSetColor = 0;
        this.mAnimationStartColor = 0;
        this.mColorUpdater = new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                StatusBarIconView.this.lambda$new$0$StatusBarIconView(valueAnimator);
            }
        };
        this.mCachedContrastBackgroundColor = 0;
        this.mTintIcons = true;
        this.mDozer = new NotificationIconDozeHelper(context);
        this.mBlocked = z;
        this.mSlot = str;
        Paint paint = new Paint();
        this.mNumberPain = paint;
        paint.setTextAlign(Align.CENTER);
        this.mNumberPain.setColor(context.getColor(C2010R$drawable.notification_number_text_color));
        this.mNumberPain.setAntiAlias(true);
        setNotification(statusBarNotification);
        setScaleType(ScaleType.CENTER);
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        if ((context.getResources().getConfiguration().uiMode & 48) != 32) {
            z2 = false;
        }
        this.mNightMode = z2;
        initializeDecorColor();
        reloadDimens();
        maybeUpdateIconScaleDimens();
    }

    public StatusBarIconView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSystemIconDesiredHeight = 15.0f;
        this.mSystemIconIntrinsicHeight = 17.0f;
        this.mSystemIconDefaultScale = 15.0f / 17.0f;
        this.mStatusBarIconDrawingSizeIncreased = 1;
        this.mStatusBarIconDrawingSize = 1;
        this.mStatusBarIconSize = 1;
        this.mIconScale = 1.0f;
        this.mDotPaint = new Paint(1);
        this.mVisibleState = 0;
        this.mIconAppearAmount = 1.0f;
        this.mCurrentSetColor = 0;
        this.mAnimationStartColor = 0;
        this.mColorUpdater = new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                StatusBarIconView.this.lambda$new$0$StatusBarIconView(valueAnimator);
            }
        };
        this.mCachedContrastBackgroundColor = 0;
        this.mTintIcons = true;
        this.mDozer = new NotificationIconDozeHelper(context);
        this.mBlocked = false;
        this.mAlwaysScaleIcon = true;
        reloadDimens();
        maybeUpdateIconScaleDimens();
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
    }

    private void maybeUpdateIconScaleDimens() {
        if (this.mNotification != null || this.mAlwaysScaleIcon) {
            updateIconScaleForNotifications();
        } else {
            updateIconScaleForSystemIcons();
        }
    }

    private void updateIconScaleForNotifications() {
        this.mIconScale = ((float) (this.mIncreasedSize ? this.mStatusBarIconDrawingSizeIncreased : this.mStatusBarIconDrawingSize)) / ((float) this.mStatusBarIconSize);
        updatePivot();
    }

    private void updateIconScaleForSystemIcons() {
        float iconHeight = getIconHeight();
        if (iconHeight != 0.0f) {
            this.mIconScale = this.mSystemIconDesiredHeight / iconHeight;
        } else {
            this.mIconScale = this.mSystemIconDefaultScale;
        }
    }

    private float getIconHeight() {
        if (getDrawable() != null) {
            return (float) getDrawable().getIntrinsicHeight();
        }
        return this.mSystemIconIntrinsicHeight;
    }

    public float getIconScaleIncreased() {
        return ((float) this.mStatusBarIconDrawingSizeIncreased) / ((float) this.mStatusBarIconDrawingSize);
    }

    public float getIconScale() {
        return this.mIconScale;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.densityDpi;
        if (i != this.mDensity) {
            this.mDensity = i;
            reloadDimens();
            updateDrawable();
            maybeUpdateIconScaleDimens();
        }
        boolean z = (configuration.uiMode & 48) == 32;
        if (z != this.mNightMode) {
            this.mNightMode = z;
            initializeDecorColor();
        }
    }

    private void reloadDimens() {
        boolean z = this.mDotRadius == ((float) this.mStaticDotRadius);
        Resources resources = getResources();
        this.mStaticDotRadius = resources.getDimensionPixelSize(C2009R$dimen.overflow_dot_radius);
        this.mStatusBarIconSize = resources.getDimensionPixelSize(C2009R$dimen.status_bar_icon_size);
        this.mStatusBarIconDrawingSizeIncreased = resources.getDimensionPixelSize(C2009R$dimen.status_bar_icon_drawing_size_dark);
        this.mStatusBarIconDrawingSize = resources.getDimensionPixelSize(C2009R$dimen.status_bar_icon_drawing_size);
        if (z) {
            this.mDotRadius = (float) this.mStaticDotRadius;
        }
        this.mSystemIconDesiredHeight = resources.getDimension(17105467);
        float dimension = resources.getDimension(17105466);
        this.mSystemIconIntrinsicHeight = dimension;
        this.mSystemIconDefaultScale = this.mSystemIconDesiredHeight / dimension;
    }

    public void setNotification(StatusBarNotification statusBarNotification) {
        this.mNotification = statusBarNotification;
        if (statusBarNotification != null) {
            setContentDescription(statusBarNotification.getNotification());
        }
        maybeUpdateIconScaleDimens();
    }

    public boolean equalIcons(Icon icon, Icon icon2) {
        boolean z = true;
        if (icon == icon2) {
            return true;
        }
        if (icon.getType() != icon2.getType()) {
            return false;
        }
        int type = icon.getType();
        if (type == 2) {
            if (!icon.getResPackage().equals(icon2.getResPackage()) || icon.getResId() != icon2.getResId()) {
                z = false;
            }
            return z;
        } else if (type == 4 || type == 6) {
            return icon.getUriString().equals(icon2.getUriString());
        } else {
            return false;
        }
    }

    public boolean set(StatusBarIcon statusBarIcon) {
        StatusBarIcon statusBarIcon2 = this.mIcon;
        int i = 0;
        boolean z = statusBarIcon2 != null && equalIcons(statusBarIcon2.icon, statusBarIcon.icon);
        boolean z2 = z && this.mIcon.iconLevel == statusBarIcon.iconLevel;
        StatusBarIcon statusBarIcon3 = this.mIcon;
        boolean z3 = statusBarIcon3 != null && statusBarIcon3.visible == statusBarIcon.visible;
        StatusBarIcon statusBarIcon4 = this.mIcon;
        boolean z4 = statusBarIcon4 != null && statusBarIcon4.number == statusBarIcon.number;
        this.mIcon = statusBarIcon.clone();
        setContentDescription(statusBarIcon.contentDescription);
        if (!z) {
            if (!updateDrawable(false)) {
                return false;
            }
            setTag(C2011R$id.icon_is_grayscale, null);
            maybeUpdateIconScaleDimens();
        }
        if (!z2) {
            setImageLevel(statusBarIcon.iconLevel);
        }
        if (!z4) {
            if (statusBarIcon.number <= 0 || !getContext().getResources().getBoolean(C2007R$bool.config_statusBarShowNumber)) {
                this.mNumberBackground = null;
                this.mNumberText = null;
            } else {
                if (this.mNumberBackground == null) {
                    this.mNumberBackground = getContext().getResources().getDrawable(C2010R$drawable.ic_notification_overlay);
                }
                placeNumber();
            }
            invalidate();
        }
        if (!z3) {
            if (!statusBarIcon.visible || this.mBlocked) {
                i = 8;
            }
            setVisibility(i);
        }
        return true;
    }

    public void updateDrawable() {
        updateDrawable(true);
    }

    private boolean updateDrawable(boolean z) {
        String str = "StatusBarIconView";
        StatusBarIcon statusBarIcon = this.mIcon;
        if (statusBarIcon == null) {
            return false;
        }
        try {
            Drawable icon = getIcon(statusBarIcon);
            if (icon == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("No icon for slot ");
                sb.append(this.mSlot);
                sb.append("; ");
                sb.append(this.mIcon.icon);
                Log.w(str, sb.toString());
                return false;
            }
            if (z) {
                setImageDrawable(null);
            }
            setImageDrawable(icon);
            return true;
        } catch (OutOfMemoryError unused) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("OOM while inflating ");
            sb2.append(this.mIcon.icon);
            sb2.append(" for slot ");
            sb2.append(this.mSlot);
            Log.w(str, sb2.toString());
            return false;
        }
    }

    public Icon getSourceIcon() {
        return this.mIcon.icon;
    }

    private Drawable getIcon(StatusBarIcon statusBarIcon) {
        return getIcon(getContext(), statusBarIcon);
    }

    public static Drawable getIcon(Context context, StatusBarIcon statusBarIcon) {
        int identifier = statusBarIcon.user.getIdentifier();
        if (identifier == -1) {
            identifier = 0;
        }
        Drawable loadDrawableAsUser = statusBarIcon.icon.loadDrawableAsUser(context, identifier);
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(C2009R$dimen.status_bar_icon_scale_factor, typedValue, true);
        float f = typedValue.getFloat();
        if (f == 1.0f) {
            return loadDrawableAsUser;
        }
        return new ScalingDrawableWrapper(loadDrawableAsUser, f);
    }

    public StatusBarIcon getStatusBarIcon() {
        return this.mIcon;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        StatusBarNotification statusBarNotification = this.mNotification;
        if (statusBarNotification != null) {
            accessibilityEvent.setParcelableData(statusBarNotification.getNotification());
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mNumberBackground != null) {
            placeNumber();
        }
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        updateDrawable();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        if (this.mIconAppearAmount > 0.0f) {
            canvas.save();
            float f2 = this.mIconScale;
            float f3 = this.mIconAppearAmount;
            canvas.scale(f2 * f3, f2 * f3, (float) (getWidth() / 2), (float) (getHeight() / 2));
            super.onDraw(canvas);
            canvas.restore();
        }
        Drawable drawable = this.mNumberBackground;
        if (drawable != null) {
            drawable.draw(canvas);
            canvas.drawText(this.mNumberText, (float) this.mNumberX, (float) this.mNumberY, this.mNumberPain);
        }
        if (this.mDotAppearAmount != 0.0f) {
            float alpha = ((float) Color.alpha(this.mDecorColor)) / 255.0f;
            float f4 = this.mDotAppearAmount;
            if (f4 <= 1.0f) {
                f = this.mDotRadius * f4;
            } else {
                float f5 = f4 - 1.0f;
                alpha *= 1.0f - f5;
                f = NotificationUtils.interpolate(this.mDotRadius, (float) (getWidth() / 4), f5);
            }
            this.mDotPaint.setAlpha((int) (alpha * 255.0f));
            canvas.drawCircle((float) (this.mStatusBarIconSize / 2), (float) (getHeight() / 2), f, this.mDotPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void debug(int i) {
        super.debug(i);
        StringBuilder sb = new StringBuilder();
        sb.append(ImageView.debugIndent(i));
        sb.append("slot=");
        sb.append(this.mSlot);
        String str = "View";
        Log.d(str, sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(ImageView.debugIndent(i));
        sb2.append("icon=");
        sb2.append(this.mIcon);
        Log.d(str, sb2.toString());
    }

    /* access modifiers changed from: 0000 */
    public void placeNumber() {
        String str;
        if (this.mIcon.number > getContext().getResources().getInteger(17694723)) {
            str = getContext().getResources().getString(17039383);
        } else {
            str = NumberFormat.getIntegerInstance().format((long) this.mIcon.number);
        }
        this.mNumberText = str;
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect();
        this.mNumberPain.getTextBounds(str, 0, str.length(), rect);
        int i = rect.right - rect.left;
        int i2 = rect.bottom - rect.top;
        this.mNumberBackground.getPadding(rect);
        int i3 = rect.left + i + rect.right;
        if (i3 < this.mNumberBackground.getMinimumWidth()) {
            i3 = this.mNumberBackground.getMinimumWidth();
        }
        int i4 = rect.right;
        this.mNumberX = (width - i4) - (((i3 - i4) - rect.left) / 2);
        int i5 = rect.top + i2 + rect.bottom;
        if (i5 < this.mNumberBackground.getMinimumWidth()) {
            i5 = this.mNumberBackground.getMinimumWidth();
        }
        int i6 = rect.bottom;
        this.mNumberY = (height - i6) - ((((i5 - rect.top) - i2) - i6) / 2);
        this.mNumberBackground.setBounds(width - i3, height - i5, width, height);
    }

    private void setContentDescription(Notification notification) {
        if (notification != null) {
            String contentDescForNotification = contentDescForNotification(this.mContext, notification);
            if (!TextUtils.isEmpty(contentDescForNotification)) {
                setContentDescription(contentDescForNotification);
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StatusBarIconView(slot=");
        sb.append(this.mSlot);
        sb.append(" icon=");
        sb.append(this.mIcon);
        sb.append(" notification=");
        sb.append(this.mNotification);
        sb.append(")");
        return sb.toString();
    }

    public StatusBarNotification getNotification() {
        return this.mNotification;
    }

    public String getSlot() {
        return this.mSlot;
    }

    public static String contentDescForNotification(Context context, Notification notification) {
        CharSequence charSequence;
        CharSequence charSequence2 = "";
        try {
            charSequence = Builder.recoverBuilder(context, notification).loadHeaderAppName();
        } catch (RuntimeException e) {
            Log.e("StatusBarIconView", "Unable to recover builder", e);
            Parcelable parcelable = notification.extras.getParcelable("android.appInfo");
            charSequence = parcelable instanceof ApplicationInfo ? String.valueOf(((ApplicationInfo) parcelable).loadLabel(context.getPackageManager())) : charSequence2;
        }
        CharSequence charSequence3 = notification.extras.getCharSequence("android.title");
        CharSequence charSequence4 = notification.extras.getCharSequence("android.text");
        CharSequence charSequence5 = notification.tickerText;
        if (TextUtils.equals(charSequence3, charSequence)) {
            charSequence3 = charSequence4;
        }
        if (!TextUtils.isEmpty(charSequence3)) {
            charSequence2 = charSequence3;
        } else if (!TextUtils.isEmpty(charSequence5)) {
            charSequence2 = charSequence5;
        }
        return context.getString(C2017R$string.accessibility_desc_notification_icon, new Object[]{charSequence, charSequence2});
    }

    public void setDecorColor(int i) {
        this.mDecorColor = i;
        updateDecorColor();
    }

    private void initializeDecorColor() {
        if (this.mNotification != null) {
            setDecorColor(getContext().getColor(this.mNightMode ? 17170884 : 17170885));
        }
    }

    private void updateDecorColor() {
        int interpolateColors = NotificationUtils.interpolateColors(this.mDecorColor, -1, this.mDozeAmount);
        if (this.mDotPaint.getColor() != interpolateColors) {
            this.mDotPaint.setColor(interpolateColors);
            if (this.mDotAppearAmount != 0.0f) {
                invalidate();
            }
        }
    }

    public void setStaticDrawableColor(int i) {
        this.mDrawableColor = i;
        setColorInternal(i);
        updateContrastedStaticColor();
        this.mIconColor = i;
        this.mDozer.setColor(i);
    }

    private void setColorInternal(int i) {
        this.mCurrentSetColor = i;
        updateIconColor();
    }

    private void updateIconColor() {
        if (!this.mTintIcons) {
            setColorFilter(null);
            return;
        }
        if (this.mCurrentSetColor != 0) {
            if (this.mMatrixColorFilter == null) {
                this.mMatrix = new float[20];
                this.mMatrixColorFilter = new ColorMatrixColorFilter(this.mMatrix);
            }
            updateTintMatrix(this.mMatrix, NotificationUtils.interpolateColors(this.mCurrentSetColor, -1, this.mDozeAmount), this.mDozeAmount * 0.67f);
            this.mMatrixColorFilter.setColorMatrixArray(this.mMatrix);
            setColorFilter(null);
            setColorFilter(this.mMatrixColorFilter);
        } else {
            this.mDozer.updateGrayscale(this, this.mDozeAmount);
        }
    }

    private static void updateTintMatrix(float[] fArr, int i, float f) {
        Arrays.fill(fArr, 0.0f);
        fArr[4] = (float) Color.red(i);
        fArr[9] = (float) Color.green(i);
        fArr[14] = (float) Color.blue(i);
        fArr[18] = (((float) Color.alpha(i)) / 255.0f) + f;
    }

    public void setIconColor(int i, boolean z) {
        if (this.mIconColor != i) {
            this.mIconColor = i;
            ValueAnimator valueAnimator = this.mColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            int i2 = this.mCurrentSetColor;
            if (i2 != i) {
                if (!z || i2 == 0) {
                    setColorInternal(i);
                } else {
                    this.mAnimationStartColor = i2;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    this.mColorAnimator = ofFloat;
                    ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
                    this.mColorAnimator.setDuration(100);
                    this.mColorAnimator.addUpdateListener(this.mColorUpdater);
                    this.mColorAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            StatusBarIconView.this.mColorAnimator = null;
                            StatusBarIconView.this.mAnimationStartColor = 0;
                        }
                    });
                    this.mColorAnimator.start();
                }
            }
        }
    }

    public int getStaticDrawableColor() {
        return this.mDrawableColor;
    }

    /* access modifiers changed from: 0000 */
    public int getContrastedStaticDrawableColor(int i) {
        if (this.mCachedContrastBackgroundColor != i) {
            this.mCachedContrastBackgroundColor = i;
            updateContrastedStaticColor();
        }
        return this.mContrastedDrawableColor;
    }

    private void updateContrastedStaticColor() {
        if (Color.alpha(this.mCachedContrastBackgroundColor) != 255) {
            this.mContrastedDrawableColor = this.mDrawableColor;
            return;
        }
        int i = this.mDrawableColor;
        if (!ContrastColorUtil.satisfiesTextContrast(this.mCachedContrastBackgroundColor, i)) {
            float[] fArr = new float[3];
            ColorUtils.colorToHSL(this.mDrawableColor, fArr);
            if (fArr[1] < 0.2f) {
                i = 0;
            }
            i = ContrastColorUtil.resolveContrastColor(this.mContext, i, this.mCachedContrastBackgroundColor, !ContrastColorUtil.isColorLight(this.mCachedContrastBackgroundColor));
        }
        this.mContrastedDrawableColor = i;
    }

    public void setVisibleState(int i) {
        setVisibleState(i, true, null);
    }

    public void setVisibleState(int i, boolean z) {
        setVisibleState(i, z, null);
    }

    public void setVisibleState(int i, boolean z, Runnable runnable) {
        setVisibleState(i, z, runnable, 0);
    }

    public void setVisibleState(int i, boolean z, Runnable runnable, long j) {
        float f;
        boolean z2;
        int i2 = i;
        final Runnable runnable2 = runnable;
        boolean z3 = false;
        if (i2 != this.mVisibleState) {
            this.mVisibleState = i2;
            ObjectAnimator objectAnimator = this.mIconAppearAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            ObjectAnimator objectAnimator2 = this.mDotAnimator;
            if (objectAnimator2 != null) {
                objectAnimator2.cancel();
            }
            if (z) {
                Interpolator interpolator = Interpolators.FAST_OUT_LINEAR_IN;
                if (i2 == 0) {
                    interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
                    f = 1.0f;
                } else {
                    f = 0.0f;
                }
                float iconAppearAmount = getIconAppearAmount();
                long j2 = 100;
                if (f != iconAppearAmount) {
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, ICON_APPEAR_AMOUNT, new float[]{iconAppearAmount, f});
                    this.mIconAppearAnimator = ofFloat;
                    ofFloat.setInterpolator(interpolator);
                    this.mIconAppearAnimator.setDuration(j == 0 ? 100 : j);
                    this.mIconAppearAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            StatusBarIconView.this.mIconAppearAnimator = null;
                            StatusBarIconView.this.runRunnable(runnable2);
                        }
                    });
                    this.mIconAppearAnimator.start();
                    z2 = true;
                } else {
                    z2 = false;
                }
                float f2 = i2 == 0 ? 2.0f : 0.0f;
                Interpolator interpolator2 = Interpolators.FAST_OUT_LINEAR_IN;
                if (i2 == 1) {
                    interpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
                    f2 = 1.0f;
                }
                float dotAppearAmount = getDotAppearAmount();
                if (f2 != dotAppearAmount) {
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, DOT_APPEAR_AMOUNT, new float[]{dotAppearAmount, f2});
                    this.mDotAnimator = ofFloat2;
                    ofFloat2.setInterpolator(interpolator2);
                    ObjectAnimator objectAnimator3 = this.mDotAnimator;
                    if (j != 0) {
                        j2 = j;
                    }
                    objectAnimator3.setDuration(j2);
                    final boolean z4 = !z2;
                    this.mDotAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            StatusBarIconView.this.mDotAnimator = null;
                            if (z4) {
                                StatusBarIconView.this.runRunnable(runnable2);
                            }
                        }
                    });
                    this.mDotAnimator.start();
                    z3 = true;
                } else {
                    z3 = z2;
                }
            } else {
                setIconAppearAmount(i2 == 0 ? 1.0f : 0.0f);
                float f3 = i2 == 1 ? 1.0f : i2 == 0 ? 2.0f : 0.0f;
                setDotAppearAmount(f3);
            }
        }
        if (!z3) {
            runRunnable(runnable2);
        }
    }

    /* access modifiers changed from: private */
    public void runRunnable(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setIconAppearAmount(float f) {
        if (this.mIconAppearAmount != f) {
            this.mIconAppearAmount = f;
            invalidate();
        }
    }

    public float getIconAppearAmount() {
        return this.mIconAppearAmount;
    }

    public int getVisibleState() {
        return this.mVisibleState;
    }

    public void setDotAppearAmount(float f) {
        if (this.mDotAppearAmount != f) {
            this.mDotAppearAmount = f;
            invalidate();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        OnVisibilityChangedListener onVisibilityChangedListener = this.mOnVisibilityChangedListener;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onVisibilityChanged(i);
        }
    }

    public float getDotAppearAmount() {
        return this.mDotAppearAmount;
    }

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener onVisibilityChangedListener) {
        this.mOnVisibilityChangedListener = onVisibilityChangedListener;
    }

    public void setDozing(boolean z, boolean z2, long j) {
        this.mDozer.setDozing(new Consumer() {
            public final void accept(Object obj) {
                StatusBarIconView.this.lambda$setDozing$1$StatusBarIconView((Float) obj);
            }
        }, z, z2, j, this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDozing$1 */
    public /* synthetic */ void lambda$setDozing$1$StatusBarIconView(Float f) {
        this.mDozeAmount = f.floatValue();
        updateDecorColor();
        updateIconColor();
        updateAllowAnimation();
    }

    private void updateAllowAnimation() {
        float f = this.mDozeAmount;
        if (f == 0.0f || f == 1.0f) {
            setAllowAnimation(this.mDozeAmount == 0.0f);
        }
    }

    public void getDrawingRect(Rect rect) {
        super.getDrawingRect(rect);
        float translationX = getTranslationX();
        float translationY = getTranslationY();
        rect.left = (int) (((float) rect.left) + translationX);
        rect.right = (int) (((float) rect.right) + translationX);
        rect.top = (int) (((float) rect.top) + translationY);
        rect.bottom = (int) (((float) rect.bottom) + translationY);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Runnable runnable = this.mLayoutRunnable;
        if (runnable != null) {
            runnable.run();
            this.mLayoutRunnable = null;
        }
        updatePivot();
    }

    private void updatePivot() {
        setPivotX(((1.0f - this.mIconScale) / 2.0f) * ((float) getWidth()));
        setPivotY((((float) getHeight()) - (this.mIconScale * ((float) getWidth()))) / 2.0f);
    }

    public void executeOnLayout(Runnable runnable) {
        this.mLayoutRunnable = runnable;
    }

    public void setDismissed() {
        Runnable runnable = this.mOnDismissListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setOnDismissListener(Runnable runnable) {
        this.mOnDismissListener = runnable;
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        int tint = DarkIconDispatcher.getTint(rect, this, i);
        setImageTintList(ColorStateList.valueOf(tint));
        setDecorColor(tint);
    }

    public boolean isIconVisible() {
        StatusBarIcon statusBarIcon = this.mIcon;
        return statusBarIcon != null && statusBarIcon.visible;
    }

    public boolean isIconBlocked() {
        return this.mBlocked;
    }

    public void setIncreasedSize(boolean z) {
        this.mIncreasedSize = z;
        maybeUpdateIconScaleDimens();
    }

    public void setTintIcons(boolean z) {
        if (this.mTintIcons != z) {
            this.mTintIcons = z;
            updateIconColor();
        }
    }
}
