package com.google.android.systemui.columbus.actions;

import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UserSelectedAction.kt */
final class UserSelectedAction$propertiesChangedListener$1 implements OnPropertiesChangedListener {
    final /* synthetic */ UserSelectedAction this$0;

    UserSelectedAction$propertiesChangedListener$1(UserSelectedAction userSelectedAction) {
        this.this$0 = userSelectedAction;
    }

    public final void onPropertiesChanged(Properties properties) {
        Intrinsics.checkExpressionValueIsNotNull(properties, "properties");
        String str = "systemui_google_columbus_user_action";
        if (properties.getKeyset().contains(str)) {
            Action access$getSelectedAction = this.this$0.getSelectedAction(properties.getString(str, "assistant"));
            if (!Intrinsics.areEqual((Object) access$getSelectedAction, (Object) this.this$0.selectedAction)) {
                this.this$0.selectedAction.onProgress(0, null);
                this.this$0.selectedAction.updateFeedbackEffects(0, null);
                this.this$0.selectedAction = access$getSelectedAction;
                StringBuilder sb = new StringBuilder();
                sb.append("User Action selected: ");
                sb.append(this.this$0.selectedAction);
                Log.i("Columbus/SelectedAction", sb.toString());
                this.this$0.notifyListener();
            }
        }
    }
}
