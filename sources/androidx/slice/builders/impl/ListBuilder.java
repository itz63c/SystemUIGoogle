package androidx.slice.builders.impl;

import androidx.slice.builders.ListBuilder.HeaderBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;

public interface ListBuilder {
    void addRow(RowBuilder rowBuilder);

    void setHeader(HeaderBuilder headerBuilder);

    void setTtl(long j);
}
