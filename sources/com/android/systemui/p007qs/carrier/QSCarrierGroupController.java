package com.android.systemui.p007qs.carrier;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.android.keyguard.CarrierTextController;
import com.android.keyguard.CarrierTextController.CarrierTextCallback;
import com.android.keyguard.CarrierTextController.CarrierTextCallbackInfo;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController */
public class QSCarrierGroupController {
    private final ActivityStarter mActivityStarter;
    private final Handler mBgHandler;
    private final Callback mCallback;
    private View[] mCarrierDividers;
    private QSCarrier[] mCarrierGroups;
    private final CarrierTextController mCarrierTextController;
    /* access modifiers changed from: private */
    public final CellSignalState[] mInfos;
    private boolean mListening;
    /* access modifiers changed from: private */
    public final C0992H mMainHandler;
    private final NetworkController mNetworkController;
    private final TextView mNoSimTextView;
    private final SignalCallback mSignalCallback;

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$Builder */
    public static class Builder {
        private final ActivityStarter mActivityStarter;
        private final com.android.keyguard.CarrierTextController.Builder mCarrierTextControllerBuilder;
        private final Handler mHandler;
        private final Looper mLooper;
        private final NetworkController mNetworkController;
        private QSCarrierGroup mView;

        public Builder(ActivityStarter activityStarter, Handler handler, Looper looper, NetworkController networkController, com.android.keyguard.CarrierTextController.Builder builder) {
            this.mActivityStarter = activityStarter;
            this.mHandler = handler;
            this.mLooper = looper;
            this.mNetworkController = networkController;
            this.mCarrierTextControllerBuilder = builder;
        }

        public Builder setQSCarrierGroup(QSCarrierGroup qSCarrierGroup) {
            this.mView = qSCarrierGroup;
            return this;
        }

        public QSCarrierGroupController build() {
            QSCarrierGroupController qSCarrierGroupController = new QSCarrierGroupController(this.mView, this.mActivityStarter, this.mHandler, this.mLooper, this.mNetworkController, this.mCarrierTextControllerBuilder);
            return qSCarrierGroupController;
        }
    }

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$Callback */
    private static class Callback implements CarrierTextCallback {
        private C0992H mHandler;

        Callback(C0992H h) {
            this.mHandler = h;
        }

        public void updateCarrierInfo(CarrierTextCallbackInfo carrierTextCallbackInfo) {
            this.mHandler.obtainMessage(0, carrierTextCallbackInfo).sendToTarget();
        }
    }

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$H */
    private static class C0992H extends Handler {
        private Consumer<CarrierTextCallbackInfo> mUpdateCarrierInfo;
        private Runnable mUpdateState;

