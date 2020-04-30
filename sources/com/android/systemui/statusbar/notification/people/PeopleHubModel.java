package com.android.systemui.statusbar.notification.people;

import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHub.kt */
public final class PeopleHubModel {
    private final Collection<PersonModel> people;

    public final PeopleHubModel copy(Collection<PersonModel> collection) {
        Intrinsics.checkParameterIsNotNull(collection, "people");
        return new PeopleHubModel(collection);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0010, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r1.people, (java.lang.Object) ((com.android.systemui.statusbar.notification.people.PeopleHubModel) r2).people) != false) goto L_0x0015;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r2) {
        /*
            r1 = this;
            if (r1 == r2) goto L_0x0015
            boolean r0 = r2 instanceof com.android.systemui.statusbar.notification.people.PeopleHubModel
            if (r0 == 0) goto L_0x0013
            com.android.systemui.statusbar.notification.people.PeopleHubModel r2 = (com.android.systemui.statusbar.notification.people.PeopleHubModel) r2
            java.util.Collection<com.android.systemui.statusbar.notification.people.PersonModel> r1 = r1.people
            java.util.Collection<com.android.systemui.statusbar.notification.people.PersonModel> r2 = r2.people
            boolean r1 = kotlin.jvm.internal.Intrinsics.areEqual(r1, r2)
            if (r1 == 0) goto L_0x0013
            goto L_0x0015
        L_0x0013:
            r1 = 0
            return r1
        L_0x0015:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.people.PeopleHubModel.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        Collection<PersonModel> collection = this.people;
        if (collection != null) {
            return collection.hashCode();
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PeopleHubModel(people=");
        sb.append(this.people);
        sb.append(")");
        return sb.toString();
    }

    public PeopleHubModel(Collection<PersonModel> collection) {
        Intrinsics.checkParameterIsNotNull(collection, "people");
        this.people = collection;
    }

    public final Collection<PersonModel> getPeople() {
        return this.people;
    }
}
