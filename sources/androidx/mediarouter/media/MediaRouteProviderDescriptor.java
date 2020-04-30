package androidx.mediarouter.media;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MediaRouteProviderDescriptor {
    final List<MediaRouteDescriptor> mRoutes;
    final boolean mSupportsDynamicGroupRoute;

    public static final class Builder {
        private List<MediaRouteDescriptor> mRoutes;
        private boolean mSupportsDynamicGroupRoute = false;

        public Builder addRoute(MediaRouteDescriptor mediaRouteDescriptor) {
            if (mediaRouteDescriptor != null) {
                List<MediaRouteDescriptor> list = this.mRoutes;
                if (list == null) {
                    this.mRoutes = new ArrayList();
                } else if (list.contains(mediaRouteDescriptor)) {
                    throw new IllegalArgumentException("route descriptor already added");
                }
                this.mRoutes.add(mediaRouteDescriptor);
                return this;
            }
            throw new IllegalArgumentException("route must not be null");
        }

        public MediaRouteProviderDescriptor build() {
            return new MediaRouteProviderDescriptor(this.mRoutes, this.mSupportsDynamicGroupRoute);
        }
    }

    MediaRouteProviderDescriptor(List<MediaRouteDescriptor> list, boolean z) {
        if (list == null) {
            list = Collections.emptyList();
        }
        this.mRoutes = list;
        this.mSupportsDynamicGroupRoute = z;
    }

    public List<MediaRouteDescriptor> getRoutes() {
        return this.mRoutes;
    }

    public boolean isValid() {
        int size = getRoutes().size();
        for (int i = 0; i < size; i++) {
            MediaRouteDescriptor mediaRouteDescriptor = (MediaRouteDescriptor) this.mRoutes.get(i);
            if (mediaRouteDescriptor == null || !mediaRouteDescriptor.isValid()) {
                return false;
            }
        }
        return true;
    }

    public boolean supportsDynamicGroupRoute() {
        return this.mSupportsDynamicGroupRoute;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MediaRouteProviderDescriptor{ ");
        sb.append("routes=");
        sb.append(Arrays.toString(getRoutes().toArray()));
        sb.append(", isValid=");
        sb.append(isValid());
        sb.append(" }");
        return sb.toString();
    }

    public static MediaRouteProviderDescriptor fromBundle(Bundle bundle) {
        List list = null;
        if (bundle == null) {
            return null;
        }
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("routes");
        if (parcelableArrayList != null && !parcelableArrayList.isEmpty()) {
            int size = parcelableArrayList.size();
            ArrayList arrayList = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                arrayList.add(MediaRouteDescriptor.fromBundle((Bundle) parcelableArrayList.get(i)));
            }
            list = arrayList;
        }
        return new MediaRouteProviderDescriptor(list, bundle.getBoolean("supportsDynamicGroupRoute", false));
    }
}
