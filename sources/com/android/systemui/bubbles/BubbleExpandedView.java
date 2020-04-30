package com.android.systemui.bubbles;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.ActivityView;
import android.app.ActivityView.StateCallback;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewRootImpl;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.recents.TriangleShape;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.AlphaOptimizedButton;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public class BubbleExpandedView extends LinearLayout implements OnClickListener {
    /* access modifiers changed from: private */
    public ActivityView mActivityView;
    /* access modifiers changed from: private */
    public ActivityViewStatus mActivityViewStatus;
    /* access modifiers changed from: private */
    public Bubble mBubble;
    /* access modifiers changed from: private */
    public BubbleController mBubbleController;
    private Point mDisplaySize;
    private int mExpandedViewTouchSlop;
    /* access modifiers changed from: private */
    public boolean mIsOverflow;
    private boolean mKeyboardVisible;
    private int mMinHeight;
    private boolean mNeedsNewHeight;
    private int mOverflowHeight;
    /* access modifiers changed from: private */
    public PendingIntent mPendingIntent;
    private ShapeDrawable mPointerDrawable;
    private int mPointerHeight;
    private int mPointerMargin;
    private View mPointerView;
    private int mPointerWidth;
    private AlphaOptimizedButton mSettingsIcon;
    private int mSettingsIconHeight;
    private BubbleStackView mStackView;
    private StateCallback mStateCallback;
    /* access modifiers changed from: private */
    public int mTaskId;
    private int[] mTempLoc;
    private Rect mTempRect;
    private WindowManager mWindowManager;

    /* renamed from: com.android.systemui.bubbles.BubbleExpandedView$2 */
    static /* synthetic */ class C07692 {

        /* renamed from: $SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus */
        static final /* synthetic */ int[] f34x9ad3957a;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.android.systemui.bubbles.BubbleExpandedView$ActivityViewStatus[] r0 = com.android.systemui.bubbles.BubbleExpandedView.ActivityViewStatus.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f34x9ad3957a = r0
                com.android.systemui.bubbles.BubbleExpandedView$ActivityViewStatus r1 = com.android.systemui.bubbles.BubbleExpandedView.ActivityViewStatus.INITIALIZING     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f34x9ad3957a     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.bubbles.BubbleExpandedView$ActivityViewStatus r1 = com.android.systemui.bubbles.BubbleExpandedView.ActivityViewStatus.INITIALIZED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f34x9ad3957a     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.bubbles.BubbleExpandedView$ActivityViewStatus r1 = com.android.systemui.bubbles.BubbleExpandedView.ActivityViewStatus.ACTIVITY_STARTED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.bubbles.BubbleExpandedView.C07692.<clinit>():void");
        }
    }

    private enum ActivityViewStatus {
        INITIALIZING,
        INITIALIZED,
        ACTIVITY_STARTED,
        RELEASED
    }

    public BubbleExpandedView(Context context) {
        this(context, null);
    }

    public BubbleExpandedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BubbleExpandedView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public BubbleExpandedView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mActivityViewStatus = ActivityViewStatus.INITIALIZING;
        this.mTaskId = -1;
        this.mTempRect = new Rect();
        this.mTempLoc = new int[2];
        this.mBubbleController = (BubbleController) Dependency.get(BubbleController.class);
        this.mStateCallback = new StateCallback() {
            public void onActivityViewReady(ActivityView activityView) {
                int i = C07692.f34x9ad3957a[BubbleExpandedView.this.mActivityViewStatus.ordinal()];
                if (i == 1 || i == 2) {
                    BubbleExpandedView.this.post(new Runnable(ActivityOptions.makeCustomAnimation(BubbleExpandedView.this.getContext(), 0, 0)) {
                        public final /* synthetic */ ActivityOptions f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            C07681.this.lambda$onActivityViewReady$0$BubbleExpandedView$1(this.f$1);
                        }
                    });
                    BubbleExpandedView.this.mActivityViewStatus = ActivityViewStatus.ACTIVITY_STARTED;
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onActivityViewReady$0 */
            public /* synthetic */ void lambda$onActivityViewReady$0$BubbleExpandedView$1(ActivityOptions activityOptions) {
                try {
                    if (BubbleExpandedView.this.mIsOverflow || !BubbleExpandedView.this.mBubble.usingShortcutInfo()) {
                        Intent intent = new Intent();
                        intent.addFlags(524288);
                        intent.addFlags(134217728);
                        BubbleExpandedView.this.mActivityView.startActivity(BubbleExpandedView.this.mPendingIntent, intent, activityOptions);
                        return;
                    }
                    BubbleExpandedView.this.mActivityView.startShortcutActivity(BubbleExpandedView.this.mBubble.getShortcutInfo(), activityOptions, null);
                } catch (RuntimeException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Exception while displaying bubble: ");
                    sb.append(BubbleExpandedView.this.getBubbleKey());
                    sb.append(", ");
                    sb.append(e.getMessage());
                    sb.append("; removing bubble");
                    Log.w("Bubbles", sb.toString());
                    BubbleExpandedView.this.mBubbleController.removeBubble(BubbleExpandedView.this.getBubbleEntry(), 10);
                }
            }

            public void onActivityViewDestroyed(ActivityView activityView) {
                BubbleExpandedView.this.mActivityViewStatus = ActivityViewStatus.RELEASED;
            }

            public void onTaskCreated(int i, ComponentName componentName) {
                BubbleExpandedView.this.mTaskId = i;
            }

            public void onTaskRemovalStarted(int i) {
                if (BubbleExpandedView.this.mBubble != null && !BubbleExpandedView.this.mBubbleController.isUserCreatedBubble(BubbleExpandedView.this.mBubble.getKey())) {
                    BubbleExpandedView.this.post(new Runnable() {
                        public final void run() {
                            C07681.this.lambda$onTaskRemovalStarted$1$BubbleExpandedView$1();
                        }
                    });
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onTaskRemovalStarted$1 */
            public /* synthetic */ void lambda$onTaskRemovalStarted$1$BubbleExpandedView$1() {
                BubbleExpandedView.this.mBubbleController.removeBubble(BubbleExpandedView.this.mBubble.getEntry(), 3);
            }
        };
        this.mDisplaySize = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        this.mWindowManager = windowManager;
        windowManager.getDefaultDisplay().getRealSize(this.mDisplaySize);
        Resources resources = getResources();
        this.mMinHeight = resources.getDimensionPixelSize(C2009R$dimen.bubble_expanded_default_height);
        this.mOverflowHeight = resources.getDimensionPixelSize(C2009R$dimen.bubble_overflow_height);
        this.mPointerMargin = resources.getDimensionPixelSize(C2009R$dimen.bubble_pointer_margin);
        this.mExpandedViewTouchSlop = resources.getDimensionPixelSize(C2009R$dimen.bubble_expanded_view_slop);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        Resources resources = getResources();
        this.mPointerView = findViewById(C2011R$id.pointer_view);
        this.mPointerWidth = resources.getDimensionPixelSize(C2009R$dimen.bubble_pointer_width);
        this.mPointerHeight = resources.getDimensionPixelSize(C2009R$dimen.bubble_pointer_height);
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) this.mPointerWidth, (float) this.mPointerHeight, true));
        this.mPointerDrawable = shapeDrawable;
        this.mPointerView.setBackground(shapeDrawable);
        this.mPointerView.setVisibility(4);
        this.mSettingsIconHeight = getContext().getResources().getDimensionPixelSize(C2009R$dimen.bubble_manage_button_height);
        AlphaOptimizedButton alphaOptimizedButton = (AlphaOptimizedButton) findViewById(C2011R$id.settings_button);
        this.mSettingsIcon = alphaOptimizedButton;
        alphaOptimizedButton.setOnClickListener(this);
        this.mActivityView = new ActivityView(this.mContext, null, 0, true);
        setContentVisibility(false);
        addView(this.mActivityView);
        bringChildToFront(this.mActivityView);
        bringChildToFront(this.mSettingsIcon);
        applyThemeAttrs();
        setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return BubbleExpandedView.this.lambda$onFinishInflate$0$BubbleExpandedView(view, windowInsets);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ WindowInsets lambda$onFinishInflate$0$BubbleExpandedView(View view, WindowInsets windowInsets) {
        boolean z = windowInsets.getSystemWindowInsetBottom() - windowInsets.getStableInsetBottom() != 0;
        this.mKeyboardVisible = z;
        if (!z && this.mNeedsNewHeight) {
            updateHeight();
        }
        return view.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: private */
    public String getBubbleKey() {
        Bubble bubble = this.mBubble;
        return bubble != null ? bubble.getKey() : "null";
    }

    /* access modifiers changed from: private */
    public NotificationEntry getBubbleEntry() {
        Bubble bubble = this.mBubble;
        if (bubble != null) {
            return bubble.getEntry();
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void applyThemeAttrs() {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{16844002, 16844145});
        int color = obtainStyledAttributes.getColor(0, -1);
        float dimensionPixelSize = (float) obtainStyledAttributes.getDimensionPixelSize(1, 0);
        obtainStyledAttributes.recycle();
        this.mPointerDrawable.setTint(color);
        if (this.mActivityView != null && ScreenDecorationsUtils.supportsRoundedCornersOnWindows(this.mContext.getResources())) {
            this.mActivityView.setCornerRadius(dimensionPixelSize);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyboardVisible = false;
        this.mNeedsNewHeight = false;
        ActivityView activityView = this.mActivityView;
        if (activityView == null) {
            return;
        }
        if (ViewRootImpl.sNewInsetsMode == 2) {
            this.mStackView.animate().setDuration(100).translationY(0.0f);
        } else {
            activityView.setForwardedInsets(Insets.of(0, 0, 0, 0));
        }
    }

    /* access modifiers changed from: 0000 */
    public void setContentVisibility(boolean z) {
        float f = z ? 1.0f : 0.0f;
        this.mPointerView.setAlpha(f);
        ActivityView activityView = this.mActivityView;
        if (activityView != null) {
            activityView.setAlpha(f);
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateInsets(WindowInsets windowInsets) {
        if (usingActivityView()) {
            int max = Math.max((this.mActivityView.getLocationOnScreen()[1] + this.mActivityView.getHeight()) - (this.mDisplaySize.y - Math.max(windowInsets.getSystemWindowInsetBottom(), windowInsets.getDisplayCutout() != null ? windowInsets.getDisplayCutout().getSafeInsetBottom() : 0)), 0);
            if (ViewRootImpl.sNewInsetsMode == 2) {
                this.mStackView.animate().setDuration(100).translationY((float) (-max)).withEndAction(new Runnable() {
                    public final void run() {
                        BubbleExpandedView.this.lambda$updateInsets$1$BubbleExpandedView();
                    }
                });
            } else {
                this.mActivityView.setForwardedInsets(Insets.of(0, 0, 0, max));
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateInsets$1 */
    public /* synthetic */ void lambda$updateInsets$1$BubbleExpandedView() {
        this.mActivityView.onLocationChanged();
    }

    /* access modifiers changed from: 0000 */
    public void setStackView(BubbleStackView bubbleStackView) {
        this.mStackView = bubbleStackView;
    }

    public void setOverflow(boolean z) {
        this.mIsOverflow = z;
        this.mPendingIntent = PendingIntent.getActivity(this.mContext, 0, new Intent(this.mContext, BubbleOverflowActivity.class), 134217728);
        this.mSettingsIcon.setVisibility(8);
    }

    /* access modifiers changed from: 0000 */
    public void update(Bubble bubble) {
        boolean z = this.mBubble == null;
        if (z || (bubble != null && bubble.getKey().equals(this.mBubble.getKey()))) {
            this.mBubble = bubble;
            this.mSettingsIcon.setContentDescription(getResources().getString(C2017R$string.bubbles_settings_button_description, new Object[]{bubble.getAppName()}));
            if (z) {
                PendingIntent bubbleIntent = this.mBubble.getBubbleIntent();
                this.mPendingIntent = bubbleIntent;
                if (!(bubbleIntent == null && this.mBubble.getShortcutInfo() == null)) {
                    setContentVisibility(false);
                    this.mActivityView.setVisibility(0);
                }
            }
            applyThemeAttrs();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Trying to update entry with different key, new bubble: ");
        sb.append(bubble.getKey());
        sb.append(" old bubble: ");
        sb.append(bubble.getKey());
        Log.w("Bubbles", sb.toString());
    }

    /* access modifiers changed from: 0000 */
    public void populateExpandedView() {
        if (usingActivityView()) {
            this.mActivityView.setCallback(this.mStateCallback);
        } else {
            Log.e("Bubbles", "Cannot populate expanded view.");
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean performBackPressIfNeeded() {
        if (!usingActivityView()) {
            return false;
        }
        this.mActivityView.performBackPress();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void updateHeight() {
        if (usingActivityView()) {
            float f = (float) this.mOverflowHeight;
            if (!this.mIsOverflow) {
                f = Math.max(this.mBubble.getDesiredHeight(this.mContext), (float) this.mMinHeight);
            }
            float max = Math.max(Math.min(f, (float) getMaxExpandedHeight()), (float) (this.mIsOverflow ? this.mOverflowHeight : this.mMinHeight));
            LayoutParams layoutParams = (LayoutParams) this.mActivityView.getLayoutParams();
            this.mNeedsNewHeight = ((float) layoutParams.height) != max;
            if (!this.mKeyboardVisible) {
                layoutParams.height = (int) max;
                this.mActivityView.setLayoutParams(layoutParams);
                this.mNeedsNewHeight = false;
            }
        }
    }

    private int getMaxExpandedHeight() {
        this.mWindowManager.getDefaultDisplay().getRealSize(this.mDisplaySize);
        return ((((this.mDisplaySize.y - this.mActivityView.getLocationOnScreen()[1]) - this.mSettingsIconHeight) - this.mPointerHeight) - this.mPointerMargin) - (getRootWindowInsets() != null ? getRootWindowInsets().getStableInsetBottom() : 0);
    }

    /* access modifiers changed from: 0000 */
    public boolean intersectingTouchableContent(int i, int i2) {
        this.mTempRect.setEmpty();
        ActivityView activityView = this.mActivityView;
        if (activityView != null) {
            int[] locationOnScreen = activityView.getLocationOnScreen();
            this.mTempLoc = locationOnScreen;
            Rect rect = this.mTempRect;
            int i3 = locationOnScreen[0];
            int i4 = this.mExpandedViewTouchSlop;
            rect.set(i3 - i4, locationOnScreen[1] - i4, locationOnScreen[0] + this.mActivityView.getWidth() + this.mExpandedViewTouchSlop, this.mTempLoc[1] + this.mActivityView.getHeight() + this.mExpandedViewTouchSlop);
        }
        if (this.mTempRect.contains(i, i2)) {
            return true;
        }
        int[] locationOnScreen2 = this.mSettingsIcon.getLocationOnScreen();
        this.mTempLoc = locationOnScreen2;
        this.mTempRect.set(locationOnScreen2[0], locationOnScreen2[1], locationOnScreen2[0] + this.mSettingsIcon.getWidth(), this.mTempLoc[1] + this.mSettingsIcon.getHeight());
        return this.mTempRect.contains(i, i2);
    }

    public void onClick(View view) {
        if (this.mBubble != null && view.getId() == C2011R$id.settings_button) {
            this.mStackView.collapseStack(new Runnable(this.mBubble.getSettingsIntent()) {
                public final /* synthetic */ Intent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BubbleExpandedView.this.lambda$onClick$2$BubbleExpandedView(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClick$2 */
    public /* synthetic */ void lambda$onClick$2$BubbleExpandedView(Intent intent) {
        this.mContext.startActivityAsUser(intent, this.mBubble.getEntry().getSbn().getUser());
        logBubbleClickEvent(this.mBubble, 9);
    }

    public void updateView() {
        if (usingActivityView() && this.mActivityView.getVisibility() == 0 && this.mActivityView.isAttachedToWindow()) {
            this.mActivityView.onLocationChanged();
        }
        updateHeight();
    }

    public void setPointerPosition(float f) {
        this.mPointerView.setTranslationX(f - (((float) this.mPointerWidth) / 2.0f));
        this.mPointerView.setVisibility(0);
    }

    public Rect getManageButtonLocationOnScreen() {
        this.mTempLoc = this.mSettingsIcon.getLocationOnScreen();
        int[] iArr = this.mTempLoc;
        return new Rect(iArr[0], iArr[1], iArr[0] + this.mSettingsIcon.getWidth(), this.mTempLoc[1] + this.mSettingsIcon.getHeight());
    }

    public void cleanUpExpandedState() {
        if (this.mActivityView != null) {
            int i = C07692.f34x9ad3957a[this.mActivityViewStatus.ordinal()];
            if (i == 2 || i == 3) {
                this.mActivityView.release();
            }
            if (this.mTaskId != -1) {
                try {
                    ActivityTaskManager.getService().removeTask(this.mTaskId);
                } catch (RemoteException unused) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to remove taskId ");
                    sb.append(this.mTaskId);
                    Log.w("Bubbles", sb.toString());
                }
                this.mTaskId = -1;
            }
            removeView(this.mActivityView);
            this.mActivityView = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void notifyDisplayEmpty() {
        if (this.mActivityViewStatus == ActivityViewStatus.ACTIVITY_STARTED) {
            this.mActivityViewStatus = ActivityViewStatus.INITIALIZED;
        }
    }

    private boolean usingActivityView() {
        return ((this.mPendingIntent == null && this.mBubble.getShortcutInfo() == null) || this.mActivityView == null) ? false : true;
    }

    public int getVirtualDisplayId() {
        if (usingActivityView()) {
            return this.mActivityView.getVirtualDisplayId();
        }
        return -1;
    }

    private void logBubbleClickEvent(Bubble bubble, int i) {
        StatusBarNotification sbn = bubble.getEntry().getSbn();
        String packageName = sbn.getPackageName();
        String channelId = sbn.getNotification().getChannelId();
        int id = sbn.getId();
        BubbleStackView bubbleStackView = this.mStackView;
        SysUiStatsLog.write(149, packageName, channelId, id, bubbleStackView.getBubbleIndex(bubbleStackView.getExpandedBubble()), this.mStackView.getBubbleCount(), i, this.mStackView.getNormalizedXPosition(), this.mStackView.getNormalizedYPosition(), bubble.showInShade(), bubble.isOngoing(), false);
    }
}
