package androidx.preference;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceGroup.OnExpandButtonClickListener;
import androidx.preference.PreferenceManager.PreferenceComparisonCallback;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.Callback;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import java.util.ArrayList;
import java.util.List;

public class PreferenceGroupAdapter extends Adapter<PreferenceViewHolder> implements OnPreferenceChangeInternalListener {
    private Handler mHandler;
    private PreferenceGroup mPreferenceGroup;
    private List<PreferenceResourceDescriptor> mPreferenceResourceDescriptors;
    private List<Preference> mPreferences;
    private Runnable mSyncRunnable = new Runnable() {
        public void run() {
            PreferenceGroupAdapter.this.updatePreferences();
        }
    };
    private List<Preference> mVisiblePreferences;

    private static class PreferenceResourceDescriptor {
        String mClassName;
        int mLayoutResId;
        int mWidgetLayoutResId;

        PreferenceResourceDescriptor(Preference preference) {
            this.mClassName = preference.getClass().getName();
            this.mLayoutResId = preference.getLayoutResource();
            this.mWidgetLayoutResId = preference.getWidgetLayoutResource();
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (!(obj instanceof PreferenceResourceDescriptor)) {
                return false;
            }
            PreferenceResourceDescriptor preferenceResourceDescriptor = (PreferenceResourceDescriptor) obj;
            if (this.mLayoutResId == preferenceResourceDescriptor.mLayoutResId && this.mWidgetLayoutResId == preferenceResourceDescriptor.mWidgetLayoutResId && TextUtils.equals(this.mClassName, preferenceResourceDescriptor.mClassName)) {
                z = true;
            }
            return z;
        }

