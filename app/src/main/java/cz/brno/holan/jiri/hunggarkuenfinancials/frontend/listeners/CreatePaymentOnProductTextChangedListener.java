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
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

public class CreatePaymentOnProductTextChangedListener implements TextWatcher {
    private TextInputLayout inputLayout;
    private EditText price;
    private Button dateButton;
    private boolean watcherOn = true;
    private CharSequence oldSequence = "";
    private CreatePaymentOnPriceTextChangedListener priceListener;
    private CreatePaymentOnDiscountChangedListener  discountListener;

    public CreatePaymentOnProductTextChangedListener(TextInputLayout inputLayout,
                                                     EditText price,
                                                     Button dateButton,
                                                     CreatePaymentOnPriceTextChangedListener priceListener,
                                                     CreatePaymentOnDiscountChangedListener discountListener) {
        this.inputLayout = inputLayout;
        this.price = price;
        this.dateButton = dateButton;
        this.priceListener = priceListener;
        this.discountListener = discountListener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        int oldLength = oldSequence.length();
        if (!watcherOn)
            return;

        oldSequence = charSequence.toString();
        String str = charSequence.toString();

        // this is true only when deleting letters
        if (charSequence.length() <= oldLength || str.lastIndexOf(",") == str.length() - 1) {
            inputLayout.setError(null);
            return;
        }

        Product product = null;
        List<Product> products = null;

        if (str.length() != 0)
            products = ProductManager.getInstance().getProducts(str);


        if (products != null && products.size() != 0) {
            product = products.get(0);
            inputLayout.setError(null);
        } else
            inputLayout.setError("No such product");

        if (product != null) {
            String textToSet = product.getName();

            watcherOn = false;
            inputLayout.getEditText().setText(textToSet);
            dateButton.setText(getValidUntilStr((int) product.getValidTime(), product.getValidGroup()));
            priceListener.setDefaultPrice(product.getPrice());
            discountListener.setDefaultPrice(product.getPrice());
            price.setText(String.valueOf(product.getPrice()));

            if (start + 1 < textToSet.length())
                inputLayout.getEditText().setSelection(start + 1, textToSet.length());
            else
                inputLayout.getEditText().setSelection(textToSet.length());
            watcherOn = true;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private String getValidUntilStr(int time, int group) {
        if (time == -1)
            return "Not specified";

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());

        switch (group) {
            case Constant.DAY_SELECTION:
                calendar.add(Calendar.DATE, time);
                break;
            case Constant.WEEK_SELECTION:
                calendar.add(Calendar.DATE, time);
                break;
            case Constant.MONTH_SELECTION:
                calendar.add(Calendar.MONTH, time);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                break;
            case Constant.YEAR_SELECTION:
                calendar.add(Calendar.YEAR, time);
                calendar.set(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DATE, -1);
        }

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(calendar.getTime());
    }
}
