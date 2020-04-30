package com.android.systemui.globalactions;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.systemui.SystemUI;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.plugins.GlobalActions.GlobalActionsManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.ExtensionController.Extension;
import com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.inject.Provider;

public class GlobalActionsComponent extends SystemUI implements Callbacks, GlobalActionsManager {
    private IStatusBarService mBarService;
    private final CommandQueue mCommandQueue;
    private Extension<GlobalActions> mExtension;
    private final ExtensionController mExtensionController;
    private final Provider<GlobalActions> mGlobalActionsProvider;
    private GlobalActions mPlugin;

    public GlobalActionsComponent(Context context, CommandQueue commandQueue, ExtensionController extensionController, Provider<GlobalActions> provider) {
        super(context);
        this.mCommandQueue = commandQueue;
        this.mExtensionController = extensionController;
        this.mGlobalActionsProvider = provider;
    }

    public void start() {
        Class<GlobalActions> cls = GlobalActions.class;
        this.mBarService = Stub.asInterface(ServiceManager.getService("statusbar"));
        ExtensionBuilder newExtension = this.mExtensionController.newExtension(cls);
        newExtension.withPlugin(cls);
        Provider<GlobalActions> provider = this.mGlobalActionsProvider;
        Objects.requireNonNull(provider);
        newExtension.withDefault(new Supplier() {
            public final Object get() {
                return (GlobalActions) Provider.this.get();
            }
        });
        newExtension.withCallback(new Consumer() {
            public final void accept(Object obj) {
                GlobalActionsComponent.this.onExtensionCallback((GlobalActions) obj);
            }
        });
        Extension<GlobalActions> build = newExtension.build();
        this.mExtension = build;
        this.mPlugin = (GlobalActions) build.get();
        this.mCommandQueue.addCallback((Callbacks) this);
    }

    /* access modifiers changed from: private */
    public void onExtensionCallback(GlobalActions globalActions) {
        GlobalActions globalActions2 = this.mPlugin;
        if (globalActions2 != null) {
            globalActions2.destroy();
        }
        this.mPlugin = globalActions;
    }

    public void handleShowShutdownUi(boolean z, String str) {
        ((GlobalActions) this.mExtension.get()).showShutdownUi(z, str);
    }

    public void handleShowGlobalActionsMenu() {
        ((GlobalActions) this.mExtension.get()).showGlobalActions(this);
    }

    public void onGlobalActionsShown() {
        try {
            this.mBarService.onGlobalActionsShown();
        } catch (RemoteException unused) {
        }
    }

    public void onGlobalActionsHidden() {
        try {
            this.mBarService.onGlobalActionsHidden();
        } catch (RemoteException unused) {
        }
    }

    public void shutdown() {
        try {
            this.mBarService.shutdown();
        } catch (RemoteException unused) {
        }
    }

    public void reboot(boolean z) {
        try {
            this.mBarService.reboot(z);
        } catch (RemoteException unused) {
        }
    }
}
