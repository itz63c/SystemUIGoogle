package com.android.systemui.p007qs;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyEventLogger;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserManager;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.C2018R$style;
import com.android.systemui.Dependency;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SecurityController.SecurityControllerCallback;

/* renamed from: com.android.systemui.qs.QSSecurityFooter */
public class QSSecurityFooter implements OnClickListener, DialogInterface.OnClickListener {
    /* access modifiers changed from: private */
    public final ActivityStarter mActivityStarter;
    private final Callback mCallback = new Callback();
    private final Context mContext;
    /* access modifiers changed from: private */
    public AlertDialog mDialog;
    /* access modifiers changed from: private */
    public final View mDivider;
    /* access modifiers changed from: private */
    public final ImageView mFooterIcon;
    /* access modifiers changed from: private */
    public int mFooterIconId;
    /* access modifiers changed from: private */
    public final TextView mFooterText;
    /* access modifiers changed from: private */
    public CharSequence mFooterTextContent = null;
    protected C0983H mHandler;
    /* access modifiers changed from: private */
    public QSTileHost mHost;
    /* access modifiers changed from: private */
    public boolean mIsVisible;
    private final Handler mMainHandler;
    /* access modifiers changed from: private */
    public final View mRootView;
    private final SecurityController mSecurityController;
    private final UserManager mUm;
    private final Runnable mUpdateDisplayState = new Runnable() {
        public void run() {
            if (QSSecurityFooter.this.mFooterTextContent != null) {
                QSSecurityFooter.this.mFooterText.setText(QSSecurityFooter.this.mFooterTextContent);
            }
            int i = 0;
            QSSecurityFooter.this.mRootView.setVisibility(QSSecurityFooter.this.mIsVisible ? 0 : 8);
            if (QSSecurityFooter.this.mDivider != null) {
                View access$800 = QSSecurityFooter.this.mDivider;
                if (QSSecurityFooter.this.mIsVisible) {
                    i = 8;
                }
                access$800.setVisibility(i);
            }
        }
    };
    private final Runnable mUpdateIcon = new Runnable() {
        public void run() {
            QSSecurityFooter.this.mFooterIcon.setImageResource(QSSecurityFooter.this.mFooterIconId);
        }
    };

    /* renamed from: com.android.systemui.qs.QSSecurityFooter$Callback */
    private class Callback implements SecurityControllerCallback {
        private Callback() {
        }

        public void onStateChanged() {
            QSSecurityFooter.this.refreshState();
        }
    }

