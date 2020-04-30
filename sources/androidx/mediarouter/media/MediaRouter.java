package androidx.mediarouter.media;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.support.p000v4.media.session.MediaSessionCompat.Token;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.ActivityManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.display.DisplayManagerCompat;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Pair;
import androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController;
import androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor;
import androidx.mediarouter.media.MediaRouteProvider.ProviderMetadata;
import androidx.mediarouter.media.MediaRouteProvider.RouteController;
import androidx.mediarouter.media.MediaRouteSelector.Builder;
import androidx.mediarouter.media.RemoteControlClientCompat.PlaybackInfo;
import androidx.mediarouter.media.SystemMediaRouteProvider.SyncCallback;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public final class MediaRouter {
    static final boolean DEBUG = Log.isLoggable("MediaRouter", 3);
    static GlobalMediaRouter sGlobal;
    final ArrayList<CallbackRecord> mCallbackRecords = new ArrayList<>();
    final Context mContext;

    public static abstract class Callback {
        public void onProviderAdded(MediaRouter mediaRouter, ProviderInfo providerInfo) {
        }

        public void onProviderChanged(MediaRouter mediaRouter, ProviderInfo providerInfo) {
        }

        public void onProviderRemoved(MediaRouter mediaRouter, ProviderInfo providerInfo) {
        }

        public void onRouteAdded(MediaRouter mediaRouter, RouteInfo routeInfo) {
        }

        public void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
        }

        public void onRoutePresentationDisplayChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
        }

        public void onRouteRemoved(MediaRouter mediaRouter, RouteInfo routeInfo) {
        }

        public void onRouteSelected(MediaRouter mediaRouter, RouteInfo routeInfo) {
        }

        public void onRouteUnselected(MediaRouter mediaRouter, RouteInfo routeInfo) {
        }

        public void onRouteVolumeChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
        }

        public void onRouteUnselected(MediaRouter mediaRouter, RouteInfo routeInfo, int i) {
            onRouteUnselected(mediaRouter, routeInfo);
        }
    }

    private static final class CallbackRecord {
        public final Callback mCallback;
        public int mFlags;
        public final MediaRouter mRouter;
        public MediaRouteSelector mSelector = MediaRouteSelector.EMPTY;

        public CallbackRecord(MediaRouter mediaRouter, Callback callback) {
            this.mRouter = mediaRouter;
            this.mCallback = callback;
        }

        public boolean filterRouteEvent(RouteInfo routeInfo) {
            return (this.mFlags & 2) != 0 || routeInfo.matchesSelector(this.mSelector);
        }
    }

    public static abstract class ControlRequestCallback {
        public void onError(String str, Bundle bundle) {
        }

        public void onResult(Bundle bundle) {
        }
    }

    private static final class GlobalMediaRouter implements SyncCallback, androidx.mediarouter.media.RegisteredMediaRouteProviderWatcher.Callback {
        final Context mApplicationContext;
        private RouteInfo mBluetoothRoute;
        final CallbackHandler mCallbackHandler = new CallbackHandler();
        private MediaSessionCompat mCompatSession;
        private RouteInfo mDefaultRoute;
        private MediaRouteDiscoveryRequest mDiscoveryRequest;
        OnDynamicRoutesChangedListener mDynamicRoutesListener = new OnDynamicRoutesChangedListener() {
            public void onRoutesChanged(DynamicGroupRouteController dynamicGroupRouteController, Collection<DynamicRouteDescriptor> collection) {
                GlobalMediaRouter globalMediaRouter = GlobalMediaRouter.this;
                if (dynamicGroupRouteController == globalMediaRouter.mSelectedRouteController) {
                    globalMediaRouter.mSelectedRoute.updateDescriptors(collection);
                }
            }
        };
        private final boolean mLowRam;
        private MediaSessionRecord mMediaSession;
        final PlaybackInfo mPlaybackInfo = new PlaybackInfo();
        private final ProviderCallback mProviderCallback = new ProviderCallback();
        private final ArrayList<ProviderInfo> mProviders = new ArrayList<>();
        private RegisteredMediaRouteProviderWatcher mRegisteredProviderWatcher;
        private final ArrayList<RemoteControlClientRecord> mRemoteControlClients = new ArrayList<>();
        private final Map<String, RouteController> mRouteControllerMap = new HashMap();
        final ArrayList<WeakReference<MediaRouter>> mRouters = new ArrayList<>();
        private final ArrayList<RouteInfo> mRoutes = new ArrayList<>();
        RouteInfo mSelectedRoute;
        RouteController mSelectedRouteController;
        final SystemMediaRouteProvider mSystemProvider;
        private final Map<Pair<String, String>, String> mUniqueIdMap = new HashMap();

        private final class CallbackHandler extends Handler {
            private final ArrayList<CallbackRecord> mTempCallbackRecords = new ArrayList<>();

            CallbackHandler() {
            }

            public void post(int i, Object obj) {
                obtainMessage(i, obj).sendToTarget();
            }

            public void post(int i, Object obj, int i2) {
                Message obtainMessage = obtainMessage(i, obj);
                obtainMessage.arg1 = i2;
                obtainMessage.sendToTarget();
            }

            public void handleMessage(Message message) {
                int i = message.what;
                Object obj = message.obj;
                int i2 = message.arg1;
                if (i == 259 && GlobalMediaRouter.this.getSelectedRoute().getId().equals(((RouteInfo) obj).getId())) {
                    GlobalMediaRouter.this.updateSelectedRouteIfNeeded(true);
                }
                syncWithSystemProvider(i, obj);
                try {
                    int size = GlobalMediaRouter.this.mRouters.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            break;
                        }
                        MediaRouter mediaRouter = (MediaRouter) ((WeakReference) GlobalMediaRouter.this.mRouters.get(size)).get();
                        if (mediaRouter == null) {
                            GlobalMediaRouter.this.mRouters.remove(size);
                        } else {
                            this.mTempCallbackRecords.addAll(mediaRouter.mCallbackRecords);
                        }
                    }
                    int size2 = this.mTempCallbackRecords.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        invokeCallback((CallbackRecord) this.mTempCallbackRecords.get(i3), i, obj, i2);
                    }
                } finally {
                    this.mTempCallbackRecords.clear();
                }
            }

            private void syncWithSystemProvider(int i, Object obj) {
                if (i != 262) {
                    switch (i) {
                        case 257:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteAdded((RouteInfo) obj);
                            return;
                        case 258:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteRemoved((RouteInfo) obj);
                            return;
                        case 259:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteChanged((RouteInfo) obj);
                            return;
                        default:
                            return;
                    }
                } else {
                    GlobalMediaRouter.this.mSystemProvider.onSyncRouteSelected((RouteInfo) obj);
                }
            }

            private void invokeCallback(CallbackRecord callbackRecord, int i, Object obj, int i2) {
                MediaRouter mediaRouter = callbackRecord.mRouter;
                Callback callback = callbackRecord.mCallback;
                int i3 = 65280 & i;
                if (i3 == 256) {
                    RouteInfo routeInfo = (RouteInfo) obj;
                    if (callbackRecord.filterRouteEvent(routeInfo)) {
                        switch (i) {
                            case 257:
                                callback.onRouteAdded(mediaRouter, routeInfo);
                                return;
                            case 258:
                                callback.onRouteRemoved(mediaRouter, routeInfo);
                                return;
                            case 259:
                                callback.onRouteChanged(mediaRouter, routeInfo);
                                return;
                            case 260:
                                callback.onRouteVolumeChanged(mediaRouter, routeInfo);
                                return;
                            case 261:
                                callback.onRoutePresentationDisplayChanged(mediaRouter, routeInfo);
                                return;
                            case 262:
                                callback.onRouteSelected(mediaRouter, routeInfo);
                                return;
                            case 263:
                                callback.onRouteUnselected(mediaRouter, routeInfo, i2);
                                return;
                            default:
                                return;
                        }
                    }
                } else if (i3 == 512) {
                    ProviderInfo providerInfo = (ProviderInfo) obj;
                    switch (i) {
                        case 513:
                            callback.onProviderAdded(mediaRouter, providerInfo);
                            return;
                        case 514:
                            callback.onProviderRemoved(mediaRouter, providerInfo);
                            return;
                        case 515:
                            callback.onProviderChanged(mediaRouter, providerInfo);
                            return;
                        default:
                            return;
                    }
                }
            }
        }

        private final class MediaSessionRecord {
            public abstract void clearVolumeHandling();

            public abstract void configureVolume(int i, int i2, int i3);

            public abstract Token getToken();
        }

        private final class ProviderCallback extends androidx.mediarouter.media.MediaRouteProvider.Callback {
            ProviderCallback() {
            }

            public void onDescriptorChanged(MediaRouteProvider mediaRouteProvider, MediaRouteProviderDescriptor mediaRouteProviderDescriptor) {
                GlobalMediaRouter.this.updateProviderDescriptor(mediaRouteProvider, mediaRouteProviderDescriptor);
            }
        }

        private final class RemoteControlClientRecord {
            private final RemoteControlClientCompat mRccCompat;
            final /* synthetic */ GlobalMediaRouter this$0;

            public void updatePlaybackInfo() {
                this.mRccCompat.setPlaybackInfo(this.this$0.mPlaybackInfo);
            }
        }

        @SuppressLint({"SyntheticAccessor"})
        GlobalMediaRouter(Context context) {
            this.mApplicationContext = context;
            DisplayManagerCompat.getInstance(context);
            this.mLowRam = ActivityManagerCompat.isLowRamDevice((ActivityManager) context.getSystemService("activity"));
            this.mSystemProvider = SystemMediaRouteProvider.obtain(context, this);
        }

        public void start() {
            addProvider(this.mSystemProvider);
            RegisteredMediaRouteProviderWatcher registeredMediaRouteProviderWatcher = new RegisteredMediaRouteProviderWatcher(this.mApplicationContext, this);
            this.mRegisteredProviderWatcher = registeredMediaRouteProviderWatcher;
            registeredMediaRouteProviderWatcher.start();
        }

        public MediaRouter getRouter(Context context) {
            int size = this.mRouters.size();
            while (true) {
                size--;
                if (size >= 0) {
                    MediaRouter mediaRouter = (MediaRouter) ((WeakReference) this.mRouters.get(size)).get();
                    if (mediaRouter == null) {
                        this.mRouters.remove(size);
                    } else if (mediaRouter.mContext == context) {
                        return mediaRouter;
                    }
                } else {
                    MediaRouter mediaRouter2 = new MediaRouter(context);
                    this.mRouters.add(new WeakReference(mediaRouter2));
                    return mediaRouter2;
                }
            }
        }

        public void requestSetVolume(RouteInfo routeInfo, int i) {
            if (routeInfo == this.mSelectedRoute) {
                RouteController routeController = this.mSelectedRouteController;
                if (routeController != null) {
                    routeController.onSetVolume(i);
                    return;
                }
            }
            if (!this.mRouteControllerMap.isEmpty()) {
                RouteController routeController2 = (RouteController) this.mRouteControllerMap.get(routeInfo.mUniqueId);
                if (routeController2 != null) {
                    routeController2.onSetVolume(i);
                }
            }
        }

        public void requestUpdateVolume(RouteInfo routeInfo, int i) {
            if (routeInfo == this.mSelectedRoute) {
                RouteController routeController = this.mSelectedRouteController;
                if (routeController != null) {
                    routeController.onUpdateVolume(i);
                }
            }
        }

        public RouteInfo getRoute(String str) {
            Iterator it = this.mRoutes.iterator();
            while (it.hasNext()) {
                RouteInfo routeInfo = (RouteInfo) it.next();
                if (routeInfo.mUniqueId.equals(str)) {
                    return routeInfo;
                }
            }
            return null;
        }

        public List<RouteInfo> getRoutes() {
            return this.mRoutes;
        }

        /* access modifiers changed from: 0000 */
        public RouteInfo getDefaultRoute() {
            RouteInfo routeInfo = this.mDefaultRoute;
            if (routeInfo != null) {
                return routeInfo;
            }
            throw new IllegalStateException("There is no default route.  The media router has not yet been fully initialized.");
        }

        /* access modifiers changed from: 0000 */
        public RouteInfo getBluetoothRoute() {
            return this.mBluetoothRoute;
        }

        /* access modifiers changed from: 0000 */
        public RouteInfo getSelectedRoute() {
            RouteInfo routeInfo = this.mSelectedRoute;
            if (routeInfo != null) {
                return routeInfo;
            }
            throw new IllegalStateException("There is no currently selected route.  The media router has not yet been fully initialized.");
        }

        /* access modifiers changed from: 0000 */
        public void selectRoute(RouteInfo routeInfo) {
            selectRoute(routeInfo, 3);
        }

        /* access modifiers changed from: 0000 */
        public void addMemberToDynamicGroup(RouteInfo routeInfo) {
            if (this.mSelectedRoute.getDynamicGroupState() == null || !(this.mSelectedRouteController instanceof DynamicGroupRouteController)) {
                throw new IllegalStateException("There is no currently selected dynamic group route.");
            }
            DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
            if (this.mSelectedRoute.getMemberRoutes().contains(routeInfo) || dynamicGroupState == null || !dynamicGroupState.isGroupable()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring attemp to add a non-groupable route to dynamic group : ");
                sb.append(routeInfo);
                Log.w("MediaRouter", sb.toString());
                return;
            }
            ((DynamicGroupRouteController) this.mSelectedRouteController).onAddMemberRoute(routeInfo.getDescriptorId());
        }

        /* access modifiers changed from: 0000 */
        public void removeMemberFromDynamicGroup(RouteInfo routeInfo) {
            if (this.mSelectedRoute.getDynamicGroupState() == null || !(this.mSelectedRouteController instanceof DynamicGroupRouteController)) {
                throw new IllegalStateException("There is no currently selected dynamic group route.");
            }
            DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
            String str = "MediaRouter";
            if (!this.mSelectedRoute.getMemberRoutes().contains(routeInfo) || dynamicGroupState == null || !dynamicGroupState.isUnselectable()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring attempt to remove a non-unselectable member route : ");
                sb.append(routeInfo);
                Log.w(str, sb.toString());
            } else if (this.mSelectedRoute.getMemberRoutes().size() <= 1) {
                Log.w(str, "Ignoring attempt to remove the last member route.");
            } else {
                ((DynamicGroupRouteController) this.mSelectedRouteController).onRemoveMemberRoute(routeInfo.getDescriptorId());
            }
        }

        /* access modifiers changed from: 0000 */
        public void selectRoute(RouteInfo routeInfo, int i) {
            String str = "MediaRouter";
            if (!this.mRoutes.contains(routeInfo)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring attempt to select removed route: ");
                sb.append(routeInfo);
                Log.w(str, sb.toString());
            } else if (!routeInfo.mEnabled) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Ignoring attempt to select disabled route: ");
                sb2.append(routeInfo);
                Log.w(str, sb2.toString());
            } else {
                setSelectedRouteInternal(routeInfo, i);
            }
        }

        public boolean isRouteAvailable(MediaRouteSelector mediaRouteSelector, int i) {
            if (mediaRouteSelector.isEmpty()) {
                return false;
            }
            if ((i & 2) == 0 && this.mLowRam) {
                return true;
            }
            int size = this.mRoutes.size();
            for (int i2 = 0; i2 < size; i2++) {
                RouteInfo routeInfo = (RouteInfo) this.mRoutes.get(i2);
                if (((i & 1) == 0 || !routeInfo.isDefaultOrBluetooth()) && routeInfo.matchesSelector(mediaRouteSelector)) {
                    return true;
                }
            }
            return false;
        }

        public void updateDiscoveryRequest() {
            Builder builder = new Builder();
            int size = this.mRouters.size();
            boolean z = false;
            boolean z2 = false;
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                MediaRouter mediaRouter = (MediaRouter) ((WeakReference) this.mRouters.get(size)).get();
                if (mediaRouter == null) {
                    this.mRouters.remove(size);
                } else {
                    int size2 = mediaRouter.mCallbackRecords.size();
                    for (int i = 0; i < size2; i++) {
                        CallbackRecord callbackRecord = (CallbackRecord) mediaRouter.mCallbackRecords.get(i);
                        builder.addSelector(callbackRecord.mSelector);
                        if ((callbackRecord.mFlags & 1) != 0) {
                            z = true;
                            z2 = true;
                        }
                        if ((callbackRecord.mFlags & 4) != 0 && !this.mLowRam) {
                            z = true;
                        }
                        if ((callbackRecord.mFlags & 8) != 0) {
                            z = true;
                        }
                    }
                }
            }
            MediaRouteSelector build = z ? builder.build() : MediaRouteSelector.EMPTY;
            MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest = this.mDiscoveryRequest;
            if (mediaRouteDiscoveryRequest == null || !mediaRouteDiscoveryRequest.getSelector().equals(build) || this.mDiscoveryRequest.isActiveScan() != z2) {
                if (!build.isEmpty() || z2) {
                    this.mDiscoveryRequest = new MediaRouteDiscoveryRequest(build, z2);
                } else if (this.mDiscoveryRequest != null) {
                    this.mDiscoveryRequest = null;
                } else {
                    return;
                }
                String str = "MediaRouter";
                if (MediaRouter.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Updated discovery request: ");
                    sb.append(this.mDiscoveryRequest);
                    Log.d(str, sb.toString());
                }
                if (z && !z2 && this.mLowRam) {
                    Log.i(str, "Forcing passive route discovery on a low-RAM device, system performance may be affected.  Please consider using CALLBACK_FLAG_REQUEST_DISCOVERY instead of CALLBACK_FLAG_FORCE_DISCOVERY.");
                }
                int size3 = this.mProviders.size();
                for (int i2 = 0; i2 < size3; i2++) {
                    ((ProviderInfo) this.mProviders.get(i2)).mProviderInstance.setDiscoveryRequest(this.mDiscoveryRequest);
                }
            }
        }

        public void addProvider(MediaRouteProvider mediaRouteProvider) {
            if (findProviderInfo(mediaRouteProvider) == null) {
                ProviderInfo providerInfo = new ProviderInfo(mediaRouteProvider);
                this.mProviders.add(providerInfo);
                if (MediaRouter.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Provider added: ");
                    sb.append(providerInfo);
                    Log.d("MediaRouter", sb.toString());
                }
                this.mCallbackHandler.post(513, providerInfo);
                updateProviderContents(providerInfo, mediaRouteProvider.getDescriptor());
                mediaRouteProvider.setCallback(this.mProviderCallback);
                mediaRouteProvider.setDiscoveryRequest(this.mDiscoveryRequest);
            }
        }

        public void removeProvider(MediaRouteProvider mediaRouteProvider) {
            ProviderInfo findProviderInfo = findProviderInfo(mediaRouteProvider);
            if (findProviderInfo != null) {
                mediaRouteProvider.setCallback(null);
                mediaRouteProvider.setDiscoveryRequest(null);
                updateProviderContents(findProviderInfo, null);
                if (MediaRouter.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Provider removed: ");
                    sb.append(findProviderInfo);
                    Log.d("MediaRouter", sb.toString());
                }
                this.mCallbackHandler.post(514, findProviderInfo);
                this.mProviders.remove(findProviderInfo);
            }
        }

        /* access modifiers changed from: 0000 */
        public void updateProviderDescriptor(MediaRouteProvider mediaRouteProvider, MediaRouteProviderDescriptor mediaRouteProviderDescriptor) {
            ProviderInfo findProviderInfo = findProviderInfo(mediaRouteProvider);
            if (findProviderInfo != null) {
                updateProviderContents(findProviderInfo, mediaRouteProviderDescriptor);
            }
        }

        private ProviderInfo findProviderInfo(MediaRouteProvider mediaRouteProvider) {
            int size = this.mProviders.size();
            for (int i = 0; i < size; i++) {
                if (((ProviderInfo) this.mProviders.get(i)).mProviderInstance == mediaRouteProvider) {
                    return (ProviderInfo) this.mProviders.get(i);
                }
            }
            return null;
        }

        private void updateProviderContents(ProviderInfo providerInfo, MediaRouteProviderDescriptor mediaRouteProviderDescriptor) {
            boolean z;
            String str;
            if (providerInfo.updateDescriptor(mediaRouteProviderDescriptor)) {
                int i = 0;
                String str2 = "MediaRouter";
                if (mediaRouteProviderDescriptor == null || (!mediaRouteProviderDescriptor.isValid() && mediaRouteProviderDescriptor != this.mSystemProvider.getDescriptor())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ignoring invalid provider descriptor: ");
                    sb.append(mediaRouteProviderDescriptor);
                    Log.w(str2, sb.toString());
                    z = false;
                } else {
                    List routes = mediaRouteProviderDescriptor.getRoutes();
                    ArrayList<Pair> arrayList = new ArrayList<>();
                    ArrayList<Pair> arrayList2 = new ArrayList<>();
                    Iterator it = routes.iterator();
                    z = false;
                    while (true) {
                        str = "Route added: ";
                        if (!it.hasNext()) {
                            break;
                        }
                        MediaRouteDescriptor mediaRouteDescriptor = (MediaRouteDescriptor) it.next();
                        if (mediaRouteDescriptor == null || !mediaRouteDescriptor.isValid()) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Ignoring invalid system route descriptor: ");
                            sb2.append(mediaRouteDescriptor);
                            Log.w(str2, sb2.toString());
                        } else {
                            String id = mediaRouteDescriptor.getId();
                            int findRouteIndexByDescriptorId = providerInfo.findRouteIndexByDescriptorId(id);
                            if (findRouteIndexByDescriptorId < 0) {
                                RouteInfo routeInfo = new RouteInfo(providerInfo, id, assignRouteUniqueId(providerInfo, id));
                                int i2 = i + 1;
                                providerInfo.mRoutes.add(i, routeInfo);
                                this.mRoutes.add(routeInfo);
                                if (mediaRouteDescriptor.getGroupMemberIds().size() > 0) {
                                    arrayList.add(new Pair(routeInfo, mediaRouteDescriptor));
                                } else {
                                    routeInfo.maybeUpdateDescriptor(mediaRouteDescriptor);
                                    if (MediaRouter.DEBUG) {
                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append(str);
                                        sb3.append(routeInfo);
                                        Log.d(str2, sb3.toString());
                                    }
                                    this.mCallbackHandler.post(257, routeInfo);
                                }
                                i = i2;
                            } else if (findRouteIndexByDescriptorId < i) {
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("Ignoring route descriptor with duplicate id: ");
                                sb4.append(mediaRouteDescriptor);
                                Log.w(str2, sb4.toString());
                            } else {
                                RouteInfo routeInfo2 = (RouteInfo) providerInfo.mRoutes.get(findRouteIndexByDescriptorId);
                                int i3 = i + 1;
                                Collections.swap(providerInfo.mRoutes, findRouteIndexByDescriptorId, i);
                                if (mediaRouteDescriptor.getGroupMemberIds().size() > 0) {
                                    arrayList2.add(new Pair(routeInfo2, mediaRouteDescriptor));
                                } else if (updateRouteDescriptorAndNotify(routeInfo2, mediaRouteDescriptor) != 0 && routeInfo2 == this.mSelectedRoute) {
                                    z = true;
                                }
                                i = i3;
                            }
                        }
                    }
                    for (Pair pair : arrayList) {
                        RouteInfo routeInfo3 = (RouteInfo) pair.first;
                        routeInfo3.maybeUpdateDescriptor((MediaRouteDescriptor) pair.second);
                        if (MediaRouter.DEBUG) {
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append(str);
                            sb5.append(routeInfo3);
                            Log.d(str2, sb5.toString());
                        }
                        this.mCallbackHandler.post(257, routeInfo3);
                    }
                    for (Pair pair2 : arrayList2) {
                        RouteInfo routeInfo4 = (RouteInfo) pair2.first;
                        if (updateRouteDescriptorAndNotify(routeInfo4, (MediaRouteDescriptor) pair2.second) != 0 && routeInfo4 == this.mSelectedRoute) {
                            z = true;
                        }
                    }
                }
                for (int size = providerInfo.mRoutes.size() - 1; size >= i; size--) {
                    RouteInfo routeInfo5 = (RouteInfo) providerInfo.mRoutes.get(size);
                    routeInfo5.maybeUpdateDescriptor(null);
                    this.mRoutes.remove(routeInfo5);
                }
                updateSelectedRouteIfNeeded(z);
                for (int size2 = providerInfo.mRoutes.size() - 1; size2 >= i; size2--) {
                    RouteInfo routeInfo6 = (RouteInfo) providerInfo.mRoutes.remove(size2);
                    if (MediaRouter.DEBUG) {
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("Route removed: ");
                        sb6.append(routeInfo6);
                        Log.d(str2, sb6.toString());
                    }
                    this.mCallbackHandler.post(258, routeInfo6);
                }
                if (MediaRouter.DEBUG) {
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("Provider changed: ");
                    sb7.append(providerInfo);
                    Log.d(str2, sb7.toString());
                }
                this.mCallbackHandler.post(515, providerInfo);
            }
        }

        private int updateRouteDescriptorAndNotify(RouteInfo routeInfo, MediaRouteDescriptor mediaRouteDescriptor) {
            int maybeUpdateDescriptor = routeInfo.maybeUpdateDescriptor(mediaRouteDescriptor);
            if (maybeUpdateDescriptor != 0) {
                String str = "MediaRouter";
                if ((maybeUpdateDescriptor & 1) != 0) {
                    if (MediaRouter.DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Route changed: ");
                        sb.append(routeInfo);
                        Log.d(str, sb.toString());
                    }
                    this.mCallbackHandler.post(259, routeInfo);
                }
                if ((maybeUpdateDescriptor & 2) != 0) {
                    if (MediaRouter.DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Route volume changed: ");
                        sb2.append(routeInfo);
                        Log.d(str, sb2.toString());
                    }
                    this.mCallbackHandler.post(260, routeInfo);
                }
                if ((maybeUpdateDescriptor & 4) != 0) {
                    if (MediaRouter.DEBUG) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Route presentation display changed: ");
                        sb3.append(routeInfo);
                        Log.d(str, sb3.toString());
                    }
                    this.mCallbackHandler.post(261, routeInfo);
                }
            }
            return maybeUpdateDescriptor;
        }

        /* access modifiers changed from: 0000 */
        public String assignRouteUniqueId(ProviderInfo providerInfo, String str) {
            String flattenToShortString = providerInfo.getComponentName().flattenToShortString();
            StringBuilder sb = new StringBuilder();
            sb.append(flattenToShortString);
            sb.append(":");
            sb.append(str);
            String sb2 = sb.toString();
            if (findRouteByUniqueId(sb2) < 0) {
                this.mUniqueIdMap.put(new Pair(flattenToShortString, str), sb2);
                return sb2;
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Either ");
            sb3.append(str);
            sb3.append(" isn't unique in ");
            sb3.append(flattenToShortString);
            sb3.append(" or we're trying to assign a unique ID for an already added route");
            Log.w("MediaRouter", sb3.toString());
            int i = 2;
            while (true) {
                String format = String.format(Locale.US, "%s_%d", new Object[]{sb2, Integer.valueOf(i)});
                if (findRouteByUniqueId(format) < 0) {
                    this.mUniqueIdMap.put(new Pair(flattenToShortString, str), format);
                    return format;
                }
                i++;
            }
        }

        private int findRouteByUniqueId(String str) {
            int size = this.mRoutes.size();
            for (int i = 0; i < size; i++) {
                if (((RouteInfo) this.mRoutes.get(i)).mUniqueId.equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        /* access modifiers changed from: 0000 */
        public String getUniqueId(ProviderInfo providerInfo, String str) {
            return (String) this.mUniqueIdMap.get(new Pair(providerInfo.getComponentName().flattenToShortString(), str));
        }

        /* access modifiers changed from: 0000 */
        public void updateSelectedRouteIfNeeded(boolean z) {
            RouteInfo routeInfo = this.mDefaultRoute;
            String str = "MediaRouter";
            if (routeInfo != null && !routeInfo.isSelectable()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Clearing the default route because it is no longer selectable: ");
                sb.append(this.mDefaultRoute);
                Log.i(str, sb.toString());
                this.mDefaultRoute = null;
            }
            if (this.mDefaultRoute == null && !this.mRoutes.isEmpty()) {
                Iterator it = this.mRoutes.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    RouteInfo routeInfo2 = (RouteInfo) it.next();
                    if (isSystemDefaultRoute(routeInfo2) && routeInfo2.isSelectable()) {
                        this.mDefaultRoute = routeInfo2;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Found default route: ");
                        sb2.append(this.mDefaultRoute);
                        Log.i(str, sb2.toString());
                        break;
                    }
                }
            }
            RouteInfo routeInfo3 = this.mBluetoothRoute;
            if (routeInfo3 != null && !routeInfo3.isSelectable()) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Clearing the bluetooth route because it is no longer selectable: ");
                sb3.append(this.mBluetoothRoute);
                Log.i(str, sb3.toString());
                this.mBluetoothRoute = null;
            }
            if (this.mBluetoothRoute == null && !this.mRoutes.isEmpty()) {
                Iterator it2 = this.mRoutes.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    RouteInfo routeInfo4 = (RouteInfo) it2.next();
                    if (isSystemLiveAudioOnlyRoute(routeInfo4) && routeInfo4.isSelectable()) {
                        this.mBluetoothRoute = routeInfo4;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("Found bluetooth route: ");
                        sb4.append(this.mBluetoothRoute);
                        Log.i(str, sb4.toString());
                        break;
                    }
                }
            }
            RouteInfo routeInfo5 = this.mSelectedRoute;
            if (routeInfo5 == null || !routeInfo5.isEnabled()) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("Unselecting the current route because it is no longer selectable: ");
                sb5.append(this.mSelectedRoute);
                Log.i(str, sb5.toString());
                setSelectedRouteInternal(chooseFallbackRoute(), 0);
            } else if (z) {
                if (this.mSelectedRoute.isGroup()) {
                    List<RouteInfo> memberRoutes = this.mSelectedRoute.getMemberRoutes();
                    HashSet hashSet = new HashSet();
                    for (RouteInfo routeInfo6 : memberRoutes) {
                        hashSet.add(routeInfo6.mUniqueId);
                    }
                    Iterator it3 = this.mRouteControllerMap.entrySet().iterator();
                    while (it3.hasNext()) {
                        Entry entry = (Entry) it3.next();
                        if (!hashSet.contains(entry.getKey())) {
                            RouteController routeController = (RouteController) entry.getValue();
                            routeController.onUnselect();
                            routeController.onRelease();
                            it3.remove();
                        }
                    }
                    for (RouteInfo routeInfo7 : memberRoutes) {
                        if (!this.mRouteControllerMap.containsKey(routeInfo7.mUniqueId)) {
                            RouteController onCreateRouteController = routeInfo7.getProviderInstance().onCreateRouteController(routeInfo7.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                            onCreateRouteController.onSelect();
                            this.mRouteControllerMap.put(routeInfo7.mUniqueId, onCreateRouteController);
                        }
                    }
                }
                updatePlaybackInfoFromSelectedRoute();
            }
        }

        /* access modifiers changed from: 0000 */
        public RouteInfo chooseFallbackRoute() {
            Iterator it = this.mRoutes.iterator();
            while (it.hasNext()) {
                RouteInfo routeInfo = (RouteInfo) it.next();
                if (routeInfo != this.mDefaultRoute && isSystemLiveAudioOnlyRoute(routeInfo) && routeInfo.isSelectable()) {
                    return routeInfo;
                }
            }
            return this.mDefaultRoute;
        }

        private boolean isSystemLiveAudioOnlyRoute(RouteInfo routeInfo) {
            return routeInfo.getProviderInstance() == this.mSystemProvider && routeInfo.supportsControlCategory("android.media.intent.category.LIVE_AUDIO") && !routeInfo.supportsControlCategory("android.media.intent.category.LIVE_VIDEO");
        }

        private boolean isSystemDefaultRoute(RouteInfo routeInfo) {
            return routeInfo.getProviderInstance() == this.mSystemProvider && routeInfo.mDescriptorId.equals("DEFAULT_ROUTE");
        }

        private void setSelectedRouteInternal(RouteInfo routeInfo, int i) {
            String str = "MediaRouter";
            if (MediaRouter.sGlobal == null || (this.mBluetoothRoute != null && routeInfo.isDefault())) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                StringBuilder sb = new StringBuilder();
                for (int i2 = 3; i2 < stackTrace.length; i2++) {
                    StackTraceElement stackTraceElement = stackTrace[i2];
                    sb.append(stackTraceElement.getClassName());
                    sb.append(".");
                    sb.append(stackTraceElement.getMethodName());
                    sb.append(":");
                    sb.append(stackTraceElement.getLineNumber());
                    sb.append("  ");
                }
                String str2 = ", callers=";
                if (MediaRouter.sGlobal == null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("setSelectedRouteInternal is called while sGlobal is null: pkgName=");
                    sb2.append(this.mApplicationContext.getPackageName());
                    sb2.append(str2);
                    sb2.append(sb.toString());
                    Log.w(str, sb2.toString());
                } else {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Default route is selected while a BT route is available: pkgName=");
                    sb3.append(this.mApplicationContext.getPackageName());
                    sb3.append(str2);
                    sb3.append(sb.toString());
                    Log.w(str, sb3.toString());
                }
            }
            RouteInfo routeInfo2 = this.mSelectedRoute;
            if (routeInfo2 != routeInfo) {
                if (routeInfo2 != null) {
                    if (MediaRouter.DEBUG) {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("Route unselected: ");
                        sb4.append(this.mSelectedRoute);
                        sb4.append(" reason: ");
                        sb4.append(i);
                        Log.d(str, sb4.toString());
                    }
                    this.mCallbackHandler.post(263, this.mSelectedRoute, i);
                    RouteController routeController = this.mSelectedRouteController;
                    if (routeController != null) {
                        routeController.onUnselect(i);
                        this.mSelectedRouteController.onRelease();
                        this.mSelectedRouteController = null;
                    }
                    if (!this.mRouteControllerMap.isEmpty()) {
                        for (RouteController routeController2 : this.mRouteControllerMap.values()) {
                            routeController2.onUnselect(i);
                            routeController2.onRelease();
                        }
                        this.mRouteControllerMap.clear();
                    }
                }
                if (routeInfo.getProvider().supportsDynamicGroup()) {
                    DynamicGroupRouteController onCreateDynamicGroupRouteController = routeInfo.getProviderInstance().onCreateDynamicGroupRouteController(routeInfo.mDescriptorId);
                    onCreateDynamicGroupRouteController.setOnDynamicRoutesChangedListener(ContextCompat.getMainExecutor(this.mApplicationContext), this.mDynamicRoutesListener);
                    this.mSelectedRouteController = onCreateDynamicGroupRouteController;
                    this.mSelectedRoute = routeInfo;
                } else {
                    this.mSelectedRouteController = routeInfo.getProviderInstance().onCreateRouteController(routeInfo.mDescriptorId);
                    this.mSelectedRoute = routeInfo;
                }
                RouteController routeController3 = this.mSelectedRouteController;
                if (routeController3 != null) {
                    routeController3.onSelect();
                }
                if (MediaRouter.DEBUG) {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append("Route selected: ");
                    sb5.append(this.mSelectedRoute);
                    Log.d(str, sb5.toString());
                }
                this.mCallbackHandler.post(262, this.mSelectedRoute);
                if (this.mSelectedRoute.isGroup()) {
                    List<RouteInfo> memberRoutes = this.mSelectedRoute.getMemberRoutes();
                    this.mRouteControllerMap.clear();
                    for (RouteInfo routeInfo3 : memberRoutes) {
                        RouteController onCreateRouteController = routeInfo3.getProviderInstance().onCreateRouteController(routeInfo3.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                        onCreateRouteController.onSelect();
                        this.mRouteControllerMap.put(routeInfo3.mUniqueId, onCreateRouteController);
                    }
                }
                updatePlaybackInfoFromSelectedRoute();
            }
        }

        public void onSystemRouteSelectedByDescriptorId(String str) {
            this.mCallbackHandler.removeMessages(262);
            ProviderInfo findProviderInfo = findProviderInfo(this.mSystemProvider);
            if (findProviderInfo != null) {
                RouteInfo findRouteByDescriptorId = findProviderInfo.findRouteByDescriptorId(str);
                if (findRouteByDescriptorId != null) {
                    findRouteByDescriptorId.select();
                }
            }
        }

        public Token getMediaSessionToken() {
            MediaSessionRecord mediaSessionRecord = this.mMediaSession;
            if (mediaSessionRecord != null) {
                return mediaSessionRecord.getToken();
            }
            MediaSessionCompat mediaSessionCompat = this.mCompatSession;
            if (mediaSessionCompat != null) {
                return mediaSessionCompat.getSessionToken();
            }
            return null;
        }

        private void updatePlaybackInfoFromSelectedRoute() {
            RouteInfo routeInfo = this.mSelectedRoute;
            if (routeInfo != null) {
                this.mPlaybackInfo.volume = routeInfo.getVolume();
                this.mPlaybackInfo.volumeMax = this.mSelectedRoute.getVolumeMax();
                this.mPlaybackInfo.volumeHandling = this.mSelectedRoute.getVolumeHandling();
                this.mPlaybackInfo.playbackStream = this.mSelectedRoute.getPlaybackStream();
                this.mPlaybackInfo.playbackType = this.mSelectedRoute.getPlaybackType();
                int size = this.mRemoteControlClients.size();
                int i = 0;
                for (int i2 = 0; i2 < size; i2++) {
                    ((RemoteControlClientRecord) this.mRemoteControlClients.get(i2)).updatePlaybackInfo();
                }
                if (this.mMediaSession == null) {
                    return;
                }
                if (this.mSelectedRoute == getDefaultRoute() || this.mSelectedRoute == getBluetoothRoute()) {
                    this.mMediaSession.clearVolumeHandling();
                    return;
                }
                if (this.mPlaybackInfo.volumeHandling == 1) {
                    i = 2;
                }
                MediaSessionRecord mediaSessionRecord = this.mMediaSession;
                PlaybackInfo playbackInfo = this.mPlaybackInfo;
                mediaSessionRecord.configureVolume(i, playbackInfo.volumeMax, playbackInfo.volume);
                return;
            }
            MediaSessionRecord mediaSessionRecord2 = this.mMediaSession;
            if (mediaSessionRecord2 != null) {
                mediaSessionRecord2.clearVolumeHandling();
            }
        }
    }

    public static final class ProviderInfo {
        private MediaRouteProviderDescriptor mDescriptor;
        private final ProviderMetadata mMetadata;
        final MediaRouteProvider mProviderInstance;
        final List<RouteInfo> mRoutes = new ArrayList();

        ProviderInfo(MediaRouteProvider mediaRouteProvider) {
            this.mProviderInstance = mediaRouteProvider;
            this.mMetadata = mediaRouteProvider.getMetadata();
        }

        public MediaRouteProvider getProviderInstance() {
            MediaRouter.checkCallingThread();
            return this.mProviderInstance;
        }

        public String getPackageName() {
            return this.mMetadata.getPackageName();
        }

        public ComponentName getComponentName() {
            return this.mMetadata.getComponentName();
        }

        public List<RouteInfo> getRoutes() {
            MediaRouter.checkCallingThread();
            return Collections.unmodifiableList(this.mRoutes);
        }

        /* access modifiers changed from: 0000 */
        public boolean updateDescriptor(MediaRouteProviderDescriptor mediaRouteProviderDescriptor) {
            if (this.mDescriptor == mediaRouteProviderDescriptor) {
                return false;
            }
            this.mDescriptor = mediaRouteProviderDescriptor;
            return true;
        }

        /* access modifiers changed from: 0000 */
        public int findRouteIndexByDescriptorId(String str) {
            int size = this.mRoutes.size();
            for (int i = 0; i < size; i++) {
                if (((RouteInfo) this.mRoutes.get(i)).mDescriptorId.equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        /* access modifiers changed from: 0000 */
        public RouteInfo findRouteByDescriptorId(String str) {
            int size = this.mRoutes.size();
            for (int i = 0; i < size; i++) {
                if (((RouteInfo) this.mRoutes.get(i)).mDescriptorId.equals(str)) {
                    return (RouteInfo) this.mRoutes.get(i);
                }
            }
            return null;
        }

        /* access modifiers changed from: 0000 */
        public boolean supportsDynamicGroup() {
            MediaRouteProviderDescriptor mediaRouteProviderDescriptor = this.mDescriptor;
            return mediaRouteProviderDescriptor != null && mediaRouteProviderDescriptor.supportsDynamicGroupRoute();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MediaRouter.RouteProviderInfo{ packageName=");
            sb.append(getPackageName());
            sb.append(" }");
            return sb.toString();
        }
    }

    public static class RouteInfo {
        private boolean mCanDisconnect;
        private int mConnectionState;
        private final ArrayList<IntentFilter> mControlFilters = new ArrayList<>();
        private String mDescription;
        MediaRouteDescriptor mDescriptor;
        final String mDescriptorId;
        private int mDeviceType;
        DynamicRouteDescriptor mDynamicDescriptor;
        private DynamicGroupState mDynamicGroupState;
        boolean mEnabled;
        private Bundle mExtras;
        private Uri mIconUri;
        private List<RouteInfo> mMemberRoutes = new ArrayList();
        private String mName;
        private int mPlaybackStream;
        private int mPlaybackType;
        private int mPresentationDisplayId = -1;
        private final ProviderInfo mProvider;
        private IntentSender mSettingsIntent;
        final String mUniqueId;
        private int mVolume;
        private int mVolumeHandling;
        private int mVolumeMax;

        public class DynamicGroupState {
            public DynamicGroupState() {
            }

            public int getSelectionState() {
                DynamicRouteDescriptor dynamicRouteDescriptor = RouteInfo.this.mDynamicDescriptor;
                if (dynamicRouteDescriptor != null) {
                    return dynamicRouteDescriptor.getSelectionState();
                }
                return 1;
            }

            public boolean isUnselectable() {
                DynamicRouteDescriptor dynamicRouteDescriptor = RouteInfo.this.mDynamicDescriptor;
                return dynamicRouteDescriptor == null || dynamicRouteDescriptor.isUnselectable();
            }

            public boolean isGroupable() {
                DynamicRouteDescriptor dynamicRouteDescriptor = RouteInfo.this.mDynamicDescriptor;
                return dynamicRouteDescriptor != null && dynamicRouteDescriptor.isGroupable();
            }

            public boolean isTransferable() {
                DynamicRouteDescriptor dynamicRouteDescriptor = RouteInfo.this.mDynamicDescriptor;
                return dynamicRouteDescriptor != null && dynamicRouteDescriptor.isTransferable();
            }
        }

        RouteInfo(ProviderInfo providerInfo, String str, String str2) {
            this.mProvider = providerInfo;
            this.mDescriptorId = str;
            this.mUniqueId = str2;
        }

        public ProviderInfo getProvider() {
            return this.mProvider;
        }

        public String getId() {
            return this.mUniqueId;
        }

        public String getName() {
            return this.mName;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public Uri getIconUri() {
            return this.mIconUri;
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public int getConnectionState() {
            return this.mConnectionState;
        }

        public boolean isSelected() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getSelectedRoute() == this;
        }

        public boolean isDefault() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getDefaultRoute() == this;
        }

        public boolean matchesSelector(MediaRouteSelector mediaRouteSelector) {
            if (mediaRouteSelector != null) {
                MediaRouter.checkCallingThread();
                return mediaRouteSelector.matchesControlFilters(this.mControlFilters);
            }
            throw new IllegalArgumentException("selector must not be null");
        }

        public boolean supportsControlCategory(String str) {
            if (str != null) {
                MediaRouter.checkCallingThread();
                int size = this.mControlFilters.size();
                for (int i = 0; i < size; i++) {
                    if (((IntentFilter) this.mControlFilters.get(i)).hasCategory(str)) {
                        return true;
                    }
                }
                return false;
            }
            throw new IllegalArgumentException("category must not be null");
        }

        public int getPlaybackType() {
            return this.mPlaybackType;
        }

        public int getPlaybackStream() {
            return this.mPlaybackStream;
        }

        public int getDeviceType() {
            return this.mDeviceType;
        }

        public boolean isDefaultOrBluetooth() {
            if (isDefault() || this.mDeviceType == 3) {
                return true;
            }
            if (!isSystemMediaRouteProvider(this) || !supportsControlCategory("android.media.intent.category.LIVE_AUDIO") || supportsControlCategory("android.media.intent.category.LIVE_VIDEO")) {
                return false;
            }
            return true;
        }

        /* access modifiers changed from: 0000 */
        public boolean isSelectable() {
            return this.mDescriptor != null && this.mEnabled;
        }

        private static boolean isSystemMediaRouteProvider(RouteInfo routeInfo) {
            return TextUtils.equals(routeInfo.getProviderInstance().getMetadata().getPackageName(), "android");
        }

        public int getVolumeHandling() {
            return this.mVolumeHandling;
        }

        public int getVolume() {
            return this.mVolume;
        }

        public int getVolumeMax() {
            return this.mVolumeMax;
        }

        public boolean canDisconnect() {
            return this.mCanDisconnect;
        }

        public void requestSetVolume(int i) {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.requestSetVolume(this, Math.min(this.mVolumeMax, Math.max(0, i)));
        }

        public void requestUpdateVolume(int i) {
            MediaRouter.checkCallingThread();
            if (i != 0) {
                MediaRouter.sGlobal.requestUpdateVolume(this, i);
            }
        }

        public int getPresentationDisplayId() {
            return this.mPresentationDisplayId;
        }

        public void select() {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.selectRoute(this);
        }

        public boolean isGroup() {
            return getMemberRoutes().size() >= 1;
        }

        public DynamicGroupState getDynamicGroupState() {
            if (this.mDynamicGroupState == null && this.mDynamicDescriptor != null) {
                this.mDynamicGroupState = new DynamicGroupState();
            }
            return this.mDynamicGroupState;
        }

        public List<RouteInfo> getMemberRoutes() {
            return Collections.unmodifiableList(this.mMemberRoutes);
        }

        public DynamicGroupRouteController getDynamicGroupController() {
            RouteController routeController = MediaRouter.sGlobal.mSelectedRouteController;
            if (routeController instanceof DynamicGroupRouteController) {
                return (DynamicGroupRouteController) routeController;
            }
            return null;
        }

        public String toString() {
            if (isGroup()) {
                StringBuilder sb = new StringBuilder(super.toString());
                sb.append('[');
                int size = this.mMemberRoutes.size();
                for (int i = 0; i < size; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(this.mMemberRoutes.get(i));
                }
                sb.append(']');
                return sb.toString();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("MediaRouter.RouteInfo{ uniqueId=");
            sb2.append(this.mUniqueId);
            sb2.append(", name=");
            sb2.append(this.mName);
            sb2.append(", description=");
            sb2.append(this.mDescription);
            sb2.append(", iconUri=");
            sb2.append(this.mIconUri);
            sb2.append(", enabled=");
            sb2.append(this.mEnabled);
            sb2.append(", connectionState=");
            sb2.append(this.mConnectionState);
            sb2.append(", canDisconnect=");
            sb2.append(this.mCanDisconnect);
            sb2.append(", playbackType=");
            sb2.append(this.mPlaybackType);
            sb2.append(", playbackStream=");
            sb2.append(this.mPlaybackStream);
            sb2.append(", deviceType=");
            sb2.append(this.mDeviceType);
            sb2.append(", volumeHandling=");
            sb2.append(this.mVolumeHandling);
            sb2.append(", volume=");
            sb2.append(this.mVolume);
            sb2.append(", volumeMax=");
            sb2.append(this.mVolumeMax);
            sb2.append(", presentationDisplayId=");
            sb2.append(this.mPresentationDisplayId);
            sb2.append(", extras=");
            sb2.append(this.mExtras);
            sb2.append(", settingsIntent=");
            sb2.append(this.mSettingsIntent);
            sb2.append(", providerPackageName=");
            sb2.append(this.mProvider.getPackageName());
            sb2.append(" }");
            return sb2.toString();
        }

        /* access modifiers changed from: 0000 */
        public int maybeUpdateDescriptor(MediaRouteDescriptor mediaRouteDescriptor) {
            if (this.mDescriptor != mediaRouteDescriptor) {
                return updateDescriptor(mediaRouteDescriptor);
            }
            return 0;
        }

        private boolean isSameControlFilters(List<IntentFilter> list, List<IntentFilter> list2) {
            boolean z = true;
            if (list == list2) {
                return true;
            }
            if (list == null || list2 == null) {
                return false;
            }
            ListIterator listIterator = list.listIterator();
            ListIterator listIterator2 = list2.listIterator();
            while (listIterator.hasNext() && listIterator2.hasNext()) {
                if (!isSameControlFilter((IntentFilter) listIterator.next(), (IntentFilter) listIterator2.next())) {
                    return false;
                }
            }
            if (listIterator.hasNext() || listIterator2.hasNext()) {
                z = false;
            }
            return z;
        }

        private boolean isSameControlFilter(IntentFilter intentFilter, IntentFilter intentFilter2) {
            if (intentFilter == intentFilter2) {
                return true;
            }
            if (intentFilter == null || intentFilter2 == null) {
                return false;
            }
            int countActions = intentFilter.countActions();
            if (countActions != intentFilter2.countActions()) {
                return false;
            }
            for (int i = 0; i < countActions; i++) {
                if (!intentFilter.getAction(i).equals(intentFilter2.getAction(i))) {
                    return false;
                }
            }
            int countCategories = intentFilter.countCategories();
            if (countCategories != intentFilter2.countCategories()) {
                return false;
            }
            for (int i2 = 0; i2 < countCategories; i2++) {
                if (!intentFilter.getCategory(i2).equals(intentFilter2.getCategory(i2))) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: 0000 */
        public int updateDescriptor(MediaRouteDescriptor mediaRouteDescriptor) {
            int i;
            this.mDescriptor = mediaRouteDescriptor;
            boolean z = false;
            if (mediaRouteDescriptor == null) {
                return 0;
            }
            if (!ObjectsCompat.equals(this.mName, mediaRouteDescriptor.getName())) {
                this.mName = mediaRouteDescriptor.getName();
                i = 1;
            } else {
                i = 0;
            }
            if (!ObjectsCompat.equals(this.mDescription, mediaRouteDescriptor.getDescription())) {
                this.mDescription = mediaRouteDescriptor.getDescription();
                i |= 1;
            }
            if (!ObjectsCompat.equals(this.mIconUri, mediaRouteDescriptor.getIconUri())) {
                this.mIconUri = mediaRouteDescriptor.getIconUri();
                i |= 1;
            }
            if (this.mEnabled != mediaRouteDescriptor.isEnabled()) {
                this.mEnabled = mediaRouteDescriptor.isEnabled();
                i |= 1;
            }
            if (this.mConnectionState != mediaRouteDescriptor.getConnectionState()) {
                this.mConnectionState = mediaRouteDescriptor.getConnectionState();
                i |= 1;
            }
            if (!isSameControlFilters(this.mControlFilters, mediaRouteDescriptor.getControlFilters())) {
                this.mControlFilters.clear();
                this.mControlFilters.addAll(mediaRouteDescriptor.getControlFilters());
                i |= 1;
            }
            if (this.mPlaybackType != mediaRouteDescriptor.getPlaybackType()) {
                this.mPlaybackType = mediaRouteDescriptor.getPlaybackType();
                i |= 1;
            }
            if (this.mPlaybackStream != mediaRouteDescriptor.getPlaybackStream()) {
                this.mPlaybackStream = mediaRouteDescriptor.getPlaybackStream();
                i |= 1;
            }
            if (this.mDeviceType != mediaRouteDescriptor.getDeviceType()) {
                this.mDeviceType = mediaRouteDescriptor.getDeviceType();
                i |= 1;
            }
            if (this.mVolumeHandling != mediaRouteDescriptor.getVolumeHandling()) {
                this.mVolumeHandling = mediaRouteDescriptor.getVolumeHandling();
                i |= 3;
            }
            if (this.mVolume != mediaRouteDescriptor.getVolume()) {
                this.mVolume = mediaRouteDescriptor.getVolume();
                i |= 3;
            }
            if (this.mVolumeMax != mediaRouteDescriptor.getVolumeMax()) {
                this.mVolumeMax = mediaRouteDescriptor.getVolumeMax();
                i |= 3;
            }
            if (this.mPresentationDisplayId != mediaRouteDescriptor.getPresentationDisplayId()) {
                this.mPresentationDisplayId = mediaRouteDescriptor.getPresentationDisplayId();
                i |= 5;
            }
            if (!ObjectsCompat.equals(this.mExtras, mediaRouteDescriptor.getExtras())) {
                this.mExtras = mediaRouteDescriptor.getExtras();
                i |= 1;
            }
            if (!ObjectsCompat.equals(this.mSettingsIntent, mediaRouteDescriptor.getSettingsActivity())) {
                this.mSettingsIntent = mediaRouteDescriptor.getSettingsActivity();
                i |= 1;
            }
            if (this.mCanDisconnect != mediaRouteDescriptor.canDisconnectAndKeepPlaying()) {
                this.mCanDisconnect = mediaRouteDescriptor.canDisconnectAndKeepPlaying();
                i |= 5;
            }
            List<String> groupMemberIds = mediaRouteDescriptor.getGroupMemberIds();
            ArrayList arrayList = new ArrayList();
            if (groupMemberIds.size() != this.mMemberRoutes.size()) {
                z = true;
            }
            for (String uniqueId : groupMemberIds) {
                RouteInfo route = MediaRouter.sGlobal.getRoute(MediaRouter.sGlobal.getUniqueId(getProvider(), uniqueId));
                if (route != null) {
                    arrayList.add(route);
                    if (!z && !this.mMemberRoutes.contains(route)) {
                        z = true;
                    }
                }
            }
            if (!z) {
                return i;
            }
            this.mMemberRoutes = arrayList;
            return i | 1;
        }

        /* access modifiers changed from: 0000 */
        public String getDescriptorId() {
            return this.mDescriptorId;
        }

        public MediaRouteProvider getProviderInstance() {
            return this.mProvider.getProviderInstance();
        }

        /* access modifiers changed from: 0000 */
        public void updateDescriptors(Collection<DynamicRouteDescriptor> collection) {
            this.mMemberRoutes.clear();
            for (DynamicRouteDescriptor dynamicRouteDescriptor : collection) {
                RouteInfo findRouteByDynamicRouteDescriptor = findRouteByDynamicRouteDescriptor(dynamicRouteDescriptor);
                if (findRouteByDynamicRouteDescriptor != null) {
                    findRouteByDynamicRouteDescriptor.mDynamicDescriptor = dynamicRouteDescriptor;
                    if (dynamicRouteDescriptor.getSelectionState() == 2 || dynamicRouteDescriptor.getSelectionState() == 3) {
                        this.mMemberRoutes.add(findRouteByDynamicRouteDescriptor);
                    }
                }
            }
            MediaRouter.sGlobal.mCallbackHandler.post(259, this);
        }

        /* access modifiers changed from: 0000 */
        public RouteInfo findRouteByDynamicRouteDescriptor(DynamicRouteDescriptor dynamicRouteDescriptor) {
            return getProvider().findRouteByDescriptorId(dynamicRouteDescriptor.getRouteDescriptor().getId());
        }
    }

    MediaRouter(Context context) {
        this.mContext = context;
    }

    public static MediaRouter getInstance(Context context) {
        if (context != null) {
            checkCallingThread();
            if (sGlobal == null) {
                GlobalMediaRouter globalMediaRouter = new GlobalMediaRouter(context.getApplicationContext());
                sGlobal = globalMediaRouter;
                globalMediaRouter.start();
            }
            return sGlobal.getRouter(context);
        }
        throw new IllegalArgumentException("context must not be null");
    }

    public List<RouteInfo> getRoutes() {
        checkCallingThread();
        return sGlobal.getRoutes();
    }

    public RouteInfo getSelectedRoute() {
        checkCallingThread();
        return sGlobal.getSelectedRoute();
    }

    public void unselect(int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("Unsupported reason to unselect route");
        }
        checkCallingThread();
        RouteInfo chooseFallbackRoute = sGlobal.chooseFallbackRoute();
        if (sGlobal.getSelectedRoute() != chooseFallbackRoute) {
            sGlobal.selectRoute(chooseFallbackRoute, i);
            return;
        }
        GlobalMediaRouter globalMediaRouter = sGlobal;
        globalMediaRouter.selectRoute(globalMediaRouter.getDefaultRoute(), i);
    }

    public void addMemberToDynamicGroup(RouteInfo routeInfo) {
        checkCallingThread();
        sGlobal.addMemberToDynamicGroup(routeInfo);
    }

    public void removeMemberFromDynamicGroup(RouteInfo routeInfo) {
        checkCallingThread();
        sGlobal.removeMemberFromDynamicGroup(routeInfo);
    }

    public boolean isRouteAvailable(MediaRouteSelector mediaRouteSelector, int i) {
        if (mediaRouteSelector != null) {
            checkCallingThread();
            return sGlobal.isRouteAvailable(mediaRouteSelector, i);
        }
        throw new IllegalArgumentException("selector must not be null");
    }

    public void addCallback(MediaRouteSelector mediaRouteSelector, Callback callback) {
        addCallback(mediaRouteSelector, callback, 0);
    }

    public void addCallback(MediaRouteSelector mediaRouteSelector, Callback callback, int i) {
        CallbackRecord callbackRecord;
        if (mediaRouteSelector == null) {
            throw new IllegalArgumentException("selector must not be null");
        } else if (callback != null) {
            checkCallingThread();
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("addCallback: selector=");
                sb.append(mediaRouteSelector);
                sb.append(", callback=");
                sb.append(callback);
                sb.append(", flags=");
                sb.append(Integer.toHexString(i));
                Log.d("MediaRouter", sb.toString());
            }
            int findCallbackRecord = findCallbackRecord(callback);
            if (findCallbackRecord < 0) {
                callbackRecord = new CallbackRecord(this, callback);
                this.mCallbackRecords.add(callbackRecord);
            } else {
                callbackRecord = (CallbackRecord) this.mCallbackRecords.get(findCallbackRecord);
            }
            boolean z = false;
            int i2 = callbackRecord.mFlags;
            boolean z2 = true;
            if (((~i2) & i) != 0) {
                callbackRecord.mFlags = i2 | i;
                z = true;
            }
            if (!callbackRecord.mSelector.contains(mediaRouteSelector)) {
                Builder builder = new Builder(callbackRecord.mSelector);
                builder.addSelector(mediaRouteSelector);
                callbackRecord.mSelector = builder.build();
            } else {
                z2 = z;
            }
            if (z2) {
                sGlobal.updateDiscoveryRequest();
            }
        } else {
            throw new IllegalArgumentException("callback must not be null");
        }
    }

    public void removeCallback(Callback callback) {
        if (callback != null) {
            checkCallingThread();
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("removeCallback: callback=");
                sb.append(callback);
                Log.d("MediaRouter", sb.toString());
            }
            int findCallbackRecord = findCallbackRecord(callback);
            if (findCallbackRecord >= 0) {
                this.mCallbackRecords.remove(findCallbackRecord);
                sGlobal.updateDiscoveryRequest();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("callback must not be null");
    }

    private int findCallbackRecord(Callback callback) {
        int size = this.mCallbackRecords.size();
        for (int i = 0; i < size; i++) {
            if (((CallbackRecord) this.mCallbackRecords.get(i)).mCallback == callback) {
                return i;
            }
        }
        return -1;
    }

    public Token getMediaSessionToken() {
        return sGlobal.getMediaSessionToken();
    }

    static void checkCallingThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("The media router service must only be accessed on the application's main thread.");
        }
    }
}
