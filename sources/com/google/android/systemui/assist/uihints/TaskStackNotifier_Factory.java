package com.google.android.systemui.assist.uihints;

import dagger.internal.Factory;

public final class TaskStackNotifier_Factory implements Factory<TaskStackNotifier> {
    private static final TaskStackNotifier_Factory INSTANCE = new TaskStackNotifier_Factory();

    public TaskStackNotifier get() {
        return provideInstance();
    }

    public static TaskStackNotifier provideInstance() {
        return new TaskStackNotifier();
    }

    public static TaskStackNotifier_Factory create() {
        return INSTANCE;
    }
}
