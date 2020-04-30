package com.android.systemui.p007qs.tileimpl;

import android.view.ContextThemeWrapper;
import com.android.systemui.C2018R$style;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tiles.AirplaneModeTile;
import com.android.systemui.p007qs.tiles.BatterySaverTile;
import com.android.systemui.p007qs.tiles.BluetoothTile;
import com.android.systemui.p007qs.tiles.CastTile;
import com.android.systemui.p007qs.tiles.CellularTile;
import com.android.systemui.p007qs.tiles.ColorInversionTile;
import com.android.systemui.p007qs.tiles.DataSaverTile;
import com.android.systemui.p007qs.tiles.DndTile;
import com.android.systemui.p007qs.tiles.FlashlightTile;
import com.android.systemui.p007qs.tiles.HotspotTile;
import com.android.systemui.p007qs.tiles.LocationTile;
import com.android.systemui.p007qs.tiles.NfcTile;
import com.android.systemui.p007qs.tiles.NightDisplayTile;
import com.android.systemui.p007qs.tiles.RotationLockTile;
import com.android.systemui.p007qs.tiles.ScreenRecordTile;
import com.android.systemui.p007qs.tiles.UiModeNightTile;
import com.android.systemui.p007qs.tiles.UserTile;
import com.android.systemui.p007qs.tiles.WifiTile;
import com.android.systemui.p007qs.tiles.WorkModeTile;
import com.android.systemui.plugins.p006qs.QSFactory;
import com.android.systemui.plugins.p006qs.QSIconView;
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.plugins.p006qs.QSTileView;
import com.android.systemui.util.leak.GarbageMonitor.MemoryTile;
import dagger.Lazy;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tileimpl.QSFactoryImpl */
public class QSFactoryImpl implements QSFactory {
    private final Provider<AirplaneModeTile> mAirplaneModeTileProvider;
    private final Provider<BatterySaverTile> mBatterySaverTileProvider;
    private final Provider<BluetoothTile> mBluetoothTileProvider;
    private final Provider<CastTile> mCastTileProvider;
    private final Provider<CellularTile> mCellularTileProvider;
    private final Provider<ColorInversionTile> mColorInversionTileProvider;
    private final Provider<DataSaverTile> mDataSaverTileProvider;
    private final Provider<DndTile> mDndTileProvider;
    private final Provider<FlashlightTile> mFlashlightTileProvider;
    private final Provider<HotspotTile> mHotspotTileProvider;
    private final Provider<LocationTile> mLocationTileProvider;
    private final Provider<MemoryTile> mMemoryTileProvider;
    private final Provider<NfcTile> mNfcTileProvider;
    private final Provider<NightDisplayTile> mNightDisplayTileProvider;
    private final Lazy<QSHost> mQsHostLazy;
    private final Provider<RotationLockTile> mRotationLockTileProvider;
    private final Provider<ScreenRecordTile> mScreenRecordTileProvider;
    private final Provider<UiModeNightTile> mUiModeNightTileProvider;
    private final Provider<UserTile> mUserTileProvider;
    private final Provider<WifiTile> mWifiTileProvider;
    private final Provider<WorkModeTile> mWorkModeTileProvider;

    public QSFactoryImpl(Lazy<QSHost> lazy, Provider<WifiTile> provider, Provider<BluetoothTile> provider2, Provider<CellularTile> provider3, Provider<DndTile> provider4, Provider<ColorInversionTile> provider5, Provider<AirplaneModeTile> provider6, Provider<WorkModeTile> provider7, Provider<RotationLockTile> provider8, Provider<FlashlightTile> provider9, Provider<LocationTile> provider10, Provider<CastTile> provider11, Provider<HotspotTile> provider12, Provider<UserTile> provider13, Provider<BatterySaverTile> provider14, Provider<DataSaverTile> provider15, Provider<NightDisplayTile> provider16, Provider<NfcTile> provider17, Provider<MemoryTile> provider18, Provider<UiModeNightTile> provider19, Provider<ScreenRecordTile> provider20) {
        this.mQsHostLazy = lazy;
        this.mWifiTileProvider = provider;
        this.mBluetoothTileProvider = provider2;
        this.mCellularTileProvider = provider3;
        this.mDndTileProvider = provider4;
        this.mColorInversionTileProvider = provider5;
        this.mAirplaneModeTileProvider = provider6;
        this.mWorkModeTileProvider = provider7;
        this.mRotationLockTileProvider = provider8;
        this.mFlashlightTileProvider = provider9;
        this.mLocationTileProvider = provider10;
        this.mCastTileProvider = provider11;
        this.mHotspotTileProvider = provider12;
        this.mUserTileProvider = provider13;
        this.mBatterySaverTileProvider = provider14;
        this.mDataSaverTileProvider = provider15;
        this.mNightDisplayTileProvider = provider16;
        this.mNfcTileProvider = provider17;
        this.mMemoryTileProvider = provider18;
        this.mUiModeNightTileProvider = provider19;
        this.mScreenRecordTileProvider = provider20;
    }

