package com.android.settingslib.applications;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.IconDrawableFactory;
import com.android.settingslib.widget.CandidateInfo;

public class DefaultAppInfo extends CandidateInfo {
    public final ComponentName componentName;
    private final Context mContext;
    protected final PackageManager mPm;
    public final PackageItemInfo packageItemInfo;
    public final int userId;

    public DefaultAppInfo(Context context, PackageManager packageManager, int i, ComponentName componentName2) {
        this(context, packageManager, i, componentName2, null, true);
    }

    public DefaultAppInfo(Context context, PackageManager packageManager, int i, ComponentName componentName2, String str, boolean z) {
        super(z);
        this.mContext = context;
        this.mPm = packageManager;
        this.packageItemInfo = null;
        this.userId = i;
        this.componentName = componentName2;
    }

    public CharSequence loadLabel() {
        if (this.componentName != null) {
            try {
                ComponentInfo componentInfo = getComponentInfo();
                if (componentInfo != null) {
                    return componentInfo.loadLabel(this.mPm);
                }
                return this.mPm.getApplicationInfoAsUser(this.componentName.getPackageName(), 0, this.userId).loadLabel(this.mPm);
            } catch (NameNotFoundException unused) {
                return null;
            }
        } else {
            PackageItemInfo packageItemInfo2 = this.packageItemInfo;
            if (packageItemInfo2 != null) {
                return packageItemInfo2.loadLabel(this.mPm);
            }
            return null;
        }
    }

    public Drawable loadIcon() {
        IconDrawableFactory newInstance = IconDrawableFactory.newInstance(this.mContext);
        if (this.componentName != null) {
            try {
                ComponentInfo componentInfo = getComponentInfo();
                ApplicationInfo applicationInfoAsUser = this.mPm.getApplicationInfoAsUser(this.componentName.getPackageName(), 0, this.userId);
                if (componentInfo != null) {
                    return newInstance.getBadgedIcon(componentInfo, applicationInfoAsUser, this.userId);
                }
                return newInstance.getBadgedIcon(applicationInfoAsUser);
            } catch (NameNotFoundException unused) {
                return null;
            }
        } else {
            PackageItemInfo packageItemInfo2 = this.packageItemInfo;
            if (packageItemInfo2 != null) {
                try {
                    return newInstance.getBadgedIcon(this.packageItemInfo, this.mPm.getApplicationInfoAsUser(packageItemInfo2.packageName, 0, this.userId), this.userId);
                } catch (NameNotFoundException unused2) {
                }
            }
            return null;
        }
    }

    public String getKey() {
        ComponentName componentName2 = this.componentName;
        if (componentName2 != null) {
            return componentName2.flattenToString();
        }
        PackageItemInfo packageItemInfo2 = this.packageItemInfo;
        if (packageItemInfo2 != null) {
            return packageItemInfo2.packageName;
        }
        return null;
    }

    private ComponentInfo getComponentInfo() {
        try {
            ComponentInfo activityInfo = AppGlobals.getPackageManager().getActivityInfo(this.componentName, 0, this.userId);
            if (activityInfo == null) {
                activityInfo = AppGlobals.getPackageManager().getServiceInfo(this.componentName, 0, this.userId);
            }
            return activityInfo;
        } catch (RemoteException unused) {
            return null;
        }
    }
}
