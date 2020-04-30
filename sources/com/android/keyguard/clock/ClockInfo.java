package com.android.keyguard.clock;

import android.graphics.Bitmap;
import java.util.function.Supplier;

final class ClockInfo {
    private final String mId;
    private final String mName;
    private final Supplier<Bitmap> mPreview;
    private final Supplier<Bitmap> mThumbnail;
    private final Supplier<String> mTitle;

    static class Builder {
        private String mId;
        private String mName;
        private Supplier<Bitmap> mPreview;
        private Supplier<Bitmap> mThumbnail;
        private Supplier<String> mTitle;

        Builder() {
        }

        public ClockInfo build() {
            ClockInfo clockInfo = new ClockInfo(this.mName, this.mTitle, this.mId, this.mThumbnail, this.mPreview);
            return clockInfo;
        }

        public Builder setName(String str) {
            this.mName = str;
            return this;
        }

        public Builder setTitle(Supplier<String> supplier) {
            this.mTitle = supplier;
            return this;
        }

        public Builder setId(String str) {
            this.mId = str;
            return this;
        }

        public Builder setThumbnail(Supplier<Bitmap> supplier) {
            this.mThumbnail = supplier;
            return this;
        }

        public Builder setPreview(Supplier<Bitmap> supplier) {
            this.mPreview = supplier;
            return this;
        }
    }

    private ClockInfo(String str, Supplier<String> supplier, String str2, Supplier<Bitmap> supplier2, Supplier<Bitmap> supplier3) {
        this.mName = str;
        this.mTitle = supplier;
        this.mId = str2;
        this.mThumbnail = supplier2;
        this.mPreview = supplier3;
    }

    /* access modifiers changed from: 0000 */
    public String getName() {
        return this.mName;
    }

    /* access modifiers changed from: 0000 */
    public String getTitle() {
        return (String) this.mTitle.get();
    }

    /* access modifiers changed from: 0000 */
    public String getId() {
        return this.mId;
    }

    /* access modifiers changed from: 0000 */
    public Bitmap getThumbnail() {
        return (Bitmap) this.mThumbnail.get();
    }

    /* access modifiers changed from: 0000 */
    public Bitmap getPreview() {
        return (Bitmap) this.mPreview.get();
    }

    static Builder builder() {
        return new Builder();
    }
}