    /* renamed from: com.android.systemui.qs.QSSecurityFooter$H */
    private class C0983H extends Handler {
        private C0983H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            try {
                if (message.what == 1) {
                    String str = "handleRefreshState";
                    QSSecurityFooter.this.handleRefreshState();
                } else if (message.what == 0) {
                    String str2 = "handleClick";
                    QSSecurityFooter.this.handleClick();
                }
            } catch (Throwable th) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error in ");
                sb.append(null);
                String sb2 = sb.toString();
                Log.w("QSSecurityFooter", sb2, th);
                QSSecurityFooter.this.mHost.warn(sb2, th);
            }
        }
    }

    /* renamed from: com.android.systemui.qs.QSSecurityFooter$VpnSpan */
    protected class VpnSpan extends ClickableSpan {
        public int hashCode() {
            return 314159257;
        }

        protected VpnSpan() {
        }

        public void onClick(View view) {
            Intent intent = new Intent("android.settings.VPN_SETTINGS");
            QSSecurityFooter.this.mDialog.dismiss();
            QSSecurityFooter.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }

        public boolean equals(Object obj) {
            return obj instanceof VpnSpan;
        }
    }

    static {
        Log.isLoggable("QSSecurityFooter", 3);
    }

    public QSSecurityFooter(QSPanel qSPanel, Context context) {
        View view = null;
        View inflate = LayoutInflater.from(context).inflate(C2013R$layout.quick_settings_footer, qSPanel, false);
        this.mRootView = inflate;
        inflate.setOnClickListener(this);
        this.mFooterText = (TextView) this.mRootView.findViewById(C2011R$id.footer_text);
        this.mFooterIcon = (ImageView) this.mRootView.findViewById(C2011R$id.footer_icon);
        this.mFooterIconId = C2010R$drawable.ic_info_outline;
        this.mContext = context;
        this.mMainHandler = new Handler(Looper.myLooper());
        this.mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        this.mSecurityController = (SecurityController) Dependency.get(SecurityController.class);
        this.mHandler = new C0983H((Looper) Dependency.get(Dependency.BG_LOOPER));
        if (qSPanel != null) {
            view = qSPanel.getDivider();
        }
        this.mDivider = view;
        this.mUm = (UserManager) this.mContext.getSystemService("user");
    }

    public void setHostEnvironment(QSTileHost qSTileHost) {
        this.mHost = qSTileHost;
    }

    public void setListening(boolean z) {
        if (z) {
            this.mSecurityController.addCallback(this.mCallback);
            refreshState();
            return;
        }
        this.mSecurityController.removeCallback(this.mCallback);
    }

    public void onConfigurationChanged() {
        FontSizeUtils.updateFontSize(this.mFooterText, C2009R$dimen.qs_tile_text_size);
    }

    public View getView() {
        return this.mRootView;
    }

    public boolean hasFooter() {
        return this.mRootView.getVisibility() != 8;
    }

    public void onClick(View view) {
        if (hasFooter()) {
            this.mHandler.sendEmptyMessage(0);
        }
    }

    /* access modifiers changed from: private */
    public void handleClick() {
        showDeviceMonitoringDialog();
        DevicePolicyEventLogger.createEvent(57).write();
    }

    public void showDeviceMonitoringDialog() {
        this.mHost.collapsePanels();
        createDialog();
    }

    public void refreshState() {
        this.mHandler.sendEmptyMessage(1);
    }

    /* access modifiers changed from: private */
    public void handleRefreshState() {
        boolean isDeviceManaged = this.mSecurityController.isDeviceManaged();
        UserInfo userInfo = this.mUm.getUserInfo(ActivityManager.getCurrentUser());
        boolean z = true;
        boolean z2 = UserManager.isDeviceInDemoMode(this.mContext) && userInfo != null && userInfo.isDemo();
        boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        boolean hasCACertInCurrentUser = this.mSecurityController.hasCACertInCurrentUser();
        boolean hasCACertInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        boolean isNetworkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        String primaryVpnName = this.mSecurityController.getPrimaryVpnName();
        String workProfileVpnName = this.mSecurityController.getWorkProfileVpnName();
        CharSequence deviceOwnerOrganizationName = this.mSecurityController.getDeviceOwnerOrganizationName();
        CharSequence workProfileOrganizationName = this.mSecurityController.getWorkProfileOrganizationName();
        if ((!isDeviceManaged || z2) && !hasCACertInCurrentUser && !hasCACertInWorkProfile && primaryVpnName == null && workProfileVpnName == null) {
            z = false;
        }
        this.mIsVisible = z;
        this.mFooterTextContent = getFooterText(isDeviceManaged, hasWorkProfile, hasCACertInCurrentUser, hasCACertInWorkProfile, isNetworkLoggingEnabled, primaryVpnName, workProfileVpnName, deviceOwnerOrganizationName, workProfileOrganizationName);
        int i = C2010R$drawable.ic_info_outline;
        if (!(primaryVpnName == null && workProfileVpnName == null)) {
            if (this.mSecurityController.isVpnBranded()) {
                i = C2010R$drawable.stat_sys_branded_vpn;
            } else {
                i = C2010R$drawable.stat_sys_vpn_ic;
            }
        }
        if (this.mFooterIconId != i) {
            this.mFooterIconId = i;
            this.mMainHandler.post(this.mUpdateIcon);
        }
        this.mMainHandler.post(this.mUpdateDisplayState);
    }

    /* access modifiers changed from: protected */
    public CharSequence getFooterText(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, String str, String str2, CharSequence charSequence, CharSequence charSequence2) {
        if (z) {
            if (z3 || z4 || z5) {
                if (charSequence == null) {
                    return this.mContext.getString(C2017R$string.quick_settings_disclosure_management_monitoring);
                }
                return this.mContext.getString(C2017R$string.quick_settings_disclosure_named_management_monitoring, new Object[]{charSequence});
            } else if (str == null || str2 == null) {
                if (str == null && str2 == null) {
                    if (charSequence == null) {
                        return this.mContext.getString(C2017R$string.quick_settings_disclosure_management);
                    }
                    return this.mContext.getString(C2017R$string.quick_settings_disclosure_named_management, new Object[]{charSequence});
                } else if (charSequence == null) {
                    Context context = this.mContext;
                    int i = C2017R$string.quick_settings_disclosure_management_named_vpn;
                    Object[] objArr = new Object[1];
                    if (str == null) {
                        str = str2;
                    }
                    objArr[0] = str;
                    return context.getString(i, objArr);
                } else {
                    Context context2 = this.mContext;
                    int i2 = C2017R$string.quick_settings_disclosure_named_management_named_vpn;
                    Object[] objArr2 = new Object[2];
                    objArr2[0] = charSequence;
                    if (str == null) {
                        str = str2;
                    }
                    objArr2[1] = str;
                    return context2.getString(i2, objArr2);
                }
            } else if (charSequence == null) {
                return this.mContext.getString(C2017R$string.quick_settings_disclosure_management_vpns);
            } else {
                return this.mContext.getString(C2017R$string.quick_settings_disclosure_named_management_vpns, new Object[]{charSequence});
            }
        } else if (z4) {
            if (charSequence2 == null) {
                return this.mContext.getString(C2017R$string.quick_settings_disclosure_managed_profile_monitoring);
            }
            return this.mContext.getString(C2017R$string.quick_settings_disclosure_named_managed_profile_monitoring, new Object[]{charSequence2});
        } else if (z3) {
            return this.mContext.getString(C2017R$string.quick_settings_disclosure_monitoring);
        } else {
            if (str != null && str2 != null) {
                return this.mContext.getString(C2017R$string.quick_settings_disclosure_vpns);
            }
            if (str2 != null) {
                return this.mContext.getString(C2017R$string.quick_settings_disclosure_managed_profile_named_vpn, new Object[]{str2});
            } else if (str == null) {
                return null;
            } else {
                if (z2) {
                    return this.mContext.getString(C2017R$string.quick_settings_disclosure_personal_profile_named_vpn, new Object[]{str});
                }
                return this.mContext.getString(C2017R$string.quick_settings_disclosure_named_vpn, new Object[]{str});
            }
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            Intent intent = new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS");
            this.mDialog.dismiss();
            this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
    }

    private void createDialog() {
        boolean isDeviceManaged = this.mSecurityController.isDeviceManaged();
        boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        CharSequence deviceOwnerOrganizationName = this.mSecurityController.getDeviceOwnerOrganizationName();
        boolean hasCACertInCurrentUser = this.mSecurityController.hasCACertInCurrentUser();
        boolean hasCACertInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        boolean isNetworkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        String primaryVpnName = this.mSecurityController.getPrimaryVpnName();
        String workProfileVpnName = this.mSecurityController.getWorkProfileVpnName();
        SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext);
        this.mDialog = systemUIDialog;
        systemUIDialog.requestWindowFeature(1);
        View inflate = LayoutInflater.from(new ContextThemeWrapper(this.mContext, C2018R$style.Theme_SystemUI_Dialog)).inflate(C2013R$layout.quick_settings_footer_dialog, null, false);
        this.mDialog.setView(inflate);
        this.mDialog.setButton(-1, getPositiveButton(), this);
        CharSequence managementMessage = getManagementMessage(isDeviceManaged, deviceOwnerOrganizationName);
        if (managementMessage == null) {
            inflate.findViewById(C2011R$id.device_management_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(C2011R$id.device_management_disclosures).setVisibility(0);
            ((TextView) inflate.findViewById(C2011R$id.device_management_warning)).setText(managementMessage);
            this.mDialog.setButton(-2, getSettingsButton(), this);
        }
        CharSequence caCertsMessage = getCaCertsMessage(isDeviceManaged, hasCACertInCurrentUser, hasCACertInWorkProfile);
        if (caCertsMessage == null) {
            inflate.findViewById(C2011R$id.ca_certs_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(C2011R$id.ca_certs_disclosures).setVisibility(0);
            TextView textView = (TextView) inflate.findViewById(C2011R$id.ca_certs_warning);
            textView.setText(caCertsMessage);
            textView.setMovementMethod(new LinkMovementMethod());
        }
        CharSequence networkLoggingMessage = getNetworkLoggingMessage(isNetworkLoggingEnabled);
        if (networkLoggingMessage == null) {
            inflate.findViewById(C2011R$id.network_logging_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(C2011R$id.network_logging_disclosures).setVisibility(0);
            ((TextView) inflate.findViewById(C2011R$id.network_logging_warning)).setText(networkLoggingMessage);
        }
        CharSequence vpnMessage = getVpnMessage(isDeviceManaged, hasWorkProfile, primaryVpnName, workProfileVpnName);
        if (vpnMessage == null) {
            inflate.findViewById(C2011R$id.vpn_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(C2011R$id.vpn_disclosures).setVisibility(0);
            TextView textView2 = (TextView) inflate.findViewById(C2011R$id.vpn_warning);
            textView2.setText(vpnMessage);
            textView2.setMovementMethod(new LinkMovementMethod());
        }
        configSubtitleVisibility(managementMessage != null, caCertsMessage != null, networkLoggingMessage != null, vpnMessage != null, inflate);
        this.mDialog.show();
        this.mDialog.getWindow().setLayout(-1, -2);
    }

    /* access modifiers changed from: protected */
    public void configSubtitleVisibility(boolean z, boolean z2, boolean z3, boolean z4, View view) {
        if (!z) {
            int i = 0;
            if (z2) {
                i = 1;
            }
            if (z3) {
                i++;
            }
            if (z4) {
                i++;
            }
            if (i == 1) {
                if (z2) {
                    view.findViewById(C2011R$id.ca_certs_subtitle).setVisibility(8);
                }
                if (z3) {
                    view.findViewById(C2011R$id.network_logging_subtitle).setVisibility(8);
                }
                if (z4) {
                    view.findViewById(C2011R$id.vpn_subtitle).setVisibility(8);
                }
            }
        }
    }

    private String getSettingsButton() {
        return this.mContext.getString(C2017R$string.monitoring_button_view_policies);
    }

    private String getPositiveButton() {
        return this.mContext.getString(C2017R$string.f31ok);
    }

    /* access modifiers changed from: protected */
    public CharSequence getManagementMessage(boolean z, CharSequence charSequence) {
        if (!z) {
            return null;
        }
        if (charSequence == null) {
            return this.mContext.getString(C2017R$string.monitoring_description_management);
        }
        return this.mContext.getString(C2017R$string.monitoring_description_named_management, new Object[]{charSequence});
    }

    /* access modifiers changed from: protected */
    public CharSequence getCaCertsMessage(boolean z, boolean z2, boolean z3) {
        if (!z2 && !z3) {
            return null;
        }
        if (z) {
            return this.mContext.getString(C2017R$string.monitoring_description_management_ca_certificate);
        }
        if (z3) {
            return this.mContext.getString(C2017R$string.monitoring_description_managed_profile_ca_certificate);
        }
        return this.mContext.getString(C2017R$string.monitoring_description_ca_certificate);
    }

    /* access modifiers changed from: protected */
    public CharSequence getNetworkLoggingMessage(boolean z) {
        if (!z) {
            return null;
        }
        return this.mContext.getString(C2017R$string.monitoring_description_management_network_logging);
    }

    /* access modifiers changed from: protected */
    public CharSequence getVpnMessage(boolean z, boolean z2, String str, String str2) {
        if (str == null && str2 == null) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (z) {
            if (str == null || str2 == null) {
                Context context = this.mContext;
                int i = C2017R$string.monitoring_description_named_vpn;
                Object[] objArr = new Object[1];
                if (str == null) {
                    str = str2;
                }
                objArr[0] = str;
                spannableStringBuilder.append(context.getString(i, objArr));
            } else {
                spannableStringBuilder.append(this.mContext.getString(C2017R$string.monitoring_description_two_named_vpns, new Object[]{str, str2}));
            }
        } else if (str != null && str2 != null) {
            spannableStringBuilder.append(this.mContext.getString(C2017R$string.monitoring_description_two_named_vpns, new Object[]{str, str2}));
        } else if (str2 != null) {
            spannableStringBuilder.append(this.mContext.getString(C2017R$string.monitoring_description_managed_profile_named_vpn, new Object[]{str2}));
        } else if (z2) {
            spannableStringBuilder.append(this.mContext.getString(C2017R$string.monitoring_description_personal_profile_named_vpn, new Object[]{str}));
        } else {
            spannableStringBuilder.append(this.mContext.getString(C2017R$string.monitoring_description_named_vpn, new Object[]{str}));
        }
        spannableStringBuilder.append(this.mContext.getString(C2017R$string.monitoring_description_vpn_settings_separator));
        spannableStringBuilder.append(this.mContext.getString(C2017R$string.monitoring_description_vpn_settings), new VpnSpan(), 0);
        return spannableStringBuilder;
    }
}
