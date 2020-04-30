package com.android.systemui.controls.management;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.Prefs;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.TooltipManager;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
public final class ControlsFavoritingActivity extends Activity {
    /* access modifiers changed from: private */
    public CharSequence appName;
    private Comparator<StructureContainer> comparator;
    /* access modifiers changed from: private */
    public ComponentName component;
    /* access modifiers changed from: private */
    public final ControlsControllerImpl controller;
    private final ControlsFavoritingActivity$currentUserTracker$1 currentUserTracker;
    private View doneButton;
    /* access modifiers changed from: private */
    public final Executor executor;
    private View iconFrame;
    private ImageView iconView;
    /* access modifiers changed from: private */
    public List<StructureContainer> listOfStructures = CollectionsKt__CollectionsKt.emptyList();
    private final ControlsFavoritingActivity$listingCallback$1 listingCallback;
    private final ControlsListingController listingController;
    /* access modifiers changed from: private */
    public TooltipManager mTooltipManager;
    private ManagementPageIndicator pageIndicator;
    private TextView statusText;
    private ViewPager2 structurePager;
    private TextView titleView;

    public static final /* synthetic */ Comparator access$getComparator$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        Comparator<StructureContainer> comparator2 = controlsFavoritingActivity.comparator;
        if (comparator2 != null) {
            return comparator2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("comparator");
        throw null;
    }

