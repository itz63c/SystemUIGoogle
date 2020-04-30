package androidx.leanback.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import androidx.leanback.transition.TransitionHelper;
import androidx.leanback.transition.TransitionListener;
import androidx.leanback.util.StateMachine;
import androidx.leanback.util.StateMachine.Condition;
import androidx.leanback.util.StateMachine.Event;
import androidx.leanback.util.StateMachine.State;

public class BaseSupportFragment extends BrandedSupportFragment {
    final Condition COND_TRANSITION_NOT_SUPPORTED = new Condition(this, "EntranceTransitionNotSupport") {
        public boolean canProceed() {
            return !TransitionHelper.systemSupportsEntranceTransitions();
        }
    };
    final Event EVT_ENTRANCE_END = new Event("onEntranceTransitionEnd");
    final Event EVT_ON_CREATE = new Event("onCreate");
    final Event EVT_ON_CREATEVIEW = new Event("onCreateView");
    final Event EVT_PREPARE_ENTRANCE = new Event("prepareEntranceTransition");
    final Event EVT_START_ENTRANCE = new Event("startEntranceTransition");
    final State STATE_ENTRANCE_COMPLETE = new State("ENTRANCE_COMPLETE", true, false);
    final State STATE_ENTRANCE_INIT = new State("ENTRANCE_INIT");
    final State STATE_ENTRANCE_ON_ENDED = new State("ENTRANCE_ON_ENDED") {
        public void run() {
            BaseSupportFragment.this.onEntranceTransitionEnd();
        }
    };
    final State STATE_ENTRANCE_ON_PREPARED = new State("ENTRANCE_ON_PREPARED", true, false) {
        public void run() {
            BaseSupportFragment.this.mProgressBarManager.show();
        }
    };
    final State STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW = new State("ENTRANCE_ON_PREPARED_ON_CREATEVIEW") {
        public void run() {
            BaseSupportFragment.this.onEntranceTransitionPrepare();
        }
    };
    final State STATE_ENTRANCE_PERFORM = new State("STATE_ENTRANCE_PERFORM") {
        public void run() {
            BaseSupportFragment.this.mProgressBarManager.hide();
            BaseSupportFragment.this.onExecuteEntranceTransition();
        }
    };
    final State STATE_START = new State("START", true, false);
    Object mEntranceTransition;
    final ProgressBarManager mProgressBarManager = new ProgressBarManager();
    final StateMachine mStateMachine = new StateMachine();

    /* access modifiers changed from: protected */
    public Object createEntranceTransition() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionEnd() {
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionPrepare() {
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionStart() {
    }

    /* access modifiers changed from: protected */
    public void runEntranceTransition(Object obj) {
    }

    @SuppressLint({"ValidFragment"})
    BaseSupportFragment() {
    }

    public void onCreate(Bundle bundle) {
        createStateMachineStates();
        createStateMachineTransitions();
        this.mStateMachine.start();
        super.onCreate(bundle);
        this.mStateMachine.fireEvent(this.EVT_ON_CREATE);
    }

    /* access modifiers changed from: 0000 */
    public void createStateMachineStates() {
        this.mStateMachine.addState(this.STATE_START);
        this.mStateMachine.addState(this.STATE_ENTRANCE_INIT);
        this.mStateMachine.addState(this.STATE_ENTRANCE_ON_PREPARED);
        this.mStateMachine.addState(this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW);
        this.mStateMachine.addState(this.STATE_ENTRANCE_PERFORM);
        this.mStateMachine.addState(this.STATE_ENTRANCE_ON_ENDED);
        this.mStateMachine.addState(this.STATE_ENTRANCE_COMPLETE);
    }

    /* access modifiers changed from: 0000 */
    public void createStateMachineTransitions() {
        this.mStateMachine.addTransition(this.STATE_START, this.STATE_ENTRANCE_INIT, this.EVT_ON_CREATE);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_COMPLETE, this.COND_TRANSITION_NOT_SUPPORTED);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_COMPLETE, this.EVT_ON_CREATEVIEW);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_ON_PREPARED, this.EVT_PREPARE_ENTRANCE);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.EVT_ON_CREATEVIEW);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_PERFORM, this.EVT_START_ENTRANCE);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.STATE_ENTRANCE_PERFORM);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_PERFORM, this.STATE_ENTRANCE_ON_ENDED, this.EVT_ENTRANCE_END);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_ENDED, this.STATE_ENTRANCE_COMPLETE);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mStateMachine.fireEvent(this.EVT_ON_CREATEVIEW);
    }

    /* access modifiers changed from: 0000 */
    public void onExecuteEntranceTransition() {
        final View view = getView();
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (BaseSupportFragment.this.getContext() == null || BaseSupportFragment.this.getView() == null) {
                        return true;
                    }
                    BaseSupportFragment.this.internalCreateEntranceTransition();
                    BaseSupportFragment.this.onEntranceTransitionStart();
                    BaseSupportFragment baseSupportFragment = BaseSupportFragment.this;
                    Object obj = baseSupportFragment.mEntranceTransition;
                    if (obj != null) {
                        baseSupportFragment.runEntranceTransition(obj);
                    } else {
                        baseSupportFragment.mStateMachine.fireEvent(baseSupportFragment.EVT_ENTRANCE_END);
                    }
                    return false;
                }
            });
            view.invalidate();
        }
    }

    /* access modifiers changed from: 0000 */
    public void internalCreateEntranceTransition() {
        Object createEntranceTransition = createEntranceTransition();
        this.mEntranceTransition = createEntranceTransition;
        if (createEntranceTransition != null) {
            TransitionHelper.addTransitionListener(createEntranceTransition, new TransitionListener() {
                public void onTransitionEnd(Object obj) {
                    BaseSupportFragment baseSupportFragment = BaseSupportFragment.this;
                    baseSupportFragment.mEntranceTransition = null;
                    baseSupportFragment.mStateMachine.fireEvent(baseSupportFragment.EVT_ENTRANCE_END);
                }
            });
        }
    }

    public final ProgressBarManager getProgressBarManager() {
        return this.mProgressBarManager;
    }
}
