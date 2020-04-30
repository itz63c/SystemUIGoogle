package com.google.android.systemui.assist;

import android.content.Context;
import android.os.UserManager;
import android.view.View;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.ArrayList;

public class OpaEnabledDispatcher implements OpaEnabledListener {
    private final Lazy<StatusBar> mStatusBarLazy;

    public OpaEnabledDispatcher(Lazy<StatusBar> lazy) {
        this.mStatusBarLazy = lazy;
    }

    public void onOpaEnabledReceived(Context context, boolean z, boolean z2, boolean z3) {
        dispatchUnchecked((z && z2) || UserManager.isDeviceInDemoMode(context));
    }

    private void dispatchUnchecked(boolean z) {
        StatusBar statusBar = (StatusBar) this.mStatusBarLazy.get();
        if (statusBar.getNavigationBarView() != null) {
            ArrayList views = statusBar.getNavigationBarView().getHomeButton().getViews();
            for (int i = 0; i < views.size(); i++) {
                ((OpaLayout) ((View) views.get(i))).setOpaEnabled(z);
            }
        }
    }
}
