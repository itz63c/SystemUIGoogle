package com.google.android.systemui.assist.uihints;

import android.content.ComponentName;
import com.android.internal.app.AssistUtils;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfo;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import java.util.HashSet;
import java.util.Set;

public class AssistantPresenceHandler implements ConfigInfoListener {
    private final AssistUtils mAssistUtils;
    private final Set<AssistantPresenceChangeListener> mAssistantPresenceChangeListeners = new HashSet();
    private boolean mGoogleIsAssistant;
    private boolean mNgaIsAssistant;
    private boolean mSysUiIsNgaUi;
    private final Set<SysUiIsNgaUiChangeListener> mSysUiIsNgaUiChangeListeners = new HashSet();

    public interface AssistantPresenceChangeListener {
        void onAssistantPresenceChanged(boolean z, boolean z2);
    }

    interface SysUiIsNgaUiChangeListener {
        void onSysUiIsNgaUiChanged(boolean z);
    }

    AssistantPresenceHandler(AssistUtils assistUtils) {
        this.mAssistUtils = assistUtils;
    }

    public void onConfigInfo(ConfigInfo configInfo) {
        updateAssistantPresence(fetchIsGoogleAssistant(), configInfo.ngaIsAssistant, configInfo.sysUiIsNgaUi);
    }

    public void registerAssistantPresenceChangeListener(AssistantPresenceChangeListener assistantPresenceChangeListener) {
        this.mAssistantPresenceChangeListeners.add(assistantPresenceChangeListener);
    }

    public void registerSysUiIsNgaUiChangeListener(SysUiIsNgaUiChangeListener sysUiIsNgaUiChangeListener) {
        this.mSysUiIsNgaUiChangeListeners.add(sysUiIsNgaUiChangeListener);
    }

    public void requestAssistantPresenceUpdate() {
        updateAssistantPresence(fetchIsGoogleAssistant(), this.mNgaIsAssistant, this.mSysUiIsNgaUi);
    }

    public boolean isSysUiNgaUi() {
        return this.mSysUiIsNgaUi;
    }

    public boolean isNgaAssistant() {
        return this.mNgaIsAssistant;
    }

    private void updateAssistantPresence(boolean z, boolean z2, boolean z3) {
        boolean z4 = true;
        boolean z5 = z && z2;
        if (!z5 || !z3) {
            z4 = false;
        }
        if (!(this.mGoogleIsAssistant == z && this.mNgaIsAssistant == z5)) {
            this.mGoogleIsAssistant = z;
            this.mNgaIsAssistant = z5;
            for (AssistantPresenceChangeListener onAssistantPresenceChanged : this.mAssistantPresenceChangeListeners) {
                onAssistantPresenceChanged.onAssistantPresenceChanged(this.mGoogleIsAssistant, this.mNgaIsAssistant);
            }
        }
        if (this.mSysUiIsNgaUi != z4) {
            this.mSysUiIsNgaUi = z4;
            for (SysUiIsNgaUiChangeListener onSysUiIsNgaUiChanged : this.mSysUiIsNgaUiChangeListeners) {
                onSysUiIsNgaUiChanged.onSysUiIsNgaUiChanged(this.mSysUiIsNgaUi);
            }
        }
    }

    private boolean fetchIsGoogleAssistant() {
        ComponentName assistComponentForUser = this.mAssistUtils.getAssistComponentForUser(-2);
        if (assistComponentForUser != null) {
            if ("com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService".equals(assistComponentForUser.flattenToString())) {
                return true;
            }
        }
        return false;
    }
}
