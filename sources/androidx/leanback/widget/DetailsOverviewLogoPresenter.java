package androidx.leanback.widget;

public class DetailsOverviewLogoPresenter extends Presenter {

    public static class ViewHolder extends androidx.leanback.widget.Presenter.ViewHolder {
    }

    public abstract boolean isBoundToImage(ViewHolder viewHolder, DetailsOverviewRow detailsOverviewRow);

    public abstract void setContext(ViewHolder viewHolder, androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter.ViewHolder viewHolder2, FullWidthDetailsOverviewRowPresenter fullWidthDetailsOverviewRowPresenter);
}
