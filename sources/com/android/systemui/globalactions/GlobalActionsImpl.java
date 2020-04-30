package com.android.systemui.globalactions;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.settingslib.Utils;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2018R$style;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.plugins.GlobalActions.GlobalActionsManager;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.ExtensionController.Extension;
import com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.Lazy;

public class GlobalActionsImpl implements GlobalActions, Callbacks {
    private final BlurUtils mBlurUtils;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController = ((DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class));
    private boolean mDisabled;
    private GlobalActionsDialog mGlobalActionsDialog;
    private final Lazy<GlobalActionsDialog> mGlobalActionsDialogLazy;
    private final KeyguardStateController mKeyguardStateController = ((KeyguardStateController) Dependency.get(KeyguardStateController.class));
    private final Extension<GlobalActionsPanelPlugin> mPanelExtension;

    public GlobalActionsImpl(Context context, CommandQueue commandQueue, Lazy<GlobalActionsDialog> lazy, BlurUtils blurUtils) {
        Class<GlobalActionsPanelPlugin> cls = GlobalActionsPanelPlugin.class;
        this.mContext = context;
        this.mGlobalActionsDialogLazy = lazy;
        this.mCommandQueue = commandQueue;
        this.mBlurUtils = blurUtils;
        commandQueue.addCallback((Callbacks) this);
        ExtensionBuilder newExtension = ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(cls);
        newExtension.withPlugin(cls);
        this.mPanelExtension = newExtension.build();
    }

    public void destroy() {
        this.mCommandQueue.removeCallback((Callbacks) this);
        GlobalActionsDialog globalActionsDialog = this.mGlobalActionsDialog;
        if (globalActionsDialog != null) {
            globalActionsDialog.destroy();
            this.mGlobalActionsDialog = null;
        }
    }

    public void showGlobalActions(GlobalActionsManager globalActionsManager) {
        if (!this.mDisabled) {
            GlobalActionsDialog globalActionsDialog = (GlobalActionsDialog) this.mGlobalActionsDialogLazy.get();
            this.mGlobalActionsDialog = globalActionsDialog;
            globalActionsDialog.showDialog(this.mKeyguardStateController.isShowing(), this.mDeviceProvisionedController.isDeviceProvisioned(), (GlobalActionsPanelPlugin) this.mPanelExtension.get());
            ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).requestFaceAuth();
        }
    }

    public void showShutdownUi(boolean z, String str) {
        ScrimDrawable scrimDrawable = new ScrimDrawable();
        Dialog dialog = new Dialog(this.mContext, C2018R$style.Theme_SystemUI_Dialog_GlobalActions);
        Window window = dialog.getWindow();
        window.requestFeature(1);
        LayoutParams attributes = window.getAttributes();
        attributes.systemUiVisibility |= 1792;
        window.getDecorView();
        window.getAttributes().width = -1;
        window.getAttributes().height = -1;
        window.getAttributes().layoutInDisplayCutoutMode = 3;
        window.setType(2020);
        window.getAttributes().setFitInsetsTypes(0);
        window.clearFlags(2);
        window.addFlags(17629472);
        window.setBackgroundDrawable(scrimDrawable);
        window.setWindowAnimations(16973828);
        dialog.setContentView(17367295);
        dialog.setCancelable(false);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, C2006R$attr.wallpaperTextColor);
        ((ProgressBar) dialog.findViewById(16908301)).getIndeterminateDrawable().setTint(colorAttrDefaultColor);
        TextView textView = (TextView) dialog.findViewById(16908308);
        TextView textView2 = (TextView) dialog.findViewById(16908309);
        textView.setTextColor(colorAttrDefaultColor);
        textView2.setTextColor(colorAttrDefaultColor);
        textView2.setText(getRebootMessage(z, str));
        String reasonMessage = getReasonMessage(str);
        if (reasonMessage != null) {
            textView.setVisibility(0);
            textView.setText(reasonMessage);
        }
        if (this.mBlurUtils.supportsBlursOnWindows()) {
            scrimDrawable.setAlpha(137);
            this.mBlurUtils.applyBlur(dialog.getWindow().getDecorView().getViewRootImpl(), this.mBlurUtils.blurRadiusOfRatio(1.0f));
        } else {
            scrimDrawable.setAlpha(242);
        }
        dialog.show();
    }

    private int getRebootMessage(boolean z, String str) {
        if (str != null && str.startsWith("recovery-update")) {
            return 17040996;
        }
        if ((str == null || !str.equals("recovery")) && !z) {
            return 17041141;
        }
        return 17040992;
    }

    private String getReasonMessage(String str) {
        if (str != null && str.startsWith("recovery-update")) {
            return this.mContext.getString(17040997);
        }
        if (str == null || !str.equals("recovery")) {
            return null;
        }
        return this.mContext.getString(17040993);
    }

    public void disable(int i, int i2, int i3, boolean z) {
        boolean z2 = (i3 & 8) != 0;
        if (i == this.mContext.getDisplayId() && z2 != this.mDisabled) {
            this.mDisabled = z2;
            if (z2) {
                GlobalActionsDialog globalActionsDialog = this.mGlobalActionsDialog;
                if (globalActionsDialog != null) {
                    globalActionsDialog.dismissDialog();
                }
            }
        }
    }
}
