package androidx.slice.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceQuery;
import androidx.slice.widget.SliceView.OnSliceActionListener;

public class MessageView extends SliceChildView {
    private TextView mDetails;
    private ImageView mIcon;

    public int getMode() {
        return 2;
    }

    public void resetView() {
    }

    public MessageView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDetails = (TextView) findViewById(16908304);
        this.mIcon = (ImageView) findViewById(16908294);
    }

    public void setSliceItem(SliceContent sliceContent, boolean z, int i, int i2, OnSliceActionListener onSliceActionListener) {
        SliceItem sliceItem = sliceContent.getSliceItem();
        setSliceActionListener(onSliceActionListener);
        SliceItem findSubtype = SliceQuery.findSubtype(sliceItem, "image", "source");
        if (!(findSubtype == null || findSubtype.getIcon() == null)) {
            Drawable loadDrawable = findSubtype.getIcon().loadDrawable(getContext());
            if (loadDrawable != null) {
                int applyDimension = (int) TypedValue.applyDimension(1, 24.0f, getContext().getResources().getDisplayMetrics());
                Bitmap createBitmap = Bitmap.createBitmap(applyDimension, applyDimension, Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                loadDrawable.setBounds(0, 0, applyDimension, applyDimension);
                loadDrawable.draw(canvas);
                this.mIcon.setImageBitmap(SliceViewUtil.getCircularBitmap(createBitmap));
            }
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (SliceItem sliceItem2 : SliceQuery.findAll(sliceItem, "text")) {
            if (spannableStringBuilder.length() != 0) {
                spannableStringBuilder.append(10);
            }
            spannableStringBuilder.append(sliceItem2.getSanitizedText());
        }
        this.mDetails.setText(spannableStringBuilder.toString());
    }
}
