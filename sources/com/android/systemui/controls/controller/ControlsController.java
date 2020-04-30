package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.os.UserHandle;
import android.service.controls.Control;
import android.service.controls.actions.ControlAction;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.UserAwareController;
import java.util.List;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsController.kt */
public interface ControlsController extends UserAwareController {

    /* compiled from: ControlsController.kt */
    public static final class DefaultImpls {
        public static void changeUser(ControlsController controlsController, UserHandle userHandle) {
            Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
            com.android.systemui.controls.UserAwareController.DefaultImpls.changeUser(controlsController, userHandle);
        }
    }

    /* compiled from: ControlsController.kt */
    public interface LoadData {
        List<ControlStatus> getAllControls();

        boolean getErrorOnLoad();

        List<String> getFavoritesIds();
    }

    void action(ComponentName componentName, ControlInfo controlInfo, ControlAction controlAction);

    void addFavorite(ComponentName componentName, CharSequence charSequence, ControlInfo controlInfo);

    boolean addSeedingFavoritesCallback(Consumer<Boolean> consumer);

    int countFavoritesForComponent(ComponentName componentName);

    boolean getAvailable();

    List<StructureInfo> getFavorites();

    List<StructureInfo> getFavoritesForComponent(ComponentName componentName);

    void onActionResponse(ComponentName componentName, String str, int i);

    void refreshStatus(ComponentName componentName, Control control);

    void seedFavoritesForComponent(ComponentName componentName, Consumer<Boolean> consumer);

    void subscribeToFavorites(StructureInfo structureInfo);

    void unsubscribe();
}
