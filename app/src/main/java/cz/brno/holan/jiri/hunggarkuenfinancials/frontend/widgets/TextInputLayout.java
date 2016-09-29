package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 26/09/16.
 */
public class TextInputLayout extends android.support.design.widget.TextInputLayout {
    public TextInputLayout(Context context) {
        super(context);
    }

    public TextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setError(CharSequence error) {
        EditText text = getEditText();
        if (text != null) {
            if (error == null) {
                text.setError(null);
                text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                return;
            }

            Drawable icon = getResources().getDrawable(R.drawable.indicator_input_error);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            text.setError(error, icon);
            text.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        }
    }
}
