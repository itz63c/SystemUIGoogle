package com.android.systemui.pip.phone;

import android.app.ActivityManager.StackInfo;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.view.MotionEvent;
import com.android.systemui.pip.phone.PipMediaController.ActionListener;
import com.android.systemui.pip.phone.PipMenuActivityController.Listener;
import com.android.systemui.shared.system.InputConsumerController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PipMenuActivityController {
    private ParceledListSlice mAppActions;
    private Context mContext;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            boolean z = true;
            if (i == 100) {
                int i2 = message.arg1;
                if (message.arg2 == 0) {
                    z = false;
                }
                PipMenuActivityController.this.onMenuStateChanged(i2, z);
            } else if (i == 101) {
                PipMenuActivityController.this.mListeners.forEach(C0905xe1b000b3.INSTANCE);
            } else if (i == 103) {
                PipMenuActivityController.this.mListeners.forEach(C0906x72ebda8b.INSTANCE);
            } else if (i == 104) {
                PipMenuActivityController.this.mToActivityMessenger = message.replyTo;
                PipMenuActivityController.this.setStartActivityRequested(false);
                if (PipMenuActivityController.this.mOnAnimationEndRunnable != null) {
                    PipMenuActivityController.this.mOnAnimationEndRunnable.run();
                    PipMenuActivityController.this.mOnAnimationEndRunnable = null;
                }
                if (PipMenuActivityController.this.mToActivityMessenger == null) {
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    PipMenuActivityController.this.onMenuStateChanged(0, z);
                }
            } else if (i == 107) {
                PipMenuActivityController.this.mListeners.forEach(C0907xb9b5077f.INSTANCE);
            }
        }
    };
    private InputConsumerController mInputConsumerController;
    /* access modifiers changed from: private */
    public ArrayList<Listener> mListeners = new ArrayList<>();
    private ActionListener mMediaActionListener = new ActionListener() {
        public void onMediaActionsChanged(List<RemoteAction> list) {
            PipMenuActivityController.this.mMediaActions = new ParceledListSlice(list);
            PipMenuActivityController.this.updateMenuActions();
        }
    };
    /* access modifiers changed from: private */
    public ParceledListSlice mMediaActions;
    private PipMediaController mMediaController;
    private int mMenuState;
    private Messenger mMessenger = new Messenger(this.mHandler);
    /* access modifiers changed from: private */
    public Runnable mOnAnimationEndRunnable;
    private boolean mStartActivityRequested;
    private long mStartActivityRequestedTime;
    private Runnable mStartActivityRequestedTimeoutRunnable = new Runnable() {
        public final void run() {
            PipMenuActivityController.this.lambda$new$0$PipMenuActivityController();
        }
    };
    private Bundle mTmpDismissFractionData = new Bundle();
    /* access modifiers changed from: private */
    public Messenger mToActivityMessenger;

    public interface Listener {
        void onPipDismiss();

        void onPipExpand();

        void onPipMenuStateChanged(int i, boolean z);

        void onPipShowMenu();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PipMenuActivityController() {
        setStartActivityRequested(false);
        Runnable runnable = this.mOnAnimationEndRunnable;
        if (runnable != null) {
            runnable.run();
            this.mOnAnimationEndRunnable = null;
        }
        Log.e("PipMenuActController", "Expected start menu activity request timed out");
    }

    public PipMenuActivityController(Context context, PipMediaController pipMediaController, InputConsumerController inputConsumerController) {
        this.mContext = context;
        this.mMediaController = pipMediaController;
        this.mInputConsumerController = inputConsumerController;
    }

    public boolean isMenuActivityVisible() {
        return this.mToActivityMessenger != null;
    }

    public void onActivityPinned() {
        this.mInputConsumerController.registerInputConsumer();
    }

    public void onActivityUnpinned() {
        hideMenu();
        this.mInputConsumerController.unregisterInputConsumer();
        setStartActivityRequested(false);
    }

    public void onPinnedStackAnimationEnded() {
        if (this.mToActivityMessenger != null) {
            Message obtain = Message.obtain();
            obtain.what = 6;
            try {
                this.mToActivityMessenger.send(obtain);
            } catch (RemoteException e) {
                Log.e("PipMenuActController", "Could not notify menu pinned animation ended", e);
            }
        }
    }

    public void addListener(Listener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void setDismissFraction(float f) {
        if (this.mToActivityMessenger != null) {
            this.mTmpDismissFractionData.clear();
            this.mTmpDismissFractionData.putFloat("dismiss_fraction", f);
            Message obtain = Message.obtain();
            obtain.what = 5;
            obtain.obj = this.mTmpDismissFractionData;
            try {
                this.mToActivityMessenger.send(obtain);
            } catch (RemoteException e) {
                Log.e("PipMenuActController", "Could not notify menu to update dismiss fraction", e);
            }
        } else if (!this.mStartActivityRequested || isStartActivityRequestedElapsed()) {
            startMenuActivity(0, null, null, false, false);
        }
    }

    public void showMenu(int i, Rect rect, Rect rect2, boolean z, boolean z2) {
        if (this.mToActivityMessenger != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("menu_state", i);
            if (rect != null) {
                bundle.putParcelable("stack_bounds", rect);
            }
            bundle.putParcelable("movement_bounds", rect2);
            bundle.putBoolean("allow_timeout", z);
            bundle.putBoolean("resize_menu_on_show", z2);
            Message obtain = Message.obtain();
            obtain.what = 1;
            obtain.obj = bundle;
            try {
                this.mToActivityMessenger.send(obtain);
            } catch (RemoteException e) {
                Log.e("PipMenuActController", "Could not notify menu to show", e);
            }
        } else if (!this.mStartActivityRequested || isStartActivityRequestedElapsed()) {
            startMenuActivity(i, rect, rect2, z, z2);
        }
    }

    public void pokeMenu() {
        if (this.mToActivityMessenger != null) {
            Message obtain = Message.obtain();
            obtain.what = 2;
            try {
                this.mToActivityMessenger.send(obtain);
            } catch (RemoteException e) {
                Log.e("PipMenuActController", "Could not notify poke menu", e);
            }
        }
    }

    public void hideMenu() {
        if (this.mToActivityMessenger != null) {
            Message obtain = Message.obtain();
            obtain.what = 3;
            try {
                this.mToActivityMessenger.send(obtain);
            } catch (RemoteException e) {
                Log.e("PipMenuActController", "Could not notify menu to hide", e);
            }
        }
    }

    public void hideMenuWithoutResize() {
        onMenuStateChanged(0, false);
    }

    public void setAppActions(ParceledListSlice parceledListSlice) {
        this.mAppActions = parceledListSlice;
        updateMenuActions();
    }

    private ParceledListSlice resolveMenuActions() {
        if (isValidActions(this.mAppActions)) {
            return this.mAppActions;
        }
        return this.mMediaActions;
    }

    private void startMenuActivity(int i, Rect rect, Rect rect2, boolean z, boolean z2) {
        String str = "PipMenuActController";
        try {
            StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
            if (stackInfo == null || stackInfo.taskIds == null || stackInfo.taskIds.length <= 0) {
                Log.e(str, "No PIP tasks found");
                return;
            }
            Intent intent = new Intent(this.mContext, PipMenuActivity.class);
            intent.setFlags(268435456);
            intent.putExtra("messenger", this.mMessenger);
            intent.putExtra("actions", resolveMenuActions());
            if (rect != null) {
                intent.putExtra("stack_bounds", rect);
            }
            if (rect2 != null) {
                intent.putExtra("movement_bounds", rect2);
            }
            intent.putExtra("menu_state", i);
            intent.putExtra("allow_timeout", z);
            intent.putExtra("resize_menu_on_show", z2);
            ActivityOptions makeCustomAnimation = ActivityOptions.makeCustomAnimation(this.mContext, 0, 0);
            makeCustomAnimation.setLaunchTaskId(stackInfo.taskIds[stackInfo.taskIds.length - 1]);
            makeCustomAnimation.setTaskOverlay(true, true);
            this.mContext.startActivityAsUser(intent, makeCustomAnimation.toBundle(), UserHandle.CURRENT);
            setStartActivityRequested(true);
        } catch (RemoteException e) {
            setStartActivityRequested(false);
            Log.e(str, "Error showing PIP menu activity", e);
        }
    }

    /* access modifiers changed from: private */
    public void updateMenuActions() {
        String str = "PipMenuActController";
        if (this.mToActivityMessenger != null) {
            Rect rect = null;
            try {
                StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
                if (stackInfo != null) {
                    rect = stackInfo.bounds;
                }
            } catch (RemoteException e) {
                Log.e(str, "Error showing PIP menu activity", e);
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable("stack_bounds", rect);
            bundle.putParcelable("actions", resolveMenuActions());
            Message obtain = Message.obtain();
            obtain.what = 4;
            obtain.obj = bundle;
            try {
                this.mToActivityMessenger.send(obtain);
            } catch (RemoteException e2) {
                Log.e(str, "Could not notify menu activity to update actions", e2);
            }
        }
    }

    private boolean isValidActions(ParceledListSlice parceledListSlice) {
        return parceledListSlice != null && parceledListSlice.getList().size() > 0;
    }

    private boolean isStartActivityRequestedElapsed() {
        return SystemClock.uptimeMillis() - this.mStartActivityRequestedTime >= 300;
    }

    /* access modifiers changed from: private */
    public void onMenuStateChanged(int i, boolean z) {
        if (i != this.mMenuState) {
            this.mListeners.forEach(new Consumer(i, z) {
                public final /* synthetic */ int f$0;
                public final /* synthetic */ boolean f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    ((Listener) obj).onPipMenuStateChanged(this.f$0, this.f$1);
                }
            });
            if (i == 2) {
                this.mMediaController.addListener(this.mMediaActionListener);
            } else {
                this.mMediaController.removeListener(this.mMediaActionListener);
            }
        }
        this.mMenuState = i;
    }

    /* access modifiers changed from: private */
    public void setStartActivityRequested(boolean z) {
        this.mHandler.removeCallbacks(this.mStartActivityRequestedTimeoutRunnable);
        this.mStartActivityRequested = z;
        this.mStartActivityRequestedTime = z ? SystemClock.uptimeMillis() : 0;
    }

    /* access modifiers changed from: 0000 */
    public void handlePointerEvent(MotionEvent motionEvent) {
        if (this.mToActivityMessenger != null) {
            Message obtain = Message.obtain();
            obtain.what = 7;
            obtain.obj = motionEvent;
            try {
                this.mToActivityMessenger.send(obtain);
            } catch (RemoteException e) {
                Log.e("PipMenuActController", "Could not dispatch touch event", e);
            }
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("  ");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("PipMenuActController");
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(sb2);
        sb4.append("mMenuState=");
        sb4.append(this.mMenuState);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append(sb2);
        sb5.append("mToActivityMessenger=");
        sb5.append(this.mToActivityMessenger);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append(sb2);
        sb6.append("mListeners=");
        sb6.append(this.mListeners.size());
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(sb2);
        sb7.append("mStartActivityRequested=");
        sb7.append(this.mStartActivityRequested);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(sb2);
        sb8.append("mStartActivityRequestedTime=");
        sb8.append(this.mStartActivityRequestedTime);
        printWriter.println(sb8.toString());
    }
}
