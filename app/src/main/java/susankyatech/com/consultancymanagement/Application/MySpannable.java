package susankyatech.com.consultancymanagement.Application;

import android.graphics.Color;
import android.text.TextPaint;
import android.view.View;

public class MySpannable extends android.text.style.ClickableSpan {

    private boolean isUnderline = false;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.parseColor("#343434"));

    }

    @Override
    public void onClick(View widget) {

    }
}
