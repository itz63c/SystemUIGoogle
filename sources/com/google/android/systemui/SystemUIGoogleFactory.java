package com.google.android.systemui;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.SystemUIFactory.ContextHolder;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.SystemUIRootComponent;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;
import com.google.android.systemui.dagger.DaggerSystemUIGoogleRootComponent;
import com.google.android.systemui.dagger.DaggerSystemUIGoogleRootComponent.Builder;
import com.google.android.systemui.screenshot.ScreenshotNotificationSmartActionsProviderGoogle;
import java.util.concurrent.Executor;

public class SystemUIGoogleFactory extends SystemUIFactory {
    /* access modifiers changed from: protected */
    public SystemUIRootComponent buildSystemUIRootComponent(Context context) {
        Builder builder = DaggerSystemUIGoogleRootComponent.builder();
        builder.dependencyProvider(new DependencyProvider());
        builder.contextHolder(new ContextHolder(context));
        return builder.build();
    }

    public ScreenshotNotificationSmartActionsProvider createScreenshotNotificationSmartActionsProvider(Context context, Executor executor, Handler handler) {
        return new ScreenshotNotificationSmartActionsProviderGoogle(context, executor, handler);
    }
}
