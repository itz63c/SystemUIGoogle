package com.android.systemui.statusbar.notification;

import android.util.Pools.SimplePool;
import android.view.View;
import com.android.internal.widget.IMessagingLayout;
import com.android.internal.widget.MessagingGroup;
import com.android.internal.widget.MessagingImageMessage;
import com.android.internal.widget.MessagingLinearLayout;
import com.android.internal.widget.MessagingLinearLayout.LayoutParams;
import com.android.internal.widget.MessagingPropertyAnimator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.TransformState.TransformInfo;
import java.util.ArrayList;
import java.util.HashMap;

public class MessagingLayoutTransformState extends TransformState {
    private static SimplePool<MessagingLayoutTransformState> sInstancePool = new SimplePool<>(40);
    private HashMap<MessagingGroup, MessagingGroup> mGroupMap = new HashMap<>();
    private MessagingLinearLayout mMessageContainer;
    private IMessagingLayout mMessagingLayout;
    private float mRelativeTranslationOffset;

    public static MessagingLayoutTransformState obtain() {
        MessagingLayoutTransformState messagingLayoutTransformState = (MessagingLayoutTransformState) sInstancePool.acquire();
        if (messagingLayoutTransformState != null) {
            return messagingLayoutTransformState;
        }
        return new MessagingLayoutTransformState();
    }

    public void initFrom(View view, TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        MessagingLinearLayout messagingLinearLayout = this.mTransformedView;
        if (messagingLinearLayout instanceof MessagingLinearLayout) {
            MessagingLinearLayout messagingLinearLayout2 = messagingLinearLayout;
            this.mMessageContainer = messagingLinearLayout2;
            this.mMessagingLayout = messagingLinearLayout2.getMessagingLayout();
            this.mRelativeTranslationOffset = view.getContext().getResources().getDisplayMetrics().density * 8.0f;
        }
    }

    public boolean transformViewTo(TransformState transformState, float f) {
        if (!(transformState instanceof MessagingLayoutTransformState)) {
            return super.transformViewTo(transformState, f);
        }
        transformViewInternal((MessagingLayoutTransformState) transformState, f, true);
        return true;
    }

    public void transformViewFrom(TransformState transformState, float f) {
        if (transformState instanceof MessagingLayoutTransformState) {
            transformViewInternal((MessagingLayoutTransformState) transformState, f, false);
        } else {
            super.transformViewFrom(transformState, f);
        }
    }

    private void transformViewInternal(MessagingLayoutTransformState messagingLayoutTransformState, float f, boolean z) {
        float f2;
        float f3;
        float f4;
        ensureVisible();
        ArrayList filterHiddenGroups = filterHiddenGroups(this.mMessagingLayout.getMessagingGroups());
        HashMap findPairs = findPairs(filterHiddenGroups, filterHiddenGroups(messagingLayoutTransformState.mMessagingLayout.getMessagingGroups()));
        MessagingGroup messagingGroup = null;
        float f5 = 0.0f;
        for (int size = filterHiddenGroups.size() - 1; size >= 0; size--) {
            MessagingGroup messagingGroup2 = (MessagingGroup) filterHiddenGroups.get(size);
            MessagingGroup messagingGroup3 = (MessagingGroup) findPairs.get(messagingGroup2);
            if (!isGone(messagingGroup2)) {
                if (messagingGroup3 != null) {
                    transformGroups(messagingGroup2, messagingGroup3, f, z);
                    if (messagingGroup == null) {
                        if (z) {
                            f5 = messagingGroup3.getAvatar().getTranslationY() - ((float) (messagingGroup2.getTop() - messagingGroup3.getTop()));
                        } else {
                            f5 = messagingGroup2.getAvatar().getTranslationY();
                        }
                        messagingGroup = messagingGroup2;
                    }
                } else {
                    if (messagingGroup != null) {
                        adaptGroupAppear(messagingGroup2, f, f5, z);
                        float top = ((float) messagingGroup2.getTop()) + f5;
                        if (!this.mTransformInfo.isAnimating()) {
                            float f6 = ((float) (-messagingGroup2.getHeight())) * 0.5f;
                            f4 = top - f6;
                            f3 = Math.abs(f6);
                        } else {
                            float f7 = ((float) (-messagingGroup2.getHeight())) * 0.75f;
                            f4 = top - f7;
                            f3 = Math.abs(f7) + ((float) messagingGroup2.getTop());
                        }
                        f2 = Math.max(0.0f, Math.min(1.0f, f4 / f3));
                        if (z) {
                            f2 = 1.0f - f2;
                        }
                    } else {
                        f2 = f;
                    }
                    if (z) {
                        disappear(messagingGroup2, f2);
                    } else {
                        appear(messagingGroup2, f2);
                    }
                }
            }
        }
    }

