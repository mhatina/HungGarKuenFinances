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

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

public class CreatePaymentOnPriceTextChangedListener implements TextWatcher {

    TextInputLayout price;
    private EditText discount;
    private float defaultPrice = -1;
    private boolean watcherOn = true;

    public CreatePaymentOnPriceTextChangedListener(TextInputLayout price, EditText discount) {
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
        this.price.setError(null);
        if (!WatcherLock.getInstance().isWatcherOn())
            return;

        float price = 0;
        float discount = 0;

        if (charSequence.length() != 0) {
            try {
                price = Float.valueOf(charSequence.toString());
            } catch (NumberFormatException ex) {
                this.price.setError(this.price.getResources().getString(R.string.incorrect_price));
            }
            discount = (1 - (price / defaultPrice)) * 100;
        }

        WatcherLock.getInstance().setWatcherOn(false);
        if (defaultPrice != -1)
            this.discount.setText(String.format("%.2f%%", discount));
        WatcherLock.getInstance().setWatcherOn(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public static class WatcherLock {
        private static WatcherLock instance = new WatcherLock();
        private boolean watcherOn = true;

        public static WatcherLock getInstance() {
            return instance;
        }

        public boolean isWatcherOn() {
            return watcherOn;
        }

        public void setWatcherOn(boolean watcherOn) {
            this.watcherOn = watcherOn;
        }
    }
}
