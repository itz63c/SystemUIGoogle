package com.google.android.systemui.elmyra.sensors;

import android.content.Context;
import android.hardware.location.ContextHubClient;
import android.hardware.location.ContextHubClientCallback;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.ContextHubManager;
import android.hardware.location.NanoAppMessage;
import android.util.Log;
import android.util.TypedValue;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Dumpable;
import com.google.android.systemui.elmyra.SnapshotConfiguration;
import com.google.android.systemui.elmyra.SnapshotController.Listener;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$GestureDetected;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$GestureProgress;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$RecognizerStart;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$SensitivityUpdate;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$SnapshotHeader;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class CHREGestureSensor implements Dumpable, GestureSensor {
    private final Context mContext;
    private ContextHubClient mContextHubClient;
    private final ContextHubClientCallback mContextHubClientCallback = new ContextHubClientCallback() {
        public void onMessageFromNanoApp(ContextHubClient contextHubClient, NanoAppMessage nanoAppMessage) {
            String str = "Elmyra/GestureSensor";
            if (nanoAppMessage.getNanoAppId() == 5147455389092024334L) {
                try {
                    int messageType = nanoAppMessage.getMessageType();
                    if (messageType != 1) {
                        switch (messageType) {
                            case 300:
                                CHREGestureSensor.this.mController.onGestureProgress(ContextHubMessages$GestureProgress.parseFrom(nanoAppMessage.getMessageBody()).progress);
                                break;
                            case 301:
                                ContextHubMessages$GestureDetected parseFrom = ContextHubMessages$GestureDetected.parseFrom(nanoAppMessage.getMessageBody());
                                CHREGestureSensor.this.mController.onGestureDetected(new DetectionProperties(parseFrom.hapticConsumed, parseFrom.hostSuspended));
                                break;
                            case 302:
                                SnapshotProtos$Snapshot parseFrom2 = SnapshotProtos$Snapshot.parseFrom(nanoAppMessage.getMessageBody());
                                parseFrom2.sensitivitySetting = CHREGestureSensor.this.mGestureConfiguration.getSensitivity();
                                CHREGestureSensor.this.mController.onSnapshotReceived(parseFrom2);
                                break;
                            case 303:
                                CHREGestureSensor.this.mController.storeChassisConfiguration(ChassisProtos$Chassis.parseFrom(nanoAppMessage.getMessageBody()));
                                break;
                            case 304:
                            case 305:
                                break;
                            default:
                                StringBuilder sb = new StringBuilder();
                                sb.append("Unknown message type: ");
                                sb.append(nanoAppMessage.getMessageType());
                                Log.e(str, sb.toString());
                                break;
                        }
                    } else if (CHREGestureSensor.this.mIsListening) {
                        CHREGestureSensor.this.startRecognizer();
                    }
                } catch (InvalidProtocolBufferNanoException e) {
                    Log.e(str, "Invalid protocol buffer", e);
                }
            }
        }

        public void onHubReset(ContextHubClient contextHubClient) {
            StringBuilder sb = new StringBuilder();
            sb.append("HubReset: ");
            sb.append(contextHubClient.getAttachedHub().getId());
            Log.d("Elmyra/GestureSensor", sb.toString());
        }

        public void onNanoAppAborted(ContextHubClient contextHubClient, long j, int i) {
            if (j == 5147455389092024334L) {
                StringBuilder sb = new StringBuilder();
                sb.append("Nanoapp aborted, code: ");
                sb.append(i);
                Log.e("Elmyra/GestureSensor", sb.toString());
            }
        }
    };
    private int mContextHubRetryCount;
    /* access modifiers changed from: private */
    public final AssistGestureController mController;
    /* access modifiers changed from: private */
    public final GestureConfiguration mGestureConfiguration;
    /* access modifiers changed from: private */
    public boolean mIsListening;
    private final float mProgressDetectThreshold;

    public CHREGestureSensor(Context context, GestureConfiguration gestureConfiguration, SnapshotConfiguration snapshotConfiguration) {
        this.mContext = context;
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(C2009R$dimen.elmyra_progress_detect_threshold, typedValue, true);
        this.mProgressDetectThreshold = typedValue.getFloat();
        AssistGestureController assistGestureController = new AssistGestureController(context, this, gestureConfiguration, snapshotConfiguration);
        this.mController = assistGestureController;
        assistGestureController.setSnapshotListener(new Listener() {
            public final void onSnapshotRequested(SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader) {
                CHREGestureSensor.this.lambda$new$0$CHREGestureSensor(snapshotProtos$SnapshotHeader);
            }
        });
        this.mGestureConfiguration = gestureConfiguration;
        gestureConfiguration.setListener(new GestureConfiguration.Listener() {
            public final void onGestureConfigurationChanged(GestureConfiguration gestureConfiguration) {
                CHREGestureSensor.this.updateSensitivity(gestureConfiguration);
            }
        });
        initializeContextHubClientIfNull();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$CHREGestureSensor(SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader) {
        sendMessageToNanoApp(203, MessageNano.toByteArray(snapshotProtos$SnapshotHeader));
    }

    public void startListening() {
        this.mIsListening = true;
        startRecognizer();
    }

    public boolean isListening() {
        return this.mIsListening;
    }

    public void stopListening() {
        sendMessageToNanoApp(201, new byte[0]);
        this.mIsListening = false;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(CHREGestureSensor.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mIsListening: ");
        sb2.append(this.mIsListening);
        printWriter.println(sb2.toString());
        if (this.mContextHubClient == null) {
            printWriter.println("  mContextHubClient is null. Likely no context hubs were found");
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mContextHubRetryCount: ");
        sb3.append(this.mContextHubRetryCount);
        printWriter.println(sb3.toString());
        this.mController.dump(fileDescriptor, printWriter, strArr);
    }

    public void setGestureListener(GestureSensor.Listener listener) {
        this.mController.setGestureListener(listener);
    }

    private void initializeContextHubClientIfNull() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub") && this.mContextHubClient == null) {
            ContextHubManager contextHubManager = (ContextHubManager) this.mContext.getSystemService("contexthub");
            List contextHubs = contextHubManager.getContextHubs();
            if (contextHubs.size() == 0) {
                Log.e("Elmyra/GestureSensor", "No context hubs found");
            } else {
                this.mContextHubClient = contextHubManager.createClient((ContextHubInfo) contextHubs.get(0), this.mContextHubClientCallback);
                this.mContextHubRetryCount++;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSensitivity(GestureConfiguration gestureConfiguration) {
        ContextHubMessages$SensitivityUpdate contextHubMessages$SensitivityUpdate = new ContextHubMessages$SensitivityUpdate();
        contextHubMessages$SensitivityUpdate.sensitivity = gestureConfiguration.getSensitivity();
        sendMessageToNanoApp(202, MessageNano.toByteArray(contextHubMessages$SensitivityUpdate));
    }

    /* access modifiers changed from: private */
    public void startRecognizer() {
        ContextHubMessages$RecognizerStart contextHubMessages$RecognizerStart = new ContextHubMessages$RecognizerStart();
        contextHubMessages$RecognizerStart.progressReportThreshold = this.mProgressDetectThreshold;
        contextHubMessages$RecognizerStart.sensitivity = this.mGestureConfiguration.getSensitivity();
        sendMessageToNanoApp(200, MessageNano.toByteArray(contextHubMessages$RecognizerStart));
        if (this.mController.getChassisConfiguration() == null) {
            sendMessageToNanoApp(204, new byte[0]);
        }
    }

    private void sendMessageToNanoApp(int i, byte[] bArr) {
        initializeContextHubClientIfNull();
        String str = "Elmyra/GestureSensor";
        if (this.mContextHubClient == null) {
            Log.e(str, "ContextHubClient null");
            return;
        }
        int sendMessageToNanoApp = this.mContextHubClient.sendMessageToNanoApp(NanoAppMessage.createMessageToNanoApp(5147455389092024334L, i, bArr));
        if (sendMessageToNanoApp != 0) {
            Log.e(str, String.format("Unable to send message %d to nanoapp, error code %d", new Object[]{Integer.valueOf(i), Integer.valueOf(sendMessageToNanoApp)}));
        }
    }
}
