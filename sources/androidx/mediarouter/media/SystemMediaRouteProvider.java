package androidx.mediarouter.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.view.Display;
import androidx.mediarouter.R$string;
import androidx.mediarouter.media.MediaRouteDescriptor.Builder;
import androidx.mediarouter.media.MediaRouteProvider.ProviderMetadata;
import androidx.mediarouter.media.MediaRouteProvider.RouteController;
import androidx.mediarouter.media.MediaRouterJellybean.Callback;
import androidx.mediarouter.media.MediaRouterJellybean.GetDefaultRouteWorkaround;
import androidx.mediarouter.media.MediaRouterJellybean.RouteInfo;
import androidx.mediarouter.media.MediaRouterJellybean.SelectRouteWorkaround;
import androidx.mediarouter.media.MediaRouterJellybean.UserRouteInfo;
import androidx.mediarouter.media.MediaRouterJellybean.VolumeCallback;
import androidx.mediarouter.media.MediaRouterJellybeanMr1.ActiveScanWorkaround;
import androidx.mediarouter.media.MediaRouterJellybeanMr1.IsConnectingWorkaround;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

abstract class SystemMediaRouteProvider extends MediaRouteProvider {

    private static class Api24Impl extends JellybeanMr2Impl {
        public Api24Impl(Context context, SyncCallback syncCallback) {
            super(context, syncCallback);
        }

        /* access modifiers changed from: protected */
        public void onBuildSystemRouteDescriptor(SystemRouteRecord systemRouteRecord, Builder builder) {
            super.onBuildSystemRouteDescriptor(systemRouteRecord, builder);
            builder.setDeviceType(MediaRouterApi24$RouteInfo.getDeviceType(systemRouteRecord.mRouteObj));
        }
    }

    static class JellybeanImpl extends SystemMediaRouteProvider implements Callback, VolumeCallback {
        private static final ArrayList<IntentFilter> LIVE_AUDIO_CONTROL_FILTERS;
        private static final ArrayList<IntentFilter> LIVE_VIDEO_CONTROL_FILTERS;
        protected boolean mActiveScan;
        protected final Object mCallbackObj;
        protected boolean mCallbackRegistered;
        private GetDefaultRouteWorkaround mGetDefaultRouteWorkaround;
        protected int mRouteTypes;
        protected final Object mRouterObj;
        private SelectRouteWorkaround mSelectRouteWorkaround;
        private final SyncCallback mSyncCallback;
        protected final ArrayList<SystemRouteRecord> mSystemRouteRecords = new ArrayList<>();
        protected final Object mUserRouteCategoryObj;
        protected final ArrayList<UserRouteRecord> mUserRouteRecords = new ArrayList<>();
        protected final Object mVolumeCallbackObj;

        protected static final class SystemRouteController extends RouteController {
            private final Object mRouteObj;

            public SystemRouteController(Object obj) {
                this.mRouteObj = obj;
            }

            public void onSetVolume(int i) {
                RouteInfo.requestSetVolume(this.mRouteObj, i);
            }

            public void onUpdateVolume(int i) {
                RouteInfo.requestUpdateVolume(this.mRouteObj, i);
            }
        }

        protected static final class SystemRouteRecord {
            public MediaRouteDescriptor mRouteDescriptor;
            public final String mRouteDescriptorId;
            public final Object mRouteObj;

            public SystemRouteRecord(Object obj, String str) {
                this.mRouteObj = obj;
                this.mRouteDescriptorId = str;
            }
        }

        protected static final class UserRouteRecord {
            public final MediaRouter.RouteInfo mRoute;
            public final Object mRouteObj;

            public UserRouteRecord(MediaRouter.RouteInfo routeInfo, Object obj) {
                this.mRoute = routeInfo;
                this.mRouteObj = obj;
            }
        }

        public void onRouteGrouped(Object obj, Object obj2, int i) {
        }

        public void onRouteUngrouped(Object obj, Object obj2) {
        }

        public void onRouteUnselected(int i, Object obj) {
        }

