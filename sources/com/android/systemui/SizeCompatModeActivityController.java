package com.android.systemui;

import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.InvalidDisplayException;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import java.lang.ref.WeakReference;

public class SizeCompatModeActivityController extends SystemUI implements Callbacks {
    private final SparseArray<RestartActivityButton> mActiveButtons = new SparseArray<>(1);
    private final CommandQueue mCommandQueue;
    private final SparseArray<WeakReference<Context>> mDisplayContextCache = new SparseArray<>(0);
    private boolean mHasShownHint;

    @VisibleForTesting
    static class RestartActivityButton extends ImageButton implements OnClickListener, OnLongClickListener {
        IBinder mLastActivityToken;
        final int mPopupOffsetX;
        final int mPopupOffsetY;
        final boolean mShouldShowHint;
        PopupWindow mShowingHint;
        final LayoutParams mWinParams;

        private static int getGravity(int i) {
            return (i == 1 ? 8388611 : 8388613) | 80;
        }

        RestartActivityButton(Context context, boolean z) {
            super(context);
            this.mShouldShowHint = !z;
            Drawable drawable = context.getDrawable(C2010R$drawable.btn_restart);
            setImageDrawable(drawable);
            setContentDescription(context.getString(C2017R$string.restart_button_description));
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            this.mPopupOffsetX = intrinsicWidth / 2;
            int i = intrinsicHeight * 2;
            this.mPopupOffsetY = i;
            ColorStateList valueOf = ColorStateList.valueOf(-3355444);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(1);
            gradientDrawable.setColor(valueOf);
            setBackground(new RippleDrawable(valueOf, null, gradientDrawable));
            setOnClickListener(this);
            setOnLongClickListener(this);
            LayoutParams layoutParams = new LayoutParams();
            this.mWinParams = layoutParams;
            layoutParams.gravity = getGravity(getResources().getConfiguration().getLayoutDirection());
            LayoutParams layoutParams2 = this.mWinParams;
            layoutParams2.width = intrinsicWidth * 2;
            layoutParams2.height = i;
            layoutParams2.type = 2038;
            layoutParams2.flags = 40;
            layoutParams2.format = -3;
            layoutParams2.privateFlags |= 16;
            StringBuilder sb = new StringBuilder();
            sb.append(SizeCompatModeActivityController.class.getSimpleName());
            sb.append(context.getDisplayId());
            layoutParams2.setTitle(sb.toString());
        }

        /* access modifiers changed from: 0000 */
        public void updateLastTargetActivity(IBinder iBinder) {
            this.mLastActivityToken = iBinder;
        }

        /* access modifiers changed from: 0000 */
        public boolean show() {
            try {
                ((WindowManager) getContext().getSystemService(WindowManager.class)).addView(this, this.mWinParams);
                return true;
            } catch (InvalidDisplayException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot show on display ");
                sb.append(getContext().getDisplayId());
                Log.w("SizeCompatMode", sb.toString(), e);
                return false;
            }
        }

        /* access modifiers changed from: 0000 */
        public void remove() {
            PopupWindow popupWindow = this.mShowingHint;
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            ((WindowManager) getContext().getSystemService(WindowManager.class)).removeViewImmediate(this);
        }

        public void onClick(View view) {
            try {
                ActivityTaskManager.getService().restartActivityProcessIfVisible(this.mLastActivityToken);
            } catch (RemoteException e) {
                Log.w("SizeCompatMode", "Unable to restart activity", e);
            }
        }

        public boolean onLongClick(View view) {
            showHint();
            return true;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.mShouldShowHint) {
                showHint();
            }
        }

