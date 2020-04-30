package com.android.systemui.recents;

import android.content.Context;
import com.android.systemui.C2017R$string;
import com.android.systemui.dagger.ContextComponentHelper;

public abstract class RecentsModule {
    public static RecentsImplementation provideRecentsImpl(Context context, ContextComponentHelper contextComponentHelper) {
        String string = context.getString(C2017R$string.config_recentsComponent);
        if (string == null || string.length() == 0) {
            throw new RuntimeException("No recents component configured", null);
        }
        RecentsImplementation resolveRecents = contextComponentHelper.resolveRecents(string);
        if (resolveRecents != null) {
            return resolveRecents;
        }
        try {
            try {
                return (RecentsImplementation) context.getClassLoader().loadClass(string).newInstance();
            } catch (Throwable th) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error creating recents component: ");
                sb.append(string);
                throw new RuntimeException(sb.toString(), th);
            }
        } catch (Throwable th2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Error loading recents component: ");
            sb2.append(string);
            throw new RuntimeException(sb2.toString(), th2);
        }
    }
}
