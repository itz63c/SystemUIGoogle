package com.android.systemui.statusbar.notification.stack;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.people.DataListener;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapter;
import com.android.systemui.statusbar.notification.people.PeopleHubViewBoundary;
import com.android.systemui.statusbar.notification.people.PersonViewModel;
import com.android.systemui.statusbar.notification.people.Subscription;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm.SectionProvider;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kotlin.sequences.Sequence;

public class NotificationSectionsManager implements SectionProvider {
    private final ActivityStarter mActivityStarter;
    private SectionHeaderView mAlertingHeader;
    private final ConfigurationController mConfigurationController;
    private final ConfigurationListener mConfigurationListener = new ConfigurationListener() {
        public void onLocaleListChanged() {
            NotificationSectionsManager notificationSectionsManager = NotificationSectionsManager.this;
            notificationSectionsManager.reinflateViews(LayoutInflater.from(notificationSectionsManager.mParent.getContext()));
        }
    };
    private SectionHeaderView mGentleHeader;
    /* access modifiers changed from: private */
    public boolean mInitialized = false;
    private final int mNumberOfSections;
    private OnClickListener mOnClearGentleNotifsClickListener;
    /* access modifiers changed from: private */
    public NotificationStackScrollLayout mParent;
    private Subscription mPeopleHubSubscription;
    /* access modifiers changed from: private */
    public PeopleHubView mPeopleHubView;
    private final PeopleHubViewAdapter mPeopleHubViewAdapter;
    private final PeopleHubViewBoundary mPeopleHubViewBoundary = new PeopleHubViewBoundary() {
        public void setVisible(boolean z) {
            if (NotificationSectionsManager.this.mPeopleHubVisible != z) {
                NotificationSectionsManager.this.mPeopleHubVisible = z;
                if (NotificationSectionsManager.this.mInitialized) {
                    NotificationSectionsManager.this.updateSectionBoundaries();
                }
            }
        }

        public View getAssociatedViewForClickAnimation() {
            return NotificationSectionsManager.this.mPeopleHubView;
        }

        public Sequence<DataListener<PersonViewModel>> getPersonViewAdapters() {
            return NotificationSectionsManager.this.mPeopleHubView.getPersonViewAdapters();
        }
    };
    /* access modifiers changed from: private */
    public boolean mPeopleHubVisible = false;
    private final NotificationSectionsFeatureManager mSectionsFeatureManager;
    private final StatusBarStateController mStatusBarStateController;

    NotificationSectionsManager(ActivityStarter activityStarter, StatusBarStateController statusBarStateController, ConfigurationController configurationController, PeopleHubViewAdapter peopleHubViewAdapter, NotificationSectionsFeatureManager notificationSectionsFeatureManager) {
        this.mActivityStarter = activityStarter;
        this.mStatusBarStateController = statusBarStateController;
        this.mConfigurationController = configurationController;
        this.mPeopleHubViewAdapter = peopleHubViewAdapter;
        this.mSectionsFeatureManager = notificationSectionsFeatureManager;
        this.mNumberOfSections = notificationSectionsFeatureManager.getNumberOfBuckets();
    }

    /* access modifiers changed from: 0000 */
    public NotificationSection[] createSectionsForBuckets() {
        int[] notificationBuckets = this.mSectionsFeatureManager.getNotificationBuckets();
        NotificationSection[] notificationSectionArr = new NotificationSection[notificationBuckets.length];
        for (int i = 0; i < notificationBuckets.length; i++) {
            notificationSectionArr[i] = new NotificationSection(this.mParent, notificationBuckets[i]);
        }
        return notificationSectionArr;
    }

