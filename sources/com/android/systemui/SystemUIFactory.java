package com.android.systemui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.dagger.DaggerSystemUIRootComponent;
import com.android.systemui.dagger.DaggerSystemUIRootComponent.Builder;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.SystemUIRootComponent;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.phone.KeyguardBouncer.BouncerExpansionCallback;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.concurrent.Executor;

public class SystemUIFactory {
    static SystemUIFactory mFactory;
    private SystemUIRootComponent mRootComponent;

    public static class ContextHolder {
        private Context mContext;

        public ContextHolder(Context context) {
            this.mContext = context;
        }

        public Context provideContext() {
            return this.mContext;
        }
    }

    public static <T extends SystemUIFactory> T getInstance() {
        return mFactory;
    }

    public static void createFromConfig(Context context) {
        if (mFactory == null) {
            String string = context.getString(C2017R$string.config_systemUIFactoryComponent);
            if (string == null || string.length() == 0) {
                throw new RuntimeException("No SystemUIFactory component configured");
            }
            try {
                SystemUIFactory systemUIFactory = (SystemUIFactory) context.getClassLoader().loadClass(string).newInstance();
                mFactory = systemUIFactory;
                systemUIFactory.init(context);
            } catch (Throwable th) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error creating SystemUIFactory component: ");
                sb.append(string);
                Log.w("SystemUIFactory", sb.toString(), th);
                throw new RuntimeException(th);
            }
        }
    }

    @VisibleForTesting
    static void cleanup() {
        mFactory = null;
    }

    private void init(Context context) {
        this.mRootComponent = buildSystemUIRootComponent(context);
        Dependency dependency = new Dependency();
        this.mRootComponent.createDependency().createSystemUI(dependency);
        dependency.start();
    }

    /* access modifiers changed from: protected */
    public SystemUIRootComponent buildSystemUIRootComponent(Context context) {
        Builder builder = DaggerSystemUIRootComponent.builder();
        builder.dependencyProvider(new DependencyProvider());
        builder.contextHolder(new ContextHolder(context));
        return builder.build();
    }

    public SystemUIRootComponent getRootComponent() {
        return this.mRootComponent;
    }

    public ScreenshotNotificationSmartActionsProvider createScreenshotNotificationSmartActionsProvider(Context context, Executor executor, Handler handler) {
        return new ScreenshotNotificationSmartActionsProvider();
    }

    public KeyguardBouncer createKeyguardBouncer(Context context, ViewMediatorCallback viewMediatorCallback, LockPatternUtils lockPatternUtils, ViewGroup viewGroup, DismissCallbackRegistry dismissCallbackRegistry, BouncerExpansionCallback bouncerExpansionCallback, KeyguardStateController keyguardStateController, FalsingManager falsingManager, KeyguardBypassController keyguardBypassController) {
        KeyguardBouncer keyguardBouncer = new KeyguardBouncer(context, viewMediatorCallback, lockPatternUtils, viewGroup, dismissCallbackRegistry, falsingManager, bouncerExpansionCallback, keyguardStateController, (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class), keyguardBypassController, new Handler(Looper.getMainLooper()));
        return keyguardBouncer;
    }

    public NotificationIconAreaController createNotificationIconAreaController(Context context, StatusBar statusBar, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardBypassController keyguardBypassController, StatusBarStateController statusBarStateController) {
        NotificationIconAreaController notificationIconAreaController = new NotificationIconAreaController(context, statusBar, statusBarStateController, notificationWakeUpCoordinator, keyguardBypassController, (NotificationMediaManager) Dependency.get(NotificationMediaManager.class), (NotificationListener) Dependency.get(NotificationListener.class), (DozeParameters) Dependency.get(DozeParameters.class));
        return notificationIconAreaController;
    }
}
