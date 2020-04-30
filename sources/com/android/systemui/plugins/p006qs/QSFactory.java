package com.android.systemui.plugins.p006qs;

import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@Dependencies({@DependsOn(target = QSTile.class), @DependsOn(target = QSTileView.class)})
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_QS_FACTORY", version = 1)
/* renamed from: com.android.systemui.plugins.qs.QSFactory */
public interface QSFactory extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_QS_FACTORY";
    public static final int VERSION = 1;

    QSTile createTile(String str);

    QSTileView createTileView(QSTile qSTile, boolean z);
}
