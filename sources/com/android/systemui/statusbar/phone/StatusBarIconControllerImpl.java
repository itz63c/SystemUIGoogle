package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.ViewGroup;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.phone.StatusBarIconController.IconManager;
import com.android.systemui.statusbar.phone.StatusBarIconList.Slot;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.MobileIconState;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy.WifiIconState;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class StatusBarIconControllerImpl extends StatusBarIconList implements Tunable, ConfigurationListener, Dumpable, Callbacks, StatusBarIconController {
    private Context mContext;
    private final ArraySet<String> mIconBlacklist = new ArraySet<>();
    private final ArrayList<IconManager> mIconGroups = new ArrayList<>();

    private void loadDimens() {
    }

    public StatusBarIconControllerImpl(Context context, CommandQueue commandQueue) {
        super(context.getResources().getStringArray(17236080));
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mContext = context;
        loadDimens();
        commandQueue.addCallback((Callbacks) this);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "icon_blacklist");
    }

    public void addIconGroup(IconManager iconManager) {
        this.mIconGroups.add(iconManager);
        ArrayList slots = getSlots();
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = (Slot) slots.get(i);
            List<StatusBarIconHolder> holderListInViewOrder = slot.getHolderListInViewOrder();
            boolean contains = this.mIconBlacklist.contains(slot.getName());
            for (StatusBarIconHolder statusBarIconHolder : holderListInViewOrder) {
                statusBarIconHolder.getTag();
                iconManager.onIconAdded(getViewIndex(getSlotIndex(slot.getName()), statusBarIconHolder.getTag()), slot.getName(), contains, statusBarIconHolder);
            }
        }
    }

    public void removeIconGroup(IconManager iconManager) {
        iconManager.destroy();
        this.mIconGroups.remove(iconManager);
    }

    public void onTuningChanged(String str, String str2) {
        if ("icon_blacklist".equals(str)) {
            this.mIconBlacklist.clear();
            this.mIconBlacklist.addAll(StatusBarIconController.getIconBlacklist(this.mContext, str2));
            ArrayList slots = getSlots();
            ArrayMap arrayMap = new ArrayMap();
            for (int size = slots.size() - 1; size >= 0; size--) {
                Slot slot = (Slot) slots.get(size);
                arrayMap.put(slot, slot.getHolderList());
                removeAllIconsForSlot(slot.getName());
            }
            for (int i = 0; i < slots.size(); i++) {
                Slot slot2 = (Slot) slots.get(i);
                List<StatusBarIconHolder> list = (List) arrayMap.get(slot2);
                if (list != null) {
                    for (StatusBarIconHolder icon : list) {
                        setIcon(getSlotIndex(slot2.getName()), icon);
                    }
                }
            }
        }
    }

    private void addSystemIcon(int i, StatusBarIconHolder statusBarIconHolder) {
        String slotName = getSlotName(i);
        this.mIconGroups.forEach(new Consumer(getViewIndex(i, statusBarIconHolder.getTag()), slotName, this.mIconBlacklist.contains(slotName), statusBarIconHolder) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ String f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ StatusBarIconHolder f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void accept(Object obj) {
                ((IconManager) obj).onIconAdded(this.f$0, this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public void setIcon(String str, int i, CharSequence charSequence) {
        int slotIndex = getSlotIndex(str);
        StatusBarIconHolder icon = getIcon(slotIndex, 0);
        if (icon == null) {
            StatusBarIcon statusBarIcon = new StatusBarIcon(UserHandle.SYSTEM, this.mContext.getPackageName(), Icon.createWithResource(this.mContext, i), 0, 0, charSequence);
            setIcon(slotIndex, StatusBarIconHolder.fromIcon(statusBarIcon));
            return;
        }
        icon.getIcon().icon = Icon.createWithResource(this.mContext, i);
        icon.getIcon().contentDescription = charSequence;
        handleSet(slotIndex, icon);
    }

    public void setSignalIcon(String str, WifiIconState wifiIconState) {
        int slotIndex = getSlotIndex(str);
        if (wifiIconState == null) {
            removeIcon(slotIndex, 0);
            return;
        }
        StatusBarIconHolder icon = getIcon(slotIndex, 0);
        if (icon == null) {
            setIcon(slotIndex, StatusBarIconHolder.fromWifiIconState(wifiIconState));
        } else {
            icon.setWifiState(wifiIconState);
            handleSet(slotIndex, icon);
        }
    }

    public void setMobileIcons(String str, List<MobileIconState> list) {
        Slot slot = getSlot(str);
        int slotIndex = getSlotIndex(str);
        Collections.reverse(list);
        for (MobileIconState mobileIconState : list) {
            StatusBarIconHolder holderForTag = slot.getHolderForTag(mobileIconState.subId);
            if (holderForTag == null) {
                setIcon(slotIndex, StatusBarIconHolder.fromMobileIconState(mobileIconState));
            } else {
                holderForTag.setMobileState(mobileIconState);
                handleSet(slotIndex, holderForTag);
            }
        }
    }

    public void setExternalIcon(String str) {
        this.mIconGroups.forEach(new Consumer(getViewIndex(getSlotIndex(str), 0), this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.status_bar_icon_drawing_size)) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                ((IconManager) obj).onIconExternal(this.f$0, this.f$1);
            }
        });
    }

    public void setIcon(String str, StatusBarIcon statusBarIcon) {
        setIcon(getSlotIndex(str), statusBarIcon);
    }

    private void setIcon(int i, StatusBarIcon statusBarIcon) {
        if (statusBarIcon == null) {
            removeAllIconsForSlot(getSlotName(i));
        } else {
            setIcon(i, StatusBarIconHolder.fromIcon(statusBarIcon));
        }
    }

    public void setIcon(int i, StatusBarIconHolder statusBarIconHolder) {
        boolean z = getIcon(i, statusBarIconHolder.getTag()) == null;
        super.setIcon(i, statusBarIconHolder);
        if (z) {
            addSystemIcon(i, statusBarIconHolder);
        } else {
            handleSet(i, statusBarIconHolder);
        }
    }

    public void setIconVisibility(String str, boolean z) {
        int slotIndex = getSlotIndex(str);
        StatusBarIconHolder icon = getIcon(slotIndex, 0);
        if (icon != null && icon.isVisible() != z) {
            icon.setVisible(z);
            handleSet(slotIndex, icon);
        }
    }

    public void removeIcon(String str) {
        removeAllIconsForSlot(str);
    }

    public void removeAllIconsForSlot(String str) {
        Slot slot = getSlot(str);
        if (slot.hasIconsInSlot()) {
            int slotIndex = getSlotIndex(str);
            for (StatusBarIconHolder statusBarIconHolder : slot.getHolderListInViewOrder()) {
                int viewIndex = getViewIndex(slotIndex, statusBarIconHolder.getTag());
                slot.removeForTag(statusBarIconHolder.getTag());
                this.mIconGroups.forEach(new Consumer(viewIndex) {
                    public final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        ((IconManager) obj).onRemoveIcon(this.f$0);
                    }
                });
            }
        }
    }

    public void removeIcon(int i, int i2) {
        if (getIcon(i, i2) != null) {
            super.removeIcon(i, i2);
            this.mIconGroups.forEach(new Consumer(getViewIndex(i, 0)) {
                public final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((IconManager) obj).onRemoveIcon(this.f$0);
                }
            });
        }
    }

    private void handleSet(int i, StatusBarIconHolder statusBarIconHolder) {
        this.mIconGroups.forEach(new Consumer(getViewIndex(i, statusBarIconHolder.getTag()), statusBarIconHolder) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ StatusBarIconHolder f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                ((IconManager) obj).onSetIconHolder(this.f$0, this.f$1);
            }
        });
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("StatusBarIconController state:");
        Iterator it = this.mIconGroups.iterator();
        while (it.hasNext()) {
            IconManager iconManager = (IconManager) it.next();
            if (iconManager.shouldLog()) {
                ViewGroup viewGroup = iconManager.mGroup;
                int childCount = viewGroup.getChildCount();
                StringBuilder sb = new StringBuilder();
                sb.append("  icon views: ");
                sb.append(childCount);
                printWriter.println(sb.toString());
                for (int i = 0; i < childCount; i++) {
                    StatusIconDisplayable statusIconDisplayable = (StatusIconDisplayable) viewGroup.getChildAt(i);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("    [");
                    sb2.append(i);
                    sb2.append("] icon=");
                    sb2.append(statusIconDisplayable);
                    printWriter.println(sb2.toString());
                }
            }
        }
        super.dump(printWriter);
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        Iterator it = this.mIconGroups.iterator();
        while (it.hasNext()) {
            IconManager iconManager = (IconManager) it.next();
            if (iconManager.isDemoable()) {
                iconManager.dispatchDemoCommand(str, bundle);
            }
        }
    }

    public void onDensityOrFontScaleChanged() {
        loadDimens();
    }
}
