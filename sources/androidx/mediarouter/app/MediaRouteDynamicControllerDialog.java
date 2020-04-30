package androidx.mediarouter.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.p000v4.media.MediaDescriptionCompat;
import android.support.p000v4.media.MediaMetadataCompat;
import android.support.p000v4.media.session.MediaControllerCompat;
import android.support.p000v4.media.session.MediaControllerCompat.Callback;
import android.support.p000v4.media.session.MediaSessionCompat.Token;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.util.ObjectsCompat;
import androidx.mediarouter.R$dimen;
import androidx.mediarouter.R$id;
import androidx.mediarouter.R$integer;
import androidx.mediarouter.R$layout;
import androidx.mediarouter.R$string;
import androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouter.RouteInfo;
import androidx.mediarouter.media.MediaRouter.RouteInfo.DynamicGroupState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MediaRouteDynamicControllerDialog extends AppCompatDialog {
    static final boolean DEBUG = Log.isLoggable("MediaRouteCtrlDialog", 3);
    RecyclerAdapter mAdapter;
    int mArtIconBackgroundColor;
    Bitmap mArtIconBitmap;
    boolean mArtIconIsLoaded;
    Bitmap mArtIconLoadedBitmap;
    Uri mArtIconUri;
    ImageView mArtView;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private ImageButton mCloseButton;
    Context mContext;
    MediaControllerCallback mControllerCallback;
    private boolean mCreated;
    MediaDescriptionCompat mDescription;
    FetchArtTask mFetchArtTask;
    final List<RouteInfo> mGroupableRoutes;
    final Handler mHandler;
    boolean mIsAnimatingVolumeSliderLayout;
    boolean mIsSelectingRoute;
    private long mLastUpdateTime;
    MediaControllerCompat mMediaController;
    final List<RouteInfo> mMemberRoutes;
    private ImageView mMetadataBackground;
    private View mMetadataBlackScrim;
    RecyclerView mRecyclerView;
    RouteInfo mRouteForVolumeUpdatingByUser;
    final MediaRouter mRouter;
    RouteInfo mSelectedRoute;
    private MediaRouteSelector mSelector;
    private Button mStopCastingButton;
    private TextView mSubtitleView;
    private String mTitlePlaceholder;
    private TextView mTitleView;
    final List<RouteInfo> mTransferableRoutes;
    final List<RouteInfo> mUngroupableRoutes;
    Map<String, Integer> mUnmutedVolumeMap;
    private boolean mUpdateMetadataViewsDeferred;
    private boolean mUpdateRoutesViewDeferred;
    VolumeChangeListener mVolumeChangeListener;
    Map<String, MediaRouteVolumeSliderHolder> mVolumeSliderHolderMap;

    private class FetchArtTask extends AsyncTask<Void, Void, Bitmap> {
        private int mBackgroundColor;
        private final Bitmap mIconBitmap;
        private final Uri mIconUri;

        FetchArtTask() {
            MediaDescriptionCompat mediaDescriptionCompat = MediaRouteDynamicControllerDialog.this.mDescription;
            Uri uri = null;
            Bitmap iconBitmap = mediaDescriptionCompat == null ? null : mediaDescriptionCompat.getIconBitmap();
            if (MediaRouteDynamicControllerDialog.isBitmapRecycled(iconBitmap)) {
                Log.w("MediaRouteCtrlDialog", "Can't fetch the given art bitmap because it's already recycled.");
                iconBitmap = null;
            }
            this.mIconBitmap = iconBitmap;
            MediaDescriptionCompat mediaDescriptionCompat2 = MediaRouteDynamicControllerDialog.this.mDescription;
            if (mediaDescriptionCompat2 != null) {
                uri = mediaDescriptionCompat2.getIconUri();
            }
            this.mIconUri = uri;
        }

        /* access modifiers changed from: 0000 */
        public Bitmap getIconBitmap() {
            return this.mIconBitmap;
        }

        /* access modifiers changed from: 0000 */
        public Uri getIconUri() {
            return this.mIconUri;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            MediaRouteDynamicControllerDialog.this.clearLoadedBitmap();
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:19|20|21|22|(3:24|(2:26|27)|28)|30|31|(2:(2:34|35)|36)(4:38|39|(2:41|42)|43)) */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x00a7, code lost:
            r5 = e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
            r0.close();
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0048 */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0053 A[Catch:{ IOException -> 0x00a7 }] */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x00c3 A[SYNTHETIC, Splitter:B:57:0x00c3] */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x00cb A[SYNTHETIC, Splitter:B:62:0x00cb] */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x00d6  */
        /* JADX WARNING: Removed duplicated region for block: B:72:0x00eb A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.graphics.Bitmap doInBackground(java.lang.Void... r9) {
            /*
                r8 = this;
                java.lang.String r9 = "Unable to open: "
                android.graphics.Bitmap r0 = r8.mIconBitmap
                r1 = 0
                r2 = 1
                java.lang.String r3 = "MediaRouteCtrlDialog"
                r4 = 0
                if (r0 == 0) goto L_0x000d
                goto L_0x00d0
            L_0x000d:
                android.net.Uri r0 = r8.mIconUri
                if (r0 == 0) goto L_0x00cf
                java.io.InputStream r0 = r8.openInputStreamByScheme(r0)     // Catch:{ IOException -> 0x00ab, all -> 0x00a9 }
                if (r0 != 0) goto L_0x0031
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00a7 }
                r5.<init>()     // Catch:{ IOException -> 0x00a7 }
                r5.append(r9)     // Catch:{ IOException -> 0x00a7 }
                android.net.Uri r6 = r8.mIconUri     // Catch:{ IOException -> 0x00a7 }
                r5.append(r6)     // Catch:{ IOException -> 0x00a7 }
                java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00a7 }
                android.util.Log.w(r3, r5)     // Catch:{ IOException -> 0x00a7 }
                if (r0 == 0) goto L_0x0030
                r0.close()     // Catch:{ IOException -> 0x0030 }
            L_0x0030:
                return r4
            L_0x0031:
                android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ IOException -> 0x00a7 }
                r5.<init>()     // Catch:{ IOException -> 0x00a7 }
                r5.inJustDecodeBounds = r2     // Catch:{ IOException -> 0x00a7 }
                android.graphics.BitmapFactory.decodeStream(r0, r4, r5)     // Catch:{ IOException -> 0x00a7 }
                int r6 = r5.outWidth     // Catch:{ IOException -> 0x00a7 }
                if (r6 == 0) goto L_0x00a1
                int r6 = r5.outHeight     // Catch:{ IOException -> 0x00a7 }
                if (r6 != 0) goto L_0x0044
                goto L_0x00a1
            L_0x0044:
                r0.reset()     // Catch:{ IOException -> 0x0048 }
                goto L_0x006d
            L_0x0048:
                r0.close()     // Catch:{ IOException -> 0x00a7 }
                android.net.Uri r6 = r8.mIconUri     // Catch:{ IOException -> 0x00a7 }
                java.io.InputStream r0 = r8.openInputStreamByScheme(r6)     // Catch:{ IOException -> 0x00a7 }
                if (r0 != 0) goto L_0x006d
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00a7 }
                r5.<init>()     // Catch:{ IOException -> 0x00a7 }
                r5.append(r9)     // Catch:{ IOException -> 0x00a7 }
                android.net.Uri r6 = r8.mIconUri     // Catch:{ IOException -> 0x00a7 }
                r5.append(r6)     // Catch:{ IOException -> 0x00a7 }
                java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00a7 }
                android.util.Log.w(r3, r5)     // Catch:{ IOException -> 0x00a7 }
                if (r0 == 0) goto L_0x006c
                r0.close()     // Catch:{ IOException -> 0x006c }
            L_0x006c:
                return r4
            L_0x006d:
                r5.inJustDecodeBounds = r1     // Catch:{ IOException -> 0x00a7 }
                androidx.mediarouter.app.MediaRouteDynamicControllerDialog r6 = androidx.mediarouter.app.MediaRouteDynamicControllerDialog.this     // Catch:{ IOException -> 0x00a7 }
                android.content.Context r6 = r6.mContext     // Catch:{ IOException -> 0x00a7 }
                android.content.res.Resources r6 = r6.getResources()     // Catch:{ IOException -> 0x00a7 }
                int r7 = androidx.mediarouter.R$dimen.mr_cast_meta_art_size     // Catch:{ IOException -> 0x00a7 }
                int r6 = r6.getDimensionPixelSize(r7)     // Catch:{ IOException -> 0x00a7 }
                int r7 = r5.outHeight     // Catch:{ IOException -> 0x00a7 }
                int r7 = r7 / r6
                int r6 = java.lang.Integer.highestOneBit(r7)     // Catch:{ IOException -> 0x00a7 }
                int r6 = java.lang.Math.max(r2, r6)     // Catch:{ IOException -> 0x00a7 }
                r5.inSampleSize = r6     // Catch:{ IOException -> 0x00a7 }
                boolean r6 = r8.isCancelled()     // Catch:{ IOException -> 0x00a7 }
                if (r6 == 0) goto L_0x0096
                if (r0 == 0) goto L_0x0095
                r0.close()     // Catch:{ IOException -> 0x0095 }
            L_0x0095:
                return r4
            L_0x0096:
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r0, r4, r5)     // Catch:{ IOException -> 0x00a7 }
                if (r0 == 0) goto L_0x009f
                r0.close()     // Catch:{ IOException -> 0x009f }
            L_0x009f:
                r0 = r9
                goto L_0x00d0
            L_0x00a1:
                if (r0 == 0) goto L_0x00a6
                r0.close()     // Catch:{ IOException -> 0x00a6 }
            L_0x00a6:
                return r4
            L_0x00a7:
                r5 = move-exception
                goto L_0x00ad
            L_0x00a9:
                r8 = move-exception
                goto L_0x00c9
            L_0x00ab:
                r5 = move-exception
                r0 = r4
            L_0x00ad:
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c7 }
                r6.<init>()     // Catch:{ all -> 0x00c7 }
                r6.append(r9)     // Catch:{ all -> 0x00c7 }
                android.net.Uri r9 = r8.mIconUri     // Catch:{ all -> 0x00c7 }
                r6.append(r9)     // Catch:{ all -> 0x00c7 }
                java.lang.String r9 = r6.toString()     // Catch:{ all -> 0x00c7 }
                android.util.Log.w(r3, r9, r5)     // Catch:{ all -> 0x00c7 }
                if (r0 == 0) goto L_0x00cf
                r0.close()     // Catch:{ IOException -> 0x00cf }
                goto L_0x00cf
            L_0x00c7:
                r8 = move-exception
                r4 = r0
            L_0x00c9:
                if (r4 == 0) goto L_0x00ce
                r4.close()     // Catch:{ IOException -> 0x00ce }
            L_0x00ce:
                throw r8
            L_0x00cf:
                r0 = r4
            L_0x00d0:
                boolean r9 = androidx.mediarouter.app.MediaRouteDynamicControllerDialog.isBitmapRecycled(r0)
                if (r9 == 0) goto L_0x00eb
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r9 = "Can't use recycled bitmap: "
                r8.append(r9)
                r8.append(r0)
                java.lang.String r8 = r8.toString()
                android.util.Log.w(r3, r8)
                return r4
            L_0x00eb:
                if (r0 == 0) goto L_0x011e
                int r9 = r0.getWidth()
                int r3 = r0.getHeight()
                if (r9 >= r3) goto L_0x011e
                androidx.palette.graphics.Palette$Builder r9 = new androidx.palette.graphics.Palette$Builder
                r9.<init>(r0)
                r9.maximumColorCount(r2)
                androidx.palette.graphics.Palette r9 = r9.generate()
                java.util.List r2 = r9.getSwatches()
                boolean r2 = r2.isEmpty()
                if (r2 == 0) goto L_0x010e
                goto L_0x011c
            L_0x010e:
                java.util.List r9 = r9.getSwatches()
                java.lang.Object r9 = r9.get(r1)
                androidx.palette.graphics.Palette$Swatch r9 = (androidx.palette.graphics.Palette.Swatch) r9
                int r1 = r9.getRgb()
            L_0x011c:
                r8.mBackgroundColor = r1
            L_0x011e:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.mediarouter.app.MediaRouteDynamicControllerDialog.FetchArtTask.doInBackground(java.lang.Void[]):android.graphics.Bitmap");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bitmap bitmap) {
            MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
            mediaRouteDynamicControllerDialog.mFetchArtTask = null;
            if (!ObjectsCompat.equals(mediaRouteDynamicControllerDialog.mArtIconBitmap, this.mIconBitmap) || !ObjectsCompat.equals(MediaRouteDynamicControllerDialog.this.mArtIconUri, this.mIconUri)) {
                MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog2 = MediaRouteDynamicControllerDialog.this;
                mediaRouteDynamicControllerDialog2.mArtIconBitmap = this.mIconBitmap;
                mediaRouteDynamicControllerDialog2.mArtIconLoadedBitmap = bitmap;
                mediaRouteDynamicControllerDialog2.mArtIconUri = this.mIconUri;
                mediaRouteDynamicControllerDialog2.mArtIconBackgroundColor = this.mBackgroundColor;
                mediaRouteDynamicControllerDialog2.mArtIconIsLoaded = true;
                mediaRouteDynamicControllerDialog2.updateMetadataViews();
            }
        }

        private InputStream openInputStreamByScheme(Uri uri) throws IOException {
            InputStream inputStream;
            String lowerCase = uri.getScheme().toLowerCase();
            if ("android.resource".equals(lowerCase) || "content".equals(lowerCase) || "file".equals(lowerCase)) {
                inputStream = MediaRouteDynamicControllerDialog.this.mContext.getContentResolver().openInputStream(uri);
            } else {
                URLConnection openConnection = new URL(uri.toString()).openConnection();
                openConnection.setConnectTimeout(30000);
                openConnection.setReadTimeout(30000);
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
            MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
            MediaControllerCompat mediaControllerCompat = mediaRouteDynamicControllerDialog.mMediaController;
            if (mediaControllerCompat != null) {
                mediaControllerCompat.unregisterCallback(mediaRouteDynamicControllerDialog.mControllerCallback);
                MediaRouteDynamicControllerDialog.this.mMediaController = null;
            }
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
            MediaRouteDynamicControllerDialog.this.mDescription = mediaMetadataCompat == null ? null : mediaMetadataCompat.getDescription();
            MediaRouteDynamicControllerDialog.this.reloadIconIfNeeded();
            MediaRouteDynamicControllerDialog.this.updateMetadataViews();
        }
    }

    private abstract class MediaRouteVolumeSliderHolder extends ViewHolder {
        final ImageButton mMuteButton;
        RouteInfo mRoute;
        final MediaRouteVolumeSlider mVolumeSlider;

        MediaRouteVolumeSliderHolder(View view, ImageButton imageButton, MediaRouteVolumeSlider mediaRouteVolumeSlider) {
            super(view);
            this.mMuteButton = imageButton;
            this.mVolumeSlider = mediaRouteVolumeSlider;
            this.mMuteButton.setImageDrawable(MediaRouterThemeHelper.getMuteButtonDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext));
            MediaRouterThemeHelper.setVolumeSliderColor(MediaRouteDynamicControllerDialog.this.mContext, this.mVolumeSlider);
        }

        /* access modifiers changed from: 0000 */
        public void bindRouteVolumeSliderHolder(RouteInfo routeInfo) {
            this.mRoute = routeInfo;
            int volume = routeInfo.getVolume();
            this.mMuteButton.setActivated(volume == 0);
            this.mMuteButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    int i;
                    MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
                    if (mediaRouteDynamicControllerDialog.mRouteForVolumeUpdatingByUser != null) {
                        mediaRouteDynamicControllerDialog.mHandler.removeMessages(2);
                    }
                    MediaRouteVolumeSliderHolder mediaRouteVolumeSliderHolder = MediaRouteVolumeSliderHolder.this;
                    MediaRouteDynamicControllerDialog.this.mRouteForVolumeUpdatingByUser = mediaRouteVolumeSliderHolder.mRoute;
                    boolean z = !view.isActivated();
                    if (z) {
                        i = 0;
                    } else {
                        i = MediaRouteVolumeSliderHolder.this.getUnmutedVolume();
                    }
                    MediaRouteVolumeSliderHolder.this.setMute(z);
                    MediaRouteVolumeSliderHolder.this.mVolumeSlider.setProgress(i);
                    MediaRouteVolumeSliderHolder.this.mRoute.requestSetVolume(i);
                    MediaRouteDynamicControllerDialog.this.mHandler.sendEmptyMessageDelayed(2, 500);
                }
            });
            this.mVolumeSlider.setTag(this.mRoute);
            this.mVolumeSlider.setMax(routeInfo.getVolumeMax());
            this.mVolumeSlider.setProgress(volume);
            this.mVolumeSlider.setOnSeekBarChangeListener(MediaRouteDynamicControllerDialog.this.mVolumeChangeListener);
        }

        /* access modifiers changed from: 0000 */
        public void updateVolume() {
            int volume = this.mRoute.getVolume();
            setMute(volume == 0);
            this.mVolumeSlider.setProgress(volume);
        }

        /* access modifiers changed from: 0000 */
        public void setMute(boolean z) {
            if (this.mMuteButton.isActivated() != z) {
                this.mMuteButton.setActivated(z);
                if (z) {
                    MediaRouteDynamicControllerDialog.this.mUnmutedVolumeMap.put(this.mRoute.getId(), Integer.valueOf(this.mVolumeSlider.getProgress()));
                } else {
                    MediaRouteDynamicControllerDialog.this.mUnmutedVolumeMap.remove(this.mRoute.getId());
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public int getUnmutedVolume() {
            Integer num = (Integer) MediaRouteDynamicControllerDialog.this.mUnmutedVolumeMap.get(this.mRoute.getId());
            if (num == null) {
                return 1;
            }
            return Math.max(1, num.intValue());
        }
    }

    private final class MediaRouterCallback extends MediaRouter.Callback {
        MediaRouterCallback() {
        }

        public void onRouteAdded(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicControllerDialog.this.updateRoutesView();
        }

        public void onRouteRemoved(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicControllerDialog.this.updateRoutesView();
        }

        public void onRouteSelected(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
            mediaRouteDynamicControllerDialog.mSelectedRoute = routeInfo;
            mediaRouteDynamicControllerDialog.mIsSelectingRoute = false;
            mediaRouteDynamicControllerDialog.updateViewsIfNeeded();
            MediaRouteDynamicControllerDialog.this.updateRoutes();
        }

        public void onRouteUnselected(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicControllerDialog.this.updateRoutesView();
        }

        public void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            boolean z;
            if (routeInfo == MediaRouteDynamicControllerDialog.this.mSelectedRoute && routeInfo.getDynamicGroupState() != null) {
                Iterator it = routeInfo.getProvider().getRoutes().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    RouteInfo routeInfo2 = (RouteInfo) it.next();
                    if (!MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes().contains(routeInfo2)) {
                        DynamicGroupState dynamicGroupState = routeInfo2.getDynamicGroupState();
                        if (dynamicGroupState != null && dynamicGroupState.isGroupable() && !MediaRouteDynamicControllerDialog.this.mGroupableRoutes.contains(routeInfo2)) {
                            z = true;
                            break;
                        }
                    }
                }
            }
            z = false;
            if (z) {
                MediaRouteDynamicControllerDialog.this.updateViewsIfNeeded();
                MediaRouteDynamicControllerDialog.this.updateRoutes();
                return;
            }
            MediaRouteDynamicControllerDialog.this.updateRoutesView();
        }

        public void onRouteVolumeChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            int volume = routeInfo.getVolume();
            if (MediaRouteDynamicControllerDialog.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteVolumeChanged(), route.getVolume:");
                sb.append(volume);
                Log.d("MediaRouteCtrlDialog", sb.toString());
            }
            MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
            if (mediaRouteDynamicControllerDialog.mRouteForVolumeUpdatingByUser != routeInfo) {
                MediaRouteVolumeSliderHolder mediaRouteVolumeSliderHolder = (MediaRouteVolumeSliderHolder) mediaRouteDynamicControllerDialog.mVolumeSliderHolderMap.get(routeInfo.getId());
                if (mediaRouteVolumeSliderHolder != null) {
                    mediaRouteVolumeSliderHolder.updateVolume();
                }
            }
        }
    }

    private final class RecyclerAdapter extends Adapter<ViewHolder> {
        private final Interpolator mAccelerateDecelerateInterpolator;
        private final Drawable mDefaultIcon;
        private Item mGroupVolumeItem;
        private final LayoutInflater mInflater;
        private final ArrayList<Item> mItems = new ArrayList<>();
        private final int mLayoutAnimationDurationMs;
        private final Drawable mSpeakerGroupIcon;
        private final Drawable mSpeakerIcon;
        private final Drawable mTvIcon;

        private class GroupViewHolder extends ViewHolder {
            final float mDisabledAlpha;
            final ImageView mImageView;
            final View mItemView;
            final ProgressBar mProgressBar;
            RouteInfo mRoute;
            final TextView mTextView;

            GroupViewHolder(View view) {
                super(view);
                this.mItemView = view;
                this.mImageView = (ImageView) view.findViewById(R$id.mr_cast_group_icon);
                this.mProgressBar = (ProgressBar) view.findViewById(R$id.mr_cast_group_progress_bar);
                this.mTextView = (TextView) view.findViewById(R$id.mr_cast_group_name);
                this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(MediaRouteDynamicControllerDialog.this.mContext);
                MediaRouterThemeHelper.setIndeterminateProgressBarColor(MediaRouteDynamicControllerDialog.this.mContext, this.mProgressBar);
            }

            private boolean isEnabled(RouteInfo routeInfo) {
                if (MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupState() != null) {
                    List memberRoutes = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes();
                    if (memberRoutes.size() == 1 && memberRoutes.get(0) == routeInfo) {
                        return false;
                    }
                    return true;
                }
                return true;
            }

            /* access modifiers changed from: 0000 */
            public void bindGroupViewHolder(Item item) {
                RouteInfo routeInfo = (RouteInfo) item.getData();
                this.mRoute = routeInfo;
                this.mImageView.setVisibility(0);
                this.mProgressBar.setVisibility(4);
                this.mItemView.setAlpha(isEnabled(routeInfo) ? 1.0f : this.mDisabledAlpha);
                this.mItemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        GroupViewHolder groupViewHolder = GroupViewHolder.this;
                        MediaRouteDynamicControllerDialog.this.mIsSelectingRoute = true;
                        groupViewHolder.mRoute.select();
                        GroupViewHolder.this.mImageView.setVisibility(4);
                        GroupViewHolder.this.mProgressBar.setVisibility(0);
                    }
                });
                this.mImageView.setImageDrawable(RecyclerAdapter.this.getIconDrawable(routeInfo));
                this.mTextView.setText(routeInfo.getName());
            }
        }

        private class GroupVolumeViewHolder extends MediaRouteVolumeSliderHolder {
            private final int mExpandedHeight;
            private final TextView mTextView;

            GroupVolumeViewHolder(View view) {
                super(view, (ImageButton) view.findViewById(R$id.mr_cast_mute_button), (MediaRouteVolumeSlider) view.findViewById(R$id.mr_cast_volume_slider));
                this.mTextView = (TextView) view.findViewById(R$id.mr_group_volume_route_name);
                Resources resources = MediaRouteDynamicControllerDialog.this.mContext.getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                TypedValue typedValue = new TypedValue();
                resources.getValue(R$dimen.mr_dynamic_volume_group_list_item_height, typedValue, true);
                this.mExpandedHeight = (int) typedValue.getDimension(displayMetrics);
            }

            /* access modifiers changed from: 0000 */
            public void bindGroupVolumeViewHolder(Item item) {
                MediaRouteDynamicControllerDialog.setLayoutHeight(this.itemView, RecyclerAdapter.this.isGroupVolumeNeeded() ? this.mExpandedHeight : 0);
                RouteInfo routeInfo = (RouteInfo) item.getData();
                super.bindRouteVolumeSliderHolder(routeInfo);
                this.mTextView.setText(routeInfo.getName());
            }

            /* access modifiers changed from: 0000 */
            public int getExpandedHeight() {
                return this.mExpandedHeight;
            }
        }

        private class HeaderViewHolder extends ViewHolder {
            private final TextView mTextView;

            HeaderViewHolder(RecyclerAdapter recyclerAdapter, View view) {
                super(view);
                this.mTextView = (TextView) view.findViewById(R$id.mr_cast_header_name);
            }

            /* access modifiers changed from: 0000 */
            public void bindHeaderViewHolder(Item item) {
                this.mTextView.setText(item.getData().toString());
            }
        }

        private class Item {
            private final Object mData;
            private final int mType;

            Item(RecyclerAdapter recyclerAdapter, Object obj, int i) {
                this.mData = obj;
                this.mType = i;
            }

            public Object getData() {
                return this.mData;
            }

            public int getType() {
                return this.mType;
            }
        }

        private class RouteViewHolder extends MediaRouteVolumeSliderHolder {
            final CheckBox mCheckBox;
            final int mCollapsedLayoutHeight;
            final float mDisabledAlpha;
            final int mExpandedLayoutHeight;
            final ImageView mImageView;
            final View mItemView;
            final ProgressBar mProgressBar;
            final TextView mTextView;
            final OnClickListener mViewClickListener = new OnClickListener() {
                public void onClick(View view) {
                    RouteViewHolder routeViewHolder = RouteViewHolder.this;
                    boolean z = !routeViewHolder.isSelected(routeViewHolder.mRoute);
                    boolean isGroup = RouteViewHolder.this.mRoute.isGroup();
                    if (z) {
                        RouteViewHolder routeViewHolder2 = RouteViewHolder.this;
                        MediaRouteDynamicControllerDialog.this.mRouter.addMemberToDynamicGroup(routeViewHolder2.mRoute);
                    } else {
                        RouteViewHolder routeViewHolder3 = RouteViewHolder.this;
                        MediaRouteDynamicControllerDialog.this.mRouter.removeMemberFromDynamicGroup(routeViewHolder3.mRoute);
                    }
                    RouteViewHolder.this.showSelectingProgress(z, !isGroup);
                    if (isGroup) {
                        List memberRoutes = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes();
                        for (RouteInfo routeInfo : RouteViewHolder.this.mRoute.getMemberRoutes()) {
                            if (memberRoutes.contains(routeInfo) != z) {
                                MediaRouteVolumeSliderHolder mediaRouteVolumeSliderHolder = (MediaRouteVolumeSliderHolder) MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.get(routeInfo.getId());
                                if (mediaRouteVolumeSliderHolder instanceof RouteViewHolder) {
                                    ((RouteViewHolder) mediaRouteVolumeSliderHolder).showSelectingProgress(z, true);
                                }
                            }
                        }
                    }
                    RouteViewHolder routeViewHolder4 = RouteViewHolder.this;
                    RecyclerAdapter.this.mayUpdateGroupVolume(routeViewHolder4.mRoute, z);
                }
            };
            final RelativeLayout mVolumeSliderLayout;

            RouteViewHolder(View view) {
                super(view, (ImageButton) view.findViewById(R$id.mr_cast_mute_button), (MediaRouteVolumeSlider) view.findViewById(R$id.mr_cast_volume_slider));
                this.mItemView = view;
                this.mImageView = (ImageView) view.findViewById(R$id.mr_cast_route_icon);
                this.mProgressBar = (ProgressBar) view.findViewById(R$id.mr_cast_route_progress_bar);
                this.mTextView = (TextView) view.findViewById(R$id.mr_cast_route_name);
                this.mVolumeSliderLayout = (RelativeLayout) view.findViewById(R$id.mr_cast_volume_layout);
                this.mCheckBox = (CheckBox) view.findViewById(R$id.mr_cast_checkbox);
                this.mCheckBox.setButtonDrawable(MediaRouterThemeHelper.getCheckBoxDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext));
                MediaRouterThemeHelper.setIndeterminateProgressBarColor(MediaRouteDynamicControllerDialog.this.mContext, this.mProgressBar);
                this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(MediaRouteDynamicControllerDialog.this.mContext);
                Resources resources = MediaRouteDynamicControllerDialog.this.mContext.getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                TypedValue typedValue = new TypedValue();
                resources.getValue(R$dimen.mr_dynamic_dialog_row_height, typedValue, true);
                this.mExpandedLayoutHeight = (int) typedValue.getDimension(displayMetrics);
                this.mCollapsedLayoutHeight = 0;
            }

            /* access modifiers changed from: 0000 */
            public boolean isSelected(RouteInfo routeInfo) {
                boolean z = true;
                if (routeInfo.isSelected()) {
                    return true;
                }
                DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                if (dynamicGroupState == null || dynamicGroupState.getSelectionState() != 3) {
                    z = false;
                }
                return z;
            }

            private boolean isEnabled(RouteInfo routeInfo) {
                boolean z = false;
                if (MediaRouteDynamicControllerDialog.this.mUngroupableRoutes.contains(routeInfo)) {
                    return false;
                }
                if (isSelected(routeInfo) && MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes().size() < 2) {
                    return false;
                }
                if (!isSelected(routeInfo) || MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupState() == null) {
                    return true;
                }
                DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                if (dynamicGroupState != null && dynamicGroupState.isUnselectable()) {
                    z = true;
                }
                return z;
            }

            /* access modifiers changed from: 0000 */
            public void bindRouteViewHolder(Item item) {
                RouteInfo routeInfo = (RouteInfo) item.getData();
                if (routeInfo == MediaRouteDynamicControllerDialog.this.mSelectedRoute && routeInfo.getMemberRoutes().size() > 0) {
                    Iterator it = routeInfo.getMemberRoutes().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        RouteInfo routeInfo2 = (RouteInfo) it.next();
                        if (!MediaRouteDynamicControllerDialog.this.mGroupableRoutes.contains(routeInfo2)) {
                            routeInfo = routeInfo2;
                            break;
                        }
                    }
                }
                bindRouteVolumeSliderHolder(routeInfo);
                this.mImageView.setImageDrawable(RecyclerAdapter.this.getIconDrawable(routeInfo));
                this.mTextView.setText(routeInfo.getName());
                float f = 1.0f;
                boolean z = false;
                if (MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupState() != null) {
                    this.mCheckBox.setVisibility(0);
                    boolean isSelected = isSelected(routeInfo);
                    boolean isEnabled = isEnabled(routeInfo);
                    this.mCheckBox.setChecked(isSelected);
                    this.mProgressBar.setVisibility(4);
                    this.mImageView.setVisibility(0);
                    this.mItemView.setEnabled(isEnabled);
                    this.mCheckBox.setEnabled(isEnabled);
                    this.mMuteButton.setEnabled(isEnabled || isSelected);
                    MediaRouteVolumeSlider mediaRouteVolumeSlider = this.mVolumeSlider;
                    if (isEnabled || isSelected) {
                        z = true;
                    }
                    mediaRouteVolumeSlider.setEnabled(z);
                    this.mItemView.setOnClickListener(this.mViewClickListener);
                    this.mCheckBox.setOnClickListener(this.mViewClickListener);
                    MediaRouteDynamicControllerDialog.setLayoutHeight(this.mVolumeSliderLayout, (!isSelected || this.mRoute.isGroup()) ? this.mCollapsedLayoutHeight : this.mExpandedLayoutHeight);
                    this.mItemView.setAlpha((isEnabled || isSelected) ? 1.0f : this.mDisabledAlpha);
                    CheckBox checkBox = this.mCheckBox;
                    if (!isEnabled && isSelected) {
                        f = this.mDisabledAlpha;
                    }
                    checkBox.setAlpha(f);
                    return;
                }
                this.mCheckBox.setVisibility(8);
                this.mProgressBar.setVisibility(4);
                this.mImageView.setVisibility(0);
                MediaRouteDynamicControllerDialog.setLayoutHeight(this.mVolumeSliderLayout, this.mExpandedLayoutHeight);
                this.mItemView.setAlpha(1.0f);
            }

            /* access modifiers changed from: 0000 */
            public void showSelectingProgress(boolean z, boolean z2) {
                this.mCheckBox.setEnabled(false);
                this.mItemView.setEnabled(false);
                this.mCheckBox.setChecked(z);
                if (z) {
                    this.mImageView.setVisibility(4);
                    this.mProgressBar.setVisibility(0);
                }
                if (z2) {
                    RecyclerAdapter.this.animateLayoutHeight(this.mVolumeSliderLayout, z ? this.mExpandedLayoutHeight : this.mCollapsedLayoutHeight);
                }
            }
        }

        RecyclerAdapter() {
            this.mInflater = LayoutInflater.from(MediaRouteDynamicControllerDialog.this.mContext);
            this.mDefaultIcon = MediaRouterThemeHelper.getDefaultDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mTvIcon = MediaRouterThemeHelper.getTvDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mSpeakerIcon = MediaRouterThemeHelper.getSpeakerDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mSpeakerGroupIcon = MediaRouterThemeHelper.getSpeakerGroupDrawableIcon(MediaRouteDynamicControllerDialog.this.mContext);
            this.mLayoutAnimationDurationMs = MediaRouteDynamicControllerDialog.this.mContext.getResources().getInteger(R$integer.mr_cast_volume_slider_layout_animation_duration_ms);
            this.mAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
            updateItems();
        }

        /* access modifiers changed from: 0000 */
        public boolean isGroupVolumeNeeded() {
            return MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes().size() > 1;
        }

        /* access modifiers changed from: 0000 */
        public void animateLayoutHeight(final View view, final int i) {
            final int i2 = view.getLayoutParams().height;
            C03681 r1 = new Animation(this) {
                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    int i = i;
                    int i2 = i2;
                    MediaRouteDynamicControllerDialog.setLayoutHeight(view, i2 + ((int) (((float) (i - i2)) * f)));
                }
            };
            r1.setAnimationListener(new AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                    MediaRouteDynamicControllerDialog.this.mIsAnimatingVolumeSliderLayout = true;
                }

                public void onAnimationEnd(Animation animation) {
                    MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
                    mediaRouteDynamicControllerDialog.mIsAnimatingVolumeSliderLayout = false;
                    mediaRouteDynamicControllerDialog.updateViewsIfNeeded();
                }
            });
            r1.setDuration((long) this.mLayoutAnimationDurationMs);
            r1.setInterpolator(this.mAccelerateDecelerateInterpolator);
            view.startAnimation(r1);
        }

        /* access modifiers changed from: 0000 */
        public void mayUpdateGroupVolume(RouteInfo routeInfo, boolean z) {
            List memberRoutes = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getMemberRoutes();
            boolean z2 = true;
            int max = Math.max(1, memberRoutes.size());
            int i = -1;
            if (routeInfo.isGroup()) {
                for (RouteInfo contains : routeInfo.getMemberRoutes()) {
                    if (memberRoutes.contains(contains) != z) {
                        max += z ? 1 : -1;
                    }
                }
            } else {
                if (z) {
                    i = 1;
                }
                max += i;
            }
            boolean isGroupVolumeNeeded = isGroupVolumeNeeded();
            int i2 = 0;
            if (max < 2) {
                z2 = false;
            }
            if (isGroupVolumeNeeded != z2) {
                ViewHolder findViewHolderForAdapterPosition = MediaRouteDynamicControllerDialog.this.mRecyclerView.findViewHolderForAdapterPosition(0);
                if (findViewHolderForAdapterPosition instanceof GroupVolumeViewHolder) {
                    GroupVolumeViewHolder groupVolumeViewHolder = (GroupVolumeViewHolder) findViewHolderForAdapterPosition;
                    View view = groupVolumeViewHolder.itemView;
                    if (z2) {
                        i2 = groupVolumeViewHolder.getExpandedHeight();
                    }
                    animateLayoutHeight(view, i2);
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void updateItems() {
            this.mItems.clear();
            this.mGroupVolumeItem = new Item(this, MediaRouteDynamicControllerDialog.this.mSelectedRoute, 1);
            if (!MediaRouteDynamicControllerDialog.this.mMemberRoutes.isEmpty()) {
                for (RouteInfo item : MediaRouteDynamicControllerDialog.this.mMemberRoutes) {
                    this.mItems.add(new Item(this, item, 3));
                }
            } else {
                this.mItems.add(new Item(this, MediaRouteDynamicControllerDialog.this.mSelectedRoute, 3));
            }
            boolean z = false;
            if (!MediaRouteDynamicControllerDialog.this.mGroupableRoutes.isEmpty()) {
                boolean z2 = false;
                for (RouteInfo routeInfo : MediaRouteDynamicControllerDialog.this.mGroupableRoutes) {
                    if (!MediaRouteDynamicControllerDialog.this.mMemberRoutes.contains(routeInfo)) {
                        if (!z2) {
                            DynamicGroupRouteController dynamicGroupController = MediaRouteDynamicControllerDialog.this.mSelectedRoute.getDynamicGroupController();
                            String groupableSelectionTitle = dynamicGroupController != null ? dynamicGroupController.getGroupableSelectionTitle() : null;
                            if (TextUtils.isEmpty(groupableSelectionTitle)) {
                                groupableSelectionTitle = MediaRouteDynamicControllerDialog.this.mContext.getString(R$string.mr_dialog_groupable_header);
                            }
                            this.mItems.add(new Item(this, groupableSelectionTitle, 2));
                            z2 = true;
                        }
                        this.mItems.add(new Item(this, routeInfo, 3));
                    }
                }
            }
            if (!MediaRouteDynamicControllerDialog.this.mTransferableRoutes.isEmpty()) {
                for (RouteInfo routeInfo2 : MediaRouteDynamicControllerDialog.this.mTransferableRoutes) {
                    RouteInfo routeInfo3 = MediaRouteDynamicControllerDialog.this.mSelectedRoute;
                    if (routeInfo3 != routeInfo2) {
                        if (!z) {
                            DynamicGroupRouteController dynamicGroupController2 = routeInfo3.getDynamicGroupController();
                            String transferableSectionTitle = dynamicGroupController2 != null ? dynamicGroupController2.getTransferableSectionTitle() : null;
                            if (TextUtils.isEmpty(transferableSectionTitle)) {
                                transferableSectionTitle = MediaRouteDynamicControllerDialog.this.mContext.getString(R$string.mr_dialog_transferable_header);
                            }
                            this.mItems.add(new Item(this, transferableSectionTitle, 2));
                            z = true;
                        }
                        this.mItems.add(new Item(this, routeInfo2, 4));
                    }
                }
            }
            notifyAdapterDataSetChanged();
        }

        /* access modifiers changed from: 0000 */
        public void notifyAdapterDataSetChanged() {
            MediaRouteDynamicControllerDialog.this.mUngroupableRoutes.clear();
            MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
            mediaRouteDynamicControllerDialog.mUngroupableRoutes.addAll(MediaRouteDialogHelper.getItemsRemoved(mediaRouteDynamicControllerDialog.mGroupableRoutes, mediaRouteDynamicControllerDialog.getCurrentGroupableRoutes()));
            notifyDataSetChanged();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 1) {
                return new GroupVolumeViewHolder(this.mInflater.inflate(R$layout.mr_cast_group_volume_item, viewGroup, false));
            }
            if (i == 2) {
                return new HeaderViewHolder(this, this.mInflater.inflate(R$layout.mr_cast_header_item, viewGroup, false));
            }
            if (i == 3) {
                return new RouteViewHolder(this.mInflater.inflate(R$layout.mr_cast_route_item, viewGroup, false));
            }
            if (i == 4) {
                return new GroupViewHolder(this.mInflater.inflate(R$layout.mr_cast_group_item, viewGroup, false));
            }
            Log.w("MediaRouteCtrlDialog", "Cannot create ViewHolder because of wrong view type");
            return null;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(i);
            Item item = getItem(i);
            if (itemViewType == 1) {
                MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.put(((RouteInfo) item.getData()).getId(), (MediaRouteVolumeSliderHolder) viewHolder);
                ((GroupVolumeViewHolder) viewHolder).bindGroupVolumeViewHolder(item);
            } else if (itemViewType == 2) {
                ((HeaderViewHolder) viewHolder).bindHeaderViewHolder(item);
            } else if (itemViewType == 3) {
                MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.put(((RouteInfo) item.getData()).getId(), (MediaRouteVolumeSliderHolder) viewHolder);
                ((RouteViewHolder) viewHolder).bindRouteViewHolder(item);
            } else if (itemViewType != 4) {
                Log.w("MediaRouteCtrlDialog", "Cannot bind item to ViewHolder because of wrong view type");
            } else {
                ((GroupViewHolder) viewHolder).bindGroupViewHolder(item);
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            super.onViewRecycled(viewHolder);
            MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.values().remove(viewHolder);
        }

        public int getItemCount() {
            return this.mItems.size() + 1;
        }

        /* access modifiers changed from: 0000 */
        public Drawable getIconDrawable(RouteInfo routeInfo) {
            Uri iconUri = routeInfo.getIconUri();
            if (iconUri != null) {
                try {
                    Drawable createFromStream = Drawable.createFromStream(MediaRouteDynamicControllerDialog.this.mContext.getContentResolver().openInputStream(iconUri), null);
                    if (createFromStream != null) {
                        return createFromStream;
                    }
                } catch (IOException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to load ");
                    sb.append(iconUri);
                    Log.w("MediaRouteCtrlDialog", sb.toString(), e);
                }
            }
            return getDefaultIconDrawable(routeInfo);
        }

        private Drawable getDefaultIconDrawable(RouteInfo routeInfo) {
            int deviceType = routeInfo.getDeviceType();
            if (deviceType == 1) {
                return this.mTvIcon;
            }
            if (deviceType == 2) {
                return this.mSpeakerIcon;
            }
            if (routeInfo.isGroup()) {
                return this.mSpeakerGroupIcon;
            }
            return this.mDefaultIcon;
        }

        public int getItemViewType(int i) {
            return getItem(i).getType();
        }

        public Item getItem(int i) {
            if (i == 0) {
                return this.mGroupVolumeItem;
            }
            return (Item) this.mItems.get(i - 1);
        }
    }

    static final class RouteComparator implements Comparator<RouteInfo> {
        static final RouteComparator sInstance = new RouteComparator();

        RouteComparator() {
        }

        public int compare(RouteInfo routeInfo, RouteInfo routeInfo2) {
            return routeInfo.getName().compareToIgnoreCase(routeInfo2.getName());
        }
    }

    private class VolumeChangeListener implements OnSeekBarChangeListener {
        VolumeChangeListener() {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
            if (mediaRouteDynamicControllerDialog.mRouteForVolumeUpdatingByUser != null) {
                mediaRouteDynamicControllerDialog.mHandler.removeMessages(2);
            }
            MediaRouteDynamicControllerDialog.this.mRouteForVolumeUpdatingByUser = (RouteInfo) seekBar.getTag();
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            MediaRouteDynamicControllerDialog.this.mHandler.sendEmptyMessageDelayed(2, 500);
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                RouteInfo routeInfo = (RouteInfo) seekBar.getTag();
                MediaRouteVolumeSliderHolder mediaRouteVolumeSliderHolder = (MediaRouteVolumeSliderHolder) MediaRouteDynamicControllerDialog.this.mVolumeSliderHolderMap.get(routeInfo.getId());
                if (mediaRouteVolumeSliderHolder != null) {
                    mediaRouteVolumeSliderHolder.setMute(i == 0);
                }
                routeInfo.requestSetVolume(i);
            }
        }
    }

    public MediaRouteDynamicControllerDialog(Context context) {
        this(context, 0);
    }

    public MediaRouteDynamicControllerDialog(Context context, int i) {
        Context createThemedDialogContext = MediaRouterThemeHelper.createThemedDialogContext(context, i, false);
        super(createThemedDialogContext, MediaRouterThemeHelper.createThemedDialogStyle(createThemedDialogContext));
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mMemberRoutes = new ArrayList();
        this.mGroupableRoutes = new ArrayList();
        this.mTransferableRoutes = new ArrayList();
        this.mUngroupableRoutes = new ArrayList();
        this.mHandler = new Handler() {
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    MediaRouteDynamicControllerDialog.this.updateRoutesView();
                } else if (i == 2) {
                    MediaRouteDynamicControllerDialog mediaRouteDynamicControllerDialog = MediaRouteDynamicControllerDialog.this;
                    if (mediaRouteDynamicControllerDialog.mRouteForVolumeUpdatingByUser != null) {
                        mediaRouteDynamicControllerDialog.mRouteForVolumeUpdatingByUser = null;
                        mediaRouteDynamicControllerDialog.updateViewsIfNeeded();
                    }
                }
            }
        };
        Context context2 = getContext();
        this.mContext = context2;
        this.mRouter = MediaRouter.getInstance(context2);
        this.mCallback = new MediaRouterCallback();
        this.mSelectedRoute = this.mRouter.getSelectedRoute();
        this.mControllerCallback = new MediaControllerCallback();
        setMediaSession(this.mRouter.getMediaSessionToken());
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
            reloadIconIfNeeded();
            updateMetadataViews();
        }
    }

    public void setRouteSelector(MediaRouteSelector mediaRouteSelector) {
        if (mediaRouteSelector == null) {
            throw new IllegalArgumentException("selector must not be null");
        } else if (!this.mSelector.equals(mediaRouteSelector)) {
            this.mSelector = mediaRouteSelector;
            if (this.mAttachedToWindow) {
                this.mRouter.removeCallback(this.mCallback);
                this.mRouter.addCallback(mediaRouteSelector, this.mCallback, 1);
                updateRoutes();
            }
        }
    }

    public void onFilterRoutes(List<RouteInfo> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            if (!onFilterRoute((RouteInfo) list.get(size))) {
                list.remove(size);
            }
        }
    }

    public boolean onFilterRoute(RouteInfo routeInfo) {
        return !routeInfo.isDefaultOrBluetooth() && routeInfo.isEnabled() && routeInfo.matchesSelector(this.mSelector) && this.mSelectedRoute != routeInfo;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.mr_cast_dialog);
        MediaRouterThemeHelper.setDialogBackgroundColor(this.mContext, this);
        ImageButton imageButton = (ImageButton) findViewById(R$id.mr_cast_close_button);
        this.mCloseButton = imageButton;
        imageButton.setColorFilter(-1);
        this.mCloseButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MediaRouteDynamicControllerDialog.this.dismiss();
            }
        });
        Button button = (Button) findViewById(R$id.mr_cast_stop_button);
        this.mStopCastingButton = button;
        button.setTextColor(-1);
        this.mStopCastingButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (MediaRouteDynamicControllerDialog.this.mSelectedRoute.isSelected()) {
                    MediaRouteDynamicControllerDialog.this.mRouter.unselect(2);
                }
                MediaRouteDynamicControllerDialog.this.dismiss();
            }
        });
        this.mAdapter = new RecyclerAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R$id.mr_cast_list);
        this.mRecyclerView = recyclerView;
        recyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.mVolumeChangeListener = new VolumeChangeListener();
        this.mVolumeSliderHolderMap = new HashMap();
        this.mUnmutedVolumeMap = new HashMap();
        this.mMetadataBackground = (ImageView) findViewById(R$id.mr_cast_meta_background);
        this.mMetadataBlackScrim = findViewById(R$id.mr_cast_meta_black_scrim);
        this.mArtView = (ImageView) findViewById(R$id.mr_cast_meta_art);
        TextView textView = (TextView) findViewById(R$id.mr_cast_meta_title);
        this.mTitleView = textView;
        textView.setTextColor(-1);
        TextView textView2 = (TextView) findViewById(R$id.mr_cast_meta_subtitle);
        this.mSubtitleView = textView2;
        textView2.setTextColor(-1);
        this.mTitlePlaceholder = this.mContext.getResources().getString(R$string.mr_cast_dialog_title_view_placeholder);
        this.mCreated = true;
        updateLayout();
    }

    /* access modifiers changed from: 0000 */
    public void updateLayout() {
        getWindow().setLayout(MediaRouteDialogHelper.getDialogWidthForDynamicGroup(this.mContext), MediaRouteDialogHelper.getDialogHeight(this.mContext));
        this.mArtIconBitmap = null;
        this.mArtIconUri = null;
        reloadIconIfNeeded();
        updateMetadataViews();
        updateRoutesView();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(this.mSelector, this.mCallback, 1);
        updateRoutes();
        setMediaSession(this.mRouter.getMediaSessionToken());
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttachedToWindow = false;
        this.mRouter.removeCallback(this.mCallback);
        this.mHandler.removeCallbacksAndMessages(null);
        setMediaSession(null);
    }

    static boolean isBitmapRecycled(Bitmap bitmap) {
        return bitmap != null && bitmap.isRecycled();
    }

    /* access modifiers changed from: 0000 */
    public void reloadIconIfNeeded() {
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
        if (iconBitmap2 != iconBitmap || (iconBitmap2 == null && !ObjectsCompat.equals(iconUri, uri))) {
            FetchArtTask fetchArtTask3 = this.mFetchArtTask;
            if (fetchArtTask3 != null) {
                fetchArtTask3.cancel(true);
            }
            FetchArtTask fetchArtTask4 = new FetchArtTask();
            this.mFetchArtTask = fetchArtTask4;
            fetchArtTask4.execute(new Void[0]);
        }
    }

    /* access modifiers changed from: 0000 */
    public void clearLoadedBitmap() {
        this.mArtIconIsLoaded = false;
        this.mArtIconLoadedBitmap = null;
        this.mArtIconBackgroundColor = 0;
    }

    private boolean shouldDeferUpdateViews() {
        if (this.mRouteForVolumeUpdatingByUser != null || this.mIsSelectingRoute || this.mIsAnimatingVolumeSliderLayout) {
            return true;
        }
        return !this.mCreated;
    }

    /* access modifiers changed from: 0000 */
    public void updateViewsIfNeeded() {
        if (this.mUpdateRoutesViewDeferred) {
            updateRoutesView();
        }
        if (this.mUpdateMetadataViewsDeferred) {
            updateMetadataViews();
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateMetadataViews() {
        if (shouldDeferUpdateViews()) {
            this.mUpdateMetadataViewsDeferred = true;
            return;
        }
        this.mUpdateMetadataViewsDeferred = false;
        if (!this.mSelectedRoute.isSelected() || this.mSelectedRoute.isDefaultOrBluetooth()) {
            dismiss();
        }
        CharSequence charSequence = null;
        if (!this.mArtIconIsLoaded || isBitmapRecycled(this.mArtIconLoadedBitmap) || this.mArtIconLoadedBitmap == null) {
            if (isBitmapRecycled(this.mArtIconLoadedBitmap)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Can't set artwork image with recycled bitmap: ");
                sb.append(this.mArtIconLoadedBitmap);
                Log.w("MediaRouteCtrlDialog", sb.toString());
            }
            this.mArtView.setVisibility(8);
            this.mMetadataBlackScrim.setVisibility(8);
            this.mMetadataBackground.setImageBitmap(null);
        } else {
            this.mArtView.setVisibility(0);
            this.mArtView.setImageBitmap(this.mArtIconLoadedBitmap);
            this.mArtView.setBackgroundColor(this.mArtIconBackgroundColor);
            this.mMetadataBlackScrim.setVisibility(0);
            if (VERSION.SDK_INT >= 17) {
                Bitmap bitmap = this.mArtIconLoadedBitmap;
                blurBitmap(bitmap, 10.0f, this.mContext);
                this.mMetadataBackground.setImageBitmap(bitmap);
            } else {
                this.mMetadataBackground.setImageBitmap(Bitmap.createBitmap(this.mArtIconLoadedBitmap));
            }
        }
        clearLoadedBitmap();
        MediaDescriptionCompat mediaDescriptionCompat = this.mDescription;
        CharSequence title = mediaDescriptionCompat == null ? null : mediaDescriptionCompat.getTitle();
        boolean z = !TextUtils.isEmpty(title);
        MediaDescriptionCompat mediaDescriptionCompat2 = this.mDescription;
        if (mediaDescriptionCompat2 != null) {
            charSequence = mediaDescriptionCompat2.getSubtitle();
        }
        boolean isEmpty = true ^ TextUtils.isEmpty(charSequence);
        if (z) {
            this.mTitleView.setText(title);
        } else {
            this.mTitleView.setText(this.mTitlePlaceholder);
        }
        if (isEmpty) {
            this.mSubtitleView.setText(charSequence);
            this.mSubtitleView.setVisibility(0);
        } else {
            this.mSubtitleView.setVisibility(8);
        }
    }

    static void setLayoutHeight(View view, int i) {
        LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }

    /* access modifiers changed from: 0000 */
    public List<RouteInfo> getCurrentGroupableRoutes() {
        ArrayList arrayList = new ArrayList();
        if (this.mSelectedRoute.getDynamicGroupState() != null) {
            for (RouteInfo routeInfo : this.mSelectedRoute.getProvider().getRoutes()) {
                DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                if (dynamicGroupState != null && dynamicGroupState.isGroupable()) {
                    arrayList.add(routeInfo);
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public void updateRoutesView() {
        if (this.mAttachedToWindow) {
            if (SystemClock.uptimeMillis() - this.mLastUpdateTime < 300) {
                this.mHandler.removeMessages(1);
                this.mHandler.sendEmptyMessageAtTime(1, this.mLastUpdateTime + 300);
            } else if (shouldDeferUpdateViews()) {
                this.mUpdateRoutesViewDeferred = true;
            } else {
                this.mUpdateRoutesViewDeferred = false;
                if (!this.mSelectedRoute.isSelected() || this.mSelectedRoute.isDefaultOrBluetooth()) {
                    dismiss();
                }
                this.mLastUpdateTime = SystemClock.uptimeMillis();
                this.mAdapter.notifyAdapterDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateRoutes() {
        this.mMemberRoutes.clear();
        this.mGroupableRoutes.clear();
        this.mTransferableRoutes.clear();
        this.mMemberRoutes.addAll(this.mSelectedRoute.getMemberRoutes());
        if (this.mSelectedRoute.getDynamicGroupState() != null) {
            for (RouteInfo routeInfo : this.mSelectedRoute.getProvider().getRoutes()) {
                DynamicGroupState dynamicGroupState = routeInfo.getDynamicGroupState();
                if (dynamicGroupState != null) {
                    if (dynamicGroupState.isGroupable()) {
                        this.mGroupableRoutes.add(routeInfo);
                    }
                    if (dynamicGroupState.isTransferable()) {
                        this.mTransferableRoutes.add(routeInfo);
                    }
                }
            }
        }
        onFilterRoutes(this.mGroupableRoutes);
        onFilterRoutes(this.mTransferableRoutes);
        Collections.sort(this.mMemberRoutes, RouteComparator.sInstance);
        Collections.sort(this.mGroupableRoutes, RouteComparator.sInstance);
        Collections.sort(this.mTransferableRoutes, RouteComparator.sInstance);
        this.mAdapter.updateItems();
    }

    private static Bitmap blurBitmap(Bitmap bitmap, float f, Context context) {
        RenderScript create = RenderScript.create(context);
        Allocation createFromBitmap = Allocation.createFromBitmap(create, bitmap);
        Allocation createTyped = Allocation.createTyped(create, createFromBitmap.getType());
        ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        create2.setRadius(f);
        create2.setInput(createFromBitmap);
        create2.forEach(createTyped);
        createTyped.copyTo(bitmap);
        createFromBitmap.destroy();
        createTyped.destroy();
        create2.destroy();
        create.destroy();
        return bitmap;
    }
}
