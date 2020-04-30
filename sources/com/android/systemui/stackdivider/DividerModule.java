package com.android.systemui.stackdivider;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.TransactionPool;
import com.android.systemui.p010wm.DisplayController;
import com.android.systemui.p010wm.DisplayImeController;
import com.android.systemui.p010wm.SystemWindows;
import com.android.systemui.recents.Recents;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.Lazy;
import java.util.Optional;

public class DividerModule {
    static Divider provideDivider(Context context, Optional<Lazy<Recents>> optional, DisplayController displayController, SystemWindows systemWindows, DisplayImeController displayImeController, Handler handler, KeyguardStateController keyguardStateController, TransactionPool transactionPool) {
        Divider divider = new Divider(context, optional, displayController, systemWindows, displayImeController, handler, keyguardStateController, transactionPool);
        return divider;
    }
}
