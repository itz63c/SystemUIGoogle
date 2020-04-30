package com.android.systemui.charging;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.LayoutParams;

public class WirelessChargingAnimation {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("WirelessChargingView", 3);
    private static WirelessChargingView mPreviousWirelessChargingView;
    private final WirelessChargingView mCurrentWirelessChargingView;

    public interface Callback {
        void onAnimationEnded();

        void onAnimationStarting();
    }

    private static class WirelessChargingView {
        private Callback mCallback;
        private final Handler mHandler;
        /* access modifiers changed from: private */
        public View mNextView;
        private final LayoutParams mParams = new LayoutParams();
        private View mView;
        private WindowManager mWM;

        public WirelessChargingView(Context context, Looper looper, int i, Callback callback, boolean z) {
            this.mCallback = callback;
            this.mNextView = new WirelessChargingLayout(context, i, z);
            LayoutParams layoutParams = this.mParams;
            layoutParams.height = -2;
            layoutParams.width = -1;
            layoutParams.format = -3;
            layoutParams.type = 2009;
            layoutParams.setTitle("Charging Animation");
            layoutParams.flags = 26;
            layoutParams.dimAmount = 0.3f;
            if (looper == null) {
                looper = Looper.myLooper();
                if (looper == null) {
                    throw new RuntimeException("Can't display wireless animation on a thread that has not called Looper.prepare()");
                }
            }
            this.mHandler = new Handler(looper, null) {
                public void handleMessage(Message message) {
                    int i = message.what;
                    if (i == 0) {
                        WirelessChargingView.this.handleShow();
                    } else if (i == 1) {
                        WirelessChargingView.this.handleHide();
                        WirelessChargingView.this.mNextView = null;
                    }
                }
            };
        }

        public void show() {
            if (WirelessChargingAnimation.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("SHOW: ");
                sb.append(this);
                Slog.d("WirelessChargingView", sb.toString());
            }
            this.mHandler.obtainMessage(0).sendToTarget();
        }

        public void hide(long j) {
            this.mHandler.removeMessages(1);
            if (WirelessChargingAnimation.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("HIDE: ");
                sb.append(this);
                Slog.d("WirelessChargingView", sb.toString());
            }
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(Message.obtain(handler, 1), j);
        }

        /* access modifiers changed from: private */
        public void handleShow() {
            String str = "WirelessChargingView";
            if (WirelessChargingAnimation.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("HANDLE SHOW: ");
                sb.append(this);
                sb.append(" mView=");
                sb.append(this.mView);
                sb.append(" mNextView=");
                sb.append(this.mNextView);
                Slog.d(str, sb.toString());
            }
            if (this.mView != this.mNextView) {
                handleHide();
                View view = this.mNextView;
                this.mView = view;
                Context applicationContext = view.getContext().getApplicationContext();
                String opPackageName = this.mView.getContext().getOpPackageName();
                if (applicationContext == null) {
                    applicationContext = this.mView.getContext();
                }
                this.mWM = (WindowManager) applicationContext.getSystemService("window");
                LayoutParams layoutParams = this.mParams;
                layoutParams.packageName = opPackageName;
                layoutParams.hideTimeoutMilliseconds = 1133;
                String str2 = " in ";
                if (this.mView.getParent() != null) {
                    if (WirelessChargingAnimation.DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("REMOVE! ");
                        sb2.append(this.mView);
                        sb2.append(str2);
                        sb2.append(this);
                        Slog.d(str, sb2.toString());
                    }
                    this.mWM.removeView(this.mView);
                }
                if (WirelessChargingAnimation.DEBUG) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("ADD! ");
                    sb3.append(this.mView);
                    sb3.append(str2);
                    sb3.append(this);
                    Slog.d(str, sb3.toString());
                }
                try {
                    if (this.mCallback != null) {
                        this.mCallback.onAnimationStarting();
                    }
                    this.mWM.addView(this.mView, this.mParams);
                } catch (BadTokenException e) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Unable to add wireless charging view. ");
                    sb4.append(e);
                    Slog.d(str, sb4.toString());
                }
            }
        }

        /* access modifiers changed from: private */
        public void handleHide() {
            String str = "WirelessChargingView";
            if (WirelessChargingAnimation.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("HANDLE HIDE: ");
                sb.append(this);
                sb.append(" mView=");
                sb.append(this.mView);
                Slog.d(str, sb.toString());
            }
            View view = this.mView;
            if (view != null) {
                if (view.getParent() != null) {
                    if (WirelessChargingAnimation.DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("REMOVE! ");
                        sb2.append(this.mView);
                        sb2.append(" in ");
                        sb2.append(this);
                        Slog.d(str, sb2.toString());
                    }
                    Callback callback = this.mCallback;
                    if (callback != null) {
                        callback.onAnimationEnded();
                    }
                    this.mWM.removeViewImmediate(this.mView);
                }
                this.mView = null;
            }
        }
    }

    public WirelessChargingAnimation(Context context, Looper looper, int i, Callback callback, boolean z) {
        WirelessChargingView wirelessChargingView = new WirelessChargingView(context, looper, i, callback, z);
        this.mCurrentWirelessChargingView = wirelessChargingView;
    }

    public static WirelessChargingAnimation makeWirelessChargingAnimation(Context context, Looper looper, int i, Callback callback, boolean z) {
        WirelessChargingAnimation wirelessChargingAnimation = new WirelessChargingAnimation(context, looper, i, callback, z);
        return wirelessChargingAnimation;
    }

    public void show() {
        WirelessChargingView wirelessChargingView = this.mCurrentWirelessChargingView;
        if (wirelessChargingView == null || wirelessChargingView.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        WirelessChargingView wirelessChargingView2 = mPreviousWirelessChargingView;
        if (wirelessChargingView2 != null) {
            wirelessChargingView2.hide(0);
        }
        WirelessChargingView wirelessChargingView3 = this.mCurrentWirelessChargingView;
        mPreviousWirelessChargingView = wirelessChargingView3;
        wirelessChargingView3.show();
        this.mCurrentWirelessChargingView.hide(1133);
    }
}