    private void appear(MessagingGroup messagingGroup, float f) {
        MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
        for (int i = 0; i < messageContainer.getChildCount(); i++) {
            View childAt = messageContainer.getChildAt(i);
            if (!isGone(childAt)) {
                appear(childAt, f);
                setClippingDeactivated(childAt, true);
            }
        }
        appear(messagingGroup.getAvatar(), f);
        appear(messagingGroup.getSenderView(), f);
        appear((View) messagingGroup.getIsolatedMessage(), f);
        setClippingDeactivated(messagingGroup.getSenderView(), true);
        setClippingDeactivated(messagingGroup.getAvatar(), true);
    }

    private void adaptGroupAppear(MessagingGroup messagingGroup, float f, float f2, boolean z) {
        float f3;
        if (z) {
            f3 = f * this.mRelativeTranslationOffset;
        } else {
            f3 = (1.0f - f) * this.mRelativeTranslationOffset;
        }
        if (messagingGroup.getSenderView().getVisibility() != 8) {
            f3 *= 0.5f;
        }
        messagingGroup.getMessageContainer().setTranslationY(f3);
        messagingGroup.getSenderView().setTranslationY(f3);
        messagingGroup.setTranslationY(f2 * 0.9f);
    }

    private void disappear(MessagingGroup messagingGroup, float f) {
        MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
        for (int i = 0; i < messageContainer.getChildCount(); i++) {
            View childAt = messageContainer.getChildAt(i);
            if (!isGone(childAt)) {
                disappear(childAt, f);
                setClippingDeactivated(childAt, true);
            }
        }
        disappear(messagingGroup.getAvatar(), f);
        disappear(messagingGroup.getSenderView(), f);
        disappear((View) messagingGroup.getIsolatedMessage(), f);
        setClippingDeactivated(messagingGroup.getSenderView(), true);
        setClippingDeactivated(messagingGroup.getAvatar(), true);
    }

    private void appear(View view, float f) {
        if (view != null && view.getVisibility() != 8) {
            TransformState createFrom = TransformState.createFrom(view, this.mTransformInfo);
            createFrom.appear(f, null);
            createFrom.recycle();
        }
    }

    private void disappear(View view, float f) {
        if (view != null && view.getVisibility() != 8) {
            TransformState createFrom = TransformState.createFrom(view, this.mTransformInfo);
            createFrom.disappear(f, null);
            createFrom.recycle();
        }
    }

