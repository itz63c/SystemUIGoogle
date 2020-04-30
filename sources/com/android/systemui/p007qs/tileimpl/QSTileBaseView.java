package com.android.systemui.p007qs.tileimpl;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.PathParser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.plugins.p006qs.QSIconView;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.State;
import com.android.systemui.plugins.p006qs.QSTileView;

/* renamed from: com.android.systemui.qs.tileimpl.QSTileBaseView */
public class QSTileBaseView extends QSTileView {
    private String mAccessibilityClass;
    private final ImageView mBg;
    private int mCircleColor;
    private final int mColorActive;
    private final int mColorDisabled;
    private final TextView mDetailText;
    private final C1015H mHandler = new C1015H();
    protected QSIconView mIcon;
    private final FrameLayout mIconFrame;
    private final int[] mLocInScreen = new int[2];
    protected RippleDrawable mRipple;
    private boolean mShowRippleEffect = true;
    private Drawable mTileBackground;
    private boolean mTileState;

    /* renamed from: com.android.systemui.qs.tileimpl.QSTileBaseView$H */
    private class C1015H extends Handler {
        public C1015H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                QSTileBaseView.this.handleStateChanged((State) message.obj);
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public QSTileBaseView(Context context, QSIconView qSIconView, boolean z) {
        super(context);
        context.getResources().getDimensionPixelSize(C2009R$dimen.qs_quick_tile_padding);
        this.mIconFrame = new FrameLayout(context);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(C2009R$dimen.qs_quick_tile_size);
        addView(this.mIconFrame, new LayoutParams(dimensionPixelSize, dimensionPixelSize));
        this.mBg = new ImageView(getContext());
        ShapeDrawable shapeDrawable = new ShapeDrawable(new PathShape(new Path(PathParser.createPathFromPathData(context.getResources().getString(17039801))), 100.0f, 100.0f));
        shapeDrawable.setTintList(ColorStateList.valueOf(0));
        int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(C2009R$dimen.qs_tile_background_size);
        shapeDrawable.setIntrinsicHeight(dimensionPixelSize2);
        shapeDrawable.setIntrinsicWidth(dimensionPixelSize2);
        this.mBg.setImageDrawable(shapeDrawable);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dimensionPixelSize2, dimensionPixelSize2, 17);
        this.mIconFrame.addView(this.mBg, layoutParams);
        this.mBg.setLayoutParams(layoutParams);
        this.mIcon = qSIconView;
        this.mIconFrame.addView(this.mIcon, new FrameLayout.LayoutParams(-2, -2, 17));
        TextView textView = (TextView) LayoutInflater.from(context).inflate(C2013R$layout.qs_tile_detail_text, this.mIconFrame, false);
        this.mDetailText = textView;
        this.mIconFrame.addView(textView);
        this.mIconFrame.setClipChildren(false);
        this.mIconFrame.setClipToPadding(false);
        Drawable newTileBackground = newTileBackground();
        this.mTileBackground = newTileBackground;
        if (newTileBackground instanceof RippleDrawable) {
            setRipple((RippleDrawable) newTileBackground);
        }
        setImportantForAccessibility(1);
        setBackground(this.mTileBackground);
        this.mColorActive = Utils.getColorAttrDefaultColor(context, 16843829);
        this.mColorDisabled = Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16843282));
        Utils.getColorAttrDefaultColor(context, 16842808);
        setPadding(0, 0, 0, 0);
        setClipChildren(false);
        setClipToPadding(false);
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public Drawable newTileBackground() {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{16843868});
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }

    private void setRipple(RippleDrawable rippleDrawable) {
        this.mRipple = rippleDrawable;
        if (getWidth() != 0) {
            updateRippleSize();
        }
    }

    private void updateRippleSize() {
        int measuredWidth = (this.mIconFrame.getMeasuredWidth() / 2) + this.mIconFrame.getLeft();
        int measuredHeight = (this.mIconFrame.getMeasuredHeight() / 2) + this.mIconFrame.getTop();
        int height = (int) (((float) this.mIcon.getHeight()) * 0.85f);
        this.mRipple.setHotspotBounds(measuredWidth - height, measuredHeight - height, measuredWidth + height, measuredHeight + height);
    }

    public void init(QSTile qSTile) {
        init(new OnClickListener() {
            public final void onClick(View view) {
                QSTile.this.click();
            }
        }, new OnClickListener() {
            public final void onClick(View view) {
                QSTile.this.secondaryClick();
            }
        }, new OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return QSTile.this.longClick();
            }
        });
        if (qSTile.supportsDetailView()) {
            this.mDetailText.setVisibility(0);
        }
    }

    public void init(OnClickListener onClickListener, OnClickListener onClickListener2, OnLongClickListener onLongClickListener) {
        setOnClickListener(onClickListener);
        setOnLongClickListener(onLongClickListener);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mRipple != null) {
            updateRippleSize();
        }
    }

    public View updateAccessibilityOrder(View view) {
        setAccessibilityTraversalAfter(view.getId());
        return this;
    }

    public void onStateChanged(State state) {
        this.mHandler.obtainMessage(1, state).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void handleStateChanged(State state) {
        int circleColor = getCircleColor(state.state);
        boolean animationsEnabled = animationsEnabled();
        int i = this.mCircleColor;
        boolean z = false;
        if (circleColor != i) {
            if (animationsEnabled) {
                ValueAnimator duration = ValueAnimator.ofArgb(new int[]{i, circleColor}).setDuration(350);
                duration.addUpdateListener(new AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        QSTileBaseView.this.lambda$handleStateChanged$3$QSTileBaseView(valueAnimator);
                    }
                });
                duration.start();
            } else {
                QSIconViewImpl.setTint(this.mBg, circleColor);
            }
            this.mCircleColor = circleColor;
        }
        this.mDetailText.setTextColor(QSTileImpl.getColorForState(getContext(), state.state));
        this.mShowRippleEffect = state.showRippleEffect;
        if (state.state != 0) {
            z = true;
        }
        setClickable(z);
        setLongClickable(state.handlesLongClick);
        this.mIcon.setIcon(state, animationsEnabled);
        setContentDescription(state.contentDescription);
        StringBuilder sb = new StringBuilder();
        int i2 = state.state;
        if (i2 == 0) {
            sb.append(this.mContext.getString(C2017R$string.tile_unavailable));
        } else if (i2 != 1) {
            if (i2 == 2 && (state instanceof BooleanState)) {
                sb.append(this.mContext.getString(C2017R$string.switch_bar_on));
            }
        } else if (state instanceof BooleanState) {
            sb.append(this.mContext.getString(C2017R$string.switch_bar_off));
        }
        if (!TextUtils.isEmpty(state.stateDescription)) {
            sb.append(", ");
            sb.append(state.stateDescription);
        }
        setStateDescription(sb.toString());
        this.mAccessibilityClass = state.state == 0 ? null : state.expandedAccessibilityClassName;
        if (state instanceof BooleanState) {
            boolean z2 = ((BooleanState) state).value;
            if (this.mTileState != z2) {
                this.mTileState = z2;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleStateChanged$3 */
    public /* synthetic */ void lambda$handleStateChanged$3$QSTileBaseView(ValueAnimator valueAnimator) {
        this.mBg.setImageTintList(ColorStateList.valueOf(((Integer) valueAnimator.getAnimatedValue()).intValue()));
    }

    /* access modifiers changed from: protected */
    public boolean animationsEnabled() {
        boolean z = false;
        if (!isShown() || getAlpha() != 1.0f) {
            return false;
        }
        getLocationOnScreen(this.mLocInScreen);
        if (this.mLocInScreen[1] >= (-getHeight())) {
            z = true;
        }
        return z;
    }

    private int getCircleColor(int i) {
        if (i == 0 || i == 1) {
            return this.mColorDisabled;
        }
        if (i == 2) {
            return this.mColorActive;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid state ");
        sb.append(i);
        Log.e("QSTileBaseView", sb.toString());
        return 0;
    }

    public void setClickable(boolean z) {
        super.setClickable(z);
        setBackground((!z || !this.mShowRippleEffect) ? null : this.mRipple);
    }

    public int getDetailY() {
        return getTop() + (getHeight() / 2);
    }

    public QSIconView getIcon() {
        return this.mIcon;
    }

    public View getIconWithBackground() {
        return this.mIconFrame;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (!TextUtils.isEmpty(this.mAccessibilityClass)) {
            accessibilityEvent.setClassName(this.mAccessibilityClass);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setSelected(false);
        if (!TextUtils.isEmpty(this.mAccessibilityClass)) {
            accessibilityNodeInfo.setClassName(this.mAccessibilityClass);
            if (Switch.class.getName().equals(this.mAccessibilityClass)) {
                accessibilityNodeInfo.setText(getResources().getString(this.mTileState ? C2017R$string.switch_bar_on : C2017R$string.switch_bar_off));
                accessibilityNodeInfo.setChecked(this.mTileState);
                accessibilityNodeInfo.setCheckable(true);
                if (isLongClickable()) {
                    accessibilityNodeInfo.addAction(new AccessibilityAction(AccessibilityAction.ACTION_LONG_CLICK.getId(), getResources().getString(C2017R$string.accessibility_long_click_tile)));
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[');
        StringBuilder sb2 = new StringBuilder();
        sb2.append("locInScreen=(");
        sb2.append(this.mLocInScreen[0]);
        sb2.append(", ");
        sb2.append(this.mLocInScreen[1]);
        sb2.append(")");
        sb.append(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(", iconView=");
        sb3.append(this.mIcon.toString());
        sb.append(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(", tileState=");
        sb4.append(this.mTileState);
        sb.append(sb4.toString());
        sb.append("]");
        return sb.toString();
    }
}
