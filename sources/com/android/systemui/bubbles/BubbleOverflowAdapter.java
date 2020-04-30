package com.android.systemui.bubbles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.android.systemui.C2013R$layout;
import java.util.List;
import java.util.function.Consumer;

/* compiled from: BubbleOverflowActivity */
class BubbleOverflowAdapter extends Adapter<ViewHolder> {
    private int mBubbleMargin;
    private List<Bubble> mBubbles;
    private Consumer<Bubble> mPromoteBubbleFromOverflow;

    /* compiled from: BubbleOverflowActivity */
    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public BadgedImageView mBadgedImageView;

        public ViewHolder(BadgedImageView badgedImageView) {
            super(badgedImageView);
            this.mBadgedImageView = badgedImageView;
        }
    }

    public BubbleOverflowAdapter(List<Bubble> list, Consumer<Bubble> consumer, int i) {
        this.mBubbles = list;
        this.mPromoteBubbleFromOverflow = consumer;
        this.mBubbleMargin = i;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        BadgedImageView badgedImageView = (BadgedImageView) LayoutInflater.from(viewGroup.getContext()).inflate(C2013R$layout.bubble_view, viewGroup, false);
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        int i2 = this.mBubbleMargin;
        layoutParams.setMargins(i2, i2, i2, i2);
        badgedImageView.setLayoutParams(layoutParams);
        return new ViewHolder(badgedImageView);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Bubble bubble = (Bubble) this.mBubbles.get(i);
        viewHolder.mBadgedImageView.update(bubble);
        viewHolder.mBadgedImageView.setOnClickListener(new OnClickListener(bubble) {
            public final /* synthetic */ Bubble f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                BubbleOverflowAdapter.this.lambda$onBindViewHolder$0$BubbleOverflowAdapter(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$0 */
    public /* synthetic */ void lambda$onBindViewHolder$0$BubbleOverflowAdapter(Bubble bubble, View view) {
        this.mBubbles.remove(bubble);
        notifyDataSetChanged();
        this.mPromoteBubbleFromOverflow.accept(bubble);
    }

    public int getItemCount() {
        return this.mBubbles.size();
    }
}
