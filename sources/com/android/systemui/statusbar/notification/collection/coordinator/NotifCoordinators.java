package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotifCoordinators implements Dumpable {
    private final List<Coordinator> mCoordinators = new ArrayList();
    private final List<NotifSection> mOrderedSections = new ArrayList();

    public NotifCoordinators(DumpManager dumpManager, FeatureFlags featureFlags, HeadsUpCoordinator headsUpCoordinator, KeyguardCoordinator keyguardCoordinator, RankingCoordinator rankingCoordinator, ForegroundCoordinator foregroundCoordinator, DeviceProvisionedCoordinator deviceProvisionedCoordinator, BubbleCoordinator bubbleCoordinator, PreparationCoordinator preparationCoordinator) {
        dumpManager.registerDumpable("NotifCoordinators", this);
        this.mCoordinators.add(new HideLocallyDismissedNotifsCoordinator());
        this.mCoordinators.add(keyguardCoordinator);
        this.mCoordinators.add(rankingCoordinator);
        this.mCoordinators.add(foregroundCoordinator);
        this.mCoordinators.add(deviceProvisionedCoordinator);
        this.mCoordinators.add(bubbleCoordinator);
        if (featureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mCoordinators.add(headsUpCoordinator);
            this.mCoordinators.add(preparationCoordinator);
        }
        for (Coordinator coordinator : this.mCoordinators) {
            if (coordinator.getSection() != null) {
                this.mOrderedSections.add(coordinator.getSection());
            }
        }
    }

    public void attach(NotifPipeline notifPipeline) {
        for (Coordinator attach : this.mCoordinators) {
            attach.attach(notifPipeline);
        }
        notifPipeline.setSections(this.mOrderedSections);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        printWriter.println();
        printWriter.println("NotifCoordinators:");
        Iterator it = this.mCoordinators.iterator();
        while (true) {
            str = "\t";
            if (!it.hasNext()) {
                break;
            }
            Coordinator coordinator = (Coordinator) it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(coordinator.getClass());
            printWriter.println(sb.toString());
        }
        for (NotifSection notifSection : this.mOrderedSections) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(notifSection.getName());
            printWriter.println(sb2.toString());
        }
    }
}
