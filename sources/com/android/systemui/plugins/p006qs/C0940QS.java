package com.android.systemui.plugins.p006qs;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.android.systemui.plugins.FragmentBase;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@DependsOn(target = HeightListener.class)
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_QS", version = 7)
/* renamed from: com.android.systemui.plugins.qs.QS */
public interface C0940QS extends FragmentBase {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_QS";
    public static final String TAG = "QS";
    public static final int VERSION = 7;

    @ProvidesInterface(version = 1)
    /* renamed from: com.android.systemui.plugins.qs.QS$HeightListener */
    public interface HeightListener {
        public static final int VERSION = 1;

        void onQsHeightChanged();
    }

    void animateHeaderSlidingIn(long j);

    void animateHeaderSlidingOut();

    void closeDetail();

    int getDesiredHeight();

    View getHeader();

    int getQsMinExpansionHeight();

    void hideImmediately();

    boolean isCustomizing();

    boolean isShowingDetail();

    void notifyCustomizeChanged();

    void setContainer(ViewGroup viewGroup);

    void setExpandClickListener(OnClickListener onClickListener);

    void setExpanded(boolean z);

    void setHasNotifications(boolean z) {
    }

    void setHeaderClickable(boolean z);

    void setHeaderListening(boolean z);

    void setHeightOverride(int i);

    void setListening(boolean z);

    void setOverscrolling(boolean z);

    void setPanelView(HeightListener heightListener);

    void setQsExpansion(float f, float f2);

    void setShowCollapsedOnKeyguard(boolean z) {
    }

    boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return isCustomizing();
    }
}
