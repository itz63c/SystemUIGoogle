package com.android.systemui.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings.System;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import java.util.List;
import java.util.function.Consumer;

public class Utils {

    public static class DisableStateTracker implements Callbacks, OnAttachStateChangeListener {
        private final CommandQueue mCommandQueue;
        private boolean mDisabled;
        private final int mMask1;
        private final int mMask2;
        private View mView;

        public DisableStateTracker(int i, int i2, CommandQueue commandQueue) {
            this.mMask1 = i;
            this.mMask2 = i2;
            this.mCommandQueue = commandQueue;
        }

        public void onViewAttachedToWindow(View view) {
            this.mView = view;
            this.mCommandQueue.addCallback((Callbacks) this);
        }

        public void onViewDetachedFromWindow(View view) {
            this.mCommandQueue.removeCallback((Callbacks) this);
            this.mView = null;
        }

        public void disable(int i, int i2, int i3, boolean z) {
            if (i == this.mView.getDisplay().getDisplayId()) {
                int i4 = this.mMask1 & i2;
                int i5 = 0;
                boolean z2 = (i4 == 0 && (this.mMask2 & i3) == 0) ? false : true;
                if (z2 != this.mDisabled) {
                    this.mDisabled = z2;
                    View view = this.mView;
                    if (z2) {
                        i5 = 8;
                    }
                    view.setVisibility(i5);
                }
            }
        }
    }

    public static <T> void safeForeach(List<T> list, Consumer<T> consumer) {
        for (int size = list.size() - 1; size >= 0; size--) {
            Object obj = list.get(size);
            if (obj != null) {
                consumer.accept(obj);
            }
        }
    }

    public static boolean isHeadlessRemoteDisplayProvider(PackageManager packageManager, String str) {
        if (packageManager.checkPermission("android.permission.REMOTE_DISPLAY_PROVIDER", str) != 0) {
            return false;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        return packageManager.queryIntentActivities(intent, 0).isEmpty();
    }

    public static boolean isGesturalModeOnDefaultDisplay(Context context, int i) {
        return context.getDisplayId() == 0 && QuickStepContract.isGesturalMode(i);
    }

    public static boolean useQsMediaPlayer(Context context) {
        return System.getInt(context.getContentResolver(), "qs_media_player", 0) > 0;
    }
}
