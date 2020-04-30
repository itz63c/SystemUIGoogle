package com.google.android.systemui;

import android.content.Context;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dumpable;
import com.android.systemui.VendorServices;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.ambientmusic.AmbientIndicationContainer;
import com.google.android.systemui.ambientmusic.AmbientIndicationService;
import com.google.android.systemui.elmyra.ElmyraContext;
import com.google.android.systemui.elmyra.ElmyraService;
import com.google.android.systemui.elmyra.ServiceConfigurationGoogle;
import com.google.android.systemui.face.FaceNotificationService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GoogleServices extends VendorServices {
    private final ServiceConfigurationGoogle mServiceConfigurationGoogle;
    private ArrayList<Object> mServices = new ArrayList<>();
    private final StatusBar mStatusBar;

    public GoogleServices(Context context, ServiceConfigurationGoogle serviceConfigurationGoogle, StatusBar statusBar) {
        super(context);
        this.mServiceConfigurationGoogle = serviceConfigurationGoogle;
        this.mStatusBar = statusBar;
    }

    public void start() {
        AmbientIndicationContainer ambientIndicationContainer = (AmbientIndicationContainer) this.mStatusBar.getNotificationShadeWindowView().findViewById(C2011R$id.ambient_indication_container);
        ambientIndicationContainer.initializeView(this.mStatusBar);
        addService(new AmbientIndicationService(this.mContext, ambientIndicationContainer));
        addService(new DisplayCutoutEmulationAdapter(this.mContext));
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub") && new ElmyraContext(this.mContext).isAvailable()) {
            addService(new ElmyraService(this.mContext, this.mServiceConfigurationGoogle));
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            addService(new FaceNotificationService(this.mContext));
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < this.mServices.size(); i++) {
            if (this.mServices.get(i) instanceof Dumpable) {
                ((Dumpable) this.mServices.get(i)).dump(fileDescriptor, printWriter, strArr);
            }
        }
    }

    private void addService(Object obj) {
        if (obj != null) {
            this.mServices.add(obj);
        }
    }
}
