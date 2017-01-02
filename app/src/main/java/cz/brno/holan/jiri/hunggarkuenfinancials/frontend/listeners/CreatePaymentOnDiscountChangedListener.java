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

import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

public class CreatePaymentOnDiscountChangedListener implements TextWatcher {
    private EditText price;
    private TextInputLayout discount;
    private boolean watcherOn = true;
    private float defaultPrice = -1;

    public CreatePaymentOnDiscountChangedListener(EditText price, TextInputLayout discount) {
        this.price = price;
        this.discount = discount;
    }

    public void setDefaultPrice(float defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        discount.setError(null);
        if (!CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().isWatcherOn()
                || charSequence.length() == 0)
            return;

        String str = charSequence.toString().replace("%", "0");
        float percent = 0;
        try {
            percent = (Float.valueOf(str) / 100f);
        } catch (NumberFormatException ex) {
            discount.setError("Incorrect discount");
        }
        if (percent > 1.0f) {
            percent = 1.0f;
            CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOn(false);
            discount.getEditText().setText("100.0%");
            CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOn(true);
        }
        float discount = 1 - percent;

        CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOn(false);
        if (defaultPrice != -1)
            price.setText(String.format("%.2f", defaultPrice * discount));
        CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOn(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
