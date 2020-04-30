package com.android.systemui.p007qs;

import android.content.Context;
import com.android.systemui.p007qs.external.TileServices;
import com.android.systemui.p007qs.logging.QSLogger;

/* renamed from: com.android.systemui.qs.QSHost */
public interface QSHost {

    /* renamed from: com.android.systemui.qs.QSHost$Callback */
    public interface Callback {
        void onTilesChanged();
    }

    void collapsePanels();

    Context getContext();

    QSLogger getQSLogger();

    TileServices getTileServices();

    Context getUserContext();

    int indexOf(String str);

    void openPanels();

    void removeTile(String str);

    void unmarkTileAsAutoAdded(String str);

    void warn(String str, Throwable th);
}
