package androidx.leanback.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.R$transition;
import androidx.leanback.transition.TransitionHelper;
import androidx.leanback.util.StateMachine.State;
import androidx.leanback.widget.BrowseFrameLayout;
import androidx.leanback.widget.OnChildLaidOutListener;
import androidx.leanback.widget.VerticalGridPresenter;
import androidx.leanback.widget.VerticalGridPresenter.ViewHolder;

public class VerticalGridSupportFragment extends BaseSupportFragment {
    final State STATE_SET_ENTRANCE_START_STATE = new State("SET_ENTRANCE_START_STATE") {
        public void run() {
            VerticalGridSupportFragment.this.setEntranceTransitionState(false);
        }
    };
    private final OnChildLaidOutListener mChildLaidOutListener = new OnChildLaidOutListener() {
        public void onChildLaidOut(ViewGroup viewGroup, View view, int i, long j) {
            if (i == 0) {
                VerticalGridSupportFragment.this.showOrHideTitle();
            }
        }
    };
    private VerticalGridPresenter mGridPresenter;
    ViewHolder mGridViewHolder;
    private Object mSceneAfterEntranceTransition;
    private int mSelectedPosition = -1;

    /* access modifiers changed from: 0000 */
    public void createStateMachineStates() {
        super.createStateMachineStates();
        this.mStateMachine.addState(this.STATE_SET_ENTRANCE_START_STATE);
    }

    /* access modifiers changed from: 0000 */
    public void createStateMachineTransitions() {
        super.createStateMachineTransitions();
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_SET_ENTRANCE_START_STATE, this.EVT_ON_CREATEVIEW);
    }

    /* access modifiers changed from: 0000 */
    public void showOrHideTitle() {
        if (this.mGridViewHolder.getGridView().findViewHolderForAdapterPosition(this.mSelectedPosition) != null) {
            if (!this.mGridViewHolder.getGridView().hasPreviousViewInSameRow(this.mSelectedPosition)) {
                showTitle(true);
            } else {
                showTitle(false);
            }
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(R$layout.lb_vertical_grid_fragment, viewGroup, false);
        installTitleView(layoutInflater, (ViewGroup) viewGroup2.findViewById(R$id.grid_frame), bundle);
        getProgressBarManager().setRootView(viewGroup2);
        this.mGridPresenter.onCreateViewHolder((ViewGroup) viewGroup2.findViewById(R$id.browse_grid_dock));
        throw null;
    }

    private void setupFocusSearchListener() {
        ((BrowseFrameLayout) getView().findViewById(R$id.grid_frame)).setOnFocusSearchListener(getTitleHelper().getOnFocusSearchListener());
    }

    public void onStart() {
        super.onStart();
        setupFocusSearchListener();
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mGridViewHolder = null;
    }

    /* access modifiers changed from: protected */
    public Object createEntranceTransition() {
        return TransitionHelper.loadTransition(getContext(), R$transition.lb_vertical_grid_entrance_transition);
    }

    /* access modifiers changed from: protected */
    public void runEntranceTransition(Object obj) {
        TransitionHelper.runTransition(this.mSceneAfterEntranceTransition, obj);
    }

    /* access modifiers changed from: 0000 */
    public void setEntranceTransitionState(boolean z) {
        this.mGridPresenter.setEntranceTransitionState(this.mGridViewHolder, z);
    }
}
