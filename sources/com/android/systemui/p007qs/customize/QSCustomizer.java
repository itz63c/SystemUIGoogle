package com.android.systemui.p007qs.customize;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toolbar;
import android.widget.Toolbar.OnMenuItemClickListener;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.State;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.C2018R$style;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.p007qs.QSDetailClipper;
import com.android.systemui.p007qs.QSTileHost;
import com.android.systemui.plugins.p006qs.C0940QS;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;
import java.util.ArrayList;

/* renamed from: com.android.systemui.qs.customize.QSCustomizer */
public class QSCustomizer extends LinearLayout implements OnMenuItemClickListener {
    /* access modifiers changed from: private */
    public boolean isShown;
    private final QSDetailClipper mClipper;
    private final AnimatorListener mCollapseAnimationListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            if (!QSCustomizer.this.isShown) {
                QSCustomizer.this.setVisibility(8);
            }
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            QSCustomizer.this.mRecyclerView.setAdapter(QSCustomizer.this.mTileAdapter);
        }

        public void onAnimationCancel(Animator animator) {
            if (!QSCustomizer.this.isShown) {
                QSCustomizer.this.setVisibility(8);
            }
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
        }
    };
    private boolean mCustomizing;
    private final AnimatorListener mExpandAnimationListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            if (QSCustomizer.this.isShown) {
                QSCustomizer.this.setCustomizing(true);
            }
            QSCustomizer.this.mOpening = false;
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
        }

        public void onAnimationCancel(Animator animator) {
            QSCustomizer.this.mOpening = false;
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
        }
    };
    private QSTileHost mHost;
    private boolean mIsShowingNavBackdrop;
    private final Callback mKeyguardCallback = new Callback() {
        public void onKeyguardShowingChanged() {
            if (QSCustomizer.this.isAttachedToWindow() && QSCustomizer.this.mKeyguardStateController.isShowing() && !QSCustomizer.this.mOpening) {
                QSCustomizer.this.hide();
            }
        }
    };
    /* access modifiers changed from: private */
    public KeyguardStateController mKeyguardStateController;
    private final LightBarController mLightBarController;
    /* access modifiers changed from: private */
    public NotificationsQuickSettingsContainer mNotifQsContainer;
    /* access modifiers changed from: private */
    public boolean mOpening;
    private C0940QS mQs;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;
    private final ScreenLifecycle mScreenLifecycle;
    /* access modifiers changed from: private */
    public TileAdapter mTileAdapter;
    private final TileQueryHelper mTileQueryHelper;
    private Toolbar mToolbar;
    private final View mTransparentView;
    private UiEventLogger mUiEventLogger = new UiEventLoggerImpl();

    /* renamed from: mX */
    private int f67mX;

    /* renamed from: mY */
    private int f68mY;

    public QSCustomizer(Context context, AttributeSet attributeSet, LightBarController lightBarController, KeyguardStateController keyguardStateController, ScreenLifecycle screenLifecycle, TileQueryHelper tileQueryHelper) {
        super(new ContextThemeWrapper(context, C2018R$style.edit_theme), attributeSet);
        LayoutInflater.from(getContext()).inflate(C2013R$layout.qs_customize_panel_content, this);
        this.mClipper = new QSDetailClipper(findViewById(C2011R$id.customize_container));
        this.mToolbar = (Toolbar) findViewById(16908704);
        TypedValue typedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(16843531, typedValue, true);
        this.mToolbar.setNavigationIcon(getResources().getDrawable(typedValue.resourceId, this.mContext.getTheme()));
        this.mToolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                QSCustomizer.this.hide();
            }
        });
        this.mToolbar.setOnMenuItemClickListener(this);
        this.mToolbar.getMenu().add(0, 1, 0, this.mContext.getString(17041019));
        this.mToolbar.setTitle(C2017R$string.qs_edit);
        this.mRecyclerView = (RecyclerView) findViewById(16908298);
        this.mTransparentView = findViewById(C2011R$id.customizer_transparent_view);
        TileAdapter tileAdapter = new TileAdapter(getContext());
        this.mTileAdapter = tileAdapter;
        this.mTileQueryHelper = tileQueryHelper;
        tileQueryHelper.setListener(tileAdapter);
        this.mRecyclerView.setAdapter(this.mTileAdapter);
        this.mTileAdapter.getItemTouchHelper().attachToRecyclerView(this.mRecyclerView);
        C09942 r4 = new GridLayoutManager(this, getContext(), 3) {
            public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            }
        };
        r4.setSpanSizeLookup(this.mTileAdapter.getSizeLookup());
        this.mRecyclerView.setLayoutManager(r4);
        this.mRecyclerView.addItemDecoration(this.mTileAdapter.getItemDecoration());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setMoveDuration(150);
        this.mRecyclerView.setItemAnimator(defaultItemAnimator);
        this.mLightBarController = lightBarController;
        this.mKeyguardStateController = keyguardStateController;
        this.mScreenLifecycle = screenLifecycle;
        updateNavBackDrop(getResources().getConfiguration());
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateNavBackDrop(configuration);
        updateResources();
    }

    private void updateResources() {
        LayoutParams layoutParams = (LayoutParams) this.mTransparentView.getLayoutParams();
        layoutParams.height = this.mContext.getResources().getDimensionPixelSize(17105418);
        this.mTransparentView.setLayoutParams(layoutParams);
    }

    private void updateNavBackDrop(Configuration configuration) {
        View findViewById = findViewById(C2011R$id.nav_bar_background);
        int i = 0;
        boolean z = configuration.smallestScreenWidthDp >= 600 || configuration.orientation != 2;
        this.mIsShowingNavBackdrop = z;
        if (findViewById != null) {
            if (!z) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
        updateNavColors();
    }

    private void updateNavColors() {
        this.mLightBarController.setQsCustomizing(this.mIsShowingNavBackdrop && this.isShown);
    }

    public void setHost(QSTileHost qSTileHost) {
        this.mHost = qSTileHost;
        this.mTileAdapter.setHost(qSTileHost);
    }

    public void setContainer(NotificationsQuickSettingsContainer notificationsQuickSettingsContainer) {
        this.mNotifQsContainer = notificationsQuickSettingsContainer;
    }

    public void setQs(C0940QS qs) {
        this.mQs = qs;
    }

    public void show(int i, int i2) {
        if (!this.isShown) {
            int[] locationOnScreen = findViewById(C2011R$id.customize_container).getLocationOnScreen();
            this.f67mX = i - locationOnScreen[0];
            this.f68mY = i2 - locationOnScreen[1];
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_OPEN);
            this.isShown = true;
            this.mOpening = true;
            setTileSpecs();
            setVisibility(0);
            this.mClipper.animateCircularClip(this.f67mX, this.f68mY, true, this.mExpandAnimationListener);
            queryTiles();
            this.mNotifQsContainer.setCustomizerAnimating(true);
            this.mNotifQsContainer.setCustomizerShowing(true);
            this.mKeyguardStateController.addCallback(this.mKeyguardCallback);
            updateNavColors();
        }
    }

    public void showImmediately() {
        if (!this.isShown) {
            setVisibility(0);
            this.mClipper.showBackground();
            this.isShown = true;
            setTileSpecs();
            setCustomizing(true);
            queryTiles();
            this.mNotifQsContainer.setCustomizerAnimating(false);
            this.mNotifQsContainer.setCustomizerShowing(true);
            this.mKeyguardStateController.addCallback(this.mKeyguardCallback);
            updateNavColors();
        }
    }

    private void queryTiles() {
        this.mTileQueryHelper.queryTiles(this.mHost);
    }

    public void hide() {
        boolean z = this.mScreenLifecycle.getScreenState() != 0;
        if (this.isShown) {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_CLOSED);
            this.isShown = false;
            this.mToolbar.dismissPopupMenus();
            setCustomizing(false);
            save();
            if (z) {
                this.mClipper.animateCircularClip(this.f67mX, this.f68mY, false, this.mCollapseAnimationListener);
            } else {
                setVisibility(8);
            }
            this.mNotifQsContainer.setCustomizerAnimating(z);
            this.mNotifQsContainer.setCustomizerShowing(false);
            this.mKeyguardStateController.removeCallback(this.mKeyguardCallback);
            updateNavColors();
        }
    }

    public boolean isShown() {
        return this.isShown;
    }

    /* access modifiers changed from: private */
    public void setCustomizing(boolean z) {
        this.mCustomizing = z;
        this.mQs.notifyCustomizeChanged();
    }

    public boolean isCustomizing() {
        return this.mCustomizing || this.mOpening;
    }

    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_RESET);
            reset();
        }
        return false;
    }

    private void reset() {
        this.mTileAdapter.resetTileSpecs(this.mHost, QSTileHost.getDefaultSpecs(this.mContext));
    }

    private void setTileSpecs() {
        ArrayList arrayList = new ArrayList();
        for (QSTile tileSpec : this.mHost.getTiles()) {
            arrayList.add(tileSpec.getTileSpec());
        }
        this.mTileAdapter.setTileSpecs(arrayList);
        this.mRecyclerView.setAdapter(this.mTileAdapter);
    }

    private void save() {
        if (this.mTileQueryHelper.isFinished()) {
            this.mTileAdapter.saveSpecs(this.mHost);
        }
    }

    public void saveInstanceState(Bundle bundle) {
        if (this.isShown) {
            this.mKeyguardStateController.removeCallback(this.mKeyguardCallback);
        }
        bundle.putBoolean("qs_customizing", this.mCustomizing);
    }

    public void restoreInstanceState(Bundle bundle) {
        if (bundle.getBoolean("qs_customizing")) {
            setVisibility(0);
            addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    QSCustomizer.this.removeOnLayoutChangeListener(this);
                    QSCustomizer.this.showImmediately();
                }
            });
        }
    }

    public void setEditLocation(int i, int i2) {
        int[] locationOnScreen = findViewById(C2011R$id.customize_container).getLocationOnScreen();
        this.f67mX = i - locationOnScreen[0];
        this.f68mY = i2 - locationOnScreen[1];
    }
}
