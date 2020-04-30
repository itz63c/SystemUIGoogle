package com.android.settingslib.wifi;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.R$attr;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiEntry.WifiEntryCallback;

public class WifiEntryPreference extends Preference implements WifiEntryCallback {
    private static final int[] FRICTION_ATTRS = {R$attr.wifi_friction};
    private static final int[] STATE_SECURED = {R$attr.state_encrypted};
    private static final int[] WIFI_CONNECTION_STRENGTH = {R$string.accessibility_no_wifi, R$string.accessibility_wifi_one_bar, R$string.accessibility_wifi_two_bars, R$string.accessibility_wifi_three_bars, R$string.accessibility_wifi_signal_full};
    private CharSequence mContentDescription;
    private final StateListDrawable mFrictionSld;
    private final IconInjector mIconInjector;
    private int mLevel = -1;
    private WifiEntry mWifiEntry;

    static class IconInjector {
        public abstract Drawable getIcon(int i);
    }

    WifiEntryPreference(Context context, WifiEntry wifiEntry, IconInjector iconInjector) {
        super(context);
        setLayoutResource(R$layout.preference_access_point);
        setWidgetLayoutResource(R$layout.access_point_friction_widget);
        this.mFrictionSld = getFrictionStateListDrawable();
        this.mWifiEntry = wifiEntry;
        wifiEntry.setListener(this);
        this.mIconInjector = iconInjector;
        refresh();
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Drawable icon = getIcon();
        if (icon != null) {
            icon.setLevel(this.mLevel);
        }
        preferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
        bindFrictionImage((ImageView) preferenceViewHolder.findViewById(R$id.friction_icon));
        preferenceViewHolder.findViewById(R$id.two_target_divider).setVisibility(4);
    }

    public void refresh() {
        setTitle((CharSequence) this.mWifiEntry.getTitle());
        int level = this.mWifiEntry.getLevel();
        if (level != this.mLevel) {
            this.mLevel = level;
            updateIcon(level);
            notifyChanged();
        }
        setSummary((CharSequence) this.mWifiEntry.getSummary(false));
        this.mContentDescription = buildContentDescription();
    }

    public void onUpdated() {
        refresh();
    }

    private void updateIcon(int i) {
        if (i == -1) {
            setIcon((Drawable) null);
            return;
        }
        Drawable icon = this.mIconInjector.getIcon(i);
        if (icon != null) {
            icon.setTintList(Utils.getColorAttr(getContext(), 16843817));
            setIcon(icon);
        } else {
            setIcon((Drawable) null);
        }
    }

    private StateListDrawable getFrictionStateListDrawable() {
        TypedArray typedArray;
        try {
            typedArray = getContext().getTheme().obtainStyledAttributes(FRICTION_ATTRS);
        } catch (NotFoundException unused) {
            typedArray = null;
        }
        if (typedArray != null) {
            return (StateListDrawable) typedArray.getDrawable(0);
        }
        return null;
    }

    private void bindFrictionImage(ImageView imageView) {
        if (imageView != null && this.mFrictionSld != null) {
            if (!(this.mWifiEntry.getSecurity() == 0 || this.mWifiEntry.getSecurity() == 4)) {
                this.mFrictionSld.setState(STATE_SECURED);
            }
            imageView.setImageDrawable(this.mFrictionSld.getCurrent());
        }
    }

    /* access modifiers changed from: 0000 */
    public CharSequence buildContentDescription() {
        String str;
        Context context = getContext();
        CharSequence title = getTitle();
        CharSequence summary = getSummary();
        String str2 = ",";
        if (!TextUtils.isEmpty(summary)) {
            title = TextUtils.concat(new CharSequence[]{title, str2, summary});
        }
        int level = this.mWifiEntry.getLevel();
        if (level >= 0) {
            int[] iArr = WIFI_CONNECTION_STRENGTH;
            if (level < iArr.length) {
                title = TextUtils.concat(new CharSequence[]{title, str2, context.getString(iArr[level])});
            }
        }
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = title;
        charSequenceArr[1] = str2;
        if (this.mWifiEntry.getSecurity() == 0) {
            str = context.getString(R$string.accessibility_wifi_security_type_none);
        } else {
            str = context.getString(R$string.accessibility_wifi_security_type_secured);
        }
        charSequenceArr[2] = str;
        return TextUtils.concat(charSequenceArr);
    }
}
