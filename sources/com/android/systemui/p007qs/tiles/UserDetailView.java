package com.android.systemui.p007qs.tiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.C2013R$layout;
import com.android.systemui.p007qs.PseudoGridView;
import com.android.systemui.p007qs.PseudoGridView.ViewGroupAdapterBridge;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController.BaseUserAdapter;
import com.android.systemui.statusbar.policy.UserSwitcherController.UserRecord;

/* renamed from: com.android.systemui.qs.tiles.UserDetailView */
public class UserDetailView extends PseudoGridView {
    protected Adapter mAdapter;

    /* renamed from: com.android.systemui.qs.tiles.UserDetailView$Adapter */
    public static class Adapter extends BaseUserAdapter implements OnClickListener {
        private final Context mContext;
        protected UserSwitcherController mController;
        private View mCurrentUserView;

        public Adapter(Context context, UserSwitcherController userSwitcherController) {
            super(userSwitcherController);
            this.mContext = context;
            this.mController = userSwitcherController;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            return createUserDetailItemView(view, viewGroup, getItem(i));
        }

        public UserDetailItemView createUserDetailItemView(View view, ViewGroup viewGroup, UserRecord userRecord) {
            UserDetailItemView convertOrInflate = UserDetailItemView.convertOrInflate(this.mContext, view, viewGroup);
            if (!userRecord.isCurrent || userRecord.isGuest) {
                convertOrInflate.setOnClickListener(this);
            } else {
                convertOrInflate.setOnClickListener(null);
                convertOrInflate.setClickable(false);
            }
            String name = getName(this.mContext, userRecord);
            Bitmap bitmap = userRecord.picture;
            if (bitmap == null) {
                convertOrInflate.bind(name, getDrawable(this.mContext, userRecord), userRecord.resolveId());
            } else {
                convertOrInflate.bind(name, bitmap, userRecord.info.id);
            }
            convertOrInflate.setActivated(userRecord.isCurrent);
            if (userRecord.isCurrent) {
                this.mCurrentUserView = convertOrInflate;
            }
            convertOrInflate.setDisabledByAdmin(userRecord.isDisabledByAdmin);
            if (!userRecord.isSwitchToEnabled) {
                convertOrInflate.setEnabled(false);
            }
            convertOrInflate.setTag(userRecord);
            return convertOrInflate;
        }

        public void onClick(View view) {
            UserRecord userRecord = (UserRecord) view.getTag();
            if (userRecord.isDisabledByAdmin) {
                this.mController.startActivity(RestrictedLockUtils.getShowAdminSupportDetailsIntent(this.mContext, userRecord.enforcedAdmin));
            } else if (userRecord.isSwitchToEnabled) {
                MetricsLogger.action(this.mContext, 156);
                if (!userRecord.isAddUser && !userRecord.isRestricted && !userRecord.isDisabledByAdmin) {
                    View view2 = this.mCurrentUserView;
                    if (view2 != null) {
                        view2.setActivated(false);
                    }
                    view.setActivated(true);
                }
                switchTo(userRecord);
            }
        }
    }

    public UserDetailView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public static UserDetailView inflate(Context context, ViewGroup viewGroup, boolean z) {
        return (UserDetailView) LayoutInflater.from(context).inflate(C2013R$layout.qs_user_detail, viewGroup, z);
    }

    public void createAndSetAdapter(UserSwitcherController userSwitcherController) {
        Adapter adapter = new Adapter(this.mContext, userSwitcherController);
        this.mAdapter = adapter;
        ViewGroupAdapterBridge.link(this, adapter);
    }

    public void refreshAdapter() {
        this.mAdapter.refresh();
    }
}
