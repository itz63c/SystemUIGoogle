package com.google.android.systemui.smartspace;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2015R$plurals;
import com.android.systemui.C2017R$string;
import com.android.systemui.smartspace.nano.SmartspaceProto$CardWrapper;
import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceUpdate.SmartspaceCard;
import com.android.systemui.smartspace.nano.SmartspaceProto.SmartspaceUpdate.SmartspaceCard.ExpiryCriteria;
import com.android.systemui.smartspace.nano.SmartspaceProto.SmartspaceUpdate.SmartspaceCard.Message;
import com.android.systemui.smartspace.nano.SmartspaceProto.SmartspaceUpdate.SmartspaceCard.Message.FormattedText;
import com.android.systemui.smartspace.nano.SmartspaceProto.SmartspaceUpdate.SmartspaceCard.Message.FormattedText.FormatParam;

public class SmartSpaceCard {
    private static int sRequestCode;
    private final SmartspaceCard mCard;
    private final Context mContext;
    private Bitmap mIcon;
    private boolean mIconProcessed;
    private final Intent mIntent;
    private final long mPublishTime;
    private int mRequestCode;

    public SmartSpaceCard(Context context, SmartspaceCard smartspaceCard, Intent intent, boolean z, Bitmap bitmap, boolean z2, long j) {
        this.mContext = context.getApplicationContext();
        this.mCard = smartspaceCard;
        this.mIntent = intent;
        this.mIcon = bitmap;
        this.mPublishTime = j;
        int i = sRequestCode + 1;
        sRequestCode = i;
        if (i > 2147483646) {
            sRequestCode = 0;
        }
        this.mRequestCode = sRequestCode;
    }

    public boolean isSensitive() {
        return this.mCard.isSensitive;
    }

