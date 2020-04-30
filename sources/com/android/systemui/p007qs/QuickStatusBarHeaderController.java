package com.android.systemui.p007qs;

import com.android.systemui.C2011R$id;
import com.android.systemui.p007qs.carrier.QSCarrierGroup;
import com.android.systemui.p007qs.carrier.QSCarrierGroupController;

/* renamed from: com.android.systemui.qs.QuickStatusBarHeaderController */
public class QuickStatusBarHeaderController {
    private final QSCarrierGroupController mQSCarrierGroupController;
    private final QuickStatusBarHeader mView;

    /* renamed from: com.android.systemui.qs.QuickStatusBarHeaderController$Builder */
    public static class Builder {
        private final com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder mQSCarrierGroupControllerBuilder;
        private QuickStatusBarHeader mView;

        public Builder(com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder builder) {
            this.mQSCarrierGroupControllerBuilder = builder;
        }

        public Builder setQuickStatusBarHeader(QuickStatusBarHeader quickStatusBarHeader) {
            this.mView = quickStatusBarHeader;
            return this;
        }

        public QuickStatusBarHeaderController build() {
            return new QuickStatusBarHeaderController(this.mView, this.mQSCarrierGroupControllerBuilder);
        }
    }

    private QuickStatusBarHeaderController(QuickStatusBarHeader quickStatusBarHeader, com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder builder) {
        this.mView = quickStatusBarHeader;
        builder.setQSCarrierGroup((QSCarrierGroup) quickStatusBarHeader.findViewById(C2011R$id.carrier_group));
        this.mQSCarrierGroupController = builder.build();
    }

    public void setListening(boolean z) {
        this.mQSCarrierGroupController.setListening(z);
        this.mView.setListening(z);
    }
}
