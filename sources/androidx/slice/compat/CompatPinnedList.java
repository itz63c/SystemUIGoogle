package androidx.slice.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.collection.ArraySet;
import androidx.core.util.ObjectsCompat;
import androidx.slice.SliceSpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CompatPinnedList {
    private final Context mContext;
    private final String mPrefsName;

    public CompatPinnedList(Context context, String str) {
        this.mContext = context;
        this.mPrefsName = str;
    }

    private SharedPreferences getPrefs() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(this.mPrefsName, 0);
        String str = "last_boot";
        long j = sharedPreferences.getLong(str, 0);
        long bootTime = getBootTime();
        if (Math.abs(j - bootTime) > 2000) {
            sharedPreferences.edit().clear().putLong(str, bootTime).apply();
        }
        return sharedPreferences;
    }

    public List<Uri> getPinnedSlices() {
        ArrayList arrayList = new ArrayList();
        for (String str : getPrefs().getAll().keySet()) {
            if (str.startsWith("pinned_")) {
                Uri parse = Uri.parse(str.substring(7));
                if (!getPins(parse).isEmpty()) {
                    arrayList.add(parse);
                }
            }
        }
        return arrayList;
    }

    private Set<String> getPins(Uri uri) {
        SharedPreferences prefs = getPrefs();
        StringBuilder sb = new StringBuilder();
        sb.append("pinned_");
        sb.append(uri.toString());
        return prefs.getStringSet(sb.toString(), new ArraySet());
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:13:0x0063=Splitter:B:13:0x0063, B:20:0x007b=Splitter:B:20:0x007b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized androidx.collection.ArraySet<androidx.slice.SliceSpec> getSpecs(android.net.Uri r7) {
        /*
            r6 = this;
            monitor-enter(r6)
            androidx.collection.ArraySet r0 = new androidx.collection.ArraySet     // Catch:{ all -> 0x0082 }
            r0.<init>()     // Catch:{ all -> 0x0082 }
            android.content.SharedPreferences r1 = r6.getPrefs()     // Catch:{ all -> 0x0082 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0082 }
            r2.<init>()     // Catch:{ all -> 0x0082 }
            java.lang.String r3 = "spec_names_"
            r2.append(r3)     // Catch:{ all -> 0x0082 }
            java.lang.String r3 = r7.toString()     // Catch:{ all -> 0x0082 }
            r2.append(r3)     // Catch:{ all -> 0x0082 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0082 }
            r3 = 0
            java.lang.String r2 = r1.getString(r2, r3)     // Catch:{ all -> 0x0082 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0082 }
            r4.<init>()     // Catch:{ all -> 0x0082 }
            java.lang.String r5 = "spec_revs_"
            r4.append(r5)     // Catch:{ all -> 0x0082 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0082 }
            r4.append(r7)     // Catch:{ all -> 0x0082 }
            java.lang.String r7 = r4.toString()     // Catch:{ all -> 0x0082 }
            java.lang.String r7 = r1.getString(r7, r3)     // Catch:{ all -> 0x0082 }
            boolean r1 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0082 }
            if (r1 != 0) goto L_0x007b
            boolean r1 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x0082 }
            if (r1 == 0) goto L_0x004a
            goto L_0x007b
        L_0x004a:
            java.lang.String r1 = ","
            r3 = -1
            java.lang.String[] r1 = r2.split(r1, r3)     // Catch:{ all -> 0x0082 }
            java.lang.String r2 = ","
            java.lang.String[] r7 = r7.split(r2, r3)     // Catch:{ all -> 0x0082 }
            int r2 = r1.length     // Catch:{ all -> 0x0082 }
            int r3 = r7.length     // Catch:{ all -> 0x0082 }
            if (r2 == r3) goto L_0x0062
            androidx.collection.ArraySet r7 = new androidx.collection.ArraySet     // Catch:{ all -> 0x0082 }
            r7.<init>()     // Catch:{ all -> 0x0082 }
            monitor-exit(r6)
            return r7
        L_0x0062:
            r2 = 0
        L_0x0063:
            int r3 = r1.length     // Catch:{ all -> 0x0082 }
            if (r2 >= r3) goto L_0x0079
            androidx.slice.SliceSpec r3 = new androidx.slice.SliceSpec     // Catch:{ all -> 0x0082 }
            r4 = r1[r2]     // Catch:{ all -> 0x0082 }
            r5 = r7[r2]     // Catch:{ all -> 0x0082 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ all -> 0x0082 }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x0082 }
            r0.add(r3)     // Catch:{ all -> 0x0082 }
            int r2 = r2 + 1
            goto L_0x0063
        L_0x0079:
            monitor-exit(r6)
            return r0
        L_0x007b:
            androidx.collection.ArraySet r7 = new androidx.collection.ArraySet     // Catch:{ all -> 0x0082 }
            r7.<init>()     // Catch:{ all -> 0x0082 }
            monitor-exit(r6)
            return r7
        L_0x0082:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.CompatPinnedList.getSpecs(android.net.Uri):androidx.collection.ArraySet");
    }

    private void setPins(Uri uri, Set<String> set) {
        Editor edit = getPrefs().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("pinned_");
        sb.append(uri.toString());
        edit.putStringSet(sb.toString(), set).apply();
    }

    private void setSpecs(Uri uri, ArraySet<SliceSpec> arraySet) {
        String[] strArr = new String[arraySet.size()];
        String[] strArr2 = new String[arraySet.size()];
        for (int i = 0; i < arraySet.size(); i++) {
            strArr[i] = ((SliceSpec) arraySet.valueAt(i)).getType();
            strArr2[i] = String.valueOf(((SliceSpec) arraySet.valueAt(i)).getRevision());
        }
        Editor edit = getPrefs().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("spec_names_");
        sb.append(uri.toString());
        String str = ",";
        Editor putString = edit.putString(sb.toString(), TextUtils.join(str, strArr));
        StringBuilder sb2 = new StringBuilder();
        sb2.append("spec_revs_");
        sb2.append(uri.toString());
        putString.putString(sb2.toString(), TextUtils.join(str, strArr2)).apply();
    }

    /* access modifiers changed from: protected */
    public long getBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    public synchronized boolean addPin(Uri uri, String str, Set<SliceSpec> set) {
        boolean isEmpty;
        Set pins = getPins(uri);
        isEmpty = pins.isEmpty();
        pins.add(str);
        setPins(uri, pins);
        if (isEmpty) {
            setSpecs(uri, new ArraySet((Collection<E>) set));
        } else {
            ArraySet specs = getSpecs(uri);
            mergeSpecs(specs, set);
            setSpecs(uri, specs);
        }
        return isEmpty;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0029, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002b, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean removePin(android.net.Uri r4, java.lang.String r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.util.Set r0 = r3.getPins(r4)     // Catch:{ all -> 0x002c }
            boolean r1 = r0.isEmpty()     // Catch:{ all -> 0x002c }
            r2 = 0
            if (r1 != 0) goto L_0x002a
            boolean r1 = r0.contains(r5)     // Catch:{ all -> 0x002c }
            if (r1 != 0) goto L_0x0013
            goto L_0x002a
        L_0x0013:
            r0.remove(r5)     // Catch:{ all -> 0x002c }
            r3.setPins(r4, r0)     // Catch:{ all -> 0x002c }
            androidx.collection.ArraySet r5 = new androidx.collection.ArraySet     // Catch:{ all -> 0x002c }
            r5.<init>()     // Catch:{ all -> 0x002c }
            r3.setSpecs(r4, r5)     // Catch:{ all -> 0x002c }
            int r4 = r0.size()     // Catch:{ all -> 0x002c }
            if (r4 != 0) goto L_0x0028
            r2 = 1
        L_0x0028:
            monitor-exit(r3)
            return r2
        L_0x002a:
            monitor-exit(r3)
            return r2
        L_0x002c:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.CompatPinnedList.removePin(android.net.Uri, java.lang.String):boolean");
    }

    private static ArraySet<SliceSpec> mergeSpecs(ArraySet<SliceSpec> arraySet, Set<SliceSpec> set) {
        int i;
        int i2 = 0;
        while (i2 < arraySet.size()) {
            SliceSpec sliceSpec = (SliceSpec) arraySet.valueAt(i2);
            SliceSpec findSpec = findSpec(set, sliceSpec.getType());
            if (findSpec == null) {
                i = i2 - 1;
                arraySet.removeAt(i2);
            } else if (findSpec.getRevision() < sliceSpec.getRevision()) {
                i = i2 - 1;
                arraySet.removeAt(i2);
                arraySet.add(findSpec);
            } else {
                i2++;
            }
            i2 = i;
            i2++;
        }
        return arraySet;
    }

    private static SliceSpec findSpec(Set<SliceSpec> set, String str) {
        for (SliceSpec sliceSpec : set) {
            if (ObjectsCompat.equals(sliceSpec.getType(), str)) {
                return sliceSpec;
            }
        }
        return null;
    }
}
