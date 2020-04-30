package androidx.appcompat.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import androidx.collection.ArraySet;
import java.lang.ref.WeakReference;
import java.util.Iterator;

public abstract class AppCompatDelegate {
    private static final ArraySet<WeakReference<AppCompatDelegate>> sActiveDelegates = new ArraySet<>();
    private static final Object sActiveDelegatesLock = new Object();
    private static int sDefaultNightMode = -100;

    public abstract void addContentView(View view, LayoutParams layoutParams);

    @Deprecated
    public void attachBaseContext(Context context) {
    }

    public abstract <T extends View> T findViewById(int i);

    public int getLocalNightMode() {
        return -100;
    }

    public abstract MenuInflater getMenuInflater();

    public abstract ActionBar getSupportActionBar();

    public abstract void installViewFactory();

    public abstract void invalidateOptionsMenu();

    public abstract void onConfigurationChanged(Configuration configuration);

    public abstract void onCreate(Bundle bundle);

    public abstract void onDestroy();

    public abstract void onPostCreate(Bundle bundle);

    public abstract void onPostResume();

    public abstract void onSaveInstanceState(Bundle bundle);

    public abstract void onStart();

    public abstract void onStop();

    public abstract boolean requestWindowFeature(int i);

    public abstract void setContentView(int i);

    public abstract void setContentView(View view);

    public abstract void setContentView(View view, LayoutParams layoutParams);

    public void setTheme(int i) {
    }

    public abstract void setTitle(CharSequence charSequence);

    public static AppCompatDelegate create(Activity activity, AppCompatCallback appCompatCallback) {
        return new AppCompatDelegateImpl(activity, appCompatCallback);
    }

    public static AppCompatDelegate create(Dialog dialog, AppCompatCallback appCompatCallback) {
        return new AppCompatDelegateImpl(dialog, appCompatCallback);
    }

    AppCompatDelegate() {
    }

    public Context attachBaseContext2(Context context) {
        attachBaseContext(context);
        return context;
    }

    public static int getDefaultNightMode() {
        return sDefaultNightMode;
    }

    static void addActiveDelegate(AppCompatDelegate appCompatDelegate) {
        synchronized (sActiveDelegatesLock) {
            removeDelegateFromActives(appCompatDelegate);
            sActiveDelegates.add(new WeakReference(appCompatDelegate));
        }
    }

    static void removeActiveDelegate(AppCompatDelegate appCompatDelegate) {
        synchronized (sActiveDelegatesLock) {
            removeDelegateFromActives(appCompatDelegate);
        }
    }

    private static void removeDelegateFromActives(AppCompatDelegate appCompatDelegate) {
        synchronized (sActiveDelegatesLock) {
            Iterator it = sActiveDelegates.iterator();
            while (it.hasNext()) {
                AppCompatDelegate appCompatDelegate2 = (AppCompatDelegate) ((WeakReference) it.next()).get();
                if (appCompatDelegate2 == appCompatDelegate || appCompatDelegate2 == null) {
                    it.remove();
                }
            }
        }
    }
}
