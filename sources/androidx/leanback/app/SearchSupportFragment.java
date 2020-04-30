package androidx.leanback.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.R$dimen;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.widget.BrowseFrameLayout;
import androidx.leanback.widget.BrowseFrameLayout.OnFocusSearchListener;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.ObjectAdapter.DataObserver;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter.ViewHolder;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SearchBar;
import androidx.leanback.widget.SearchBar.SearchBarListener;
import androidx.leanback.widget.SearchBar.SearchBarPermissionListener;
import androidx.leanback.widget.SpeechRecognitionCallback;
import androidx.leanback.widget.VerticalGridView;

public class SearchSupportFragment extends Fragment {
    private static final String ARG_PREFIX = SearchSupportFragment.class.getCanonicalName();
    private static final String ARG_QUERY;
    private static final String ARG_TITLE;
    final DataObserver mAdapterObserver = new DataObserver() {
        public void onChanged() {
            SearchSupportFragment searchSupportFragment = SearchSupportFragment.this;
            searchSupportFragment.mHandler.removeCallbacks(searchSupportFragment.mResultsChangedCallback);
            SearchSupportFragment searchSupportFragment2 = SearchSupportFragment.this;
            searchSupportFragment2.mHandler.post(searchSupportFragment2.mResultsChangedCallback);
        }
    };
    boolean mAutoStartRecognition = true;
    private Drawable mBadgeDrawable;
    private ExternalQuery mExternalQuery;
    final Handler mHandler = new Handler();
    private boolean mIsPaused;
    private OnItemViewClickedListener mOnItemViewClickedListener;
    OnItemViewSelectedListener mOnItemViewSelectedListener;
    String mPendingQuery = null;
    private boolean mPendingStartRecognitionWhenPaused;
    private SearchBarPermissionListener mPermissionListener = new SearchBarPermissionListener() {
        public void requestAudioPermission() {
            SearchSupportFragment.this.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 0);
        }
    };
    SearchResultProvider mProvider;
    ObjectAdapter mResultAdapter;
    final Runnable mResultsChangedCallback = new Runnable() {
        public void run() {
            RowsSupportFragment rowsSupportFragment = SearchSupportFragment.this.mRowsSupportFragment;
            if (rowsSupportFragment != null) {
                ObjectAdapter adapter = rowsSupportFragment.getAdapter();
                SearchSupportFragment searchSupportFragment = SearchSupportFragment.this;
                if (!(adapter == searchSupportFragment.mResultAdapter || (searchSupportFragment.mRowsSupportFragment.getAdapter() == null && SearchSupportFragment.this.mResultAdapter.size() == 0))) {
                    SearchSupportFragment searchSupportFragment2 = SearchSupportFragment.this;
                    searchSupportFragment2.mRowsSupportFragment.setAdapter(searchSupportFragment2.mResultAdapter);
                    SearchSupportFragment.this.mRowsSupportFragment.setSelectedPosition(0);
                }
            }
            SearchSupportFragment.this.updateSearchBarVisibility();
            SearchSupportFragment searchSupportFragment3 = SearchSupportFragment.this;
            int i = searchSupportFragment3.mStatus | 1;
            searchSupportFragment3.mStatus = i;
            if ((i & 2) != 0) {
                searchSupportFragment3.updateFocus();
            }
        }
    };
    RowsSupportFragment mRowsSupportFragment;
    SearchBar mSearchBar;
    private final Runnable mSetSearchResultProvider = new Runnable() {
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0034, code lost:
            if (r0.size() != 0) goto L_0x0036;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r3 = this;
                androidx.leanback.app.SearchSupportFragment r0 = androidx.leanback.app.SearchSupportFragment.this
                androidx.leanback.app.RowsSupportFragment r1 = r0.mRowsSupportFragment
                if (r1 != 0) goto L_0x0007
                return
            L_0x0007:
                androidx.leanback.app.SearchSupportFragment$SearchResultProvider r0 = r0.mProvider
                androidx.leanback.widget.ObjectAdapter r0 = r0.getResultsAdapter()
                androidx.leanback.app.SearchSupportFragment r1 = androidx.leanback.app.SearchSupportFragment.this
                androidx.leanback.widget.ObjectAdapter r1 = r1.mResultAdapter
                if (r0 == r1) goto L_0x0044
                if (r1 != 0) goto L_0x0017
                r1 = 1
                goto L_0x0018
            L_0x0017:
                r1 = 0
            L_0x0018:
                androidx.leanback.app.SearchSupportFragment r2 = androidx.leanback.app.SearchSupportFragment.this
                r2.releaseAdapter()
                androidx.leanback.app.SearchSupportFragment r2 = androidx.leanback.app.SearchSupportFragment.this
                r2.mResultAdapter = r0
                if (r0 == 0) goto L_0x0028
                androidx.leanback.widget.ObjectAdapter$DataObserver r2 = r2.mAdapterObserver
                r0.registerObserver(r2)
            L_0x0028:
                if (r1 == 0) goto L_0x0036
                androidx.leanback.app.SearchSupportFragment r0 = androidx.leanback.app.SearchSupportFragment.this
                androidx.leanback.widget.ObjectAdapter r0 = r0.mResultAdapter
                if (r0 == 0) goto L_0x003f
                int r0 = r0.size()
                if (r0 == 0) goto L_0x003f
            L_0x0036:
                androidx.leanback.app.SearchSupportFragment r0 = androidx.leanback.app.SearchSupportFragment.this
                androidx.leanback.app.RowsSupportFragment r1 = r0.mRowsSupportFragment
                androidx.leanback.widget.ObjectAdapter r0 = r0.mResultAdapter
                r1.setAdapter(r0)
            L_0x003f:
                androidx.leanback.app.SearchSupportFragment r0 = androidx.leanback.app.SearchSupportFragment.this
                r0.executePendingQuery()
            L_0x0044:
                androidx.leanback.app.SearchSupportFragment r0 = androidx.leanback.app.SearchSupportFragment.this
                boolean r1 = r0.mAutoStartRecognition
                if (r1 == 0) goto L_0x005d
                android.os.Handler r1 = r0.mHandler
                java.lang.Runnable r0 = r0.mStartRecognitionRunnable
                r1.removeCallbacks(r0)
                androidx.leanback.app.SearchSupportFragment r3 = androidx.leanback.app.SearchSupportFragment.this
                android.os.Handler r0 = r3.mHandler
                java.lang.Runnable r3 = r3.mStartRecognitionRunnable
                r1 = 300(0x12c, double:1.48E-321)
                r0.postDelayed(r3, r1)
                goto L_0x0060
            L_0x005d:
                r0.updateFocus()
            L_0x0060:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.app.SearchSupportFragment.C02553.run():void");
        }
    };
    private SpeechRecognitionCallback mSpeechRecognitionCallback;
    private SpeechRecognizer mSpeechRecognizer;
    final Runnable mStartRecognitionRunnable = new Runnable() {
        public void run() {
            SearchSupportFragment searchSupportFragment = SearchSupportFragment.this;
            searchSupportFragment.mAutoStartRecognition = false;
            searchSupportFragment.mSearchBar.startRecognition();
        }
    };
    int mStatus;
    private String mTitle;

    static class ExternalQuery {
        String mQuery;
        boolean mSubmit;
    }

    public interface SearchResultProvider {
        ObjectAdapter getResultsAdapter();

        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    static {
        Class<SearchSupportFragment> cls = SearchSupportFragment.class;
        StringBuilder sb = new StringBuilder();
        sb.append(ARG_PREFIX);
        sb.append(".query");
        ARG_QUERY = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(ARG_PREFIX);
        sb2.append(".title");
        ARG_TITLE = sb2.toString();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 0 && strArr.length > 0 && strArr[0].equals("android.permission.RECORD_AUDIO") && iArr[0] == 0) {
            startRecognition();
        }
    }

    public void onCreate(Bundle bundle) {
        if (this.mAutoStartRecognition) {
            this.mAutoStartRecognition = bundle == null;
        }
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.lb_search_fragment, viewGroup, false);
        BrowseFrameLayout browseFrameLayout = (BrowseFrameLayout) inflate.findViewById(R$id.lb_search_frame);
        SearchBar searchBar = (SearchBar) browseFrameLayout.findViewById(R$id.lb_search_bar);
        this.mSearchBar = searchBar;
        searchBar.setSearchBarListener(new SearchBarListener() {
            public void onSearchQueryChange(String str) {
                SearchSupportFragment searchSupportFragment = SearchSupportFragment.this;
                if (searchSupportFragment.mProvider != null) {
                    searchSupportFragment.retrieveResults(str);
                } else {
                    searchSupportFragment.mPendingQuery = str;
                }
            }

            public void onSearchQuerySubmit(String str) {
                SearchSupportFragment.this.submitQuery(str);
            }

            public void onKeyboardDismiss(String str) {
                SearchSupportFragment.this.queryComplete();
            }
        });
        this.mSearchBar.setSpeechRecognitionCallback(this.mSpeechRecognitionCallback);
        this.mSearchBar.setPermissionListener(this.mPermissionListener);
        applyExternalQuery();
        readArguments(getArguments());
        Drawable drawable = this.mBadgeDrawable;
        if (drawable != null) {
            setBadgeDrawable(drawable);
        }
        String str = this.mTitle;
        if (str != null) {
            setTitle(str);
        }
        if (getChildFragmentManager().findFragmentById(R$id.lb_results_frame) == null) {
            this.mRowsSupportFragment = new RowsSupportFragment();
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.replace(R$id.lb_results_frame, this.mRowsSupportFragment);
            beginTransaction.commit();
        } else {
            this.mRowsSupportFragment = (RowsSupportFragment) getChildFragmentManager().findFragmentById(R$id.lb_results_frame);
        }
        this.mRowsSupportFragment.setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            public void onItemSelected(ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, Row row) {
                SearchSupportFragment.this.updateSearchBarVisibility();
                OnItemViewSelectedListener onItemViewSelectedListener = SearchSupportFragment.this.mOnItemViewSelectedListener;
                if (onItemViewSelectedListener != null) {
                    onItemViewSelectedListener.onItemSelected(viewHolder, obj, viewHolder2, row);
                }
            }
        });
        this.mRowsSupportFragment.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
        this.mRowsSupportFragment.setExpand(true);
        if (this.mProvider != null) {
            onSetSearchResultProvider();
        }
        browseFrameLayout.setOnFocusSearchListener(new OnFocusSearchListener() {
            public View onFocusSearch(View view, int i) {
                RowsSupportFragment rowsSupportFragment = SearchSupportFragment.this.mRowsSupportFragment;
                if (rowsSupportFragment == null || rowsSupportFragment.getView() == null || !SearchSupportFragment.this.mRowsSupportFragment.getView().hasFocus()) {
                    if (SearchSupportFragment.this.mSearchBar.hasFocus() && i == 130 && SearchSupportFragment.this.mRowsSupportFragment.getView() != null) {
                        ObjectAdapter objectAdapter = SearchSupportFragment.this.mResultAdapter;
                        if (objectAdapter != null && objectAdapter.size() > 0) {
                            return SearchSupportFragment.this.mRowsSupportFragment.getView();
                        }
                    }
                } else if (i == 33) {
                    return SearchSupportFragment.this.mSearchBar.findViewById(R$id.lb_search_bar_speech_orb);
                }
                return null;
            }
        });
        return inflate;
    }

    public void onStart() {
        super.onStart();
        VerticalGridView verticalGridView = this.mRowsSupportFragment.getVerticalGridView();
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.lb_search_browse_rows_align_top);
        verticalGridView.setItemAlignmentOffset(0);
        verticalGridView.setItemAlignmentOffsetPercent(-1.0f);
        verticalGridView.setWindowAlignmentOffset(dimensionPixelSize);
        verticalGridView.setWindowAlignmentOffsetPercent(-1.0f);
        verticalGridView.setWindowAlignment(0);
    }

    public void onResume() {
        super.onResume();
        this.mIsPaused = false;
        if (this.mSpeechRecognitionCallback == null && this.mSpeechRecognizer == null) {
            SpeechRecognizer createSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
            this.mSpeechRecognizer = createSpeechRecognizer;
            this.mSearchBar.setSpeechRecognizer(createSpeechRecognizer);
        }
        if (this.mPendingStartRecognitionWhenPaused) {
            this.mPendingStartRecognitionWhenPaused = false;
            this.mSearchBar.startRecognition();
            return;
        }
        this.mSearchBar.stopRecognition();
    }

    public void onPause() {
        releaseRecognizer();
        this.mIsPaused = true;
        super.onPause();
    }

    public void onDestroy() {
        releaseAdapter();
        super.onDestroy();
    }

    private void releaseRecognizer() {
        if (this.mSpeechRecognizer != null) {
            this.mSearchBar.setSpeechRecognizer(null);
            this.mSpeechRecognizer.destroy();
            this.mSpeechRecognizer = null;
        }
    }

    public void startRecognition() {
        if (this.mIsPaused) {
            this.mPendingStartRecognitionWhenPaused = true;
        } else {
            this.mSearchBar.startRecognition();
        }
    }

    public void setTitle(String str) {
        this.mTitle = str;
        SearchBar searchBar = this.mSearchBar;
        if (searchBar != null) {
            searchBar.setTitle(str);
        }
    }

    public void setBadgeDrawable(Drawable drawable) {
        this.mBadgeDrawable = drawable;
        SearchBar searchBar = this.mSearchBar;
        if (searchBar != null) {
            searchBar.setBadgeDrawable(drawable);
        }
    }

    /* access modifiers changed from: 0000 */
    public void retrieveResults(String str) {
        if (this.mProvider.onQueryTextChange(str)) {
            this.mStatus &= -3;
        }
    }

    /* access modifiers changed from: 0000 */
    public void submitQuery(String str) {
        queryComplete();
        SearchResultProvider searchResultProvider = this.mProvider;
        if (searchResultProvider != null) {
            searchResultProvider.onQueryTextSubmit(str);
        }
    }

    /* access modifiers changed from: 0000 */
    public void queryComplete() {
        this.mStatus |= 2;
        focusOnResults();
    }

    /* access modifiers changed from: 0000 */
    public void updateSearchBarVisibility() {
        int i;
        RowsSupportFragment rowsSupportFragment = this.mRowsSupportFragment;
        int selectedPosition = rowsSupportFragment != null ? rowsSupportFragment.getSelectedPosition() : -1;
        SearchBar searchBar = this.mSearchBar;
        if (selectedPosition > 0) {
            ObjectAdapter objectAdapter = this.mResultAdapter;
            if (!(objectAdapter == null || objectAdapter.size() == 0)) {
                i = 8;
                searchBar.setVisibility(i);
            }
        }
        i = 0;
        searchBar.setVisibility(i);
    }

    /* access modifiers changed from: 0000 */
    public void updateFocus() {
        ObjectAdapter objectAdapter = this.mResultAdapter;
        if (objectAdapter != null && objectAdapter.size() > 0) {
            RowsSupportFragment rowsSupportFragment = this.mRowsSupportFragment;
            if (rowsSupportFragment != null && rowsSupportFragment.getAdapter() == this.mResultAdapter) {
                focusOnResults();
                return;
            }
        }
        this.mSearchBar.requestFocus();
    }

    private void focusOnResults() {
        RowsSupportFragment rowsSupportFragment = this.mRowsSupportFragment;
        if (rowsSupportFragment != null && rowsSupportFragment.getVerticalGridView() != null && this.mResultAdapter.size() != 0 && this.mRowsSupportFragment.getVerticalGridView().requestFocus()) {
            this.mStatus &= -2;
        }
    }

    private void onSetSearchResultProvider() {
        this.mHandler.removeCallbacks(this.mSetSearchResultProvider);
        this.mHandler.post(this.mSetSearchResultProvider);
    }

    /* access modifiers changed from: 0000 */
    public void releaseAdapter() {
        ObjectAdapter objectAdapter = this.mResultAdapter;
        if (objectAdapter != null) {
            objectAdapter.unregisterObserver(this.mAdapterObserver);
            this.mResultAdapter = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void executePendingQuery() {
        String str = this.mPendingQuery;
        if (str != null && this.mResultAdapter != null) {
            this.mPendingQuery = null;
            retrieveResults(str);
        }
    }

    private void applyExternalQuery() {
        ExternalQuery externalQuery = this.mExternalQuery;
        if (externalQuery != null) {
            SearchBar searchBar = this.mSearchBar;
            if (searchBar != null) {
                searchBar.setSearchQuery(externalQuery.mQuery);
                ExternalQuery externalQuery2 = this.mExternalQuery;
                if (externalQuery2.mSubmit) {
                    submitQuery(externalQuery2.mQuery);
                }
                this.mExternalQuery = null;
            }
        }
    }

    private void readArguments(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(ARG_QUERY)) {
                setSearchQuery(bundle.getString(ARG_QUERY));
            }
            if (bundle.containsKey(ARG_TITLE)) {
                setTitle(bundle.getString(ARG_TITLE));
            }
        }
    }

    private void setSearchQuery(String str) {
        this.mSearchBar.setSearchQuery(str);
    }
}
