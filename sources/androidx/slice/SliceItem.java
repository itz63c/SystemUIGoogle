package androidx.slice;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Pair;
import androidx.versionedparcelable.CustomVersionedParcelable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public final class SliceItem extends CustomVersionedParcelable {
    String mFormat;
    protected String[] mHints;
    SliceItemHolder mHolder;
    Object mObj;
    CharSequence mSanitizedText;
    String mSubType;

    public interface ActionHandler {
        void onAction(SliceItem sliceItem, Context context, Intent intent);
    }

    public SliceItem(Object obj, String str, String str2, String[] strArr) {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
        this.mHints = strArr;
        this.mFormat = str;
        this.mSubType = str2;
        this.mObj = obj;
    }

    public SliceItem(Object obj, String str, String str2, List<String> list) {
        this(obj, str, str2, (String[]) list.toArray(new String[list.size()]));
    }

    public SliceItem() {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
    }

    public SliceItem(PendingIntent pendingIntent, Slice slice, String str, String str2, String[] strArr) {
        this((Object) new Pair(pendingIntent, slice), str, str2, strArr);
    }

    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    public void addHint(String str) {
        this.mHints = (String[]) ArrayUtils.appendElement(String.class, this.mHints, str);
    }

    public String getFormat() {
        return this.mFormat;
    }

    public String getSubType() {
        return this.mSubType;
    }

    public CharSequence getText() {
        return (CharSequence) this.mObj;
    }

    public CharSequence getSanitizedText() {
        if (this.mSanitizedText == null) {
            this.mSanitizedText = sanitizeText(getText());
        }
        return this.mSanitizedText;
    }

    public IconCompat getIcon() {
        return (IconCompat) this.mObj;
    }

    public PendingIntent getAction() {
        F f = ((Pair) this.mObj).first;
        if (f instanceof PendingIntent) {
            return (PendingIntent) f;
        }
        return null;
    }

    public void fireAction(Context context, Intent intent) throws CanceledException {
        fireActionInternal(context, intent);
    }

    public boolean fireActionInternal(Context context, Intent intent) throws CanceledException {
        F f = ((Pair) this.mObj).first;
        if (f instanceof PendingIntent) {
            ((PendingIntent) f).send(context, 0, intent, null, null);
            return false;
        }
        ((ActionHandler) f).onAction(this, context, intent);
        return true;
    }

    public RemoteInput getRemoteInput() {
        return (RemoteInput) this.mObj;
    }

    public int getInt() {
        return ((Integer) this.mObj).intValue();
    }

    public Slice getSlice() {
        if ("action".equals(getFormat())) {
            return (Slice) ((Pair) this.mObj).second;
        }
        return (Slice) this.mObj;
    }

    public long getLong() {
        return ((Long) this.mObj).longValue();
    }

    public boolean hasHint(String str) {
        return ArrayUtils.contains(this.mHints, str);
    }

    public SliceItem(Bundle bundle) {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
        this.mHints = bundle.getStringArray("hints");
        this.mFormat = bundle.getString("format");
        this.mSubType = bundle.getString("subtype");
        this.mObj = readObj(this.mFormat, bundle);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putStringArray("hints", this.mHints);
        bundle.putString("format", this.mFormat);
        bundle.putString("subtype", this.mSubType);
        writeObj(bundle, this.mObj, this.mFormat);
        return bundle;
    }

    public boolean hasAnyHints(String... strArr) {
        if (strArr == null) {
            return false;
        }
        for (String contains : strArr) {
            if (ArrayUtils.contains(this.mHints, contains)) {
                return true;
            }
        }
        return false;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeObj(android.os.Bundle r2, java.lang.Object r3, java.lang.String r4) {
        /*
            r1 = this;
            int r0 = r4.hashCode()
            switch(r0) {
                case -1422950858: goto L_0x0044;
                case 104431: goto L_0x003a;
                case 3327612: goto L_0x0030;
                case 3556653: goto L_0x0026;
                case 100313435: goto L_0x001c;
                case 100358090: goto L_0x0012;
                case 109526418: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x004e
        L_0x0008:
            java.lang.String r0 = "slice"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x004e
            r4 = 2
            goto L_0x004f
        L_0x0012:
            java.lang.String r0 = "input"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x004e
            r4 = 1
            goto L_0x004f
        L_0x001c:
            java.lang.String r0 = "image"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x004e
            r4 = 0
            goto L_0x004f
        L_0x0026:
            java.lang.String r0 = "text"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x004e
            r4 = 4
            goto L_0x004f
        L_0x0030:
            java.lang.String r0 = "long"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x004e
            r4 = 6
            goto L_0x004f
        L_0x003a:
            java.lang.String r0 = "int"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x004e
            r4 = 5
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "action"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x004e
            r4 = 3
            goto L_0x004f
        L_0x004e:
            r4 = -1
        L_0x004f:
            java.lang.String r0 = "obj"
            switch(r4) {
                case 0: goto L_0x009a;
                case 1: goto L_0x0094;
                case 2: goto L_0x008a;
                case 3: goto L_0x0073;
                case 4: goto L_0x006d;
                case 5: goto L_0x0061;
                case 6: goto L_0x0055;
                default: goto L_0x0054;
            }
        L_0x0054:
            goto L_0x00a3
        L_0x0055:
            java.lang.Object r1 = r1.mObj
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
            r2.putLong(r0, r3)
            goto L_0x00a3
        L_0x0061:
            java.lang.Object r1 = r1.mObj
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2.putInt(r0, r1)
            goto L_0x00a3
        L_0x006d:
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            r2.putCharSequence(r0, r3)
            goto L_0x00a3
        L_0x0073:
            androidx.core.util.Pair r3 = (androidx.core.util.Pair) r3
            F r1 = r3.first
            android.app.PendingIntent r1 = (android.app.PendingIntent) r1
            r2.putParcelable(r0, r1)
            S r1 = r3.second
            androidx.slice.Slice r1 = (androidx.slice.Slice) r1
            android.os.Bundle r1 = r1.toBundle()
            java.lang.String r3 = "obj_2"
            r2.putBundle(r3, r1)
            goto L_0x00a3
        L_0x008a:
            androidx.slice.Slice r3 = (androidx.slice.Slice) r3
            android.os.Bundle r1 = r3.toBundle()
            r2.putParcelable(r0, r1)
            goto L_0x00a3
        L_0x0094:
            android.os.Parcelable r3 = (android.os.Parcelable) r3
            r2.putParcelable(r0, r3)
            goto L_0x00a3
        L_0x009a:
            androidx.core.graphics.drawable.IconCompat r3 = (androidx.core.graphics.drawable.IconCompat) r3
            android.os.Bundle r1 = r3.toBundle()
            r2.putBundle(r0, r1)
        L_0x00a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItem.writeObj(android.os.Bundle, java.lang.Object, java.lang.String):void");
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Object readObj(java.lang.String r3, android.os.Bundle r4) {
        /*
            int r0 = r3.hashCode()
            switch(r0) {
                case -1422950858: goto L_0x0044;
                case 104431: goto L_0x003a;
                case 3327612: goto L_0x0030;
                case 3556653: goto L_0x0026;
                case 100313435: goto L_0x001c;
                case 100358090: goto L_0x0012;
                case 109526418: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x004e
        L_0x0008:
            java.lang.String r0 = "slice"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 2
            goto L_0x004f
        L_0x0012:
            java.lang.String r0 = "input"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 1
            goto L_0x004f
        L_0x001c:
            java.lang.String r0 = "image"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 0
            goto L_0x004f
        L_0x0026:
            java.lang.String r0 = "text"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 3
            goto L_0x004f
        L_0x0030:
            java.lang.String r0 = "long"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 6
            goto L_0x004f
        L_0x003a:
            java.lang.String r0 = "int"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 5
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "action"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 4
            goto L_0x004f
        L_0x004e:
            r0 = -1
        L_0x004f:
            java.lang.String r1 = "obj"
            switch(r0) {
                case 0: goto L_0x00a6;
                case 1: goto L_0x00a1;
                case 2: goto L_0x0097;
                case 3: goto L_0x0092;
                case 4: goto L_0x007d;
                case 5: goto L_0x0074;
                case 6: goto L_0x006b;
                default: goto L_0x0054;
            }
        L_0x0054:
            java.lang.RuntimeException r4 = new java.lang.RuntimeException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unsupported type "
            r0.append(r1)
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            r4.<init>(r3)
            throw r4
        L_0x006b:
            long r3 = r4.getLong(r1)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            return r3
        L_0x0074:
            int r3 = r4.getInt(r1)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            return r3
        L_0x007d:
            androidx.core.util.Pair r3 = new androidx.core.util.Pair
            android.os.Parcelable r0 = r4.getParcelable(r1)
            androidx.slice.Slice r1 = new androidx.slice.Slice
            java.lang.String r2 = "obj_2"
            android.os.Bundle r4 = r4.getBundle(r2)
            r1.<init>(r4)
            r3.<init>(r0, r1)
            return r3
        L_0x0092:
            java.lang.CharSequence r3 = r4.getCharSequence(r1)
            return r3
        L_0x0097:
            androidx.slice.Slice r3 = new androidx.slice.Slice
            android.os.Bundle r4 = r4.getBundle(r1)
            r3.<init>(r4)
            return r3
        L_0x00a1:
            android.os.Parcelable r3 = r4.getParcelable(r1)
            return r3
        L_0x00a6:
            android.os.Bundle r3 = r4.getBundle(r1)
            androidx.core.graphics.drawable.IconCompat r3 = androidx.core.graphics.drawable.IconCompat.createFromBundle(r3)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItem.readObj(java.lang.String, android.os.Bundle):java.lang.Object");
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String typeToString(java.lang.String r2) {
        /*
            int r0 = r2.hashCode()
            switch(r0) {
                case -1422950858: goto L_0x0044;
                case 104431: goto L_0x003a;
                case 3327612: goto L_0x0030;
                case 3556653: goto L_0x0026;
                case 100313435: goto L_0x001c;
                case 100358090: goto L_0x0012;
                case 109526418: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x004e
        L_0x0008:
            java.lang.String r0 = "slice"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 0
            goto L_0x004f
        L_0x0012:
            java.lang.String r0 = "input"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 6
            goto L_0x004f
        L_0x001c:
            java.lang.String r0 = "image"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 2
            goto L_0x004f
        L_0x0026:
            java.lang.String r0 = "text"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 1
            goto L_0x004f
        L_0x0030:
            java.lang.String r0 = "long"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 5
            goto L_0x004f
        L_0x003a:
            java.lang.String r0 = "int"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 4
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "action"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 3
            goto L_0x004f
        L_0x004e:
            r0 = -1
        L_0x004f:
            switch(r0) {
                case 0: goto L_0x0076;
                case 1: goto L_0x0073;
                case 2: goto L_0x0070;
                case 3: goto L_0x006d;
                case 4: goto L_0x006a;
                case 5: goto L_0x0067;
                case 6: goto L_0x0064;
                default: goto L_0x0052;
            }
        L_0x0052:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unrecognized format: "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r2 = r0.toString()
            return r2
        L_0x0064:
            java.lang.String r2 = "RemoteInput"
            return r2
        L_0x0067:
            java.lang.String r2 = "Long"
            return r2
        L_0x006a:
            java.lang.String r2 = "Int"
            return r2
        L_0x006d:
            java.lang.String r2 = "Action"
            return r2
        L_0x0070:
            java.lang.String r2 = "Image"
            return r2
        L_0x0073:
            java.lang.String r2 = "Text"
            return r2
        L_0x0076:
            java.lang.String r2 = "Slice"
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceItem.typeToString(java.lang.String):java.lang.String");
    }

    public String toString() {
        return toString("");
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(getFormat());
        if (getSubType() != null) {
            sb.append('<');
            sb.append(getSubType());
            sb.append('>');
        }
        sb.append(' ');
        String[] strArr = this.mHints;
        if (strArr.length > 0) {
            Slice.appendHints(sb, strArr);
            sb.append(' ');
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("  ");
        String sb3 = sb2.toString();
        String format = getFormat();
        char c = 65535;
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 1;
                    break;
                }
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 4;
                    break;
                }
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 5;
                    break;
                }
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 2;
                    break;
                }
                break;
            case 100313435:
                if (format.equals("image")) {
                    c = 3;
                    break;
                }
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 0;
                    break;
                }
                break;
        }
        String str2 = "{\n";
        if (c == 0) {
            sb.append(str2);
            sb.append(getSlice().toString(sb3));
            sb.append(10);
            sb.append(str);
            sb.append('}');
        } else if (c == 1) {
            F f = ((Pair) this.mObj).first;
            sb.append('[');
            sb.append(f);
            sb.append("] ");
            sb.append(str2);
            sb.append(getSlice().toString(sb3));
            sb.append(10);
            sb.append(str);
            sb.append('}');
        } else if (c == 2) {
            sb.append('\"');
            sb.append(getText());
            sb.append('\"');
        } else if (c == 3) {
            sb.append(getIcon());
        } else if (c == 4) {
            if ("color".equals(getSubType())) {
                int i = getInt();
                sb.append(String.format("a=0x%02x r=0x%02x g=0x%02x b=0x%02x", new Object[]{Integer.valueOf(Color.alpha(i)), Integer.valueOf(Color.red(i)), Integer.valueOf(Color.green(i)), Integer.valueOf(Color.blue(i))}));
            } else {
                if ("layout_direction".equals(getSubType())) {
                    sb.append(layoutDirectionToString(getInt()));
                } else {
                    sb.append(getInt());
                }
            }
        } else if (c != 5) {
            sb.append(typeToString(getFormat()));
        } else {
            if (!"millis".equals(getSubType())) {
                sb.append(getLong());
                sb.append('L');
            } else if (getLong() == -1) {
                sb.append("INFINITY");
            } else {
                sb.append(DateUtils.getRelativeTimeSpanString(getLong(), Calendar.getInstance().getTimeInMillis(), 1000, 262144));
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public void onPreParceling(boolean z) {
        this.mHolder = new SliceItemHolder(this.mFormat, this.mObj, z);
    }

    public void onPostParceling() {
        SliceItemHolder sliceItemHolder = this.mHolder;
        if (sliceItemHolder != null) {
            this.mObj = sliceItemHolder.getObj(this.mFormat);
            this.mHolder.release();
        } else {
            this.mObj = null;
        }
        this.mHolder = null;
    }

    private static String layoutDirectionToString(int i) {
        if (i == 0) {
            return "LTR";
        }
        if (i == 1) {
            return "RTL";
        }
        if (i != 2) {
            return i != 3 ? Integer.toString(i) : "LOCALE";
        }
        return "INHERIT";
    }

    private static CharSequence sanitizeText(CharSequence charSequence) {
        if (charSequence instanceof Spannable) {
            fixSpannableText((Spannable) charSequence);
            return charSequence;
        } else if (!(charSequence instanceof Spanned) || checkSpannedText((Spanned) charSequence)) {
            return charSequence;
        } else {
            SpannableString spannableString = new SpannableString(charSequence);
            fixSpannableText(spannableString);
            return spannableString;
        }
    }

    private static boolean checkSpannedText(Spanned spanned) {
        for (Object checkSpan : spanned.getSpans(0, spanned.length(), Object.class)) {
            if (!checkSpan(checkSpan)) {
                return false;
            }
        }
        return true;
    }

    private static void fixSpannableText(Spannable spannable) {
        Object[] spans;
        for (Object obj : spannable.getSpans(0, spannable.length(), Object.class)) {
            Object fixSpan = fixSpan(obj);
            if (fixSpan != obj) {
                if (fixSpan != null) {
                    spannable.setSpan(fixSpan, spannable.getSpanStart(obj), spannable.getSpanEnd(obj), spannable.getSpanFlags(obj));
                }
                spannable.removeSpan(obj);
            }
        }
    }

    private static boolean checkSpan(Object obj) {
        return (obj instanceof AlignmentSpan) || (obj instanceof ForegroundColorSpan) || (obj instanceof RelativeSizeSpan) || (obj instanceof StyleSpan);
    }

    private static Object fixSpan(Object obj) {
        if (checkSpan(obj)) {
            return obj;
        }
        return null;
    }
}
