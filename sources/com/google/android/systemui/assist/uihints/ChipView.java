package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Space;
import android.widget.TextView;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.google.common.base.Preconditions;

public class ChipView extends FrameLayout {
    private final Drawable BACKGROUND_DARK;
    private final Drawable BACKGROUND_LIGHT;
    private final int TEXT_COLOR_DARK;
    private final int TEXT_COLOR_LIGHT;
    private LinearLayout mChip;
    private ImageView mIconView;
    private TextView mLabelView;
    private Space mSpaceView;

    public ChipView(Context context) {
        this(context, null);
    }

    public ChipView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChipView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ChipView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.BACKGROUND_DARK = context.getDrawable(C2010R$drawable.assist_chip_background_dark);
        this.BACKGROUND_LIGHT = context.getDrawable(C2010R$drawable.assist_chip_background_light);
        this.TEXT_COLOR_DARK = context.getColor(C2008R$color.assist_chip_text_dark);
        this.TEXT_COLOR_LIGHT = context.getColor(C2008R$color.assist_chip_text_light);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        LinearLayout linearLayout = (LinearLayout) findViewById(C2011R$id.chip_background);
        Preconditions.checkNotNull(linearLayout);
        this.mChip = linearLayout;
        ImageView imageView = (ImageView) findViewById(C2011R$id.chip_icon);
        Preconditions.checkNotNull(imageView);
        this.mIconView = imageView;
        TextView textView = (TextView) findViewById(C2011R$id.chip_label);
        Preconditions.checkNotNull(textView);
        this.mLabelView = textView;
        Space space = (Space) findViewById(C2011R$id.chip_element_padding);
        Preconditions.checkNotNull(space);
        this.mSpaceView = space;
    }

    /* access modifiers changed from: 0000 */
    public boolean setChip(Bundle bundle) {
        Icon icon = (Icon) bundle.getParcelable("icon");
        String string = bundle.getString("label");
        String str = "ChipView";
        if (icon == null && (string == null || string.length() == 0)) {
            Log.w(str, "Neither icon nor label provided; ignoring chip");
            return false;
        }
        if (icon == null) {
            this.mIconView.setVisibility(8);
            this.mSpaceView.setVisibility(8);
            this.mLabelView.setText(string);
            LayoutParams layoutParams = (LayoutParams) this.mLabelView.getLayoutParams();
            int i = layoutParams.rightMargin;
            layoutParams.setMargins(i, layoutParams.topMargin, i, layoutParams.bottomMargin);
        } else if (string == null || string.length() == 0) {
            this.mLabelView.setVisibility(8);
            this.mSpaceView.setVisibility(8);
            this.mIconView.setImageIcon(icon);
            LayoutParams layoutParams2 = (LayoutParams) this.mIconView.getLayoutParams();
            int i2 = layoutParams2.leftMargin;
            layoutParams2.setMargins(i2, layoutParams2.topMargin, i2, layoutParams2.bottomMargin);
        } else {
            this.mIconView.setImageIcon(icon);
            this.mLabelView.setText(string);
        }
        String str2 = "tap_action";
        if (bundle.getParcelable(str2) == null) {
            Log.w(str, "No tap action provided; ignoring chip");
            return false;
        }
        setTapAction((PendingIntent) bundle.getParcelable(str2));
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void updateTextSize(float f) {
        this.mLabelView.setTextSize(0, f);
    }

    /* access modifiers changed from: 0000 */
    public void setHasDarkBackground(boolean z) {
        this.mChip.setBackground(z ? this.BACKGROUND_DARK : this.BACKGROUND_LIGHT);
        this.mLabelView.setTextColor(z ? this.TEXT_COLOR_DARK : this.TEXT_COLOR_LIGHT);
    }

    private void setTapAction(PendingIntent pendingIntent) {
        setOnClickListener(new OnClickListener(pendingIntent) {
            public final /* synthetic */ PendingIntent f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(View view) {
                ChipView.lambda$setTapAction$0(this.f$0, view);
            }
        });
    }

    static /* synthetic */ void lambda$setTapAction$0(PendingIntent pendingIntent, View view) {
        try {
            pendingIntent.send();
        } catch (CanceledException e) {
            Log.w("ChipView", "Pending intent cancelled", e);
        }
    }
}
