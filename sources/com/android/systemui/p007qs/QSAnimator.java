package com.android.systemui.p007qs;

import android.util.Log;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnLayoutChangeListener;
import com.android.systemui.Dependency;
import com.android.systemui.p007qs.PagedTileLayout.PageListener;
import com.android.systemui.p007qs.PagedTileLayout.TilePage;
import com.android.systemui.p007qs.QSHost.Callback;
import com.android.systemui.p007qs.QSPanel.QSTileLayout;
import com.android.systemui.p007qs.TouchAnimator.Builder;
import com.android.systemui.p007qs.TouchAnimator.Listener;
import com.android.systemui.p007qs.TouchAnimator.ListenerAdapter;
import com.android.systemui.plugins.p006qs.C0940QS;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTileView;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/* renamed from: com.android.systemui.qs.QSAnimator */
public class QSAnimator implements Callback, PageListener, Listener, OnLayoutChangeListener, OnAttachStateChangeListener, Tunable {
    private final ArrayList<View> mAllViews = new ArrayList<>();
    private boolean mAllowFancy;
    private TouchAnimator mBrightnessAnimator;
    private TouchAnimator mFirstPageAnimator;
    private TouchAnimator mFirstPageDelayedAnimator;
    private boolean mFullRows;
    private QSTileHost mHost;
    private float mLastPosition;
    private final Listener mNonFirstPageListener = new ListenerAdapter() {
        public void onAnimationAtEnd() {
            QSAnimator.this.mQuickQsPanel.setVisibility(4);
        }

        public void onAnimationStarted() {
            QSAnimator.this.mQuickQsPanel.setVisibility(0);
        }
    };
    private TouchAnimator mNonfirstPageAnimator;
    private TouchAnimator mNonfirstPageDelayedAnimator;
    private int mNumQuickTiles;
    private boolean mOnFirstPage = true;
    private boolean mOnKeyguard;
    private PagedTileLayout mPagedLayout;
    private final C0940QS mQs;
    private final QSPanel mQsPanel;
    /* access modifiers changed from: private */
    public final QuickQSPanel mQuickQsPanel;
    private final ArrayList<View> mQuickQsViews = new ArrayList<>();
    private boolean mShowCollapsedOnKeyguard;
    private TouchAnimator mTranslationXAnimator;
    private TouchAnimator mTranslationYAnimator;
    private Runnable mUpdateAnimators = new Runnable() {
        public void run() {
            QSAnimator.this.updateAnimators();
            QSAnimator.this.setCurrentPosition();
        }
    };

    public QSAnimator(C0940QS qs, QuickQSPanel quickQSPanel, QSPanel qSPanel) {
        this.mQs = qs;
        this.mQuickQsPanel = quickQSPanel;
        this.mQsPanel = qSPanel;
        qSPanel.addOnAttachStateChangeListener(this);
        qs.getView().addOnLayoutChangeListener(this);
        if (this.mQsPanel.isAttachedToWindow()) {
            onViewAttachedToWindow(null);
        }
        QSTileLayout tileLayout = this.mQsPanel.getTileLayout();
        if (tileLayout instanceof PagedTileLayout) {
            this.mPagedLayout = (PagedTileLayout) tileLayout;
        } else {
            Log.w("QSAnimator", "QS Not using page layout");
        }
        qSPanel.setPageListener(this);
    }

    public void onRtlChanged() {
        updateAnimators();
    }

