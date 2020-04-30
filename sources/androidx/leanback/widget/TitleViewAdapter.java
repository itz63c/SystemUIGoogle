package androidx.leanback.widget;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.leanback.widget.SearchOrbView.Colors;

public abstract class TitleViewAdapter {

    public interface Provider {
        TitleViewAdapter getTitleViewAdapter();
    }

    public abstract View getSearchAffordanceView();

    public abstract void setAnimationEnabled(boolean z);

    public abstract void setBadgeDrawable(Drawable drawable);

    public abstract void setOnSearchClickedListener(OnClickListener onClickListener);

    public abstract void setSearchAffordanceColors(Colors colors);

    public abstract void setTitle(CharSequence charSequence);

    public abstract void updateComponentsVisibility(int i);
}
