package androidx.mediarouter.app;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.p000v4.media.MediaDescriptionCompat;
import android.support.p000v4.media.MediaMetadataCompat;
import android.support.p000v4.media.session.MediaControllerCompat;
import android.support.p000v4.media.session.MediaControllerCompat.Callback;
import android.support.p000v4.media.session.MediaSessionCompat.Token;
import android.support.p000v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.ObjectsCompat;
import androidx.mediarouter.R$dimen;
import androidx.mediarouter.R$id;
import androidx.mediarouter.R$integer;
import androidx.mediarouter.R$interpolator;
import androidx.mediarouter.R$layout;
import androidx.mediarouter.R$string;
import androidx.mediarouter.app.OverlayListView.OverlayObject;
import androidx.mediarouter.app.OverlayListView.OverlayObject.OnAnimationEndListener;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouter.RouteInfo;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MediaRouteControllerDialog extends AlertDialog {
    static final int CONNECTION_TIMEOUT_MILLIS = ((int) TimeUnit.SECONDS.toMillis(30));
    static final boolean DEBUG = Log.isLoggable("MediaRouteCtrlDialog", 3);
    private Interpolator mAccelerateDecelerateInterpolator;
    final AccessibilityManager mAccessibilityManager;
    int mArtIconBackgroundColor;
    Bitmap mArtIconBitmap;
    boolean mArtIconIsLoaded;
    Bitmap mArtIconLoadedBitmap;
    Uri mArtIconUri;
    private ImageView mArtView;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private ImageButton mCloseButton;
    Context mContext;
    MediaControllerCallback mControllerCallback;
    private boolean mCreated;
    private FrameLayout mCustomControlLayout;
    private View mCustomControlView;
    FrameLayout mDefaultControlLayout;
    MediaDescriptionCompat mDescription;
    private LinearLayout mDialogAreaLayout;
    private int mDialogContentWidth;
    private Button mDisconnectButton;
    private View mDividerView;
    private FrameLayout mExpandableAreaLayout;
    private Interpolator mFastOutSlowInInterpolator;
    FetchArtTask mFetchArtTask;
    private MediaRouteExpandCollapseButton mGroupExpandCollapseButton;
    int mGroupListAnimationDurationMs;
    Runnable mGroupListFadeInAnimation;
    private int mGroupListFadeInDurationMs;
    private int mGroupListFadeOutDurationMs;
    private List<RouteInfo> mGroupMemberRoutes;
    Set<RouteInfo> mGroupMemberRoutesAdded;
    Set<RouteInfo> mGroupMemberRoutesAnimatingWithBitmap;
    private Set<RouteInfo> mGroupMemberRoutesRemoved;
    boolean mHasPendingUpdate;
    private Interpolator mInterpolator;
    boolean mIsGroupExpanded;
    boolean mIsGroupListAnimating;
    boolean mIsGroupListAnimationPending;
    private Interpolator mLinearOutSlowInInterpolator;
    MediaControllerCompat mMediaController;
    private LinearLayout mMediaMainControlLayout;
    boolean mPendingUpdateAnimationNeeded;
    private ImageButton mPlaybackControlButton;
    private RelativeLayout mPlaybackControlLayout;
    final RouteInfo mRoute;
    RouteInfo mRouteInVolumeSliderTouched;
    private TextView mRouteNameTextView;
    final MediaRouter mRouter;
    PlaybackStateCompat mState;
    private Button mStopCastingButton;
    private TextView mSubtitleView;
    private TextView mTitleView;
    VolumeChangeListener mVolumeChangeListener;
    private boolean mVolumeControlEnabled;
    private LinearLayout mVolumeControlLayout;
    VolumeGroupAdapter mVolumeGroupAdapter;
    OverlayListView mVolumeGroupList;
    private int mVolumeGroupListItemHeight;
    private int mVolumeGroupListItemIconSize;
    private int mVolumeGroupListMaxHeight;
    private final int mVolumeGroupListPaddingTop;
    SeekBar mVolumeSlider;
    Map<RouteInfo, SeekBar> mVolumeSliderMap;

    private final class ClickListener implements OnClickListener {
        ClickListener() {
        }

        public void onClick(View view) {
            int id = view.getId();
            int i = 1;
            if (id == 16908313 || id == 16908314) {
                if (MediaRouteControllerDialog.this.mRoute.isSelected()) {
                    MediaRouter mediaRouter = MediaRouteControllerDialog.this.mRouter;
                    if (id == 16908313) {
                        i = 2;
                    }
                    mediaRouter.unselect(i);
                }
                MediaRouteControllerDialog.this.dismiss();
            } else if (id == R$id.mr_control_playback_ctrl) {
                MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
                if (mediaRouteControllerDialog.mMediaController != null) {
                    PlaybackStateCompat playbackStateCompat = mediaRouteControllerDialog.mState;
                    if (playbackStateCompat != null) {
                        int i2 = 0;
                        if (playbackStateCompat.getState() != 3) {
                            i = 0;
                        }
                        if (i != 0 && MediaRouteControllerDialog.this.isPauseActionSupported()) {
                            MediaRouteControllerDialog.this.mMediaController.getTransportControls().pause();
                            i2 = R$string.mr_controller_pause;
                        } else if (i != 0 && MediaRouteControllerDialog.this.isStopActionSupported()) {
                            MediaRouteControllerDialog.this.mMediaController.getTransportControls().stop();
                            i2 = R$string.mr_controller_stop;
                        } else if (i == 0 && MediaRouteControllerDialog.this.isPlayActionSupported()) {
                            MediaRouteControllerDialog.this.mMediaController.getTransportControls().play();
                            i2 = R$string.mr_controller_play;
                        }
                        AccessibilityManager accessibilityManager = MediaRouteControllerDialog.this.mAccessibilityManager;
                        if (accessibilityManager != null && accessibilityManager.isEnabled() && i2 != 0) {
                            AccessibilityEvent obtain = AccessibilityEvent.obtain(16384);
                            obtain.setPackageName(MediaRouteControllerDialog.this.mContext.getPackageName());
                            obtain.setClassName(ClickListener.class.getName());
                            obtain.getText().add(MediaRouteControllerDialog.this.mContext.getString(i2));
                            MediaRouteControllerDialog.this.mAccessibilityManager.sendAccessibilityEvent(obtain);
                        }
                    }
                }
            } else if (id == R$id.mr_close) {
                MediaRouteControllerDialog.this.dismiss();
            }
        }
    }

    private class FetchArtTask extends AsyncTask<Void, Void, Bitmap> {
        private int mBackgroundColor;
        private final Bitmap mIconBitmap;
        private final Uri mIconUri;
        private long mStartTimeMillis;

        FetchArtTask() {
            MediaDescriptionCompat mediaDescriptionCompat = MediaRouteControllerDialog.this.mDescription;
            Uri uri = null;
            Bitmap iconBitmap = mediaDescriptionCompat == null ? null : mediaDescriptionCompat.getIconBitmap();
            if (MediaRouteControllerDialog.isBitmapRecycled(iconBitmap)) {
                Log.w("MediaRouteCtrlDialog", "Can't fetch the given art bitmap because it's already recycled.");
                iconBitmap = null;
            }
            this.mIconBitmap = iconBitmap;
            MediaDescriptionCompat mediaDescriptionCompat2 = MediaRouteControllerDialog.this.mDescription;
            if (mediaDescriptionCompat2 != null) {
                uri = mediaDescriptionCompat2.getIconUri();
            }
            this.mIconUri = uri;
        }

        public Bitmap getIconBitmap() {
            return this.mIconBitmap;
        }

        public Uri getIconUri() {
            return this.mIconUri;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.mStartTimeMillis = SystemClock.uptimeMillis();
            MediaRouteControllerDialog.this.clearLoadedBitmap();
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0048 */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0053 A[Catch:{ IOException -> 0x00a3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x00bf A[SYNTHETIC, Splitter:B:57:0x00bf] */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x00c7 A[SYNTHETIC, Splitter:B:62:0x00c7] */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x00d2  */
        /* JADX WARNING: Removed duplicated region for block: B:72:0x00e7 A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.graphics.Bitmap doInBackground(java.lang.Void... r10) {
            /*
                r9 = this;
                java.lang.String r10 = "Unable to open: "
                android.graphics.Bitmap r0 = r9.mIconBitmap
                r1 = 0
                r2 = 1
                java.lang.String r3 = "MediaRouteCtrlDialog"
                r4 = 0
                if (r0 == 0) goto L_0x000d
                goto L_0x00cc
            L_0x000d:
                android.net.Uri r0 = r9.mIconUri
                if (r0 == 0) goto L_0x00cb
                java.io.InputStream r0 = r9.openInputStreamByScheme(r0)     // Catch:{ IOException -> 0x00a7, all -> 0x00a5 }
                if (r0 != 0) goto L_0x0031
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00a3 }
                r5.<init>()     // Catch:{ IOException -> 0x00a3 }
                r5.append(r10)     // Catch:{ IOException -> 0x00a3 }
                android.net.Uri r6 = r9.mIconUri     // Catch:{ IOException -> 0x00a3 }
                r5.append(r6)     // Catch:{ IOException -> 0x00a3 }
                java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00a3 }
                android.util.Log.w(r3, r5)     // Catch:{ IOException -> 0x00a3 }
                if (r0 == 0) goto L_0x0030
                r0.close()     // Catch:{ IOException -> 0x0030 }
            L_0x0030:
                return r4
            L_0x0031:
                android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ IOException -> 0x00a3 }
                r5.<init>()     // Catch:{ IOException -> 0x00a3 }
                r5.inJustDecodeBounds = r2     // Catch:{ IOException -> 0x00a3 }
                android.graphics.BitmapFactory.decodeStream(r0, r4, r5)     // Catch:{ IOException -> 0x00a3 }
                int r6 = r5.outWidth     // Catch:{ IOException -> 0x00a3 }
                if (r6 == 0) goto L_0x009d
                int r6 = r5.outHeight     // Catch:{ IOException -> 0x00a3 }
                if (r6 != 0) goto L_0x0044
                goto L_0x009d
            L_0x0044:
                r0.reset()     // Catch:{ IOException -> 0x0048 }
                goto L_0x006d
            L_0x0048:
                r0.close()     // Catch:{ IOException -> 0x00a3 }
                android.net.Uri r6 = r9.mIconUri     // Catch:{ IOException -> 0x00a3 }
                java.io.InputStream r0 = r9.openInputStreamByScheme(r6)     // Catch:{ IOException -> 0x00a3 }
                if (r0 != 0) goto L_0x006d
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00a3 }
                r5.<init>()     // Catch:{ IOException -> 0x00a3 }
                r5.append(r10)     // Catch:{ IOException -> 0x00a3 }
                android.net.Uri r6 = r9.mIconUri     // Catch:{ IOException -> 0x00a3 }
                r5.append(r6)     // Catch:{ IOException -> 0x00a3 }
                java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00a3 }
                android.util.Log.w(r3, r5)     // Catch:{ IOException -> 0x00a3 }
                if (r0 == 0) goto L_0x006c
                r0.close()     // Catch:{ IOException -> 0x006c }
            L_0x006c:
                return r4
            L_0x006d:
                r5.inJustDecodeBounds = r1     // Catch:{ IOException -> 0x00a3 }
                androidx.mediarouter.app.MediaRouteControllerDialog r6 = androidx.mediarouter.app.MediaRouteControllerDialog.this     // Catch:{ IOException -> 0x00a3 }
                int r7 = r5.outWidth     // Catch:{ IOException -> 0x00a3 }
                int r8 = r5.outHeight     // Catch:{ IOException -> 0x00a3 }
                int r6 = r6.getDesiredArtHeight(r7, r8)     // Catch:{ IOException -> 0x00a3 }
                int r7 = r5.outHeight     // Catch:{ IOException -> 0x00a3 }
                int r7 = r7 / r6
                int r6 = java.lang.Integer.highestOneBit(r7)     // Catch:{ IOException -> 0x00a3 }
                int r6 = java.lang.Math.max(r2, r6)     // Catch:{ IOException -> 0x00a3 }
                r5.inSampleSize = r6     // Catch:{ IOException -> 0x00a3 }
                boolean r6 = r9.isCancelled()     // Catch:{ IOException -> 0x00a3 }
                if (r6 == 0) goto L_0x0092
                if (r0 == 0) goto L_0x0091
                r0.close()     // Catch:{ IOException -> 0x0091 }
            L_0x0091:
                return r4
            L_0x0092:
                android.graphics.Bitmap r10 = android.graphics.BitmapFactory.decodeStream(r0, r4, r5)     // Catch:{ IOException -> 0x00a3 }
                if (r0 == 0) goto L_0x009b
                r0.close()     // Catch:{ IOException -> 0x009b }
            L_0x009b:
                r0 = r10
                goto L_0x00cc
            L_0x009d:
                if (r0 == 0) goto L_0x00a2
                r0.close()     // Catch:{ IOException -> 0x00a2 }
            L_0x00a2:
                return r4
            L_0x00a3:
                r5 = move-exception
                goto L_0x00a9
            L_0x00a5:
                r9 = move-exception
                goto L_0x00c5
            L_0x00a7:
                r5 = move-exception
                r0 = r4
            L_0x00a9:
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c3 }
                r6.<init>()     // Catch:{ all -> 0x00c3 }
                r6.append(r10)     // Catch:{ all -> 0x00c3 }
                android.net.Uri r10 = r9.mIconUri     // Catch:{ all -> 0x00c3 }
                r6.append(r10)     // Catch:{ all -> 0x00c3 }
                java.lang.String r10 = r6.toString()     // Catch:{ all -> 0x00c3 }
                android.util.Log.w(r3, r10, r5)     // Catch:{ all -> 0x00c3 }
                if (r0 == 0) goto L_0x00cb
                r0.close()     // Catch:{ IOException -> 0x00cb }
                goto L_0x00cb
            L_0x00c3:
                r9 = move-exception
                r4 = r0
            L_0x00c5:
                if (r4 == 0) goto L_0x00ca
                r4.close()     // Catch:{ IOException -> 0x00ca }
            L_0x00ca:
                throw r9
            L_0x00cb:
                r0 = r4
            L_0x00cc:
                boolean r10 = androidx.mediarouter.app.MediaRouteControllerDialog.isBitmapRecycled(r0)
                if (r10 == 0) goto L_0x00e7
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "Can't use recycled bitmap: "
                r9.append(r10)
                r9.append(r0)
                java.lang.String r9 = r9.toString()
                android.util.Log.w(r3, r9)
                return r4
            L_0x00e7:
                if (r0 == 0) goto L_0x011a
                int r10 = r0.getWidth()
                int r3 = r0.getHeight()
                if (r10 >= r3) goto L_0x011a
                androidx.palette.graphics.Palette$Builder r10 = new androidx.palette.graphics.Palette$Builder
                r10.<init>(r0)
                r10.maximumColorCount(r2)
                androidx.palette.graphics.Palette r10 = r10.generate()
                java.util.List r2 = r10.getSwatches()
                boolean r2 = r2.isEmpty()
                if (r2 == 0) goto L_0x010a
                goto L_0x0118
            L_0x010a:
                java.util.List r10 = r10.getSwatches()
                java.lang.Object r10 = r10.get(r1)
                androidx.palette.graphics.Palette$Swatch r10 = (androidx.palette.graphics.Palette.Swatch) r10
                int r1 = r10.getRgb()
            L_0x0118:
                r9.mBackgroundColor = r1
            L_0x011a:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.mediarouter.app.MediaRouteControllerDialog.FetchArtTask.doInBackground(java.lang.Void[]):android.graphics.Bitmap");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bitmap bitmap) {
            MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            mediaRouteControllerDialog.mFetchArtTask = null;
            if (!ObjectsCompat.equals(mediaRouteControllerDialog.mArtIconBitmap, this.mIconBitmap) || !ObjectsCompat.equals(MediaRouteControllerDialog.this.mArtIconUri, this.mIconUri)) {
                MediaRouteControllerDialog mediaRouteControllerDialog2 = MediaRouteControllerDialog.this;
                mediaRouteControllerDialog2.mArtIconBitmap = this.mIconBitmap;
                mediaRouteControllerDialog2.mArtIconLoadedBitmap = bitmap;
                mediaRouteControllerDialog2.mArtIconUri = this.mIconUri;
                mediaRouteControllerDialog2.mArtIconBackgroundColor = this.mBackgroundColor;
                boolean z = true;
                mediaRouteControllerDialog2.mArtIconIsLoaded = true;
                long uptimeMillis = SystemClock.uptimeMillis() - this.mStartTimeMillis;
                MediaRouteControllerDialog mediaRouteControllerDialog3 = MediaRouteControllerDialog.this;
                if (uptimeMillis <= 120) {
                    z = false;
                }
                mediaRouteControllerDialog3.update(z);
            }
        }

        private InputStream openInputStreamByScheme(Uri uri) throws IOException {
            InputStream inputStream;
            String lowerCase = uri.getScheme().toLowerCase();
            if ("android.resource".equals(lowerCase) || "content".equals(lowerCase) || "file".equals(lowerCase)) {
                inputStream = MediaRouteControllerDialog.this.mContext.getContentResolver().openInputStream(uri);
            } else {
                URLConnection openConnection = new URL(uri.toString()).openConnection();
                openConnection.setConnectTimeout(MediaRouteControllerDialog.CONNECTION_TIMEOUT_MILLIS);
                openConnection.setReadTimeout(MediaRouteControllerDialog.CONNECTION_TIMEOUT_MILLIS);
                inputStream = openConnection.getInputStream();
            }
            if (inputStream == null) {
                return null;
            }
            return new BufferedInputStream(inputStream);
        }
    }

    private final class MediaControllerCallback extends Callback {
        MediaControllerCallback() {
        }

        public void onSessionDestroyed() {
            MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            MediaControllerCompat mediaControllerCompat = mediaRouteControllerDialog.mMediaController;
            if (mediaControllerCompat != null) {
                mediaControllerCompat.unregisterCallback(mediaRouteControllerDialog.mControllerCallback);
                MediaRouteControllerDialog.this.mMediaController = null;
            }
        }

        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            mediaRouteControllerDialog.mState = playbackStateCompat;
            mediaRouteControllerDialog.update(false);
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            MediaRouteControllerDialog.this.mDescription = mediaMetadataCompat == null ? null : mediaMetadataCompat.getDescription();
            MediaRouteControllerDialog.this.updateArtIconIfNeeded();
            MediaRouteControllerDialog.this.update(false);
        }
    }

    private final class MediaRouterCallback extends MediaRouter.Callback {
        MediaRouterCallback() {
        }

        public void onRouteUnselected(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteControllerDialog.this.update(false);
        }

        public void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteControllerDialog.this.update(true);
        }

        public void onRouteVolumeChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            SeekBar seekBar = (SeekBar) MediaRouteControllerDialog.this.mVolumeSliderMap.get(routeInfo);
            int volume = routeInfo.getVolume();
            if (MediaRouteControllerDialog.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteVolumeChanged(), route.getVolume:");
                sb.append(volume);
                Log.d("MediaRouteCtrlDialog", sb.toString());
            }
            if (seekBar != null && MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched != routeInfo) {
                seekBar.setProgress(volume);
            }
        }
    }

    private class VolumeChangeListener implements OnSeekBarChangeListener {
        private final Runnable mStopTrackingTouch = new Runnable() {
            public void run() {
                MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
                if (mediaRouteControllerDialog.mRouteInVolumeSliderTouched != null) {
                    mediaRouteControllerDialog.mRouteInVolumeSliderTouched = null;
                    if (mediaRouteControllerDialog.mHasPendingUpdate) {
                        mediaRouteControllerDialog.update(mediaRouteControllerDialog.mPendingUpdateAnimationNeeded);
                    }
                }
            }
        };

        VolumeChangeListener() {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
            if (mediaRouteControllerDialog.mRouteInVolumeSliderTouched != null) {
                mediaRouteControllerDialog.mVolumeSlider.removeCallbacks(this.mStopTrackingTouch);
            }
            MediaRouteControllerDialog.this.mRouteInVolumeSliderTouched = (RouteInfo) seekBar.getTag();
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            MediaRouteControllerDialog.this.mVolumeSlider.postDelayed(this.mStopTrackingTouch, 500);
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                RouteInfo routeInfo = (RouteInfo) seekBar.getTag();
                if (MediaRouteControllerDialog.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("onProgressChanged(): calling MediaRouter.RouteInfo.requestSetVolume(");
                    sb.append(i);
                    sb.append(")");
                    Log.d("MediaRouteCtrlDialog", sb.toString());
                }
                routeInfo.requestSetVolume(i);
            }
        }
    }

    private class VolumeGroupAdapter extends ArrayAdapter<RouteInfo> {
        final float mDisabledAlpha;

        public boolean isEnabled(int i) {
            return false;
        }

        public VolumeGroupAdapter(Context context, List<RouteInfo> list) {
            super(context, 0, list);
            this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(context);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int i2;
            int i3 = 0;
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.mr_controller_volume_item, viewGroup, false);
            } else {
                MediaRouteControllerDialog.this.updateVolumeGroupItemHeight(view);
            }
            RouteInfo routeInfo = (RouteInfo) getItem(i);
            if (routeInfo != null) {
                boolean isEnabled = routeInfo.isEnabled();
                TextView textView = (TextView) view.findViewById(R$id.mr_name);
                textView.setEnabled(isEnabled);
                textView.setText(routeInfo.getName());
                MediaRouteVolumeSlider mediaRouteVolumeSlider = (MediaRouteVolumeSlider) view.findViewById(R$id.mr_volume_slider);
                MediaRouterThemeHelper.setVolumeSliderColor(viewGroup.getContext(), mediaRouteVolumeSlider, MediaRouteControllerDialog.this.mVolumeGroupList);
                mediaRouteVolumeSlider.setTag(routeInfo);
                MediaRouteControllerDialog.this.mVolumeSliderMap.put(routeInfo, mediaRouteVolumeSlider);
                mediaRouteVolumeSlider.setHideThumb(!isEnabled);
                mediaRouteVolumeSlider.setEnabled(isEnabled);
                if (isEnabled) {
                    if (MediaRouteControllerDialog.this.isVolumeControlAvailable(routeInfo)) {
                        mediaRouteVolumeSlider.setMax(routeInfo.getVolumeMax());
                        mediaRouteVolumeSlider.setProgress(routeInfo.getVolume());
                        mediaRouteVolumeSlider.setOnSeekBarChangeListener(MediaRouteControllerDialog.this.mVolumeChangeListener);
                    } else {
                        mediaRouteVolumeSlider.setMax(100);
                        mediaRouteVolumeSlider.setProgress(100);
                        mediaRouteVolumeSlider.setEnabled(false);
                    }
                }
                ImageView imageView = (ImageView) view.findViewById(R$id.mr_volume_item_icon);
                if (isEnabled) {
                    i2 = 255;
                } else {
                    i2 = (int) (this.mDisabledAlpha * 255.0f);
                }
                imageView.setAlpha(i2);
                LinearLayout linearLayout = (LinearLayout) view.findViewById(R$id.volume_item_container);
                if (MediaRouteControllerDialog.this.mGroupMemberRoutesAnimatingWithBitmap.contains(routeInfo)) {
                    i3 = 4;
                }
                linearLayout.setVisibility(i3);
                Set<RouteInfo> set = MediaRouteControllerDialog.this.mGroupMemberRoutesAdded;
                if (set != null && set.contains(routeInfo)) {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 0.0f);
                    alphaAnimation.setDuration(0);
                    alphaAnimation.setFillEnabled(true);
                    alphaAnimation.setFillAfter(true);
                    view.clearAnimation();
                    view.startAnimation(alphaAnimation);
                }
            }
            return view;
        }
    }

    public View onCreateMediaControlView(Bundle bundle) {
        return null;
    }

    public MediaRouteControllerDialog(Context context) {
        this(context, 0);
    }

    public MediaRouteControllerDialog(Context context, int i) {
        Context createThemedDialogContext = MediaRouterThemeHelper.createThemedDialogContext(context, i, true);
        super(createThemedDialogContext, MediaRouterThemeHelper.createThemedDialogStyle(createThemedDialogContext));
        this.mVolumeControlEnabled = true;
        this.mGroupListFadeInAnimation = new Runnable() {
            public void run() {
                MediaRouteControllerDialog.this.startGroupListFadeInAnimation();
            }
        };
        this.mContext = getContext();
        this.mControllerCallback = new MediaControllerCallback();
        this.mRouter = MediaRouter.getInstance(this.mContext);
        this.mCallback = new MediaRouterCallback();
        this.mRoute = this.mRouter.getSelectedRoute();
        setMediaSession(this.mRouter.getMediaSessionToken());
        this.mVolumeGroupListPaddingTop = this.mContext.getResources().getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_padding_top);
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        if (VERSION.SDK_INT >= 21) {
            this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(createThemedDialogContext, R$interpolator.mr_linear_out_slow_in);
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(createThemedDialogContext, R$interpolator.mr_fast_out_slow_in);
        }
        this.mAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    }

    private void setMediaSession(Token token) {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        MediaDescriptionCompat mediaDescriptionCompat = null;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(this.mControllerCallback);
            this.mMediaController = null;
        }
        if (token != null && this.mAttachedToWindow) {
            MediaControllerCompat mediaControllerCompat2 = new MediaControllerCompat(this.mContext, token);
            this.mMediaController = mediaControllerCompat2;
            mediaControllerCompat2.registerCallback(this.mControllerCallback);
            MediaMetadataCompat metadata = this.mMediaController.getMetadata();
            if (metadata != null) {
                mediaDescriptionCompat = metadata.getDescription();
            }
            this.mDescription = mediaDescriptionCompat;
            this.mState = this.mMediaController.getPlaybackState();
            updateArtIconIfNeeded();
            update(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setBackgroundDrawableResource(17170445);
        setContentView(R$layout.mr_controller_material_dialog_b);
        findViewById(16908315).setVisibility(8);
        ClickListener clickListener = new ClickListener();
        FrameLayout frameLayout = (FrameLayout) findViewById(R$id.mr_expandable_area);
        this.mExpandableAreaLayout = frameLayout;
        frameLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MediaRouteControllerDialog.this.dismiss();
            }
        });
        LinearLayout linearLayout = (LinearLayout) findViewById(R$id.mr_dialog_area);
        this.mDialogAreaLayout = linearLayout;
        linearLayout.setOnClickListener(new OnClickListener(this) {
            public void onClick(View view) {
            }
        });
        int buttonTextColor = MediaRouterThemeHelper.getButtonTextColor(this.mContext);
        Button button = (Button) findViewById(16908314);
        this.mDisconnectButton = button;
        button.setText(R$string.mr_controller_disconnect);
        this.mDisconnectButton.setTextColor(buttonTextColor);
        this.mDisconnectButton.setOnClickListener(clickListener);
        Button button2 = (Button) findViewById(16908313);
        this.mStopCastingButton = button2;
        button2.setText(R$string.mr_controller_stop_casting);
        this.mStopCastingButton.setTextColor(buttonTextColor);
        this.mStopCastingButton.setOnClickListener(clickListener);
        this.mRouteNameTextView = (TextView) findViewById(R$id.mr_name);
        ImageButton imageButton = (ImageButton) findViewById(R$id.mr_close);
        this.mCloseButton = imageButton;
        imageButton.setOnClickListener(clickListener);
        this.mCustomControlLayout = (FrameLayout) findViewById(R$id.mr_custom_control);
        this.mDefaultControlLayout = (FrameLayout) findViewById(R$id.mr_default_control);
        C03534 r2 = new OnClickListener() {
            public void onClick(View view) {
                MediaControllerCompat mediaControllerCompat = MediaRouteControllerDialog.this.mMediaController;
                if (mediaControllerCompat != null) {
                    PendingIntent sessionActivity = mediaControllerCompat.getSessionActivity();
                    if (sessionActivity != null) {
                        try {
                            sessionActivity.send();
                            MediaRouteControllerDialog.this.dismiss();
                        } catch (CanceledException unused) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(sessionActivity);
                            sb.append(" was not sent, it had been canceled.");
                            Log.e("MediaRouteCtrlDialog", sb.toString());
                        }
                    }
                }
            }
        };
        ImageView imageView = (ImageView) findViewById(R$id.mr_art);
        this.mArtView = imageView;
        imageView.setOnClickListener(r2);
        findViewById(R$id.mr_control_title_container).setOnClickListener(r2);
        this.mMediaMainControlLayout = (LinearLayout) findViewById(R$id.mr_media_main_control);
        this.mDividerView = findViewById(R$id.mr_control_divider);
        this.mPlaybackControlLayout = (RelativeLayout) findViewById(R$id.mr_playback_control);
        this.mTitleView = (TextView) findViewById(R$id.mr_control_title);
        this.mSubtitleView = (TextView) findViewById(R$id.mr_control_subtitle);
        ImageButton imageButton2 = (ImageButton) findViewById(R$id.mr_control_playback_ctrl);
        this.mPlaybackControlButton = imageButton2;
        imageButton2.setOnClickListener(clickListener);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R$id.mr_volume_control);
        this.mVolumeControlLayout = linearLayout2;
        linearLayout2.setVisibility(8);
        SeekBar seekBar = (SeekBar) findViewById(R$id.mr_volume_slider);
        this.mVolumeSlider = seekBar;
        seekBar.setTag(this.mRoute);
        VolumeChangeListener volumeChangeListener = new VolumeChangeListener();
        this.mVolumeChangeListener = volumeChangeListener;
        this.mVolumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
        this.mVolumeGroupList = (OverlayListView) findViewById(R$id.mr_volume_group_list);
        this.mGroupMemberRoutes = new ArrayList();
        VolumeGroupAdapter volumeGroupAdapter = new VolumeGroupAdapter(this.mVolumeGroupList.getContext(), this.mGroupMemberRoutes);
        this.mVolumeGroupAdapter = volumeGroupAdapter;
        this.mVolumeGroupList.setAdapter(volumeGroupAdapter);
        this.mGroupMemberRoutesAnimatingWithBitmap = new HashSet();
        MediaRouterThemeHelper.setMediaControlsBackgroundColor(this.mContext, this.mMediaMainControlLayout, this.mVolumeGroupList, this.mRoute.isGroup());
        MediaRouterThemeHelper.setVolumeSliderColor(this.mContext, (MediaRouteVolumeSlider) this.mVolumeSlider, this.mMediaMainControlLayout);
        HashMap hashMap = new HashMap();
        this.mVolumeSliderMap = hashMap;
        hashMap.put(this.mRoute, this.mVolumeSlider);
        MediaRouteExpandCollapseButton mediaRouteExpandCollapseButton = (MediaRouteExpandCollapseButton) findViewById(R$id.mr_group_expand_collapse);
        this.mGroupExpandCollapseButton = mediaRouteExpandCollapseButton;
        mediaRouteExpandCollapseButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
                boolean z = !mediaRouteControllerDialog.mIsGroupExpanded;
                mediaRouteControllerDialog.mIsGroupExpanded = z;
                if (z) {
                    mediaRouteControllerDialog.mVolumeGroupList.setVisibility(0);
                }
                MediaRouteControllerDialog.this.loadInterpolator();
                MediaRouteControllerDialog.this.updateLayoutHeight(true);
            }
        });
        loadInterpolator();
        this.mGroupListAnimationDurationMs = this.mContext.getResources().getInteger(R$integer.mr_controller_volume_group_list_animation_duration_ms);
        this.mGroupListFadeInDurationMs = this.mContext.getResources().getInteger(R$integer.mr_controller_volume_group_list_fade_in_duration_ms);
        this.mGroupListFadeOutDurationMs = this.mContext.getResources().getInteger(R$integer.mr_controller_volume_group_list_fade_out_duration_ms);
        View onCreateMediaControlView = onCreateMediaControlView(bundle);
        this.mCustomControlView = onCreateMediaControlView;
        if (onCreateMediaControlView != null) {
            this.mCustomControlLayout.addView(onCreateMediaControlView);
            this.mCustomControlLayout.setVisibility(0);
        }
        this.mCreated = true;
        updateLayout();
    }

    /* access modifiers changed from: 0000 */
    public void updateLayout() {
        int dialogWidth = MediaRouteDialogHelper.getDialogWidth(this.mContext);
        getWindow().setLayout(dialogWidth, -2);
        View decorView = getWindow().getDecorView();
        this.mDialogContentWidth = (dialogWidth - decorView.getPaddingLeft()) - decorView.getPaddingRight();
        Resources resources = this.mContext.getResources();
        this.mVolumeGroupListItemIconSize = resources.getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_item_icon_size);
        this.mVolumeGroupListItemHeight = resources.getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_item_height);
        this.mVolumeGroupListMaxHeight = resources.getDimensionPixelSize(R$dimen.mr_controller_volume_group_list_max_height);
        this.mArtIconBitmap = null;
        this.mArtIconUri = null;
        updateArtIconIfNeeded();
        update(false);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(MediaRouteSelector.EMPTY, this.mCallback, 2);
        setMediaSession(this.mRouter.getMediaSessionToken());
    }

    public void onDetachedFromWindow() {
        this.mRouter.removeCallback(this.mCallback);
        setMediaSession(null);
        this.mAttachedToWindow = false;
        super.onDetachedFromWindow();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 25 && i != 24) {
            return super.onKeyDown(i, keyEvent);
        }
        this.mRoute.requestUpdateVolume(i == 25 ? -1 : 1);
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 25 || i == 24) {
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    /* access modifiers changed from: 0000 */
    public void update(boolean z) {
        if (this.mRouteInVolumeSliderTouched != null) {
            this.mHasPendingUpdate = true;
            this.mPendingUpdateAnimationNeeded = z | this.mPendingUpdateAnimationNeeded;
            return;
        }
        int i = 0;
        this.mHasPendingUpdate = false;
        this.mPendingUpdateAnimationNeeded = false;
        if (!this.mRoute.isSelected() || this.mRoute.isDefaultOrBluetooth()) {
            dismiss();
        } else if (this.mCreated) {
            this.mRouteNameTextView.setText(this.mRoute.getName());
            Button button = this.mDisconnectButton;
            if (!this.mRoute.canDisconnect()) {
                i = 8;
            }
            button.setVisibility(i);
            if (this.mCustomControlView == null && this.mArtIconIsLoaded) {
                if (isBitmapRecycled(this.mArtIconLoadedBitmap)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Can't set artwork image with recycled bitmap: ");
                    sb.append(this.mArtIconLoadedBitmap);
                    Log.w("MediaRouteCtrlDialog", sb.toString());
                } else {
                    this.mArtView.setImageBitmap(this.mArtIconLoadedBitmap);
                    this.mArtView.setBackgroundColor(this.mArtIconBackgroundColor);
                }
                clearLoadedBitmap();
            }
            updateVolumeControlLayout();
            updatePlaybackControlLayout();
            updateLayoutHeight(z);
        }
    }

    static boolean isBitmapRecycled(Bitmap bitmap) {
        return bitmap != null && bitmap.isRecycled();
    }

    private boolean canShowPlaybackControlLayout() {
        return this.mCustomControlView == null && !(this.mDescription == null && this.mState == null);
    }

    private int getMainControllerHeight(boolean z) {
        if (!z && this.mVolumeControlLayout.getVisibility() != 0) {
            return 0;
        }
        int paddingTop = 0 + this.mMediaMainControlLayout.getPaddingTop() + this.mMediaMainControlLayout.getPaddingBottom();
        if (z) {
            paddingTop += this.mPlaybackControlLayout.getMeasuredHeight();
        }
        if (this.mVolumeControlLayout.getVisibility() == 0) {
            paddingTop += this.mVolumeControlLayout.getMeasuredHeight();
        }
        return (!z || this.mVolumeControlLayout.getVisibility() != 0) ? paddingTop : paddingTop + this.mDividerView.getMeasuredHeight();
    }

    private void updateMediaControlVisibility(boolean z) {
        int i = 0;
        this.mDividerView.setVisibility((this.mVolumeControlLayout.getVisibility() != 0 || !z) ? 8 : 0);
        LinearLayout linearLayout = this.mMediaMainControlLayout;
        if (this.mVolumeControlLayout.getVisibility() == 8 && !z) {
            i = 8;
        }
        linearLayout.setVisibility(i);
    }

    /* access modifiers changed from: 0000 */
    public void updateLayoutHeight(final boolean z) {
        this.mDefaultControlLayout.requestLayout();
        this.mDefaultControlLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mDefaultControlLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
                if (mediaRouteControllerDialog.mIsGroupListAnimating) {
                    mediaRouteControllerDialog.mIsGroupListAnimationPending = true;
                } else {
                    mediaRouteControllerDialog.updateLayoutHeightInternal(z);
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0154  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateLayoutHeightInternal(boolean r10) {
        /*
            r9 = this;
            android.widget.LinearLayout r0 = r9.mMediaMainControlLayout
            int r0 = getLayoutHeight(r0)
            android.widget.LinearLayout r1 = r9.mMediaMainControlLayout
            r2 = -1
            setLayoutHeight(r1, r2)
            boolean r1 = r9.canShowPlaybackControlLayout()
            r9.updateMediaControlVisibility(r1)
            android.view.Window r1 = r9.getWindow()
            android.view.View r1 = r1.getDecorView()
            android.view.Window r2 = r9.getWindow()
            android.view.WindowManager$LayoutParams r2 = r2.getAttributes()
            int r2 = r2.width
            r3 = 1073741824(0x40000000, float:2.0)
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r3)
            r3 = 0
            r1.measure(r2, r3)
            android.widget.LinearLayout r2 = r9.mMediaMainControlLayout
            setLayoutHeight(r2, r0)
            android.view.View r0 = r9.mCustomControlView
            if (r0 != 0) goto L_0x0071
            android.widget.ImageView r0 = r9.mArtView
            android.graphics.drawable.Drawable r0 = r0.getDrawable()
            boolean r0 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r0 == 0) goto L_0x0071
            android.widget.ImageView r0 = r9.mArtView
            android.graphics.drawable.Drawable r0 = r0.getDrawable()
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            if (r0 == 0) goto L_0x0071
            int r2 = r0.getWidth()
            int r4 = r0.getHeight()
            int r2 = r9.getDesiredArtHeight(r2, r4)
            android.widget.ImageView r4 = r9.mArtView
            int r5 = r0.getWidth()
            int r0 = r0.getHeight()
            if (r5 < r0) goto L_0x006b
            android.widget.ImageView$ScaleType r0 = android.widget.ImageView.ScaleType.FIT_XY
            goto L_0x006d
        L_0x006b:
            android.widget.ImageView$ScaleType r0 = android.widget.ImageView.ScaleType.FIT_CENTER
        L_0x006d:
            r4.setScaleType(r0)
            goto L_0x0072
        L_0x0071:
            r2 = r3
        L_0x0072:
            boolean r0 = r9.canShowPlaybackControlLayout()
            int r0 = r9.getMainControllerHeight(r0)
            java.util.List<androidx.mediarouter.media.MediaRouter$RouteInfo> r4 = r9.mGroupMemberRoutes
            int r4 = r4.size()
            androidx.mediarouter.media.MediaRouter$RouteInfo r5 = r9.mRoute
            boolean r5 = r5.isGroup()
            if (r5 == 0) goto L_0x0096
            int r5 = r9.mVolumeGroupListItemHeight
            androidx.mediarouter.media.MediaRouter$RouteInfo r6 = r9.mRoute
            java.util.List r6 = r6.getMemberRoutes()
            int r6 = r6.size()
            int r5 = r5 * r6
            goto L_0x0097
        L_0x0096:
            r5 = r3
        L_0x0097:
            if (r4 <= 0) goto L_0x009c
            int r4 = r9.mVolumeGroupListPaddingTop
            int r5 = r5 + r4
        L_0x009c:
            int r4 = r9.mVolumeGroupListMaxHeight
            int r4 = java.lang.Math.min(r5, r4)
            boolean r5 = r9.mIsGroupExpanded
            if (r5 == 0) goto L_0x00a7
            goto L_0x00a8
        L_0x00a7:
            r4 = r3
        L_0x00a8:
            int r5 = java.lang.Math.max(r2, r4)
            int r5 = r5 + r0
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r1.getWindowVisibleDisplayFrame(r6)
            android.widget.LinearLayout r1 = r9.mDialogAreaLayout
            int r1 = r1.getMeasuredHeight()
            android.widget.FrameLayout r7 = r9.mDefaultControlLayout
            int r7 = r7.getMeasuredHeight()
            int r1 = r1 - r7
            int r7 = r6.height()
            int r7 = r7 - r1
            android.view.View r1 = r9.mCustomControlView
            r8 = 8
            if (r1 != 0) goto L_0x00dc
            if (r2 <= 0) goto L_0x00dc
            if (r5 > r7) goto L_0x00dc
            android.widget.ImageView r0 = r9.mArtView
            r0.setVisibility(r3)
            android.widget.ImageView r0 = r9.mArtView
            setLayoutHeight(r0, r2)
            goto L_0x00f9
        L_0x00dc:
            androidx.mediarouter.app.OverlayListView r1 = r9.mVolumeGroupList
            int r1 = getLayoutHeight(r1)
            android.widget.LinearLayout r2 = r9.mMediaMainControlLayout
            int r2 = r2.getMeasuredHeight()
            int r1 = r1 + r2
            android.widget.FrameLayout r2 = r9.mDefaultControlLayout
            int r2 = r2.getMeasuredHeight()
            if (r1 < r2) goto L_0x00f6
            android.widget.ImageView r1 = r9.mArtView
            r1.setVisibility(r8)
        L_0x00f6:
            int r5 = r4 + r0
            r2 = r3
        L_0x00f9:
            boolean r0 = r9.canShowPlaybackControlLayout()
            if (r0 == 0) goto L_0x0107
            if (r5 > r7) goto L_0x0107
            android.widget.RelativeLayout r0 = r9.mPlaybackControlLayout
            r0.setVisibility(r3)
            goto L_0x010c
        L_0x0107:
            android.widget.RelativeLayout r0 = r9.mPlaybackControlLayout
            r0.setVisibility(r8)
        L_0x010c:
            android.widget.RelativeLayout r0 = r9.mPlaybackControlLayout
            int r0 = r0.getVisibility()
            r1 = 1
            if (r0 != 0) goto L_0x0117
            r0 = r1
            goto L_0x0118
        L_0x0117:
            r0 = r3
        L_0x0118:
            r9.updateMediaControlVisibility(r0)
            android.widget.RelativeLayout r0 = r9.mPlaybackControlLayout
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0124
            r3 = r1
        L_0x0124:
            int r0 = r9.getMainControllerHeight(r3)
            int r1 = java.lang.Math.max(r2, r4)
            int r1 = r1 + r0
            if (r1 <= r7) goto L_0x0132
            int r1 = r1 - r7
            int r4 = r4 - r1
            goto L_0x0133
        L_0x0132:
            r7 = r1
        L_0x0133:
            android.widget.LinearLayout r1 = r9.mMediaMainControlLayout
            r1.clearAnimation()
            androidx.mediarouter.app.OverlayListView r1 = r9.mVolumeGroupList
            r1.clearAnimation()
            android.widget.FrameLayout r1 = r9.mDefaultControlLayout
            r1.clearAnimation()
            if (r10 == 0) goto L_0x0154
            android.widget.LinearLayout r1 = r9.mMediaMainControlLayout
            r9.animateLayoutHeight(r1, r0)
            androidx.mediarouter.app.OverlayListView r0 = r9.mVolumeGroupList
            r9.animateLayoutHeight(r0, r4)
            android.widget.FrameLayout r0 = r9.mDefaultControlLayout
            r9.animateLayoutHeight(r0, r7)
            goto L_0x0163
        L_0x0154:
            android.widget.LinearLayout r1 = r9.mMediaMainControlLayout
            setLayoutHeight(r1, r0)
            androidx.mediarouter.app.OverlayListView r0 = r9.mVolumeGroupList
            setLayoutHeight(r0, r4)
            android.widget.FrameLayout r0 = r9.mDefaultControlLayout
            setLayoutHeight(r0, r7)
        L_0x0163:
            android.widget.FrameLayout r0 = r9.mExpandableAreaLayout
            int r1 = r6.height()
            setLayoutHeight(r0, r1)
            r9.rebuildVolumeGroupList(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.mediarouter.app.MediaRouteControllerDialog.updateLayoutHeightInternal(boolean):void");
    }

    /* access modifiers changed from: 0000 */
    public void updateVolumeGroupItemHeight(View view) {
        setLayoutHeight((LinearLayout) view.findViewById(R$id.volume_item_container), this.mVolumeGroupListItemHeight);
        View findViewById = view.findViewById(R$id.mr_volume_item_icon);
        LayoutParams layoutParams = findViewById.getLayoutParams();
        int i = this.mVolumeGroupListItemIconSize;
        layoutParams.width = i;
        layoutParams.height = i;
        findViewById.setLayoutParams(layoutParams);
    }

    private void animateLayoutHeight(final View view, final int i) {
        final int layoutHeight = getLayoutHeight(view);
        C03567 r1 = new Animation(this) {
            /* access modifiers changed from: protected */
            public void applyTransformation(float f, Transformation transformation) {
                int i = layoutHeight;
                MediaRouteControllerDialog.setLayoutHeight(view, i - ((int) (((float) (i - i)) * f)));
            }
        };
        r1.setDuration((long) this.mGroupListAnimationDurationMs);
        if (VERSION.SDK_INT >= 21) {
            r1.setInterpolator(this.mInterpolator);
        }
        view.startAnimation(r1);
    }

    /* access modifiers changed from: 0000 */
    public void loadInterpolator() {
        if (VERSION.SDK_INT >= 21) {
            this.mInterpolator = this.mIsGroupExpanded ? this.mLinearOutSlowInInterpolator : this.mFastOutSlowInInterpolator;
        } else {
            this.mInterpolator = this.mAccelerateDecelerateInterpolator;
        }
    }

    private void updateVolumeControlLayout() {
        int i = 8;
        if (!isVolumeControlAvailable(this.mRoute)) {
            this.mVolumeControlLayout.setVisibility(8);
        } else if (this.mVolumeControlLayout.getVisibility() == 8) {
            this.mVolumeControlLayout.setVisibility(0);
            this.mVolumeSlider.setMax(this.mRoute.getVolumeMax());
            this.mVolumeSlider.setProgress(this.mRoute.getVolume());
            MediaRouteExpandCollapseButton mediaRouteExpandCollapseButton = this.mGroupExpandCollapseButton;
            if (this.mRoute.isGroup()) {
                i = 0;
            }
            mediaRouteExpandCollapseButton.setVisibility(i);
        }
    }

    private void rebuildVolumeGroupList(boolean z) {
        List memberRoutes = this.mRoute.getMemberRoutes();
        if (memberRoutes.isEmpty()) {
            this.mGroupMemberRoutes.clear();
            this.mVolumeGroupAdapter.notifyDataSetChanged();
        } else if (MediaRouteDialogHelper.listUnorderedEquals(this.mGroupMemberRoutes, memberRoutes)) {
            this.mVolumeGroupAdapter.notifyDataSetChanged();
        } else {
            Map itemBoundMap = z ? MediaRouteDialogHelper.getItemBoundMap(this.mVolumeGroupList, this.mVolumeGroupAdapter) : null;
            Map itemBitmapMap = z ? MediaRouteDialogHelper.getItemBitmapMap(this.mContext, this.mVolumeGroupList, this.mVolumeGroupAdapter) : null;
            this.mGroupMemberRoutesAdded = MediaRouteDialogHelper.getItemsAdded(this.mGroupMemberRoutes, memberRoutes);
            this.mGroupMemberRoutesRemoved = MediaRouteDialogHelper.getItemsRemoved(this.mGroupMemberRoutes, memberRoutes);
            this.mGroupMemberRoutes.addAll(0, this.mGroupMemberRoutesAdded);
            this.mGroupMemberRoutes.removeAll(this.mGroupMemberRoutesRemoved);
            this.mVolumeGroupAdapter.notifyDataSetChanged();
            if (!z || !this.mIsGroupExpanded || this.mGroupMemberRoutesAdded.size() + this.mGroupMemberRoutesRemoved.size() <= 0) {
                this.mGroupMemberRoutesAdded = null;
                this.mGroupMemberRoutesRemoved = null;
                return;
            }
            animateGroupListItems(itemBoundMap, itemBitmapMap);
        }
    }

    private void animateGroupListItems(final Map<RouteInfo, Rect> map, final Map<RouteInfo, BitmapDrawable> map2) {
        this.mVolumeGroupList.setEnabled(false);
        this.mVolumeGroupList.requestLayout();
        this.mIsGroupListAnimating = true;
        this.mVolumeGroupList.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mVolumeGroupList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MediaRouteControllerDialog.this.animateGroupListItemsInternal(map, map2);
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void animateGroupListItemsInternal(Map<RouteInfo, Rect> map, Map<RouteInfo, BitmapDrawable> map2) {
        OverlayObject overlayObject;
        Map<RouteInfo, Rect> map3 = map;
        Set<RouteInfo> set = this.mGroupMemberRoutesAdded;
        if (set != null && this.mGroupMemberRoutesRemoved != null) {
            int size = set.size() - this.mGroupMemberRoutesRemoved.size();
            C03589 r3 = new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                    MediaRouteControllerDialog.this.mVolumeGroupList.startAnimationAll();
                    MediaRouteControllerDialog mediaRouteControllerDialog = MediaRouteControllerDialog.this;
                    mediaRouteControllerDialog.mVolumeGroupList.postDelayed(mediaRouteControllerDialog.mGroupListFadeInAnimation, (long) mediaRouteControllerDialog.mGroupListAnimationDurationMs);
                }
            };
            int firstVisiblePosition = this.mVolumeGroupList.getFirstVisiblePosition();
            boolean z = false;
            for (int i = 0; i < this.mVolumeGroupList.getChildCount(); i++) {
                View childAt = this.mVolumeGroupList.getChildAt(i);
                RouteInfo routeInfo = (RouteInfo) this.mVolumeGroupAdapter.getItem(firstVisiblePosition + i);
                Rect rect = (Rect) map3.get(routeInfo);
                int top = childAt.getTop();
                int i2 = rect != null ? rect.top : (this.mVolumeGroupListItemHeight * size) + top;
                AnimationSet animationSet = new AnimationSet(true);
                Set<RouteInfo> set2 = this.mGroupMemberRoutesAdded;
                if (set2 != null && set2.contains(routeInfo)) {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 0.0f);
                    alphaAnimation.setDuration((long) this.mGroupListFadeInDurationMs);
                    animationSet.addAnimation(alphaAnimation);
                    i2 = top;
                }
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (i2 - top), 0.0f);
                translateAnimation.setDuration((long) this.mGroupListAnimationDurationMs);
                animationSet.addAnimation(translateAnimation);
                animationSet.setFillAfter(true);
                animationSet.setFillEnabled(true);
                animationSet.setInterpolator(this.mInterpolator);
                if (!z) {
                    animationSet.setAnimationListener(r3);
                    z = true;
                }
                childAt.clearAnimation();
                childAt.startAnimation(animationSet);
                map3.remove(routeInfo);
                map2.remove(routeInfo);
            }
            Map<RouteInfo, BitmapDrawable> map4 = map2;
            for (Entry entry : map2.entrySet()) {
                final RouteInfo routeInfo2 = (RouteInfo) entry.getKey();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) entry.getValue();
                Rect rect2 = (Rect) map3.get(routeInfo2);
                if (this.mGroupMemberRoutesRemoved.contains(routeInfo2)) {
                    overlayObject = new OverlayObject(bitmapDrawable, rect2);
                    overlayObject.setAlphaAnimation(1.0f, 0.0f);
                    overlayObject.setDuration((long) this.mGroupListFadeOutDurationMs);
                    overlayObject.setInterpolator(this.mInterpolator);
                } else {
                    int i3 = this.mVolumeGroupListItemHeight * size;
                    OverlayObject overlayObject2 = new OverlayObject(bitmapDrawable, rect2);
                    overlayObject2.setTranslateYAnimation(i3);
                    overlayObject2.setDuration((long) this.mGroupListAnimationDurationMs);
                    overlayObject2.setInterpolator(this.mInterpolator);
                    overlayObject2.setAnimationEndListener(new OnAnimationEndListener() {
                        public void onAnimationEnd() {
                            MediaRouteControllerDialog.this.mGroupMemberRoutesAnimatingWithBitmap.remove(routeInfo2);
                            MediaRouteControllerDialog.this.mVolumeGroupAdapter.notifyDataSetChanged();
                        }
                    });
                    this.mGroupMemberRoutesAnimatingWithBitmap.add(routeInfo2);
                    overlayObject = overlayObject2;
                }
                this.mVolumeGroupList.addOverlayObject(overlayObject);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void startGroupListFadeInAnimation() {
        clearGroupListAnimation(true);
        this.mVolumeGroupList.requestLayout();
        this.mVolumeGroupList.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MediaRouteControllerDialog.this.mVolumeGroupList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MediaRouteControllerDialog.this.startGroupListFadeInAnimationInternal();
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void startGroupListFadeInAnimationInternal() {
        Set<RouteInfo> set = this.mGroupMemberRoutesAdded;
        if (set == null || set.size() == 0) {
            finishAnimation(true);
        } else {
            fadeInAddedRoutes();
        }
    }

    /* access modifiers changed from: 0000 */
    public void finishAnimation(boolean z) {
        this.mGroupMemberRoutesAdded = null;
        this.mGroupMemberRoutesRemoved = null;
        this.mIsGroupListAnimating = false;
        if (this.mIsGroupListAnimationPending) {
            this.mIsGroupListAnimationPending = false;
            updateLayoutHeight(z);
        }
        this.mVolumeGroupList.setEnabled(true);
    }

    private void fadeInAddedRoutes() {
        C035012 r0 = new AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                MediaRouteControllerDialog.this.finishAnimation(true);
            }
        };
        int firstVisiblePosition = this.mVolumeGroupList.getFirstVisiblePosition();
        boolean z = false;
        for (int i = 0; i < this.mVolumeGroupList.getChildCount(); i++) {
            View childAt = this.mVolumeGroupList.getChildAt(i);
            if (this.mGroupMemberRoutesAdded.contains((RouteInfo) this.mVolumeGroupAdapter.getItem(firstVisiblePosition + i))) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration((long) this.mGroupListFadeInDurationMs);
                alphaAnimation.setFillEnabled(true);
                alphaAnimation.setFillAfter(true);
                if (!z) {
                    alphaAnimation.setAnimationListener(r0);
                    z = true;
                }
                childAt.clearAnimation();
                childAt.startAnimation(alphaAnimation);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void clearGroupListAnimation(boolean z) {
        int firstVisiblePosition = this.mVolumeGroupList.getFirstVisiblePosition();
        for (int i = 0; i < this.mVolumeGroupList.getChildCount(); i++) {
            View childAt = this.mVolumeGroupList.getChildAt(i);
            RouteInfo routeInfo = (RouteInfo) this.mVolumeGroupAdapter.getItem(firstVisiblePosition + i);
            if (z) {
                Set<RouteInfo> set = this.mGroupMemberRoutesAdded;
                if (set != null && set.contains(routeInfo)) {
                }
            }
            ((LinearLayout) childAt.findViewById(R$id.volume_item_container)).setVisibility(0);
            AnimationSet animationSet = new AnimationSet(true);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
            alphaAnimation.setDuration(0);
            animationSet.addAnimation(alphaAnimation);
            new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f).setDuration(0);
            animationSet.setFillAfter(true);
            animationSet.setFillEnabled(true);
            childAt.clearAnimation();
            childAt.startAnimation(animationSet);
        }
        this.mVolumeGroupList.stopAnimationAll();
        if (!z) {
            finishAnimation(false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePlaybackControlLayout() {
        /*
            r8 = this;
            boolean r0 = r8.canShowPlaybackControlLayout()
            if (r0 == 0) goto L_0x00ea
            android.support.v4.media.MediaDescriptionCompat r0 = r8.mDescription
            r1 = 0
            if (r0 != 0) goto L_0x000d
            r0 = r1
            goto L_0x0011
        L_0x000d:
            java.lang.CharSequence r0 = r0.getTitle()
        L_0x0011:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            r3 = 1
            r2 = r2 ^ r3
            android.support.v4.media.MediaDescriptionCompat r4 = r8.mDescription
            if (r4 != 0) goto L_0x001c
            goto L_0x0020
        L_0x001c:
            java.lang.CharSequence r1 = r4.getSubtitle()
        L_0x0020:
            boolean r4 = android.text.TextUtils.isEmpty(r1)
            r4 = r4 ^ r3
            androidx.mediarouter.media.MediaRouter$RouteInfo r5 = r8.mRoute
            int r5 = r5.getPresentationDisplayId()
            r6 = -1
            r7 = 0
            if (r5 == r6) goto L_0x0039
            android.widget.TextView r0 = r8.mTitleView
            int r1 = androidx.mediarouter.R$string.mr_controller_casting_screen
            r0.setText(r1)
        L_0x0036:
            r0 = r3
        L_0x0037:
            r1 = r7
            goto L_0x006b
        L_0x0039:
            android.support.v4.media.session.PlaybackStateCompat r5 = r8.mState
            if (r5 == 0) goto L_0x0063
            int r5 = r5.getState()
            if (r5 != 0) goto L_0x0044
            goto L_0x0063
        L_0x0044:
            if (r2 != 0) goto L_0x0050
            if (r4 != 0) goto L_0x0050
            android.widget.TextView r0 = r8.mTitleView
            int r1 = androidx.mediarouter.R$string.mr_controller_no_info_available
            r0.setText(r1)
            goto L_0x0036
        L_0x0050:
            if (r2 == 0) goto L_0x0059
            android.widget.TextView r2 = r8.mTitleView
            r2.setText(r0)
            r0 = r3
            goto L_0x005a
        L_0x0059:
            r0 = r7
        L_0x005a:
            if (r4 == 0) goto L_0x0037
            android.widget.TextView r2 = r8.mSubtitleView
            r2.setText(r1)
            r1 = r3
            goto L_0x006b
        L_0x0063:
            android.widget.TextView r0 = r8.mTitleView
            int r1 = androidx.mediarouter.R$string.mr_controller_no_media_selected
            r0.setText(r1)
            goto L_0x0036
        L_0x006b:
            android.widget.TextView r2 = r8.mTitleView
            r4 = 8
            if (r0 == 0) goto L_0x0073
            r0 = r7
            goto L_0x0074
        L_0x0073:
            r0 = r4
        L_0x0074:
            r2.setVisibility(r0)
            android.widget.TextView r0 = r8.mSubtitleView
            if (r1 == 0) goto L_0x007d
            r1 = r7
            goto L_0x007e
        L_0x007d:
            r1 = r4
        L_0x007e:
            r0.setVisibility(r1)
            android.support.v4.media.session.PlaybackStateCompat r0 = r8.mState
            if (r0 == 0) goto L_0x00ea
            int r0 = r0.getState()
            r1 = 6
            if (r0 == r1) goto L_0x0098
            android.support.v4.media.session.PlaybackStateCompat r0 = r8.mState
            int r0 = r0.getState()
            r1 = 3
            if (r0 != r1) goto L_0x0096
            goto L_0x0098
        L_0x0096:
            r0 = r7
            goto L_0x0099
        L_0x0098:
            r0 = r3
        L_0x0099:
            android.widget.ImageButton r1 = r8.mPlaybackControlButton
            android.content.Context r1 = r1.getContext()
            if (r0 == 0) goto L_0x00ac
            boolean r2 = r8.isPauseActionSupported()
            if (r2 == 0) goto L_0x00ac
            int r0 = androidx.mediarouter.R$attr.mediaRoutePauseDrawable
            int r2 = androidx.mediarouter.R$string.mr_controller_pause
            goto L_0x00c9
        L_0x00ac:
            if (r0 == 0) goto L_0x00b9
            boolean r2 = r8.isStopActionSupported()
            if (r2 == 0) goto L_0x00b9
            int r0 = androidx.mediarouter.R$attr.mediaRouteStopDrawable
            int r2 = androidx.mediarouter.R$string.mr_controller_stop
            goto L_0x00c9
        L_0x00b9:
            if (r0 != 0) goto L_0x00c6
            boolean r0 = r8.isPlayActionSupported()
            if (r0 == 0) goto L_0x00c6
            int r0 = androidx.mediarouter.R$attr.mediaRoutePlayDrawable
            int r2 = androidx.mediarouter.R$string.mr_controller_play
            goto L_0x00c9
        L_0x00c6:
            r0 = r7
            r2 = r0
            r3 = r2
        L_0x00c9:
            android.widget.ImageButton r5 = r8.mPlaybackControlButton
            if (r3 == 0) goto L_0x00ce
            goto L_0x00cf
        L_0x00ce:
            r7 = r4
        L_0x00cf:
            r5.setVisibility(r7)
            if (r3 == 0) goto L_0x00ea
            android.widget.ImageButton r3 = r8.mPlaybackControlButton
            int r0 = androidx.mediarouter.app.MediaRouterThemeHelper.getThemeResource(r1, r0)
            r3.setImageResource(r0)
            android.widget.ImageButton r8 = r8.mPlaybackControlButton
            android.content.res.Resources r0 = r1.getResources()
            java.lang.CharSequence r0 = r0.getText(r2)
            r8.setContentDescription(r0)
        L_0x00ea:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.mediarouter.app.MediaRouteControllerDialog.updatePlaybackControlLayout():void");
    }

    /* access modifiers changed from: 0000 */
    public boolean isPlayActionSupported() {
        return (this.mState.getActions() & 516) != 0;
    }

    /* access modifiers changed from: 0000 */
    public boolean isPauseActionSupported() {
        return (this.mState.getActions() & 514) != 0;
    }

    /* access modifiers changed from: 0000 */
    public boolean isStopActionSupported() {
        return (this.mState.getActions() & 1) != 0;
    }

    /* access modifiers changed from: 0000 */
    public boolean isVolumeControlAvailable(RouteInfo routeInfo) {
        return this.mVolumeControlEnabled && routeInfo.getVolumeHandling() == 1;
    }

    private static int getLayoutHeight(View view) {
        return view.getLayoutParams().height;
    }

    static void setLayoutHeight(View view, int i) {
        LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }

    private static boolean uriEquals(Uri uri, Uri uri2) {
        if (uri == null || !uri.equals(uri2)) {
            return uri == null && uri2 == null;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public int getDesiredArtHeight(int i, int i2) {
        float f;
        float f2;
        if (i >= i2) {
            f = ((float) this.mDialogContentWidth) * ((float) i2);
            f2 = (float) i;
        } else {
            f = ((float) this.mDialogContentWidth) * 9.0f;
            f2 = 16.0f;
        }
        return (int) ((f / f2) + 0.5f);
    }

    /* access modifiers changed from: 0000 */
    public void updateArtIconIfNeeded() {
        if (this.mCustomControlView == null && isIconChanged()) {
            FetchArtTask fetchArtTask = this.mFetchArtTask;
            if (fetchArtTask != null) {
                fetchArtTask.cancel(true);
            }
            FetchArtTask fetchArtTask2 = new FetchArtTask();
            this.mFetchArtTask = fetchArtTask2;
            fetchArtTask2.execute(new Void[0]);
        }
    }

    /* access modifiers changed from: 0000 */
    public void clearLoadedBitmap() {
        this.mArtIconIsLoaded = false;
        this.mArtIconLoadedBitmap = null;
        this.mArtIconBackgroundColor = 0;
    }

    private boolean isIconChanged() {
        MediaDescriptionCompat mediaDescriptionCompat = this.mDescription;
        Uri uri = null;
        Bitmap iconBitmap = mediaDescriptionCompat == null ? null : mediaDescriptionCompat.getIconBitmap();
        MediaDescriptionCompat mediaDescriptionCompat2 = this.mDescription;
        if (mediaDescriptionCompat2 != null) {
            uri = mediaDescriptionCompat2.getIconUri();
        }
        FetchArtTask fetchArtTask = this.mFetchArtTask;
        Bitmap iconBitmap2 = fetchArtTask == null ? this.mArtIconBitmap : fetchArtTask.getIconBitmap();
        FetchArtTask fetchArtTask2 = this.mFetchArtTask;
        Uri iconUri = fetchArtTask2 == null ? this.mArtIconUri : fetchArtTask2.getIconUri();
        if (iconBitmap2 != iconBitmap) {
            return true;
        }
        if (iconBitmap2 != null || uriEquals(iconUri, uri)) {
            return false;
        }
        return true;
    }
}
