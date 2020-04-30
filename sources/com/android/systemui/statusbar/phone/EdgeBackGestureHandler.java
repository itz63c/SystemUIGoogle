package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.hardware.input.InputManager;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.ISystemGestureExclusionListener;
import android.view.ISystemGestureExclusionListener.Stub;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputMonitor;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import com.android.internal.policy.GestureNavigationSettingsObserver;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.model.SysUiState;
import com.android.systemui.model.SysUiState.SysUiStateCallback;
import com.android.systemui.plugins.NavigationEdgeBackPlugin;
import com.android.systemui.plugins.NavigationEdgeBackPlugin.BackCallback;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.tracing.nano.EdgeBackGestureHandlerProto;
import com.android.systemui.tracing.nano.SystemUiTraceProto;
import java.io.PrintWriter;
import java.util.concurrent.Executor;

public class EdgeBackGestureHandler implements DisplayListener, PluginListener<NavigationEdgeBackPlugin>, ProtoTraceable<SystemUiTraceProto> {
    private static final int MAX_LONG_PRESS_TIMEOUT = SystemProperties.getInt("gestures.back_timeout", 250);
    private boolean mAllowGesture = false;
    private final BackCallback mBackCallback = new BackCallback() {
        public void triggerBack() {
            EdgeBackGestureHandler.this.sendEvent(0, 4);
            int i = 1;
            EdgeBackGestureHandler.this.sendEvent(1, 4);
            EdgeBackGestureHandler.this.mOverviewProxyService.notifyBackAction(true, (int) EdgeBackGestureHandler.this.mDownPoint.x, (int) EdgeBackGestureHandler.this.mDownPoint.y, false, !EdgeBackGestureHandler.this.mIsOnLeftEdge);
            int i2 = EdgeBackGestureHandler.this.mInRejectedExclusion ? 2 : 1;
            int i3 = (int) EdgeBackGestureHandler.this.mDownPoint.y;
            if (!EdgeBackGestureHandler.this.mIsOnLeftEdge) {
                i = 2;
            }
            SysUiStatsLog.write(224, i2, i3, i);
        }

        public void cancelBack() {
            int i = 1;
            EdgeBackGestureHandler.this.mOverviewProxyService.notifyBackAction(false, (int) EdgeBackGestureHandler.this.mDownPoint.x, (int) EdgeBackGestureHandler.this.mDownPoint.y, false, !EdgeBackGestureHandler.this.mIsOnLeftEdge);
            int i2 = (int) EdgeBackGestureHandler.this.mDownPoint.y;
            if (!EdgeBackGestureHandler.this.mIsOnLeftEdge) {
                i = 2;
            }
            SysUiStatsLog.write(224, 4, i2, i);
        }
    };
    private int mBottomGestureHeight;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final int mDisplayId;
    private final Point mDisplaySize = new Point();
    /* access modifiers changed from: private */
    public final PointF mDownPoint = new PointF();
    private NavigationEdgeBackPlugin mEdgeBackPlugin;
    private int mEdgeWidthLeft;
    private int mEdgeWidthRight;
    /* access modifiers changed from: private */
    public final Region mExcludeRegion = new Region();
    private ISystemGestureExclusionListener mGestureExclusionListener = new Stub() {
        public void onSystemGestureExclusionChanged(int i, Region region, Region region2) {
            if (i == EdgeBackGestureHandler.this.mDisplayId) {
                EdgeBackGestureHandler.this.mMainExecutor.execute(new Runnable(region, region2) {
                    public final /* synthetic */ Region f$1;
                    public final /* synthetic */ Region f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        C14531.this.mo17084x7f99a266(this.f$1, this.f$2);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSystemGestureExclusionChanged$0 */
        public /* synthetic */ void mo17084x7f99a266(Region region, Region region2) {
            EdgeBackGestureHandler.this.mExcludeRegion.set(region);
            Region access$300 = EdgeBackGestureHandler.this.mUnrestrictedExcludeRegion;
            if (region2 != null) {
                region = region2;
            }
            access$300.set(region);
        }
    };
    private final GestureNavigationSettingsObserver mGestureNavigationSettingsObserver;
    /* access modifiers changed from: private */
    public boolean mInRejectedExclusion = false;
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private boolean mIsAttached;
    private boolean mIsEnabled;
    private boolean mIsGesturalModeEnabled;
    private boolean mIsNavBarShownTransiently;
    /* access modifiers changed from: private */
    public boolean mIsOnLeftEdge;
    private int mLeftInset;
    private final int mLongPressTimeout;
    /* access modifiers changed from: private */
    public final Executor mMainExecutor;
    /* access modifiers changed from: private */
    public final OverviewProxyService mOverviewProxyService;
    private PluginManager mPluginManager;
    private int mRightInset;
    private int mSysUiFlags;
    private boolean mThresholdCrossed = false;
    private final float mTouchSlop;
    /* access modifiers changed from: private */
    public final Region mUnrestrictedExcludeRegion = new Region();

    class SysUiInputEventReceiver extends InputEventReceiver {
        SysUiInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        public void onInputEvent(InputEvent inputEvent) {
            EdgeBackGestureHandler.this.onInputEvent(inputEvent);
            finishInputEvent(inputEvent, true);
        }
    }

    public void onDisplayAdded(int i) {
    }

    public void onDisplayRemoved(int i) {
    }

    public EdgeBackGestureHandler(Context context, OverviewProxyService overviewProxyService, SysUiState sysUiState, PluginManager pluginManager) {
        Resources resources = context.getResources();
        this.mContext = context;
        this.mDisplayId = context.getDisplayId();
        this.mMainExecutor = context.getMainExecutor();
        this.mOverviewProxyService = overviewProxyService;
        this.mPluginManager = pluginManager;
        ((ProtoTracer) Dependency.get(ProtoTracer.class)).add(this);
        this.mTouchSlop = ((float) ViewConfiguration.get(context).getScaledTouchSlop()) * 0.75f;
        this.mLongPressTimeout = Math.min(MAX_LONG_PRESS_TIMEOUT, ViewConfiguration.getLongPressTimeout());
        this.mGestureNavigationSettingsObserver = new GestureNavigationSettingsObserver(this.mContext.getMainThreadHandler(), this.mContext, new Runnable(resources) {
            public final /* synthetic */ Resources f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                EdgeBackGestureHandler.this.lambda$new$0$EdgeBackGestureHandler(this.f$1);
            }
        });
        lambda$new$0(resources);
        sysUiState.addCallback(new SysUiStateCallback() {
            public final void onSystemUiStateChanged(int i) {
                EdgeBackGestureHandler.this.lambda$new$1$EdgeBackGestureHandler(i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$EdgeBackGestureHandler(int i) {
        this.mSysUiFlags = i;
    }

    /* renamed from: updateCurrentUserResources */
    public void lambda$new$0(Resources resources) {
        this.mEdgeWidthLeft = this.mGestureNavigationSettingsObserver.getLeftSensitivity(resources);
        this.mEdgeWidthRight = this.mGestureNavigationSettingsObserver.getRightSensitivity(resources);
        this.mBottomGestureHeight = resources.getDimensionPixelSize(17105314);
    }

    public void onNavBarAttached() {
        this.mIsAttached = true;
        updateIsEnabled();
    }

    public void onNavBarDetached() {
        this.mIsAttached = false;
        updateIsEnabled();
    }

    public void onNavigationModeChanged(int i, Context context) {
        this.mIsGesturalModeEnabled = QuickStepContract.isGesturalMode(i);
        updateIsEnabled();
        lambda$new$0(context.getResources());
    }

    public void onNavBarTransientStateChanged(boolean z) {
        this.mIsNavBarShownTransiently = z;
    }

    private void disposeInputChannel() {
        InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }

    private void updateIsEnabled() {
        boolean z = this.mIsAttached && this.mIsGesturalModeEnabled;
        if (z != this.mIsEnabled) {
            this.mIsEnabled = z;
            disposeInputChannel();
            NavigationEdgeBackPlugin navigationEdgeBackPlugin = this.mEdgeBackPlugin;
            if (navigationEdgeBackPlugin != null) {
                navigationEdgeBackPlugin.onDestroy();
                this.mEdgeBackPlugin = null;
            }
            String str = "EdgeBackGestureHandler";
            if (!this.mIsEnabled) {
                this.mGestureNavigationSettingsObserver.unregister();
                ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).unregisterDisplayListener(this);
                this.mPluginManager.removePluginListener(this);
                try {
                    WindowManagerGlobal.getWindowManagerService().unregisterSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                } catch (RemoteException e) {
                    Log.e(str, "Failed to unregister window manager callbacks", e);
                }
            } else {
                this.mGestureNavigationSettingsObserver.register();
                updateDisplaySize();
                ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).registerDisplayListener(this, this.mContext.getMainThreadHandler());
                try {
                    WindowManagerGlobal.getWindowManagerService().registerSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                } catch (RemoteException e2) {
                    Log.e(str, "Failed to register window manager callbacks", e2);
                }
                this.mInputMonitor = InputManager.getInstance().monitorGestureInput("edge-swipe", this.mDisplayId);
                this.mInputEventReceiver = new SysUiInputEventReceiver(this.mInputMonitor.getInputChannel(), Looper.getMainLooper());
                setEdgeBackPlugin(new NavigationBarEdgePanel(this.mContext));
                this.mPluginManager.addPluginListener((PluginListener<T>) this, NavigationEdgeBackPlugin.class, false);
            }
        }
    }

    public void onPluginConnected(NavigationEdgeBackPlugin navigationEdgeBackPlugin, Context context) {
        setEdgeBackPlugin(navigationEdgeBackPlugin);
    }

    public void onPluginDisconnected(NavigationEdgeBackPlugin navigationEdgeBackPlugin) {
        setEdgeBackPlugin(new NavigationBarEdgePanel(this.mContext));
    }

    private void setEdgeBackPlugin(NavigationEdgeBackPlugin navigationEdgeBackPlugin) {
        NavigationEdgeBackPlugin navigationEdgeBackPlugin2 = this.mEdgeBackPlugin;
        if (navigationEdgeBackPlugin2 != null) {
            navigationEdgeBackPlugin2.onDestroy();
        }
        this.mEdgeBackPlugin = navigationEdgeBackPlugin;
        navigationEdgeBackPlugin.setBackCallback(this.mBackCallback);
        this.mEdgeBackPlugin.setLayoutParams(createLayoutParams());
        updateDisplaySize();
    }

    private LayoutParams createLayoutParams() {
        Resources resources = this.mContext.getResources();
        LayoutParams layoutParams = new LayoutParams(resources.getDimensionPixelSize(C2009R$dimen.navigation_edge_panel_width), resources.getDimensionPixelSize(C2009R$dimen.navigation_edge_panel_height), 2024, 8388904, -3);
        layoutParams.privateFlags |= 16;
        StringBuilder sb = new StringBuilder();
        sb.append("EdgeBackGestureHandler");
        sb.append(this.mContext.getDisplayId());
        layoutParams.setTitle(sb.toString());
        layoutParams.accessibilityTitle = this.mContext.getString(C2017R$string.nav_bar_edge_panel);
        layoutParams.windowAnimations = 0;
        layoutParams.setFitInsetsTypes(0);
        return layoutParams;
    }

    /* access modifiers changed from: private */
    public void onInputEvent(InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            onMotionEvent((MotionEvent) inputEvent);
        }
    }

    private boolean isWithinTouchRegion(int i, int i2) {
        if ((i > this.mEdgeWidthLeft + this.mLeftInset && i < (this.mDisplaySize.x - this.mEdgeWidthRight) - this.mRightInset) || i2 >= this.mDisplaySize.y - this.mBottomGestureHeight) {
            return false;
        }
        if (this.mIsNavBarShownTransiently) {
            return true;
        }
        boolean contains = this.mExcludeRegion.contains(i, i2);
        if (contains) {
            this.mOverviewProxyService.notifyBackAction(false, -1, -1, false, !this.mIsOnLeftEdge);
            SysUiStatsLog.write(224, 3, i2, this.mIsOnLeftEdge ? 1 : 2);
        } else {
            this.mInRejectedExclusion = this.mUnrestrictedExcludeRegion.contains(i, i2);
        }
        return !contains;
    }

    private void cancelGesture(MotionEvent motionEvent) {
        this.mAllowGesture = false;
        this.mInRejectedExclusion = false;
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        obtain.setAction(3);
        this.mEdgeBackPlugin.onMotionEvent(obtain);
        obtain.recycle();
    }

    private void onMotionEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        boolean z = true;
        if (actionMasked == 0) {
            this.mIsOnLeftEdge = motionEvent.getX() <= ((float) (this.mEdgeWidthLeft + this.mLeftInset));
            this.mInRejectedExclusion = false;
            if (QuickStepContract.isBackGestureDisabled(this.mSysUiFlags) || !isWithinTouchRegion((int) motionEvent.getX(), (int) motionEvent.getY())) {
                z = false;
            }
            this.mAllowGesture = z;
            if (z) {
                this.mEdgeBackPlugin.setIsLeftPanel(this.mIsOnLeftEdge);
                this.mEdgeBackPlugin.onMotionEvent(motionEvent);
                this.mDownPoint.set(motionEvent.getX(), motionEvent.getY());
                this.mThresholdCrossed = false;
            }
        } else if (this.mAllowGesture) {
            if (!this.mThresholdCrossed) {
                if (actionMasked == 5) {
                    cancelGesture(motionEvent);
                    return;
                } else if (actionMasked == 2) {
                    if (motionEvent.getEventTime() - motionEvent.getDownTime() > ((long) this.mLongPressTimeout)) {
                        cancelGesture(motionEvent);
                        return;
                    }
                    float abs = Math.abs(motionEvent.getX() - this.mDownPoint.x);
                    float abs2 = Math.abs(motionEvent.getY() - this.mDownPoint.y);
                    if (abs2 > abs && abs2 > this.mTouchSlop) {
                        cancelGesture(motionEvent);
                        return;
                    } else if (abs > abs2 && abs > this.mTouchSlop) {
                        this.mThresholdCrossed = true;
                        this.mInputMonitor.pilferPointers();
                    }
                }
            }
            this.mEdgeBackPlugin.onMotionEvent(motionEvent);
        }
        ((ProtoTracer) Dependency.get(ProtoTracer.class)).update();
    }

    public void onDisplayChanged(int i) {
        if (i == this.mDisplayId) {
            updateDisplaySize();
        }
    }

    private void updateDisplaySize() {
        this.mContext.getDisplay().getRealSize(this.mDisplaySize);
        NavigationEdgeBackPlugin navigationEdgeBackPlugin = this.mEdgeBackPlugin;
        if (navigationEdgeBackPlugin != null) {
            navigationEdgeBackPlugin.setDisplaySize(this.mDisplaySize);
        }
    }

    /* access modifiers changed from: private */
    public void sendEvent(int i, int i2) {
        long uptimeMillis = SystemClock.uptimeMillis();
        KeyEvent keyEvent = new KeyEvent(uptimeMillis, uptimeMillis, i, i2, 0, 0, -1, 0, 72, 257);
        int expandedDisplayId = ((BubbleController) Dependency.get(BubbleController.class)).getExpandedDisplayId(this.mContext);
        if (i2 == 4 && expandedDisplayId != -1) {
            keyEvent.setDisplayId(expandedDisplayId);
        }
        InputManager.getInstance().injectInputEvent(keyEvent, 0);
    }

    public void setInsets(int i, int i2) {
        this.mLeftInset = i;
        this.mRightInset = i2;
        NavigationEdgeBackPlugin navigationEdgeBackPlugin = this.mEdgeBackPlugin;
        if (navigationEdgeBackPlugin != null) {
            navigationEdgeBackPlugin.setInsets(i, i2);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("EdgeBackGestureHandler:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mIsEnabled=");
        sb.append(this.mIsEnabled);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mAllowGesture=");
        sb2.append(this.mAllowGesture);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mInRejectedExclusion");
        sb3.append(this.mInRejectedExclusion);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mExcludeRegion=");
        sb4.append(this.mExcludeRegion);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  mUnrestrictedExcludeRegion=");
        sb5.append(this.mUnrestrictedExcludeRegion);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  mIsAttached=");
        sb6.append(this.mIsAttached);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("  mEdgeWidthLeft=");
        sb7.append(this.mEdgeWidthLeft);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append("  mEdgeWidthRight=");
        sb8.append(this.mEdgeWidthRight);
        printWriter.println(sb8.toString());
    }

    public void writeToProto(SystemUiTraceProto systemUiTraceProto) {
        if (systemUiTraceProto.edgeBackGestureHandler == null) {
            systemUiTraceProto.edgeBackGestureHandler = new EdgeBackGestureHandlerProto();
        }
        systemUiTraceProto.edgeBackGestureHandler.allowGesture = this.mAllowGesture;
    }
}
