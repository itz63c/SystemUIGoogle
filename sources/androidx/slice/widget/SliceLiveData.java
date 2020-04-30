package androidx.slice.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.lifecycle.LiveData;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.SliceSpecs;
import androidx.slice.SliceViewManager;
import androidx.slice.SliceViewManager.SliceCallback;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public final class SliceLiveData {
    public static final SliceSpec OLD_BASIC = new SliceSpec("androidx.app.slice.BASIC", 1);
    public static final SliceSpec OLD_LIST = new SliceSpec("androidx.app.slice.LIST", 1);
    public static final Set<SliceSpec> SUPPORTED_SPECS = new ArraySet((Collection<E>) Arrays.asList(new SliceSpec[]{SliceSpecs.BASIC, SliceSpecs.LIST, SliceSpecs.LIST_V2, OLD_BASIC, OLD_LIST}));

    public interface OnErrorListener {
        void onSliceError(int i, Throwable th);
    }

    private static class SliceLiveDataImpl extends LiveData<Slice> {
        final Intent mIntent;
        final OnErrorListener mListener;
        final SliceCallback mSliceCallback = new SliceCallback() {
            public final void onSliceUpdated(Slice slice) {
                SliceLiveDataImpl.this.postValue(slice);
            }
        };
        final SliceViewManager mSliceViewManager;
        private final Runnable mUpdateSlice = new Runnable() {
            public void run() {
                Slice slice;
                try {
                    if (SliceLiveDataImpl.this.mUri != null) {
                        slice = SliceLiveDataImpl.this.mSliceViewManager.bindSlice(SliceLiveDataImpl.this.mUri);
                    } else {
                        slice = SliceLiveDataImpl.this.mSliceViewManager.bindSlice(SliceLiveDataImpl.this.mIntent);
                    }
                    if (slice == null) {
                        SliceLiveDataImpl.this.onSliceError(2, null);
                        return;
                    }
                    if (SliceLiveDataImpl.this.mUri == null) {
                        SliceLiveDataImpl.this.mUri = slice.getUri();
                        SliceLiveDataImpl.this.mSliceViewManager.registerSliceCallback(SliceLiveDataImpl.this.mUri, SliceLiveDataImpl.this.mSliceCallback);
                    }
                    SliceLiveDataImpl.this.postValue(slice);
                } catch (IllegalArgumentException e) {
                    SliceLiveDataImpl.this.onSliceError(3, e);
                } catch (Exception e2) {
                    SliceLiveDataImpl.this.onSliceError(0, e2);
                }
            }
        };
        Uri mUri;

        SliceLiveDataImpl(Context context, Uri uri, OnErrorListener onErrorListener) {
            this.mSliceViewManager = SliceViewManager.getInstance(context);
            this.mUri = uri;
            this.mIntent = null;
            this.mListener = onErrorListener;
        }

        /* access modifiers changed from: protected */
        public void onActive() {
            AsyncTask.execute(this.mUpdateSlice);
            Uri uri = this.mUri;
            if (uri != null) {
                this.mSliceViewManager.registerSliceCallback(uri, this.mSliceCallback);
            }
        }

        /* access modifiers changed from: protected */
        public void onInactive() {
            Uri uri = this.mUri;
            if (uri != null) {
                this.mSliceViewManager.unregisterSliceCallback(uri, this.mSliceCallback);
            }
        }

        /* access modifiers changed from: 0000 */
        public void onSliceError(int i, Throwable th) {
            Uri uri = this.mUri;
            if (uri != null) {
                this.mSliceViewManager.unregisterSliceCallback(uri, this.mSliceCallback);
            }
            OnErrorListener onErrorListener = this.mListener;
            if (onErrorListener != null) {
                onErrorListener.onSliceError(i, th);
                return;
            }
            String str = "SliceLiveData";
            if (th != null) {
                Log.e(str, "Error binding slice", th);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Error binding slice, error code: ");
                sb.append(i);
                Log.e(str, sb.toString());
            }
        }
    }

    public static LiveData<Slice> fromUri(Context context, Uri uri) {
        return new SliceLiveDataImpl(context.getApplicationContext(), uri, null);
    }
}
