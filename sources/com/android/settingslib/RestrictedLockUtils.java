package com.android.settingslib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.Objects;

public class RestrictedLockUtils {

    public static class EnforcedAdmin {
        public ComponentName component = null;
        public String enforcedRestriction = null;
        public UserHandle user = null;

        public static EnforcedAdmin createDefaultEnforcedAdminWithRestriction(String str) {
            EnforcedAdmin enforcedAdmin = new EnforcedAdmin();
            enforcedAdmin.enforcedRestriction = str;
            return enforcedAdmin;
        }

        public EnforcedAdmin(ComponentName componentName, String str, UserHandle userHandle) {
            this.component = componentName;
            this.enforcedRestriction = str;
            this.user = userHandle;
        }

        public EnforcedAdmin() {
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj == null || EnforcedAdmin.class != obj.getClass()) {
                return false;
            }
            EnforcedAdmin enforcedAdmin = (EnforcedAdmin) obj;
            if (!Objects.equals(this.user, enforcedAdmin.user) || !Objects.equals(this.component, enforcedAdmin.component) || !Objects.equals(this.enforcedRestriction, enforcedAdmin.enforcedRestriction)) {
                z = false;
            }
            return z;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.component, this.enforcedRestriction, this.user});
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("EnforcedAdmin{component=");
            sb.append(this.component);
            sb.append(", enforcedRestriction='");
            sb.append(this.enforcedRestriction);
            sb.append(", user=");
            sb.append(this.user);
            sb.append('}');
            return sb.toString();
        }
    }

    public static void sendShowAdminSupportDetailsIntent(Context context, EnforcedAdmin enforcedAdmin) {
        Intent showAdminSupportDetailsIntent = getShowAdminSupportDetailsIntent(context, enforcedAdmin);
        int myUserId = UserHandle.myUserId();
        if (enforcedAdmin != null) {
            UserHandle userHandle = enforcedAdmin.user;
            if (userHandle != null && isCurrentUserOrProfile(context, userHandle.getIdentifier())) {
                myUserId = enforcedAdmin.user.getIdentifier();
            }
            showAdminSupportDetailsIntent.putExtra("android.app.extra.RESTRICTION", enforcedAdmin.enforcedRestriction);
        }
        context.startActivityAsUser(showAdminSupportDetailsIntent, UserHandle.of(myUserId));
    }

    public static Intent getShowAdminSupportDetailsIntent(Context context, EnforcedAdmin enforcedAdmin) {
        Intent intent = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
        if (enforcedAdmin != null) {
            ComponentName componentName = enforcedAdmin.component;
            if (componentName != null) {
                intent.putExtra("android.app.extra.DEVICE_ADMIN", componentName);
            }
            intent.putExtra("android.intent.extra.USER", enforcedAdmin.user);
        }
        return intent;
    }

    public static boolean isCurrentUserOrProfile(Context context, int i) {
        return ((UserManager) context.getSystemService(UserManager.class)).getUserProfiles().contains(UserHandle.of(i));
    }
}
