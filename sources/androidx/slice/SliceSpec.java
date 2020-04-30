package androidx.slice;

import androidx.versionedparcelable.VersionedParcelable;

public final class SliceSpec implements VersionedParcelable {
    int mRevision = 1;
    String mType;

    public SliceSpec() {
    }

    public SliceSpec(String str, int i) {
        this.mType = str;
        this.mRevision = i;
    }

    public String getType() {
        return this.mType;
    }

    public int getRevision() {
        return this.mRevision;
    }

    public boolean canRender(SliceSpec sliceSpec) {
        boolean z = false;
        if (!this.mType.equals(sliceSpec.mType)) {
            return false;
        }
        if (this.mRevision >= sliceSpec.mRevision) {
            z = true;
        }
        return z;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof SliceSpec)) {
            return false;
        }
        SliceSpec sliceSpec = (SliceSpec) obj;
        if (this.mType.equals(sliceSpec.mType) && this.mRevision == sliceSpec.mRevision) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.mType.hashCode() + this.mRevision;
    }

    public String toString() {
        return String.format("SliceSpec{%s,%d}", new Object[]{this.mType, Integer.valueOf(this.mRevision)});
    }
}
