package androidx.slice;

import android.app.slice.SliceManager;
import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.os.Process;
import android.os.UserHandle;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

class SliceManagerWrapper extends SliceManager {
    private final SliceManager mManager;

    SliceManagerWrapper(Context context) {
        this((SliceManager) context.getSystemService(SliceManager.class));
    }

    SliceManagerWrapper(SliceManager sliceManager) {
        this.mManager = sliceManager;
    }

    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        if (VERSION.SDK_INT == 28) {
            uri = maybeAddCurrentUserId(uri);
        }
        return SliceConvert.wrap(this.mManager.getPinnedSpecs(uri));
    }

    public List<Uri> getPinnedSlices() {
        return this.mManager.getPinnedSlices();
    }

    private Uri maybeAddCurrentUserId(Uri uri) {
        if (uri != null) {
            String str = "@";
            if (!uri.getAuthority().contains(str)) {
                String authority = uri.getAuthority();
                Builder buildUpon = uri.buildUpon();
                StringBuilder sb = new StringBuilder();
                sb.append(getCurrentUserId());
                sb.append(str);
                sb.append(authority);
                return buildUpon.encodedAuthority(sb.toString()).build();
            }
        }
        return uri;
    }

    private int getCurrentUserId() {
        UserHandle myUserHandle = Process.myUserHandle();
        try {
            return ((Integer) myUserHandle.getClass().getDeclaredMethod("getIdentifier", new Class[0]).invoke(myUserHandle, new Object[0])).intValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            return 0;
        }
    }
}
