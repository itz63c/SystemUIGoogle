package com.android.systemui.controls.management;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.service.controls.Control;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.controller.ControlInfo;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.p004ui.RenderInfo;
import com.android.systemui.controls.p004ui.RenderInfo.Companion;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.util.LifecycleActivity;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsRequestDialog.kt */
public final class ControlsRequestDialog extends LifecycleActivity implements OnClickListener, OnCancelListener {
    private final BroadcastDispatcher broadcastDispatcher;
    private final ControlsRequestDialog$callback$1 callback = new ControlsRequestDialog$callback$1();
    private ComponentName component;
    private Control control;
    /* access modifiers changed from: private */
    public final ControlsController controller;
    private final ControlsListingController controlsListingController;
    private final ControlsRequestDialog$currentUserTracker$1 currentUserTracker = new ControlsRequestDialog$currentUserTracker$1(this, this.broadcastDispatcher);
    private Dialog dialog;

    public ControlsRequestDialog(ControlsController controlsController, BroadcastDispatcher broadcastDispatcher2, ControlsListingController controlsListingController2) {
        Intrinsics.checkParameterIsNotNull(controlsController, "controller");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkParameterIsNotNull(controlsListingController2, "controlsListingController");
        this.controller = controlsController;
        this.broadcastDispatcher = broadcastDispatcher2;
        this.controlsListingController = controlsListingController2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String str = "ControlsRequestDialog";
        if (!this.controller.getAvailable()) {
            Log.w(str, "Quick Controls not available for this user ");
            finish();
        }
        this.currentUserTracker.startTracking();
        this.controlsListingController.addCallback(this.callback);
        int intExtra = getIntent().getIntExtra("android.intent.extra.USER_ID", -10000);
        int currentUserId = this.controller.getCurrentUserId();
        if (intExtra != currentUserId) {
            StringBuilder sb = new StringBuilder();
            sb.append("Current user (");
            sb.append(currentUserId);
            sb.append(") different from request user (");
            sb.append(intExtra);
            sb.append(')');
            Log.w(str, sb.toString());
            finish();
        }
        ComponentName componentName = (ComponentName) getIntent().getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        if (componentName != null) {
            this.component = componentName;
            Control parcelableExtra = getIntent().getParcelableExtra("android.service.controls.extra.CONTROL");
            if (parcelableExtra != null) {
                this.control = parcelableExtra;
                return;
            }
            Log.e(str, "Request did not contain control");
            finish();
            return;
        }
        Log.e(str, "Request did not contain componentName");
        finish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        CharSequence verifyComponentAndGetLabel = verifyComponentAndGetLabel();
        String str = "ControlsRequestDialog";
        if (verifyComponentAndGetLabel == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("The component specified (");
            ComponentName componentName = this.component;
            if (componentName != null) {
                sb.append(componentName.flattenToString());
                sb.append(' ');
                sb.append("is not a valid ControlsProviderService");
                Log.e(str, sb.toString());
                finish();
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("component");
            throw null;
        }
        if (isCurrentFavorite()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("The control ");
            Control control2 = this.control;
            if (control2 != null) {
                sb2.append(control2.getTitle());
                sb2.append(" is already a favorite");
                Log.w(str, sb2.toString());
                finish();
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
        }
        Dialog createDialog = createDialog(verifyComponentAndGetLabel);
        this.dialog = createDialog;
        if (createDialog != null) {
            createDialog.show();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Dialog dialog2 = this.dialog;
        if (dialog2 != null) {
            dialog2.dismiss();
        }
        this.currentUserTracker.stopTracking();
        this.controlsListingController.removeCallback(this.callback);
        super.onDestroy();
    }

    private final CharSequence verifyComponentAndGetLabel() {
        ControlsListingController controlsListingController2 = this.controlsListingController;
        ComponentName componentName = this.component;
        if (componentName != null) {
            return controlsListingController2.getAppLabel(componentName);
        }
        Intrinsics.throwUninitializedPropertyAccessException("component");
        throw null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0065 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean isCurrentFavorite() {
        /*
            r7 = this;
            com.android.systemui.controls.controller.ControlsController r0 = r7.controller
            android.content.ComponentName r1 = r7.component
            r2 = 0
            if (r1 == 0) goto L_0x0066
            java.util.List r0 = r0.getFavoritesForComponent(r1)
            boolean r1 = r0 instanceof java.util.Collection
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0019
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x0019
        L_0x0017:
            r3 = r4
            goto L_0x0065
        L_0x0019:
            java.util.Iterator r0 = r0.iterator()
        L_0x001d:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0017
            java.lang.Object r1 = r0.next()
            com.android.systemui.controls.controller.StructureInfo r1 = (com.android.systemui.controls.controller.StructureInfo) r1
            java.util.List r1 = r1.getControls()
            boolean r5 = r1 instanceof java.util.Collection
            if (r5 == 0) goto L_0x0039
            boolean r5 = r1.isEmpty()
            if (r5 == 0) goto L_0x0039
        L_0x0037:
            r1 = r4
            goto L_0x0063
        L_0x0039:
            java.util.Iterator r1 = r1.iterator()
        L_0x003d:
            boolean r5 = r1.hasNext()
            if (r5 == 0) goto L_0x0037
            java.lang.Object r5 = r1.next()
            com.android.systemui.controls.controller.ControlInfo r5 = (com.android.systemui.controls.controller.ControlInfo) r5
            java.lang.String r5 = r5.getControlId()
            android.service.controls.Control r6 = r7.control
            if (r6 == 0) goto L_0x005d
            java.lang.String r6 = r6.getControlId()
            boolean r5 = kotlin.jvm.internal.Intrinsics.areEqual(r5, r6)
            if (r5 == 0) goto L_0x003d
            r1 = r3
            goto L_0x0063
        L_0x005d:
            java.lang.String r7 = "control"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r7)
            throw r2
        L_0x0063:
            if (r1 == 0) goto L_0x001d
        L_0x0065:
            return r3
        L_0x0066:
            java.lang.String r7 = "component"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r7)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.management.ControlsRequestDialog.isCurrentFavorite():boolean");
    }

    public final Dialog createDialog(CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(charSequence, "label");
        Companion companion = RenderInfo.Companion;
        ComponentName componentName = this.component;
        if (componentName != null) {
            Control control2 = this.control;
            String str = "control";
            if (control2 != null) {
                RenderInfo lookup$default = Companion.lookup$default(companion, this, componentName, control2.getDeviceType(), true, 0, 16, null);
                View inflate = LayoutInflater.from(this).inflate(C2013R$layout.controls_dialog, null);
                ImageView imageView = (ImageView) inflate.requireViewById(C2011R$id.icon);
                imageView.setImageDrawable(lookup$default.getIcon());
                Context context = imageView.getContext();
                String str2 = "context";
                Intrinsics.checkExpressionValueIsNotNull(context, str2);
                Resources resources = context.getResources();
                int foreground = lookup$default.getForeground();
                Context context2 = imageView.getContext();
                Intrinsics.checkExpressionValueIsNotNull(context2, str2);
                imageView.setImageTintList(resources.getColorStateList(foreground, context2.getTheme()));
                View requireViewById = inflate.requireViewById(C2011R$id.title);
                Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById<TextView>(R.id.title)");
                TextView textView = (TextView) requireViewById;
                Control control3 = this.control;
                if (control3 != null) {
                    textView.setText(control3.getTitle());
                    View requireViewById2 = inflate.requireViewById(C2011R$id.subtitle);
                    Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById<TextView>(R.id.subtitle)");
                    TextView textView2 = (TextView) requireViewById2;
                    Control control4 = this.control;
                    if (control4 != null) {
                        textView2.setText(control4.getSubtitle());
                        View requireViewById3 = inflate.requireViewById(C2011R$id.control);
                        Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "requireViewById<View>(R.id.control)");
                        requireViewById3.setElevation(inflate.getResources().getFloat(C2009R$dimen.control_card_elevation));
                        AlertDialog create = new Builder(this).setTitle(getString(C2017R$string.controls_dialog_title)).setMessage(getString(C2017R$string.controls_dialog_message, new Object[]{charSequence})).setPositiveButton(C2017R$string.controls_dialog_ok, this).setNegativeButton(17039360, this).setOnCancelListener(this).setView(inflate).create();
                        SystemUIDialog.registerDismissListener(create);
                        create.setCanceledOnTouchOutside(true);
                        Intrinsics.checkExpressionValueIsNotNull(create, "dialog");
                        return create;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("component");
        throw null;
    }

    public void onCancel(DialogInterface dialogInterface) {
        finish();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            ControlsController controlsController = this.controller;
            ComponentName componentName = getComponentName();
            Intrinsics.checkExpressionValueIsNotNull(componentName, "componentName");
            Control control2 = this.control;
            String str = "control";
            if (control2 != null) {
                CharSequence structure = control2.getStructure();
                if (structure == null) {
                    structure = "";
                }
                Control control3 = this.control;
                if (control3 != null) {
                    String controlId = control3.getControlId();
                    Intrinsics.checkExpressionValueIsNotNull(controlId, "control.controlId");
                    Control control4 = this.control;
                    if (control4 != null) {
                        CharSequence title = control4.getTitle();
                        Intrinsics.checkExpressionValueIsNotNull(title, "control.title");
                        Control control5 = this.control;
                        if (control5 != null) {
                            CharSequence subtitle = control5.getSubtitle();
                            Intrinsics.checkExpressionValueIsNotNull(subtitle, "control.subtitle");
                            Control control6 = this.control;
                            if (control6 != null) {
                                controlsController.addFavorite(componentName, structure, new ControlInfo(controlId, title, subtitle, control6.getDeviceType()));
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
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        }
        finish();
    }
}
