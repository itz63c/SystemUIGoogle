package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class ContentParcelables$ContentGroup implements Parcelable {
    public static final Creator<ContentParcelables$ContentGroup> CREATOR = new Creator<ContentParcelables$ContentGroup>() {
        public ContentParcelables$ContentGroup createFromParcel(Parcel parcel) {
            return new ContentParcelables$ContentGroup(parcel);
        }

        public ContentParcelables$ContentGroup[] newArray(int i) {
            return new ContentParcelables$ContentGroup[i];
        }
    };
    public List<SuggestParcelables$ContentRect> contentRects;
    public List<ContentParcelables$Selection> selections;

    public int describeContents() {
        return 0;
    }

    public ContentParcelables$ContentGroup(Parcel parcel) {
        readFromParcel(parcel);
    }

    private ContentParcelables$ContentGroup() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.contentRects == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.contentRects.size());
            for (SuggestParcelables$ContentRect suggestParcelables$ContentRect : this.contentRects) {
                if (suggestParcelables$ContentRect == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$ContentRect.writeToParcel(parcel, i);
                }
            }
        }
        if (this.selections == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeInt(this.selections.size());
        for (ContentParcelables$Selection contentParcelables$Selection : this.selections) {
            if (contentParcelables$Selection == null) {
                parcel.writeByte(0);
            } else {
                parcel.writeByte(1);
                contentParcelables$Selection.writeToParcel(parcel, i);
            }
        }
    }

    private void readFromParcel(Parcel parcel) {
        ContentParcelables$Selection contentParcelables$Selection;
        SuggestParcelables$ContentRect suggestParcelables$ContentRect;
        if (parcel.readByte() == 0) {
            this.contentRects = null;
        } else {
            int readInt = parcel.readInt();
            SuggestParcelables$ContentRect[] suggestParcelables$ContentRectArr = new SuggestParcelables$ContentRect[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$ContentRect = null;
                } else {
                    suggestParcelables$ContentRect = (SuggestParcelables$ContentRect) SuggestParcelables$ContentRect.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$ContentRectArr[i] = suggestParcelables$ContentRect;
            }
            this.contentRects = Arrays.asList(suggestParcelables$ContentRectArr);
        }
        if (parcel.readByte() == 0) {
            this.selections = null;
            return;
        }
        int readInt2 = parcel.readInt();
        ContentParcelables$Selection[] contentParcelables$SelectionArr = new ContentParcelables$Selection[readInt2];
        for (int i2 = 0; i2 < readInt2; i2++) {
            if (parcel.readByte() == 0) {
                contentParcelables$Selection = null;
            } else {
                contentParcelables$Selection = (ContentParcelables$Selection) ContentParcelables$Selection.CREATOR.createFromParcel(parcel);
            }
            contentParcelables$SelectionArr[i2] = contentParcelables$Selection;
        }
        this.selections = Arrays.asList(contentParcelables$SelectionArr);
    }
}
