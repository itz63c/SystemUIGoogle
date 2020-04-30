package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import android.view.View.OnClickListener;
import com.android.systemui.statusbar.notification.people.PersonViewModel;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* compiled from: PeopleHubView.kt */
final class PeopleHubView$PersonDataListenerImpl$onDataChanged$2 implements OnClickListener {
    final /* synthetic */ PersonViewModel $data;

    PeopleHubView$PersonDataListenerImpl$onDataChanged$2(PersonViewModel personViewModel) {
        this.$data = personViewModel;
    }

    public final void onClick(View view) {
        PersonViewModel personViewModel = this.$data;
        if (personViewModel != null) {
            Function0 onClick = personViewModel.getOnClick();
            if (onClick != null) {
                Unit unit = (Unit) onClick.invoke();
            }
        }
    }
}
