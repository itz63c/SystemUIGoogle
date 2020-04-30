package com.android.systemui.statusbar.notification.people;

import java.util.function.Predicate;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubManager$addActivePerson$1<T> implements Predicate<PersonModel> {
    final /* synthetic */ PersonModel $person;

    PeopleHubManager$addActivePerson$1(PersonModel personModel) {
        this.$person = personModel;
    }

    public final boolean test(PersonModel personModel) {
        return Intrinsics.areEqual((Object) personModel.getKey(), (Object) this.$person.getKey());
    }
}