        C0992H(Looper looper, Consumer<CarrierTextCallbackInfo> consumer, Runnable runnable) {
            super(looper);
            this.mUpdateCarrierInfo = consumer;
            this.mUpdateState = runnable;
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mUpdateCarrierInfo.accept((CarrierTextCallbackInfo) message.obj);
            } else if (i != 1) {
                super.handleMessage(message);
            } else {
                this.mUpdateState.run();
            }
        }
    }

    private QSCarrierGroupController(QSCarrierGroup qSCarrierGroup, ActivityStarter activityStarter, Handler handler, Looper looper, NetworkController networkController, com.android.keyguard.CarrierTextController.Builder builder) {
        this.mInfos = new CellSignalState[3];
        this.mCarrierDividers = new View[2];
        this.mCarrierGroups = new QSCarrier[3];
        this.mSignalCallback = new SignalCallback() {
            public void setMobileDataIndicators(IconState iconState, IconState iconState2, int i, int i2, boolean z, boolean z2, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, boolean z3, int i3, boolean z4) {
                int slotIndex = QSCarrierGroupController.this.getSlotIndex(i3);
                String str = "QSCarrierGroup";
                if (slotIndex >= 3) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("setMobileDataIndicators - slot: ");
                    sb.append(slotIndex);
                    Log.w(str, sb.toString());
                } else if (slotIndex == -1) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Invalid SIM slot index for subscription: ");
                    sb2.append(i3);
                    Log.e(str, sb2.toString());
                } else {
                    CellSignalState[] access$000 = QSCarrierGroupController.this.mInfos;
                    CellSignalState cellSignalState = new CellSignalState(iconState.visible, iconState.icon, iconState.contentDescription, charSequence.toString(), z4);
                    access$000[slotIndex] = cellSignalState;
                    QSCarrierGroupController.this.mMainHandler.obtainMessage(1).sendToTarget();
                }
            }

            public void setNoSims(boolean z, boolean z2) {
                if (z) {
                    for (int i = 0; i < 3; i++) {
                        QSCarrierGroupController.this.mInfos[i] = QSCarrierGroupController.this.mInfos[i].changeVisibility(false);
                    }
                }
                QSCarrierGroupController.this.mMainHandler.obtainMessage(1).sendToTarget();
            }
        };
        this.mActivityStarter = activityStarter;
        this.mBgHandler = handler;
        this.mNetworkController = networkController;
        builder.setShowAirplaneMode(false);
        builder.setShowMissingSim(false);
        this.mCarrierTextController = builder.build();
        $$Lambda$QSCarrierGroupController$ZRGu7m0elMW0O61dCq0iB2l54 r6 = new OnClickListener() {
            public final void onClick(View view) {
                QSCarrierGroupController.this.lambda$new$0$QSCarrierGroupController(view);
            }
        };
        qSCarrierGroup.setOnClickListener(r6);
        TextView noSimTextView = qSCarrierGroup.getNoSimTextView();
        this.mNoSimTextView = noSimTextView;
        noSimTextView.setOnClickListener(r6);
        C0992H h = new C0992H(looper, new Consumer() {
            public final void accept(Object obj) {
                QSCarrierGroupController.this.handleUpdateCarrierInfo((CarrierTextCallbackInfo) obj);
            }
        }, new Runnable() {
            public final void run() {
                QSCarrierGroupController.this.handleUpdateState();
            }
        });
        this.mMainHandler = h;
        this.mCallback = new Callback(h);
        this.mCarrierGroups[0] = qSCarrierGroup.getCarrier1View();
        this.mCarrierGroups[1] = qSCarrierGroup.getCarrier2View();
        this.mCarrierGroups[2] = qSCarrierGroup.getCarrier3View();
        this.mCarrierDividers[0] = qSCarrierGroup.getCarrierDivider1();
        this.mCarrierDividers[1] = qSCarrierGroup.getCarrierDivider2();
        for (int i = 0; i < 3; i++) {
            this.mInfos[i] = new CellSignalState();
            this.mCarrierGroups[i].setOnClickListener(r6);
        }
        qSCarrierGroup.setImportantForAccessibility(1);
        qSCarrierGroup.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View view) {
            }

            public void onViewDetachedFromWindow(View view) {
                QSCarrierGroupController.this.setListening(false);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$QSCarrierGroupController(View view) {
        if (view.isVisibleToUser()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.WIRELESS_SETTINGS"), 0);
        }
    }

    /* access modifiers changed from: protected */
    public int getSlotIndex(int i) {
        return SubscriptionManager.getSlotIndex(i);
    }

    public void setListening(boolean z) {
        if (z != this.mListening) {
            this.mListening = z;
            this.mBgHandler.post(new Runnable() {
                public final void run() {
                    QSCarrierGroupController.this.updateListeners();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void updateListeners() {
        if (this.mListening) {
            if (this.mNetworkController.hasVoiceCallingFeature()) {
                this.mNetworkController.addCallback(this.mSignalCallback);
            }
            this.mCarrierTextController.setListening(this.mCallback);
            return;
        }
        this.mNetworkController.removeCallback(this.mSignalCallback);
        this.mCarrierTextController.setListening(null);
    }

    /* access modifiers changed from: private */
    public void handleUpdateState() {
        if (!this.mMainHandler.getLooper().isCurrentThread()) {
            this.mMainHandler.obtainMessage(1).sendToTarget();
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            this.mCarrierGroups[i2].updateState(this.mInfos[i2]);
        }
        View view = this.mCarrierDividers[0];
        CellSignalState[] cellSignalStateArr = this.mInfos;
        view.setVisibility((!cellSignalStateArr[0].visible || !cellSignalStateArr[1].visible) ? 8 : 0);
        View view2 = this.mCarrierDividers[1];
        CellSignalState[] cellSignalStateArr2 = this.mInfos;
        if (!cellSignalStateArr2[1].visible || !cellSignalStateArr2[2].visible) {
            CellSignalState[] cellSignalStateArr3 = this.mInfos;
            if (!cellSignalStateArr3[0].visible || !cellSignalStateArr3[2].visible) {
                i = 8;
            }
        }
        view2.setVisibility(i);
    }

    /* access modifiers changed from: private */
    public void handleUpdateCarrierInfo(CarrierTextCallbackInfo carrierTextCallbackInfo) {
        if (!this.mMainHandler.getLooper().isCurrentThread()) {
            this.mMainHandler.obtainMessage(0, carrierTextCallbackInfo).sendToTarget();
            return;
        }
        this.mNoSimTextView.setVisibility(8);
        if (carrierTextCallbackInfo.airplaneMode || !carrierTextCallbackInfo.anySimReady) {
            for (int i = 0; i < 3; i++) {
                CellSignalState[] cellSignalStateArr = this.mInfos;
                cellSignalStateArr[i] = cellSignalStateArr[i].changeVisibility(false);
                this.mCarrierGroups[i].setCarrierText("");
                this.mCarrierGroups[i].setVisibility(8);
            }
            this.mNoSimTextView.setText(carrierTextCallbackInfo.carrierText);
            this.mNoSimTextView.setVisibility(0);
        } else {
            boolean[] zArr = new boolean[3];
            String str = "QSCarrierGroup";
            if (carrierTextCallbackInfo.listOfCarriers.length == carrierTextCallbackInfo.subscriptionIds.length) {
                int i2 = 0;
                while (i2 < 3 && i2 < carrierTextCallbackInfo.listOfCarriers.length) {
                    int slotIndex = getSlotIndex(carrierTextCallbackInfo.subscriptionIds[i2]);
                    if (slotIndex >= 3) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("updateInfoCarrier - slot: ");
                        sb.append(slotIndex);
                        Log.w(str, sb.toString());
                    } else if (slotIndex == -1) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Invalid SIM slot index for subscription: ");
                        sb2.append(carrierTextCallbackInfo.subscriptionIds[i2]);
                        Log.e(str, sb2.toString());
                    } else {
                        CellSignalState[] cellSignalStateArr2 = this.mInfos;
                        cellSignalStateArr2[slotIndex] = cellSignalStateArr2[slotIndex].changeVisibility(true);
                        zArr[slotIndex] = true;
                        this.mCarrierGroups[slotIndex].setCarrierText(carrierTextCallbackInfo.listOfCarriers[i2].toString().trim());
                        this.mCarrierGroups[slotIndex].setVisibility(0);
                    }
                    i2++;
                }
                for (int i3 = 0; i3 < 3; i3++) {
                    if (!zArr[i3]) {
                        CellSignalState[] cellSignalStateArr3 = this.mInfos;
                        cellSignalStateArr3[i3] = cellSignalStateArr3[i3].changeVisibility(false);
                        this.mCarrierGroups[i3].setVisibility(8);
                    }
                }
            } else {
                Log.e(str, "Carrier information arrays not of same length");
            }
        }
        handleUpdateState();
    }
}
