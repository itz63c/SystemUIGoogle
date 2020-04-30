package androidx.leanback.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.leanback.R$layout;

public class VideoSupportFragment extends PlaybackSupportFragment {
    Callback mMediaPlaybackCallback;
    int mState;
    SurfaceView mVideoSurface;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) super.onCreateView(layoutInflater, viewGroup, bundle);
        SurfaceView surfaceView = (SurfaceView) LayoutInflater.from(getContext()).inflate(R$layout.lb_video_surface, viewGroup2, false);
        this.mVideoSurface = surfaceView;
        viewGroup2.addView(surfaceView, 0);
        this.mVideoSurface.getHolder().addCallback(new Callback() {
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Callback callback = VideoSupportFragment.this.mMediaPlaybackCallback;
                if (callback != null) {
                    callback.surfaceCreated(surfaceHolder);
                }
                VideoSupportFragment.this.mState = 1;
            }

            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                Callback callback = VideoSupportFragment.this.mMediaPlaybackCallback;
                if (callback != null) {
                    callback.surfaceChanged(surfaceHolder, i, i2, i3);
                }
            }

            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Callback callback = VideoSupportFragment.this.mMediaPlaybackCallback;
                if (callback != null) {
                    callback.surfaceDestroyed(surfaceHolder);
                }
                VideoSupportFragment.this.mState = 0;
            }
        });
        setBackgroundType(2);
        return viewGroup2;
    }

    public void onDestroyView() {
        this.mVideoSurface = null;
        super.onDestroyView();
    }
}
