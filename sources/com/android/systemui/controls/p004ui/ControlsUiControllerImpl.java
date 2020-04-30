package com.android.systemui.controls.p004ui;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListPopupWindow;
import android.widget.Space;
import android.widget.TextView;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.controls.controller.ControlInfo;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.controller.StructureInfo;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.management.ControlsListingController.ControlsListingCallback;
import com.android.systemui.util.concurrency.DelayableExecutor;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl */
/* compiled from: ControlsUiControllerImpl.kt */
public final class ControlsUiControllerImpl implements ControlsUiController {
    private static final ComponentName EMPTY_COMPONENT;
    private static final StructureInfo EMPTY_STRUCTURE;
    /* access modifiers changed from: private */
    public Dialog activeDialog;
    private final SelectionItem addControlsItem;
    private List<StructureInfo> allStructures;
    private final DelayableExecutor bgExecutor;
    private final Context context;
    /* access modifiers changed from: private */
    public final Map<ControlKey, ControlViewHolder> controlViewsById = new LinkedHashMap();
    private final Map<ControlKey, ControlWithState> controlsById = new LinkedHashMap();
    private final Lazy<ControlsController> controlsController;
    private final Lazy<ControlsListingController> controlsListingController;
    private boolean hidden = true;
    /* access modifiers changed from: private */
    public List<SelectionItem> lastItems;
    private ControlsListingCallback listingCallback;
    private ViewGroup parent;
    /* access modifiers changed from: private */
    public ListPopupWindow popup;
    private StructureInfo selectedStructure = EMPTY_STRUCTURE;
    private final SharedPreferences sharedPreferences;
    private final DelayableExecutor uiExecutor;

    public ControlsUiControllerImpl(Lazy<ControlsController> lazy, Context context2, DelayableExecutor delayableExecutor, DelayableExecutor delayableExecutor2, Lazy<ControlsListingController> lazy2, SharedPreferences sharedPreferences2) {
        Intrinsics.checkParameterIsNotNull(lazy, "controlsController");
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(delayableExecutor, "uiExecutor");
        Intrinsics.checkParameterIsNotNull(delayableExecutor2, "bgExecutor");
        Intrinsics.checkParameterIsNotNull(lazy2, "controlsListingController");
        Intrinsics.checkParameterIsNotNull(sharedPreferences2, "sharedPreferences");
        this.controlsController = lazy;
        this.context = context2;
        this.uiExecutor = delayableExecutor;
        this.bgExecutor = delayableExecutor2;
        this.controlsListingController = lazy2;
        this.sharedPreferences = sharedPreferences2;
        Drawable drawable = this.context.getDrawable(C2010R$drawable.ic_add);
        drawable.setTint(this.context.getResources().getColor(C2008R$color.control_secondary_text, null));
        String string = this.context.getResources().getString(C2017R$string.controls_providers_title);
        Intrinsics.checkExpressionValueIsNotNull(string, "context.resources.getStr…controls_providers_title)");
        Intrinsics.checkExpressionValueIsNotNull(drawable, "addDrawable");
        this.addControlsItem = new SelectionItem(string, "", drawable, EMPTY_COMPONENT);
    }

    public static final /* synthetic */ List access$getLastItems$p(ControlsUiControllerImpl controlsUiControllerImpl) {
        List<SelectionItem> list = controlsUiControllerImpl.lastItems;
        if (list != null) {
            return list;
        }
        Intrinsics.throwUninitializedPropertyAccessException("lastItems");
        throw null;
    }

    public final Context getContext() {
        return this.context;
    }

    public final DelayableExecutor getUiExecutor() {
        return this.uiExecutor;
    }

    public final DelayableExecutor getBgExecutor() {
        return this.bgExecutor;
    }

    static {
        String str = "";
        ComponentName componentName = new ComponentName(str, str);
        EMPTY_COMPONENT = componentName;
        EMPTY_STRUCTURE = new StructureInfo(componentName, str, new ArrayList());
    }

    public boolean getAvailable() {
        return ((ControlsController) this.controlsController.get()).getAvailable();
    }

