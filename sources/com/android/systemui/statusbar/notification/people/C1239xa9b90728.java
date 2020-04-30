package com.android.systemui.statusbar.notification.people;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.statusbar.notification.people.PeopleHubViewModelFactoryImpl$createWithAssociatedClickView$personViewModels$1 */
/* compiled from: PeopleHubViewController.kt */
final class C1239xa9b90728 extends Lambda implements Function1<PersonModel, PersonViewModel> {
    public static final C1239xa9b90728 INSTANCE = new C1239xa9b90728();

    C1239xa9b90728() {
        super(1);
    }

    public final PersonViewModel invoke(PersonModel personModel) {
        Intrinsics.checkParameterIsNotNull(personModel, "personModel");
        return new PersonViewModel(personModel.getName(), personModel.getAvatar(), new C1240xf85ca6ba(personModel));
    }
}
