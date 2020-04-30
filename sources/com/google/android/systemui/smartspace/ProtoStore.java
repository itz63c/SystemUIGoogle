package com.google.android.systemui.smartspace;

import android.content.Context;
import android.util.Log;
import com.google.protobuf.nano.MessageNano;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ProtoStore {
    private final Context mContext;

    public ProtoStore(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void store(MessageNano messageNano, String str) {
        FileOutputStream openFileOutput;
        String str2 = "ProtoStore";
        try {
            openFileOutput = this.mContext.openFileOutput(str, 0);
            if (messageNano != null) {
                openFileOutput.write(MessageNano.toByteArray(messageNano));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("deleting ");
                sb.append(str);
                Log.d(str2, sb.toString());
                this.mContext.deleteFile(str);
            }
            if (openFileOutput != null) {
                openFileOutput.close();
                return;
            }
            return;
        } catch (FileNotFoundException unused) {
            Log.d(str2, "file does not exist");
            return;
        } catch (Exception e) {
            Log.e(str2, "unable to write file", e);
            return;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public <T extends MessageNano> boolean load(String str, T t) {
        FileInputStream fileInputStream;
        String str2 = "ProtoStore";
        File fileStreamPath = this.mContext.getFileStreamPath(str);
        try {
            fileInputStream = new FileInputStream(fileStreamPath);
            int length = (int) fileStreamPath.length();
            byte[] bArr = new byte[length];
            fileInputStream.read(bArr, 0, length);
            MessageNano.mergeFrom(t, bArr);
            fileInputStream.close();
            return true;
        } catch (FileNotFoundException unused) {
            Log.d(str2, "no cached data");
            return false;
        } catch (Exception e) {
            Log.e(str2, "unable to load data", e);
            return false;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }
}
