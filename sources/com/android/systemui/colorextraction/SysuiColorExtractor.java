package com.android.systemui.colorextraction;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.ColorExtractor.GradientColors;
import com.android.internal.colorextraction.types.ExtractionType;
import com.android.internal.colorextraction.types.Tonal;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

public class SysuiColorExtractor extends ColorExtractor implements Dumpable, ConfigurationListener {
    private final GradientColors mBackdropColors;
    private boolean mHasMediaArtwork;
    private final GradientColors mNeutralColorsLock;
    private final Tonal mTonal;

    public SysuiColorExtractor(Context context, ConfigurationController configurationController) {
        this(context, new Tonal(context), configurationController, (WallpaperManager) context.getSystemService(WallpaperManager.class), false);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.app.WallpaperManager$OnColorsChangedListener, java.lang.Object, com.android.systemui.colorextraction.SysuiColorExtractor] */
    @VisibleForTesting
    public SysuiColorExtractor(Context context, ExtractionType extractionType, ConfigurationController configurationController, WallpaperManager wallpaperManager, boolean z) {
        super(context, extractionType, z, wallpaperManager);
        this.mTonal = extractionType instanceof Tonal ? (Tonal) extractionType : new Tonal(context);
        this.mNeutralColorsLock = new GradientColors();
        configurationController.addCallback(this);
        GradientColors gradientColors = new GradientColors();
        this.mBackdropColors = gradientColors;
        gradientColors.setMainColor(-16777216);
        if (wallpaperManager.isWallpaperSupported()) {
            wallpaperManager.removeOnColorsChangedListener(this);
            wallpaperManager.addOnColorsChangedListener(this, null, -1);
        }
    }

    /* access modifiers changed from: protected */
    public void extractWallpaperColors() {
        SysuiColorExtractor.super.extractWallpaperColors();
        Tonal tonal = this.mTonal;
        if (tonal != null && this.mNeutralColorsLock != null) {
            WallpaperColors wallpaperColors = this.mLockColors;
            if (wallpaperColors == null) {
                wallpaperColors = this.mSystemColors;
            }
            tonal.applyFallback(wallpaperColors, this.mNeutralColorsLock);
        }
    }

    public void onColorsChanged(WallpaperColors wallpaperColors, int i, int i2) {
        if (i2 == KeyguardUpdateMonitor.getCurrentUser()) {
            if ((i & 2) != 0) {
                this.mTonal.applyFallback(wallpaperColors, this.mNeutralColorsLock);
            }
            SysuiColorExtractor.super.onColorsChanged(wallpaperColors, i);
        }
    }

    public void onUiModeChanged() {
        extractWallpaperColors();
        triggerColorsChanged(3);
    }

    public GradientColors getColors(int i, int i2) {
        if (!this.mHasMediaArtwork || (i & 2) == 0) {
            return SysuiColorExtractor.super.getColors(i, i2);
        }
        return this.mBackdropColors;
    }

    public GradientColors getNeutralColors() {
        return this.mHasMediaArtwork ? this.mBackdropColors : this.mNeutralColorsLock;
    }

    public void setHasMediaArtwork(boolean z) {
        if (this.mHasMediaArtwork != z) {
            this.mHasMediaArtwork = z;
            triggerColorsChanged(2);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SysuiColorExtractor:");
        printWriter.println("  Current wallpaper colors:");
        StringBuilder sb = new StringBuilder();
        String str = "    system: ";
        sb.append(str);
        sb.append(this.mSystemColors);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        String str2 = "    lock: ";
        sb2.append(str2);
        sb2.append(this.mLockColors);
        printWriter.println(sb2.toString());
        GradientColors[] gradientColorsArr = (GradientColors[]) this.mGradientColors.get(1);
        GradientColors[] gradientColorsArr2 = (GradientColors[]) this.mGradientColors.get(2);
        printWriter.println("  Gradients:");
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(Arrays.toString(gradientColorsArr));
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str2);
        sb4.append(Arrays.toString(gradientColorsArr2));
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  Neutral colors: ");
        sb5.append(this.mNeutralColorsLock);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  Has media backdrop: ");
        sb6.append(this.mHasMediaArtwork);
        printWriter.println(sb6.toString());
    }
}