    public QSTile createTile(String str) {
        QSTileImpl createTileInternal = createTileInternal(str);
        if (createTileInternal != null) {
            createTileInternal.handleStale();
        }
        return createTileInternal;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.systemui.p007qs.tileimpl.QSTileImpl createTileInternal(java.lang.String r2) {
        /*
            r1 = this;
            int r0 = r2.hashCode()
            switch(r0) {
                case -2016941037: goto L_0x00d1;
                case -1183073498: goto L_0x00c6;
                case -805491779: goto L_0x00bb;
                case -677011630: goto L_0x00b1;
                case -331239923: goto L_0x00a6;
                case -40300674: goto L_0x009c;
                case 3154: goto L_0x0092;
                case 99610: goto L_0x0088;
                case 108971: goto L_0x007d;
                case 3046207: goto L_0x0072;
                case 3049826: goto L_0x0067;
                case 3075958: goto L_0x005b;
                case 3599307: goto L_0x004f;
                case 3649301: goto L_0x0044;
                case 3655441: goto L_0x0039;
                case 104817688: goto L_0x002d;
                case 109211285: goto L_0x0021;
                case 1099603663: goto L_0x0015;
                case 1901043637: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x00db
        L_0x0009:
            java.lang.String r0 = "location"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 9
            goto L_0x00dc
        L_0x0015:
            java.lang.String r0 = "hotspot"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 11
            goto L_0x00dc
        L_0x0021:
            java.lang.String r0 = "saver"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 14
            goto L_0x00dc
        L_0x002d:
            java.lang.String r0 = "night"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 15
            goto L_0x00dc
        L_0x0039:
            java.lang.String r0 = "work"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 6
            goto L_0x00dc
        L_0x0044:
            java.lang.String r0 = "wifi"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 0
            goto L_0x00dc
        L_0x004f:
            java.lang.String r0 = "user"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 12
            goto L_0x00dc
        L_0x005b:
            java.lang.String r0 = "dark"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 17
            goto L_0x00dc
        L_0x0067:
            java.lang.String r0 = "cell"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 2
            goto L_0x00dc
        L_0x0072:
            java.lang.String r0 = "cast"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 10
            goto L_0x00dc
        L_0x007d:
            java.lang.String r0 = "nfc"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 16
            goto L_0x00dc
        L_0x0088:
            java.lang.String r0 = "dnd"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 3
            goto L_0x00dc
        L_0x0092:
            java.lang.String r0 = "bt"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 1
            goto L_0x00dc
        L_0x009c:
            java.lang.String r0 = "rotation"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 7
            goto L_0x00dc
        L_0x00a6:
            java.lang.String r0 = "battery"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 13
            goto L_0x00dc
        L_0x00b1:
            java.lang.String r0 = "airplane"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 5
            goto L_0x00dc
        L_0x00bb:
            java.lang.String r0 = "screenrecord"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 18
            goto L_0x00dc
        L_0x00c6:
            java.lang.String r0 = "flashlight"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 8
            goto L_0x00dc
        L_0x00d1:
            java.lang.String r0 = "inversion"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00db
            r0 = 4
            goto L_0x00dc
        L_0x00db:
            r0 = -1
        L_0x00dc:
            switch(r0) {
                case 0: goto L_0x01a2;
                case 1: goto L_0x0199;
                case 2: goto L_0x0190;
                case 3: goto L_0x0187;
                case 4: goto L_0x017e;
                case 5: goto L_0x0175;
                case 6: goto L_0x016c;
                case 7: goto L_0x0163;
                case 8: goto L_0x015a;
                case 9: goto L_0x0151;
                case 10: goto L_0x0148;
                case 11: goto L_0x013f;
                case 12: goto L_0x0136;
                case 13: goto L_0x012d;
                case 14: goto L_0x0124;
                case 15: goto L_0x011b;
                case 16: goto L_0x0112;
                case 17: goto L_0x0109;
                case 18: goto L_0x0100;
                default: goto L_0x00df;
            }
        L_0x00df:
            java.lang.String r0 = "custom("
            boolean r0 = r2.startsWith(r0)
            if (r0 == 0) goto L_0x01ab
            dagger.Lazy<com.android.systemui.qs.QSHost> r0 = r1.mQsHostLazy
            java.lang.Object r0 = r0.get()
            com.android.systemui.qs.QSHost r0 = (com.android.systemui.p007qs.QSHost) r0
            dagger.Lazy<com.android.systemui.qs.QSHost> r1 = r1.mQsHostLazy
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.QSHost r1 = (com.android.systemui.p007qs.QSHost) r1
            android.content.Context r1 = r1.getUserContext()
            com.android.systemui.qs.external.CustomTile r1 = com.android.systemui.p007qs.external.CustomTile.create(r0, r2, r1)
            return r1
        L_0x0100:
            javax.inject.Provider<com.android.systemui.qs.tiles.ScreenRecordTile> r1 = r1.mScreenRecordTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0109:
            javax.inject.Provider<com.android.systemui.qs.tiles.UiModeNightTile> r1 = r1.mUiModeNightTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0112:
            javax.inject.Provider<com.android.systemui.qs.tiles.NfcTile> r1 = r1.mNfcTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x011b:
            javax.inject.Provider<com.android.systemui.qs.tiles.NightDisplayTile> r1 = r1.mNightDisplayTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0124:
            javax.inject.Provider<com.android.systemui.qs.tiles.DataSaverTile> r1 = r1.mDataSaverTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x012d:
            javax.inject.Provider<com.android.systemui.qs.tiles.BatterySaverTile> r1 = r1.mBatterySaverTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0136:
            javax.inject.Provider<com.android.systemui.qs.tiles.UserTile> r1 = r1.mUserTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x013f:
            javax.inject.Provider<com.android.systemui.qs.tiles.HotspotTile> r1 = r1.mHotspotTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0148:
            javax.inject.Provider<com.android.systemui.qs.tiles.CastTile> r1 = r1.mCastTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0151:
            javax.inject.Provider<com.android.systemui.qs.tiles.LocationTile> r1 = r1.mLocationTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x015a:
            javax.inject.Provider<com.android.systemui.qs.tiles.FlashlightTile> r1 = r1.mFlashlightTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0163:
            javax.inject.Provider<com.android.systemui.qs.tiles.RotationLockTile> r1 = r1.mRotationLockTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x016c:
            javax.inject.Provider<com.android.systemui.qs.tiles.WorkModeTile> r1 = r1.mWorkModeTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0175:
            javax.inject.Provider<com.android.systemui.qs.tiles.AirplaneModeTile> r1 = r1.mAirplaneModeTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x017e:
            javax.inject.Provider<com.android.systemui.qs.tiles.ColorInversionTile> r1 = r1.mColorInversionTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0187:
            javax.inject.Provider<com.android.systemui.qs.tiles.DndTile> r1 = r1.mDndTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0190:
            javax.inject.Provider<com.android.systemui.qs.tiles.CellularTile> r1 = r1.mCellularTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x0199:
            javax.inject.Provider<com.android.systemui.qs.tiles.BluetoothTile> r1 = r1.mBluetoothTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x01a2:
            javax.inject.Provider<com.android.systemui.qs.tiles.WifiTile> r1 = r1.mWifiTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x01ab:
            boolean r0 = android.os.Build.IS_DEBUGGABLE
            if (r0 == 0) goto L_0x01c0
            java.lang.String r0 = "dbg:mem"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x01c0
            javax.inject.Provider<com.android.systemui.util.leak.GarbageMonitor$MemoryTile> r1 = r1.mMemoryTileProvider
            java.lang.Object r1 = r1.get()
            com.android.systemui.qs.tileimpl.QSTileImpl r1 = (com.android.systemui.p007qs.tileimpl.QSTileImpl) r1
            return r1
        L_0x01c0:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r0 = "No stock tile spec: "
            r1.append(r0)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "QSFactory"
            android.util.Log.w(r2, r1)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.tileimpl.QSFactoryImpl.createTileInternal(java.lang.String):com.android.systemui.qs.tileimpl.QSTileImpl");
    }

    public QSTileView createTileView(QSTile qSTile, boolean z) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(((QSHost) this.mQsHostLazy.get()).getContext(), C2018R$style.qs_theme);
        QSIconView createTileView = qSTile.createTileView(contextThemeWrapper);
        if (z) {
            return new QSTileBaseView(contextThemeWrapper, createTileView, z);
        }
        return new QSTileView(contextThemeWrapper, createTileView);
    }
}
