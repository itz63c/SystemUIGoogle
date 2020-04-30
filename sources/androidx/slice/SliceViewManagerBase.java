package androidx.slice;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Pair;
import androidx.slice.SliceViewManager.SliceCallback;
import androidx.slice.widget.SliceLiveData;
import java.util.concurrent.Executor;

public abstract class SliceViewManagerBase extends SliceViewManager {
    protected final Context mContext;
    private final ArrayMap<Pair<Uri, SliceCallback>, SliceListenerImpl> mListenerLookup = new ArrayMap<>();

    private class SliceListenerImpl {
        final SliceCallback mCallback;
        final Executor mExecutor;
        private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean z) {
                AsyncTask.execute(SliceListenerImpl.this.mUpdateSlice);
            }
        };
        private boolean mPinned;
        final Runnable mUpdateSlice = new Runnable() {
            public void run() {
                SliceListenerImpl.this.tryPin();
                SliceListenerImpl sliceListenerImpl = SliceListenerImpl.this;
                final Slice bindSlice = Slice.bindSlice(SliceViewManagerBase.this.mContext, sliceListenerImpl.mUri, SliceLiveData.SUPPORTED_SPECS);
                SliceListenerImpl.this.mExecutor.execute(new Runnable() {
                    public void run() {
                        SliceListenerImpl.this.mCallback.onSliceUpdated(bindSlice);
                    }
                });
            }
        };
        Uri mUri;

        SliceListenerImpl(Uri uri, Executor executor, SliceCallback sliceCallback) {
            this.mUri = uri;
            this.mExecutor = executor;
            this.mCallback = sliceCallback;
        }

        /* access modifiers changed from: 0000 */
        public void startListening() {
            ContentProviderClient acquireContentProviderClient = SliceViewManagerBase.this.mContext.getContentResolver().acquireContentProviderClient(this.mUri);
            if (acquireContentProviderClient != null) {
                acquireContentProviderClient.release();
                SliceViewManagerBase.this.mContext.getContentResolver().registerContentObserver(this.mUri, true, this.mObserver);
                tryPin();
            }
        }

        /* access modifiers changed from: 0000 */
        public void tryPin() {
            if (!this.mPinned) {
                try {
                    SliceViewManagerBase.this.pinSlice(this.mUri);
                    this.mPinned = true;
                } catch (SecurityException unused) {
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void stopListening() {
            SliceViewManagerBase.this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
            if (this.mPinned) {
                SliceViewManagerBase.this.unpinSlice(this.mUri);
                this.mPinned = false;
            }
        }
    }

    SliceViewManagerBase(Context context) {
        this.mContext = context;
    }

    public void registerSliceCallback(Uri uri, SliceCallback sliceCallback) {
        final Handler handler = new Handler(Looper.getMainLooper());
        registerSliceCallback(uri, new Executor(this) {
            public void execute(Runnable runnable) {
                handler.post(runnable);
            }
        }, sliceCallback);
    }

    public void registerSliceCallback(Uri uri, Executor executor, SliceCallback sliceCallback) {
        SliceListenerImpl sliceListenerImpl = new SliceListenerImpl(uri, executor, sliceCallback);
        getListener(uri, sliceCallback, sliceListenerImpl);
        sliceListenerImpl.startListening();
    }

    public void unregisterSliceCallback(Uri uri, SliceCallback sliceCallback) {
        synchronized (this.mListenerLookup) {
            SliceListenerImpl sliceListenerImpl = (SliceListenerImpl) this.mListenerLookup.remove(new Pair(uri, sliceCallback));
            if (sliceListenerImpl != null) {
                sliceListenerImpl.stopListening();
            }
        }
    }

    private SliceListenerImpl getListener(Uri uri, SliceCallback sliceCallback, SliceListenerImpl sliceListenerImpl) {
        Pair pair = new Pair(uri, sliceCallback);
        synchronized (this.mListenerLookup) {
            SliceListenerImpl sliceListenerImpl2 = (SliceListenerImpl) this.mListenerLookup.put(pair, sliceListenerImpl);
            if (sliceListenerImpl2 != null) {
                sliceListenerImpl2.stopListening();
            }
        }
        return sliceListenerImpl;
    }
}
