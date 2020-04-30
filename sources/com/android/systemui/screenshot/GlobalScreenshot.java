package com.android.systemui.screenshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Notification.Action;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.drawable.Icon;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.screenshot.GlobalScreenshot.ActionProxyReceiver;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class GlobalScreenshot implements OnComputeInternalInsetsListener {
    private final HorizontalScrollView mActionsContainer;
    private final LinearLayout mActionsView;
    private final ImageView mBackgroundProtection;
    private final ImageView mBackgroundView;
    private MediaActionSound mCameraSound;
    private final Context mContext;
    private float mCornerSizeX;
    /* access modifiers changed from: private */
    public final FrameLayout mDismissButton;
    /* access modifiers changed from: private */
    public float mDismissButtonSize;
    private final Display mDisplay;
    private final DisplayMetrics mDisplayMetrics;
    private final Interpolator mFastOutSlowIn;
    /* access modifiers changed from: private */
    public final ScreenshotNotificationsController mNotificationsController;
    private AsyncTask<Void, Void, Void> mSaveInBgTask;
    private Bitmap mScreenBitmap;
    /* access modifiers changed from: private */
    public Animator mScreenshotAnimation;
    private final ImageView mScreenshotFlash;
    /* access modifiers changed from: private */
    public final Handler mScreenshotHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                GlobalScreenshot.this.clearScreenshot("timeout");
            }
        }
    };
    private float mScreenshotHeightPx;
    /* access modifiers changed from: private */
    public final View mScreenshotLayout;
    private float mScreenshotOffsetXPx;
    private float mScreenshotOffsetYPx;
    private final ScreenshotSelectorView mScreenshotSelectorView;
    /* access modifiers changed from: private */
    public final ImageView mScreenshotView;
    private final LayoutParams mWindowLayoutParams;
    /* access modifiers changed from: private */
    public final WindowManager mWindowManager;

    public static class ActionProxyReceiver extends BroadcastReceiver {
        private final StatusBar mStatusBar;

        public ActionProxyReceiver(Optional<Lazy<StatusBar>> optional) {
            StatusBar statusBar = null;
            Lazy lazy = (Lazy) optional.orElse(null);
            if (lazy != null) {
                statusBar = (StatusBar) lazy.get();
            }
            this.mStatusBar = statusBar;
        }

        public void onReceive(Context context, Intent intent) {
            C1051xe277d236 r1 = new Runnable(intent, context) {
                public final /* synthetic */ Intent f$0;
                public final /* synthetic */ Context f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    ActionProxyReceiver.lambda$onReceive$0(this.f$0, this.f$1);
                }
            };
            StatusBar statusBar = this.mStatusBar;
            if (statusBar != null) {
                statusBar.executeRunnableDismissingKeyguard(r1, null, true, true, true);
            } else {
                r1.run();
            }
            if (intent.getBooleanExtra("android:smart_actions_enabled", false)) {
                ScreenshotSmartActions.notifyScreenshotAction(context, intent.getStringExtra("android:screenshot_id"), "android.intent.action.EDIT".equals(intent.getAction()) ? "Edit" : "Share", false);
            }
        }

        static /* synthetic */ void lambda$onReceive$0(Intent intent, Context context) {
            try {
                ActivityManagerWrapper.getInstance().closeSystemWindows("screenshot").get(3000, TimeUnit.MILLISECONDS);
                Intent intent2 = (Intent) intent.getParcelableExtra("android:screenshot_action_intent");
                if (intent.getBooleanExtra("android:screenshot_cancel_notification", false)) {
                    ScreenshotNotificationsController.cancelScreenshotNotification(context);
                }
                ActivityOptions makeBasic = ActivityOptions.makeBasic();
                makeBasic.setDisallowEnterPictureInPictureWhileLaunching(intent.getBooleanExtra("android:screenshot_disallow_enter_pip", false));
                context.startActivityAsUser(intent2, makeBasic.toBundle(), UserHandle.CURRENT);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Slog.e("GlobalScreenshot", "Unable to share screenshot", e);
            }
        }
    }

    static abstract class ActionsReadyListener {
        /* access modifiers changed from: 0000 */
        public abstract void onActionsReady(Uri uri, List<Action> list, List<Action> list2);

        ActionsReadyListener() {
        }
    }

    public static class DeleteScreenshotReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String str = "android:screenshot_uri_id";
            if (intent.hasExtra(str)) {
                ScreenshotNotificationsController.cancelScreenshotNotification(context);
                Uri parse = Uri.parse(intent.getStringExtra(str));
                new DeleteImageInBackgroundTask(context).execute(new Uri[]{parse});
                if (intent.getBooleanExtra("android:smart_actions_enabled", false)) {
                    ScreenshotSmartActions.notifyScreenshotAction(context, intent.getStringExtra("android:screenshot_id"), "Delete", false);
                }
            }
        }
    }

    static class SaveImageInBackgroundData {
        public boolean createDeleteAction;
        public int errorMsgResId;
        public Consumer<Uri> finisher;
        public Bitmap image;
        public Uri imageUri;
        public ActionsReadyListener mActionsReadyListener;

        SaveImageInBackgroundData() {
        }

        /* access modifiers changed from: 0000 */
        public void clearImage() {
            this.image = null;
            this.imageUri = null;
        }
    }

    public static class SmartActionsReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Intent intent2 = ((PendingIntent) intent.getParcelableExtra("android:screenshot_action_intent")).getIntent();
            String stringExtra = intent.getStringExtra("android:screenshot_action_type");
            StringBuilder sb = new StringBuilder();
            sb.append("Executing smart action [");
            sb.append(stringExtra);
            sb.append("]:");
            sb.append(intent2);
            Slog.d("GlobalScreenshot", sb.toString());
            context.startActivityAsUser(intent2, ActivityOptions.makeBasic().toBundle(), UserHandle.CURRENT);
            ScreenshotSmartActions.notifyScreenshotAction(context, intent.getStringExtra("android:screenshot_id"), stringExtra, true);
        }
    }

    public static class TargetChosenReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            ScreenshotNotificationsController.cancelScreenshotNotification(context);
        }
    }

    public GlobalScreenshot(Context context, Resources resources, LayoutInflater layoutInflater, ScreenshotNotificationsController screenshotNotificationsController) {
        this.mContext = context;
        this.mNotificationsController = screenshotNotificationsController;
        View inflate = layoutInflater.inflate(C2013R$layout.global_screenshot, null);
        this.mScreenshotLayout = inflate;
        this.mBackgroundView = (ImageView) inflate.findViewById(C2011R$id.global_screenshot_background);
        ImageView imageView = (ImageView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot);
        this.mScreenshotView = imageView;
        imageView.setClipToOutline(true);
        this.mScreenshotView.setOutlineProvider(new ViewOutlineProvider(this) {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(new Rect(0, 0, view.getWidth(), view.getHeight()), ((float) view.getWidth()) * 0.05f);
            }
        });
        this.mActionsContainer = (HorizontalScrollView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_actions_container);
        this.mActionsView = (LinearLayout) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_actions);
        this.mBackgroundProtection = (ImageView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_actions_background);
        FrameLayout frameLayout = (FrameLayout) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_dismiss_button);
        this.mDismissButton = frameLayout;
        frameLayout.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                GlobalScreenshot.this.lambda$new$0$GlobalScreenshot(view);
            }
        });
        this.mScreenshotFlash = (ImageView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_flash);
        this.mScreenshotSelectorView = (ScreenshotSelectorView) this.mScreenshotLayout.findViewById(C2011R$id.global_screenshot_selector);
        this.mScreenshotLayout.setFocusable(true);
        this.mScreenshotSelectorView.setFocusable(true);
        this.mScreenshotSelectorView.setFocusableInTouchMode(true);
        this.mScreenshotView.setPivotX(0.0f);
        this.mScreenshotView.setPivotY(0.0f);
        LayoutParams layoutParams = new LayoutParams(-1, -1, 0, 0, 2036, 787744, -3);
        this.mWindowLayoutParams = layoutParams;
        layoutParams.setTitle("ScreenshotAnimation");
        LayoutParams layoutParams2 = this.mWindowLayoutParams;
        layoutParams2.layoutInDisplayCutoutMode = 3;
        layoutParams2.setFitInsetsTypes(0);
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        this.mWindowManager = windowManager;
        this.mDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mDisplayMetrics = displayMetrics;
        this.mDisplay.getRealMetrics(displayMetrics);
        this.mScreenshotOffsetXPx = (float) resources.getDimensionPixelSize(C2009R$dimen.screenshot_offset_x);
        this.mScreenshotOffsetYPx = (float) resources.getDimensionPixelSize(C2009R$dimen.screenshot_offset_y);
        this.mScreenshotHeightPx = (float) resources.getDimensionPixelSize(C2009R$dimen.screenshot_action_container_offset_y);
        this.mDismissButtonSize = (float) resources.getDimensionPixelSize(C2009R$dimen.screenshot_dismiss_button_tappable_size);
        this.mCornerSizeX = (float) resources.getDimensionPixelSize(C2009R$dimen.global_screenshot_x_scale);
        this.mFastOutSlowIn = AnimationUtils.loadInterpolator(this.mContext, 17563661);
        MediaActionSound mediaActionSound = new MediaActionSound();
        this.mCameraSound = mediaActionSound;
        mediaActionSound.load(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GlobalScreenshot(View view) {
        clearScreenshot("dismiss_button");
    }

    public void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo) {
        internalInsetsInfo.setTouchableInsets(3);
        Region region = new Region();
        Rect rect = new Rect();
        this.mScreenshotView.getBoundsOnScreen(rect);
        region.op(rect, Op.UNION);
        Rect rect2 = new Rect();
        this.mActionsContainer.getBoundsOnScreen(rect2);
        region.op(rect2, Op.UNION);
        Rect rect3 = new Rect();
        this.mDismissButton.getBoundsOnScreen(rect3);
        region.op(rect3, Op.UNION);
        internalInsetsInfo.touchableRegion.set(region);
    }

    private void saveScreenshotInWorkerThread(Consumer<Uri> consumer, ActionsReadyListener actionsReadyListener) {
        SaveImageInBackgroundData saveImageInBackgroundData = new SaveImageInBackgroundData();
        saveImageInBackgroundData.image = this.mScreenBitmap;
        saveImageInBackgroundData.finisher = consumer;
        saveImageInBackgroundData.mActionsReadyListener = actionsReadyListener;
        saveImageInBackgroundData.createDeleteAction = false;
        AsyncTask<Void, Void, Void> asyncTask = this.mSaveInBgTask;
        if (asyncTask != null) {
            asyncTask.cancel(false);
        }
        this.mSaveInBgTask = new SaveImageInBackgroundTask(this.mContext, saveImageInBackgroundData).execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public void takeScreenshot(Consumer<Uri> consumer, Rect rect) {
        clearScreenshot("new screenshot requested");
        int rotation = this.mDisplay.getRotation();
        int width = rect.width();
        int height = rect.height();
        DisplayMetrics displayMetrics = this.mDisplayMetrics;
        takeScreenshot(SurfaceControl.screenshot(rect, width, height, rotation), consumer, new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels));
    }

    private void takeScreenshot(Bitmap bitmap, Consumer<Uri> consumer, Rect rect) {
        this.mScreenBitmap = bitmap;
        if (bitmap == null) {
            this.mNotificationsController.notifyScreenshotError(C2017R$string.screenshot_failed_to_capture_text);
            consumer.accept(null);
            return;
        }
        bitmap.setHasAlpha(false);
        this.mScreenBitmap.prepareToDraw();
        this.mWindowManager.addView(this.mScreenshotLayout, this.mWindowLayoutParams);
        this.mScreenshotLayout.getViewTreeObserver().addOnComputeInternalInsetsListener(this);
        startAnimation(consumer, rect.width(), rect.height(), rect);
    }

    /* access modifiers changed from: 0000 */
    public void takeScreenshot(Consumer<Uri> consumer) {
        this.mDisplay.getRealMetrics(this.mDisplayMetrics);
        DisplayMetrics displayMetrics = this.mDisplayMetrics;
        takeScreenshot(consumer, new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels));
    }

    /* access modifiers changed from: 0000 */
    public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i, Consumer<Uri> consumer) {
        clearScreenshot("new screenshot requested");
        takeScreenshot(bitmap, consumer, rect);
    }

    /* access modifiers changed from: 0000 */
    @SuppressLint({"ClickableViewAccessibility"})
    public void takeScreenshotPartial(final Consumer<Uri> consumer) {
        this.mWindowManager.addView(this.mScreenshotLayout, this.mWindowLayoutParams);
        this.mScreenshotSelectorView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ScreenshotSelectorView screenshotSelectorView = (ScreenshotSelectorView) view;
                int action = motionEvent.getAction();
                if (action == 0) {
                    screenshotSelectorView.startSelection((int) motionEvent.getX(), (int) motionEvent.getY());
                    return true;
                } else if (action == 1) {
                    screenshotSelectorView.setVisibility(8);
                    GlobalScreenshot.this.mWindowManager.removeView(GlobalScreenshot.this.mScreenshotLayout);
                    Rect selectionRect = screenshotSelectorView.getSelectionRect();
                    if (!(selectionRect == null || selectionRect.width() == 0 || selectionRect.height() == 0)) {
                        GlobalScreenshot.this.mScreenshotLayout.post(new Runnable(consumer, selectionRect) {
                            public final /* synthetic */ Consumer f$1;
                            public final /* synthetic */ Rect f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                C10543.this.lambda$onTouch$0$GlobalScreenshot$3(this.f$1, this.f$2);
                            }
                        });
                    }
                    screenshotSelectorView.stopSelection();
                    return true;
                } else if (action != 2) {
                    return false;
                } else {
                    screenshotSelectorView.updateSelection((int) motionEvent.getX(), (int) motionEvent.getY());
                    return true;
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onTouch$0 */
            public /* synthetic */ void lambda$onTouch$0$GlobalScreenshot$3(Consumer consumer, Rect rect) {
                GlobalScreenshot.this.takeScreenshot(consumer, rect);
            }
        });
        this.mScreenshotLayout.post(new Runnable() {
            public final void run() {
                GlobalScreenshot.this.lambda$takeScreenshotPartial$1$GlobalScreenshot();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$takeScreenshotPartial$1 */
    public /* synthetic */ void lambda$takeScreenshotPartial$1$GlobalScreenshot() {
        this.mScreenshotSelectorView.setVisibility(0);
        this.mScreenshotSelectorView.requestFocus();
    }

    /* access modifiers changed from: 0000 */
    public void stopScreenshot() {
        if (this.mScreenshotSelectorView.getSelectionRect() != null) {
            this.mWindowManager.removeView(this.mScreenshotLayout);
            this.mScreenshotSelectorView.stopSelection();
        }
    }

    /* access modifiers changed from: private */
    public void clearScreenshot(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("clearing screenshot: ");
        sb.append(str);
        Log.e("GlobalScreenshot", sb.toString());
        if (this.mScreenshotLayout.isAttachedToWindow()) {
            this.mWindowManager.removeView(this.mScreenshotLayout);
        }
        this.mScreenshotHandler.removeMessages(2);
        this.mScreenshotLayout.getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
        this.mScreenshotView.setImageBitmap(null);
        this.mActionsContainer.setVisibility(8);
        this.mBackgroundView.setVisibility(8);
        this.mBackgroundProtection.setAlpha(0.0f);
        this.mDismissButton.setVisibility(8);
        this.mScreenshotView.setVisibility(8);
        this.mScreenshotView.setLayerType(0, null);
    }

    private void startAnimation(Consumer<Uri> consumer, int i, int i2, Rect rect) {
        if (((PowerManager) this.mContext.getSystemService("power")).isPowerSaveMode()) {
            Toast.makeText(this.mContext, C2017R$string.screenshot_saved_title, 0).show();
        }
        this.mScreenshotView.setImageBitmap(this.mScreenBitmap);
        this.mScreenshotAnimation = createScreenshotDropInAnimation(i, i2, rect);
        saveScreenshotInWorkerThread(consumer, new ActionsReadyListener() {
            /* access modifiers changed from: 0000 */
            public void onActionsReady(Uri uri, List<Action> list, List<Action> list2) {
                if (uri == null) {
                    GlobalScreenshot.this.mNotificationsController.notifyScreenshotError(C2017R$string.screenshot_failed_to_capture_text);
                } else {
                    GlobalScreenshot.this.mScreenshotHandler.post(new Runnable(list, list2) {
                        public final /* synthetic */ List f$1;
                        public final /* synthetic */ List f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            C10554.this.lambda$onActionsReady$0$GlobalScreenshot$4(this.f$1, this.f$2);
                        }
                    });
                }
                GlobalScreenshot.this.mScreenshotHandler.removeMessages(2);
                GlobalScreenshot.this.mScreenshotHandler.sendMessageDelayed(GlobalScreenshot.this.mScreenshotHandler.obtainMessage(2), 6000);
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onActionsReady$0 */
            public /* synthetic */ void lambda$onActionsReady$0$GlobalScreenshot$4(final List list, final List list2) {
                if (GlobalScreenshot.this.mScreenshotAnimation == null || !GlobalScreenshot.this.mScreenshotAnimation.isRunning()) {
                    GlobalScreenshot.this.createScreenshotActionsShadeAnimation(list, list2).start();
                } else {
                    GlobalScreenshot.this.mScreenshotAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            GlobalScreenshot.this.createScreenshotActionsShadeAnimation(list, list2).start();
                        }
                    });
                }
            }
        });
        this.mScreenshotHandler.post(new Runnable() {
            public final void run() {
                GlobalScreenshot.this.lambda$startAnimation$2$GlobalScreenshot();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startAnimation$2 */
    public /* synthetic */ void lambda$startAnimation$2$GlobalScreenshot() {
        this.mCameraSound.play(0);
        this.mScreenshotView.setLayerType(2, null);
        this.mScreenshotView.buildLayer();
        this.mScreenshotAnimation.start();
    }

    private AnimatorSet createScreenshotDropInAnimation(int i, int i2, Rect rect) {
        float f = (float) i;
        float f2 = this.mCornerSizeX / f;
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(133);
        ofFloat.setInterpolator(this.mFastOutSlowIn);
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GlobalScreenshot.this.lambda$createScreenshotDropInAnimation$3$GlobalScreenshot(valueAnimator);
            }
        });
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        ofFloat2.setDuration(217);
        ofFloat2.setInterpolator(this.mFastOutSlowIn);
        ofFloat2.addUpdateListener(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GlobalScreenshot.this.lambda$createScreenshotDropInAnimation$4$GlobalScreenshot(valueAnimator);
            }
        });
        PointF pointF = new PointF((float) rect.centerX(), (float) rect.centerY());
        int i3 = i2;
        PointF pointF2 = new PointF(this.mScreenshotOffsetXPx + ((f * f2) / 2.0f), (((float) this.mDisplayMetrics.heightPixels) - this.mScreenshotOffsetYPx) - ((((float) i3) * f2) / 2.0f));
        ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat3.setDuration(500);
        $$Lambda$GlobalScreenshot$iMnnKeE2VoCJit2kgHvxMtO653Y r16 = r0;
        ValueAnimator valueAnimator = ofFloat3;
        $$Lambda$GlobalScreenshot$iMnnKeE2VoCJit2kgHvxMtO653Y r0 = new AnimatorUpdateListener(0.468f, f2, 0.468f, pointF, pointF2, i, i3) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ PointF f$4;
            public final /* synthetic */ PointF f$5;
            public final /* synthetic */ int f$6;
            public final /* synthetic */ int f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GlobalScreenshot.this.lambda$createScreenshotDropInAnimation$5$GlobalScreenshot(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, valueAnimator);
            }
        };
        ValueAnimator valueAnimator2 = valueAnimator;
        valueAnimator2.addUpdateListener(r16);
        valueAnimator2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                GlobalScreenshot.this.mScreenshotView.setVisibility(0);
            }
        });
        this.mScreenshotFlash.setAlpha(0.0f);
        this.mScreenshotFlash.setVisibility(0);
        animatorSet.play(ofFloat2).after(ofFloat);
        animatorSet.play(ofFloat2).with(valueAnimator2);
        final float f3 = f2;
        final PointF pointF3 = pointF2;
        final int i4 = i;
        final int i5 = i2;
        C10586 r02 = new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                GlobalScreenshot.this.mScreenshotView.setScaleX(f3);
                GlobalScreenshot.this.mScreenshotView.setScaleY(f3);
                GlobalScreenshot.this.mScreenshotView.setX(pointF3.x - ((((float) i4) * f3) / 2.0f));
                GlobalScreenshot.this.mScreenshotView.setY(pointF3.y - ((((float) i5) * f3) / 2.0f));
                Rect rect = new Rect();
                GlobalScreenshot.this.mScreenshotView.getBoundsOnScreen(rect);
                GlobalScreenshot.this.mDismissButton.setX(((float) rect.right) - (GlobalScreenshot.this.mDismissButtonSize / 2.0f));
                GlobalScreenshot.this.mDismissButton.setY(((float) rect.top) - (GlobalScreenshot.this.mDismissButtonSize / 2.0f));
                GlobalScreenshot.this.mDismissButton.setVisibility(0);
            }
        };
        animatorSet.addListener(r02);
        return animatorSet;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createScreenshotDropInAnimation$3 */
    public /* synthetic */ void lambda$createScreenshotDropInAnimation$3$GlobalScreenshot(ValueAnimator valueAnimator) {
        this.mScreenshotFlash.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createScreenshotDropInAnimation$4 */
    public /* synthetic */ void lambda$createScreenshotDropInAnimation$4$GlobalScreenshot(ValueAnimator valueAnimator) {
        this.mScreenshotFlash.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createScreenshotDropInAnimation$5 */
    public /* synthetic */ void lambda$createScreenshotDropInAnimation$5$GlobalScreenshot(float f, float f2, float f3, PointF pointF, PointF pointF2, int i, int i2, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (animatedFraction < f) {
            float lerp = MathUtils.lerp(1.0f, f2, this.mFastOutSlowIn.getInterpolation(animatedFraction / f));
            this.mScreenshotView.setScaleX(lerp);
            this.mScreenshotView.setScaleY(lerp);
        } else {
            this.mScreenshotView.setScaleX(f2);
            this.mScreenshotView.setScaleY(f2);
        }
        if (animatedFraction < f3) {
            float lerp2 = MathUtils.lerp(pointF.x, pointF2.x, this.mFastOutSlowIn.getInterpolation(animatedFraction / f3));
            ImageView imageView = this.mScreenshotView;
            imageView.setX(lerp2 - ((((float) i) * imageView.getScaleX()) / 2.0f));
        } else {
            ImageView imageView2 = this.mScreenshotView;
            imageView2.setX(pointF2.x - ((((float) i) * imageView2.getScaleX()) / 2.0f));
        }
        float lerp3 = MathUtils.lerp(pointF.y, pointF2.y, this.mFastOutSlowIn.getInterpolation(animatedFraction));
        ImageView imageView3 = this.mScreenshotView;
        imageView3.setY(lerp3 - ((((float) i2) * imageView3.getScaleY()) / 2.0f));
    }

    /* access modifiers changed from: private */
    public ValueAnimator createScreenshotActionsShadeAnimation(List<Action> list, List<Action> list2) {
        LayoutInflater from = LayoutInflater.from(this.mContext);
        this.mActionsView.removeAllViews();
        this.mActionsContainer.setScrollX(0);
        this.mScreenshotLayout.invalidate();
        this.mScreenshotLayout.requestLayout();
        this.mScreenshotLayout.getViewTreeObserver().dispatchOnGlobalLayout();
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
        for (Action action : list) {
            ScreenshotActionChip screenshotActionChip = (ScreenshotActionChip) from.inflate(C2013R$layout.global_screenshot_action_chip, this.mActionsView, false);
            screenshotActionChip.setText(action.title);
            screenshotActionChip.setIcon(action.getIcon(), false);
            screenshotActionChip.setPendingIntent(action.actionIntent, new Runnable() {
                public final void run() {
                    GlobalScreenshot.this.lambda$createScreenshotActionsShadeAnimation$6$GlobalScreenshot();
                }
            });
            this.mActionsView.addView(screenshotActionChip);
        }
        for (Action action2 : list2) {
            ScreenshotActionChip screenshotActionChip2 = (ScreenshotActionChip) from.inflate(C2013R$layout.global_screenshot_action_chip, this.mActionsView, false);
            screenshotActionChip2.setText(action2.title);
            screenshotActionChip2.setIcon(action2.getIcon(), true);
            screenshotActionChip2.setPendingIntent(action2.actionIntent, new Runnable() {
                public final void run() {
                    GlobalScreenshot.this.lambda$createScreenshotActionsShadeAnimation$7$GlobalScreenshot();
                }
            });
            if (action2.actionIntent.getIntent().getAction().equals("android.intent.action.EDIT")) {
                this.mScreenshotView.setOnClickListener(new OnClickListener(action2) {
                    public final /* synthetic */ Action f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        GlobalScreenshot.this.lambda$createScreenshotActionsShadeAnimation$8$GlobalScreenshot(this.f$1, view);
                    }
                });
            }
            this.mActionsView.addView(screenshotActionChip2);
        }
        if (DeviceConfig.getBoolean("systemui", "enable_screenshot_scrolling", false)) {
            ScreenshotActionChip screenshotActionChip3 = (ScreenshotActionChip) from.inflate(C2013R$layout.global_screenshot_action_chip, this.mActionsView, false);
            Toast makeText = Toast.makeText(this.mContext, "Not implemented", 0);
            screenshotActionChip3.setText("Extend");
            screenshotActionChip3.setIcon(Icon.createWithResource(this.mContext, C2010R$drawable.ic_arrow_downward), true);
            screenshotActionChip3.setOnClickListener(new OnClickListener(makeText) {
                public final /* synthetic */ Toast f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(View view) {
                    this.f$0.show();
                }
            });
            this.mActionsView.addView(screenshotActionChip3);
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mActionsContainer.setY((float) this.mDisplayMetrics.heightPixels);
        this.mActionsContainer.setVisibility(0);
        this.mActionsContainer.measure(0, 0);
        ofFloat.addUpdateListener(new AnimatorUpdateListener(((float) this.mActionsContainer.getMeasuredHeight()) + this.mScreenshotHeightPx) {
            public final /* synthetic */ float f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GlobalScreenshot.this.lambda$createScreenshotActionsShadeAnimation$10$GlobalScreenshot(this.f$1, valueAnimator);
            }
        });
        return ofFloat;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createScreenshotActionsShadeAnimation$6 */
    public /* synthetic */ void lambda$createScreenshotActionsShadeAnimation$6$GlobalScreenshot() {
        clearScreenshot("chip tapped");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createScreenshotActionsShadeAnimation$7 */
    public /* synthetic */ void lambda$createScreenshotActionsShadeAnimation$7$GlobalScreenshot() {
        clearScreenshot("chip tapped");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createScreenshotActionsShadeAnimation$8 */
    public /* synthetic */ void lambda$createScreenshotActionsShadeAnimation$8$GlobalScreenshot(Action action, View view) {
        try {
            action.actionIntent.send();
            clearScreenshot("screenshot preview tapped");
        } catch (CanceledException e) {
            Log.e("GlobalScreenshot", "Intent cancelled", e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createScreenshotActionsShadeAnimation$10 */
    public /* synthetic */ void lambda$createScreenshotActionsShadeAnimation$10$GlobalScreenshot(float f, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        this.mBackgroundProtection.setAlpha(animatedFraction);
        this.mActionsContainer.setY(((float) this.mDisplayMetrics.heightPixels) - (f * animatedFraction));
    }
}
