package android.support.p000v4.media;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.p000v4.media.MediaDescriptionCompat.Builder;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import androidx.collection.ArrayMap;

@SuppressLint({"BanParcelableUsage"})
/* renamed from: android.support.v4.media.MediaMetadataCompat */
public final class MediaMetadataCompat implements Parcelable {
    public static final Creator<MediaMetadataCompat> CREATOR = new Creator<MediaMetadataCompat>() {
        public MediaMetadataCompat createFromParcel(Parcel parcel) {
            return new MediaMetadataCompat(parcel);
        }

        public MediaMetadataCompat[] newArray(int i) {
            return new MediaMetadataCompat[i];
        }
    };
    static final ArrayMap<String, Integer> METADATA_KEYS_TYPE;
    private static final String[] PREFERRED_BITMAP_ORDER;
    private static final String[] PREFERRED_DESCRIPTION_ORDER = {"android.media.metadata.TITLE", "android.media.metadata.ARTIST", "android.media.metadata.ALBUM", "android.media.metadata.ALBUM_ARTIST", "android.media.metadata.WRITER", "android.media.metadata.AUTHOR", "android.media.metadata.COMPOSER"};
    private static final String[] PREFERRED_URI_ORDER;
    final Bundle mBundle;
    private MediaDescriptionCompat mDescription;
    private MediaMetadata mMetadataFwk;

    public int describeContents() {
        return 0;
    }

