package com.android.systemui.controls;

import android.os.UserHandle;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UserAwareController.kt */
public interface UserAwareController {

    /* compiled from: UserAwareController.kt */
    public static final class DefaultImpls {
        public static void changeUser(UserAwareController userAwareController, UserHandle userHandle) {
            Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
        }
    }

    void changeUser(UserHandle userHandle);

    int getCurrentUserId();
}
