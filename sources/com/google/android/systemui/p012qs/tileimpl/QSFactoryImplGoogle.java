package com.google.android.systemui.p012qs.tileimpl;

import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSFactoryImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
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
import com.android.systemui.plugins.p006qs.QSTile;
import com.android.systemui.util.leak.GarbageMonitor.MemoryTile;
import com.google.android.systemui.p012qs.tiles.BatteryShareTile;
import dagger.Lazy;
import javax.inject.Provider;

/* renamed from: com.google.android.systemui.qs.tileimpl.QSFactoryImplGoogle */
public class QSFactoryImplGoogle extends QSFactoryImpl {
    private final Provider<BatteryShareTile> mBatteryShareTileProvider;

    public QSFactoryImplGoogle(Lazy<QSHost> lazy, Provider<WifiTile> provider, Provider<BluetoothTile> provider2, Provider<CellularTile> provider3, Provider<DndTile> provider4, Provider<ColorInversionTile> provider5, Provider<AirplaneModeTile> provider6, Provider<WorkModeTile> provider7, Provider<RotationLockTile> provider8, Provider<FlashlightTile> provider9, Provider<LocationTile> provider10, Provider<CastTile> provider11, Provider<HotspotTile> provider12, Provider<UserTile> provider13, Provider<BatterySaverTile> provider14, Provider<DataSaverTile> provider15, Provider<NightDisplayTile> provider16, Provider<NfcTile> provider17, Provider<MemoryTile> provider18, Provider<UiModeNightTile> provider19, Provider<ScreenRecordTile> provider20, Provider<BatteryShareTile> provider21) {
        super(lazy, provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20);
        this.mBatteryShareTileProvider = provider21;
    }

    public QSTile createTile(String str) {
        QSTileImpl createTileInternal = createTileInternal(str);
        if (createTileInternal != null) {
            return createTileInternal;
        }
        return super.createTile(str);
    }

    private QSTileImpl createTileInternal(String str) {
        if (((str.hashCode() == 1099846370 && str.equals("reverse")) ? (char) 0 : 65535) != 0) {
            return null;
        }
        return (QSTileImpl) this.mBatteryShareTileProvider.get();
    }
}
