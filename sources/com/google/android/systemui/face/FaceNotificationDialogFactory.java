package com.google.android.systemui.face;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceManager.RemovalCallback;
import android.util.Log;
import com.android.systemui.C2017R$string;
import com.android.systemui.statusbar.phone.SystemUIDialog;

class FaceNotificationDialogFactory {
    static /* synthetic */ void lambda$createReenrollDialog$1(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$createReenrollFailureDialog$2(DialogInterface dialogInterface, int i) {
    }

    static Dialog createReenrollDialog(Context context) {
        SystemUIDialog systemUIDialog = new SystemUIDialog(context);
        systemUIDialog.setTitle(context.getString(C2017R$string.face_reenroll_dialog_title));
        systemUIDialog.setMessage(context.getString(C2017R$string.face_reenroll_dialog_content));
        systemUIDialog.setPositiveButton(C2017R$string.face_reenroll_dialog_confirm, new OnClickListener(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                FaceNotificationDialogFactory.onReenrollDialogConfirm(this.f$0);
            }
        });
        systemUIDialog.setNegativeButton(C2017R$string.face_reenroll_dialog_cancel, C1940x280dcf9b.INSTANCE);
        return systemUIDialog;
    }

    /* access modifiers changed from: private */
    public static Dialog createReenrollFailureDialog(Context context) {
        SystemUIDialog systemUIDialog = new SystemUIDialog(context);
        systemUIDialog.setMessage(context.getText(C2017R$string.face_reenroll_failure_dialog_content));
        systemUIDialog.setPositiveButton(C2017R$string.f31ok, C1939xf01dc073.INSTANCE);
        return systemUIDialog;
    }

    /* access modifiers changed from: private */
    public static void onReenrollDialogConfirm(final Context context) {
        FaceManager faceManager = (FaceManager) context.getSystemService(FaceManager.class);
        if (faceManager == null) {
            Log.e("FaceNotificationDialogF", "Not launching enrollment. Face manager was null!");
            createReenrollFailureDialog(context).show();
            return;
        }
        faceManager.remove(new Face("", 0, 0), context.getUserId(), new RemovalCallback() {
            boolean mDidShowFailureDialog;

            public void onRemovalError(Face face, int i, CharSequence charSequence) {
                Log.e("FaceNotificationDialogF", "Not launching enrollment. Failed to remove existing face(s).");
                if (!this.mDidShowFailureDialog) {
                    this.mDidShowFailureDialog = true;
                    FaceNotificationDialogFactory.createReenrollFailureDialog(context).show();
                }
            }

            public void onRemovalSucceeded(Face face, int i) {
                if (!this.mDidShowFailureDialog && i == 0) {
                    Intent intent = new Intent("android.settings.BIOMETRIC_ENROLL");
                    intent.setPackage("com.android.settings");
                    intent.setFlags(268435456);
                    context.startActivity(intent);
                }
            }
        });
    }
}
