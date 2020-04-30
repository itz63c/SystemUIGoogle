package androidx.fragment.app;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

final class FragmentManagerViewModel extends ViewModel {
    private static final Factory FACTORY = new Factory() {
        public <T extends ViewModel> T create(Class<T> cls) {
            return new FragmentManagerViewModel(true);
        }
    };
    private final HashMap<String, FragmentManagerViewModel> mChildNonConfigs = new HashMap<>();
    private boolean mHasBeenCleared = false;
    private boolean mHasSavedSnapshot = false;
    private boolean mIsStateSaved = false;
    private final HashMap<String, Fragment> mRetainedFragments = new HashMap<>();
    private final boolean mStateAutomaticallySaved;
    private final HashMap<String, ViewModelStore> mViewModelStores = new HashMap<>();

    static FragmentManagerViewModel getInstance(ViewModelStore viewModelStore) {
        return (FragmentManagerViewModel) new ViewModelProvider(viewModelStore, FACTORY).get(FragmentManagerViewModel.class);
    }

    FragmentManagerViewModel(boolean z) {
        this.mStateAutomaticallySaved = z;
    }

    /* access modifiers changed from: 0000 */
    public void setIsStateSaved(boolean z) {
        this.mIsStateSaved = z;
    }

    /* access modifiers changed from: protected */
    public void onCleared() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("onCleared called for ");
            sb.append(this);
            Log.d("FragmentManager", sb.toString());
        }
        this.mHasBeenCleared = true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isCleared() {
        return this.mHasBeenCleared;
    }

    /* access modifiers changed from: 0000 */
    public void addRetainedFragment(Fragment fragment) {
        String str = "FragmentManager";
        if (this.mIsStateSaved) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v(str, "Ignoring addRetainedFragment as the state is already saved");
            }
        } else if (!this.mRetainedFragments.containsKey(fragment.mWho)) {
            this.mRetainedFragments.put(fragment.mWho, fragment);
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Updating retained Fragments: Added ");
                sb.append(fragment);
                Log.v(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public Fragment findRetainedFragmentByWho(String str) {
        return (Fragment) this.mRetainedFragments.get(str);
    }

    /* access modifiers changed from: 0000 */
    public Collection<Fragment> getRetainedFragments() {
        return new ArrayList(this.mRetainedFragments.values());
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldDestroy(Fragment fragment) {
        if (!this.mRetainedFragments.containsKey(fragment.mWho)) {
            return true;
        }
        if (this.mStateAutomaticallySaved) {
            return this.mHasBeenCleared;
        }
        return !this.mHasSavedSnapshot;
    }

    /* access modifiers changed from: 0000 */
    public void removeRetainedFragment(Fragment fragment) {
        String str = "FragmentManager";
        if (this.mIsStateSaved) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v(str, "Ignoring removeRetainedFragment as the state is already saved");
            }
            return;
        }
        if ((this.mRetainedFragments.remove(fragment.mWho) != null) && FragmentManager.isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Updating retained Fragments: Removed ");
            sb.append(fragment);
            Log.v(str, sb.toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public FragmentManagerViewModel getChildNonConfig(Fragment fragment) {
        FragmentManagerViewModel fragmentManagerViewModel = (FragmentManagerViewModel) this.mChildNonConfigs.get(fragment.mWho);
        if (fragmentManagerViewModel != null) {
            return fragmentManagerViewModel;
        }
        FragmentManagerViewModel fragmentManagerViewModel2 = new FragmentManagerViewModel(this.mStateAutomaticallySaved);
        this.mChildNonConfigs.put(fragment.mWho, fragmentManagerViewModel2);
        return fragmentManagerViewModel2;
    }

    /* access modifiers changed from: 0000 */
    public ViewModelStore getViewModelStore(Fragment fragment) {
        ViewModelStore viewModelStore = (ViewModelStore) this.mViewModelStores.get(fragment.mWho);
        if (viewModelStore != null) {
            return viewModelStore;
        }
        ViewModelStore viewModelStore2 = new ViewModelStore();
        this.mViewModelStores.put(fragment.mWho, viewModelStore2);
        return viewModelStore2;
    }

    /* access modifiers changed from: 0000 */
    public void clearNonConfigState(Fragment fragment) {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Clearing non-config state for ");
            sb.append(fragment);
            Log.d("FragmentManager", sb.toString());
        }
        FragmentManagerViewModel fragmentManagerViewModel = (FragmentManagerViewModel) this.mChildNonConfigs.get(fragment.mWho);
        if (fragmentManagerViewModel != null) {
            fragmentManagerViewModel.onCleared();
            this.mChildNonConfigs.remove(fragment.mWho);
        }
        ViewModelStore viewModelStore = (ViewModelStore) this.mViewModelStores.get(fragment.mWho);
        if (viewModelStore != null) {
            viewModelStore.clear();
            this.mViewModelStores.remove(fragment.mWho);
        }
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || FragmentManagerViewModel.class != obj.getClass()) {
            return false;
        }
        FragmentManagerViewModel fragmentManagerViewModel = (FragmentManagerViewModel) obj;
        if (!this.mRetainedFragments.equals(fragmentManagerViewModel.mRetainedFragments) || !this.mChildNonConfigs.equals(fragmentManagerViewModel.mChildNonConfigs) || !this.mViewModelStores.equals(fragmentManagerViewModel.mViewModelStores)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((this.mRetainedFragments.hashCode() * 31) + this.mChildNonConfigs.hashCode()) * 31) + this.mViewModelStores.hashCode();
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder("FragmentManagerViewModel{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("} Fragments (");
        Iterator it = this.mRetainedFragments.values().iterator();
        while (true) {
            str = ", ";
            if (!it.hasNext()) {
                break;
            }
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(str);
            }
        }
        sb.append(") Child Non Config (");
        Iterator it2 = this.mChildNonConfigs.keySet().iterator();
        while (it2.hasNext()) {
            sb.append((String) it2.next());
            if (it2.hasNext()) {
                sb.append(str);
            }
        }
        sb.append(") ViewModelStores (");
        Iterator it3 = this.mViewModelStores.keySet().iterator();
        while (it3.hasNext()) {
            sb.append((String) it3.next());
            if (it3.hasNext()) {
                sb.append(str);
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
