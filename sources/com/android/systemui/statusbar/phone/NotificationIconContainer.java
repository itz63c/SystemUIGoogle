package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.util.AttributeSet;
import android.view.View;
import androidx.collection.ArrayMap;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.AlphaOptimizedFrameLayout;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.stack.AnimationFilter;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ViewState;
import java.util.ArrayList;
import java.util.HashMap;

public class NotificationIconContainer extends AlphaOptimizedFrameLayout {
    /* access modifiers changed from: private */
    public static final AnimationProperties ADD_ICON_PROPERTIES;
    /* access modifiers changed from: private */
    public static final AnimationProperties DOT_ANIMATION_PROPERTIES;
    /* access modifiers changed from: private */
    public static final AnimationProperties ICON_ANIMATION_PROPERTIES;
    /* access modifiers changed from: private */
    public static final AnimationProperties UNISOLATION_PROPERTY;
    /* access modifiers changed from: private */
    public static final AnimationProperties UNISOLATION_PROPERTY_OTHERS;
    /* access modifiers changed from: private */
    public static final AnimationProperties sTempProperties = new AnimationProperties() {
        private AnimationFilter mAnimationFilter = new AnimationFilter();

        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    };
    private int[] mAbsolutePosition = new int[2];
    private int mActualLayoutWidth = Integer.MIN_VALUE;
    private float mActualPaddingEnd = -2.14748365E9f;
    private float mActualPaddingStart = -2.14748365E9f;
    /* access modifiers changed from: private */
    public int mAddAnimationStartIndex = -1;
    private boolean mAnimationsEnabled = true;
    /* access modifiers changed from: private */
    public int mCannedAnimationStartIndex = -1;
    private boolean mChangingViewPositions;
    /* access modifiers changed from: private */
    public boolean mDisallowNextAnimation;
    private int mDotPadding;
    private boolean mDozing;
    private IconState mFirstVisibleIconState;
    private int mIconSize;
    private final HashMap<View, IconState> mIconStates = new HashMap<>();
    private boolean mIsStaticLayout = true;
    /* access modifiers changed from: private */
    public StatusBarIconView mIsolatedIcon;
    /* access modifiers changed from: private */
    public View mIsolatedIconForAnimation;
    private Rect mIsolatedIconLocation;
    private IconState mLastVisibleIconState;
    private int mNumDots;
    private boolean mOnLockScreen;
    private float mOpenedAmount = 0.0f;
    private int mOverflowWidth;
    private ArrayMap<String, ArrayList<StatusBarIcon>> mReplacingIcons;
    private int mSpeedBumpIndex = -1;
    private int mStaticDotDiameter;
    private int mStaticDotRadius;
    private float mVisualOverflowStart;

    public class IconState extends ViewState {
        public float clampedAppearAmount = 1.0f;
        public int customTransformHeight = Integer.MIN_VALUE;
        public float iconAppearAmount = 1.0f;
        public int iconColor = 0;
        public boolean isLastExpandIcon;
        public boolean justAdded = true;
        /* access modifiers changed from: private */
        public boolean justReplaced;
        public boolean needsCannedAnimation;
        public boolean noAnimations;
        public boolean translateContent;
        public boolean useFullTransitionAmount;
        public boolean useLinearTransitionAmount;
        public int visibleState;

