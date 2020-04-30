package com.android.systemui.pip.p005tv;

import android.app.PendingIntent.CanceledException;
import android.app.RemoteAction;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon.OnDrawableLoadedListener;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.pip.p005tv.PipManager.MediaListener;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.systemui.pip.tv.PipControlsViewController */
public class PipControlsViewController {
    private static final String TAG = "PipControlsViewController";
    private List<RemoteAction> mCustomActions = new ArrayList();
    private ArrayList<PipControlButtonView> mCustomButtonViews = new ArrayList<>();
    private final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {
        public void onFocusChange(View view, boolean z) {
            if (z) {
                PipControlsViewController.this.mFocusedChild = (PipControlButtonView) view;
            } else if (PipControlsViewController.this.mFocusedChild == view) {
                PipControlsViewController.this.mFocusedChild = null;
            }
        }
    };
    /* access modifiers changed from: private */
    public PipControlButtonView mFocusedChild;
    private final Handler mHandler;
    private final LayoutInflater mLayoutInflater;
    private Listener mListener;
    private MediaController mMediaController;
    private Callback mMediaControllerCallback = new Callback() {
        public void onPlaybackStateChanged(PlaybackState playbackState) {
            PipControlsViewController.this.updateUserActions();
        }
    };
    private OnAttachStateChangeListener mOnAttachStateChangeListener = new OnAttachStateChangeListener() {
        public void onViewAttachedToWindow(View view) {
            PipControlsViewController.this.updateMediaController();
            PipControlsViewController.this.mPipManager.addMediaListener(PipControlsViewController.this.mPipMediaListener);
        }

        public void onViewDetachedFromWindow(View view) {
            PipControlsViewController.this.mPipManager.removeMediaListener(PipControlsViewController.this.mPipMediaListener);
        }
    };
    /* access modifiers changed from: private */
    public final PipManager mPipManager;
    /* access modifiers changed from: private */
    public final MediaListener mPipMediaListener = new MediaListener() {
        public final void onMediaControllerChanged() {
            PipControlsViewController.this.updateMediaController();
        }
    };
    private final PipControlButtonView mPlayPauseButtonView;
    private final PipControlsView mView;

    /* renamed from: com.android.systemui.pip.tv.PipControlsViewController$Listener */
    public interface Listener {
        void onClosed();
    }

    public PipControlsView getView() {
        return this.mView;
    }

