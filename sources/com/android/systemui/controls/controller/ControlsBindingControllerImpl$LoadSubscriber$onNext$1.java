package com.android.systemui.controls.controller;

import android.service.controls.Control;

/* compiled from: ControlsBindingControllerImpl.kt */
final class ControlsBindingControllerImpl$LoadSubscriber$onNext$1 implements Runnable {

    /* renamed from: $c */
    final /* synthetic */ Control f41$c;
    final /* synthetic */ LoadSubscriber this$0;

    ControlsBindingControllerImpl$LoadSubscriber$onNext$1(LoadSubscriber loadSubscriber, Control control) {
        this.this$0 = loadSubscriber;
        this.f41$c = control;
    }

    public final void run() {
        this.this$0.getLoadedControls().add(this.f41$c);
    }
}
