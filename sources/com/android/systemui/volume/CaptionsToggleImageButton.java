package com.android.systemui.volume;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.core.view.accessibility.AccessibilityViewCommand.CommandArguments;
import com.android.keyguard.AlphaOptimizedImageButton;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;

public class CaptionsToggleImageButton extends AlphaOptimizedImageButton {
    private static final int[] OPTED_OUT_STATE = {C2006R$attr.optedOut};
    private boolean mCaptionsEnabled = false;
    private ConfirmedTapListener mConfirmedTapListener;
    private GestureDetector mGestureDetector;
    private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return CaptionsToggleImageButton.this.tryToSendTapConfirmedEvent();
        }
    };
    private boolean mOptedOut = false;

    interface ConfirmedTapListener {
        void onConfirmedTap();
    }

    public CaptionsToggleImageButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setContentDescription(getContext().getString(C2017R$string.volume_odi_captions_content_description));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        GestureDetector gestureDetector = this.mGestureDetector;
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }

    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (this.mOptedOut) {
            ImageButton.mergeDrawableStates(onCreateDrawableState, OPTED_OUT_STATE);
        }
        return onCreateDrawableState;
    }

    /* access modifiers changed from: 0000 */
    public Runnable setCaptionsEnabled(boolean z) {
        String str;
        int i;
        this.mCaptionsEnabled = z;
        AccessibilityActionCompat accessibilityActionCompat = AccessibilityActionCompat.ACTION_CLICK;
        if (z) {
            str = getContext().getString(C2017R$string.volume_odi_captions_hint_disable);
        } else {
            str = getContext().getString(C2017R$string.volume_odi_captions_hint_enable);
        }
        ViewCompat.replaceAccessibilityAction(this, accessibilityActionCompat, str, new AccessibilityViewCommand() {
            public final boolean perform(View view, CommandArguments commandArguments) {
                return CaptionsToggleImageButton.this.lambda$setCaptionsEnabled$0$CaptionsToggleImageButton(view, commandArguments);
            }
        });
        if (this.mCaptionsEnabled) {
            i = C2010R$drawable.ic_volume_odi_captions;
        } else {
            i = C2010R$drawable.ic_volume_odi_captions_disabled;
        }
        return setImageResourceAsync(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setCaptionsEnabled$0 */
    public /* synthetic */ boolean lambda$setCaptionsEnabled$0$CaptionsToggleImageButton(View view, CommandArguments commandArguments) {
        return tryToSendTapConfirmedEvent();
    }

    /* access modifiers changed from: private */
    public boolean tryToSendTapConfirmedEvent() {
        ConfirmedTapListener confirmedTapListener = this.mConfirmedTapListener;
        if (confirmedTapListener == null) {
            return false;
        }
        confirmedTapListener.onConfirmedTap();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean getCaptionsEnabled() {
        return this.mCaptionsEnabled;
    }

    /* access modifiers changed from: 0000 */
    public void setOptedOut(boolean z) {
        this.mOptedOut = z;
        refreshDrawableState();
    }

    /* access modifiers changed from: 0000 */
    public boolean getOptedOut() {
        return this.mOptedOut;
    }

    /* access modifiers changed from: 0000 */
    public void setOnConfirmedTapListener(ConfirmedTapListener confirmedTapListener, Handler handler) {
        this.mConfirmedTapListener = confirmedTapListener;
        if (this.mGestureDetector == null) {
            this.mGestureDetector = new GestureDetector(getContext(), this.mGestureListener, handler);
        }
    }
}
