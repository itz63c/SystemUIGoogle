package com.google.android.systemui.keyguard;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.google.android.systemui.dagger.SystemUIGoogleRootComponent;
import com.google.android.systemui.smartspace.SmartSpaceCard;
import com.google.android.systemui.smartspace.SmartSpaceController;
import com.google.android.systemui.smartspace.SmartSpaceData;
import com.google.android.systemui.smartspace.SmartSpaceUpdateListener;
import java.lang.ref.WeakReference;

public class KeyguardSliceProviderGoogle extends KeyguardSliceProvider implements SmartSpaceUpdateListener {
    private static final boolean DEBUG = Log.isLoggable("KeyguardSliceProvider", 3);
    private final Uri mCalendarUri = Uri.parse("content://com.android.systemui.keyguard/smartSpace/calendar");
    private boolean mHideSensitiveContent;
    private boolean mHideWorkContent = true;
    public SmartSpaceController mSmartSpaceController;
    private SmartSpaceData mSmartSpaceData;
    private final Uri mWeatherUri = Uri.parse("content://com.android.systemui.keyguard/smartSpace/weather");

    private static class AddShadowTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private final float mBlurRadius;
        private final WeakReference<KeyguardSliceProviderGoogle> mProviderReference;
        private final SmartSpaceCard mWeatherCard;

