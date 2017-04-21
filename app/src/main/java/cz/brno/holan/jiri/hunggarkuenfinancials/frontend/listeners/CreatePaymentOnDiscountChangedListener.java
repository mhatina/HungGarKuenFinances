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

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Locale;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

import static cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.CreateNewEntityActivity.setEditTextContent;

public class CreatePaymentOnDiscountChangedListener implements TextWatcher {
    private final EditText price;
    private final TextInputLayout discount;
    private float defaultPrice = -1;

    public CreatePaymentOnDiscountChangedListener(EditText price, TextInputLayout discount) {
        this.price = price;
        this.discount = discount;
    }

    void setDefaultPrice(float defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        discount.setError(null);
        if (CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().isWatcherOff()
                || charSequence.length() == 0)
            return;

        String str = charSequence.toString().replace("%", "0");
        float percent = 0;
        try {
            percent = (Float.valueOf(str) / 100f);
        } catch (NumberFormatException ex) {
            discount.setError(discount.getResources().getString(R.string.discount_error));
        }
        if (percent > 1.0f) {
            percent = 1.0f;
            CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOff(true);
            setEditTextContent(discount, "100.0%");
            CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOff(false);
        }
        float discount = 1 - percent;

        CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOff(true);
        if (defaultPrice != -1)
            price.setText(String.format(Locale.getDefault(), "%.2f", defaultPrice * discount));
        CreatePaymentOnPriceTextChangedListener.WatcherLock.getInstance().setWatcherOff(false);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