        public IconState() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:37:0x0082  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0112  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void applyToView(android.view.View r12) {
            /*
                r11 = this;
                boolean r0 = r12 instanceof com.android.systemui.statusbar.StatusBarIconView
                r1 = 0
                if (r0 == 0) goto L_0x016b
                r0 = r12
                com.android.systemui.statusbar.StatusBarIconView r0 = (com.android.systemui.statusbar.StatusBarIconView) r0
                r2 = 0
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                boolean r3 = r3.areAnimationsEnabled(r0)
                r4 = 1
                if (r3 == 0) goto L_0x0020
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                boolean r3 = r3.mDisallowNextAnimation
                if (r3 != 0) goto L_0x0020
                boolean r3 = r11.noAnimations
                if (r3 != 0) goto L_0x0020
                r3 = r4
                goto L_0x0021
            L_0x0020:
                r3 = r1
            L_0x0021:
                if (r3 == 0) goto L_0x0141
                boolean r5 = r11.justAdded
                r6 = 2
                if (r5 != 0) goto L_0x003a
                boolean r5 = r11.justReplaced
                if (r5 == 0) goto L_0x002d
                goto L_0x003a
            L_0x002d:
                int r5 = r11.visibleState
                int r7 = r0.getVisibleState()
                if (r5 == r7) goto L_0x0054
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.DOT_ANIMATION_PROPERTIES
                goto L_0x0052
            L_0x003a:
                super.applyToView(r0)
                boolean r5 = r11.justAdded
                if (r5 == 0) goto L_0x0054
                float r5 = r11.iconAppearAmount
                r7 = 0
                int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r5 == 0) goto L_0x0054
                r0.setAlpha(r7)
                r0.setVisibleState(r6, r1)
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.ADD_ICON_PROPERTIES
            L_0x0052:
                r5 = r4
                goto L_0x0055
            L_0x0054:
                r5 = r1
            L_0x0055:
                if (r5 != 0) goto L_0x007c
                com.android.systemui.statusbar.phone.NotificationIconContainer r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r7 = r7.mAddAnimationStartIndex
                if (r7 < 0) goto L_0x007c
                com.android.systemui.statusbar.phone.NotificationIconContainer r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r7 = r7.indexOfChild(r12)
                com.android.systemui.statusbar.phone.NotificationIconContainer r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r8 = r8.mAddAnimationStartIndex
                if (r7 < r8) goto L_0x007c
                int r7 = r0.getVisibleState()
                if (r7 != r6) goto L_0x0077
                int r7 = r11.visibleState
                if (r7 == r6) goto L_0x007c
            L_0x0077:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.DOT_ANIMATION_PROPERTIES
                r5 = r4
            L_0x007c:
                boolean r7 = r11.needsCannedAnimation
                r8 = 100
                if (r7 == 0) goto L_0x00cb
                com.android.systemui.statusbar.notification.stack.AnimationProperties r5 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                com.android.systemui.statusbar.notification.stack.AnimationFilter r5 = r5.getAnimationFilter()
                r5.reset()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.ICON_ANIMATION_PROPERTIES
                com.android.systemui.statusbar.notification.stack.AnimationFilter r7 = r7.getAnimationFilter()
                r5.combineFilter(r7)
                com.android.systemui.statusbar.notification.stack.AnimationProperties r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r7.resetCustomInterpolators()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                com.android.systemui.statusbar.notification.stack.AnimationProperties r10 = com.android.systemui.statusbar.phone.NotificationIconContainer.ICON_ANIMATION_PROPERTIES
                r7.combineCustomInterpolators(r10)
                if (r2 == 0) goto L_0x00ba
                com.android.systemui.statusbar.notification.stack.AnimationFilter r7 = r2.getAnimationFilter()
                r5.combineFilter(r7)
                com.android.systemui.statusbar.notification.stack.AnimationProperties r5 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r5.combineCustomInterpolators(r2)
            L_0x00ba:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r2.setDuration(r8)
                com.android.systemui.statusbar.phone.NotificationIconContainer r5 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r7 = r5.indexOfChild(r12)
                r5.mCannedAnimationStartIndex = r7
                r5 = r4
            L_0x00cb:
                if (r5 != 0) goto L_0x010a
                com.android.systemui.statusbar.phone.NotificationIconContainer r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r7 = r7.mCannedAnimationStartIndex
                if (r7 < 0) goto L_0x010a
                com.android.systemui.statusbar.phone.NotificationIconContainer r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r7 = r7.indexOfChild(r12)
                com.android.systemui.statusbar.phone.NotificationIconContainer r10 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r10 = r10.mCannedAnimationStartIndex
                if (r7 <= r10) goto L_0x010a
                int r7 = r0.getVisibleState()
                if (r7 != r6) goto L_0x00ed
                int r7 = r11.visibleState
                if (r7 == r6) goto L_0x010a
            L_0x00ed:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                com.android.systemui.statusbar.notification.stack.AnimationFilter r2 = r2.getAnimationFilter()
                r2.reset()
                r2.animateX()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r2.resetCustomInterpolators()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r2.setDuration(r8)
                r5 = r4
            L_0x010a:
                com.android.systemui.statusbar.phone.NotificationIconContainer r6 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                android.view.View r6 = r6.mIsolatedIconForAnimation
                if (r6 == 0) goto L_0x0142
                com.android.systemui.statusbar.phone.NotificationIconContainer r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                android.view.View r2 = r2.mIsolatedIconForAnimation
                r5 = 0
                if (r12 != r2) goto L_0x012e
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.UNISOLATION_PROPERTY
                com.android.systemui.statusbar.phone.NotificationIconContainer r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                com.android.systemui.statusbar.StatusBarIconView r7 = r7.mIsolatedIcon
                if (r7 == 0) goto L_0x0129
                goto L_0x012a
            L_0x0129:
                r8 = r5
            L_0x012a:
                r2.setDelay(r8)
                goto L_0x013f
            L_0x012e:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r2 = com.android.systemui.statusbar.phone.NotificationIconContainer.UNISOLATION_PROPERTY_OTHERS
                com.android.systemui.statusbar.phone.NotificationIconContainer r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                com.android.systemui.statusbar.StatusBarIconView r7 = r7.mIsolatedIcon
                if (r7 != 0) goto L_0x013b
                goto L_0x013c
            L_0x013b:
                r8 = r5
            L_0x013c:
                r2.setDelay(r8)
            L_0x013f:
                r5 = r4
                goto L_0x0142
            L_0x0141:
                r5 = r1
            L_0x0142:
                int r6 = r11.visibleState
                r0.setVisibleState(r6, r3)
                int r6 = r11.iconColor
                boolean r7 = r11.needsCannedAnimation
                if (r7 == 0) goto L_0x0151
                if (r3 == 0) goto L_0x0151
                r3 = r4
                goto L_0x0152
            L_0x0151:
                r3 = r1
            L_0x0152:
                r0.setIconColor(r6, r3)
                if (r5 == 0) goto L_0x015b
                r11.animateTo(r0, r2)
                goto L_0x015e
            L_0x015b:
                super.applyToView(r12)
            L_0x015e:
                float r12 = r11.iconAppearAmount
                r2 = 1065353216(0x3f800000, float:1.0)
                int r12 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
                if (r12 != 0) goto L_0x0167
                goto L_0x0168
            L_0x0167:
                r4 = r1
            L_0x0168:
                r0.setIsInShelf(r4)
            L_0x016b:
                r11.justAdded = r1
                r11.justReplaced = r1
                r11.needsCannedAnimation = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationIconContainer.IconState.applyToView(android.view.View):void");
        }

        public boolean hasCustomTransformHeight() {
            return this.isLastExpandIcon && this.customTransformHeight != Integer.MIN_VALUE;
        }

        public void initFrom(View view) {
            super.initFrom(view);
            if (view instanceof StatusBarIconView) {
                this.iconColor = ((StatusBarIconView) view).getStaticDrawableColor();
            }
        }
    }

    static {
        C15191 r0 = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;

            {
                AnimationFilter animationFilter = new AnimationFilter();
                animationFilter.animateX();
                this.mAnimationFilter = animationFilter;
            }

            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        r0.setDuration(200);
        DOT_ANIMATION_PROPERTIES = r0;
        C15202 r02 = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;

            {
                AnimationFilter animationFilter = new AnimationFilter();
                animationFilter.animateY();
                animationFilter.animateAlpha();
                animationFilter.animateScale();
                this.mAnimationFilter = animationFilter;
            }

            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        r02.setDuration(100);
        r02.setCustomInterpolator(View.TRANSLATION_Y, Interpolators.ICON_OVERSHOT);
        ICON_ANIMATION_PROPERTIES = r02;
        C15224 r03 = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;

            {
                AnimationFilter animationFilter = new AnimationFilter();
                animationFilter.animateAlpha();
                this.mAnimationFilter = animationFilter;
            }

            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        r03.setDuration(200);
        r03.setDelay(50);
        ADD_ICON_PROPERTIES = r03;
        C15235 r04 = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;

            {
                AnimationFilter animationFilter = new AnimationFilter();
                animationFilter.animateAlpha();
                this.mAnimationFilter = animationFilter;
            }

            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        r04.setDuration(110);
        UNISOLATION_PROPERTY_OTHERS = r04;
        C15246 r05 = new AnimationProperties() {
            private AnimationFilter mAnimationFilter;

            {
                AnimationFilter animationFilter = new AnimationFilter();
                animationFilter.animateX();
                this.mAnimationFilter = animationFilter;
            }

            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        r05.setDuration(110);
        UNISOLATION_PROPERTY = r05;
    }

    public NotificationIconContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initDimens();
        setWillNotDraw(true);
    }

    private void initDimens() {
        this.mDotPadding = getResources().getDimensionPixelSize(C2009R$dimen.overflow_icon_dot_padding);
        int dimensionPixelSize = getResources().getDimensionPixelSize(C2009R$dimen.overflow_dot_radius);
        this.mStaticDotRadius = dimensionPixelSize;
        this.mStaticDotDiameter = dimensionPixelSize * 2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(-65536);
        paint.setStyle(Style.STROKE);
        canvas.drawRect(getActualPaddingStart(), 0.0f, getLayoutEnd(), (float) getHeight(), paint);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        initDimens();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float height = ((float) getHeight()) / 2.0f;
        this.mIconSize = 0;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            int i6 = (int) (height - (((float) measuredHeight) / 2.0f));
            childAt.layout(0, i6, measuredWidth, measuredHeight + i6);
            if (i5 == 0) {
                setIconSize(childAt.getWidth());
            }
        }
        getLocationOnScreen(this.mAbsolutePosition);
        if (this.mIsStaticLayout) {
            updateState();
        }
    }

    private void setIconSize(int i) {
        this.mIconSize = i;
        this.mOverflowWidth = i + ((this.mStaticDotDiameter + this.mDotPadding) * 0);
    }

    private void updateState() {
        resetViewStates();
        calculateIconTranslations();
        applyIconStates();
    }

    public void applyIconStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            ViewState viewState = (ViewState) this.mIconStates.get(childAt);
            if (viewState != null) {
                viewState.applyToView(childAt);
            }
        }
        this.mAddAnimationStartIndex = -1;
        this.mCannedAnimationStartIndex = -1;
        this.mDisallowNextAnimation = false;
        this.mIsolatedIconForAnimation = null;
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        boolean isReplacingIcon = isReplacingIcon(view);
        if (!this.mChangingViewPositions) {
            IconState iconState = new IconState();
            if (isReplacingIcon) {
                iconState.justAdded = false;
                iconState.justReplaced = true;
            }
            this.mIconStates.put(view, iconState);
        }
        int indexOfChild = indexOfChild(view);
        if (indexOfChild < getChildCount() - 1 && !isReplacingIcon && ((IconState) this.mIconStates.get(getChildAt(indexOfChild + 1))).iconAppearAmount > 0.0f) {
            int i = this.mAddAnimationStartIndex;
            if (i < 0) {
                this.mAddAnimationStartIndex = indexOfChild;
            } else {
                this.mAddAnimationStartIndex = Math.min(i, indexOfChild);
            }
        }
        if (view instanceof StatusBarIconView) {
            ((StatusBarIconView) view).setDozing(this.mDozing, false, 0);
        }
    }

    private boolean isReplacingIcon(View view) {
        if (this.mReplacingIcons == null || !(view instanceof StatusBarIconView)) {
            return false;
        }
        StatusBarIconView statusBarIconView = (StatusBarIconView) view;
        Icon sourceIcon = statusBarIconView.getSourceIcon();
        ArrayList arrayList = (ArrayList) this.mReplacingIcons.get(statusBarIconView.getNotification().getGroupKey());
        if (arrayList == null || !sourceIcon.sameAs(((StatusBarIcon) arrayList.get(0)).icon)) {
            return false;
        }
        return true;
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (view instanceof StatusBarIconView) {
            boolean isReplacingIcon = isReplacingIcon(view);
            StatusBarIconView statusBarIconView = (StatusBarIconView) view;
            if (areAnimationsEnabled(statusBarIconView) && statusBarIconView.getVisibleState() != 2 && view.getVisibility() == 0 && isReplacingIcon) {
                int findFirstViewIndexAfter = findFirstViewIndexAfter(statusBarIconView.getTranslationX());
                int i = this.mAddAnimationStartIndex;
                if (i < 0) {
                    this.mAddAnimationStartIndex = findFirstViewIndexAfter;
                } else {
                    this.mAddAnimationStartIndex = Math.min(i, findFirstViewIndexAfter);
                }
            }
            if (!this.mChangingViewPositions) {
                this.mIconStates.remove(view);
                if (areAnimationsEnabled(statusBarIconView) && !isReplacingIcon) {
                    boolean z = false;
                    addTransientView(statusBarIconView, 0);
                    if (view == this.mIsolatedIcon) {
                        z = true;
                    }
                    statusBarIconView.setVisibleState(2, true, new Runnable(statusBarIconView) {
                        public final /* synthetic */ StatusBarIconView f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            NotificationIconContainer.this.lambda$onViewRemoved$0$NotificationIconContainer(this.f$1);
                        }
                    }, z ? 110 : 0);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onViewRemoved$0 */
    public /* synthetic */ void lambda$onViewRemoved$0$NotificationIconContainer(StatusBarIconView statusBarIconView) {
        removeTransientView(statusBarIconView);
    }

    /* access modifiers changed from: private */
    public boolean areAnimationsEnabled(StatusBarIconView statusBarIconView) {
        return this.mAnimationsEnabled || statusBarIconView == this.mIsolatedIcon;
    }

    private int findFirstViewIndexAfter(float f) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getTranslationX() > f) {
                return i;
            }
        }
        return getChildCount();
    }

    public void resetViewStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            ViewState viewState = (ViewState) this.mIconStates.get(childAt);
            viewState.initFrom(childAt);
            StatusBarIconView statusBarIconView = this.mIsolatedIcon;
            viewState.alpha = (statusBarIconView == null || childAt == statusBarIconView) ? 1.0f : 0.0f;
            viewState.hidden = false;
        }
    }

    public void calculateIconTranslations() {
        float f;
        boolean z;
        int i;
        float actualPaddingStart = getActualPaddingStart();
        int childCount = getChildCount();
        int i2 = this.mOnLockScreen ? 5 : this.mIsStaticLayout ? 4 : childCount;
        float layoutEnd = getLayoutEnd();
        float maxOverflowStart = getMaxOverflowStart();
        float f2 = 0.0f;
        this.mVisualOverflowStart = 0.0f;
        this.mFirstVisibleIconState = null;
        int i3 = this.mSpeedBumpIndex;
        int i4 = -1;
        boolean z2 = i3 != -1 && i3 < getChildCount();
        int i5 = -1;
        int i6 = 0;
        while (i6 < childCount) {
            View childAt = getChildAt(i6);
            IconState iconState = (IconState) this.mIconStates.get(childAt);
            iconState.xTranslation = actualPaddingStart;
            if (this.mFirstVisibleIconState == null) {
                this.mFirstVisibleIconState = iconState;
            }
            int i7 = this.mSpeedBumpIndex;
            boolean z3 = (i7 != i4 && i6 >= i7 && iconState.iconAppearAmount > f2) || i6 >= i2;
            boolean z4 = i6 == childCount + -1;
            float iconScaleIncreased = (!this.mOnLockScreen || !(childAt instanceof StatusBarIconView)) ? 1.0f : ((StatusBarIconView) childAt).getIconScaleIncreased();
            if (this.mOpenedAmount != f2) {
                z4 = z4 && !z2 && !z3;
            }
            iconState.visibleState = 0;
            if (z4) {
                f = layoutEnd - ((float) this.mIconSize);
            } else {
                f = maxOverflowStart - ((float) this.mIconSize);
            }
            if (actualPaddingStart > f) {
                i = -1;
                z = true;
            } else {
                z = false;
                i = -1;
            }
            if (i5 == i && (z3 || z)) {
                i5 = (!z4 || z3) ? i6 : i6 - 1;
                this.mVisualOverflowStart = layoutEnd - ((float) this.mOverflowWidth);
                if (z3 || this.mIsStaticLayout) {
                    this.mVisualOverflowStart = Math.min(actualPaddingStart, this.mVisualOverflowStart);
                }
            }
            actualPaddingStart += iconState.iconAppearAmount * ((float) childAt.getWidth()) * iconScaleIncreased;
            i6++;
            f2 = 0.0f;
            i4 = -1;
        }
        this.mNumDots = 0;
        if (i5 != -1) {
            actualPaddingStart = this.mVisualOverflowStart;
            for (int i8 = i5; i8 < childCount; i8++) {
                IconState iconState2 = (IconState) this.mIconStates.get(getChildAt(i8));
                int i9 = this.mStaticDotDiameter + this.mDotPadding;
                iconState2.xTranslation = actualPaddingStart;
                int i10 = this.mNumDots;
                if (i10 < 1) {
                    if (i10 != 0 || iconState2.iconAppearAmount >= 0.8f) {
                        iconState2.visibleState = 1;
                        this.mNumDots++;
                    } else {
                        iconState2.visibleState = 0;
                    }
                    if (this.mNumDots == 1) {
                        i9 *= 1;
                    }
                    actualPaddingStart += ((float) i9) * iconState2.iconAppearAmount;
                    this.mLastVisibleIconState = iconState2;
                } else {
                    iconState2.visibleState = 2;
                }
            }
        } else if (childCount > 0) {
            this.mLastVisibleIconState = (IconState) this.mIconStates.get(getChildAt(childCount - 1));
            this.mFirstVisibleIconState = (IconState) this.mIconStates.get(getChildAt(0));
        }
        if (this.mOnLockScreen && actualPaddingStart < getLayoutEnd()) {
            IconState iconState3 = this.mFirstVisibleIconState;
            float f3 = iconState3 == null ? 0.0f : iconState3.xTranslation;
            IconState iconState4 = this.mLastVisibleIconState;
            float layoutEnd2 = ((getLayoutEnd() - getActualPaddingStart()) - (iconState4 != null ? Math.min((float) getWidth(), iconState4.xTranslation + ((float) this.mIconSize)) - f3 : 0.0f)) / 2.0f;
            if (i5 != -1) {
                layoutEnd2 = (((getLayoutEnd() - this.mVisualOverflowStart) / 2.0f) + layoutEnd2) / 2.0f;
            }
            for (int i11 = 0; i11 < childCount; i11++) {
                IconState iconState5 = (IconState) this.mIconStates.get(getChildAt(i11));
                iconState5.xTranslation += layoutEnd2;
            }
        }
        if (isLayoutRtl()) {
            for (int i12 = 0; i12 < childCount; i12++) {
                View childAt2 = getChildAt(i12);
                IconState iconState6 = (IconState) this.mIconStates.get(childAt2);
                iconState6.xTranslation = (((float) getWidth()) - iconState6.xTranslation) - ((float) childAt2.getWidth());
            }
        }
        StatusBarIconView statusBarIconView = this.mIsolatedIcon;
        if (statusBarIconView != null) {
            IconState iconState7 = (IconState) this.mIconStates.get(statusBarIconView);
            if (iconState7 != null) {
                iconState7.xTranslation = ((float) (this.mIsolatedIconLocation.left - this.mAbsolutePosition[0])) - (((1.0f - this.mIsolatedIcon.getIconScale()) * ((float) this.mIsolatedIcon.getWidth())) / 2.0f);
                iconState7.visibleState = 0;
            }
        }
    }

    private float getLayoutEnd() {
        return ((float) getActualWidth()) - getActualPaddingEnd();
    }

    private float getActualPaddingEnd() {
        float f = this.mActualPaddingEnd;
        return f == -2.14748365E9f ? (float) getPaddingEnd() : f;
    }

    private float getActualPaddingStart() {
        float f = this.mActualPaddingStart;
        return f == -2.14748365E9f ? (float) getPaddingStart() : f;
    }

    public void setIsStaticLayout(boolean z) {
        this.mIsStaticLayout = z;
    }

    public void setActualLayoutWidth(int i) {
        this.mActualLayoutWidth = i;
    }

    public void setActualPaddingEnd(float f) {
        this.mActualPaddingEnd = f;
    }

    public void setActualPaddingStart(float f) {
        this.mActualPaddingStart = f;
    }

    public int getActualWidth() {
        int i = this.mActualLayoutWidth;
        return i == Integer.MIN_VALUE ? getWidth() : i;
    }

    public int getFinalTranslationX() {
        float f;
        if (this.mLastVisibleIconState == null) {
            return 0;
        }
        if (isLayoutRtl()) {
            f = ((float) getWidth()) - this.mLastVisibleIconState.xTranslation;
        } else {
            f = this.mLastVisibleIconState.xTranslation + ((float) this.mIconSize);
        }
        return Math.min(getWidth(), (int) f);
    }

    private float getMaxOverflowStart() {
        return getLayoutEnd() - ((float) this.mOverflowWidth);
    }

    public void setChangingViewPositions(boolean z) {
        this.mChangingViewPositions = z;
    }

    public void setDozing(boolean z, boolean z2, long j) {
        this.mDozing = z;
        this.mDisallowNextAnimation |= !z2;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof StatusBarIconView) {
                ((StatusBarIconView) childAt).setDozing(z, z2, j);
            }
        }
    }

    public IconState getIconState(StatusBarIconView statusBarIconView) {
        return (IconState) this.mIconStates.get(statusBarIconView);
    }

    public void setSpeedBumpIndex(int i) {
        this.mSpeedBumpIndex = i;
    }

    public void setOpenedAmount(float f) {
        this.mOpenedAmount = f;
    }

    public boolean hasOverflow() {
        return this.mNumDots > 0;
    }

    public boolean hasPartialOverflow() {
        int i = this.mNumDots;
        return i > 0 && i < 1;
    }

    public int getPartialOverflowExtraPadding() {
        if (!hasPartialOverflow()) {
            return 0;
        }
        int i = (1 - this.mNumDots) * (this.mStaticDotDiameter + this.mDotPadding);
        if (getFinalTranslationX() + i > getWidth()) {
            i = getWidth() - getFinalTranslationX();
        }
        return i;
    }

    public int getNoOverflowExtraPadding() {
        if (this.mNumDots != 0) {
            return 0;
        }
        int i = this.mOverflowWidth;
        if (getFinalTranslationX() + i > getWidth()) {
            i = getWidth() - getFinalTranslationX();
        }
        return i;
    }

    public void setAnimationsEnabled(boolean z) {
        if (!z && this.mAnimationsEnabled) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                ViewState viewState = (ViewState) this.mIconStates.get(childAt);
                if (viewState != null) {
                    viewState.cancelAnimations(childAt);
                    viewState.applyToView(childAt);
                }
            }
        }
        this.mAnimationsEnabled = z;
    }

    public void setReplacingIcons(ArrayMap<String, ArrayList<StatusBarIcon>> arrayMap) {
        this.mReplacingIcons = arrayMap;
    }

    public void showIconIsolated(StatusBarIconView statusBarIconView, boolean z) {
        if (z) {
            this.mIsolatedIconForAnimation = statusBarIconView != null ? statusBarIconView : this.mIsolatedIcon;
        }
        this.mIsolatedIcon = statusBarIconView;
        updateState();
    }

    public void setIsolatedIconLocation(Rect rect, boolean z) {
        this.mIsolatedIconLocation = rect;
        if (z) {
            updateState();
        }
    }

    public void setOnLockScreen(boolean z) {
        this.mOnLockScreen = z;
    }
}
