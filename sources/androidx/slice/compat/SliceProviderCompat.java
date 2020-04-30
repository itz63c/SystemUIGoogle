package androidx.slice.compat;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Preconditions;
import androidx.slice.Slice;
import androidx.slice.SliceItemHolder;
import androidx.slice.SliceItemHolder.HolderHandler;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.versionedparcelable.ParcelUtils;
import androidx.versionedparcelable.VersionedParcelable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SliceProviderCompat {
    private final Runnable mAnr = new Runnable() {
        public void run() {
            Process.sendSignal(Process.myPid(), 3);
            StringBuilder sb = new StringBuilder();
            sb.append("Timed out while handling slice callback ");
            sb.append(SliceProviderCompat.this.mCallback);
            Log.wtf("SliceProviderCompat", sb.toString());
        }
    };
    String mCallback;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private CompatPermissionManager mPermissionManager;
    private CompatPinnedList mPinnedList;
    private final SliceProvider mProvider;

    private static class ProviderHolder implements AutoCloseable {
        final ContentProviderClient mProvider;

        ProviderHolder(ContentProviderClient contentProviderClient) {
            this.mProvider = contentProviderClient;
        }

        public void close() {
            ContentProviderClient contentProviderClient = this.mProvider;
            if (contentProviderClient != null) {
                if (VERSION.SDK_INT >= 24) {
                    contentProviderClient.close();
                } else {
                    contentProviderClient.release();
                }
            }
        }
    }

    public SliceProviderCompat(SliceProvider sliceProvider, CompatPermissionManager compatPermissionManager, Context context) {
        this.mProvider = sliceProvider;
        this.mContext = context;
        StringBuilder sb = new StringBuilder();
        sb.append("slice_data_");
        sb.append(SliceProviderCompat.class.getName());
        String sb2 = sb.toString();
        String str = "slice_data_all_slice_files";
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(str, 0);
        Set stringSet = sharedPreferences.getStringSet(str, Collections.emptySet());
        if (!stringSet.contains(sb2)) {
            ArraySet arraySet = new ArraySet((Collection<E>) stringSet);
            arraySet.add(sb2);
            sharedPreferences.edit().putStringSet(str, arraySet).commit();
        }
        this.mPinnedList = new CompatPinnedList(this.mContext, sb2);
        this.mPermissionManager = compatPermissionManager;
    }

    private Context getContext() {
        return this.mContext;
    }

    public String getCallingPackage() {
        return this.mProvider.getCallingPackage();
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Parcelable parcelable = null;
        if (str.equals("bind_slice")) {
            Uri uri = (Uri) bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri.getAuthority());
            Slice handleBindSlice = handleBindSlice(uri, getSpecs(bundle), getCallingPackage());
            Bundle bundle2 = new Bundle();
            if ("supports_versioned_parcelable".equals(str2)) {
                synchronized (SliceItemHolder.sSerializeLock) {
                    String str3 = "slice";
                    if (handleBindSlice != null) {
                        parcelable = ParcelUtils.toParcelable(handleBindSlice);
                    }
                    bundle2.putParcelable(str3, parcelable);
                }
            } else {
                if (handleBindSlice != null) {
                    parcelable = handleBindSlice.toBundle();
                }
                bundle2.putParcelable("slice", parcelable);
            }
            return bundle2;
        } else if (str.equals("map_slice")) {
            this.mProvider.onMapIntentToUri((Intent) bundle.getParcelable("slice_intent"));
            throw null;
        } else if (str.equals("map_only")) {
            this.mProvider.onMapIntentToUri((Intent) bundle.getParcelable("slice_intent"));
            throw null;
        } else if (str.equals("pin_slice")) {
            Uri uri2 = (Uri) bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri2.getAuthority());
            Set specs = getSpecs(bundle);
            if (this.mPinnedList.addPin(uri2, bundle.getString("pkg"), specs)) {
                handleSlicePinned(uri2);
            }
            return null;
        } else if (str.equals("unpin_slice")) {
            Uri uri3 = (Uri) bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri3.getAuthority());
            if (this.mPinnedList.removePin(uri3, bundle.getString("pkg"))) {
                handleSliceUnpinned(uri3);
            }
            return null;
        } else if (str.equals("get_specs")) {
            Uri uri4 = (Uri) bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri4.getAuthority());
            Bundle bundle3 = new Bundle();
            ArraySet specs2 = this.mPinnedList.getSpecs(uri4);
            if (specs2.size() != 0) {
                addSpecs(bundle3, specs2);
                return bundle3;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(uri4);
            sb.append(" is not pinned");
            throw new IllegalStateException(sb.toString());
        } else if (str.equals("get_descendants")) {
            Uri uri5 = (Uri) bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri5.getAuthority());
            Bundle bundle4 = new Bundle();
            bundle4.putParcelableArrayList("slice_descendants", new ArrayList(handleGetDescendants(uri5)));
            return bundle4;
        } else if (str.equals("check_perms")) {
            Uri uri6 = (Uri) bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri6.getAuthority());
            int i = bundle.getInt("pid");
            int i2 = bundle.getInt("uid");
            Bundle bundle5 = new Bundle();
            bundle5.putInt("result", this.mPermissionManager.checkSlicePermission(uri6, i, i2));
            return bundle5;
        } else {
            if (str.equals("grant_perms")) {
                Uri uri7 = (Uri) bundle.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri7.getAuthority());
                String string = bundle.getString("pkg");
                if (Binder.getCallingUid() == Process.myUid()) {
                    this.mPermissionManager.grantSlicePermission(uri7, string);
                } else {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
            } else if (str.equals("revoke_perms")) {
                Uri uri8 = (Uri) bundle.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri8.getAuthority());
                String string2 = bundle.getString("pkg");
                if (Binder.getCallingUid() == Process.myUid()) {
                    this.mPermissionManager.revokeSlicePermission(uri8, string2);
                } else {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
            }
            return null;
        }
    }

    private Collection<Uri> handleGetDescendants(Uri uri) {
        this.mCallback = "onGetSliceDescendants";
        return this.mProvider.onGetSliceDescendants(uri);
    }

    private void handleSlicePinned(Uri uri) {
        this.mCallback = "onSlicePinned";
        this.mHandler.postDelayed(this.mAnr, 2000);
        try {
            this.mProvider.onSlicePinned(uri);
            this.mProvider.handleSlicePinned(uri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private void handleSliceUnpinned(Uri uri) {
        this.mCallback = "onSliceUnpinned";
        this.mHandler.postDelayed(this.mAnr, 2000);
        try {
            this.mProvider.onSliceUnpinned(uri);
            this.mProvider.handleSliceUnpinned(uri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private Slice handleBindSlice(Uri uri, Set<SliceSpec> set, String str) {
        if (str == null) {
            str = getContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        }
        if (this.mPermissionManager.checkSlicePermission(uri, Binder.getCallingPid(), Binder.getCallingUid()) != 0) {
            return this.mProvider.createPermissionSlice(uri, str);
        }
        return onBindSliceStrict(uri, set);
    }

    private Slice onBindSliceStrict(Uri uri, Set<SliceSpec> set) {
        ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        this.mCallback = "onBindSlice";
        this.mHandler.postDelayed(this.mAnr, 2000);
        try {
            StrictMode.setThreadPolicy(new Builder().detectAll().penaltyDeath().build());
            SliceProvider.setSpecs(set);
            try {
                Slice onBindSlice = this.mProvider.onBindSlice(uri);
                SliceProvider.setSpecs(null);
                this.mHandler.removeCallbacks(this.mAnr);
                StrictMode.setThreadPolicy(threadPolicy);
                return onBindSlice;
            } catch (Exception e) {
                String str = "SliceProviderCompat";
                StringBuilder sb = new StringBuilder();
                sb.append("Slice with URI ");
                sb.append(uri.toString());
                sb.append(" is invalid.");
                Log.wtf(str, sb.toString(), e);
                SliceProvider.setSpecs(null);
                this.mHandler.removeCallbacks(this.mAnr);
                StrictMode.setThreadPolicy(threadPolicy);
                return null;
            }
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(threadPolicy);
            throw th;
        }
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                addSpecs(bundle, set);
                return parseSlice(context, acquireClient.mProvider.call("bind_slice", "supports_versioned_parcelable", bundle));
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to bind slice", e);
                return null;
            } finally {
                acquireClient.close();
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown URI ");
            sb.append(uri);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public static void addSpecs(Bundle bundle, Set<SliceSpec> set) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (SliceSpec sliceSpec : set) {
            arrayList.add(sliceSpec.getType());
            arrayList2.add(Integer.valueOf(sliceSpec.getRevision()));
        }
        bundle.putStringArrayList("specs", arrayList);
        bundle.putIntegerArrayList("revs", arrayList2);
    }

    public static Set<SliceSpec> getSpecs(Bundle bundle) {
        ArraySet arraySet = new ArraySet();
        ArrayList stringArrayList = bundle.getStringArrayList("specs");
        ArrayList integerArrayList = bundle.getIntegerArrayList("revs");
        if (!(stringArrayList == null || integerArrayList == null)) {
            for (int i = 0; i < stringArrayList.size(); i++) {
                arraySet.add(new SliceSpec((String) stringArrayList.get(i), ((Integer) integerArrayList.get(i)).intValue()));
            }
        }
        return arraySet;
    }

    public static Slice bindSlice(Context context, Intent intent, Set<SliceSpec> set) {
        Preconditions.checkNotNull(intent, "intent");
        Preconditions.checkArgument((intent.getComponent() == null && intent.getPackage() == null && intent.getData() == null) ? false : true, String.format("Slice intent must be explicit %s", new Object[]{intent}));
        ContentResolver contentResolver = context.getContentResolver();
        Uri data = intent.getData();
        if (data != null) {
            if ("vnd.android.slice".equals(contentResolver.getType(data))) {
                return bindSlice(context, data, set);
            }
        }
        Intent intent2 = new Intent(intent);
        String str = "android.app.slice.category.SLICE";
        if (!intent2.hasCategory(str)) {
            intent2.addCategory(str);
        }
        List queryIntentContentProviders = context.getPackageManager().queryIntentContentProviders(intent2, 0);
        if (queryIntentContentProviders == null || queryIntentContentProviders.isEmpty()) {
            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 128);
            if (resolveActivity != null) {
                ActivityInfo activityInfo = resolveActivity.activityInfo;
                if (activityInfo != null) {
                    Bundle bundle = activityInfo.metaData;
                    if (bundle != null) {
                        String str2 = "android.metadata.SLICE_URI";
                        if (bundle.containsKey(str2)) {
                            return bindSlice(context, Uri.parse(resolveActivity.activityInfo.metaData.getString(str2)), set);
                        }
                    }
                }
            }
            return null;
        }
        Uri build = new Uri.Builder().scheme("content").authority(((ResolveInfo) queryIntentContentProviders.get(0)).providerInfo.authority).build();
        ProviderHolder acquireClient = acquireClient(contentResolver, build);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("slice_intent", intent);
                addSpecs(bundle2, set);
                return parseSlice(context, acquireClient.mProvider.call("map_slice", "supports_versioned_parcelable", bundle2));
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to bind slice", e);
                return null;
            } finally {
                acquireClient.close();
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown URI ");
            sb.append(build);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private static Slice parseSlice(final Context context, Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        synchronized (SliceItemHolder.sSerializeLock) {
            try {
                SliceItemHolder.sHandler = new HolderHandler() {
                    public void handle(SliceItemHolder sliceItemHolder, String str) {
                        VersionedParcelable versionedParcelable = sliceItemHolder.mVersionedParcelable;
                        if (versionedParcelable instanceof IconCompat) {
                            IconCompat iconCompat = (IconCompat) versionedParcelable;
                            iconCompat.checkResource(context);
                            if (iconCompat.getType() == 2 && iconCompat.getResId() == 0) {
                                sliceItemHolder.mVersionedParcelable = null;
                            }
                        }
                    }
                };
                bundle.setClassLoader(SliceProviderCompat.class.getClassLoader());
                Parcelable parcelable = bundle.getParcelable("slice");
                if (parcelable == null) {
                    SliceItemHolder.sHandler = null;
                    return null;
                } else if (parcelable instanceof Bundle) {
                    Slice slice = new Slice((Bundle) parcelable);
                    SliceItemHolder.sHandler = null;
                    return slice;
                } else {
                    Slice slice2 = (Slice) ParcelUtils.fromParcelable(parcelable);
                    SliceItemHolder.sHandler = null;
                    return slice2;
                }
            } catch (Throwable th) {
                SliceItemHolder.sHandler = null;
                throw th;
            }
        }
    }

    public static void pinSlice(Context context, Uri uri, Set<SliceSpec> set) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                bundle.putString("pkg", context.getPackageName());
                addSpecs(bundle, set);
                acquireClient.mProvider.call("pin_slice", "supports_versioned_parcelable", bundle);
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to pin slice", e);
            } catch (Throwable th) {
                acquireClient.close();
                throw th;
            }
            acquireClient.close();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown URI ");
        sb.append(uri);
        throw new IllegalArgumentException(sb.toString());
    }

    public static void unpinSlice(Context context, Uri uri, Set<SliceSpec> set) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                bundle.putString("pkg", context.getPackageName());
                addSpecs(bundle, set);
                acquireClient.mProvider.call("unpin_slice", "supports_versioned_parcelable", bundle);
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to unpin slice", e);
            } catch (Throwable th) {
                acquireClient.close();
                throw th;
            }
            acquireClient.close();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown URI ");
        sb.append(uri);
        throw new IllegalArgumentException(sb.toString());
    }

    public static Set<SliceSpec> getPinnedSpecs(Context context, Uri uri) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                Bundle call = acquireClient.mProvider.call("get_specs", "supports_versioned_parcelable", bundle);
                if (call != null) {
                    Set<SliceSpec> specs = getSpecs(call);
                    acquireClient.close();
                    return specs;
                }
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to get pinned specs", e);
            } catch (Throwable th) {
                acquireClient.close();
                throw th;
            }
            acquireClient.close();
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown URI ");
        sb.append(uri);
        throw new IllegalArgumentException(sb.toString());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
        if (r2 != null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0038, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void grantSlicePermission(android.content.Context r2, java.lang.String r3, java.lang.String r4, android.net.Uri r5) {
        /*
            android.content.ContentResolver r2 = r2.getContentResolver()
            androidx.slice.compat.SliceProviderCompat$ProviderHolder r2 = acquireClient(r2, r5)     // Catch:{ RemoteException -> 0x0039 }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ all -> 0x002b }
            r0.<init>()     // Catch:{ all -> 0x002b }
            java.lang.String r1 = "slice_uri"
            r0.putParcelable(r1, r5)     // Catch:{ all -> 0x002b }
            java.lang.String r5 = "provider_pkg"
            r0.putString(r5, r3)     // Catch:{ all -> 0x002b }
            java.lang.String r3 = "pkg"
            r0.putString(r3, r4)     // Catch:{ all -> 0x002b }
            android.content.ContentProviderClient r3 = r2.mProvider     // Catch:{ all -> 0x002b }
            java.lang.String r4 = "grant_perms"
            java.lang.String r5 = "supports_versioned_parcelable"
            r3.call(r4, r5, r0)     // Catch:{ all -> 0x002b }
            if (r2 == 0) goto L_0x0041
            r2.close()     // Catch:{ RemoteException -> 0x0039 }
            goto L_0x0041
        L_0x002b:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002d }
        L_0x002d:
            r4 = move-exception
            if (r2 == 0) goto L_0x0038
            r2.close()     // Catch:{ all -> 0x0034 }
            goto L_0x0038
        L_0x0034:
            r2 = move-exception
            r3.addSuppressed(r2)     // Catch:{ RemoteException -> 0x0039 }
        L_0x0038:
            throw r4     // Catch:{ RemoteException -> 0x0039 }
        L_0x0039:
            r2 = move-exception
            java.lang.String r3 = "SliceProviderCompat"
            java.lang.String r4 = "Unable to get slice descendants"
            android.util.Log.e(r3, r4, r2)
        L_0x0041:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.SliceProviderCompat.grantSlicePermission(android.content.Context, java.lang.String, java.lang.String, android.net.Uri):void");
    }

    public static List<Uri> getPinnedSlices(Context context) {
        ArrayList arrayList = new ArrayList();
        String str = "slice_data_all_slice_files";
        for (String compatPinnedList : context.getSharedPreferences(str, 0).getStringSet(str, Collections.emptySet())) {
            arrayList.addAll(new CompatPinnedList(context, compatPinnedList).getPinnedSlices());
        }
        return arrayList;
    }

    private static ProviderHolder acquireClient(ContentResolver contentResolver, Uri uri) {
        ContentProviderClient acquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uri);
        if (acquireUnstableContentProviderClient != null) {
            return new ProviderHolder(acquireUnstableContentProviderClient);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("No provider found for ");
        sb.append(uri);
        throw new IllegalArgumentException(sb.toString());
    }
}
