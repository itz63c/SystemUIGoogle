package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.R$array;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$string;
import com.android.settingslib.widget.AdaptiveIcon;
import com.android.settingslib.widget.AdaptiveOutlineDrawable;
import java.io.IOException;

public class BluetoothUtils {
    private static ErrorListener sErrorListener;

    public interface ErrorListener {
        void onShowError(Context context, String str, int i);
    }

    static void showError(Context context, String str, int i) {
        ErrorListener errorListener = sErrorListener;
        if (errorListener != null) {
            errorListener.onShowError(context, str, i);
        }
    }

    public static void setErrorListener(ErrorListener errorListener) {
        sErrorListener = errorListener;
    }

    public static Pair<Drawable, String> getBtClassDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
        if (btClass != null) {
            int majorDeviceClass = btClass.getMajorDeviceClass();
            if (majorDeviceClass == 256) {
                return new Pair<>(getBluetoothDrawable(context, 17302321), context.getString(R$string.bluetooth_talkback_computer));
            }
            if (majorDeviceClass == 512) {
                return new Pair<>(getBluetoothDrawable(context, 17302781), context.getString(R$string.bluetooth_talkback_phone));
            }
            if (majorDeviceClass == 1280) {
                return new Pair<>(getBluetoothDrawable(context, HidProfile.getHidClassDrawable(btClass)), context.getString(R$string.bluetooth_talkback_input_peripheral));
            }
            if (majorDeviceClass == 1536) {
                return new Pair<>(getBluetoothDrawable(context, 17302813), context.getString(R$string.bluetooth_talkback_imaging));
            }
        }
        for (LocalBluetoothProfile drawableResource : cachedBluetoothDevice.getProfiles()) {
            int drawableResource2 = drawableResource.getDrawableResource(btClass);
            if (drawableResource2 != 0) {
                return new Pair<>(getBluetoothDrawable(context, drawableResource2), null);
            }
        }
        if (btClass != null) {
            if (btClass.doesClassMatch(0)) {
                return new Pair<>(getBluetoothDrawable(context, 17302319), context.getString(R$string.bluetooth_talkback_headset));
            }
            if (btClass.doesClassMatch(1)) {
                return new Pair<>(getBluetoothDrawable(context, 17302318), context.getString(R$string.bluetooth_talkback_headphone));
            }
        }
        return new Pair<>(getBluetoothDrawable(context, 17302811).mutate(), context.getString(R$string.bluetooth_talkback_bluetooth));
    }

    public static Drawable getBluetoothDrawable(Context context, int i) {
        return context.getDrawable(i);
    }

    public static Pair<Drawable, String> getBtRainbowDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        String str = "BluetoothUtils";
        Pair btClassDrawableWithDescription = getBtClassDrawableWithDescription(context, cachedBluetoothDevice);
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        boolean booleanMetaData = getBooleanMetaData(device, 6);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.bt_nearby_icon_size);
        Resources resources = context.getResources();
        if (booleanMetaData) {
            Uri uriMetaData = getUriMetaData(device, 5);
            if (uriMetaData != null) {
                try {
                    context.getContentResolver().takePersistableUriPermission(uriMetaData, 1);
                } catch (SecurityException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to take persistable permission for: ");
                    sb.append(uriMetaData);
                    Log.e(str, sb.toString(), e);
                }
                try {
                    Bitmap bitmap = Media.getBitmap(context.getContentResolver(), uriMetaData);
                    if (bitmap != null) {
                        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, dimensionPixelSize, dimensionPixelSize, false);
                        bitmap.recycle();
                        return new Pair<>(new AdaptiveOutlineDrawable(resources, createScaledBitmap), (String) btClassDrawableWithDescription.second);
                    }
                } catch (IOException e2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Failed to get drawable for: ");
                    sb2.append(uriMetaData);
                    Log.e(str, sb2.toString(), e2);
                } catch (SecurityException e3) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Failed to get permission for: ");
                    sb3.append(uriMetaData);
                    Log.e(str, sb3.toString(), e3);
                }
            }
        }
        return new Pair<>(buildBtRainbowDrawable(context, (Drawable) btClassDrawableWithDescription.first, cachedBluetoothDevice.getAddress().hashCode()), (String) btClassDrawableWithDescription.second);
    }

    public static Drawable buildBtRainbowDrawable(Context context, Drawable drawable, int i) {
        Resources resources = context.getResources();
        int[] intArray = resources.getIntArray(R$array.bt_icon_fg_colors);
        int[] intArray2 = resources.getIntArray(R$array.bt_icon_bg_colors);
        int abs = Math.abs(i % intArray2.length);
        drawable.setTint(intArray[abs]);
        AdaptiveIcon adaptiveIcon = new AdaptiveIcon(context, drawable);
        adaptiveIcon.setBackgroundColor(intArray2[abs]);
        return adaptiveIcon;
    }

    public static boolean getBooleanMetaData(BluetoothDevice bluetoothDevice, int i) {
        if (bluetoothDevice == null) {
            return false;
        }
        byte[] metadata = bluetoothDevice.getMetadata(i);
        if (metadata == null) {
            return false;
        }
        return Boolean.parseBoolean(new String(metadata));
    }

    public static String getStringMetaData(BluetoothDevice bluetoothDevice, int i) {
        if (bluetoothDevice == null) {
            return null;
        }
        byte[] metadata = bluetoothDevice.getMetadata(i);
        if (metadata == null) {
            return null;
        }
        return new String(metadata);
    }

    public static Uri getUriMetaData(BluetoothDevice bluetoothDevice, int i) {
        String stringMetaData = getStringMetaData(bluetoothDevice, i);
        if (stringMetaData == null) {
            return null;
        }
        return Uri.parse(stringMetaData);
    }
}