        static {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addCategory("android.media.intent.category.LIVE_AUDIO");
            ArrayList<IntentFilter> arrayList = new ArrayList<>();
            LIVE_AUDIO_CONTROL_FILTERS = arrayList;
            arrayList.add(intentFilter);
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addCategory("android.media.intent.category.LIVE_VIDEO");
            ArrayList<IntentFilter> arrayList2 = new ArrayList<>();
            LIVE_VIDEO_CONTROL_FILTERS = arrayList2;
            arrayList2.add(intentFilter2);
        }

        public JellybeanImpl(Context context, SyncCallback syncCallback) {
            super(context);
            this.mSyncCallback = syncCallback;
            this.mRouterObj = MediaRouterJellybean.getMediaRouter(context);
            this.mCallbackObj = createCallbackObj();
            this.mVolumeCallbackObj = createVolumeCallbackObj();
            this.mUserRouteCategoryObj = MediaRouterJellybean.createRouteCategory(this.mRouterObj, context.getResources().getString(R$string.mr_user_route_category_name), false);
            updateSystemRoutes();
        }

        public RouteController onCreateRouteController(String str) {
            int findSystemRouteRecordByDescriptorId = findSystemRouteRecordByDescriptorId(str);
            if (findSystemRouteRecordByDescriptorId >= 0) {
                return new SystemRouteController(((SystemRouteRecord) this.mSystemRouteRecords.get(findSystemRouteRecordByDescriptorId)).mRouteObj);
            }
            return null;
        }

        public void onDiscoveryRequestChanged(MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest) {
            boolean z;
            int i = 0;
            if (mediaRouteDiscoveryRequest != null) {
                List controlCategories = mediaRouteDiscoveryRequest.getSelector().getControlCategories();
                int size = controlCategories.size();
                int i2 = 0;
                while (i < size) {
                    String str = (String) controlCategories.get(i);
                    i2 = str.equals("android.media.intent.category.LIVE_AUDIO") ? i2 | 1 : str.equals("android.media.intent.category.LIVE_VIDEO") ? i2 | 2 : i2 | 8388608;
                    i++;
                }
                z = mediaRouteDiscoveryRequest.isActiveScan();
                i = i2;
            } else {
                z = false;
            }
            if (this.mRouteTypes != i || this.mActiveScan != z) {
                this.mRouteTypes = i;
                this.mActiveScan = z;
                updateSystemRoutes();
            }
        }

        public void onRouteAdded(Object obj) {
            if (addSystemRouteNoPublish(obj)) {
                publishRoutes();
            }
        }

        private void updateSystemRoutes() {
            updateCallback();
            boolean z = false;
            for (Object addSystemRouteNoPublish : MediaRouterJellybean.getRoutes(this.mRouterObj)) {
                z |= addSystemRouteNoPublish(addSystemRouteNoPublish);
            }
            if (z) {
                publishRoutes();
            }
        }

        private boolean addSystemRouteNoPublish(Object obj) {
            if (getUserRouteRecord(obj) != null || findSystemRouteRecord(obj) >= 0) {
                return false;
            }
            SystemRouteRecord systemRouteRecord = new SystemRouteRecord(obj, assignRouteId(obj));
            updateSystemRouteDescriptor(systemRouteRecord);
            this.mSystemRouteRecords.add(systemRouteRecord);
            return true;
        }

        private String assignRouteId(Object obj) {
            String str;
            if (getDefaultRoute() == obj) {
                str = "DEFAULT_ROUTE";
            } else {
                str = String.format(Locale.US, "ROUTE_%08x", new Object[]{Integer.valueOf(getRouteName(obj).hashCode())});
            }
            if (findSystemRouteRecordByDescriptorId(str) < 0) {
                return str;
            }
            int i = 2;
            while (true) {
                String format = String.format(Locale.US, "%s_%d", new Object[]{str, Integer.valueOf(i)});
                if (findSystemRouteRecordByDescriptorId(format) < 0) {
                    return format;
                }
                i++;
            }
        }

        public void onRouteRemoved(Object obj) {
            if (getUserRouteRecord(obj) == null) {
                int findSystemRouteRecord = findSystemRouteRecord(obj);
                if (findSystemRouteRecord >= 0) {
                    this.mSystemRouteRecords.remove(findSystemRouteRecord);
                    publishRoutes();
                }
            }
        }

