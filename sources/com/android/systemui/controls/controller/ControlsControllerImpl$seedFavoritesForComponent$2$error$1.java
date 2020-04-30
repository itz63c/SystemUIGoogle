package com.android.systemui.controls.controller;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$seedFavoritesForComponent$2$error$1 implements Runnable {
    final /* synthetic */ ControlsControllerImpl$seedFavoritesForComponent$2 this$0;

    ControlsControllerImpl$seedFavoritesForComponent$2$error$1(ControlsControllerImpl$seedFavoritesForComponent$2 controlsControllerImpl$seedFavoritesForComponent$2) {
        this.this$0 = controlsControllerImpl$seedFavoritesForComponent$2;
    }

    public final void run() {
        this.this$0.$callback.accept(Boolean.FALSE);
        this.this$0.this$0.endSeedingCall(false);
    }
}