        AddShadowTask(KeyguardSliceProviderGoogle keyguardSliceProviderGoogle, SmartSpaceCard smartSpaceCard) {
            this.mProviderReference = new WeakReference<>(keyguardSliceProviderGoogle);
            this.mWeatherCard = smartSpaceCard;
            this.mBlurRadius = keyguardSliceProviderGoogle.getContext().getResources().getDimension(C2009R$dimen.smartspace_icon_shadow);
        }

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            return applyShadow(bitmapArr[0]);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bitmap bitmap) {
            KeyguardSliceProviderGoogle keyguardSliceProviderGoogle;
            synchronized (this) {
                this.mWeatherCard.setIcon(bitmap);
                keyguardSliceProviderGoogle = (KeyguardSliceProviderGoogle) this.mProviderReference.get();
            }
            if (keyguardSliceProviderGoogle != null) {
                keyguardSliceProviderGoogle.notifyChange();
            }
        }

        private Bitmap applyShadow(Bitmap bitmap) {
            BlurMaskFilter blurMaskFilter = new BlurMaskFilter(this.mBlurRadius, Blur.NORMAL);
            Paint paint = new Paint();
            paint.setMaskFilter(blurMaskFilter);
            int[] iArr = new int[2];
            Bitmap extractAlpha = bitmap.extractAlpha(paint, iArr);
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint2 = new Paint();
            paint2.setAlpha(70);
            canvas.drawBitmap(extractAlpha, (float) iArr[0], ((float) iArr[1]) + (this.mBlurRadius / 2.0f), paint2);
            extractAlpha.recycle();
            paint2.setAlpha(255);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint2);
            return createBitmap;
        }
    }

    public boolean onCreateSliceProvider() {
        boolean onCreateSliceProvider = super.onCreateSliceProvider();
        ((SystemUIGoogleRootComponent) SystemUIFactory.getInstance().getRootComponent()).inject(this);
        this.mSmartSpaceData = new SmartSpaceData();
        this.mSmartSpaceController.addListener(this);
        return onCreateSliceProvider;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mSmartSpaceController.removeListener(this);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00e3, code lost:
        r6 = r7.build();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00e9, code lost:
        if (DEBUG == false) goto L_0x0101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00eb, code lost:
        r7 = new java.lang.StringBuilder();
        r7.append("Binding slice: ");
        r7.append(r6);
        android.util.Log.d("KeyguardSliceProvider", r7.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0101, code lost:
        android.os.Trace.endSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0104, code lost:
        return r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.slice.Slice onBindSlice(android.net.Uri r7) {
        /*
            r6 = this;
            java.lang.String r7 = "KeyguardSliceProviderGoogle#onBindSlice"
            android.os.Trace.beginSection(r7)
            androidx.slice.builders.ListBuilder r7 = new androidx.slice.builders.ListBuilder
            android.content.Context r0 = r6.getContext()
            android.net.Uri r1 = r6.mSliceUri
            r2 = -1
            r7.<init>(r0, r1, r2)
            monitor-enter(r6)
            com.google.android.systemui.smartspace.SmartSpaceData r0 = r6.mSmartSpaceData     // Catch:{ all -> 0x0105 }
            com.google.android.systemui.smartspace.SmartSpaceCard r0 = r0.getCurrentCard()     // Catch:{ all -> 0x0105 }
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0056
            boolean r3 = r0.isExpired()     // Catch:{ all -> 0x0105 }
            if (r3 != 0) goto L_0x0056
            java.lang.String r3 = r0.getTitle()     // Catch:{ all -> 0x0105 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0105 }
            if (r3 != 0) goto L_0x0056
            boolean r3 = r0.isSensitive()     // Catch:{ all -> 0x0105 }
            if (r3 == 0) goto L_0x003f
            boolean r4 = r6.mHideSensitiveContent     // Catch:{ all -> 0x0105 }
            if (r4 != 0) goto L_0x003f
            boolean r4 = r0.isWorkProfile()     // Catch:{ all -> 0x0105 }
            if (r4 != 0) goto L_0x003f
            r4 = r2
            goto L_0x0040
        L_0x003f:
            r4 = r1
        L_0x0040:
            if (r3 == 0) goto L_0x004e
            boolean r5 = r6.mHideWorkContent     // Catch:{ all -> 0x0105 }
            if (r5 != 0) goto L_0x004e
            boolean r5 = r0.isWorkProfile()     // Catch:{ all -> 0x0105 }
            if (r5 == 0) goto L_0x004e
            r5 = r2
            goto L_0x004f
        L_0x004e:
            r5 = r1
        L_0x004f:
            if (r3 == 0) goto L_0x0055
            if (r4 != 0) goto L_0x0055
            if (r5 == 0) goto L_0x0056
        L_0x0055:
            r1 = r2
        L_0x0056:
            if (r1 == 0) goto L_0x00bb
            android.graphics.Bitmap r1 = r0.getIcon()     // Catch:{ all -> 0x0105 }
            r3 = 0
            if (r1 != 0) goto L_0x0061
            r1 = r3
            goto L_0x0065
        L_0x0061:
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithBitmap(r1)     // Catch:{ all -> 0x0105 }
        L_0x0065:
            android.app.PendingIntent r4 = r0.getPendingIntent()     // Catch:{ all -> 0x0105 }
            if (r1 == 0) goto L_0x0076
            if (r4 != 0) goto L_0x006e
            goto L_0x0076
        L_0x006e:
            java.lang.String r3 = r0.getTitle()     // Catch:{ all -> 0x0105 }
            androidx.slice.builders.SliceAction r3 = androidx.slice.builders.SliceAction.create(r4, r1, r2, r3)     // Catch:{ all -> 0x0105 }
        L_0x0076:
            androidx.slice.builders.ListBuilder$HeaderBuilder r4 = new androidx.slice.builders.ListBuilder$HeaderBuilder     // Catch:{ all -> 0x0105 }
            android.net.Uri r5 = r6.mHeaderUri     // Catch:{ all -> 0x0105 }
            r4.<init>(r5)     // Catch:{ all -> 0x0105 }
            java.lang.CharSequence r5 = r0.getFormattedTitle()     // Catch:{ all -> 0x0105 }
            r4.setTitle(r5)     // Catch:{ all -> 0x0105 }
            if (r3 == 0) goto L_0x0089
            r4.setPrimaryAction(r3)     // Catch:{ all -> 0x0105 }
        L_0x0089:
            r7.setHeader(r4)     // Catch:{ all -> 0x0105 }
            java.lang.String r0 = r0.getSubtitle()     // Catch:{ all -> 0x0105 }
            if (r0 == 0) goto L_0x00a9
            androidx.slice.builders.ListBuilder$RowBuilder r4 = new androidx.slice.builders.ListBuilder$RowBuilder     // Catch:{ all -> 0x0105 }
            android.net.Uri r5 = r6.mCalendarUri     // Catch:{ all -> 0x0105 }
            r4.<init>(r5)     // Catch:{ all -> 0x0105 }
            r4.setTitle(r0)     // Catch:{ all -> 0x0105 }
            if (r1 == 0) goto L_0x00a1
            r4.addEndItem(r1, r2)     // Catch:{ all -> 0x0105 }
        L_0x00a1:
            if (r3 == 0) goto L_0x00a6
            r4.setPrimaryAction(r3)     // Catch:{ all -> 0x0105 }
        L_0x00a6:
            r7.addRow(r4)     // Catch:{ all -> 0x0105 }
        L_0x00a9:
            r6.addWeather(r7)     // Catch:{ all -> 0x0105 }
            r6.addZenModeLocked(r7)     // Catch:{ all -> 0x0105 }
            r6.addPrimaryActionLocked(r7)     // Catch:{ all -> 0x0105 }
            android.os.Trace.endSection()     // Catch:{ all -> 0x0105 }
            androidx.slice.Slice r7 = r7.build()     // Catch:{ all -> 0x0105 }
            monitor-exit(r6)     // Catch:{ all -> 0x0105 }
            return r7
        L_0x00bb:
            boolean r0 = r6.needsMediaLocked()     // Catch:{ all -> 0x0105 }
            if (r0 == 0) goto L_0x00c5
            r6.addMediaLocked(r7)     // Catch:{ all -> 0x0105 }
            goto L_0x00d6
        L_0x00c5:
            androidx.slice.builders.ListBuilder$RowBuilder r0 = new androidx.slice.builders.ListBuilder$RowBuilder     // Catch:{ all -> 0x0105 }
            android.net.Uri r1 = r6.mDateUri     // Catch:{ all -> 0x0105 }
            r0.<init>(r1)     // Catch:{ all -> 0x0105 }
            java.lang.String r1 = r6.getFormattedDateLocked()     // Catch:{ all -> 0x0105 }
            r0.setTitle(r1)     // Catch:{ all -> 0x0105 }
            r7.addRow(r0)     // Catch:{ all -> 0x0105 }
        L_0x00d6:
            r6.addWeather(r7)     // Catch:{ all -> 0x0105 }
            r6.addNextAlarmLocked(r7)     // Catch:{ all -> 0x0105 }
            r6.addZenModeLocked(r7)     // Catch:{ all -> 0x0105 }
            r6.addPrimaryActionLocked(r7)     // Catch:{ all -> 0x0105 }
            monitor-exit(r6)     // Catch:{ all -> 0x0105 }
            androidx.slice.Slice r6 = r7.build()
            boolean r7 = DEBUG
            if (r7 == 0) goto L_0x0101
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r0 = "Binding slice: "
            r7.append(r0)
            r7.append(r6)
            java.lang.String r7 = r7.toString()
            java.lang.String r0 = "KeyguardSliceProvider"
            android.util.Log.d(r0, r7)
        L_0x0101:
            android.os.Trace.endSection()
            return r6
        L_0x0105:
            r7 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0105 }
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.keyguard.KeyguardSliceProviderGoogle.onBindSlice(android.net.Uri):androidx.slice.Slice");
    }

    private void addWeather(ListBuilder listBuilder) {
        SmartSpaceCard weatherCard = this.mSmartSpaceData.getWeatherCard();
        if (weatherCard != null && !weatherCard.isExpired()) {
            RowBuilder rowBuilder = new RowBuilder(this.mWeatherUri);
            rowBuilder.setTitle(weatherCard.getTitle());
            Bitmap icon = weatherCard.getIcon();
            if (icon != null) {
                IconCompat createWithBitmap = IconCompat.createWithBitmap(icon);
                createWithBitmap.setTintMode(Mode.DST);
                rowBuilder.addEndItem(createWithBitmap, 1);
            }
            listBuilder.addRow(rowBuilder);
        }
    }

    public void onSensitiveModeChanged(boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        synchronized (this) {
            z3 = true;
            if (this.mHideSensitiveContent != z) {
                this.mHideSensitiveContent = z;
                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Public mode changed, hide data: ");
                    sb.append(z);
                    Log.d("KeyguardSliceProvider", sb.toString());
                }
                z4 = true;
            } else {
                z4 = false;
            }
            if (this.mHideWorkContent != z2) {
                this.mHideWorkContent = z2;
                if (DEBUG) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Public work mode changed, hide data: ");
                    sb2.append(z2);
                    Log.d("KeyguardSliceProvider", sb2.toString());
                }
            } else {
                z3 = z4;
            }
        }
        if (z3) {
            notifyChange();
        }
    }

    public void onSmartSpaceUpdated(SmartSpaceData smartSpaceData) {
        synchronized (this) {
            this.mSmartSpaceData = smartSpaceData;
        }
        SmartSpaceCard weatherCard = smartSpaceData.getWeatherCard();
        if (weatherCard == null || weatherCard.getIcon() == null || weatherCard.isIconProcessed()) {
            notifyChange();
            return;
        }
        weatherCard.setIconProcessed(true);
        new AddShadowTask(this, weatherCard).execute(new Bitmap[]{weatherCard.getIcon()});
    }

    /* access modifiers changed from: protected */
    public void updateClockLocked() {
        notifyChange();
    }
}
