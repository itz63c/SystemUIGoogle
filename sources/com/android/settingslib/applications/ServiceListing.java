package com.android.settingslib.applications;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Slog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ServiceListing {
    private final List<Callback> mCallbacks;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final HashSet<ComponentName> mEnabledServices;
    private final String mIntentAction;
    private boolean mListening;
    private final String mNoun;
    private final BroadcastReceiver mPackageReceiver;
    private final String mPermission;
    private final List<ServiceInfo> mServices;
    private final String mSetting;
    private final ContentObserver mSettingsObserver;
    private final String mTag;

    public static class Builder {
        private final Context mContext;
        private String mIntentAction;
        private String mNoun;
        private String mPermission;
        private String mSetting;
        private String mTag;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTag(String str) {
            this.mTag = str;
            return this;
        }

        public Builder setSetting(String str) {
            this.mSetting = str;
            return this;
        }

        public Builder setIntentAction(String str) {
            this.mIntentAction = str;
            return this;
        }

        public Builder setPermission(String str) {
            this.mPermission = str;
            return this;
        }

        public Builder setNoun(String str) {
            this.mNoun = str;
            return this;
        }

        public ServiceListing build() {
            ServiceListing serviceListing = new ServiceListing(this.mContext, this.mTag, this.mSetting, this.mIntentAction, this.mPermission, this.mNoun);
            return serviceListing;
        }
    }

    public interface Callback {
        void onServicesReloaded(List<ServiceInfo> list);
    }

    private ServiceListing(Context context, String str, String str2, String str3, String str4, String str5) {
        this.mEnabledServices = new HashSet<>();
        this.mServices = new ArrayList();
        this.mCallbacks = new ArrayList();
        this.mSettingsObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean z, Uri uri) {
                ServiceListing.this.reload();
            }
        };
        this.mPackageReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ServiceListing.this.reload();
            }
        };
        this.mContentResolver = context.getContentResolver();
        this.mContext = context;
        this.mTag = str;
        this.mSetting = str2;
        this.mIntentAction = str3;
        this.mPermission = str4;
        this.mNoun = str5;
    }

    public void addCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void setListening(boolean z) {
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
                intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
                intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
                intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
                intentFilter.addDataScheme("package");
                this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
                this.mContentResolver.registerContentObserver(Secure.getUriFor(this.mSetting), false, this.mSettingsObserver);
            } else {
                this.mContext.unregisterReceiver(this.mPackageReceiver);
                this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
            }
        }
    }

    private void loadEnabledServices() {
        this.mEnabledServices.clear();
        String string = Secure.getString(this.mContentResolver, this.mSetting);
        if (string != null && !"".equals(string)) {
            for (String unflattenFromString : string.split(":")) {
                ComponentName unflattenFromString2 = ComponentName.unflattenFromString(unflattenFromString);
                if (unflattenFromString2 != null) {
                    this.mEnabledServices.add(unflattenFromString2);
                }
            }
        }
    }

    public void reload() {
        loadEnabledServices();
        this.mServices.clear();
        for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent(this.mIntentAction), 132, ActivityManager.getCurrentUser())) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (!this.mPermission.equals(serviceInfo.permission)) {
                String str = this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("Skipping ");
                sb.append(this.mNoun);
                sb.append(" service ");
                sb.append(serviceInfo.packageName);
                sb.append("/");
                sb.append(serviceInfo.name);
                sb.append(": it does not require the permission ");
                sb.append(this.mPermission);
                Slog.w(str, sb.toString());
            } else {
                this.mServices.add(serviceInfo);
            }
        }
        for (Callback onServicesReloaded : this.mCallbacks) {
            onServicesReloaded.onServicesReloaded(this.mServices);
        }
    }
}
