package androidx.mediarouter.media;

import android.media.MediaRouter;
import android.media.MediaRouter.Callback;

final class MediaRouterJellybeanMr2 {

    public static final class RouteInfo {
        public static CharSequence getDescription(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getDescription();
        }

        public static boolean isConnecting(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).isConnecting();
        }
    }

    public static final class UserRouteInfo {
        public static void setDescription(Object obj, CharSequence charSequence) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setDescription(charSequence);
        }
    }

    public static Object getDefaultRoute(Object obj) {
        return ((MediaRouter) obj).getDefaultRoute();
    }

    public static void addCallback(Object obj, int i, Object obj2, int i2) {
        ((MediaRouter) obj).addCallback(i, (Callback) obj2, i2);
    }
}
