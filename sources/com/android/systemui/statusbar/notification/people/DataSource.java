package com.android.systemui.statusbar.notification.people;

/* compiled from: ViewPipeline.kt */
public interface DataSource<T> {
    Subscription registerListener(DataListener<? super T> dataListener);
}
