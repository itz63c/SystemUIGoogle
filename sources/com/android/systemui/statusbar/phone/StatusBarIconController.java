package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.C2005R$array;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.DemoMode;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.StatusBarMobileView;
import com.android.systemui.statusbar.StatusBarWifiView;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.MobileIconState;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.WifiIconState;
import com.android.systemui.util.Utils.DisableStateTracker;
import java.util.List;

public interface StatusBarIconController {

    public static class DarkIconManager extends IconManager {
        private final DarkIconDispatcher mDarkIconDispatcher = ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class));
        private int mIconHPadding = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.status_bar_icon_padding);

        public DarkIconManager(LinearLayout linearLayout, CommandQueue commandQueue) {
            super(linearLayout, commandQueue);
        }

        /* access modifiers changed from: protected */
        public void onIconAdded(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            this.mDarkIconDispatcher.addDarkReceiver((DarkReceiver) addHolder(i, str, z, statusBarIconHolder));
        }

        /* access modifiers changed from: protected */
        public LayoutParams onCreateLayoutParams() {
            LayoutParams layoutParams = new LayoutParams(-2, this.mIconSize);
            int i = this.mIconHPadding;
            layoutParams.setMargins(i, 0, i, 0);
            return layoutParams;
        }

        /* access modifiers changed from: protected */
        public void destroy() {
            for (int i = 0; i < this.mGroup.getChildCount(); i++) {
                this.mDarkIconDispatcher.removeDarkReceiver((DarkReceiver) this.mGroup.getChildAt(i));
            }
            this.mGroup.removeAllViews();
        }

        /* access modifiers changed from: protected */
        public void onRemoveIcon(int i) {
            this.mDarkIconDispatcher.removeDarkReceiver((DarkReceiver) this.mGroup.getChildAt(i));
            super.onRemoveIcon(i);
        }

        public void onSetIcon(int i, StatusBarIcon statusBarIcon) {
            super.onSetIcon(i, statusBarIcon);
            this.mDarkIconDispatcher.applyDark((DarkReceiver) this.mGroup.getChildAt(i));
        }

        /* access modifiers changed from: protected */
        public DemoStatusIcons createDemoStatusIcons() {
            DemoStatusIcons createDemoStatusIcons = super.createDemoStatusIcons();
            this.mDarkIconDispatcher.addDarkReceiver((DarkReceiver) createDemoStatusIcons);
            return createDemoStatusIcons;
        }

        /* access modifiers changed from: protected */
        public void exitDemoMode() {
            this.mDarkIconDispatcher.removeDarkReceiver((DarkReceiver) this.mDemoStatusIcons);
            super.exitDemoMode();
        }
    }

    public static class IconManager implements DemoMode {
        protected final Context mContext;
        protected DemoStatusIcons mDemoStatusIcons;
        protected boolean mDemoable = true;
        protected final ViewGroup mGroup;
        protected final int mIconSize;
        private boolean mIsInDemoMode;
        protected boolean mShouldLog = false;

        public IconManager(ViewGroup viewGroup, CommandQueue commandQueue) {
            this.mGroup = viewGroup;
            Context context = viewGroup.getContext();
            this.mContext = context;
            this.mIconSize = context.getResources().getDimensionPixelSize(17105465);
            DisableStateTracker disableStateTracker = new DisableStateTracker(0, 2, commandQueue);
            this.mGroup.addOnAttachStateChangeListener(disableStateTracker);
            if (this.mGroup.isAttachedToWindow()) {
                disableStateTracker.onViewAttachedToWindow(this.mGroup);
            }
        }

        public boolean isDemoable() {
            return this.mDemoable;
        }

        public void setShouldLog(boolean z) {
            this.mShouldLog = z;
        }

        public boolean shouldLog() {
            return this.mShouldLog;
        }

        /* access modifiers changed from: protected */
        public void onIconAdded(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            addHolder(i, str, z, statusBarIconHolder);
        }

        /* access modifiers changed from: protected */
        public StatusIconDisplayable addHolder(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            int type = statusBarIconHolder.getType();
            if (type == 0) {
                return addIcon(i, str, z, statusBarIconHolder.getIcon());
            }
            if (type == 1) {
                return addSignalIcon(i, str, statusBarIconHolder.getWifiState());
            }
            if (type != 2) {
                return null;
            }
            return addMobileIcon(i, str, statusBarIconHolder.getMobileState());
        }

        /* access modifiers changed from: protected */
        public StatusBarIconView addIcon(int i, String str, boolean z, StatusBarIcon statusBarIcon) {
            StatusBarIconView onCreateStatusBarIconView = onCreateStatusBarIconView(str, z);
            onCreateStatusBarIconView.set(statusBarIcon);
            this.mGroup.addView(onCreateStatusBarIconView, i, onCreateLayoutParams());
            return onCreateStatusBarIconView;
        }

        /* access modifiers changed from: protected */
        public StatusBarWifiView addSignalIcon(int i, String str, WifiIconState wifiIconState) {
            StatusBarWifiView onCreateStatusBarWifiView = onCreateStatusBarWifiView(str);
            onCreateStatusBarWifiView.applyWifiState(wifiIconState);
            this.mGroup.addView(onCreateStatusBarWifiView, i, onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addDemoWifiView(wifiIconState);
            }
            return onCreateStatusBarWifiView;
        }

        /* access modifiers changed from: protected */
        public StatusBarMobileView addMobileIcon(int i, String str, MobileIconState mobileIconState) {
            StatusBarMobileView onCreateStatusBarMobileView = onCreateStatusBarMobileView(str);
            onCreateStatusBarMobileView.applyMobileState(mobileIconState);
            this.mGroup.addView(onCreateStatusBarMobileView, i, onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addMobileView(mobileIconState);
            }
            return onCreateStatusBarMobileView;
        }

        private StatusBarIconView onCreateStatusBarIconView(String str, boolean z) {
            return new StatusBarIconView(this.mContext, str, null, z);
        }

        private StatusBarWifiView onCreateStatusBarWifiView(String str) {
            return StatusBarWifiView.fromContext(this.mContext, str);
        }

        private StatusBarMobileView onCreateStatusBarMobileView(String str) {
            return StatusBarMobileView.fromContext(this.mContext, str);
        }

        /* access modifiers changed from: protected */
        public LayoutParams onCreateLayoutParams() {
            return new LayoutParams(-2, this.mIconSize);
        }

        /* access modifiers changed from: protected */
        public void destroy() {
            this.mGroup.removeAllViews();
        }

        /* access modifiers changed from: protected */
        public void onIconExternal(int i, int i2) {
            ImageView imageView = (ImageView) this.mGroup.getChildAt(i);
            imageView.setScaleType(ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            setHeightAndCenter(imageView, i2);
        }

        private void setHeightAndCenter(ImageView imageView, int i) {
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.height = i;
            if (layoutParams instanceof LayoutParams) {
                ((LayoutParams) layoutParams).gravity = 16;
            }
            imageView.setLayoutParams(layoutParams);
        }

        /* access modifiers changed from: protected */
        public void onRemoveIcon(int i) {
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.onRemoveIcon((StatusIconDisplayable) this.mGroup.getChildAt(i));
            }
            this.mGroup.removeViewAt(i);
        }

        public void onSetIcon(int i, StatusBarIcon statusBarIcon) {
            ((StatusBarIconView) this.mGroup.getChildAt(i)).set(statusBarIcon);
        }

        public void onSetIconHolder(int i, StatusBarIconHolder statusBarIconHolder) {
            int type = statusBarIconHolder.getType();
            if (type == 0) {
                onSetIcon(i, statusBarIconHolder.getIcon());
            } else if (type != 1) {
                if (type == 2) {
                    onSetMobileIcon(i, statusBarIconHolder.getMobileState());
                }
            } else {
                onSetSignalIcon(i, statusBarIconHolder.getWifiState());
            }
        }

        public void onSetSignalIcon(int i, WifiIconState wifiIconState) {
            StatusBarWifiView statusBarWifiView = (StatusBarWifiView) this.mGroup.getChildAt(i);
            if (statusBarWifiView != null) {
                statusBarWifiView.applyWifiState(wifiIconState);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateWifiState(wifiIconState);
            }
        }

        public void onSetMobileIcon(int i, MobileIconState mobileIconState) {
            StatusBarMobileView statusBarMobileView = (StatusBarMobileView) this.mGroup.getChildAt(i);
            if (statusBarMobileView != null) {
                statusBarMobileView.applyMobileState(mobileIconState);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateMobileState(mobileIconState);
            }
        }

        public void dispatchDemoCommand(String str, Bundle bundle) {
            if (this.mDemoable) {
                if (str.equals("exit")) {
                    DemoStatusIcons demoStatusIcons = this.mDemoStatusIcons;
                    if (demoStatusIcons != null) {
                        demoStatusIcons.dispatchDemoCommand(str, bundle);
                        exitDemoMode();
                    }
                    this.mIsInDemoMode = false;
                } else {
                    if (this.mDemoStatusIcons == null) {
                        this.mIsInDemoMode = true;
                        this.mDemoStatusIcons = createDemoStatusIcons();
                    }
                    this.mDemoStatusIcons.dispatchDemoCommand(str, bundle);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void exitDemoMode() {
            this.mDemoStatusIcons.remove();
            this.mDemoStatusIcons = null;
        }

        /* access modifiers changed from: protected */
        public DemoStatusIcons createDemoStatusIcons() {
            return new DemoStatusIcons((LinearLayout) this.mGroup, this.mIconSize);
        }
    }

    public static class TintedIconManager extends IconManager {
        private int mColor;

        public TintedIconManager(ViewGroup viewGroup, CommandQueue commandQueue) {
            super(viewGroup, commandQueue);
        }

        /* access modifiers changed from: protected */
        public void onIconAdded(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            StatusIconDisplayable addHolder = addHolder(i, str, z, statusBarIconHolder);
            addHolder.setStaticDrawableColor(this.mColor);
            addHolder.setDecorColor(this.mColor);
        }

        public void setTint(int i) {
            this.mColor = i;
            for (int i2 = 0; i2 < this.mGroup.getChildCount(); i2++) {
                View childAt = this.mGroup.getChildAt(i2);
                if (childAt instanceof StatusIconDisplayable) {
                    StatusIconDisplayable statusIconDisplayable = (StatusIconDisplayable) childAt;
                    statusIconDisplayable.setStaticDrawableColor(this.mColor);
                    statusIconDisplayable.setDecorColor(this.mColor);
                }
            }
        }

        /* access modifiers changed from: protected */
        public DemoStatusIcons createDemoStatusIcons() {
            DemoStatusIcons createDemoStatusIcons = super.createDemoStatusIcons();
            createDemoStatusIcons.setColor(this.mColor);
            return createDemoStatusIcons;
        }
    }

    void addIconGroup(IconManager iconManager);

    void removeAllIconsForSlot(String str);

    void removeIconGroup(IconManager iconManager);

    void setExternalIcon(String str);

    void setIcon(String str, int i, CharSequence charSequence);

    void setIcon(String str, StatusBarIcon statusBarIcon);

    void setIconVisibility(String str, boolean z);

    void setMobileIcons(String str, List<MobileIconState> list);

    void setSignalIcon(String str, WifiIconState wifiIconState);

    static ArraySet<String> getIconBlacklist(Context context, String str) {
        String[] strArr;
        ArraySet<String> arraySet = new ArraySet<>();
        if (str == null) {
            strArr = context.getResources().getStringArray(C2005R$array.config_statusBarIconBlackList);
        } else {
            strArr = str.split(",");
        }
        for (String str2 : strArr) {
            if (!TextUtils.isEmpty(str2)) {
                arraySet.add(str2);
            }
        }
        return arraySet;
    }
}
