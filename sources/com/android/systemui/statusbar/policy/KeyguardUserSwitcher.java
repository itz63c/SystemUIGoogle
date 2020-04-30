package com.android.systemui.statusbar.policy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.p007qs.tiles.UserDetailItemView;
import com.android.systemui.statusbar.phone.KeyguardStatusBarView;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.policy.UserSwitcherController.BaseUserAdapter;
import com.android.systemui.statusbar.policy.UserSwitcherController.UserRecord;

public class KeyguardUserSwitcher {
    private final Adapter mAdapter;
    /* access modifiers changed from: private */
    public boolean mAnimating;
    private final AppearAnimationUtils mAppearAnimationUtils;
    private final KeyguardUserSwitcherScrim mBackground;
    /* access modifiers changed from: private */
    public ObjectAnimator mBgAnimator;
    public final DataSetObserver mDataSetObserver = new DataSetObserver() {
        public void onChanged() {
            KeyguardUserSwitcher.this.refresh();
        }
    };
    private final KeyguardStatusBarView mStatusBarView;
    /* access modifiers changed from: private */
    public ViewGroup mUserSwitcher;
    /* access modifiers changed from: private */
    public final Container mUserSwitcherContainer;
    private UserSwitcherController mUserSwitcherController;

    public static class Adapter extends BaseUserAdapter implements OnClickListener {
        private Context mContext;
        private View mCurrentUserView;
        private KeyguardUserSwitcher mKeyguardUserSwitcher;

        public Adapter(Context context, UserSwitcherController userSwitcherController, KeyguardUserSwitcher keyguardUserSwitcher) {
            super(userSwitcherController);
            this.mContext = context;
            this.mKeyguardUserSwitcher = keyguardUserSwitcher;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            UserRecord item = getItem(i);
            if (!(view instanceof UserDetailItemView) || !(view.getTag() instanceof UserRecord)) {
                view = LayoutInflater.from(this.mContext).inflate(C2013R$layout.keyguard_user_switcher_item, viewGroup, false);
                view.setOnClickListener(this);
            }
            UserDetailItemView userDetailItemView = (UserDetailItemView) view;
            String name = getName(this.mContext, item);
            Bitmap bitmap = item.picture;
            if (bitmap == null) {
                userDetailItemView.bind(name, getDrawable(this.mContext, item).mutate(), item.resolveId());
            } else {
                userDetailItemView.bind(name, bitmap, item.info.id);
            }
            userDetailItemView.setAvatarEnabled(item.isSwitchToEnabled);
            view.setActivated(item.isCurrent);
            if (item.isCurrent) {
                this.mCurrentUserView = view;
            }
            view.setTag(item);
            return view;
        }

        public void onClick(View view) {
            UserRecord userRecord = (UserRecord) view.getTag();
            if (userRecord.isCurrent && !userRecord.isGuest) {
                this.mKeyguardUserSwitcher.hideIfNotSimple(true);
            } else if (userRecord.isSwitchToEnabled) {
                if (!userRecord.isAddUser && !userRecord.isRestricted && !userRecord.isDisabledByAdmin) {
                    View view2 = this.mCurrentUserView;
                    if (view2 != null) {
                        view2.setActivated(false);
                    }
                    view.setActivated(true);
                }
                switchTo(userRecord);
            }
        }
    }

    public static class Container extends FrameLayout {
        private KeyguardUserSwitcher mKeyguardUserSwitcher;

        public Container(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            setClipChildren(false);
        }

        public void setKeyguardUserSwitcher(KeyguardUserSwitcher keyguardUserSwitcher) {
            this.mKeyguardUserSwitcher = keyguardUserSwitcher;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
            if (keyguardUserSwitcher != null && !keyguardUserSwitcher.isAnimating()) {
                this.mKeyguardUserSwitcher.hideIfNotSimple(true);
            }
            return false;
        }
    }

    public KeyguardUserSwitcher(Context context, ViewStub viewStub, KeyguardStatusBarView keyguardStatusBarView, NotificationPanelViewController notificationPanelViewController) {
        boolean z = context.getResources().getBoolean(C2007R$bool.config_keyguardUserSwitcher);
        UserSwitcherController userSwitcherController = (UserSwitcherController) Dependency.get(UserSwitcherController.class);
        if (userSwitcherController == null || !z) {
            this.mUserSwitcherContainer = null;
            this.mStatusBarView = null;
            this.mAdapter = null;
            this.mAppearAnimationUtils = null;
            this.mBackground = null;
            return;
        }
        this.mUserSwitcherContainer = (Container) viewStub.inflate();
        this.mBackground = new KeyguardUserSwitcherScrim(context);
        reinflateViews();
        this.mStatusBarView = keyguardStatusBarView;
        keyguardStatusBarView.setKeyguardUserSwitcher(this);
        notificationPanelViewController.setKeyguardUserSwitcher(this);
        Adapter adapter = new Adapter(context, userSwitcherController, this);
        this.mAdapter = adapter;
        adapter.registerDataSetObserver(this.mDataSetObserver);
        this.mUserSwitcherController = userSwitcherController;
        AppearAnimationUtils appearAnimationUtils = new AppearAnimationUtils(context, 400, -0.5f, 0.5f, Interpolators.FAST_OUT_SLOW_IN);
        this.mAppearAnimationUtils = appearAnimationUtils;
        this.mUserSwitcherContainer.setKeyguardUserSwitcher(this);
    }

