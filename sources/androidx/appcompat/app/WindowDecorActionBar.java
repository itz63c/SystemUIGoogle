package androidx.appcompat.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.appcompat.R$attr;
import androidx.appcompat.R$id;
import androidx.appcompat.R$styleable;
import androidx.appcompat.app.ActionBar.OnMenuVisibilityListener;
import androidx.appcompat.view.ActionBarPolicy;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.ViewPropertyAnimatorCompatSet;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.ActionBarContainer;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.ActionBarOverlayLayout;
import androidx.appcompat.widget.ActionBarOverlayLayout.ActionBarVisibilityCallback;
import androidx.appcompat.widget.DecorToolbar;
import androidx.appcompat.widget.ScrollingTabContainerView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.core.view.ViewPropertyAnimatorUpdateListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WindowDecorActionBar extends ActionBar implements ActionBarVisibilityCallback {
    private static final Interpolator sHideInterpolator = new AccelerateInterpolator();
    private static final Interpolator sShowInterpolator = new DecelerateInterpolator();
    ActionModeImpl mActionMode;
    ActionBarContainer mContainerView;
    boolean mContentAnimations = true;
    View mContentView;
    Context mContext;
    ActionBarContextView mContextView;
    private int mCurWindowVisibility = 0;
    ViewPropertyAnimatorCompatSet mCurrentShowAnim;
    DecorToolbar mDecorToolbar;
    ActionMode mDeferredDestroyActionMode;
    Callback mDeferredModeDestroyCallback;
    private boolean mDisplayHomeAsUpSet;
    private boolean mHasEmbeddedTabs;
    boolean mHiddenByApp;
    boolean mHiddenBySystem;
    final ViewPropertyAnimatorListener mHideListener = new ViewPropertyAnimatorListenerAdapter() {
        public void onAnimationEnd(View view) {
            WindowDecorActionBar windowDecorActionBar = WindowDecorActionBar.this;
            if (windowDecorActionBar.mContentAnimations) {
                View view2 = windowDecorActionBar.mContentView;
                if (view2 != null) {
                    view2.setTranslationY(0.0f);
                    WindowDecorActionBar.this.mContainerView.setTranslationY(0.0f);
                }
            }
            WindowDecorActionBar.this.mContainerView.setVisibility(8);
            WindowDecorActionBar.this.mContainerView.setTransitioning(false);
            WindowDecorActionBar windowDecorActionBar2 = WindowDecorActionBar.this;
            windowDecorActionBar2.mCurrentShowAnim = null;
            windowDecorActionBar2.completeDeferredDestroyActionMode();
            ActionBarOverlayLayout actionBarOverlayLayout = WindowDecorActionBar.this.mOverlayLayout;
            if (actionBarOverlayLayout != null) {
                ViewCompat.requestApplyInsets(actionBarOverlayLayout);
            }
        }
    };
    boolean mHideOnContentScroll;
    private boolean mLastMenuVisibility;
    private ArrayList<OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList<>();
    private boolean mNowShowing = true;
    ActionBarOverlayLayout mOverlayLayout;
    private boolean mShowHideAnimationEnabled;
    final ViewPropertyAnimatorListener mShowListener = new ViewPropertyAnimatorListenerAdapter() {
        public void onAnimationEnd(View view) {
            WindowDecorActionBar windowDecorActionBar = WindowDecorActionBar.this;
            windowDecorActionBar.mCurrentShowAnim = null;
            windowDecorActionBar.mContainerView.requestLayout();
        }
    };
    private boolean mShowingForMode;
    ScrollingTabContainerView mTabScrollView;
    private Context mThemedContext;
    final ViewPropertyAnimatorUpdateListener mUpdateListener = new ViewPropertyAnimatorUpdateListener() {
        public void onAnimationUpdate(View view) {
            ((View) WindowDecorActionBar.this.mContainerView.getParent()).invalidate();
        }
    };

    public class ActionModeImpl extends ActionMode implements MenuBuilder.Callback {
        private final Context mActionModeContext;
        private Callback mCallback;
        private WeakReference<View> mCustomView;
        private final MenuBuilder mMenu;

        public ActionModeImpl(Context context, Callback callback) {
            this.mActionModeContext = context;
            this.mCallback = callback;
            MenuBuilder menuBuilder = new MenuBuilder(context);
            menuBuilder.setDefaultShowAsAction(1);
            this.mMenu = menuBuilder;
            menuBuilder.setCallback(this);
        }

        public MenuInflater getMenuInflater() {
            return new SupportMenuInflater(this.mActionModeContext);
        }

        public Menu getMenu() {
            return this.mMenu;
        }

        public void finish() {
            WindowDecorActionBar windowDecorActionBar = WindowDecorActionBar.this;
            if (windowDecorActionBar.mActionMode == this) {
                if (!WindowDecorActionBar.checkShowingFlags(windowDecorActionBar.mHiddenByApp, windowDecorActionBar.mHiddenBySystem, false)) {
                    WindowDecorActionBar windowDecorActionBar2 = WindowDecorActionBar.this;
                    windowDecorActionBar2.mDeferredDestroyActionMode = this;
                    windowDecorActionBar2.mDeferredModeDestroyCallback = this.mCallback;
                } else {
                    this.mCallback.onDestroyActionMode(this);
                }
                this.mCallback = null;
                WindowDecorActionBar.this.animateToMode(false);
                WindowDecorActionBar.this.mContextView.closeMode();
                WindowDecorActionBar.this.mDecorToolbar.getViewGroup().sendAccessibilityEvent(32);
                WindowDecorActionBar windowDecorActionBar3 = WindowDecorActionBar.this;
                windowDecorActionBar3.mOverlayLayout.setHideOnContentScrollEnabled(windowDecorActionBar3.mHideOnContentScroll);
                WindowDecorActionBar.this.mActionMode = null;
            }
        }

        public void invalidate() {
            if (WindowDecorActionBar.this.mActionMode == this) {
                this.mMenu.stopDispatchingItemsChanged();
                try {
                    this.mCallback.onPrepareActionMode(this, this.mMenu);
                } finally {
                    this.mMenu.startDispatchingItemsChanged();
                }
            }
        }

        public boolean dispatchOnCreate() {
            this.mMenu.stopDispatchingItemsChanged();
            try {
                boolean onCreateActionMode = this.mCallback.onCreateActionMode(this, this.mMenu);
                return onCreateActionMode;
            } finally {
                this.mMenu.startDispatchingItemsChanged();
            }
        }

        public void setCustomView(View view) {
            WindowDecorActionBar.this.mContextView.setCustomView(view);
            this.mCustomView = new WeakReference<>(view);
        }

        public void setSubtitle(CharSequence charSequence) {
            WindowDecorActionBar.this.mContextView.setSubtitle(charSequence);
        }

        public void setTitle(CharSequence charSequence) {
            WindowDecorActionBar.this.mContextView.setTitle(charSequence);
        }

        public void setTitle(int i) {
            setTitle((CharSequence) WindowDecorActionBar.this.mContext.getResources().getString(i));
        }

        public void setSubtitle(int i) {
            setSubtitle((CharSequence) WindowDecorActionBar.this.mContext.getResources().getString(i));
        }

        public CharSequence getTitle() {
            return WindowDecorActionBar.this.mContextView.getTitle();
        }

        public CharSequence getSubtitle() {
            return WindowDecorActionBar.this.mContextView.getSubtitle();
        }

        public void setTitleOptionalHint(boolean z) {
            super.setTitleOptionalHint(z);
            WindowDecorActionBar.this.mContextView.setTitleOptional(z);
        }

        public boolean isTitleOptional() {
            return WindowDecorActionBar.this.mContextView.isTitleOptional();
        }

        public View getCustomView() {
            WeakReference<View> weakReference = this.mCustomView;
            if (weakReference != null) {
                return (View) weakReference.get();
            }
            return null;
        }

        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            Callback callback = this.mCallback;
            if (callback != null) {
                return callback.onActionItemClicked(this, menuItem);
            }
            return false;
        }

        public void onMenuModeChange(MenuBuilder menuBuilder) {
            if (this.mCallback != null) {
                invalidate();
                WindowDecorActionBar.this.mContextView.showOverflowMenu();
            }
        }
    }

    static boolean checkShowingFlags(boolean z, boolean z2, boolean z3) {
        if (z3) {
            return true;
        }
        return !z && !z2;
    }

    public void onContentScrollStopped() {
    }

    public WindowDecorActionBar(Activity activity, boolean z) {
        new ArrayList();
        View decorView = activity.getWindow().getDecorView();
        init(decorView);
        if (!z) {
            this.mContentView = decorView.findViewById(16908290);
        }
    }

    public WindowDecorActionBar(Dialog dialog) {
        new ArrayList();
        init(dialog.getWindow().getDecorView());
    }

    private void init(View view) {
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) view.findViewById(R$id.decor_content_parent);
        this.mOverlayLayout = actionBarOverlayLayout;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setActionBarVisibilityCallback(this);
        }
        this.mDecorToolbar = getDecorToolbar(view.findViewById(R$id.action_bar));
        this.mContextView = (ActionBarContextView) view.findViewById(R$id.action_context_bar);
        ActionBarContainer actionBarContainer = (ActionBarContainer) view.findViewById(R$id.action_bar_container);
        this.mContainerView = actionBarContainer;
        DecorToolbar decorToolbar = this.mDecorToolbar;
        if (decorToolbar == null || this.mContextView == null || actionBarContainer == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(WindowDecorActionBar.class.getSimpleName());
            sb.append(" can only be used with a compatible window decor layout");
            throw new IllegalStateException(sb.toString());
        }
        this.mContext = decorToolbar.getContext();
        boolean z = (this.mDecorToolbar.getDisplayOptions() & 4) != 0;
        if (z) {
            this.mDisplayHomeAsUpSet = true;
        }
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(this.mContext);
        setHomeButtonEnabled(actionBarPolicy.enableHomeButtonByDefault() || z);
        setHasEmbeddedTabs(actionBarPolicy.hasEmbeddedTabs());
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(null, R$styleable.ActionBar, R$attr.actionBarStyle, 0);
        if (obtainStyledAttributes.getBoolean(R$styleable.ActionBar_hideOnContentScroll, false)) {
            setHideOnContentScrollEnabled(true);
        }
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.ActionBar_elevation, 0);
        if (dimensionPixelSize != 0) {
            setElevation((float) dimensionPixelSize);
        }
        obtainStyledAttributes.recycle();
    }

    private DecorToolbar getDecorToolbar(View view) {
        if (view instanceof DecorToolbar) {
            return (DecorToolbar) view;
        }
        if (view instanceof Toolbar) {
            return ((Toolbar) view).getWrapper();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Can't make a decor toolbar out of ");
        sb.append(view != null ? view.getClass().getSimpleName() : "null");
        throw new IllegalStateException(sb.toString());
    }

    public void setElevation(float f) {
        ViewCompat.setElevation(this.mContainerView, f);
    }

    public void onConfigurationChanged(Configuration configuration) {
        setHasEmbeddedTabs(ActionBarPolicy.get(this.mContext).hasEmbeddedTabs());
    }

    private void setHasEmbeddedTabs(boolean z) {
        this.mHasEmbeddedTabs = z;
        if (!z) {
            this.mDecorToolbar.setEmbeddedTabView(null);
            this.mContainerView.setTabContainer(this.mTabScrollView);
        } else {
            this.mContainerView.setTabContainer(null);
            this.mDecorToolbar.setEmbeddedTabView(this.mTabScrollView);
        }
        boolean z2 = true;
        boolean z3 = getNavigationMode() == 2;
        ScrollingTabContainerView scrollingTabContainerView = this.mTabScrollView;
        if (scrollingTabContainerView != null) {
            if (z3) {
                scrollingTabContainerView.setVisibility(0);
                ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
                if (actionBarOverlayLayout != null) {
                    ViewCompat.requestApplyInsets(actionBarOverlayLayout);
                }
            } else {
                scrollingTabContainerView.setVisibility(8);
            }
        }
        this.mDecorToolbar.setCollapsible(!this.mHasEmbeddedTabs && z3);
        ActionBarOverlayLayout actionBarOverlayLayout2 = this.mOverlayLayout;
        if (this.mHasEmbeddedTabs || !z3) {
            z2 = false;
        }
        actionBarOverlayLayout2.setHasNonEmbeddedTabs(z2);
    }

    /* access modifiers changed from: 0000 */
    public void completeDeferredDestroyActionMode() {
        Callback callback = this.mDeferredModeDestroyCallback;
        if (callback != null) {
            callback.onDestroyActionMode(this.mDeferredDestroyActionMode);
            this.mDeferredDestroyActionMode = null;
            this.mDeferredModeDestroyCallback = null;
        }
    }

    public void onWindowVisibilityChanged(int i) {
        this.mCurWindowVisibility = i;
    }

    public void setShowHideAnimationEnabled(boolean z) {
        this.mShowHideAnimationEnabled = z;
        if (!z) {
            ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet = this.mCurrentShowAnim;
            if (viewPropertyAnimatorCompatSet != null) {
                viewPropertyAnimatorCompatSet.cancel();
            }
        }
    }

    public void dispatchMenuVisibilityChanged(boolean z) {
        if (z != this.mLastMenuVisibility) {
            this.mLastMenuVisibility = z;
            int size = this.mMenuVisibilityListeners.size();
            for (int i = 0; i < size; i++) {
                ((OnMenuVisibilityListener) this.mMenuVisibilityListeners.get(i)).onMenuVisibilityChanged(z);
            }
        }
    }

    public void setDisplayHomeAsUpEnabled(boolean z) {
        setDisplayOptions(z ? 4 : 0, 4);
    }

    public void setHomeButtonEnabled(boolean z) {
        this.mDecorToolbar.setHomeButtonEnabled(z);
    }

    public void setWindowTitle(CharSequence charSequence) {
        this.mDecorToolbar.setWindowTitle(charSequence);
    }

    public void setDisplayOptions(int i, int i2) {
        int displayOptions = this.mDecorToolbar.getDisplayOptions();
        if ((i2 & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mDecorToolbar.setDisplayOptions((i & i2) | ((~i2) & displayOptions));
    }

    public int getNavigationMode() {
        return this.mDecorToolbar.getNavigationMode();
    }

    public int getDisplayOptions() {
        return this.mDecorToolbar.getDisplayOptions();
    }

    public ActionMode startActionMode(Callback callback) {
        ActionModeImpl actionModeImpl = this.mActionMode;
        if (actionModeImpl != null) {
            actionModeImpl.finish();
        }
        this.mOverlayLayout.setHideOnContentScrollEnabled(false);
        this.mContextView.killMode();
        ActionModeImpl actionModeImpl2 = new ActionModeImpl(this.mContextView.getContext(), callback);
        if (!actionModeImpl2.dispatchOnCreate()) {
            return null;
        }
        this.mActionMode = actionModeImpl2;
        actionModeImpl2.invalidate();
        this.mContextView.initForMode(actionModeImpl2);
        animateToMode(true);
        this.mContextView.sendAccessibilityEvent(32);
        return actionModeImpl2;
    }

    public void enableContentAnimations(boolean z) {
        this.mContentAnimations = z;
    }

    private void showForActionMode() {
        if (!this.mShowingForMode) {
            this.mShowingForMode = true;
            ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
            if (actionBarOverlayLayout != null) {
                actionBarOverlayLayout.setShowingForActionMode(true);
            }
            updateVisibility(false);
        }
    }

    public void showForSystem() {
        if (this.mHiddenBySystem) {
            this.mHiddenBySystem = false;
            updateVisibility(true);
        }
    }

    private void hideForActionMode() {
        if (this.mShowingForMode) {
            this.mShowingForMode = false;
            ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
            if (actionBarOverlayLayout != null) {
                actionBarOverlayLayout.setShowingForActionMode(false);
            }
            updateVisibility(false);
        }
    }

    public void hideForSystem() {
        if (!this.mHiddenBySystem) {
            this.mHiddenBySystem = true;
            updateVisibility(true);
        }
    }

    public void setHideOnContentScrollEnabled(boolean z) {
        if (!z || this.mOverlayLayout.isInOverlayMode()) {
            this.mHideOnContentScroll = z;
            this.mOverlayLayout.setHideOnContentScrollEnabled(z);
            return;
        }
        throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll");
    }

    private void updateVisibility(boolean z) {
        if (checkShowingFlags(this.mHiddenByApp, this.mHiddenBySystem, this.mShowingForMode)) {
            if (!this.mNowShowing) {
                this.mNowShowing = true;
                doShow(z);
            }
        } else if (this.mNowShowing) {
            this.mNowShowing = false;
            doHide(z);
        }
    }

    public void doShow(boolean z) {
        ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet = this.mCurrentShowAnim;
        if (viewPropertyAnimatorCompatSet != null) {
            viewPropertyAnimatorCompatSet.cancel();
        }
        this.mContainerView.setVisibility(0);
        if (this.mCurWindowVisibility != 0 || (!this.mShowHideAnimationEnabled && !z)) {
            this.mContainerView.setAlpha(1.0f);
            this.mContainerView.setTranslationY(0.0f);
            if (this.mContentAnimations) {
                View view = this.mContentView;
                if (view != null) {
                    view.setTranslationY(0.0f);
                }
            }
            this.mShowListener.onAnimationEnd(null);
        } else {
            this.mContainerView.setTranslationY(0.0f);
            float f = (float) (-this.mContainerView.getHeight());
            if (z) {
                int[] iArr = {0, 0};
                this.mContainerView.getLocationInWindow(iArr);
                f -= (float) iArr[1];
            }
            this.mContainerView.setTranslationY(f);
            ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet2 = new ViewPropertyAnimatorCompatSet();
            ViewPropertyAnimatorCompat animate = ViewCompat.animate(this.mContainerView);
            animate.translationY(0.0f);
            animate.setUpdateListener(this.mUpdateListener);
            viewPropertyAnimatorCompatSet2.play(animate);
            if (this.mContentAnimations) {
                View view2 = this.mContentView;
                if (view2 != null) {
                    view2.setTranslationY(f);
                    ViewPropertyAnimatorCompat animate2 = ViewCompat.animate(this.mContentView);
                    animate2.translationY(0.0f);
                    viewPropertyAnimatorCompatSet2.play(animate2);
                }
            }
            viewPropertyAnimatorCompatSet2.setInterpolator(sShowInterpolator);
            viewPropertyAnimatorCompatSet2.setDuration(250);
            viewPropertyAnimatorCompatSet2.setListener(this.mShowListener);
            this.mCurrentShowAnim = viewPropertyAnimatorCompatSet2;
            viewPropertyAnimatorCompatSet2.start();
        }
        ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
        if (actionBarOverlayLayout != null) {
            ViewCompat.requestApplyInsets(actionBarOverlayLayout);
        }
    }

    public void doHide(boolean z) {
        ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet = this.mCurrentShowAnim;
        if (viewPropertyAnimatorCompatSet != null) {
            viewPropertyAnimatorCompatSet.cancel();
        }
        if (this.mCurWindowVisibility != 0 || (!this.mShowHideAnimationEnabled && !z)) {
            this.mHideListener.onAnimationEnd(null);
            return;
        }
        this.mContainerView.setAlpha(1.0f);
        this.mContainerView.setTransitioning(true);
        ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet2 = new ViewPropertyAnimatorCompatSet();
        float f = (float) (-this.mContainerView.getHeight());
        if (z) {
            int[] iArr = {0, 0};
            this.mContainerView.getLocationInWindow(iArr);
            f -= (float) iArr[1];
        }
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(this.mContainerView);
        animate.translationY(f);
        animate.setUpdateListener(this.mUpdateListener);
        viewPropertyAnimatorCompatSet2.play(animate);
        if (this.mContentAnimations) {
            View view = this.mContentView;
            if (view != null) {
                ViewPropertyAnimatorCompat animate2 = ViewCompat.animate(view);
                animate2.translationY(f);
                viewPropertyAnimatorCompatSet2.play(animate2);
            }
        }
        viewPropertyAnimatorCompatSet2.setInterpolator(sHideInterpolator);
        viewPropertyAnimatorCompatSet2.setDuration(250);
        viewPropertyAnimatorCompatSet2.setListener(this.mHideListener);
        this.mCurrentShowAnim = viewPropertyAnimatorCompatSet2;
        viewPropertyAnimatorCompatSet2.start();
    }

    public void animateToMode(boolean z) {
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat;
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat2;
        if (z) {
            showForActionMode();
        } else {
            hideForActionMode();
        }
        if (shouldAnimateContextView()) {
            if (z) {
                viewPropertyAnimatorCompat = this.mDecorToolbar.setupAnimatorToVisibility(4, 100);
                viewPropertyAnimatorCompat2 = this.mContextView.setupAnimatorToVisibility(0, 200);
            } else {
                ViewPropertyAnimatorCompat viewPropertyAnimatorCompat3 = this.mDecorToolbar.setupAnimatorToVisibility(0, 200);
                viewPropertyAnimatorCompat = this.mContextView.setupAnimatorToVisibility(8, 100);
                viewPropertyAnimatorCompat2 = viewPropertyAnimatorCompat3;
            }
            ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
            viewPropertyAnimatorCompatSet.playSequentially(viewPropertyAnimatorCompat, viewPropertyAnimatorCompat2);
            viewPropertyAnimatorCompatSet.start();
        } else if (z) {
            this.mDecorToolbar.setVisibility(4);
            this.mContextView.setVisibility(0);
        } else {
            this.mDecorToolbar.setVisibility(0);
            this.mContextView.setVisibility(8);
        }
    }

    private boolean shouldAnimateContextView() {
        return ViewCompat.isLaidOut(this.mContainerView);
    }

    public Context getThemedContext() {
        if (this.mThemedContext == null) {
            TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(R$attr.actionBarWidgetTheme, typedValue, true);
            int i = typedValue.resourceId;
            if (i != 0) {
                this.mThemedContext = new ContextThemeWrapper(this.mContext, i);
            } else {
                this.mThemedContext = this.mContext;
            }
        }
        return this.mThemedContext;
    }

    public void onContentScrollStarted() {
        ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet = this.mCurrentShowAnim;
        if (viewPropertyAnimatorCompatSet != null) {
            viewPropertyAnimatorCompatSet.cancel();
            this.mCurrentShowAnim = null;
        }
    }

    public boolean collapseActionView() {
        DecorToolbar decorToolbar = this.mDecorToolbar;
        if (decorToolbar == null || !decorToolbar.hasExpandedActionView()) {
            return false;
        }
        this.mDecorToolbar.collapseActionView();
        return true;
    }

    public void setDefaultDisplayHomeAsUpEnabled(boolean z) {
        if (!this.mDisplayHomeAsUpSet) {
            setDisplayHomeAsUpEnabled(z);
        }
    }

    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        ActionModeImpl actionModeImpl = this.mActionMode;
        if (actionModeImpl == null) {
            return false;
        }
        Menu menu = actionModeImpl.getMenu();
        if (menu == null) {
            return false;
        }
        boolean z = true;
        if (KeyCharacterMap.load(keyEvent != null ? keyEvent.getDeviceId() : -1).getKeyboardType() == 1) {
            z = false;
        }
        menu.setQwertyMode(z);
        return menu.performShortcut(i, keyEvent, 0);
    }
}
