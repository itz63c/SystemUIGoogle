package com.android.systemui.statusbar.notification.row;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Notification.MessagingStyle.Message;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.ImageResolver;
import com.android.internal.widget.LocalImageResolver;
import com.android.internal.widget.MessagingMessage;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class NotificationInlineImageResolver implements ImageResolver {
    private static final String TAG = NotificationInlineImageResolver.class.getSimpleName();
    private final Context mContext;
    private final ImageCache mImageCache;
    @VisibleForTesting
    protected int mMaxImageHeight;
    @VisibleForTesting
    protected int mMaxImageWidth;
    private Set<Uri> mWantedUriSet;

    interface ImageCache {
        Drawable get(Uri uri);

        boolean hasEntry(Uri uri);

        void preload(Uri uri);

        void purge();

        void setImageResolver(NotificationInlineImageResolver notificationInlineImageResolver);
    }

    public NotificationInlineImageResolver(Context context, ImageCache imageCache) {
        this.mContext = context.getApplicationContext();
        this.mImageCache = imageCache;
        if (imageCache != null) {
            imageCache.setImageResolver(this);
        }
        updateMaxImageSizes();
    }

    public boolean hasCache() {
        return this.mImageCache != null && !ActivityManager.isLowRamDeviceStatic();
    }

    private boolean isLowRam() {
        return ActivityManager.isLowRamDeviceStatic();
    }

    public void updateMaxImageSizes() {
        this.mMaxImageWidth = getMaxImageWidth();
        this.mMaxImageHeight = getMaxImageHeight();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int getMaxImageWidth() {
        return this.mContext.getResources().getDimensionPixelSize(isLowRam() ? 17105340 : 17105339);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int getMaxImageHeight() {
        return this.mContext.getResources().getDimensionPixelSize(isLowRam() ? 17105338 : 17105337);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public BitmapDrawable resolveImageInternal(Uri uri) throws IOException {
        return (BitmapDrawable) LocalImageResolver.resolveImage(uri, this.mContext);
    }

    /* access modifiers changed from: 0000 */
    public Drawable resolveImage(Uri uri) throws IOException {
        BitmapDrawable resolveImageInternal = resolveImageInternal(uri);
        resolveImageInternal.setBitmap(Icon.scaleDownIfNecessary(resolveImageInternal.getBitmap(), this.mMaxImageWidth, this.mMaxImageHeight));
        return resolveImageInternal;
    }

    public Drawable loadImage(Uri uri) {
        try {
            if (!hasCache()) {
                return resolveImage(uri);
            }
            if (!this.mImageCache.hasEntry(uri)) {
                this.mImageCache.preload(uri);
            }
            return this.mImageCache.get(uri);
        } catch (IOException | SecurityException e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("loadImage: Can't load image from ");
            sb.append(uri);
            Log.d(str, sb.toString(), e);
            return null;
        }
    }

    public void preloadImages(Notification notification) {
        if (hasCache()) {
            retrieveWantedUriSet(notification);
            getWantedUriSet().forEach(new Consumer() {
                public final void accept(Object obj) {
                    NotificationInlineImageResolver.this.lambda$preloadImages$0$NotificationInlineImageResolver((Uri) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$preloadImages$0 */
    public /* synthetic */ void lambda$preloadImages$0$NotificationInlineImageResolver(Uri uri) {
        if (!this.mImageCache.hasEntry(uri)) {
            this.mImageCache.preload(uri);
        }
    }

    public void purgeCache() {
        if (hasCache()) {
            this.mImageCache.purge();
        }
    }

    private void retrieveWantedUriSet(Notification notification) {
        List<Message> list;
        HashSet hashSet = new HashSet();
        Bundle bundle = notification.extras;
        if (bundle != null) {
            Parcelable[] parcelableArray = bundle.getParcelableArray("android.messages");
            List<Message> list2 = null;
            if (parcelableArray == null) {
                list = null;
            } else {
                list = Message.getMessagesFromBundleArray(parcelableArray);
            }
            if (list != null) {
                for (Message message : list) {
                    if (MessagingMessage.hasImage(message)) {
                        hashSet.add(message.getDataUri());
                    }
                }
            }
            Parcelable[] parcelableArray2 = bundle.getParcelableArray("android.messages.historic");
            if (parcelableArray2 != null) {
                list2 = Message.getMessagesFromBundleArray(parcelableArray2);
            }
            if (list2 != null) {
                for (Message message2 : list2) {
                    if (MessagingMessage.hasImage(message2)) {
                        hashSet.add(message2.getDataUri());
                    }
                }
            }
            this.mWantedUriSet = hashSet;
        }
    }

    /* access modifiers changed from: 0000 */
    public Set<Uri> getWantedUriSet() {
        return this.mWantedUriSet;
    }
}
