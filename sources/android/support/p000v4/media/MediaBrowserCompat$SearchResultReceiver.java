package android.support.p000v4.media;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.support.p000v4.p001os.ResultReceiver;
import java.util.ArrayList;

/* renamed from: android.support.v4.media.MediaBrowserCompat$SearchResultReceiver */
class MediaBrowserCompat$SearchResultReceiver extends ResultReceiver {
    private final MediaBrowserCompat$SearchCallback mCallback;
    private final Bundle mExtras;
    private final String mQuery;

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        if (bundle != null) {
            bundle = MediaSessionCompat.unparcelWithClassLoader(bundle);
        }
        if (i == 0 && bundle != null) {
            String str = "search_results";
            if (bundle.containsKey(str)) {
                Parcelable[] parcelableArray = bundle.getParcelableArray(str);
                if (parcelableArray != null) {
                    ArrayList arrayList = new ArrayList();
                    for (Parcelable parcelable : parcelableArray) {
                        arrayList.add((MediaBrowserCompat$MediaItem) parcelable);
                    }
                    this.mCallback.onSearchResult(this.mQuery, this.mExtras, arrayList);
                } else {
                    this.mCallback.onError(this.mQuery, this.mExtras);
                }
                return;
            }
        }
        this.mCallback.onError(this.mQuery, this.mExtras);
    }
}
