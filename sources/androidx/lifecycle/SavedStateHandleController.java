package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistry.AutoRecreated;
import androidx.savedstate.SavedStateRegistryOwner;

final class SavedStateHandleController implements LifecycleEventObserver {
    private final SavedStateHandle mHandle;
    private boolean mIsAttached;
    private final String mKey;

    static final class OnRecreation implements AutoRecreated {
        OnRecreation() {
        }

        public void onRecreated(SavedStateRegistryOwner savedStateRegistryOwner) {
            if (savedStateRegistryOwner instanceof ViewModelStoreOwner) {
                ViewModelStore viewModelStore = ((ViewModelStoreOwner) savedStateRegistryOwner).getViewModelStore();
                SavedStateRegistry savedStateRegistry = savedStateRegistryOwner.getSavedStateRegistry();
                for (String str : viewModelStore.keys()) {
                    SavedStateHandleController.attachHandleIfNeeded(viewModelStore.get(str), savedStateRegistry, savedStateRegistryOwner.getLifecycle());
                }
                if (!viewModelStore.keys().isEmpty()) {
                    savedStateRegistry.runOnNextRecreation(OnRecreation.class);
                    return;
                }
                return;
            }
            throw new IllegalStateException("Internal error: OnRecreation should be registered only on componentsthat implement ViewModelStoreOwner");
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isAttached() {
        return this.mIsAttached;
    }

    /* access modifiers changed from: 0000 */
    public void attachToLifecycle(SavedStateRegistry savedStateRegistry, Lifecycle lifecycle) {
        if (!this.mIsAttached) {
            this.mIsAttached = true;
            lifecycle.addObserver(this);
            savedStateRegistry.registerSavedStateProvider(this.mKey, this.mHandle.savedStateProvider());
            return;
        }
        throw new IllegalStateException("Already attached to lifecycleOwner");
    }

    public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
        if (event == Event.ON_DESTROY) {
            this.mIsAttached = false;
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
    }

    static void attachHandleIfNeeded(ViewModel viewModel, SavedStateRegistry savedStateRegistry, Lifecycle lifecycle) {
        SavedStateHandleController savedStateHandleController = (SavedStateHandleController) viewModel.getTag("androidx.lifecycle.savedstate.vm.tag");
        if (savedStateHandleController != null && !savedStateHandleController.isAttached()) {
            savedStateHandleController.attachToLifecycle(savedStateRegistry, lifecycle);
            tryToAddRecreator(savedStateRegistry, lifecycle);
        }
    }

    private static void tryToAddRecreator(final SavedStateRegistry savedStateRegistry, final Lifecycle lifecycle) {
        State currentState = lifecycle.getCurrentState();
        if (currentState == State.INITIALIZED || currentState.isAtLeast(State.STARTED)) {
            savedStateRegistry.runOnNextRecreation(OnRecreation.class);
        } else {
            lifecycle.addObserver(new LifecycleEventObserver() {
                public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
                    if (event == Event.ON_START) {
                        lifecycle.removeObserver(this);
                        savedStateRegistry.runOnNextRecreation(OnRecreation.class);
                    }
                }
            });
        }
    }
}
