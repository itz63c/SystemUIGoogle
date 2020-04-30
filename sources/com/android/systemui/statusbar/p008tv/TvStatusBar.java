package com.android.systemui.statusbar.p008tv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;

/* renamed from: com.android.systemui.statusbar.tv.TvStatusBar */
public class TvStatusBar extends SystemUI implements Callbacks {
    private final CommandQueue mCommandQueue;

    public TvStatusBar(Context context, CommandQueue commandQueue) {
        super(context);
        this.mCommandQueue = commandQueue;
    }

    public void start() {
        IStatusBarService asInterface = Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mCommandQueue.addCallback((Callbacks) this);
        try {
            asInterface.registerStatusBar(this.mCommandQueue);
        } catch (RemoteException unused) {
        }
        new AudioRecordingDisclosureBar(this.mContext).start();
    }

    public void animateExpandNotificationsPanel() {
        startSystemActivity(new Intent("com.android.tv.action.OPEN_NOTIFICATIONS_PANEL"));
    }

    private void startSystemActivity(Intent intent) {
        ResolveInfo resolveActivity = this.mContext.getPackageManager().resolveActivity(intent, 1048576);
        if (resolveActivity != null) {
            ActivityInfo activityInfo = resolveActivity.activityInfo;
            if (activityInfo != null) {
                intent.setPackage(activityInfo.packageName);
                this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
            }
        }
    }
}
