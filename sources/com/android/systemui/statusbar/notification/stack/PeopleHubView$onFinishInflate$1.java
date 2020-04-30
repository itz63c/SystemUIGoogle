package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import android.widget.ImageView;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* compiled from: PeopleHubView.kt */
final class PeopleHubView$onFinishInflate$1 extends Lambda implements Function1<Integer, PersonDataListenerImpl> {
    final /* synthetic */ PeopleHubView this$0;

    PeopleHubView$onFinishInflate$1(PeopleHubView peopleHubView) {
        this.this$0 = peopleHubView;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return invoke(((Number) obj).intValue());
    }

    public final PersonDataListenerImpl invoke(int i) {
        View childAt = PeopleHubView.access$getContents$p(this.this$0).getChildAt(i);
        if (!(childAt instanceof ImageView)) {
            childAt = null;
        }
        ImageView imageView = (ImageView) childAt;
        if (imageView != null) {
            return new PersonDataListenerImpl(this.this$0, imageView);
        }
        return null;
    }
}
