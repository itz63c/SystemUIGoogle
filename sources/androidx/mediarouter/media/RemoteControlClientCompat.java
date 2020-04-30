package androidx.mediarouter.media;

abstract class RemoteControlClientCompat {

    public static final class PlaybackInfo {
        public int playbackStream;
        public int playbackType;
        public int volume;
        public int volumeHandling = 0;
        public int volumeMax;
    }

    public abstract void setPlaybackInfo(PlaybackInfo playbackInfo);
}
