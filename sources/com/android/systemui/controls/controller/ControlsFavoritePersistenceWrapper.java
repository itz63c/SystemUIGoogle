package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.util.Log;
import android.util.Xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$IntRef;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* compiled from: ControlsFavoritePersistenceWrapper.kt */
public final class ControlsFavoritePersistenceWrapper {
    private final Executor executor;
    /* access modifiers changed from: private */
    public File file;

    public ControlsFavoritePersistenceWrapper(File file2, Executor executor2) {
        Intrinsics.checkParameterIsNotNull(file2, "file");
        Intrinsics.checkParameterIsNotNull(executor2, "executor");
        this.file = file2;
        this.executor = executor2;
    }

    public final void changeFile(File file2) {
        Intrinsics.checkParameterIsNotNull(file2, "fileName");
        this.file = file2;
    }

    public final void storeFavorites(List<StructureInfo> list) {
        Intrinsics.checkParameterIsNotNull(list, "structures");
        this.executor.execute(new ControlsFavoritePersistenceWrapper$storeFavorites$1(this, list));
    }

    public final List<StructureInfo> readFavorites() {
        String str = "Failed parsing favorites file: ";
        String str2 = "ControlsFavoritePersistenceWrapper";
        if (!this.file.exists()) {
            Log.d(str2, "No favorites, returning empty list");
            return CollectionsKt__CollectionsKt.emptyList();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(this.file);
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("Reading data from file: ");
                sb.append(this.file);
                Log.d(str2, sb.toString());
                XmlPullParser newPullParser = Xml.newPullParser();
                newPullParser.setInput(fileInputStream, null);
                Intrinsics.checkExpressionValueIsNotNull(newPullParser, "parser");
                List<StructureInfo> parseXml = parseXml(newPullParser);
                IoUtils.closeQuietly(fileInputStream);
                return parseXml;
            } catch (XmlPullParserException e) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(this.file);
                throw new IllegalStateException(sb2.toString(), e);
            } catch (IOException e2) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str);
                sb3.append(this.file);
                throw new IllegalStateException(sb3.toString(), e2);
            } catch (Throwable th) {
                IoUtils.closeQuietly(fileInputStream);
                throw th;
            }
        } catch (FileNotFoundException unused) {
            Log.i(str2, "No file found");
            return CollectionsKt__CollectionsKt.emptyList();
        }
    }

    private final List<StructureInfo> parseXml(XmlPullParser xmlPullParser) {
        Ref$IntRef ref$IntRef = new Ref$IntRef();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ComponentName componentName = null;
        CharSequence charSequence = null;
        while (true) {
            int next = xmlPullParser.next();
            ref$IntRef.element = next;
            if (next == 1) {
                return arrayList;
            }
            String name = xmlPullParser.getName();
            String str = "";
            if (name == null) {
                name = str;
            }
            String str2 = "structure";
            if (ref$IntRef.element == 2 && Intrinsics.areEqual((Object) name, (Object) str2)) {
                componentName = ComponentName.unflattenFromString(xmlPullParser.getAttributeValue(null, "component"));
                charSequence = xmlPullParser.getAttributeValue(null, str2);
                if (charSequence == null) {
                    charSequence = str;
                }
            } else if (ref$IntRef.element == 2 && Intrinsics.areEqual((Object) name, (Object) "control")) {
                String attributeValue = xmlPullParser.getAttributeValue(null, "id");
                String attributeValue2 = xmlPullParser.getAttributeValue(null, "title");
                String attributeValue3 = xmlPullParser.getAttributeValue(null, "subtitle");
                if (attributeValue3 != null) {
                    str = attributeValue3;
                }
                String attributeValue4 = xmlPullParser.getAttributeValue(null, "type");
                Integer valueOf = attributeValue4 != null ? Integer.valueOf(Integer.parseInt(attributeValue4)) : null;
                if (!(attributeValue == null || attributeValue2 == null || valueOf == null)) {
                    arrayList2.add(new ControlInfo(attributeValue, attributeValue2, str, valueOf.intValue()));
                }
            } else if (ref$IntRef.element == 3 && Intrinsics.areEqual((Object) name, (Object) str2)) {
                if (componentName == null) {
                    Intrinsics.throwNpe();
                    throw null;
                } else if (charSequence != null) {
                    arrayList.add(new StructureInfo(componentName, charSequence, CollectionsKt___CollectionsKt.toList(arrayList2)));
                    arrayList2.clear();
                } else {
                    Intrinsics.throwNpe();
                    throw null;
                }
            }
        }
    }
}
