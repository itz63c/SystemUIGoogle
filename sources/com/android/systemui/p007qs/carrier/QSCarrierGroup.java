package com.android.systemui.p007qs.carrier;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.C2011R$id;

/* renamed from: com.android.systemui.qs.carrier.QSCarrierGroup */
public class QSCarrierGroup extends LinearLayout {
    public QSCarrierGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: 0000 */
    public TextView getNoSimTextView() {
        return (TextView) findViewById(C2011R$id.no_carrier_text);
    }

    /* access modifiers changed from: 0000 */
    public QSCarrier getCarrier1View() {
        return (QSCarrier) findViewById(C2011R$id.carrier1);
    }

    /* access modifiers changed from: 0000 */
    public QSCarrier getCarrier2View() {
        return (QSCarrier) findViewById(C2011R$id.carrier2);
    }

    /* access modifiers changed from: 0000 */
    public QSCarrier getCarrier3View() {
        return (QSCarrier) findViewById(C2011R$id.carrier3);
    }

    /* access modifiers changed from: 0000 */
    public View getCarrierDivider1() {
        return findViewById(C2011R$id.qs_carrier_divider1);
    }

    /* access modifiers changed from: 0000 */
    public View getCarrierDivider2() {
        return findViewById(C2011R$id.qs_carrier_divider2);
    }
}
