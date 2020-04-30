package com.android.systemui.screenrecord;

import android.app.Activity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;

public class ScreenRecordDialog extends Activity {
    private Switch mAudioSwitch;
    private final RecordingController mController;
    private Switch mTapsSwitch;

    public ScreenRecordDialog(RecordingController recordingController) {
        this.mController = recordingController;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.getDecorView();
        window.setLayout(-1, -2);
        window.setGravity(48);
        setContentView(C2013R$layout.screen_record_dialog);
        ((Button) findViewById(C2011R$id.button_cancel)).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                ScreenRecordDialog.this.lambda$onCreate$0$ScreenRecordDialog(view);
            }
        });
        ((Button) findViewById(C2011R$id.button_start)).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                ScreenRecordDialog.this.lambda$onCreate$1$ScreenRecordDialog(view);
            }
        });
        this.mAudioSwitch = (Switch) findViewById(C2011R$id.screenrecord_audio_switch);
        this.mTapsSwitch = (Switch) findViewById(C2011R$id.screenrecord_taps_switch);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$ScreenRecordDialog(View view) {
        finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ void lambda$onCreate$1$ScreenRecordDialog(View view) {
        requestScreenCapture();
        finish();
    }

    private void requestScreenCapture() {
        this.mController.startCountdown(3000, 1000, PendingIntent.getForegroundService(this, 2, RecordingService.getStartIntent(this, -1, null, this.mAudioSwitch.isChecked(), this.mTapsSwitch.isChecked()), 134217728), PendingIntent.getService(this, 2, RecordingService.getStopIntent(this), 134217728));
    }
}
