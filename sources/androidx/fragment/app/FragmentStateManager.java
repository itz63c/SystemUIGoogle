package androidx.fragment.app;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.p002os.CancellationSignal;
import androidx.core.view.ViewCompat;
import androidx.fragment.R$id;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.ViewModelStoreOwner;

class FragmentStateManager {
    private final FragmentLifecycleCallbacksDispatcher mDispatcher;
    private CancellationSignal mEnterAnimationCancellationSignal;
    private CancellationSignal mExitAnimationCancellationSignal;
    private final Fragment mFragment;
    private int mFragmentManagerState = -1;
    private final FragmentStore mFragmentStore;
    private boolean mMovingToState = false;

    /* renamed from: androidx.fragment.app.FragmentStateManager$1 */
    static /* synthetic */ class C01701 {
        static final /* synthetic */ int[] $SwitchMap$androidx$lifecycle$Lifecycle$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                androidx.lifecycle.Lifecycle$State[] r0 = androidx.lifecycle.Lifecycle.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$androidx$lifecycle$Lifecycle$State = r0
                androidx.lifecycle.Lifecycle$State r1 = androidx.lifecycle.Lifecycle.State.RESUMED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$androidx$lifecycle$Lifecycle$State     // Catch:{ NoSuchFieldError -> 0x001d }
                androidx.lifecycle.Lifecycle$State r1 = androidx.lifecycle.Lifecycle.State.STARTED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$androidx$lifecycle$Lifecycle$State     // Catch:{ NoSuchFieldError -> 0x0028 }
                androidx.lifecycle.Lifecycle$State r1 = androidx.lifecycle.Lifecycle.State.CREATED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.FragmentStateManager.C01701.<clinit>():void");
        }
    }

    FragmentStateManager(FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, FragmentStore fragmentStore, Fragment fragment) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragment;
    }

    FragmentStateManager(FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, FragmentStore fragmentStore, ClassLoader classLoader, FragmentFactory fragmentFactory, FragmentState fragmentState) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragmentFactory.instantiate(classLoader, fragmentState.mClassName);
        Bundle bundle = fragmentState.mArguments;
        if (bundle != null) {
            bundle.setClassLoader(classLoader);
        }
        this.mFragment.setArguments(fragmentState.mArguments);
        Fragment fragment = this.mFragment;
        fragment.mWho = fragmentState.mWho;
        fragment.mFromLayout = fragmentState.mFromLayout;
        fragment.mRestored = true;
        fragment.mFragmentId = fragmentState.mFragmentId;
        fragment.mContainerId = fragmentState.mContainerId;
        fragment.mTag = fragmentState.mTag;
        fragment.mRetainInstance = fragmentState.mRetainInstance;
        fragment.mRemoving = fragmentState.mRemoving;
        fragment.mDetached = fragmentState.mDetached;
        fragment.mHidden = fragmentState.mHidden;
        fragment.mMaxState = State.values()[fragmentState.mMaxLifecycleState];
        Bundle bundle2 = fragmentState.mSavedFragmentState;
        if (bundle2 != null) {
            this.mFragment.mSavedFragmentState = bundle2;
        } else {
            this.mFragment.mSavedFragmentState = new Bundle();
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Instantiated fragment ");
            sb.append(this.mFragment);
            Log.v("FragmentManager", sb.toString());
        }
    }

    FragmentStateManager(FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, FragmentStore fragmentStore, Fragment fragment, FragmentState fragmentState) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragment;
        fragment.mSavedViewState = null;
        fragment.mBackStackNesting = 0;
        fragment.mInLayout = false;
        fragment.mAdded = false;
        Fragment fragment2 = fragment.mTarget;
        fragment.mTargetWho = fragment2 != null ? fragment2.mWho : null;
        Fragment fragment3 = this.mFragment;
        fragment3.mTarget = null;
        Bundle bundle = fragmentState.mSavedFragmentState;
        if (bundle != null) {
            fragment3.mSavedFragmentState = bundle;
        } else {
            fragment3.mSavedFragmentState = new Bundle();
        }
    }

    /* access modifiers changed from: 0000 */
    public Fragment getFragment() {
        return this.mFragment;
    }

    /* access modifiers changed from: 0000 */
    public void setFragmentManagerState(int i) {
        this.mFragmentManagerState = i;
    }

    /* access modifiers changed from: 0000 */
    public int computeExpectedState() {
        Fragment fragment = this.mFragment;
        if (fragment.mFragmentManager == null) {
            return fragment.mState;
        }
        int i = this.mFragmentManagerState;
        if (fragment.mFromLayout) {
            if (fragment.mInLayout) {
                i = Math.max(i, 1);
            } else if (i < 2) {
                i = Math.min(i, fragment.mState);
            } else {
                i = Math.min(i, 1);
            }
        }
        if (!this.mFragment.mAdded) {
            i = Math.min(i, 1);
        }
        Fragment fragment2 = this.mFragment;
        if (fragment2.mRemoving) {
            if (fragment2.isInBackStack()) {
                i = Math.min(i, 1);
            } else {
                i = Math.min(i, -1);
            }
        }
        Fragment fragment3 = this.mFragment;
        if (fragment3.mDeferStart && fragment3.mState < 3) {
            i = Math.min(i, 2);
        }
        int i2 = C01701.$SwitchMap$androidx$lifecycle$Lifecycle$State[this.mFragment.mMaxState.ordinal()];
        if (i2 != 1) {
            if (i2 == 2) {
                i = Math.min(i, 3);
            } else if (i2 != 3) {
                i = Math.min(i, -1);
            } else {
                i = Math.min(i, 1);
            }
        }
        return i;
    }

    /* access modifiers changed from: 0000 */
    public void moveToExpectedState() {
        String str = "FragmentManager";
        if (this.mMovingToState) {
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring re-entrant call to moveToExpectedState() for ");
                sb.append(getFragment());
                Log.v(str, sb.toString());
            }
            return;
        }
        try {
            this.mMovingToState = true;
            while (true) {
                int computeExpectedState = computeExpectedState();
                if (computeExpectedState == this.mFragment.mState) {
                    return;
                }
                if (computeExpectedState > this.mFragment.mState) {
                    int i = this.mFragment.mState + 1;
                    if (this.mExitAnimationCancellationSignal != null) {
                        this.mExitAnimationCancellationSignal.cancel();
                    }
                    if (i == 0) {
                        attach();
                    } else if (i == 1) {
                        create();
                    } else if (i == 2) {
                        ensureInflatedView();
                        createView();
                        activityCreated();
                        restoreViewState();
                        if (this.mFragment.mContainer != null) {
                            SpecialEffectsController orCreateController = SpecialEffectsController.getOrCreateController(this.mFragment.mContainer);
                            CancellationSignal cancellationSignal = new CancellationSignal();
                            this.mEnterAnimationCancellationSignal = cancellationSignal;
                            orCreateController.enqueueAdd(this, cancellationSignal);
                        }
                    } else if (i == 3) {
                        start();
                    } else if (i == 4) {
                        resume();
                    }
                } else {
                    int i2 = this.mFragment.mState - 1;
                    if (this.mEnterAnimationCancellationSignal != null) {
                        this.mEnterAnimationCancellationSignal.cancel();
                    }
                    if (i2 == -1) {
                        detach();
                    } else if (i2 == 0) {
                        if (!(this.mFragment.mRemoving && !this.mFragment.isInBackStack())) {
                            if (!this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
                                if (this.mFragment.mTargetWho != null) {
                                    Fragment findActiveFragment = this.mFragmentStore.findActiveFragment(this.mFragment.mTargetWho);
                                    if (findActiveFragment != null && findActiveFragment.mRetainInstance) {
                                        this.mFragment.mTarget = findActiveFragment;
                                    }
                                }
                                destroy();
                            }
                        }
                        this.mFragmentStore.makeInactive(this);
                        destroy();
                    } else if (i2 == 1) {
                        if (FragmentManager.isLoggingEnabled(3)) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("movefrom ACTIVITY_CREATED: ");
                            sb2.append(this.mFragment);
                            Log.d(str, sb2.toString());
                        }
                        if (this.mFragment.mContainer != null) {
                            SpecialEffectsController orCreateController2 = SpecialEffectsController.getOrCreateController(this.mFragment.mContainer);
                            CancellationSignal cancellationSignal2 = new CancellationSignal();
                            this.mExitAnimationCancellationSignal = cancellationSignal2;
                            orCreateController2.enqueueRemove(this, cancellationSignal2);
                        }
                        destroyFragmentView();
                    } else if (i2 == 2) {
                        stop();
                    } else if (i2 == 3) {
                        pause();
                    }
                }
            }
        } finally {
            this.mMovingToState = false;
        }
    }

    /* access modifiers changed from: 0000 */
    public void ensureInflatedView() {
        Fragment fragment = this.mFragment;
        if (fragment.mFromLayout && fragment.mInLayout && !fragment.mPerformedCreateView) {
            if (FragmentManager.isLoggingEnabled(3)) {
                StringBuilder sb = new StringBuilder();
                sb.append("moveto CREATE_VIEW: ");
                sb.append(this.mFragment);
                Log.d("FragmentManager", sb.toString());
            }
            Fragment fragment2 = this.mFragment;
            fragment2.performCreateView(fragment2.performGetLayoutInflater(fragment2.mSavedFragmentState), null, this.mFragment.mSavedFragmentState);
            View view = this.mFragment.mView;
            if (view != null) {
                view.setSaveFromParentEnabled(false);
                Fragment fragment3 = this.mFragment;
                fragment3.mView.setTag(R$id.fragment_container_view_tag, fragment3);
                Fragment fragment4 = this.mFragment;
                if (fragment4.mHidden) {
                    fragment4.mView.setVisibility(8);
                }
                Fragment fragment5 = this.mFragment;
                fragment5.onViewCreated(fragment5.mView, fragment5.mSavedFragmentState);
                FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
                Fragment fragment6 = this.mFragment;
                fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentViewCreated(fragment6, fragment6.mView, fragment6.mSavedFragmentState, false);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void restoreState(ClassLoader classLoader) {
        Bundle bundle = this.mFragment.mSavedFragmentState;
        if (bundle != null) {
            bundle.setClassLoader(classLoader);
            Fragment fragment = this.mFragment;
            fragment.mSavedViewState = fragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
            Fragment fragment2 = this.mFragment;
            fragment2.mTargetWho = fragment2.mSavedFragmentState.getString("android:target_state");
            Fragment fragment3 = this.mFragment;
            if (fragment3.mTargetWho != null) {
                fragment3.mTargetRequestCode = fragment3.mSavedFragmentState.getInt("android:target_req_state", 0);
            }
            Fragment fragment4 = this.mFragment;
            Boolean bool = fragment4.mSavedUserVisibleHint;
            if (bool != null) {
                fragment4.mUserVisibleHint = bool.booleanValue();
                this.mFragment.mSavedUserVisibleHint = null;
            } else {
                fragment4.mUserVisibleHint = fragment4.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
            }
            Fragment fragment5 = this.mFragment;
            if (!fragment5.mUserVisibleHint) {
                fragment5.mDeferStart = true;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void attach() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("moveto ATTACHED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        Fragment fragment = this.mFragment;
        Fragment fragment2 = fragment.mTarget;
        String str = " that does not belong to this FragmentManager!";
        String str2 = " declared target fragment ";
        String str3 = "Fragment ";
        FragmentStateManager fragmentStateManager = null;
        if (fragment2 != null) {
            FragmentStateManager fragmentStateManager2 = this.mFragmentStore.getFragmentStateManager(fragment2.mWho);
            if (fragmentStateManager2 != null) {
                Fragment fragment3 = this.mFragment;
                fragment3.mTargetWho = fragment3.mTarget.mWho;
                fragment3.mTarget = null;
                fragmentStateManager = fragmentStateManager2;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str3);
                sb2.append(this.mFragment);
                sb2.append(str2);
                sb2.append(this.mFragment.mTarget);
                sb2.append(str);
                throw new IllegalStateException(sb2.toString());
            }
        } else {
            String str4 = fragment.mTargetWho;
            if (str4 != null) {
                fragmentStateManager = this.mFragmentStore.getFragmentStateManager(str4);
                if (fragmentStateManager == null) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str3);
                    sb3.append(this.mFragment);
                    sb3.append(str2);
                    sb3.append(this.mFragment.mTargetWho);
                    sb3.append(str);
                    throw new IllegalStateException(sb3.toString());
                }
            }
        }
        if (fragmentStateManager != null) {
            fragmentStateManager.moveToExpectedState();
        }
        Fragment fragment4 = this.mFragment;
        fragment4.mHost = fragment4.mFragmentManager.getHost();
        Fragment fragment5 = this.mFragment;
        fragment5.mParentFragment = fragment5.mFragmentManager.getParent();
        this.mDispatcher.dispatchOnFragmentPreAttached(this.mFragment, false);
        this.mFragment.performAttach();
        this.mDispatcher.dispatchOnFragmentAttached(this.mFragment, false);
    }

    /* access modifiers changed from: 0000 */
    public void create() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("moveto CREATED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        Fragment fragment = this.mFragment;
        if (!fragment.mIsCreated) {
            this.mDispatcher.dispatchOnFragmentPreCreated(fragment, fragment.mSavedFragmentState, false);
            Fragment fragment2 = this.mFragment;
            fragment2.performCreate(fragment2.mSavedFragmentState);
            FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
            Fragment fragment3 = this.mFragment;
            fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentCreated(fragment3, fragment3.mSavedFragmentState, false);
            return;
        }
        fragment.restoreChildFragmentState(fragment.mSavedFragmentState);
        this.mFragment.mState = 1;
    }

    /* access modifiers changed from: 0000 */
    public void createView() {
        String str;
        if (!this.mFragment.mFromLayout) {
            if (FragmentManager.isLoggingEnabled(3)) {
                StringBuilder sb = new StringBuilder();
                sb.append("moveto CREATE_VIEW: ");
                sb.append(this.mFragment);
                Log.d("FragmentManager", sb.toString());
            }
            ViewGroup viewGroup = null;
            Fragment fragment = this.mFragment;
            ViewGroup viewGroup2 = fragment.mContainer;
            if (viewGroup2 != null) {
                viewGroup = viewGroup2;
            } else {
                int i = fragment.mContainerId;
                if (i != 0) {
                    if (i != -1) {
                        viewGroup = (ViewGroup) fragment.mFragmentManager.getContainer().onFindViewById(this.mFragment.mContainerId);
                        if (viewGroup == null) {
                            Fragment fragment2 = this.mFragment;
                            if (!fragment2.mRestored) {
                                try {
                                    str = fragment2.getResources().getResourceName(this.mFragment.mContainerId);
                                } catch (NotFoundException unused) {
                                    str = "unknown";
                                }
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("No view found for id 0x");
                                sb2.append(Integer.toHexString(this.mFragment.mContainerId));
                                sb2.append(" (");
                                sb2.append(str);
                                sb2.append(") for fragment ");
                                sb2.append(this.mFragment);
                                throw new IllegalArgumentException(sb2.toString());
                            }
                        }
                    } else {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Cannot create fragment ");
                        sb3.append(this.mFragment);
                        sb3.append(" for a container view with no id");
                        throw new IllegalArgumentException(sb3.toString());
                    }
                }
            }
            Fragment fragment3 = this.mFragment;
            fragment3.mContainer = viewGroup;
            fragment3.performCreateView(fragment3.performGetLayoutInflater(fragment3.mSavedFragmentState), viewGroup, this.mFragment.mSavedFragmentState);
            View view = this.mFragment.mView;
            if (view != null) {
                boolean z = false;
                view.setSaveFromParentEnabled(false);
                Fragment fragment4 = this.mFragment;
                fragment4.mView.setTag(R$id.fragment_container_view_tag, fragment4);
                if (viewGroup != null) {
                    viewGroup.addView(this.mFragment.mView);
                }
                Fragment fragment5 = this.mFragment;
                if (fragment5.mHidden) {
                    fragment5.mView.setVisibility(8);
                }
                ViewCompat.requestApplyInsets(this.mFragment.mView);
                Fragment fragment6 = this.mFragment;
                fragment6.onViewCreated(fragment6.mView, fragment6.mSavedFragmentState);
                FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
                Fragment fragment7 = this.mFragment;
                fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentViewCreated(fragment7, fragment7.mView, fragment7.mSavedFragmentState, false);
                Fragment fragment8 = this.mFragment;
                if (fragment8.mView.getVisibility() == 0 && this.mFragment.mContainer != null) {
                    z = true;
                }
                fragment8.mIsNewlyAdded = z;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void activityCreated() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("moveto ACTIVITY_CREATED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        Fragment fragment = this.mFragment;
        fragment.performActivityCreated(fragment.mSavedFragmentState);
        FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
        Fragment fragment2 = this.mFragment;
        fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentActivityCreated(fragment2, fragment2.mSavedFragmentState, false);
    }

    /* access modifiers changed from: 0000 */
    public void restoreViewState() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("moveto RESTORE_VIEW_STATE: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        Fragment fragment = this.mFragment;
        if (fragment.mView != null) {
            fragment.restoreViewState(fragment.mSavedFragmentState);
        }
        this.mFragment.mSavedFragmentState = null;
    }

    /* access modifiers changed from: 0000 */
    public void start() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("moveto STARTED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performStart();
        this.mDispatcher.dispatchOnFragmentStarted(this.mFragment, false);
    }

    /* access modifiers changed from: 0000 */
    public void resume() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("moveto RESUMED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performResume();
        this.mDispatcher.dispatchOnFragmentResumed(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mSavedFragmentState = null;
        fragment.mSavedViewState = null;
    }

    /* access modifiers changed from: 0000 */
    public void pause() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("movefrom RESUMED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performPause();
        this.mDispatcher.dispatchOnFragmentPaused(this.mFragment, false);
    }

    /* access modifiers changed from: 0000 */
    public void stop() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("movefrom STARTED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        this.mFragment.performStop();
        this.mDispatcher.dispatchOnFragmentStopped(this.mFragment, false);
    }

    /* access modifiers changed from: 0000 */
    public FragmentState saveState() {
        FragmentState fragmentState = new FragmentState(this.mFragment);
        if (this.mFragment.mState <= -1 || fragmentState.mSavedFragmentState != null) {
            fragmentState.mSavedFragmentState = this.mFragment.mSavedFragmentState;
        } else {
            Bundle saveBasicState = saveBasicState();
            fragmentState.mSavedFragmentState = saveBasicState;
            if (this.mFragment.mTargetWho != null) {
                if (saveBasicState == null) {
                    fragmentState.mSavedFragmentState = new Bundle();
                }
                fragmentState.mSavedFragmentState.putString("android:target_state", this.mFragment.mTargetWho);
                int i = this.mFragment.mTargetRequestCode;
                if (i != 0) {
                    fragmentState.mSavedFragmentState.putInt("android:target_req_state", i);
                }
            }
        }
        return fragmentState;
    }

    private Bundle saveBasicState() {
        Bundle bundle = new Bundle();
        this.mFragment.performSaveInstanceState(bundle);
        this.mDispatcher.dispatchOnFragmentSaveInstanceState(this.mFragment, bundle, false);
        if (bundle.isEmpty()) {
            bundle = null;
        }
        if (this.mFragment.mView != null) {
            saveViewState();
        }
        if (this.mFragment.mSavedViewState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray("android:view_state", this.mFragment.mSavedViewState);
        }
        if (!this.mFragment.mUserVisibleHint) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean("android:user_visible_hint", this.mFragment.mUserVisibleHint);
        }
        return bundle;
    }

    /* access modifiers changed from: 0000 */
    public void saveViewState() {
        if (this.mFragment.mView != null) {
            SparseArray<Parcelable> sparseArray = new SparseArray<>();
            this.mFragment.mView.saveHierarchyState(sparseArray);
            if (sparseArray.size() > 0) {
                this.mFragment.mSavedViewState = sparseArray;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void destroyFragmentView() {
        this.mFragment.performDestroyView();
        this.mDispatcher.dispatchOnFragmentViewDestroyed(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mContainer = null;
        fragment.mView = null;
        fragment.mViewLifecycleOwner = null;
        fragment.mViewLifecycleOwnerLiveData.setValue(null);
        this.mFragment.mInLayout = false;
    }

    /* access modifiers changed from: 0000 */
    public void destroy() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("movefrom CREATED: ");
            sb.append(this.mFragment);
            Log.d("FragmentManager", sb.toString());
        }
        Fragment fragment = this.mFragment;
        boolean z = true;
        boolean z2 = fragment.mRemoving && !fragment.isInBackStack();
        if (z2 || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
            FragmentHostCallback<?> fragmentHostCallback = this.mFragment.mHost;
            if (fragmentHostCallback instanceof ViewModelStoreOwner) {
                z = this.mFragmentStore.getNonConfig().isCleared();
            } else if (fragmentHostCallback.getContext() instanceof Activity) {
                z = true ^ ((Activity) fragmentHostCallback.getContext()).isChangingConfigurations();
            }
            if (z2 || z) {
                this.mFragmentStore.getNonConfig().clearNonConfigState(this.mFragment);
            }
            this.mFragment.performDestroy();
            this.mDispatcher.dispatchOnFragmentDestroyed(this.mFragment, false);
            return;
        }
        this.mFragment.mState = 0;
    }

    /* access modifiers changed from: 0000 */
    public void detach() {
        String str = "FragmentManager";
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("movefrom ATTACHED: ");
            sb.append(this.mFragment);
            Log.d(str, sb.toString());
        }
        this.mFragment.performDetach();
        boolean z = false;
        this.mDispatcher.dispatchOnFragmentDetached(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mState = -1;
        fragment.mHost = null;
        fragment.mParentFragment = null;
        fragment.mFragmentManager = null;
        if (fragment.mRemoving && !fragment.isInBackStack()) {
            z = true;
        }
        if (z || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
            if (FragmentManager.isLoggingEnabled(3)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("initState called for fragment: ");
                sb2.append(this.mFragment);
                Log.d(str, sb2.toString());
            }
            this.mFragment.initState();
        }
    }
}
