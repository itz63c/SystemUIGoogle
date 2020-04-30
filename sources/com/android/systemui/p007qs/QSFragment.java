package com.android.systemui.p007qs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2018R$style;
import com.android.systemui.Interpolators;
import com.android.systemui.p007qs.QSContainerImplController.Builder;
import com.android.systemui.p007qs.customize.QSCustomizer;
import com.android.systemui.plugins.p006qs.C0940QS;
import com.android.systemui.plugins.p006qs.C0940QS.HeightListener;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.util.LifecycleFragment;

/* renamed from: com.android.systemui.qs.QSFragment */
public class QSFragment extends LifecycleFragment implements C0940QS, Callbacks, StateListener {
    /* access modifiers changed from: private */
    public final AnimatorListener mAnimateHeaderSlidingInListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            QSFragment.this.mHeaderAnimating = false;
            QSFragment.this.updateQsState();
        }
    };
    private QSContainerImpl mContainer;
    /* access modifiers changed from: private */
    public long mDelay;
    private QSFooter mFooter;
    protected QuickStatusBarHeader mHeader;
    /* access modifiers changed from: private */
    public boolean mHeaderAnimating;
    private final QSTileHost mHost;
    private final InjectionInflationController mInjectionInflater;
    private boolean mLastKeyguardAndExpanded;
    private float mLastQSExpansion = -1.0f;
    private int mLayoutDirection;
    private boolean mListening;
    private HeightListener mPanelView;
    private QSAnimator mQSAnimator;
    private QSContainerImplController mQSContainerImplController;
    private final Builder mQSContainerImplControllerBuilder;
    private QSCustomizer mQSCustomizer;
    private QSDetail mQSDetail;
    protected QSPanel mQSPanel;
    private final Rect mQsBounds = new Rect();
    private boolean mQsDisabled;
    private boolean mQsExpanded;
    private final RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler;
    private boolean mShowCollapsedOnKeyguard;
    private boolean mStackScrollerOverscrolling;
    private final OnPreDrawListener mStartHeaderSlidingIn = new OnPreDrawListener() {
        public boolean onPreDraw() {
            QSFragment.this.getView().getViewTreeObserver().removeOnPreDrawListener(this);
            QSFragment.this.getView().animate().translationY(0.0f).setStartDelay(QSFragment.this.mDelay).setDuration(448).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setListener(QSFragment.this.mAnimateHeaderSlidingInListener).start();
            return true;
        }
    };
    private int mState;
    private final StatusBarStateController mStatusBarStateController;

    public void setHasNotifications(boolean z) {
    }

    public void setHeaderClickable(boolean z) {
    }

    public QSFragment(RemoteInputQuickSettingsDisabler remoteInputQuickSettingsDisabler, InjectionInflationController injectionInflationController, QSTileHost qSTileHost, StatusBarStateController statusBarStateController, CommandQueue commandQueue, Builder builder) {
        this.mRemoteInputQuickSettingsDisabler = remoteInputQuickSettingsDisabler;
        this.mInjectionInflater = injectionInflationController;
        this.mQSContainerImplControllerBuilder = builder;
        commandQueue.observe(getLifecycle(), this);
        this.mHost = qSTileHost;
        this.mStatusBarStateController = statusBarStateController;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.mInjectionInflater.injectable(layoutInflater.cloneInContext(new ContextThemeWrapper(getContext(), C2018R$style.qs_theme))).inflate(C2013R$layout.qs_panel, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mQSPanel = (QSPanel) view.findViewById(C2011R$id.quick_settings_panel);
        this.mQSDetail = (QSDetail) view.findViewById(C2011R$id.qs_detail);
        this.mHeader = (QuickStatusBarHeader) view.findViewById(C2011R$id.header);
        this.mFooter = (QSFooter) view.findViewById(C2011R$id.qs_footer);
        this.mContainer = (QSContainerImpl) view.findViewById(C2011R$id.quick_settings_container);
        Builder builder = this.mQSContainerImplControllerBuilder;
        builder.setQSContainerImpl((QSContainerImpl) view);
        this.mQSContainerImplController = builder.build();
        this.mQSDetail.setQsPanel(this.mQSPanel, this.mHeader, (View) this.mFooter);
        this.mQSAnimator = new QSAnimator(this, (QuickQSPanel) this.mHeader.findViewById(C2011R$id.quick_qs_panel), this.mQSPanel);
        QSCustomizer qSCustomizer = (QSCustomizer) view.findViewById(C2011R$id.qs_customize);
        this.mQSCustomizer = qSCustomizer;
        qSCustomizer.setQs(this);
        if (bundle != null) {
            setExpanded(bundle.getBoolean("expanded"));
            setListening(bundle.getBoolean("listening"));
            setEditLocation(view);
            this.mQSCustomizer.restoreInstanceState(bundle);
            if (this.mQsExpanded) {
                this.mQSPanel.getTileLayout().restoreInstanceState(bundle);
            }
        }
        setHost(this.mHost);
        this.mStatusBarStateController.addCallback(this);
        onStateChanged(this.mStatusBarStateController.getState());
    }

    public void onDestroy() {
        super.onDestroy();
        this.mStatusBarStateController.removeCallback(this);
        if (this.mListening) {
            setListening(false);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("expanded", this.mQsExpanded);
        bundle.putBoolean("listening", this.mListening);
        this.mQSCustomizer.saveInstanceState(bundle);
        if (this.mQsExpanded) {
            this.mQSPanel.getTileLayout().saveInstanceState(bundle);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isListening() {
        return this.mListening;
    }

    /* access modifiers changed from: 0000 */
    public boolean isExpanded() {
        return this.mQsExpanded;
    }

    public View getHeader() {
        return this.mHeader;
    }

    public void setPanelView(HeightListener heightListener) {
        this.mPanelView = heightListener;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setEditLocation(getView());
        if (configuration.getLayoutDirection() != this.mLayoutDirection) {
            this.mLayoutDirection = configuration.getLayoutDirection();
            QSAnimator qSAnimator = this.mQSAnimator;
            if (qSAnimator != null) {
                qSAnimator.onRtlChanged();
            }
        }
    }

    private void setEditLocation(View view) {
        View findViewById = view.findViewById(16908291);
        int[] locationOnScreen = findViewById.getLocationOnScreen();
        this.mQSCustomizer.setEditLocation(locationOnScreen[0] + (findViewById.getWidth() / 2), locationOnScreen[1] + (findViewById.getHeight() / 2));
    }

    public void setContainer(ViewGroup viewGroup) {
        if (viewGroup instanceof NotificationsQuickSettingsContainer) {
            this.mQSCustomizer.setContainer((NotificationsQuickSettingsContainer) viewGroup);
        }
    }

    public boolean isCustomizing() {
        return this.mQSCustomizer.isCustomizing();
    }

    public void setHost(QSTileHost qSTileHost) {
        this.mQSPanel.setHost(qSTileHost, this.mQSCustomizer);
        this.mHeader.setQSPanel(this.mQSPanel);
        this.mFooter.setQSPanel(this.mQSPanel);
        this.mQSDetail.setHost(qSTileHost);
        QSAnimator qSAnimator = this.mQSAnimator;
        if (qSAnimator != null) {
            qSAnimator.setHost(qSTileHost);
        }
    }

    public void disable(int i, int i2, int i3, boolean z) {
        if (i == getContext().getDisplayId()) {
            int adjustDisableFlags = this.mRemoteInputQuickSettingsDisabler.adjustDisableFlags(i3);
            boolean z2 = (adjustDisableFlags & 1) != 0;
            if (z2 != this.mQsDisabled) {
                this.mQsDisabled = z2;
                this.mContainer.disable(i2, adjustDisableFlags, z);
                this.mHeader.disable(i2, adjustDisableFlags, z);
                this.mFooter.disable(i2, adjustDisableFlags, z);
                updateQsState();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateQsState() {
        boolean z = true;
        int i = 0;
        boolean z2 = this.mQsExpanded || this.mStackScrollerOverscrolling || this.mHeaderAnimating;
        this.mQSPanel.setExpanded(this.mQsExpanded);
        this.mQSDetail.setExpanded(this.mQsExpanded);
        boolean isKeyguardShowing = isKeyguardShowing();
        this.mHeader.setVisibility((this.mQsExpanded || !isKeyguardShowing || this.mHeaderAnimating || this.mShowCollapsedOnKeyguard) ? 0 : 4);
        this.mHeader.setExpanded((isKeyguardShowing && !this.mHeaderAnimating && !this.mShowCollapsedOnKeyguard) || (this.mQsExpanded && !this.mStackScrollerOverscrolling));
        this.mFooter.setVisibility((this.mQsDisabled || (!this.mQsExpanded && isKeyguardShowing && !this.mHeaderAnimating && !this.mShowCollapsedOnKeyguard)) ? 4 : 0);
        QSFooter qSFooter = this.mFooter;
        if ((!isKeyguardShowing || this.mHeaderAnimating || this.mShowCollapsedOnKeyguard) && (!this.mQsExpanded || this.mStackScrollerOverscrolling)) {
            z = false;
        }
        qSFooter.setExpanded(z);
        QSPanel qSPanel = this.mQSPanel;
        if (this.mQsDisabled || !z2) {
            i = 4;
        }
        qSPanel.setVisibility(i);
    }

    private boolean isKeyguardShowing() {
        return this.mStatusBarStateController.getState() == 1;
    }

    public void setShowCollapsedOnKeyguard(boolean z) {
        if (z != this.mShowCollapsedOnKeyguard) {
            this.mShowCollapsedOnKeyguard = z;
            updateQsState();
            QSAnimator qSAnimator = this.mQSAnimator;
            if (qSAnimator != null) {
                qSAnimator.setShowCollapsedOnKeyguard(z);
            }
            if (!z && isKeyguardShowing()) {
                setQsExpansion(this.mLastQSExpansion, 0.0f);
            }
        }
    }

    public QSPanel getQsPanel() {
        return this.mQSPanel;
    }

    public boolean isShowingDetail() {
        return this.mQSPanel.isShowingCustomize() || this.mQSDetail.isShowingDetail();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return isCustomizing();
    }

    public void setExpanded(boolean z) {
        this.mQsExpanded = z;
        this.mQSPanel.setListening(this.mListening, z);
        updateQsState();
    }

    private void setKeyguardShowing(boolean z) {
        this.mLastQSExpansion = -1.0f;
        QSAnimator qSAnimator = this.mQSAnimator;
        if (qSAnimator != null) {
            qSAnimator.setOnKeyguard(z);
        }
        this.mFooter.setKeyguardShowing(z);
        updateQsState();
    }

    public void setOverscrolling(boolean z) {
        this.mStackScrollerOverscrolling = z;
        updateQsState();
    }

    public void setListening(boolean z) {
        this.mListening = z;
        this.mQSContainerImplController.setListening(z);
        this.mHeader.setListening(z);
        this.mFooter.setListening(z);
        this.mQSPanel.setListening(this.mListening, this.mQsExpanded);
    }

    public void setHeaderListening(boolean z) {
        this.mHeader.setListening(z);
        this.mFooter.setListening(z);
    }

    public void setQsExpansion(float f, float f2) {
        this.mContainer.setExpansion(f);
        float f3 = 1.0f;
        float f4 = f - 1.0f;
        boolean z = true;
        boolean z2 = isKeyguardShowing() && !this.mShowCollapsedOnKeyguard;
        if (!this.mHeaderAnimating && !headerWillBeAnimating()) {
            View view = getView();
            if (z2) {
                f2 = ((float) this.mHeader.getHeight()) * f4;
            }
            view.setTranslationY(f2);
        }
        if (f != this.mLastQSExpansion || this.mLastKeyguardAndExpanded != z2) {
            this.mLastQSExpansion = f;
            this.mLastKeyguardAndExpanded = z2;
            if (f != 1.0f) {
                z = false;
            }
            float bottom = f4 * ((float) ((this.mQSPanel.getBottom() - this.mHeader.getBottom()) + this.mHeader.getPaddingBottom() + this.mFooter.getHeight()));
            this.mHeader.setExpansion(z2, f, bottom);
            QSFooter qSFooter = this.mFooter;
            if (!z2) {
                f3 = f;
            }
            qSFooter.setExpansion(f3);
            this.mQSPanel.getQsTileRevealController().setExpansion(f);
            this.mQSPanel.getTileLayout().setExpansion(f);
            this.mQSPanel.setTranslationY(bottom);
            this.mQSDetail.setFullyExpanded(z);
            if (z) {
                this.mQSPanel.setClipBounds(null);
            } else {
                this.mQsBounds.top = (int) (-this.mQSPanel.getTranslationY());
                this.mQsBounds.right = this.mQSPanel.getWidth();
                this.mQsBounds.bottom = this.mQSPanel.getHeight();
                this.mQSPanel.setClipBounds(this.mQsBounds);
            }
            QSAnimator qSAnimator = this.mQSAnimator;
            if (qSAnimator != null) {
                qSAnimator.setPosition(f);
            }
        }
    }

    private boolean headerWillBeAnimating() {
        if (this.mState != 1 || !this.mShowCollapsedOnKeyguard || isKeyguardShowing()) {
            return false;
        }
        return true;
    }

    public void animateHeaderSlidingIn(long j) {
        if (!this.mQsExpanded && getView().getTranslationY() != 0.0f) {
            this.mHeaderAnimating = true;
            this.mDelay = j;
            getView().getViewTreeObserver().addOnPreDrawListener(this.mStartHeaderSlidingIn);
        }
    }

    public void animateHeaderSlidingOut() {
        if (getView().getY() != ((float) (-this.mHeader.getHeight()))) {
            this.mHeaderAnimating = true;
            getView().animate().y((float) (-this.mHeader.getHeight())).setStartDelay(0).setDuration(360).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (QSFragment.this.getView() != null) {
                        QSFragment.this.getView().animate().setListener(null);
                    }
                    QSFragment.this.mHeaderAnimating = false;
                    QSFragment.this.updateQsState();
                }
            }).start();
        }
    }

    public void setExpandClickListener(OnClickListener onClickListener) {
        this.mFooter.setExpandClickListener(onClickListener);
    }

    public void closeDetail() {
        this.mQSPanel.closeDetail();
    }

    public void notifyCustomizeChanged() {
        this.mContainer.updateExpansion();
        int i = 0;
        this.mQSPanel.setVisibility(!this.mQSCustomizer.isCustomizing() ? 0 : 4);
        QSFooter qSFooter = this.mFooter;
        if (this.mQSCustomizer.isCustomizing()) {
            i = 4;
        }
        qSFooter.setVisibility(i);
        this.mPanelView.onQsHeightChanged();
    }

    public int getDesiredHeight() {
        if (this.mQSCustomizer.isCustomizing()) {
            return getView().getHeight();
        }
        if (!this.mQSDetail.isClosingDetail()) {
            return getView().getMeasuredHeight();
        }
        LayoutParams layoutParams = (LayoutParams) this.mQSPanel.getLayoutParams();
        return layoutParams.topMargin + layoutParams.bottomMargin + this.mQSPanel.getMeasuredHeight() + getView().getPaddingBottom();
    }

    public void setHeightOverride(int i) {
        this.mContainer.setHeightOverride(i);
    }

    public int getQsMinExpansionHeight() {
        return this.mHeader.getHeight();
    }

    public void hideImmediately() {
        getView().animate().cancel();
        getView().setY((float) (-this.mHeader.getHeight()));
    }

    public void onStateChanged(int i) {
        this.mState = i;
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        setKeyguardShowing(z);
    }
}
