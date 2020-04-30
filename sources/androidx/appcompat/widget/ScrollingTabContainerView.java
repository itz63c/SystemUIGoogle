package androidx.appcompat.widget;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.HorizontalScrollView;

public class ScrollingTabContainerView extends HorizontalScrollView implements OnItemSelectedListener {
    public abstract void setAllowCollapse(boolean z);
}
