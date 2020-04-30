package com.google.android.systemui.assist.uihints;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.CompositionSamplingListener;
import android.view.SurfaceControl;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;

final class LightnessProvider implements CardInfoListener {
    /* access modifiers changed from: private */
    public boolean mCardVisible = false;
    /* access modifiers changed from: private */
    public int mColorMode = 0;
    private final CompositionSamplingListener mColorMonitor = new CompositionSamplingListener($$Lambda$_14QHG018Z6p13d3hzJuGTWnNeo.INSTANCE) {
        public void onSampleCollected(float f) {
            LightnessProvider.this.mUiHandler.post(new Runnable(f) {
                public final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    C18751.this.lambda$onSampleCollected$0$LightnessProvider$1(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSampleCollected$0 */
        public /* synthetic */ void lambda$onSampleCollected$0$LightnessProvider$1(float f) {
            if (LightnessProvider.this.mListener != null && !LightnessProvider.this.mMuted) {
                if (!LightnessProvider.this.mCardVisible || LightnessProvider.this.mColorMode == 0) {
                    LightnessProvider.this.mListener.onLightnessUpdate(f);
                }
            }
        }
    };
    private boolean mIsMonitoringColor = false;
    /* access modifiers changed from: private */
    public LightnessListener mListener;
    /* access modifiers changed from: private */
    public boolean mMuted = false;
    /* access modifiers changed from: private */
    public final Handler mUiHandler = new Handler(Looper.getMainLooper());

    LightnessProvider() {
    }

    public void onCardInfo(boolean z, int i, boolean z2, boolean z3) {
        setCardVisible(z, i);
    }

    /* access modifiers changed from: 0000 */
    public void setListener(LightnessListener lightnessListener) {
        this.mListener = lightnessListener;
    }

    /* access modifiers changed from: 0000 */
    public void setMuted(boolean z) {
        this.mMuted = z;
    }

    /* access modifiers changed from: 0000 */
    public void enableColorMonitoring(boolean z, Rect rect, SurfaceControl surfaceControl) {
        if (this.mIsMonitoringColor != z) {
            this.mIsMonitoringColor = z;
            if (z) {
                CompositionSamplingListener.register(this.mColorMonitor, 0, surfaceControl, rect);
            } else {
                CompositionSamplingListener.unregister(this.mColorMonitor);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setCardVisible(boolean z, int i) {
        this.mCardVisible = z;
        this.mColorMode = i;
        LightnessListener lightnessListener = this.mListener;
        if (lightnessListener != null && z) {
            if (i == 1) {
                lightnessListener.onLightnessUpdate(0.0f);
            } else if (i == 2) {
                lightnessListener.onLightnessUpdate(1.0f);
            }
        }
    }
}
