package androidx.slice.builders.impl;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice.Builder;
import androidx.slice.SliceItem;
import androidx.slice.SliceSpec;
import androidx.slice.builders.ListBuilder.HeaderBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;
import java.util.Set;

public class ListBuilderBasicImpl extends TemplateBuilderImpl implements ListBuilder {
    private IconCompat mIconCompat;
    boolean mIsError;
    private Set<String> mKeywords;
    private SliceAction mSliceAction;
    private CharSequence mSubtitle;
    private CharSequence mTitle;

    public ListBuilderBasicImpl(Builder builder, SliceSpec sliceSpec) {
        super(builder, sliceSpec);
    }

    public void addRow(RowBuilder rowBuilder) {
        if (this.mTitle == null && rowBuilder.getTitle() != null) {
            this.mTitle = rowBuilder.getTitle();
        }
        if (this.mSubtitle == null && rowBuilder.getSubtitle() != null) {
            this.mSubtitle = rowBuilder.getSubtitle();
        }
        if (this.mSliceAction == null && rowBuilder.getPrimaryAction() != null) {
            this.mSliceAction = rowBuilder.getPrimaryAction();
        }
        if (this.mSliceAction == null && rowBuilder.getTitleAction() != null) {
            this.mSliceAction = rowBuilder.getTitleAction();
        }
        if (this.mIconCompat == null && rowBuilder.getTitleIcon() != null) {
            this.mIconCompat = rowBuilder.getTitleIcon();
        }
    }

    public void setHeader(HeaderBuilder headerBuilder) {
        if (headerBuilder.getTitle() != null) {
            this.mTitle = headerBuilder.getTitle();
        }
        if (headerBuilder.getSubtitle() != null) {
            this.mSubtitle = headerBuilder.getSubtitle();
        }
        if (headerBuilder.getPrimaryAction() != null) {
            this.mSliceAction = headerBuilder.getPrimaryAction();
        }
    }

    public void setTtl(long j) {
        long j2 = -1;
        if (j != -1) {
            j2 = getClock().currentTimeMillis() + j;
        }
        getBuilder().addTimestamp(j2, "millis", "ttl");
    }

    public void apply(Builder builder) {
        if (this.mIsError) {
            builder.addHints("error");
        }
        if (this.mKeywords != null) {
            Builder builder2 = new Builder(getBuilder());
            for (String addText : this.mKeywords) {
                builder2.addText((CharSequence) addText, (String) null, new String[0]);
            }
            builder2.addHints("keywords");
            builder.addSubSlice(builder2.build());
        }
        Builder builder3 = new Builder(getBuilder());
        SliceAction sliceAction = this.mSliceAction;
        if (sliceAction != null) {
            if (this.mTitle == null && sliceAction.getTitle() != null) {
                this.mTitle = this.mSliceAction.getTitle();
            }
            if (this.mIconCompat == null && this.mSliceAction.getIcon() != null) {
                this.mIconCompat = this.mSliceAction.getIcon();
            }
            this.mSliceAction.setPrimaryAction(builder3);
        }
        CharSequence charSequence = this.mTitle;
        String str = "title";
        String str2 = "text";
        if (charSequence != null) {
            builder3.addItem(new SliceItem((Object) charSequence, str2, (String) null, new String[]{str}));
        }
        CharSequence charSequence2 = this.mSubtitle;
        if (charSequence2 != null) {
            builder3.addItem(new SliceItem((Object) charSequence2, str2, (String) null, new String[0]));
        }
        IconCompat iconCompat = this.mIconCompat;
        if (iconCompat != null) {
            builder.addIcon(iconCompat, (String) null, str);
        }
        builder.addSubSlice(builder3.build());
    }
}
