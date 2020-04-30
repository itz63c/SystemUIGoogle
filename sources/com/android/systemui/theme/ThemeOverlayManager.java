package com.android.systemui.theme;

import android.content.om.OverlayInfo;
import android.content.om.OverlayManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ThemeOverlayManager {
    static final String ANDROID_PACKAGE = "android";
    static final String OVERLAY_CATEGORY_COLOR = "android.theme.customization.accent_color";
    static final String OVERLAY_CATEGORY_FONT = "android.theme.customization.font";
    static final String OVERLAY_CATEGORY_ICON_ANDROID = "android.theme.customization.icon_pack.android";
    static final String OVERLAY_CATEGORY_ICON_LAUNCHER = "android.theme.customization.icon_pack.launcher";
    static final String OVERLAY_CATEGORY_ICON_SETTINGS = "android.theme.customization.icon_pack.settings";
    static final String OVERLAY_CATEGORY_ICON_SYSUI = "android.theme.customization.icon_pack.systemui";
    static final String OVERLAY_CATEGORY_ICON_THEME_PICKER = "android.theme.customization.icon_pack.themepicker";
    static final String OVERLAY_CATEGORY_SHAPE = "android.theme.customization.adaptive_icon_shape";
    static final String SETTINGS_PACKAGE = "com.android.settings";
    static final Set<String> SYSTEM_USER_CATEGORIES = Sets.newHashSet(new String[]{OVERLAY_CATEGORY_COLOR, OVERLAY_CATEGORY_FONT, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_ICON_ANDROID, OVERLAY_CATEGORY_ICON_SYSUI});
    static final String SYSUI_PACKAGE = "com.android.systemui";
    static final List<String> THEME_CATEGORIES = Lists.newArrayList(new String[]{OVERLAY_CATEGORY_ICON_LAUNCHER, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_FONT, OVERLAY_CATEGORY_COLOR, OVERLAY_CATEGORY_ICON_ANDROID, OVERLAY_CATEGORY_ICON_SYSUI, OVERLAY_CATEGORY_ICON_SETTINGS, OVERLAY_CATEGORY_ICON_THEME_PICKER});
    private final Map<String, String> mCategoryToTargetPackage = new ArrayMap();
    private final Executor mExecutor;
    private final String mLauncherPackage;
    private final OverlayManager mOverlayManager;
    private final Map<String, Set<String>> mTargetPackageToCategories = new ArrayMap();
    private final String mThemePickerPackage;

    ThemeOverlayManager(OverlayManager overlayManager, Executor executor, String str, String str2) {
        this.mOverlayManager = overlayManager;
        this.mExecutor = executor;
        this.mLauncherPackage = str;
        this.mThemePickerPackage = str2;
        Map<String, Set<String>> map = this.mTargetPackageToCategories;
        String str3 = OVERLAY_CATEGORY_COLOR;
        String str4 = OVERLAY_CATEGORY_FONT;
        String str5 = OVERLAY_CATEGORY_SHAPE;
        String str6 = OVERLAY_CATEGORY_ICON_ANDROID;
        HashSet newHashSet = Sets.newHashSet(new String[]{str3, str4, str5, str6});
        String str7 = ANDROID_PACKAGE;
        map.put(str7, newHashSet);
        Map<String, Set<String>> map2 = this.mTargetPackageToCategories;
        String str8 = OVERLAY_CATEGORY_ICON_SYSUI;
        HashSet newHashSet2 = Sets.newHashSet(new String[]{str8});
        String str9 = SYSUI_PACKAGE;
        map2.put(str9, newHashSet2);
        Map<String, Set<String>> map3 = this.mTargetPackageToCategories;
        String str10 = OVERLAY_CATEGORY_ICON_SETTINGS;
        HashSet newHashSet3 = Sets.newHashSet(new String[]{str10});
        String str11 = SETTINGS_PACKAGE;
        map3.put(str11, newHashSet3);
        Map<String, Set<String>> map4 = this.mTargetPackageToCategories;
        String str12 = this.mLauncherPackage;
        String str13 = OVERLAY_CATEGORY_ICON_LAUNCHER;
        map4.put(str12, Sets.newHashSet(new String[]{str13}));
        Map<String, Set<String>> map5 = this.mTargetPackageToCategories;
        String str14 = this.mThemePickerPackage;
        String str15 = OVERLAY_CATEGORY_ICON_THEME_PICKER;
        map5.put(str14, Sets.newHashSet(new String[]{str15}));
        this.mCategoryToTargetPackage.put(str3, str7);
        this.mCategoryToTargetPackage.put(str4, str7);
        this.mCategoryToTargetPackage.put(str5, str7);
        this.mCategoryToTargetPackage.put(str6, str7);
        this.mCategoryToTargetPackage.put(str8, str9);
        this.mCategoryToTargetPackage.put(str10, str11);
        this.mCategoryToTargetPackage.put(str13, this.mLauncherPackage);
        this.mCategoryToTargetPackage.put(str15, this.mThemePickerPackage);
    }

    /* access modifiers changed from: 0000 */
    public void applyCurrentUserOverlays(Map<String, String> map, Set<UserHandle> set) {
        HashSet hashSet = new HashSet(THEME_CATEGORIES);
        hashSet.removeAll(map.keySet());
        Set set2 = (Set) hashSet.stream().map(new Function() {
            public final Object apply(Object obj) {
                return ThemeOverlayManager.this.lambda$applyCurrentUserOverlays$0$ThemeOverlayManager((String) obj);
            }
        }).collect(Collectors.toSet());
        ArrayList arrayList = new ArrayList();
        set2.forEach(new Consumer(arrayList) {
            public final /* synthetic */ List f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                ThemeOverlayManager.this.lambda$applyCurrentUserOverlays$1$ThemeOverlayManager(this.f$1, (String) obj);
            }
        });
        Map map2 = (Map) arrayList.stream().filter(new Predicate() {
            public final boolean test(Object obj) {
                return ThemeOverlayManager.this.lambda$applyCurrentUserOverlays$2$ThemeOverlayManager((OverlayInfo) obj);
            }
        }).filter(new Predicate(hashSet) {
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return this.f$0.contains(((OverlayInfo) obj).category);
            }
        }).filter($$Lambda$ThemeOverlayManager$vK2aROqMaNCgMb7ixs5bp0NF79c.INSTANCE).collect(Collectors.toMap($$Lambda$ThemeOverlayManager$tpreaivLMVK4R3Uf26BCg27Af8.INSTANCE, $$Lambda$ThemeOverlayManager$GlioDk646gj_04NkaTcsRN_awI4.INSTANCE));
        for (String str : THEME_CATEGORIES) {
            if (map.containsKey(str)) {
                setEnabled((String) map.get(str), str, set, true);
            } else if (map2.containsKey(str)) {
                setEnabled((String) map2.get(str), str, set, false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyCurrentUserOverlays$0 */
    public /* synthetic */ String lambda$applyCurrentUserOverlays$0$ThemeOverlayManager(String str) {
        return (String) this.mCategoryToTargetPackage.get(str);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyCurrentUserOverlays$1 */
    public /* synthetic */ void lambda$applyCurrentUserOverlays$1$ThemeOverlayManager(List list, String str) {
        list.addAll(this.mOverlayManager.getOverlayInfosForTarget(str, UserHandle.SYSTEM));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyCurrentUserOverlays$2 */
    public /* synthetic */ boolean lambda$applyCurrentUserOverlays$2$ThemeOverlayManager(OverlayInfo overlayInfo) {
        return ((Set) this.mTargetPackageToCategories.get(overlayInfo.targetPackageName)).contains(overlayInfo.category);
    }

    private void setEnabled(String str, String str2, Set<UserHandle> set, boolean z) {
        for (UserHandle enabledAsync : set) {
            setEnabledAsync(str, enabledAsync, z);
        }
        if (!set.contains(UserHandle.SYSTEM) && SYSTEM_USER_CATEGORIES.contains(str2)) {
            setEnabledAsync(str, UserHandle.SYSTEM, z);
        }
    }

    private void setEnabledAsync(String str, UserHandle userHandle, boolean z) {
        this.mExecutor.execute(new Runnable(str, userHandle, z) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ UserHandle f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ThemeOverlayManager.this.lambda$setEnabledAsync$7$ThemeOverlayManager(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setEnabledAsync$7 */
    public /* synthetic */ void lambda$setEnabledAsync$7$ThemeOverlayManager(String str, UserHandle userHandle, boolean z) {
        if (z) {
            try {
                this.mOverlayManager.setEnabledExclusiveInCategory(str, userHandle);
            } catch (IllegalStateException | SecurityException e) {
                Object[] objArr = {str, userHandle, Boolean.valueOf(z)};
                Log.e("ThemeOverlayManager", String.format("setEnabled failed: %s %s %b", objArr), e);
            }
        } else {
            this.mOverlayManager.setEnabled(str, false, userHandle);
        }
    }
}
