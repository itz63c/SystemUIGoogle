package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.util.LifecycleActivity;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsProviderSelectorActivity.kt */
public final class ControlsProviderSelectorActivity extends LifecycleActivity {
    private final Executor backExecutor;
    private final ControlsController controlsController;
    private final ControlsProviderSelectorActivity$currentUserTracker$1 currentUserTracker;
    private final Executor executor;
    /* access modifiers changed from: private */
    public final ControlsListingController listingController;
    private RecyclerView recyclerView;

    public ControlsProviderSelectorActivity(Executor executor2, Executor executor3, ControlsListingController controlsListingController, ControlsController controlsController2, BroadcastDispatcher broadcastDispatcher) {
        Intrinsics.checkParameterIsNotNull(executor2, "executor");
        Intrinsics.checkParameterIsNotNull(executor3, "backExecutor");
        Intrinsics.checkParameterIsNotNull(controlsListingController, "listingController");
        Intrinsics.checkParameterIsNotNull(controlsController2, "controlsController");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher, "broadcastDispatcher");
        this.executor = executor2;
        this.backExecutor = executor3;
        this.listingController = controlsListingController;
        this.controlsController = controlsController2;
        this.currentUserTracker = new ControlsProviderSelectorActivity$currentUserTracker$1(this, broadcastDispatcher, broadcastDispatcher);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C2013R$layout.controls_management);
        ViewStub viewStub = (ViewStub) requireViewById(C2011R$id.stub);
        viewStub.setLayoutResource(C2013R$layout.controls_management_apps);
        viewStub.inflate();
        View requireViewById = requireViewById(C2011R$id.list);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById(R.id.list)");
        RecyclerView recyclerView2 = (RecyclerView) requireViewById;
        this.recyclerView = recyclerView2;
        String str = "recyclerView";
        if (recyclerView2 != null) {
            Executor executor2 = this.backExecutor;
            Executor executor3 = this.executor;
            Lifecycle lifecycle = getLifecycle();
            ControlsListingController controlsListingController = this.listingController;
            LayoutInflater from = LayoutInflater.from(this);
            Intrinsics.checkExpressionValueIsNotNull(from, "LayoutInflater.from(this)");
            ControlsProviderSelectorActivity$onCreate$2 controlsProviderSelectorActivity$onCreate$2 = new ControlsProviderSelectorActivity$onCreate$2(this);
            Resources resources = getResources();
            String str2 = "resources";
            Intrinsics.checkExpressionValueIsNotNull(resources, str2);
            FavoritesRenderer favoritesRenderer = new FavoritesRenderer(resources, new ControlsProviderSelectorActivity$onCreate$3(this.controlsController));
            Resources resources2 = getResources();
            Intrinsics.checkExpressionValueIsNotNull(resources2, str2);
            AppAdapter appAdapter = new AppAdapter(executor2, executor3, lifecycle, controlsListingController, from, controlsProviderSelectorActivity$onCreate$2, favoritesRenderer, resources2);
            recyclerView2.setAdapter(appAdapter);
            RecyclerView recyclerView3 = this.recyclerView;
            if (recyclerView3 != null) {
                recyclerView3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                View requireViewById2 = requireViewById(C2011R$id.title);
                Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById<TextView>(R.id.title)");
                ((TextView) requireViewById2).setText(getResources().getText(C2017R$string.controls_providers_title));
                View requireViewById3 = requireViewById(C2011R$id.subtitle);
                Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "requireViewById<TextView>(R.id.subtitle)");
                ((TextView) requireViewById3).setText(getResources().getText(C2017R$string.controls_providers_subtitle));
                ((Button) requireViewById(C2011R$id.done)).setOnClickListener(new ControlsProviderSelectorActivity$onCreate$4(this));
                this.currentUserTracker.startTracking();
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }

    public final void launchFavoritingActivity(ComponentName componentName) {
        this.backExecutor.execute(new ControlsProviderSelectorActivity$launchFavoritingActivity$1(this, componentName));
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.currentUserTracker.stopTracking();
        super.onDestroy();
    }
}
