package com.android.systemui.p007qs;

import com.android.systemui.C2011R$id;

/* renamed from: com.android.systemui.qs.QSContainerImplController */
public class QSContainerImplController {
    private final QuickStatusBarHeaderController mQuickStatusBarHeaderController;
    private final QSContainerImpl mView;

    /* renamed from: com.android.systemui.qs.QSContainerImplController$Builder */
    public static class Builder {
        private final com.android.systemui.p007qs.QuickStatusBarHeaderController.Builder mQuickStatusBarHeaderControllerBuilder;
        private QSContainerImpl mView;

        public Builder(com.android.systemui.p007qs.QuickStatusBarHeaderController.Builder builder) {
            this.mQuickStatusBarHeaderControllerBuilder = builder;
        }

        public Builder setQSContainerImpl(QSContainerImpl qSContainerImpl) {
            this.mView = qSContainerImpl;
            return this;
        }

        public QSContainerImplController build() {
            return new QSContainerImplController(this.mView, this.mQuickStatusBarHeaderControllerBuilder);
        }
    }

    private QSContainerImplController(QSContainerImpl qSContainerImpl, com.android.systemui.p007qs.QuickStatusBarHeaderController.Builder builder) {
        this.mView = qSContainerImpl;
        builder.setQuickStatusBarHeader((QuickStatusBarHeader) qSContainerImpl.findViewById(C2011R$id.header));
        this.mQuickStatusBarHeaderController = builder.build();
    }

    public void setListening(boolean z) {
        this.mQuickStatusBarHeaderController.setListening(z);
    }
}
