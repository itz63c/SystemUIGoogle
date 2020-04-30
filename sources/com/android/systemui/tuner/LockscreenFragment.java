package com.android.systemui.tuner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.C2019R$xml;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.IntentButtonProvider.IntentButton;
import com.android.systemui.plugins.IntentButtonProvider.IntentButton.IconState;
import com.android.systemui.statusbar.ScalingDrawableWrapper;
import com.android.systemui.statusbar.phone.ExpandableIndicator;
import com.android.systemui.statusbar.policy.ExtensionController.TunerFactory;
import com.android.systemui.tuner.LockscreenFragment.Adapter;
import com.android.systemui.tuner.LockscreenFragment.Holder;
import com.android.systemui.tuner.ShortcutParser.Shortcut;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class LockscreenFragment extends PreferenceFragment {
    private final ArrayList<Tunable> mTunables = new ArrayList<>();
    private TunerService mTunerService;

    private static class ActivityButton implements IntentButton {
        private final IconState mIconState;
        private final Intent mIntent;

        public ActivityButton(Context context, ActivityInfo activityInfo) {
            this.mIntent = new Intent().setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
            IconState iconState = new IconState();
            this.mIconState = iconState;
            iconState.drawable = activityInfo.loadIcon(context.getPackageManager()).mutate();
            this.mIconState.contentDescription = activityInfo.loadLabel(context.getPackageManager());
            int applyDimension = (int) TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
            IconState iconState2 = this.mIconState;
            Drawable drawable = this.mIconState.drawable;
            iconState2.drawable = new ScalingDrawableWrapper(drawable, ((float) applyDimension) / ((float) drawable.getIntrinsicWidth()));
            this.mIconState.tint = false;
        }

        public IconState getIcon() {
            return this.mIconState;
        }

        public Intent getIntent() {
            return this.mIntent;
        }
    }

    public static class Adapter extends androidx.recyclerview.widget.RecyclerView.Adapter<Holder> {
        private final Consumer<Item> mCallback;
        private ArrayList<Item> mItems;

        public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(C2013R$layout.tuner_shortcut_item, viewGroup, false));
        }

        public void onBindViewHolder(Holder holder, int i) {
            Item item = (Item) this.mItems.get(i);
            holder.icon.setImageDrawable(item.getDrawable());
            holder.title.setText(item.getLabel());
            holder.itemView.setOnClickListener(new OnClickListener(holder) {
                public final /* synthetic */ Holder f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    Adapter.this.lambda$onBindViewHolder$0$LockscreenFragment$Adapter(this.f$1, view);
                }
            });
            Boolean expando = item.getExpando();
            if (expando != null) {
                holder.expand.setVisibility(0);
                holder.expand.setExpanded(expando.booleanValue());
                holder.expand.setOnClickListener(new OnClickListener(holder) {
                    public final /* synthetic */ Holder f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        Adapter.this.lambda$onBindViewHolder$1$LockscreenFragment$Adapter(this.f$1, view);
                    }
                });
                return;
            }
            holder.expand.setVisibility(8);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$LockscreenFragment$Adapter(Holder holder, View view) {
            this.mCallback.accept((Item) this.mItems.get(holder.getAdapterPosition()));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$1 */
        public /* synthetic */ void lambda$onBindViewHolder$1$LockscreenFragment$Adapter(Holder holder, View view) {
            ((Item) this.mItems.get(holder.getAdapterPosition())).toggleExpando(this);
        }

        public int getItemCount() {
            return this.mItems.size();
        }

        public void remItem(Item item) {
            int indexOf = this.mItems.indexOf(item);
            this.mItems.remove(item);
            notifyItemRemoved(indexOf);
        }

        public void addItem(Item item, Item item2) {
            int indexOf = this.mItems.indexOf(item) + 1;
            this.mItems.add(indexOf, item2);
            notifyItemInserted(indexOf);
        }
    }

    private static class App extends Item {
        private final ArrayList<Item> mChildren;
        private final Context mContext;
        private boolean mExpanded;
        private final LauncherActivityInfo mInfo;

        public Drawable getDrawable() {
            return this.mInfo.getBadgedIcon(this.mContext.getResources().getConfiguration().densityDpi);
        }

        public String getLabel() {
            return this.mInfo.getLabel().toString();
        }

        public Boolean getExpando() {
            if (this.mChildren.size() != 0) {
                return Boolean.valueOf(this.mExpanded);
            }
            return null;
        }

        public void toggleExpando(Adapter adapter) {
            boolean z = !this.mExpanded;
            this.mExpanded = z;
            if (z) {
                this.mChildren.forEach(new Consumer(adapter) {
                    public final /* synthetic */ Adapter f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        App.this.lambda$toggleExpando$0$LockscreenFragment$App(this.f$1, (Item) obj);
                    }
                });
            } else {
                this.mChildren.forEach(new Consumer() {
                    public final void accept(Object obj) {
                        Adapter.this.remItem((Item) obj);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$toggleExpando$0 */
        public /* synthetic */ void lambda$toggleExpando$0$LockscreenFragment$App(Adapter adapter, Item item) {
            adapter.addItem(this, item);
        }
    }

    public static class Holder extends ViewHolder {
        public final ExpandableIndicator expand;
        public final ImageView icon;
        public final TextView title;

        public Holder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(16908294);
            this.title = (TextView) view.findViewById(16908310);
            this.expand = (ExpandableIndicator) view.findViewById(C2011R$id.expand);
        }
    }

    private static abstract class Item {
        public abstract Drawable getDrawable();

        public abstract Boolean getExpando();

        public abstract String getLabel();

        public void toggleExpando(Adapter adapter) {
        }

        private Item() {
        }
    }

    public static class LockButtonFactory implements TunerFactory<IntentButton> {
        private final Context mContext;
        private final String mKey;

        public LockButtonFactory(Context context, String str) {
            this.mContext = context;
            this.mKey = str;
        }

        public String[] keys() {
            return new String[]{this.mKey};
        }

        public IntentButton create(Map<String, String> map) {
            String str = (String) map.get(this.mKey);
            if (!TextUtils.isEmpty(str)) {
                if (str.contains("::")) {
                    Shortcut shortcutInfo = LockscreenFragment.getShortcutInfo(this.mContext, str);
                    if (shortcutInfo != null) {
                        return new ShortcutButton(this.mContext, shortcutInfo);
                    }
                } else if (str.contains("/")) {
                    ActivityInfo activityinfo = LockscreenFragment.getActivityinfo(this.mContext, str);
                    if (activityinfo != null) {
                        return new ActivityButton(this.mContext, activityinfo);
                    }
                }
            }
            return null;
        }
    }

    private static class ShortcutButton implements IntentButton {
        private final IconState mIconState;
        private final Shortcut mShortcut;

        public ShortcutButton(Context context, Shortcut shortcut) {
            this.mShortcut = shortcut;
            IconState iconState = new IconState();
            this.mIconState = iconState;
            iconState.drawable = shortcut.icon.loadDrawable(context).mutate();
            this.mIconState.contentDescription = this.mShortcut.label;
            int applyDimension = (int) TypedValue.applyDimension(1, 32.0f, context.getResources().getDisplayMetrics());
            IconState iconState2 = this.mIconState;
            Drawable drawable = this.mIconState.drawable;
            iconState2.drawable = new ScalingDrawableWrapper(drawable, ((float) applyDimension) / ((float) drawable.getIntrinsicWidth()));
            this.mIconState.tint = false;
        }

        public IconState getIcon() {
            return this.mIconState;
        }

        public Intent getIntent() {
            return this.mShortcut.intent;
        }
    }

    private static class StaticShortcut extends Item {
        private final Context mContext;
        private final Shortcut mShortcut;

        public Boolean getExpando() {
            return null;
        }

        public Drawable getDrawable() {
            return this.mShortcut.icon.loadDrawable(this.mContext);
        }

        public String getLabel() {
            return this.mShortcut.label;
        }
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        this.mTunerService = (TunerService) Dependency.get(TunerService.class);
        new Handler();
        addPreferencesFromResource(C2019R$xml.lockscreen_settings);
        setupGroup("sysui_keyguard_left", "sysui_keyguard_left_unlock");
        setupGroup("sysui_keyguard_right", "sysui_keyguard_right_unlock");
    }

    public void onDestroy() {
        super.onDestroy();
        this.mTunables.forEach(new Consumer() {
            public final void accept(Object obj) {
                LockscreenFragment.this.lambda$onDestroy$0$LockscreenFragment((Tunable) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDestroy$0 */
    public /* synthetic */ void lambda$onDestroy$0$LockscreenFragment(Tunable tunable) {
        this.mTunerService.removeTunable(tunable);
    }

    private void setupGroup(String str, String str2) {
        addTunable(new Tunable((SwitchPreference) findPreference(str2), findPreference(str)) {
            public final /* synthetic */ SwitchPreference f$1;
            public final /* synthetic */ Preference f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onTuningChanged(String str, String str2) {
                LockscreenFragment.this.lambda$setupGroup$1$LockscreenFragment(this.f$1, this.f$2, str, str2);
            }
        }, str);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setupGroup$1 */
    public /* synthetic */ void lambda$setupGroup$1$LockscreenFragment(SwitchPreference switchPreference, Preference preference, String str, String str2) {
        switchPreference.setVisible(!TextUtils.isEmpty(str2));
        setSummary(preference, str2);
    }

    private void setSummary(Preference preference, String str) {
        if (str == null) {
            preference.setSummary(C2017R$string.lockscreen_none);
            return;
        }
        CharSequence charSequence = null;
        if (str.contains("::")) {
            Shortcut shortcutInfo = getShortcutInfo(getContext(), str);
            if (shortcutInfo != null) {
                charSequence = shortcutInfo.label;
            }
            preference.setSummary(charSequence);
        } else if (str.contains("/")) {
            ActivityInfo activityinfo = getActivityinfo(getContext(), str);
            if (activityinfo != null) {
                charSequence = activityinfo.loadLabel(getContext().getPackageManager());
            }
            preference.setSummary(charSequence);
        } else {
            preference.setSummary(C2017R$string.lockscreen_none);
        }
    }

    private void addTunable(Tunable tunable, String... strArr) {
        this.mTunables.add(tunable);
        this.mTunerService.addTunable(tunable, strArr);
    }

    public static ActivityInfo getActivityinfo(Context context, String str) {
        try {
            return context.getPackageManager().getActivityInfo(ComponentName.unflattenFromString(str), 0);
        } catch (NameNotFoundException unused) {
            return null;
        }
    }

    public static Shortcut getShortcutInfo(Context context, String str) {
        return Shortcut.create(context, str);
    }
}
