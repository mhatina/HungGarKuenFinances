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

public class CreatePaymentOnPriceTextChangedListener implements TextWatcher {

    private final TextInputLayout price;
    private final EditText discount;
    private float defaultPrice = -1;

    public CreatePaymentOnPriceTextChangedListener(TextInputLayout price, EditText discount) {
        this.price = price;
        this.discount = discount;
    }

    void setDefaultPrice(float defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        this.price.setError(null);
        if (WatcherLock.getInstance().isWatcherOff())
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

        WatcherLock.getInstance().setWatcherOff(true);
        if (defaultPrice != -1)
            this.discount.setText(String.format(Locale.getDefault(), "%.2f%%", discount));
        WatcherLock.getInstance().setWatcherOff(false);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public static class WatcherLock {
        private final static WatcherLock instance = new WatcherLock();
        private boolean watcherOff = false;

        public static WatcherLock getInstance() {
            return instance;
        }

        public boolean isWatcherOff() {
            return watcherOff;
        }

        public void setWatcherOff(boolean watcherOff) {
            this.watcherOff = watcherOff;
        }
    }
}
