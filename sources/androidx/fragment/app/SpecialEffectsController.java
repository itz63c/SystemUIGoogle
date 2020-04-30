package androidx.fragment.app;

import android.view.ViewGroup;
import androidx.core.p002os.CancellationSignal;
import androidx.core.p002os.CancellationSignal.OnCancelListener;
import androidx.fragment.R$id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class SpecialEffectsController {
    final HashMap<Fragment, Operation> mAwaitingCompletionOperations = new HashMap<>();
    final ArrayList<Operation> mPendingOperations = new ArrayList<>();

    private static class FragmentStateManagerOperation extends Operation {
        FragmentStateManagerOperation(Type type, FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
            super(type, fragmentStateManager.getFragment(), cancellationSignal);
        }
    }

    static class Operation {
        private final List<Runnable> mCompletionListeners = new ArrayList();
        private final Fragment mFragment;

        enum Type {
            ADD,
            REMOVE
        }

        Operation(Type type, Fragment fragment, CancellationSignal cancellationSignal) {
            this.mFragment = fragment;
        }

        public final Fragment getFragment() {
            return this.mFragment;
        }

        /* access modifiers changed from: 0000 */
        public final void addCompletionListener(Runnable runnable) {
            this.mCompletionListeners.add(runnable);
        }
    }

    static SpecialEffectsController getOrCreateController(ViewGroup viewGroup) {
        return getOrCreateController(viewGroup, FragmentManager.findFragmentManager(viewGroup).getSpecialEffectsControllerFactory());
    }

    static SpecialEffectsController getOrCreateController(ViewGroup viewGroup, SpecialEffectsControllerFactory specialEffectsControllerFactory) {
        Object tag = viewGroup.getTag(R$id.special_effects_controller_view_tag);
        if (tag instanceof SpecialEffectsController) {
            return (SpecialEffectsController) tag;
        }
        SpecialEffectsController createController = specialEffectsControllerFactory.createController(viewGroup);
        viewGroup.setTag(R$id.special_effects_controller_view_tag, createController);
        return createController;
    }

    SpecialEffectsController(ViewGroup viewGroup) {
    }

    /* access modifiers changed from: 0000 */
    public void enqueueAdd(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Type.ADD, fragmentStateManager, cancellationSignal);
    }

    /* access modifiers changed from: 0000 */
    public void enqueueRemove(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Type.REMOVE, fragmentStateManager, cancellationSignal);
    }

    private void enqueue(Type type, FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        if (!cancellationSignal.isCanceled()) {
            synchronized (this.mPendingOperations) {
                final CancellationSignal cancellationSignal2 = new CancellationSignal();
                final FragmentStateManagerOperation fragmentStateManagerOperation = new FragmentStateManagerOperation(type, fragmentStateManager, cancellationSignal2);
                this.mPendingOperations.add(fragmentStateManagerOperation);
                this.mAwaitingCompletionOperations.put(fragmentStateManagerOperation.getFragment(), fragmentStateManagerOperation);
                cancellationSignal.setOnCancelListener(new OnCancelListener() {
                    public void onCancel() {
                        synchronized (SpecialEffectsController.this.mPendingOperations) {
                            SpecialEffectsController.this.mPendingOperations.remove(fragmentStateManagerOperation);
                            SpecialEffectsController.this.mAwaitingCompletionOperations.remove(fragmentStateManagerOperation.getFragment());
                            cancellationSignal2.cancel();
                        }
                    }
                });
                fragmentStateManagerOperation.addCompletionListener(new Runnable() {
                    public void run() {
                        SpecialEffectsController.this.mAwaitingCompletionOperations.remove(fragmentStateManagerOperation.getFragment());
                    }
                });
            }
        }
    }
}
