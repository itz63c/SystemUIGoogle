package com.android.systemui.p007qs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.DetailAdapter;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import java.util.Objects;

/* renamed from: com.android.systemui.qs.QSDetail */
public class QSDetail extends LinearLayout {
    /* access modifiers changed from: private */
    public boolean mAnimatingOpen;
    private QSDetailClipper mClipper;
    /* access modifiers changed from: private */
    public boolean mClosingDetail;
    /* access modifiers changed from: private */
    public DetailAdapter mDetailAdapter;
    /* access modifiers changed from: private */
    public ViewGroup mDetailContent;
    protected TextView mDetailDoneButton;
    protected TextView mDetailSettingsButton;
    private final SparseArray<View> mDetailViews = new SparseArray<>();
    /* access modifiers changed from: private */
    public View mFooter;
    private boolean mFullyExpanded;
    /* access modifiers changed from: private */
    public QuickStatusBarHeader mHeader;
    private final AnimatorListenerAdapter mHideGridContentWhenDone = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animator) {
            animator.removeListener(this);
            QSDetail.this.mAnimatingOpen = false;
            QSDetail.this.checkPendingAnimations();
        }

        public void onAnimationEnd(Animator animator) {
            if (QSDetail.this.mDetailAdapter != null) {
                QSDetail.this.mQsPanel.setGridContentVisibility(false);
                QSDetail.this.mHeader.setVisibility(4);
                QSDetail.this.mFooter.setVisibility(4);
            }
            QSDetail.this.mAnimatingOpen = false;
            QSDetail.this.checkPendingAnimations();
        }
    };
    private int mOpenX;
    private int mOpenY;
    protected View mQsDetailHeader;
    protected ImageView mQsDetailHeaderProgress;
    /* access modifiers changed from: private */
    public Switch mQsDetailHeaderSwitch;
    private ViewStub mQsDetailHeaderSwitchStub;
    protected TextView mQsDetailHeaderTitle;
    /* access modifiers changed from: private */
    public QSPanel mQsPanel;
    protected Callback mQsPanelCallback = new Callback() {
        public void onToggleStateChanged(final boolean z) {
            QSDetail.this.post(new Runnable() {
                public void run() {
                    QSDetail qSDetail = QSDetail.this;
                    qSDetail.handleToggleStateChanged(z, qSDetail.mDetailAdapter != null && QSDetail.this.mDetailAdapter.getToggleEnabled());
                }
            });
        }

        public void onShowingDetail(final DetailAdapter detailAdapter, final int i, final int i2) {
            QSDetail.this.post(new Runnable() {
                public void run() {
                    if (QSDetail.this.isAttachedToWindow()) {
                        QSDetail.this.handleShowingDetail(detailAdapter, i, i2, false);
                    }
                }
            });
        }

        public void onScanStateChanged(final boolean z) {
            QSDetail.this.post(new Runnable() {
                public void run() {
                    QSDetail.this.handleScanStateChanged(z);
                }
            });
        }
    };
    private boolean mScanState;
    private boolean mSwitchState;
    private final AnimatorListenerAdapter mTeardownDetailWhenDone = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            QSDetail.this.mDetailContent.removeAllViews();
            QSDetail.this.setVisibility(4);
            QSDetail.this.mClosingDetail = false;
        }
    };
    private boolean mTriggeredExpand;

    /* renamed from: com.android.systemui.qs.QSDetail$Callback */
    public interface Callback {
        void onScanStateChanged(boolean z);

        void onShowingDetail(DetailAdapter detailAdapter, int i, int i2);

        void onToggleStateChanged(boolean z);
    }

    public void setHost(QSTileHost qSTileHost) {
    }

    public QSDetail(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FontSizeUtils.updateFontSize(this.mDetailDoneButton, C2009R$dimen.qs_detail_button_text_size);
        FontSizeUtils.updateFontSize(this.mDetailSettingsButton, C2009R$dimen.qs_detail_button_text_size);
        for (int i = 0; i < this.mDetailViews.size(); i++) {
            ((View) this.mDetailViews.valueAt(i)).dispatchConfigurationChanged(configuration);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDetailContent = (ViewGroup) findViewById(16908290);
        this.mDetailSettingsButton = (TextView) findViewById(16908314);
        this.mDetailDoneButton = (TextView) findViewById(16908313);
        View findViewById = findViewById(C2011R$id.qs_detail_header);
        this.mQsDetailHeader = findViewById;
        this.mQsDetailHeaderTitle = (TextView) findViewById.findViewById(16908310);
        this.mQsDetailHeaderSwitchStub = (ViewStub) this.mQsDetailHeader.findViewById(C2011R$id.toggle_stub);
        this.mQsDetailHeaderProgress = (ImageView) findViewById(C2011R$id.qs_detail_header_progress);
        updateDetailText();
        this.mClipper = new QSDetailClipper(this);
        this.mDetailDoneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                QSDetail qSDetail = QSDetail.this;
                qSDetail.announceForAccessibility(qSDetail.mContext.getString(C2017R$string.accessibility_desc_quick_settings));
                QSDetail.this.mQsPanel.closeDetail();
            }
        });
    }

    public void setQsPanel(QSPanel qSPanel, QuickStatusBarHeader quickStatusBarHeader, View view) {
        this.mQsPanel = qSPanel;
        this.mHeader = quickStatusBarHeader;
        this.mFooter = view;
        quickStatusBarHeader.setCallback(this.mQsPanelCallback);
        this.mQsPanel.setCallback(this.mQsPanelCallback);
    }

    public boolean isShowingDetail() {
        return this.mDetailAdapter != null;
    }

    public void setFullyExpanded(boolean z) {
        this.mFullyExpanded = z;
    }

    public void setExpanded(boolean z) {
        if (!z) {
            this.mTriggeredExpand = false;
        }
    }

    private void updateDetailText() {
        this.mDetailDoneButton.setText(C2017R$string.quick_settings_done);
        this.mDetailSettingsButton.setText(C2017R$string.quick_settings_more_settings);
    }

    public boolean isClosingDetail() {
        return this.mClosingDetail;
    }

    public void handleShowingDetail(DetailAdapter detailAdapter, int i, int i2, boolean z) {
        AnimatorListenerAdapter animatorListenerAdapter;
        boolean z2 = detailAdapter != null;
        setClickable(z2);
        if (z2) {
            setupDetailHeader(detailAdapter);
            if (!z || this.mFullyExpanded) {
                this.mTriggeredExpand = false;
            } else {
                this.mTriggeredExpand = true;
                ((CommandQueue) Dependency.get(CommandQueue.class)).animateExpandSettingsPanel(null);
            }
            this.mOpenX = i;
            this.mOpenY = i2;
        } else {
            i = this.mOpenX;
            i2 = this.mOpenY;
            if (z && this.mTriggeredExpand) {
                ((CommandQueue) Dependency.get(CommandQueue.class)).animateCollapsePanels();
                this.mTriggeredExpand = false;
            }
        }
        boolean z3 = (this.mDetailAdapter != null) != (detailAdapter != null);
        if (z3 || this.mDetailAdapter != detailAdapter) {
            if (detailAdapter != null) {
                int metricsCategory = detailAdapter.getMetricsCategory();
                View createDetailView = detailAdapter.createDetailView(this.mContext, (View) this.mDetailViews.get(metricsCategory), this.mDetailContent);
                if (createDetailView != null) {
                    setupDetailFooter(detailAdapter);
                    this.mDetailContent.removeAllViews();
                    this.mDetailContent.addView(createDetailView);
                    this.mDetailViews.put(metricsCategory, createDetailView);
                    ((MetricsLogger) Dependency.get(MetricsLogger.class)).visible(detailAdapter.getMetricsCategory());
                    announceForAccessibility(this.mContext.getString(C2017R$string.accessibility_quick_settings_detail, new Object[]{detailAdapter.getTitle()}));
                    this.mDetailAdapter = detailAdapter;
                    animatorListenerAdapter = this.mHideGridContentWhenDone;
                    setVisibility(0);
                } else {
                    throw new IllegalStateException("Must return detail view");
                }
            } else {
                if (this.mDetailAdapter != null) {
                    ((MetricsLogger) Dependency.get(MetricsLogger.class)).hidden(this.mDetailAdapter.getMetricsCategory());
                }
                this.mClosingDetail = true;
                this.mDetailAdapter = null;
                animatorListenerAdapter = this.mTeardownDetailWhenDone;
                this.mHeader.setVisibility(0);
                this.mFooter.setVisibility(0);
                this.mQsPanel.setGridContentVisibility(true);
                this.mQsPanelCallback.onScanStateChanged(false);
            }
            sendAccessibilityEvent(32);
            animateDetailVisibleDiff(i, i2, z3, animatorListenerAdapter);
        }
    }

    /* access modifiers changed from: protected */
    public void animateDetailVisibleDiff(int i, int i2, boolean z, AnimatorListener animatorListener) {
        if (z) {
            boolean z2 = true;
            this.mAnimatingOpen = this.mDetailAdapter != null;
            if (this.mFullyExpanded || this.mDetailAdapter != null) {
                setAlpha(1.0f);
                QSDetailClipper qSDetailClipper = this.mClipper;
                if (this.mDetailAdapter == null) {
                    z2 = false;
                }
                qSDetailClipper.animateCircularClip(i, i2, z2, animatorListener);
                return;
            }
            animate().alpha(0.0f).setDuration(300).setListener(animatorListener).start();
        }
    }

    /* access modifiers changed from: protected */
    public void setupDetailFooter(DetailAdapter detailAdapter) {
        Intent settingsIntent = detailAdapter.getSettingsIntent();
        this.mDetailSettingsButton.setVisibility(settingsIntent != null ? 0 : 8);
        this.mDetailSettingsButton.setOnClickListener(new OnClickListener(settingsIntent) {
            public final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                QSDetail.lambda$setupDetailFooter$0(DetailAdapter.this, this.f$1, view);
            }
        });
    }

    static /* synthetic */ void lambda$setupDetailFooter$0(DetailAdapter detailAdapter, Intent intent, View view) {
        ((MetricsLogger) Dependency.get(MetricsLogger.class)).action(929, detailAdapter.getMetricsCategory());
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(intent, 0);
    }

    /* access modifiers changed from: protected */
    public void setupDetailHeader(final DetailAdapter detailAdapter) {
        this.mQsDetailHeaderTitle.setText(detailAdapter.getTitle());
        Boolean toggleState = detailAdapter.getToggleState();
        if (toggleState == null) {
            Switch switchR = this.mQsDetailHeaderSwitch;
            if (switchR != null) {
                switchR.setVisibility(4);
            }
            this.mQsDetailHeader.setClickable(false);
            return;
        }
        if (this.mQsDetailHeaderSwitch == null) {
            this.mQsDetailHeaderSwitch = (Switch) this.mQsDetailHeaderSwitchStub.inflate();
        }
        this.mQsDetailHeaderSwitch.setVisibility(0);
        handleToggleStateChanged(toggleState.booleanValue(), detailAdapter.getToggleEnabled());
        this.mQsDetailHeader.setClickable(true);
        this.mQsDetailHeader.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                boolean z = !QSDetail.this.mQsDetailHeaderSwitch.isChecked();
                QSDetail.this.mQsDetailHeaderSwitch.setChecked(z);
                detailAdapter.setToggleState(z);
            }
        });
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        Pair cornerCutoutMargins = StatusBarWindowView.cornerCutoutMargins(windowInsets.getDisplayCutout(), getDisplay());
        if (cornerCutoutMargins == null) {
            this.mQsDetailHeader.setPaddingRelative(getResources().getDimensionPixelSize(C2009R$dimen.qs_detail_header_padding), getPaddingTop(), getResources().getDimensionPixelSize(C2009R$dimen.qs_detail_header_padding), getPaddingBottom());
        } else {
            this.mQsDetailHeader.setPadding(((Integer) cornerCutoutMargins.first).intValue(), getPaddingTop(), ((Integer) cornerCutoutMargins.second).intValue(), getPaddingBottom());
        }
        return super.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: private */
    public void handleToggleStateChanged(boolean z, boolean z2) {
        this.mSwitchState = z;
        if (!this.mAnimatingOpen) {
            Switch switchR = this.mQsDetailHeaderSwitch;
            if (switchR != null) {
                switchR.setChecked(z);
            }
            this.mQsDetailHeader.setEnabled(z2);
            Switch switchR2 = this.mQsDetailHeaderSwitch;
            if (switchR2 != null) {
                switchR2.setEnabled(z2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleScanStateChanged(boolean z) {
        if (this.mScanState != z) {
            this.mScanState = z;
            Animatable animatable = (Animatable) this.mQsDetailHeaderProgress.getDrawable();
            if (z) {
                this.mQsDetailHeaderProgress.animate().cancel();
                ViewPropertyAnimator alpha = this.mQsDetailHeaderProgress.animate().alpha(1.0f);
                Objects.requireNonNull(animatable);
                alpha.withEndAction(new Runnable(animatable) {
                    public final /* synthetic */ Animatable f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.start();
                    }
                }).start();
            } else {
                this.mQsDetailHeaderProgress.animate().cancel();
                ViewPropertyAnimator alpha2 = this.mQsDetailHeaderProgress.animate().alpha(0.0f);
                Objects.requireNonNull(animatable);
                alpha2.withEndAction(new Runnable(animatable) {
                    public final /* synthetic */ Animatable f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.stop();
                    }
                }).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkPendingAnimations() {
        boolean z = this.mSwitchState;
        DetailAdapter detailAdapter = this.mDetailAdapter;
        handleToggleStateChanged(z, detailAdapter != null && detailAdapter.getToggleEnabled());
    }
}
