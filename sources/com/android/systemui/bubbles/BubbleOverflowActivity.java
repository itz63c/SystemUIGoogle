package com.android.systemui.bubbles;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2013R$layout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BubbleOverflowActivity extends Activity {
    private BubbleOverflowAdapter mAdapter;
    private BubbleController mBubbleController;
    private LinearLayout mEmptyState;
    private List<Bubble> mOverflowBubbles = new ArrayList();
    private RecyclerView mRecyclerView;

    public BubbleOverflowActivity(BubbleController bubbleController) {
        this.mBubbleController = bubbleController;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C2013R$layout.bubble_overflow_activity);
        setBackgroundColor();
        this.mEmptyState = (LinearLayout) findViewById(C2011R$id.bubble_overflow_empty_state);
        RecyclerView recyclerView = (RecyclerView) findViewById(C2011R$id.bubble_overflow_recycler);
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), getResources().getInteger(C2012R$integer.bubbles_overflow_columns)));
        int dimensionPixelSize = getResources().getDimensionPixelSize(C2009R$dimen.bubble_overflow_margin);
        List<Bubble> list = this.mOverflowBubbles;
        BubbleController bubbleController = this.mBubbleController;
        Objects.requireNonNull(bubbleController);
        BubbleOverflowAdapter bubbleOverflowAdapter = new BubbleOverflowAdapter(list, new Consumer() {
            public final void accept(Object obj) {
                BubbleController.this.promoteBubbleFromOverflow((Bubble) obj);
            }
        }, dimensionPixelSize);
        this.mAdapter = bubbleOverflowAdapter;
        this.mRecyclerView.setAdapter(bubbleOverflowAdapter);
        onDataChanged(this.mBubbleController.getOverflowBubbles());
        this.mBubbleController.setOverflowCallback(new Runnable() {
            public final void run() {
                BubbleOverflowActivity.this.lambda$onCreate$0$BubbleOverflowActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$BubbleOverflowActivity() {
        onDataChanged(this.mBubbleController.getOverflowBubbles());
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundColor() {
        TypedArray obtainStyledAttributes = getApplicationContext().obtainStyledAttributes(new int[]{16844002});
        int color = obtainStyledAttributes.getColor(0, -1);
        obtainStyledAttributes.recycle();
        findViewById(16908290).setBackgroundColor(color);
    }

    /* access modifiers changed from: 0000 */
    public void onDataChanged(List<Bubble> list) {
        this.mOverflowBubbles.clear();
        this.mOverflowBubbles.addAll(list);
        this.mAdapter.notifyDataSetChanged();
        if (this.mOverflowBubbles.isEmpty()) {
            this.mEmptyState.setVisibility(0);
        } else {
            this.mEmptyState.setVisibility(8);
        }
    }

    public void onStart() {
        super.onStart();
    }

    public void onRestart() {
        super.onRestart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