    public PipControlsViewController(PipControlsView pipControlsView, PipManager pipManager, LayoutInflater layoutInflater, Handler handler) {
        this.mView = pipControlsView;
        this.mPipManager = pipManager;
        this.mLayoutInflater = layoutInflater;
        this.mHandler = handler;
        pipControlsView.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        if (this.mView.isAttachedToWindow()) {
            this.mOnAttachStateChangeListener.onViewAttachedToWindow(this.mView);
        }
        PipControlButtonView fullButtonView = this.mView.getFullButtonView();
        fullButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
        fullButtonView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                PipControlsViewController.this.lambda$new$0$PipControlsViewController(view);
            }
        });
        PipControlButtonView closeButtonView = this.mView.getCloseButtonView();
        closeButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
        closeButtonView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                PipControlsViewController.this.lambda$new$1$PipControlsViewController(view);
            }
        });
        PipControlButtonView playPauseButtonView = this.mView.getPlayPauseButtonView();
        this.mPlayPauseButtonView = playPauseButtonView;
        playPauseButtonView.setOnFocusChangeListener(this.mFocusChangeListener);
        this.mPlayPauseButtonView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                PipControlsViewController.this.lambda$new$2$PipControlsViewController(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PipControlsViewController(View view) {
        this.mPipManager.movePipToFullscreen();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$PipControlsViewController(View view) {
        this.mPipManager.closePip();
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onClosed();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$PipControlsViewController(View view) {
        MediaController mediaController = this.mMediaController;
        if (mediaController != null && mediaController.getPlaybackState() != null) {
            if (this.mPipManager.getPlaybackState() == 1) {
                this.mMediaController.getTransportControls().play();
            } else if (this.mPipManager.getPlaybackState() == 0) {
                this.mMediaController.getTransportControls().pause();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateMediaController() {
        MediaController mediaController = this.mPipManager.getMediaController();
        MediaController mediaController2 = this.mMediaController;
        if (mediaController2 != mediaController) {
            if (mediaController2 != null) {
                mediaController2.unregisterCallback(this.mMediaControllerCallback);
            }
            this.mMediaController = mediaController;
            if (mediaController != null) {
                mediaController.registerCallback(this.mMediaControllerCallback);
            }
            updateUserActions();
        }
    }

    /* access modifiers changed from: private */
    public void updateUserActions() {
        int i = 0;
        if (!this.mCustomActions.isEmpty()) {
            while (this.mCustomButtonViews.size() < this.mCustomActions.size()) {
                PipControlButtonView pipControlButtonView = (PipControlButtonView) this.mLayoutInflater.inflate(C2013R$layout.tv_pip_custom_control, this.mView, false);
                this.mView.addView(pipControlButtonView);
                this.mCustomButtonViews.add(pipControlButtonView);
            }
            int i2 = 0;
            while (i2 < this.mCustomButtonViews.size()) {
                ((PipControlButtonView) this.mCustomButtonViews.get(i2)).setVisibility(i2 < this.mCustomActions.size() ? 0 : 8);
                i2++;
            }
            while (i < this.mCustomActions.size()) {
                RemoteAction remoteAction = (RemoteAction) this.mCustomActions.get(i);
                PipControlButtonView pipControlButtonView2 = (PipControlButtonView) this.mCustomButtonViews.get(i);
                remoteAction.getIcon().loadDrawableAsync(this.mView.getContext(), new OnDrawableLoadedListener() {
                    public final void onDrawableLoaded(Drawable drawable) {
                        PipControlsViewController.lambda$updateUserActions$3(PipControlButtonView.this, drawable);
                    }
                }, this.mHandler);
                pipControlButtonView2.setText(remoteAction.getContentDescription());
                if (remoteAction.isEnabled()) {
                    pipControlButtonView2.setOnClickListener(new OnClickListener(remoteAction) {
                        public final /* synthetic */ RemoteAction f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onClick(View view) {
                            PipControlsViewController.lambda$updateUserActions$4(this.f$0, view);
                        }
                    });
                }
                pipControlButtonView2.setEnabled(remoteAction.isEnabled());
                pipControlButtonView2.setAlpha(remoteAction.isEnabled() ? 1.0f : 0.54f);
                i++;
            }
            this.mPlayPauseButtonView.setVisibility(8);
            return;
        }
        int playbackState = this.mPipManager.getPlaybackState();
        if (playbackState == 2) {
            this.mPlayPauseButtonView.setVisibility(8);
        } else {
            this.mPlayPauseButtonView.setVisibility(0);
            if (playbackState == 0) {
                this.mPlayPauseButtonView.setImageResource(C2010R$drawable.ic_pause_white);
                this.mPlayPauseButtonView.setText(C2017R$string.pip_pause);
            } else {
                this.mPlayPauseButtonView.setImageResource(C2010R$drawable.ic_play_arrow_white);
                this.mPlayPauseButtonView.setText(C2017R$string.pip_play);
            }
        }
        while (i < this.mCustomButtonViews.size()) {
            ((PipControlButtonView) this.mCustomButtonViews.get(i)).setVisibility(8);
            i++;
        }
    }

    static /* synthetic */ void lambda$updateUserActions$3(PipControlButtonView pipControlButtonView, Drawable drawable) {
        drawable.setTint(-1);
        pipControlButtonView.setImageDrawable(drawable);
    }

    static /* synthetic */ void lambda$updateUserActions$4(RemoteAction remoteAction, View view) {
        try {
            remoteAction.getActionIntent().send();
        } catch (CanceledException e) {
            Log.w(TAG, "Failed to send action", e);
        }
    }

    public void setActions(List<RemoteAction> list) {
        this.mCustomActions.clear();
        this.mCustomActions.addAll(list);
        updateUserActions();
    }
}
