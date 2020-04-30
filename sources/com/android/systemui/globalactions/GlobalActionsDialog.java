package com.android.systemui.globalactions;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.service.dreams.IDreamManager;
import android.sysprop.TelephonyProperties;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.ColorExtractor.GradientColors;
import com.android.internal.colorextraction.ColorExtractor.OnColorsChangedListener;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.util.ScreenRecordHelper;
import com.android.internal.util.ScreenshotHelper;
import com.android.internal.view.RotationPolicy;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.C2018R$style;
import com.android.systemui.Interpolators;
import com.android.systemui.MultiListLayout;
import com.android.systemui.MultiListLayout.MultiListAdapter;
import com.android.systemui.MultiListLayout.RotationListener;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import com.android.systemui.controls.p004ui.ControlsUiController;
import com.android.systemui.globalactions.GlobalActionsDialog.MyAdapter;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.GlobalActions.GlobalActionsManager;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.plugins.GlobalActionsPanelPlugin.Callbacks;
import com.android.systemui.plugins.GlobalActionsPanelPlugin.PanelViewController;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;
import com.android.systemui.util.leak.RotationUtils;
import com.android.systemui.volume.SystemUIInterpolators$LogAccelerateInterpolator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class GlobalActionsDialog implements OnDismissListener, OnShowListener, ConfigurationListener, Callbacks {
    private final ActivityStarter mActivityStarter;
    /* access modifiers changed from: private */
    public MyAdapter mAdapter;
    private ContentObserver mAirplaneModeObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z) {
            GlobalActionsDialog.this.onAirplaneModeChanged();
        }
    };
    /* access modifiers changed from: private */
    public ToggleAction mAirplaneModeOn;
    /* access modifiers changed from: private */
    public State mAirplaneState = State.Off;
    private boolean mAnyControlsProviders = false;
    /* access modifiers changed from: private */
    public final AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public final Executor mBackgroundExecutor;
    private final BlurUtils mBlurUtils;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) || "android.intent.action.SCREEN_OFF".equals(action)) {
                String stringExtra = intent.getStringExtra("reason");
                if (!"globalactions".equals(stringExtra)) {
                    GlobalActionsDialog.this.mHandler.sendMessage(GlobalActionsDialog.this.mHandler.obtainMessage(0, stringExtra));
                }
            } else if ("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED".equals(action) && !intent.getBooleanExtra("android.telephony.extra.PHONE_IN_ECM_STATE", false) && GlobalActionsDialog.this.mIsWaitingForEcmExit) {
                GlobalActionsDialog.this.mIsWaitingForEcmExit = false;
                GlobalActionsDialog.this.changeAirplaneModeSystemSetting(true);
            }
        }
    };
    private final ConfigurationController mConfigurationController;
    private final ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final ControlsListingController mControlsListingController;
    private ControlsUiController mControlsUiController;
    private final NotificationShadeDepthController mDepthController;
    private final DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public boolean mDeviceProvisioned = false;
    /* access modifiers changed from: private */
    public ActionsDialog mDialog;
    private final IDreamManager mDreamManager;
    /* access modifiers changed from: private */
    public final EmergencyAffordanceManager mEmergencyAffordanceManager;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 0) {
                if (i == 1) {
                    GlobalActionsDialog.this.refreshSilentMode();
                    GlobalActionsDialog.this.mAdapter.notifyDataSetChanged();
                } else if (i == 2) {
                    GlobalActionsDialog.this.handleShow();
                }
            } else if (GlobalActionsDialog.this.mDialog != null) {
                if ("dream".equals(message.obj)) {
                    GlobalActionsDialog.this.mDialog.dismissImmediately();
                } else {
                    GlobalActionsDialog.this.mDialog.dismiss();
                }
                GlobalActionsDialog.this.mDialog = null;
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mHasTelephony;
    private boolean mHasVibrator;
    /* access modifiers changed from: private */
    public final IActivityManager mIActivityManager;
    /* access modifiers changed from: private */
    public final IWindowManager mIWindowManager;
    /* access modifiers changed from: private */
    public boolean mIsWaitingForEcmExit = false;
    /* access modifiers changed from: private */
    public ArrayList<Action> mItems;
    /* access modifiers changed from: private */
    public boolean mKeyguardShowing = false;
    private final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public final LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private GlobalActionsPanelPlugin mPanelPlugin;
    PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onServiceStateChanged(ServiceState serviceState) {
            if (GlobalActionsDialog.this.mHasTelephony) {
                GlobalActionsDialog.this.mAirplaneState = serviceState.getState() == 3 ? State.On : State.Off;
                GlobalActionsDialog.this.mAirplaneModeOn.updateState(GlobalActionsDialog.this.mAirplaneState);
                GlobalActionsDialog.this.mAdapter.notifyDataSetChanged();
            }
        }
    };
    private final Resources mResources;
    private BroadcastReceiver mRingerModeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {
                GlobalActionsDialog.this.mHandler.sendEmptyMessage(1);
            }
        }
    };
    /* access modifiers changed from: private */
    public final ScreenRecordHelper mScreenRecordHelper;
    /* access modifiers changed from: private */
    public final ScreenshotHelper mScreenshotHelper;
    private final boolean mShowSilentToggle;
    private Action mSilentModeAction;
    private final IStatusBarService mStatusBarService;
    private final SysuiColorExtractor mSysuiColorExtractor;
    /* access modifiers changed from: private */
    public final TelecomManager mTelecomManager;
    private final TrustManager mTrustManager;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;
    /* access modifiers changed from: private */
    public final GlobalActionsManager mWindowManagerFuncs;

    public interface Action {
        View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater);

        boolean isEnabled();

        void onPress();

        boolean shouldBeSeparated() {
            return false;
        }

        boolean showBeforeProvisioning();

        boolean showDuringKeyguard();
    }

    private static final class ActionsDialog extends Dialog implements DialogInterface, OnColorsChangedListener {
        private final MyAdapter mAdapter;
        private Drawable mBackgroundDrawable;
        private final BlurUtils mBlurUtils;
        private final SysuiColorExtractor mColorExtractor;
        /* access modifiers changed from: private */
        public final Context mContext;
        private ControlsUiController mControlsUiController;
        private ViewGroup mControlsView;
        private final NotificationShadeDepthController mDepthController;
        private MultiListLayout mGlobalActionsLayout;
        private boolean mHadTopUi;
        private boolean mKeyguardShowing;
        private final NotificationShadeWindowController mNotificationShadeWindowController;
        /* access modifiers changed from: private */
        public final PanelViewController mPanelController;
        private ResetOrientationData mResetOrientationData;
        private float mScrimAlpha;
        private boolean mShowing;
        private final IStatusBarService mStatusBarService;
        private final IBinder mToken = new Binder();

        private static class ResetOrientationData {
            public boolean locked;
            public int rotation;

            private ResetOrientationData() {
            }
        }

        ActionsDialog(Context context, MyAdapter myAdapter, PanelViewController panelViewController, NotificationShadeDepthController notificationShadeDepthController, SysuiColorExtractor sysuiColorExtractor, IStatusBarService iStatusBarService, NotificationShadeWindowController notificationShadeWindowController, ControlsUiController controlsUiController, BlurUtils blurUtils) {
            super(context, C2018R$style.Theme_SystemUI_Dialog_GlobalActions);
            this.mContext = context;
            this.mAdapter = myAdapter;
            this.mDepthController = notificationShadeDepthController;
            this.mColorExtractor = sysuiColorExtractor;
            this.mStatusBarService = iStatusBarService;
            this.mNotificationShadeWindowController = notificationShadeWindowController;
            this.mControlsUiController = controlsUiController;
            this.mBlurUtils = blurUtils;
            Window window = getWindow();
            window.requestFeature(1);
            window.getDecorView();
            LayoutParams attributes = window.getAttributes();
            attributes.systemUiVisibility |= 1792;
            window.setLayout(-1, -1);
            window.clearFlags(2);
            window.addFlags(17629472);
            window.setType(2020);
            window.getAttributes().setFitInsetsTypes(0);
            setTitle(17040149);
            this.mPanelController = panelViewController;
            initializeLayout();
        }

        private boolean shouldUsePanel() {
            PanelViewController panelViewController = this.mPanelController;
            return (panelViewController == null || panelViewController.getPanelContent() == null) ? false : true;
        }

        private void initializePanel() {
            int rotation = RotationUtils.getRotation(this.mContext);
            boolean isRotationLocked = RotationPolicy.isRotationLocked(this.mContext);
            if (rotation == 0) {
                if (!isRotationLocked) {
                    if (this.mResetOrientationData == null) {
                        ResetOrientationData resetOrientationData = new ResetOrientationData();
                        this.mResetOrientationData = resetOrientationData;
                        resetOrientationData.locked = false;
                    }
                    this.mGlobalActionsLayout.post(new Runnable() {
                        public final void run() {
                            ActionsDialog.this.lambda$initializePanel$1$GlobalActionsDialog$ActionsDialog();
                        }
                    });
                }
                setRotationSuggestionsEnabled(false);
                ((FrameLayout) findViewById(C2011R$id.global_actions_panel_container)).addView(this.mPanelController.getPanelContent(), new FrameLayout.LayoutParams(-1, -1));
            } else if (isRotationLocked) {
                if (this.mResetOrientationData == null) {
                    ResetOrientationData resetOrientationData2 = new ResetOrientationData();
                    this.mResetOrientationData = resetOrientationData2;
                    resetOrientationData2.locked = true;
                    resetOrientationData2.rotation = rotation;
                }
                this.mGlobalActionsLayout.post(new Runnable() {
                    public final void run() {
                        ActionsDialog.this.lambda$initializePanel$0$GlobalActionsDialog$ActionsDialog();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initializePanel$0 */
        public /* synthetic */ void lambda$initializePanel$0$GlobalActionsDialog$ActionsDialog() {
            RotationPolicy.setRotationLockAtAngle(this.mContext, false, 0);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initializePanel$1 */
        public /* synthetic */ void lambda$initializePanel$1$GlobalActionsDialog$ActionsDialog() {
            RotationPolicy.setRotationLockAtAngle(this.mContext, true, 0);
        }

        private void initializeLayout() {
            setContentView(getGlobalActionsLayoutId(this.mContext));
            fixNavBarClipping();
            this.mControlsView = (ViewGroup) findViewById(C2011R$id.global_actions_controls);
            MultiListLayout multiListLayout = (MultiListLayout) findViewById(C2011R$id.global_actions_view);
            this.mGlobalActionsLayout = multiListLayout;
            multiListLayout.setOutsideTouchListener(new OnClickListener() {
                public final void onClick(View view) {
                    ActionsDialog.this.lambda$initializeLayout$2$GlobalActionsDialog$ActionsDialog(view);
                }
            });
            this.mGlobalActionsLayout.setListViewAccessibilityDelegate(new AccessibilityDelegate() {
                public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                    accessibilityEvent.getText().add(ActionsDialog.this.mContext.getString(17040149));
                    return true;
                }
            });
            this.mGlobalActionsLayout.setRotationListener(new RotationListener() {
                public final void onRotate(int i, int i2) {
                    ActionsDialog.this.onRotate(i, i2);
                }
            });
            this.mGlobalActionsLayout.setAdapter(this.mAdapter);
            ((View) this.mGlobalActionsLayout.getParent()).setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    ActionsDialog.this.lambda$initializeLayout$3$GlobalActionsDialog$ActionsDialog(view);
                }
            });
            View findViewById = findViewById(C2011R$id.global_actions_grid_root);
            if (findViewById != null) {
                findViewById.setOnClickListener(new OnClickListener() {
                    public final void onClick(View view) {
                        ActionsDialog.this.lambda$initializeLayout$4$GlobalActionsDialog$ActionsDialog(view);
                    }
                });
            }
            if (shouldUsePanel()) {
                initializePanel();
            }
            if (this.mBackgroundDrawable == null) {
                this.mBackgroundDrawable = new ScrimDrawable();
                this.mScrimAlpha = this.mBlurUtils.supportsBlursOnWindows() ? 0.54f : 0.75f;
            }
            getWindow().setBackgroundDrawable(this.mBackgroundDrawable);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initializeLayout$2 */
        public /* synthetic */ void lambda$initializeLayout$2$GlobalActionsDialog$ActionsDialog(View view) {
            dismiss();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initializeLayout$3 */
        public /* synthetic */ void lambda$initializeLayout$3$GlobalActionsDialog$ActionsDialog(View view) {
            dismiss();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$initializeLayout$4 */
        public /* synthetic */ void lambda$initializeLayout$4$GlobalActionsDialog$ActionsDialog(View view) {
            dismiss();
        }

        private void fixNavBarClipping() {
            ViewGroup viewGroup = (ViewGroup) findViewById(16908290);
            viewGroup.setClipChildren(false);
            viewGroup.setClipToPadding(false);
            ViewGroup viewGroup2 = (ViewGroup) viewGroup.getParent();
            viewGroup2.setClipChildren(false);
            viewGroup2.setClipToPadding(false);
        }

        private int getGlobalActionsLayoutId(Context context) {
            if (this.mControlsUiController != null) {
                return C2013R$layout.global_actions_grid_v2;
            }
            int rotation = RotationUtils.getRotation(context);
            boolean z = GlobalActionsDialog.isForceGridEnabled(context) || (shouldUsePanel() && rotation == 0);
            if (rotation == 2) {
                if (z) {
                    return C2013R$layout.global_actions_grid_seascape;
                }
                return C2013R$layout.global_actions_column_seascape;
            } else if (z) {
                return C2013R$layout.global_actions_grid;
            } else {
                return C2013R$layout.global_actions_column;
            }
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            super.setCanceledOnTouchOutside(true);
            super.onStart();
            this.mGlobalActionsLayout.updateList();
            if (this.mBackgroundDrawable instanceof ScrimDrawable) {
                this.mColorExtractor.addOnColorsChangedListener(this);
                updateColors(this.mColorExtractor.getNeutralColors(), false);
            }
        }

        private void updateColors(GradientColors gradientColors, boolean z) {
            ScrimDrawable scrimDrawable = this.mBackgroundDrawable;
            if (scrimDrawable instanceof ScrimDrawable) {
                scrimDrawable.setColor(gradientColors.supportsDarkText() ? -1 : -16777216, z);
                View decorView = getWindow().getDecorView();
                if (gradientColors.supportsDarkText()) {
                    decorView.setSystemUiVisibility(8208);
                } else {
                    decorView.setSystemUiVisibility(0);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            super.onStop();
            this.mColorExtractor.removeOnColorsChangedListener(this);
        }

        public void show() {
            super.show();
            this.mShowing = true;
            this.mHadTopUi = this.mNotificationShadeWindowController.getForceHasTopUi();
            this.mNotificationShadeWindowController.setForceHasTopUi(true);
            this.mBackgroundDrawable.setAlpha(0);
            MultiListLayout multiListLayout = this.mGlobalActionsLayout;
            multiListLayout.setTranslationX(multiListLayout.getAnimationOffsetX());
            MultiListLayout multiListLayout2 = this.mGlobalActionsLayout;
            multiListLayout2.setTranslationY(multiListLayout2.getAnimationOffsetY());
            this.mGlobalActionsLayout.setAlpha(0.0f);
            this.mGlobalActionsLayout.animate().alpha(1.0f).translationX(0.0f).translationY(0.0f).setDuration(300).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ActionsDialog.this.lambda$show$5$GlobalActionsDialog$ActionsDialog(valueAnimator);
                }
            }).start();
            ControlsUiController controlsUiController = this.mControlsUiController;
            if (controlsUiController != null) {
                controlsUiController.show(this.mControlsView);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$show$5 */
        public /* synthetic */ void lambda$show$5$GlobalActionsDialog$ActionsDialog(ValueAnimator valueAnimator) {
            float animatedFraction = valueAnimator.getAnimatedFraction();
            this.mBackgroundDrawable.setAlpha((int) (this.mScrimAlpha * animatedFraction * 255.0f));
            this.mDepthController.updateGlobalDialogVisibility(animatedFraction, this.mGlobalActionsLayout);
        }

        public void dismiss() {
            if (this.mShowing) {
                this.mShowing = false;
                ControlsUiController controlsUiController = this.mControlsUiController;
                if (controlsUiController != null) {
                    controlsUiController.hide();
                }
                this.mGlobalActionsLayout.setTranslationX(0.0f);
                this.mGlobalActionsLayout.setTranslationY(0.0f);
                this.mGlobalActionsLayout.setAlpha(1.0f);
                this.mGlobalActionsLayout.animate().alpha(0.0f).translationX(this.mGlobalActionsLayout.getAnimationOffsetX()).translationY(this.mGlobalActionsLayout.getAnimationOffsetY()).setDuration(300).withEndAction(new Runnable() {
                    public final void run() {
                        ActionsDialog.this.completeDismiss();
                    }
                }).setInterpolator(new SystemUIInterpolators$LogAccelerateInterpolator()).setUpdateListener(new AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ActionsDialog.this.lambda$dismiss$6$GlobalActionsDialog$ActionsDialog(valueAnimator);
                    }
                }).start();
                dismissPanel();
                resetOrientation();
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$dismiss$6 */
        public /* synthetic */ void lambda$dismiss$6$GlobalActionsDialog$ActionsDialog(ValueAnimator valueAnimator) {
            float animatedFraction = 1.0f - valueAnimator.getAnimatedFraction();
            this.mBackgroundDrawable.setAlpha((int) (this.mScrimAlpha * animatedFraction * 255.0f));
            this.mDepthController.updateGlobalDialogVisibility(animatedFraction, this.mGlobalActionsLayout);
        }

        /* access modifiers changed from: 0000 */
        public void dismissImmediately() {
            this.mShowing = false;
            ControlsUiController controlsUiController = this.mControlsUiController;
            if (controlsUiController != null) {
                controlsUiController.hide();
            }
            dismissPanel();
            resetOrientation();
            completeDismiss();
        }

        /* access modifiers changed from: private */
        public void completeDismiss() {
            this.mNotificationShadeWindowController.setForceHasTopUi(this.mHadTopUi);
            super.dismiss();
        }

        private void dismissPanel() {
            PanelViewController panelViewController = this.mPanelController;
            if (panelViewController != null) {
                panelViewController.onDismissed();
            }
        }

        private void setRotationSuggestionsEnabled(boolean z) {
            try {
                this.mStatusBarService.disable2ForUser(z ? 0 : 16, this.mToken, this.mContext.getPackageName(), Binder.getCallingUserHandle().getIdentifier());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        private void resetOrientation() {
            ResetOrientationData resetOrientationData = this.mResetOrientationData;
            if (resetOrientationData != null) {
                RotationPolicy.setRotationLockAtAngle(this.mContext, resetOrientationData.locked, resetOrientationData.rotation);
            }
            setRotationSuggestionsEnabled(true);
        }

        public void onColorsChanged(ColorExtractor colorExtractor, int i) {
            if (this.mKeyguardShowing) {
                if ((i & 2) != 0) {
                    updateColors(colorExtractor.getColors(2), true);
                }
            } else if ((i & 1) != 0) {
                updateColors(colorExtractor.getColors(1), true);
            }
        }

        public void setKeyguardShowing(boolean z) {
            this.mKeyguardShowing = z;
        }

        public void refreshDialog() {
            initializeLayout();
            this.mGlobalActionsLayout.updateList();
        }

        public void onRotate(int i, int i2) {
            if (this.mShowing) {
                refreshDialog();
            }
        }
    }

    private class AirplaneModeAction extends ToggleAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        AirplaneModeAction() {
            super(17302459, 17302461, 17040152, 17040151, 17040150);
        }

        /* access modifiers changed from: 0000 */
        public void onToggle(boolean z) {
            if (!GlobalActionsDialog.this.mHasTelephony || !((Boolean) TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)).booleanValue()) {
                GlobalActionsDialog.this.changeAirplaneModeSystemSetting(z);
                return;
            }
            GlobalActionsDialog.this.mIsWaitingForEcmExit = true;
            Intent intent = new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", null);
            intent.addFlags(268435456);
            GlobalActionsDialog.this.mContext.startActivity(intent);
        }

        /* access modifiers changed from: protected */
        public void changeStateFromPress(boolean z) {
            if (GlobalActionsDialog.this.mHasTelephony && !((Boolean) TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)).booleanValue()) {
                State state = z ? State.TurningOn : State.TurningOff;
                this.mState = state;
                GlobalActionsDialog.this.mAirplaneState = state;
            }
        }
    }

    private class BugReportAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        public BugReportAction() {
            super(17302463, 17039669);
        }

        public void onPress() {
            if (!ActivityManager.isUserAMonkey()) {
                GlobalActionsDialog.this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            GlobalActionsDialog.this.mMetricsLogger.action(292);
                            if (!GlobalActionsDialog.this.mIActivityManager.launchBugReportHandlerApp()) {
                                Log.w("GlobalActionsDialog", "Bugreport handler could not be launched");
                                GlobalActionsDialog.this.mIActivityManager.requestInteractiveBugReport();
                            }
                        } catch (RemoteException unused) {
                        }
                    }
                }, 500);
            }
        }

        public boolean onLongPress() {
            if (ActivityManager.isUserAMonkey()) {
                return false;
            }
            try {
                GlobalActionsDialog.this.mMetricsLogger.action(293);
                GlobalActionsDialog.this.mIActivityManager.requestFullBugReport();
            } catch (RemoteException unused) {
            }
            return false;
        }
    }

    private abstract class EmergencyAction extends SinglePressAction {
        public boolean shouldBeSeparated() {
            return true;
        }

        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        EmergencyAction(GlobalActionsDialog globalActionsDialog, int i, int i2) {
            super(i, i2);
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            int i;
            View create = super.create(context, view, viewGroup, layoutInflater);
            if (shouldBeSeparated()) {
                i = create.getResources().getColor(C2008R$color.global_actions_alert_text);
            } else {
                i = create.getResources().getColor(C2008R$color.global_actions_text);
            }
            TextView textView = (TextView) create.findViewById(16908299);
            textView.setTextColor(i);
            textView.setSelected(true);
            ((ImageView) create.findViewById(16908294)).getDrawable().setTint(i);
            return create;
        }
    }

    private class EmergencyAffordanceAction extends EmergencyAction {
        EmergencyAffordanceAction() {
            super(GlobalActionsDialog.this, 17302211, 17040137);
        }

        public void onPress() {
            GlobalActionsDialog.this.mEmergencyAffordanceManager.performEmergencyCall();
        }
    }

    private class EmergencyDialerAction extends EmergencyAction {
        private EmergencyDialerAction() {
            super(GlobalActionsDialog.this, C2010R$drawable.ic_emergency_star, 17040137);
        }

        public void onPress() {
            GlobalActionsDialog.this.mMetricsLogger.action(1569);
            if (GlobalActionsDialog.this.mTelecomManager != null) {
                Intent createLaunchEmergencyDialerIntent = GlobalActionsDialog.this.mTelecomManager.createLaunchEmergencyDialerIntent(null);
                createLaunchEmergencyDialerIntent.addFlags(343932928);
                createLaunchEmergencyDialerIntent.putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 2);
                GlobalActionsDialog.this.mContext.startActivityAsUser(createLaunchEmergencyDialerIntent, UserHandle.CURRENT);
            }
        }
    }

    private final class LogoutAction extends SinglePressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        private LogoutAction() {
            super(17302513, 17040140);
        }

        public void onPress() {
            GlobalActionsDialog.this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    LogoutAction.this.lambda$onPress$0$GlobalActionsDialog$LogoutAction();
                }
            }, 500);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPress$0 */
        public /* synthetic */ void lambda$onPress$0$GlobalActionsDialog$LogoutAction() {
            try {
                int i = GlobalActionsDialog.this.getCurrentUser().id;
                GlobalActionsDialog.this.mIActivityManager.switchUser(0);
                GlobalActionsDialog.this.mIActivityManager.stopUser(i, true, null);
            } catch (RemoteException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Couldn't logout user ");
                sb.append(e);
                Log.e("GlobalActionsDialog", sb.toString());
            }
        }
    }

    private interface LongPressAction extends Action {
        boolean onLongPress();
    }

    public class MyAdapter extends MultiListAdapter {
        public boolean areAllItemsEnabled() {
            return false;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public MyAdapter() {
        }

        private int countItems(boolean z) {
            int i = 0;
            for (int i2 = 0; i2 < GlobalActionsDialog.this.mItems.size(); i2++) {
                Action action = (Action) GlobalActionsDialog.this.mItems.get(i2);
                if (shouldBeShown(action) && action.shouldBeSeparated() == z) {
                    i++;
                }
            }
            return i;
        }

        private boolean shouldBeShown(Action action) {
            if (GlobalActionsDialog.this.mKeyguardShowing && !action.showDuringKeyguard()) {
                return false;
            }
            if (GlobalActionsDialog.this.mDeviceProvisioned || action.showBeforeProvisioning()) {
                return true;
            }
            return false;
        }

        public int countSeparatedItems() {
            return countItems(true);
        }

        public int countListItems() {
            return countItems(false);
        }

        public int getCount() {
            return countSeparatedItems() + countListItems();
        }

        public boolean isEnabled(int i) {
            return getItem(i).isEnabled();
        }

        public Action getItem(int i) {
            int i2 = 0;
            for (int i3 = 0; i3 < GlobalActionsDialog.this.mItems.size(); i3++) {
                Action action = (Action) GlobalActionsDialog.this.mItems.get(i3);
                if (shouldBeShown(action)) {
                    if (i2 == i) {
                        return action;
                    }
                    i2++;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("position ");
            sb.append(i);
            sb.append(" out of range of showable actions, filtered count=");
            sb.append(getCount());
            sb.append(", keyguardshowing=");
            sb.append(GlobalActionsDialog.this.mKeyguardShowing);
            sb.append(", provisioned=");
            sb.append(GlobalActionsDialog.this.mDeviceProvisioned);
            throw new IllegalArgumentException(sb.toString());
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Action item = getItem(i);
            View create = item.create(GlobalActionsDialog.this.mContext, view, viewGroup, LayoutInflater.from(GlobalActionsDialog.this.mContext));
            create.setOnClickListener(new OnClickListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    MyAdapter.this.lambda$getView$0$GlobalActionsDialog$MyAdapter(this.f$1, view);
                }
            });
            if (item instanceof LongPressAction) {
                create.setOnLongClickListener(new OnLongClickListener(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onLongClick(View view) {
                        return MyAdapter.this.lambda$getView$1$GlobalActionsDialog$MyAdapter(this.f$1, view);
                    }
                });
            }
            return create;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$getView$0 */
        public /* synthetic */ void lambda$getView$0$GlobalActionsDialog$MyAdapter(int i, View view) {
            onClickItem(i);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$getView$1 */
        public /* synthetic */ boolean lambda$getView$1$GlobalActionsDialog$MyAdapter(int i, View view) {
            return onLongClickItem(i);
        }

        public boolean onLongClickItem(int i) {
            Action item = GlobalActionsDialog.this.mAdapter.getItem(i);
            if (!(item instanceof LongPressAction)) {
                return false;
            }
            if (GlobalActionsDialog.this.mDialog != null) {
                GlobalActionsDialog.this.mDialog.dismiss();
            } else {
                Log.w("GlobalActionsDialog", "Action long-clicked while mDialog is null.");
            }
            return ((LongPressAction) item).onLongPress();
        }

        public void onClickItem(int i) {
            Action item = GlobalActionsDialog.this.mAdapter.getItem(i);
            if (!(item instanceof SilentModeTriStateAction)) {
                if (GlobalActionsDialog.this.mDialog != null) {
                    GlobalActionsDialog.this.mDialog.dismiss();
                } else {
                    Log.w("GlobalActionsDialog", "Action clicked while mDialog is null.");
                }
                item.onPress();
            }
        }

        public boolean shouldBeSeparated(int i) {
            return getItem(i).shouldBeSeparated();
        }
    }

    private final class PowerAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        private PowerAction() {
            super(17301552, 17040141);
        }

        public boolean onLongPress() {
            if (GlobalActionsDialog.this.mUserManager.hasUserRestriction("no_safe_boot")) {
                return false;
            }
            GlobalActionsDialog.this.mWindowManagerFuncs.reboot(true);
            return true;
        }

        public void onPress() {
            GlobalActionsDialog.this.mWindowManagerFuncs.shutdown();
        }
    }

    private final class RestartAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        private RestartAction() {
            super(17302801, 17040142);
        }

        public boolean onLongPress() {
            if (GlobalActionsDialog.this.mUserManager.hasUserRestriction("no_safe_boot")) {
                return false;
            }
            GlobalActionsDialog.this.mWindowManagerFuncs.reboot(true);
            return true;
        }

        public void onPress() {
            GlobalActionsDialog.this.mWindowManagerFuncs.reboot(false);
        }
    }

    private class ScreenshotAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        public ScreenshotAction() {
            super(17302803, 17040143);
        }

        public void onPress() {
            GlobalActionsDialog.this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    GlobalActionsDialog.this.mScreenshotHelper.takeScreenshot(1, true, true, GlobalActionsDialog.this.mHandler, null);
                    GlobalActionsDialog.this.mMetricsLogger.action(1282);
                }
            }, 500);
        }

        public boolean onLongPress() {
            if (FeatureFlagUtils.isEnabled(GlobalActionsDialog.this.mContext, "settings_screenrecord_long_press")) {
                GlobalActionsDialog.this.mScreenRecordHelper.launchRecordPrompt();
            } else {
                onPress();
            }
            return true;
        }
    }

    private class SilentModeToggleAction extends ToggleAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        public SilentModeToggleAction() {
            super(17302312, 17302311, 17040147, 17040146, 17040145);
        }

        /* access modifiers changed from: 0000 */
        public void onToggle(boolean z) {
            if (z) {
                GlobalActionsDialog.this.mAudioManager.setRingerMode(0);
            } else {
                GlobalActionsDialog.this.mAudioManager.setRingerMode(2);
            }
        }
    }

    private static class SilentModeTriStateAction implements Action, OnClickListener {
        private final int[] ITEM_IDS = {16909234, 16909235, 16909236};
        private final AudioManager mAudioManager;
        private final Handler mHandler;

        private int indexToRingerMode(int i) {
            return i;
        }

        private int ringerModeToIndex(int i) {
            return i;
        }

        public boolean isEnabled() {
            return true;
        }

        public void onPress() {
        }

        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        SilentModeTriStateAction(AudioManager audioManager, Handler handler) {
            this.mAudioManager = audioManager;
            this.mHandler = handler;
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            View inflate = layoutInflater.inflate(17367162, viewGroup, false);
            int ringerMode = this.mAudioManager.getRingerMode();
            ringerModeToIndex(ringerMode);
            int i = 0;
            while (i < 3) {
                View findViewById = inflate.findViewById(this.ITEM_IDS[i]);
                findViewById.setSelected(ringerMode == i);
                findViewById.setTag(Integer.valueOf(i));
                findViewById.setOnClickListener(this);
                i++;
            }
            return inflate;
        }

        public void onClick(View view) {
            if (view.getTag() instanceof Integer) {
                int intValue = ((Integer) view.getTag()).intValue();
                AudioManager audioManager = this.mAudioManager;
                indexToRingerMode(intValue);
                audioManager.setRingerMode(intValue);
                this.mHandler.sendEmptyMessageDelayed(0, 300);
            }
        }
    }

    private abstract class SinglePressAction implements Action {
        private final Drawable mIcon;
        private final int mIconResId;
        private final CharSequence mMessage;
        private final int mMessageResId;

        public boolean isEnabled() {
            return true;
        }

        public abstract void onPress();

        protected SinglePressAction(int i, int i2) {
            this.mIconResId = i;
            this.mMessageResId = i2;
            this.mMessage = null;
            this.mIcon = null;
        }

        protected SinglePressAction(int i, Drawable drawable, CharSequence charSequence) {
            this.mIconResId = i;
            this.mMessageResId = 0;
            this.mMessage = charSequence;
            this.mIcon = drawable;
        }

        /* access modifiers changed from: protected */
        public int getActionLayoutId(Context context) {
            if (GlobalActionsDialog.this.shouldShowControls()) {
                return C2013R$layout.global_actions_grid_item_v2;
            }
            return C2013R$layout.global_actions_grid_item;
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            View inflate = layoutInflater.inflate(getActionLayoutId(context), viewGroup, false);
            ImageView imageView = (ImageView) inflate.findViewById(16908294);
            TextView textView = (TextView) inflate.findViewById(16908299);
            textView.setSelected(true);
            Drawable drawable = this.mIcon;
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
                imageView.setScaleType(ScaleType.CENTER_CROP);
            } else {
                int i = this.mIconResId;
                if (i != 0) {
                    imageView.setImageDrawable(context.getDrawable(i));
                }
            }
            CharSequence charSequence = this.mMessage;
            if (charSequence != null) {
                textView.setText(charSequence);
            } else {
                textView.setText(this.mMessageResId);
            }
            return inflate;
        }
    }

    private static abstract class ToggleAction implements Action {
        protected int mDisabledIconResid;
        protected int mDisabledStatusMessageResId;
        protected int mEnabledIconResId;
        protected int mEnabledStatusMessageResId;
        protected State mState = State.Off;

        enum State {
            Off(false),
            TurningOn(true),
            TurningOff(true),
            On(false);
            
            private final boolean inTransition;

            private State(boolean z) {
                this.inTransition = z;
            }

            public boolean inTransition() {
                return this.inTransition;
            }
        }

        /* access modifiers changed from: 0000 */
        public abstract void onToggle(boolean z);

        /* access modifiers changed from: 0000 */
        public void willCreate() {
        }

        public ToggleAction(int i, int i2, int i3, int i4, int i5) {
            this.mEnabledIconResId = i;
            this.mDisabledIconResid = i2;
            this.mEnabledStatusMessageResId = i4;
            this.mDisabledStatusMessageResId = i5;
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            willCreate();
            boolean z = false;
            View inflate = layoutInflater.inflate(C2013R$layout.global_actions_grid_item, viewGroup, false);
            ImageView imageView = (ImageView) inflate.findViewById(16908294);
            TextView textView = (TextView) inflate.findViewById(16908299);
            boolean isEnabled = isEnabled();
            State state = this.mState;
            if (state == State.On || state == State.TurningOn) {
                z = true;
            }
            if (textView != null) {
                textView.setText(z ? this.mEnabledStatusMessageResId : this.mDisabledStatusMessageResId);
                textView.setEnabled(isEnabled);
                textView.setSelected(true);
            }
            if (imageView != null) {
                imageView.setImageDrawable(context.getDrawable(z ? this.mEnabledIconResId : this.mDisabledIconResid));
                imageView.setEnabled(isEnabled);
            }
            inflate.setEnabled(isEnabled);
            return inflate;
        }

        public final void onPress() {
            if (this.mState.inTransition()) {
                Log.w("GlobalActionsDialog", "shouldn't be able to toggle when in transition");
                return;
            }
            boolean z = this.mState != State.On;
            onToggle(z);
            changeStateFromPress(z);
        }

        public boolean isEnabled() {
            return !this.mState.inTransition();
        }

        /* access modifiers changed from: protected */
        public void changeStateFromPress(boolean z) {
            this.mState = z ? State.On : State.Off;
        }

        public void updateState(State state) {
            this.mState = state;
        }
    }

    public GlobalActionsDialog(Context context, GlobalActionsManager globalActionsManager, AudioManager audioManager, IDreamManager iDreamManager, DevicePolicyManager devicePolicyManager, LockPatternUtils lockPatternUtils, BroadcastDispatcher broadcastDispatcher, ConnectivityManager connectivityManager, TelephonyManager telephonyManager, ContentResolver contentResolver, Vibrator vibrator, Resources resources, ConfigurationController configurationController, ActivityStarter activityStarter, KeyguardStateController keyguardStateController, UserManager userManager, TrustManager trustManager, IActivityManager iActivityManager, TelecomManager telecomManager, MetricsLogger metricsLogger, NotificationShadeDepthController notificationShadeDepthController, SysuiColorExtractor sysuiColorExtractor, IStatusBarService iStatusBarService, BlurUtils blurUtils, NotificationShadeWindowController notificationShadeWindowController, ControlsUiController controlsUiController, IWindowManager iWindowManager, Executor executor, ControlsListingController controlsListingController, ControlsController controlsController) {
        Context context2 = context;
        ContentResolver contentResolver2 = contentResolver;
        Resources resources2 = resources;
        final KeyguardStateController keyguardStateController2 = keyguardStateController;
        boolean z = false;
        this.mContext = new ContextThemeWrapper(context, C2018R$style.qs_theme);
        this.mWindowManagerFuncs = globalActionsManager;
        this.mAudioManager = audioManager;
        this.mDreamManager = iDreamManager;
        this.mDevicePolicyManager = devicePolicyManager;
        this.mLockPatternUtils = lockPatternUtils;
        this.mKeyguardStateController = keyguardStateController2;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mContentResolver = contentResolver2;
        this.mResources = resources2;
        this.mConfigurationController = configurationController;
        this.mUserManager = userManager;
        this.mTrustManager = trustManager;
        this.mIActivityManager = iActivityManager;
        this.mTelecomManager = telecomManager;
        this.mMetricsLogger = metricsLogger;
        this.mDepthController = notificationShadeDepthController;
        this.mSysuiColorExtractor = sysuiColorExtractor;
        this.mStatusBarService = iStatusBarService;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mControlsUiController = controlsUiController;
        this.mIWindowManager = iWindowManager;
        this.mBackgroundExecutor = executor;
        this.mControlsListingController = controlsListingController;
        this.mBlurUtils = blurUtils;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mHasTelephony = connectivityManager.isNetworkSupported(0);
        telephonyManager.listen(this.mPhoneStateListener, 1);
        contentResolver2.registerContentObserver(Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
        if (vibrator != null && vibrator.hasVibrator()) {
            z = true;
        }
        this.mHasVibrator = z;
        this.mShowSilentToggle = !resources2.getBoolean(17891567);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
        this.mScreenshotHelper = new ScreenshotHelper(context);
        this.mScreenRecordHelper = new ScreenRecordHelper(context);
        this.mConfigurationController.addCallback(this);
        this.mActivityStarter = activityStarter;
        keyguardStateController2.addCallback(new Callback() {
            public void onUnlockedChanged() {
                if (GlobalActionsDialog.this.mDialog != null && GlobalActionsDialog.this.mDialog.mPanelController != null) {
                    GlobalActionsDialog.this.mDialog.mPanelController.onDeviceLockStateChanged(keyguardStateController2.isUnlocked() || keyguardStateController2.canDismissLockScreen());
                }
            }
        });
        this.mControlsListingController.addCallback(new ControlsListingCallback(this.mContext.getResources().getString(C2017R$string.config_controlsPreferredPackage), context, controlsController) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ Context f$2;
            public final /* synthetic */ ControlsController f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onServicesUpdated(List list) {
                GlobalActionsDialog.this.lambda$new$1$GlobalActionsDialog(this.f$1, this.f$2, this.f$3, list);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GlobalActionsDialog(String str, Context context, ControlsController controlsController, List list) {
        boolean z = true;
        this.mAnyControlsProviders = !list.isEmpty();
        ComponentName componentName = null;
        Iterator it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ControlsServiceInfo controlsServiceInfo = (ControlsServiceInfo) it.next();
            if (controlsServiceInfo.componentName.getPackageName().equals(str)) {
                componentName = controlsServiceInfo.componentName;
                break;
            }
        }
        if (componentName != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("controls_prefs", 0);
            boolean z2 = sharedPreferences.getBoolean("ControlsSeedingCompleted", false);
            if (controlsController.getFavorites().size() <= 0) {
                z = false;
            }
            if (!z2 && !z) {
                controlsController.seedFavoritesForComponent(componentName, new Consumer(sharedPreferences) {
                    public final /* synthetic */ SharedPreferences f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        GlobalActionsDialog.lambda$new$0(this.f$0, (Boolean) obj);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$new$0(SharedPreferences sharedPreferences, Boolean bool) {
        StringBuilder sb = new StringBuilder();
        sb.append("Controls seeded: ");
        sb.append(bool);
        Log.i("GlobalActionsDialog", sb.toString());
        sharedPreferences.edit().putBoolean("ControlsSeedingCompleted", bool.booleanValue()).apply();
    }

    public void showDialog(boolean z, boolean z2, GlobalActionsPanelPlugin globalActionsPanelPlugin) {
        this.mKeyguardShowing = z;
        this.mDeviceProvisioned = z2;
        this.mPanelPlugin = globalActionsPanelPlugin;
        ActionsDialog actionsDialog = this.mDialog;
        if (actionsDialog != null) {
            actionsDialog.dismiss();
            this.mDialog = null;
            this.mHandler.sendEmptyMessage(2);
            return;
        }
        handleShow();
    }

    public void dismissDialog() {
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessage(0);
    }

    private void awakenIfNecessary() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager != null) {
            try {
                if (iDreamManager.isDreaming()) {
                    this.mDreamManager.awaken();
                }
            } catch (RemoteException unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleShow() {
        awakenIfNecessary();
        this.mDialog = createDialog();
        prepareDialog();
        if (this.mAdapter.getCount() != 1 || !(this.mAdapter.getItem(0) instanceof SinglePressAction) || (this.mAdapter.getItem(0) instanceof LongPressAction)) {
            LayoutParams attributes = this.mDialog.getWindow().getAttributes();
            attributes.setTitle("ActionsDialog");
            attributes.layoutInDisplayCutoutMode = 3;
            this.mDialog.getWindow().setAttributes(attributes);
            this.mDialog.show();
            this.mWindowManagerFuncs.onGlobalActionsShown();
            return;
        }
        ((SinglePressAction) this.mAdapter.getItem(0)).onPress();
    }

    private ActionsDialog createDialog() {
        ControlsUiController controlsUiController;
        if (!this.mHasVibrator) {
            this.mSilentModeAction = new SilentModeToggleAction();
        } else {
            this.mSilentModeAction = new SilentModeTriStateAction(this.mAudioManager, this.mHandler);
        }
        this.mAirplaneModeOn = new AirplaneModeAction();
        onAirplaneModeChanged();
        this.mItems = new ArrayList<>();
        String[] stringArray = this.mResources.getStringArray(17236041);
        ArraySet arraySet = new ArraySet();
        int i = 0;
        while (true) {
            controlsUiController = null;
            if (i >= stringArray.length) {
                break;
            }
            String str = stringArray[i];
            if (!arraySet.contains(str)) {
                if ("power".equals(str)) {
                    this.mItems.add(new PowerAction());
                } else if ("airplane".equals(str)) {
                    this.mItems.add(this.mAirplaneModeOn);
                } else if ("bugreport".equals(str)) {
                    if (Global.getInt(this.mContentResolver, "bugreport_in_power_menu", 0) != 0 && isCurrentUserOwner()) {
                        this.mItems.add(new BugReportAction());
                    }
                } else if ("silent".equals(str)) {
                    if (this.mShowSilentToggle) {
                        this.mItems.add(this.mSilentModeAction);
                    }
                } else if ("users".equals(str)) {
                    if (SystemProperties.getBoolean("fw.power_user_switcher", false)) {
                        addUsersToMenu(this.mItems);
                    }
                } else if ("settings".equals(str)) {
                    this.mItems.add(getSettingsAction());
                } else if ("lockdown".equals(str)) {
                    if (Secure.getIntForUser(this.mContentResolver, "lockdown_in_power_menu", 0, getCurrentUser().id) != 0 && shouldDisplayLockdown()) {
                        this.mItems.add(getLockdownAction());
                    }
                } else if ("voiceassist".equals(str)) {
                    this.mItems.add(getVoiceAssistAction());
                } else if ("assist".equals(str)) {
                    this.mItems.add(getAssistAction());
                } else if ("restart".equals(str)) {
                    this.mItems.add(new RestartAction());
                } else if ("screenshot".equals(str)) {
                    this.mItems.add(new ScreenshotAction());
                } else if ("logout".equals(str)) {
                    if (this.mDevicePolicyManager.isLogoutEnabled() && getCurrentUser().id != 0) {
                        this.mItems.add(new LogoutAction());
                    }
                } else if (!"emergency".equals(str)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid global action key ");
                    sb.append(str);
                    Log.e("GlobalActionsDialog", sb.toString());
                } else if (!this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
                    this.mItems.add(new EmergencyDialerAction());
                }
                arraySet.add(str);
            }
            i++;
        }
        if (this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            this.mItems.add(new EmergencyAffordanceAction());
        }
        this.mAdapter = new MyAdapter();
        Context context = this.mContext;
        MyAdapter myAdapter = this.mAdapter;
        PanelViewController walletPanelViewController = getWalletPanelViewController();
        NotificationShadeDepthController notificationShadeDepthController = this.mDepthController;
        SysuiColorExtractor sysuiColorExtractor = this.mSysuiColorExtractor;
        IStatusBarService iStatusBarService = this.mStatusBarService;
        NotificationShadeWindowController notificationShadeWindowController = this.mNotificationShadeWindowController;
        if (shouldShowControls()) {
            controlsUiController = this.mControlsUiController;
        }
        ActionsDialog actionsDialog = new ActionsDialog(context, myAdapter, walletPanelViewController, notificationShadeDepthController, sysuiColorExtractor, iStatusBarService, notificationShadeWindowController, controlsUiController, this.mBlurUtils);
        actionsDialog.setCanceledOnTouchOutside(false);
        actionsDialog.setKeyguardShowing(this.mKeyguardShowing);
        actionsDialog.setOnDismissListener(this);
        actionsDialog.setOnShowListener(this);
        return actionsDialog;
    }

    private boolean shouldDisplayLockdown() {
        boolean z = false;
        if (!this.mKeyguardStateController.isMethodSecure()) {
            return false;
        }
        int strongAuthForUser = this.mLockPatternUtils.getStrongAuthForUser(getCurrentUser().id);
        if (strongAuthForUser == 0 || strongAuthForUser == 4) {
            z = true;
        }
        return z;
    }

    public void onUiModeChanged() {
        this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
        ActionsDialog actionsDialog = this.mDialog;
        if (actionsDialog != null && actionsDialog.isShowing()) {
            this.mDialog.refreshDialog();
        }
    }

    public void destroy() {
        this.mConfigurationController.removeCallback(this);
    }

    private PanelViewController getWalletPanelViewController() {
        GlobalActionsPanelPlugin globalActionsPanelPlugin = this.mPanelPlugin;
        if (globalActionsPanelPlugin == null) {
            return null;
        }
        return globalActionsPanelPlugin.onPanelShown(this, !this.mKeyguardStateController.isUnlocked());
    }

    public void dismissGlobalActionsMenu() {
        dismissDialog();
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent) {
        this.mActivityStarter.startPendingIntentDismissingKeyguard(pendingIntent);
    }

    private Action getSettingsAction() {
        return new SinglePressAction(17302809, 17040144) {
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.settings.SETTINGS");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }
        };
    }

    private Action getAssistAction() {
        return new SinglePressAction(17302294, 17040135) {
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.intent.action.ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }
        };
    }

    private Action getVoiceAssistAction() {
        return new SinglePressAction(17302851, 17040148) {
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.intent.action.VOICE_ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }
        };
    }

    private Action getLockdownAction() {
        return new SinglePressAction(17302466, 17040139) {
            public boolean showBeforeProvisioning() {
                return false;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                GlobalActionsDialog.this.mLockPatternUtils.requireStrongAuth(32, -1);
                try {
                    GlobalActionsDialog.this.mIWindowManager.lockNow(null);
                    GlobalActionsDialog.this.mBackgroundExecutor.execute(new Runnable() {
                        public final void run() {
                            C08625.this.lambda$onPress$0$GlobalActionsDialog$5();
                        }
                    });
                } catch (RemoteException e) {
                    Log.e("GlobalActionsDialog", "Error while trying to lock device.", e);
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onPress$0 */
            public /* synthetic */ void lambda$onPress$0$GlobalActionsDialog$5() {
                GlobalActionsDialog.this.lockProfiles();
            }
        };
    }

    /* access modifiers changed from: private */
    public void lockProfiles() {
        int[] enabledProfileIds;
        int i = getCurrentUser().id;
        for (int i2 : this.mUserManager.getEnabledProfileIds(i)) {
            if (i2 != i) {
                this.mTrustManager.setDeviceLockedForUser(i2, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public UserInfo getCurrentUser() {
        try {
            return this.mIActivityManager.getCurrentUser();
        } catch (RemoteException unused) {
            return null;
        }
    }

    private boolean isCurrentUserOwner() {
        UserInfo currentUser = getCurrentUser();
        return currentUser == null || currentUser.isPrimary();
    }

    private void addUsersToMenu(ArrayList<Action> arrayList) {
        if (this.mUserManager.isUserSwitcherEnabled()) {
            List<UserInfo> users = this.mUserManager.getUsers();
            UserInfo currentUser = getCurrentUser();
            for (final UserInfo userInfo : users) {
                if (userInfo.supportsSwitchToByUser()) {
                    boolean z = true;
                    if (currentUser != null ? currentUser.id != userInfo.id : userInfo.id != 0) {
                        z = false;
                    }
                    String str = userInfo.iconPath;
                    Drawable createFromPath = str != null ? Drawable.createFromPath(str) : null;
                    StringBuilder sb = new StringBuilder();
                    String str2 = userInfo.name;
                    if (str2 == null) {
                        str2 = "Primary";
                    }
                    sb.append(str2);
                    sb.append(z ? " ✔" : "");
                    C08636 r3 = new SinglePressAction(17302682, createFromPath, sb.toString()) {
                        public boolean showBeforeProvisioning() {
                            return false;
                        }

                        public boolean showDuringKeyguard() {
                            return true;
                        }

                        public void onPress() {
                            try {
                                GlobalActionsDialog.this.mIActivityManager.switchUser(userInfo.id);
                            } catch (RemoteException e) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Couldn't switch user ");
                                sb.append(e);
                                Log.e("GlobalActionsDialog", sb.toString());
                            }
                        }
                    };
                    arrayList.add(r3);
                }
            }
        }
    }

    private void prepareDialog() {
        refreshSilentMode();
        this.mAirplaneModeOn.updateState(this.mAirplaneState);
        this.mAdapter.notifyDataSetChanged();
        if (this.mShowSilentToggle) {
            this.mBroadcastDispatcher.registerReceiver(this.mRingerModeReceiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
        }
    }

    /* access modifiers changed from: private */
    public void refreshSilentMode() {
        if (!this.mHasVibrator) {
            ((ToggleAction) this.mSilentModeAction).updateState(this.mAudioManager.getRingerMode() != 2 ? State.On : State.Off);
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (this.mDialog == dialogInterface) {
            this.mDialog = null;
        }
        this.mWindowManagerFuncs.onGlobalActionsHidden();
        if (this.mShowSilentToggle) {
            try {
                this.mBroadcastDispatcher.unregisterReceiver(this.mRingerModeReceiver);
            } catch (IllegalArgumentException e) {
                Log.w("GlobalActionsDialog", e);
            }
        }
    }

    public void onShow(DialogInterface dialogInterface) {
        this.mMetricsLogger.visible(1568);
    }

    /* access modifiers changed from: private */
    public void onAirplaneModeChanged() {
        if (!this.mHasTelephony) {
            boolean z = false;
            if (Global.getInt(this.mContentResolver, "airplane_mode_on", 0) == 1) {
                z = true;
            }
            State state = z ? State.On : State.Off;
            this.mAirplaneState = state;
            this.mAirplaneModeOn.updateState(state);
        }
    }

    /* access modifiers changed from: private */
    public void changeAirplaneModeSystemSetting(boolean z) {
        Global.putInt(this.mContentResolver, "airplane_mode_on", z ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.addFlags(536870912);
        intent.putExtra("state", z);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        if (!this.mHasTelephony) {
            this.mAirplaneState = z ? State.On : State.Off;
        }
    }

    private static boolean isPanelDebugModeEnabled(Context context) {
        return Secure.getInt(context.getContentResolver(), "global_actions_panel_debug_enabled", 0) == 1;
    }

    /* access modifiers changed from: private */
    public static boolean isForceGridEnabled(Context context) {
        return isPanelDebugModeEnabled(context);
    }

    /* access modifiers changed from: private */
    public boolean shouldShowControls() {
        return this.mKeyguardStateController.isUnlocked() && this.mControlsUiController.getAvailable() && this.mAnyControlsProviders;
    }
}
