package com.android.systemui.bubbles;

import android.app.Notification.BubbleMetadata;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.R$dimen;
import com.android.launcher3.icons.ShadowGenerator;
import com.android.systemui.C2009R$dimen;

public class BubbleIconFactory extends BaseIconFactory {
    protected BubbleIconFactory(Context context) {
        super(context, context.getResources().getConfiguration().densityDpi, context.getResources().getDimensionPixelSize(C2009R$dimen.individual_bubble_size));
    }

    /* access modifiers changed from: 0000 */
    public int getBadgeSize() {
        return this.mContext.getResources().getDimensionPixelSize(R$dimen.profile_badge_size);
    }

    /* access modifiers changed from: 0000 */
    public Drawable getBubbleDrawable(Context context, ShortcutInfo shortcutInfo, BubbleMetadata bubbleMetadata) {
        if (shortcutInfo != null) {
            return ((LauncherApps) context.getSystemService("launcherapps")).getShortcutIconDrawable(shortcutInfo, context.getResources().getConfiguration().densityDpi);
        }
        Icon bubbleIcon = bubbleMetadata.getBubbleIcon();
        if (bubbleIcon == null) {
            return null;
        }
        if (bubbleIcon.getType() == 4 || bubbleIcon.getType() == 6) {
            context.grantUriPermission(context.getPackageName(), bubbleIcon.getUri(), 1);
        }
        return bubbleIcon.loadDrawable(context);
    }

    /* access modifiers changed from: 0000 */
    public BitmapInfo getBadgeBitmap(Drawable drawable) {
        Bitmap createIconBitmap = createIconBitmap(drawable, 1.0f, getBadgeSize());
        Canvas canvas = new Canvas();
        ShadowGenerator shadowGenerator = new ShadowGenerator(getBadgeSize());
        canvas.setBitmap(createIconBitmap);
        shadowGenerator.recreateIcon(Bitmap.createBitmap(createIconBitmap), canvas);
        return createIconBitmap(createIconBitmap);
    }

    /* access modifiers changed from: 0000 */
    public BitmapInfo getBubbleBitmap(Drawable drawable, BitmapInfo bitmapInfo) {
        BitmapInfo createBadgedIconBitmap = createBadgedIconBitmap(drawable, null, true);
        badgeWithDrawable(createBadgedIconBitmap.icon, (Drawable) new BitmapDrawable(this.mContext.getResources(), bitmapInfo.icon));
        return createBadgedIconBitmap;
    }
}
