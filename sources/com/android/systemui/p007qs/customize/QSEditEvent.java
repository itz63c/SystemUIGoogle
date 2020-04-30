package com.android.systemui.p007qs.customize;

import com.android.internal.logging.UiEventLogger.UiEventEnum;

/* renamed from: com.android.systemui.qs.customize.QSEditEvent */
/* compiled from: QSEditEvent.kt */
public enum QSEditEvent implements UiEventEnum {
    QS_EDIT_REMOVE(210),
    QS_EDIT_ADD(211),
    QS_EDIT_MOVE(212),
    QS_EDIT_OPEN(213),
    QS_EDIT_CLOSED(214),
    QS_EDIT_RESET(215);
    
    private final int _id;

    private QSEditEvent(int i) {
        this._id = i;
    }

    public int getId() {
        return this._id;
    }
}
