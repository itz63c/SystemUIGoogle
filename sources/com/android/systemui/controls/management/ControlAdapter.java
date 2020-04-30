package com.android.systemui.controls.management;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.android.systemui.C2013R$layout;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlAdapter.kt */
public final class ControlAdapter extends Adapter<Holder> {
    private final float elevation;
    /* access modifiers changed from: private */
    public ControlsModel model;
    private final SpanSizeLookup spanSizeLookup = new ControlAdapter$spanSizeLookup$1(this);

    public ControlAdapter(float f) {
        this.elevation = f;
    }

    public final SpanSizeLookup getSpanSizeLookup() {
        return this.spanSizeLookup;
    }

    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 0) {
            View inflate = from.inflate(C2013R$layout.controls_zone_header, viewGroup, false);
            Intrinsics.checkExpressionValueIsNotNull(inflate, "layoutInflater.inflate(R…ne_header, parent, false)");
            return new ZoneHolder(inflate);
        } else if (i == 1) {
            View inflate2 = from.inflate(C2013R$layout.controls_base_item, viewGroup, false);
            inflate2.getLayoutParams().width = -1;
            inflate2.setElevation(this.elevation);
            Intrinsics.checkExpressionValueIsNotNull(inflate2, "layoutInflater.inflate(R…ion\n                    }");
            return new ControlHolder(inflate2, new ControlAdapter$onCreateViewHolder$2(this));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Wrong viewType: ");
            sb.append(i);
            throw new IllegalStateException(sb.toString());
        }
    }

    public final void changeModel(ControlsModel controlsModel) {
        Intrinsics.checkParameterIsNotNull(controlsModel, "model");
        this.model = controlsModel;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        ControlsModel controlsModel = this.model;
        if (controlsModel != null) {
            List elements = controlsModel.getElements();
            if (elements != null) {
                return elements.size();
            }
        }
        return 0;
    }

    public void onBindViewHolder(Holder holder, int i) {
        Intrinsics.checkParameterIsNotNull(holder, "holder");
        ControlsModel controlsModel = this.model;
        if (controlsModel != null) {
            holder.bindData((ElementWrapper) controlsModel.getElements().get(i));
        }
    }

    public int getItemViewType(int i) {
        ControlsModel controlsModel = this.model;
        if (controlsModel != null) {
            ElementWrapper elementWrapper = (ElementWrapper) controlsModel.getElements().get(i);
            if (elementWrapper instanceof ZoneNameWrapper) {
                return 0;
            }
            if (elementWrapper instanceof ControlWrapper) {
                return 1;
            }
            throw new NoWhenBranchMatchedException();
        }
        throw new IllegalStateException("Getting item type for null model");
    }
}