    private void reinflateViews() {
        ViewGroup viewGroup = this.mUserSwitcher;
        if (viewGroup != null) {
            viewGroup.setBackground(null);
            this.mUserSwitcher.removeOnLayoutChangeListener(this.mBackground);
        }
        this.mUserSwitcherContainer.removeAllViews();
        LayoutInflater.from(this.mUserSwitcherContainer.getContext()).inflate(C2013R$layout.keyguard_user_switcher_inner, this.mUserSwitcherContainer);
        ViewGroup viewGroup2 = (ViewGroup) this.mUserSwitcherContainer.findViewById(C2011R$id.keyguard_user_switcher_inner);
        this.mUserSwitcher = viewGroup2;
        viewGroup2.addOnLayoutChangeListener(this.mBackground);
        this.mUserSwitcher.setBackground(this.mBackground);
    }

    public void setKeyguard(boolean z, boolean z2) {
        if (this.mUserSwitcher == null) {
            return;
        }
        if (!z || !shouldExpandByDefault()) {
            hide(z2);
        } else {
            show(z2);
        }
    }

    private boolean shouldExpandByDefault() {
        UserSwitcherController userSwitcherController = this.mUserSwitcherController;
        return userSwitcherController != null && userSwitcherController.isSimpleUserSwitcher();
    }

    public void show(boolean z) {
        if (this.mUserSwitcher != null && this.mUserSwitcherContainer.getVisibility() != 0) {
            cancelAnimations();
            this.mAdapter.refresh();
            this.mUserSwitcherContainer.setVisibility(0);
            this.mStatusBarView.setKeyguardUserSwitcherShowing(true, z);
            if (z) {
                startAppearAnimation();
            }
        }
    }

    private boolean hide(boolean z) {
        if (this.mUserSwitcher == null || this.mUserSwitcherContainer.getVisibility() != 0) {
            return false;
        }
        cancelAnimations();
        if (z) {
            startDisappearAnimation();
        } else {
            this.mUserSwitcherContainer.setVisibility(8);
        }
        this.mStatusBarView.setKeyguardUserSwitcherShowing(false, z);
        return true;
    }

    private void cancelAnimations() {
        int childCount = this.mUserSwitcher.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mUserSwitcher.getChildAt(i).animate().cancel();
        }
        ObjectAnimator objectAnimator = this.mBgAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        this.mUserSwitcher.animate().cancel();
        this.mAnimating = false;
    }

    private void startAppearAnimation() {
        int childCount = this.mUserSwitcher.getChildCount();
        View[] viewArr = new View[childCount];
        for (int i = 0; i < childCount; i++) {
            viewArr[i] = this.mUserSwitcher.getChildAt(i);
        }
        this.mUserSwitcher.setClipChildren(false);
        this.mUserSwitcher.setClipToPadding(false);
        this.mAppearAnimationUtils.startAnimation(viewArr, new Runnable() {
            public void run() {
                KeyguardUserSwitcher.this.mUserSwitcher.setClipChildren(true);
                KeyguardUserSwitcher.this.mUserSwitcher.setClipToPadding(true);
            }
        });
        this.mAnimating = true;
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this.mBackground, "alpha", new int[]{0, 255});
        this.mBgAnimator = ofInt;
        ofInt.setDuration(400);
        this.mBgAnimator.setInterpolator(Interpolators.ALPHA_IN);
        this.mBgAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                KeyguardUserSwitcher.this.mBgAnimator = null;
                KeyguardUserSwitcher.this.mAnimating = false;
            }
        });
        this.mBgAnimator.start();
    }

    private void startDisappearAnimation() {
        this.mAnimating = true;
        this.mUserSwitcher.animate().alpha(0.0f).setDuration(300).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable() {
            public void run() {
                KeyguardUserSwitcher.this.mUserSwitcherContainer.setVisibility(8);
                KeyguardUserSwitcher.this.mUserSwitcher.setAlpha(1.0f);
                KeyguardUserSwitcher.this.mAnimating = false;
            }
        });
    }

    /* access modifiers changed from: private */
    public void refresh() {
        int childCount = this.mUserSwitcher.getChildCount();
        int count = this.mAdapter.getCount();
        int max = Math.max(childCount, count);
        for (int i = 0; i < max; i++) {
            if (i < count) {
                View view = null;
                if (i < childCount) {
                    view = this.mUserSwitcher.getChildAt(i);
                }
                View view2 = this.mAdapter.getView(i, view, this.mUserSwitcher);
                if (view == null) {
                    this.mUserSwitcher.addView(view2);
                } else if (view != view2) {
                    this.mUserSwitcher.removeViewAt(i);
                    this.mUserSwitcher.addView(view2, i);
                }
            } else {
                this.mUserSwitcher.removeViewAt(this.mUserSwitcher.getChildCount() - 1);
            }
        }
    }

    public boolean hideIfNotSimple(boolean z) {
        if (this.mUserSwitcherContainer == null || this.mUserSwitcherController.isSimpleUserSwitcher()) {
            return false;
        }
        return hide(z);
    }

    /* access modifiers changed from: 0000 */
    public boolean isAnimating() {
        return this.mAnimating;
    }

    public void onDensityOrFontScaleChanged() {
        if (this.mUserSwitcherContainer != null) {
            reinflateViews();
            refresh();
        }
    }
}