    static {
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        METADATA_KEYS_TYPE = arrayMap;
        Integer valueOf = Integer.valueOf(1);
        arrayMap.put("android.media.metadata.TITLE", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.ARTIST", valueOf);
        ArrayMap<String, Integer> arrayMap2 = METADATA_KEYS_TYPE;
        Integer valueOf2 = Integer.valueOf(0);
        arrayMap2.put("android.media.metadata.DURATION", valueOf2);
        METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.AUTHOR", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.WRITER", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.COMPOSER", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.COMPILATION", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.DATE", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.YEAR", valueOf2);
        METADATA_KEYS_TYPE.put("android.media.metadata.GENRE", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.TRACK_NUMBER", valueOf2);
        METADATA_KEYS_TYPE.put("android.media.metadata.NUM_TRACKS", valueOf2);
        METADATA_KEYS_TYPE.put("android.media.metadata.DISC_NUMBER", valueOf2);
        METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ARTIST", valueOf);
        ArrayMap<String, Integer> arrayMap3 = METADATA_KEYS_TYPE;
        Integer valueOf3 = Integer.valueOf(2);
        String str = "android.media.metadata.ART";
        arrayMap3.put(str, valueOf3);
        String str2 = "android.media.metadata.ART_URI";
        METADATA_KEYS_TYPE.put(str2, valueOf);
        String str3 = "android.media.metadata.ALBUM_ART";
        METADATA_KEYS_TYPE.put(str3, valueOf3);
        String str4 = "android.media.metadata.ALBUM_ART_URI";
        METADATA_KEYS_TYPE.put(str4, valueOf);
        ArrayMap<String, Integer> arrayMap4 = METADATA_KEYS_TYPE;
        Integer valueOf4 = Integer.valueOf(3);
        arrayMap4.put("android.media.metadata.USER_RATING", valueOf4);
        METADATA_KEYS_TYPE.put("android.media.metadata.RATING", valueOf4);
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_TITLE", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_SUBTITLE", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_DESCRIPTION", valueOf);
        String str5 = "android.media.metadata.DISPLAY_ICON";
        METADATA_KEYS_TYPE.put(str5, valueOf3);
        String str6 = "android.media.metadata.DISPLAY_ICON_URI";
        METADATA_KEYS_TYPE.put(str6, valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.MEDIA_ID", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.BT_FOLDER_TYPE", valueOf2);
        METADATA_KEYS_TYPE.put("android.media.metadata.MEDIA_URI", valueOf);
        METADATA_KEYS_TYPE.put("android.media.metadata.ADVERTISEMENT", valueOf2);
        METADATA_KEYS_TYPE.put("android.media.metadata.DOWNLOAD_STATUS", valueOf2);
        PREFERRED_BITMAP_ORDER = new String[]{str5, str, str3};
        PREFERRED_URI_ORDER = new String[]{str6, str2, str4};
    }

    MediaMetadataCompat(Parcel parcel) {
        this.mBundle = parcel.readBundle(MediaSessionCompat.class.getClassLoader());
    }

    public CharSequence getText(String str) {
        return this.mBundle.getCharSequence(str);
    }

    public String getString(String str) {
        CharSequence charSequence = this.mBundle.getCharSequence(str);
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }

    public long getLong(String str) {
        return this.mBundle.getLong(str, 0);
    }

    public Bitmap getBitmap(String str) {
        try {
            return (Bitmap) this.mBundle.getParcelable(str);
        } catch (Exception e) {
            Log.w("MediaMetadata", "Failed to retrieve a key as Bitmap.", e);
            return null;
        }
    }

    public MediaDescriptionCompat getDescription() {
        Uri uri;
        Bitmap bitmap;
        Uri uri2;
        MediaDescriptionCompat mediaDescriptionCompat = this.mDescription;
        if (mediaDescriptionCompat != null) {
            return mediaDescriptionCompat;
        }
        String string = getString("android.media.metadata.MEDIA_ID");
        CharSequence[] charSequenceArr = new CharSequence[3];
        CharSequence text = getText("android.media.metadata.DISPLAY_TITLE");
        if (TextUtils.isEmpty(text)) {
            int i = 0;
            int i2 = 0;
            while (i < 3) {
                String[] strArr = PREFERRED_DESCRIPTION_ORDER;
                if (i2 >= strArr.length) {
                    break;
                }
                int i3 = i2 + 1;
                CharSequence text2 = getText(strArr[i2]);
                if (!TextUtils.isEmpty(text2)) {
                    int i4 = i + 1;
                    charSequenceArr[i] = text2;
                    i = i4;
                }
                i2 = i3;
            }
        } else {
            charSequenceArr[0] = text;
            charSequenceArr[1] = getText("android.media.metadata.DISPLAY_SUBTITLE");
            charSequenceArr[2] = getText("android.media.metadata.DISPLAY_DESCRIPTION");
        }
        int i5 = 0;
        while (true) {
            String[] strArr2 = PREFERRED_BITMAP_ORDER;
            uri = null;
            if (i5 >= strArr2.length) {
                bitmap = null;
                break;
            }
            bitmap = getBitmap(strArr2[i5]);
            if (bitmap != null) {
                break;
            }
            i5++;
        }
        int i6 = 0;
        while (true) {
            String[] strArr3 = PREFERRED_URI_ORDER;
            if (i6 >= strArr3.length) {
                uri2 = null;
                break;
            }
            String string2 = getString(strArr3[i6]);
            if (!TextUtils.isEmpty(string2)) {
                uri2 = Uri.parse(string2);
                break;
            }
            i6++;
        }
        String string3 = getString("android.media.metadata.MEDIA_URI");
        if (!TextUtils.isEmpty(string3)) {
            uri = Uri.parse(string3);
        }
        Builder builder = new Builder();
        builder.setMediaId(string);
        builder.setTitle(charSequenceArr[0]);
        builder.setSubtitle(charSequenceArr[1]);
        builder.setDescription(charSequenceArr[2]);
        builder.setIconBitmap(bitmap);
        builder.setIconUri(uri2);
        builder.setMediaUri(uri);
        Bundle bundle = new Bundle();
        String str = "android.media.metadata.BT_FOLDER_TYPE";
        if (this.mBundle.containsKey(str)) {
            bundle.putLong("android.media.extra.BT_FOLDER_TYPE", getLong(str));
        }
        String str2 = "android.media.metadata.DOWNLOAD_STATUS";
        if (this.mBundle.containsKey(str2)) {
            bundle.putLong("android.media.extra.DOWNLOAD_STATUS", getLong(str2));
        }
        if (!bundle.isEmpty()) {
            builder.setExtras(bundle);
        }
        MediaDescriptionCompat build = builder.build();
        this.mDescription = build;
        return build;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(this.mBundle);
    }

    public static MediaMetadataCompat fromMediaMetadata(Object obj) {
        if (obj == null || VERSION.SDK_INT < 21) {
            return null;
        }
        Parcel obtain = Parcel.obtain();
        MediaMetadata mediaMetadata = (MediaMetadata) obj;
        mediaMetadata.writeToParcel(obtain, 0);
        obtain.setDataPosition(0);
        MediaMetadataCompat mediaMetadataCompat = (MediaMetadataCompat) CREATOR.createFromParcel(obtain);
        obtain.recycle();
        mediaMetadataCompat.mMetadataFwk = mediaMetadata;
        return mediaMetadataCompat;
    }
}
