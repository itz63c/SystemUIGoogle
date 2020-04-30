package com.android.systemui.statusbar.phone;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.doze.util.BurnInHelperKt;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.IntentButtonProvider;
import com.android.systemui.plugins.IntentButtonProvider.IntentButton;
import com.android.systemui.plugins.IntentButtonProvider.IntentButton.IconState;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityController.AccessibilityStateChangedCallback;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.ExtensionController.Extension;
import com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;
import com.android.systemui.statusbar.policy.PreviewInflater;
import com.android.systemui.tuner.LockscreenFragment.LockButtonFactory;
import com.android.systemui.tuner.TunerService;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyguardBottomAreaView extends FrameLayout implements OnClickListener, Callback, AccessibilityStateChangedCallback {
    public static final Intent INSECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA");
    /* access modifiers changed from: private */
    public static final Intent PHONE_INTENT = new Intent("android.intent.action.DIAL");
    /* access modifiers changed from: private */
    public static final Intent SECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE").addFlags(8388608);
    private AccessibilityController mAccessibilityController;
    private AccessibilityDelegate mAccessibilityDelegate;
    private ActivityIntentHelper mActivityIntentHelper;
    private ActivityStarter mActivityStarter;
    private KeyguardAffordanceHelper mAffordanceHelper;
    private int mBurnInXOffset;
    private int mBurnInYOffset;
    private View mCameraPreview;
    private float mDarkAmount;
    private final BroadcastReceiver mDevicePolicyReceiver;
    private boolean mDozing;
    private ViewGroup mIndicationArea;
    private int mIndicationBottomMargin;
    private TextView mIndicationText;
    /* access modifiers changed from: private */
    public KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public KeyguardAffordanceView mLeftAffordanceView;
    /* access modifiers changed from: private */
    public Drawable mLeftAssistIcon;
    private IntentButton mLeftButton;
    private String mLeftButtonStr;
    private Extension<IntentButton> mLeftExtension;
    /* access modifiers changed from: private */
    public boolean mLeftIsVoiceAssist;
    private View mLeftPreview;
    private ViewGroup mOverlayContainer;
    private ViewGroup mPreviewContainer;
    private PreviewInflater mPreviewInflater;
    private boolean mPrewarmBound;
    private final ServiceConnection mPrewarmConnection;
    /* access modifiers changed from: private */
    public Messenger mPrewarmMessenger;
    /* access modifiers changed from: private */
    public KeyguardAffordanceView mRightAffordanceView;
    private IntentButton mRightButton;
    private String mRightButtonStr;
    private Extension<IntentButton> mRightExtension;
    /* access modifiers changed from: private */
    public final boolean mShowCameraAffordance;
    /* access modifiers changed from: private */
    public final boolean mShowLeftAffordance;
    /* access modifiers changed from: private */
    public StatusBar mStatusBar;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    /* access modifiers changed from: private */
    public boolean mUserSetupComplete;

    private class DefaultLeftButton implements IntentButton {
        private IconState mIconState;

        private DefaultLeftButton() {
            this.mIconState = new IconState();
        }

        public IconState getIcon() {
            KeyguardBottomAreaView keyguardBottomAreaView = KeyguardBottomAreaView.this;
            keyguardBottomAreaView.mLeftIsVoiceAssist = keyguardBottomAreaView.canLaunchVoiceAssist();
            boolean z = true;
            if (KeyguardBottomAreaView.this.mLeftIsVoiceAssist) {
                IconState iconState = this.mIconState;
                if (!KeyguardBottomAreaView.this.mUserSetupComplete || !KeyguardBottomAreaView.this.mShowLeftAffordance) {
                    z = false;
                }
                iconState.isVisible = z;
                if (KeyguardBottomAreaView.this.mLeftAssistIcon == null) {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(C2010R$drawable.ic_mic_26dp);
                } else {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mLeftAssistIcon;
                }
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(C2017R$string.accessibility_voice_assist_button);
            } else {
                IconState iconState2 = this.mIconState;
                if (!KeyguardBottomAreaView.this.mUserSetupComplete || !KeyguardBottomAreaView.this.mShowLeftAffordance || !KeyguardBottomAreaView.this.isPhoneVisible()) {
                    z = false;
                }
                iconState2.isVisible = z;
                this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(17302781);
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(C2017R$string.accessibility_phone_button);
            }
            return this.mIconState;
        }

        public Intent getIntent() {
            return KeyguardBottomAreaView.PHONE_INTENT;
        }
    }

    private class DefaultRightButton implements IntentButton {
        private IconState mIconState;

        private DefaultRightButton() {
            this.mIconState = new IconState();
        }

        public IconState getIcon() {
            boolean z = true;
            boolean z2 = KeyguardBottomAreaView.this.mStatusBar != null && !KeyguardBottomAreaView.this.mStatusBar.isCameraAllowedByAdmin();
            IconState iconState = this.mIconState;
            if (z2 || !KeyguardBottomAreaView.this.mShowCameraAffordance || !KeyguardBottomAreaView.this.mUserSetupComplete || KeyguardBottomAreaView.this.resolveCameraIntent() == null) {
                z = false;
            }
            iconState.isVisible = z;
            this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(C2010R$drawable.ic_camera_alt_24dp);
            this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(C2017R$string.accessibility_camera_button);
            return this.mIconState;
        }

        public Intent getIntent() {
            return (!KeyguardBottomAreaView.this.mKeyguardStateController.isMethodSecure() || KeyguardBottomAreaView.this.mKeyguardStateController.canDismissLockScreen()) ? KeyguardBottomAreaView.INSECURE_CAMERA_INTENT : KeyguardBottomAreaView.SECURE_CAMERA_INTENT;
        }
    }

    /* access modifiers changed from: private */
    public static boolean isSuccessfulLaunch(int i) {
        return i == 0 || i == 3 || i == 2;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public KeyguardBottomAreaView(Context context) {
        this(context, null);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mPrewarmConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                KeyguardBottomAreaView.this.mPrewarmMessenger = new Messenger(iBinder);
            }

            public void onServiceDisconnected(ComponentName componentName) {
                KeyguardBottomAreaView.this.mPrewarmMessenger = null;
            }
        };
        this.mRightButton = new DefaultRightButton();
        this.mLeftButton = new DefaultLeftButton();
        this.mAccessibilityDelegate = new AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                String str = view == KeyguardBottomAreaView.this.mRightAffordanceView ? KeyguardBottomAreaView.this.getResources().getString(C2017R$string.camera_label) : view == KeyguardBottomAreaView.this.mLeftAffordanceView ? KeyguardBottomAreaView.this.mLeftIsVoiceAssist ? KeyguardBottomAreaView.this.getResources().getString(C2017R$string.voice_assist_label) : KeyguardBottomAreaView.this.getResources().getString(C2017R$string.phone_label) : null;
                accessibilityNodeInfo.addAction(new AccessibilityAction(16, str));
            }

            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                if (i == 16) {
                    if (view == KeyguardBottomAreaView.this.mRightAffordanceView) {
                        KeyguardBottomAreaView.this.launchCamera("lockscreen_affordance");
                        return true;
                    } else if (view == KeyguardBottomAreaView.this.mLeftAffordanceView) {
                        KeyguardBottomAreaView.this.launchLeftAffordance();
                        return true;
                    }
                }
                return super.performAccessibilityAction(view, i, bundle);
            }
        };
        this.mDevicePolicyReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                KeyguardBottomAreaView.this.post(new Runnable() {
                    public void run() {
                        KeyguardBottomAreaView.this.updateCameraVisibility();
                    }
                });
            }
        };
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            public void onUserSwitchComplete(int i) {
                KeyguardBottomAreaView.this.updateCameraVisibility();
            }

            public void onUserUnlocked() {
                KeyguardBottomAreaView.this.inflateCameraPreview();
                KeyguardBottomAreaView.this.updateCameraVisibility();
                KeyguardBottomAreaView.this.updateLeftAffordance();
            }
        };
        this.mShowLeftAffordance = getResources().getBoolean(C2007R$bool.config_keyguardShowLeftAffordance);
        this.mShowCameraAffordance = getResources().getBoolean(C2007R$bool.config_keyguardShowCameraAffordance);
    }

    public void initFrom(KeyguardBottomAreaView keyguardBottomAreaView) {
        setStatusBar(keyguardBottomAreaView.mStatusBar);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPreviewInflater = new PreviewInflater(this.mContext, new LockPatternUtils(this.mContext), new ActivityIntentHelper(this.mContext));
        this.mPreviewContainer = (ViewGroup) findViewById(C2011R$id.preview_container);
        this.mOverlayContainer = (ViewGroup) findViewById(C2011R$id.overlay_container);
        this.mRightAffordanceView = (KeyguardAffordanceView) findViewById(C2011R$id.camera_button);
        this.mLeftAffordanceView = (KeyguardAffordanceView) findViewById(C2011R$id.left_button);
        this.mIndicationArea = (ViewGroup) findViewById(C2011R$id.keyguard_indication_area);
        this.mIndicationText = (TextView) findViewById(C2011R$id.keyguard_indication_text);
        this.mIndicationBottomMargin = getResources().getDimensionPixelSize(C2009R$dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = getResources().getDimensionPixelSize(C2009R$dimen.default_burn_in_prevention_offset);
        updateCameraVisibility();
        KeyguardStateController keyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        this.mKeyguardStateController = keyguardStateController;
        keyguardStateController.addCallback(this);
        setClipChildren(false);
        setClipToPadding(false);
        inflateCameraPreview();
        this.mRightAffordanceView.setOnClickListener(this);
        this.mLeftAffordanceView.setOnClickListener(this);
        initAccessibility();
        this.mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        FlashlightController flashlightController = (FlashlightController) Dependency.get(FlashlightController.class);
        this.mAccessibilityController = (AccessibilityController) Dependency.get(AccessibilityController.class);
        this.mActivityIntentHelper = new ActivityIntentHelper(getContext());
        updateLeftAffordance();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        Class<IntentButtonProvider> cls = IntentButtonProvider.class;
        Class<IntentButton> cls2 = IntentButton.class;
        Class<ExtensionController> cls3 = ExtensionController.class;
        super.onAttachedToWindow();
        this.mAccessibilityController.addStateChangedCallback(this);
        ExtensionBuilder newExtension = ((ExtensionController) Dependency.get(cls3)).newExtension(cls2);
        newExtension.withPlugin(cls, "com.android.systemui.action.PLUGIN_LOCKSCREEN_RIGHT_BUTTON", $$Lambda$KeyguardBottomAreaView$g4KaNPI9kzVsHrOlMYmA_f9J2Y.INSTANCE);
        newExtension.withTunerFactory(new LockButtonFactory(this.mContext, "sysui_keyguard_right"));
        newExtension.withDefault(new Supplier() {
            public final Object get() {
                return KeyguardBottomAreaView.this.lambda$onAttachedToWindow$1$KeyguardBottomAreaView();
            }
        });
        newExtension.withCallback(new Consumer() {
            public final void accept(Object obj) {
                KeyguardBottomAreaView.this.lambda$onAttachedToWindow$2$KeyguardBottomAreaView((IntentButton) obj);
            }
        });
        this.mRightExtension = newExtension.build();
        ExtensionBuilder newExtension2 = ((ExtensionController) Dependency.get(cls3)).newExtension(cls2);
        newExtension2.withPlugin(cls, "com.android.systemui.action.PLUGIN_LOCKSCREEN_LEFT_BUTTON", $$Lambda$KeyguardBottomAreaView$Eh9_ou4HbbT4H4ZFilpDDtanY4k.INSTANCE);
        newExtension2.withTunerFactory(new LockButtonFactory(this.mContext, "sysui_keyguard_left"));
        newExtension2.withDefault(new Supplier() {
            public final Object get() {
                return KeyguardBottomAreaView.this.lambda$onAttachedToWindow$4$KeyguardBottomAreaView();
            }
        });
        newExtension2.withCallback(new Consumer() {
            public final void accept(Object obj) {
                KeyguardBottomAreaView.this.lambda$onAttachedToWindow$5$KeyguardBottomAreaView((IntentButton) obj);
            }
        });
        this.mLeftExtension = newExtension2.build();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        getContext().registerReceiverAsUser(this.mDevicePolicyReceiver, UserHandle.ALL, intentFilter, null, null);
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mUpdateMonitorCallback);
        this.mKeyguardStateController.addCallback(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onAttachedToWindow$1 */
    public /* synthetic */ IntentButton lambda$onAttachedToWindow$1$KeyguardBottomAreaView() {
        return new DefaultRightButton();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onAttachedToWindow$4 */
    public /* synthetic */ IntentButton lambda$onAttachedToWindow$4$KeyguardBottomAreaView() {
        return new DefaultLeftButton();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardStateController.removeCallback(this);
        this.mAccessibilityController.removeStateChangedCallback(this);
        this.mRightExtension.destroy();
        this.mLeftExtension.destroy();
        getContext().unregisterReceiver(this.mDevicePolicyReceiver);
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).removeCallback(this.mUpdateMonitorCallback);
    }

    private void initAccessibility() {
        this.mLeftAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
        this.mRightAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mIndicationBottomMargin = getResources().getDimensionPixelSize(C2009R$dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = getResources().getDimensionPixelSize(C2009R$dimen.default_burn_in_prevention_offset);
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.mIndicationArea.getLayoutParams();
        int i = marginLayoutParams.bottomMargin;
        int i2 = this.mIndicationBottomMargin;
        if (i != i2) {
            marginLayoutParams.bottomMargin = i2;
            this.mIndicationArea.setLayoutParams(marginLayoutParams);
        }
        this.mIndicationText.setTextSize(0, (float) getResources().getDimensionPixelSize(17105490));
        LayoutParams layoutParams = this.mRightAffordanceView.getLayoutParams();
        layoutParams.width = getResources().getDimensionPixelSize(C2009R$dimen.keyguard_affordance_width);
        layoutParams.height = getResources().getDimensionPixelSize(C2009R$dimen.keyguard_affordance_height);
        this.mRightAffordanceView.setLayoutParams(layoutParams);
        updateRightAffordanceIcon();
        LayoutParams layoutParams2 = this.mLeftAffordanceView.getLayoutParams();
        layoutParams2.width = getResources().getDimensionPixelSize(C2009R$dimen.keyguard_affordance_width);
        layoutParams2.height = getResources().getDimensionPixelSize(C2009R$dimen.keyguard_affordance_height);
        this.mLeftAffordanceView.setLayoutParams(layoutParams2);
        updateLeftAffordanceIcon();
    }

    private void updateRightAffordanceIcon() {
        IconState icon = this.mRightButton.getIcon();
        this.mRightAffordanceView.setVisibility((this.mDozing || !icon.isVisible) ? 8 : 0);
        if (!(icon.drawable == this.mRightAffordanceView.getDrawable() && icon.tint == this.mRightAffordanceView.shouldTint())) {
            this.mRightAffordanceView.setImageDrawable(icon.drawable, icon.tint);
        }
        this.mRightAffordanceView.setContentDescription(icon.contentDescription);
    }

    public void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
        updateCameraVisibility();
    }

    public void setAffordanceHelper(KeyguardAffordanceHelper keyguardAffordanceHelper) {
        this.mAffordanceHelper = keyguardAffordanceHelper;
    }

    public void setUserSetupComplete(boolean z) {
        this.mUserSetupComplete = z;
        updateCameraVisibility();
        updateLeftAffordanceIcon();
    }

    private Intent getCameraIntent() {
        return this.mRightButton.getIntent();
    }

    public ResolveInfo resolveCameraIntent() {
        return this.mContext.getPackageManager().resolveActivityAsUser(getCameraIntent(), 65536, KeyguardUpdateMonitor.getCurrentUser());
    }

    /* access modifiers changed from: private */
    public void updateCameraVisibility() {
        KeyguardAffordanceView keyguardAffordanceView = this.mRightAffordanceView;
        if (keyguardAffordanceView != null) {
            keyguardAffordanceView.setVisibility((this.mDozing || !this.mShowCameraAffordance || !this.mRightButton.getIcon().isVisible) ? 8 : 0);
        }
    }

    private void updateLeftAffordanceIcon() {
        int i = 8;
        if (!this.mShowLeftAffordance || this.mDozing) {
            this.mLeftAffordanceView.setVisibility(8);
            return;
        }
        IconState icon = this.mLeftButton.getIcon();
        KeyguardAffordanceView keyguardAffordanceView = this.mLeftAffordanceView;
        if (icon.isVisible) {
            i = 0;
        }
        keyguardAffordanceView.setVisibility(i);
        if (!(icon.drawable == this.mLeftAffordanceView.getDrawable() && icon.tint == this.mLeftAffordanceView.shouldTint())) {
            this.mLeftAffordanceView.setImageDrawable(icon.drawable, icon.tint);
        }
        this.mLeftAffordanceView.setContentDescription(icon.contentDescription);
    }

    public boolean isLeftVoiceAssist() {
        return this.mLeftIsVoiceAssist;
    }

    /* access modifiers changed from: private */
    public boolean isPhoneVisible() {
        PackageManager packageManager = this.mContext.getPackageManager();
        if (!packageManager.hasSystemFeature("android.hardware.telephony") || packageManager.resolveActivity(PHONE_INTENT, 0) == null) {
            return false;
        }
        return true;
    }

    public void onStateChanged(boolean z, boolean z2) {
        this.mRightAffordanceView.setClickable(z2);
        this.mLeftAffordanceView.setClickable(z2);
        this.mRightAffordanceView.setFocusable(z);
        this.mLeftAffordanceView.setFocusable(z);
    }

    public void onClick(View view) {
        if (view == this.mRightAffordanceView) {
            launchCamera("lockscreen_affordance");
        } else if (view == this.mLeftAffordanceView) {
            launchLeftAffordance();
        }
    }

    public void bindCameraPrewarmService() {
        ActivityInfo targetActivityInfo = this.mActivityIntentHelper.getTargetActivityInfo(getCameraIntent(), KeyguardUpdateMonitor.getCurrentUser(), true);
        if (targetActivityInfo != null) {
            Bundle bundle = targetActivityInfo.metaData;
            if (bundle != null) {
                String string = bundle.getString("android.media.still_image_camera_preview_service");
                if (string != null) {
                    Intent intent = new Intent();
                    intent.setClassName(targetActivityInfo.packageName, string);
                    intent.setAction("android.service.media.CameraPrewarmService.ACTION_PREWARM");
                    try {
                        if (getContext().bindServiceAsUser(intent, this.mPrewarmConnection, 67108865, new UserHandle(-2))) {
                            this.mPrewarmBound = true;
                        }
                    } catch (SecurityException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unable to bind to prewarm service package=");
                        sb.append(targetActivityInfo.packageName);
                        sb.append(" class=");
                        sb.append(string);
                        Log.w("StatusBar/KeyguardBottomAreaView", sb.toString(), e);
                    }
                }
            }
        }
    }

    public void unbindCameraPrewarmService(boolean z) {
        if (this.mPrewarmBound) {
            Messenger messenger = this.mPrewarmMessenger;
            if (messenger != null && z) {
                try {
                    messenger.send(Message.obtain(null, 1));
                } catch (RemoteException e) {
                    Log.w("StatusBar/KeyguardBottomAreaView", "Error sending camera fired message", e);
                }
            }
            this.mContext.unbindService(this.mPrewarmConnection);
            this.mPrewarmBound = false;
        }
    }

    public void launchCamera(String str) {
        final Intent cameraIntent = getCameraIntent();
        cameraIntent.putExtra("com.android.systemui.camera_launch_source", str);
        boolean wouldLaunchResolverActivity = this.mActivityIntentHelper.wouldLaunchResolverActivity(cameraIntent, KeyguardUpdateMonitor.getCurrentUser());
        if (cameraIntent != SECURE_CAMERA_INTENT || wouldLaunchResolverActivity) {
            this.mActivityStarter.startActivity(cameraIntent, false, (ActivityStarter.Callback) new ActivityStarter.Callback() {
                public void onActivityStarted(int i) {
                    KeyguardBottomAreaView.this.unbindCameraPrewarmService(KeyguardBottomAreaView.isSuccessfulLaunch(i));
                }
            });
        } else {
            AsyncTask.execute(new Runnable() {
                public void run() {
                    int i;
                    ActivityOptions makeBasic = ActivityOptions.makeBasic();
                    makeBasic.setDisallowEnterPictureInPictureWhileLaunching(true);
                    makeBasic.setRotationAnimationHint(3);
                    try {
                        i = ActivityTaskManager.getService().startActivityAsUser(null, KeyguardBottomAreaView.this.getContext().getBasePackageName(), KeyguardBottomAreaView.this.getContext().getAttributionTag(), cameraIntent, cameraIntent.resolveTypeIfNeeded(KeyguardBottomAreaView.this.getContext().getContentResolver()), null, null, 0, 268435456, null, makeBasic.toBundle(), UserHandle.CURRENT.getIdentifier());
                    } catch (RemoteException e) {
                        Log.w("StatusBar/KeyguardBottomAreaView", "Unable to start camera activity", e);
                        i = -96;
                    }
                    final boolean access$600 = KeyguardBottomAreaView.isSuccessfulLaunch(i);
                    KeyguardBottomAreaView.this.post(new Runnable() {
                        public void run() {
                            KeyguardBottomAreaView.this.unbindCameraPrewarmService(access$600);
                        }
                    });
                }
            });
        }
    }

    public void setDarkAmount(float f) {
        if (f != this.mDarkAmount) {
            this.mDarkAmount = f;
            dozeTimeTick();
        }
    }

    public void launchLeftAffordance() {
        if (this.mLeftIsVoiceAssist) {
            launchVoiceAssist();
        } else {
            launchPhone();
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void launchVoiceAssist() {
        C14715 r1 = new Runnable(this) {
            public void run() {
                ((AssistManager) Dependency.get(AssistManager.class)).launchVoiceAssistFromKeyguard();
            }
        };
        if (!this.mKeyguardStateController.canDismissLockScreen()) {
            AsyncTask.execute(r1);
        } else {
            this.mStatusBar.executeRunnableDismissingKeyguard(r1, null, !TextUtils.isEmpty(this.mRightButtonStr) && ((TunerService) Dependency.get(TunerService.class)).getValue("sysui_keyguard_right_unlock", 1) != 0, false, true);
        }
    }

    /* access modifiers changed from: private */
    public boolean canLaunchVoiceAssist() {
        return ((AssistManager) Dependency.get(AssistManager.class)).canVoiceAssistBeLaunchedFromKeyguard();
    }

    private void launchPhone() {
        final TelecomManager from = TelecomManager.from(this.mContext);
        if (from.isInCall()) {
            AsyncTask.execute(new Runnable(this) {
                public void run() {
                    from.showInCallScreen(false);
                }
            });
            return;
        }
        boolean z = true;
        if (TextUtils.isEmpty(this.mLeftButtonStr) || ((TunerService) Dependency.get(TunerService.class)).getValue("sysui_keyguard_left_unlock", 1) == 0) {
            z = false;
        }
        this.mActivityStarter.startActivity(this.mLeftButton.getIntent(), z);
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (view == this && i == 0) {
            updateCameraVisibility();
        }
    }

    public KeyguardAffordanceView getLeftView() {
        return this.mLeftAffordanceView;
    }

    public KeyguardAffordanceView getRightView() {
        return this.mRightAffordanceView;
    }

    public View getLeftPreview() {
        return this.mLeftPreview;
    }

    public View getRightPreview() {
        return this.mCameraPreview;
    }

    public View getIndicationArea() {
        return this.mIndicationArea;
    }

    public void onUnlockedChanged() {
        updateCameraVisibility();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0021  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void inflateCameraPreview() {
        /*
            r4 = this;
            android.view.View r0 = r4.mCameraPreview
            r1 = 0
            if (r0 == 0) goto L_0x0012
            android.view.ViewGroup r2 = r4.mPreviewContainer
            r2.removeView(r0)
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0012
            r0 = 1
            goto L_0x0013
        L_0x0012:
            r0 = r1
        L_0x0013:
            com.android.systemui.statusbar.policy.PreviewInflater r2 = r4.mPreviewInflater
            android.content.Intent r3 = r4.getCameraIntent()
            android.view.View r2 = r2.inflatePreview(r3)
            r4.mCameraPreview = r2
            if (r2 == 0) goto L_0x002f
            android.view.ViewGroup r3 = r4.mPreviewContainer
            r3.addView(r2)
            android.view.View r2 = r4.mCameraPreview
            if (r0 == 0) goto L_0x002b
            goto L_0x002c
        L_0x002b:
            r1 = 4
        L_0x002c:
            r2.setVisibility(r1)
        L_0x002f:
            com.android.systemui.statusbar.phone.KeyguardAffordanceHelper r4 = r4.mAffordanceHelper
            if (r4 == 0) goto L_0x0036
            r4.updatePreviews()
        L_0x0036:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.inflateCameraPreview():void");
    }

    private void updateLeftPreview() {
        Class<AssistManager> cls = AssistManager.class;
        View view = this.mLeftPreview;
        if (view != null) {
            this.mPreviewContainer.removeView(view);
        }
        if (!this.mLeftIsVoiceAssist) {
            this.mLeftPreview = this.mPreviewInflater.inflatePreview(this.mLeftButton.getIntent());
        } else if (((AssistManager) Dependency.get(cls)).getVoiceInteractorComponentName() != null) {
            this.mLeftPreview = this.mPreviewInflater.inflatePreviewFromService(((AssistManager) Dependency.get(cls)).getVoiceInteractorComponentName());
        }
        View view2 = this.mLeftPreview;
        if (view2 != null) {
            this.mPreviewContainer.addView(view2);
            this.mLeftPreview.setVisibility(4);
        }
        KeyguardAffordanceHelper keyguardAffordanceHelper = this.mAffordanceHelper;
        if (keyguardAffordanceHelper != null) {
            keyguardAffordanceHelper.updatePreviews();
        }
    }

    public void startFinishDozeAnimation() {
        long j = 0;
        if (this.mLeftAffordanceView.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mLeftAffordanceView, 0);
            j = 48;
        }
        if (this.mRightAffordanceView.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mRightAffordanceView, j);
        }
    }

    private void startFinishDozeAnimationElement(View view, long j) {
        view.setAlpha(0.0f);
        view.setTranslationY((float) (view.getHeight() / 2));
        view.animate().alpha(1.0f).translationY(0.0f).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(j).setDuration(250);
    }

    public void updateLeftAffordance() {
        updateLeftAffordanceIcon();
        updateLeftPreview();
    }

    /* access modifiers changed from: private */
    /* renamed from: setRightButton */
    public void lambda$onAttachedToWindow$2(IntentButton intentButton) {
        this.mRightButton = intentButton;
        updateRightAffordanceIcon();
        updateCameraVisibility();
        inflateCameraPreview();
    }

    /* access modifiers changed from: private */
    /* renamed from: setLeftButton */
    public void lambda$onAttachedToWindow$5(IntentButton intentButton) {
        this.mLeftButton = intentButton;
        if (!(intentButton instanceof DefaultLeftButton)) {
            this.mLeftIsVoiceAssist = false;
        }
        updateLeftAffordance();
    }

    public void setDozing(boolean z, boolean z2) {
        this.mDozing = z;
        updateCameraVisibility();
        updateLeftAffordanceIcon();
        if (z) {
            this.mOverlayContainer.setVisibility(4);
            return;
        }
        this.mOverlayContainer.setVisibility(0);
        if (z2) {
            startFinishDozeAnimation();
        }
    }

    public void dozeTimeTick() {
        this.mIndicationArea.setTranslationY(((float) (BurnInHelperKt.getBurnInOffset(this.mBurnInYOffset * 2, false) - this.mBurnInYOffset)) * this.mDarkAmount);
    }

    public void setAntiBurnInOffsetX(int i) {
        if (this.mBurnInXOffset != i) {
            this.mBurnInXOffset = i;
            this.mIndicationArea.setTranslationX((float) i);
        }
    }

    public void setAffordanceAlpha(float f) {
        this.mLeftAffordanceView.setAlpha(f);
        this.mRightAffordanceView.setAlpha(f);
        this.mIndicationArea.setAlpha(f);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        int safeInsetBottom = windowInsets.getDisplayCutout() != null ? windowInsets.getDisplayCutout().getSafeInsetBottom() : 0;
        if (isPaddingRelative()) {
            setPaddingRelative(getPaddingStart(), getPaddingTop(), getPaddingEnd(), safeInsetBottom);
        } else {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), safeInsetBottom);
        }
        return windowInsets;
    }
}
