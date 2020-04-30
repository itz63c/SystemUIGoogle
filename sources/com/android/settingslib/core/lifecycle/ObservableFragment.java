package com.android.settingslib.core.lifecycle;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleOwner;

public class ObservableFragment extends Fragment implements LifecycleOwner {
    private final Lifecycle mLifecycle = new Lifecycle(this);

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mLifecycle.onAttach(context);
    }

    public void onCreate(Bundle bundle) {
        this.mLifecycle.onCreate(bundle);
        this.mLifecycle.handleLifecycleEvent(Event.ON_CREATE);
        super.onCreate(bundle);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mLifecycle.onSaveInstanceState(bundle);
    }

    public void onStart() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_START);
        super.onStart();
    }

    public void onResume() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_RESUME);
        super.onResume();
    }

    public void onPause() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_PAUSE);
        super.onPause();
    }

    public void onStop() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_STOP);
        super.onStop();
    }

    public void onDestroy() {
        this.mLifecycle.handleLifecycleEvent(Event.ON_DESTROY);
        super.onDestroy();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        this.mLifecycle.onCreateOptionsMenu(menu, menuInflater);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        this.mLifecycle.onPrepareOptionsMenu(menu);
        super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean onOptionsItemSelected = this.mLifecycle.onOptionsItemSelected(menuItem);
        return !onOptionsItemSelected ? super.onOptionsItemSelected(menuItem) : onOptionsItemSelected;
    }
}
