package com.android.systemui.pip;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.SurfaceControl.Transaction;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;

public class PipSurfaceTransactionHelper {
    private final int mCornerRadius;
    private final boolean mEnableCornerRadius;

    interface SurfaceControlTransactionFactory {
        Transaction getTransaction();
    }

    public PipSurfaceTransactionHelper(Context context) {
        Resources resources = context.getResources();
        this.mEnableCornerRadius = resources.getBoolean(C2007R$bool.config_pipEnableRoundCorner);
        this.mCornerRadius = resources.getDimensionPixelSize(C2009R$dimen.pip_corner_radius);
    }

    /* access modifiers changed from: 0000 */
    public PipSurfaceTransactionHelper alpha(Transaction transaction, SurfaceControl surfaceControl, float f) {
        transaction.setAlpha(surfaceControl, f);
        return this;
    }

    /* access modifiers changed from: 0000 */
    public PipSurfaceTransactionHelper crop(Transaction transaction, SurfaceControl surfaceControl, Rect rect) {
        transaction.setWindowCrop(surfaceControl, rect.width(), rect.height()).setPosition(surfaceControl, (float) rect.left, (float) rect.top);
        return this;
    }

    /* access modifiers changed from: 0000 */
    public PipSurfaceTransactionHelper round(Transaction transaction, SurfaceControl surfaceControl, boolean z) {
        if (this.mEnableCornerRadius) {
            transaction.setCornerRadius(surfaceControl, z ? (float) this.mCornerRadius : 0.0f);
        }
        return this;
    }
}
