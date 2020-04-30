package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingController.RecordingStateChangeCallback;

/* renamed from: com.android.systemui.qs.tiles.ScreenRecordTile */
public class ScreenRecordTile extends QSTileImpl<BooleanState> implements RecordingStateChangeCallback {
    private Callback mCallback;
    private RecordingController mController;
    /* access modifiers changed from: private */
    public long mMillisUntilFinished = 0;

    /* renamed from: com.android.systemui.qs.tiles.ScreenRecordTile$Callback */
    private final class Callback implements RecordingStateChangeCallback {
        private Callback() {
        }

        public void onCountdown(long j) {
            ScreenRecordTile.this.mMillisUntilFinished = j;
            ScreenRecordTile.this.refreshState();
        }

        public void onCountdownEnd() {
            ScreenRecordTile.this.refreshState();
        }

        public void onRecordingStart() {
            ScreenRecordTile.this.refreshState();
        }

        public void onRecordingEnd() {
            ScreenRecordTile.this.refreshState();
        }
    }

    public Intent getLongClickIntent() {
        return null;
    }

    public int getMetricsCategory() {
        return 0;
    }

    public ScreenRecordTile(QSHost qSHost, RecordingController recordingController) {
        super(qSHost);
        Callback callback = new Callback();
        this.mCallback = callback;
        this.mController = recordingController;
        recordingController.observe((LifecycleOwner) this, callback);
    }

    public BooleanState newTileState() {
        BooleanState booleanState = new BooleanState();
        booleanState.label = this.mContext.getString(C2017R$string.quick_settings_screen_record_label);
        booleanState.handlesLongClick = false;
        return booleanState;
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        if (this.mController.isStarting()) {
            cancelCountdown();
        } else if (this.mController.isRecording()) {
            stopRecording();
        } else {
            startCountdown();
        }
        refreshState();
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        boolean isStarting = this.mController.isStarting();
        boolean isRecording = this.mController.isRecording();
        booleanState.value = isRecording || isStarting;
        booleanState.state = (isRecording || isStarting) ? 2 : 1;
        if (isRecording) {
            booleanState.icon = ResourceIcon.get(C2010R$drawable.ic_qs_screenrecord);
            booleanState.secondaryLabel = this.mContext.getString(C2017R$string.quick_settings_screen_record_stop);
        } else if (isStarting) {
            int floorDiv = (int) Math.floorDiv(this.mMillisUntilFinished + 500, 1000);
            booleanState.icon = ResourceIcon.get(C2010R$drawable.ic_qs_screenrecord);
            booleanState.secondaryLabel = String.format("%d...", new Object[]{Integer.valueOf(floorDiv)});
        } else {
            booleanState.icon = ResourceIcon.get(C2010R$drawable.ic_qs_screenrecord);
            booleanState.secondaryLabel = this.mContext.getString(C2017R$string.quick_settings_screen_record_start);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_screen_record_label);
    }

    private void startCountdown() {
        Log.d("ScreenRecordTile", "Starting countdown");
        getHost().collapsePanels();
        this.mController.launchRecordPrompt();
    }

    private void cancelCountdown() {
        Log.d("ScreenRecordTile", "Cancelling countdown");
        this.mController.cancelCountdown();
    }

    private void stopRecording() {
        Log.d("ScreenRecordTile", "Stopping recording from tile");
        this.mController.stopRecording();
    }
}
