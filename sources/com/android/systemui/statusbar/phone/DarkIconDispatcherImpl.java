package com.android.systemui.statusbar.phone;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.widget.ImageView;
import androidx.appcompat.R$styleable;
import com.android.systemui.C2008R$color;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.LightBarTransitionsController.DarkIntensityApplier;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class DarkIconDispatcherImpl implements SysuiDarkIconDispatcher, DarkIntensityApplier {
    private float mDarkIntensity;
    private int mDarkModeIconColorSingleTone;
    private int mIconTint = -1;
    private int mLightModeIconColorSingleTone;
    private final ArrayMap<Object, DarkReceiver> mReceivers = new ArrayMap<>();
    private final Rect mTintArea = new Rect();
    private final LightBarTransitionsController mTransitionsController;

    public int getTintAnimationDuration() {
        return R$styleable.AppCompatTheme_windowFixedWidthMajor;
    }

    public DarkIconDispatcherImpl(Context context, CommandQueue commandQueue) {
        this.mDarkModeIconColorSingleTone = context.getColor(C2008R$color.dark_mode_icon_color_single_tone);
        this.mLightModeIconColorSingleTone = context.getColor(C2008R$color.light_mode_icon_color_single_tone);
        this.mTransitionsController = new LightBarTransitionsController(context, this, commandQueue);
    }

    public LightBarTransitionsController getTransitionsController() {
        return this.mTransitionsController;
    }

    public void addDarkReceiver(DarkReceiver darkReceiver) {
        this.mReceivers.put(darkReceiver, darkReceiver);
        darkReceiver.onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addDarkReceiver$0 */
    public /* synthetic */ void lambda$addDarkReceiver$0$DarkIconDispatcherImpl(ImageView imageView, Rect rect, float f, int i) {
        imageView.setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(this.mTintArea, imageView, this.mIconTint)));
    }

    public void addDarkReceiver(ImageView imageView) {
        $$Lambda$DarkIconDispatcherImpl$ok51JmL9mmr4FNW4V8J0PDfHR6I r0 = new DarkReceiver(imageView) {
            public final /* synthetic */ ImageView f$1;

            {
                this.f$1 = r2;
            }

            public final void onDarkChanged(Rect rect, float f, int i) {
                DarkIconDispatcherImpl.this.lambda$addDarkReceiver$0$DarkIconDispatcherImpl(this.f$1, rect, f, i);
            }
        };
        this.mReceivers.put(imageView, r0);
        r0.onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    public void removeDarkReceiver(DarkReceiver darkReceiver) {
        this.mReceivers.remove(darkReceiver);
    }

    public void removeDarkReceiver(ImageView imageView) {
        this.mReceivers.remove(imageView);
    }

    public void applyDark(DarkReceiver darkReceiver) {
        ((DarkReceiver) this.mReceivers.get(darkReceiver)).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    public void setIconsDarkArea(Rect rect) {
        if (rect != null || !this.mTintArea.isEmpty()) {
            if (rect == null) {
                this.mTintArea.setEmpty();
            } else {
                this.mTintArea.set(rect);
            }
            applyIconTint();
        }
    }

    public void applyDarkIntensity(float f) {
        this.mDarkIntensity = f;
        this.mIconTint = ((Integer) ArgbEvaluator.getInstance().evaluate(f, Integer.valueOf(this.mLightModeIconColorSingleTone), Integer.valueOf(this.mDarkModeIconColorSingleTone))).intValue();
        applyIconTint();
    }

    private void applyIconTint() {
        for (int i = 0; i < this.mReceivers.size(); i++) {
            ((DarkReceiver) this.mReceivers.valueAt(i)).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("DarkIconDispatcher: ");
        StringBuilder sb = new StringBuilder();
        sb.append("  mIconTint: 0x");
        sb.append(Integer.toHexString(this.mIconTint));
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mDarkIntensity: ");
        sb2.append(this.mDarkIntensity);
        sb2.append("f");
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mTintArea: ");
        sb3.append(this.mTintArea);
        printWriter.println(sb3.toString());
    }
}
