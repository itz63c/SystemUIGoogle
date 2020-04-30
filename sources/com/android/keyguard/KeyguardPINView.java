package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;

public class KeyguardPINView extends KeyguardPinBasedInputView {
    private final AppearAnimationUtils mAppearAnimationUtils;
    private ViewGroup mContainer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private int mDisappearYTranslation;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private ViewGroup mRow0;
    private ViewGroup mRow1;
    private ViewGroup mRow2;
    private ViewGroup mRow3;
    private View[][] mViews;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public KeyguardPINView(Context context) {
        this(context, null);
    }

    public KeyguardPINView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAppearAnimationUtils = new AppearAnimationUtils(context);
        Context context2 = context;
        DisappearAnimationUtils disappearAnimationUtils = new DisappearAnimationUtils(context2, 125, 0.6f, 0.45f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtils = disappearAnimationUtils;
        DisappearAnimationUtils disappearAnimationUtils2 = new DisappearAnimationUtils(context2, 187, 0.6f, 0.45f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = disappearAnimationUtils2;
        this.mDisappearYTranslation = getResources().getDimensionPixelSize(C2009R$dimen.disappear_y_translation);
        this.mKeyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
    }

    /* access modifiers changed from: protected */
    public void resetState() {
        super.resetState();
        SecurityMessageDisplay securityMessageDisplay = this.mSecurityMessageDisplay;
        if (securityMessageDisplay != null) {
            securityMessageDisplay.setMessage((CharSequence) "");
        }
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return C2011R$id.pinEntry;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContainer = (ViewGroup) findViewById(C2011R$id.container);
        this.mRow0 = (ViewGroup) findViewById(C2011R$id.row0);
        this.mRow1 = (ViewGroup) findViewById(C2011R$id.row1);
        this.mRow2 = (ViewGroup) findViewById(C2011R$id.row2);
        this.mRow3 = (ViewGroup) findViewById(C2011R$id.row3);
        findViewById(C2011R$id.divider);
        this.mViews = new View[][]{new View[]{this.mRow0, null, null}, new View[]{findViewById(C2011R$id.key1), findViewById(C2011R$id.key2), findViewById(C2011R$id.key3)}, new View[]{findViewById(C2011R$id.key4), findViewById(C2011R$id.key5), findViewById(C2011R$id.key6)}, new View[]{findViewById(C2011R$id.key7), findViewById(C2011R$id.key8), findViewById(C2011R$id.key9)}, new View[]{findViewById(C2011R$id.delete_button), findViewById(C2011R$id.key0), findViewById(C2011R$id.key_enter)}, new View[]{null, this.mEcaView, null}};
        View findViewById = findViewById(C2011R$id.cancel_button);
        if (findViewById != null) {
            findViewById.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    KeyguardPINView.this.lambda$onFinishInflate$0$KeyguardPINView(view);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ void lambda$onFinishInflate$0$KeyguardPINView(View view) {
        this.mCallback.reset();
        this.mCallback.onCancelClicked();
    }

    public int getWrongPasswordStringId() {
        return C2017R$string.kg_wrong_pin;
    }

    public void startAppearAnimation() {
        enableClipping(false);
        setAlpha(1.0f);
        setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
        AppearAnimationUtils.startTranslationYAnimation(this, 0, 500, 0.0f, this.mAppearAnimationUtils.getInterpolator());
        this.mAppearAnimationUtils.startAnimation2d(this.mViews, new Runnable() {
            public void run() {
                KeyguardPINView.this.enableClipping(true);
            }
        });
    }

    public boolean startDisappearAnimation(final Runnable runnable) {
        DisappearAnimationUtils disappearAnimationUtils;
        enableClipping(false);
        setTranslationY(0.0f);
        AppearAnimationUtils.startTranslationYAnimation(this, 0, 280, (float) this.mDisappearYTranslation, this.mDisappearAnimationUtils.getInterpolator());
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            disappearAnimationUtils = this.mDisappearAnimationUtilsLocked;
        } else {
            disappearAnimationUtils = this.mDisappearAnimationUtils;
        }
        disappearAnimationUtils.startAnimation2d(this.mViews, new Runnable() {
            public void run() {
                KeyguardPINView.this.enableClipping(true);
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    public void enableClipping(boolean z) {
        this.mContainer.setClipToPadding(z);
        this.mContainer.setClipChildren(z);
        this.mRow1.setClipToPadding(z);
        this.mRow2.setClipToPadding(z);
        this.mRow3.setClipToPadding(z);
        setClipChildren(z);
    }
}
