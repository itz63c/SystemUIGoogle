package androidx.leanback.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.R$dimen;
import androidx.leanback.R$fraction;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.R$styleable;
import androidx.leanback.R$transition;
import androidx.leanback.app.HeadersSupportFragment.OnHeaderClickedListener;
import androidx.leanback.app.HeadersSupportFragment.OnHeaderViewSelectedListener;
import androidx.leanback.transition.TransitionHelper;
import androidx.leanback.transition.TransitionListener;
import androidx.leanback.util.StateMachine.Event;
import androidx.leanback.util.StateMachine.State;
import androidx.leanback.widget.BrowseFrameLayout;
import androidx.leanback.widget.BrowseFrameLayout.OnChildFocusListener;
import androidx.leanback.widget.BrowseFrameLayout.OnFocusSearchListener;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowHeaderPresenter.ViewHolder;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.ScaleFrameLayout;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import java.util.HashMap;
import java.util.Map;

public class BrowseSupportFragment extends BaseSupportFragment {
    private static final String ARG_HEADERS_STATE;
    private static final String ARG_TITLE;
    final Event EVT_HEADER_VIEW_CREATED = new Event("headerFragmentViewCreated");
    final Event EVT_MAIN_FRAGMENT_VIEW_CREATED = new Event("mainFragmentViewCreated");
    final Event EVT_SCREEN_DATA_READY = new Event("screenDataReady");
    final State STATE_SET_ENTRANCE_START_STATE = new State("SET_ENTRANCE_START_STATE") {
        public void run() {
            BrowseSupportFragment.this.setEntranceTransitionStartState();
        }
    };
    private ObjectAdapter mAdapter;
    BackStackListener mBackStackChangedListener;
    private int mBrandColor = 0;
    private boolean mBrandColorSet;
    BrowseFrameLayout mBrowseFrame;
    BrowseTransitionListener mBrowseTransitionListener;
    boolean mCanShowHeaders = true;
    private int mContainerListAlignTop;
    private int mContainerListMarginStart;
    OnItemViewSelectedListener mExternalOnItemViewSelectedListener;
    private OnHeaderClickedListener mHeaderClickedListener = new OnHeaderClickedListener() {
        public void onHeaderClicked(ViewHolder viewHolder, Row row) {
            BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
            if (browseSupportFragment.mCanShowHeaders && browseSupportFragment.mShowingHeaders && !browseSupportFragment.isInHeadersTransition()) {
                Fragment fragment = BrowseSupportFragment.this.mMainFragment;
                if (fragment != null && fragment.getView() != null) {
                    BrowseSupportFragment.this.startHeadersTransitionInternal(false);
                    BrowseSupportFragment.this.mMainFragment.getView().requestFocus();
                }
            }
        }
    };
    private PresenterSelector mHeaderPresenterSelector;
    private OnHeaderViewSelectedListener mHeaderViewSelectedListener = new OnHeaderViewSelectedListener() {
        public void onHeaderSelected(ViewHolder viewHolder, Row row) {
            int selectedPosition = BrowseSupportFragment.this.mHeadersSupportFragment.getSelectedPosition();
            BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
            if (browseSupportFragment.mShowingHeaders) {
                browseSupportFragment.onRowSelected(selectedPosition);
            }
        }
    };
    boolean mHeadersBackStackEnabled = true;
    private int mHeadersState = 1;
    HeadersSupportFragment mHeadersSupportFragment;
    Object mHeadersTransition;
    boolean mIsPageRow;
    Fragment mMainFragment;
    MainFragmentAdapter mMainFragmentAdapter;
    private MainFragmentAdapterRegistry mMainFragmentAdapterRegistry = new MainFragmentAdapterRegistry();
    ListRowDataAdapter mMainFragmentListRowDataAdapter;
    MainFragmentRowsAdapter mMainFragmentRowsAdapter;
    private boolean mMainFragmentScaleEnabled = true;
    private final OnChildFocusListener mOnChildFocusListener = new OnChildFocusListener() {
        public boolean onRequestFocusInDescendants(int i, Rect rect) {
            boolean z = true;
            if (BrowseSupportFragment.this.getChildFragmentManager().isDestroyed()) {
                return true;
            }
            BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
            if (browseSupportFragment.mCanShowHeaders && browseSupportFragment.mShowingHeaders) {
                HeadersSupportFragment headersSupportFragment = browseSupportFragment.mHeadersSupportFragment;
                if (!(headersSupportFragment == null || headersSupportFragment.getView() == null || !BrowseSupportFragment.this.mHeadersSupportFragment.getView().requestFocus(i, rect))) {
                    return true;
                }
            }
            Fragment fragment = BrowseSupportFragment.this.mMainFragment;
            if (fragment != null && fragment.getView() != null && BrowseSupportFragment.this.mMainFragment.getView().requestFocus(i, rect)) {
                return true;
            }
            if (BrowseSupportFragment.this.getTitleView() == null || !BrowseSupportFragment.this.getTitleView().requestFocus(i, rect)) {
                z = false;
            }
            return z;
        }

        public void onRequestChildFocus(View view, View view2) {
            if (!BrowseSupportFragment.this.getChildFragmentManager().isDestroyed()) {
                BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
                if (browseSupportFragment.mCanShowHeaders && !browseSupportFragment.isInHeadersTransition()) {
                    int id = view.getId();
                    if (id == R$id.browse_container_dock) {
                        BrowseSupportFragment browseSupportFragment2 = BrowseSupportFragment.this;
                        if (browseSupportFragment2.mShowingHeaders) {
                            browseSupportFragment2.startHeadersTransitionInternal(false);
                        }
                    }
                    if (id == R$id.browse_headers_dock) {
                        BrowseSupportFragment browseSupportFragment3 = BrowseSupportFragment.this;
                        if (!browseSupportFragment3.mShowingHeaders) {
                            browseSupportFragment3.startHeadersTransitionInternal(true);
                        }
                    }
                }
            }
        }
    };
    private final OnFocusSearchListener mOnFocusSearchListener = new OnFocusSearchListener() {
        public View onFocusSearch(View view, int i) {
            BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
            if (browseSupportFragment.mCanShowHeaders && browseSupportFragment.isInHeadersTransition()) {
                return view;
            }
            if (BrowseSupportFragment.this.getTitleView() != null && view != BrowseSupportFragment.this.getTitleView() && i == 33) {
                return BrowseSupportFragment.this.getTitleView();
            }
            if (BrowseSupportFragment.this.getTitleView() == null || !BrowseSupportFragment.this.getTitleView().hasFocus() || i != 130) {
                boolean z = true;
                if (ViewCompat.getLayoutDirection(view) != 1) {
                    z = false;
                }
                int i2 = 66;
                int i3 = z ? 66 : 17;
                if (z) {
                    i2 = 17;
                }
                BrowseSupportFragment browseSupportFragment2 = BrowseSupportFragment.this;
                if (browseSupportFragment2.mCanShowHeaders && i == i3) {
                    if (!browseSupportFragment2.isVerticalScrolling()) {
                        BrowseSupportFragment browseSupportFragment3 = BrowseSupportFragment.this;
                        if (!browseSupportFragment3.mShowingHeaders && browseSupportFragment3.isHeadersDataReady()) {
                            return BrowseSupportFragment.this.mHeadersSupportFragment.getVerticalGridView();
                        }
                    }
                    return view;
                } else if (i == i2) {
                    if (BrowseSupportFragment.this.isVerticalScrolling()) {
                        return view;
                    }
                    Fragment fragment = BrowseSupportFragment.this.mMainFragment;
                    return (fragment == null || fragment.getView() == null) ? view : BrowseSupportFragment.this.mMainFragment.getView();
                } else if (i != 130 || !BrowseSupportFragment.this.mShowingHeaders) {
                    return null;
                } else {
                    return view;
                }
            } else {
                BrowseSupportFragment browseSupportFragment4 = BrowseSupportFragment.this;
                return (!browseSupportFragment4.mCanShowHeaders || !browseSupportFragment4.mShowingHeaders) ? BrowseSupportFragment.this.mMainFragment.getView() : browseSupportFragment4.mHeadersSupportFragment.getVerticalGridView();
            }
        }
    };
    private OnItemViewClickedListener mOnItemViewClickedListener;
    Object mPageRow;
    private float mScaleFactor;
    private ScaleFrameLayout mScaleFrameLayout;
    private Object mSceneAfterEntranceTransition;
    Object mSceneWithHeaders;
    Object mSceneWithoutHeaders;
    private int mSelectedPosition = -1;
    private final SetSelectionRunnable mSetSelectionRunnable = new SetSelectionRunnable();
    boolean mShowingHeaders = true;
    boolean mStopped = true;
    private final OnScrollListener mWaitScrollFinishAndCommitMainFragment = new OnScrollListener() {
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0) {
                recyclerView.removeOnScrollListener(this);
                BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
                if (!browseSupportFragment.mStopped) {
                    browseSupportFragment.commitMainFragment();
                }
            }
        }
    };
    String mWithHeadersBackStackName;

    final class BackStackListener implements OnBackStackChangedListener {
        int mIndexOfHeadersBackStack = -1;
        int mLastEntryCount;

        BackStackListener() {
            this.mLastEntryCount = BrowseSupportFragment.this.getFragmentManager().getBackStackEntryCount();
        }

        /* access modifiers changed from: 0000 */
        public void load(Bundle bundle) {
            if (bundle != null) {
                int i = bundle.getInt("headerStackIndex", -1);
                this.mIndexOfHeadersBackStack = i;
                BrowseSupportFragment.this.mShowingHeaders = i == -1;
                return;
            }
            BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
            if (!browseSupportFragment.mShowingHeaders) {
                FragmentTransaction beginTransaction = browseSupportFragment.getFragmentManager().beginTransaction();
                beginTransaction.addToBackStack(BrowseSupportFragment.this.mWithHeadersBackStackName);
                beginTransaction.commit();
            }
        }

        /* access modifiers changed from: 0000 */
        public void save(Bundle bundle) {
            bundle.putInt("headerStackIndex", this.mIndexOfHeadersBackStack);
        }

        public void onBackStackChanged() {
            if (BrowseSupportFragment.this.getFragmentManager() == null) {
                Log.w("BrowseSupportFragment", "getFragmentManager() is null, stack:", new Exception());
                return;
            }
            int backStackEntryCount = BrowseSupportFragment.this.getFragmentManager().getBackStackEntryCount();
            int i = this.mLastEntryCount;
            if (backStackEntryCount > i) {
                int i2 = backStackEntryCount - 1;
                if (BrowseSupportFragment.this.mWithHeadersBackStackName.equals(BrowseSupportFragment.this.getFragmentManager().getBackStackEntryAt(i2).getName())) {
                    this.mIndexOfHeadersBackStack = i2;
                }
            } else if (backStackEntryCount < i && this.mIndexOfHeadersBackStack >= backStackEntryCount) {
                if (!BrowseSupportFragment.this.isHeadersDataReady()) {
                    FragmentTransaction beginTransaction = BrowseSupportFragment.this.getFragmentManager().beginTransaction();
                    beginTransaction.addToBackStack(BrowseSupportFragment.this.mWithHeadersBackStackName);
                    beginTransaction.commit();
                    return;
                }
                this.mIndexOfHeadersBackStack = -1;
                BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
                if (!browseSupportFragment.mShowingHeaders) {
                    browseSupportFragment.startHeadersTransitionInternal(true);
                }
            }
            this.mLastEntryCount = backStackEntryCount;
        }
    }

    public static class BrowseTransitionListener {
        public abstract void onHeadersTransitionStart(boolean z);

        public abstract void onHeadersTransitionStop(boolean z);
    }

    private class ExpandPreLayout implements OnPreDrawListener {
        private final Runnable mCallback;
        private int mState;
        private final View mView;
        private MainFragmentAdapter mainFragmentAdapter;

        ExpandPreLayout(Runnable runnable, MainFragmentAdapter mainFragmentAdapter2, View view) {
            this.mView = view;
            this.mCallback = runnable;
            this.mainFragmentAdapter = mainFragmentAdapter2;
        }

        /* access modifiers changed from: 0000 */
        public void execute() {
            this.mView.getViewTreeObserver().addOnPreDrawListener(this);
            this.mainFragmentAdapter.setExpand(false);
            this.mView.invalidate();
            this.mState = 0;
        }

        public boolean onPreDraw() {
            if (BrowseSupportFragment.this.getView() == null || BrowseSupportFragment.this.getContext() == null) {
                this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
            int i = this.mState;
            if (i == 0) {
                this.mainFragmentAdapter.setExpand(true);
                this.mView.invalidate();
                this.mState = 1;
            } else if (i == 1) {
                this.mCallback.run();
                this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
                this.mState = 2;
            }
            return false;
        }
    }

    public static abstract class FragmentFactory<T extends Fragment> {
        public abstract T createFragment(Object obj);
    }

    public interface FragmentHost {
        void notifyViewCreated(MainFragmentAdapter mainFragmentAdapter);

        void showTitleView(boolean z);
    }

    private final class FragmentHostImpl implements FragmentHost {
        boolean mShowTitleView = true;

        FragmentHostImpl() {
        }

        public void notifyViewCreated(MainFragmentAdapter mainFragmentAdapter) {
            BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
            browseSupportFragment.mStateMachine.fireEvent(browseSupportFragment.EVT_MAIN_FRAGMENT_VIEW_CREATED);
            BrowseSupportFragment browseSupportFragment2 = BrowseSupportFragment.this;
            if (!browseSupportFragment2.mIsPageRow) {
                browseSupportFragment2.mStateMachine.fireEvent(browseSupportFragment2.EVT_SCREEN_DATA_READY);
            }
        }

        public void showTitleView(boolean z) {
            this.mShowTitleView = z;
            MainFragmentAdapter mainFragmentAdapter = BrowseSupportFragment.this.mMainFragmentAdapter;
            if (mainFragmentAdapter != null && mainFragmentAdapter.getFragmentHost() == this) {
                BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
                if (browseSupportFragment.mIsPageRow) {
                    browseSupportFragment.updateTitleViewVisibility();
                }
            }
        }
    }

    public static class ListRowFragmentFactory extends FragmentFactory<RowsSupportFragment> {
        public RowsSupportFragment createFragment(Object obj) {
            return new RowsSupportFragment();
        }
    }

    public static class MainFragmentAdapter<T extends Fragment> {
        private final T mFragment;
        FragmentHostImpl mFragmentHost;
        private boolean mScalingEnabled;

        public boolean isScrolling() {
            return false;
        }

        public void onTransitionEnd() {
        }

        public boolean onTransitionPrepare() {
            return false;
        }

        public void onTransitionStart() {
        }

        public void setAlignment(int i) {
        }

        public void setEntranceTransitionState(boolean z) {
        }

        public void setExpand(boolean z) {
        }

        public MainFragmentAdapter(T t) {
            this.mFragment = t;
        }

        public final T getFragment() {
            return this.mFragment;
        }

        public boolean isScalingEnabled() {
            return this.mScalingEnabled;
        }

        public void setScalingEnabled(boolean z) {
            this.mScalingEnabled = z;
        }

        public final FragmentHost getFragmentHost() {
            return this.mFragmentHost;
        }

        /* access modifiers changed from: 0000 */
        public void setFragmentHost(FragmentHostImpl fragmentHostImpl) {
            this.mFragmentHost = fragmentHostImpl;
        }
    }

    public interface MainFragmentAdapterProvider {
        MainFragmentAdapter getMainFragmentAdapter();
    }

    public static final class MainFragmentAdapterRegistry {
        private static final FragmentFactory sDefaultFragmentFactory = new ListRowFragmentFactory();
        private final Map<Class<?>, FragmentFactory> mItemToFragmentFactoryMapping = new HashMap();

        public MainFragmentAdapterRegistry() {
            registerFragment(ListRow.class, sDefaultFragmentFactory);
        }

        public void registerFragment(Class<?> cls, FragmentFactory fragmentFactory) {
            this.mItemToFragmentFactoryMapping.put(cls, fragmentFactory);
        }

        public Fragment createFragment(Object obj) {
            FragmentFactory fragmentFactory;
            FragmentFactory fragmentFactory2 = sDefaultFragmentFactory;
            if (obj == null) {
                fragmentFactory = fragmentFactory2;
            } else {
                fragmentFactory = (FragmentFactory) this.mItemToFragmentFactoryMapping.get(obj.getClass());
            }
            if (fragmentFactory != null) {
                fragmentFactory2 = fragmentFactory;
            }
            return fragmentFactory2.createFragment(obj);
        }
    }

    class MainFragmentItemViewSelectedListener implements OnItemViewSelectedListener {
        MainFragmentRowsAdapter mMainFragmentRowsAdapter;

        public MainFragmentItemViewSelectedListener(MainFragmentRowsAdapter mainFragmentRowsAdapter) {
            this.mMainFragmentRowsAdapter = mainFragmentRowsAdapter;
        }

        public void onItemSelected(Presenter.ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, Row row) {
            BrowseSupportFragment.this.onRowSelected(this.mMainFragmentRowsAdapter.getSelectedPosition());
            OnItemViewSelectedListener onItemViewSelectedListener = BrowseSupportFragment.this.mExternalOnItemViewSelectedListener;
            if (onItemViewSelectedListener != null) {
                onItemViewSelectedListener.onItemSelected(viewHolder, obj, viewHolder2, row);
            }
        }
    }

    public static class MainFragmentRowsAdapter<T extends Fragment> {
        private final T mFragment;

        public abstract int getSelectedPosition();

        public abstract void setAdapter(ObjectAdapter objectAdapter);

        public abstract void setOnItemViewClickedListener(OnItemViewClickedListener onItemViewClickedListener);

        public abstract void setOnItemViewSelectedListener(OnItemViewSelectedListener onItemViewSelectedListener);

        public abstract void setSelectedPosition(int i, boolean z);

        public MainFragmentRowsAdapter(T t) {
            if (t != null) {
                this.mFragment = t;
                return;
            }
            throw new IllegalArgumentException("Fragment can't be null");
        }

        public final T getFragment() {
            return this.mFragment;
        }
    }

    public interface MainFragmentRowsAdapterProvider {
        MainFragmentRowsAdapter getMainFragmentRowsAdapter();
    }

    private final class SetSelectionRunnable implements Runnable {
        private int mPosition;
        private boolean mSmooth;
        private int mType;

        SetSelectionRunnable() {
            reset();
        }

        /* access modifiers changed from: 0000 */
        public void post(int i, int i2, boolean z) {
            if (i2 >= this.mType) {
                this.mPosition = i;
                this.mType = i2;
                this.mSmooth = z;
                BrowseSupportFragment.this.mBrowseFrame.removeCallbacks(this);
                BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
                if (!browseSupportFragment.mStopped) {
                    browseSupportFragment.mBrowseFrame.post(this);
                }
            }
        }

        public void run() {
            BrowseSupportFragment.this.setSelection(this.mPosition, this.mSmooth);
            reset();
        }

        public void stop() {
            BrowseSupportFragment.this.mBrowseFrame.removeCallbacks(this);
        }

        public void start() {
            if (this.mType != -1) {
                BrowseSupportFragment.this.mBrowseFrame.post(this);
            }
        }

        private void reset() {
            this.mPosition = -1;
            this.mType = -1;
            this.mSmooth = false;
        }
    }

    /* access modifiers changed from: 0000 */
    public void createStateMachineStates() {
        super.createStateMachineStates();
        this.mStateMachine.addState(this.STATE_SET_ENTRANCE_START_STATE);
    }

    /* access modifiers changed from: 0000 */
    public void createStateMachineTransitions() {
        super.createStateMachineTransitions();
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_SET_ENTRANCE_START_STATE, this.EVT_HEADER_VIEW_CREATED);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.EVT_MAIN_FRAGMENT_VIEW_CREATED);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_PERFORM, this.EVT_SCREEN_DATA_READY);
    }

    private boolean createMainFragment(ObjectAdapter objectAdapter, int i) {
        Object obj;
        Object obj2 = null;
        boolean z = true;
        if (!this.mCanShowHeaders) {
            obj = null;
        } else if (objectAdapter == null || objectAdapter.size() == 0) {
            return false;
        } else {
            if (i < 0) {
                i = 0;
            } else if (i >= objectAdapter.size()) {
                throw new IllegalArgumentException(String.format("Invalid position %d requested", new Object[]{Integer.valueOf(i)}));
            }
            obj = objectAdapter.get(i);
        }
        boolean z2 = this.mIsPageRow;
        Object obj3 = this.mPageRow;
        boolean z3 = this.mCanShowHeaders;
        this.mIsPageRow = false;
        if (0 != 0) {
            obj2 = obj;
        }
        this.mPageRow = obj2;
        if (this.mMainFragment != null) {
            if (!z2) {
                z = this.mIsPageRow;
            } else if (this.mIsPageRow && (obj3 == null || obj3 == obj2)) {
                z = false;
            }
        }
        if (z) {
            Fragment createFragment = this.mMainFragmentAdapterRegistry.createFragment(obj);
            this.mMainFragment = createFragment;
            if (createFragment instanceof MainFragmentAdapterProvider) {
                setMainFragmentAdapter();
            } else {
                throw new IllegalArgumentException("Fragment must implement MainFragmentAdapterProvider");
            }
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public void setMainFragmentAdapter() {
        MainFragmentAdapter mainFragmentAdapter = ((MainFragmentAdapterProvider) this.mMainFragment).getMainFragmentAdapter();
        this.mMainFragmentAdapter = mainFragmentAdapter;
        mainFragmentAdapter.setFragmentHost(new FragmentHostImpl());
        if (!this.mIsPageRow) {
            Fragment fragment = this.mMainFragment;
            if (fragment instanceof MainFragmentRowsAdapterProvider) {
                setMainFragmentRowsAdapter(((MainFragmentRowsAdapterProvider) fragment).getMainFragmentRowsAdapter());
            } else {
                setMainFragmentRowsAdapter(null);
            }
            this.mIsPageRow = this.mMainFragmentRowsAdapter == null;
            return;
        }
        setMainFragmentRowsAdapter(null);
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(BrowseSupportFragment.class.getCanonicalName());
        sb.append(".title");
        ARG_TITLE = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(BrowseSupportFragment.class.getCanonicalName());
        sb2.append(".headersState");
        ARG_HEADERS_STATE = sb2.toString();
    }

    /* access modifiers changed from: 0000 */
    public void setMainFragmentRowsAdapter(MainFragmentRowsAdapter mainFragmentRowsAdapter) {
        MainFragmentRowsAdapter mainFragmentRowsAdapter2 = this.mMainFragmentRowsAdapter;
        if (mainFragmentRowsAdapter != mainFragmentRowsAdapter2) {
            if (mainFragmentRowsAdapter2 != null) {
                mainFragmentRowsAdapter2.setAdapter(null);
            }
            this.mMainFragmentRowsAdapter = mainFragmentRowsAdapter;
            if (mainFragmentRowsAdapter != null) {
                mainFragmentRowsAdapter.setOnItemViewSelectedListener(new MainFragmentItemViewSelectedListener(mainFragmentRowsAdapter));
                this.mMainFragmentRowsAdapter.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
            }
            updateMainFragmentRowsAdapter();
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateMainFragmentRowsAdapter() {
        ListRowDataAdapter listRowDataAdapter = this.mMainFragmentListRowDataAdapter;
        ListRowDataAdapter listRowDataAdapter2 = null;
        if (listRowDataAdapter != null) {
            listRowDataAdapter.detach();
            this.mMainFragmentListRowDataAdapter = null;
        }
        if (this.mMainFragmentRowsAdapter != null) {
            ObjectAdapter objectAdapter = this.mAdapter;
            if (objectAdapter != null) {
                listRowDataAdapter2 = new ListRowDataAdapter(objectAdapter);
            }
            this.mMainFragmentListRowDataAdapter = listRowDataAdapter2;
            this.mMainFragmentRowsAdapter.setAdapter(listRowDataAdapter2);
        }
    }

    public boolean isInHeadersTransition() {
        return this.mHeadersTransition != null;
    }

    public boolean isShowingHeaders() {
        return this.mShowingHeaders;
    }

    /* access modifiers changed from: 0000 */
    public void startHeadersTransitionInternal(final boolean z) {
        if (!getFragmentManager().isDestroyed() && isHeadersDataReady()) {
            this.mShowingHeaders = z;
            this.mMainFragmentAdapter.onTransitionPrepare();
            this.mMainFragmentAdapter.onTransitionStart();
            onExpandTransitionStart(!z, new Runnable() {
                public void run() {
                    BrowseSupportFragment.this.mHeadersSupportFragment.onTransitionPrepare();
                    BrowseSupportFragment.this.mHeadersSupportFragment.onTransitionStart();
                    BrowseSupportFragment.this.createHeadersTransition();
                    BrowseTransitionListener browseTransitionListener = BrowseSupportFragment.this.mBrowseTransitionListener;
                    if (browseTransitionListener != null) {
                        browseTransitionListener.onHeadersTransitionStart(z);
                    }
                    TransitionHelper.runTransition(z ? BrowseSupportFragment.this.mSceneWithHeaders : BrowseSupportFragment.this.mSceneWithoutHeaders, BrowseSupportFragment.this.mHeadersTransition);
                    BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
                    if (!browseSupportFragment.mHeadersBackStackEnabled) {
                        return;
                    }
                    if (!z) {
                        FragmentTransaction beginTransaction = browseSupportFragment.getFragmentManager().beginTransaction();
                        beginTransaction.addToBackStack(BrowseSupportFragment.this.mWithHeadersBackStackName);
                        beginTransaction.commit();
                        return;
                    }
                    int i = browseSupportFragment.mBackStackChangedListener.mIndexOfHeadersBackStack;
                    if (i >= 0) {
                        BrowseSupportFragment.this.getFragmentManager().popBackStackImmediate(browseSupportFragment.getFragmentManager().getBackStackEntryAt(i).getId(), 1);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isVerticalScrolling() {
        return this.mHeadersSupportFragment.isScrolling() || this.mMainFragmentAdapter.isScrolling();
    }

    /* access modifiers changed from: 0000 */
    public final boolean isHeadersDataReady() {
        ObjectAdapter objectAdapter = this.mAdapter;
        return (objectAdapter == null || objectAdapter.size() == 0) ? false : true;
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("currentSelectedPosition", this.mSelectedPosition);
        bundle.putBoolean("isPageRow", this.mIsPageRow);
        BackStackListener backStackListener = this.mBackStackChangedListener;
        if (backStackListener != null) {
            backStackListener.save(bundle);
            return;
        }
        bundle.putBoolean("headerShow", this.mShowingHeaders);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(R$styleable.LeanbackTheme);
        this.mContainerListMarginStart = (int) obtainStyledAttributes.getDimension(R$styleable.LeanbackTheme_browseRowsMarginStart, (float) context.getResources().getDimensionPixelSize(R$dimen.lb_browse_rows_margin_start));
        this.mContainerListAlignTop = (int) obtainStyledAttributes.getDimension(R$styleable.LeanbackTheme_browseRowsMarginTop, (float) context.getResources().getDimensionPixelSize(R$dimen.lb_browse_rows_margin_top));
        obtainStyledAttributes.recycle();
        readArguments(getArguments());
        if (this.mCanShowHeaders) {
            if (this.mHeadersBackStackEnabled) {
                StringBuilder sb = new StringBuilder();
                sb.append("lbHeadersBackStack_");
                sb.append(this);
                this.mWithHeadersBackStackName = sb.toString();
                this.mBackStackChangedListener = new BackStackListener();
                getFragmentManager().addOnBackStackChangedListener(this.mBackStackChangedListener);
                this.mBackStackChangedListener.load(bundle);
            } else if (bundle != null) {
                this.mShowingHeaders = bundle.getBoolean("headerShow");
            }
        }
        this.mScaleFactor = getResources().getFraction(R$fraction.lb_browse_rows_scale, 1, 1);
    }

    public void onDestroyView() {
        setMainFragmentRowsAdapter(null);
        this.mPageRow = null;
        this.mMainFragmentAdapter = null;
        this.mMainFragment = null;
        this.mHeadersSupportFragment = null;
        super.onDestroyView();
    }

    public void onDestroy() {
        if (this.mBackStackChangedListener != null) {
            getFragmentManager().removeOnBackStackChangedListener(this.mBackStackChangedListener);
        }
        super.onDestroy();
    }

    public HeadersSupportFragment onCreateHeadersSupportFragment() {
        return new HeadersSupportFragment();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (getChildFragmentManager().findFragmentById(R$id.scale_frame) == null) {
            this.mHeadersSupportFragment = onCreateHeadersSupportFragment();
            createMainFragment(this.mAdapter, this.mSelectedPosition);
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.replace(R$id.browse_headers_dock, this.mHeadersSupportFragment);
            Fragment fragment = this.mMainFragment;
            if (fragment != null) {
                beginTransaction.replace(R$id.scale_frame, fragment);
            } else {
                MainFragmentAdapter mainFragmentAdapter = new MainFragmentAdapter(null);
                this.mMainFragmentAdapter = mainFragmentAdapter;
                mainFragmentAdapter.setFragmentHost(new FragmentHostImpl());
            }
            beginTransaction.commit();
        } else {
            this.mHeadersSupportFragment = (HeadersSupportFragment) getChildFragmentManager().findFragmentById(R$id.browse_headers_dock);
            this.mMainFragment = getChildFragmentManager().findFragmentById(R$id.scale_frame);
            this.mIsPageRow = bundle != null && bundle.getBoolean("isPageRow", false);
            this.mSelectedPosition = bundle != null ? bundle.getInt("currentSelectedPosition", 0) : 0;
            setMainFragmentAdapter();
        }
        this.mHeadersSupportFragment.setHeadersGone(true ^ this.mCanShowHeaders);
        PresenterSelector presenterSelector = this.mHeaderPresenterSelector;
        if (presenterSelector != null) {
            this.mHeadersSupportFragment.setPresenterSelector(presenterSelector);
        }
        this.mHeadersSupportFragment.setAdapter(this.mAdapter);
        this.mHeadersSupportFragment.setOnHeaderViewSelectedListener(this.mHeaderViewSelectedListener);
        this.mHeadersSupportFragment.setOnHeaderClickedListener(this.mHeaderClickedListener);
        View inflate = layoutInflater.inflate(R$layout.lb_browse_fragment, viewGroup, false);
        getProgressBarManager().setRootView((ViewGroup) inflate);
        BrowseFrameLayout browseFrameLayout = (BrowseFrameLayout) inflate.findViewById(R$id.browse_frame);
        this.mBrowseFrame = browseFrameLayout;
        browseFrameLayout.setOnChildFocusListener(this.mOnChildFocusListener);
        this.mBrowseFrame.setOnFocusSearchListener(this.mOnFocusSearchListener);
        installTitleView(layoutInflater, this.mBrowseFrame, bundle);
        ScaleFrameLayout scaleFrameLayout = (ScaleFrameLayout) inflate.findViewById(R$id.scale_frame);
        this.mScaleFrameLayout = scaleFrameLayout;
        scaleFrameLayout.setPivotX(0.0f);
        this.mScaleFrameLayout.setPivotY((float) this.mContainerListAlignTop);
        if (this.mBrandColorSet) {
            this.mHeadersSupportFragment.setBackgroundColor(this.mBrandColor);
        }
        this.mSceneWithHeaders = TransitionHelper.createScene(this.mBrowseFrame, new Runnable() {
            public void run() {
                BrowseSupportFragment.this.showHeaders(true);
            }
        });
        this.mSceneWithoutHeaders = TransitionHelper.createScene(this.mBrowseFrame, new Runnable() {
            public void run() {
                BrowseSupportFragment.this.showHeaders(false);
            }
        });
        this.mSceneAfterEntranceTransition = TransitionHelper.createScene(this.mBrowseFrame, new Runnable() {
            public void run() {
                BrowseSupportFragment.this.setEntranceTransitionEndState();
            }
        });
        return inflate;
    }

    /* access modifiers changed from: 0000 */
    public void createHeadersTransition() {
        Object loadTransition = TransitionHelper.loadTransition(getContext(), this.mShowingHeaders ? R$transition.lb_browse_headers_in : R$transition.lb_browse_headers_out);
        this.mHeadersTransition = loadTransition;
        TransitionHelper.addTransitionListener(loadTransition, new TransitionListener() {
            public void onTransitionStart(Object obj) {
            }

            public void onTransitionEnd(Object obj) {
                BrowseSupportFragment browseSupportFragment = BrowseSupportFragment.this;
                browseSupportFragment.mHeadersTransition = null;
                MainFragmentAdapter mainFragmentAdapter = browseSupportFragment.mMainFragmentAdapter;
                if (mainFragmentAdapter != null) {
                    mainFragmentAdapter.onTransitionEnd();
                    BrowseSupportFragment browseSupportFragment2 = BrowseSupportFragment.this;
                    if (!browseSupportFragment2.mShowingHeaders) {
                        Fragment fragment = browseSupportFragment2.mMainFragment;
                        if (fragment != null) {
                            View view = fragment.getView();
                            if (view != null && !view.hasFocus()) {
                                view.requestFocus();
                            }
                        }
                    }
                }
                HeadersSupportFragment headersSupportFragment = BrowseSupportFragment.this.mHeadersSupportFragment;
                if (headersSupportFragment != null) {
                    headersSupportFragment.onTransitionEnd();
                    BrowseSupportFragment browseSupportFragment3 = BrowseSupportFragment.this;
                    if (browseSupportFragment3.mShowingHeaders) {
                        VerticalGridView verticalGridView = browseSupportFragment3.mHeadersSupportFragment.getVerticalGridView();
                        if (verticalGridView != null && !verticalGridView.hasFocus()) {
                            verticalGridView.requestFocus();
                        }
                    }
                }
                BrowseSupportFragment.this.updateTitleViewVisibility();
                BrowseSupportFragment browseSupportFragment4 = BrowseSupportFragment.this;
                BrowseTransitionListener browseTransitionListener = browseSupportFragment4.mBrowseTransitionListener;
                if (browseTransitionListener != null) {
                    browseTransitionListener.onHeadersTransitionStop(browseSupportFragment4.mShowingHeaders);
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x001f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0040  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x001a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateTitleViewVisibility() {
        /*
            r3 = this;
            boolean r0 = r3.mShowingHeaders
            r1 = 0
            if (r0 != 0) goto L_0x0023
            boolean r0 = r3.mIsPageRow
            if (r0 == 0) goto L_0x0012
            androidx.leanback.app.BrowseSupportFragment$MainFragmentAdapter r0 = r3.mMainFragmentAdapter
            if (r0 == 0) goto L_0x0012
            androidx.leanback.app.BrowseSupportFragment$FragmentHostImpl r0 = r0.mFragmentHost
            boolean r0 = r0.mShowTitleView
            goto L_0x0018
        L_0x0012:
            int r0 = r3.mSelectedPosition
            boolean r0 = r3.isFirstRowWithContent(r0)
        L_0x0018:
            if (r0 == 0) goto L_0x001f
            r0 = 6
            r3.showTitle(r0)
            goto L_0x004e
        L_0x001f:
            r3.showTitle(r1)
            goto L_0x004e
        L_0x0023:
            boolean r0 = r3.mIsPageRow
            if (r0 == 0) goto L_0x0030
            androidx.leanback.app.BrowseSupportFragment$MainFragmentAdapter r0 = r3.mMainFragmentAdapter
            if (r0 == 0) goto L_0x0030
            androidx.leanback.app.BrowseSupportFragment$FragmentHostImpl r0 = r0.mFragmentHost
            boolean r0 = r0.mShowTitleView
            goto L_0x0036
        L_0x0030:
            int r0 = r3.mSelectedPosition
            boolean r0 = r3.isFirstRowWithContent(r0)
        L_0x0036:
            int r2 = r3.mSelectedPosition
            boolean r2 = r3.isFirstRowWithContentOrPageRow(r2)
            if (r0 == 0) goto L_0x0040
            r0 = 2
            goto L_0x0041
        L_0x0040:
            r0 = r1
        L_0x0041:
            if (r2 == 0) goto L_0x0045
            r0 = r0 | 4
        L_0x0045:
            if (r0 == 0) goto L_0x004b
            r3.showTitle(r0)
            goto L_0x004e
        L_0x004b:
            r3.showTitle(r1)
        L_0x004e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.app.BrowseSupportFragment.updateTitleViewVisibility():void");
    }

    /* access modifiers changed from: 0000 */
    public boolean isFirstRowWithContentOrPageRow(int i) {
        ObjectAdapter objectAdapter = this.mAdapter;
        if (objectAdapter == null || objectAdapter.size() == 0) {
            return true;
        }
        int i2 = 0;
        while (i2 < this.mAdapter.size()) {
            if (!((Row) this.mAdapter.get(i2)).isRenderedAsRowView()) {
                i2++;
            } else if (i == i2) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isFirstRowWithContent(int i) {
        ObjectAdapter objectAdapter = this.mAdapter;
        boolean z = true;
        if (!(objectAdapter == null || objectAdapter.size() == 0)) {
            for (int i2 = 0; i2 < this.mAdapter.size(); i2++) {
                if (((Row) this.mAdapter.get(i2)).isRenderedAsRowView()) {
                    if (i != i2) {
                        z = false;
                    }
                    return z;
                }
            }
        }
        return true;
    }

    private void setHeadersOnScreen(boolean z) {
        int i;
        View view = this.mHeadersSupportFragment.getView();
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        if (z) {
            i = 0;
        } else {
            i = -this.mContainerListMarginStart;
        }
        marginLayoutParams.setMarginStart(i);
        view.setLayoutParams(marginLayoutParams);
    }

    /* access modifiers changed from: 0000 */
    public void showHeaders(boolean z) {
        this.mHeadersSupportFragment.setHeadersEnabled(z);
        setHeadersOnScreen(z);
        expandMainFragment(!z);
    }

    private void expandMainFragment(boolean z) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.mScaleFrameLayout.getLayoutParams();
        marginLayoutParams.setMarginStart(!z ? this.mContainerListMarginStart : 0);
        this.mScaleFrameLayout.setLayoutParams(marginLayoutParams);
        this.mMainFragmentAdapter.setExpand(z);
        setMainFragmentAlignment();
        float f = (z || !this.mMainFragmentScaleEnabled || !this.mMainFragmentAdapter.isScalingEnabled()) ? 1.0f : this.mScaleFactor;
        this.mScaleFrameLayout.setLayoutScaleY(f);
        this.mScaleFrameLayout.setChildScale(f);
    }

    /* access modifiers changed from: 0000 */
    public void onRowSelected(int i) {
        this.mSetSelectionRunnable.post(i, 0, true);
    }

    /* access modifiers changed from: 0000 */
    public void setSelection(int i, boolean z) {
        if (i != -1) {
            this.mSelectedPosition = i;
            HeadersSupportFragment headersSupportFragment = this.mHeadersSupportFragment;
            if (!(headersSupportFragment == null || this.mMainFragmentAdapter == null)) {
                headersSupportFragment.setSelectedPosition(i, z);
                replaceMainFragment(i);
                MainFragmentRowsAdapter mainFragmentRowsAdapter = this.mMainFragmentRowsAdapter;
                if (mainFragmentRowsAdapter != null) {
                    mainFragmentRowsAdapter.setSelectedPosition(i, z);
                }
                updateTitleViewVisibility();
            }
        }
    }

    private void replaceMainFragment(int i) {
        if (createMainFragment(this.mAdapter, i)) {
            swapToMainFragment();
            expandMainFragment(!this.mCanShowHeaders || !this.mShowingHeaders);
        }
    }

    /* access modifiers changed from: 0000 */
    public final void commitMainFragment() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager.findFragmentById(R$id.scale_frame) != this.mMainFragment) {
            FragmentTransaction beginTransaction = childFragmentManager.beginTransaction();
            beginTransaction.replace(R$id.scale_frame, this.mMainFragment);
            beginTransaction.commit();
        }
    }

    private void swapToMainFragment() {
        if (!this.mStopped) {
            VerticalGridView verticalGridView = this.mHeadersSupportFragment.getVerticalGridView();
            if (!isShowingHeaders() || verticalGridView == null || verticalGridView.getScrollState() == 0) {
                commitMainFragment();
            } else {
                FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
                beginTransaction.replace(R$id.scale_frame, new Fragment());
                beginTransaction.commit();
                verticalGridView.removeOnScrollListener(this.mWaitScrollFinishAndCommitMainFragment);
                verticalGridView.addOnScrollListener(this.mWaitScrollFinishAndCommitMainFragment);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0048  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onStart() {
        /*
            r2 = this;
            super.onStart()
            androidx.leanback.app.HeadersSupportFragment r0 = r2.mHeadersSupportFragment
            int r1 = r2.mContainerListAlignTop
            r0.setAlignment(r1)
            r2.setMainFragmentAlignment()
            boolean r0 = r2.mCanShowHeaders
            if (r0 == 0) goto L_0x0029
            boolean r0 = r2.mShowingHeaders
            if (r0 == 0) goto L_0x0029
            androidx.leanback.app.HeadersSupportFragment r0 = r2.mHeadersSupportFragment
            if (r0 == 0) goto L_0x0029
            android.view.View r0 = r0.getView()
            if (r0 == 0) goto L_0x0029
            androidx.leanback.app.HeadersSupportFragment r0 = r2.mHeadersSupportFragment
            android.view.View r0 = r0.getView()
            r0.requestFocus()
            goto L_0x0044
        L_0x0029:
            boolean r0 = r2.mCanShowHeaders
            if (r0 == 0) goto L_0x0031
            boolean r0 = r2.mShowingHeaders
            if (r0 != 0) goto L_0x0044
        L_0x0031:
            androidx.fragment.app.Fragment r0 = r2.mMainFragment
            if (r0 == 0) goto L_0x0044
            android.view.View r0 = r0.getView()
            if (r0 == 0) goto L_0x0044
            androidx.fragment.app.Fragment r0 = r2.mMainFragment
            android.view.View r0 = r0.getView()
            r0.requestFocus()
        L_0x0044:
            boolean r0 = r2.mCanShowHeaders
            if (r0 == 0) goto L_0x004d
            boolean r0 = r2.mShowingHeaders
            r2.showHeaders(r0)
        L_0x004d:
            androidx.leanback.util.StateMachine r0 = r2.mStateMachine
            androidx.leanback.util.StateMachine$Event r1 = r2.EVT_HEADER_VIEW_CREATED
            r0.fireEvent(r1)
            r0 = 0
            r2.mStopped = r0
            r2.commitMainFragment()
            androidx.leanback.app.BrowseSupportFragment$SetSelectionRunnable r2 = r2.mSetSelectionRunnable
            r2.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.app.BrowseSupportFragment.onStart():void");
    }

    public void onStop() {
        this.mStopped = true;
        this.mSetSelectionRunnable.stop();
        super.onStop();
    }

    private void onExpandTransitionStart(boolean z, Runnable runnable) {
        if (z) {
            runnable.run();
        } else {
            new ExpandPreLayout(runnable, this.mMainFragmentAdapter, getView()).execute();
        }
    }

    private void setMainFragmentAlignment() {
        int i = this.mContainerListAlignTop;
        if (this.mMainFragmentScaleEnabled && this.mMainFragmentAdapter.isScalingEnabled() && this.mShowingHeaders) {
            i = (int) ((((float) i) / this.mScaleFactor) + 0.5f);
        }
        this.mMainFragmentAdapter.setAlignment(i);
    }

    private void readArguments(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(ARG_TITLE)) {
                setTitle(bundle.getString(ARG_TITLE));
            }
            if (bundle.containsKey(ARG_HEADERS_STATE)) {
                setHeadersState(bundle.getInt(ARG_HEADERS_STATE));
            }
        }
    }

    public void setHeadersState(int i) {
        if (i < 1 || i > 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid headers state: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        } else if (i != this.mHeadersState) {
            this.mHeadersState = i;
            if (i == 1) {
                this.mCanShowHeaders = true;
                this.mShowingHeaders = true;
            } else if (i == 2) {
                this.mCanShowHeaders = true;
                this.mShowingHeaders = false;
            } else if (i != 3) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Unknown headers state: ");
                sb2.append(i);
                Log.w("BrowseSupportFragment", sb2.toString());
            } else {
                this.mCanShowHeaders = false;
                this.mShowingHeaders = false;
            }
            HeadersSupportFragment headersSupportFragment = this.mHeadersSupportFragment;
            if (headersSupportFragment != null) {
                headersSupportFragment.setHeadersGone(!this.mCanShowHeaders);
            }
        }
    }

    /* access modifiers changed from: protected */
    public Object createEntranceTransition() {
        return TransitionHelper.loadTransition(getContext(), R$transition.lb_browse_entrance_transition);
    }

    /* access modifiers changed from: protected */
    public void runEntranceTransition(Object obj) {
        TransitionHelper.runTransition(this.mSceneAfterEntranceTransition, obj);
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionPrepare() {
        this.mHeadersSupportFragment.onTransitionPrepare();
        this.mMainFragmentAdapter.setEntranceTransitionState(false);
        this.mMainFragmentAdapter.onTransitionPrepare();
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionStart() {
        this.mHeadersSupportFragment.onTransitionStart();
        this.mMainFragmentAdapter.onTransitionStart();
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionEnd() {
        MainFragmentAdapter mainFragmentAdapter = this.mMainFragmentAdapter;
        if (mainFragmentAdapter != null) {
            mainFragmentAdapter.onTransitionEnd();
        }
        HeadersSupportFragment headersSupportFragment = this.mHeadersSupportFragment;
        if (headersSupportFragment != null) {
            headersSupportFragment.onTransitionEnd();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setSearchOrbViewOnScreen(boolean z) {
        int i;
        View searchAffordanceView = getTitleViewAdapter().getSearchAffordanceView();
        if (searchAffordanceView != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) searchAffordanceView.getLayoutParams();
            if (z) {
                i = 0;
            } else {
                i = -this.mContainerListMarginStart;
            }
            marginLayoutParams.setMarginStart(i);
            searchAffordanceView.setLayoutParams(marginLayoutParams);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setEntranceTransitionStartState() {
        setHeadersOnScreen(false);
        setSearchOrbViewOnScreen(false);
    }

    /* access modifiers changed from: 0000 */
    public void setEntranceTransitionEndState() {
        setHeadersOnScreen(this.mShowingHeaders);
        setSearchOrbViewOnScreen(true);
        this.mMainFragmentAdapter.setEntranceTransitionState(true);
    }
}
