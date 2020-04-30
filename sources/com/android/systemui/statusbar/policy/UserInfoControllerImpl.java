package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import com.android.internal.util.UserIcons;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2018R$style;
import com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener;
import java.util.ArrayList;
import java.util.Iterator;

public class UserInfoControllerImpl implements UserInfoController {
    private final ArrayList<OnUserInfoChangedListener> mCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Context mContext;
    private final BroadcastReceiver mProfileReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.provider.Contacts.PROFILE_CHANGED".equals(action) || "android.intent.action.USER_INFO_CHANGED".equals(action)) {
                try {
                    if (intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId()) == ActivityManager.getService().getCurrentUser().id) {
                        UserInfoControllerImpl.this.reloadUserInfo();
                    }
                } catch (RemoteException e) {
                    Log.e("UserInfoController", "Couldn't get current user id for profile change", e);
                }
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                UserInfoControllerImpl.this.reloadUserInfo();
            }
        }
    };
    /* access modifiers changed from: private */
    public String mUserAccount;
    /* access modifiers changed from: private */
    public Drawable mUserDrawable;
    /* access modifiers changed from: private */
    public AsyncTask<Void, Void, UserInfoQueryResult> mUserInfoTask;
    /* access modifiers changed from: private */
    public String mUserName;

    private static class UserInfoQueryResult {
        private Drawable mAvatar;
        private String mName;
        private String mUserAccount;

        public UserInfoQueryResult(String str, Drawable drawable, String str2) {
            this.mName = str;
            this.mAvatar = drawable;
            this.mUserAccount = str2;
        }

        public String getName() {
            return this.mName;
        }

        public Drawable getAvatar() {
            return this.mAvatar;
        }

        public String getUserAccount() {
            return this.mUserAccount;
        }
    }

    public UserInfoControllerImpl(Context context) {
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.provider.Contacts.PROFILE_CHANGED");
        intentFilter2.addAction("android.intent.action.USER_INFO_CHANGED");
        this.mContext.registerReceiverAsUser(this.mProfileReceiver, UserHandle.ALL, intentFilter2, null, null);
    }

    public void addCallback(OnUserInfoChangedListener onUserInfoChangedListener) {
        this.mCallbacks.add(onUserInfoChangedListener);
        onUserInfoChangedListener.onUserInfoChanged(this.mUserName, this.mUserDrawable, this.mUserAccount);
    }

    public void removeCallback(OnUserInfoChangedListener onUserInfoChangedListener) {
        this.mCallbacks.remove(onUserInfoChangedListener);
    }

    public void reloadUserInfo() {
        AsyncTask<Void, Void, UserInfoQueryResult> asyncTask = this.mUserInfoTask;
        if (asyncTask != null) {
            asyncTask.cancel(false);
            this.mUserInfoTask = null;
        }
        queryForUserInformation();
    }

    private void queryForUserInformation() {
        String str = "UserInfoController";
        try {
            UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            final Context createPackageContextAsUser = this.mContext.createPackageContextAsUser("android", 0, new UserHandle(currentUser.id));
            final int i = currentUser.id;
            final boolean isGuest = currentUser.isGuest();
            final String str2 = currentUser.name;
            final boolean z = this.mContext.getThemeResId() != C2018R$style.Theme_SystemUI_Light;
            Resources resources = this.mContext.getResources();
            final int max = Math.max(resources.getDimensionPixelSize(C2009R$dimen.multi_user_avatar_expanded_size), resources.getDimensionPixelSize(C2009R$dimen.multi_user_avatar_keyguard_size));
            C16873 r6 = new AsyncTask<Void, Void, UserInfoQueryResult>() {
                /* access modifiers changed from: protected */
                public UserInfoQueryResult doInBackground(Void... voidArr) {
                    Drawable drawable;
                    UserManager userManager = UserManager.get(UserInfoControllerImpl.this.mContext);
                    String str = str2;
                    Bitmap userIcon = userManager.getUserIcon(i);
                    if (userIcon != null) {
                        UserIconDrawable userIconDrawable = new UserIconDrawable(max);
                        userIconDrawable.setIcon(userIcon);
                        userIconDrawable.setBadgeIfManagedUser(UserInfoControllerImpl.this.mContext, i);
                        userIconDrawable.bake();
                        drawable = userIconDrawable;
                    } else {
                        drawable = UserIcons.getDefaultUserIcon(createPackageContextAsUser.getResources(), isGuest ? -10000 : i, z);
                    }
                    if (userManager.getUsers().size() <= 1) {
                        String str2 = "display_name";
                        Cursor query = createPackageContextAsUser.getContentResolver().query(Profile.CONTENT_URI, new String[]{"_id", str2}, null, null, null);
                        if (query != null) {
                            try {
                                if (query.moveToFirst()) {
                                    str = query.getString(query.getColumnIndex(str2));
                                }
                            } finally {
                                query.close();
                            }
                        }
                    }
                    return new UserInfoQueryResult(str, drawable, userManager.getUserAccount(i));
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(UserInfoQueryResult userInfoQueryResult) {
                    UserInfoControllerImpl.this.mUserName = userInfoQueryResult.getName();
                    UserInfoControllerImpl.this.mUserDrawable = userInfoQueryResult.getAvatar();
                    UserInfoControllerImpl.this.mUserAccount = userInfoQueryResult.getUserAccount();
                    UserInfoControllerImpl.this.mUserInfoTask = null;
                    UserInfoControllerImpl.this.notifyChanged();
                }
            };
            this.mUserInfoTask = r6;
            r6.execute(new Void[0]);
        } catch (NameNotFoundException e) {
            Log.e(str, "Couldn't create user context", e);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            Log.e(str, "Couldn't get user info", e2);
            throw new RuntimeException(e2);
        }
    }

    /* access modifiers changed from: private */
    public void notifyChanged() {
        Iterator it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            ((OnUserInfoChangedListener) it.next()).onUserInfoChanged(this.mUserName, this.mUserDrawable, this.mUserAccount);
        }
    }

    public void onDensityOrFontScaleChanged() {
        reloadUserInfo();
    }
}
