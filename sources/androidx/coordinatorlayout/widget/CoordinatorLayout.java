package androidx.coordinatorlayout.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewParent;
import androidx.coordinatorlayout.R$attr;
import androidx.coordinatorlayout.R$style;
import androidx.coordinatorlayout.R$styleable;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Pools$Pool;
import androidx.core.util.Pools$SynchronizedPool;
import androidx.core.view.GravityCompat;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.customview.view.AbsSavedState;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatorLayout extends ViewGroup implements NestedScrollingParent2, NestedScrollingParent3 {
    static final Class<?>[] CONSTRUCTOR_PARAMS = {Context.class, AttributeSet.class};
    static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;
    static final String WIDGET_PACKAGE_NAME;
    static final ThreadLocal<Map<String, Constructor<Behavior>>> sConstructors = new ThreadLocal<>();
    private static final Pools$Pool<Rect> sRectPool = new Pools$SynchronizedPool(12);
    private OnApplyWindowInsetsListener mApplyWindowInsetsListener;
    private final int[] mBehaviorConsumed;
    private View mBehaviorTouchView;
    private final DirectedAcyclicGraph<View> mChildDag;
    private final List<View> mDependencySortedChildren;
    private boolean mDisallowInterceptReset;
    private boolean mDrawStatusBarBackground;
    private boolean mIsAttachedToWindow;
    private int[] mKeylines;
    private WindowInsetsCompat mLastInsets;
    private boolean mNeedsPreDrawListener;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private View mNestedScrollingTarget;
    private final int[] mNestedScrollingV2ConsumedCompat;
    OnHierarchyChangeListener mOnHierarchyChangeListener;
    private OnPreDrawListener mOnPreDrawListener;
    private Paint mScrimPaint;
    private Drawable mStatusBarBackground;
    private final List<View> mTempList1;

    public interface AttachedBehavior {
        Behavior getBehavior();
    }

    public static abstract class Behavior<V extends View> {
        public boolean getInsetDodgeRect(CoordinatorLayout coordinatorLayout, V v, Rect rect) {
            return false;
        }

        public int getScrimColor(CoordinatorLayout coordinatorLayout, V v) {
            return -16777216;
        }

        public float getScrimOpacity(CoordinatorLayout coordinatorLayout, V v) {
            return 0.0f;
        }

        public boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, V v, View view) {
            return false;
        }

        public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, V v, WindowInsetsCompat windowInsetsCompat) {
            return windowInsetsCompat;
        }

        public void onAttachedToLayoutParams(LayoutParams layoutParams) {
        }

        public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, V v, View view) {
            return false;
        }

        public void onDependentViewRemoved(CoordinatorLayout coordinatorLayout, V v, View view) {
        }

        public void onDetachedFromLayoutParams() {
        }

        public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
            return false;
        }

        public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, V v, int i) {
            return false;
        }

        public boolean onMeasureChild(CoordinatorLayout coordinatorLayout, V v, int i, int i2, int i3, int i4) {
            return false;
        }

        public boolean onNestedFling(CoordinatorLayout coordinatorLayout, V v, View view, float f, float f2, boolean z) {
            return false;
        }

        public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V v, View view, float f, float f2) {
            return false;
        }

        @Deprecated
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int[] iArr) {
        }

        @Deprecated
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int i3, int i4) {
        }

        @Deprecated
        public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, V v, View view, View view2, int i) {
        }

        public boolean onRequestChildRectangleOnScreen(CoordinatorLayout coordinatorLayout, V v, Rect rect, boolean z) {
            return false;
        }

        public void onRestoreInstanceState(CoordinatorLayout coordinatorLayout, V v, Parcelable parcelable) {
        }

        @Deprecated
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, View view2, int i) {
            return false;
        }

        @Deprecated
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view) {
        }

        public boolean onTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
            return false;
        }

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attributeSet) {
        }

        public boolean blocksInteractionBelow(CoordinatorLayout coordinatorLayout, V v) {
            return getScrimOpacity(coordinatorLayout, v) > 0.0f;
        }

        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, View view2, int i, int i2) {
            if (i2 == 0) {
                return onStartNestedScroll(coordinatorLayout, v, view, view2, i);
            }
            return false;
        }

        public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, V v, View view, View view2, int i, int i2) {
            if (i2 == 0) {
                onNestedScrollAccepted(coordinatorLayout, v, view, view2, i);
            }
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i) {
            if (i == 0) {
                onStopNestedScroll(coordinatorLayout, v, view);
            }
        }

        @Deprecated
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int i3, int i4, int i5) {
            if (i5 == 0) {
                onNestedScroll(coordinatorLayout, v, view, i, i2, i3, i4);
            }
        }

        public void onNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
            iArr[0] = iArr[0] + i3;
            iArr[1] = iArr[1] + i4;
            onNestedScroll(coordinatorLayout, v, view, i, i2, i3, i4, i5);
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int[] iArr, int i3) {
            if (i3 == 0) {
                onNestedPreScroll(coordinatorLayout, v, view, i, i2, iArr);
            }
        }

        public Parcelable onSaveInstanceState(CoordinatorLayout coordinatorLayout, V v) {
            return BaseSavedState.EMPTY_STATE;
        }
    }

    @Deprecated
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultBehavior {
        Class<? extends Behavior> value();
    }

    private class HierarchyChangeListener implements OnHierarchyChangeListener {
        HierarchyChangeListener() {
        }

        public void onChildViewAdded(View view, View view2) {
            OnHierarchyChangeListener onHierarchyChangeListener = CoordinatorLayout.this.mOnHierarchyChangeListener;
            if (onHierarchyChangeListener != null) {
                onHierarchyChangeListener.onChildViewAdded(view, view2);
            }
        }

        public void onChildViewRemoved(View view, View view2) {
            CoordinatorLayout.this.onChildViewsChanged(2);
            OnHierarchyChangeListener onHierarchyChangeListener = CoordinatorLayout.this.mOnHierarchyChangeListener;
            if (onHierarchyChangeListener != null) {
                onHierarchyChangeListener.onChildViewRemoved(view, view2);
            }
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int anchorGravity = 0;
        public int dodgeInsetEdges = 0;
        public int gravity = 0;
        public int insetEdge = 0;
        public int keyline = -1;
        View mAnchorDirectChild;
        int mAnchorId = -1;
        View mAnchorView;
        Behavior mBehavior;
        boolean mBehaviorResolved = false;
        private boolean mDidAcceptNestedScrollNonTouch;
        private boolean mDidAcceptNestedScrollTouch;
        private boolean mDidBlockInteraction;
        private boolean mDidChangeAfterNestedScroll;
        int mInsetOffsetX;
        int mInsetOffsetY;
        final Rect mLastChildRect = new Rect();

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.CoordinatorLayout_Layout);
            this.gravity = obtainStyledAttributes.getInteger(R$styleable.CoordinatorLayout_Layout_android_layout_gravity, 0);
            this.mAnchorId = obtainStyledAttributes.getResourceId(R$styleable.CoordinatorLayout_Layout_layout_anchor, -1);
            this.anchorGravity = obtainStyledAttributes.getInteger(R$styleable.CoordinatorLayout_Layout_layout_anchorGravity, 0);
            this.keyline = obtainStyledAttributes.getInteger(R$styleable.CoordinatorLayout_Layout_layout_keyline, -1);
            this.insetEdge = obtainStyledAttributes.getInt(R$styleable.CoordinatorLayout_Layout_layout_insetEdge, 0);
            this.dodgeInsetEdges = obtainStyledAttributes.getInt(R$styleable.CoordinatorLayout_Layout_layout_dodgeInsetEdges, 0);
            boolean hasValue = obtainStyledAttributes.hasValue(R$styleable.CoordinatorLayout_Layout_layout_behavior);
            this.mBehaviorResolved = hasValue;
            if (hasValue) {
                this.mBehavior = CoordinatorLayout.parseBehavior(context, attributeSet, obtainStyledAttributes.getString(R$styleable.CoordinatorLayout_Layout_layout_behavior));
            }
            obtainStyledAttributes.recycle();
            Behavior behavior = this.mBehavior;
            if (behavior != null) {
                behavior.onAttachedToLayoutParams(this);
            }
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public Behavior getBehavior() {
            return this.mBehavior;
        }

        public void setBehavior(Behavior behavior) {
            Behavior behavior2 = this.mBehavior;
            if (behavior2 != behavior) {
                if (behavior2 != null) {
                    behavior2.onDetachedFromLayoutParams();
                }
                this.mBehavior = behavior;
                this.mBehaviorResolved = true;
                if (behavior != null) {
                    behavior.onAttachedToLayoutParams(this);
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void setLastChildRect(Rect rect) {
            this.mLastChildRect.set(rect);
        }

        /* access modifiers changed from: 0000 */
        public Rect getLastChildRect() {
            return this.mLastChildRect;
        }

        /* access modifiers changed from: 0000 */
        public boolean checkAnchorChanged() {
            return this.mAnchorView == null && this.mAnchorId != -1;
        }

        /* access modifiers changed from: 0000 */
        public boolean didBlockInteraction() {
            if (this.mBehavior == null) {
                this.mDidBlockInteraction = false;
            }
            return this.mDidBlockInteraction;
        }

        /* access modifiers changed from: 0000 */
        public boolean isBlockingInteractionBelow(CoordinatorLayout coordinatorLayout, View view) {
            boolean z = this.mDidBlockInteraction;
            if (z) {
                return true;
            }
            Behavior behavior = this.mBehavior;
            boolean blocksInteractionBelow = (behavior != null ? behavior.blocksInteractionBelow(coordinatorLayout, view) : false) | z;
            this.mDidBlockInteraction = blocksInteractionBelow;
            return blocksInteractionBelow;
        }

        /* access modifiers changed from: 0000 */
        public void resetTouchBehaviorTracking() {
            this.mDidBlockInteraction = false;
        }

        /* access modifiers changed from: 0000 */
        public void resetNestedScroll(int i) {
            setNestedScrollAccepted(i, false);
        }

        /* access modifiers changed from: 0000 */
        public void setNestedScrollAccepted(int i, boolean z) {
            if (i == 0) {
                this.mDidAcceptNestedScrollTouch = z;
            } else if (i == 1) {
                this.mDidAcceptNestedScrollNonTouch = z;
            }
        }

        /* access modifiers changed from: 0000 */
        public boolean isNestedScrollAccepted(int i) {
            if (i == 0) {
                return this.mDidAcceptNestedScrollTouch;
            }
            if (i != 1) {
                return false;
            }
            return this.mDidAcceptNestedScrollNonTouch;
        }

        /* access modifiers changed from: 0000 */
        public boolean getChangedAfterNestedScroll() {
            return this.mDidChangeAfterNestedScroll;
        }

        /* access modifiers changed from: 0000 */
        public void setChangedAfterNestedScroll(boolean z) {
            this.mDidChangeAfterNestedScroll = z;
        }

        /* access modifiers changed from: 0000 */
        public void resetChangedAfterNestedScroll() {
            this.mDidChangeAfterNestedScroll = false;
        }

        /* access modifiers changed from: 0000 */
        public boolean dependsOn(CoordinatorLayout coordinatorLayout, View view, View view2) {
            if (view2 != this.mAnchorDirectChild && !shouldDodge(view2, ViewCompat.getLayoutDirection(coordinatorLayout))) {
                Behavior behavior = this.mBehavior;
                if (behavior == null || !behavior.layoutDependsOn(coordinatorLayout, view, view2)) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: 0000 */
        public View findAnchorView(CoordinatorLayout coordinatorLayout, View view) {
            if (this.mAnchorId == -1) {
                this.mAnchorDirectChild = null;
                this.mAnchorView = null;
                return null;
            }
            if (this.mAnchorView == null || !verifyAnchorView(view, coordinatorLayout)) {
                resolveAnchorView(view, coordinatorLayout);
            }
            return this.mAnchorView;
        }

        private void resolveAnchorView(View view, CoordinatorLayout coordinatorLayout) {
            View findViewById = coordinatorLayout.findViewById(this.mAnchorId);
            this.mAnchorView = findViewById;
            if (findViewById != null) {
                if (findViewById != coordinatorLayout) {
                    ViewParent parent = findViewById.getParent();
                    while (parent != coordinatorLayout && parent != null) {
                        if (parent != view) {
                            if (parent instanceof View) {
                                findViewById = (View) parent;
                            }
                            parent = parent.getParent();
                        } else if (coordinatorLayout.isInEditMode()) {
                            this.mAnchorDirectChild = null;
                            this.mAnchorView = null;
                            return;
                        } else {
                            throw new IllegalStateException("Anchor must not be a descendant of the anchored view");
                        }
                    }
                    this.mAnchorDirectChild = findViewById;
                } else if (coordinatorLayout.isInEditMode()) {
                    this.mAnchorDirectChild = null;
                    this.mAnchorView = null;
                } else {
                    throw new IllegalStateException("View can not be anchored to the the parent CoordinatorLayout");
                }
            } else if (coordinatorLayout.isInEditMode()) {
                this.mAnchorDirectChild = null;
                this.mAnchorView = null;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Could not find CoordinatorLayout descendant view with id ");
                sb.append(coordinatorLayout.getResources().getResourceName(this.mAnchorId));
                sb.append(" to anchor view ");
                sb.append(view);
                throw new IllegalStateException(sb.toString());
            }
        }

        private boolean verifyAnchorView(View view, CoordinatorLayout coordinatorLayout) {
            if (this.mAnchorView.getId() != this.mAnchorId) {
                return false;
            }
            View view2 = this.mAnchorView;
            for (ViewParent parent = view2.getParent(); parent != coordinatorLayout; parent = parent.getParent()) {
                if (parent == null || parent == view) {
                    this.mAnchorDirectChild = null;
                    this.mAnchorView = null;
                    return false;
                }
                if (parent instanceof View) {
                    view2 = (View) parent;
                }
            }
            this.mAnchorDirectChild = view2;
            return true;
        }

        private boolean shouldDodge(View view, int i) {
            int absoluteGravity = GravityCompat.getAbsoluteGravity(((LayoutParams) view.getLayoutParams()).insetEdge, i);
            return absoluteGravity != 0 && (GravityCompat.getAbsoluteGravity(this.dodgeInsetEdges, i) & absoluteGravity) == absoluteGravity;
        }
    }

    class OnPreDrawListener implements android.view.ViewTreeObserver.OnPreDrawListener {
        OnPreDrawListener() {
        }

        public boolean onPreDraw() {
            CoordinatorLayout.this.onChildViewsChanged(0);
            return true;
        }
    }

    protected static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        SparseArray<Parcelable> behaviorStates;

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            int readInt = parcel.readInt();
            int[] iArr = new int[readInt];
            parcel.readIntArray(iArr);
            Parcelable[] readParcelableArray = parcel.readParcelableArray(classLoader);
            this.behaviorStates = new SparseArray<>(readInt);
            for (int i = 0; i < readInt; i++) {
                this.behaviorStates.append(iArr[i], readParcelableArray[i]);
            }
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            SparseArray<Parcelable> sparseArray = this.behaviorStates;
            int size = sparseArray != null ? sparseArray.size() : 0;
            parcel.writeInt(size);
            int[] iArr = new int[size];
            Parcelable[] parcelableArr = new Parcelable[size];
            for (int i2 = 0; i2 < size; i2++) {
                iArr[i2] = this.behaviorStates.keyAt(i2);
                parcelableArr[i2] = (Parcelable) this.behaviorStates.valueAt(i2);
            }
            parcel.writeIntArray(iArr);
            parcel.writeParcelableArray(parcelableArr, i);
        }
    }

    static class ViewElevationComparator implements Comparator<View> {
        ViewElevationComparator() {
        }

        public int compare(View view, View view2) {
            float z = ViewCompat.getZ(view);
            float z2 = ViewCompat.getZ(view2);
            if (z > z2) {
                return -1;
            }
            return z < z2 ? 1 : 0;
        }
    }

    private static int clamp(int i, int i2, int i3) {
        return i < i2 ? i2 : i > i3 ? i3 : i;
    }

    private static int resolveAnchoredChildGravity(int i) {
        if (i == 0) {
            return 17;
        }
        return i;
    }

    private static int resolveGravity(int i) {
        if ((i & 7) == 0) {
            i |= 8388611;
        }
        return (i & 112) == 0 ? i | 48 : i;
    }

    private static int resolveKeylineGravity(int i) {
        if (i == 0) {
            return 8388661;
        }
        return i;
    }

    static {
        Package packageR = CoordinatorLayout.class.getPackage();
        WIDGET_PACKAGE_NAME = packageR != null ? packageR.getName() : null;
        if (VERSION.SDK_INT >= 21) {
            TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();
        } else {
            TOP_SORTED_CHILDREN_COMPARATOR = null;
        }
    }

    private static Rect acquireTempRect() {
        Rect rect = (Rect) sRectPool.acquire();
        return rect == null ? new Rect() : rect;
    }

    private static void releaseTempRect(Rect rect) {
        rect.setEmpty();
        sRectPool.release(rect);
    }

    public CoordinatorLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.coordinatorLayoutStyle);
    }

    public CoordinatorLayout(Context context, AttributeSet attributeSet, int i) {
        TypedArray typedArray;
        super(context, attributeSet, i);
        this.mDependencySortedChildren = new ArrayList();
        this.mChildDag = new DirectedAcyclicGraph<>();
        this.mTempList1 = new ArrayList();
        new ArrayList();
        this.mBehaviorConsumed = new int[2];
        this.mNestedScrollingV2ConsumedCompat = new int[2];
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        if (i == 0) {
            typedArray = context.obtainStyledAttributes(attributeSet, R$styleable.CoordinatorLayout, 0, R$style.Widget_Support_CoordinatorLayout);
        } else {
            typedArray = context.obtainStyledAttributes(attributeSet, R$styleable.CoordinatorLayout, i, 0);
        }
        if (i == 0) {
            ViewCompat.saveAttributeDataForStyleable(this, context, R$styleable.CoordinatorLayout, attributeSet, typedArray, 0, R$style.Widget_Support_CoordinatorLayout);
        } else {
            ViewCompat.saveAttributeDataForStyleable(this, context, R$styleable.CoordinatorLayout, attributeSet, typedArray, i, 0);
        }
        int resourceId = typedArray.getResourceId(R$styleable.CoordinatorLayout_keylines, 0);
        if (resourceId != 0) {
            Resources resources = context.getResources();
            this.mKeylines = resources.getIntArray(resourceId);
            float f = resources.getDisplayMetrics().density;
            int length = this.mKeylines.length;
            for (int i2 = 0; i2 < length; i2++) {
                int[] iArr = this.mKeylines;
                iArr[i2] = (int) (((float) iArr[i2]) * f);
            }
        }
        this.mStatusBarBackground = typedArray.getDrawable(R$styleable.CoordinatorLayout_statusBarBackground);
        typedArray.recycle();
        setupForInsets();
        super.setOnHierarchyChangeListener(new HierarchyChangeListener());
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
    }

    public void setOnHierarchyChangeListener(OnHierarchyChangeListener onHierarchyChangeListener) {
        this.mOnHierarchyChangeListener = onHierarchyChangeListener;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetTouchBehaviors();
        if (this.mNeedsPreDrawListener) {
            if (this.mOnPreDrawListener == null) {
                this.mOnPreDrawListener = new OnPreDrawListener();
            }
            getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
        }
        if (this.mLastInsets == null && ViewCompat.getFitsSystemWindows(this)) {
            ViewCompat.requestApplyInsets(this);
        }
        this.mIsAttachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resetTouchBehaviors();
        if (this.mNeedsPreDrawListener && this.mOnPreDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
        }
        View view = this.mNestedScrollingTarget;
        if (view != null) {
            onStopNestedScroll(view);
        }
        this.mIsAttachedToWindow = false;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        Drawable drawable = this.mStatusBarBackground;
        boolean z = false;
        if (drawable != null && drawable.isStateful()) {
            z = false | drawable.setState(drawableState);
        }
        if (z) {
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mStatusBarBackground;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        boolean z = i == 0;
        Drawable drawable = this.mStatusBarBackground;
        if (drawable != null && drawable.isVisible() != z) {
            this.mStatusBarBackground.setVisible(z, false);
        }
    }

    /* access modifiers changed from: 0000 */
    public final WindowInsetsCompat setWindowInsets(WindowInsetsCompat windowInsetsCompat) {
        if (ObjectsCompat.equals(this.mLastInsets, windowInsetsCompat)) {
            return windowInsetsCompat;
        }
        this.mLastInsets = windowInsetsCompat;
        boolean z = true;
        boolean z2 = windowInsetsCompat != null && windowInsetsCompat.getSystemWindowInsetTop() > 0;
        this.mDrawStatusBarBackground = z2;
        if (z2 || getBackground() != null) {
            z = false;
        }
        setWillNotDraw(z);
        WindowInsetsCompat dispatchApplyWindowInsetsToBehaviors = dispatchApplyWindowInsetsToBehaviors(windowInsetsCompat);
        requestLayout();
        return dispatchApplyWindowInsetsToBehaviors;
    }

    private void cancelInterceptBehaviors() {
        int childCount = getChildCount();
        MotionEvent motionEvent = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            Behavior behavior = ((LayoutParams) childAt.getLayoutParams()).getBehavior();
            if (behavior != null) {
                if (motionEvent == null) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    motionEvent = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                }
                behavior.onInterceptTouchEvent(this, childAt, motionEvent);
            }
        }
        if (motionEvent != null) {
            motionEvent.recycle();
        }
    }

    private void resetTouchBehaviors() {
        View view = this.mBehaviorTouchView;
        if (view != null) {
            Behavior behavior = ((LayoutParams) view.getLayoutParams()).getBehavior();
            if (behavior != null) {
                long uptimeMillis = SystemClock.uptimeMillis();
                MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                behavior.onTouchEvent(this, this.mBehaviorTouchView, obtain);
                obtain.recycle();
            }
            this.mBehaviorTouchView = null;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((LayoutParams) getChildAt(i).getLayoutParams()).resetTouchBehaviorTracking();
        }
        this.mDisallowInterceptReset = false;
    }

    private void getTopSortedChildren(List<View> list) {
        list.clear();
        boolean isChildrenDrawingOrderEnabled = isChildrenDrawingOrderEnabled();
        int childCount = getChildCount();
        int i = childCount - 1;
        while (i >= 0) {
            list.add(getChildAt(isChildrenDrawingOrderEnabled ? getChildDrawingOrder(childCount, i) : i));
            i--;
        }
        Comparator<View> comparator = TOP_SORTED_CHILDREN_COMPARATOR;
        if (comparator != null) {
            Collections.sort(list, comparator);
        }
    }

    private boolean performIntercept(MotionEvent motionEvent, int i) {
        int actionMasked = motionEvent.getActionMasked();
        List<View> list = this.mTempList1;
        getTopSortedChildren(list);
        int size = list.size();
        MotionEvent motionEvent2 = null;
        boolean z = false;
        boolean z2 = false;
        for (int i2 = 0; i2 < size; i2++) {
            View view = (View) list.get(i2);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Behavior behavior = layoutParams.getBehavior();
            if ((!z && !z2) || actionMasked == 0) {
                if (!z2 && !z && behavior != null) {
                    z = performEvent(behavior, view, motionEvent, i);
                    if (z) {
                        this.mBehaviorTouchView = view;
                        if (!(actionMasked == 3 || actionMasked == 1)) {
                            for (int i3 = 0; i3 < i2; i3++) {
                                View view2 = (View) list.get(i3);
                                Behavior behavior2 = ((LayoutParams) view2.getLayoutParams()).getBehavior();
                                if (behavior2 != null) {
                                    if (motionEvent2 == null) {
                                        motionEvent2 = obtainCancelEvent(motionEvent);
                                    }
                                    performEvent(behavior2, view2, motionEvent2, i);
                                }
                            }
                        }
                    }
                }
                boolean didBlockInteraction = layoutParams.didBlockInteraction();
                boolean isBlockingInteractionBelow = layoutParams.isBlockingInteractionBelow(this, view);
                z2 = isBlockingInteractionBelow && !didBlockInteraction;
                if (isBlockingInteractionBelow && !z2) {
                    break;
                }
            } else if (behavior != null) {
                if (motionEvent2 == null) {
                    motionEvent2 = obtainCancelEvent(motionEvent);
                }
                performEvent(behavior, view, motionEvent2, i);
            }
        }
        list.clear();
        if (motionEvent2 != null) {
            motionEvent2.recycle();
        }
        return z;
    }

    private boolean performEvent(Behavior behavior, View view, MotionEvent motionEvent, int i) {
        if (i == 0) {
            return behavior.onInterceptTouchEvent(this, view, motionEvent);
        }
        if (i == 1) {
            return behavior.onTouchEvent(this, view, motionEvent);
        }
        throw new IllegalArgumentException();
    }

    private MotionEvent obtainCancelEvent(MotionEvent motionEvent) {
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        obtain.setAction(3);
        return obtain;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            resetTouchBehaviors();
        }
        boolean performIntercept = performIntercept(motionEvent, 0);
        if (actionMasked == 1 || actionMasked == 3) {
            this.mBehaviorTouchView = null;
            resetTouchBehaviors();
        }
        return performIntercept;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        int actionMasked = motionEvent.getActionMasked();
        View view = this.mBehaviorTouchView;
        boolean z2 = false;
        if (view != null) {
            Behavior behavior = ((LayoutParams) view.getLayoutParams()).getBehavior();
            z = behavior != null ? behavior.onTouchEvent(this, this.mBehaviorTouchView, motionEvent) : false;
        } else {
            z = performIntercept(motionEvent, 1);
            if (actionMasked != 0 && z) {
                z2 = true;
            }
        }
        if (this.mBehaviorTouchView == null || actionMasked == 3) {
            z |= super.onTouchEvent(motionEvent);
        } else if (z2) {
            MotionEvent obtainCancelEvent = obtainCancelEvent(motionEvent);
            super.onTouchEvent(obtainCancelEvent);
            obtainCancelEvent.recycle();
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.mBehaviorTouchView = null;
            resetTouchBehaviors();
        }
        return z;
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        super.requestDisallowInterceptTouchEvent(z);
        if (z && !this.mDisallowInterceptReset) {
            if (this.mBehaviorTouchView == null) {
                cancelInterceptBehaviors();
            }
            resetTouchBehaviors();
            this.mDisallowInterceptReset = true;
        }
    }

    private int getKeyline(int i) {
        int[] iArr = this.mKeylines;
        String str = "CoordinatorLayout";
        if (iArr == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No keylines defined for ");
            sb.append(this);
            sb.append(" - attempted index lookup ");
            sb.append(i);
            Log.e(str, sb.toString());
            return 0;
        } else if (i >= 0 && i < iArr.length) {
            return iArr[i];
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Keyline index ");
            sb2.append(i);
            sb2.append(" out of range for ");
            sb2.append(this);
            Log.e(str, sb2.toString());
            return 0;
        }
    }

    static Behavior parseBehavior(Context context, AttributeSet attributeSet, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.startsWith(".")) {
            StringBuilder sb = new StringBuilder();
            sb.append(context.getPackageName());
            sb.append(str);
            str = sb.toString();
        } else if (str.indexOf(46) < 0 && !TextUtils.isEmpty(WIDGET_PACKAGE_NAME)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(WIDGET_PACKAGE_NAME);
            sb2.append('.');
            sb2.append(str);
            str = sb2.toString();
        }
        try {
            Map map = (Map) sConstructors.get();
            if (map == null) {
                map = new HashMap();
                sConstructors.set(map);
            }
            Constructor constructor = (Constructor) map.get(str);
            if (constructor == null) {
                constructor = Class.forName(str, false, context.getClassLoader()).getConstructor(CONSTRUCTOR_PARAMS);
                constructor.setAccessible(true);
                map.put(str, constructor);
            }
            return (Behavior) constructor.newInstance(new Object[]{context, attributeSet});
        } catch (Exception e) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Could not inflate Behavior subclass ");
            sb3.append(str);
            throw new RuntimeException(sb3.toString(), e);
        }
    }

    /* access modifiers changed from: 0000 */
    public LayoutParams getResolvedLayoutParams(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (!layoutParams.mBehaviorResolved) {
            String str = "CoordinatorLayout";
            if (view instanceof AttachedBehavior) {
                Behavior behavior = ((AttachedBehavior) view).getBehavior();
                if (behavior == null) {
                    Log.e(str, "Attached behavior class is null");
                }
                layoutParams.setBehavior(behavior);
                layoutParams.mBehaviorResolved = true;
            } else {
                DefaultBehavior defaultBehavior = null;
                for (Class cls = view.getClass(); cls != null; cls = cls.getSuperclass()) {
                    defaultBehavior = (DefaultBehavior) cls.getAnnotation(DefaultBehavior.class);
                    if (defaultBehavior != null) {
                        break;
                    }
                }
                if (defaultBehavior != null) {
                    try {
                        layoutParams.setBehavior((Behavior) defaultBehavior.value().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
                    } catch (Exception e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Default behavior class ");
                        sb.append(defaultBehavior.value().getName());
                        sb.append(" could not be instantiated. Did you forget a default constructor?");
                        Log.e(str, sb.toString(), e);
                    }
                }
                layoutParams.mBehaviorResolved = true;
            }
        }
        return layoutParams;
    }

    private void prepareChildren() {
        this.mDependencySortedChildren.clear();
        this.mChildDag.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            LayoutParams resolvedLayoutParams = getResolvedLayoutParams(childAt);
            resolvedLayoutParams.findAnchorView(this, childAt);
            this.mChildDag.addNode(childAt);
            for (int i2 = 0; i2 < childCount; i2++) {
                if (i2 != i) {
                    View childAt2 = getChildAt(i2);
                    if (resolvedLayoutParams.dependsOn(this, childAt, childAt2)) {
                        if (!this.mChildDag.contains(childAt2)) {
                            this.mChildDag.addNode(childAt2);
                        }
                        this.mChildDag.addEdge(childAt2, childAt);
                    }
                }
            }
        }
        this.mDependencySortedChildren.addAll(this.mChildDag.getSortedList());
        Collections.reverse(this.mDependencySortedChildren);
    }

    /* access modifiers changed from: 0000 */
    public void getDescendantRect(View view, Rect rect) {
        ViewGroupUtils.getDescendantRect(this, view, rect);
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumWidth() {
        return Math.max(super.getSuggestedMinimumWidth(), getPaddingLeft() + getPaddingRight());
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumHeight() {
        return Math.max(super.getSuggestedMinimumHeight(), getPaddingTop() + getPaddingBottom());
    }

    public void onMeasureChild(View view, int i, int i2, int i3, int i4) {
        measureChildWithMargins(view, i, i2, i3, i4);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x011a, code lost:
        if (r0.onMeasureChild(r30, r20, r11, r21, r23, 0) == false) goto L_0x012a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x011d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r31, int r32) {
        /*
            r30 = this;
            r7 = r30
            r30.prepareChildren()
            r30.ensurePreDrawListener()
            int r8 = r30.getPaddingLeft()
            int r0 = r30.getPaddingTop()
            int r9 = r30.getPaddingRight()
            int r1 = r30.getPaddingBottom()
            int r10 = androidx.core.view.ViewCompat.getLayoutDirection(r30)
            r2 = 1
            if (r10 != r2) goto L_0x0021
            r12 = r2
            goto L_0x0022
        L_0x0021:
            r12 = 0
        L_0x0022:
            int r13 = android.view.View.MeasureSpec.getMode(r31)
            int r14 = android.view.View.MeasureSpec.getSize(r31)
            int r15 = android.view.View.MeasureSpec.getMode(r32)
            int r16 = android.view.View.MeasureSpec.getSize(r32)
            int r17 = r8 + r9
            int r18 = r0 + r1
            int r0 = r30.getSuggestedMinimumWidth()
            int r1 = r30.getSuggestedMinimumHeight()
            androidx.core.view.WindowInsetsCompat r3 = r7.mLastInsets
            if (r3 == 0) goto L_0x004b
            boolean r3 = androidx.core.view.ViewCompat.getFitsSystemWindows(r30)
            if (r3 == 0) goto L_0x004b
            r19 = r2
            goto L_0x004d
        L_0x004b:
            r19 = 0
        L_0x004d:
            java.util.List<android.view.View> r2 = r7.mDependencySortedChildren
            int r6 = r2.size()
            r5 = r0
            r4 = r1
            r2 = 0
            r3 = 0
        L_0x0057:
            if (r3 >= r6) goto L_0x016f
            java.util.List<android.view.View> r0 = r7.mDependencySortedChildren
            java.lang.Object r0 = r0.get(r3)
            r20 = r0
            android.view.View r20 = (android.view.View) r20
            int r0 = r20.getVisibility()
            r1 = 8
            if (r0 != r1) goto L_0x0073
            r22 = r3
            r29 = r6
            r28 = r8
            goto L_0x0167
        L_0x0073:
            android.view.ViewGroup$LayoutParams r0 = r20.getLayoutParams()
            r1 = r0
            androidx.coordinatorlayout.widget.CoordinatorLayout$LayoutParams r1 = (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) r1
            int r0 = r1.keyline
            if (r0 < 0) goto L_0x00ba
            if (r13 == 0) goto L_0x00ba
            int r0 = r7.getKeyline(r0)
            int r11 = r1.gravity
            int r11 = resolveKeylineGravity(r11)
            int r11 = androidx.core.view.GravityCompat.getAbsoluteGravity(r11, r10)
            r11 = r11 & 7
            r22 = r2
            r2 = 3
            if (r11 != r2) goto L_0x0097
            if (r12 == 0) goto L_0x009c
        L_0x0097:
            r2 = 5
            if (r11 != r2) goto L_0x00a8
            if (r12 == 0) goto L_0x00a8
        L_0x009c:
            int r2 = r14 - r9
            int r2 = r2 - r0
            r0 = 0
            int r2 = java.lang.Math.max(r0, r2)
            r11 = r0
            r21 = r2
            goto L_0x00bf
        L_0x00a8:
            if (r11 != r2) goto L_0x00ac
            if (r12 == 0) goto L_0x00b1
        L_0x00ac:
            r2 = 3
            if (r11 != r2) goto L_0x00bc
            if (r12 == 0) goto L_0x00bc
        L_0x00b1:
            int r0 = r0 - r8
            r11 = 0
            int r0 = java.lang.Math.max(r11, r0)
            r21 = r0
            goto L_0x00bf
        L_0x00ba:
            r22 = r2
        L_0x00bc:
            r11 = 0
            r21 = r11
        L_0x00bf:
            if (r19 == 0) goto L_0x00f1
            boolean r0 = androidx.core.view.ViewCompat.getFitsSystemWindows(r20)
            if (r0 != 0) goto L_0x00f1
            androidx.core.view.WindowInsetsCompat r0 = r7.mLastInsets
            int r0 = r0.getSystemWindowInsetLeft()
            androidx.core.view.WindowInsetsCompat r2 = r7.mLastInsets
            int r2 = r2.getSystemWindowInsetRight()
            int r0 = r0 + r2
            androidx.core.view.WindowInsetsCompat r2 = r7.mLastInsets
            int r2 = r2.getSystemWindowInsetTop()
            androidx.core.view.WindowInsetsCompat r11 = r7.mLastInsets
            int r11 = r11.getSystemWindowInsetBottom()
            int r2 = r2 + r11
            int r0 = r14 - r0
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r13)
            int r2 = r16 - r2
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r15)
            r11 = r0
            r23 = r2
            goto L_0x00f5
        L_0x00f1:
            r11 = r31
            r23 = r32
        L_0x00f5:
            androidx.coordinatorlayout.widget.CoordinatorLayout$Behavior r0 = r1.getBehavior()
            if (r0 == 0) goto L_0x011d
            r24 = 0
            r2 = r1
            r1 = r30
            r26 = r2
            r25 = r22
            r2 = r20
            r22 = r3
            r3 = r11
            r27 = r4
            r4 = r21
            r28 = r8
            r8 = r5
            r5 = r23
            r29 = r6
            r6 = r24
            boolean r0 = r0.onMeasureChild(r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x0137
            goto L_0x012a
        L_0x011d:
            r26 = r1
            r27 = r4
            r29 = r6
            r28 = r8
            r25 = r22
            r22 = r3
            r8 = r5
        L_0x012a:
            r5 = 0
            r0 = r30
            r1 = r20
            r2 = r11
            r3 = r21
            r4 = r23
            r0.onMeasureChild(r1, r2, r3, r4, r5)
        L_0x0137:
            int r0 = r20.getMeasuredWidth()
            int r0 = r17 + r0
            r1 = r26
            int r2 = r1.leftMargin
            int r0 = r0 + r2
            int r2 = r1.rightMargin
            int r0 = r0 + r2
            int r0 = java.lang.Math.max(r8, r0)
            int r2 = r20.getMeasuredHeight()
            int r2 = r18 + r2
            int r3 = r1.topMargin
            int r2 = r2 + r3
            int r1 = r1.bottomMargin
            int r2 = r2 + r1
            r1 = r27
            int r1 = java.lang.Math.max(r1, r2)
            int r2 = r20.getMeasuredState()
            r11 = r25
            int r2 = android.view.View.combineMeasuredStates(r11, r2)
            r5 = r0
            r4 = r1
        L_0x0167:
            int r3 = r22 + 1
            r8 = r28
            r6 = r29
            goto L_0x0057
        L_0x016f:
            r11 = r2
            r1 = r4
            r8 = r5
            r0 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r0 = r0 & r11
            r2 = r31
            int r0 = android.view.View.resolveSizeAndState(r8, r2, r0)
            int r2 = r11 << 16
            r3 = r32
            int r1 = android.view.View.resolveSizeAndState(r1, r3, r2)
            r7.setMeasuredDimension(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.coordinatorlayout.widget.CoordinatorLayout.onMeasure(int, int):void");
    }

    private WindowInsetsCompat dispatchApplyWindowInsetsToBehaviors(WindowInsetsCompat windowInsetsCompat) {
        if (windowInsetsCompat.isConsumed()) {
            return windowInsetsCompat;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (ViewCompat.getFitsSystemWindows(childAt)) {
                Behavior behavior = ((LayoutParams) childAt.getLayoutParams()).getBehavior();
                if (behavior != null) {
                    windowInsetsCompat = behavior.onApplyWindowInsets(this, childAt, windowInsetsCompat);
                    if (windowInsetsCompat.isConsumed()) {
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        return windowInsetsCompat;
    }

    public void onLayoutChild(View view, int i) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (!layoutParams.checkAnchorChanged()) {
            View view2 = layoutParams.mAnchorView;
            if (view2 != null) {
                layoutChildWithAnchor(view, view2, i);
                return;
            }
            int i2 = layoutParams.keyline;
            if (i2 >= 0) {
                layoutChildWithKeyline(view, i2, i);
            } else {
                layoutChild(view, i);
            }
        } else {
            throw new IllegalStateException("An anchor may not be changed after CoordinatorLayout measurement begins before layout is complete.");
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        int size = this.mDependencySortedChildren.size();
        for (int i5 = 0; i5 < size; i5++) {
            View view = (View) this.mDependencySortedChildren.get(i5);
            if (view.getVisibility() != 8) {
                Behavior behavior = ((LayoutParams) view.getLayoutParams()).getBehavior();
                if (behavior == null || !behavior.onLayoutChild(this, view, layoutDirection)) {
                    onLayoutChild(view, layoutDirection);
                }
            }
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            WindowInsetsCompat windowInsetsCompat = this.mLastInsets;
            int systemWindowInsetTop = windowInsetsCompat != null ? windowInsetsCompat.getSystemWindowInsetTop() : 0;
            if (systemWindowInsetTop > 0) {
                this.mStatusBarBackground.setBounds(0, 0, getWidth(), systemWindowInsetTop);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }

    public void setFitsSystemWindows(boolean z) {
        super.setFitsSystemWindows(z);
        setupForInsets();
    }

    /* access modifiers changed from: 0000 */
    public void recordLastChildRect(View view, Rect rect) {
        ((LayoutParams) view.getLayoutParams()).setLastChildRect(rect);
    }

    /* access modifiers changed from: 0000 */
    public void getLastChildRect(View view, Rect rect) {
        rect.set(((LayoutParams) view.getLayoutParams()).getLastChildRect());
    }

    /* access modifiers changed from: 0000 */
    public void getChildRect(View view, boolean z, Rect rect) {
        if (view.isLayoutRequested() || view.getVisibility() == 8) {
            rect.setEmpty();
            return;
        }
        if (z) {
            getDescendantRect(view, rect);
        } else {
            rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        }
    }

    private void getDesiredAnchoredChildRectWithoutConstraints(int i, Rect rect, Rect rect2, LayoutParams layoutParams, int i2, int i3) {
        int i4;
        int i5;
        int absoluteGravity = GravityCompat.getAbsoluteGravity(resolveAnchoredChildGravity(layoutParams.gravity), i);
        int absoluteGravity2 = GravityCompat.getAbsoluteGravity(resolveGravity(layoutParams.anchorGravity), i);
        int i6 = absoluteGravity & 7;
        int i7 = absoluteGravity & 112;
        int i8 = absoluteGravity2 & 7;
        int i9 = absoluteGravity2 & 112;
        if (i8 == 1) {
            i4 = rect.left + (rect.width() / 2);
        } else if (i8 != 5) {
            i4 = rect.left;
        } else {
            i4 = rect.right;
        }
        if (i9 == 16) {
            i5 = rect.top + (rect.height() / 2);
        } else if (i9 != 80) {
            i5 = rect.top;
        } else {
            i5 = rect.bottom;
        }
        if (i6 == 1) {
            i4 -= i2 / 2;
        } else if (i6 != 5) {
            i4 -= i2;
        }
        if (i7 == 16) {
            i5 -= i3 / 2;
        } else if (i7 != 80) {
            i5 -= i3;
        }
        rect2.set(i4, i5, i2 + i4, i3 + i5);
    }

    private void constrainChildRect(LayoutParams layoutParams, Rect rect, int i, int i2) {
        int width = getWidth();
        int height = getHeight();
        int max = Math.max(getPaddingLeft() + layoutParams.leftMargin, Math.min(rect.left, ((width - getPaddingRight()) - i) - layoutParams.rightMargin));
        int max2 = Math.max(getPaddingTop() + layoutParams.topMargin, Math.min(rect.top, ((height - getPaddingBottom()) - i2) - layoutParams.bottomMargin));
        rect.set(max, max2, i + max, i2 + max2);
    }

    /* access modifiers changed from: 0000 */
    public void getDesiredAnchoredChildRect(View view, int i, Rect rect, Rect rect2) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        getDesiredAnchoredChildRectWithoutConstraints(i, rect, rect2, layoutParams, measuredWidth, measuredHeight);
        constrainChildRect(layoutParams, rect2, measuredWidth, measuredHeight);
    }

    private void layoutChildWithAnchor(View view, View view2, int i) {
        Rect acquireTempRect = acquireTempRect();
        Rect acquireTempRect2 = acquireTempRect();
        try {
            getDescendantRect(view2, acquireTempRect);
            getDesiredAnchoredChildRect(view, i, acquireTempRect, acquireTempRect2);
            view.layout(acquireTempRect2.left, acquireTempRect2.top, acquireTempRect2.right, acquireTempRect2.bottom);
        } finally {
            releaseTempRect(acquireTempRect);
            releaseTempRect(acquireTempRect2);
        }
    }

    private void layoutChildWithKeyline(View view, int i, int i2) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int absoluteGravity = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(layoutParams.gravity), i2);
        int i3 = absoluteGravity & 7;
        int i4 = absoluteGravity & 112;
        int width = getWidth();
        int height = getHeight();
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        if (i2 == 1) {
            i = width - i;
        }
        int keyline = getKeyline(i) - measuredWidth;
        int i5 = 0;
        if (i3 == 1) {
            keyline += measuredWidth / 2;
        } else if (i3 == 5) {
            keyline += measuredWidth;
        }
        if (i4 == 16) {
            i5 = 0 + (measuredHeight / 2);
        } else if (i4 == 80) {
            i5 = measuredHeight + 0;
        }
        int max = Math.max(getPaddingLeft() + layoutParams.leftMargin, Math.min(keyline, ((width - getPaddingRight()) - measuredWidth) - layoutParams.rightMargin));
        int max2 = Math.max(getPaddingTop() + layoutParams.topMargin, Math.min(i5, ((height - getPaddingBottom()) - measuredHeight) - layoutParams.bottomMargin));
        view.layout(max, max2, measuredWidth + max, measuredHeight + max2);
    }

    private void layoutChild(View view, int i) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        Rect acquireTempRect = acquireTempRect();
        acquireTempRect.set(getPaddingLeft() + layoutParams.leftMargin, getPaddingTop() + layoutParams.topMargin, (getWidth() - getPaddingRight()) - layoutParams.rightMargin, (getHeight() - getPaddingBottom()) - layoutParams.bottomMargin);
        if (this.mLastInsets != null && ViewCompat.getFitsSystemWindows(this) && !ViewCompat.getFitsSystemWindows(view)) {
            acquireTempRect.left += this.mLastInsets.getSystemWindowInsetLeft();
            acquireTempRect.top += this.mLastInsets.getSystemWindowInsetTop();
            acquireTempRect.right -= this.mLastInsets.getSystemWindowInsetRight();
            acquireTempRect.bottom -= this.mLastInsets.getSystemWindowInsetBottom();
        }
        Rect acquireTempRect2 = acquireTempRect();
        GravityCompat.apply(resolveGravity(layoutParams.gravity), view.getMeasuredWidth(), view.getMeasuredHeight(), acquireTempRect, acquireTempRect2, i);
        view.layout(acquireTempRect2.left, acquireTempRect2.top, acquireTempRect2.right, acquireTempRect2.bottom);
        releaseTempRect(acquireTempRect);
        releaseTempRect(acquireTempRect2);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        Behavior behavior = layoutParams.mBehavior;
        if (behavior != null) {
            float scrimOpacity = behavior.getScrimOpacity(this, view);
            if (scrimOpacity > 0.0f) {
                if (this.mScrimPaint == null) {
                    this.mScrimPaint = new Paint();
                }
                this.mScrimPaint.setColor(layoutParams.mBehavior.getScrimColor(this, view));
                this.mScrimPaint.setAlpha(clamp(Math.round(scrimOpacity * 255.0f), 0, 255));
                int save = canvas.save();
                if (view.isOpaque()) {
                    canvas.clipRect((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom(), Op.DIFFERENCE);
                }
                canvas.drawRect((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - getPaddingBottom()), this.mScrimPaint);
                canvas.restoreToCount(save);
            }
        }
        return super.drawChild(canvas, view, j);
    }

    /* access modifiers changed from: 0000 */
    public final void onChildViewsChanged(int i) {
        boolean z;
        int i2 = i;
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        int size = this.mDependencySortedChildren.size();
        Rect acquireTempRect = acquireTempRect();
        Rect acquireTempRect2 = acquireTempRect();
        Rect acquireTempRect3 = acquireTempRect();
        for (int i3 = 0; i3 < size; i3++) {
            View view = (View) this.mDependencySortedChildren.get(i3);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (i2 != 0 || view.getVisibility() != 8) {
                for (int i4 = 0; i4 < i3; i4++) {
                    if (layoutParams.mAnchorDirectChild == ((View) this.mDependencySortedChildren.get(i4))) {
                        offsetChildToAnchor(view, layoutDirection);
                    }
                }
                getChildRect(view, true, acquireTempRect2);
                if (layoutParams.insetEdge != 0 && !acquireTempRect2.isEmpty()) {
                    int absoluteGravity = GravityCompat.getAbsoluteGravity(layoutParams.insetEdge, layoutDirection);
                    int i5 = absoluteGravity & 112;
                    if (i5 == 48) {
                        acquireTempRect.top = Math.max(acquireTempRect.top, acquireTempRect2.bottom);
                    } else if (i5 == 80) {
                        acquireTempRect.bottom = Math.max(acquireTempRect.bottom, getHeight() - acquireTempRect2.top);
                    }
                    int i6 = absoluteGravity & 7;
                    if (i6 == 3) {
                        acquireTempRect.left = Math.max(acquireTempRect.left, acquireTempRect2.right);
                    } else if (i6 == 5) {
                        acquireTempRect.right = Math.max(acquireTempRect.right, getWidth() - acquireTempRect2.left);
                    }
                }
                if (layoutParams.dodgeInsetEdges != 0 && view.getVisibility() == 0) {
                    offsetChildByInset(view, acquireTempRect, layoutDirection);
                }
                if (i2 != 2) {
                    getLastChildRect(view, acquireTempRect3);
                    if (!acquireTempRect3.equals(acquireTempRect2)) {
                        recordLastChildRect(view, acquireTempRect2);
                    }
                }
                for (int i7 = i3 + 1; i7 < size; i7++) {
                    View view2 = (View) this.mDependencySortedChildren.get(i7);
                    LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
                    Behavior behavior = layoutParams2.getBehavior();
                    if (behavior != null && behavior.layoutDependsOn(this, view2, view)) {
                        if (i2 != 0 || !layoutParams2.getChangedAfterNestedScroll()) {
                            if (i2 != 2) {
                                z = behavior.onDependentViewChanged(this, view2, view);
                            } else {
                                behavior.onDependentViewRemoved(this, view2, view);
                                z = true;
                            }
                            if (i2 == 1) {
                                layoutParams2.setChangedAfterNestedScroll(z);
                            }
                        } else {
                            layoutParams2.resetChangedAfterNestedScroll();
                        }
                    }
                }
            }
        }
        releaseTempRect(acquireTempRect);
        releaseTempRect(acquireTempRect2);
        releaseTempRect(acquireTempRect3);
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00f7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void offsetChildByInset(android.view.View r9, android.graphics.Rect r10, int r11) {
        /*
            r8 = this;
            boolean r0 = androidx.core.view.ViewCompat.isLaidOut(r9)
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            int r0 = r9.getWidth()
            if (r0 <= 0) goto L_0x00fd
            int r0 = r9.getHeight()
            if (r0 > 0) goto L_0x0015
            goto L_0x00fd
        L_0x0015:
            android.view.ViewGroup$LayoutParams r0 = r9.getLayoutParams()
            androidx.coordinatorlayout.widget.CoordinatorLayout$LayoutParams r0 = (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) r0
            androidx.coordinatorlayout.widget.CoordinatorLayout$Behavior r1 = r0.getBehavior()
            android.graphics.Rect r2 = acquireTempRect()
            android.graphics.Rect r3 = acquireTempRect()
            int r4 = r9.getLeft()
            int r5 = r9.getTop()
            int r6 = r9.getRight()
            int r7 = r9.getBottom()
            r3.set(r4, r5, r6, r7)
            if (r1 == 0) goto L_0x0070
            boolean r1 = r1.getInsetDodgeRect(r8, r9, r2)
            if (r1 == 0) goto L_0x0070
            boolean r1 = r3.contains(r2)
            if (r1 == 0) goto L_0x0049
            goto L_0x0073
        L_0x0049:
            java.lang.IllegalArgumentException r8 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Rect should be within the child's bounds. Rect:"
            r9.append(r10)
            java.lang.String r10 = r2.toShortString()
            r9.append(r10)
            java.lang.String r10 = " | Bounds:"
            r9.append(r10)
            java.lang.String r10 = r3.toShortString()
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.<init>(r9)
            throw r8
        L_0x0070:
            r2.set(r3)
        L_0x0073:
            releaseTempRect(r3)
            boolean r1 = r2.isEmpty()
            if (r1 == 0) goto L_0x0080
            releaseTempRect(r2)
            return
        L_0x0080:
            int r1 = r0.dodgeInsetEdges
            int r11 = androidx.core.view.GravityCompat.getAbsoluteGravity(r1, r11)
            r1 = r11 & 48
            r3 = 48
            r4 = 1
            r5 = 0
            if (r1 != r3) goto L_0x00a0
            int r1 = r2.top
            int r3 = r0.topMargin
            int r1 = r1 - r3
            int r3 = r0.mInsetOffsetY
            int r1 = r1 - r3
            int r3 = r10.top
            if (r1 >= r3) goto L_0x00a0
            int r3 = r3 - r1
            r8.setInsetOffsetY(r9, r3)
            r1 = r4
            goto L_0x00a1
        L_0x00a0:
            r1 = r5
        L_0x00a1:
            r3 = r11 & 80
            r6 = 80
            if (r3 != r6) goto L_0x00bd
            int r3 = r8.getHeight()
            int r6 = r2.bottom
            int r3 = r3 - r6
            int r6 = r0.bottomMargin
            int r3 = r3 - r6
            int r6 = r0.mInsetOffsetY
            int r3 = r3 + r6
            int r6 = r10.bottom
            if (r3 >= r6) goto L_0x00bd
            int r3 = r3 - r6
            r8.setInsetOffsetY(r9, r3)
            r1 = r4
        L_0x00bd:
            if (r1 != 0) goto L_0x00c2
            r8.setInsetOffsetY(r9, r5)
        L_0x00c2:
            r1 = r11 & 3
            r3 = 3
            if (r1 != r3) goto L_0x00d9
            int r1 = r2.left
            int r3 = r0.leftMargin
            int r1 = r1 - r3
            int r3 = r0.mInsetOffsetX
            int r1 = r1 - r3
            int r3 = r10.left
            if (r1 >= r3) goto L_0x00d9
            int r3 = r3 - r1
            r8.setInsetOffsetX(r9, r3)
            r1 = r4
            goto L_0x00da
        L_0x00d9:
            r1 = r5
        L_0x00da:
            r3 = 5
            r11 = r11 & r3
            if (r11 != r3) goto L_0x00f4
            int r11 = r8.getWidth()
            int r3 = r2.right
            int r11 = r11 - r3
            int r3 = r0.rightMargin
            int r11 = r11 - r3
            int r0 = r0.mInsetOffsetX
            int r11 = r11 + r0
            int r10 = r10.right
            if (r11 >= r10) goto L_0x00f4
            int r11 = r11 - r10
            r8.setInsetOffsetX(r9, r11)
            goto L_0x00f5
        L_0x00f4:
            r4 = r1
        L_0x00f5:
            if (r4 != 0) goto L_0x00fa
            r8.setInsetOffsetX(r9, r5)
        L_0x00fa:
            releaseTempRect(r2)
        L_0x00fd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.coordinatorlayout.widget.CoordinatorLayout.offsetChildByInset(android.view.View, android.graphics.Rect, int):void");
    }

    private void setInsetOffsetX(View view, int i) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i2 = layoutParams.mInsetOffsetX;
        if (i2 != i) {
            ViewCompat.offsetLeftAndRight(view, i - i2);
            layoutParams.mInsetOffsetX = i;
        }
    }

    private void setInsetOffsetY(View view, int i) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i2 = layoutParams.mInsetOffsetY;
        if (i2 != i) {
            ViewCompat.offsetTopAndBottom(view, i - i2);
            layoutParams.mInsetOffsetY = i;
        }
    }

    /* access modifiers changed from: 0000 */
    public final List<View> getDependencySortedChildren() {
        prepareChildren();
        return Collections.unmodifiableList(this.mDependencySortedChildren);
    }

    /* access modifiers changed from: 0000 */
    public void ensurePreDrawListener() {
        int childCount = getChildCount();
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= childCount) {
                break;
            } else if (hasDependencies(getChildAt(i))) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (z == this.mNeedsPreDrawListener) {
            return;
        }
        if (z) {
            addPreDrawListener();
        } else {
            removePreDrawListener();
        }
    }

    private boolean hasDependencies(View view) {
        return this.mChildDag.hasOutgoingEdges(view);
    }

    /* access modifiers changed from: 0000 */
    public void addPreDrawListener() {
        if (this.mIsAttachedToWindow) {
            if (this.mOnPreDrawListener == null) {
                this.mOnPreDrawListener = new OnPreDrawListener();
            }
            getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
        }
        this.mNeedsPreDrawListener = true;
    }

    /* access modifiers changed from: 0000 */
    public void removePreDrawListener() {
        if (this.mIsAttachedToWindow && this.mOnPreDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
        }
        this.mNeedsPreDrawListener = false;
    }

    /* access modifiers changed from: 0000 */
    public void offsetChildToAnchor(View view, int i) {
        View view2 = view;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (layoutParams.mAnchorView != null) {
            Rect acquireTempRect = acquireTempRect();
            Rect acquireTempRect2 = acquireTempRect();
            Rect acquireTempRect3 = acquireTempRect();
            getDescendantRect(layoutParams.mAnchorView, acquireTempRect);
            boolean z = false;
            getChildRect(view2, false, acquireTempRect2);
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            getDesiredAnchoredChildRectWithoutConstraints(i, acquireTempRect, acquireTempRect3, layoutParams, measuredWidth, measuredHeight);
            if (!(acquireTempRect3.left == acquireTempRect2.left && acquireTempRect3.top == acquireTempRect2.top)) {
                z = true;
            }
            constrainChildRect(layoutParams, acquireTempRect3, measuredWidth, measuredHeight);
            int i2 = acquireTempRect3.left - acquireTempRect2.left;
            int i3 = acquireTempRect3.top - acquireTempRect2.top;
            if (i2 != 0) {
                ViewCompat.offsetLeftAndRight(view2, i2);
            }
            if (i3 != 0) {
                ViewCompat.offsetTopAndBottom(view2, i3);
            }
            if (z) {
                Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    behavior.onDependentViewChanged(this, view2, layoutParams.mAnchorView);
                }
            }
            releaseTempRect(acquireTempRect);
            releaseTempRect(acquireTempRect2);
            releaseTempRect(acquireTempRect3);
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams) && super.checkLayoutParams(layoutParams);
    }

    public boolean onStartNestedScroll(View view, View view2, int i) {
        return onStartNestedScroll(view, view2, i, 0);
    }

    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        int i3 = i2;
        int childCount = getChildCount();
        boolean z = false;
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    boolean onStartNestedScroll = behavior.onStartNestedScroll(this, childAt, view, view2, i, i2);
                    z |= onStartNestedScroll;
                    layoutParams.setNestedScrollAccepted(i3, onStartNestedScroll);
                } else {
                    layoutParams.setNestedScrollAccepted(i3, false);
                }
            }
        }
        return z;
    }

    public void onNestedScrollAccepted(View view, View view2, int i) {
        onNestedScrollAccepted(view, view2, i, 0);
    }

    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i, i2);
        this.mNestedScrollingTarget = view2;
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.isNestedScrollAccepted(i2)) {
                Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    behavior.onNestedScrollAccepted(this, childAt, view, view2, i, i2);
                }
            }
        }
    }

    public void onStopNestedScroll(View view) {
        onStopNestedScroll(view, 0);
    }

    public void onStopNestedScroll(View view, int i) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(view, i);
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.isNestedScrollAccepted(i)) {
                Behavior behavior = layoutParams.getBehavior();
                if (behavior != null) {
                    behavior.onStopNestedScroll(this, childAt, view, i);
                }
                layoutParams.resetNestedScroll(i);
                layoutParams.resetChangedAfterNestedScroll();
            }
        }
        this.mNestedScrollingTarget = null;
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        onNestedScroll(view, i, i2, i3, i4, 0);
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        onNestedScroll(view, i, i2, i3, i4, 0, this.mNestedScrollingV2ConsumedCompat);
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        int i6;
        boolean z;
        int i7;
        int childCount = getChildCount();
        boolean z2 = false;
        int i8 = 0;
        int i9 = 0;
        for (int i10 = 0; i10 < childCount; i10++) {
            View childAt = getChildAt(i10);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isNestedScrollAccepted(i5)) {
                    Behavior behavior = layoutParams.getBehavior();
                    if (behavior != null) {
                        int[] iArr2 = this.mBehaviorConsumed;
                        iArr2[0] = 0;
                        iArr2[1] = 0;
                        behavior.onNestedScroll(this, childAt, view, i, i2, i3, i4, i5, iArr2);
                        int[] iArr3 = this.mBehaviorConsumed;
                        if (i3 > 0) {
                            i6 = Math.max(i8, iArr3[0]);
                        } else {
                            i6 = Math.min(i8, iArr3[0]);
                        }
                        i8 = i6;
                        if (i4 > 0) {
                            z = true;
                            i7 = Math.max(i9, this.mBehaviorConsumed[1]);
                        } else {
                            z = true;
                            i7 = Math.min(i9, this.mBehaviorConsumed[1]);
                        }
                        i9 = i7;
                        z2 = z;
                    }
                }
            }
        }
        iArr[0] = iArr[0] + i8;
        iArr[1] = iArr[1] + i9;
        if (z2) {
            onChildViewsChanged(1);
        }
    }

    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        onNestedPreScroll(view, i, i2, iArr, 0);
    }

    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
        int i4;
        int i5;
        int childCount = getChildCount();
        boolean z = false;
        int i6 = 0;
        int i7 = 0;
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            if (childAt.getVisibility() == 8) {
                int i9 = i3;
            } else {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isNestedScrollAccepted(i3)) {
                    Behavior behavior = layoutParams.getBehavior();
                    if (behavior != null) {
                        int[] iArr2 = this.mBehaviorConsumed;
                        iArr2[0] = 0;
                        iArr2[1] = 0;
                        behavior.onNestedPreScroll(this, childAt, view, i, i2, iArr2, i3);
                        int[] iArr3 = this.mBehaviorConsumed;
                        if (i > 0) {
                            i4 = Math.max(i6, iArr3[0]);
                        } else {
                            i4 = Math.min(i6, iArr3[0]);
                        }
                        i6 = i4;
                        int[] iArr4 = this.mBehaviorConsumed;
                        if (i2 > 0) {
                            i5 = Math.max(i7, iArr4[1]);
                        } else {
                            i5 = Math.min(i7, iArr4[1]);
                        }
                        i7 = i5;
                        z = true;
                    }
                }
            }
        }
        iArr[0] = i6;
        iArr[1] = i7;
        if (z) {
            onChildViewsChanged(1);
        }
    }

    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        int childCount = getChildCount();
        boolean z2 = false;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isNestedScrollAccepted(0)) {
                    Behavior behavior = layoutParams.getBehavior();
                    if (behavior != null) {
                        z2 |= behavior.onNestedFling(this, childAt, view, f, f2, z);
                    }
                }
            }
        }
        if (z2) {
            onChildViewsChanged(1);
        }
        return z2;
    }

    public boolean onNestedPreFling(View view, float f, float f2) {
        int childCount = getChildCount();
        boolean z = false;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isNestedScrollAccepted(0)) {
                    Behavior behavior = layoutParams.getBehavior();
                    if (behavior != null) {
                        z |= behavior.onNestedPreFling(this, childAt, view, f, f2);
                    }
                }
            }
        }
        return z;
    }

    public int getNestedScrollAxes() {
        return this.mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        SparseArray<Parcelable> sparseArray = savedState.behaviorStates;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int id = childAt.getId();
            Behavior behavior = getResolvedLayoutParams(childAt).getBehavior();
            if (!(id == -1 || behavior == null)) {
                Parcelable parcelable2 = (Parcelable) sparseArray.get(id);
                if (parcelable2 != null) {
                    behavior.onRestoreInstanceState(this, childAt, parcelable2);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        SparseArray<Parcelable> sparseArray = new SparseArray<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int id = childAt.getId();
            Behavior behavior = ((LayoutParams) childAt.getLayoutParams()).getBehavior();
            if (!(id == -1 || behavior == null)) {
                Parcelable onSaveInstanceState = behavior.onSaveInstanceState(this, childAt);
                if (onSaveInstanceState != null) {
                    sparseArray.append(id, onSaveInstanceState);
                }
            }
        }
        savedState.behaviorStates = sparseArray;
        return savedState;
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        Behavior behavior = ((LayoutParams) view.getLayoutParams()).getBehavior();
        if (behavior == null || !behavior.onRequestChildRectangleOnScreen(this, view, rect, z)) {
            return super.requestChildRectangleOnScreen(view, rect, z);
        }
        return true;
    }

    private void setupForInsets() {
        if (VERSION.SDK_INT >= 21) {
            if (ViewCompat.getFitsSystemWindows(this)) {
                if (this.mApplyWindowInsetsListener == null) {
                    this.mApplyWindowInsetsListener = new OnApplyWindowInsetsListener() {
                        public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                            return CoordinatorLayout.this.setWindowInsets(windowInsetsCompat);
                        }
                    };
                }
                ViewCompat.setOnApplyWindowInsetsListener(this, this.mApplyWindowInsetsListener);
                setSystemUiVisibility(1280);
            } else {
                ViewCompat.setOnApplyWindowInsetsListener(this, null);
            }
        }
    }
}
