package androidx.fragment.app;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.util.Preconditions;

public abstract class FragmentHostCallback<E> extends FragmentContainer {
    private final Activity mActivity;
    private final Context mContext;
    final FragmentManager mFragmentManager;
    private final Handler mHandler;

    /* access modifiers changed from: 0000 */
    public void onAttachFragment(Fragment fragment) {
    }

    public View onFindViewById(int i) {
        return null;
    }

    public abstract E onGetHost();

    public boolean onHasView() {
        return true;
    }

    public void onRequestPermissionsFromFragment(Fragment fragment, String[] strArr, int i) {
    }

    public boolean onShouldSaveFragmentState(Fragment fragment) {
        return true;
    }

    public void onSupportInvalidateOptionsMenu() {
    }

    FragmentHostCallback(FragmentActivity fragmentActivity) {
        this(fragmentActivity, fragmentActivity, new Handler(), 0);
    }

    FragmentHostCallback(Activity activity, Context context, Handler handler, int i) {
        this.mFragmentManager = new FragmentManagerImpl();
        this.mActivity = activity;
        Preconditions.checkNotNull(context, "context == null");
        this.mContext = context;
        Preconditions.checkNotNull(handler, "handler == null");
        this.mHandler = handler;
    }

    public LayoutInflater onGetLayoutInflater() {
        return LayoutInflater.from(this.mContext);
    }

    /* access modifiers changed from: 0000 */
    public Activity getActivity() {
        return this.mActivity;
    }

    /* access modifiers changed from: 0000 */
    public Context getContext() {
        return this.mContext;
    }

    /* access modifiers changed from: 0000 */
    public Handler getHandler() {
        return this.mHandler;
    }
}
