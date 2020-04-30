package androidx.slice;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Process;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import androidx.core.app.CoreComponentFactory.CompatWrapped;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice.Builder;
import androidx.slice.compat.CompatPermissionManager;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.compat.SliceProviderWrapperContainer$SliceProviderWrapper;
import androidx.slice.core.R$drawable;
import androidx.slice.core.R$string;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class SliceProvider extends ContentProvider implements CompatWrapped {
    private static Clock sClock;
    private static Set<SliceSpec> sSpecs;
    private String[] mAuthorities;
    private String mAuthority;
    private final String[] mAutoGrantPermissions = new String[0];
    private SliceProviderCompat mCompat;
    private Context mContext = null;
    private List<Uri> mPinnedSliceUris;
    private final Object mPinnedSliceUrisLock = new Object();

    public final int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        return 0;
    }

    public final Uri canonicalize(Uri uri) {
        return null;
    }

    public final int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public final Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public abstract Slice onBindSlice(Uri uri);

    public PendingIntent onCreatePermissionRequest(Uri uri, String str) {
        return null;
    }

    public abstract boolean onCreateSliceProvider();

    public void onSlicePinned(Uri uri) {
    }

    public void onSliceUnpinned(Uri uri) {
    }

    public final Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) {
        return null;
    }

    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return null;
    }

    public final int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public Object getWrapper() {
        if (VERSION.SDK_INT >= 28) {
            return new SliceProviderWrapperContainer$SliceProviderWrapper(this, this.mAutoGrantPermissions);
        }
        return null;
    }

    public final boolean onCreate() {
        int i = VERSION.SDK_INT;
        if (i < 19) {
            return false;
        }
        if (i < 28) {
            this.mCompat = new SliceProviderCompat(this, onCreatePermissionManager(this.mAutoGrantPermissions), getContext());
        }
        return onCreateSliceProvider();
    }

    /* access modifiers changed from: protected */
    public CompatPermissionManager onCreatePermissionManager(String[] strArr) {
        Context context = getContext();
        StringBuilder sb = new StringBuilder();
        sb.append("slice_perms_");
        sb.append(getClass().getName());
        return new CompatPermissionManager(context, sb.toString(), Process.myUid(), strArr);
    }

    public final String getType(Uri uri) {
        if (VERSION.SDK_INT < 19) {
            return null;
        }
        return "vnd.android.slice";
    }

    public void attachInfo(Context context, ProviderInfo providerInfo) {
        super.attachInfo(context, providerInfo);
        if (this.mContext == null) {
            this.mContext = context;
            if (providerInfo != null) {
                setAuthorities(providerInfo.authority);
            }
        }
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Bundle bundle2 = null;
        if (VERSION.SDK_INT < 19 || bundle == null) {
            return null;
        }
        SliceProviderCompat sliceProviderCompat = this.mCompat;
        if (sliceProviderCompat != null) {
            bundle2 = sliceProviderCompat.call(str, str2, bundle);
        }
        return bundle2;
    }

    private void setAuthorities(String str) {
        if (str == null) {
            return;
        }
        if (str.indexOf(59) == -1) {
            this.mAuthority = str;
            this.mAuthorities = null;
            return;
        }
        this.mAuthority = null;
        this.mAuthorities = str.split(";");
    }

    private boolean matchesOurAuthorities(String str) {
        String str2 = this.mAuthority;
        if (str2 != null) {
            return str2.equals(str);
        }
        String[] strArr = this.mAuthorities;
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                if (this.mAuthorities[i].equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Slice createPermissionSlice(Uri uri, String str) {
        Context context = getContext();
        PendingIntent onCreatePermissionRequest = onCreatePermissionRequest(uri, str);
        if (onCreatePermissionRequest == null) {
            onCreatePermissionRequest = createPermissionIntent(context, uri, str);
        }
        Builder builder = new Builder(uri);
        Builder builder2 = new Builder(builder);
        builder2.addIcon(IconCompat.createWithResource(context, R$drawable.abc_ic_permission), (String) null, new String[0]);
        builder2.addHints(Arrays.asList(new String[]{"title", "shortcut"}));
        builder2.addAction(onCreatePermissionRequest, new Builder(builder).build(), null);
        TypedValue typedValue = new TypedValue();
        new ContextThemeWrapper(context, 16974123).getTheme().resolveAttribute(16843829, typedValue, true);
        int i = typedValue.data;
        Builder builder3 = new Builder(uri.buildUpon().appendPath("permission").build());
        builder3.addIcon(IconCompat.createWithResource(context, R$drawable.abc_ic_arrow_forward), (String) null, new String[0]);
        builder3.addText(getPermissionString(context, str), (String) null, new String[0]);
        builder3.addInt(i, "color", new String[0]);
        builder3.addSubSlice(builder2.build(), null);
        builder.addSubSlice(builder3.build(), null);
        builder.addHints(Arrays.asList(new String[]{"permission_request"}));
        return builder.build();
    }

    private static PendingIntent createPermissionIntent(Context context, Uri uri, String str) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context.getPackageName(), "androidx.slice.compat.SlicePermissionActivity"));
        intent.putExtra("slice_uri", uri);
        intent.putExtra("pkg", str);
        intent.putExtra("provider_pkg", context.getPackageName());
        intent.setData(uri.buildUpon().appendQueryParameter("package", str).build());
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private static CharSequence getPermissionString(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return context.getString(R$string.abc_slices_permission_request, new Object[]{packageManager.getApplicationInfo(str, 0).loadLabel(packageManager), context.getApplicationInfo().loadLabel(packageManager)});
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Unknown calling app", e);
        }
    }

    public void handleSlicePinned(Uri uri) {
        List pinnedSlices = getPinnedSlices();
        if (!pinnedSlices.contains(uri)) {
            pinnedSlices.add(uri);
        }
    }

    public void handleSliceUnpinned(Uri uri) {
        List pinnedSlices = getPinnedSlices();
        if (pinnedSlices.contains(uri)) {
            pinnedSlices.remove(uri);
        }
    }

    public Uri onMapIntentToUri(Intent intent) {
        throw new UnsupportedOperationException("This provider has not implemented intent to uri mapping");
    }

    public Collection<Uri> onGetSliceDescendants(Uri uri) {
        return Collections.emptyList();
    }

    public List<Uri> getPinnedSlices() {
        synchronized (this.mPinnedSliceUrisLock) {
            if (this.mPinnedSliceUris == null) {
                this.mPinnedSliceUris = new ArrayList(SliceManager.getInstance(getContext()).getPinnedSlices());
            }
        }
        return this.mPinnedSliceUris;
    }

    public void validateIncomingAuthority(String str) throws SecurityException {
        String str2;
        if (!matchesOurAuthorities(getAuthorityWithoutUserId(str))) {
            StringBuilder sb = new StringBuilder();
            sb.append("The authority ");
            sb.append(str);
            sb.append(" does not match the one of the contentProvider: ");
            String sb2 = sb.toString();
            if (this.mAuthority != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(sb2);
                sb3.append(this.mAuthority);
                str2 = sb3.toString();
            } else {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(sb2);
                sb4.append(Arrays.toString(this.mAuthorities));
                str2 = sb4.toString();
            }
            throw new SecurityException(str2);
        }
    }

    private static String getAuthorityWithoutUserId(String str) {
        if (str == null) {
            return null;
        }
        return str.substring(str.lastIndexOf(64) + 1);
    }

    public static void setSpecs(Set<SliceSpec> set) {
        sSpecs = set;
    }

    public static Set<SliceSpec> getCurrentSpecs() {
        return sSpecs;
    }

    public static Clock getClock() {
        return sClock;
    }
}
