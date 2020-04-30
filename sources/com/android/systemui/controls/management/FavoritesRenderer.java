package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.content.res.Resources;
import com.android.systemui.C2015R$plurals;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AppAdapter.kt */
public final class FavoritesRenderer {
    private final Function1<ComponentName, Integer> favoriteFunction;
    private final Resources resources;

    public FavoritesRenderer(Resources resources2, Function1<? super ComponentName, Integer> function1) {
        Intrinsics.checkParameterIsNotNull(resources2, "resources");
        Intrinsics.checkParameterIsNotNull(function1, "favoriteFunction");
        this.resources = resources2;
        this.favoriteFunction = function1;
    }

    public final String renderFavoritesForComponent(ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        int intValue = ((Number) this.favoriteFunction.invoke(componentName)).intValue();
        if (intValue == 0) {
            return "";
        }
        String quantityString = this.resources.getQuantityString(C2015R$plurals.controls_number_of_favorites, intValue, new Object[]{Integer.valueOf(intValue)});
        Intrinsics.checkExpressionValueIsNotNull(quantityString, "resources.getQuantityStr…r_of_favorites, qty, qty)");
        return quantityString;
    }
}
