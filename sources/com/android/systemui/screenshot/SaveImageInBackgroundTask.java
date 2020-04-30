package com.android.systemui.screenshot;

import android.app.ActivityTaskManager;
import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Icon;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.screenshot.GlobalScreenshot.ActionProxyReceiver;
import com.android.systemui.screenshot.GlobalScreenshot.DeleteScreenshotReceiver;
import com.android.systemui.screenshot.GlobalScreenshot.SmartActionsReceiver;
import com.android.systemui.screenshot.GlobalScreenshot.TargetChosenReceiver;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

class SaveImageInBackgroundTask extends AsyncTask<Void, Void, Void> {
    private final Context mContext;
    private final boolean mCreateDeleteAction;
    private final String mImageFileName;
    private final long mImageTime;
    private final SaveImageInBackgroundData mParams;
    private final Random mRandom = new Random();
    private final String mScreenshotId;
    private final boolean mSmartActionsEnabled;
    private final ScreenshotNotificationSmartActionsProvider mSmartActionsProvider;

    SaveImageInBackgroundTask(Context context, SaveImageInBackgroundData saveImageInBackgroundData) {
        this.mContext = context;
        this.mParams = saveImageInBackgroundData;
        this.mImageTime = System.currentTimeMillis();
        this.mImageFileName = String.format("Screenshot_%s.png", new Object[]{new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(this.mImageTime))});
        this.mScreenshotId = String.format("Screenshot_%s", new Object[]{UUID.randomUUID()});
        this.mCreateDeleteAction = saveImageInBackgroundData.createDeleteAction;
        boolean z = DeviceConfig.getBoolean("systemui", "enable_screenshot_notification_smart_actions", true);
        this.mSmartActionsEnabled = z;
        if (z) {
            this.mSmartActionsProvider = SystemUIFactory.getInstance().createScreenshotNotificationSmartActionsProvider(context, AsyncTask.THREAD_POOL_EXECUTOR, new Handler());
        } else {
            this.mSmartActionsProvider = new ScreenshotNotificationSmartActionsProvider();
        }
    }

    /* access modifiers changed from: protected */
    public Void doInBackground(Void... voidArr) {
        OutputStream openOutputStream;
        ParcelFileDescriptor openFile;
        String str = "is_pending";
        String str2 = "date_expires";
        if (isCancelled()) {
            return null;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Bitmap bitmap = this.mParams.image;
        Resources resources = this.mContext.getResources();
        try {
            CompletableFuture smartActionsFuture = ScreenshotSmartActions.getSmartActionsFuture(this.mScreenshotId, bitmap, this.mSmartActionsProvider, this.mSmartActionsEnabled, isManagedProfile(this.mContext));
            ContentValues contentValues = new ContentValues();
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.DIRECTORY_PICTURES);
            sb.append(File.separator);
            sb.append(Environment.DIRECTORY_SCREENSHOTS);
            contentValues.put("relative_path", sb.toString());
            contentValues.put("_display_name", this.mImageFileName);
            contentValues.put("mime_type", "image/png");
            contentValues.put("date_added", Long.valueOf(this.mImageTime / 1000));
            contentValues.put("date_modified", Long.valueOf(this.mImageTime / 1000));
            contentValues.put(str2, Long.valueOf((this.mImageTime + 86400000) / 1000));
            contentValues.put(str, Integer.valueOf(1));
            Uri insert = contentResolver.insert(Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                openOutputStream = contentResolver.openOutputStream(insert);
                if (bitmap.compress(CompressFormat.PNG, 100, openOutputStream)) {
                    if (openOutputStream != null) {
                        openOutputStream.close();
                    }
                    openFile = contentResolver.openFile(insert, "rw", null);
                    ExifInterface exifInterface = new ExifInterface(openFile.getFileDescriptor());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Android ");
                    sb2.append(Build.DISPLAY);
                    exifInterface.setAttribute("Software", sb2.toString());
                    exifInterface.setAttribute("ImageWidth", Integer.toString(bitmap.getWidth()));
                    exifInterface.setAttribute("ImageLength", Integer.toString(bitmap.getHeight()));
                    ZonedDateTime ofInstant = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.mImageTime), ZoneId.systemDefault());
                    exifInterface.setAttribute("DateTimeOriginal", DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").format(ofInstant));
                    exifInterface.setAttribute("SubSecTimeOriginal", DateTimeFormatter.ofPattern("SSS").format(ofInstant));
                    String str3 = "OffsetTimeOriginal";
                    if (Objects.equals(ofInstant.getOffset(), ZoneOffset.UTC)) {
                        exifInterface.setAttribute(str3, "+00:00");
                    } else {
                        exifInterface.setAttribute(str3, DateTimeFormatter.ofPattern("XXX").format(ofInstant));
                    }
                    exifInterface.saveAttributes();
                    if (openFile != null) {
                        openFile.close();
                    }
                    contentValues.clear();
                    contentValues.put(str, Integer.valueOf(0));
                    contentValues.putNull(str2);
                    contentResolver.update(insert, contentValues, null, null);
                    List populateNotificationActions = populateNotificationActions(this.mContext, resources, insert);
                    ArrayList arrayList = new ArrayList();
                    if (this.mSmartActionsEnabled) {
                        arrayList.addAll(buildSmartActions(ScreenshotSmartActions.getSmartActions(this.mScreenshotId, smartActionsFuture, DeviceConfig.getInt("systemui", "screenshot_notification_smart_actions_timeout_ms", 1000), this.mSmartActionsProvider), this.mContext));
                    }
                    this.mParams.mActionsReadyListener.onActionsReady(insert, arrayList, populateNotificationActions);
                    this.mParams.imageUri = insert;
                    this.mParams.image = null;
                    this.mParams.errorMsgResId = 0;
                    return null;
                }
                throw new IOException("Failed to compress");
            } catch (Exception e) {
                contentResolver.delete(insert, null);
                throw e;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        } catch (Exception e2) {
            Slog.e("SaveImageInBackgroundTask", "unable to save screenshot", e2);
            this.mParams.clearImage();
            SaveImageInBackgroundData saveImageInBackgroundData = this.mParams;
            saveImageInBackgroundData.errorMsgResId = C2017R$string.screenshot_failed_to_save_text;
            saveImageInBackgroundData.mActionsReadyListener.onActionsReady(null, null, null);
        }
        throw th;
        throw th;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Void voidR) {
        SaveImageInBackgroundData saveImageInBackgroundData = this.mParams;
        saveImageInBackgroundData.finisher.accept(saveImageInBackgroundData.imageUri);
    }

    /* access modifiers changed from: protected */
    public void onCancelled(Void voidR) {
        this.mParams.mActionsReadyListener.onActionsReady(null, null, null);
        this.mParams.finisher.accept(null);
        this.mParams.clearImage();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public List<Action> populateNotificationActions(Context context, Resources resources, Uri uri) {
        Context context2 = context;
        Resources resources2 = resources;
        Uri uri2 = uri;
        String format = DateFormat.getDateTimeInstance().format(new Date(this.mImageTime));
        boolean z = true;
        String format2 = String.format("Screenshot (%s)", new Object[]{format});
        String str = "android.intent.action.SEND";
        Intent intent = new Intent(str);
        String str2 = "image/png";
        intent.setType(str2);
        intent.putExtra("android.intent.extra.STREAM", uri2);
        intent.setClipData(new ClipData(new ClipDescription("content", new String[]{"text/plain"}), new Item(uri2)));
        intent.putExtra("android.intent.extra.SUBJECT", format2);
        intent.addFlags(1);
        int userId = context.getUserId();
        ArrayList arrayList = new ArrayList();
        Intent addFlags = Intent.createChooser(intent, null, PendingIntent.getBroadcast(context2, userId, new Intent(context2, TargetChosenReceiver.class), 1342177280).getIntentSender()).addFlags(268468224).addFlags(1);
        String str3 = "android:screenshot_action_intent";
        String str4 = "android:screenshot_id";
        String str5 = "android:smart_actions_enabled";
        arrayList.add(new Builder(Icon.createWithResource(resources2, C2010R$drawable.ic_screenshot_share), resources2.getString(17041125), PendingIntent.getBroadcastAsUser(context2, userId, new Intent(context2, ActionProxyReceiver.class).putExtra(str3, addFlags).putExtra("android:screenshot_disallow_enter_pip", true).putExtra(str4, this.mScreenshotId).putExtra(str5, this.mSmartActionsEnabled).setAction(str).addFlags(268435456), 268435456, UserHandle.SYSTEM)).build());
        String string = context2.getString(C2017R$string.config_screenshotEditor);
        String str6 = "android.intent.action.EDIT";
        Intent intent2 = new Intent(str6);
        if (!TextUtils.isEmpty(string)) {
            intent2.setComponent(ComponentName.unflattenFromString(string));
        }
        intent2.setType(str2);
        intent2.setData(uri2);
        intent2.addFlags(1);
        intent2.addFlags(2);
        Intent putExtra = new Intent(context2, ActionProxyReceiver.class).putExtra(str3, intent2);
        if (intent2.getComponent() == null) {
            z = false;
        }
        arrayList.add(new Builder(Icon.createWithResource(resources2, C2010R$drawable.ic_screenshot_edit), resources2.getString(17041086), PendingIntent.getBroadcastAsUser(context2, userId, putExtra.putExtra("android:screenshot_cancel_notification", z).putExtra(str4, this.mScreenshotId).putExtra(str5, this.mSmartActionsEnabled).setAction(str6).addFlags(268435456), 268435456, UserHandle.SYSTEM)).build());
        if (this.mCreateDeleteAction) {
            arrayList.add(new Builder(Icon.createWithResource(resources2, C2010R$drawable.ic_screenshot_delete), resources2.getString(17039934), PendingIntent.getBroadcast(context2, userId, new Intent(context2, DeleteScreenshotReceiver.class).putExtra("android:screenshot_uri_id", uri.toString()).putExtra(str4, this.mScreenshotId).putExtra(str5, this.mSmartActionsEnabled).addFlags(268435456), 1342177280)).build());
        }
        return arrayList;
    }

    private int getUserHandleOfForegroundApplication(Context context) {
        try {
            return ActivityTaskManager.getService().getLastResumedActivityUserId();
        } catch (RemoteException e) {
            Slog.w("SaveImageInBackgroundTask", "getUserHandleOfForegroundApplication: ", e);
            return context.getUserId();
        }
    }

    private boolean isManagedProfile(Context context) {
        return UserManager.get(context).getUserInfo(getUserHandleOfForegroundApplication(context)).isManagedProfile();
    }

    private List<Action> buildSmartActions(List<Action> list, Context context) {
        ArrayList arrayList = new ArrayList();
        for (Action action : list) {
            Bundle extras = action.getExtras();
            String string = extras.getString("action_type", "Smart Action");
            Intent addFlags = new Intent(context, SmartActionsReceiver.class).putExtra("android:screenshot_action_intent", action.actionIntent).addFlags(268435456);
            addIntentExtras(this.mScreenshotId, addFlags, string, this.mSmartActionsEnabled);
            arrayList.add(new Builder(action.getIcon(), action.title, PendingIntent.getBroadcast(context, this.mRandom.nextInt(), addFlags, 268435456)).setContextual(true).addExtras(extras).build());
        }
        return arrayList;
    }

    private static void addIntentExtras(String str, Intent intent, String str2, boolean z) {
        intent.putExtra("android:screenshot_action_type", str2).putExtra("android:screenshot_id", str).putExtra("android:smart_actions_enabled", z);
    }
}
