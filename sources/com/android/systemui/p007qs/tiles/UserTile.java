package com.android.systemui.p007qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.p006qs.DetailAdapter;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.State;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener;
import com.android.systemui.statusbar.policy.UserSwitcherController;

/* renamed from: com.android.systemui.qs.tiles.UserTile */
public class UserTile extends QSTileImpl<State> implements OnUserInfoChangedListener {
    private Pair<String, Drawable> mLastUpdate;
    private final UserInfoController mUserInfoController;
    private final UserSwitcherController mUserSwitcherController;

    public int getMetricsCategory() {
        return 260;
    }

    public UserTile(QSHost qSHost, UserSwitcherController userSwitcherController, UserInfoController userInfoController) {
        super(qSHost);
        this.mUserSwitcherController = userSwitcherController;
        this.mUserInfoController = userInfoController;
        userInfoController.observe(getLifecycle(), this);
    }

    public State newTileState() {
        return new State();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.USER_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        showDetail(true);
    }

    public DetailAdapter getDetailAdapter() {
        return this.mUserSwitcherController.userDetailAdapter;
    }

    public CharSequence getTileLabel() {
        return getState().label;
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(State state, Object obj) {
        final Pair<String, Drawable> pair = obj != null ? (Pair) obj : this.mLastUpdate;
        if (pair != null) {
            Object obj2 = pair.first;
            state.label = (CharSequence) obj2;
            state.contentDescription = (CharSequence) obj2;
            state.icon = new Icon(this) {
                public Drawable getDrawable(Context context) {
                    return (Drawable) pair.second;
                }
            };
        }
    }

    public void onUserInfoChanged(String str, Drawable drawable, String str2) {
        Pair<String, Drawable> pair = new Pair<>(str, drawable);
        this.mLastUpdate = pair;
        refreshState(pair);
    }
}
