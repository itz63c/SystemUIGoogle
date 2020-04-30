package androidx.mediarouter.app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouter.Callback;

public class MediaRouteDiscoveryFragment extends Fragment {
    private Callback mCallback;
    private MediaRouter mRouter;
    private MediaRouteSelector mSelector;

    public int onPrepareCallbackFlags() {
        return 4;
    }

    private void ensureRouter() {
        if (this.mRouter == null) {
            this.mRouter = MediaRouter.getInstance(getContext());
        }
    }

    private void ensureRouteSelector() {
        if (this.mSelector == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                this.mSelector = MediaRouteSelector.fromBundle(arguments.getBundle("selector"));
            }
            if (this.mSelector == null) {
                this.mSelector = MediaRouteSelector.EMPTY;
            }
        }
    }

    public Callback onCreateCallback() {
        return new Callback(this) {
        };
    }

    public void onStart() {
        super.onStart();
        ensureRouteSelector();
        ensureRouter();
        Callback onCreateCallback = onCreateCallback();
        this.mCallback = onCreateCallback;
        if (onCreateCallback != null) {
            this.mRouter.addCallback(this.mSelector, onCreateCallback, onPrepareCallbackFlags());
        }
    }

    public void onStop() {
        Callback callback = this.mCallback;
        if (callback != null) {
            this.mRouter.removeCallback(callback);
            this.mCallback = null;
        }
        super.onStop();
    }
}
