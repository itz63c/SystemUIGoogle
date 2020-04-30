package com.android.systemui.p007qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.ViewGroup;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.p007qs.QSPanel.QSTileLayout;
import com.android.systemui.p007qs.QSPanel.TileRecord;
import com.android.systemui.plugins.p006qs.QSTileView;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.qs.DoubleLineTileLayout */
/* compiled from: DoubleLineTileLayout.kt */
public final class DoubleLineTileLayout extends ViewGroup implements QSTileLayout {
    private boolean _listening;
    private int cellMarginHorizontal;
    private int cellMarginVertical;
    private final ArrayList<TileRecord> mRecords = new ArrayList<>();
    private int smallTileSize;

    public DoubleLineTileLayout(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
        setFocusableInTouchMode(true);
        setClipChildren(false);
        setClipToPadding(false);
        updateResources();
    }

    private final int getTwoLineHeight() {
        return (this.smallTileSize * 2) + this.cellMarginVertical;
    }

    public void addTile(TileRecord tileRecord) {
        Intrinsics.checkParameterIsNotNull(tileRecord, "tile");
        this.mRecords.add(tileRecord);
        tileRecord.tile.setListening(this, this._listening);
        addTileView(tileRecord);
    }

    /* access modifiers changed from: protected */
    public final void addTileView(TileRecord tileRecord) {
        Intrinsics.checkParameterIsNotNull(tileRecord, "tile");
        addView(tileRecord.tileView);
    }

    public void removeTile(TileRecord tileRecord) {
        Intrinsics.checkParameterIsNotNull(tileRecord, "tile");
        this.mRecords.remove(tileRecord);
        tileRecord.tile.setListening(this, false);
        removeView(tileRecord.tileView);
    }

    public void removeAllViews() {
        for (TileRecord tileRecord : this.mRecords) {
            tileRecord.tile.setListening(this, false);
        }
        this.mRecords.clear();
        super.removeAllViews();
    }

    public int getOffsetTop(TileRecord tileRecord) {
        return getTop();
    }

    public boolean updateResources() {
        Context context = this.mContext;
        Intrinsics.checkExpressionValueIsNotNull(context, "mContext");
        Resources resources = context.getResources();
        this.smallTileSize = resources.getDimensionPixelSize(C2009R$dimen.qs_quick_tile_size);
        this.cellMarginHorizontal = resources.getDimensionPixelSize(C2009R$dimen.qs_tile_margin_horizontal);
        this.cellMarginVertical = resources.getDimensionPixelSize(C2009R$dimen.new_qs_vertical_margin);
        requestLayout();
        return false;
    }

    public void setListening(boolean z) {
        if (this._listening != z) {
            this._listening = z;
            Iterator it = this.mRecords.iterator();
            while (it.hasNext()) {
                ((TileRecord) it.next()).tile.setListening(this, z);
            }
        }
    }

    public int getNumVisibleTiles() {
        return this.mRecords.size();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        Intrinsics.checkParameterIsNotNull(configuration, "newConfig");
        super.onConfigurationChanged(configuration);
        updateResources();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        updateResources();
    }

    /* JADX WARNING: type inference failed for: r0v9 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r5, int r6) {
        /*
            r4 = this;
            java.util.ArrayList<com.android.systemui.qs.QSPanel$TileRecord> r5 = r4.mRecords
            java.util.Iterator r5 = r5.iterator()
            r6 = 0
            r0 = r4
        L_0x0008:
            boolean r1 = r5.hasNext()
            if (r1 == 0) goto L_0x0039
            java.lang.Object r1 = r5.next()
            com.android.systemui.qs.QSPanel$TileRecord r1 = (com.android.systemui.p007qs.QSPanel.TileRecord) r1
            com.android.systemui.plugins.qs.QSTileView r1 = r1.tileView
            java.lang.String r2 = "tileView"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r2)
            int r2 = r1.getVisibility()
            r3 = 8
            if (r2 == r3) goto L_0x0008
            r1.updateAccessibilityOrder(r0)
            int r6 = r6 + 1
            int r0 = r4.smallTileSize
            int r0 = com.android.systemui.p007qs.TileLayout.exactly(r0)
            int r2 = r4.smallTileSize
            int r2 = com.android.systemui.p007qs.TileLayout.exactly(r2)
            r1.measure(r0, r2)
            r0 = r1
            goto L_0x0008
        L_0x0039:
            int r5 = r4.getTwoLineHeight()
            int r6 = r6 / 2
            int r0 = r4.getPaddingStart()
            int r1 = r4.getPaddingEnd()
            int r0 = r0 + r1
            int r1 = r4.smallTileSize
            int r1 = r1 * r6
            int r0 = r0 + r1
            int r6 = r6 + -1
            int r1 = r4.cellMarginHorizontal
            int r6 = r6 * r1
            int r0 = r0 + r6
            r4.setMeasuredDimension(r0, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.DoubleLineTileLayout.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        ArrayList<TileRecord> arrayList = this.mRecords;
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (true) {
            boolean z2 = false;
            if (!it.hasNext()) {
                break;
            }
            Object next = it.next();
            QSTileView qSTileView = ((TileRecord) next).tileView;
            Intrinsics.checkExpressionValueIsNotNull(qSTileView, "it.tileView");
            if (qSTileView.getVisibility() != 8) {
                z2 = true;
            }
            if (z2) {
                arrayList2.add(next);
            }
        }
        int i5 = 0;
        for (Object next2 : arrayList2) {
            int i6 = i5 + 1;
            if (i5 >= 0) {
                TileRecord tileRecord = (TileRecord) next2;
                int leftForColumn = getLeftForColumn(i5 % (arrayList2.size() / 2));
                int topBottomRow = i5 < arrayList2.size() / 2 ? 0 : getTopBottomRow();
                QSTileView qSTileView2 = tileRecord.tileView;
                int i7 = this.smallTileSize;
                qSTileView2.layout(leftForColumn, topBottomRow, leftForColumn + i7, i7 + topBottomRow);
                i5 = i6;
            } else {
                CollectionsKt.throwIndexOverflow();
                throw null;
            }
        }
    }

    private final int getLeftForColumn(int i) {
        return i * (this.smallTileSize + this.cellMarginHorizontal);
    }

    private final int getTopBottomRow() {
        return this.smallTileSize + this.cellMarginVertical;
    }
}
