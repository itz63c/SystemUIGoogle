package androidx.slice.builders;

import android.content.Context;
import android.net.Uri;
import androidx.slice.Clock;
import androidx.slice.Slice.Builder;
import androidx.slice.SliceManager;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import java.util.ArrayList;
import java.util.List;

public abstract class TemplateSliceBuilder {
    private final Builder mBuilder;
    private final Context mContext;
    private final TemplateBuilderImpl mImpl;
    private List<SliceSpec> mSpecs;

    /* access modifiers changed from: protected */
    public abstract TemplateBuilderImpl selectImpl();

    /* access modifiers changed from: 0000 */
    public abstract void setImpl(TemplateBuilderImpl templateBuilderImpl);

    public TemplateSliceBuilder(Context context, Uri uri) {
        this.mBuilder = new Builder(uri);
        this.mContext = context;
        this.mSpecs = getSpecs(uri);
        TemplateBuilderImpl selectImpl = selectImpl();
        this.mImpl = selectImpl;
        if (selectImpl != null) {
            setImpl(selectImpl);
            return;
        }
        throw new IllegalArgumentException("No valid specs found");
    }

    /* access modifiers changed from: protected */
    public Builder getBuilder() {
        return this.mBuilder;
    }

    /* access modifiers changed from: protected */
    public boolean checkCompatible(SliceSpec sliceSpec) {
        int size = this.mSpecs.size();
        for (int i = 0; i < size; i++) {
            if (((SliceSpec) this.mSpecs.get(i)).canRender(sliceSpec)) {
                return true;
            }
        }
        return false;
    }

    private List<SliceSpec> getSpecs(Uri uri) {
        if (SliceProvider.getCurrentSpecs() != null) {
            return new ArrayList(SliceProvider.getCurrentSpecs());
        }
        return new ArrayList(SliceManager.getInstance(this.mContext).getPinnedSpecs(uri));
    }

    /* access modifiers changed from: protected */
    public Clock getClock() {
        if (SliceProvider.getClock() != null) {
            return SliceProvider.getClock();
        }
        return new SystemClock();
    }
}
