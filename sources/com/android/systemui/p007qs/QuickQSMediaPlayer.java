package com.android.systemui.p007qs;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.media.session.MediaController;
import android.media.session.MediaSession.Token;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.media.MediaControlPanel;
import com.android.systemui.statusbar.NotificationMediaManager;
import java.util.concurrent.Executor;

/* renamed from: com.android.systemui.qs.QuickQSMediaPlayer */
public class QuickQSMediaPlayer extends MediaControlPanel {
    private static final int[] QQS_ACTION_IDS = {C2011R$id.action0, C2011R$id.action1, C2011R$id.action2};

    public QuickQSMediaPlayer(Context context, ViewGroup viewGroup, NotificationMediaManager notificationMediaManager, Executor executor, Executor executor2) {
        super(context, viewGroup, notificationMediaManager, C2013R$layout.qqs_media_panel, QQS_ACTION_IDS, executor, executor2);
    }

    public void setMediaSession(Token token, Icon icon, int i, int i2, View view, int[] iArr, PendingIntent pendingIntent) {
        Token token2 = token;
        int[] iArr2 = iArr;
        int[] iArr3 = QQS_ACTION_IDS;
        String packageName = getController() != null ? getController().getPackageName() : "";
        MediaController mediaController = new MediaController(getContext(), token);
        Token mediaSessionToken = getMediaSessionToken();
        int i3 = 0;
        boolean z = mediaSessionToken != null && mediaSessionToken.equals(token) && packageName.equals(mediaController.getPackageName());
        if (getController() == null || z || isPlaying(mediaController)) {
            super.setMediaSession(token, icon, i, i2, pendingIntent, null, null);
            LinearLayout linearLayout = (LinearLayout) view;
            if (iArr2 != null) {
                int min = Math.min(Math.min(iArr2.length, linearLayout.getChildCount()), iArr3.length);
                int i4 = 0;
                while (i4 < min) {
                    ImageButton imageButton = (ImageButton) this.mMediaNotifView.findViewById(iArr3[i4]);
                    ImageButton imageButton2 = (ImageButton) linearLayout.findViewById(MediaControlPanel.NOTIF_ACTION_IDS[iArr2[i4]]);
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
                                this.f$0.performClick();
                            }
                        });
                    }
                    i4++;
                }
                i3 = i4;
            }
            while (i3 < iArr3.length) {
                ((ImageButton) this.mMediaNotifView.findViewById(iArr3[i3])).setVisibility(8);
                i3++;
            }
        }
    }
}
