package com.android.systemui.statusbar.notification.people;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.statusbar.notification.people.PeopleHubViewModelFactoryImpl$createWithAssociatedClickView$personViewModels$1$onClick$1 */
/* compiled from: PeopleHubViewController.kt */
final class C1240xf85ca6ba extends Lambda implements Function0<Unit> {
    final /* synthetic */ PersonModel $personModel;

    C1240xf85ca6ba(PersonModel personModel) {
        this.$personModel = personModel;
        super(0);
    }

    public final void invoke() {
        this.$personModel.getClickRunnable().run();
    }
}