    public boolean isWorkProfile() {
        return this.mCard.isWorkProfile;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public Bitmap getIcon() {
        return this.mIcon;
    }

    public void setIcon(Bitmap bitmap) {
        this.mIcon = bitmap;
    }

    public void setIconProcessed(boolean z) {
        this.mIconProcessed = z;
    }

    public boolean isIconProcessed() {
        return this.mIconProcessed;
    }

    public String getTitle() {
        return substitute(true);
    }

    public CharSequence getFormattedTitle() {
        FormatParam[] formatParamArr;
        Message message = getMessage();
        String str = "";
        if (message == null) {
            return str;
        }
        FormattedText formattedText = message.title;
        if (formattedText != null) {
            String str2 = formattedText.text;
            if (str2 != null) {
                if (!hasParams(formattedText)) {
                    return str2;
                }
                String str3 = null;
                String str4 = null;
                int i = 0;
                while (true) {
                    formatParamArr = formattedText.formatParam;
                    if (i >= formatParamArr.length) {
                        break;
                    }
                    FormatParam formatParam = formatParamArr[i];
                    if (formatParam != null) {
                        int i2 = formatParam.formatParamArgs;
                        if (i2 == 1 || i2 == 2) {
                            str4 = getDurationText(formatParam);
                        } else if (i2 == 3) {
                            str3 = formatParam.text;
                        }
                    }
                    i++;
                }
                if (this.mCard.cardType == 3 && formatParamArr.length == 2) {
                    str4 = formatParamArr[0].text;
                    str3 = formatParamArr[1].text;
                }
                if (str3 == null) {
                    return str;
                }
                if (str4 == null) {
                    if (message != this.mCard.duringEvent) {
                        return str2;
                    }
                    str4 = this.mContext.getString(C2017R$string.smartspace_now);
                }
                return this.mContext.getString(C2017R$string.smartspace_pill_text_format, new Object[]{str4, str3});
            }
        }
        return str;
    }

    public String getSubtitle() {
        return substitute(false);
    }

    private Message getMessage() {
        long currentTimeMillis = System.currentTimeMillis();
        SmartspaceCard smartspaceCard = this.mCard;
        long j = smartspaceCard.eventTimeMillis;
        long j2 = smartspaceCard.eventDurationMillis + j;
        if (currentTimeMillis < j) {
            Message message = smartspaceCard.preEvent;
            if (message != null) {
                return message;
            }
        }
        if (currentTimeMillis > j2) {
            Message message2 = this.mCard.postEvent;
            if (message2 != null) {
                return message2;
            }
        }
        Message message3 = this.mCard.duringEvent;
        if (message3 != null) {
            return message3;
        }
        return null;
    }

    private FormattedText getFormattedText(boolean z) {
        Message message = getMessage();
        if (message == null) {
            return null;
        }
        return z ? message.title : message.subtitle;
    }

    private boolean hasParams(FormattedText formattedText) {
        if (!(formattedText == null || formattedText.text == null)) {
            FormatParam[] formatParamArr = formattedText.formatParam;
            if (formatParamArr != null && formatParamArr.length > 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public long getMillisToEvent(FormatParam formatParam) {
        long j;
        if (formatParam.formatParamArgs == 2) {
            SmartspaceCard smartspaceCard = this.mCard;
            j = smartspaceCard.eventTimeMillis + smartspaceCard.eventDurationMillis;
        } else {
            j = this.mCard.eventTimeMillis;
        }
        return Math.abs(System.currentTimeMillis() - j);
    }

    private int getMinutesToEvent(FormatParam formatParam) {
        return (int) Math.ceil(((double) getMillisToEvent(formatParam)) / 60000.0d);
    }

    private String substitute(boolean z) {
        return substitute(z, null);
    }

    private String[] getTextArgs(FormatParam[] formatParamArr, String str) {
        int length = formatParamArr.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            int i2 = formatParamArr[i].formatParamArgs;
            if (i2 == 1 || i2 == 2) {
                strArr[i] = getDurationText(formatParamArr[i]);
            } else {
                String str2 = "";
                if (i2 != 3) {
                    strArr[i] = str2;
                } else if (str == null || formatParamArr[i].truncateLocation == 0) {
                    if (formatParamArr[i].text != null) {
                        str2 = formatParamArr[i].text;
                    }
                    strArr[i] = str2;
                } else {
                    strArr[i] = str;
                }
            }
        }
        return strArr;
    }

    private String getDurationText(FormatParam formatParam) {
        int minutesToEvent = getMinutesToEvent(formatParam);
        if (minutesToEvent >= 60) {
            int i = minutesToEvent / 60;
            int i2 = minutesToEvent % 60;
            String quantityString = this.mContext.getResources().getQuantityString(C2015R$plurals.smartspace_hours, i, new Object[]{Integer.valueOf(i)});
            if (i2 <= 0) {
                return quantityString;
            }
            String quantityString2 = this.mContext.getResources().getQuantityString(C2015R$plurals.smartspace_minutes, i2, new Object[]{Integer.valueOf(i2)});
            return this.mContext.getString(C2017R$string.smartspace_hours_mins, new Object[]{quantityString, quantityString2});
        }
        return this.mContext.getResources().getQuantityString(C2015R$plurals.smartspace_minutes, minutesToEvent, new Object[]{Integer.valueOf(minutesToEvent)});
    }

    private String substitute(boolean z, String str) {
        FormattedText formattedText = getFormattedText(z);
        if (formattedText != null) {
            String str2 = formattedText.text;
            if (str2 != null) {
                return hasParams(formattedText) ? String.format(str2, getTextArgs(formattedText.formatParam, str)) : str2;
            }
        }
        return "";
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > getExpiration();
    }

    public long getExpiration() {
        SmartspaceCard smartspaceCard = this.mCard;
        if (smartspaceCard != null) {
            ExpiryCriteria expiryCriteria = smartspaceCard.expiryCriteria;
            if (expiryCriteria != null) {
                return expiryCriteria.expirationTimeMillis;
            }
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("title:");
        sb.append(getTitle());
        sb.append(" subtitle:");
        sb.append(getSubtitle());
        sb.append(" expires:");
        sb.append(getExpiration());
        sb.append(" published:");
        sb.append(this.mPublishTime);
        return sb.toString();
    }

    static SmartSpaceCard fromWrapper(Context context, SmartspaceProto$CardWrapper smartspaceProto$CardWrapper, boolean z) {
        if (smartspaceProto$CardWrapper == null) {
            return null;
        }
        try {
            Intent parseUri = (smartspaceProto$CardWrapper.card.tapAction == null || TextUtils.isEmpty(smartspaceProto$CardWrapper.card.tapAction.intent)) ? null : Intent.parseUri(smartspaceProto$CardWrapper.card.tapAction.intent, 0);
            Bitmap decodeByteArray = smartspaceProto$CardWrapper.icon != null ? BitmapFactory.decodeByteArray(smartspaceProto$CardWrapper.icon, 0, smartspaceProto$CardWrapper.icon.length, null) : null;
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(C2009R$dimen.header_icon_size);
            if (decodeByteArray != null && decodeByteArray.getHeight() > dimensionPixelSize) {
                decodeByteArray = Bitmap.createScaledBitmap(decodeByteArray, (int) (((float) decodeByteArray.getWidth()) * (((float) dimensionPixelSize) / ((float) decodeByteArray.getHeight()))), dimensionPixelSize, true);
            }
            SmartSpaceCard smartSpaceCard = new SmartSpaceCard(context, smartspaceProto$CardWrapper.card, parseUri, z, decodeByteArray, smartspaceProto$CardWrapper.isIconGrayscale, smartspaceProto$CardWrapper.publishTime);
            return smartSpaceCard;
        } catch (Exception e) {
            Log.e("SmartspaceCard", "from proto", e);
            return null;
        }
    }

    public PendingIntent getPendingIntent() {
        if (this.mCard.tapAction == null) {
            return null;
        }
        Intent intent = new Intent(getIntent());
        int i = this.mCard.tapAction.actionType;
        if (i == 1) {
            intent.addFlags(268435456);
            intent.setPackage("com.google.android.googlequicksearchbox");
            return PendingIntent.getBroadcast(this.mContext, this.mRequestCode, intent, 0);
        } else if (i != 2) {
            return null;
        } else {
            return PendingIntent.getActivity(this.mContext, this.mRequestCode, intent, 0);
        }
    }
}