        public void onRouteChanged(Object obj) {
            if (getUserRouteRecord(obj) == null) {
                int findSystemRouteRecord = findSystemRouteRecord(obj);
                if (findSystemRouteRecord >= 0) {
                    updateSystemRouteDescriptor((SystemRouteRecord) this.mSystemRouteRecords.get(findSystemRouteRecord));
                    publishRoutes();
                }
            }
        }

        public void onRouteVolumeChanged(Object obj) {
            if (getUserRouteRecord(obj) == null) {
                int findSystemRouteRecord = findSystemRouteRecord(obj);
                if (findSystemRouteRecord >= 0) {
                    SystemRouteRecord systemRouteRecord = (SystemRouteRecord) this.mSystemRouteRecords.get(findSystemRouteRecord);
                    int volume = RouteInfo.getVolume(obj);
                    if (volume != systemRouteRecord.mRouteDescriptor.getVolume()) {
                        Builder builder = new Builder(systemRouteRecord.mRouteDescriptor);
                        builder.setVolume(volume);
                        systemRouteRecord.mRouteDescriptor = builder.build();
                        publishRoutes();
                    }
                }
            }
        }

        public void onRouteSelected(int i, Object obj) {
            if (obj == MediaRouterJellybean.getSelectedRoute(this.mRouterObj, 8388611)) {
                UserRouteRecord userRouteRecord = getUserRouteRecord(obj);
                if (userRouteRecord != null) {
                    userRouteRecord.mRoute.select();
                } else {
                    int findSystemRouteRecord = findSystemRouteRecord(obj);
                    if (findSystemRouteRecord >= 0) {
                        this.mSyncCallback.onSystemRouteSelectedByDescriptorId(((SystemRouteRecord) this.mSystemRouteRecords.get(findSystemRouteRecord)).mRouteDescriptorId);
                    }
                }
            }
        }

        public void onVolumeSetRequest(Object obj, int i) {
            UserRouteRecord userRouteRecord = getUserRouteRecord(obj);
            if (userRouteRecord != null) {
                userRouteRecord.mRoute.requestSetVolume(i);
            }
        }

        public void onVolumeUpdateRequest(Object obj, int i) {
            UserRouteRecord userRouteRecord = getUserRouteRecord(obj);
            if (userRouteRecord != null) {
                userRouteRecord.mRoute.requestUpdateVolume(i);
            }
        }

        public void onSyncRouteAdded(MediaRouter.RouteInfo routeInfo) {
            if (routeInfo.getProviderInstance() != this) {
                Object createUserRoute = MediaRouterJellybean.createUserRoute(this.mRouterObj, this.mUserRouteCategoryObj);
                UserRouteRecord userRouteRecord = new UserRouteRecord(routeInfo, createUserRoute);
                RouteInfo.setTag(createUserRoute, userRouteRecord);
                UserRouteInfo.setVolumeCallback(createUserRoute, this.mVolumeCallbackObj);
                updateUserRouteProperties(userRouteRecord);
                this.mUserRouteRecords.add(userRouteRecord);
                MediaRouterJellybean.addUserRoute(this.mRouterObj, createUserRoute);
                return;
            }
            int findSystemRouteRecord = findSystemRouteRecord(MediaRouterJellybean.getSelectedRoute(this.mRouterObj, 8388611));
            if (findSystemRouteRecord >= 0 && ((SystemRouteRecord) this.mSystemRouteRecords.get(findSystemRouteRecord)).mRouteDescriptorId.equals(routeInfo.getDescriptorId())) {
                routeInfo.select();
            }
        }

        public void onSyncRouteRemoved(MediaRouter.RouteInfo routeInfo) {
            if (routeInfo.getProviderInstance() != this) {
                int findUserRouteRecord = findUserRouteRecord(routeInfo);
                if (findUserRouteRecord >= 0) {
                    UserRouteRecord userRouteRecord = (UserRouteRecord) this.mUserRouteRecords.remove(findUserRouteRecord);
                    RouteInfo.setTag(userRouteRecord.mRouteObj, null);
                    UserRouteInfo.setVolumeCallback(userRouteRecord.mRouteObj, null);
                    MediaRouterJellybean.removeUserRoute(this.mRouterObj, userRouteRecord.mRouteObj);
                }
            }
        }

