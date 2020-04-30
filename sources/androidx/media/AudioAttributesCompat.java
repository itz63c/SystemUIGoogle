package androidx.media;

import android.media.AudioAttributes;
import android.os.Build.VERSION;
import android.util.SparseIntArray;
import androidx.versionedparcelable.VersionedParcelable;

public class AudioAttributesCompat implements VersionedParcelable {
    private static final SparseIntArray SUPPRESSIBLE_USAGES;
    static boolean sForceLegacyBehavior;
    public AudioAttributesImpl mImpl;

    public static class Builder {
        final androidx.media.AudioAttributesImpl.Builder mBuilderImpl;

        public Builder() {
            if (AudioAttributesCompat.sForceLegacyBehavior) {
                this.mBuilderImpl = new Builder();
                return;
            }
            int i = VERSION.SDK_INT;
            if (i >= 26) {
                this.mBuilderImpl = new Builder();
            } else if (i >= 21) {
                this.mBuilderImpl = new Builder();
            } else {
                this.mBuilderImpl = new Builder();
            }
        }

        public AudioAttributesCompat build() {
            return new AudioAttributesCompat(this.mBuilderImpl.build());
        }

        public Builder setLegacyStreamType(int i) {
            this.mBuilderImpl.setLegacyStreamType(i);
            return this;
        }
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        SUPPRESSIBLE_USAGES = sparseIntArray;
        sparseIntArray.put(5, 1);
        SUPPRESSIBLE_USAGES.put(6, 2);
        SUPPRESSIBLE_USAGES.put(7, 2);
        SUPPRESSIBLE_USAGES.put(8, 1);
        SUPPRESSIBLE_USAGES.put(9, 1);
        SUPPRESSIBLE_USAGES.put(10, 1);
    }

    public AudioAttributesCompat() {
    }

    AudioAttributesCompat(AudioAttributesImpl audioAttributesImpl) {
        this.mImpl = audioAttributesImpl;
    }

    public static AudioAttributesCompat wrap(Object obj) {
        if (sForceLegacyBehavior) {
            return null;
        }
        int i = VERSION.SDK_INT;
        if (i >= 26) {
            return new AudioAttributesCompat(new AudioAttributesImplApi26((AudioAttributes) obj));
        }
        if (i >= 21) {
            return new AudioAttributesCompat(new AudioAttributesImplApi21((AudioAttributes) obj));
        }
        return null;
    }

    public int hashCode() {
        return this.mImpl.hashCode();
    }

    public String toString() {
        return this.mImpl.toString();
    }

    static String usageToString(int i) {
        switch (i) {
            case 0:
                return "USAGE_UNKNOWN";
            case 1:
                return "USAGE_MEDIA";
            case 2:
                return "USAGE_VOICE_COMMUNICATION";
            case 3:
                return "USAGE_VOICE_COMMUNICATION_SIGNALLING";
            case 4:
                return "USAGE_ALARM";
            case 5:
                return "USAGE_NOTIFICATION";
            case 6:
                return "USAGE_NOTIFICATION_RINGTONE";
            case 7:
                return "USAGE_NOTIFICATION_COMMUNICATION_REQUEST";
            case 8:
                return "USAGE_NOTIFICATION_COMMUNICATION_INSTANT";
            case 9:
                return "USAGE_NOTIFICATION_COMMUNICATION_DELAYED";
            case 10:
                return "USAGE_NOTIFICATION_EVENT";
            case 11:
                return "USAGE_ASSISTANCE_ACCESSIBILITY";
            case 12:
                return "USAGE_ASSISTANCE_NAVIGATION_GUIDANCE";
            case 13:
                return "USAGE_ASSISTANCE_SONIFICATION";
            case 14:
                return "USAGE_GAME";
            case 16:
                return "USAGE_ASSISTANT";
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("unknown usage ");
                sb.append(i);
                return sb.toString();
        }
    }

    static int toVolumeStreamType(boolean z, int i, int i2) {
        int i3 = 1;
        if ((i & 1) == 1) {
            if (!z) {
                i3 = 7;
            }
            return i3;
        }
        int i4 = 0;
        if ((i & 4) == 4) {
            if (!z) {
                i4 = 6;
            }
            return i4;
        }
        switch (i2) {
            case 0:
            case 1:
            case 12:
            case 14:
            case 16:
                return 3;
            case 2:
                break;
            case 3:
                if (!z) {
                    i4 = 8;
                    break;
                }
                break;
            case 4:
                return 4;
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                return 5;
            case 6:
                return 2;
            case 11:
                return 10;
            case 13:
                return 1;
            default:
                if (!z) {
                    return 3;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown usage value ");
                sb.append(i2);
                sb.append(" in audio attributes");
                throw new IllegalArgumentException(sb.toString());
        }
        return i4;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof AudioAttributesCompat)) {
            return false;
        }
        AudioAttributesCompat audioAttributesCompat = (AudioAttributesCompat) obj;
        AudioAttributesImpl audioAttributesImpl = this.mImpl;
        if (audioAttributesImpl != null) {
            return audioAttributesImpl.equals(audioAttributesCompat.mImpl);
        }
        if (audioAttributesCompat.mImpl == null) {
            z = true;
        }
        return z;
    }
}