        public void setLayoutDirection(int i) {
            int gravity = getGravity(i);
            LayoutParams layoutParams = this.mWinParams;
            if (layoutParams.gravity != gravity) {
                layoutParams.gravity = gravity;
                PopupWindow popupWindow = this.mShowingHint;
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    showHint();
                }
                ((WindowManager) getContext().getSystemService(WindowManager.class)).updateViewLayout(this, this.mWinParams);
            }
            super.setLayoutDirection(i);
        }

        /* access modifiers changed from: 0000 */
        public void showHint() {
            if (this.mShowingHint == null) {
                View inflate = LayoutInflater.from(getContext()).inflate(C2013R$layout.size_compat_mode_hint, null);
                PopupWindow popupWindow = new PopupWindow(inflate, -2, -2);
                popupWindow.setWindowLayoutType(this.mWinParams.type);
                popupWindow.setElevation(getResources().getDimension(C2009R$dimen.bubble_elevation));
                popupWindow.setAnimationStyle(16973910);
                popupWindow.setClippingEnabled(false);
                popupWindow.setOnDismissListener(new OnDismissListener() {
                    public final void onDismiss() {
                        RestartActivityButton.this.mo9670x13b61354();
                    }
                });
                this.mShowingHint = popupWindow;
                Button button = (Button) inflate.findViewById(C2011R$id.got_it);
                button.setBackground(new RippleDrawable(ColorStateList.valueOf(-3355444), null, null));
                button.setOnClickListener(new OnClickListener(popupWindow) {
                    public final /* synthetic */ PopupWindow f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        this.f$0.dismiss();
                    }
                });
                popupWindow.showAtLocation(this, this.mWinParams.gravity, this.mPopupOffsetX, this.mPopupOffsetY);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$showHint$0 */
        public /* synthetic */ void mo9670x13b61354() {
            this.mShowingHint = null;
        }
    }

    @VisibleForTesting
    SizeCompatModeActivityController(Context context, ActivityManagerWrapper activityManagerWrapper, CommandQueue commandQueue) {
        super(context);
        this.mCommandQueue = commandQueue;
        activityManagerWrapper.registerTaskStackListener(new TaskStackChangeListener() {
            public void onSizeCompatModeActivityChanged(int i, IBinder iBinder) {
                SizeCompatModeActivityController.this.updateRestartButton(i, iBinder);
            }
        });
    }

    public void start() {
        this.mCommandQueue.addCallback((Callbacks) this);
    }

    public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
        RestartActivityButton restartActivityButton = (RestartActivityButton) this.mActiveButtons.get(i);
        if (restartActivityButton != null) {
            int i4 = 0;
            if ((i2 & 2) != 0) {
                i4 = 8;
            }
            if (restartActivityButton.getVisibility() != i4) {
                restartActivityButton.setVisibility(i4);
            }
        }
    }

    public void onDisplayRemoved(int i) {
        this.mDisplayContextCache.remove(i);
        removeRestartButton(i);
    }

    private void removeRestartButton(int i) {
        RestartActivityButton restartActivityButton = (RestartActivityButton) this.mActiveButtons.get(i);
        if (restartActivityButton != null) {
            restartActivityButton.remove();
            this.mActiveButtons.remove(i);
        }
    }

    /* access modifiers changed from: private */
    public void updateRestartButton(int i, IBinder iBinder) {
        if (iBinder == null) {
            removeRestartButton(i);
            return;
        }
        RestartActivityButton restartActivityButton = (RestartActivityButton) this.mActiveButtons.get(i);
        if (restartActivityButton != null) {
            restartActivityButton.updateLastTargetActivity(iBinder);
            return;
        }
        Context orCreateDisplayContext = getOrCreateDisplayContext(i);
        if (orCreateDisplayContext == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot get context for display ");
            sb.append(i);
            Log.i("SizeCompatMode", sb.toString());
            return;
        }
        RestartActivityButton createRestartButton = createRestartButton(orCreateDisplayContext);
        createRestartButton.updateLastTargetActivity(iBinder);
        if (createRestartButton.show()) {
            this.mActiveButtons.append(i, createRestartButton);
        } else {
            onDisplayRemoved(i);
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public RestartActivityButton createRestartButton(Context context) {
        RestartActivityButton restartActivityButton = new RestartActivityButton(context, this.mHasShownHint);
        this.mHasShownHint = true;
        return restartActivityButton;
    }

    private Context getOrCreateDisplayContext(int i) {
        if (i == 0) {
            return this.mContext;
        }
        Context context = null;
        WeakReference weakReference = (WeakReference) this.mDisplayContextCache.get(i);
        if (weakReference != null) {
            context = (Context) weakReference.get();
        }
        if (context == null) {
            Display display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(i);
            if (display != null) {
                context = this.mContext.createDisplayContext(display);
                this.mDisplayContextCache.put(i, new WeakReference(context));
            }
        }
        return context;
    }
}