    private final ControlsListingCallback createCallback(Function1<? super List<SelectionItem>, Unit> function1) {
        return new ControlsUiControllerImpl$createCallback$1(this, function1);
    }

    public void show(ViewGroup viewGroup) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        Log.d("ControlsUiController", "show()");
        this.parent = viewGroup;
        this.hidden = false;
        List<StructureInfo> favorites = ((ControlsController) this.controlsController.get()).getFavorites();
        this.allStructures = favorites;
        String str = "allStructures";
        if (favorites != null) {
            this.selectedStructure = loadPreference(favorites);
            if (((ControlsController) this.controlsController.get()).addSeedingFavoritesCallback(new ControlsUiControllerImpl$show$cb$1(this, viewGroup))) {
                this.listingCallback = createCallback(new ControlsUiControllerImpl$show$1(this));
            } else {
                if (this.selectedStructure.getControls().isEmpty()) {
                    List<StructureInfo> list = this.allStructures;
                    if (list == null) {
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        throw null;
                    } else if (list.size() <= 1) {
                        this.listingCallback = createCallback(new ControlsUiControllerImpl$show$2(this));
                    }
                }
                List<ControlInfo> controls = this.selectedStructure.getControls();
                ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controls, 10));
                for (ControlInfo controlWithState : controls) {
                    arrayList.add(new ControlWithState(this.selectedStructure.getComponentName(), controlWithState, null));
                }
                Map<ControlKey, ControlWithState> map = this.controlsById;
                for (Object next : arrayList) {
                    map.put(new ControlKey(this.selectedStructure.getComponentName(), ((ControlWithState) next).getCi().getControlId()), next);
                }
                this.listingCallback = createCallback(new ControlsUiControllerImpl$show$5(this));
                ((ControlsController) this.controlsController.get()).subscribeToFavorites(this.selectedStructure);
            }
            ControlsListingController controlsListingController2 = (ControlsListingController) this.controlsListingController.get();
            ControlsListingCallback controlsListingCallback = this.listingCallback;
            if (controlsListingCallback != null) {
                controlsListingController2.addCallback(controlsListingCallback);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
    }

    /* access modifiers changed from: private */
    public final void reload(ViewGroup viewGroup) {
        if (!this.hidden) {
            show(viewGroup);
        }
    }

    /* access modifiers changed from: private */
    public final void showSeedingView(List<SelectionItem> list) {
        ViewGroup viewGroup = this.parent;
        String str = "parent";
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            LayoutInflater from = LayoutInflater.from(this.context);
            int i = C2013R$layout.controls_no_favorites;
            ViewGroup viewGroup2 = this.parent;
            if (viewGroup2 != null) {
                from.inflate(i, viewGroup2, true);
                ViewGroup viewGroup3 = this.parent;
                if (viewGroup3 != null) {
                    ((TextView) viewGroup3.requireViewById(C2011R$id.controls_subtitle)).setVisibility(0);
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
    }

    /* JADX WARNING: type inference failed for: r5v3 */
    /* JADX WARNING: type inference failed for: r6v0, types: [com.android.systemui.controls.ui.ControlsUiControllerImpl$sam$android_view_View_OnClickListener$0] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void showInitialSetupView(java.util.List<com.android.systemui.controls.p004ui.SelectionItem> r8) {
        /*
            r7 = this;
            android.view.ViewGroup r0 = r7.parent
            r1 = 0
            java.lang.String r2 = "parent"
            if (r0 == 0) goto L_0x0099
            r0.removeAllViews()
            android.content.Context r0 = r7.context
            android.view.LayoutInflater r0 = android.view.LayoutInflater.from(r0)
            int r3 = com.android.systemui.C2013R$layout.controls_no_favorites
            android.view.ViewGroup r4 = r7.parent
            if (r4 == 0) goto L_0x0095
            r5 = 1
            r0.inflate(r3, r4, r5)
            android.view.ViewGroup r3 = r7.parent
            if (r3 == 0) goto L_0x0091
            int r4 = com.android.systemui.C2011R$id.controls_no_favorites_group
            android.view.View r3 = r3.requireViewById(r4)
            java.lang.String r4 = "null cannot be cast to non-null type android.view.ViewGroup"
            if (r3 == 0) goto L_0x008b
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            android.content.Context r5 = r7.context
            kotlin.jvm.functions.Function1 r5 = r7.launchSelectorActivityListener(r5)
            if (r5 == 0) goto L_0x0038
            com.android.systemui.controls.ui.ControlsUiControllerImpl$sam$android_view_View_OnClickListener$0 r6 = new com.android.systemui.controls.ui.ControlsUiControllerImpl$sam$android_view_View_OnClickListener$0
            r6.<init>(r5)
            r5 = r6
        L_0x0038:
            android.view.View$OnClickListener r5 = (android.view.View.OnClickListener) r5
            r3.setOnClickListener(r5)
            android.view.ViewGroup r7 = r7.parent
            if (r7 == 0) goto L_0x0087
            int r1 = com.android.systemui.C2011R$id.controls_icon_row
            android.view.View r7 = r7.requireViewById(r1)
            if (r7 == 0) goto L_0x0081
            android.view.ViewGroup r7 = (android.view.ViewGroup) r7
            java.util.Iterator r8 = r8.iterator()
        L_0x004f:
            boolean r1 = r8.hasNext()
            if (r1 == 0) goto L_0x0080
            java.lang.Object r1 = r8.next()
            com.android.systemui.controls.ui.SelectionItem r1 = (com.android.systemui.controls.p004ui.SelectionItem) r1
            int r2 = com.android.systemui.C2013R$layout.controls_icon
            r4 = 0
            android.view.View r2 = r0.inflate(r2, r3, r4)
            if (r2 == 0) goto L_0x0078
            android.widget.ImageView r2 = (android.widget.ImageView) r2
            java.lang.CharSequence r4 = r1.getTitle()
            r2.setContentDescription(r4)
            android.graphics.drawable.Drawable r1 = r1.getIcon()
            r2.setImageDrawable(r1)
            r7.addView(r2)
            goto L_0x004f
        L_0x0078:
            kotlin.TypeCastException r7 = new kotlin.TypeCastException
            java.lang.String r8 = "null cannot be cast to non-null type android.widget.ImageView"
            r7.<init>(r8)
            throw r7
        L_0x0080:
            return
        L_0x0081:
            kotlin.TypeCastException r7 = new kotlin.TypeCastException
            r7.<init>(r4)
            throw r7
        L_0x0087:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        L_0x008b:
            kotlin.TypeCastException r7 = new kotlin.TypeCastException
            r7.<init>(r4)
            throw r7
        L_0x0091:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        L_0x0095:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        L_0x0099:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.ControlsUiControllerImpl.showInitialSetupView(java.util.List):void");
    }

    private final Function1<View, Unit> launchSelectorActivityListener(Context context2) {
        return new ControlsUiControllerImpl$launchSelectorActivityListener$1(context2);
    }

    /* access modifiers changed from: private */
    public final void showControlsView(List<SelectionItem> list) {
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            this.controlViewsById.clear();
            createListView();
            createDropDown(list);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }

    private final SelectionItem findSelectionItem(StructureInfo structureInfo, List<SelectionItem> list) {
        Object obj;
        boolean z;
        Iterator it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            SelectionItem selectionItem = (SelectionItem) obj;
            if (!Intrinsics.areEqual((Object) selectionItem.getComponentName(), (Object) structureInfo.getComponentName()) || !Intrinsics.areEqual((Object) selectionItem.getStructure(), (Object) structureInfo.getStructure())) {
                z = false;
                continue;
            } else {
                z = true;
                continue;
            }
            if (z) {
                break;
            }
        }
        return (SelectionItem) obj;
    }

    private final void createListView() {
        LayoutInflater from = LayoutInflater.from(this.context);
        int i = C2013R$layout.controls_with_favorites;
        ViewGroup viewGroup = this.parent;
        String str = "parent";
        if (viewGroup != null) {
            from.inflate(i, viewGroup, true);
            ViewGroup viewGroup2 = this.parent;
            if (viewGroup2 != null) {
                View requireViewById = viewGroup2.requireViewById(C2011R$id.global_actions_controls_list);
                String str2 = "null cannot be cast to non-null type android.view.ViewGroup";
                if (requireViewById != null) {
                    ViewGroup viewGroup3 = (ViewGroup) requireViewById;
                    Intrinsics.checkExpressionValueIsNotNull(from, "inflater");
                    ViewGroup createRow = createRow(from, viewGroup3);
                    for (ControlInfo controlInfo : this.selectedStructure.getControls()) {
                        if (createRow.getChildCount() == 2) {
                            createRow = createRow(from, viewGroup3);
                        }
                        View inflate = from.inflate(C2013R$layout.controls_base_item, createRow, false);
                        if (inflate != null) {
                            ViewGroup viewGroup4 = (ViewGroup) inflate;
                            createRow.addView(viewGroup4);
                            Object obj = this.controlsController.get();
                            Intrinsics.checkExpressionValueIsNotNull(obj, "controlsController.get()");
                            ControlViewHolder controlViewHolder = new ControlViewHolder(viewGroup4, (ControlsController) obj, this.uiExecutor, this.bgExecutor);
                            ControlKey controlKey = new ControlKey(this.selectedStructure.getComponentName(), controlInfo.getControlId());
                            controlViewHolder.bindData((ControlWithState) MapsKt__MapsKt.getValue(this.controlsById, controlKey));
                            this.controlViewsById.put(controlKey, controlViewHolder);
                        } else {
                            throw new TypeCastException(str2);
                        }
                    }
                    if (this.selectedStructure.getControls().size() % 2 == 1) {
                        createRow.addView(new Space(this.context), new LayoutParams(0, 0, 1.0f));
                        return;
                    }
                    return;
                }
                throw new TypeCastException(str2);
            }
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0018, code lost:
        if (r0 != null) goto L_0x001d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final com.android.systemui.controls.controller.StructureInfo loadPreference(java.util.List<com.android.systemui.controls.controller.StructureInfo> r8) {
        /*
            r7 = this;
            boolean r0 = r8.isEmpty()
            if (r0 == 0) goto L_0x0009
            com.android.systemui.controls.controller.StructureInfo r7 = EMPTY_STRUCTURE
            return r7
        L_0x0009:
            android.content.SharedPreferences r0 = r7.sharedPreferences
            java.lang.String r1 = "controls_component"
            r2 = 0
            java.lang.String r0 = r0.getString(r1, r2)
            if (r0 == 0) goto L_0x001b
            android.content.ComponentName r0 = android.content.ComponentName.unflattenFromString(r0)
            if (r0 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            android.content.ComponentName r0 = EMPTY_COMPONENT
        L_0x001d:
            android.content.SharedPreferences r7 = r7.sharedPreferences
            java.lang.String r1 = "controls_structure"
            java.lang.String r3 = ""
            java.lang.String r7 = r7.getString(r1, r3)
            java.util.Iterator r1 = r8.iterator()
        L_0x002b:
            boolean r3 = r1.hasNext()
            r4 = 0
            if (r3 == 0) goto L_0x0053
            java.lang.Object r3 = r1.next()
            r5 = r3
            com.android.systemui.controls.controller.StructureInfo r5 = (com.android.systemui.controls.controller.StructureInfo) r5
            android.content.ComponentName r6 = r5.getComponentName()
            boolean r6 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r6)
            if (r6 == 0) goto L_0x004f
            java.lang.CharSequence r5 = r5.getStructure()
            boolean r5 = kotlin.jvm.internal.Intrinsics.areEqual(r7, r5)
            if (r5 == 0) goto L_0x004f
            r5 = 1
            goto L_0x0050
        L_0x004f:
            r5 = r4
        L_0x0050:
            if (r5 == 0) goto L_0x002b
            r2 = r3
        L_0x0053:
            com.android.systemui.controls.controller.StructureInfo r2 = (com.android.systemui.controls.controller.StructureInfo) r2
            if (r2 == 0) goto L_0x0058
            goto L_0x005f
        L_0x0058:
            java.lang.Object r7 = r8.get(r4)
            r2 = r7
            com.android.systemui.controls.controller.StructureInfo r2 = (com.android.systemui.controls.controller.StructureInfo) r2
        L_0x005f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.ControlsUiControllerImpl.loadPreference(java.util.List):com.android.systemui.controls.controller.StructureInfo");
    }

    private final void updatePreferences(StructureInfo structureInfo) {
        String str = "controls_structure";
        this.sharedPreferences.edit().putString("controls_component", structureInfo.getComponentName().flattenToString()).putString(str, structureInfo.getStructure().toString()).commit();
    }

    /* access modifiers changed from: private */
    public final void switchAppOrStructure(SelectionItem selectionItem) {
        boolean z;
        String str = "parent";
        if (Intrinsics.areEqual((Object) selectionItem, (Object) this.addControlsItem)) {
            Function1 launchSelectorActivityListener = launchSelectorActivityListener(this.context);
            ViewGroup viewGroup = this.parent;
            if (viewGroup != null) {
                launchSelectorActivityListener.invoke(viewGroup);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        } else {
            List<StructureInfo> list = this.allStructures;
            if (list != null) {
                for (StructureInfo structureInfo : list) {
                    if (!Intrinsics.areEqual((Object) structureInfo.getStructure(), (Object) selectionItem.getStructure()) || !Intrinsics.areEqual((Object) structureInfo.getComponentName(), (Object) selectionItem.getComponentName())) {
                        z = false;
                        continue;
                    } else {
                        z = true;
                        continue;
                    }
                    if (z) {
                        if (!Intrinsics.areEqual((Object) structureInfo, (Object) this.selectedStructure)) {
                            this.selectedStructure = structureInfo;
                            updatePreferences(structureInfo);
                            ControlsListingController controlsListingController2 = (ControlsListingController) this.controlsListingController.get();
                            ControlsListingCallback controlsListingCallback = this.listingCallback;
                            if (controlsListingCallback != null) {
                                controlsListingController2.removeCallback(controlsListingCallback);
                                ViewGroup viewGroup2 = this.parent;
                                if (viewGroup2 != null) {
                                    reload(viewGroup2);
                                    return;
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException(str);
                                    throw null;
                                }
                            } else {
                                Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
                                throw null;
                            }
                        } else {
                            return;
                        }
                    }
                }
                throw new NoSuchElementException("Collection contains no element matching the predicate.");
            }
            Intrinsics.throwUninitializedPropertyAccessException("allStructures");
            throw null;
        }
    }

    public void hide() {
        Log.d("ControlsUiController", "hide()");
        this.hidden = true;
        ListPopupWindow listPopupWindow = this.popup;
        if (listPopupWindow != null) {
            listPopupWindow.dismiss();
        }
        Dialog dialog = this.activeDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
        ((ControlsController) this.controlsController.get()).unsubscribe();
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            this.controlsById.clear();
            this.controlViewsById.clear();
            ControlsListingController controlsListingController2 = (ControlsListingController) this.controlsListingController.get();
            ControlsListingCallback controlsListingCallback = this.listingCallback;
            if (controlsListingCallback != null) {
                controlsListingController2.removeCallback(controlsListingCallback);
                RenderInfo.Companion.clearCache();
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }

    public void onRefreshState(ComponentName componentName, List<Control> list) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(list, "controls");
        String str = "ControlsUiController";
        Log.d(str, "onRefreshState()");
        for (Control control : list) {
            Map<ControlKey, ControlWithState> map = this.controlsById;
            String controlId = control.getControlId();
            String str2 = "c.getControlId()";
            Intrinsics.checkExpressionValueIsNotNull(controlId, str2);
            ControlWithState controlWithState = (ControlWithState) map.get(new ControlKey(componentName, controlId));
            if (controlWithState != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRefreshState() for id: ");
                sb.append(control.getControlId());
                Log.d(str, sb.toString());
                ControlWithState controlWithState2 = new ControlWithState(componentName, controlWithState.getCi(), control);
                String controlId2 = control.getControlId();
                Intrinsics.checkExpressionValueIsNotNull(controlId2, str2);
                ControlKey controlKey = new ControlKey(componentName, controlId2);
                this.controlsById.put(controlKey, controlWithState2);
                DelayableExecutor delayableExecutor = this.uiExecutor;
                C0817x27f1e8ba controlsUiControllerImpl$onRefreshState$$inlined$forEach$lambda$1 = new C0817x27f1e8ba(controlKey, controlWithState2, control, this, componentName);
                delayableExecutor.execute(controlsUiControllerImpl$onRefreshState$$inlined$forEach$lambda$1);
            }
        }
    }

    public void onActionResponse(ComponentName componentName, String str, int i) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        this.uiExecutor.execute(new ControlsUiControllerImpl$onActionResponse$1(this, new ControlKey(componentName, str), i));
    }

    private final ViewGroup createRow(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View inflate = layoutInflater.inflate(C2013R$layout.controls_row, viewGroup, false);
        if (inflate != null) {
            ViewGroup viewGroup2 = (ViewGroup) inflate;
            viewGroup.addView(viewGroup2);
            return viewGroup2;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
    }

    private final void createDropDown(List<SelectionItem> list) {
        for (SelectionItem selectionItem : list) {
            RenderInfo.Companion.registerComponentIcon(selectionItem.getComponentName(), selectionItem.getIcon());
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt___RangesKt.coerceAtLeast(MapsKt__MapsKt.mapCapacity(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10)), 16));
        for (Object next : list) {
            linkedHashMap.put(((SelectionItem) next).getComponentName(), next);
        }
        List<StructureInfo> list2 = this.allStructures;
        if (list2 != null) {
            ArrayList arrayList = new ArrayList();
            for (StructureInfo structureInfo : list2) {
                SelectionItem selectionItem2 = (SelectionItem) linkedHashMap.get(structureInfo.getComponentName());
                Object copy$default = selectionItem2 != null ? SelectionItem.copy$default(selectionItem2, null, structureInfo.getStructure(), null, null, 13, null) : null;
                if (copy$default != null) {
                    arrayList.add(copy$default);
                }
            }
            SelectionItem findSelectionItem = findSelectionItem(this.selectedStructure, arrayList);
            if (findSelectionItem == null) {
                findSelectionItem = (SelectionItem) list.get(0);
            }
            Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            T itemAdapter = new ItemAdapter(this.context, C2013R$layout.controls_spinner_item);
            itemAdapter.addAll(CollectionsKt___CollectionsKt.plus((Collection) arrayList, (Object) this.addControlsItem));
            ref$ObjectRef.element = itemAdapter;
            ViewGroup viewGroup = this.parent;
            String str = "parent";
            if (viewGroup != null) {
                TextView textView = (TextView) viewGroup.requireViewById(C2011R$id.app_or_structure_spinner);
                textView.setText(findSelectionItem.getTitle());
                Drawable background = textView.getBackground();
                if (background != null) {
                    Drawable drawable = ((LayerDrawable) background).getDrawable(1);
                    Context context2 = textView.getContext();
                    Intrinsics.checkExpressionValueIsNotNull(context2, "context");
                    drawable.setTint(context2.getResources().getColor(C2008R$color.control_spinner_dropdown, null));
                    ViewGroup viewGroup2 = this.parent;
                    if (viewGroup2 != null) {
                        ImageView imageView = (ImageView) viewGroup2.requireViewById(C2011R$id.app_icon);
                        imageView.setContentDescription(findSelectionItem.getTitle());
                        imageView.setImageDrawable(findSelectionItem.getIcon());
                        ViewGroup viewGroup3 = this.parent;
                        if (viewGroup3 != null) {
                            ViewGroup viewGroup4 = (ViewGroup) viewGroup3.requireViewById(C2011R$id.controls_header);
                            viewGroup4.setOnClickListener(new ControlsUiControllerImpl$createDropDown$4(this, viewGroup4, ref$ObjectRef));
                            return;
                        }
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        throw null;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
                throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
            }
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("allStructures");
        throw null;
    }
}
