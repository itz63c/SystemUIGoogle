package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.android.systemui.C2011R$id;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.KeyboardInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ZerostateInfoListener;
import com.google.android.systemui.assist.uihints.input.TouchActionRegion;
import java.util.Optional;

public class IconController implements KeyboardInfoListener, ZerostateInfoListener, ConfigurationListener, TouchActionRegion {
    private boolean mHasAccurateLuma;
    private final KeyboardIconView mKeyboardIcon;
    private boolean mKeyboardIconRequested;
    private PendingIntent mOnKeyboardIconTap;
    private PendingIntent mOnZerostateIconTap;
    private final ViewGroup mParent;
    private final ZeroStateIconView mZeroStateIcon;
    private boolean mZerostateIconRequested;

    IconController(LayoutInflater layoutInflater, ViewGroup viewGroup, ConfigurationController configurationController) {
        this.mParent = viewGroup;
        KeyboardIconView keyboardIconView = (KeyboardIconView) viewGroup.findViewById(C2011R$id.keyboard);
        this.mKeyboardIcon = keyboardIconView;
        keyboardIconView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                IconController.this.lambda$new$0$IconController(view);
            }
        });
        ZeroStateIconView zeroStateIconView = (ZeroStateIconView) this.mParent.findViewById(C2011R$id.zerostate);
        this.mZeroStateIcon = zeroStateIconView;
        zeroStateIconView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                IconController.this.lambda$new$1$IconController(view);
            }
        });
        configurationController.addCallback(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$IconController(View view) {
        PendingIntent pendingIntent = this.mOnKeyboardIconTap;
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException e) {
                Log.e("IconController", "Pending intent cancelled", e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$IconController(View view) {
        PendingIntent pendingIntent = this.mOnZerostateIconTap;
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException e) {
                Log.e("IconController", "Pending intent cancelled", e);
            }
        }
    }

    public void onShowKeyboard(PendingIntent pendingIntent) {
        this.mKeyboardIconRequested = pendingIntent != null;
        this.mOnKeyboardIconTap = pendingIntent;
        maybeUpdateKeyboardVisibility();
    }

    public void onHideKeyboard() {
        this.mKeyboardIconRequested = false;
        this.mOnKeyboardIconTap = null;
        maybeUpdateKeyboardVisibility();
    }

    public void onShowZerostate(PendingIntent pendingIntent) {
        this.mZerostateIconRequested = pendingIntent != null;
        this.mOnZerostateIconTap = pendingIntent;
        maybeUpdateZerostateVisibility();
    }

    public void onHideZerostate() {
        this.mZerostateIconRequested = false;
        this.mOnZerostateIconTap = null;
        maybeUpdateZerostateVisibility();
    }

    public void onDensityOrFontScaleChanged() {
        this.mKeyboardIcon.onDensityChanged();
        this.mZeroStateIcon.onDensityChanged();
    }

    /* access modifiers changed from: 0000 */
    public void setHasAccurateLuma(boolean z) {
        this.mHasAccurateLuma = z;
        maybeUpdateKeyboardVisibility();
        maybeUpdateZerostateVisibility();
    }

    /* access modifiers changed from: 0000 */
    public void setHasDarkBackground(boolean z) {
        this.mKeyboardIcon.setHasDarkBackground(z);
        this.mZeroStateIcon.setHasDarkBackground(z);
    }

    /* access modifiers changed from: 0000 */
    public boolean isRequested() {
        return this.mKeyboardIconRequested || this.mZerostateIconRequested;
    }

    /* access modifiers changed from: 0000 */
    public boolean isVisible() {
        return this.mKeyboardIcon.getVisibility() == 0 || this.mZeroStateIcon.getVisibility() == 0;
    }

    public Optional<Region> getTouchActionRegion() {
        Region region = new Region();
        if (this.mKeyboardIcon.getVisibility() == 0) {
            Rect rect = new Rect();
            this.mKeyboardIcon.getHitRect(rect);
            region.union(rect);
        }
        if (this.mZeroStateIcon.getVisibility() == 0) {
            Rect rect2 = new Rect();
            this.mZeroStateIcon.getHitRect(rect2);
            region.union(rect2);
        }
        return region.isEmpty() ? Optional.empty() : Optional.of(region);
    }

    private void maybeUpdateKeyboardVisibility() {
        maybeUpdateIconVisibility(this.mKeyboardIcon, this.mKeyboardIconRequested);
    }

    private void maybeUpdateZerostateVisibility() {
        maybeUpdateIconVisibility(this.mZeroStateIcon, this.mZerostateIconRequested);
    }

    private void maybeUpdateIconVisibility(View view, boolean z) {
        boolean z2 = true;
        int i = 0;
        boolean z3 = view.getVisibility() == 0;
        if (!z || !this.mHasAccurateLuma) {
            z2 = false;
        }
        if (z3 != z2) {
            if (!z2) {
                i = 8;
            }
            view.setVisibility(i);
        }
    }
}
