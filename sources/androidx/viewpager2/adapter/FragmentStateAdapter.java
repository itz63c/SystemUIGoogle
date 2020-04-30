package androidx.viewpager2.adapter;

import android.os.Handler;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public abstract class FragmentStateAdapter extends Adapter<FragmentViewHolder> implements StatefulAdapter {

    /* renamed from: androidx.viewpager2.adapter.FragmentStateAdapter$2 */
    class C04962 implements LifecycleEventObserver {
        final /* synthetic */ FragmentStateAdapter this$0;
        final /* synthetic */ FragmentViewHolder val$holder;

        public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
            if (!this.this$0.shouldDelayFragmentTransactions()) {
                lifecycleOwner.getLifecycle().removeObserver(this);
                if (ViewCompat.isAttachedToWindow(this.val$holder.getContainer())) {
                    this.this$0.placeFragmentInViewHolder(this.val$holder);
                }
            }
        }
    }

    /* renamed from: androidx.viewpager2.adapter.FragmentStateAdapter$5 */
    class C04975 implements LifecycleEventObserver {
        final /* synthetic */ Handler val$handler;
        final /* synthetic */ Runnable val$runnable;

        public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
            if (event == Event.ON_DESTROY) {
                this.val$handler.removeCallbacks(this.val$runnable);
                lifecycleOwner.getLifecycle().removeObserver(this);
            }
        }
    }

    class FragmentMaxLifecycleEnforcer {

        /* renamed from: androidx.viewpager2.adapter.FragmentStateAdapter$FragmentMaxLifecycleEnforcer$3 */
        class C04983 implements LifecycleEventObserver {
            final /* synthetic */ FragmentMaxLifecycleEnforcer this$1;

            public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
                this.this$1.updateFragmentMaxLifecycle(false);
            }
        }

        /* access modifiers changed from: 0000 */
        public abstract void updateFragmentMaxLifecycle(boolean z);
    }

    /* access modifiers changed from: 0000 */
    public abstract void placeFragmentInViewHolder(FragmentViewHolder fragmentViewHolder);

    /* access modifiers changed from: 0000 */
    public abstract boolean shouldDelayFragmentTransactions();
}
