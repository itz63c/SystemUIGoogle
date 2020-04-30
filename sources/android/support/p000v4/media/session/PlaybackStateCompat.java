package android.support.p000v4.media.session;

import android.annotation.SuppressLint;
import android.media.session.PlaybackState;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"BanParcelableUsage"})
/* renamed from: android.support.v4.media.session.PlaybackStateCompat */
public final class PlaybackStateCompat implements Parcelable {
    public static final Creator<PlaybackStateCompat> CREATOR = new Creator<PlaybackStateCompat>() {
        public PlaybackStateCompat createFromParcel(Parcel parcel) {
            return new PlaybackStateCompat(parcel);
        }

        public PlaybackStateCompat[] newArray(int i) {
            return new PlaybackStateCompat[i];
        }
    };
    final long mActions;
    final long mActiveItemId;
    final long mBufferedPosition;
    List<CustomAction> mCustomActions;
    final int mErrorCode;
    final CharSequence mErrorMessage;
    final Bundle mExtras;
    final long mPosition;
    final float mSpeed;
    final int mState;
    final long mUpdateTime;

    /* renamed from: android.support.v4.media.session.PlaybackStateCompat$CustomAction */
    public static final class CustomAction implements Parcelable {
        public static final Creator<CustomAction> CREATOR = new Creator<CustomAction>() {
            public CustomAction createFromParcel(Parcel parcel) {
                return new CustomAction(parcel);
            }

            public CustomAction[] newArray(int i) {
                return new CustomAction[i];
            }
        };
        private final String mAction;
        private final Bundle mExtras;
        private final int mIcon;
        private final CharSequence mName;

        public int describeContents() {
            return 0;
        }

        CustomAction(String str, CharSequence charSequence, int i, Bundle bundle) {
            this.mAction = str;
            this.mName = charSequence;
            this.mIcon = i;
            this.mExtras = bundle;
        }

        CustomAction(Parcel parcel) {
            this.mAction = parcel.readString();
            this.mName = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.mIcon = parcel.readInt();
            this.mExtras = parcel.readBundle(MediaSessionCompat.class.getClassLoader());
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mAction);
            TextUtils.writeToParcel(this.mName, parcel, i);
            parcel.writeInt(this.mIcon);
            parcel.writeBundle(this.mExtras);
        }

        public static CustomAction fromCustomAction(Object obj) {
            if (obj == null || VERSION.SDK_INT < 21) {
                return null;
            }
            android.media.session.PlaybackState.CustomAction customAction = (android.media.session.PlaybackState.CustomAction) obj;
            Bundle extras = customAction.getExtras();
            MediaSessionCompat.ensureClassLoader(extras);
            return new CustomAction(customAction.getAction(), customAction.getName(), customAction.getIcon(), extras);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Action:mName='");
            sb.append(this.mName);
            sb.append(", mIcon=");
            sb.append(this.mIcon);
            sb.append(", mExtras=");
            sb.append(this.mExtras);
            return sb.toString();
        }
    }

    public int describeContents() {
        return 0;
    }

    PlaybackStateCompat(int i, long j, long j2, float f, long j3, int i2, CharSequence charSequence, long j4, List<CustomAction> list, long j5, Bundle bundle) {
        this.mState = i;
        this.mPosition = j;
        this.mBufferedPosition = j2;
        this.mSpeed = f;
        this.mActions = j3;
        this.mErrorCode = i2;
        this.mErrorMessage = charSequence;
        this.mUpdateTime = j4;
        this.mCustomActions = new ArrayList(list);
        this.mActiveItemId = j5;
        this.mExtras = bundle;
    }

    PlaybackStateCompat(Parcel parcel) {
        this.mState = parcel.readInt();
        this.mPosition = parcel.readLong();
        this.mSpeed = parcel.readFloat();
        this.mUpdateTime = parcel.readLong();
        this.mBufferedPosition = parcel.readLong();
        this.mActions = parcel.readLong();
        this.mErrorMessage = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mCustomActions = parcel.createTypedArrayList(CustomAction.CREATOR);
        this.mActiveItemId = parcel.readLong();
        this.mExtras = parcel.readBundle(MediaSessionCompat.class.getClassLoader());
        this.mErrorCode = parcel.readInt();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PlaybackState {");
        sb.append("state=");
        sb.append(this.mState);
        sb.append(", position=");
        sb.append(this.mPosition);
        sb.append(", buffered position=");
        sb.append(this.mBufferedPosition);
        sb.append(", speed=");
        sb.append(this.mSpeed);
        sb.append(", updated=");
        sb.append(this.mUpdateTime);
        sb.append(", actions=");
        sb.append(this.mActions);
        sb.append(", error code=");
        sb.append(this.mErrorCode);
        sb.append(", error message=");
        sb.append(this.mErrorMessage);
        sb.append(", custom actions=");
        sb.append(this.mCustomActions);
        sb.append(", active item id=");
        sb.append(this.mActiveItemId);
        sb.append("}");
        return sb.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mState);
        parcel.writeLong(this.mPosition);
        parcel.writeFloat(this.mSpeed);
        parcel.writeLong(this.mUpdateTime);
        parcel.writeLong(this.mBufferedPosition);
        parcel.writeLong(this.mActions);
        TextUtils.writeToParcel(this.mErrorMessage, parcel, i);
        parcel.writeTypedList(this.mCustomActions);
        parcel.writeLong(this.mActiveItemId);
        parcel.writeBundle(this.mExtras);
        parcel.writeInt(this.mErrorCode);
    }

    public int getState() {
        return this.mState;
    }

    public long getActions() {
        return this.mActions;
    }

    public static PlaybackStateCompat fromPlaybackState(Object obj) {
        List list;
        int i = VERSION.SDK_INT;
        Bundle bundle = null;
        if (obj == null || i < 21) {
            return null;
        }
        PlaybackState playbackState = (PlaybackState) obj;
        List<Object> customActions = playbackState.getCustomActions();
        if (customActions != null) {
            ArrayList arrayList = new ArrayList(customActions.size());
            for (Object fromCustomAction : customActions) {
                arrayList.add(CustomAction.fromCustomAction(fromCustomAction));
            }
            list = arrayList;
        } else {
            list = null;
        }
        if (i >= 22) {
            bundle = playbackState.getExtras();
            MediaSessionCompat.ensureClassLoader(bundle);
        }
        PlaybackStateCompat playbackStateCompat = new PlaybackStateCompat(playbackState.getState(), playbackState.getPosition(), playbackState.getBufferedPosition(), playbackState.getPlaybackSpeed(), playbackState.getActions(), 0, playbackState.getErrorMessage(), playbackState.getLastPositionUpdateTime(), list, playbackState.getActiveQueueItemId(), bundle);
        return playbackStateCompat;
    }
}
