package com.google.android.systemui;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IWallpaperManager;
import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.os.Handler;
import android.os.RemoteException;
import android.util.ArraySet;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.DejankUtils;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ScrimState;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.wakelock.DelayedWakeLock.Builder;
import com.google.android.collect.Sets;
import java.util.function.Supplier;

public class LiveWallpaperScrimController extends ScrimController {
    private static ArraySet<ComponentName> REDUCED_SCRIM_WALLPAPERS;
    private int mCurrentUser = ActivityManager.getCurrentUser();
    private final LockscreenWallpaper mLockscreenWallpaper;
    private final IWallpaperManager mWallpaperManager;

    static {
        String str = "com.breel.wallpapers18";
        REDUCED_SCRIM_WALLPAPERS = Sets.newArraySet(new ComponentName[]{new ComponentName("com.breel.geswallpapers", "com.breel.geswallpapers.wallpapers.EarthWallpaperService"), new ComponentName(str, "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV1"), new ComponentName(str, "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV2"), new ComponentName(str, "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV3"), new ComponentName(str, "com.breel.wallpapers18.surfandturf.wallpapers.variations.SurfAndTurfWallpaperV2"), new ComponentName(str, "com.breel.wallpapers18.cities.wallpapers.variations.SanFranciscoWallpaper"), new ComponentName(str, "com.breel.wallpapers18.cities.wallpapers.variations.NewYorkWallpaper")});
    }

    public LiveWallpaperScrimController(LightBarController lightBarController, DozeParameters dozeParameters, AlarmManager alarmManager, KeyguardStateController keyguardStateController, Builder builder, Handler handler, IWallpaperManager iWallpaperManager, LockscreenWallpaper lockscreenWallpaper, KeyguardUpdateMonitor keyguardUpdateMonitor, SysuiColorExtractor sysuiColorExtractor, DockManager dockManager, BlurUtils blurUtils) {
        super(lightBarController, dozeParameters, alarmManager, keyguardStateController, builder, handler, keyguardUpdateMonitor, sysuiColorExtractor, dockManager, blurUtils);
        this.mWallpaperManager = iWallpaperManager;
        this.mLockscreenWallpaper = lockscreenWallpaper;
    }

    public void transitionTo(ScrimState scrimState) {
        if (scrimState == ScrimState.KEYGUARD) {
            updateScrimValues();
        }
        super.transitionTo(scrimState);
    }

    private void updateScrimValues() {
        if (isReducedScrimWallpaperSet()) {
            setScrimBehindValues(0.25f);
        } else {
            setScrimBehindValues(0.2f);
        }
    }

    public void setCurrentUser(int i) {
        this.mCurrentUser = i;
        updateScrimValues();
    }

    private boolean isReducedScrimWallpaperSet() {
        return ((Boolean) DejankUtils.whitelistIpcs((Supplier<T>) new Supplier() {
            public final Object get() {
                return LiveWallpaperScrimController.this.lambda$isReducedScrimWallpaperSet$0$LiveWallpaperScrimController();
            }
        })).booleanValue();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$isReducedScrimWallpaperSet$0 */
    public /* synthetic */ Boolean lambda$isReducedScrimWallpaperSet$0$LiveWallpaperScrimController() {
        try {
            WallpaperInfo wallpaperInfo = this.mWallpaperManager.getWallpaperInfo(this.mCurrentUser);
            if (wallpaperInfo != null && REDUCED_SCRIM_WALLPAPERS.contains(wallpaperInfo.getComponent())) {
                return Boolean.valueOf(this.mLockscreenWallpaper.getBitmap() == null);
            }
        } catch (RemoteException unused) {
        }
        return Boolean.FALSE;
    }
}