    public void setOnKeyguard(boolean z) {
        this.mOnKeyguard = z;
        updateQQSVisibility();
        if (this.mOnKeyguard) {
            clearAnimationState();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setShowCollapsedOnKeyguard(boolean z) {
        this.mShowCollapsedOnKeyguard = z;
        updateQQSVisibility();
        setCurrentPosition();
    }

    /* access modifiers changed from: private */
    public void setCurrentPosition() {
        setPosition(this.mLastPosition);
    }

    private void updateQQSVisibility() {
        this.mQuickQsPanel.setVisibility((!this.mOnKeyguard || this.mShowCollapsedOnKeyguard) ? 0 : 4);
    }

    public void setHost(QSTileHost qSTileHost) {
        this.mHost = qSTileHost;
        qSTileHost.addCallback(this);
        updateAnimators();
    }

    public void onViewAttachedToWindow(View view) {
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "sysui_qs_fancy_anim", "sysui_qs_move_whole_rows", "sysui_qqs_count");
    }

    public void onViewDetachedFromWindow(View view) {
        QSTileHost qSTileHost = this.mHost;
        if (qSTileHost != null) {
            qSTileHost.removeCallback(this);
        }
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
    }

    public void onTuningChanged(String str, String str2) {
        if ("sysui_qs_fancy_anim".equals(str)) {
            boolean parseIntegerSwitch = TunerService.parseIntegerSwitch(str2, true);
            this.mAllowFancy = parseIntegerSwitch;
            if (!parseIntegerSwitch) {
                clearAnimationState();
            }
        } else if ("sysui_qs_move_whole_rows".equals(str)) {
            this.mFullRows = TunerService.parseIntegerSwitch(str2, true);
        } else if ("sysui_qqs_count".equals(str)) {
            this.mNumQuickTiles = QuickQSPanel.parseNumTiles(str2);
            clearAnimationState();
        }
        updateAnimators();
    }

    public void onPageChanged(boolean z) {
        if (this.mOnFirstPage != z) {
            if (!z) {
                clearAnimationState();
            }
            this.mOnFirstPage = z;
        }
    }

    /* access modifiers changed from: private */
    public void updateAnimators() {
        String str;
        int i;
        QSTileLayout qSTileLayout;
        int i2;
        float f;
        Collection collection;
        int i3;
        int i4;
        QSTileLayout qSTileLayout2;
        int[] iArr;
        Builder builder = new Builder();
        Builder builder2 = new Builder();
        Builder builder3 = new Builder();
        if (this.mQsPanel.getHost() != null) {
            Collection tiles = this.mQsPanel.getHost().getTiles();
            int[] iArr2 = new int[2];
            int[] iArr3 = new int[2];
            clearAnimationState();
            this.mAllViews.clear();
            this.mQuickQsViews.clear();
            QSTileLayout tileLayout = this.mQsPanel.getTileLayout();
            this.mAllViews.add((View) tileLayout);
            int measuredHeight = this.mQs.getView() != null ? this.mQs.getView().getMeasuredHeight() : 0;
            int measuredWidth = this.mQs.getView() != null ? this.mQs.getView().getMeasuredWidth() : 0;
            int bottom = (measuredHeight - this.mQs.getHeader().getBottom()) + this.mQs.getHeader().getPaddingBottom();
            float f2 = (float) bottom;
            String str2 = "translationY";
            builder.addFloat(tileLayout, str2, f2, 0.0f);
            Iterator it = tiles.iterator();
            int i5 = 0;
            int i6 = 0;
            while (true) {
                str = "alpha";
                if (!it.hasNext()) {
                    break;
                }
                QSTile qSTile = (QSTile) it.next();
                Iterator it2 = it;
                QSTileView tileView = this.mQsPanel.getTileView(qSTile);
                if (tileView == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("tileView is null ");
                    sb.append(qSTile.getTileSpec());
                    Log.e("QSAnimator", sb.toString());
                    collection = tiles;
                    i3 = bottom;
                    i2 = measuredWidth;
                    f = f2;
                } else {
                    collection = tiles;
                    View iconView = tileView.getIcon().getIconView();
                    i3 = bottom;
                    View view = this.mQs.getView();
                    f = f2;
                    i2 = measuredWidth;
                    String str3 = "translationX";
                    if (i5 >= this.mQuickQsPanel.getTileLayout().getNumVisibleTiles() || !this.mAllowFancy) {
                        qSTileLayout2 = tileLayout;
                        if (!this.mFullRows || !isIconInAnimatedRow(i5)) {
                            iArr = iArr2;
                            builder.addFloat(tileView, str, 0.0f, 1.0f);
                            bottom = i3;
                            builder.addFloat(tileView, str2, (float) (-bottom), 0.0f);
                            this.mAllViews.add(tileView);
                            i5++;
                            it = it2;
                            tiles = collection;
                            f2 = f;
                            i4 = i2;
                            iArr2 = iArr;
                            tileLayout = qSTileLayout2;
                        } else {
                            iArr2[0] = iArr2[0] + i6;
                            getRelativePosition(iArr3, iconView, view);
                            int i7 = iArr3[0] - iArr2[0];
                            int i8 = iArr3[1] - iArr2[1];
                            iArr = iArr2;
                            builder.addFloat(tileView, str2, f, 0.0f);
                            builder2.addFloat(tileView, str3, (float) (-i7), 0.0f);
                            float f3 = (float) (-i8);
                            builder3.addFloat(tileView, str2, f3, 0.0f);
                            builder3.addFloat(iconView, str2, f3, 0.0f);
                            this.mAllViews.add(iconView);
                        }
                    } else {
                        QSTileView tileView2 = this.mQuickQsPanel.getTileView(qSTile);
                        if (tileView2 != null) {
                            int i9 = iArr2[0];
                            getRelativePosition(iArr2, tileView2.getIcon().getIconView(), view);
                            getRelativePosition(iArr3, iconView, view);
                            int i10 = iArr3[0] - iArr2[0];
                            int i11 = iArr3[1] - iArr2[1];
                            i6 = iArr2[0] - i9;
                            if (i5 < tileLayout.getNumVisibleTiles()) {
                                builder2.addFloat(tileView2, str3, 0.0f, (float) i10);
                                builder3.addFloat(tileView2, str2, 0.0f, (float) i11);
                                builder2.addFloat(tileView, str3, (float) (-i10), 0.0f);
                                builder3.addFloat(tileView, str2, (float) (-i11), 0.0f);
                                qSTileLayout2 = tileLayout;
                            } else {
                                qSTileLayout2 = tileLayout;
                                builder.addFloat(tileView2, str, 1.0f, 0.0f);
                                builder3.addFloat(tileView2, str2, 0.0f, (float) i11);
                                builder2.addFloat(tileView2, str3, 0.0f, (float) (this.mQsPanel.isLayoutRtl() ? i10 - i2 : i10 + i2));
                            }
                            this.mQuickQsViews.add(tileView.getIconWithBackground());
                            this.mAllViews.add(tileView.getIcon());
                            this.mAllViews.add(tileView2);
                            iArr = iArr2;
                        }
                    }
                    bottom = i3;
                    this.mAllViews.add(tileView);
                    i5++;
                    it = it2;
                    tiles = collection;
                    f2 = f;
                    i4 = i2;
                    iArr2 = iArr;
                    tileLayout = qSTileLayout2;
                }
                it = it2;
                bottom = i3;
                tiles = collection;
                f2 = f;
                i4 = i2;
            }
            Collection collection2 = tiles;
            QSTileLayout qSTileLayout3 = tileLayout;
            float f4 = f2;
            if (Utils.useQsMediaPlayer(this.mQsPanel.getContext())) {
                View mediaPanel = this.mQsPanel.getMediaPanel();
                View view2 = this.mQuickQsPanel.getMediaPlayer().getView();
                i = 2;
                builder2.addFloat(mediaPanel, str, 0.0f, 1.0f);
                builder2.addFloat(view2, str, 1.0f, 0.0f);
            } else {
                i = 2;
            }
            if (this.mAllowFancy) {
                View brightnessView = this.mQsPanel.getBrightnessView();
                if (brightnessView != null) {
                    float[] fArr = new float[i];
                    fArr[0] = f4;
                    fArr[1] = 0.0f;
                    builder.addFloat(brightnessView, str2, fArr);
                    Builder builder4 = new Builder();
                    float[] fArr2 = new float[i];
                    // fill-array-data instruction
                    fArr2[0] = 0;
                    fArr2[1] = 1065353216;
                    builder4.addFloat(brightnessView, str, fArr2);
                    builder4.setStartDelay(0.5f);
                    this.mBrightnessAnimator = builder4.build();
                    this.mAllViews.add(brightnessView);
                } else {
                    this.mBrightnessAnimator = null;
                }
                builder.setListener(this);
                this.mFirstPageAnimator = builder.build();
                Builder builder5 = new Builder();
                builder5.setStartDelay(0.86f);
                qSTileLayout = qSTileLayout3;
                builder5.addFloat(qSTileLayout, str, 0.0f, 1.0f);
                builder5.addFloat(this.mQsPanel.getDivider(), str, 0.0f, 1.0f);
                builder5.addFloat(this.mQsPanel.getFooter().getView(), str, 0.0f, 1.0f);
                this.mFirstPageDelayedAnimator = builder5.build();
                this.mAllViews.add(this.mQsPanel.getDivider());
                this.mAllViews.add(this.mQsPanel.getFooter().getView());
                float f5 = collection2.size() <= 3 ? 1.0f : collection2.size() <= 6 ? 0.4f : 0.0f;
                PathInterpolatorBuilder pathInterpolatorBuilder = new PathInterpolatorBuilder(0.0f, 0.0f, f5, 1.0f);
                builder2.setInterpolator(pathInterpolatorBuilder.getXInterpolator());
                builder3.setInterpolator(pathInterpolatorBuilder.getYInterpolator());
                this.mTranslationXAnimator = builder2.build();
                this.mTranslationYAnimator = builder3.build();
            } else {
                qSTileLayout = qSTileLayout3;
            }
            Builder builder6 = new Builder();
            builder6.addFloat(this.mQuickQsPanel, str, 1.0f, 0.0f);
            builder6.addFloat(this.mQsPanel.getDivider(), str, 0.0f, 1.0f);
            builder6.setListener(this.mNonFirstPageListener);
            builder6.setEndDelay(0.5f);
            this.mNonfirstPageAnimator = builder6.build();
            Builder builder7 = new Builder();
            builder7.setStartDelay(0.14f);
            builder7.addFloat(qSTileLayout, str, 0.0f, 1.0f);
            this.mNonfirstPageDelayedAnimator = builder7.build();
        }
    }

    private boolean isIconInAnimatedRow(int i) {
        PagedTileLayout pagedTileLayout = this.mPagedLayout;
        boolean z = false;
        if (pagedTileLayout == null) {
            return false;
        }
        int columnCount = pagedTileLayout.getColumnCount();
        if (i < (((this.mNumQuickTiles + columnCount) - 1) / columnCount) * columnCount) {
            z = true;
        }
        return z;
    }

    private void getRelativePosition(int[] iArr, View view, View view2) {
        iArr[0] = (view.getWidth() / 2) + 0;
        iArr[1] = 0;
        getRelativePositionInt(iArr, view, view2);
    }

    private void getRelativePositionInt(int[] iArr, View view, View view2) {
        if (view != view2 && view != null) {
            if (!(view instanceof TilePage)) {
                iArr[0] = iArr[0] + view.getLeft();
                iArr[1] = iArr[1] + view.getTop();
            }
            getRelativePositionInt(iArr, (View) view.getParent(), view2);
        }
    }

    public void setPosition(float f) {
        if (this.mFirstPageAnimator != null) {
            if (this.mOnKeyguard) {
                f = this.mShowCollapsedOnKeyguard ? 0.0f : 1.0f;
            }
            this.mLastPosition = f;
            if (!this.mOnFirstPage || !this.mAllowFancy) {
                this.mNonfirstPageAnimator.setPosition(f);
                this.mNonfirstPageDelayedAnimator.setPosition(f);
            } else {
                this.mQuickQsPanel.setAlpha(1.0f);
                this.mFirstPageAnimator.setPosition(f);
                this.mFirstPageDelayedAnimator.setPosition(f);
                this.mTranslationXAnimator.setPosition(f);
                this.mTranslationYAnimator.setPosition(f);
                TouchAnimator touchAnimator = this.mBrightnessAnimator;
                if (touchAnimator != null) {
                    touchAnimator.setPosition(f);
                }
            }
        }
    }

    public void onAnimationAtStart() {
        this.mQuickQsPanel.setVisibility(0);
    }

    public void onAnimationAtEnd() {
        this.mQuickQsPanel.setVisibility(4);
        int size = this.mQuickQsViews.size();
        for (int i = 0; i < size; i++) {
            ((View) this.mQuickQsViews.get(i)).setVisibility(0);
        }
    }

    public void onAnimationStarted() {
        updateQQSVisibility();
        if (this.mOnFirstPage) {
            int size = this.mQuickQsViews.size();
            for (int i = 0; i < size; i++) {
                ((View) this.mQuickQsViews.get(i)).setVisibility(4);
            }
        }
    }

    private void clearAnimationState() {
        int size = this.mAllViews.size();
        this.mQuickQsPanel.setAlpha(0.0f);
        for (int i = 0; i < size; i++) {
            View view = (View) this.mAllViews.get(i);
            view.setAlpha(1.0f);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }
        int size2 = this.mQuickQsViews.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ((View) this.mQuickQsViews.get(i2)).setVisibility(0);
        }
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mQsPanel.post(this.mUpdateAnimators);
    }

    public void onTilesChanged() {
        this.mQsPanel.post(this.mUpdateAnimators);
    }
}
