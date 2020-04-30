package com.android.systemui.toast;

import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.ITransientNotificationCallback;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.accessibility.AccessibilityManager;
import android.widget.ToastPresenter;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import java.util.Objects;

public class ToastUI extends SystemUI implements Callbacks {
    private final CommandQueue mCommandQueue;
    private ToastEntry mCurrentToast;
    private final INotificationManager mNotificationManager;
    private final ToastPresenter mPresenter;
    private final WindowManager mWindowManager;

    private static class ToastEntry {
        public final ITransientNotificationCallback callback;
        public final String packageName;
        public final IBinder token;
        public final View view;
        public final IBinder windowToken;

        private ToastEntry(String str, IBinder iBinder, View view2, IBinder iBinder2, ITransientNotificationCallback iTransientNotificationCallback) {
            this.packageName = str;
            this.token = iBinder;
            this.view = view2;
            this.windowToken = iBinder2;
            this.callback = iTransientNotificationCallback;
        }
    }

    public ToastUI(Context context, CommandQueue commandQueue) {
        this(context, commandQueue, (WindowManager) context.getSystemService("window"), Stub.asInterface(ServiceManager.getService("notification")), AccessibilityManager.getInstance(context));
    }

    @VisibleForTesting
    ToastUI(Context context, CommandQueue commandQueue, WindowManager windowManager, INotificationManager iNotificationManager, AccessibilityManager accessibilityManager) {
        super(context);
        this.mCommandQueue = commandQueue;
        this.mWindowManager = windowManager;
        this.mNotificationManager = iNotificationManager;
        this.mPresenter = new ToastPresenter(context, accessibilityManager);
    }

    public void start() {
        this.mCommandQueue.addCallback((Callbacks) this);
    }

    public void showToast(String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i, ITransientNotificationCallback iTransientNotificationCallback) {
        String str2 = "ToastUI";
        if (this.mCurrentToast != null) {
            hideCurrentToast();
        }
        View textToastView = this.mPresenter.getTextToastView(charSequence);
        LayoutParams layoutParams = getLayoutParams(str, iBinder2, i);
        ToastEntry toastEntry = new ToastEntry(str, iBinder, textToastView, iBinder2, iTransientNotificationCallback);
        this.mCurrentToast = toastEntry;
        try {
            this.mWindowManager.addView(textToastView, layoutParams);
            this.mPresenter.trySendAccessibilityEvent(textToastView, str);
            if (iTransientNotificationCallback != null) {
                try {
                    iTransientNotificationCallback.onToastShown();
                } catch (RemoteException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error calling back ");
                    sb.append(str);
                    sb.append(" to notify onToastShow()");
                    Log.w(str2, sb.toString(), e);
                }
            }
        } catch (BadTokenException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Error while attempting to show toast from ");
            sb2.append(str);
            Log.w(str2, sb2.toString(), e2);
        }
    }

    public void hideToast(String str, IBinder iBinder) {
        ToastEntry toastEntry = this.mCurrentToast;
        if (toastEntry == null || !Objects.equals(toastEntry.packageName, str) || !Objects.equals(this.mCurrentToast.token, iBinder)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Attempt to hide non-current toast from package ");
            sb.append(str);
            Log.w("ToastUI", sb.toString());
            return;
        }
        hideCurrentToast();
    }

    private void hideCurrentToast() {
        String str = "ToastUI";
        if (this.mCurrentToast.view.getParent() != null) {
            this.mWindowManager.removeViewImmediate(this.mCurrentToast.view);
        }
        ToastEntry toastEntry = this.mCurrentToast;
        String str2 = toastEntry.packageName;
        try {
            this.mNotificationManager.finishToken(str2, toastEntry.windowToken);
        } catch (RemoteException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error finishing toast window token from package ");
            sb.append(str2);
            Log.w(str, sb.toString(), e);
        }
        ITransientNotificationCallback iTransientNotificationCallback = this.mCurrentToast.callback;
        if (iTransientNotificationCallback != null) {
            try {
                iTransientNotificationCallback.onToastHidden();
            } catch (RemoteException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Error calling back ");
                sb2.append(str2);
                sb2.append(" to notify onToastHide()");
                Log.w(str, sb2.toString(), e2);
            }
        }
        this.mCurrentToast = null;
    }

    private LayoutParams getLayoutParams(String str, IBinder iBinder, int i) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.mPresenter.startLayoutParams(layoutParams, str);
        this.mPresenter.adjustLayoutParams(layoutParams, iBinder, i, this.mContext.getResources().getInteger(17694908), 0, this.mContext.getResources().getDimensionPixelSize(17105518), 0.0f, 0.0f);
        return layoutParams;
    }
}
