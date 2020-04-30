package com.android.systemui.statusbar.notification.collection.listbuilder;

public class PipelineState {
    private int mState = 0;

    /* renamed from: is */
    public boolean mo15180is(int i) {
        return i == this.mState;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int i) {
        this.mState = i;
    }

    public void incrementTo(int i) {
        if (this.mState == i - 1) {
            this.mState = i;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot increment from state ");
        sb.append(this.mState);
        sb.append(" to state ");
        sb.append(i);
        throw new IllegalStateException(sb.toString());
    }

    public void requireState(int i) {
        if (i != this.mState) {
            StringBuilder sb = new StringBuilder();
            sb.append("Required state is <");
            sb.append(i);
            sb.append(" but actual state is ");
            sb.append(this.mState);
            throw new IllegalStateException(sb.toString());
        }
    }

    public void requireIsBefore(int i) {
        if (this.mState >= i) {
            StringBuilder sb = new StringBuilder();
            sb.append("Required state is <");
            sb.append(i);
            sb.append(" but actual state is ");
            sb.append(this.mState);
            throw new IllegalStateException(sb.toString());
        }
    }
}
