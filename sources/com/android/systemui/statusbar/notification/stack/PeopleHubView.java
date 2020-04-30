package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.C2011R$id;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.notification.people.DataListener;
import com.android.systemui.statusbar.notification.people.PersonViewModel;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* compiled from: PeopleHubView.kt */
public final class PeopleHubView extends StackScrollerDecorView implements SwipeableView {
    private boolean canSwipe = true;
    private ViewGroup contents;
    private Sequence<? extends DataListener<? super PersonViewModel>> personViewAdapters;

    /* compiled from: PeopleHubView.kt */
    private final class PersonDataListenerImpl implements DataListener<PersonViewModel> {
        private final ImageView avatarView;

        public PersonDataListenerImpl(PeopleHubView peopleHubView, ImageView imageView) {
            Intrinsics.checkParameterIsNotNull(imageView, "avatarView");
            this.avatarView = imageView;
        }

        public void onDataChanged(PersonViewModel personViewModel) {
            this.avatarView.setVisibility(personViewModel != null ? 0 : 8);
            this.avatarView.setImageDrawable(personViewModel != null ? personViewModel.getIcon() : null);
            this.avatarView.setOnClickListener(new PeopleHubView$PersonDataListenerImpl$onDataChanged$2(personViewModel));
        }
    }

    public NotificationMenuRowPlugin createMenu() {
        return null;
    }

    /* access modifiers changed from: protected */
    public View findSecondaryView() {
        return null;
    }

    public boolean hasFinishedInitialization() {
        return true;
    }

    public boolean needsClippingToShelf() {
        return true;
    }

    public static final /* synthetic */ ViewGroup access$getContents$p(PeopleHubView peopleHubView) {
        ViewGroup viewGroup = peopleHubView.contents;
        if (viewGroup != null) {
            return viewGroup;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }

    public PeopleHubView(Context context, AttributeSet attributeSet) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(attributeSet, "attrs");
        super(context, attributeSet);
    }

    public final Sequence<DataListener<PersonViewModel>> getPersonViewAdapters() {
        Sequence<? extends DataListener<? super PersonViewModel>> sequence = this.personViewAdapters;
        if (sequence != null) {
            return sequence;
        }
        Intrinsics.throwUninitializedPropertyAccessException("personViewAdapters");
        throw null;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        View requireViewById = requireViewById(C2011R$id.people_list);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById(R.id.people_list)");
        ViewGroup viewGroup = (ViewGroup) requireViewById;
        this.contents = viewGroup;
        if (viewGroup != null) {
            this.personViewAdapters = CollectionsKt___CollectionsKt.asSequence(SequencesKt___SequencesKt.toList(SequencesKt___SequencesKt.mapNotNull(CollectionsKt___CollectionsKt.asSequence(RangesKt___RangesKt.until(0, viewGroup.getChildCount())), new PeopleHubView$onFinishInflate$1(this))));
            super.onFinishInflate();
            setVisible(true, false);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }

    /* access modifiers changed from: protected */
    public View findContentView() {
        ViewGroup viewGroup = this.contents;
        if (viewGroup != null) {
            return viewGroup;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }

    public void resetTranslation() {
        setTranslationX(0.0f);
    }

    public void setTranslation(float f) {
        if (this.canSwipe) {
            super.setTranslation(f);
        }
    }

    public final void setCanSwipe(boolean z) {
        boolean z2 = this.canSwipe;
        if (z2 != z) {
            if (z2) {
                resetTranslation();
            }
            this.canSwipe = z;
        }
    }

    /* access modifiers changed from: protected */
    public void applyContentTransformation(float f, float f2) {
        super.applyContentTransformation(f, f2);
        ViewGroup viewGroup = this.contents;
        String str = "contents";
        if (viewGroup != null) {
            int childCount = viewGroup.getChildCount();
            int i = 0;
            while (i < childCount) {
                ViewGroup viewGroup2 = this.contents;
                if (viewGroup2 != null) {
                    View childAt = viewGroup2.getChildAt(i);
                    Intrinsics.checkExpressionValueIsNotNull(childAt, "view");
                    childAt.setAlpha(f);
                    childAt.setTranslationY(f2);
                    i++;
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }
}