        public void onSyncRouteChanged(MediaRouter.RouteInfo routeInfo) {
            if (routeInfo.getProviderInstance() != this) {
                int findUserRouteRecord = findUserRouteRecord(routeInfo);
                if (findUserRouteRecord >= 0) {
                    updateUserRouteProperties((UserRouteRecord) this.mUserRouteRecords.get(findUserRouteRecord));
                }
            }
        }

        public void onSyncRouteSelected(MediaRouter.RouteInfo routeInfo) {
            if (routeInfo.isSelected()) {
                if (routeInfo.getProviderInstance() != this) {
                    int findUserRouteRecord = findUserRouteRecord(routeInfo);
                    if (findUserRouteRecord >= 0) {
                        selectRoute(((UserRouteRecord) this.mUserRouteRecords.get(findUserRouteRecord)).mRouteObj);
                    }
                } else {
                    int findSystemRouteRecordByDescriptorId = findSystemRouteRecordByDescriptorId(routeInfo.getDescriptorId());
                    if (findSystemRouteRecordByDescriptorId >= 0) {
                        selectRoute(((SystemRouteRecord) this.mSystemRouteRecords.get(findSystemRouteRecordByDescriptorId)).mRouteObj);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void publishRoutes() {
            MediaRouteProviderDescriptor.Builder builder = new MediaRouteProviderDescriptor.Builder();
            int size = this.mSystemRouteRecords.size();
            for (int i = 0; i < size; i++) {
                builder.addRoute(((SystemRouteRecord) this.mSystemRouteRecords.get(i)).mRouteDescriptor);
            }
            setDescriptor(builder.build());
        }

        /* access modifiers changed from: protected */
        public int findSystemRouteRecord(Object obj) {
            int size = this.mSystemRouteRecords.size();
            for (int i = 0; i < size; i++) {
                if (((SystemRouteRecord) this.mSystemRouteRecords.get(i)).mRouteObj == obj) {
                    return i;
                }
            }
            return -1;
        }

        /* access modifiers changed from: protected */
        public int findSystemRouteRecordByDescriptorId(String str) {
            int size = this.mSystemRouteRecords.size();
            for (int i = 0; i < size; i++) {
                if (((SystemRouteRecord) this.mSystemRouteRecords.get(i)).mRouteDescriptorId.equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        /* access modifiers changed from: protected */
        public int findUserRouteRecord(MediaRouter.RouteInfo routeInfo) {
            int size = this.mUserRouteRecords.size();
            for (int i = 0; i < size; i++) {
                if (((UserRouteRecord) this.mUserRouteRecords.get(i)).mRoute == routeInfo) {
                    return i;
                }
            }
            return -1;
        }

        /* access modifiers changed from: protected */
        public UserRouteRecord getUserRouteRecord(Object obj) {
            Object tag = RouteInfo.getTag(obj);
            if (tag instanceof UserRouteRecord) {
                return (UserRouteRecord) tag;
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void updateSystemRouteDescriptor(SystemRouteRecord systemRouteRecord) {
            Builder builder = new Builder(systemRouteRecord.mRouteDescriptorId, getRouteName(systemRouteRecord.mRouteObj));
            onBuildSystemRouteDescriptor(systemRouteRecord, builder);
            systemRouteRecord.mRouteDescriptor = builder.build();
        }

        /* access modifiers changed from: protected */
        public String getRouteName(Object obj) {
            CharSequence name = RouteInfo.getName(obj, getContext());
            return name != null ? name.toString() : "";
        }

        /* access modifiers changed from: protected */
        public void onBuildSystemRouteDescriptor(SystemRouteRecord systemRouteRecord, Builder builder) {
            int supportedTypes = RouteInfo.getSupportedTypes(systemRouteRecord.mRouteObj);
            if ((supportedTypes & 1) != 0) {
                builder.addControlFilters(LIVE_AUDIO_CONTROL_FILTERS);
            }
            if ((supportedTypes & 2) != 0) {
                builder.addControlFilters(LIVE_VIDEO_CONTROL_FILTERS);
            }
            builder.setPlaybackType(RouteInfo.getPlaybackType(systemRouteRecord.mRouteObj));
            builder.setPlaybackStream(RouteInfo.getPlaybackStream(systemRouteRecord.mRouteObj));
            builder.setVolume(RouteInfo.getVolume(systemRouteRecord.mRouteObj));
            builder.setVolumeMax(RouteInfo.getVolumeMax(systemRouteRecord.mRouteObj));
            builder.setVolumeHandling(RouteInfo.getVolumeHandling(systemRouteRecord.mRouteObj));
        }

        /* access modifiers changed from: protected */
        public void updateUserRouteProperties(UserRouteRecord userRouteRecord) {
            UserRouteInfo.setName(userRouteRecord.mRouteObj, userRouteRecord.mRoute.getName());
            UserRouteInfo.setPlaybackType(userRouteRecord.mRouteObj, userRouteRecord.mRoute.getPlaybackType());
            UserRouteInfo.setPlaybackStream(userRouteRecord.mRouteObj, userRouteRecord.mRoute.getPlaybackStream());
            UserRouteInfo.setVolume(userRouteRecord.mRouteObj, userRouteRecord.mRoute.getVolume());
            UserRouteInfo.setVolumeMax(userRouteRecord.mRouteObj, userRouteRecord.mRoute.getVolumeMax());
            UserRouteInfo.setVolumeHandling(userRouteRecord.mRouteObj, userRouteRecord.mRoute.getVolumeHandling());
        }

        /* access modifiers changed from: protected */
        public void updateCallback() {
            if (this.mCallbackRegistered) {
                this.mCallbackRegistered = false;
                MediaRouterJellybean.removeCallback(this.mRouterObj, this.mCallbackObj);
            }
            int i = this.mRouteTypes;
            if (i != 0) {
                this.mCallbackRegistered = true;
                MediaRouterJellybean.addCallback(this.mRouterObj, i, this.mCallbackObj);
            }
        }

        /* access modifiers changed from: protected */
        public Object createCallbackObj() {
            return MediaRouterJellybean.createCallback(this);
        }

        /* access modifiers changed from: protected */
        public Object createVolumeCallbackObj() {
            return MediaRouterJellybean.createVolumeCallback(this);
        }

        /* access modifiers changed from: protected */
        public void selectRoute(Object obj) {
            if (this.mSelectRouteWorkaround == null) {
                this.mSelectRouteWorkaround = new SelectRouteWorkaround();
            }
            this.mSelectRouteWorkaround.selectRoute(this.mRouterObj, 8388611, obj);
        }

        /* access modifiers changed from: protected */
        public Object getDefaultRoute() {
            if (this.mGetDefaultRouteWorkaround == null) {
                this.mGetDefaultRouteWorkaround = new GetDefaultRouteWorkaround();
            }
            return this.mGetDefaultRouteWorkaround.getDefaultRoute(this.mRouterObj);
        }
    }

    private static class JellybeanMr1Impl extends JellybeanImpl implements MediaRouterJellybeanMr1.Callback {
        private ActiveScanWorkaround mActiveScanWorkaround;
        private IsConnectingWorkaround mIsConnectingWorkaround;

        public JellybeanMr1Impl(Context context, SyncCallback syncCallback) {
            super(context, syncCallback);
        }

        public void onRoutePresentationDisplayChanged(Object obj) {
            int findSystemRouteRecord = findSystemRouteRecord(obj);
            if (findSystemRouteRecord >= 0) {
                SystemRouteRecord systemRouteRecord = (SystemRouteRecord) this.mSystemRouteRecords.get(findSystemRouteRecord);
                Display presentationDisplay = MediaRouterJellybeanMr1.RouteInfo.getPresentationDisplay(obj);
                int displayId = presentationDisplay != null ? presentationDisplay.getDisplayId() : -1;
                if (displayId != systemRouteRecord.mRouteDescriptor.getPresentationDisplayId()) {
                    Builder builder = new Builder(systemRouteRecord.mRouteDescriptor);
                    builder.setPresentationDisplayId(displayId);
                    systemRouteRecord.mRouteDescriptor = builder.build();
                    publishRoutes();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onBuildSystemRouteDescriptor(SystemRouteRecord systemRouteRecord, Builder builder) {
            super.onBuildSystemRouteDescriptor(systemRouteRecord, builder);
            if (!MediaRouterJellybeanMr1.RouteInfo.isEnabled(systemRouteRecord.mRouteObj)) {
                builder.setEnabled(false);
            }
            if (isConnecting(systemRouteRecord)) {
                builder.setConnectionState(1);
            }
            Display presentationDisplay = MediaRouterJellybeanMr1.RouteInfo.getPresentationDisplay(systemRouteRecord.mRouteObj);
            if (presentationDisplay != null) {
                builder.setPresentationDisplayId(presentationDisplay.getDisplayId());
            }
        }

        /* access modifiers changed from: protected */
        public void updateCallback() {
            super.updateCallback();
            if (this.mActiveScanWorkaround == null) {
                this.mActiveScanWorkaround = new ActiveScanWorkaround(getContext(), getHandler());
            }
            this.mActiveScanWorkaround.setActiveScanRouteTypes(this.mActiveScan ? this.mRouteTypes : 0);
        }

        /* access modifiers changed from: protected */
        public Object createCallbackObj() {
            return MediaRouterJellybeanMr1.createCallback(this);
        }

        /* access modifiers changed from: protected */
        public boolean isConnecting(SystemRouteRecord systemRouteRecord) {
            if (this.mIsConnectingWorkaround == null) {
                this.mIsConnectingWorkaround = new IsConnectingWorkaround();
            }
            return this.mIsConnectingWorkaround.isConnecting(systemRouteRecord.mRouteObj);
        }
    }

    private static class JellybeanMr2Impl extends JellybeanMr1Impl {
        public JellybeanMr2Impl(Context context, SyncCallback syncCallback) {
            super(context, syncCallback);
        }

        /* access modifiers changed from: protected */
        public void onBuildSystemRouteDescriptor(SystemRouteRecord systemRouteRecord, Builder builder) {
            super.onBuildSystemRouteDescriptor(systemRouteRecord, builder);
            CharSequence description = MediaRouterJellybeanMr2.RouteInfo.getDescription(systemRouteRecord.mRouteObj);
            if (description != null) {
                builder.setDescription(description.toString());
            }
        }

        /* access modifiers changed from: protected */
        public void selectRoute(Object obj) {
            MediaRouterJellybean.selectRoute(this.mRouterObj, 8388611, obj);
        }

        /* access modifiers changed from: protected */
        public Object getDefaultRoute() {
            return MediaRouterJellybeanMr2.getDefaultRoute(this.mRouterObj);
        }

        /* access modifiers changed from: protected */
        public void updateUserRouteProperties(UserRouteRecord userRouteRecord) {
            super.updateUserRouteProperties(userRouteRecord);
            MediaRouterJellybeanMr2.UserRouteInfo.setDescription(userRouteRecord.mRouteObj, userRouteRecord.mRoute.getDescription());
        }

        /* access modifiers changed from: protected */
        public void updateCallback() {
            if (this.mCallbackRegistered) {
                MediaRouterJellybean.removeCallback(this.mRouterObj, this.mCallbackObj);
            }
            this.mCallbackRegistered = true;
            MediaRouterJellybeanMr2.addCallback(this.mRouterObj, this.mRouteTypes, this.mCallbackObj, this.mActiveScan | true ? 1 : 0);
        }

        /* access modifiers changed from: protected */
        public boolean isConnecting(SystemRouteRecord systemRouteRecord) {
            return MediaRouterJellybeanMr2.RouteInfo.isConnecting(systemRouteRecord.mRouteObj);
        }
    }

    static class LegacyImpl extends SystemMediaRouteProvider {
        private static final ArrayList<IntentFilter> CONTROL_FILTERS;
        final AudioManager mAudioManager;
        int mLastReportedVolume = -1;
        private final VolumeChangeReceiver mVolumeChangeReceiver;

        final class DefaultRouteController extends RouteController {
            DefaultRouteController() {
            }

            public void onSetVolume(int i) {
                LegacyImpl.this.mAudioManager.setStreamVolume(3, i, 0);
                LegacyImpl.this.publishRoutes();
            }

            public void onUpdateVolume(int i) {
                int streamVolume = LegacyImpl.this.mAudioManager.getStreamVolume(3);
                if (Math.min(LegacyImpl.this.mAudioManager.getStreamMaxVolume(3), Math.max(0, i + streamVolume)) != streamVolume) {
                    LegacyImpl.this.mAudioManager.setStreamVolume(3, streamVolume, 0);
                }
                LegacyImpl.this.publishRoutes();
            }
        }

        final class VolumeChangeReceiver extends BroadcastReceiver {
            VolumeChangeReceiver() {
            }

            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION") && intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) == 3) {
                    int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
                    if (intExtra >= 0) {
                        LegacyImpl legacyImpl = LegacyImpl.this;
                        if (intExtra != legacyImpl.mLastReportedVolume) {
                            legacyImpl.publishRoutes();
                        }
                    }
                }
            }
        }

        static {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addCategory("android.media.intent.category.LIVE_AUDIO");
            intentFilter.addCategory("android.media.intent.category.LIVE_VIDEO");
            ArrayList<IntentFilter> arrayList = new ArrayList<>();
            CONTROL_FILTERS = arrayList;
            arrayList.add(intentFilter);
        }

        public LegacyImpl(Context context) {
            super(context);
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
            VolumeChangeReceiver volumeChangeReceiver = new VolumeChangeReceiver();
            this.mVolumeChangeReceiver = volumeChangeReceiver;
            context.registerReceiver(volumeChangeReceiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
            publishRoutes();
        }

        /* access modifiers changed from: 0000 */
        public void publishRoutes() {
            Resources resources = getContext().getResources();
            int streamMaxVolume = this.mAudioManager.getStreamMaxVolume(3);
            this.mLastReportedVolume = this.mAudioManager.getStreamVolume(3);
            Builder builder = new Builder("DEFAULT_ROUTE", resources.getString(R$string.mr_system_route_name));
            builder.addControlFilters(CONTROL_FILTERS);
            builder.setPlaybackStream(3);
            builder.setPlaybackType(0);
            builder.setVolumeHandling(1);
            builder.setVolumeMax(streamMaxVolume);
            builder.setVolume(this.mLastReportedVolume);
            MediaRouteDescriptor build = builder.build();
            MediaRouteProviderDescriptor.Builder builder2 = new MediaRouteProviderDescriptor.Builder();
            builder2.addRoute(build);
            setDescriptor(builder2.build());
        }

        public RouteController onCreateRouteController(String str) {
            if (str.equals("DEFAULT_ROUTE")) {
                return new DefaultRouteController();
            }
            return null;
        }
    }

    public interface SyncCallback {
        void onSystemRouteSelectedByDescriptorId(String str);
    }

    public void onSyncRouteAdded(MediaRouter.RouteInfo routeInfo) {
    }

    public void onSyncRouteChanged(MediaRouter.RouteInfo routeInfo) {
    }

    public void onSyncRouteRemoved(MediaRouter.RouteInfo routeInfo) {
    }

    public void onSyncRouteSelected(MediaRouter.RouteInfo routeInfo) {
    }

    protected SystemMediaRouteProvider(Context context) {
        super(context, new ProviderMetadata(new ComponentName("android", SystemMediaRouteProvider.class.getName())));
    }

    public static SystemMediaRouteProvider obtain(Context context, SyncCallback syncCallback) {
        int i = VERSION.SDK_INT;
        if (i >= 24) {
            return new Api24Impl(context, syncCallback);
        }
        if (i >= 18) {
            return new JellybeanMr2Impl(context, syncCallback);
        }
        if (i >= 17) {
            return new JellybeanMr1Impl(context, syncCallback);
        }
        if (i >= 16) {
            return new JellybeanImpl(context, syncCallback);
        }
        return new LegacyImpl(context);
    }
}
