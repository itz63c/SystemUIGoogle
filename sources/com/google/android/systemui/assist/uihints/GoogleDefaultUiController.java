package com.google.android.systemui.assist.uihints;

import android.content.Context;
import android.view.LayoutInflater;
import com.android.systemui.C2013R$layout;
import com.android.systemui.assist.p003ui.DefaultUiController;

public class GoogleDefaultUiController extends DefaultUiController {
    public GoogleDefaultUiController(Context context) {
        super(context);
        context.getResources();
        setGoogleAssistant(false);
        AssistantInvocationLightsView assistantInvocationLightsView = (AssistantInvocationLightsView) LayoutInflater.from(context).inflate(C2013R$layout.invocation_lights, this.mRoot, false);
        this.mInvocationLightsView = assistantInvocationLightsView;
        this.mRoot.addView(assistantInvocationLightsView);
    }

    public void setGoogleAssistant(boolean z) {
        ((AssistantInvocationLightsView) this.mInvocationLightsView).setGoogleAssistant(z);
    }
}
