package com.android.systemui.p007qs.customize;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSTileHost;
import com.android.systemui.p007qs.customize.TileQueryHelper.TileInfo;
import com.android.systemui.p007qs.customize.TileQueryHelper.TileStateListener;
import com.android.systemui.p007qs.external.CustomTile;
import com.android.systemui.p007qs.tileimpl.QSIconViewImpl;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.systemui.qs.customize.TileAdapter */
public class TileAdapter extends Adapter<Holder> implements TileStateListener {
    /* access modifiers changed from: private */
    public int mAccessibilityAction = 0;
    private int mAccessibilityFromIndex;
    private CharSequence mAccessibilityFromLabel;
    private final AccessibilityManager mAccessibilityManager;
    private List<TileInfo> mAllTiles;
    private final Callback mCallbacks = new Callback() {
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            super.onSelectedChanged(viewHolder, i);
            if (i != 2) {
                viewHolder = null;
            }
            if (viewHolder != TileAdapter.this.mCurrentDrag) {
                if (TileAdapter.this.mCurrentDrag != null) {
                    int adapterPosition = TileAdapter.this.mCurrentDrag.getAdapterPosition();
                    if (adapterPosition != -1) {
                        TileAdapter.this.mCurrentDrag.mTileView.setShowAppLabel(adapterPosition > TileAdapter.this.mEditIndex && !((TileInfo) TileAdapter.this.mTiles.get(adapterPosition)).isSystem);
                        TileAdapter.this.mCurrentDrag.stopDrag();
                        TileAdapter.this.mCurrentDrag = null;
                    } else {
                        return;
                    }
                }
                if (viewHolder != null) {
                    TileAdapter.this.mCurrentDrag = (Holder) viewHolder;
                    TileAdapter.this.mCurrentDrag.startDrag();
                }
                TileAdapter.this.mHandler.post(new Runnable() {
                    public void run() {
                        TileAdapter tileAdapter = TileAdapter.this;
                        tileAdapter.notifyItemChanged(tileAdapter.mEditIndex);
                    }
                });
            }
        }

