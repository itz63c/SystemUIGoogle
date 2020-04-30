package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.C2011R$id;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.p004ui.RenderInfo;
import com.android.systemui.controls.p004ui.RenderInfo.Companion;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlAdapter.kt */
final class ControlHolder extends Holder {
    /* access modifiers changed from: private */
    public final CheckBox favorite;
    private final Function2<String, Boolean, Unit> favoriteCallback;
    private final ViewGroup favoriteFrame;
    private final ImageView icon;
    private final TextView removed;
    private final TextView subtitle;
    private final TextView title;

    public ControlHolder(View view, Function2<? super String, ? super Boolean, Unit> function2) {
        Intrinsics.checkParameterIsNotNull(view, "view");
        Intrinsics.checkParameterIsNotNull(function2, "favoriteCallback");
        super(view, null);
        this.favoriteCallback = function2;
        View requireViewById = this.itemView.requireViewById(C2011R$id.icon);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "itemView.requireViewById(R.id.icon)");
        this.icon = (ImageView) requireViewById;
        View requireViewById2 = this.itemView.requireViewById(C2011R$id.title);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "itemView.requireViewById(R.id.title)");
        this.title = (TextView) requireViewById2;
        View requireViewById3 = this.itemView.requireViewById(C2011R$id.subtitle);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "itemView.requireViewById(R.id.subtitle)");
        this.subtitle = (TextView) requireViewById3;
        View requireViewById4 = this.itemView.requireViewById(C2011R$id.status);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById4, "itemView.requireViewById(R.id.status)");
        this.removed = (TextView) requireViewById4;
        View requireViewById5 = this.itemView.requireViewById(C2011R$id.favorite);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById5, "itemView.requireViewById<CheckBox>(R.id.favorite)");
        this.favorite = (CheckBox) requireViewById5;
        View requireViewById6 = this.itemView.requireViewById(C2011R$id.favorite_container);
        ViewGroup viewGroup = (ViewGroup) requireViewById6;
        viewGroup.setVisibility(0);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById6, "itemView\n            .re…lity = View.VISIBLE\n    }");
        this.favoriteFrame = viewGroup;
    }

    public final Function2<String, Boolean, Unit> getFavoriteCallback() {
        return this.favoriteCallback;
    }

    public void bindData(ElementWrapper elementWrapper) {
        Intrinsics.checkParameterIsNotNull(elementWrapper, "wrapper");
        ControlStatus controlStatus = ((ControlWrapper) elementWrapper).getControlStatus();
        RenderInfo renderInfo = getRenderInfo(controlStatus.getComponent(), controlStatus.getControl().getDeviceType());
        this.title.setText(controlStatus.getControl().getTitle());
        this.subtitle.setText(controlStatus.getControl().getSubtitle());
        this.favorite.setChecked(controlStatus.getFavorite());
        this.removed.setText(controlStatus.getRemoved() ? "Removed" : "");
        this.favorite.setOnClickListener(new ControlHolder$bindData$1(this, controlStatus));
        this.favoriteFrame.setOnClickListener(new ControlHolder$bindData$2(this));
        applyRenderInfo(renderInfo);
    }

    private final RenderInfo getRenderInfo(ComponentName componentName, int i) {
        Companion companion = RenderInfo.Companion;
        View view = this.itemView;
        Intrinsics.checkExpressionValueIsNotNull(view, "itemView");
        Context context = view.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "itemView.context");
        return Companion.lookup$default(companion, context, componentName, i, true, 0, 16, null);
    }

    private final void applyRenderInfo(RenderInfo renderInfo) {
        View view = this.itemView;
        Intrinsics.checkExpressionValueIsNotNull(view, "itemView");
        Context context = view.getContext();
        ColorStateList colorStateList = context.getResources().getColorStateList(renderInfo.getForeground(), context.getTheme());
        this.icon.setImageDrawable(renderInfo.getIcon());
        this.icon.setImageTintList(colorStateList);
    }
}
