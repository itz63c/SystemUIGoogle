package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Space;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.android.systemui.statusbar.phone.ReverseLinearLayout.ReverseRelativeLayout;
import com.android.systemui.statusbar.policy.KeyButtonView;
import java.util.Objects;

public class NavigationBarInflaterView extends FrameLayout implements ModeChangedListener {
    private boolean mAlternativeOrder;
    @VisibleForTesting
    SparseArray<ButtonDispatcher> mButtonDispatchers;
    private String mCurrentLayout;
    protected FrameLayout mHorizontal;
    private boolean mIsVertical;
    protected LayoutInflater mLandscapeInflater;
    private View mLastLandscape;
    private View mLastPortrait;
    protected LayoutInflater mLayoutInflater;
    private int mNavBarMode = 0;
    private OverviewProxyService mOverviewProxyService;
    private boolean mUsingCustomLayout;
    protected FrameLayout mVertical;

    public NavigationBarInflaterView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        createInflaters();
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
        this.mNavBarMode = ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void createInflaters() {
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        Configuration configuration = new Configuration();
        configuration.setTo(this.mContext.getResources().getConfiguration());
        configuration.orientation = 2;
        this.mLandscapeInflater = LayoutInflater.from(this.mContext.createConfigurationContext(configuration));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        inflateChildren();
        clearViews();
        inflateLayout(getDefaultLayout());
    }

    private void inflateChildren() {
        removeAllViews();
        FrameLayout frameLayout = (FrameLayout) this.mLayoutInflater.inflate(C2013R$layout.navigation_layout, this, false);
        this.mHorizontal = frameLayout;
        addView(frameLayout);
        FrameLayout frameLayout2 = (FrameLayout) this.mLayoutInflater.inflate(C2013R$layout.navigation_layout_vertical, this, false);
        this.mVertical = frameLayout2;
        addView(frameLayout2);
        updateAlternativeOrder();
    }

