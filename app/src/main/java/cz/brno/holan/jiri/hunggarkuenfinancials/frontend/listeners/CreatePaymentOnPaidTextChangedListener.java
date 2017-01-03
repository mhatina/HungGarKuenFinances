/*
 * Copyright (C) 2016  Martin Hatina
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners;

import android.renderscript.Float2;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CreatePaymentOnPaidTextChangedListener implements TextWatcher {
    private EditText owns;
    private EditText price;

    public CreatePaymentOnPaidTextChangedListener(EditText owns, EditText price) {
        this.owns = owns;
        this.price = price;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence.length() == 0)
            return;

        float payed = Float.valueOf(charSequence.toString());
        float price = Float.valueOf(this.price.getText().toString());

        owns.setText(String.format("%.2f", price - payed));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
