package com.android.systemui.bubbles;

import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.InboxStyle;
import android.app.Notification.MediaStyle;
import android.app.Notification.MessagingStyle;
import android.app.Notification.MessagingStyle.Message;
import android.app.Person;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.util.PathParser;
import android.view.LayoutInflater;
import com.android.internal.graphics.ColorUtils;
import com.android.launcher3.icons.BitmapInfo;
import com.android.systemui.C2013R$layout;
import com.android.systemui.bubbles.Bubble.FlyoutMessage;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.lang.ref.WeakReference;

public class BubbleViewInfoTask extends AsyncTask<Void, Void, BubbleViewInfo> {
    private Bubble mBubble;
    private Callback mCallback;
    private WeakReference<Context> mContext;
    private BubbleIconFactory mIconFactory;
    private WeakReference<BubbleStackView> mStackView;

    static class BubbleViewInfo {
        String appName;
        Bitmap badgedBubbleImage;
        int dotColor;
        Path dotPath;
        BubbleExpandedView expandedView;
        FlyoutMessage flyoutMessage;
        BadgedImageView imageView;
        ShortcutInfo shortcutInfo;

        BubbleViewInfo() {
        }

        static BubbleViewInfo populate(Context context, BubbleStackView bubbleStackView, BubbleIconFactory bubbleIconFactory, Bubble bubble) {
            BubbleViewInfo bubbleViewInfo = new BubbleViewInfo();
            if (!bubble.isInflated()) {
                LayoutInflater from = LayoutInflater.from(context);
                bubbleViewInfo.imageView = (BadgedImageView) from.inflate(C2013R$layout.bubble_view, bubbleStackView, false);
                BubbleExpandedView bubbleExpandedView = (BubbleExpandedView) from.inflate(C2013R$layout.bubble_expanded_view, bubbleStackView, false);
                bubbleViewInfo.expandedView = bubbleExpandedView;
                bubbleExpandedView.setStackView(bubbleStackView);
            }
            StatusBarNotification sbn = bubble.getEntry().getSbn();
            String packageName = sbn.getPackageName();
            String shortcutId = bubble.getEntry().getBubbleMetadata().getShortcutId();
            if (shortcutId != null) {
                bubbleViewInfo.shortcutInfo = BubbleExperimentConfig.getShortcutInfo(context, packageName, sbn.getUser(), shortcutId);
            } else {
                String shortcutId2 = sbn.getNotification().getShortcutId();
                if (BubbleExperimentConfig.useShortcutInfoToBubble(context) && shortcutId2 != null) {
                    bubbleViewInfo.shortcutInfo = BubbleExperimentConfig.getShortcutInfo(context, packageName, sbn.getUser(), shortcutId2);
                }
            }
            PackageManager packageManager = context.getPackageManager();
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 795136);
                if (applicationInfo != null) {
                    bubbleViewInfo.appName = String.valueOf(packageManager.getApplicationLabel(applicationInfo));
                }
                Drawable applicationIcon = packageManager.getApplicationIcon(packageName);
                Drawable userBadgedIcon = packageManager.getUserBadgedIcon(applicationIcon, sbn.getUser());
                Drawable bubbleDrawable = bubbleIconFactory.getBubbleDrawable(context, bubbleViewInfo.shortcutInfo, bubble.getEntry().getBubbleMetadata());
                if (bubbleDrawable != null) {
                    applicationIcon = bubbleDrawable;
                }
                BitmapInfo badgeBitmap = bubbleIconFactory.getBadgeBitmap(userBadgedIcon);
                bubbleViewInfo.badgedBubbleImage = bubbleIconFactory.getBubbleBitmap(applicationIcon, badgeBitmap).icon;
                Path createPathFromPathData = PathParser.createPathFromPathData(context.getResources().getString(17039801));
                Matrix matrix = new Matrix();
                float scale = bubbleIconFactory.getNormalizer().getScale(applicationIcon, null, null, null);
                matrix.setScale(scale, scale, 50.0f, 50.0f);
                createPathFromPathData.transform(matrix);
                bubbleViewInfo.dotPath = createPathFromPathData;
                bubbleViewInfo.dotColor = ColorUtils.blendARGB(badgeBitmap.color, -1, 0.54f);
                bubbleViewInfo.flyoutMessage = BubbleViewInfoTask.extractFlyoutMessage(context, bubble.getEntry());
                return bubbleViewInfo;
            } catch (NameNotFoundException unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("Unable to find package: ");
                sb.append(packageName);
                Log.w("Bubbles", sb.toString());
                return null;
            }
        }
    }

    public interface Callback {
        void onBubbleViewsReady(Bubble bubble);
    }

    BubbleViewInfoTask(Bubble bubble, Context context, BubbleStackView bubbleStackView, BubbleIconFactory bubbleIconFactory, Callback callback) {
        this.mBubble = bubble;
        this.mContext = new WeakReference<>(context);
        this.mStackView = new WeakReference<>(bubbleStackView);
        this.mIconFactory = bubbleIconFactory;
        this.mCallback = callback;
    }

    /* access modifiers changed from: protected */
    public BubbleViewInfo doInBackground(Void... voidArr) {
        return BubbleViewInfo.populate((Context) this.mContext.get(), (BubbleStackView) this.mStackView.get(), this.mIconFactory, this.mBubble);
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(BubbleViewInfo bubbleViewInfo) {
        if (bubbleViewInfo != null) {
            this.mBubble.setViewInfo(bubbleViewInfo);
            if (this.mCallback != null && !isCancelled()) {
                this.mCallback.onBubbleViewsReady(this.mBubble);
            }
        }
    }

    static FlyoutMessage extractFlyoutMessage(Context context, NotificationEntry notificationEntry) {
        Notification notification = notificationEntry.getSbn().getNotification();
        Class notificationStyle = notification.getNotificationStyle();
        FlyoutMessage flyoutMessage = new FlyoutMessage();
        flyoutMessage.isGroupChat = notification.extras.getBoolean("android.isGroupConversation");
        try {
            String str = "android.text";
            if (BigTextStyle.class.equals(notificationStyle)) {
                CharSequence charSequence = notification.extras.getCharSequence("android.bigText");
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = notification.extras.getCharSequence(str);
                }
                flyoutMessage.message = charSequence;
                return flyoutMessage;
            }
            if (MessagingStyle.class.equals(notificationStyle)) {
                Message findLatestIncomingMessage = MessagingStyle.findLatestIncomingMessage(Message.getMessagesFromBundleArray((Parcelable[]) notification.extras.get("android.messages")));
                if (findLatestIncomingMessage != null) {
                    flyoutMessage.message = findLatestIncomingMessage.getText();
                    Person senderPerson = findLatestIncomingMessage.getSenderPerson();
                    flyoutMessage.senderName = senderPerson != null ? senderPerson.getName() : null;
                    flyoutMessage.senderAvatar = null;
                    if (!(senderPerson == null || senderPerson.getIcon() == null)) {
                        if (senderPerson.getIcon().getType() == 4 || senderPerson.getIcon().getType() == 6) {
                            context.grantUriPermission(context.getPackageName(), senderPerson.getIcon().getUri(), 1);
                        }
                        flyoutMessage.senderAvatar = senderPerson.getIcon().loadDrawable(context);
                    }
                    return flyoutMessage;
                }
            } else if (InboxStyle.class.equals(notificationStyle)) {
                CharSequence[] charSequenceArray = notification.extras.getCharSequenceArray("android.textLines");
                if (charSequenceArray != null && charSequenceArray.length > 0) {
                    flyoutMessage.message = charSequenceArray[charSequenceArray.length - 1];
                    return flyoutMessage;
                }
            } else if (MediaStyle.class.equals(notificationStyle)) {
                return flyoutMessage;
            } else {
                flyoutMessage.message = notification.extras.getCharSequence(str);
                return flyoutMessage;
            }
            return flyoutMessage;
        } catch (ArrayIndexOutOfBoundsException | ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