    private ArrayList<MessagingGroup> filterHiddenGroups(ArrayList<MessagingGroup> arrayList) {
        ArrayList<MessagingGroup> arrayList2 = new ArrayList<>(arrayList);
        int i = 0;
        while (i < arrayList2.size()) {
            if (isGone((MessagingGroup) arrayList2.get(i))) {
                arrayList2.remove(i);
                i--;
            }
            i++;
        }
        return arrayList2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void transformGroups(com.android.internal.widget.MessagingGroup r20, com.android.internal.widget.MessagingGroup r21, float r22, boolean r23) {
        /*
            r19 = this;
            r7 = r19
            com.android.internal.widget.MessagingImageMessage r0 = r21.getIsolatedMessage()
            r9 = 1
            if (r0 != 0) goto L_0x0013
            com.android.systemui.statusbar.notification.TransformState$TransformInfo r0 = r7.mTransformInfo
            boolean r0 = r0.isAnimating()
            if (r0 != 0) goto L_0x0013
            r10 = r9
            goto L_0x0014
        L_0x0013:
            r10 = 0
        L_0x0014:
            android.view.View r3 = r20.getSenderView()
            android.view.View r4 = r21.getSenderView()
            r5 = 1
            r0 = r19
            r1 = r22
            r2 = r23
            r6 = r10
            r0.transformView(r1, r2, r3, r4, r5, r6)
            android.view.View r3 = r20.getAvatar()
            android.view.View r4 = r21.getAvatar()
            r0.transformView(r1, r2, r3, r4, r5, r6)
            java.util.List r11 = r20.getMessages()
            java.util.List r12 = r21.getMessages()
            r13 = 0
            r0 = r22
            r15 = r13
            r14 = 0
        L_0x003f:
            int r1 = r11.size()
            if (r14 >= r1) goto L_0x0110
            int r1 = r11.size()
            int r1 = r1 - r9
            int r1 = r1 - r14
            java.lang.Object r1 = r11.get(r1)
            com.android.internal.widget.MessagingMessage r1 = (com.android.internal.widget.MessagingMessage) r1
            android.view.View r6 = r1.getView()
            boolean r1 = r7.isGone(r6)
            if (r1 == 0) goto L_0x005f
            r1 = r20
            goto L_0x010c
        L_0x005f:
            int r1 = r12.size()
            int r1 = r1 - r9
            int r1 = r1 - r14
            r2 = 0
            if (r1 < 0) goto L_0x007b
            java.lang.Object r1 = r12.get(r1)
            com.android.internal.widget.MessagingMessage r1 = (com.android.internal.widget.MessagingMessage) r1
            android.view.View r1 = r1.getView()
            boolean r3 = r7.isGone(r1)
            if (r3 == 0) goto L_0x0079
            goto L_0x007b
        L_0x0079:
            r5 = r1
            goto L_0x007c
        L_0x007b:
            r5 = r2
        L_0x007c:
            if (r5 != 0) goto L_0x00a1
            int r1 = (r15 > r13 ? 1 : (r15 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x00a1
            int r0 = r6.getTop()
            int r1 = r6.getHeight()
            int r0 = r0 + r1
            float r0 = (float) r0
            float r0 = r0 + r15
            int r1 = r6.getHeight()
            float r1 = (float) r1
            float r0 = r0 / r1
            r1 = 1065353216(0x3f800000, float:1.0)
            float r0 = java.lang.Math.min(r1, r0)
            float r0 = java.lang.Math.max(r13, r0)
            if (r23 == 0) goto L_0x00a1
            float r0 = r1 - r0
        L_0x00a1:
            r16 = r0
            r17 = 0
            r0 = r19
            r1 = r16
            r2 = r23
            r3 = r6
            r4 = r5
            r8 = r5
            r5 = r17
            r18 = r6
            r6 = r10
            r0.transformView(r1, r2, r3, r4, r5, r6)
            com.android.internal.widget.MessagingImageMessage r0 = r21.getIsolatedMessage()
            if (r0 != r8) goto L_0x00be
            r0 = r9
            goto L_0x00bf
        L_0x00be:
            r0 = 0
        L_0x00bf:
            int r1 = (r16 > r13 ? 1 : (r16 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x00d1
            if (r0 != 0) goto L_0x00cb
            boolean r1 = r21.isSingleLine()
            if (r1 == 0) goto L_0x00d1
        L_0x00cb:
            r1 = r20
            r1.setClippingDisabled(r9)
            goto L_0x00d3
        L_0x00d1:
            r1 = r20
        L_0x00d3:
            if (r8 != 0) goto L_0x00de
            r2 = r18
            r2.setTranslationY(r15)
            r7.setClippingDeactivated(r2, r9)
            goto L_0x010a
        L_0x00de:
            r2 = r18
            com.android.internal.widget.MessagingImageMessage r3 = r20.getIsolatedMessage()
            if (r3 == r2) goto L_0x010a
            if (r0 == 0) goto L_0x00e9
            goto L_0x010a
        L_0x00e9:
            if (r23 == 0) goto L_0x0106
            int r0 = r2.getTop()
            int r2 = r20.getTop()
            int r0 = r0 + r2
            int r2 = r8.getTop()
            int r0 = r0 - r2
            int r2 = r8.getTop()
            int r0 = r0 - r2
            float r0 = (float) r0
            float r2 = r8.getTranslationY()
            float r15 = r2 - r0
            goto L_0x010a
        L_0x0106:
            float r15 = r2.getTranslationY()
        L_0x010a:
            r0 = r16
        L_0x010c:
            int r14 = r14 + 1
            goto L_0x003f
        L_0x0110:
            r1 = r20
            r20.updateClipRect()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.MessagingLayoutTransformState.transformGroups(com.android.internal.widget.MessagingGroup, com.android.internal.widget.MessagingGroup, float, boolean):void");
    }

    private void transformView(float f, boolean z, View view, View view2, boolean z2, boolean z3) {
        TransformState createFrom = TransformState.createFrom(view, this.mTransformInfo);
        if (z3) {
            createFrom.setDefaultInterpolator(Interpolators.LINEAR);
        }
        createFrom.setIsSameAsAnyView(z2 && !isGone(view2));
        if (z) {
            if (view2 != null) {
                TransformState createFrom2 = TransformState.createFrom(view2, this.mTransformInfo);
                if (!isGone(view2)) {
                    createFrom.transformViewTo(createFrom2, f);
                } else {
                    if (!isGone(view)) {
                        createFrom.disappear(f, null);
                    }
                    createFrom.transformViewVerticalTo(createFrom2, f);
                }
                createFrom2.recycle();
            } else {
                createFrom.disappear(f, null);
            }
        } else if (view2 != null) {
            TransformState createFrom3 = TransformState.createFrom(view2, this.mTransformInfo);
            if (!isGone(view2)) {
                createFrom.transformViewFrom(createFrom3, f);
            } else {
                if (!isGone(view)) {
                    createFrom.appear(f, null);
                }
                createFrom.transformViewVerticalFrom(createFrom3, f);
            }
            createFrom3.recycle();
        } else {
            createFrom.appear(f, null);
        }
        createFrom.recycle();
    }

    private HashMap<MessagingGroup, MessagingGroup> findPairs(ArrayList<MessagingGroup> arrayList, ArrayList<MessagingGroup> arrayList2) {
        this.mGroupMap.clear();
        int i = Integer.MAX_VALUE;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            MessagingGroup messagingGroup = (MessagingGroup) arrayList.get(size);
            MessagingGroup messagingGroup2 = null;
            int i2 = 0;
            for (int min = Math.min(arrayList2.size(), i) - 1; min >= 0; min--) {
                MessagingGroup messagingGroup3 = (MessagingGroup) arrayList2.get(min);
                int calculateGroupCompatibility = messagingGroup.calculateGroupCompatibility(messagingGroup3);
                if (calculateGroupCompatibility > i2) {
                    i = min;
                    messagingGroup2 = messagingGroup3;
                    i2 = calculateGroupCompatibility;
                }
            }
            if (messagingGroup2 != null) {
                this.mGroupMap.put(messagingGroup, messagingGroup2);
            }
        }
        return this.mGroupMap;
    }

    private boolean isGone(View view) {
        if (view == null || view.getVisibility() == 8) {
            return true;
        }
        LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof LayoutParams) || !layoutParams.hide) {
            return false;
        }
        return true;
    }

    public void setVisible(boolean z, boolean z2) {
        super.setVisible(z, z2);
        resetTransformedView();
        ArrayList messagingGroups = this.mMessagingLayout.getMessagingGroups();
        for (int i = 0; i < messagingGroups.size(); i++) {
            MessagingGroup messagingGroup = (MessagingGroup) messagingGroups.get(i);
            if (!isGone(messagingGroup)) {
                MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
                for (int i2 = 0; i2 < messageContainer.getChildCount(); i2++) {
                    setVisible(messageContainer.getChildAt(i2), z, z2);
                }
                setVisible(messagingGroup.getAvatar(), z, z2);
                setVisible(messagingGroup.getSenderView(), z, z2);
                MessagingImageMessage isolatedMessage = messagingGroup.getIsolatedMessage();
                if (isolatedMessage != null) {
                    setVisible(isolatedMessage, z, z2);
                }
            }
        }
    }

    private void setVisible(View view, boolean z, boolean z2) {
        if (!isGone(view) && !MessagingPropertyAnimator.isAnimatingAlpha(view)) {
            TransformState createFrom = TransformState.createFrom(view, this.mTransformInfo);
            createFrom.setVisible(z, z2);
            createFrom.recycle();
        }
    }

    /* access modifiers changed from: protected */
    public void resetTransformedView() {
        super.resetTransformedView();
        ArrayList messagingGroups = this.mMessagingLayout.getMessagingGroups();
        for (int i = 0; i < messagingGroups.size(); i++) {
            MessagingGroup messagingGroup = (MessagingGroup) messagingGroups.get(i);
            if (!isGone(messagingGroup)) {
                MessagingLinearLayout messageContainer = messagingGroup.getMessageContainer();
                for (int i2 = 0; i2 < messageContainer.getChildCount(); i2++) {
                    View childAt = messageContainer.getChildAt(i2);
                    if (!isGone(childAt)) {
                        resetTransformedView(childAt);
                        setClippingDeactivated(childAt, false);
                    }
                }
                resetTransformedView(messagingGroup.getAvatar());
                resetTransformedView(messagingGroup.getSenderView());
                MessagingImageMessage isolatedMessage = messagingGroup.getIsolatedMessage();
                if (isolatedMessage != null) {
                    resetTransformedView(isolatedMessage);
                }
                setClippingDeactivated(messagingGroup.getAvatar(), false);
                setClippingDeactivated(messagingGroup.getSenderView(), false);
                messagingGroup.setTranslationY(0.0f);
                messagingGroup.getMessageContainer().setTranslationY(0.0f);
                messagingGroup.getSenderView().setTranslationY(0.0f);
            }
            messagingGroup.setClippingDisabled(false);
            messagingGroup.updateClipRect();
        }
    }

    public void prepareFadeIn() {
        super.prepareFadeIn();
        setVisible(true, false);
    }

    private void resetTransformedView(View view) {
        TransformState createFrom = TransformState.createFrom(view, this.mTransformInfo);
        createFrom.resetTransformedView();
        createFrom.recycle();
    }

    /* access modifiers changed from: protected */
    public void reset() {
        super.reset();
        this.mMessageContainer = null;
        this.mMessagingLayout = null;
    }

    public void recycle() {
        super.recycle();
        this.mGroupMap.clear();
        sInstancePool.release(this);
    }
}
