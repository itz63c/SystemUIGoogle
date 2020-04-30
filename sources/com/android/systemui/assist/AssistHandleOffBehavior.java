package com.android.systemui.assist;

import android.content.Context;

final class AssistHandleOffBehavior implements BehaviorController {
    AssistHandleOffBehavior() {
    }

    public void onModeActivated(Context context, AssistHandleCallbacks assistHandleCallbacks) {
        assistHandleCallbacks.hide();
    }
}
