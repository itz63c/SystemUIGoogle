package com.android.systemui.statusbar;

import android.animation.Animator;
import android.app.WallpaperManager;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.View;
import android.view.animation.Interpolator;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.internal.util.IndentingPrintWriter;
import com.android.systemui.Dumpable;
import com.android.systemui.Interpolators;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.PanelExpansionListener;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationShadeDepthController.kt */
public final class NotificationShadeDepthController implements PanelExpansionListener, Dumpable {
    /* access modifiers changed from: private */
    public final BiometricUnlockController biometricUnlockController;
    /* access modifiers changed from: private */
    public View blurRoot;
    /* access modifiers changed from: private */
    public final BlurUtils blurUtils;
    private final Choreographer choreographer;
    /* access modifiers changed from: private */
    public float globalDialogVisibility;
    /* access modifiers changed from: private */
    public Animator keyguardAnimator;
    private final NotificationShadeDepthController$keyguardStateCallback$1 keyguardStateCallback = new NotificationShadeDepthController$keyguardStateCallback$1(this);
    /* access modifiers changed from: private */
    public final KeyguardStateController keyguardStateController;
    /* access modifiers changed from: private */
    public Animator notificationAnimator;
    /* access modifiers changed from: private */
    public final NotificationShadeWindowController notificationShadeWindowController;
    /* access modifiers changed from: private */
    public int pendingShadeBlurRadius = -1;
    public View root;
    /* access modifiers changed from: private */
    public int shadeBlurRadius;
    private float shadeExpansion;
    private SpringAnimation shadeSpring = new SpringAnimation(this, new NotificationShadeDepthController$shadeSpring$1(this, "shadeBlurRadius"));
    private final NotificationShadeDepthController$statusBarStateCallback$1 statusBarStateCallback = new NotificationShadeDepthController$statusBarStateCallback$1(this);
    private final StatusBarStateController statusBarStateController;
    private final FrameCallback updateBlurCallback = new NotificationShadeDepthController$updateBlurCallback$1(this);
    /* access modifiers changed from: private */
    public boolean updateScheduled;
    /* access modifiers changed from: private */
    public int wakeAndUnlockBlurRadius;
    /* access modifiers changed from: private */
    public final WallpaperManager wallpaperManager;
    /* access modifiers changed from: private */
    public final Interpolator zoomInterpolator = Interpolators.ACCELERATE_DECELERATE;

    public static /* synthetic */ void shadeSpring$annotations() {
    }

    public NotificationShadeDepthController(StatusBarStateController statusBarStateController2, BlurUtils blurUtils2, BiometricUnlockController biometricUnlockController2, KeyguardStateController keyguardStateController2, Choreographer choreographer2, WallpaperManager wallpaperManager2, NotificationShadeWindowController notificationShadeWindowController2, DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(blurUtils2, "blurUtils");
        Intrinsics.checkParameterIsNotNull(biometricUnlockController2, "biometricUnlockController");
        Intrinsics.checkParameterIsNotNull(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkParameterIsNotNull(choreographer2, "choreographer");
        Intrinsics.checkParameterIsNotNull(wallpaperManager2, "wallpaperManager");
        Intrinsics.checkParameterIsNotNull(notificationShadeWindowController2, "notificationShadeWindowController");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.statusBarStateController = statusBarStateController2;
        this.blurUtils = blurUtils2;
        this.biometricUnlockController = biometricUnlockController2;
        this.keyguardStateController = keyguardStateController2;
        this.choreographer = choreographer2;
        this.wallpaperManager = wallpaperManager2;
        this.notificationShadeWindowController = notificationShadeWindowController2;
        String name = NotificationShadeDepthController.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        this.keyguardStateController.addCallback(this.keyguardStateCallback);
        this.shadeSpring.setSpring(new SpringForce(0.0f));
        SpringForce spring = this.shadeSpring.getSpring();
        String str = "shadeSpring.spring";
        Intrinsics.checkExpressionValueIsNotNull(spring, str);
        spring.setDampingRatio(1.0f);
        SpringForce spring2 = this.shadeSpring.getSpring();
        Intrinsics.checkExpressionValueIsNotNull(spring2, str);
        spring2.setStiffness(200.0f);
        this.shadeSpring.addEndListener(new OnAnimationEndListener(this) {
            final /* synthetic */ NotificationShadeDepthController this$0;

            {
                this.this$0 = r1;
            }

            public final void onAnimationEnd(DynamicAnimation<DynamicAnimation<?>> dynamicAnimation, boolean z, float f, float f2) {
                this.this$0.pendingShadeBlurRadius = -1;
            }
        });
        this.statusBarStateController.addCallback(this.statusBarStateCallback);
    }

    public final View getRoot() {
        View view = this.root;
        if (view != null) {
            return view;
        }
        Intrinsics.throwUninitializedPropertyAccessException("root");
        throw null;
    }

    public final void setRoot(View view) {
        Intrinsics.checkParameterIsNotNull(view, "<set-?>");
        this.root = view;
    }

    public final SpringAnimation getShadeSpring() {
        return this.shadeSpring;
    }

    /* access modifiers changed from: private */
    public final void setShadeBlurRadius(int i) {
        if (this.shadeBlurRadius != i) {
            this.shadeBlurRadius = i;
            scheduleUpdate$default(this, null, 1, null);
        }
    }

    /* access modifiers changed from: private */
    public final void setWakeAndUnlockBlurRadius(int i) {
        if (this.wakeAndUnlockBlurRadius != i) {
            this.wakeAndUnlockBlurRadius = i;
            scheduleUpdate$default(this, null, 1, null);
        }
    }

    public void onPanelExpansionChanged(float f, boolean z) {
        if (f != this.shadeExpansion) {
            this.shadeExpansion = f;
            updateShadeBlur();
        }
    }

    /* access modifiers changed from: private */
    public final void updateShadeBlur() {
        int blurRadiusOfRatio = this.statusBarStateController.getState() == 0 ? this.blurUtils.blurRadiusOfRatio(this.shadeExpansion) : 0;
        if (this.pendingShadeBlurRadius != blurRadiusOfRatio) {
            this.pendingShadeBlurRadius = blurRadiusOfRatio;
            this.shadeSpring.animateToFinalPosition((float) blurRadiusOfRatio);
        }
    }

    static /* synthetic */ void scheduleUpdate$default(NotificationShadeDepthController notificationShadeDepthController, View view, int i, Object obj) {
        if ((i & 1) != 0) {
            view = null;
        }
        notificationShadeDepthController.scheduleUpdate(view);
    }

    private final void scheduleUpdate(View view) {
        if (!this.updateScheduled) {
            this.updateScheduled = true;
            this.blurRoot = view;
            this.choreographer.postFrameCallback(this.updateBlurCallback);
        }
    }

    public final void updateGlobalDialogVisibility(float f, View view) {
        Intrinsics.checkParameterIsNotNull(view, "dialogView");
        if (f != this.globalDialogVisibility) {
            this.globalDialogVisibility = f;
            scheduleUpdate(view);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("StatusBarWindowBlurController:");
        indentingPrintWriter.increaseIndent();
        StringBuilder sb = new StringBuilder();
        sb.append("shadeBlurRadius: ");
        sb.append(this.shadeBlurRadius);
        indentingPrintWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("wakeAndUnlockBlur: ");
        sb2.append(this.wakeAndUnlockBlurRadius);
        indentingPrintWriter.println(sb2.toString());
    }
}
