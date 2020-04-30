package com.android.systemui.p007qs;

import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession.Token;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.media.MediaControlPanel;
import com.android.systemui.statusbar.NotificationMediaManager;
import java.util.concurrent.Executor;

/* renamed from: com.android.systemui.qs.QSMediaPlayer */
public class QSMediaPlayer extends MediaControlPanel {
    static final int[] QS_ACTION_IDS = {C2011R$id.action0, C2011R$id.action1, C2011R$id.action2, C2011R$id.action3, C2011R$id.action4};
    private final QSPanel mParent;

    public QSMediaPlayer(Context context, ViewGroup viewGroup, NotificationMediaManager notificationMediaManager, Executor executor, Executor executor2) {
        super(context, viewGroup, notificationMediaManager, C2013R$layout.qs_media_panel, QS_ACTION_IDS, executor, executor2);
        this.mParent = (QSPanel) viewGroup;
    }

    public void setMediaSession(Token token, Icon icon, int i, int i2, View view, Notification notification, MediaDevice mediaDevice) {
        int i3 = i;
        Notification notification2 = notification;
        int[] iArr = QS_ACTION_IDS;
        String loadHeaderAppName = Builder.recoverBuilder(getContext(), notification2).loadHeaderAppName();
        super.setMediaSession(token, icon, i, i2, notification2.contentIntent, loadHeaderAppName, mediaDevice);
        LinearLayout linearLayout = (LinearLayout) view;
        int i4 = 0;
        while (i4 < linearLayout.getChildCount() && i4 < iArr.length) {
            ImageButton imageButton = (ImageButton) this.mMediaNotifView.findViewById(iArr[i4]);
            ImageButton imageButton2 = (ImageButton) linearLayout.findViewById(MediaControlPanel.NOTIF_ACTION_IDS[i4]);
            if (imageButton2 == null || imageButton2.getDrawable() == null || imageButton2.getVisibility() != 0) {
                imageButton.setVisibility(8);
            } else {
                imageButton.setImageDrawable(imageButton2.getDrawable().mutate());
                imageButton.setVisibility(0);
                imageButton.setOnClickListener(new OnClickListener(imageButton2) {
                    public final /* synthetic */ ImageButton f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        QSMediaPlayer.lambda$setMediaSession$0(this.f$0, view);
                    }
                });
            }
            i4++;
        }
        while (i4 < iArr.length) {
            ((ImageButton) this.mMediaNotifView.findViewById(iArr[i4])).setVisibility(8);
            i4++;
        }
        View findViewById = this.mMediaNotifView.findViewById(C2011R$id.media_guts);
        View findViewById2 = this.mMediaNotifView.findViewById(C2011R$id.qs_media_controls_options);
        findViewById2.setMinimumHeight(findViewById.getHeight());
        findViewById2.findViewById(C2011R$id.remove).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                QSMediaPlayer.this.lambda$setMediaSession$1$QSMediaPlayer(view);
            }
        });
        ((ImageView) findViewById2.findViewById(C2011R$id.remove_icon)).setImageTintList(ColorStateList.valueOf(i));
        ((TextView) findViewById2.findViewById(C2011R$id.remove_text)).setTextColor(i);
        TextView textView = (TextView) findViewById2.findViewById(C2011R$id.cancel);
        textView.setTextColor(i);
        textView.setOnClickListener(new OnClickListener(findViewById2, findViewById) {
            public final /* synthetic */ View f$0;
            public final /* synthetic */ View f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                QSMediaPlayer.lambda$setMediaSession$2(this.f$0, this.f$1, view);
            }
        });
        this.mMediaNotifView.setOnLongClickListener(null);
        findViewById2.setVisibility(8);
        findViewById.setVisibility(0);
    }

    static /* synthetic */ void lambda$setMediaSession$0(ImageButton imageButton, View view) {
        Log.d("QSMediaPlayer", "clicking on other button");
        imageButton.performClick();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMediaSession$1 */
    public /* synthetic */ void lambda$setMediaSession$1$QSMediaPlayer(View view) {
        this.mParent.removeMediaPlayer(this);
    }

    static /* synthetic */ void lambda$setMediaSession$2(View view, View view2, View view3) {
        view.setVisibility(8);
        view2.setVisibility(0);
    }

    public void clearControls() {
        super.clearControls();
        this.mMediaNotifView.setOnLongClickListener(new OnLongClickListener(this.mMediaNotifView.findViewById(C2011R$id.media_guts), this.mMediaNotifView.findViewById(C2011R$id.qs_media_controls_options)) {
            public final /* synthetic */ View f$0;
            public final /* synthetic */ View f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean onLongClick(View view) {
                return QSMediaPlayer.lambda$clearControls$3(this.f$0, this.f$1, view);
            }
        });
    }

    static /* synthetic */ boolean lambda$clearControls$3(View view, View view2, View view3) {
        view.setVisibility(8);
        view2.setVisibility(0);
        return true;
    }
}
