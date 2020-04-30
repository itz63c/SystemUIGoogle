package androidx.slice.compat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.text.TextUtils;
import androidx.collection.ArraySet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CompatPermissionManager {
    private final String[] mAutoGrantPermissions;
    private final Context mContext;
    private final int mMyUid;
    private final String mPrefsName;

    public static class PermissionState {
        private final String mKey;
        private final ArraySet<String[]> mPaths;

        PermissionState(Set<String> set, String str, boolean z) {
            ArraySet<String[]> arraySet = new ArraySet<>();
            this.mPaths = arraySet;
            if (z) {
                arraySet.add(new String[0]);
            } else {
                for (String decodeSegments : set) {
                    this.mPaths.add(decodeSegments(decodeSegments));
                }
            }
            this.mKey = str;
        }

        public boolean hasAllPermissions() {
            return hasAccess(Collections.emptyList());
        }

        public String getKey() {
            return this.mKey;
        }

        public Set<String> toPersistable() {
            ArraySet arraySet = new ArraySet();
            Iterator it = this.mPaths.iterator();
            while (it.hasNext()) {
                arraySet.add(encodeSegments((String[]) it.next()));
            }
            return arraySet;
        }

        public boolean hasAccess(List<String> list) {
            String[] strArr = (String[]) list.toArray(new String[list.size()]);
            Iterator it = this.mPaths.iterator();
            while (it.hasNext()) {
                if (isPathPrefixMatch((String[]) it.next(), strArr)) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: 0000 */
        public boolean addPath(List<String> list) {
            String[] strArr = (String[]) list.toArray(new String[list.size()]);
            for (int size = this.mPaths.size() - 1; size >= 0; size--) {
                String[] strArr2 = (String[]) this.mPaths.valueAt(size);
                if (isPathPrefixMatch(strArr2, strArr)) {
                    return false;
                }
                if (isPathPrefixMatch(strArr, strArr2)) {
                    this.mPaths.removeAt(size);
                }
            }
            this.mPaths.add(strArr);
            return true;
        }

        /* access modifiers changed from: 0000 */
        public boolean removePath(List<String> list) {
            String[] strArr = (String[]) list.toArray(new String[list.size()]);
            boolean z = false;
            for (int size = this.mPaths.size() - 1; size >= 0; size--) {
                if (isPathPrefixMatch(strArr, (String[]) this.mPaths.valueAt(size))) {
                    this.mPaths.removeAt(size);
                    z = true;
                }
            }
            return z;
        }

        private boolean isPathPrefixMatch(String[] strArr, String[] strArr2) {
            int length = strArr.length;
            if (strArr2.length < length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                if (!Objects.equals(strArr2[i], strArr[i])) {
                    return false;
                }
            }
            return true;
        }

        private String encodeSegments(String[] strArr) {
            String[] strArr2 = new String[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                strArr2[i] = Uri.encode(strArr[i]);
            }
            return TextUtils.join("/", strArr2);
        }

        private String[] decodeSegments(String str) {
            String[] split = str.split("/", -1);
            for (int i = 0; i < split.length; i++) {
                split[i] = Uri.decode(split[i]);
            }
            return split;
        }
    }

    public CompatPermissionManager(Context context, String str, int i, String[] strArr) {
        this.mContext = context;
        this.mPrefsName = str;
        this.mMyUid = i;
        this.mAutoGrantPermissions = strArr;
    }

    private SharedPreferences getPrefs() {
        return this.mContext.getSharedPreferences(this.mPrefsName, 0);
    }

    @SuppressLint({"WrongConstant"})
    public int checkSlicePermission(Uri uri, int i, int i2) {
        if (i2 == this.mMyUid) {
            return 0;
        }
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i2);
        for (String checkSlicePermission : packagesForUid) {
            if (checkSlicePermission(uri, checkSlicePermission) == 0) {
                return 0;
            }
        }
        for (String checkPermission : this.mAutoGrantPermissions) {
            if (this.mContext.checkPermission(checkPermission, i, i2) == 0) {
                for (String grantSlicePermission : packagesForUid) {
                    grantSlicePermission(uri, grantSlicePermission);
                }
                return 0;
            }
        }
        return this.mContext.checkUriPermission(uri, i, i2, 2);
    }

    private int checkSlicePermission(Uri uri, String str) {
        return getPermissionState(str, uri.getAuthority()).hasAccess(uri.getPathSegments()) ? 0 : -1;
    }

    public void grantSlicePermission(Uri uri, String str) {
        PermissionState permissionState = getPermissionState(str, uri.getAuthority());
        if (permissionState.addPath(uri.getPathSegments())) {
            persist(permissionState);
        }
    }

    public void revokeSlicePermission(Uri uri, String str) {
        PermissionState permissionState = getPermissionState(str, uri.getAuthority());
        if (permissionState.removePath(uri.getPathSegments())) {
            persist(permissionState);
        }
    }

    private synchronized void persist(PermissionState permissionState) {
        Editor putStringSet = getPrefs().edit().putStringSet(permissionState.getKey(), permissionState.toPersistable());
        StringBuilder sb = new StringBuilder();
        sb.append(permissionState.getKey());
        sb.append("_all");
        putStringSet.putBoolean(sb.toString(), permissionState.hasAllPermissions()).apply();
    }

    private PermissionState getPermissionState(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("_");
        sb.append(str2);
        String sb2 = sb.toString();
        Set stringSet = getPrefs().getStringSet(sb2, Collections.emptySet());
        SharedPreferences prefs = getPrefs();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append("_all");
        return new PermissionState(stringSet, sb2, prefs.getBoolean(sb3.toString(), false));
    }
}
