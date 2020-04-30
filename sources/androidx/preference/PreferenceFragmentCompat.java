package androidx.preference;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.DialogPreference.TargetFragment;
import androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener;
import androidx.preference.PreferenceManager.OnNavigateToScreenListener;
import androidx.preference.PreferenceManager.OnPreferenceTreeClickListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public abstract class PreferenceFragmentCompat extends Fragment implements OnPreferenceTreeClickListener, OnDisplayPreferenceDialogListener, OnNavigateToScreenListener, TargetFragment {
    private final DividerDecoration mDividerDecoration = new DividerDecoration();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                PreferenceFragmentCompat.this.bindPreferences();
            }
        }
    };
    private boolean mHavePrefs;
    private int mLayoutResId = R$layout.preference_list_fragment;
    RecyclerView mList;
    private PreferenceManager mPreferenceManager;
    private final Runnable mRequestFocus = new Runnable() {
        public void run() {
            RecyclerView recyclerView = PreferenceFragmentCompat.this.mList;
            recyclerView.focusableViewAvailable(recyclerView);
        }
    };
    private Runnable mSelectPreferenceRunnable;

    private class DividerDecoration extends ItemDecoration {
        private boolean mAllowDividerAfterLastItem = true;
        private Drawable mDivider;
        private int mDividerHeight;

        DividerDecoration() {
        }

        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
            if (this.mDivider != null) {
                int childCount = recyclerView.getChildCount();
                int width = recyclerView.getWidth();
                for (int i = 0; i < childCount; i++) {
                    View childAt = recyclerView.getChildAt(i);
                    if (shouldDrawDividerBelow(childAt, recyclerView)) {
                        int y = ((int) childAt.getY()) + childAt.getHeight();
                        this.mDivider.setBounds(0, y, width, this.mDividerHeight + y);
                        this.mDivider.draw(canvas);
                    }
                }
            }
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            if (shouldDrawDividerBelow(view, recyclerView)) {
                rect.bottom = this.mDividerHeight;
            }
        }

        private boolean shouldDrawDividerBelow(View view, RecyclerView recyclerView) {
            ViewHolder childViewHolder = recyclerView.getChildViewHolder(view);
            boolean z = false;
            if (!((childViewHolder instanceof PreferenceViewHolder) && ((PreferenceViewHolder) childViewHolder).isDividerAllowedBelow())) {
                return false;
            }
            boolean z2 = this.mAllowDividerAfterLastItem;
            int indexOfChild = recyclerView.indexOfChild(view);
            if (indexOfChild < recyclerView.getChildCount() - 1) {
                ViewHolder childViewHolder2 = recyclerView.getChildViewHolder(recyclerView.getChildAt(indexOfChild + 1));
                if ((childViewHolder2 instanceof PreferenceViewHolder) && ((PreferenceViewHolder) childViewHolder2).isDividerAllowedAbove()) {
                    z = true;
                }
                z2 = z;
            }
            return z2;
        }

        public void setDivider(Drawable drawable) {
            if (drawable != null) {
                this.mDividerHeight = drawable.getIntrinsicHeight();
            } else {
                this.mDividerHeight = 0;
            }
            this.mDivider = drawable;
            PreferenceFragmentCompat.this.mList.invalidateItemDecorations();
        }

        public void setDividerHeight(int i) {
            this.mDividerHeight = i;
            PreferenceFragmentCompat.this.mList.invalidateItemDecorations();
        }

        public void setAllowDividerAfterLastItem(boolean z) {
            this.mAllowDividerAfterLastItem = z;
        }
    }

    public interface OnPreferenceDisplayDialogCallback {
        boolean onPreferenceDisplayDialog(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference);
    }

    public interface OnPreferenceStartFragmentCallback {
        boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference);
    }

    public interface OnPreferenceStartScreenCallback {
        boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen);
    }

    public Fragment getCallbackFragment() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void onBindPreferences() {
    }

    public abstract void onCreatePreferences(Bundle bundle, String str);

    /* access modifiers changed from: protected */
    public void onUnbindPreferences() {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R$attr.preferenceTheme, typedValue, true);
        int i = typedValue.resourceId;
        if (i == 0) {
            i = R$style.PreferenceThemeOverlay;
        }
        getActivity().getTheme().applyStyle(i, false);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        this.mPreferenceManager = preferenceManager;
        preferenceManager.setOnNavigateToScreenListener(this);
        onCreatePreferences(bundle, getArguments() != null ? getArguments().getString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT") : null);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(null, R$styleable.PreferenceFragmentCompat, R$attr.preferenceFragmentCompatStyle, 0);
        this.mLayoutResId = obtainStyledAttributes.getResourceId(R$styleable.PreferenceFragmentCompat_android_layout, this.mLayoutResId);
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.PreferenceFragmentCompat_android_divider);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.PreferenceFragmentCompat_android_dividerHeight, -1);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.PreferenceFragmentCompat_allowDividerAfterLastItem, true);
        obtainStyledAttributes.recycle();
        LayoutInflater cloneInContext = layoutInflater.cloneInContext(getContext());
        View inflate = cloneInContext.inflate(this.mLayoutResId, viewGroup, false);
        View findViewById = inflate.findViewById(16908351);
        if (findViewById instanceof ViewGroup) {
            ViewGroup viewGroup2 = (ViewGroup) findViewById;
            RecyclerView onCreateRecyclerView = onCreateRecyclerView(cloneInContext, viewGroup2, bundle);
            if (onCreateRecyclerView != null) {
                this.mList = onCreateRecyclerView;
                onCreateRecyclerView.addItemDecoration(this.mDividerDecoration);
                setDivider(drawable);
                if (dimensionPixelSize != -1) {
                    setDividerHeight(dimensionPixelSize);
                }
                this.mDividerDecoration.setAllowDividerAfterLastItem(z);
                if (this.mList.getParent() == null) {
                    viewGroup2.addView(this.mList);
                }
                this.mHandler.post(this.mRequestFocus);
                return inflate;
            }
            throw new RuntimeException("Could not create RecyclerView");
        }
        throw new IllegalStateException("Content has view with id attribute 'android.R.id.list_container' that is not a ViewGroup class");
    }

    public void setDivider(Drawable drawable) {
        this.mDividerDecoration.setDivider(drawable);
    }

    public void setDividerHeight(int i) {
        this.mDividerDecoration.setDividerHeight(i);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (bundle != null) {
            Bundle bundle2 = bundle.getBundle("android:preferences");
            if (bundle2 != null) {
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.restoreHierarchyState(bundle2);
                }
            }
        }
        if (this.mHavePrefs) {
            bindPreferences();
            Runnable runnable = this.mSelectPreferenceRunnable;
            if (runnable != null) {
                runnable.run();
                this.mSelectPreferenceRunnable = null;
            }
        }
    }

    public void onStart() {
        super.onStart();
        this.mPreferenceManager.setOnPreferenceTreeClickListener(this);
        this.mPreferenceManager.setOnDisplayPreferenceDialogListener(this);
    }

    public void onStop() {
        super.onStop();
        this.mPreferenceManager.setOnPreferenceTreeClickListener(null);
        this.mPreferenceManager.setOnDisplayPreferenceDialogListener(null);
    }

    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mHandler.removeMessages(1);
        if (this.mHavePrefs) {
            unbindPreferences();
        }
        this.mList = null;
        super.onDestroyView();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            Bundle bundle2 = new Bundle();
            preferenceScreen.saveHierarchyState(bundle2);
            bundle.putBundle("android:preferences", bundle2);
        }
    }

    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceManager.getPreferenceScreen();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() == null) {
            return false;
        }
        boolean onPreferenceStartFragment = getCallbackFragment() instanceof OnPreferenceStartFragmentCallback ? ((OnPreferenceStartFragmentCallback) getCallbackFragment()).onPreferenceStartFragment(this, preference) : false;
        if (!onPreferenceStartFragment && (getActivity() instanceof OnPreferenceStartFragmentCallback)) {
            onPreferenceStartFragment = ((OnPreferenceStartFragmentCallback) getActivity()).onPreferenceStartFragment(this, preference);
        }
        if (!onPreferenceStartFragment) {
            Log.w("PreferenceFragment", "onPreferenceStartFragment is not implemented in the parent activity - attempting to use a fallback implementation. You should implement this method so that you can configure the new fragment that will be displayed, and set a transition between the fragments.");
            FragmentManager supportFragmentManager = requireActivity().getSupportFragmentManager();
            Bundle extras = preference.getExtras();
            Fragment instantiate = supportFragmentManager.getFragmentFactory().instantiate(requireActivity().getClassLoader(), preference.getFragment());
            instantiate.setArguments(extras);
            instantiate.setTargetFragment(this, 0);
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.replace(((View) getView().getParent()).getId(), instantiate);
            beginTransaction.addToBackStack(null);
            beginTransaction.commit();
        }
        return true;
    }

    public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
        if (!(getCallbackFragment() instanceof OnPreferenceStartScreenCallback ? ((OnPreferenceStartScreenCallback) getCallbackFragment()).onPreferenceStartScreen(this, preferenceScreen) : false) && (getActivity() instanceof OnPreferenceStartScreenCallback)) {
            ((OnPreferenceStartScreenCallback) getActivity()).onPreferenceStartScreen(this, preferenceScreen);
        }
    }

    public <T extends Preference> T findPreference(CharSequence charSequence) {
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager == null) {
            return null;
        }
        return preferenceManager.findPreference(charSequence);
    }

    /* access modifiers changed from: 0000 */
    public void bindPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            getListView().setAdapter(onCreateAdapter(preferenceScreen));
            preferenceScreen.onAttached();
        }
        onBindPreferences();
    }

    private void unbindPreferences() {
        getListView().setAdapter(null);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.onDetached();
        }
        onUnbindPreferences();
    }

    public final RecyclerView getListView() {
        return this.mList;
    }

    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (getContext().getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            RecyclerView recyclerView = (RecyclerView) viewGroup.findViewById(R$id.recycler_view);
            if (recyclerView != null) {
                return recyclerView;
            }
        }
        RecyclerView recyclerView2 = (RecyclerView) layoutInflater.inflate(R$layout.preference_recyclerview, viewGroup, false);
        recyclerView2.setLayoutManager(onCreateLayoutManager());
        recyclerView2.setAccessibilityDelegateCompat(new PreferenceRecyclerViewAccessibilityDelegate(recyclerView2));
        return recyclerView2;
    }

    public LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    /* access modifiers changed from: protected */
    public Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new PreferenceGroupAdapter(preferenceScreen);
    }

    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment;
        boolean onPreferenceDisplayDialog = getCallbackFragment() instanceof OnPreferenceDisplayDialogCallback ? ((OnPreferenceDisplayDialogCallback) getCallbackFragment()).onPreferenceDisplayDialog(this, preference) : false;
        if (!onPreferenceDisplayDialog && (getActivity() instanceof OnPreferenceDisplayDialogCallback)) {
            onPreferenceDisplayDialog = ((OnPreferenceDisplayDialogCallback) getActivity()).onPreferenceDisplayDialog(this, preference);
        }
        if (!onPreferenceDisplayDialog) {
            String str = "androidx.preference.PreferenceFragment.DIALOG";
            if (getFragmentManager().findFragmentByTag(str) == null) {
                if (preference instanceof EditTextPreference) {
                    dialogFragment = EditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
                } else if (preference instanceof ListPreference) {
                    dialogFragment = ListPreferenceDialogFragmentCompat.newInstance(preference.getKey());
                } else if (preference instanceof MultiSelectListPreference) {
                    dialogFragment = MultiSelectListPreferenceDialogFragmentCompat.newInstance(preference.getKey());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Cannot display dialog for an unknown Preference type: ");
                    sb.append(preference.getClass().getSimpleName());
                    sb.append(". Make sure to implement onPreferenceDisplayDialog() to handle displaying a custom dialog for this Preference.");
                    throw new IllegalArgumentException(sb.toString());
                }
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(getFragmentManager(), str);
            }
        }
    }
}
