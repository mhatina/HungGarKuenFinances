/*
 * Copyright (C) 2016  Martin Hatina
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

public class TextInputLayout extends android.support.design.widget.TextInputLayout {
    private CharSequence mError;

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
                mError = null;
                text.setError(null);
                text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                return;
            }

            mError = error;
            Drawable icon = getResources().getDrawable(R.drawable.indicator_input_error, null);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            text.setError(error, icon);
            text.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        }
    }

    @Nullable
    @Override
    public CharSequence getError() {
        return mError;
    }


}
