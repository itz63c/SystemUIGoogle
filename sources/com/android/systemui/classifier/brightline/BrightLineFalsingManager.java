package com.android.systemui.classifier.brightline;

import android.app.ActivityManager;
import android.hardware.biometrics.BiometricSourceType;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.IndentingPrintWriter;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.StatusBarState;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.sensors.ProximitySensor.ProximityEvent;
import com.android.systemui.util.sensors.ProximitySensor.ProximitySensorListener;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BrightLineFalsingManager implements FalsingManager {
    static final boolean DEBUG = Log.isLoggable("FalsingManager", 3);
    private static final Queue<String> RECENT_INFO_LOG = new ArrayDeque(41);
    private static final Queue<DebugSwipeRecord> RECENT_SWIPES = new ArrayDeque(21);
    private final List<FalsingClassifier> mClassifiers;
    private final FalsingDataProvider mDataProvider;
    private final DockManager mDockManager;
    private int mIsFalseTouchCalls;
    /* access modifiers changed from: private */
    public boolean mJustUnlockedWithFace;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            if (i == KeyguardUpdateMonitor.getCurrentUser() && biometricSourceType == BiometricSourceType.FACE) {
                BrightLineFalsingManager.this.mJustUnlockedWithFace = true;
            }
        }
    };
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private MetricsLogger mMetricsLogger;
    private boolean mPreviousResult = false;
    private final ProximitySensor mProximitySensor;
    private boolean mScreenOn;
    private ProximitySensorListener mSensorEventListener = new ProximitySensorListener() {
        public final void onSensorEvent(ProximityEvent proximityEvent) {
            BrightLineFalsingManager.this.onProximityEvent(proximityEvent);
        }
    };
    private boolean mSessionStarted;
    private boolean mShowingAod;
    /* access modifiers changed from: private */
    public int mState;
    private final StatusBarStateController mStatusBarStateController;
    private StateListener mStatusBarStateListener = new StateListener() {
        public void onStateChanged(int i) {
            StringBuilder sb = new StringBuilder();
            sb.append("StatusBarState=");
            sb.append(StatusBarState.toShortString(i));
            BrightLineFalsingManager.logDebug(sb.toString());
            BrightLineFalsingManager.this.mState = i;
            BrightLineFalsingManager.this.updateSessionActive();
        }
    };

    private static class DebugSwipeRecord {
        private final int mInteractionType;
        private final boolean mIsFalse;
        private final List<XYDt> mRecentMotionEvents;

        DebugSwipeRecord(boolean z, int i, List<XYDt> list) {
            this.mIsFalse = z;
            this.mInteractionType = i;
            this.mRecentMotionEvents = list;
        }

        /* access modifiers changed from: 0000 */
        public String getString() {
            StringJoiner stringJoiner = new StringJoiner(",");
            stringJoiner.add(Integer.toString(1)).add(this.mIsFalse ? "1" : "0").add(Integer.toString(this.mInteractionType));
            for (XYDt xYDt : this.mRecentMotionEvents) {
                stringJoiner.add(xYDt.toString());
            }
            return stringJoiner.toString();
        }
    }

    private static class XYDt {
        private final int mDT;

        /* renamed from: mX */
        private final int f37mX;

        /* renamed from: mY */
        private final int f38mY;

        XYDt(int i, int i2, int i3) {
            this.f37mX = i;
            this.f38mY = i2;
            this.mDT = i3;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.f37mX);
            String str = ",";
            sb.append(str);
            sb.append(this.f38mY);
            sb.append(str);
            sb.append(this.mDT);
            return sb.toString();
        }
    }

    public boolean isClassifierEnabled() {
        return true;
    }

    public boolean isReportingEnabled() {
        return false;
    }

    public boolean isUnlockingDisabled() {
        return false;
    }

    public void onAffordanceSwipingAborted() {
    }

    public void onCameraHintStarted() {
    }

    public void onCameraOn() {
    }

    public void onExpansionFromPulseStopped() {
    }

    public void onLeftAffordanceHintStarted() {
    }

    public void onLeftAffordanceOn() {
    }

    public void onNotificationActive() {
    }

    public void onNotificationDismissed() {
    }

    public void onNotificationDoubleTap(boolean z, float f, float f2) {
    }

    public void onNotificatonStopDismissing() {
    }

    public void onNotificatonStopDraggingDown() {
    }

    public void onTrackingStopped() {
    }

    public void onUnlockHintStarted() {
    }

    public Uri reportRejectedTouch() {
        return null;
    }

    public void setNotificationExpanded() {
    }

    public boolean shouldEnforceBouncer() {
        return false;
    }

    public BrightLineFalsingManager(FalsingDataProvider falsingDataProvider, KeyguardUpdateMonitor keyguardUpdateMonitor, ProximitySensor proximitySensor, DeviceConfigProxy deviceConfigProxy, DockManager dockManager, StatusBarStateController statusBarStateController) {
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mDataProvider = falsingDataProvider;
        this.mProximitySensor = proximitySensor;
        this.mDockManager = dockManager;
        this.mStatusBarStateController = statusBarStateController;
        keyguardUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
        this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mState = this.mStatusBarStateController.getState();
        this.mMetricsLogger = new MetricsLogger();
        this.mClassifiers = new ArrayList();
        DistanceClassifier distanceClassifier = new DistanceClassifier(this.mDataProvider, deviceConfigProxy);
        ProximityClassifier proximityClassifier = new ProximityClassifier(distanceClassifier, this.mDataProvider, deviceConfigProxy);
        this.mClassifiers.add(new PointerCountClassifier(this.mDataProvider));
        this.mClassifiers.add(new TypeClassifier(this.mDataProvider));
        this.mClassifiers.add(new DiagonalClassifier(this.mDataProvider, deviceConfigProxy));
        this.mClassifiers.add(distanceClassifier);
        this.mClassifiers.add(proximityClassifier);
        this.mClassifiers.add(new ZigZagClassifier(this.mDataProvider, deviceConfigProxy));
    }

    private void registerSensors() {
        this.mProximitySensor.register(this.mSensorEventListener);
    }

    private void unregisterSensors() {
        this.mProximitySensor.unregister(this.mSensorEventListener);
    }

    private void sessionStart() {
        if (!this.mSessionStarted && shouldSessionBeActive()) {
            logDebug("Starting Session");
            this.mSessionStarted = true;
            this.mJustUnlockedWithFace = false;
            registerSensors();
            this.mClassifiers.forEach($$Lambda$HclOlu42IVtKALxwbwHP3Y1rdRk.INSTANCE);
        }
    }

    private void sessionEnd() {
        if (this.mSessionStarted) {
            logDebug("Ending Session");
            this.mSessionStarted = false;
            unregisterSensors();
            this.mDataProvider.onSessionEnd();
            this.mClassifiers.forEach($$Lambda$47wU6WxQ76Gt_ecwypSCrFl04Q.INSTANCE);
            int i = this.mIsFalseTouchCalls;
            if (i != 0) {
                this.mMetricsLogger.histogram("falsing_failure_after_attempts", i);
                this.mIsFalseTouchCalls = 0;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSessionActive() {
        if (shouldSessionBeActive()) {
            sessionStart();
        } else {
            sessionEnd();
        }
    }

    private boolean shouldSessionBeActive() {
        return this.mScreenOn && this.mState == 1 && !this.mShowingAod;
    }

    private void updateInteractionType(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("InteractionType: ");
        sb.append(i);
        logDebug(sb.toString());
        this.mDataProvider.setInteractionType(i);
    }

    public boolean isFalseTouch() {
        if (!this.mDataProvider.isDirty()) {
            return this.mPreviousResult;
        }
        this.mPreviousResult = !ActivityManager.isRunningInUserTestHarness() && !this.mJustUnlockedWithFace && !this.mDockManager.isDocked() && this.mClassifiers.stream().anyMatch(new Predicate(this) {
            public final /* synthetic */ BrightLineFalsingManager f$0;

            public final 
/*
Method generation error in method: com.android.systemui.classifier.brightline.-$$Lambda$BrightLineFalsingManager$a2Ll-_HVGMZ_iA7riIG6wQYElYM.test(java.lang.Object):null, dex: classes.dex
            java.lang.NullPointerException
            	at jadx.core.codegen.ClassGen.useType(ClassGen.java:442)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:109)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:311)
            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:661)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:595)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:353)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:223)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:105)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:773)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:713)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:357)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:223)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:105)
            	at jadx.core.codegen.ConditionGen.wrap(ConditionGen.java:95)
            	at jadx.core.codegen.ConditionGen.addCompare(ConditionGen.java:117)
            	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:57)
            	at jadx.core.codegen.ConditionGen.wrap(ConditionGen.java:84)
            	at jadx.core.codegen.ConditionGen.addAndOr(ConditionGen.java:151)
            	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:70)
            	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:46)
            	at jadx.core.codegen.InsnGen.makeTernary(InsnGen.java:910)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:465)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:223)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:105)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:418)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:76)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:32)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:20)
            	at jadx.core.ProcessClass.process(ProcessClass.java:36)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
            
*/
        });
        StringBuilder sb = new StringBuilder();
        sb.append("Is false touch? ");
        sb.append(this.mPreviousResult);
        logDebug(sb.toString());
        if (Build.IS_ENG || Build.IS_USERDEBUG) {
            RECENT_SWIPES.add(new DebugSwipeRecord(this.mPreviousResult, this.mDataProvider.getInteractionType(), (List) this.mDataProvider.getRecentMotionEvents().stream().map($$Lambda$BrightLineFalsingManager$CaQ6cuS9SHkQ1By76SF5W8vub7I.INSTANCE).collect(Collectors.toList())));
            while (RECENT_SWIPES.size() > 40) {
                DebugSwipeRecord debugSwipeRecord = (DebugSwipeRecord) RECENT_SWIPES.remove();
            }
        }
        return this.mPreviousResult;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$isFalseTouch$0 */
    public /* synthetic */ boolean lambda$isFalseTouch$0$BrightLineFalsingManager(FalsingClassifier falsingClassifier) {
        boolean isFalseTouch = falsingClassifier.isFalseTouch();
        if (isFalseTouch) {
            logInfo(String.format(null, "{classifier=%s, interactionType=%d}", new Object[]{falsingClassifier.getClass().getName(), Integer.valueOf(this.mDataProvider.getInteractionType())}));
            String reason = falsingClassifier.getReason();
            if (reason != null) {
                logInfo(reason);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(falsingClassifier.getClass().getName());
            sb.append(": false");
            logDebug(sb.toString());
        }
        return isFalseTouch;
    }

    static /* synthetic */ XYDt lambda$isFalseTouch$1(MotionEvent motionEvent) {
        return new XYDt((int) motionEvent.getX(), (int) motionEvent.getY(), (int) (motionEvent.getEventTime() - motionEvent.getDownTime()));
    }

    public void onTouchEvent(MotionEvent motionEvent, int i, int i2) {
        this.mDataProvider.onMotionEvent(motionEvent);
        this.mClassifiers.forEach(new Consumer(motionEvent) {
            public final /* synthetic */ MotionEvent f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((FalsingClassifier) obj).onTouchEvent(this.f$0);
            }
        });
    }

    /* access modifiers changed from: private */
    public void onProximityEvent(ProximityEvent proximityEvent) {
        this.mClassifiers.forEach(new Consumer() {
            public final void accept(Object obj) {
                ((FalsingClassifier) obj).onProximityEvent(ProximityEvent.this);
            }
        });
    }

    public void onSuccessfulUnlock() {
        int i = this.mIsFalseTouchCalls;
        if (i != 0) {
            this.mMetricsLogger.histogram("falsing_success_after_attempts", i);
            this.mIsFalseTouchCalls = 0;
        }
        sessionEnd();
    }

    public void setShowingAod(boolean z) {
        this.mShowingAod = z;
        updateSessionActive();
    }

    public void onNotificatonStartDraggingDown() {
        updateInteractionType(2);
    }

    public void onQsDown() {
        updateInteractionType(0);
    }

    public void setQsExpanded(boolean z) {
        if (z) {
            unregisterSensors();
        } else if (this.mSessionStarted) {
            registerSensors();
        }
    }

    public void onTrackingStarted(boolean z) {
        updateInteractionType(z ? 8 : 4);
    }

    public void onAffordanceSwipingStarted(boolean z) {
        updateInteractionType(z ? 6 : 5);
    }

    public void onStartExpandingFromPulse() {
        updateInteractionType(9);
    }

    public void onScreenOnFromTouch() {
        onScreenTurningOn();
    }

    public void onScreenTurningOn() {
        this.mScreenOn = true;
        updateSessionActive();
    }

    public void onScreenOff() {
        this.mScreenOn = false;
        updateSessionActive();
    }

    public void onNotificatonStartDismissing() {
        updateInteractionType(1);
    }

    public void onBouncerShown() {
        unregisterSensors();
    }

    public void onBouncerHidden() {
        if (this.mSessionStarted) {
            registerSensors();
        }
    }

    public void dump(PrintWriter printWriter) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("BRIGHTLINE FALSING MANAGER");
        indentingPrintWriter.print("classifierEnabled=");
        indentingPrintWriter.println(isClassifierEnabled() ? 1 : 0);
        indentingPrintWriter.print("mJustUnlockedWithFace=");
        indentingPrintWriter.println(this.mJustUnlockedWithFace ? 1 : 0);
        indentingPrintWriter.print("isDocked=");
        indentingPrintWriter.println(this.mDockManager.isDocked() ? 1 : 0);
        indentingPrintWriter.print("width=");
        indentingPrintWriter.println(this.mDataProvider.getWidthPixels());
        indentingPrintWriter.print("height=");
        indentingPrintWriter.println(this.mDataProvider.getHeightPixels());
        indentingPrintWriter.println();
        if (RECENT_SWIPES.size() != 0) {
            indentingPrintWriter.println("Recent swipes:");
            indentingPrintWriter.increaseIndent();
            for (DebugSwipeRecord string : RECENT_SWIPES) {
                indentingPrintWriter.println(string.getString());
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
        } else {
            indentingPrintWriter.println("No recent swipes");
        }
        indentingPrintWriter.println();
        indentingPrintWriter.println("Recent falsing info:");
        indentingPrintWriter.increaseIndent();
        for (String println : RECENT_INFO_LOG) {
            indentingPrintWriter.println(println);
        }
        indentingPrintWriter.println();
    }

    public void cleanup() {
        unregisterSensors();
        this.mKeyguardUpdateMonitor.removeCallback(this.mKeyguardUpdateCallback);
        this.mStatusBarStateController.removeCallback(this.mStatusBarStateListener);
    }

    static void logDebug(String str) {
        logDebug(str, null);
    }

    static void logDebug(String str, Throwable th) {
        if (DEBUG) {
            Log.d("FalsingManager", str, th);
        }
    }

    static void logInfo(String str) {
        Log.i("FalsingManager", str);
        RECENT_INFO_LOG.add(str);
        while (RECENT_INFO_LOG.size() > 40) {
            RECENT_INFO_LOG.remove();
        }
    }
}
