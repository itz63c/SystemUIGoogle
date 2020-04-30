package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import androidx.palette.graphics.Palette;
import androidx.palette.graphics.Palette.Filter;
import androidx.palette.graphics.Palette.Swatch;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.C2008R$color;

public class MediaNotificationProcessor {
    private final Filter mBlackWhiteFilter;
    private final ImageGradientColorizer mColorizer;
    private final Context mContext;
    private final Context mPackageContext;

    static /* synthetic */ boolean lambda$new$0(int i, float[] fArr) {
        return !isWhiteOrBlack(fArr);
    }

    public MediaNotificationProcessor(Context context, Context context2) {
        this(context, context2, new ImageGradientColorizer());
    }

    MediaNotificationProcessor(Context context, Context context2, ImageGradientColorizer imageGradientColorizer) {
        this.mBlackWhiteFilter = $$Lambda$MediaNotificationProcessor$oWRwwE503YseXSqqQUwqkZxEskY.INSTANCE;
        this.mContext = context;
        this.mPackageContext = context2;
        this.mColorizer = imageGradientColorizer;
    }

    public void processNotification(Notification notification, Builder builder) {
        int i;
        Icon largeIcon = notification.getLargeIcon();
        if (largeIcon != null) {
            boolean z = true;
            builder.setRebuildStyledRemoteViews(true);
            Drawable loadDrawable = largeIcon.loadDrawable(this.mPackageContext);
            if (notification.isColorizedMedia()) {
                int intrinsicWidth = loadDrawable.getIntrinsicWidth();
                int intrinsicHeight = loadDrawable.getIntrinsicHeight();
                int i2 = intrinsicWidth * intrinsicHeight;
                if (i2 > 22500) {
                    double sqrt = Math.sqrt((double) (22500.0f / ((float) i2)));
                    intrinsicWidth = (int) (((double) intrinsicWidth) * sqrt);
                    intrinsicHeight = (int) (sqrt * ((double) intrinsicHeight));
                }
                Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                loadDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                loadDrawable.draw(canvas);
                Palette.Builder generateArtworkPaletteBuilder = generateArtworkPaletteBuilder(createBitmap);
                Swatch findBackgroundSwatch = findBackgroundSwatch(generateArtworkPaletteBuilder.generate());
                i = findBackgroundSwatch.getRgb();
                generateArtworkPaletteBuilder.setRegion((int) (((float) createBitmap.getWidth()) * 0.4f), 0, createBitmap.getWidth(), createBitmap.getHeight());
                if (!isWhiteOrBlack(findBackgroundSwatch.getHsl())) {
                    generateArtworkPaletteBuilder.addFilter(new Filter(findBackgroundSwatch.getHsl()[0]) {
                        public final /* synthetic */ float f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final boolean isAllowed(int i, float[] fArr) {
                            return MediaNotificationProcessor.lambda$processNotification$1(this.f$0, i, fArr);
                        }
                    });
                }
                generateArtworkPaletteBuilder.addFilter(this.mBlackWhiteFilter);
                builder.setColorPalette(i, selectForegroundColor(i, generateArtworkPaletteBuilder.generate()));
            } else {
                i = this.mContext.getColor(C2008R$color.notification_material_background_color);
            }
            ImageGradientColorizer imageGradientColorizer = this.mColorizer;
            if (this.mContext.getResources().getConfiguration().getLayoutDirection() != 1) {
                z = false;
            }
            builder.setLargeIcon(Icon.createWithBitmap(imageGradientColorizer.colorize(loadDrawable, i, z)));
        }
    }

    static /* synthetic */ boolean lambda$processNotification$1(float f, int i, float[] fArr) {
        float abs = Math.abs(fArr[0] - f);
        return abs > 10.0f && abs < 350.0f;
    }

    private int selectForegroundColor(int i, Palette palette) {
        if (ContrastColorUtil.isColorLight(i)) {
            return selectForegroundColorForSwatches(palette.getDarkVibrantSwatch(), palette.getVibrantSwatch(), palette.getDarkMutedSwatch(), palette.getMutedSwatch(), palette.getDominantSwatch(), -16777216);
        }
        return selectForegroundColorForSwatches(palette.getLightVibrantSwatch(), palette.getVibrantSwatch(), palette.getLightMutedSwatch(), palette.getMutedSwatch(), palette.getDominantSwatch(), -1);
    }

