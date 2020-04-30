package android.support.p000v4.media;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.support.p000v4.p001os.ResultReceiver;

/* renamed from: android.support.v4.media.MediaBrowserCompat$ItemReceiver */
class MediaBrowserCompat$ItemReceiver extends ResultReceiver {
    private final MediaBrowserCompat$ItemCallback mCallback;
    private final String mMediaId;

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        if (bundle != null) {
            bundle = MediaSessionCompat.unparcelWithClassLoader(bundle);
        }
        if (i == 0 && bundle != null) {
            String str = "media_item";
            if (bundle.containsKey(str)) {
                Parcelable parcelable = bundle.getParcelable(str);
                if (parcelable == null || (parcelable instanceof MediaBrowserCompat$MediaItem)) {
                    this.mCallback.onItemLoaded((MediaBrowserCompat$MediaItem) parcelable);
                } else {
                    this.mCallback.onError(this.mMediaId);
                }
                return;
            }
        }
        this.mCallback.onError(this.mMediaId);
    }
}
