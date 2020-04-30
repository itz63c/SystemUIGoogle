package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EntitiesData implements Parcelable {
    public static final Creator<EntitiesData> CREATOR = new Creator<EntitiesData>() {
        public EntitiesData createFromParcel(Parcel parcel) {
            return EntitiesData.read(parcel);
        }

        public EntitiesData[] newArray(int i) {
            return new EntitiesData[i];
        }
    };
    private final Map<String, Bitmap> bitmapMap;
    private final SuggestParcelables$Entities entities;
    private final Map<String, PendingIntent> pendingIntentMap;

    public int describeContents() {
        return 0;
    }

    public static EntitiesData create(SuggestParcelables$Entities suggestParcelables$Entities, Map<String, Bitmap> map, Map<String, PendingIntent> map2) {
        return new EntitiesData(suggestParcelables$Entities, map, map2);
    }

    public static EntitiesData read(Parcel parcel) {
        SuggestParcelables$Entities suggestParcelables$Entities = new SuggestParcelables$Entities(parcel);
        HashMap hashMap = new HashMap();
        SuggestParcelables$ExtrasInfo suggestParcelables$ExtrasInfo = suggestParcelables$Entities.extrasInfo;
        if (suggestParcelables$ExtrasInfo != null && suggestParcelables$ExtrasInfo.containsBitmaps) {
            parcel.readMap(hashMap, Bitmap.class.getClassLoader());
        }
        HashMap hashMap2 = new HashMap();
        SuggestParcelables$ExtrasInfo suggestParcelables$ExtrasInfo2 = suggestParcelables$Entities.extrasInfo;
        if (suggestParcelables$ExtrasInfo2 != null && suggestParcelables$ExtrasInfo2.containsPendingIntents) {
            parcel.readMap(hashMap2, PendingIntent.class.getClassLoader());
        }
        return create(suggestParcelables$Entities, hashMap, hashMap2);
    }

    public void writeToParcel(Parcel parcel, int i) {
        this.entities.writeToParcel(parcel, 0);
        SuggestParcelables$ExtrasInfo suggestParcelables$ExtrasInfo = this.entities.extrasInfo;
        if (suggestParcelables$ExtrasInfo != null) {
            if (suggestParcelables$ExtrasInfo.containsBitmaps) {
                parcel.writeMap(this.bitmapMap);
            }
            if (this.entities.extrasInfo.containsPendingIntents) {
                parcel.writeMap(this.pendingIntentMap);
            }
        }
    }

    @Nullable
    public Bitmap getBitmap(String str) {
        return (Bitmap) this.bitmapMap.get(str);
    }

    @Nullable
    public PendingIntent getPendingIntent(String str) {
        return (PendingIntent) this.pendingIntentMap.get(str);
    }

    public SuggestParcelables$Entities entities() {
        return this.entities;
    }

    private EntitiesData(SuggestParcelables$Entities suggestParcelables$Entities, Map<String, Bitmap> map, Map<String, PendingIntent> map2) {
        this.entities = suggestParcelables$Entities;
        this.bitmapMap = map;
        this.pendingIntentMap = map2;
    }
}
