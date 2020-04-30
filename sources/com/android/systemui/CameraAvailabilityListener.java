package com.android.systemui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager.AvailabilityCallback;
import android.util.PathParser;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CameraAvailabilityListener.kt */
public final class CameraAvailabilityListener {
    public static final Factory Factory = new Factory(null);
    private final AvailabilityCallback availabilityCallback = new CameraAvailabilityListener$availabilityCallback$1(this);
    private final CameraManager cameraManager;
    private Rect cutoutBounds = new Rect();
    private final Path cutoutProtectionPath;
    private final Executor executor;
    private final List<CameraTransitionCallback> listeners = new ArrayList();
    /* access modifiers changed from: private */
    public final String targetCameraId;

    /* compiled from: CameraAvailabilityListener.kt */
    public interface CameraTransitionCallback {
        void onApplyCameraProtection(Path path, Rect rect);

        void onHideCameraProtection();
    }

    /* compiled from: CameraAvailabilityListener.kt */
    public static final class Factory {
        private Factory() {
        }

        public /* synthetic */ Factory(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final CameraAvailabilityListener build(Context context, Executor executor) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(executor, "executor");
            Object systemService = context.getSystemService("camera");
            if (systemService != null) {
                CameraManager cameraManager = (CameraManager) systemService;
                Resources resources = context.getResources();
                String string = resources.getString(C2017R$string.config_frontBuiltInDisplayCutoutProtection);
                String string2 = resources.getString(C2017R$string.config_protectedCameraId);
                Intrinsics.checkExpressionValueIsNotNull(string, "pathString");
                Path pathFromString = pathFromString(string);
                Intrinsics.checkExpressionValueIsNotNull(string2, "cameraId");
                return new CameraAvailabilityListener(cameraManager, pathFromString, string2, executor);
            }
            throw new TypeCastException("null cannot be cast to non-null type android.hardware.camera2.CameraManager");
        }

        private final Path pathFromString(String str) {
            if (str != null) {
                try {
                    Path createPathFromPathData = PathParser.createPathFromPathData(StringsKt__StringsKt.trim(str).toString());
                    Intrinsics.checkExpressionValueIsNotNull(createPathFromPathData, "PathParser.createPathFromPathData(spec)");
                    return createPathFromPathData;
                } catch (Throwable th) {
                    throw new IllegalArgumentException("Invalid protection path", th);
                }
            } else {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
            }
        }
    }

    public CameraAvailabilityListener(CameraManager cameraManager2, Path path, String str, Executor executor2) {
        Intrinsics.checkParameterIsNotNull(cameraManager2, "cameraManager");
        Intrinsics.checkParameterIsNotNull(path, "cutoutProtectionPath");
        Intrinsics.checkParameterIsNotNull(str, "targetCameraId");
        Intrinsics.checkParameterIsNotNull(executor2, "executor");
        this.cameraManager = cameraManager2;
        this.cutoutProtectionPath = path;
        this.targetCameraId = str;
        this.executor = executor2;
        RectF rectF = new RectF();
        this.cutoutProtectionPath.computeBounds(rectF, false);
        this.cutoutBounds.set(MathKt__MathJVMKt.roundToInt(rectF.left), MathKt__MathJVMKt.roundToInt(rectF.top), MathKt__MathJVMKt.roundToInt(rectF.right), MathKt__MathJVMKt.roundToInt(rectF.bottom));
    }

    public final void startListening() {
        registerCameraListener();
    }

    public final void addTransitionCallback(CameraTransitionCallback cameraTransitionCallback) {
        Intrinsics.checkParameterIsNotNull(cameraTransitionCallback, "callback");
        this.listeners.add(cameraTransitionCallback);
    }

    private final void registerCameraListener() {
        this.cameraManager.registerAvailabilityCallback(this.executor, this.availabilityCallback);
    }

    /* access modifiers changed from: private */
    public final void notifyCameraActive() {
        for (CameraTransitionCallback onApplyCameraProtection : this.listeners) {
            onApplyCameraProtection.onApplyCameraProtection(this.cutoutProtectionPath, this.cutoutBounds);
        }
    }

    /* access modifiers changed from: private */
    public final void notifyCameraInactive() {
        for (CameraTransitionCallback onHideCameraProtection : this.listeners) {
            onHideCameraProtection.onHideCameraProtection();
        }
    }
}
