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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Locale;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

import static cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.CreateNewEntityActivity.setEditTextContent;

public class CreatePaymentOnPaidTextChangedListener implements TextWatcher {
    private final TextInputLayout owns;
    private final EditText price;

    public CreatePaymentOnPaidTextChangedListener(TextInputLayout owns, EditText price) {
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
        float result = price - payed;

        if (result < 0) {
            owns.setHint(owns.getContext().getResources().getString(R.string.payment_change));
            result *= -1;
        } else
            owns.setHint(owns.getContext().getResources().getString(R.string.payment_owns));
        setEditTextContent(owns, String.format(Locale.getDefault(), "%.2f", result));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