        public int hashCode() {
            return ((((527 + this.mLayoutResId) * 31) + this.mWidgetLayoutResId) * 31) + this.mClassName.hashCode();
        }
    }

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
        this.mPreferenceGroup = preferenceGroup;
        this.mHandler = new Handler();
        this.mPreferenceGroup.setOnPreferenceChangeInternalListener(this);
        this.mPreferences = new ArrayList();
        this.mVisiblePreferences = new ArrayList();
        this.mPreferenceResourceDescriptors = new ArrayList();
        PreferenceGroup preferenceGroup2 = this.mPreferenceGroup;
        if (preferenceGroup2 instanceof PreferenceScreen) {
            setHasStableIds(((PreferenceScreen) preferenceGroup2).shouldUseGeneratedIds());
        } else {
            setHasStableIds(true);
        }
        updatePreferences();
    }

    /* access modifiers changed from: 0000 */
    public void updatePreferences() {
        for (Preference onPreferenceChangeInternalListener : this.mPreferences) {
            onPreferenceChangeInternalListener.setOnPreferenceChangeInternalListener(null);
        }
        ArrayList arrayList = new ArrayList(this.mPreferences.size());
        this.mPreferences = arrayList;
        flattenPreferenceGroup(arrayList, this.mPreferenceGroup);
        final List<Preference> list = this.mVisiblePreferences;
        final List<Preference> createVisiblePreferencesList = createVisiblePreferencesList(this.mPreferenceGroup);
        this.mVisiblePreferences = createVisiblePreferencesList;
        PreferenceManager preferenceManager = this.mPreferenceGroup.getPreferenceManager();
        if (preferenceManager == null || preferenceManager.getPreferenceComparisonCallback() == null) {
            notifyDataSetChanged();
        } else {
            final PreferenceComparisonCallback preferenceComparisonCallback = preferenceManager.getPreferenceComparisonCallback();
            DiffUtil.calculateDiff(new Callback(this) {
                public int getOldListSize() {
                    return list.size();
                }

                public int getNewListSize() {
                    return createVisiblePreferencesList.size();
                }

                public boolean areItemsTheSame(int i, int i2) {
                    return preferenceComparisonCallback.arePreferenceItemsTheSame((Preference) list.get(i), (Preference) createVisiblePreferencesList.get(i2));
                }

                public boolean areContentsTheSame(int i, int i2) {
                    return preferenceComparisonCallback.arePreferenceContentsTheSame((Preference) list.get(i), (Preference) createVisiblePreferencesList.get(i2));
                }
            }).dispatchUpdatesTo((Adapter) this);
        }
        for (Preference clearWasDetached : this.mPreferences) {
            clearWasDetached.clearWasDetached();
        }
    }

    private void flattenPreferenceGroup(List<Preference> list, PreferenceGroup preferenceGroup) {
        preferenceGroup.sortPreferences();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            list.add(preference);
            PreferenceResourceDescriptor preferenceResourceDescriptor = new PreferenceResourceDescriptor(preference);
            if (!this.mPreferenceResourceDescriptors.contains(preferenceResourceDescriptor)) {
                this.mPreferenceResourceDescriptors.add(preferenceResourceDescriptor);
            }
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup2 = (PreferenceGroup) preference;
                if (preferenceGroup2.isOnSameScreenAsChildren()) {
                    flattenPreferenceGroup(list, preferenceGroup2);
                }
            }
            preference.setOnPreferenceChangeInternalListener(this);
        }
    }

    private List<Preference> createVisiblePreferencesList(PreferenceGroup preferenceGroup) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        int i = 0;
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            Preference preference = preferenceGroup.getPreference(i2);
            if (preference.isVisible()) {
                if (!isGroupExpandable(preferenceGroup) || i < preferenceGroup.getInitialExpandedChildrenCount()) {
                    arrayList.add(preference);
                } else {
                    arrayList2.add(preference);
                }
                if (!(preference instanceof PreferenceGroup)) {
                    i++;
                } else {
                    PreferenceGroup preferenceGroup2 = (PreferenceGroup) preference;
                    if (!preferenceGroup2.isOnSameScreenAsChildren()) {
                        continue;
                    } else if (!isGroupExpandable(preferenceGroup) || !isGroupExpandable(preferenceGroup2)) {
                        for (Preference preference2 : createVisiblePreferencesList(preferenceGroup2)) {
                            if (!isGroupExpandable(preferenceGroup) || i < preferenceGroup.getInitialExpandedChildrenCount()) {
                                arrayList.add(preference2);
                            } else {
                                arrayList2.add(preference2);
                            }
                            i++;
                        }
                    } else {
                        throw new IllegalStateException("Nesting an expandable group inside of another expandable group is not supported!");
                    }
                }
            }
        }
        if (isGroupExpandable(preferenceGroup) && i > preferenceGroup.getInitialExpandedChildrenCount()) {
            arrayList.add(createExpandButton(preferenceGroup, arrayList2));
        }
        return arrayList;
    }

    private ExpandButton createExpandButton(final PreferenceGroup preferenceGroup, List<Preference> list) {
        ExpandButton expandButton = new ExpandButton(preferenceGroup.getContext(), list, preferenceGroup.getId());
        expandButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                preferenceGroup.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
                PreferenceGroupAdapter.this.onPreferenceHierarchyChange(preference);
                OnExpandButtonClickListener onExpandButtonClickListener = preferenceGroup.getOnExpandButtonClickListener();
                if (onExpandButtonClickListener != null) {
                    onExpandButtonClickListener.onExpandButtonClick();
                }
                return true;
            }
        });
        return expandButton;
    }

    private boolean isGroupExpandable(PreferenceGroup preferenceGroup) {
        return preferenceGroup.getInitialExpandedChildrenCount() != Integer.MAX_VALUE;
    }

    public Preference getItem(int i) {
        if (i < 0 || i >= getItemCount()) {
            return null;
        }
        return (Preference) this.mVisiblePreferences.get(i);
    }

    public int getItemCount() {
        return this.mVisiblePreferences.size();
    }

    public long getItemId(int i) {
        if (!hasStableIds()) {
            return -1;
        }
        return getItem(i).getId();
    }

    public void onPreferenceChange(Preference preference) {
        int indexOf = this.mVisiblePreferences.indexOf(preference);
        if (indexOf != -1) {
            notifyItemChanged(indexOf, preference);
        }
    }

    public void onPreferenceHierarchyChange(Preference preference) {
        this.mHandler.removeCallbacks(this.mSyncRunnable);
        this.mHandler.post(this.mSyncRunnable);
    }

    public void onPreferenceVisibilityChange(Preference preference) {
        onPreferenceHierarchyChange(preference);
    }

    public int getItemViewType(int i) {
        PreferenceResourceDescriptor preferenceResourceDescriptor = new PreferenceResourceDescriptor(getItem(i));
        int indexOf = this.mPreferenceResourceDescriptors.indexOf(preferenceResourceDescriptor);
        if (indexOf != -1) {
            return indexOf;
        }
        int size = this.mPreferenceResourceDescriptors.size();
        this.mPreferenceResourceDescriptors.add(preferenceResourceDescriptor);
        return size;
    }

    public PreferenceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        PreferenceResourceDescriptor preferenceResourceDescriptor = (PreferenceResourceDescriptor) this.mPreferenceResourceDescriptors.get(i);
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        TypedArray obtainStyledAttributes = viewGroup.getContext().obtainStyledAttributes(null, R$styleable.BackgroundStyle);
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.BackgroundStyle_android_selectableItemBackground);
        if (drawable == null) {
            drawable = AppCompatResources.getDrawable(viewGroup.getContext(), 17301602);
        }
        obtainStyledAttributes.recycle();
        View inflate = from.inflate(preferenceResourceDescriptor.mLayoutResId, viewGroup, false);
        if (inflate.getBackground() == null) {
            ViewCompat.setBackground(inflate, drawable);
        }
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(16908312);
        if (viewGroup2 != null) {
            int i2 = preferenceResourceDescriptor.mWidgetLayoutResId;
            if (i2 != 0) {
                from.inflate(i2, viewGroup2);
            } else {
                viewGroup2.setVisibility(8);
            }
        }
        return new PreferenceViewHolder(inflate);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        Preference item = getItem(i);
        preferenceViewHolder.resetState();
        item.onBindViewHolder(preferenceViewHolder);
    }
}
