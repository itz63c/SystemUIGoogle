package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.collection.ArraySet;
import androidx.core.p002os.CancellationSignal;
import androidx.fragment.R$id;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.ViewModelStore;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FragmentManager {
    private static boolean DEBUG = false;
    ArrayList<BackStackRecord> mBackStack;
    private ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    private final AtomicInteger mBackStackIndex = new AtomicInteger();
    private FragmentContainer mContainer;
    private ArrayList<Fragment> mCreatedMenus;
    int mCurState = -1;
    private SpecialEffectsControllerFactory mDefaultSpecialEffectsControllerFactory = new SpecialEffectsControllerFactory(this) {
        public SpecialEffectsController createController(ViewGroup viewGroup) {
            return new DefaultSpecialEffectsController(viewGroup);
        }
    };
    private boolean mDestroyed;
    private Runnable mExecCommit = new Runnable() {
        public void run() {
            FragmentManager.this.execPendingActions(true);
        }
    };
    private boolean mExecutingActions;
    private ConcurrentHashMap<Fragment, HashSet<CancellationSignal>> mExitAnimationCancellationSignals = new ConcurrentHashMap<>();
    private FragmentFactory mFragmentFactory = null;
    private final FragmentStore mFragmentStore = new FragmentStore();
    private final Callback mFragmentTransitionCallback = new Callback() {
        public void onStart(Fragment fragment, CancellationSignal cancellationSignal) {
            FragmentManager.this.addCancellationSignal(fragment, cancellationSignal);
        }

        public void onComplete(Fragment fragment, CancellationSignal cancellationSignal) {
            if (!cancellationSignal.isCanceled()) {
                FragmentManager.this.removeCancellationSignal(fragment, cancellationSignal);
            }
        }
    };
    private boolean mHavePendingDeferredStart;
    private FragmentHostCallback<?> mHost;
    private FragmentFactory mHostFragmentFactory = new FragmentFactory() {
        public Fragment instantiate(ClassLoader classLoader, String str) {
            return FragmentManager.this.getHost().instantiate(FragmentManager.this.getHost().getContext(), str, null);
        }
    };
    private final FragmentLayoutInflaterFactory mLayoutInflaterFactory = new FragmentLayoutInflaterFactory(this);
    private final FragmentLifecycleCallbacksDispatcher mLifecycleCallbacksDispatcher = new FragmentLifecycleCallbacksDispatcher(this);
    private boolean mNeedMenuInvalidate;
    private FragmentManagerViewModel mNonConfig;
    private final OnBackPressedCallback mOnBackPressedCallback = new OnBackPressedCallback(false) {
        public void handleOnBackPressed() {
            FragmentManager.this.handleOnBackPressed();
        }
    };
    private OnBackPressedDispatcher mOnBackPressedDispatcher;
    private Fragment mParent;
    private final ArrayList<OpGenerator> mPendingActions = new ArrayList<>();
    private ArrayList<StartEnterTransitionListener> mPostponedTransactions;
    Fragment mPrimaryNav;
    private SpecialEffectsControllerFactory mSpecialEffectsControllerFactory = null;
    private boolean mStateSaved;
    private boolean mStopped;
    private ArrayList<Fragment> mTmpAddedFragments;
    private ArrayList<Boolean> mTmpIsPop;
    private ArrayList<BackStackRecord> mTmpRecords;

    public interface BackStackEntry {
        int getId();

        String getName();
    }

    public static abstract class FragmentLifecycleCallbacks {
        public abstract void onFragmentActivityCreated(FragmentManager fragmentManager, Fragment fragment, Bundle bundle);

        public abstract void onFragmentAttached(FragmentManager fragmentManager, Fragment fragment, Context context);

        public abstract void onFragmentCreated(FragmentManager fragmentManager, Fragment fragment, Bundle bundle);

        public abstract void onFragmentDestroyed(FragmentManager fragmentManager, Fragment fragment);

        public abstract void onFragmentDetached(FragmentManager fragmentManager, Fragment fragment);

        public abstract void onFragmentPaused(FragmentManager fragmentManager, Fragment fragment);

        public abstract void onFragmentPreAttached(FragmentManager fragmentManager, Fragment fragment, Context context);

        public abstract void onFragmentPreCreated(FragmentManager fragmentManager, Fragment fragment, Bundle bundle);

        public abstract void onFragmentResumed(FragmentManager fragmentManager, Fragment fragment);

        public abstract void onFragmentSaveInstanceState(FragmentManager fragmentManager, Fragment fragment, Bundle bundle);

        public abstract void onFragmentStarted(FragmentManager fragmentManager, Fragment fragment);

        public abstract void onFragmentStopped(FragmentManager fragmentManager, Fragment fragment);

        public abstract void onFragmentViewCreated(FragmentManager fragmentManager, Fragment fragment, View view, Bundle bundle);

        public abstract void onFragmentViewDestroyed(FragmentManager fragmentManager, Fragment fragment);
    }

    public interface OnBackStackChangedListener {
        void onBackStackChanged();
    }

    interface OpGenerator {
        boolean generateOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2);
    }

    private class PopBackStackState implements OpGenerator {
        final int mFlags;
        final int mId;
        final String mName;

        PopBackStackState(String str, int i, int i2) {
            this.mName = str;
            this.mId = i;
            this.mFlags = i2;
        }

        public boolean generateOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
            Fragment fragment = FragmentManager.this.mPrimaryNav;
            if (fragment != null && this.mId < 0 && this.mName == null && fragment.getChildFragmentManager().popBackStackImmediate()) {
                return false;
            }
            return FragmentManager.this.popBackStackState(arrayList, arrayList2, this.mName, this.mId, this.mFlags);
        }
    }

    static class StartEnterTransitionListener implements OnStartEnterTransitionListener {
        final boolean mIsBack;
        private int mNumPostponed;
        final BackStackRecord mRecord;

        StartEnterTransitionListener(BackStackRecord backStackRecord, boolean z) {
            this.mIsBack = z;
            this.mRecord = backStackRecord;
        }

        public void onStartEnterTransition() {
            int i = this.mNumPostponed - 1;
            this.mNumPostponed = i;
            if (i == 0) {
                this.mRecord.mManager.scheduleCommit();
            }
        }

        public void startListening() {
            this.mNumPostponed++;
        }

        public boolean isReady() {
            return this.mNumPostponed == 0;
        }

        /* access modifiers changed from: 0000 */
        public void completeTransaction() {
            boolean z = this.mNumPostponed > 0;
            for (Fragment fragment : this.mRecord.mManager.getFragments()) {
                fragment.setOnStartEnterTransitionListener(null);
                if (z && fragment.isPostponed()) {
                    fragment.startPostponedEnterTransition();
                }
            }
            BackStackRecord backStackRecord = this.mRecord;
            backStackRecord.mManager.completeExecute(backStackRecord, this.mIsBack, !z, true);
        }

        /* access modifiers changed from: 0000 */
        public void cancelTransaction() {
            BackStackRecord backStackRecord = this.mRecord;
            backStackRecord.mManager.completeExecute(backStackRecord, this.mIsBack, false, false);
        }
    }

    static int reverseTransit(int i) {
        if (i == 4097) {
            return 8194;
        }
        if (i != 4099) {
            return i != 8194 ? 0 : 4097;
        }
        return 4099;
    }

    static boolean isLoggingEnabled(int i) {
        return DEBUG || Log.isLoggable("FragmentManager", i);
    }

    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    public boolean executePendingTransactions() {
        boolean execPendingActions = execPendingActions(true);
        forcePostponedTransactions();
        return execPendingActions;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        if (getBackStackEntryCount() <= 0) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0022, code lost:
        if (isPrimaryNavigation(r3.mParent) == false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0025, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        r0.setEnabled(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0029, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
        r0 = r3.mOnBackPressedCallback;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateOnBackPressedCallbackEnabled() {
        /*
            r3 = this;
            java.util.ArrayList<androidx.fragment.app.FragmentManager$OpGenerator> r0 = r3.mPendingActions
            monitor-enter(r0)
            java.util.ArrayList<androidx.fragment.app.FragmentManager$OpGenerator> r1 = r3.mPendingActions     // Catch:{ all -> 0x002a }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x002a }
            r2 = 1
            if (r1 != 0) goto L_0x0013
            androidx.activity.OnBackPressedCallback r3 = r3.mOnBackPressedCallback     // Catch:{ all -> 0x002a }
            r3.setEnabled(r2)     // Catch:{ all -> 0x002a }
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            return
        L_0x0013:
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            androidx.activity.OnBackPressedCallback r0 = r3.mOnBackPressedCallback
            int r1 = r3.getBackStackEntryCount()
            if (r1 <= 0) goto L_0x0025
            androidx.fragment.app.Fragment r1 = r3.mParent
            boolean r3 = r3.isPrimaryNavigation(r1)
            if (r3 == 0) goto L_0x0025
            goto L_0x0026
        L_0x0025:
            r2 = 0
        L_0x0026:
            r0.setEnabled(r2)
            return
        L_0x002a:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.FragmentManager.updateOnBackPressedCallbackEnabled():void");
    }

    /* access modifiers changed from: 0000 */
    public boolean isPrimaryNavigation(Fragment fragment) {
        boolean z = true;
        if (fragment == null) {
            return true;
        }
        FragmentManager fragmentManager = fragment.mFragmentManager;
        if (!fragment.equals(fragmentManager.getPrimaryNavigationFragment()) || !isPrimaryNavigation(fragmentManager.mParent)) {
            z = false;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public void handleOnBackPressed() {
        execPendingActions(true);
        if (this.mOnBackPressedCallback.isEnabled()) {
            popBackStackImmediate();
        } else {
            this.mOnBackPressedDispatcher.onBackPressed();
        }
    }

    public boolean popBackStackImmediate() {
        return popBackStackImmediate(null, -1, 0);
    }

    public void popBackStack(int i, int i2) {
        if (i >= 0) {
            enqueueAction(new PopBackStackState(null, i, i2), false);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Bad id: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public boolean popBackStackImmediate(int i, int i2) {
        if (i >= 0) {
            return popBackStackImmediate(null, i, i2);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Bad id: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    private boolean popBackStackImmediate(String str, int i, int i2) {
        execPendingActions(false);
        ensureExecReady(true);
        Fragment fragment = this.mPrimaryNav;
        if (fragment != null && i < 0 && str == null && fragment.getChildFragmentManager().popBackStackImmediate()) {
            return true;
        }
        boolean popBackStackState = popBackStackState(this.mTmpRecords, this.mTmpIsPop, str, i, i2);
        if (popBackStackState) {
            this.mExecutingActions = true;
            try {
                removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
            } finally {
                cleanupExec();
            }
        }
        updateOnBackPressedCallbackEnabled();
        doPendingDeferredStart();
        this.mFragmentStore.burpActive();
        return popBackStackState;
    }

    public int getBackStackEntryCount() {
        ArrayList<BackStackRecord> arrayList = this.mBackStack;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public BackStackEntry getBackStackEntryAt(int i) {
        return (BackStackEntry) this.mBackStack.get(i);
    }

    public void addOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList<>();
        }
        this.mBackStackChangeListeners.add(onBackStackChangedListener);
    }

    public void removeOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
        ArrayList<OnBackStackChangedListener> arrayList = this.mBackStackChangeListeners;
        if (arrayList != null) {
            arrayList.remove(onBackStackChangedListener);
        }
    }

    /* access modifiers changed from: 0000 */
    public void addCancellationSignal(Fragment fragment, CancellationSignal cancellationSignal) {
        if (this.mExitAnimationCancellationSignals.get(fragment) == null) {
            this.mExitAnimationCancellationSignals.put(fragment, new HashSet());
        }
        ((HashSet) this.mExitAnimationCancellationSignals.get(fragment)).add(cancellationSignal);
    }

    /* access modifiers changed from: 0000 */
    public void removeCancellationSignal(Fragment fragment, CancellationSignal cancellationSignal) {
        HashSet hashSet = (HashSet) this.mExitAnimationCancellationSignals.get(fragment);
        if (hashSet != null && hashSet.remove(cancellationSignal) && hashSet.isEmpty()) {
            this.mExitAnimationCancellationSignals.remove(fragment);
            if (fragment.mState < 3) {
                destroyFragmentView(fragment);
                moveToState(fragment);
            }
        }
    }

    private static Fragment findViewFragment(View view) {
        while (view != null) {
            Fragment viewFragment = getViewFragment(view);
            if (viewFragment != null) {
                return viewFragment;
            }
            ViewParent parent = view.getParent();
            view = parent instanceof View ? (View) parent : null;
        }
        return null;
    }

    static Fragment getViewFragment(View view) {
        Object tag = view.getTag(R$id.fragment_container_view_tag);
        if (tag instanceof Fragment) {
            return (Fragment) tag;
        }
        return null;
    }

    static FragmentManager findFragmentManager(View view) {
        Fragment findViewFragment = findViewFragment(view);
        if (findViewFragment != null) {
            return findViewFragment.getChildFragmentManager();
        }
        Context context = view.getContext();
        FragmentActivity fragmentActivity = null;
        while (true) {
            if (!(context instanceof ContextWrapper)) {
                break;
            } else if (context instanceof FragmentActivity) {
                fragmentActivity = (FragmentActivity) context;
                break;
            } else {
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        if (fragmentActivity != null) {
            return fragmentActivity.getSupportFragmentManager();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("View ");
        sb.append(view);
        sb.append(" is not within a subclass of FragmentActivity.");
        throw new IllegalStateException(sb.toString());
    }

    public List<Fragment> getFragments() {
        return this.mFragmentStore.getFragments();
    }

    /* access modifiers changed from: 0000 */
    public ViewModelStore getViewModelStore(Fragment fragment) {
        return this.mNonConfig.getViewModelStore(fragment);
    }

    private FragmentManagerViewModel getChildNonConfig(Fragment fragment) {
        return this.mNonConfig.getChildNonConfig(fragment);
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        Fragment fragment = this.mParent;
        String str = "}";
        String str2 = "{";
        if (fragment != null) {
            sb.append(fragment.getClass().getSimpleName());
            sb.append(str2);
            sb.append(Integer.toHexString(System.identityHashCode(this.mParent)));
            sb.append(str);
        } else {
            FragmentHostCallback<?> fragmentHostCallback = this.mHost;
            if (fragmentHostCallback != null) {
                sb.append(fragmentHostCallback.getClass().getSimpleName());
                sb.append(str2);
                sb.append(Integer.toHexString(System.identityHashCode(this.mHost)));
                sb.append(str);
            } else {
                sb.append("null");
            }
        }
        sb.append("}}");
        return sb.toString();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("    ");
        String sb2 = sb.toString();
        this.mFragmentStore.dump(str, fileDescriptor, printWriter, strArr);
        ArrayList<Fragment> arrayList = this.mCreatedMenus;
        if (arrayList != null) {
            int size = arrayList.size();
            if (size > 0) {
                printWriter.print(str);
                printWriter.println("Fragments Created Menus:");
                for (int i = 0; i < size; i++) {
                    Fragment fragment = (Fragment) this.mCreatedMenus.get(i);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(fragment.toString());
                }
            }
        }
        ArrayList<BackStackRecord> arrayList2 = this.mBackStack;
        if (arrayList2 != null) {
            int size2 = arrayList2.size();
            if (size2 > 0) {
                printWriter.print(str);
                printWriter.println("Back Stack:");
                for (int i2 = 0; i2 < size2; i2++) {
                    BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.get(i2);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i2);
                    printWriter.print(": ");
                    printWriter.println(backStackRecord.toString());
                    backStackRecord.dump(sb2, printWriter);
                }
            }
        }
        printWriter.print(str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Back Stack Index: ");
        sb3.append(this.mBackStackIndex.get());
        printWriter.println(sb3.toString());
        synchronized (this.mPendingActions) {
            int size3 = this.mPendingActions.size();
            if (size3 > 0) {
                printWriter.print(str);
                printWriter.println("Pending Actions:");
                for (int i3 = 0; i3 < size3; i3++) {
                    OpGenerator opGenerator = (OpGenerator) this.mPendingActions.get(i3);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i3);
                    printWriter.print(": ");
                    printWriter.println(opGenerator);
                }
            }
        }
        printWriter.print(str);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(str);
        printWriter.print("  mHost=");
        printWriter.println(this.mHost);
        printWriter.print(str);
        printWriter.print("  mContainer=");
        printWriter.println(this.mContainer);
        if (this.mParent != null) {
            printWriter.print(str);
            printWriter.print("  mParent=");
            printWriter.println(this.mParent);
        }
        printWriter.print(str);
        printWriter.print("  mCurState=");
        printWriter.print(this.mCurState);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.mStateSaved);
        printWriter.print(" mStopped=");
        printWriter.print(this.mStopped);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            printWriter.print(str);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.mNeedMenuInvalidate);
        }
    }

    /* access modifiers changed from: 0000 */
    public void performPendingDeferredStart(FragmentStateManager fragmentStateManager) {
        Fragment fragment = fragmentStateManager.getFragment();
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
            } else {
                fragment.mDeferStart = false;
                moveToState(fragment);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isStateAtLeast(int i) {
        return this.mCurState >= i;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003f, code lost:
        if (r9 != 3) goto L_0x014d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0149  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void moveToState(androidx.fragment.app.Fragment r10, int r11) {
        /*
            r9 = this;
            androidx.fragment.app.FragmentStore r0 = r9.mFragmentStore
            java.lang.String r1 = r10.mWho
            androidx.fragment.app.FragmentStateManager r0 = r0.getFragmentStateManager(r1)
            r1 = 1
            if (r0 != 0) goto L_0x0017
            androidx.fragment.app.FragmentStateManager r0 = new androidx.fragment.app.FragmentStateManager
            androidx.fragment.app.FragmentLifecycleCallbacksDispatcher r2 = r9.mLifecycleCallbacksDispatcher
            androidx.fragment.app.FragmentStore r3 = r9.mFragmentStore
            r0.<init>(r2, r3, r10)
            r0.setFragmentManagerState(r1)
        L_0x0017:
            int r2 = r0.computeExpectedState()
            int r11 = java.lang.Math.min(r11, r2)
            int r2 = r10.mState
            java.lang.String r3 = "FragmentManager"
            r4 = -1
            r5 = 2
            r6 = 3
            if (r2 > r11) goto L_0x0069
            if (r2 >= r11) goto L_0x0035
            java.util.concurrent.ConcurrentHashMap<androidx.fragment.app.Fragment, java.util.HashSet<androidx.core.os.CancellationSignal>> r2 = r9.mExitAnimationCancellationSignals
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0035
            r9.cancelExitAnimation(r10)
        L_0x0035:
            int r9 = r10.mState
            if (r9 == r4) goto L_0x0043
            if (r9 == 0) goto L_0x0048
            if (r9 == r1) goto L_0x004d
            if (r9 == r5) goto L_0x005d
            if (r9 == r6) goto L_0x0062
            goto L_0x014d
        L_0x0043:
            if (r11 <= r4) goto L_0x0048
            r0.attach()
        L_0x0048:
            if (r11 <= 0) goto L_0x004d
            r0.create()
        L_0x004d:
            if (r11 <= r4) goto L_0x0052
            r0.ensureInflatedView()
        L_0x0052:
            if (r11 <= r1) goto L_0x005d
            r0.createView()
            r0.activityCreated()
            r0.restoreViewState()
        L_0x005d:
            if (r11 <= r5) goto L_0x0062
            r0.start()
        L_0x0062:
            if (r11 <= r6) goto L_0x014d
            r0.resume()
            goto L_0x014d
        L_0x0069:
            if (r2 <= r11) goto L_0x014d
            if (r2 == 0) goto L_0x0146
            r7 = 0
            if (r2 == r1) goto L_0x010a
            if (r2 == r5) goto L_0x0083
            if (r2 == r6) goto L_0x007e
            r8 = 4
            if (r2 == r8) goto L_0x0079
            goto L_0x014d
        L_0x0079:
            if (r11 >= r8) goto L_0x007e
            r0.pause()
        L_0x007e:
            if (r11 >= r6) goto L_0x0083
            r0.stop()
        L_0x0083:
            if (r11 >= r5) goto L_0x010a
            boolean r2 = isLoggingEnabled(r6)
            if (r2 == 0) goto L_0x009f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "movefrom ACTIVITY_CREATED: "
            r2.append(r5)
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r3, r2)
        L_0x009f:
            android.view.View r2 = r10.mView
            if (r2 == 0) goto L_0x00b2
            androidx.fragment.app.FragmentHostCallback<?> r2 = r9.mHost
            boolean r2 = r2.onShouldSaveFragmentState(r10)
            if (r2 == 0) goto L_0x00b2
            android.util.SparseArray<android.os.Parcelable> r2 = r10.mSavedViewState
            if (r2 != 0) goto L_0x00b2
            r0.saveViewState()
        L_0x00b2:
            r2 = 0
            android.view.View r5 = r10.mView
            if (r5 == 0) goto L_0x00ff
            android.view.ViewGroup r8 = r10.mContainer
            if (r8 == 0) goto L_0x00ff
            r8.endViewTransition(r5)
            android.view.View r5 = r10.mView
            r5.clearAnimation()
            boolean r5 = r10.isRemovingParent()
            if (r5 != 0) goto L_0x00ff
            int r5 = r9.mCurState
            r8 = 0
            if (r5 <= r4) goto L_0x00ea
            boolean r4 = r9.mDestroyed
            if (r4 != 0) goto L_0x00ea
            android.view.View r4 = r10.mView
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x00ea
            float r4 = r10.mPostponedAlpha
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 < 0) goto L_0x00ea
            androidx.fragment.app.FragmentHostCallback<?> r2 = r9.mHost
            android.content.Context r2 = r2.getContext()
            androidx.fragment.app.FragmentAnim$AnimationOrAnimator r2 = androidx.fragment.app.FragmentAnim.loadAnimation(r2, r10, r7)
        L_0x00ea:
            r10.mPostponedAlpha = r8
            android.view.ViewGroup r4 = r10.mContainer
            android.view.View r5 = r10.mView
            if (r2 == 0) goto L_0x00f7
            androidx.fragment.app.FragmentTransition$Callback r8 = r9.mFragmentTransitionCallback
            androidx.fragment.app.FragmentAnim.animateRemoveFragment(r10, r2, r8)
        L_0x00f7:
            r4.removeView(r5)
            android.view.ViewGroup r2 = r10.mContainer
            if (r4 == r2) goto L_0x00ff
            return
        L_0x00ff:
            java.util.concurrent.ConcurrentHashMap<androidx.fragment.app.Fragment, java.util.HashSet<androidx.core.os.CancellationSignal>> r2 = r9.mExitAnimationCancellationSignals
            java.lang.Object r2 = r2.get(r10)
            if (r2 != 0) goto L_0x010a
            r0.destroyFragmentView()
        L_0x010a:
            if (r11 >= r1) goto L_0x0146
            boolean r2 = r10.mRemoving
            if (r2 == 0) goto L_0x0117
            boolean r2 = r10.isInBackStack()
            if (r2 != 0) goto L_0x0117
            r7 = r1
        L_0x0117:
            if (r7 != 0) goto L_0x0135
            androidx.fragment.app.FragmentManagerViewModel r2 = r9.mNonConfig
            boolean r2 = r2.shouldDestroy(r10)
            if (r2 == 0) goto L_0x0122
            goto L_0x0135
        L_0x0122:
            java.lang.String r2 = r10.mTargetWho
            if (r2 == 0) goto L_0x013a
            androidx.fragment.app.Fragment r2 = r9.findActiveFragment(r2)
            if (r2 == 0) goto L_0x013a
            boolean r4 = r2.getRetainInstance()
            if (r4 == 0) goto L_0x013a
            r10.mTarget = r2
            goto L_0x013a
        L_0x0135:
            androidx.fragment.app.FragmentStore r2 = r9.mFragmentStore
            r2.makeInactive(r0)
        L_0x013a:
            java.util.concurrent.ConcurrentHashMap<androidx.fragment.app.Fragment, java.util.HashSet<androidx.core.os.CancellationSignal>> r9 = r9.mExitAnimationCancellationSignals
            java.lang.Object r9 = r9.get(r10)
            if (r9 == 0) goto L_0x0143
            goto L_0x0147
        L_0x0143:
            r0.destroy()
        L_0x0146:
            r1 = r11
        L_0x0147:
            if (r1 >= 0) goto L_0x014c
            r0.detach()
        L_0x014c:
            r11 = r1
        L_0x014d:
            int r9 = r10.mState
            if (r9 == r11) goto L_0x017f
            boolean r9 = isLoggingEnabled(r6)
            if (r9 == 0) goto L_0x017d
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r0 = "moveToState: Fragment state for "
            r9.append(r0)
            r9.append(r10)
            java.lang.String r0 = " not updated inline; expected state "
            r9.append(r0)
            r9.append(r11)
            java.lang.String r0 = " found "
            r9.append(r0)
            int r0 = r10.mState
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            android.util.Log.d(r3, r9)
        L_0x017d:
            r10.mState = r11
        L_0x017f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.FragmentManager.moveToState(androidx.fragment.app.Fragment, int):void");
    }

    private void cancelExitAnimation(Fragment fragment) {
        HashSet hashSet = (HashSet) this.mExitAnimationCancellationSignals.get(fragment);
        if (hashSet != null) {
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                ((CancellationSignal) it.next()).cancel();
            }
            hashSet.clear();
            destroyFragmentView(fragment);
            this.mExitAnimationCancellationSignals.remove(fragment);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setExitAnimationOrder(Fragment fragment, boolean z) {
        ViewGroup fragmentContainer = getFragmentContainer(fragment);
        if (fragmentContainer != null && (fragmentContainer instanceof FragmentContainerView)) {
            ((FragmentContainerView) fragmentContainer).setDrawDisappearingViewsLast(!z);
        }
    }

    private void destroyFragmentView(Fragment fragment) {
        fragment.performDestroyView();
        this.mLifecycleCallbacksDispatcher.dispatchOnFragmentViewDestroyed(fragment, false);
        fragment.mContainer = null;
        fragment.mView = null;
        fragment.mViewLifecycleOwner = null;
        fragment.mViewLifecycleOwnerLiveData.setValue(null);
        fragment.mInLayout = false;
    }

    /* access modifiers changed from: 0000 */
    public void moveToState(Fragment fragment) {
        moveToState(fragment, this.mCurState);
    }

    private void completeShowHideFragment(final Fragment fragment) {
        if (fragment.mView != null) {
            AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(this.mHost.getContext(), fragment, !fragment.mHidden);
            if (loadAnimation != null) {
                Animator animator = loadAnimation.animator;
                if (animator != null) {
                    animator.setTarget(fragment.mView);
                    if (!fragment.mHidden) {
                        fragment.mView.setVisibility(0);
                    } else if (fragment.isHideReplaced()) {
                        fragment.setHideReplaced(false);
                    } else {
                        final ViewGroup viewGroup = fragment.mContainer;
                        final View view = fragment.mView;
                        viewGroup.startViewTransition(view);
                        loadAnimation.animator.addListener(new AnimatorListenerAdapter(this) {
                            public void onAnimationEnd(Animator animator) {
                                viewGroup.endViewTransition(view);
                                animator.removeListener(this);
                                Fragment fragment = fragment;
                                View view = fragment.mView;
                                if (view != null && fragment.mHidden) {
                                    view.setVisibility(8);
                                }
                            }
                        });
                    }
                    loadAnimation.animator.start();
                }
            }
            if (loadAnimation != null) {
                fragment.mView.startAnimation(loadAnimation.animation);
                loadAnimation.animation.start();
            }
            fragment.mView.setVisibility((!fragment.mHidden || fragment.isHideReplaced()) ? 0 : 8);
            if (fragment.isHideReplaced()) {
                fragment.setHideReplaced(false);
            }
        }
        if (fragment.mAdded && isMenuAvailable(fragment)) {
            this.mNeedMenuInvalidate = true;
        }
        fragment.mHiddenChanged = false;
        fragment.onHiddenChanged(fragment.mHidden);
    }

    /* access modifiers changed from: 0000 */
    public void moveFragmentToExpectedState(Fragment fragment) {
        if (!this.mFragmentStore.containsActiveFragment(fragment.mWho)) {
            if (isLoggingEnabled(3)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring moving ");
                sb.append(fragment);
                sb.append(" to state ");
                sb.append(this.mCurState);
                sb.append("since it is not added to ");
                sb.append(this);
                Log.d("FragmentManager", sb.toString());
            }
            return;
        }
        moveToState(fragment);
        if (fragment.mView != null) {
            Fragment findFragmentUnder = this.mFragmentStore.findFragmentUnder(fragment);
            if (findFragmentUnder != null) {
                View view = findFragmentUnder.mView;
                ViewGroup viewGroup = fragment.mContainer;
                int indexOfChild = viewGroup.indexOfChild(view);
                int indexOfChild2 = viewGroup.indexOfChild(fragment.mView);
                if (indexOfChild2 < indexOfChild) {
                    viewGroup.removeViewAt(indexOfChild2);
                    viewGroup.addView(fragment.mView, indexOfChild);
                }
            }
            if (fragment.mIsNewlyAdded && fragment.mContainer != null) {
                float f = fragment.mPostponedAlpha;
                if (f > 0.0f) {
                    fragment.mView.setAlpha(f);
                }
                fragment.mPostponedAlpha = 0.0f;
                fragment.mIsNewlyAdded = false;
                AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(this.mHost.getContext(), fragment, true);
                if (loadAnimation != null) {
                    Animation animation = loadAnimation.animation;
                    if (animation != null) {
                        fragment.mView.startAnimation(animation);
                    } else {
                        loadAnimation.animator.setTarget(fragment.mView);
                        loadAnimation.animator.start();
                    }
                }
            }
        }
        if (fragment.mHiddenChanged) {
            completeShowHideFragment(fragment);
        }
    }

    /* access modifiers changed from: 0000 */
    public void moveToState(int i, boolean z) {
        if (this.mHost == null && i != -1) {
            throw new IllegalStateException("No activity");
        } else if (z || i != this.mCurState) {
            this.mCurState = i;
            for (Fragment moveFragmentToExpectedState : this.mFragmentStore.getFragments()) {
                moveFragmentToExpectedState(moveFragmentToExpectedState);
            }
            for (FragmentStateManager fragment : this.mFragmentStore.getActiveFragmentStateManagers()) {
                Fragment fragment2 = fragment.getFragment();
                if (!fragment2.mIsNewlyAdded) {
                    moveFragmentToExpectedState(fragment2);
                }
            }
            startPendingDeferredFragments();
            if (this.mNeedMenuInvalidate) {
                FragmentHostCallback<?> fragmentHostCallback = this.mHost;
                if (fragmentHostCallback != null && this.mCurState == 4) {
                    fragmentHostCallback.onSupportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = false;
                }
            }
        }
    }

    private void startPendingDeferredFragments() {
        for (FragmentStateManager performPendingDeferredStart : this.mFragmentStore.getActiveFragmentStateManagers()) {
            performPendingDeferredStart(performPendingDeferredStart);
        }
    }

    /* access modifiers changed from: 0000 */
    public FragmentStateManager createOrGetFragmentStateManager(Fragment fragment) {
        FragmentStateManager fragmentStateManager = this.mFragmentStore.getFragmentStateManager(fragment.mWho);
        if (fragmentStateManager != null) {
            return fragmentStateManager;
        }
        FragmentStateManager fragmentStateManager2 = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, fragment);
        fragmentStateManager2.restoreState(this.mHost.getContext().getClassLoader());
        fragmentStateManager2.setFragmentManagerState(this.mCurState);
        return fragmentStateManager2;
    }

    /* access modifiers changed from: 0000 */
    public void addFragment(Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("add: ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
        this.mFragmentStore.makeActive(createOrGetFragmentStateManager(fragment));
        if (!fragment.mDetached) {
            this.mFragmentStore.addFragment(fragment);
            fragment.mRemoving = false;
            if (fragment.mView == null) {
                fragment.mHiddenChanged = false;
            }
            if (isMenuAvailable(fragment)) {
                this.mNeedMenuInvalidate = true;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void removeFragment(Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("remove: ");
            sb.append(fragment);
            sb.append(" nesting=");
            sb.append(fragment.mBackStackNesting);
            Log.v("FragmentManager", sb.toString());
        }
        boolean z = !fragment.isInBackStack();
        if (!fragment.mDetached || z) {
            this.mFragmentStore.removeFragment(fragment);
            if (isMenuAvailable(fragment)) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mRemoving = true;
            setVisibleRemovingFragment(fragment);
        }
    }

    /* access modifiers changed from: 0000 */
    public void hideFragment(Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("hide: ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
        if (!fragment.mHidden) {
            fragment.mHidden = true;
            fragment.mHiddenChanged = true ^ fragment.mHiddenChanged;
            setVisibleRemovingFragment(fragment);
        }
    }

    /* access modifiers changed from: 0000 */
    public void showFragment(Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("show: ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
        if (fragment.mHidden) {
            fragment.mHidden = false;
            fragment.mHiddenChanged = !fragment.mHiddenChanged;
        }
    }

    /* access modifiers changed from: 0000 */
    public void detachFragment(Fragment fragment) {
        String str = "FragmentManager";
        if (isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("detach: ");
            sb.append(fragment);
            Log.v(str, sb.toString());
        }
        if (!fragment.mDetached) {
            fragment.mDetached = true;
            if (fragment.mAdded) {
                if (isLoggingEnabled(2)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("remove from detach: ");
                    sb2.append(fragment);
                    Log.v(str, sb2.toString());
                }
                this.mFragmentStore.removeFragment(fragment);
                if (isMenuAvailable(fragment)) {
                    this.mNeedMenuInvalidate = true;
                }
                setVisibleRemovingFragment(fragment);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void attachFragment(Fragment fragment) {
        String str = "FragmentManager";
        if (isLoggingEnabled(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("attach: ");
            sb.append(fragment);
            Log.v(str, sb.toString());
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (!fragment.mAdded) {
                this.mFragmentStore.addFragment(fragment);
                if (isLoggingEnabled(2)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("add from attach: ");
                    sb2.append(fragment);
                    Log.v(str, sb2.toString());
                }
                if (isMenuAvailable(fragment)) {
                    this.mNeedMenuInvalidate = true;
                }
            }
        }
    }

    public Fragment findFragmentById(int i) {
        return this.mFragmentStore.findFragmentById(i);
    }

    public Fragment findFragmentByTag(String str) {
        return this.mFragmentStore.findFragmentByTag(str);
    }

    /* access modifiers changed from: 0000 */
    public Fragment findFragmentByWho(String str) {
        return this.mFragmentStore.findFragmentByWho(str);
    }

    /* access modifiers changed from: 0000 */
    public Fragment findActiveFragment(String str) {
        return this.mFragmentStore.findActiveFragment(str);
    }

    private void checkStateLoss() {
        if (isStateSaved()) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        }
    }

    public boolean isStateSaved() {
        return this.mStateSaved || this.mStopped;
    }

    /* access modifiers changed from: 0000 */
    public void enqueueAction(OpGenerator opGenerator, boolean z) {
        if (!z) {
            if (this.mHost != null) {
                checkStateLoss();
            } else if (this.mDestroyed) {
                throw new IllegalStateException("FragmentManager has been destroyed");
            } else {
                throw new IllegalStateException("FragmentManager has not been attached to a host.");
            }
        }
        synchronized (this.mPendingActions) {
            if (this.mHost != null) {
                this.mPendingActions.add(opGenerator);
                scheduleCommit();
            } else if (!z) {
                throw new IllegalStateException("Activity has been destroyed");
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void scheduleCommit() {
        synchronized (this.mPendingActions) {
            boolean z = false;
            boolean z2 = this.mPostponedTransactions != null && !this.mPostponedTransactions.isEmpty();
            if (this.mPendingActions.size() == 1) {
                z = true;
            }
            if (z2 || z) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
                updateOnBackPressedCallbackEnabled();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int allocBackStackIndex() {
        return this.mBackStackIndex.getAndIncrement();
    }

    private void ensureExecReady(boolean z) {
        if (this.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        } else if (this.mHost == null) {
            if (this.mDestroyed) {
                throw new IllegalStateException("FragmentManager has been destroyed");
            }
            throw new IllegalStateException("FragmentManager has not been attached to a host.");
        } else if (Looper.myLooper() == this.mHost.getHandler().getLooper()) {
            if (!z) {
                checkStateLoss();
            }
            if (this.mTmpRecords == null) {
                this.mTmpRecords = new ArrayList<>();
                this.mTmpIsPop = new ArrayList<>();
            }
            this.mExecutingActions = true;
            try {
                executePostponedTransaction(null, null);
            } finally {
                this.mExecutingActions = false;
            }
        } else {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        }
    }

    /* access modifiers changed from: 0000 */
    public void execSingleAction(OpGenerator opGenerator, boolean z) {
        if (!z || (this.mHost != null && !this.mDestroyed)) {
            ensureExecReady(z);
            if (opGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
                this.mExecutingActions = true;
                try {
                    removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                } finally {
                    cleanupExec();
                }
            }
            updateOnBackPressedCallbackEnabled();
            doPendingDeferredStart();
            this.mFragmentStore.burpActive();
        }
    }

    private void cleanupExec() {
        this.mExecutingActions = false;
        this.mTmpIsPop.clear();
        this.mTmpRecords.clear();
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: 0000 */
    public boolean execPendingActions(boolean z) {
        ensureExecReady(z);
        boolean z2 = false;
        while (generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                cleanupExec();
                z2 = true;
            } catch (Throwable th) {
                cleanupExec();
                throw th;
            }
        }
        updateOnBackPressedCallbackEnabled();
        doPendingDeferredStart();
        this.mFragmentStore.burpActive();
        return z2;
    }

    private void executePostponedTransaction(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
        ArrayList<StartEnterTransitionListener> arrayList3 = this.mPostponedTransactions;
        int size = arrayList3 == null ? 0 : arrayList3.size();
        int i = 0;
        while (i < size) {
            StartEnterTransitionListener startEnterTransitionListener = (StartEnterTransitionListener) this.mPostponedTransactions.get(i);
            if (arrayList != null && !startEnterTransitionListener.mIsBack) {
                int indexOf = arrayList.indexOf(startEnterTransitionListener.mRecord);
                if (!(indexOf == -1 || arrayList2 == null || !((Boolean) arrayList2.get(indexOf)).booleanValue())) {
                    this.mPostponedTransactions.remove(i);
                    i--;
                    size--;
                    startEnterTransitionListener.cancelTransaction();
                    i++;
                }
            }
            if (startEnterTransitionListener.isReady() || (arrayList != null && startEnterTransitionListener.mRecord.interactsWith(arrayList, 0, arrayList.size()))) {
                this.mPostponedTransactions.remove(i);
                i--;
                size--;
                if (arrayList != null && !startEnterTransitionListener.mIsBack) {
                    int indexOf2 = arrayList.indexOf(startEnterTransitionListener.mRecord);
                    if (!(indexOf2 == -1 || arrayList2 == null || !((Boolean) arrayList2.get(indexOf2)).booleanValue())) {
                        startEnterTransitionListener.cancelTransaction();
                    }
                }
                startEnterTransitionListener.completeTransaction();
            }
            i++;
        }
    }

    private void removeRedundantOperationsAndExecute(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
        if (!arrayList.isEmpty()) {
            if (arrayList.size() == arrayList2.size()) {
                executePostponedTransaction(arrayList, arrayList2);
                int size = arrayList.size();
                int i = 0;
                int i2 = 0;
                while (i < size) {
                    if (!((BackStackRecord) arrayList.get(i)).mReorderingAllowed) {
                        if (i2 != i) {
                            executeOpsTogether(arrayList, arrayList2, i2, i);
                        }
                        i2 = i + 1;
                        if (((Boolean) arrayList2.get(i)).booleanValue()) {
                            while (i2 < size && ((Boolean) arrayList2.get(i2)).booleanValue() && !((BackStackRecord) arrayList.get(i2)).mReorderingAllowed) {
                                i2++;
                            }
                        }
                        executeOpsTogether(arrayList, arrayList2, i, i2);
                        i = i2 - 1;
                    }
                    i++;
                }
                if (i2 != size) {
                    executeOpsTogether(arrayList, arrayList2, i2, size);
                }
                return;
            }
            throw new IllegalStateException("Internal error with the back stack records");
        }
    }

    private void executeOpsTogether(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        ArrayList<Boolean> arrayList3;
        boolean z;
        int i7;
        ArrayList<Boolean> arrayList4;
        int i8;
        boolean z2;
        ArrayList<BackStackRecord> arrayList5 = arrayList;
        ArrayList<Boolean> arrayList6 = arrayList2;
        int i9 = i;
        int i10 = i2;
        boolean z3 = ((BackStackRecord) arrayList5.get(i9)).mReorderingAllowed;
        ArrayList<Fragment> arrayList7 = this.mTmpAddedFragments;
        if (arrayList7 == null) {
            this.mTmpAddedFragments = new ArrayList<>();
        } else {
            arrayList7.clear();
        }
        this.mTmpAddedFragments.addAll(this.mFragmentStore.getFragments());
        Fragment primaryNavigationFragment = getPrimaryNavigationFragment();
        boolean z4 = false;
        for (int i11 = i9; i11 < i10; i11++) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList5.get(i11);
            if (!((Boolean) arrayList6.get(i11)).booleanValue()) {
                primaryNavigationFragment = backStackRecord.expandOps(this.mTmpAddedFragments, primaryNavigationFragment);
            } else {
                primaryNavigationFragment = backStackRecord.trackAddedFragmentsInPop(this.mTmpAddedFragments, primaryNavigationFragment);
            }
            z4 = z4 || backStackRecord.mAddToBackStack;
        }
        this.mTmpAddedFragments.clear();
        if (z3 || this.mCurState < 1) {
            i3 = 1;
        } else {
            i3 = 1;
            FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, arrayList, arrayList2, i, i2, false, this.mFragmentTransitionCallback);
        }
        executeOps(arrayList, arrayList2, i, i2);
        if (z3) {
            ArraySet arraySet = new ArraySet();
            addAddedFragments(arraySet);
            i4 = i3;
            z = z3;
            i5 = i10;
            i6 = i9;
            arrayList3 = arrayList6;
            i7 = postponePostponableTransactions(arrayList, arrayList2, i, i2, arraySet);
            makeRemovedFragmentsInvisible(arraySet);
        } else {
            i4 = i3;
            z = z3;
            i5 = i10;
            i6 = i9;
            arrayList3 = arrayList6;
            i7 = i5;
        }
        if (i7 == i6 || !z) {
            arrayList4 = arrayList3;
            i8 = i5;
        } else {
            if (this.mCurState >= i4) {
                arrayList4 = arrayList3;
                int i12 = i7;
                i8 = i5;
                z2 = i4;
                FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, arrayList, arrayList2, i, i12, true, this.mFragmentTransitionCallback);
            } else {
                arrayList4 = arrayList3;
                i8 = i5;
                z2 = i4;
            }
            moveToState(this.mCurState, z2);
        }
        for (int i13 = i; i13 < i8; i13++) {
            BackStackRecord backStackRecord2 = (BackStackRecord) arrayList5.get(i13);
            if (((Boolean) arrayList4.get(i13)).booleanValue() && backStackRecord2.mIndex >= 0) {
                backStackRecord2.mIndex = -1;
            }
            backStackRecord2.runOnCommitRunnables();
        }
        if (z4) {
            reportBackStackChanged();
        }
    }

    private void makeRemovedFragmentsInvisible(ArraySet<Fragment> arraySet) {
        int size = arraySet.size();
        for (int i = 0; i < size; i++) {
            Fragment fragment = (Fragment) arraySet.valueAt(i);
            if (!fragment.mAdded) {
                View requireView = fragment.requireView();
                fragment.mPostponedAlpha = requireView.getAlpha();
                requireView.setAlpha(0.0f);
            }
        }
    }

    private int postponePostponableTransactions(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2, ArraySet<Fragment> arraySet) {
        int i3 = i2;
        for (int i4 = i2 - 1; i4 >= i; i4--) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i4);
            boolean booleanValue = ((Boolean) arrayList2.get(i4)).booleanValue();
            if (backStackRecord.isPostponed() && !backStackRecord.interactsWith(arrayList, i4 + 1, i2)) {
                if (this.mPostponedTransactions == null) {
                    this.mPostponedTransactions = new ArrayList<>();
                }
                StartEnterTransitionListener startEnterTransitionListener = new StartEnterTransitionListener(backStackRecord, booleanValue);
                this.mPostponedTransactions.add(startEnterTransitionListener);
                backStackRecord.setOnStartPostponedListener(startEnterTransitionListener);
                if (booleanValue) {
                    backStackRecord.executeOps();
                } else {
                    backStackRecord.executePopOps(false);
                }
                i3--;
                if (i4 != i3) {
                    arrayList.remove(i4);
                    arrayList.add(i3, backStackRecord);
                }
                addAddedFragments(arraySet);
            }
        }
        return i3;
    }

    /* access modifiers changed from: 0000 */
    public void completeExecute(BackStackRecord backStackRecord, boolean z, boolean z2, boolean z3) {
        if (z) {
            backStackRecord.executePopOps(z3);
        } else {
            backStackRecord.executeOps();
        }
        ArrayList arrayList = new ArrayList(1);
        ArrayList arrayList2 = new ArrayList(1);
        arrayList.add(backStackRecord);
        arrayList2.add(Boolean.valueOf(z));
        if (z2 && this.mCurState >= 1) {
            FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, arrayList, arrayList2, 0, 1, true, this.mFragmentTransitionCallback);
        }
        if (z3) {
            moveToState(this.mCurState, true);
        }
        for (Fragment fragment : this.mFragmentStore.getActiveFragments()) {
            if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && backStackRecord.interactsWith(fragment.mContainerId)) {
                float f = fragment.mPostponedAlpha;
                if (f > 0.0f) {
                    fragment.mView.setAlpha(f);
                }
                if (z3) {
                    fragment.mPostponedAlpha = 0.0f;
                } else {
                    fragment.mPostponedAlpha = -1.0f;
                    fragment.mIsNewlyAdded = false;
                }
            }
        }
    }

    private static void executeOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2) {
        while (i < i2) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i);
            boolean z = true;
            if (((Boolean) arrayList2.get(i)).booleanValue()) {
                backStackRecord.bumpBackStackNesting(-1);
                if (i != i2 - 1) {
                    z = false;
                }
                backStackRecord.executePopOps(z);
            } else {
                backStackRecord.bumpBackStackNesting(1);
                backStackRecord.executeOps();
            }
            i++;
        }
    }

    private void setVisibleRemovingFragment(Fragment fragment) {
        ViewGroup fragmentContainer = getFragmentContainer(fragment);
        if (fragmentContainer != null) {
            if (fragmentContainer.getTag(R$id.visible_removing_fragment_view_tag) == null) {
                fragmentContainer.setTag(R$id.visible_removing_fragment_view_tag, fragment);
            }
            ((Fragment) fragmentContainer.getTag(R$id.visible_removing_fragment_view_tag)).setNextAnim(fragment.getNextAnim());
        }
    }

    private ViewGroup getFragmentContainer(Fragment fragment) {
        if (fragment.mContainerId > 0 && this.mContainer.onHasView()) {
            View onFindViewById = this.mContainer.onFindViewById(fragment.mContainerId);
            if (onFindViewById instanceof ViewGroup) {
                return (ViewGroup) onFindViewById;
            }
        }
        return null;
    }

    private void addAddedFragments(ArraySet<Fragment> arraySet) {
        int i = this.mCurState;
        if (i >= 1) {
            int min = Math.min(i, 3);
            for (Fragment fragment : this.mFragmentStore.getFragments()) {
                if (fragment.mState < min) {
                    moveToState(fragment, min);
                    if (fragment.mView != null && !fragment.mHidden && fragment.mIsNewlyAdded) {
                        arraySet.add(fragment);
                    }
                }
            }
        }
    }

    private void forcePostponedTransactions() {
        if (this.mPostponedTransactions != null) {
            while (!this.mPostponedTransactions.isEmpty()) {
                ((StartEnterTransitionListener) this.mPostponedTransactions.remove(0)).completeTransaction();
            }
        }
    }

    private void endAnimatingAwayFragments() {
        if (!this.mExitAnimationCancellationSignals.isEmpty()) {
            for (Fragment fragment : this.mExitAnimationCancellationSignals.keySet()) {
                cancelExitAnimation(fragment);
                moveToState(fragment);
            }
        }
    }

    private boolean generateOpsForPendingActions(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
        synchronized (this.mPendingActions) {
            if (this.mPendingActions.isEmpty()) {
                return false;
            }
            boolean z = false;
            for (int i = 0; i < this.mPendingActions.size(); i++) {
                z |= ((OpGenerator) this.mPendingActions.get(i)).generateOps(arrayList, arrayList2);
            }
            this.mPendingActions.clear();
            this.mHost.getHandler().removeCallbacks(this.mExecCommit);
            return z;
        }
    }

    private void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            this.mHavePendingDeferredStart = false;
            startPendingDeferredFragments();
        }
    }

    private void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i++) {
                ((OnBackStackChangedListener) this.mBackStackChangeListeners.get(i)).onBackStackChanged();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void addBackStackState(BackStackRecord backStackRecord) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList<>();
        }
        this.mBackStack.add(backStackRecord);
    }

    /* access modifiers changed from: 0000 */
    public boolean popBackStackState(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, String str, int i, int i2) {
        int i3;
        Boolean bool = Boolean.TRUE;
        ArrayList<BackStackRecord> arrayList3 = this.mBackStack;
        if (arrayList3 == null) {
            return false;
        }
        if (str == null && i < 0 && (i2 & 1) == 0) {
            int size = arrayList3.size() - 1;
            if (size < 0) {
                return false;
            }
            arrayList.add(this.mBackStack.remove(size));
            arrayList2.add(bool);
        } else {
            if (str != null || i >= 0) {
                int size2 = this.mBackStack.size() - 1;
                while (size2 >= 0) {
                    BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.get(size2);
                    if ((str != null && str.equals(backStackRecord.getName())) || (i >= 0 && i == backStackRecord.mIndex)) {
                        break;
                    }
                    size2--;
                }
                if (size2 < 0) {
                    return false;
                }
                if ((i2 & 1) != 0) {
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        BackStackRecord backStackRecord2 = (BackStackRecord) this.mBackStack.get(size2);
                        if ((str == null || !str.equals(backStackRecord2.getName())) && (i < 0 || i != backStackRecord2.mIndex)) {
                            break;
                        }
                    }
                }
                i3 = size2;
            } else {
                i3 = -1;
            }
            if (i3 == this.mBackStack.size() - 1) {
                return false;
            }
            for (int size3 = this.mBackStack.size() - 1; size3 > i3; size3--) {
                arrayList.add(this.mBackStack.remove(size3));
                arrayList2.add(bool);
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public Parcelable saveAllState() {
        forcePostponedTransactions();
        endAnimatingAwayFragments();
        execPendingActions(true);
        this.mStateSaved = true;
        this.mNonConfig.setIsStateSaved(true);
        ArrayList<FragmentState> saveActiveFragments = this.mFragmentStore.saveActiveFragments();
        String str = "FragmentManager";
        BackStackState[] backStackStateArr = null;
        if (saveActiveFragments.isEmpty()) {
            if (isLoggingEnabled(2)) {
                Log.v(str, "saveAllState: no fragments!");
            }
            return null;
        }
        ArrayList<String> saveAddedFragments = this.mFragmentStore.saveAddedFragments();
        ArrayList<BackStackRecord> arrayList = this.mBackStack;
        if (arrayList != null) {
            int size = arrayList.size();
            if (size > 0) {
                backStackStateArr = new BackStackState[size];
                for (int i = 0; i < size; i++) {
                    backStackStateArr[i] = new BackStackState((BackStackRecord) this.mBackStack.get(i));
                    if (isLoggingEnabled(2)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("saveAllState: adding back stack #");
                        sb.append(i);
                        sb.append(": ");
                        sb.append(this.mBackStack.get(i));
                        Log.v(str, sb.toString());
                    }
                }
            }
        }
        FragmentManagerState fragmentManagerState = new FragmentManagerState();
        fragmentManagerState.mActive = saveActiveFragments;
        fragmentManagerState.mAdded = saveAddedFragments;
        fragmentManagerState.mBackStack = backStackStateArr;
        fragmentManagerState.mBackStackIndex = this.mBackStackIndex.get();
        Fragment fragment = this.mPrimaryNav;
        if (fragment != null) {
            fragmentManagerState.mPrimaryNavActiveWho = fragment.mWho;
        }
        return fragmentManagerState;
    }

    /* access modifiers changed from: 0000 */
    public void restoreSaveState(Parcelable parcelable) {
        String str;
        String str2;
        FragmentStateManager fragmentStateManager;
        if (parcelable != null) {
            FragmentManagerState fragmentManagerState = (FragmentManagerState) parcelable;
            if (fragmentManagerState.mActive != null) {
                this.mFragmentStore.resetActiveFragments();
                Iterator it = fragmentManagerState.mActive.iterator();
                while (true) {
                    str = "): ";
                    str2 = "FragmentManager";
                    if (!it.hasNext()) {
                        break;
                    }
                    FragmentState fragmentState = (FragmentState) it.next();
                    if (fragmentState != null) {
                        Fragment findRetainedFragmentByWho = this.mNonConfig.findRetainedFragmentByWho(fragmentState.mWho);
                        if (findRetainedFragmentByWho != null) {
                            if (isLoggingEnabled(2)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("restoreSaveState: re-attaching retained ");
                                sb.append(findRetainedFragmentByWho);
                                Log.v(str2, sb.toString());
                            }
                            fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, findRetainedFragmentByWho, fragmentState);
                        } else {
                            fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, this.mHost.getContext().getClassLoader(), getFragmentFactory(), fragmentState);
                        }
                        Fragment fragment = fragmentStateManager.getFragment();
                        fragment.mFragmentManager = this;
                        if (isLoggingEnabled(2)) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("restoreSaveState: active (");
                            sb2.append(fragment.mWho);
                            sb2.append(str);
                            sb2.append(fragment);
                            Log.v(str2, sb2.toString());
                        }
                        fragmentStateManager.restoreState(this.mHost.getContext().getClassLoader());
                        this.mFragmentStore.makeActive(fragmentStateManager);
                        fragmentStateManager.setFragmentManagerState(this.mCurState);
                    }
                }
                for (Fragment fragment2 : this.mNonConfig.getRetainedFragments()) {
                    if (!this.mFragmentStore.containsActiveFragment(fragment2.mWho)) {
                        if (isLoggingEnabled(2)) {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("Discarding retained Fragment ");
                            sb3.append(fragment2);
                            sb3.append(" that was not found in the set of active Fragments ");
                            sb3.append(fragmentManagerState.mActive);
                            Log.v(str2, sb3.toString());
                        }
                        fragment2.mFragmentManager = this;
                        FragmentStateManager fragmentStateManager2 = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, fragment2);
                        fragmentStateManager2.setFragmentManagerState(1);
                        fragmentStateManager2.moveToExpectedState();
                        fragment2.mRemoving = true;
                        fragmentStateManager2.moveToExpectedState();
                    }
                }
                this.mFragmentStore.restoreAddedFragments(fragmentManagerState.mAdded);
                if (fragmentManagerState.mBackStack != null) {
                    this.mBackStack = new ArrayList<>(fragmentManagerState.mBackStack.length);
                    int i = 0;
                    while (true) {
                        BackStackState[] backStackStateArr = fragmentManagerState.mBackStack;
                        if (i >= backStackStateArr.length) {
                            break;
                        }
                        BackStackRecord instantiate = backStackStateArr[i].instantiate(this);
                        if (isLoggingEnabled(2)) {
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("restoreAllState: back stack #");
                            sb4.append(i);
                            sb4.append(" (index ");
                            sb4.append(instantiate.mIndex);
                            sb4.append(str);
                            sb4.append(instantiate);
                            Log.v(str2, sb4.toString());
                            PrintWriter printWriter = new PrintWriter(new LogWriter(str2));
                            instantiate.dump("  ", printWriter, false);
                            printWriter.close();
                        }
                        this.mBackStack.add(instantiate);
                        i++;
                    }
                } else {
                    this.mBackStack = null;
                }
                this.mBackStackIndex.set(fragmentManagerState.mBackStackIndex);
                String str3 = fragmentManagerState.mPrimaryNavActiveWho;
                if (str3 != null) {
                    Fragment findActiveFragment = findActiveFragment(str3);
                    this.mPrimaryNav = findActiveFragment;
                    dispatchParentPrimaryNavigationFragmentChanged(findActiveFragment);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public FragmentHostCallback<?> getHost() {
        return this.mHost;
    }

    /* access modifiers changed from: 0000 */
    public Fragment getParent() {
        return this.mParent;
    }

    /* access modifiers changed from: 0000 */
    public FragmentContainer getContainer() {
        return this.mContainer;
    }

    /* access modifiers changed from: 0000 */
    public FragmentStore getFragmentStore() {
        return this.mFragmentStore;
    }

    /* JADX WARNING: type inference failed for: r4v7, types: [androidx.lifecycle.LifecycleOwner] */
    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void attachController(androidx.fragment.app.FragmentHostCallback<?> r3, androidx.fragment.app.FragmentContainer r4, androidx.fragment.app.Fragment r5) {
        /*
            r2 = this;
            androidx.fragment.app.FragmentHostCallback<?> r0 = r2.mHost
            if (r0 != 0) goto L_0x005b
            r2.mHost = r3
            r2.mContainer = r4
            r2.mParent = r5
            if (r5 == 0) goto L_0x000f
            r2.updateOnBackPressedCallbackEnabled()
        L_0x000f:
            boolean r4 = r3 instanceof androidx.activity.OnBackPressedDispatcherOwner
            if (r4 == 0) goto L_0x0026
            r4 = r3
            androidx.activity.OnBackPressedDispatcherOwner r4 = (androidx.activity.OnBackPressedDispatcherOwner) r4
            androidx.activity.OnBackPressedDispatcher r0 = r4.getOnBackPressedDispatcher()
            r2.mOnBackPressedDispatcher = r0
            if (r5 == 0) goto L_0x001f
            r4 = r5
        L_0x001f:
            androidx.activity.OnBackPressedDispatcher r0 = r2.mOnBackPressedDispatcher
            androidx.activity.OnBackPressedCallback r1 = r2.mOnBackPressedCallback
            r0.addCallback(r4, r1)
        L_0x0026:
            if (r5 == 0) goto L_0x0031
            androidx.fragment.app.FragmentManager r3 = r5.mFragmentManager
            androidx.fragment.app.FragmentManagerViewModel r3 = r3.getChildNonConfig(r5)
            r2.mNonConfig = r3
            goto L_0x004a
        L_0x0031:
            boolean r4 = r3 instanceof androidx.lifecycle.ViewModelStoreOwner
            if (r4 == 0) goto L_0x0042
            androidx.lifecycle.ViewModelStoreOwner r3 = (androidx.lifecycle.ViewModelStoreOwner) r3
            androidx.lifecycle.ViewModelStore r3 = r3.getViewModelStore()
            androidx.fragment.app.FragmentManagerViewModel r3 = androidx.fragment.app.FragmentManagerViewModel.getInstance(r3)
            r2.mNonConfig = r3
            goto L_0x004a
        L_0x0042:
            androidx.fragment.app.FragmentManagerViewModel r3 = new androidx.fragment.app.FragmentManagerViewModel
            r4 = 0
            r3.<init>(r4)
            r2.mNonConfig = r3
        L_0x004a:
            androidx.fragment.app.FragmentManagerViewModel r3 = r2.mNonConfig
            boolean r4 = r2.isStateSaved()
            r3.setIsStateSaved(r4)
            androidx.fragment.app.FragmentStore r3 = r2.mFragmentStore
            androidx.fragment.app.FragmentManagerViewModel r2 = r2.mNonConfig
            r3.setNonConfig(r2)
            return
        L_0x005b:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.String r3 = "Already attached"
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.FragmentManager.attachController(androidx.fragment.app.FragmentHostCallback, androidx.fragment.app.FragmentContainer, androidx.fragment.app.Fragment):void");
    }

    /* access modifiers changed from: 0000 */
    public void noteStateNotSaved() {
        if (this.mHost != null) {
            this.mStateSaved = false;
            this.mStopped = false;
            this.mNonConfig.setIsStateSaved(false);
            for (Fragment fragment : this.mFragmentStore.getFragments()) {
                if (fragment != null) {
                    fragment.noteStateNotSaved();
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchAttach() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(0);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchCreate() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(1);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(2);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchStart() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(3);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchResume() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(4);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchPause() {
        dispatchStateChange(3);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchStop() {
        this.mStopped = true;
        this.mNonConfig.setIsStateSaved(true);
        dispatchStateChange(2);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchDestroyView() {
        dispatchStateChange(1);
    }

    /* access modifiers changed from: 0000 */
    public void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions(true);
        endAnimatingAwayFragments();
        dispatchStateChange(-1);
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
        if (this.mOnBackPressedDispatcher != null) {
            this.mOnBackPressedCallback.remove();
            this.mOnBackPressedDispatcher = null;
        }
    }

    /* JADX INFO: finally extract failed */
    private void dispatchStateChange(int i) {
        try {
            this.mExecutingActions = true;
            this.mFragmentStore.dispatchStateChange(i);
            moveToState(i, false);
            this.mExecutingActions = false;
            execPendingActions(true);
        } catch (Throwable th) {
            this.mExecutingActions = false;
            throw th;
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchMultiWindowModeChanged(boolean z) {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performMultiWindowModeChanged(z);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchPictureInPictureModeChanged(boolean z) {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performPictureInPictureModeChanged(z);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchConfigurationChanged(Configuration configuration) {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performConfigurationChanged(configuration);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchLowMemory() {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performLowMemory();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (this.mCurState < 1) {
            return false;
        }
        ArrayList<Fragment> arrayList = null;
        boolean z = false;
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performCreateOptionsMenu(menu, menuInflater)) {
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                arrayList.add(fragment);
                z = true;
            }
        }
        if (this.mCreatedMenus != null) {
            for (int i = 0; i < this.mCreatedMenus.size(); i++) {
                Fragment fragment2 = (Fragment) this.mCreatedMenus.get(i);
                if (arrayList == null || !arrayList.contains(fragment2)) {
                    fragment2.onDestroyOptionsMenu();
                }
            }
        }
        this.mCreatedMenus = arrayList;
        return z;
    }

    /* access modifiers changed from: 0000 */
    public boolean dispatchPrepareOptionsMenu(Menu menu) {
        boolean z = false;
        if (this.mCurState < 1) {
            return false;
        }
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                z = true;
            }
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public boolean dispatchOptionsItemSelected(MenuItem menuItem) {
        if (this.mCurState < 1) {
            return false;
        }
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public boolean dispatchContextItemSelected(MenuItem menuItem) {
        if (this.mCurState < 1) {
            return false;
        }
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public void dispatchOptionsMenuClosed(Menu menu) {
        if (this.mCurState >= 1) {
            for (Fragment fragment : this.mFragmentStore.getFragments()) {
                if (fragment != null) {
                    fragment.performOptionsMenuClosed(menu);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setPrimaryNavigationFragment(Fragment fragment) {
        if (fragment == null || (fragment.equals(findActiveFragment(fragment.mWho)) && (fragment.mHost == null || fragment.mFragmentManager == this))) {
            Fragment fragment2 = this.mPrimaryNav;
            this.mPrimaryNav = fragment;
            dispatchParentPrimaryNavigationFragmentChanged(fragment2);
            dispatchParentPrimaryNavigationFragmentChanged(this.mPrimaryNav);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Fragment ");
        sb.append(fragment);
        sb.append(" is not an active fragment of FragmentManager ");
        sb.append(this);
        throw new IllegalArgumentException(sb.toString());
    }

    private void dispatchParentPrimaryNavigationFragmentChanged(Fragment fragment) {
        if (fragment != null && fragment.equals(findActiveFragment(fragment.mWho))) {
            fragment.performPrimaryNavigationFragmentChanged();
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchPrimaryNavigationFragmentChanged() {
        updateOnBackPressedCallbackEnabled();
        dispatchParentPrimaryNavigationFragmentChanged(this.mPrimaryNav);
    }

    public Fragment getPrimaryNavigationFragment() {
        return this.mPrimaryNav;
    }

    /* access modifiers changed from: 0000 */
    public void setMaxLifecycle(Fragment fragment, State state) {
        if (!fragment.equals(findActiveFragment(fragment.mWho)) || !(fragment.mHost == null || fragment.mFragmentManager == this)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Fragment ");
            sb.append(fragment);
            sb.append(" is not an active fragment of FragmentManager ");
            sb.append(this);
            throw new IllegalArgumentException(sb.toString());
        }
        fragment.mMaxState = state;
    }

    public FragmentFactory getFragmentFactory() {
        FragmentFactory fragmentFactory = this.mFragmentFactory;
        if (fragmentFactory != null) {
            return fragmentFactory;
        }
        Fragment fragment = this.mParent;
        if (fragment != null) {
            return fragment.mFragmentManager.getFragmentFactory();
        }
        return this.mHostFragmentFactory;
    }

    /* access modifiers changed from: 0000 */
    public SpecialEffectsControllerFactory getSpecialEffectsControllerFactory() {
        SpecialEffectsControllerFactory specialEffectsControllerFactory = this.mSpecialEffectsControllerFactory;
        if (specialEffectsControllerFactory != null) {
            return specialEffectsControllerFactory;
        }
        Fragment fragment = this.mParent;
        if (fragment != null) {
            return fragment.mFragmentManager.getSpecialEffectsControllerFactory();
        }
        return this.mDefaultSpecialEffectsControllerFactory;
    }

    /* access modifiers changed from: 0000 */
    public FragmentLifecycleCallbacksDispatcher getLifecycleCallbacksDispatcher() {
        return this.mLifecycleCallbacksDispatcher;
    }

    /* access modifiers changed from: 0000 */
    public boolean checkForMenus() {
        boolean z = false;
        for (Fragment fragment : this.mFragmentStore.getActiveFragments()) {
            if (fragment != null) {
                z = isMenuAvailable(fragment);
                continue;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    private boolean isMenuAvailable(Fragment fragment) {
        return (fragment.mHasMenu && fragment.mMenuVisible) || fragment.mChildFragmentManager.checkForMenus();
    }

    /* access modifiers changed from: 0000 */
    public Factory2 getLayoutInflaterFactory() {
        return this.mLayoutInflaterFactory;
    }
}
