package com.android.systemui;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.System;
import android.util.Log;
import android.view.WindowManagerGlobal;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public class GuestResumeSessionReceiver extends BroadcastReceiver {
    private Dialog mNewSessionDialog;

    private static class ResetSessionDialog extends SystemUIDialog implements OnClickListener {
        private final int mUserId;

        public ResetSessionDialog(Context context, int i) {
            super(context);
            setTitle(context.getString(C2017R$string.guest_wipe_session_title));
            setMessage(context.getString(C2017R$string.guest_wipe_session_message));
            setCanceledOnTouchOutside(false);
            setButton(-2, context.getString(C2017R$string.guest_wipe_session_wipe), this);
            setButton(-1, context.getString(C2017R$string.guest_wipe_session_dontwipe), this);
            this.mUserId = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -2) {
                GuestResumeSessionReceiver.wipeGuestSession(getContext(), this.mUserId);
                dismiss();
            } else if (i == -1) {
                cancel();
            }
        }
    }

    public void register(BroadcastDispatcher broadcastDispatcher) {
        broadcastDispatcher.registerReceiver(this, new IntentFilter("android.intent.action.USER_SWITCHED"), null, UserHandle.SYSTEM);
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
            cancelDialog();
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            if (intExtra == -10000) {
                StringBuilder sb = new StringBuilder();
                sb.append(intent);
                sb.append(" sent to ");
                String str = "GuestResumeSessionReceiver";
                sb.append(str);
                sb.append(" without EXTRA_USER_HANDLE");
                Log.e(str, sb.toString());
                return;
            }
            try {
                if (ActivityManager.getService().getCurrentUser().isGuest()) {
                    ContentResolver contentResolver = context.getContentResolver();
                    String str2 = "systemui.guest_has_logged_in";
                    if (System.getIntForUser(contentResolver, str2, 0, intExtra) != 0) {
                        ResetSessionDialog resetSessionDialog = new ResetSessionDialog(context, intExtra);
                        this.mNewSessionDialog = resetSessionDialog;
                        resetSessionDialog.show();
                    } else {
                        System.putIntForUser(contentResolver, str2, 1, intExtra);
                    }
                }
            } catch (RemoteException unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static void wipeGuestSession(Context context, int i) {
        String str = "GuestResumeSessionReceiver";
        UserManager userManager = (UserManager) context.getSystemService("user");
        try {
            UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            String str2 = "User requesting to start a new session (";
            if (currentUser.id != i) {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(i);
                sb.append(") is not current user (");
                sb.append(currentUser.id);
                sb.append(")");
                Log.w(str, sb.toString());
            } else if (!currentUser.isGuest()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                sb2.append(i);
                sb2.append(") is not a guest");
                Log.w(str, sb2.toString());
            } else if (!userManager.markGuestForDeletion(currentUser.id)) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Couldn't mark the guest for deletion for user ");
                sb3.append(i);
                Log.w(str, sb3.toString());
            } else {
                UserInfo createGuest = userManager.createGuest(context, currentUser.name);
                if (createGuest == null) {
                    try {
                        Log.e(str, "Could not create new guest, switching back to system user");
                        ActivityManager.getService().switchUser(0);
                        userManager.removeUser(currentUser.id);
                        WindowManagerGlobal.getWindowManagerService().lockNow(null);
                    } catch (RemoteException unused) {
                        Log.e(str, "Couldn't wipe session because ActivityManager or WindowManager is dead");
                    }
                } else {
                    ActivityManager.getService().switchUser(createGuest.id);
                    userManager.removeUser(currentUser.id);
                }
            }
        } catch (RemoteException unused2) {
            Log.e(str, "Couldn't wipe session because ActivityManager is dead");
        }
    }

    private void cancelDialog() {
        Dialog dialog = this.mNewSessionDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mNewSessionDialog.cancel();
            this.mNewSessionDialog = null;
        }
    }
}
