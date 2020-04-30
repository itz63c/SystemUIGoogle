package com.google.android.systemui.elmyra.plugin;

import android.content.Context;
import android.hardware.location.ContextHubClient;
import android.hardware.location.ContextHubClientCallback;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.ContextHubManager;
import android.hardware.location.NanoAppMessage;
import android.hardware.location.NanoAppState;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.Pair;
import com.android.systemui.plugins.SensorManagerPlugin;
import com.android.systemui.plugins.SensorManagerPlugin.Sensor;
import com.android.systemui.plugins.SensorManagerPlugin.SensorEvent;
import com.android.systemui.plugins.SensorManagerPlugin.SensorEventListener;
import com.android.systemui.plugins.annotations.Requires;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Requires(target = SensorManagerPlugin.class, version = 1)
public class ElmyraSensorPlugin implements SensorManagerPlugin {
    private List<Pair<Sensor, SensorEventListener>> mClients;
    private Context mContext;
    private ContextHubClient mContextHubClient;
    private final ContextHubClientCallback mContextHubClientCallback = new ContextHubClientCallback() {
        public void onMessageFromNanoApp(ContextHubClient contextHubClient, NanoAppMessage nanoAppMessage) {
            if (nanoAppMessage.getNanoAppId() == 5147455389092024334L) {
                if (!ElmyraSensorPlugin.this.mNanoAppFound) {
                    Log.wtf("ElmyraSensorPlugin", "onMessageFromNanoApp(): nanoapp not found");
                    return;
                }
                if (nanoAppMessage.getMessageType() == 304) {
                    ElmyraSensorPlugin.this.onGrabDetected();
                }
            }
        }
    };
    private ContextHubInfo mContextHubInfo;
    private ContextHubManager mContextHubManager;
    private Thread mInitThread;
    private boolean mListening;
    /* access modifiers changed from: private */
    public boolean mNanoAppFound;

    public void onCreate(Context context, Context context2) {
        this.mContext = context2;
        this.mClients = new ArrayList();
        this.mContextHubManager = (ContextHubManager) context2.getSystemService(ContextHubManager.class);
        this.mInitThread = new Thread("InitElmyraSensorPlugin") {
            public void run() {
                ElmyraSensorPlugin elmyraSensorPlugin = ElmyraSensorPlugin.this;
                elmyraSensorPlugin.mNanoAppFound = elmyraSensorPlugin.findNanoApp();
            }
        };
    }

    public void registerListener(Sensor sensor, SensorEventListener sensorEventListener) {
        if (sensor.getType() == 1) {
            this.mClients.add(new Pair(sensor, sensorEventListener));
            updateChreListener();
        }
    }

    public void unregisterListener(Sensor sensor, SensorEventListener sensorEventListener) {
        if (sensor.getType() == 1) {
            for (int size = this.mClients.size() - 1; size >= 0; size--) {
                Pair pair = (Pair) this.mClients.get(size);
                if (((Sensor) pair.first).equals(sensor) && ((SensorEventListener) pair.second).equals(sensorEventListener)) {
                    this.mClients.remove(pair);
                }
            }
            updateChreListener();
        }
    }

    /* access modifiers changed from: private */
    public void onGrabDetected() {
        for (int i = 0; i < this.mClients.size(); i++) {
            Pair pair = (Pair) this.mClients.get(i);
            ((SensorEventListener) pair.second).onSensorChanged(new SensorEvent((Sensor) pair.first, 1));
        }
    }

    /* access modifiers changed from: private */
    public boolean findNanoApp() {
        if (this.mNanoAppFound) {
            return true;
        }
        List contextHubs = this.mContextHubManager.getContextHubs();
        String str = "ElmyraSensorPlugin";
        if (contextHubs.size() == 0) {
            Log.e(str, "No context hubs found");
            return false;
        }
        this.mContextHubInfo = (ContextHubInfo) contextHubs.get(0);
        try {
            Iterator it = ((List) this.mContextHubManager.queryNanoApps((ContextHubInfo) contextHubs.get(0)).waitForResponse(5, TimeUnit.SECONDS).getContents()).iterator();
            while (true) {
                if (it.hasNext()) {
                    if (((NanoAppState) it.next()).getNanoAppId() == 5147455389092024334L) {
                        this.mNanoAppFound = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            return true;
        } catch (InterruptedException unused) {
            Log.e(str, "Interrupted while looking for nanoapp");
            return false;
        } catch (TimeoutException unused2) {
            Log.e(str, "Timed out looking for nanoapp");
            return false;
        }
    }

    private void awaitInit() {
        try {
            this.mInitThread.join();
        } catch (InterruptedException e) {
            Log.e("ElmyraSensorPlugin", "Interrupted while waiting for init", e);
        }
    }

    private void updateChreListener() {
        if (Secure.getInt(this.mContext.getContentResolver(), "com.google.android.systemui.elmyra.plugin.ENABLE", 0) != 0) {
            if (this.mClients.isEmpty() && this.mListening) {
                stopListening();
            } else if (!this.mClients.isEmpty() && !this.mListening) {
                startListening();
            }
        }
    }

    private void startListening() {
        awaitInit();
        if ((this.mNanoAppFound || findNanoApp()) && !this.mListening) {
            this.mContextHubClient = this.mContextHubManager.createClient(this.mContextHubInfo, this.mContextHubClientCallback);
            sendMessageToNanoApp(205, new byte[0]);
            this.mListening = true;
        }
    }

    private void stopListening() {
        awaitInit();
        if (!this.mNanoAppFound) {
            boolean findNanoApp = findNanoApp();
            StringBuilder sb = new StringBuilder();
            sb.append("stopListening(): nanoapp not found, refind = ");
            sb.append(findNanoApp);
            Log.e("ElmyraSensorPlugin", sb.toString());
            if (!findNanoApp) {
                return;
            }
        }
        if (this.mListening) {
            sendMessageToNanoApp(206, new byte[0]);
            this.mContextHubClient.close();
            this.mListening = false;
        }
    }

    private void sendMessageToNanoApp(int i, byte[] bArr) {
        String str = "ElmyraSensorPlugin";
        if (this.mListening || i == 205) {
            int sendMessageToNanoApp = this.mContextHubClient.sendMessageToNanoApp(NanoAppMessage.createMessageToNanoApp(5147455389092024334L, i, bArr));
            if (sendMessageToNanoApp != 0) {
                Log.e(str, String.format("Unable to send message %d to nanoapp, error code %d", new Object[]{Integer.valueOf(i), Integer.valueOf(sendMessageToNanoApp)}));
            }
            return;
        }
        Log.w(str, String.format("Attempted to send message %d to inactive recognizer", new Object[]{Integer.valueOf(i)}));
    }
}