    /* access modifiers changed from: 0000 */
    public void initialize(NotificationStackScrollLayout notificationStackScrollLayout, LayoutInflater layoutInflater) {
        if (!this.mInitialized) {
            this.mInitialized = true;
            this.mParent = notificationStackScrollLayout;
            reinflateViews(layoutInflater);
            this.mConfigurationController.addCallback(this.mConfigurationListener);
            return;
        }
        throw new IllegalStateException("NotificationSectionsManager already initialized");
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0031  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private <T extends com.android.systemui.statusbar.notification.row.ExpandableView> T reinflateView(T r4, android.view.LayoutInflater r5, int r6) {
        /*
            r3 = this;
            r0 = -1
            if (r4 == 0) goto L_0x0025
            android.view.ViewGroup r1 = r4.getTransientContainer()
            if (r1 == 0) goto L_0x0013
            android.view.ViewGroup r4 = r4.getTransientContainer()
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r1 = r3.mGentleHeader
            r4.removeView(r1)
            goto L_0x0025
        L_0x0013:
            android.view.ViewParent r1 = r4.getParent()
            if (r1 == 0) goto L_0x0025
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout r1 = r3.mParent
            int r1 = r1.indexOfChild(r4)
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout r2 = r3.mParent
            r2.removeView(r4)
            goto L_0x0026
        L_0x0025:
            r1 = r0
        L_0x0026:
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout r4 = r3.mParent
            r2 = 0
            android.view.View r4 = r5.inflate(r6, r4, r2)
            com.android.systemui.statusbar.notification.row.ExpandableView r4 = (com.android.systemui.statusbar.notification.row.ExpandableView) r4
            if (r1 == r0) goto L_0x0036
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout r3 = r3.mParent
            r3.addView(r4, r1)
        L_0x0036:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.reinflateView(com.android.systemui.statusbar.notification.row.ExpandableView, android.view.LayoutInflater, int):com.android.systemui.statusbar.notification.row.ExpandableView");
    }

    /* access modifiers changed from: 0000 */
    public void reinflateViews(LayoutInflater layoutInflater) {
        SectionHeaderView sectionHeaderView = (SectionHeaderView) reinflateView(this.mGentleHeader, layoutInflater, C2013R$layout.status_bar_notification_section_header);
        this.mGentleHeader = sectionHeaderView;
        sectionHeaderView.setHeaderText(C2017R$string.notification_section_header_gentle);
        this.mGentleHeader.setOnHeaderClickListener(new OnClickListener() {
            public final void onClick(View view) {
                NotificationSectionsManager.this.onGentleHeaderClick(view);
            }
        });
        this.mGentleHeader.setOnClearAllClickListener(new OnClickListener() {
            public final void onClick(View view) {
                NotificationSectionsManager.this.onClearGentleNotifsClick(view);
            }
        });
        SectionHeaderView sectionHeaderView2 = (SectionHeaderView) reinflateView(this.mAlertingHeader, layoutInflater, C2013R$layout.status_bar_notification_section_header);
        this.mAlertingHeader = sectionHeaderView2;
        sectionHeaderView2.setHeaderText(C2017R$string.notification_section_header_alerting);
        this.mAlertingHeader.setOnHeaderClickListener(new OnClickListener() {
            public final void onClick(View view) {
                NotificationSectionsManager.this.onGentleHeaderClick(view);
            }
        });
        Subscription subscription = this.mPeopleHubSubscription;
        if (subscription != null) {
            subscription.unsubscribe();
        }
        this.mPeopleHubView = (PeopleHubView) reinflateView(this.mPeopleHubView, layoutInflater, C2013R$layout.people_strip);
        this.mPeopleHubSubscription = this.mPeopleHubViewAdapter.bindView(this.mPeopleHubViewBoundary);
    }

    /* access modifiers changed from: 0000 */
    public void setOnClearGentleNotifsClickListener(OnClickListener onClickListener) {
        this.mOnClearGentleNotifsClickListener = onClickListener;
    }

    public boolean beginsSection(View view, View view2) {
        return view == this.mGentleHeader || view == this.mPeopleHubView || view == this.mAlertingHeader || !Objects.equals(getBucket(view), getBucket(view2));
    }

    private boolean isUsingMultipleSections() {
        return this.mNumberOfSections > 1;
    }

    private Integer getBucket(View view) {
        if (view == this.mGentleHeader) {
            return Integer.valueOf(3);
        }
        if (view == this.mPeopleHubView) {
            return Integer.valueOf(1);
        }
        if (view == this.mAlertingHeader) {
            return Integer.valueOf(2);
        }
        if (view instanceof ExpandableNotificationRow) {
            return Integer.valueOf(((ExpandableNotificationRow) view).getEntry().getBucket());
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void updateSectionBoundaries() {
        if (isUsingMultipleSections()) {
            boolean z = true;
            boolean z2 = this.mStatusBarStateController.getState() != 1;
            boolean isFilteringEnabled = this.mSectionsFeatureManager.isFilteringEnabled();
            int childCount = this.mParent.getChildCount();
            int i = -1;
            int i2 = -1;
            int i3 = -1;
            int i4 = 0;
            int i5 = -1;
            int i6 = -1;
            boolean z3 = false;
            int i7 = -1;
            for (int i8 = 0; i8 < childCount; i8++) {
                View childAt = this.mParent.getChildAt(i8);
                if (childAt == this.mPeopleHubView) {
                    i5 = i8;
                } else if (childAt == this.mAlertingHeader) {
                    i7 = i8;
                } else if (childAt == this.mGentleHeader) {
                    i6 = i8;
                } else if (!(childAt instanceof ExpandableNotificationRow)) {
                    continue;
                } else {
                    int bucket = ((ExpandableNotificationRow) childAt).getEntry().getBucket();
                    if (bucket != 0) {
                        if (bucket == 1) {
                            if (z2 && i == -1) {
                                int i9 = i5 != -1 ? i8 - 1 : i8;
                                if (i7 != -1) {
                                    i9--;
                                }
                                i = i9;
                                if (i6 != -1) {
                                    i--;
                                }
                            }
                            z3 = true;
                        } else if (bucket != 2) {
                            if (bucket != 3) {
                                throw new IllegalStateException("Cannot find section bucket for view");
                            } else if (z2 && i3 == -1) {
                                if (i6 != -1) {
                                    i3 = i8 - 1;
                                } else {
                                    i3 = i8;
                                    i4 = i3;
                                }
                            }
                        } else if (z2 && isFilteringEnabled && i2 == -1) {
                            i2 = i7 != -1 ? i8 - 1 : i8;
                            if (i6 != -1) {
                                i2--;
                            }
                        }
                    }
                    i4 = i8;
                }
            }
            if (z2 && isFilteringEnabled && this.mPeopleHubVisible && i == -1) {
                i = i2 != -1 ? i2 : i3 != -1 ? i3 : i4;
                if (i5 != -1 && i5 < i) {
                    i--;
                }
            }
            adjustHeaderVisibilityAndPosition(i3, this.mGentleHeader, i6);
            adjustHeaderVisibilityAndPosition(i2, this.mAlertingHeader, i7);
            adjustHeaderVisibilityAndPosition(i, this.mPeopleHubView, i5);
            this.mGentleHeader.setAreThereDismissableGentleNotifs(this.mParent.hasActiveClearableNotifications(2));
            PeopleHubView peopleHubView = this.mPeopleHubView;
            if (!z2 || !this.mPeopleHubVisible || z3) {
                z = false;
            }
            peopleHubView.setCanSwipe(z);
            if (i != i5) {
                this.mPeopleHubView.resetTranslation();
            }
        }
    }

    private void adjustHeaderVisibilityAndPosition(int i, StackScrollerDecorView stackScrollerDecorView, int i2) {
        if (i == -1) {
            if (i2 != -1) {
                this.mParent.removeView(stackScrollerDecorView);
            }
        } else if (i2 == -1) {
            if (stackScrollerDecorView.getTransientContainer() != null) {
                stackScrollerDecorView.getTransientContainer().removeTransientView(stackScrollerDecorView);
                stackScrollerDecorView.setTransientContainer(null);
            }
            stackScrollerDecorView.setContentVisible(true);
            this.mParent.addView(stackScrollerDecorView, i);
        } else {
            this.mParent.changeViewPosition(stackScrollerDecorView, i);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean updateFirstAndLastViewsForAllSections(NotificationSection[] notificationSectionArr, List<ActivatableNotificationView> list) {
        boolean z;
        boolean z2;
        if (notificationSectionArr.length <= 0 || list.size() <= 0) {
            for (NotificationSection notificationSection : notificationSectionArr) {
                notificationSection.setFirstVisibleChild(null);
                notificationSection.setLastVisibleChild(null);
            }
            return false;
        }
        ArrayList arrayList = new ArrayList();
        boolean z3 = false;
        for (NotificationSection notificationSection2 : notificationSectionArr) {
            int bucket = notificationSection2.getBucket();
            arrayList.clear();
            for (ActivatableNotificationView activatableNotificationView : list) {
                Integer bucket2 = getBucket(activatableNotificationView);
                if (bucket2 != null) {
                    if (bucket2.intValue() == bucket) {
                        arrayList.add(activatableNotificationView);
                    }
                    if (arrayList.size() >= 1) {
                        z2 = z3 | notificationSection2.setFirstVisibleChild((ActivatableNotificationView) arrayList.get(0));
                        z = notificationSection2.setLastVisibleChild((ActivatableNotificationView) arrayList.get(arrayList.size() - 1));
                    } else {
                        z2 = z3 | notificationSection2.setFirstVisibleChild(null);
                        z = notificationSection2.setLastVisibleChild(null);
                    }
                    z3 = z2 | z;
                } else {
                    throw new IllegalArgumentException("Cannot find section bucket for view");
                }
            }
        }
        return z3;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public ExpandableView getGentleHeaderView() {
        return this.mGentleHeader;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public ExpandableView getAlertingHeaderView() {
        return this.mAlertingHeader;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public ExpandableView getPeopleHeaderView() {
        return this.mPeopleHubView;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setPeopleHubVisible(boolean z) {
        this.mPeopleHubVisible = z;
    }

    /* access modifiers changed from: private */
    public void onGentleHeaderClick(View view) {
        this.mActivityStarter.startActivity(new Intent("android.settings.NOTIFICATION_SETTINGS"), true, true, 536870912);
    }

    /* access modifiers changed from: private */
    public void onClearGentleNotifsClick(View view) {
        OnClickListener onClickListener = this.mOnClearGentleNotifsClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    /* access modifiers changed from: 0000 */
    public void hidePeopleRow() {
        this.mPeopleHubVisible = false;
        updateSectionBoundaries();
    }
}
