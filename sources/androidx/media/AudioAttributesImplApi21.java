package androidx.media;

import android.media.AudioAttributes;

public class AudioAttributesImplApi21 implements AudioAttributesImpl {
    public AudioAttributes mAudioAttributes;
    public int mLegacyStreamType;

    static class Builder implements androidx.media.AudioAttributesImpl.Builder {
        final android.media.AudioAttributes.Builder mFwkBuilder = new android.media.AudioAttributes.Builder();

        Builder() {
        }

        public AudioAttributesImpl build() {
            return new AudioAttributesImplApi21(this.mFwkBuilder.build());
        }

        public Builder setLegacyStreamType(int i) {
            this.mFwkBuilder.setLegacyStreamType(i);
            return this;
        }
    }

    public AudioAttributesImplApi21() {
        this.mLegacyStreamType = -1;
    }

    AudioAttributesImplApi21(AudioAttributes audioAttributes) {
        this(audioAttributes, -1);
    }

    AudioAttributesImplApi21(AudioAttributes audioAttributes, int i) {
        this.mLegacyStreamType = -1;
        this.mAudioAttributes = audioAttributes;
        this.mLegacyStreamType = i;
    }

    public int hashCode() {
        return this.mAudioAttributes.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AudioAttributesImplApi21)) {
            return false;
        }
        return this.mAudioAttributes.equals(((AudioAttributesImplApi21) obj).mAudioAttributes);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AudioAttributesCompat: audioattributes=");
        sb.append(this.mAudioAttributes);
        return sb.toString();
    }
}
