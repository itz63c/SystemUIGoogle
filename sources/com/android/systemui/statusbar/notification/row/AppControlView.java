package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.systemui.C2011R$id;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ChannelEditorListView.kt */
public final class AppControlView extends LinearLayout {
    public TextView channelName;
    public ImageView iconView;

    /* renamed from: switch reason: not valid java name */
    public Switch f105switch;

    public AppControlView(Context context, AttributeSet attributeSet) {
        Intrinsics.checkParameterIsNotNull(context, "c");
        Intrinsics.checkParameterIsNotNull(attributeSet, "attrs");
        super(context, attributeSet);
    }

    public final ImageView getIconView() {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            return imageView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("iconView");
        throw null;
    }

    public final TextView getChannelName() {
        TextView textView = this.channelName;
        if (textView != null) {
            return textView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("channelName");
        throw null;
    }

    public final Switch getSwitch() {
        Switch switchR = this.f105switch;
        if (switchR != null) {
            return switchR;
        }
        Intrinsics.throwUninitializedPropertyAccessException("switch");
        throw null;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        View findViewById = findViewById(C2011R$id.icon);
        Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById(R.id.icon)");
        this.iconView = (ImageView) findViewById;
        View findViewById2 = findViewById(C2011R$id.app_name);
        Intrinsics.checkExpressionValueIsNotNull(findViewById2, "findViewById(R.id.app_name)");
        this.channelName = (TextView) findViewById2;
        View findViewById3 = findViewById(C2011R$id.toggle);
        Intrinsics.checkExpressionValueIsNotNull(findViewById3, "findViewById(R.id.toggle)");
        this.f105switch = (Switch) findViewById3;
        setOnClickListener(new AppControlView$onFinishInflate$1(this));
    }
}