    private int selectForegroundColorForSwatches(Swatch swatch, Swatch swatch2, Swatch swatch3, Swatch swatch4, Swatch swatch5, int i) {
        Swatch selectVibrantCandidate = selectVibrantCandidate(swatch, swatch2);
        if (selectVibrantCandidate == null) {
            selectVibrantCandidate = selectMutedCandidate(swatch4, swatch3);
        }
        if (selectVibrantCandidate == null) {
            return hasEnoughPopulation(swatch5) ? swatch5.getRgb() : i;
        }
        if (swatch5 == selectVibrantCandidate) {
            return selectVibrantCandidate.getRgb();
        }
        if (((float) selectVibrantCandidate.getPopulation()) / ((float) swatch5.getPopulation()) >= 0.01f || swatch5.getHsl()[1] <= 0.19f) {
            return selectVibrantCandidate.getRgb();
        }
        return swatch5.getRgb();
    }

    private Swatch selectMutedCandidate(Swatch swatch, Swatch swatch2) {
        boolean hasEnoughPopulation = hasEnoughPopulation(swatch);
        boolean hasEnoughPopulation2 = hasEnoughPopulation(swatch2);
        if (hasEnoughPopulation && hasEnoughPopulation2) {
            return swatch.getHsl()[1] * (((float) swatch.getPopulation()) / ((float) swatch2.getPopulation())) > swatch2.getHsl()[1] ? swatch : swatch2;
        } else if (hasEnoughPopulation) {
            return swatch;
        } else {
            if (hasEnoughPopulation2) {
                return swatch2;
            }
            return null;
        }
    }

    private Swatch selectVibrantCandidate(Swatch swatch, Swatch swatch2) {
        boolean hasEnoughPopulation = hasEnoughPopulation(swatch);
        boolean hasEnoughPopulation2 = hasEnoughPopulation(swatch2);
        if (hasEnoughPopulation && hasEnoughPopulation2) {
            return ((float) swatch.getPopulation()) / ((float) swatch2.getPopulation()) < 1.0f ? swatch2 : swatch;
        } else if (hasEnoughPopulation) {
            return swatch;
        } else {
            if (hasEnoughPopulation2) {
                return swatch2;
            }
            return null;
        }
    }

    private boolean hasEnoughPopulation(Swatch swatch) {
        return swatch != null && ((double) (((float) swatch.getPopulation()) / 22500.0f)) > 0.002d;
    }

    public static Swatch findBackgroundSwatch(Bitmap bitmap) {
        return findBackgroundSwatch(generateArtworkPaletteBuilder(bitmap).generate());
    }

    private static Swatch findBackgroundSwatch(Palette palette) {
        Swatch dominantSwatch = palette.getDominantSwatch();
        if (dominantSwatch == null) {
            return new Swatch(-1, 100);
        }
        if (!isWhiteOrBlack(dominantSwatch.getHsl())) {
            return dominantSwatch;
        }
        float f = -1.0f;
        Swatch swatch = null;
        for (Swatch swatch2 : palette.getSwatches()) {
            if (swatch2 != dominantSwatch && ((float) swatch2.getPopulation()) > f && !isWhiteOrBlack(swatch2.getHsl())) {
                f = (float) swatch2.getPopulation();
                swatch = swatch2;
            }
        }
        return (swatch != null && ((float) dominantSwatch.getPopulation()) / f <= 2.5f) ? swatch : dominantSwatch;
    }

    private static Palette.Builder generateArtworkPaletteBuilder(Bitmap bitmap) {
        Palette.Builder from = Palette.from(bitmap);
        from.setRegion(0, 0, bitmap.getWidth() / 2, bitmap.getHeight());
        from.clearFilters();
        from.resizeBitmapArea(22500);
        return from;
    }

    private static boolean isWhiteOrBlack(float[] fArr) {
        return isBlack(fArr) || isWhite(fArr);
    }

    private static boolean isBlack(float[] fArr) {
        return fArr[2] <= 0.08f;
    }

    private static boolean isWhite(float[] fArr) {
        return fArr[2] >= 0.9f;
    }
}
