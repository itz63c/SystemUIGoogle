package com.android.keyguard.clock;

import android.content.ContentProvider;
import android.content.ContentProvider.PipeDataWriter;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.List;
import java.util.function.Supplier;

public final class ClockOptionsProvider extends ContentProvider {
    private final Supplier<List<ClockInfo>> mClocksSupplier;

    private static class MyWriter implements PipeDataWriter<Bitmap> {
        private MyWriter() {
        }

        public void writeDataToPipe(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, Bitmap bitmap) {
            AutoCloseOutputStream autoCloseOutputStream;
            try {
                autoCloseOutputStream = new AutoCloseOutputStream(parcelFileDescriptor);
                bitmap.compress(CompressFormat.PNG, 100, autoCloseOutputStream);
                autoCloseOutputStream.close();
                return;
            } catch (Exception e) {
                Log.w("ClockOptionsProvider", "fail to write to pipe", e);
                return;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        }
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public ClockOptionsProvider() {
        this($$Lambda$ClockOptionsProvider$VCFr6VBqrtOSuPKYuOzo6kUuyg.INSTANCE);
    }

    @VisibleForTesting
    ClockOptionsProvider(Supplier<List<ClockInfo>> supplier) {
        this.mClocksSupplier = supplier;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0021, code lost:
        if ("thumbnail".equals(r2.get(0)) != false) goto L_0x0023;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getType(android.net.Uri r3) {
        /*
            r2 = this;
            java.util.List r2 = r3.getPathSegments()
            int r3 = r2.size()
            if (r3 <= 0) goto L_0x0026
            r3 = 0
            java.lang.Object r0 = r2.get(r3)
            java.lang.String r1 = "preview"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0023
            java.lang.Object r2 = r2.get(r3)
            java.lang.String r3 = "thumbnail"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0026
        L_0x0023:
            java.lang.String r2 = "image/png"
            return r2
        L_0x0026:
            java.lang.String r2 = "vnd.android.cursor.dir/clock_faces"
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.clock.ClockOptionsProvider.getType(android.net.Uri):java.lang.String");
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        if (!"/list_options".equals(uri.getPath())) {
            return null;
        }
        String str3 = "name";
        String str4 = "title";
        String str5 = "id";
        String str6 = "thumbnail";
        String str7 = "preview";
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{str3, str4, str5, str6, str7});
        List list = (List) this.mClocksSupplier.get();
        for (int i = 0; i < list.size(); i++) {
            ClockInfo clockInfo = (ClockInfo) list.get(i);
            matrixCursor.newRow().add(str3, clockInfo.getName()).add(str4, clockInfo.getTitle()).add(str5, clockInfo.getId()).add(str6, createThumbnailUri(clockInfo)).add(str7, createPreviewUri(clockInfo));
        }
        return matrixCursor;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0022, code lost:
        if ("thumbnail".equals(r15.get(0)) != false) goto L_0x0024;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.ParcelFileDescriptor openFile(android.net.Uri r14, java.lang.String r15) throws java.io.FileNotFoundException {
        /*
            r13 = this;
            java.util.List r15 = r14.getPathSegments()
            int r0 = r15.size()
            r1 = 2
            if (r0 != r1) goto L_0x0091
            r0 = 0
            java.lang.Object r1 = r15.get(r0)
            java.lang.String r2 = "preview"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0024
            java.lang.Object r1 = r15.get(r0)
            java.lang.String r3 = "thumbnail"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0091
        L_0x0024:
            r1 = 1
            java.lang.Object r1 = r15.get(r1)
            java.lang.String r1 = (java.lang.String) r1
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 != 0) goto L_0x0089
            java.util.function.Supplier<java.util.List<com.android.keyguard.clock.ClockInfo>> r3 = r13.mClocksSupplier
            java.lang.Object r3 = r3.get()
            java.util.List r3 = (java.util.List) r3
            r4 = r0
        L_0x003a:
            int r5 = r3.size()
            r6 = 0
            if (r4 >= r5) goto L_0x005b
            java.lang.Object r5 = r3.get(r4)
            com.android.keyguard.clock.ClockInfo r5 = (com.android.keyguard.clock.ClockInfo) r5
            java.lang.String r5 = r5.getId()
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x0058
            java.lang.Object r1 = r3.get(r4)
            com.android.keyguard.clock.ClockInfo r1 = (com.android.keyguard.clock.ClockInfo) r1
            goto L_0x005c
        L_0x0058:
            int r4 = r4 + 1
            goto L_0x003a
        L_0x005b:
            r1 = r6
        L_0x005c:
            if (r1 == 0) goto L_0x0081
            r10 = 0
            java.lang.Object r15 = r15.get(r0)
            boolean r15 = r2.equals(r15)
            if (r15 == 0) goto L_0x006e
            android.graphics.Bitmap r15 = r1.getPreview()
            goto L_0x0072
        L_0x006e:
            android.graphics.Bitmap r15 = r1.getThumbnail()
        L_0x0072:
            r11 = r15
            com.android.keyguard.clock.ClockOptionsProvider$MyWriter r12 = new com.android.keyguard.clock.ClockOptionsProvider$MyWriter
            r12.<init>()
            java.lang.String r9 = "image/png"
            r7 = r13
            r8 = r14
            android.os.ParcelFileDescriptor r13 = r7.openPipeHelper(r8, r9, r10, r11, r12)
            return r13
        L_0x0081:
            java.io.FileNotFoundException r13 = new java.io.FileNotFoundException
            java.lang.String r14 = "Invalid preview url, id not found"
            r13.<init>(r14)
            throw r13
        L_0x0089:
            java.io.FileNotFoundException r13 = new java.io.FileNotFoundException
            java.lang.String r14 = "Invalid preview url, missing id"
            r13.<init>(r14)
            throw r13
        L_0x0091:
            java.io.FileNotFoundException r13 = new java.io.FileNotFoundException
            java.lang.String r14 = "Invalid preview url"
            r13.<init>(r14)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.clock.ClockOptionsProvider.openFile(android.net.Uri, java.lang.String):android.os.ParcelFileDescriptor");
    }

    private Uri createThumbnailUri(ClockInfo clockInfo) {
        return new Builder().scheme("content").authority("com.android.keyguard.clock").appendPath("thumbnail").appendPath(clockInfo.getId()).build();
    }

    private Uri createPreviewUri(ClockInfo clockInfo) {
        return new Builder().scheme("content").authority("com.android.keyguard.clock").appendPath("preview").appendPath(clockInfo.getId()).build();
    }
}
