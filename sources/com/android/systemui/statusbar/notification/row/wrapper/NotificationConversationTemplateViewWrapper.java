package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.internal.widget.ConversationLayout;
import com.android.internal.widget.MessagingLinearLayout;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationConversationTemplateViewWrapper.kt */
public final class NotificationConversationTemplateViewWrapper extends NotificationTemplateViewWrapper {
    private View conversationBadge;
    private View conversationIcon;
    private final ConversationLayout conversationLayout;
    private View expandButton;
    private View expandButtonContainer;
    private MessagingLinearLayout messagingLinearLayout;
    private final int minHeightWithActions;

    public NotificationConversationTemplateViewWrapper(Context context, View view, ExpandableNotificationRow expandableNotificationRow) {
        Intrinsics.checkParameterIsNotNull(context, "ctx");
        Intrinsics.checkParameterIsNotNull(view, "view");
        Intrinsics.checkParameterIsNotNull(expandableNotificationRow, "row");
        super(context, view, expandableNotificationRow);
        this.conversationLayout = (ConversationLayout) view;
        this.minHeightWithActions = NotificationUtils.getFontScaledHeight(context, C2009R$dimen.notification_messaging_actions_min_height);
    }

    private final void resolveViews() {
        this.messagingLinearLayout = this.conversationLayout.getMessagingLinearLayout();
        this.conversationIcon = this.conversationLayout.requireViewById(16908878);
        this.conversationBadge = this.conversationLayout.requireViewById(16908879);
        this.expandButton = this.conversationLayout.requireViewById(16908930);
        View requireViewById = this.conversationLayout.requireViewById(16908932);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "conversationLayout.requi….expand_button_container)");
        this.expandButtonContainer = requireViewById;
    }

    public void onContentUpdated(ExpandableNotificationRow expandableNotificationRow) {
        Intrinsics.checkParameterIsNotNull(expandableNotificationRow, "row");
        resolveViews();
        super.onContentUpdated(expandableNotificationRow);
    }

    /* access modifiers changed from: protected */
    public void updateTransformedTypes() {
        super.updateTransformedTypes();
        MessagingLinearLayout messagingLinearLayout2 = this.messagingLinearLayout;
        if (messagingLinearLayout2 != null) {
            this.mTransformationHelper.addTransformedView(messagingLinearLayout2.getId(), messagingLinearLayout2);
        }
        View view = this.conversationIcon;
        if (view != null) {
            this.mTransformationHelper.addViewTransformingToSimilar(view.getId(), view);
        }
        View view2 = this.conversationBadge;
        if (view2 != null) {
            this.mTransformationHelper.addViewTransformingToSimilar(view2.getId(), view2);
        }
        View view3 = this.expandButton;
        if (view3 != null) {
            this.mTransformationHelper.addViewTransformingToSimilar(view3.getId(), view3);
        }
    }

    public void setRemoteInputVisible(boolean z) {
        this.conversationLayout.showHistoricMessages(z);
    }

    public void updateExpandability(boolean z, OnClickListener onClickListener) {
        this.conversationLayout.updateExpandability(z, onClickListener);
    }

    public boolean disallowSingleClick(float f, float f2) {
        View view = this.expandButtonContainer;
        String str = "expandButtonContainer";
        if (view != null) {
            if (view.getVisibility() == 0) {
                View view2 = this.expandButtonContainer;
                if (view2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                } else if (isOnView(view2, f, f2)) {
                    return true;
                }
            }
            return super.disallowSingleClick(f, f2);
        }
        Intrinsics.throwUninitializedPropertyAccessException(str);
        throw null;
    }

    public int getMinLayoutHeight() {
        View view = this.mActionsContainer;
        if (view != null) {
            Intrinsics.checkExpressionValueIsNotNull(view, "mActionsContainer");
            if (view.getVisibility() != 8) {
                return this.minHeightWithActions;
            }
        }
        return super.getMinLayoutHeight();
    }
}
