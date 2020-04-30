package com.android.settingslib.net;

import android.content.Context;
import android.net.NetworkTemplate;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.util.ArrayUtils;

public class DataUsageUtils {
    public static NetworkTemplate getMobileTemplate(Context context, int i) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        NetworkTemplate buildTemplateMobileAll = NetworkTemplate.buildTemplateMobileAll(telephonyManager.getSubscriberId());
        String str = "DataUsageUtils";
        if (!subscriptionManager.isActiveSubscriptionId(i)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Subscription is not active: ");
            sb.append(i);
            Log.i(str, sb.toString());
            return buildTemplateMobileAll;
        }
        String[] mergedImsisFromGroup = telephonyManager.createForSubscriptionId(i).getMergedImsisFromGroup();
        if (!ArrayUtils.isEmpty(mergedImsisFromGroup)) {
            return NetworkTemplate.normalize(buildTemplateMobileAll, mergedImsisFromGroup);
        }
        Log.i(str, "mergedSubscriberIds is null.");
        return buildTemplateMobileAll;
    }
}
