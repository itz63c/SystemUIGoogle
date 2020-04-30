package com.android.systemui.statusbar.policy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.statusbar.phone.KeyguardPreviewContainer;
import java.util.List;

public class PreviewInflater {
    private final ActivityIntentHelper mActivityIntentHelper;
    private Context mContext;

    private static class WidgetInfo {
        String contextPackage;
        int layoutId;

        private WidgetInfo() {
        }
    }

    public PreviewInflater(Context context, LockPatternUtils lockPatternUtils, ActivityIntentHelper activityIntentHelper) {
        this.mContext = context;
        this.mActivityIntentHelper = activityIntentHelper;
    }

    public View inflatePreview(Intent intent) {
        return inflatePreview(getWidgetInfo(intent));
    }

    public View inflatePreviewFromService(ComponentName componentName) {
        return inflatePreview(getWidgetInfoFromService(componentName));
    }

    private KeyguardPreviewContainer inflatePreview(WidgetInfo widgetInfo) {
        if (widgetInfo == null) {
            return null;
        }
        View inflateWidgetView = inflateWidgetView(widgetInfo);
        if (inflateWidgetView == null) {
            return null;
        }
        KeyguardPreviewContainer keyguardPreviewContainer = new KeyguardPreviewContainer(this.mContext, null);
        keyguardPreviewContainer.addView(inflateWidgetView);
        return keyguardPreviewContainer;
    }

    private View inflateWidgetView(WidgetInfo widgetInfo) {
        try {
            Context createPackageContext = this.mContext.createPackageContext(widgetInfo.contextPackage, 4);
            return ((LayoutInflater) createPackageContext.getSystemService("layout_inflater")).cloneInContext(createPackageContext).inflate(widgetInfo.layoutId, null, false);
        } catch (NameNotFoundException | RuntimeException e) {
            Log.w("PreviewInflater", "Error creating widget view", e);
            return null;
        }
    }

    private WidgetInfo getWidgetInfoFromService(ComponentName componentName) {
        try {
            return getWidgetInfoFromMetaData(componentName.getPackageName(), this.mContext.getPackageManager().getServiceInfo(componentName, 128).metaData);
        } catch (NameNotFoundException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to load preview; ");
            sb.append(componentName.flattenToShortString());
            sb.append(" not found");
            Log.w("PreviewInflater", sb.toString(), e);
            return null;
        }
    }

    private WidgetInfo getWidgetInfoFromMetaData(String str, Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        int i = bundle.getInt("com.android.keyguard.layout");
        if (i == 0) {
            return null;
        }
        WidgetInfo widgetInfo = new WidgetInfo();
        widgetInfo.contextPackage = str;
        widgetInfo.layoutId = i;
        return widgetInfo;
    }

    private WidgetInfo getWidgetInfo(Intent intent) {
        PackageManager packageManager = this.mContext.getPackageManager();
        List queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(intent, 851968, KeyguardUpdateMonitor.getCurrentUser());
        if (queryIntentActivitiesAsUser.size() == 0) {
            return null;
        }
        ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(intent, 852096, KeyguardUpdateMonitor.getCurrentUser());
        if (!this.mActivityIntentHelper.wouldLaunchResolverActivity(resolveActivityAsUser, queryIntentActivitiesAsUser) && resolveActivityAsUser != null) {
            ActivityInfo activityInfo = resolveActivityAsUser.activityInfo;
            if (activityInfo != null) {
                return getWidgetInfoFromMetaData(activityInfo.packageName, activityInfo.metaData);
            }
        }
        return null;
    }
}
