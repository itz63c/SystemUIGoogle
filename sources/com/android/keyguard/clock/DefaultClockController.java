package com.android.keyguard.clock;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint.Style;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.android.internal.colorextraction.ColorExtractor.GradientColors;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.ClockPlugin;
import java.util.TimeZone;

public class DefaultClockController implements ClockPlugin {
    private final SysuiColorExtractor mColorExtractor;
    private final LayoutInflater mLayoutInflater;
    private final ViewPreviewer mRenderer = new ViewPreviewer();
    private final Resources mResources;
    private TextView mTextDate;
    private TextView mTextTime;
    private View mView;

    public String getName() {
        return "default";
    }

    public View getView() {
        return null;
    }

    public void onTimeTick() {
    }

    public void onTimeZoneChanged(TimeZone timeZone) {
    }

    public void setColorPalette(boolean z, int[] iArr) {
    }

    public void setDarkAmount(float f) {
    }

    public void setStyle(Style style) {
    }

    public boolean shouldShowStatusArea() {
        return true;
    }

    public DefaultClockController(Resources resources, LayoutInflater layoutInflater, SysuiColorExtractor sysuiColorExtractor) {
        this.mResources = resources;
        this.mLayoutInflater = layoutInflater;
        this.mColorExtractor = sysuiColorExtractor;
    }

    private void createViews() {
        View inflate = this.mLayoutInflater.inflate(C2013R$layout.default_clock_preview, null);
        this.mView = inflate;
        this.mTextTime = (TextView) inflate.findViewById(C2011R$id.time);
        this.mTextDate = (TextView) this.mView.findViewById(C2011R$id.date);
    }

    public void onDestroyView() {
        this.mView = null;
        this.mTextTime = null;
        this.mTextDate = null;
    }

    public String getTitle() {
        return this.mResources.getString(C2017R$string.clock_title_default);
    }

    public Bitmap getThumbnail() {
        return BitmapFactory.decodeResource(this.mResources, C2010R$drawable.default_thumbnail);
    }

    public Bitmap getPreview(int i, int i2) {
        View bigClockView = getBigClockView();
        setDarkAmount(1.0f);
        setTextColor(-1);
        GradientColors colors = this.mColorExtractor.getColors(2);
        setColorPalette(colors.supportsDarkText(), colors.getColorPalette());
        onTimeTick();
        return this.mRenderer.createPreview(bigClockView, i, i2);
    }

    public View getBigClockView() {
        if (this.mView == null) {
            createViews();
        }
        return this.mView;
    }

    public int getPreferredY(int i) {
        return i / 2;
    }

    public void setTextColor(int i) {
        this.mTextTime.setTextColor(i);
        this.mTextDate.setTextColor(i);
    }
}
