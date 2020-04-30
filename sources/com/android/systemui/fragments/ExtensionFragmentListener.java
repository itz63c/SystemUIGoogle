package com.android.systemui.fragments;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import com.android.systemui.plugins.FragmentBase;
import com.android.systemui.statusbar.policy.ExtensionController.Extension;
import java.util.function.Consumer;

public class ExtensionFragmentListener<T extends FragmentBase> implements Consumer<T> {
    private final Extension<T> mExtension;
    private final FragmentHostManager mFragmentHostManager;
    private final int mId;
    private String mOldClass;
    private final String mTag;

    private ExtensionFragmentListener(View view, String str, int i, Extension<T> extension) {
        this.mTag = str;
        FragmentHostManager fragmentHostManager = FragmentHostManager.get(view);
        this.mFragmentHostManager = fragmentHostManager;
        this.mExtension = extension;
        this.mId = i;
        fragmentHostManager.getFragmentManager().beginTransaction().replace(i, (Fragment) this.mExtension.get(), this.mTag).commit();
        this.mExtension.clearItem(false);
    }

    public void accept(T t) {
        try {
            Fragment.class.cast(t);
            this.mFragmentHostManager.getExtensionManager().setCurrentExtension(this.mId, this.mTag, this.mOldClass, t.getClass().getName(), this.mExtension.getContext());
            this.mOldClass = t.getClass().getName();
        } catch (ClassCastException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(t.getClass().getName());
            sb.append(" must be a Fragment");
            Log.e("ExtensionFragmentListener", sb.toString(), e);
        }
        this.mExtension.clearItem(true);
    }

    public static <T> void attachExtensonToFragment(View view, String str, int i, Extension<T> extension) {
        extension.addCallback(new ExtensionFragmentListener(view, str, i, extension));
    }
}
