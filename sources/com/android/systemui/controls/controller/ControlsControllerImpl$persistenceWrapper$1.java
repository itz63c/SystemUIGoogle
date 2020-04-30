package com.android.systemui.controls.controller;

import android.os.Environment;
import java.io.File;
import java.util.function.Supplier;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$persistenceWrapper$1<T> implements Supplier<ControlsFavoritePersistenceWrapper> {
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$persistenceWrapper$1(ControlsControllerImpl controlsControllerImpl) {
        this.this$0 = controlsControllerImpl;
    }

    public final ControlsFavoritePersistenceWrapper get() {
        File buildPath = Environment.buildPath(this.this$0.context.getFilesDir(), new String[]{"controls_favorites.xml"});
        Intrinsics.checkExpressionValueIsNotNull(buildPath, "Environment.buildPath(\n …LE_NAME\n                )");
        return new ControlsFavoritePersistenceWrapper(buildPath, this.this$0.executor);
    }
}
