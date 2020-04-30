package androidx.mediarouter.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import androidx.mediarouter.R$id;
import androidx.mediarouter.R$integer;
import androidx.mediarouter.R$layout;
import androidx.mediarouter.R$string;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.MediaRouter.Callback;
import androidx.mediarouter.media.MediaRouter.RouteInfo;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaRouteDynamicChooserDialog extends AppCompatDialog {
    private RecyclerAdapter mAdapter;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private ImageButton mCloseButton;
    Context mContext;
    private final Handler mHandler;
    private long mLastUpdateTime;
    private RecyclerView mRecyclerView;
    final MediaRouter mRouter;
    List<RouteInfo> mRoutes;
    private MediaRouteSelector mSelector;
    private long mUpdateRoutesDelayMs;

    private final class MediaRouterCallback extends Callback {
        MediaRouterCallback() {
        }

        public void onRouteAdded(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.refreshRoutes();
        }

        public void onRouteRemoved(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.refreshRoutes();
        }

        public void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.refreshRoutes();
        }

        public void onRouteSelected(MediaRouter mediaRouter, RouteInfo routeInfo) {
            MediaRouteDynamicChooserDialog.this.dismiss();
        }
    }

    private final class RecyclerAdapter extends Adapter<ViewHolder> {
        private final Drawable mDefaultIcon;
        private final LayoutInflater mInflater;
        private final ArrayList<Item> mItems = new ArrayList<>();
        private final Drawable mSpeakerGroupIcon;
        private final Drawable mSpeakerIcon;
        private final Drawable mTvIcon;

        private class HeaderViewHolder extends ViewHolder {
            TextView mTextView;

            HeaderViewHolder(RecyclerAdapter recyclerAdapter, View view) {
                super(view);
                this.mTextView = (TextView) view.findViewById(R$id.mr_picker_header_name);
            }

            public void bindHeaderView(Item item) {
                this.mTextView.setText(item.getData().toString());
            }
        }

        private class Item {
            private final Object mData;
            private final int mType;

            Item(RecyclerAdapter recyclerAdapter, Object obj) {
                this.mData = obj;
                if (obj instanceof String) {
                    this.mType = 1;
                } else if (obj instanceof RouteInfo) {
                    this.mType = 2;
                } else {
                    this.mType = 0;
                    Log.w("RecyclerAdapter", "Wrong type of data passed to Item constructor");
                }
            }

            public Object getData() {
                return this.mData;
            }

            public int getType() {
                return this.mType;
            }
        }

        private class RouteViewHolder extends ViewHolder {
            final ImageView mImageView;
            final View mItemView;
            final ProgressBar mProgressBar;
            final TextView mTextView;

            RouteViewHolder(View view) {
                super(view);
                this.mItemView = view;
                this.mImageView = (ImageView) view.findViewById(R$id.mr_picker_route_icon);
                this.mProgressBar = (ProgressBar) view.findViewById(R$id.mr_picker_route_progress_bar);
                this.mTextView = (TextView) view.findViewById(R$id.mr_picker_route_name);
                MediaRouterThemeHelper.setIndeterminateProgressBarColor(MediaRouteDynamicChooserDialog.this.mContext, this.mProgressBar);
            }

            public void bindRouteView(Item item) {
                final RouteInfo routeInfo = (RouteInfo) item.getData();
                this.mItemView.setVisibility(0);
                this.mProgressBar.setVisibility(4);
                this.mItemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        routeInfo.select();
                        RouteViewHolder.this.mImageView.setVisibility(4);
                        RouteViewHolder.this.mProgressBar.setVisibility(0);
                    }
                });
                this.mTextView.setText(routeInfo.getName());
                this.mImageView.setImageDrawable(RecyclerAdapter.this.getIconDrawable(routeInfo));
            }
        }

        RecyclerAdapter() {
            this.mInflater = LayoutInflater.from(MediaRouteDynamicChooserDialog.this.mContext);
            this.mDefaultIcon = MediaRouterThemeHelper.getDefaultDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            this.mTvIcon = MediaRouterThemeHelper.getTvDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            this.mSpeakerIcon = MediaRouterThemeHelper.getSpeakerDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            this.mSpeakerGroupIcon = MediaRouterThemeHelper.getSpeakerGroupDrawableIcon(MediaRouteDynamicChooserDialog.this.mContext);
            rebuildItems();
        }

        /* access modifiers changed from: 0000 */
        public void rebuildItems() {
            this.mItems.clear();
            this.mItems.add(new Item(this, MediaRouteDynamicChooserDialog.this.mContext.getString(R$string.mr_chooser_title)));
            for (RouteInfo item : MediaRouteDynamicChooserDialog.this.mRoutes) {
                this.mItems.add(new Item(this, item));
            }
            notifyDataSetChanged();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 1) {
                return new HeaderViewHolder(this, this.mInflater.inflate(R$layout.mr_picker_header_item, viewGroup, false));
            }
            if (i == 2) {
                return new RouteViewHolder(this.mInflater.inflate(R$layout.mr_picker_route_item, viewGroup, false));
            }
            Log.w("RecyclerAdapter", "Cannot create ViewHolder because of wrong view type");
            return null;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(i);
            Item item = getItem(i);
            if (itemViewType == 1) {
                ((HeaderViewHolder) viewHolder).bindHeaderView(item);
            } else if (itemViewType != 2) {
                Log.w("RecyclerAdapter", "Cannot bind item to ViewHolder because of wrong view type");
            } else {
                ((RouteViewHolder) viewHolder).bindRouteView(item);
            }
        }

        public int getItemCount() {
            return this.mItems.size();
        }

        /* access modifiers changed from: 0000 */
        public Drawable getIconDrawable(RouteInfo routeInfo) {
            Uri iconUri = routeInfo.getIconUri();
            if (iconUri != null) {
                try {
                    Drawable createFromStream = Drawable.createFromStream(MediaRouteDynamicChooserDialog.this.mContext.getContentResolver().openInputStream(iconUri), null);
                    if (createFromStream != null) {
                        return createFromStream;
                    }
                } catch (IOException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to load ");
                    sb.append(iconUri);
                    Log.w("RecyclerAdapter", sb.toString(), e);
                }
            }
            return getDefaultIconDrawable(routeInfo);
        }

        private Drawable getDefaultIconDrawable(RouteInfo routeInfo) {
            int deviceType = routeInfo.getDeviceType();
            if (deviceType == 1) {
                return this.mTvIcon;
            }
            if (deviceType == 2) {
                return this.mSpeakerIcon;
            }
            if (routeInfo.isGroup()) {
                return this.mSpeakerGroupIcon;
            }
            return this.mDefaultIcon;
        }

        public int getItemViewType(int i) {
            return ((Item) this.mItems.get(i)).getType();
        }

        public Item getItem(int i) {
            return (Item) this.mItems.get(i);
        }
    }

    static final class RouteComparator implements Comparator<RouteInfo> {
        public static final RouteComparator sInstance = new RouteComparator();

        RouteComparator() {
        }

        public int compare(RouteInfo routeInfo, RouteInfo routeInfo2) {
            return routeInfo.getName().compareToIgnoreCase(routeInfo2.getName());
        }
    }

    public MediaRouteDynamicChooserDialog(Context context) {
        this(context, 0);
    }

    public MediaRouteDynamicChooserDialog(Context context, int i) {
        Context createThemedDialogContext = MediaRouterThemeHelper.createThemedDialogContext(context, i, false);
        super(createThemedDialogContext, MediaRouterThemeHelper.createThemedDialogStyle(createThemedDialogContext));
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mHandler = new Handler() {
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    MediaRouteDynamicChooserDialog.this.updateRoutes((List) message.obj);
                }
            }
        };
        Context context2 = getContext();
        this.mRouter = MediaRouter.getInstance(context2);
        this.mCallback = new MediaRouterCallback();
        this.mContext = context2;
        this.mUpdateRoutesDelayMs = (long) context2.getResources().getInteger(R$integer.mr_update_routes_delay_ms);
    }

    public void setRouteSelector(MediaRouteSelector mediaRouteSelector) {
        if (mediaRouteSelector == null) {
            throw new IllegalArgumentException("selector must not be null");
        } else if (!this.mSelector.equals(mediaRouteSelector)) {
            this.mSelector = mediaRouteSelector;
            if (this.mAttachedToWindow) {
                this.mRouter.removeCallback(this.mCallback);
                this.mRouter.addCallback(mediaRouteSelector, this.mCallback, 1);
            }
            refreshRoutes();
        }
    }

    public void onFilterRoutes(List<RouteInfo> list) {
        int size = list.size();
        while (true) {
            int i = size - 1;
            if (size > 0) {
                if (!onFilterRoute((RouteInfo) list.get(i))) {
                    list.remove(i);
                }
                size = i;
            } else {
                return;
            }
        }
    }

    public boolean onFilterRoute(RouteInfo routeInfo) {
        return !routeInfo.isDefaultOrBluetooth() && routeInfo.isEnabled() && routeInfo.matchesSelector(this.mSelector);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.mr_picker_dialog);
        MediaRouterThemeHelper.setDialogBackgroundColor(this.mContext, this);
        this.mRoutes = new ArrayList();
        ImageButton imageButton = (ImageButton) findViewById(R$id.mr_picker_close_button);
        this.mCloseButton = imageButton;
        imageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MediaRouteDynamicChooserDialog.this.dismiss();
            }
        });
        this.mAdapter = new RecyclerAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R$id.mr_picker_list);
        this.mRecyclerView = recyclerView;
        recyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        updateLayout();
    }

    /* access modifiers changed from: 0000 */
    public void updateLayout() {
        getWindow().setLayout(MediaRouteDialogHelper.getDialogWidthForDynamicGroup(this.mContext), MediaRouteDialogHelper.getDialogHeight(this.mContext));
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(this.mSelector, this.mCallback, 1);
        refreshRoutes();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttachedToWindow = false;
        this.mRouter.removeCallback(this.mCallback);
        this.mHandler.removeMessages(1);
    }

    public void refreshRoutes() {
        if (this.mAttachedToWindow) {
            ArrayList arrayList = new ArrayList(this.mRouter.getRoutes());
            onFilterRoutes(arrayList);
            Collections.sort(arrayList, RouteComparator.sInstance);
            if (SystemClock.uptimeMillis() - this.mLastUpdateTime >= this.mUpdateRoutesDelayMs) {
                updateRoutes(arrayList);
                return;
            }
            this.mHandler.removeMessages(1);
            Handler handler = this.mHandler;
            handler.sendMessageAtTime(handler.obtainMessage(1, arrayList), this.mLastUpdateTime + this.mUpdateRoutesDelayMs);
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateRoutes(List<RouteInfo> list) {
        this.mLastUpdateTime = SystemClock.uptimeMillis();
        this.mRoutes.clear();
        this.mRoutes.addAll(list);
        this.mAdapter.rebuildItems();
    }
}
