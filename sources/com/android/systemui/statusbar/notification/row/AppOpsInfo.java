package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent;

public class AppOpsInfo extends LinearLayout implements GutsContent {
    private String mAppName;
    private ArraySet<Integer> mAppOps;
    private int mAppUid;
    private NotificationGuts mGutsContainer;
    private MetricsLogger mMetricsLogger;
    private OnClickListener mOnOk = new OnClickListener() {
        public final void onClick(View view) {
            AppOpsInfo.this.lambda$new$0$AppOpsInfo(view);
        }
    };
    private OnSettingsClickListener mOnSettingsClickListener;
    private String mPkg;
    private PackageManager mPm;
    private StatusBarNotification mSbn;

    public interface OnSettingsClickListener {
        void onClick(View view, String str, int i, ArraySet<Integer> arraySet);
    }

    public View getContentView() {
        return this;
    }

    public boolean handleCloseControls(boolean z, boolean z2) {
        return false;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    public boolean willBeRemoved() {
        return false;
    }

    public AppOpsInfo(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void bindGuts(PackageManager packageManager, OnSettingsClickListener onSettingsClickListener, StatusBarNotification statusBarNotification, ArraySet<Integer> arraySet) {
        String packageName = statusBarNotification.getPackageName();
        this.mPkg = packageName;
        this.mSbn = statusBarNotification;
        this.mPm = packageManager;
        this.mAppName = packageName;
        this.mOnSettingsClickListener = onSettingsClickListener;
        this.mAppOps = arraySet;
        bindHeader();
        bindPrompt();
        bindButtons();
        MetricsLogger metricsLogger = new MetricsLogger();
        this.mMetricsLogger = metricsLogger;
        metricsLogger.visibility(1345, true);
    }

    private void bindHeader() {
        Drawable drawable;
        try {
            ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(this.mPkg, 795136);
            if (applicationInfo != null) {
                this.mAppUid = this.mSbn.getUid();
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(applicationInfo));
                drawable = this.mPm.getApplicationIcon(applicationInfo);
            } else {
                drawable = null;
            }
        } catch (NameNotFoundException unused) {
            drawable = this.mPm.getDefaultActivityIcon();
        }
        ((ImageView) findViewById(C2011R$id.pkgicon)).setImageDrawable(drawable);
        ((TextView) findViewById(C2011R$id.pkgname)).setText(this.mAppName);
    }

    private void bindPrompt() {
        ((TextView) findViewById(C2011R$id.prompt)).setText(getPrompt());
    }

    private void bindButtons() {
        findViewById(C2011R$id.settings).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                AppOpsInfo.this.lambda$bindButtons$1$AppOpsInfo(view);
            }
        });
        ((TextView) findViewById(C2011R$id.f28ok)).setOnClickListener(this.mOnOk);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindButtons$1 */
    public /* synthetic */ void lambda$bindButtons$1$AppOpsInfo(View view) {
        this.mOnSettingsClickListener.onClick(view, this.mPkg, this.mAppUid, this.mAppOps);
    }

    private String getPrompt() {
        ArraySet<Integer> arraySet = this.mAppOps;
        if (arraySet == null || arraySet.size() == 0) {
            return "";
        }
        if (this.mAppOps.size() == 1) {
            if (this.mAppOps.contains(Integer.valueOf(26))) {
                return this.mContext.getString(C2017R$string.appops_camera);
            }
            if (this.mAppOps.contains(Integer.valueOf(27))) {
                return this.mContext.getString(C2017R$string.appops_microphone);
            }
            return this.mContext.getString(C2017R$string.appops_overlay);
        } else if (this.mAppOps.size() != 2) {
            return this.mContext.getString(C2017R$string.appops_camera_mic_overlay);
        } else {
            if (!this.mAppOps.contains(Integer.valueOf(26))) {
                return this.mContext.getString(C2017R$string.appops_mic_overlay);
            }
            if (this.mAppOps.contains(Integer.valueOf(27))) {
                return this.mContext.getString(C2017R$string.appops_camera_mic);
            }
            return this.mContext.getString(C2017R$string.appops_camera_overlay);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (this.mGutsContainer != null && accessibilityEvent.getEventType() == 32) {
            if (this.mGutsContainer.isExposed()) {
                accessibilityEvent.getText().add(this.mContext.getString(C2017R$string.notification_channel_controls_opened_accessibility, new Object[]{this.mAppName}));
                return;
            }
            accessibilityEvent.getText().add(this.mContext.getString(C2017R$string.notification_channel_controls_closed_accessibility, new Object[]{this.mAppName}));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: closeControls */
    public void lambda$new$0(View view) {
        this.mMetricsLogger.visibility(1345, false);
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        this.mGutsContainer.getLocationOnScreen(iArr);
        view.getLocationOnScreen(iArr2);
        int width = view.getWidth() / 2;
        this.mGutsContainer.closeControls((iArr2[0] - iArr[0]) + width, (iArr2[1] - iArr[1]) + (view.getHeight() / 2), false, false);
    }

    public void setGutsParent(NotificationGuts notificationGuts) {
        this.mGutsContainer = notificationGuts;
    }

    public int getActualHeight() {
        return getHeight();
    }
}
