package com.android.systemui.tuner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class ShortcutParser {
    private AttributeSet mAttrs;
    private final Context mContext;
    private final String mName;
    private final String mPkg;
    private final int mResId;
    private Resources mResources;

    public static class Shortcut {
        public Icon icon;

        /* renamed from: id */
        public String f83id;
        public Intent intent;
        public String label;
        public String name;
        public String pkg;

        public static Shortcut create(Context context, String str) {
            String[] split = str.split("::");
            try {
                for (Shortcut shortcut : new ShortcutParser(context, new ComponentName(split[0], split[1])).getShortcuts()) {
                    if (shortcut.f83id.equals(split[2])) {
                        return shortcut;
                    }
                }
            } catch (NameNotFoundException unused) {
            }
            return null;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.pkg);
            String str = "::";
            sb.append(str);
            sb.append(this.name);
            sb.append(str);
            sb.append(this.f83id);
            return sb.toString();
        }
    }

    public ShortcutParser(Context context, ComponentName componentName) throws NameNotFoundException {
        this(context, componentName.getPackageName(), componentName.getClassName(), getResId(context, componentName));
    }

    private static int getResId(Context context, ComponentName componentName) throws NameNotFoundException {
        ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(componentName, 128);
        Bundle bundle = activityInfo.metaData;
        if (bundle != null) {
            String str = "android.app.shortcuts";
            if (bundle.containsKey(str)) {
                return activityInfo.metaData.getInt(str);
            }
        }
        return 0;
    }

    public ShortcutParser(Context context, String str, String str2, int i) {
        this.mContext = context;
        this.mPkg = str;
        this.mResId = i;
        this.mName = str2;
    }

    public List<Shortcut> getShortcuts() {
        ArrayList arrayList = new ArrayList();
        if (this.mResId != 0) {
            try {
                Resources resourcesForApplication = this.mContext.getPackageManager().getResourcesForApplication(this.mPkg);
                this.mResources = resourcesForApplication;
                XmlResourceParser xml = resourcesForApplication.getXml(this.mResId);
                this.mAttrs = Xml.asAttributeSet(xml);
                while (true) {
                    int next = xml.next();
                    if (next == 1) {
                        break;
                    } else if (next == 2) {
                        if (xml.getName().equals("shortcut")) {
                            Shortcut parseShortcut = parseShortcut(xml);
                            if (parseShortcut != null) {
                                arrayList.add(parseShortcut);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    private Shortcut parseShortcut(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        TypedArray obtainAttributes = this.mResources.obtainAttributes(this.mAttrs, R.styleable.Shortcut);
        Shortcut shortcut = new Shortcut();
        if (!obtainAttributes.getBoolean(1, true)) {
            return null;
        }
        String string = obtainAttributes.getString(2);
        int resourceId = obtainAttributes.getResourceId(0, 0);
        int resourceId2 = obtainAttributes.getResourceId(3, 0);
        String str = this.mPkg;
        shortcut.pkg = str;
        shortcut.icon = Icon.createWithResource(str, resourceId);
        shortcut.f83id = string;
        shortcut.label = this.mResources.getString(resourceId2);
        shortcut.name = this.mName;
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 3) {
                break;
            } else if (next == 2 && xmlResourceParser.getName().equals("intent")) {
                shortcut.intent = Intent.parseIntent(this.mResources, xmlResourceParser, this.mAttrs);
            }
        }
        if (shortcut.intent == null) {
            shortcut = null;
        }
        return shortcut;
    }
}
