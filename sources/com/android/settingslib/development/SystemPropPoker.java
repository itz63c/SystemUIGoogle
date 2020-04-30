package com.android.settingslib.development;

import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class SystemPropPoker {
    private static final SystemPropPoker sInstance = new SystemPropPoker();
    private boolean mBlockPokes = false;

    public static class PokerTask extends AsyncTask<Void, Void, Void> {
        /* access modifiers changed from: 0000 */
        public String[] listServices() {
            return ServiceManager.listServices();
        }

        /* access modifiers changed from: 0000 */
        public IBinder checkService(String str) {
            return ServiceManager.checkService(str);
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            String[] listServices = listServices();
            String str = "SystemPropPoker";
            if (listServices == null) {
                Log.e(str, "There are no services, how odd");
                return null;
            }
            for (String str2 : listServices) {
                IBinder checkService = checkService(str2);
                if (checkService != null) {
                    Parcel obtain = Parcel.obtain();
                    try {
                        checkService.transact(1599295570, obtain, null, 0);
                    } catch (RemoteException unused) {
                    } catch (Exception e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Someone wrote a bad service '");
                        sb.append(str2);
                        sb.append("' that doesn't like to be poked");
                        Log.i(str, sb.toString(), e);
                    }
                    obtain.recycle();
                }
            }
            return null;
        }
    }

    private SystemPropPoker() {
    }

    public static SystemPropPoker getInstance() {
        return sInstance;
    }

    public void poke() {
        if (!this.mBlockPokes) {
            createPokerTask().execute(new Void[0]);
        }
    }

    /* access modifiers changed from: 0000 */
    public PokerTask createPokerTask() {
        return new PokerTask();
    }
}
