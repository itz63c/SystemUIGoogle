package com.android.wifitrackerlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class BaseWifiTracker implements LifecycleObserver {
    private static boolean sVerboseLogging;
    private final BroadcastReceiver mBroadcastReceiver;
    protected final ConnectivityManager mConnectivityManager;
    protected final Context mContext;
    protected final Handler mMainHandler;
    protected final long mMaxScanAgeMillis;
    private final NetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    protected final long mScanIntervalMillis;
    protected final ScanResultUpdater mScanResultUpdater;
    private final Scanner mScanner;
    /* access modifiers changed from: private */
    public final String mTag;
    protected final WifiManager mWifiManager;
    protected final Handler mWorkerHandler;

    private class Scanner extends Handler {
        private int mRetry;
        final /* synthetic */ BaseWifiTracker this$0;

        /* access modifiers changed from: private */
        public void start() {
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v(this.this$0.mTag, "Scanner start");
            }
            post(new Runnable() {
                public final void run() {
                    Scanner.this.postScan();
                }
            });
        }

        /* access modifiers changed from: private */
        public void stop() {
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v(this.this$0.mTag, "Scanner stop");
            }
            this.mRetry = 0;
            removeCallbacksAndMessages(null);
        }

        /* access modifiers changed from: private */
        public void postScan() {
            if (this.this$0.mWifiManager.startScan()) {
                this.mRetry = 0;
            } else {
                int i = this.mRetry + 1;
                this.mRetry = i;
                if (i >= 3) {
                    if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                        String access$000 = this.this$0.mTag;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Scanner failed to start scan ");
                        sb.append(this.mRetry);
                        sb.append(" times!");
                        Log.v(access$000, sb.toString());
                    }
                    this.mRetry = 0;
                    return;
                }
            }
            postDelayed(new Runnable() {
                public final void run() {
                    Scanner.this.postScan();
                }
            }, this.this$0.mScanIntervalMillis);
        }
    }

    /* access modifiers changed from: protected */
    public void handleOnStart() {
    }

    public static boolean isVerboseLoggingEnabled() {
        return sVerboseLogging;
    }

    @OnLifecycleEvent(Event.ON_START)
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, null, this.mWorkerHandler);
        this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mWorkerHandler);
        if (this.mWifiManager.getWifiState() == 3) {
            this.mScanner.start();
        } else {
            this.mScanner.stop();
        }
        this.mWorkerHandler.post(new Runnable() {
            public final void run() {
                BaseWifiTracker.this.handleOnStart();
            }
        });
    }

    @OnLifecycleEvent(Event.ON_STOP)
    public void onStop() {
        this.mScanner.stop();
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    }
}
