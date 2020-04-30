package com.android.systemui.controls.controller;

import android.util.AtomicFile;
import android.util.Log;
import android.util.Xml;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlSerializer;

/* compiled from: ControlsFavoritePersistenceWrapper.kt */
final class ControlsFavoritePersistenceWrapper$storeFavorites$1 implements Runnable {
    final /* synthetic */ List $structures;
    final /* synthetic */ ControlsFavoritePersistenceWrapper this$0;

    ControlsFavoritePersistenceWrapper$storeFavorites$1(ControlsFavoritePersistenceWrapper controlsFavoritePersistenceWrapper, List list) {
        this.this$0 = controlsFavoritePersistenceWrapper;
        this.$structures = list;
    }

    public final void run() {
        String str = "control";
        String str2 = "controls";
        String str3 = "structures";
        String str4 = "version";
        String str5 = "structure";
        StringBuilder sb = new StringBuilder();
        sb.append("Saving data to file: ");
        sb.append(this.this$0.file);
        String str6 = "ControlsFavoritePersistenceWrapper";
        Log.d(str6, sb.toString());
        AtomicFile atomicFile = new AtomicFile(this.this$0.file);
        try {
            FileOutputStream startWrite = atomicFile.startWrite();
            try {
                XmlSerializer newSerializer = Xml.newSerializer();
                newSerializer.setOutput(startWrite, "utf-8");
                newSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                newSerializer.startDocument(null, Boolean.TRUE);
                newSerializer.startTag(null, str4);
                newSerializer.text("1");
                newSerializer.endTag(null, str4);
                newSerializer.startTag(null, str3);
                for (StructureInfo structureInfo : this.$structures) {
                    newSerializer.startTag(null, str5);
                    newSerializer.attribute(null, "component", structureInfo.getComponentName().flattenToString());
                    newSerializer.attribute(null, str5, structureInfo.getStructure().toString());
                    newSerializer.startTag(null, str2);
                    for (ControlInfo controlInfo : structureInfo.getControls()) {
                        newSerializer.startTag(null, str);
                        newSerializer.attribute(null, "id", controlInfo.getControlId());
                        newSerializer.attribute(null, "title", controlInfo.getControlTitle().toString());
                        newSerializer.attribute(null, "subtitle", controlInfo.getControlSubtitle().toString());
                        newSerializer.attribute(null, "type", String.valueOf(controlInfo.getDeviceType()));
                        newSerializer.endTag(null, str);
                    }
                    newSerializer.endTag(null, str2);
                    newSerializer.endTag(null, str5);
                }
                newSerializer.endTag(null, str3);
                newSerializer.endDocument();
                atomicFile.finishWrite(startWrite);
            } catch (Throwable th) {
                IoUtils.closeQuietly(startWrite);
                throw th;
            }
            IoUtils.closeQuietly(startWrite);
        } catch (IOException e) {
            Log.e(str6, "Failed to start write file", e);
        }
    }
}
