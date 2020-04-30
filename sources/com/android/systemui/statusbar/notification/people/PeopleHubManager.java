package com.android.systemui.statusbar.notification.people;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubNotificationListener.kt */
public final class PeopleHubManager {
    private final Map<String, PersonModel> activePeople = new LinkedHashMap();
    private final ArrayDeque<PersonModel> inactivePeople = new ArrayDeque<>(10);

    public final boolean migrateActivePerson(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        PersonModel personModel = (PersonModel) this.activePeople.remove(str);
        if (personModel == null) {
            return false;
        }
        if (this.inactivePeople.size() >= 10) {
            this.inactivePeople.removeLast();
        }
        this.inactivePeople.addFirst(personModel);
        return true;
    }

    public final void removeActivePerson(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        this.activePeople.remove(str);
    }

    public final boolean addActivePerson(PersonModel personModel) {
        Intrinsics.checkParameterIsNotNull(personModel, "person");
        this.activePeople.put(personModel.getKey(), personModel);
        return this.inactivePeople.removeIf(new PeopleHubManager$addActivePerson$1(personModel));
    }

    public final PeopleHubModel getPeopleHubModel() {
        return new PeopleHubModel(this.inactivePeople);
    }
}
