package androidx.mediarouter.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import androidx.core.view.ActionProvider;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouter.Callback;
import androidx.mediarouter.media.MediaRouter.ProviderInfo;
import androidx.mediarouter.media.MediaRouter.RouteInfo;
import java.lang.ref.WeakReference;

public class MediaRouteActionProvider extends ActionProvider {
    private boolean mAlwaysVisible;
    private MediaRouteButton mButton;
    private MediaRouteDialogFactory mDialogFactory = MediaRouteDialogFactory.getDefault();
    private final MediaRouter mRouter;
    private MediaRouteSelector mSelector = MediaRouteSelector.EMPTY;
    private boolean mUseDynamicGroup;

    private static final class MediaRouterCallback extends Callback {
        private final WeakReference<MediaRouteActionProvider> mProviderWeak;

        public MediaRouterCallback(MediaRouteActionProvider mediaRouteActionProvider) {
            this.mProviderWeak = new WeakReference<>(mediaRouteActionProvider);
        }

        public void onRouteAdded(MediaRouter mediaRouter, RouteInfo routeInfo) {
            refreshRoute(mediaRouter);
        }

        public void onRouteRemoved(MediaRouter mediaRouter, RouteInfo routeInfo) {
            refreshRoute(mediaRouter);
        }

        public void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            refreshRoute(mediaRouter);
        }

        public void onProviderAdded(MediaRouter mediaRouter, ProviderInfo providerInfo) {
            refreshRoute(mediaRouter);
        }

        public void onProviderRemoved(MediaRouter mediaRouter, ProviderInfo providerInfo) {
            refreshRoute(mediaRouter);
        }

        public void onProviderChanged(MediaRouter mediaRouter, ProviderInfo providerInfo) {
            refreshRoute(mediaRouter);
        }

        private void refreshRoute(MediaRouter mediaRouter) {
            MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) this.mProviderWeak.get();
            if (mediaRouteActionProvider != null) {
                mediaRouteActionProvider.refreshRoute();
            } else {
                mediaRouter.removeCallback(this);
            }
        }
    }

    public boolean overridesItemVisibility() {
        return true;
    }

    public MediaRouteActionProvider(Context context) {
        super(context);
        this.mRouter = MediaRouter.getInstance(context);
        new MediaRouterCallback(this);
    }

    public MediaRouteButton onCreateMediaRouteButton() {
        return new MediaRouteButton(getContext());
    }

    public View onCreateActionView() {
        if (this.mButton != null) {
            Log.e("MRActionProvider", "onCreateActionView: this ActionProvider is already associated with a menu item. Don't reuse MediaRouteActionProvider instances! Abandoning the old menu item...");
        }
        MediaRouteButton onCreateMediaRouteButton = onCreateMediaRouteButton();
        this.mButton = onCreateMediaRouteButton;
        onCreateMediaRouteButton.setCheatSheetEnabled(true);
        this.mButton.setRouteSelector(this.mSelector);
        if (this.mUseDynamicGroup) {
            this.mButton.enableDynamicGroup();
        }
        this.mButton.setAlwaysVisible(this.mAlwaysVisible);
        this.mButton.setDialogFactory(this.mDialogFactory);
        this.mButton.setLayoutParams(new LayoutParams(-2, -1));
        return this.mButton;
    }

    public boolean onPerformDefaultAction() {
        MediaRouteButton mediaRouteButton = this.mButton;
        if (mediaRouteButton != null) {
            return mediaRouteButton.showDialog();
        }
        return false;
    }

    public boolean isVisible() {
        return this.mAlwaysVisible || this.mRouter.isRouteAvailable(this.mSelector, 1);
    }

    /* access modifiers changed from: 0000 */
    public void refreshRoute() {
        refreshVisibility();
    }
}
