package com.android.systemui.statusbar.notification.people;

import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* compiled from: PeopleHub.kt */
public final class PeopleHubViewModel {
    private final boolean isVisible;
    private final Sequence<PersonViewModel> people;

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
        if (r2.isVisible == r3.isVisible) goto L_0x001b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x001b
            boolean r0 = r3 instanceof com.android.systemui.statusbar.notification.people.PeopleHubViewModel
            if (r0 == 0) goto L_0x0019
            com.android.systemui.statusbar.notification.people.PeopleHubViewModel r3 = (com.android.systemui.statusbar.notification.people.PeopleHubViewModel) r3
            kotlin.sequences.Sequence<com.android.systemui.statusbar.notification.people.PersonViewModel> r0 = r2.people
            kotlin.sequences.Sequence<com.android.systemui.statusbar.notification.people.PersonViewModel> r1 = r3.people
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0019
            boolean r2 = r2.isVisible
            boolean r3 = r3.isVisible
            if (r2 != r3) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            r2 = 0
            return r2
        L_0x001b:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.people.PeopleHubViewModel.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        Sequence<PersonViewModel> sequence = this.people;
        int hashCode = (sequence != null ? sequence.hashCode() : 0) * 31;
        boolean z = this.isVisible;
        if (z) {
            z = true;
        }
        return hashCode + (z ? 1 : 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PeopleHubViewModel(people=");
        sb.append(this.people);
        sb.append(", isVisible=");
        sb.append(this.isVisible);
        sb.append(")");
        return sb.toString();
    }

    public PeopleHubViewModel(Sequence<PersonViewModel> sequence, boolean z) {
        Intrinsics.checkParameterIsNotNull(sequence, "people");
        this.people = sequence;
        this.isVisible = z;
    }

    public final Sequence<PersonViewModel> getPeople() {
        return this.people;
    }

    public final boolean isVisible() {
        return this.isVisible;
    }
}
