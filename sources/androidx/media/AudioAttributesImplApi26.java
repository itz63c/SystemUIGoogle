package androidx.media;

import android.media.AudioAttributes;

public class AudioAttributesImplApi26 extends AudioAttributesImplApi21 {

    static class Builder extends Builder {
        Builder() {
        }

        public AudioAttributesImpl build() {
            return new AudioAttributesImplApi26(this.mFwkBuilder.build());
        }
    }

    public AudioAttributesImplApi26() {
    }

    AudioAttributesImplApi26(AudioAttributes audioAttributes) {
        super(audioAttributes, -1);
    }
}
