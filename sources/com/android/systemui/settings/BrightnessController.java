package com.android.systemui.settings;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings.System;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.util.Log;
import android.util.MathUtils;
import com.android.internal.BrightnessSynchronizer;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.display.BrightnessUtils;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.ToggleSlider.Listener;
import java.util.ArrayList;
import java.util.Iterator;

public class BrightnessController implements Listener {
    /* access modifiers changed from: private */
    public static final Uri BRIGHTNESS_FLOAT_URI = System.getUriFor("screen_brightness_float");
    /* access modifiers changed from: private */
    public static final Uri BRIGHTNESS_FOR_VR_FLOAT_URI = System.getUriFor("screen_brightness_for_vr_float");
    /* access modifiers changed from: private */
    public static final Uri BRIGHTNESS_MODE_URI = System.getUriFor("screen_brightness_mode");
    /* access modifiers changed from: private */
    public static final Uri BRIGHTNESS_URI = System.getUriFor("screen_brightness");
    /* access modifiers changed from: private */
    public volatile boolean mAutomatic;
    /* access modifiers changed from: private */
    public final boolean mAutomaticAvailable;
    /* access modifiers changed from: private */
    public final Handler mBackgroundHandler;
    /* access modifiers changed from: private */
    public final BrightnessObserver mBrightnessObserver;
    /* access modifiers changed from: private */
    public ArrayList<BrightnessStateChangeCallback> mChangeCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final ToggleSlider mControl;
    private boolean mControlValueInitialized;
    /* access modifiers changed from: private */
    public final float mDefaultBacklight;
    /* access modifiers changed from: private */
    public final float mDefaultBacklightForVr;
    private final DisplayManager mDisplayManager;
    /* access modifiers changed from: private */
    public boolean mExternalChange;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            boolean z = true;
            BrightnessController.this.mExternalChange = true;
            try {
                int i = message.what;
                if (i == 1) {
                    BrightnessController brightnessController = BrightnessController.this;
                    float intBitsToFloat = Float.intBitsToFloat(message.arg1);
                    if (message.arg2 == 0) {
                        z = false;
                    }
                    brightnessController.updateSlider(intBitsToFloat, z);
                } else if (i == 2) {
                    ToggleSlider access$2200 = BrightnessController.this.mControl;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    access$2200.setChecked(z);
                } else if (i == 3) {
                    BrightnessController.this.mControl.setOnChangedListener(BrightnessController.this);
                } else if (i == 4) {
                    BrightnessController.this.mControl.setOnChangedListener(null);
                } else if (i != 5) {
                    super.handleMessage(message);
                } else {
                    BrightnessController brightnessController2 = BrightnessController.this;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    brightnessController2.updateVrMode(z);
                }
            } finally {
                BrightnessController.this.mExternalChange = false;
            }
        }
    };
    /* access modifiers changed from: private */
    public volatile boolean mIsVrModeEnabled;
    /* access modifiers changed from: private */
    public boolean mListening;
    private final float mMaximumBacklight;
    private final float mMaximumBacklightForVr;
    private final float mMinimumBacklight;
    private final float mMinimumBacklightForVr;
    private ValueAnimator mSliderAnimator;
    private final Runnable mStartListeningRunnable = new Runnable() {
        public void run() {
            if (!BrightnessController.this.mListening) {
                BrightnessController.this.mListening = true;
                if (BrightnessController.this.mVrManager != null) {
                    try {
                        BrightnessController.this.mVrManager.registerListener(BrightnessController.this.mVrStateCallbacks);
                        BrightnessController.this.mIsVrModeEnabled = BrightnessController.this.mVrManager.getVrModeState();
                    } catch (RemoteException e) {
                        Log.e("StatusBar.BrightnessController", "Failed to register VR mode state listener: ", e);
                    }
                }
                BrightnessController.this.mBrightnessObserver.startObserving();
                BrightnessController.this.mUserTracker.startTracking();
                BrightnessController.this.mUpdateModeRunnable.run();
                BrightnessController.this.mUpdateSliderRunnable.run();
                BrightnessController.this.mHandler.sendEmptyMessage(3);
            }
        }
    };
    private final Runnable mStopListeningRunnable = new Runnable() {
        public void run() {
            if (BrightnessController.this.mListening) {
                BrightnessController.this.mListening = false;
                if (BrightnessController.this.mVrManager != null) {
                    try {
                        BrightnessController.this.mVrManager.unregisterListener(BrightnessController.this.mVrStateCallbacks);
                    } catch (RemoteException e) {
                        Log.e("StatusBar.BrightnessController", "Failed to unregister VR mode state listener: ", e);
                    }
                }
                BrightnessController.this.mBrightnessObserver.stopObserving();
                BrightnessController.this.mUserTracker.stopTracking();
                BrightnessController.this.mHandler.sendEmptyMessage(4);
            }
        }
    };
    /* access modifiers changed from: private */
    public final Runnable mUpdateModeRunnable = new Runnable() {
        public void run() {
            boolean z = false;
            if (BrightnessController.this.mAutomaticAvailable) {
                int intForUser = System.getIntForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2);
                BrightnessController brightnessController = BrightnessController.this;
                if (intForUser != 0) {
                    z = true;
                }
                brightnessController.mAutomatic = z;
                return;
            }
            BrightnessController.this.mHandler.obtainMessage(2, Integer.valueOf(0)).sendToTarget();
        }
    };
    /* access modifiers changed from: private */
    public final Runnable mUpdateSliderRunnable = new Runnable() {
        public void run() {
            float f;
            boolean access$1200 = BrightnessController.this.mIsVrModeEnabled;
            if (access$1200) {
                f = System.getFloatForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness_for_vr_float", BrightnessController.this.mDefaultBacklightForVr, -2);
            } else {
                f = System.getFloatForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness_float", BrightnessController.this.mDefaultBacklight, -2);
            }
            BrightnessController.this.mHandler.obtainMessage(1, Float.floatToIntBits(f), access$1200 ? 1 : 0).sendToTarget();
        }
    };
    /* access modifiers changed from: private */
    public final CurrentUserTracker mUserTracker;
    /* access modifiers changed from: private */
    public final IVrManager mVrManager;
    /* access modifiers changed from: private */
    public final IVrStateCallbacks mVrStateCallbacks = new Stub() {
        public void onVrStateChanged(boolean z) {
            BrightnessController.this.mHandler.obtainMessage(5, z ? 1 : 0, 0).sendToTarget();
        }
    };

    private class BrightnessObserver extends ContentObserver {
        public BrightnessObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            onChange(z, null);
        }

        public void onChange(boolean z, Uri uri) {
            if (!z) {
                if (BrightnessController.BRIGHTNESS_MODE_URI.equals(uri)) {
                    BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                    BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
                } else if (BrightnessController.BRIGHTNESS_FLOAT_URI.equals(uri)) {
                    BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
                } else if (BrightnessController.BRIGHTNESS_FOR_VR_FLOAT_URI.equals(uri)) {
                    BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
                } else {
                    BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                    BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
                }
                Iterator it = BrightnessController.this.mChangeCallbacks.iterator();
                while (it.hasNext()) {
                    ((BrightnessStateChangeCallback) it.next()).onBrightnessLevelChanged();
                }
            }
        }

        public void startObserving() {
            ContentResolver contentResolver = BrightnessController.this.mContext.getContentResolver();
            contentResolver.unregisterContentObserver(this);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_MODE_URI, false, this, -1);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_URI, false, this, -1);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_FLOAT_URI, false, this, -1);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_FOR_VR_FLOAT_URI, false, this, -1);
        }

        public void stopObserving() {
            BrightnessController.this.mContext.getContentResolver().unregisterContentObserver(this);
        }
    }

    public interface BrightnessStateChangeCallback {
        void onBrightnessLevelChanged();
    }

    public void onInit(ToggleSlider toggleSlider) {
    }

    public BrightnessController(Context context, ToggleSlider toggleSlider, BroadcastDispatcher broadcastDispatcher) {
        this.mContext = context;
        this.mControl = toggleSlider;
        toggleSlider.setMax(65535);
        this.mBackgroundHandler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
        this.mUserTracker = new CurrentUserTracker(broadcastDispatcher) {
            public void onUserSwitched(int i) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
        };
        this.mBrightnessObserver = new BrightnessObserver(this.mHandler);
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mMinimumBacklight = powerManager.getBrightnessConstraint(0);
        this.mMaximumBacklight = powerManager.getBrightnessConstraint(1);
        this.mDefaultBacklight = powerManager.getBrightnessConstraint(2);
        this.mMinimumBacklightForVr = powerManager.getBrightnessConstraint(5);
        this.mMaximumBacklightForVr = powerManager.getBrightnessConstraint(6);
        this.mDefaultBacklightForVr = powerManager.getBrightnessConstraint(7);
        this.mAutomaticAvailable = context.getResources().getBoolean(17891368);
        this.mDisplayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mVrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
    }

    public void registerCallbacks() {
        this.mBackgroundHandler.post(this.mStartListeningRunnable);
    }

    public void unregisterCallbacks() {
        this.mBackgroundHandler.post(this.mStopListeningRunnable);
        this.mControlValueInitialized = false;
    }

    public void onChanged(ToggleSlider toggleSlider, boolean z, boolean z2, int i, boolean z3) {
        float f;
        int i2;
        final String str;
        float f2;
        if (!this.mExternalChange) {
            ValueAnimator valueAnimator = this.mSliderAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.mIsVrModeEnabled) {
                i2 = 498;
                f = this.mMinimumBacklightForVr;
                f2 = this.mMaximumBacklightForVr;
                str = "screen_brightness_for_vr_float";
            } else {
                i2 = this.mAutomatic ? 219 : 218;
                f = this.mMinimumBacklight;
                f2 = this.mMaximumBacklight;
                str = "screen_brightness_float";
            }
            final float min = MathUtils.min(BrightnessUtils.convertGammaToLinearFloat(i, f, f2), 1.0f);
            if (z3) {
                Context context = this.mContext;
                MetricsLogger.action(context, i2, BrightnessSynchronizer.brightnessFloatToInt(context, min));
            }
            setBrightness(min);
            if (!z) {
                AsyncTask.execute(new Runnable() {
                    public void run() {
                        System.putFloatForUser(BrightnessController.this.mContext.getContentResolver(), str, min, -2);
                    }
                });
            }
            Iterator it = this.mChangeCallbacks.iterator();
            while (it.hasNext()) {
                ((BrightnessStateChangeCallback) it.next()).onBrightnessLevelChanged();
            }
        }
    }

    public void checkRestrictionAndSetEnabled() {
        this.mBackgroundHandler.post(new Runnable() {
            public void run() {
                ((ToggleSliderView) BrightnessController.this.mControl).setEnforcedAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(BrightnessController.this.mContext, "no_config_brightness", BrightnessController.this.mUserTracker.getCurrentUserId()));
            }
        });
    }

    private void setBrightness(float f) {
        this.mDisplayManager.setTemporaryBrightness(f);
    }

    /* access modifiers changed from: private */
    public void updateVrMode(boolean z) {
        if (this.mIsVrModeEnabled != z) {
            this.mIsVrModeEnabled = z;
            this.mBackgroundHandler.post(this.mUpdateSliderRunnable);
        }
    }

    /* access modifiers changed from: private */
    public void updateSlider(float f, boolean z) {
        float f2;
        float f3;
        if (z) {
            f2 = this.mMinimumBacklightForVr;
            f3 = this.mMaximumBacklightForVr;
        } else {
            f2 = this.mMinimumBacklight;
            f3 = this.mMaximumBacklight;
        }
        if (BrightnessSynchronizer.brightnessFloatToInt(this.mContext, f) != BrightnessSynchronizer.brightnessFloatToInt(this.mContext, BrightnessUtils.convertGammaToLinearFloat(this.mControl.getValue(), f2, f3))) {
            animateSliderTo(BrightnessUtils.convertLinearToGammaFloat(f, f2, f3));
        }
    }

    private void animateSliderTo(int i) {
        if (!this.mControlValueInitialized) {
            this.mControl.setValue(i);
            this.mControlValueInitialized = true;
        }
        ValueAnimator valueAnimator = this.mSliderAnimator;
        if (valueAnimator != null && valueAnimator.isStarted()) {
            this.mSliderAnimator.cancel();
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{this.mControl.getValue(), i});
        this.mSliderAnimator = ofInt;
        ofInt.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BrightnessController.this.lambda$animateSliderTo$0$BrightnessController(valueAnimator);
            }
        });
        this.mSliderAnimator.setDuration((long) ((Math.abs(this.mControl.getValue() - i) * 3000) / 65535));
        this.mSliderAnimator.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateSliderTo$0 */
    public /* synthetic */ void lambda$animateSliderTo$0$BrightnessController(ValueAnimator valueAnimator) {
        this.mExternalChange = true;
        this.mControl.setValue(((Integer) valueAnimator.getAnimatedValue()).intValue());
        this.mExternalChange = false;
    }
}