    /* access modifiers changed from: protected */
    public String getDefaultLayout() {
        int i;
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            i = C2017R$string.config_navBarLayoutHandle;
        } else if (this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            i = C2017R$string.config_navBarLayoutQuickstep;
        } else {
            i = C2017R$string.config_navBarLayout;
        }
        return getContext().getString(i);
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
        onLikelyDefaultLayoutChange();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).removeListener(this);
        super.onDetachedFromWindow();
    }

    public void onLikelyDefaultLayoutChange() {
        if (!this.mUsingCustomLayout) {
            String defaultLayout = getDefaultLayout();
            if (!Objects.equals(this.mCurrentLayout, defaultLayout)) {
                clearViews();
                inflateLayout(defaultLayout);
            }
        }
    }

    public void setButtonDispatchers(SparseArray<ButtonDispatcher> sparseArray) {
        this.mButtonDispatchers = sparseArray;
        for (int i = 0; i < sparseArray.size(); i++) {
            initiallyFill((ButtonDispatcher) sparseArray.valueAt(i));
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateButtonDispatchersCurrentView() {
        if (this.mButtonDispatchers != null) {
            FrameLayout frameLayout = this.mIsVertical ? this.mVertical : this.mHorizontal;
            for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
                ((ButtonDispatcher) this.mButtonDispatchers.valueAt(i)).setCurrentView(frameLayout);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setVertical(boolean z) {
        if (z != this.mIsVertical) {
            this.mIsVertical = z;
        }
    }

    /* access modifiers changed from: 0000 */
    public void setAlternativeOrder(boolean z) {
        if (z != this.mAlternativeOrder) {
            this.mAlternativeOrder = z;
            updateAlternativeOrder();
        }
    }

    private void updateAlternativeOrder() {
        updateAlternativeOrder(this.mHorizontal.findViewById(C2011R$id.ends_group));
        updateAlternativeOrder(this.mHorizontal.findViewById(C2011R$id.center_group));
        updateAlternativeOrder(this.mVertical.findViewById(C2011R$id.ends_group));
        updateAlternativeOrder(this.mVertical.findViewById(C2011R$id.center_group));
    }

    private void updateAlternativeOrder(View view) {
        if (view instanceof ReverseLinearLayout) {
            ((ReverseLinearLayout) view).setAlternativeOrder(this.mAlternativeOrder);
        }
    }

    private void initiallyFill(ButtonDispatcher buttonDispatcher) {
        addAll(buttonDispatcher, (ViewGroup) this.mHorizontal.findViewById(C2011R$id.ends_group));
        addAll(buttonDispatcher, (ViewGroup) this.mHorizontal.findViewById(C2011R$id.center_group));
        addAll(buttonDispatcher, (ViewGroup) this.mVertical.findViewById(C2011R$id.ends_group));
        addAll(buttonDispatcher, (ViewGroup) this.mVertical.findViewById(C2011R$id.center_group));
    }

    private void addAll(ButtonDispatcher buttonDispatcher, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i).getId() == buttonDispatcher.getId()) {
                buttonDispatcher.addView(viewGroup.getChildAt(i));
            }
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                addAll(buttonDispatcher, (ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void inflateLayout(String str) {
        this.mCurrentLayout = str;
        if (str == null) {
            str = getDefaultLayout();
        }
        String str2 = ";";
        String[] split = str.split(str2, 3);
        if (split.length != 3) {
            Log.d("NavBarInflater", "Invalid layout.");
            split = getDefaultLayout().split(str2, 3);
        }
        String str3 = ",";
        String[] split2 = split[0].split(str3);
        String[] split3 = split[1].split(str3);
        String[] split4 = split[2].split(str3);
        inflateButtons(split2, (ViewGroup) this.mHorizontal.findViewById(C2011R$id.ends_group), false, true);
        inflateButtons(split2, (ViewGroup) this.mVertical.findViewById(C2011R$id.ends_group), true, true);
        inflateButtons(split3, (ViewGroup) this.mHorizontal.findViewById(C2011R$id.center_group), false, false);
        inflateButtons(split3, (ViewGroup) this.mVertical.findViewById(C2011R$id.center_group), true, false);
        addGravitySpacer((LinearLayout) this.mHorizontal.findViewById(C2011R$id.ends_group));
        addGravitySpacer((LinearLayout) this.mVertical.findViewById(C2011R$id.ends_group));
        inflateButtons(split4, (ViewGroup) this.mHorizontal.findViewById(C2011R$id.ends_group), false, false);
        inflateButtons(split4, (ViewGroup) this.mVertical.findViewById(C2011R$id.ends_group), true, false);
        updateButtonDispatchersCurrentView();
    }

    private void addGravitySpacer(LinearLayout linearLayout) {
        linearLayout.addView(new Space(this.mContext), new LayoutParams(0, 0, 1.0f));
    }

    private void inflateButtons(String[] strArr, ViewGroup viewGroup, boolean z, boolean z2) {
        for (String inflateButton : strArr) {
            inflateButton(inflateButton, viewGroup, z, z2);
        }
    }

    /* access modifiers changed from: protected */
    public View inflateButton(String str, ViewGroup viewGroup, boolean z, boolean z2) {
        View createView = createView(str, viewGroup, z ? this.mLandscapeInflater : this.mLayoutInflater);
        if (createView == null) {
            return null;
        }
        View applySize = applySize(createView, str, z, z2);
        viewGroup.addView(applySize);
        addToDispatchers(applySize);
        View view = z ? this.mLastLandscape : this.mLastPortrait;
        View childAt = applySize instanceof ReverseRelativeLayout ? ((ReverseRelativeLayout) applySize).getChildAt(0) : applySize;
        if (view != null) {
            childAt.setAccessibilityTraversalAfter(view.getId());
        }
        if (z) {
            this.mLastLandscape = childAt;
        } else {
            this.mLastPortrait = childAt;
        }
        return applySize;
    }

    private View applySize(View view, String str, boolean z, boolean z2) {
        String extractSize = extractSize(str);
        if (extractSize == null) {
            return view;
        }
        String str2 = "W";
        String str3 = "A";
        if (extractSize.contains(str2) || extractSize.contains(str3)) {
            ReverseRelativeLayout reverseRelativeLayout = new ReverseRelativeLayout(this.mContext);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(view.getLayoutParams());
            int i = z ? z2 ? 48 : 80 : z2 ? 8388611 : 8388613;
            if (extractSize.endsWith("WC")) {
                i = 17;
            } else if (extractSize.endsWith("C")) {
                i = 16;
            }
            reverseRelativeLayout.setDefaultGravity(i);
            reverseRelativeLayout.setGravity(i);
            reverseRelativeLayout.addView(view, layoutParams);
            if (extractSize.contains(str2)) {
                reverseRelativeLayout.setLayoutParams(new LayoutParams(0, -1, Float.parseFloat(extractSize.substring(0, extractSize.indexOf(str2)))));
            } else {
                reverseRelativeLayout.setLayoutParams(new LayoutParams((int) convertDpToPx(this.mContext, Float.parseFloat(extractSize.substring(0, extractSize.indexOf(str3)))), -1));
            }
            reverseRelativeLayout.setClipChildren(false);
            reverseRelativeLayout.setClipToPadding(false);
            return reverseRelativeLayout;
        }
        float parseFloat = Float.parseFloat(extractSize);
        ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
        layoutParams2.width = (int) (((float) layoutParams2.width) * parseFloat);
        return view;
    }

    private View createView(String str, ViewGroup viewGroup, LayoutInflater layoutInflater) {
        String extractButton = extractButton(str);
        String str2 = "menu_ime";
        String str3 = "space";
        if ("left".equals(extractButton)) {
            extractButton = extractButton(str3);
        } else if ("right".equals(extractButton)) {
            extractButton = extractButton(str2);
        }
        if ("home".equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.home, viewGroup, false);
        }
        if ("back".equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.back, viewGroup, false);
        }
        if ("recent".equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.recent_apps, viewGroup, false);
        }
        if (str2.equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.menu_ime, viewGroup, false);
        }
        if (str3.equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.nav_key_space, viewGroup, false);
        }
        if ("clipboard".equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.clipboard, viewGroup, false);
        }
        if ("contextual".equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.contextual, viewGroup, false);
        }
        if ("home_handle".equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.home_handle, viewGroup, false);
        }
        if ("ime_switcher".equals(extractButton)) {
            return layoutInflater.inflate(C2013R$layout.ime_switcher, viewGroup, false);
        }
        if (!extractButton.startsWith("key")) {
            return null;
        }
        String extractImage = extractImage(extractButton);
        int extractKeycode = extractKeycode(extractButton);
        View inflate = layoutInflater.inflate(C2013R$layout.custom_key, viewGroup, false);
        KeyButtonView keyButtonView = (KeyButtonView) inflate;
        keyButtonView.setCode(extractKeycode);
        if (extractImage != null) {
            if (extractImage.contains(":")) {
                keyButtonView.loadAsync(Icon.createWithContentUri(extractImage));
            } else if (extractImage.contains("/")) {
                int indexOf = extractImage.indexOf(47);
                keyButtonView.loadAsync(Icon.createWithResource(extractImage.substring(0, indexOf), Integer.parseInt(extractImage.substring(indexOf + 1))));
            }
        }
        return inflate;
    }

    public static String extractImage(String str) {
        String str2 = ":";
        if (!str.contains(str2)) {
            return null;
        }
        return str.substring(str.indexOf(str2) + 1, str.indexOf(")"));
    }

    public static int extractKeycode(String str) {
        String str2 = "(";
        if (!str.contains(str2)) {
            return 1;
        }
        return Integer.parseInt(str.substring(str.indexOf(str2) + 1, str.indexOf(":")));
    }

    public static String extractSize(String str) {
        String str2 = "[";
        if (!str.contains(str2)) {
            return null;
        }
        return str.substring(str.indexOf(str2) + 1, str.indexOf("]"));
    }

    public static String extractButton(String str) {
        String str2 = "[";
        if (!str.contains(str2)) {
            return str;
        }
        return str.substring(0, str.indexOf(str2));
    }

    private void addToDispatchers(View view) {
        SparseArray<ButtonDispatcher> sparseArray = this.mButtonDispatchers;
        if (sparseArray != null) {
            int indexOfKey = sparseArray.indexOfKey(view.getId());
            if (indexOfKey >= 0) {
                ((ButtonDispatcher) this.mButtonDispatchers.valueAt(indexOfKey)).addView(view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    addToDispatchers(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private void clearViews() {
        if (this.mButtonDispatchers != null) {
            for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
                ((ButtonDispatcher) this.mButtonDispatchers.valueAt(i)).clear();
            }
        }
        clearAllChildren((ViewGroup) this.mHorizontal.findViewById(C2011R$id.nav_buttons));
        clearAllChildren((ViewGroup) this.mVertical.findViewById(C2011R$id.nav_buttons));
    }

    private void clearAllChildren(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            ((ViewGroup) viewGroup.getChildAt(i)).removeAllViews();
        }
    }

    private static float convertDpToPx(Context context, float f) {
        return f * context.getResources().getDisplayMetrics().density;
    }
}
