package com.android.systemui.statusbar.notification.people;

import android.view.View;
import kotlin.sequences.Sequence;

/* compiled from: PeopleHubViewController.kt */
public interface PeopleHubViewBoundary {
    View getAssociatedViewForClickAnimation();

    Sequence<DataListener<PersonViewModel>> getPersonViewAdapters();

    void setVisible(boolean z);
}
