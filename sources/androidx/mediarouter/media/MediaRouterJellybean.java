package androidx.mediarouter.media;

import android.content.Context;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteCategory;
import android.media.MediaRouter.RouteGroup;
import android.os.Build.VERSION;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class MediaRouterJellybean {

    public interface Callback {
        void onRouteAdded(Object obj);

        void onRouteChanged(Object obj);

        void onRouteGrouped(Object obj, Object obj2, int i);

        void onRouteRemoved(Object obj);

        void onRouteSelected(int i, Object obj);

        void onRouteUngrouped(Object obj, Object obj2);

        void onRouteUnselected(int i, Object obj);

        void onRouteVolumeChanged(Object obj);
    }

    static class CallbackProxy<T extends Callback> extends android.media.MediaRouter.Callback {
        protected final T mCallback;

        public CallbackProxy(T t) {
            this.mCallback = t;
        }

        public void onRouteSelected(MediaRouter mediaRouter, int i, android.media.MediaRouter.RouteInfo routeInfo) {
            this.mCallback.onRouteSelected(i, routeInfo);
        }

        public void onRouteUnselected(MediaRouter mediaRouter, int i, android.media.MediaRouter.RouteInfo routeInfo) {
            this.mCallback.onRouteUnselected(i, routeInfo);
        }

        public void onRouteAdded(MediaRouter mediaRouter, android.media.MediaRouter.RouteInfo routeInfo) {
            this.mCallback.onRouteAdded(routeInfo);
        }

        public void onRouteRemoved(MediaRouter mediaRouter, android.media.MediaRouter.RouteInfo routeInfo) {
            this.mCallback.onRouteRemoved(routeInfo);
        }

        public void onRouteChanged(MediaRouter mediaRouter, android.media.MediaRouter.RouteInfo routeInfo) {
            this.mCallback.onRouteChanged(routeInfo);
        }

        public void onRouteGrouped(MediaRouter mediaRouter, android.media.MediaRouter.RouteInfo routeInfo, RouteGroup routeGroup, int i) {
            this.mCallback.onRouteGrouped(routeInfo, routeGroup, i);
        }

        public void onRouteUngrouped(MediaRouter mediaRouter, android.media.MediaRouter.RouteInfo routeInfo, RouteGroup routeGroup) {
            this.mCallback.onRouteUngrouped(routeInfo, routeGroup);
        }

        public void onRouteVolumeChanged(MediaRouter mediaRouter, android.media.MediaRouter.RouteInfo routeInfo) {
            this.mCallback.onRouteVolumeChanged(routeInfo);
        }
    }

    public static final class GetDefaultRouteWorkaround {
        private Method mGetSystemAudioRouteMethod;

        public GetDefaultRouteWorkaround() {
            int i = VERSION.SDK_INT;
            if (i < 16 || i > 17) {
                throw new UnsupportedOperationException();
            }
            try {
                this.mGetSystemAudioRouteMethod = MediaRouter.class.getMethod("getSystemAudioRoute", new Class[0]);
            } catch (NoSuchMethodException unused) {
            }
        }

        public Object getDefaultRoute(Object obj) {
            MediaRouter mediaRouter = (MediaRouter) obj;
            Method method = this.mGetSystemAudioRouteMethod;
            if (method != null) {
                try {
                    return method.invoke(mediaRouter, new Object[0]);
                } catch (IllegalAccessException | InvocationTargetException unused) {
                }
            }
            return mediaRouter.getRouteAt(0);
        }
    }

    public static final class RouteInfo {
        public static CharSequence getName(Object obj, Context context) {
            return ((android.media.MediaRouter.RouteInfo) obj).getName(context);
        }

        public static int getSupportedTypes(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getSupportedTypes();
        }

        public static int getPlaybackType(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getPlaybackType();
        }

        public static int getPlaybackStream(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getPlaybackStream();
        }

        public static int getVolume(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getVolume();
        }

        public static int getVolumeMax(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getVolumeMax();
        }

        public static int getVolumeHandling(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getVolumeHandling();
        }

        public static Object getTag(Object obj) {
            return ((android.media.MediaRouter.RouteInfo) obj).getTag();
        }

        public static void setTag(Object obj, Object obj2) {
            ((android.media.MediaRouter.RouteInfo) obj).setTag(obj2);
        }

        public static void requestSetVolume(Object obj, int i) {
            ((android.media.MediaRouter.RouteInfo) obj).requestSetVolume(i);
        }

        public static void requestUpdateVolume(Object obj, int i) {
            ((android.media.MediaRouter.RouteInfo) obj).requestUpdateVolume(i);
        }
    }

    public static final class SelectRouteWorkaround {
        private Method mSelectRouteIntMethod;

        public SelectRouteWorkaround() {
            int i = VERSION.SDK_INT;
            if (i < 16 || i > 17) {
                throw new UnsupportedOperationException();
            }
            try {
                this.mSelectRouteIntMethod = MediaRouter.class.getMethod("selectRouteInt", new Class[]{Integer.TYPE, android.media.MediaRouter.RouteInfo.class});
            } catch (NoSuchMethodException unused) {
            }
        }

        public void selectRoute(Object obj, int i, Object obj2) {
            String str = "Cannot programmatically select non-user route.  Media routing may not work.";
            MediaRouter mediaRouter = (MediaRouter) obj;
            android.media.MediaRouter.RouteInfo routeInfo = (android.media.MediaRouter.RouteInfo) obj2;
            if ((routeInfo.getSupportedTypes() & 8388608) == 0) {
                Method method = this.mSelectRouteIntMethod;
                String str2 = "MediaRouterJellybean";
                if (method != null) {
                    try {
                        method.invoke(mediaRouter, new Object[]{Integer.valueOf(i), routeInfo});
                        return;
                    } catch (IllegalAccessException e) {
                        Log.w(str2, str, e);
                    } catch (InvocationTargetException e2) {
                        Log.w(str2, str, e2);
                    }
                } else {
                    Log.w(str2, "Cannot programmatically select non-user route because the platform is missing the selectRouteInt() method.  Media routing may not work.");
                }
            }
            mediaRouter.selectRoute(i, routeInfo);
        }
    }

    public static final class UserRouteInfo {
        public static void setName(Object obj, CharSequence charSequence) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setName(charSequence);
        }

        public static void setPlaybackType(Object obj, int i) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setPlaybackType(i);
        }

        public static void setPlaybackStream(Object obj, int i) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setPlaybackStream(i);
        }

        public static void setVolume(Object obj, int i) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setVolume(i);
        }

        public static void setVolumeMax(Object obj, int i) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setVolumeMax(i);
        }

        public static void setVolumeHandling(Object obj, int i) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setVolumeHandling(i);
        }

        public static void setVolumeCallback(Object obj, Object obj2) {
            ((android.media.MediaRouter.UserRouteInfo) obj).setVolumeCallback((android.media.MediaRouter.VolumeCallback) obj2);
        }
    }

    public interface VolumeCallback {
        void onVolumeSetRequest(Object obj, int i);

        void onVolumeUpdateRequest(Object obj, int i);
    }

    static class VolumeCallbackProxy<T extends VolumeCallback> extends android.media.MediaRouter.VolumeCallback {
        protected final T mCallback;

        public VolumeCallbackProxy(T t) {
            this.mCallback = t;
        }

        public void onVolumeSetRequest(android.media.MediaRouter.RouteInfo routeInfo, int i) {
            this.mCallback.onVolumeSetRequest(routeInfo, i);
        }

        public void onVolumeUpdateRequest(android.media.MediaRouter.RouteInfo routeInfo, int i) {
            this.mCallback.onVolumeUpdateRequest(routeInfo, i);
        }
    }

    public static Object getMediaRouter(Context context) {
        return context.getSystemService("media_router");
    }

    public static List getRoutes(Object obj) {
        MediaRouter mediaRouter = (MediaRouter) obj;
        int routeCount = mediaRouter.getRouteCount();
        ArrayList arrayList = new ArrayList(routeCount);
        for (int i = 0; i < routeCount; i++) {
            arrayList.add(mediaRouter.getRouteAt(i));
        }
        return arrayList;
    }

    public static Object getSelectedRoute(Object obj, int i) {
        return ((MediaRouter) obj).getSelectedRoute(i);
    }

    public static void selectRoute(Object obj, int i, Object obj2) {
        ((MediaRouter) obj).selectRoute(i, (android.media.MediaRouter.RouteInfo) obj2);
    }

    public static void addCallback(Object obj, int i, Object obj2) {
        ((MediaRouter) obj).addCallback(i, (android.media.MediaRouter.Callback) obj2);
    }

    public static void removeCallback(Object obj, Object obj2) {
        ((MediaRouter) obj).removeCallback((android.media.MediaRouter.Callback) obj2);
    }

    public static Object createRouteCategory(Object obj, String str, boolean z) {
        return ((MediaRouter) obj).createRouteCategory(str, z);
    }

    public static Object createUserRoute(Object obj, Object obj2) {
        return ((MediaRouter) obj).createUserRoute((RouteCategory) obj2);
    }

    public static void addUserRoute(Object obj, Object obj2) {
        ((MediaRouter) obj).addUserRoute((android.media.MediaRouter.UserRouteInfo) obj2);
    }

    public static void removeUserRoute(Object obj, Object obj2) {
        ((MediaRouter) obj).removeUserRoute((android.media.MediaRouter.UserRouteInfo) obj2);
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }

    public static Object createVolumeCallback(VolumeCallback volumeCallback) {
        return new VolumeCallbackProxy(volumeCallback);
    }
}
