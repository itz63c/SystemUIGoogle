package com.android.systemui.media;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.MediaSession.Token;
import android.media.session.PlaybackState;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.android.settingslib.media.MediaDevice;
import com.android.settingslib.widget.AdaptiveIcon;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationMediaManager.MediaListener;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaControlPanel implements MediaListener {
    protected static final int[] NOTIF_ACTION_IDS = {16908691, 16908692, 16908693, 16908694, 16908695};
    private final int[] mActionIds;
    private int mBackgroundColor;
    private final Executor mBackgroundExecutor;
    private Context mContext;
    /* access modifiers changed from: private */
    public MediaController mController;
    private int mForegroundColor;
    private final Executor mForegroundExecutor;
    private final NotificationMediaManager mMediaManager;
    protected LinearLayout mMediaNotifView;
    protected ComponentName mRecvComponent;
    private View mSeamless;
    /* access modifiers changed from: private */
    public Callback mSessionCallback = new Callback() {
        public void onSessionDestroyed() {
            Log.d("MediaControlPanel", "session destroyed");
            MediaControlPanel.this.mController.unregisterCallback(MediaControlPanel.this.mSessionCallback);
            MediaControlPanel.this.clearControls();
        }
    };
    private Token mToken;

    public MediaControlPanel(Context context, ViewGroup viewGroup, NotificationMediaManager notificationMediaManager, int i, int[] iArr, Executor executor, Executor executor2) {
        this.mContext = context;
        this.mMediaNotifView = (LinearLayout) LayoutInflater.from(context).inflate(i, viewGroup, false);
        this.mMediaManager = notificationMediaManager;
        this.mActionIds = iArr;
        this.mForegroundExecutor = executor;
        this.mBackgroundExecutor = executor2;
    }

    public View getView() {
        return this.mMediaNotifView;
    }

    public Context getContext() {
        return this.mContext;
    }

    public void setMediaSession(Token token, Icon icon, int i, int i2, PendingIntent pendingIntent, String str, MediaDevice mediaDevice) {
        this.mToken = token;
        this.mForegroundColor = i;
        this.mBackgroundColor = i2;
        MediaController mediaController = new MediaController(this.mContext, this.mToken);
        this.mController = mediaController;
        MediaMetadata metadata = mediaController.getMetadata();
        List<ResolveInfo> queryBroadcastReceiversAsUser = this.mContext.getPackageManager().queryBroadcastReceiversAsUser(new Intent("android.intent.action.MEDIA_BUTTON"), 0, this.mContext.getUser());
        if (queryBroadcastReceiversAsUser != null) {
            for (ResolveInfo resolveInfo : queryBroadcastReceiversAsUser) {
                if (resolveInfo.activityInfo.packageName.equals(this.mController.getPackageName())) {
                    this.mRecvComponent = resolveInfo.getComponentInfo().getComponentName();
                }
            }
        }
        this.mController.registerCallback(this.mSessionCallback);
        if (metadata == null) {
            Log.e("MediaControlPanel", "Media metadata was null");
            return;
        }
        ImageView imageView = (ImageView) this.mMediaNotifView.findViewById(C2011R$id.album_art);
        if (imageView != null) {
            this.mBackgroundExecutor.execute(new Runnable(metadata, imageView) {
                public final /* synthetic */ MediaMetadata f$1;
                public final /* synthetic */ ImageView f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaControlPanel.this.lambda$setMediaSession$0$MediaControlPanel(this.f$1, this.f$2);
                }
            });
        }
        this.mMediaNotifView.setBackgroundTintList(ColorStateList.valueOf(this.mBackgroundColor));
        if (pendingIntent != null) {
            this.mMediaNotifView.setOnClickListener(new OnClickListener(pendingIntent) {
                public final /* synthetic */ PendingIntent f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    MediaControlPanel.this.lambda$setMediaSession$1$MediaControlPanel(this.f$1, view);
                }
            });
        }
        ImageView imageView2 = (ImageView) this.mMediaNotifView.findViewById(C2011R$id.icon);
        Drawable loadDrawable = icon.loadDrawable(this.mContext);
        loadDrawable.setTint(this.mForegroundColor);
        imageView2.setImageDrawable(loadDrawable);
        TextView textView = (TextView) this.mMediaNotifView.findViewById(C2011R$id.header_title);
        textView.setText(metadata.getString("android.media.metadata.TITLE"));
        textView.setTextColor(this.mForegroundColor);
        TextView textView2 = (TextView) this.mMediaNotifView.findViewById(C2011R$id.app_name);
        if (textView2 != null) {
            textView2.setText(str);
            textView2.setTextColor(this.mForegroundColor);
        }
        TextView textView3 = (TextView) this.mMediaNotifView.findViewById(C2011R$id.header_artist);
        if (textView3 != null) {
            textView3.setText(metadata.getString("android.media.metadata.ARTIST"));
            textView3.setTextColor(this.mForegroundColor);
        }
        View findViewById = this.mMediaNotifView.findViewById(C2011R$id.media_seamless);
        this.mSeamless = findViewById;
        if (findViewById != null) {
            findViewById.setVisibility(0);
            updateDevice(mediaDevice);
            this.mSeamless.setOnClickListener(new OnClickListener((ActivityStarter) Dependency.get(ActivityStarter.class)) {
                public final /* synthetic */ ActivityStarter f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    MediaControlPanel.this.lambda$setMediaSession$2$MediaControlPanel(this.f$1, view);
                }
            });
        }
        this.mMediaManager.removeCallback(this);
        this.mMediaManager.addCallback(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMediaSession$1 */
    public /* synthetic */ void lambda$setMediaSession$1$MediaControlPanel(PendingIntent pendingIntent, View view) {
        try {
            pendingIntent.send();
            this.mContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        } catch (CanceledException e) {
            Log.e("MediaControlPanel", "Pending intent was canceled", e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMediaSession$2 */
    public /* synthetic */ void lambda$setMediaSession$2$MediaControlPanel(ActivityStarter activityStarter, View view) {
        String str = "key_media_session_token";
        activityStarter.startActivity(new Intent().setAction("com.android.settings.panel.action.MEDIA_OUTPUT").putExtra("com.android.settings.panel.extra.PACKAGE_NAME", this.mController.getPackageName()).putExtra(str, this.mToken), false, true, 268468224);
    }

    public Token getMediaSessionToken() {
        return this.mToken;
    }

    public MediaController getController() {
        return this.mController;
    }

    public String getMediaPlayerPackage() {
        return this.mController.getPackageName();
    }

    public boolean hasMediaSession() {
        MediaController mediaController = this.mController;
        return (mediaController == null || mediaController.getPlaybackState() == null) ? false : true;
    }

    public boolean isPlaying() {
        return isPlaying(this.mController);
    }

    /* access modifiers changed from: protected */
    public boolean isPlaying(MediaController mediaController) {
        boolean z = false;
        if (mediaController == null) {
            return false;
        }
        PlaybackState playbackState = mediaController.getPlaybackState();
        if (playbackState == null) {
            return false;
        }
        if (playbackState.getState() == 3) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: private */
    /* renamed from: processAlbumArt */
    public void lambda$setMediaSession$0(MediaMetadata mediaMetadata, ImageView imageView) {
        RoundedBitmapDrawable roundedBitmapDrawable;
        Bitmap bitmap = mediaMetadata.getBitmap("android.media.metadata.ALBUM_ART");
        float dimension = this.mContext.getResources().getDimension(C2009R$dimen.qs_media_corner_radius);
        if (bitmap != null) {
            int dimension2 = (int) this.mContext.getResources().getDimension(C2009R$dimen.qs_media_album_size);
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(this.mContext.getResources(), Bitmap.createScaledBitmap(bitmap.copy(Config.ARGB_8888, true), dimension2, dimension2, false));
            roundedBitmapDrawable.setCornerRadius(dimension);
        } else {
            Log.e("MediaControlPanel", "No album art available");
            roundedBitmapDrawable = null;
        }
        this.mForegroundExecutor.execute(new Runnable(imageView) {
            public final /* synthetic */ ImageView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaControlPanel.lambda$processAlbumArt$3(RoundedBitmapDrawable.this, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$processAlbumArt$3(RoundedBitmapDrawable roundedBitmapDrawable, ImageView imageView) {
        if (roundedBitmapDrawable != null) {
            imageView.setImageDrawable(roundedBitmapDrawable);
            imageView.setVisibility(0);
            return;
        }
        imageView.setImageDrawable(null);
        imageView.setVisibility(8);
    }

    public void updateDevice(MediaDevice mediaDevice) {
        if (this.mSeamless != null) {
            this.mForegroundExecutor.execute(new Runnable(mediaDevice) {
                public final /* synthetic */ MediaDevice f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaControlPanel.this.lambda$updateDevice$4$MediaControlPanel(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updateChipInternal */
    public void lambda$updateDevice$4(MediaDevice mediaDevice) {
        ColorStateList valueOf = ColorStateList.valueOf(this.mForegroundColor);
        GradientDrawable gradientDrawable = (GradientDrawable) ((RippleDrawable) ((LinearLayout) this.mSeamless).getBackground()).getDrawable(0);
        gradientDrawable.setStroke(2, this.mForegroundColor);
        gradientDrawable.setColor(this.mBackgroundColor);
        ImageView imageView = (ImageView) this.mSeamless.findViewById(C2011R$id.media_seamless_image);
        TextView textView = (TextView) this.mSeamless.findViewById(C2011R$id.media_seamless_text);
        textView.setTextColor(valueOf);
        if (mediaDevice != null) {
            Drawable icon = mediaDevice.getIcon();
            imageView.setVisibility(0);
            imageView.setImageTintList(valueOf);
            if (icon instanceof AdaptiveIcon) {
                AdaptiveIcon adaptiveIcon = (AdaptiveIcon) icon;
                adaptiveIcon.setBackgroundColor(this.mBackgroundColor);
                imageView.setImageDrawable(adaptiveIcon);
            } else {
                imageView.setImageDrawable(icon);
            }
            textView.setText(mediaDevice.getName());
            return;
        }
        imageView.setVisibility(8);
        textView.setText(17040018);
    }

    public void clearControls() {
        int i = 0;
        while (true) {
            int[] iArr = this.mActionIds;
            if (i < iArr.length) {
                ImageButton imageButton = (ImageButton) this.mMediaNotifView.findViewById(iArr[i]);
                if (imageButton != null) {
                    imageButton.setVisibility(8);
                }
                i++;
            } else {
                ImageButton imageButton2 = (ImageButton) this.mMediaNotifView.findViewById(iArr[0]);
                imageButton2.setOnClickListener(new OnClickListener() {
                    public final void onClick(View view) {
                        MediaControlPanel.this.lambda$clearControls$5$MediaControlPanel(view);
                    }
                });
                imageButton2.setImageDrawable(this.mContext.getResources().getDrawable(C2010R$drawable.lb_ic_play));
                imageButton2.setImageTintList(ColorStateList.valueOf(this.mForegroundColor));
                imageButton2.setVisibility(0);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$clearControls$5 */
    public /* synthetic */ void lambda$clearControls$5$MediaControlPanel(View view) {
        String str = "MediaControlPanel";
        Log.d(str, "Attempting to restart session");
        if (this.mRecvComponent != null) {
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            intent.setComponent(this.mRecvComponent);
            intent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(0, 126));
            this.mContext.sendBroadcast(intent);
        } else if (this.mController.getSessionActivity() != null) {
            try {
                this.mController.getSessionActivity().send();
            } catch (CanceledException e) {
                Log.e(str, "Pending intent was canceled", e);
            }
        } else {
            Log.e(str, "No receiver or activity to restart");
        }
    }

    public void onMetadataOrStateChanged(MediaMetadata mediaMetadata, int i) {
        if (i == 0) {
            clearControls();
            this.mMediaManager.removeCallback(this);
        }
    }
}
