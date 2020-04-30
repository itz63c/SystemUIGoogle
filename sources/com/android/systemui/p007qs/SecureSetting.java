package com.android.systemui.p007qs;

import android.app.ActivityManager;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.Secure;

/* renamed from: com.android.systemui.qs.SecureSetting */
public abstract class SecureSetting extends ContentObserver {
    private final Context mContext;
    private boolean mListening;
    private int mObservedValue = 0;
    private final String mSettingName;
    private int mUserId;

    /* access modifiers changed from: protected */
    public abstract void handleValueChanged(int i, boolean z);

    public SecureSetting(Context context, Handler handler, String str) {
        super(handler);
        this.mContext = context;
        this.mSettingName = str;
        this.mUserId = ActivityManager.getCurrentUser();
    }

    public int getValue() {
        return Secure.getIntForUser(this.mContext.getContentResolver(), this.mSettingName, 0, this.mUserId);
    }

    public void setValue(int i) {
        Secure.putIntForUser(this.mContext.getContentResolver(), this.mSettingName, i, this.mUserId);
    }

    public void setListening(boolean z) {
        if (z != this.mListening) {
            this.mListening = z;
            if (z) {
                this.mObservedValue = getValue();
                this.mContext.getContentResolver().registerContentObserver(Secure.getUriFor(this.mSettingName), false, this, this.mUserId);
            } else {
                this.mContext.getContentResolver().unregisterContentObserver(this);
                this.mObservedValue = 0;
            }
        }
    }

    public void onChange(boolean z) {
        int value = getValue();
        handleValueChanged(value, value != this.mObservedValue);
        this.mObservedValue = value;
    }

    public void setUserId(int i) {
        this.mUserId = i;
        if (this.mListening) {
            setListening(false);
            setListening(true);
        }
    }
}