        public boolean canDropOver(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            int adapterPosition = viewHolder2.getAdapterPosition();
            boolean z = false;
            if (!(adapterPosition == 0 || adapterPosition == -1)) {
                if (!TileAdapter.this.canRemoveTiles() && viewHolder.getAdapterPosition() < TileAdapter.this.mEditIndex) {
                    if (adapterPosition < TileAdapter.this.mEditIndex) {
                        z = true;
                    }
                    return z;
                } else if (adapterPosition <= TileAdapter.this.mEditIndex + 1) {
                    z = true;
                }
            }
            return z;
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1 || itemViewType == 3 || itemViewType == 4) {
                return Callback.makeMovementFlags(0, 0);
            }
            return Callback.makeMovementFlags(15, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            int adapterPosition = viewHolder.getAdapterPosition();
            int adapterPosition2 = viewHolder2.getAdapterPosition();
            if (adapterPosition == 0 || adapterPosition == -1 || adapterPosition2 == 0 || adapterPosition2 == -1) {
                return false;
            }
            return TileAdapter.this.move(adapterPosition, adapterPosition2, viewHolder2.itemView);
        }
    };
    private final Context mContext;
    /* access modifiers changed from: private */
    public Holder mCurrentDrag;
    private List<String> mCurrentSpecs;
    private final ItemDecoration mDecoration;
    /* access modifiers changed from: private */
    public int mEditIndex;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private QSTileHost mHost;
    private final ItemTouchHelper mItemTouchHelper;
    private final int mMinNumTiles;
    private boolean mNeedsFocus;
    private List<TileInfo> mOtherTiles;
    private final SpanSizeLookup mSizeLookup = new SpanSizeLookup() {
        public int getSpanSize(int i) {
            int itemViewType = TileAdapter.this.getItemViewType(i);
            return (itemViewType == 1 || itemViewType == 4 || itemViewType == 3) ? 3 : 1;
        }
    };
    /* access modifiers changed from: private */
    public int mTileDividerIndex;
    /* access modifiers changed from: private */
    public final List<TileInfo> mTiles = new ArrayList();
    private UiEventLogger mUiEventLogger = new UiEventLoggerImpl();

    /* renamed from: com.android.systemui.qs.customize.TileAdapter$Holder */
    public class Holder extends ViewHolder {
        /* access modifiers changed from: private */
        public CustomizeTileView mTileView;

        public Holder(TileAdapter tileAdapter, View view) {
            super(view);
            if (view instanceof FrameLayout) {
                CustomizeTileView customizeTileView = (CustomizeTileView) ((FrameLayout) view).getChildAt(0);
                this.mTileView = customizeTileView;
                customizeTileView.setBackground(null);
                this.mTileView.getIcon().disableAnimation();
            }
        }

        public void clearDrag() {
            this.itemView.clearAnimation();
            this.mTileView.findViewById(C2011R$id.tile_label).clearAnimation();
            this.mTileView.findViewById(C2011R$id.tile_label).setAlpha(1.0f);
            this.mTileView.getAppLabel().clearAnimation();
            this.mTileView.getAppLabel().setAlpha(0.6f);
        }

        public void startDrag() {
            this.itemView.animate().setDuration(100).scaleX(1.2f).scaleY(1.2f);
            this.mTileView.findViewById(C2011R$id.tile_label).animate().setDuration(100).alpha(0.0f);
            this.mTileView.getAppLabel().animate().setDuration(100).alpha(0.0f);
        }

        public void stopDrag() {
            this.itemView.animate().setDuration(100).scaleX(1.0f).scaleY(1.0f);
            this.mTileView.findViewById(C2011R$id.tile_label).animate().setDuration(100).alpha(1.0f);
            this.mTileView.getAppLabel().animate().setDuration(100).alpha(0.6f);
        }
    }

    /* renamed from: com.android.systemui.qs.customize.TileAdapter$TileItemDecoration */
    private class TileItemDecoration extends ItemDecoration {
        private final Drawable mDrawable;

        private TileItemDecoration(Context context) {
            this.mDrawable = context.getDrawable(C2010R$drawable.qs_customize_tile_decoration);
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
            super.onDraw(canvas, recyclerView, state);
            int childCount = recyclerView.getChildCount();
            int width = recyclerView.getWidth();
            int bottom = recyclerView.getBottom();
            int i = 0;
            while (i < childCount) {
                View childAt = recyclerView.getChildAt(i);
                ViewHolder childViewHolder = recyclerView.getChildViewHolder(childAt);
                if (childViewHolder.getAdapterPosition() == 0 || (childViewHolder.getAdapterPosition() < TileAdapter.this.mEditIndex && !(childAt instanceof TextView))) {
                    i++;
                } else {
                    this.mDrawable.setBounds(0, childAt.getTop() + ((LayoutParams) childAt.getLayoutParams()).topMargin + Math.round(ViewCompat.getTranslationY(childAt)), width, bottom);
                    this.mDrawable.draw(canvas);
                    return;
                }
            }
        }
    }

    public TileAdapter(Context context) {
        this.mContext = context;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mItemTouchHelper = new ItemTouchHelper(this.mCallbacks);
        this.mDecoration = new TileItemDecoration(context);
        this.mMinNumTiles = context.getResources().getInteger(C2012R$integer.quick_settings_min_num_tiles);
    }

    public void setHost(QSTileHost qSTileHost) {
        this.mHost = qSTileHost;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return this.mItemTouchHelper;
    }

    public ItemDecoration getItemDecoration() {
        return this.mDecoration;
    }

    public void saveSpecs(QSTileHost qSTileHost) {
        ArrayList arrayList = new ArrayList();
        clearAccessibilityState();
        int i = 1;
        while (i < this.mTiles.size() && this.mTiles.get(i) != null) {
            arrayList.add(((TileInfo) this.mTiles.get(i)).spec);
            i++;
        }
        qSTileHost.changeTiles(this.mCurrentSpecs, arrayList);
        this.mCurrentSpecs = arrayList;
    }

    private void clearAccessibilityState() {
        if (this.mAccessibilityAction == 1) {
            List<TileInfo> list = this.mTiles;
            int i = this.mEditIndex - 1;
            this.mEditIndex = i;
            list.remove(i);
            this.mTileDividerIndex--;
            notifyDataSetChanged();
        }
        this.mAccessibilityAction = 0;
    }

    public void resetTileSpecs(QSTileHost qSTileHost, List<String> list) {
        qSTileHost.changeTiles(this.mCurrentSpecs, list);
        setTileSpecs(list);
    }

    public void setTileSpecs(List<String> list) {
        if (!list.equals(this.mCurrentSpecs)) {
            this.mCurrentSpecs = list;
            recalcSpecs();
        }
    }

    public void onTilesChanged(List<TileInfo> list) {
        this.mAllTiles = list;
        recalcSpecs();
    }

    private void recalcSpecs() {
        if (this.mCurrentSpecs != null && this.mAllTiles != null) {
            this.mOtherTiles = new ArrayList(this.mAllTiles);
            this.mTiles.clear();
            this.mTiles.add(null);
            int i = 0;
            for (int i2 = 0; i2 < this.mCurrentSpecs.size(); i2++) {
                TileInfo andRemoveOther = getAndRemoveOther((String) this.mCurrentSpecs.get(i2));
                if (andRemoveOther != null) {
                    this.mTiles.add(andRemoveOther);
                }
            }
            this.mTiles.add(null);
            while (i < this.mOtherTiles.size()) {
                TileInfo tileInfo = (TileInfo) this.mOtherTiles.get(i);
                if (tileInfo.isSystem) {
                    int i3 = i - 1;
                    this.mOtherTiles.remove(i);
                    this.mTiles.add(tileInfo);
                    i = i3;
                }
                i++;
            }
            this.mTileDividerIndex = this.mTiles.size();
            this.mTiles.add(null);
            this.mTiles.addAll(this.mOtherTiles);
            updateDividerLocations();
            notifyDataSetChanged();
        }
    }

    private TileInfo getAndRemoveOther(String str) {
        for (int i = 0; i < this.mOtherTiles.size(); i++) {
            if (((TileInfo) this.mOtherTiles.get(i)).spec.equals(str)) {
                return (TileInfo) this.mOtherTiles.remove(i);
            }
        }
        return null;
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 3;
        }
        if (this.mAccessibilityAction == 1 && i == this.mEditIndex - 1) {
            return 2;
        }
        if (i == this.mTileDividerIndex) {
            return 4;
        }
        if (this.mTiles.get(i) == null) {
            return 1;
        }
        return 0;
    }

    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater from = LayoutInflater.from(context);
        if (i == 3) {
            return new Holder(this, from.inflate(C2013R$layout.qs_customize_header, viewGroup, false));
        }
        if (i == 4) {
            return new Holder(this, from.inflate(C2013R$layout.qs_customize_tile_divider, viewGroup, false));
        }
        if (i == 1) {
            return new Holder(this, from.inflate(C2013R$layout.qs_customize_divider, viewGroup, false));
        }
        FrameLayout frameLayout = (FrameLayout) from.inflate(C2013R$layout.qs_customize_tile_frame, viewGroup, false);
        frameLayout.addView(new CustomizeTileView(context, new QSIconViewImpl(context)));
        return new Holder(this, frameLayout);
    }

    public int getItemCount() {
        return this.mTiles.size();
    }

    public boolean onFailedToRecycleView(Holder holder) {
        holder.clearDrag();
        return true;
    }

    private void setSelectableForHeaders(View view) {
        if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
            int i = 1;
            boolean z = this.mAccessibilityAction == 0;
            view.setFocusable(z);
            if (!z) {
                i = 4;
            }
            view.setImportantForAccessibility(i);
            view.setFocusableInTouchMode(z);
        }
    }

    public void onBindViewHolder(final Holder holder, int i) {
        String str;
        if (holder.getItemViewType() == 3) {
            setSelectableForHeaders(holder.itemView);
            return;
        }
        int i2 = 4;
        boolean z = false;
        if (holder.getItemViewType() == 4) {
            View view = holder.itemView;
            if (this.mTileDividerIndex < this.mTiles.size() - 1) {
                i2 = 0;
            }
            view.setVisibility(i2);
        } else if (holder.getItemViewType() == 1) {
            Resources resources = this.mContext.getResources();
            if (this.mCurrentDrag == null) {
                str = resources.getString(C2017R$string.drag_to_add_tiles);
            } else if (canRemoveTiles() || this.mCurrentDrag.getAdapterPosition() >= this.mEditIndex) {
                str = resources.getString(C2017R$string.drag_to_remove_tiles);
            } else {
                str = resources.getString(C2017R$string.drag_to_remove_disabled, new Object[]{Integer.valueOf(this.mMinNumTiles)});
            }
            ((TextView) holder.itemView.findViewById(16908310)).setText(str);
            setSelectableForHeaders(holder.itemView);
        } else if (holder.getItemViewType() == 2) {
            holder.mTileView.setClickable(true);
            holder.mTileView.setFocusable(true);
            holder.mTileView.setFocusableInTouchMode(true);
            holder.mTileView.setVisibility(0);
            holder.mTileView.setImportantForAccessibility(1);
            holder.mTileView.setContentDescription(this.mContext.getString(C2017R$string.accessibility_qs_edit_tile_add, new Object[]{this.mAccessibilityFromLabel, Integer.valueOf(i)}));
            holder.mTileView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    TileAdapter.this.selectPosition(holder.getAdapterPosition(), view);
                }
            });
            focusOnHolder(holder);
        } else {
            TileInfo tileInfo = (TileInfo) this.mTiles.get(i);
            if (i > this.mEditIndex) {
                QSTile.State state = tileInfo.state;
                state.contentDescription = this.mContext.getString(C2017R$string.accessibility_qs_edit_add_tile_label, new Object[]{state.label});
            } else {
                int i3 = this.mAccessibilityAction;
                if (i3 == 1) {
                    tileInfo.state.contentDescription = this.mContext.getString(C2017R$string.accessibility_qs_edit_tile_add, new Object[]{this.mAccessibilityFromLabel, Integer.valueOf(i)});
                } else if (i3 == 2) {
                    tileInfo.state.contentDescription = this.mContext.getString(C2017R$string.accessibility_qs_edit_tile_move, new Object[]{this.mAccessibilityFromLabel, Integer.valueOf(i)});
                } else {
                    tileInfo.state.contentDescription = this.mContext.getString(C2017R$string.accessibility_qs_edit_tile_label, new Object[]{Integer.valueOf(i), tileInfo.state.label});
                }
            }
            holder.mTileView.handleStateChanged(tileInfo.state);
            holder.mTileView.setShowAppLabel(i > this.mEditIndex && !tileInfo.isSystem);
            if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
                if (this.mAccessibilityAction == 0 || i < this.mEditIndex) {
                    z = true;
                }
                holder.mTileView.setClickable(z);
                holder.mTileView.setFocusable(z);
                CustomizeTileView access$100 = holder.mTileView;
                if (z) {
                    i2 = 1;
                }
                access$100.setImportantForAccessibility(i2);
                holder.mTileView.setFocusableInTouchMode(z);
                if (z) {
                    holder.mTileView.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != -1) {
                                if (TileAdapter.this.mAccessibilityAction != 0) {
                                    TileAdapter.this.selectPosition(adapterPosition, view);
                                } else if (adapterPosition < TileAdapter.this.mEditIndex && TileAdapter.this.canRemoveTiles()) {
                                    TileAdapter.this.showAccessibilityDialog(adapterPosition, view);
                                } else if (adapterPosition >= TileAdapter.this.mEditIndex || TileAdapter.this.canRemoveTiles()) {
                                    TileAdapter.this.startAccessibleAdd(adapterPosition);
                                } else {
                                    TileAdapter.this.startAccessibleMove(adapterPosition);
                                }
                            }
                        }
                    });
                    if (i == this.mAccessibilityFromIndex) {
                        focusOnHolder(holder);
                    }
                }
            }
        }
    }

    private void focusOnHolder(final Holder holder) {
        if (this.mNeedsFocus) {
            holder.mTileView.requestLayout();
            holder.mTileView.addOnLayoutChangeListener(new OnLayoutChangeListener(this) {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    holder.mTileView.removeOnLayoutChangeListener(this);
                    holder.mTileView.requestFocus();
                }
            });
            this.mNeedsFocus = false;
        }
    }

    /* access modifiers changed from: private */
    public boolean canRemoveTiles() {
        return this.mCurrentSpecs.size() > this.mMinNumTiles;
    }

    /* access modifiers changed from: private */
    public void selectPosition(int i, View view) {
        if (this.mAccessibilityAction == 1) {
            List<TileInfo> list = this.mTiles;
            int i2 = this.mEditIndex;
            this.mEditIndex = i2 - 1;
            list.remove(i2);
            notifyItemRemoved(this.mEditIndex);
        }
        this.mAccessibilityAction = 0;
        move(this.mAccessibilityFromIndex, i, view);
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void showAccessibilityDialog(final int i, final View view) {
        final TileInfo tileInfo = (TileInfo) this.mTiles.get(i);
        AlertDialog create = new Builder(this.mContext).setItems(new CharSequence[]{this.mContext.getString(C2017R$string.accessibility_qs_edit_move_tile, new Object[]{tileInfo.state.label}), this.mContext.getString(C2017R$string.accessibility_qs_edit_remove_tile, new Object[]{tileInfo.state.label})}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TileAdapter.this.startAccessibleMove(i);
                    return;
                }
                TileAdapter tileAdapter = TileAdapter.this;
                tileAdapter.move(i, tileInfo.isSystem ? tileAdapter.mEditIndex : tileAdapter.mTileDividerIndex, view);
                TileAdapter tileAdapter2 = TileAdapter.this;
                tileAdapter2.notifyItemChanged(tileAdapter2.mTileDividerIndex);
                TileAdapter.this.notifyDataSetChanged();
            }
        }).setNegativeButton(17039360, null).create();
        SystemUIDialog.setShowForAllUsers(create, true);
        SystemUIDialog.applyFlags(create);
        create.show();
    }

    /* access modifiers changed from: private */
    public void startAccessibleAdd(int i) {
        this.mAccessibilityFromIndex = i;
        this.mAccessibilityFromLabel = ((TileInfo) this.mTiles.get(i)).state.label;
        this.mAccessibilityAction = 1;
        List<TileInfo> list = this.mTiles;
        int i2 = this.mEditIndex;
        this.mEditIndex = i2 + 1;
        list.add(i2, null);
        this.mTileDividerIndex++;
        this.mNeedsFocus = true;
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void startAccessibleMove(int i) {
        this.mAccessibilityFromIndex = i;
        this.mAccessibilityFromLabel = ((TileInfo) this.mTiles.get(i)).state.label;
        this.mAccessibilityAction = 2;
        this.mNeedsFocus = true;
        notifyDataSetChanged();
    }

    public SpanSizeLookup getSizeLookup() {
        return this.mSizeLookup;
    }

    /* access modifiers changed from: private */
    public boolean move(int i, int i2, View view) {
        if (i2 == i) {
            return true;
        }
        CharSequence charSequence = ((TileInfo) this.mTiles.get(i)).state.label;
        move(i, i2, this.mTiles);
        updateDividerLocations();
        int i3 = this.mEditIndex;
        if (i2 >= i3) {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_REMOVE, 0, strip((TileInfo) this.mTiles.get(i2)));
        } else if (i >= i3) {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_ADD, 0, strip((TileInfo) this.mTiles.get(i2)));
        } else {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_MOVE, 0, strip((TileInfo) this.mTiles.get(i2)));
        }
        saveSpecs(this.mHost);
        return true;
    }

    private void updateDividerLocations() {
        this.mEditIndex = -1;
        this.mTileDividerIndex = this.mTiles.size();
        for (int i = 1; i < this.mTiles.size(); i++) {
            if (this.mTiles.get(i) == null) {
                if (this.mEditIndex == -1) {
                    this.mEditIndex = i;
                } else {
                    this.mTileDividerIndex = i;
                }
            }
        }
        int size = this.mTiles.size() - 1;
        int i2 = this.mTileDividerIndex;
        if (size == i2) {
            notifyItemChanged(i2);
        }
    }

    private static String strip(TileInfo tileInfo) {
        String str = tileInfo.spec;
        return str.startsWith("custom(") ? CustomTile.getComponentFromSpec(str).getPackageName() : str;
    }

    private <T> void move(int i, int i2, List<T> list) {
        list.add(i2, list.remove(i));
        notifyItemMoved(i, i2);
    }
}
