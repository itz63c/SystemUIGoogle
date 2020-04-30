package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManager.EnforcingUser;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.util.List;

public class RestrictedLockUtilsInternal extends RestrictedLockUtils {
    static Proxy sProxy = new Proxy();

    static class Proxy {
        Proxy() {
        }
    }

    public static EnforcedAdmin checkIfRestrictionEnforced(Context context, String str, int i) {
        EnforcedAdmin enforcedAdmin;
        EnforcedAdmin enforcedAdmin2;
        if (((DevicePolicyManager) context.getSystemService("device_policy")) == null) {
            return null;
        }
        UserManager userManager = UserManager.get(context);
        List userRestrictionSources = userManager.getUserRestrictionSources(str, UserHandle.of(i));
        if (userRestrictionSources.isEmpty()) {
            return null;
        }
        if (userRestrictionSources.size() > 1) {
            return EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(str);
        }
        int userRestrictionSource = ((EnforcingUser) userRestrictionSources.get(0)).getUserRestrictionSource();
        int identifier = ((EnforcingUser) userRestrictionSources.get(0)).getUserHandle().getIdentifier();
        if (userRestrictionSource == 4) {
            if (identifier == i) {
                return getProfileOwner(context, str, identifier);
            }
            UserInfo profileParent = userManager.getProfileParent(identifier);
            if (profileParent == null || profileParent.id != i) {
                enforcedAdmin2 = EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(str);
            } else {
                enforcedAdmin2 = getProfileOwner(context, str, identifier);
            }
            return enforcedAdmin2;
        } else if (userRestrictionSource != 2) {
            return null;
        } else {
            if (identifier == i) {
                enforcedAdmin = getDeviceOwner(context, str);
            } else {
                enforcedAdmin = EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(str);
            }
            return enforcedAdmin;
        }
    }

    public static boolean hasBaseUserRestriction(Context context, String str, int i) {
        return ((UserManager) context.getSystemService("user")).hasBaseUserRestriction(str, UserHandle.of(i));
    }

    private static UserHandle getUserHandleOf(int i) {
        if (i == -10000) {
            return null;
        }
        return UserHandle.of(i);
    }

    private static EnforcedAdmin getDeviceOwner(Context context, String str) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        EnforcedAdmin enforcedAdmin = null;
        if (devicePolicyManager == null) {
            return null;
        }
        ComponentName deviceOwnerComponentOnAnyUser = devicePolicyManager.getDeviceOwnerComponentOnAnyUser();
        if (deviceOwnerComponentOnAnyUser != null) {
            enforcedAdmin = new EnforcedAdmin(deviceOwnerComponentOnAnyUser, str, devicePolicyManager.getDeviceOwnerUser());
        }
        return enforcedAdmin;
    }

    private static EnforcedAdmin getProfileOwner(Context context, String str, int i) {
        EnforcedAdmin enforcedAdmin = null;
        if (i == -10000) {
            return null;
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null) {
            return null;
        }
        ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(i);
        if (profileOwnerAsUser != null) {
            enforcedAdmin = new EnforcedAdmin(profileOwnerAsUser, str, getUserHandleOf(i));
        }
        return enforcedAdmin;
    }
}
