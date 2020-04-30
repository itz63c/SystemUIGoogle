package com.android.systemui.controls.controller;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.service.controls.Control;
import android.service.controls.Control.StatelessBuilder;
import android.service.controls.actions.ControlAction;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlsController.DefaultImpls;
import com.android.systemui.controls.controller.ControlsController.LoadData;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.p004ui.ControlsUiController;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl implements Dumpable, ControlsController {
    private static final Uri URI = Secure.getUriFor("systemui.controls_available");
    /* access modifiers changed from: private */
    public boolean available;
    /* access modifiers changed from: private */
    public final ControlsBindingController bindingController;
    private final BroadcastDispatcher broadcastDispatcher;
    /* access modifiers changed from: private */
    public final Context context;
    /* access modifiers changed from: private */
    public UserHandle currentUser = UserHandle.of(ActivityManager.getCurrentUser());
    /* access modifiers changed from: private */
    public final DelayableExecutor executor;
    /* access modifiers changed from: private */
    public final ControlsControllerImpl$listingCallback$1 listingCallback;
    /* access modifiers changed from: private */
    public final ControlsListingController listingController;
    /* access modifiers changed from: private */
    public Runnable loadCanceller;
    /* access modifiers changed from: private */
    public final ControlsFavoritePersistenceWrapper persistenceWrapper;
    /* access modifiers changed from: private */
    public final List<Consumer<Boolean>> seedingCallbacks = new ArrayList();
    /* access modifiers changed from: private */
    public boolean seedingInProgress;
    private final ContentObserver settingObserver;
    private final ControlsUiController uiController;
    /* access modifiers changed from: private */
    public boolean userChanging = true;
    private final ControlsControllerImpl$userSwitchReceiver$1 userSwitchReceiver;

    @VisibleForTesting
    public static /* synthetic */ void settingObserver$annotations() {
    }

    public ControlsControllerImpl(Context context2, DelayableExecutor delayableExecutor, ControlsUiController controlsUiController, ControlsBindingController controlsBindingController, ControlsListingController controlsListingController, BroadcastDispatcher broadcastDispatcher2, Optional<ControlsFavoritePersistenceWrapper> optional, DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(delayableExecutor, "executor");
        Intrinsics.checkParameterIsNotNull(controlsUiController, "uiController");
        Intrinsics.checkParameterIsNotNull(controlsBindingController, "bindingController");
        Intrinsics.checkParameterIsNotNull(controlsListingController, "listingController");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkParameterIsNotNull(optional, "optionalWrapper");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.context = context2;
        this.executor = delayableExecutor;
        this.uiController = controlsUiController;
        this.bindingController = controlsBindingController;
        this.listingController = controlsListingController;
        this.broadcastDispatcher = broadcastDispatcher2;
        boolean z = true;
        if (Secure.getIntForUser(getContentResolver(), "systemui.controls_available", 1, getCurrentUserId()) == 0) {
            z = false;
        }
        this.available = z;
        this.persistenceWrapper = (ControlsFavoritePersistenceWrapper) optional.orElseGet(new ControlsControllerImpl$persistenceWrapper$1(this));
        this.userSwitchReceiver = new ControlsControllerImpl$userSwitchReceiver$1(this);
        this.settingObserver = new ControlsControllerImpl$settingObserver$1(this, null);
        this.listingCallback = new ControlsControllerImpl$listingCallback$1(this);
        String name = ControlsControllerImpl.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        resetFavorites(getAvailable());
        this.userChanging = false;
        BroadcastDispatcher broadcastDispatcher3 = this.broadcastDispatcher;
        ControlsControllerImpl$userSwitchReceiver$1 controlsControllerImpl$userSwitchReceiver$1 = this.userSwitchReceiver;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.USER_SWITCHED");
        DelayableExecutor delayableExecutor2 = this.executor;
        UserHandle userHandle = UserHandle.ALL;
        Intrinsics.checkExpressionValueIsNotNull(userHandle, "UserHandle.ALL");
        broadcastDispatcher3.registerReceiver(controlsControllerImpl$userSwitchReceiver$1, intentFilter, delayableExecutor2, userHandle);
        getContentResolver().registerContentObserver(URI, false, this.settingObserver, -1);
    }

    public void changeUser(UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
        DefaultImpls.changeUser(this, userHandle);
    }

    public int getCurrentUserId() {
        UserHandle userHandle = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(userHandle, "currentUser");
        return userHandle.getIdentifier();
    }

    /* access modifiers changed from: private */
    public final ContentResolver getContentResolver() {
        ContentResolver contentResolver = this.context.getContentResolver();
        Intrinsics.checkExpressionValueIsNotNull(contentResolver, "context.contentResolver");
        return contentResolver;
    }

    public boolean getAvailable() {
        return this.available;
    }

    /* access modifiers changed from: private */
    public final void setValuesForUser(UserHandle userHandle) {
        StringBuilder sb = new StringBuilder();
        sb.append("Changing to user: ");
        sb.append(userHandle);
        Log.d("ControlsControllerImpl", sb.toString());
        this.currentUser = userHandle;
        Context createContextAsUser = this.context.createContextAsUser(userHandle, 0);
        Intrinsics.checkExpressionValueIsNotNull(createContextAsUser, "userContext");
        File buildPath = Environment.buildPath(createContextAsUser.getFilesDir(), new String[]{"controls_favorites.xml"});
        ControlsFavoritePersistenceWrapper controlsFavoritePersistenceWrapper = this.persistenceWrapper;
        Intrinsics.checkExpressionValueIsNotNull(buildPath, "fileName");
        controlsFavoritePersistenceWrapper.changeFile(buildPath);
        boolean z = true;
        if (Secure.getIntForUser(getContentResolver(), "systemui.controls_available", 1, userHandle.getIdentifier()) == 0) {
            z = false;
        }
        this.available = z;
        resetFavorites(getAvailable());
        this.bindingController.changeUser(userHandle);
        this.listingController.changeUser(userHandle);
        this.userChanging = false;
    }

    /* access modifiers changed from: private */
    public final void resetFavorites(boolean z) {
        Favorites.INSTANCE.clear();
        if (z) {
            Favorites.INSTANCE.load(this.persistenceWrapper.readFavorites());
            this.listingController.addCallback(this.listingCallback);
        }
    }

    private final boolean confirmAvailability() {
        String str = "ControlsControllerImpl";
        if (this.userChanging) {
            Log.w(str, "Controls not available while user is changing");
            return false;
        } else if (getAvailable()) {
            return true;
        } else {
            Log.d(str, "Controls not available");
            return false;
        }
    }

    public void loadForComponent(ComponentName componentName, Consumer<LoadData> consumer) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(consumer, "dataCallback");
        if (!confirmAvailability()) {
            if (this.userChanging) {
                this.loadCanceller = this.executor.executeDelayed(new ControlsControllerImpl$loadForComponent$1(this, componentName, consumer), 500, TimeUnit.MILLISECONDS);
            } else {
                consumer.accept(ControlsControllerKt.createLoadDataObject(CollectionsKt__CollectionsKt.emptyList(), CollectionsKt__CollectionsKt.emptyList(), true));
            }
            return;
        }
        this.loadCanceller = this.bindingController.bindAndLoad(componentName, new ControlsControllerImpl$loadForComponent$2(this, componentName, consumer));
    }

    public boolean addSeedingFavoritesCallback(Consumer<Boolean> consumer) {
        Intrinsics.checkParameterIsNotNull(consumer, "callback");
        if (!this.seedingInProgress) {
            return false;
        }
        this.executor.execute(new ControlsControllerImpl$addSeedingFavoritesCallback$1(this, consumer));
        return true;
    }

    public void seedFavoritesForComponent(ComponentName componentName, Consumer<Boolean> consumer) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(consumer, "callback");
        StringBuilder sb = new StringBuilder();
        sb.append("Beginning request to seed favorites for: ");
        sb.append(componentName);
        Log.i("ControlsControllerImpl", sb.toString());
        if (!confirmAvailability()) {
            if (this.userChanging) {
                this.executor.executeDelayed(new ControlsControllerImpl$seedFavoritesForComponent$1(this, componentName, consumer), 500, TimeUnit.MILLISECONDS);
            } else {
                consumer.accept(Boolean.FALSE);
            }
            return;
        }
        this.seedingInProgress = true;
        this.bindingController.bindAndLoadSuggested(componentName, new ControlsControllerImpl$seedFavoritesForComponent$2(this, componentName, consumer));
    }

    /* access modifiers changed from: private */
    public final void endSeedingCall(boolean z) {
        this.seedingInProgress = false;
        for (Consumer accept : this.seedingCallbacks) {
            accept.accept(Boolean.valueOf(z));
        }
        this.seedingCallbacks.clear();
    }

    public void cancelLoad() {
        Runnable runnable = this.loadCanceller;
        if (runnable != null) {
            this.executor.execute(runnable);
        }
    }

    static /* synthetic */ ControlStatus createRemovedStatus$default(ControlsControllerImpl controlsControllerImpl, ComponentName componentName, ControlInfo controlInfo, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = true;
        }
        return controlsControllerImpl.createRemovedStatus(componentName, controlInfo, z);
    }

    /* access modifiers changed from: private */
    public final ControlStatus createRemovedStatus(ComponentName componentName, ControlInfo controlInfo, boolean z) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(componentName.getPackageName());
        Control build = new StatelessBuilder(controlInfo.getControlId(), PendingIntent.getActivity(this.context, componentName.hashCode(), intent, 0)).setTitle(controlInfo.getControlTitle()).setDeviceType(controlInfo.getDeviceType()).build();
        Intrinsics.checkExpressionValueIsNotNull(build, "control");
        return new ControlStatus(build, componentName, true, z);
    }

    public void subscribeToFavorites(StructureInfo structureInfo) {
        Intrinsics.checkParameterIsNotNull(structureInfo, "structureInfo");
        if (confirmAvailability()) {
            this.bindingController.subscribe(structureInfo);
        }
    }

    public void unsubscribe() {
        if (confirmAvailability()) {
            this.bindingController.unsubscribe();
        }
    }

    public void addFavorite(ComponentName componentName, CharSequence charSequence, ControlInfo controlInfo) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(charSequence, "structureName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "controlInfo");
        if (confirmAvailability()) {
            this.executor.execute(new ControlsControllerImpl$addFavorite$1(this, componentName, charSequence, controlInfo));
        }
    }

    public void replaceFavoritesForStructure(StructureInfo structureInfo) {
        Intrinsics.checkParameterIsNotNull(structureInfo, "structureInfo");
        if (confirmAvailability()) {
            this.executor.execute(new ControlsControllerImpl$replaceFavoritesForStructure$1(this, structureInfo));
        }
    }

    public void refreshStatus(ComponentName componentName, Control control) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(control, "control");
        if (!confirmAvailability()) {
            Log.d("ControlsControllerImpl", "Controls not available");
            return;
        }
        this.executor.execute(new ControlsControllerImpl$refreshStatus$1(this, componentName, control));
        this.uiController.onRefreshState(componentName, CollectionsKt__CollectionsJVMKt.listOf(control));
    }

    public void onActionResponse(ComponentName componentName, String str, int i) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        if (confirmAvailability()) {
            this.uiController.onActionResponse(componentName, str, i);
        }
    }

    public void action(ComponentName componentName, ControlInfo controlInfo, ControlAction controlAction) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "controlInfo");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        if (confirmAvailability()) {
            this.bindingController.action(componentName, controlInfo, controlAction);
        }
    }

    public List<StructureInfo> getFavorites() {
        return Favorites.INSTANCE.getAllStructures();
    }

    public int countFavoritesForComponent(ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        return Favorites.INSTANCE.getControlsForComponent(componentName).size();
    }

    public List<StructureInfo> getFavoritesForComponent(ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        return Favorites.INSTANCE.getStructuresForComponent(componentName);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        printWriter.println("ControlsController state:");
        StringBuilder sb = new StringBuilder();
        sb.append("  Available: ");
        sb.append(getAvailable());
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  Changing users: ");
        sb2.append(this.userChanging);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  Current user: ");
        UserHandle userHandle = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(userHandle, "currentUser");
        sb3.append(userHandle.getIdentifier());
        printWriter.println(sb3.toString());
        printWriter.println("  Favorites:");
        for (StructureInfo structureInfo : Favorites.INSTANCE.getAllStructures()) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("    ");
            sb4.append(structureInfo);
            printWriter.println(sb4.toString());
            for (ControlInfo controlInfo : structureInfo.getControls()) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("      ");
                sb5.append(controlInfo);
                printWriter.println(sb5.toString());
            }
        }
        printWriter.println(this.bindingController.toString());
    }

    /* access modifiers changed from: private */
    public final Set<String> findRemoved(Set<String> set, List<Control> list) {
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (Control controlId : list) {
            arrayList.add(controlId.getControlId());
        }
        return SetsKt___SetsKt.minus(set, arrayList);
    }
}
