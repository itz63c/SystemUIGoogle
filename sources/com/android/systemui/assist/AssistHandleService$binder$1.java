package com.android.systemui.assist;

import com.android.systemui.assist.IAssistHandleService.Stub;

/* compiled from: AssistHandleService.kt */
public final class AssistHandleService$binder$1 extends Stub {
    final /* synthetic */ AssistHandleService this$0;

    AssistHandleService$binder$1(AssistHandleService assistHandleService) {
        this.this$0 = assistHandleService;
    }

    public void requestAssistHandles() {
        ((AssistManager) this.this$0.assistManager.get()).requestAssistHandles();
    }
}