    public static final /* synthetic */ View access$getDoneButton$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        View view = controlsFavoritingActivity.doneButton;
        if (view != null) {
            return view;
        }
        Intrinsics.throwUninitializedPropertyAccessException("doneButton");
        throw null;
    }

    public static final /* synthetic */ View access$getIconFrame$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        View view = controlsFavoritingActivity.iconFrame;
        if (view != null) {
            return view;
        }
        Intrinsics.throwUninitializedPropertyAccessException("iconFrame");
        throw null;
    }

    public static final /* synthetic */ ImageView access$getIconView$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        ImageView imageView = controlsFavoritingActivity.iconView;
        if (imageView != null) {
            return imageView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("iconView");
        throw null;
    }

    public static final /* synthetic */ ManagementPageIndicator access$getPageIndicator$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        ManagementPageIndicator managementPageIndicator = controlsFavoritingActivity.pageIndicator;
        if (managementPageIndicator != null) {
            return managementPageIndicator;
        }
        Intrinsics.throwUninitializedPropertyAccessException("pageIndicator");
        throw null;
    }

    public static final /* synthetic */ TextView access$getStatusText$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        TextView textView = controlsFavoritingActivity.statusText;
        if (textView != null) {
            return textView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("statusText");
        throw null;
    }

    public static final /* synthetic */ ViewPager2 access$getStructurePager$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        ViewPager2 viewPager2 = controlsFavoritingActivity.structurePager;
        if (viewPager2 != null) {
            return viewPager2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("structurePager");
        throw null;
    }

    public static final /* synthetic */ TextView access$getTitleView$p(ControlsFavoritingActivity controlsFavoritingActivity) {
        TextView textView = controlsFavoritingActivity.titleView;
        if (textView != null) {
            return textView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("titleView");
        throw null;
    }

    public ControlsFavoritingActivity(Executor executor2, ControlsControllerImpl controlsControllerImpl, ControlsListingController controlsListingController, BroadcastDispatcher broadcastDispatcher) {
        Intrinsics.checkParameterIsNotNull(executor2, "executor");
        Intrinsics.checkParameterIsNotNull(controlsControllerImpl, "controller");
        Intrinsics.checkParameterIsNotNull(controlsListingController, "listingController");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher, "broadcastDispatcher");
        this.executor = executor2;
        this.controller = controlsControllerImpl;
        this.listingController = controlsListingController;
        this.currentUserTracker = new ControlsFavoritingActivity$currentUserTracker$1(this, broadcastDispatcher, broadcastDispatcher);
        this.listingCallback = new ControlsFavoritingActivity$listingCallback$1(this);
    }

    public void onBackPressed() {
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Resources resources = getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "resources");
        Configuration configuration = resources.getConfiguration();
        Intrinsics.checkExpressionValueIsNotNull(configuration, "resources.configuration");
        Collator instance = Collator.getInstance(configuration.getLocales().get(0));
        Intrinsics.checkExpressionValueIsNotNull(instance, "collator");
        this.comparator = new ControlsFavoritingActivity$onCreate$$inlined$compareBy$1(instance);
        this.appName = getIntent().getCharSequenceExtra("extra_app_label");
        this.component = (ComponentName) getIntent().getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        bindViews();
        setUpPager();
        loadControls();
        this.listingController.addCallback(this.listingCallback);
        this.currentUserTracker.startTracking();
    }

    private final void loadControls() {
        ComponentName componentName = this.component;
        if (componentName != null) {
            TextView textView = this.statusText;
            if (textView != null) {
                textView.setText(getResources().getText(17040319));
                this.controller.loadForComponent(componentName, new ControlsFavoritingActivity$loadControls$$inlined$let$lambda$1(getResources().getText(C2017R$string.controls_favorite_other_zone_header), this));
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("statusText");
            throw null;
        }
    }

    private final void setUpPager() {
        ViewPager2 viewPager2 = this.structurePager;
        if (viewPager2 != null) {
            viewPager2.setAdapter(new StructureAdapter(CollectionsKt__CollectionsKt.emptyList()));
            viewPager2.registerOnPageChangeCallback(new ControlsFavoritingActivity$setUpPager$$inlined$apply$lambda$1(this));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("structurePager");
        throw null;
    }

    private final void bindViews() {
        setContentView(C2013R$layout.controls_management);
        ViewStub viewStub = (ViewStub) requireViewById(C2011R$id.stub);
        viewStub.setLayoutResource(C2013R$layout.controls_management_favorites);
        viewStub.inflate();
        View requireViewById = requireViewById(C2011R$id.status_message);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById(R.id.status_message)");
        this.statusText = (TextView) requireViewById;
        if (shouldShowTooltip()) {
            TextView textView = this.statusText;
            if (textView != null) {
                Context context = textView.getContext();
                Intrinsics.checkExpressionValueIsNotNull(context, "statusText.context");
                TooltipManager tooltipManager = new TooltipManager(context, "ControlsStructureSwipeTooltipCount", 2, false, 8, null);
                this.mTooltipManager = tooltipManager;
                addContentView(tooltipManager != null ? tooltipManager.getLayout() : null, new LayoutParams(-2, -2, 51));
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("statusText");
                throw null;
            }
        }
        View requireViewById2 = requireViewById(C2011R$id.structure_page_indicator);
        ManagementPageIndicator managementPageIndicator = (ManagementPageIndicator) requireViewById2;
        managementPageIndicator.addOnLayoutChangeListener(new ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$1(this));
        managementPageIndicator.setVisibilityListener(new ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$2(this));
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById<Manageme…}\n            }\n        }");
        this.pageIndicator = managementPageIndicator;
        View requireViewById3 = requireViewById(C2011R$id.title);
        TextView textView2 = (TextView) requireViewById3;
        CharSequence charSequence = this.appName;
        if (charSequence == null) {
            charSequence = textView2.getResources().getText(C2017R$string.controls_favorite_default_title);
        }
        textView2.setText(charSequence);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "requireViewById<TextView…_default_title)\n        }");
        this.titleView = textView2;
        View requireViewById4 = requireViewById(C2011R$id.subtitle);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById4, "requireViewById<TextView>(R.id.subtitle)");
        ((TextView) requireViewById4).setText(getResources().getText(C2017R$string.controls_favorite_subtitle));
        View requireViewById5 = requireViewById(16908294);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById5, "requireViewById(com.android.internal.R.id.icon)");
        this.iconView = (ImageView) requireViewById5;
        View requireViewById6 = requireViewById(C2011R$id.icon_frame);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById6, "requireViewById(R.id.icon_frame)");
        this.iconFrame = requireViewById6;
        View requireViewById7 = requireViewById(C2011R$id.structure_pager);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById7, "requireViewById<ViewPager2>(R.id.structure_pager)");
        ViewPager2 viewPager2 = (ViewPager2) requireViewById7;
        this.structurePager = viewPager2;
        if (viewPager2 != null) {
            viewPager2.registerOnPageChangeCallback(new ControlsFavoritingActivity$bindViews$4(this));
            bindButtons();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("structurePager");
        throw null;
    }

    private final void bindButtons() {
        Button button = (Button) requireViewById(C2011R$id.other_apps);
        button.setVisibility(0);
        button.setOnClickListener(new ControlsFavoritingActivity$bindButtons$$inlined$apply$lambda$1(this));
        View requireViewById = requireViewById(C2011R$id.done);
        Button button2 = (Button) requireViewById;
        button2.setEnabled(false);
        button2.setOnClickListener(new ControlsFavoritingActivity$bindButtons$$inlined$apply$lambda$2(this));
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById<Button>(…)\n            }\n        }");
        this.doneButton = requireViewById;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        TooltipManager tooltipManager = this.mTooltipManager;
        if (tooltipManager != null) {
            tooltipManager.hide(false);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        Intrinsics.checkParameterIsNotNull(configuration, "newConfig");
        super.onConfigurationChanged(configuration);
        TooltipManager tooltipManager = this.mTooltipManager;
        if (tooltipManager != null) {
            tooltipManager.hide(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.currentUserTracker.stopTracking();
        this.listingController.removeCallback(this.listingCallback);
        this.controller.cancelLoad();
        super.onDestroy();
    }

    private final boolean shouldShowTooltip() {
        return Prefs.getInt(getApplicationContext(), "ControlsStructureSwipeTooltipCount", 0) < 2;
    }
}
