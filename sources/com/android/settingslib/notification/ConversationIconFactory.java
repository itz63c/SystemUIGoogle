package com.android.settingslib.notification;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ShortcutInfo;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.IconDrawableFactory;
import android.util.Log;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.settingslib.R$color;

public class ConversationIconFactory extends BaseIconFactory {
    final IconDrawableFactory mIconDrawableFactory;
    private int mImportantConversationColor;
    final LauncherApps mLauncherApps;
    final PackageManager mPackageManager;

    public static class ConversationIconDrawable extends Drawable {
        private Drawable mBadgeIcon;
        private Drawable mBaseIcon;
        private int mIconSize;
        private Paint mRingPaint;
        private boolean mShowRing;

        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public ConversationIconDrawable(Drawable drawable, Drawable drawable2, int i, int i2, boolean z) {
            this.mBaseIcon = drawable;
            this.mBadgeIcon = drawable2;
            this.mIconSize = i;
            this.mShowRing = z;
            Paint paint = new Paint();
            this.mRingPaint = paint;
            paint.setStyle(Style.STROKE);
            this.mRingPaint.setColor(i2);
        }

        public int getIntrinsicWidth() {
            return this.mIconSize;
        }

        public int getIntrinsicHeight() {
            return this.mIconSize;
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            float width = ((float) bounds.width()) / 48.0f;
            int centerX = bounds.centerX();
            int centerX2 = bounds.centerX();
            int i = (int) (2.0f * width);
            int i2 = (int) (42.0f * width);
            int i3 = (int) (width * 16.800001f);
            Drawable drawable = this.mBaseIcon;
            String str = "ConversationIconFactory";
            if (drawable != null) {
                int i4 = i2 / 2;
                drawable.setBounds(centerX - i4, centerX2 - i4, centerX + i4, centerX2 + i4);
                this.mBaseIcon.draw(canvas);
            } else {
                Log.w(str, "ConversationIconDrawable has null base icon");
            }
            Drawable drawable2 = this.mBadgeIcon;
            if (drawable2 != null) {
                int i5 = bounds.right;
                int i6 = (i5 - i3) - i;
                int i7 = bounds.bottom;
                drawable2.setBounds(i6, (i7 - i3) - i, i5 - i, i7 - i);
                this.mBadgeIcon.draw(canvas);
            } else {
                Log.w(str, "ConversationIconDrawable has null badge icon");
            }
            if (this.mShowRing) {
                float f = (float) i;
                this.mRingPaint.setStrokeWidth(f);
                float f2 = ((float) i3) * 0.5f;
                canvas.drawCircle((((float) bounds.right) - f2) - f, (((float) bounds.bottom) - f2) - f, (0.5f * f) + f2, this.mRingPaint);
            }
        }
    }

    public ConversationIconFactory(Context context, LauncherApps launcherApps, PackageManager packageManager, IconDrawableFactory iconDrawableFactory, int i) {
        super(context, context.getResources().getConfiguration().densityDpi, i);
        this.mLauncherApps = launcherApps;
        this.mPackageManager = packageManager;
        this.mIconDrawableFactory = iconDrawableFactory;
        this.mImportantConversationColor = context.getResources().getColor(R$color.important_conversation, null);
    }

    private Drawable getBaseIconDrawable(ShortcutInfo shortcutInfo) {
        return this.mLauncherApps.getShortcutIconDrawable(shortcutInfo, this.mFillResIconDpi);
    }

    private Drawable getAppBadge(String str, int i) {
        try {
            return this.mIconDrawableFactory.getBadgedIcon(this.mPackageManager.getApplicationInfoAsUser(str, 128, i), i);
        } catch (NameNotFoundException unused) {
            return this.mPackageManager.getDefaultActivityIcon();
        }
    }

    public Drawable getConversationDrawable(ShortcutInfo shortcutInfo, String str, int i, boolean z) {
        return getConversationDrawable(getBaseIconDrawable(shortcutInfo), str, i, z);
    }

    public Drawable getConversationDrawable(Drawable drawable, String str, int i, boolean z) {
        ConversationIconDrawable conversationIconDrawable = new ConversationIconDrawable(drawable, getAppBadge(str, UserHandle.getUserId(i)), this.mIconBitmapSize, this.mImportantConversationColor, z);
        return conversationIconDrawable;
    }
}
