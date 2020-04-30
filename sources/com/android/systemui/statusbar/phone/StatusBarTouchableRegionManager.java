package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.WindowInsets;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dumpable;
import com.android.systemui.ScreenDecorations.DisplayCutoutView;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.bubbles.BubbleController.BubbleStateChangeListener;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone.OnHeadsUpPhoneListenerChange;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController.ForcePluginOpenListener;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public final class StatusBarTouchableRegionManager implements Dumpable {
    private final BubbleController mBubbleController;
    private final Context mContext;
    private int mDisplayCutoutTouchableRegionSize;
    /* access modifiers changed from: private */
    public boolean mForceCollapsedUntilLayout = false;
    private final HeadsUpManagerPhone mHeadsUpManager;
    /* access modifiers changed from: private */
    public boolean mIsStatusBarExpanded = false;
    /* access modifiers changed from: private */
    public View mNotificationPanelView;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private View mNotificationShadeWindowView;
    private final OnComputeInternalInsetsListener mOnComputeInternalInsetsListener = new OnComputeInternalInsetsListener() {
        public void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo) {
            if (!StatusBarTouchableRegionManager.this.mIsStatusBarExpanded && !StatusBarTouchableRegionManager.this.mStatusBar.isBouncerShowing()) {
                internalInsetsInfo.setTouchableInsets(3);
                internalInsetsInfo.touchableRegion.set(StatusBarTouchableRegionManager.this.calculateTouchableRegion());
            }
        }
    };
    private boolean mShouldAdjustInsets = false;
    /* access modifiers changed from: private */
    public StatusBar mStatusBar;
    private int mStatusBarHeight;
    private Region mTouchableRegion = new Region();

    public StatusBarTouchableRegionManager(Context context, NotificationShadeWindowController notificationShadeWindowController, ConfigurationController configurationController, HeadsUpManagerPhone headsUpManagerPhone, BubbleController bubbleController) {
        this.mContext = context;
        initResources();
        configurationController.addCallback(new ConfigurationListener() {
            public void onDensityOrFontScaleChanged() {
                StatusBarTouchableRegionManager.this.initResources();
            }

            public void onOverlayChanged() {
                StatusBarTouchableRegionManager.this.initResources();
            }
        });
        this.mHeadsUpManager = headsUpManagerPhone;
        headsUpManagerPhone.addListener(new OnHeadsUpChangedListener() {
            public void onHeadsUpPinnedModeChanged(boolean z) {
                String str = "TouchableRegionManager";
                if (Log.isLoggable(str, 5)) {
                    Log.w(str, "onHeadsUpPinnedModeChanged");
                }
                StatusBarTouchableRegionManager.this.updateTouchableRegion();
            }
        });
        this.mHeadsUpManager.addHeadsUpPhoneListener(new OnHeadsUpPhoneListenerChange() {
            public void onHeadsUpGoingAwayStateChanged(boolean z) {
                if (!z) {
                    StatusBarTouchableRegionManager.this.updateTouchableRegionAfterLayout();
                } else {
                    StatusBarTouchableRegionManager.this.updateTouchableRegion();
                }
            }
        });
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        notificationShadeWindowController.setForcePluginOpenListener(new ForcePluginOpenListener() {
            public final void onChange(boolean z) {
                StatusBarTouchableRegionManager.this.lambda$new$0$StatusBarTouchableRegionManager(z);
            }
        });
        this.mBubbleController = bubbleController;
        bubbleController.setBubbleStateChangeListener(new BubbleStateChangeListener() {
            public final void onHasBubblesChanged(boolean z) {
                StatusBarTouchableRegionManager.this.lambda$new$1$StatusBarTouchableRegionManager(z);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$StatusBarTouchableRegionManager(boolean z) {
        updateTouchableRegion();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$StatusBarTouchableRegionManager(boolean z) {
        updateTouchableRegion();
    }

    /* access modifiers changed from: protected */
    public void setup(StatusBar statusBar, View view) {
        this.mStatusBar = statusBar;
        this.mNotificationShadeWindowView = view;
        this.mNotificationPanelView = view.findViewById(C2011R$id.notification_panel);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("StatusBarTouchableRegionManager state:");
        printWriter.print("  mTouchableRegion=");
        printWriter.println(this.mTouchableRegion);
    }

    /* access modifiers changed from: 0000 */
    public void setPanelExpanded(boolean z) {
        if (z != this.mIsStatusBarExpanded) {
            this.mIsStatusBarExpanded = z;
            if (z) {
                this.mForceCollapsedUntilLayout = false;
            }
            updateTouchableRegion();
        }
    }

    /* access modifiers changed from: 0000 */
    public Region calculateTouchableRegion() {
        Region touchableRegion = this.mHeadsUpManager.getTouchableRegion();
        if (touchableRegion != null) {
            this.mTouchableRegion.set(touchableRegion);
        } else {
            this.mTouchableRegion.set(0, 0, this.mNotificationShadeWindowView.getWidth(), this.mStatusBarHeight);
            updateRegionForNotch(this.mTouchableRegion);
        }
        Rect touchableRegion2 = this.mBubbleController.getTouchableRegion();
        if (touchableRegion2 != null) {
            this.mTouchableRegion.union(touchableRegion2);
        }
        return this.mTouchableRegion;
    }

    /* access modifiers changed from: private */
    public void initResources() {
        Resources resources = this.mContext.getResources();
        this.mDisplayCutoutTouchableRegionSize = resources.getDimensionPixelSize(17105165);
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105462);
    }

    /* access modifiers changed from: private */
    public void updateTouchableRegion() {
        View view = this.mNotificationShadeWindowView;
        boolean z = true;
        boolean z2 = (view == null || view.getRootWindowInsets() == null || this.mNotificationShadeWindowView.getRootWindowInsets().getDisplayCutout() == null) ? false : true;
        if (!this.mHeadsUpManager.hasPinnedHeadsUp() && !this.mHeadsUpManager.isHeadsUpGoingAway() && !this.mBubbleController.hasBubbles() && !this.mForceCollapsedUntilLayout && !z2 && !this.mNotificationShadeWindowController.getForcePluginOpen()) {
            z = false;
        }
        if (z != this.mShouldAdjustInsets) {
            if (z) {
                this.mNotificationShadeWindowView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
                this.mNotificationShadeWindowView.requestLayout();
            } else {
                this.mNotificationShadeWindowView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
            }
            this.mShouldAdjustInsets = z;
        }
    }

    /* access modifiers changed from: private */
    public void updateTouchableRegionAfterLayout() {
        View view = this.mNotificationPanelView;
        if (view != null) {
            this.mForceCollapsedUntilLayout = true;
            view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    if (!StatusBarTouchableRegionManager.this.mNotificationPanelView.isVisibleToUser()) {
                        StatusBarTouchableRegionManager.this.mNotificationPanelView.removeOnLayoutChangeListener(this);
                        StatusBarTouchableRegionManager.this.mForceCollapsedUntilLayout = false;
                        StatusBarTouchableRegionManager.this.updateTouchableRegion();
                    }
                }
            });
        }
    }

    private void updateRegionForNotch(Region region) {
        WindowInsets rootWindowInsets = this.mNotificationShadeWindowView.getRootWindowInsets();
        if (rootWindowInsets == null) {
            Log.w("TouchableRegionManager", "StatusBarWindowView is not attached.");
            return;
        }
        DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
        if (displayCutout != null) {
            Rect rect = new Rect();
            DisplayCutoutView.boundsFromDirection(displayCutout, 48, rect);
            rect.offset(0, this.mDisplayCutoutTouchableRegionSize);
            region.union(rect);
        }
    }
}
