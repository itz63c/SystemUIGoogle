package com.android.keyguard;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.C2005R$array;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.R$styleable;

public class NumPadKey extends ViewGroup {
    static String[] sKlondike;
    /* access modifiers changed from: private */
    public int mDigit;
    private final TextView mDigitText;
    private final TextView mKlondikeText;
    private OnClickListener mListener;
    private final LockPatternUtils mLockPatternUtils;
    private final PowerManager mPM;
    /* access modifiers changed from: private */
    public PasswordTextView mTextView;
    /* access modifiers changed from: private */
    public int mTextViewResId;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }

    public NumPadKey(Context context) {
        this(context, null);
    }

    public NumPadKey(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NumPadKey(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, C2013R$layout.keyguard_num_pad_key);
    }

    /* JADX INFO: finally extract failed */
    protected NumPadKey(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.mDigit = -1;
        this.mListener = new OnClickListener() {
            public void onClick(View view) {
                if (NumPadKey.this.mTextView == null && NumPadKey.this.mTextViewResId > 0) {
                    View findViewById = NumPadKey.this.getRootView().findViewById(NumPadKey.this.mTextViewResId);
                    if (findViewById != null && (findViewById instanceof PasswordTextView)) {
                        NumPadKey.this.mTextView = (PasswordTextView) findViewById;
                    }
                }
                if (NumPadKey.this.mTextView != null && NumPadKey.this.mTextView.isEnabled()) {
                    NumPadKey.this.mTextView.append(Character.forDigit(NumPadKey.this.mDigit, 10));
                }
                NumPadKey.this.userActivity();
            }
        };
        setFocusable(true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.NumPadKey);
        try {
            this.mDigit = obtainStyledAttributes.getInt(R$styleable.NumPadKey_digit, this.mDigit);
            this.mTextViewResId = obtainStyledAttributes.getResourceId(R$styleable.NumPadKey_textView, 0);
            obtainStyledAttributes.recycle();
            setOnClickListener(this.mListener);
            setOnHoverListener(new LiftToActivateListener(context));
            this.mLockPatternUtils = new LockPatternUtils(context);
            this.mPM = (PowerManager) this.mContext.getSystemService("power");
            ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(i2, this, true);
            TextView textView = (TextView) findViewById(C2011R$id.digit_text);
            this.mDigitText = textView;
            textView.setText(Integer.toString(this.mDigit));
            this.mKlondikeText = (TextView) findViewById(C2011R$id.klondike_text);
            if (this.mDigit >= 0) {
                if (sKlondike == null) {
                    sKlondike = getResources().getStringArray(C2005R$array.lockscreen_num_pad_klondike);
                }
                String[] strArr = sKlondike;
                if (strArr != null) {
                    int length = strArr.length;
                    int i3 = this.mDigit;
                    if (length > i3) {
                        String str = strArr[i3];
                        if (str.length() > 0) {
                            this.mKlondikeText.setText(str);
                        } else {
                            this.mKlondikeText.setVisibility(4);
                        }
                    }
                }
            }
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R.styleable.View);
            if (!obtainStyledAttributes2.hasValueOrEmpty(13)) {
                setBackground(this.mContext.getDrawable(C2010R$drawable.ripple_drawable_pin));
            }
            obtainStyledAttributes2.recycle();
            setContentDescription(this.mDigitText.getText().toString());
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            doHapticKeyClick();
        }
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        measureChildren(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int measuredHeight = this.mDigitText.getMeasuredHeight();
        int measuredHeight2 = this.mKlondikeText.getMeasuredHeight();
        int height = (getHeight() / 2) - ((measuredHeight + measuredHeight2) / 2);
        int width = getWidth() / 2;
        int measuredWidth = width - (this.mDigitText.getMeasuredWidth() / 2);
        int i5 = measuredHeight + height;
        TextView textView = this.mDigitText;
        textView.layout(measuredWidth, height, textView.getMeasuredWidth() + measuredWidth, i5);
        int i6 = (int) (((float) i5) - (((float) measuredHeight2) * 0.35f));
        int i7 = measuredHeight2 + i6;
        int measuredWidth2 = width - (this.mKlondikeText.getMeasuredWidth() / 2);
        TextView textView2 = this.mKlondikeText;
        textView2.layout(measuredWidth2, i6, textView2.getMeasuredWidth() + measuredWidth2, i7);
    }

    public void doHapticKeyClick() {
        if (this.mLockPatternUtils.isTactileFeedbackEnabled()) {
            performHapticFeedback(1, 3);
        }
    }
}
