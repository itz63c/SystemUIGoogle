package com.android.systemui.assist;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.slice.Clock;
import com.android.internal.app.AssistUtils;
import com.android.systemui.statusbar.NavigationBarController;
import java.util.EnumMap;
import java.util.Map;

public abstract class AssistModule {
    static Handler provideBackgroundHandler() {
        HandlerThread handlerThread = new HandlerThread("AssistHandleThread");
        handlerThread.start();
        return handlerThread.getThreadHandler();
    }

    static Map<AssistHandleBehavior, BehaviorController> provideAssistHandleBehaviorControllerMap(AssistHandleOffBehavior assistHandleOffBehavior, AssistHandleLikeHomeBehavior assistHandleLikeHomeBehavior, AssistHandleReminderExpBehavior assistHandleReminderExpBehavior) {
        EnumMap enumMap = new EnumMap(AssistHandleBehavior.class);
        enumMap.put(AssistHandleBehavior.OFF, assistHandleOffBehavior);
        enumMap.put(AssistHandleBehavior.LIKE_HOME, assistHandleLikeHomeBehavior);
        enumMap.put(AssistHandleBehavior.REMINDER_EXP, assistHandleReminderExpBehavior);
        return enumMap;
    }

    static AssistHandleViewController provideAssistHandleViewController(NavigationBarController navigationBarController) {
        return navigationBarController.getAssistHandlerViewController();
    }

    static AssistUtils provideAssistUtils(Context context) {
        return new AssistUtils(context);
    }

    static Clock provideSystemClock() {
        return $$Lambda$WyKlJnsW9STKD48w13qf39mFKI.INSTANCE;
    }
}
