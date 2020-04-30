package androidx.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import androidx.core.content.ContextCompat;

public class PreferenceManager {
    private Context mContext;
    private Editor mEditor;
    private long mNextId = 0;
    private boolean mNoCommit;
    private OnDisplayPreferenceDialogListener mOnDisplayPreferenceDialogListener;
    private OnNavigateToScreenListener mOnNavigateToScreenListener;
    private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
    private PreferenceComparisonCallback mPreferenceComparisonCallback;
    private PreferenceDataStore mPreferenceDataStore;
    private PreferenceScreen mPreferenceScreen;
    private SharedPreferences mSharedPreferences;
    private int mSharedPreferencesMode;
    private String mSharedPreferencesName;
    private int mStorage = 0;

    public interface OnDisplayPreferenceDialogListener {
        void onDisplayPreferenceDialog(Preference preference);
    }

    public interface OnNavigateToScreenListener {
        void onNavigateToScreen(PreferenceScreen preferenceScreen);
    }

    public interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(Preference preference);
    }

    public static abstract class PreferenceComparisonCallback {
        public abstract boolean arePreferenceContentsTheSame(Preference preference, Preference preference2);

        public abstract boolean arePreferenceItemsTheSame(Preference preference, Preference preference2);
    }

    public PreferenceManager(Context context) {
        this.mContext = context;
        setSharedPreferencesName(getDefaultSharedPreferencesName(context));
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getPackageName());
        sb.append("_preferences");
        return sb.toString();
    }

    public PreferenceScreen inflateFromResource(Context context, int i, PreferenceScreen preferenceScreen) {
        setNoCommit(true);
        PreferenceScreen preferenceScreen2 = (PreferenceScreen) new PreferenceInflater(context, this).inflate(i, (PreferenceGroup) preferenceScreen);
        preferenceScreen2.onAttachedToHierarchy(this);
        setNoCommit(false);
        return preferenceScreen2;
    }

    public PreferenceScreen createPreferenceScreen(Context context) {
        PreferenceScreen preferenceScreen = new PreferenceScreen(context, null);
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    /* access modifiers changed from: 0000 */
    public long getNextId() {
        long j;
        synchronized (this) {
            j = this.mNextId;
            this.mNextId = 1 + j;
        }
        return j;
    }

    public void setSharedPreferencesName(String str) {
        this.mSharedPreferencesName = str;
        this.mSharedPreferences = null;
    }

    public PreferenceDataStore getPreferenceDataStore() {
        return this.mPreferenceDataStore;
    }

    public SharedPreferences getSharedPreferences() {
        Context context;
        if (getPreferenceDataStore() != null) {
            return null;
        }
        if (this.mSharedPreferences == null) {
            if (this.mStorage != 1) {
                context = this.mContext;
            } else {
                context = ContextCompat.createDeviceProtectedStorageContext(this.mContext);
            }
            this.mSharedPreferences = context.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
        }
        return this.mSharedPreferences;
    }

    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceScreen;
    }

    public boolean setPreferences(PreferenceScreen preferenceScreen) {
        PreferenceScreen preferenceScreen2 = this.mPreferenceScreen;
        if (preferenceScreen == preferenceScreen2) {
            return false;
        }
        if (preferenceScreen2 != null) {
            preferenceScreen2.onDetached();
        }
        this.mPreferenceScreen = preferenceScreen;
        return true;
    }

    public <T extends Preference> T findPreference(CharSequence charSequence) {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen == null) {
            return null;
        }
        return preferenceScreen.findPreference(charSequence);
    }

    /* access modifiers changed from: 0000 */
    public Editor getEditor() {
        if (this.mPreferenceDataStore != null) {
            return null;
        }
        if (!this.mNoCommit) {
            return getSharedPreferences().edit();
        }
        if (this.mEditor == null) {
            this.mEditor = getSharedPreferences().edit();
        }
        return this.mEditor;
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldCommit() {
        return !this.mNoCommit;
    }

    private void setNoCommit(boolean z) {
        if (!z) {
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.apply();
            }
        }
        this.mNoCommit = z;
    }

    public Context getContext() {
        return this.mContext;
    }

    public PreferenceComparisonCallback getPreferenceComparisonCallback() {
        return this.mPreferenceComparisonCallback;
    }

    public void setOnDisplayPreferenceDialogListener(OnDisplayPreferenceDialogListener onDisplayPreferenceDialogListener) {
        this.mOnDisplayPreferenceDialogListener = onDisplayPreferenceDialogListener;
    }

    public void showDialog(Preference preference) {
        OnDisplayPreferenceDialogListener onDisplayPreferenceDialogListener = this.mOnDisplayPreferenceDialogListener;
        if (onDisplayPreferenceDialogListener != null) {
            onDisplayPreferenceDialogListener.onDisplayPreferenceDialog(preference);
        }
    }

    public void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener onPreferenceTreeClickListener) {
        this.mOnPreferenceTreeClickListener = onPreferenceTreeClickListener;
    }

    public OnPreferenceTreeClickListener getOnPreferenceTreeClickListener() {
        return this.mOnPreferenceTreeClickListener;
    }

    public void setOnNavigateToScreenListener(OnNavigateToScreenListener onNavigateToScreenListener) {
        this.mOnNavigateToScreenListener = onNavigateToScreenListener;
    }

    public OnNavigateToScreenListener getOnNavigateToScreenListener() {
        return this.mOnNavigateToScreenListener;
    }
}
