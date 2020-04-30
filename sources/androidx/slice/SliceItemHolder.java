package androidx.slice;

import android.os.Parcelable;
import androidx.core.text.HtmlCompat;
import androidx.core.util.Pair;
import androidx.versionedparcelable.VersionedParcelable;
import java.util.ArrayList;

public class SliceItemHolder implements VersionedParcelable {
    public static HolderHandler sHandler;
    public static final Object sSerializeLock = new Object();
    Object mCallback;
    int mInt = 0;
    long mLong = 0;
    Parcelable mParcelable = null;
    private SliceItemPool mPool;
    String mStr = null;
    public VersionedParcelable mVersionedParcelable = null;

    public interface HolderHandler {
        void handle(SliceItemHolder sliceItemHolder, String str);
    }

    public static class SliceItemPool {
        private final ArrayList<SliceItemHolder> mCached = new ArrayList<>();

        public SliceItemHolder get() {
            if (this.mCached.size() <= 0) {
                return new SliceItemHolder(this);
            }
            ArrayList<SliceItemHolder> arrayList = this.mCached;
            return (SliceItemHolder) arrayList.remove(arrayList.size() - 1);
        }

        public void release(SliceItemHolder sliceItemHolder) {
            sliceItemHolder.mParcelable = null;
            sliceItemHolder.mCallback = null;
            sliceItemHolder.mVersionedParcelable = null;
            sliceItemHolder.mInt = 0;
            sliceItemHolder.mLong = 0;
            sliceItemHolder.mStr = null;
            this.mCached.add(sliceItemHolder);
        }
    }

    SliceItemHolder(SliceItemPool sliceItemPool) {
        this.mPool = sliceItemPool;
    }

    public void release() {
        SliceItemPool sliceItemPool = this.mPool;
        if (sliceItemPool != null) {
            sliceItemPool.release(this);
        }
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public SliceItemHolder(java.lang.String r4, java.lang.Object r5, boolean r6) {
        /*
            r3 = this;
            r3.<init>()
            r0 = 0
            r3.mVersionedParcelable = r0
            r3.mParcelable = r0
            r3.mStr = r0
            r0 = 0
            r3.mInt = r0
            r1 = 0
            r3.mLong = r1
            int r1 = r4.hashCode()
            switch(r1) {
                case -1422950858: goto L_0x0055;
                case 104431: goto L_0x004b;
                case 3327612: goto L_0x0041;
                case 3556653: goto L_0x0037;
                case 100313435: goto L_0x002d;
                case 100358090: goto L_0x0023;
                case 109526418: goto L_0x0019;
                default: goto L_0x0018;
            }
        L_0x0018:
            goto L_0x005f
        L_0x0019:
            java.lang.String r1 = "slice"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x005f
            r1 = 2
            goto L_0x0060
        L_0x0023:
            java.lang.String r1 = "input"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x005f
            r1 = 3
            goto L_0x0060
        L_0x002d:
            java.lang.String r1 = "image"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x005f
            r1 = 1
            goto L_0x0060
        L_0x0037:
            java.lang.String r1 = "text"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x005f
            r1 = 4
            goto L_0x0060
        L_0x0041:
            java.lang.String r1 = "long"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x005f
            r1 = 6
            goto L_0x0060
        L_0x004b:
            java.lang.String r1 = "int"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x005f
            r1 = 5
            goto L_0x0060
        L_0x0055:
            java.lang.String r1 = "action"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x005f
            r1 = r0
            goto L_0x0060
        L_0x005f:
            r1 = -1
        L_0x0060:
            switch(r1) {
                case 0: goto L_0x0090;
                case 1: goto L_0x008b;
                case 2: goto L_0x008b;
                case 3: goto L_0x0086;
                case 4: goto L_0x0076;
                case 5: goto L_0x006d;
                case 6: goto L_0x0064;
                default: goto L_0x0063;
            }
        L_0x0063:
            goto L_0x00ae
        L_0x0064:
            java.lang.Long r5 = (java.lang.Long) r5
            long r5 = r5.longValue()
            r3.mLong = r5
            goto L_0x00ae
        L_0x006d:
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            r3.mInt = r5
            goto L_0x00ae
        L_0x0076:
            boolean r6 = r5 instanceof android.text.Spanned
            if (r6 == 0) goto L_0x0081
            android.text.Spanned r5 = (android.text.Spanned) r5
            java.lang.String r5 = androidx.core.text.HtmlCompat.toHtml(r5, r0)
            goto L_0x0083
        L_0x0081:
            java.lang.String r5 = (java.lang.String) r5
        L_0x0083:
            r3.mStr = r5
            goto L_0x00ae
        L_0x0086:
            android.os.Parcelable r5 = (android.os.Parcelable) r5
            r3.mParcelable = r5
            goto L_0x00ae
        L_0x008b:
            androidx.versionedparcelable.VersionedParcelable r5 = (androidx.versionedparcelable.VersionedParcelable) r5
            r3.mVersionedParcelable = r5
            goto L_0x00ae
        L_0x0090:
            androidx.core.util.Pair r5 = (androidx.core.util.Pair) r5
            F r0 = r5.first
            boolean r1 = r0 instanceof android.app.PendingIntent
            if (r1 == 0) goto L_0x009d
            android.os.Parcelable r0 = (android.os.Parcelable) r0
            r3.mParcelable = r0
            goto L_0x009f
        L_0x009d:
            if (r6 == 0) goto L_0x00a6
        L_0x009f:
            S r5 = r5.second
            androidx.versionedparcelable.VersionedParcelable r5 = (androidx.versionedparcelable.VersionedParcelable) r5
            r3.mVersionedParcelable = r5
            goto L_0x00ae
        L_0x00a6:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.String r4 = "Cannot write callback to parcel"
            r3.<init>(r4)
            throw r3
        L_0x00ae:
            androidx.slice.SliceItemHolder$HolderHandler r5 = sHandler
            if (r5 == 0) goto L_0x00b5
            r5.handle(r3, r4)
        L_0x00b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItemHolder.<init>(java.lang.String, java.lang.Object, boolean):void");
    }

    public Object getObj(String str) {
        HolderHandler holderHandler = sHandler;
        if (holderHandler != null) {
            holderHandler.handle(this, str);
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -1422950858:
                if (str.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (str.equals("int")) {
                    c = 5;
                    break;
                }
                break;
            case 3327612:
                if (str.equals("long")) {
                    c = 6;
                    break;
                }
                break;
            case 3556653:
                if (str.equals("text")) {
                    c = 4;
                    break;
                }
                break;
            case 100313435:
                if (str.equals("image")) {
                    c = 1;
                    break;
                }
                break;
            case 100358090:
                if (str.equals("input")) {
                    c = 3;
                    break;
                }
                break;
            case 109526418:
                if (str.equals("slice")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (this.mParcelable == null && this.mVersionedParcelable == null) {
                    return null;
                }
                Object obj = this.mParcelable;
                if (obj == null) {
                    obj = this.mCallback;
                }
                return new Pair(obj, (Slice) this.mVersionedParcelable);
            case 1:
            case 2:
                return this.mVersionedParcelable;
            case 3:
                return this.mParcelable;
            case 4:
                String str2 = this.mStr;
                return (str2 == null || str2.length() == 0) ? "" : HtmlCompat.fromHtml(this.mStr, 0);
            case 5:
                return Integer.valueOf(this.mInt);
            case 6:
                return Long.valueOf(this.mLong);
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized format ");
                sb.append(str);
                throw new IllegalArgumentException(sb.toString());
        }
    }
}
