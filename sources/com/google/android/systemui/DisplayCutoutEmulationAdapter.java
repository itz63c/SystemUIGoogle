package com.google.android.systemui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.om.IOverlayManager;
import android.content.om.IOverlayManager.Stub;
import android.content.om.OverlayInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;

public class DisplayCutoutEmulationAdapter {
    private final Context mContext;
    private final ContentObserver mObserver = new ContentObserver(Handler.getMain()) {
        public void onChange(boolean z) {
            DisplayCutoutEmulationAdapter.this.update();
        }
    };
    private final IOverlayManager mOverlayManager;

    public DisplayCutoutEmulationAdapter(Context context) {
        this.mContext = context;
        this.mOverlayManager = Stub.asInterface(ServiceManager.getService("overlay"));
        register();
        update();
    }

    private void register() {
        this.mContext.getContentResolver().registerContentObserver(Global.getUriFor("com.google.android.systemui.display_cutout_emulation"), false, this.mObserver, 0);
    }

    /* access modifiers changed from: private */
    public void update() {
        String str = "CutoutEmulationAdapter";
        String stringForUser = Global.getStringForUser(this.mContext.getContentResolver(), "com.google.android.systemui.display_cutout_emulation", 0);
        if (stringForUser != null) {
            String[] split = stringForUser.split(":", 2);
            try {
                int parseInt = Integer.parseInt(split[0]);
                String str2 = split[1];
                String str3 = "com.google.android.systemui.display_cutout_emulation.VERSION";
                if (parseInt > getPrefs().getInt(str3, -1)) {
                    if (str2.isEmpty() || str2.startsWith("com.android.internal.display.cutout.emulation")) {
                        setEmulationOverlay(str2);
                        getPrefs().edit().putInt(str3, parseInt).apply();
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid overlay prefix: ");
                    sb.append(stringForUser);
                    Log.e(str, sb.toString());
                }
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Invalid configuration: ");
                sb2.append(stringForUser);
                Log.e(str, sb2.toString(), e);
            }
        }
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext);
    }

    private void setEmulationOverlay(String str) {
        OverlayInfo[] overlayInfos = getOverlayInfos();
        String str2 = null;
        for (OverlayInfo overlayInfo : overlayInfos) {
            if (overlayInfo.isEnabled()) {
                str2 = overlayInfo.packageName;
            }
        }
        if ((!TextUtils.isEmpty(str) || !TextUtils.isEmpty(str2)) && !TextUtils.equals(str, str2)) {
            for (OverlayInfo overlayInfo2 : overlayInfos) {
                boolean isEnabled = overlayInfo2.isEnabled();
                boolean equals = TextUtils.equals(overlayInfo2.packageName, str);
                if (isEnabled != equals) {
                    try {
                        this.mOverlayManager.setEnabled(overlayInfo2.packageName, equals, 0);
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }
    }

    private OverlayInfo[] getOverlayInfos() {
        try {
            List overlayInfosForTarget = this.mOverlayManager.getOverlayInfosForTarget("android", 0);
            for (int size = overlayInfosForTarget.size() - 1; size >= 0; size--) {
                if (!((OverlayInfo) overlayInfosForTarget.get(size)).packageName.startsWith("com.android.internal.display.cutout.emulation")) {
                    overlayInfosForTarget.remove(size);
                }
            }
            return (OverlayInfo[]) overlayInfosForTarget.toArray(new OverlayInfo[overlayInfosForTarget.size()]);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
