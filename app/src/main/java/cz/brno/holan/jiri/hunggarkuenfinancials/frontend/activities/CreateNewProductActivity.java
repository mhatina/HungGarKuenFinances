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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.OneTimeOnly;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Periodic;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

public class CreateNewProductActivity extends CreateNewEntityActivity implements ImageButton.OnClickListener {

    public CreateNewProductActivity() {
        super(R.layout.layout_product_new);
    }

    @Override
    public void init() {
        setTitle(getString(R.string.new_product_title));
    }

    @Override
    public void initForEdit() {
        TextInputLayout name = (TextInputLayout) findViewById(R.id.create_new_product_name);
        TextInputLayout valid_for = (TextInputLayout) findViewById(R.id.create_new_product_valid_time);
        TextInputLayout price = (TextInputLayout) findViewById(R.id.create_new_product_price);
        TextInputLayout note = (TextInputLayout) findViewById(R.id.create_new_note);
        Spinner validity = (Spinner) findViewById(R.id.create_new_product_validity);
        NumberPicker detail = (NumberPicker) findViewById(R.id.create_new_product_detail_number_picker);
        ImageButton button = (ImageButton) findViewById(R.id.create_new_product_periodic);
        Product product = ProductManager.getInstance().findProduct(getIntent().getLongExtra(Constant.EDIT_ENTITY, 0));

        setTitle(getString(R.string.edit_product_title));

        name.getEditText().setText(product.getName());
        if (product.getValidTime() != -1)
            valid_for.getEditText().setText(String.valueOf(product.getValidTime()));
        price.getEditText().setText(String.valueOf(product.getPrice()));
        note.getEditText().setText(product.getNote());
        toggleButton(button, product.getClass().equals(OneTimeOnly.class));

        button = (ImageButton) findViewById(R.id.create_new_product_adult);
        toggleButton(button, (product.getGroup() & Constant.ADULT_GROUP) > 0);

        button = (ImageButton) findViewById(R.id.create_new_product_youngster);
        toggleButton(button, (product.getGroup() & Constant.YOUNGSTER_GROUP) > 0);

        button = (ImageButton) findViewById(R.id.create_new_product_junior);
        toggleButton(button, (product.getGroup() & Constant.JUNIOR_GROUP) > 0);

        button = (ImageButton) findViewById(R.id.create_new_product_child);
        toggleButton(button, (product.getGroup() & Constant.CHILD_GROUP) > 0);

        if (product.getClass() == Periodic.class)
            detail.setValue(((Periodic) product).getPerWeek());
        else
            detail.setValue(((OneTimeOnly) product).getStock());

        validity.setSelection(product.getValidGroup());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOnClickListener(R.id.create_new_product_periodic);
        setOnClickListener(R.id.create_new_product_adult);
        setOnClickListener(R.id.create_new_product_youngster);
        setOnClickListener(R.id.create_new_product_junior);
        setOnClickListener(R.id.create_new_product_child);

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.create_new_product_detail_number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(7);
        numberPicker.setValue(2);

        Spinner validityPeriod = (Spinner) findViewById(R.id.create_new_product_validity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.validity_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        validityPeriod.setAdapter(adapter);
        validityPeriod.setSelection(Constant.MONTH_SELECTION);
    }

    private void setOnClickListener(int resource) {
        ImageButton button = (ImageButton) findViewById(resource);
        button.setOnClickListener(this);
    }

    @Override
    public boolean save() {
        Product product;
        ProductManager manager = ProductManager.getInstance();

        TextInputLayout name_layout = (TextInputLayout) findViewById(R.id.create_new_product_name);
        TextInputLayout valid_for_layout = (TextInputLayout) findViewById(R.id.create_new_product_valid_time);
        TextInputLayout price_layout = (TextInputLayout) findViewById(R.id.create_new_product_price);
        TextInputLayout note_layout = (TextInputLayout) findViewById(R.id.create_new_note);
        Spinner validity_spinner = (Spinner) findViewById(R.id.create_new_product_validity);
        NumberPicker detail_picker = (NumberPicker) findViewById(R.id.create_new_product_detail_number_picker);
        ImageButton button = (ImageButton) findViewById(R.id.create_new_product_periodic);
        Class<?> classType = button.getColorFilter() == null ? Periodic.class : OneTimeOnly.class;

        String name = verifyName(name_layout);
        int price = verifyPrice(price_layout);

        if (name == null || price == -1) {
            return false;
        } else if (getIntent().hasExtra(Constant.EDIT_ENTITY)) {
            product = manager.findProduct(getIntent().getLongExtra(Constant.EDIT_ENTITY, 0));

            if (product.getClass().equals(classType)) {
                Product newProduct = manager.createProduct(classType, name, price, detail_picker.getValue());
                manager.replaceProduct(product, newProduct);
                product = newProduct;
            } else {
                product.setName(name);
                product.setPrice(price);
            }

            if (valid_for_layout.getEditText().getText().length() != 0) {
                long valid_for = Long.valueOf(valid_for_layout.getEditText().getText().toString());
                product.setValidTime(valid_for);
            } else {
                product.setValidTime(-1);
            }

            button = (ImageButton) findViewById(R.id.create_new_product_adult);
            product.toggleGroup(Constant.ADULT_GROUP, button.getColorFilter() == null);
            button = (ImageButton) findViewById(R.id.create_new_product_youngster);
            product.toggleGroup(Constant.YOUNGSTER_GROUP, button.getColorFilter() == null);
            button = (ImageButton) findViewById(R.id.create_new_product_junior);
            product.toggleGroup(Constant.JUNIOR_GROUP, button.getColorFilter() == null);
            button = (ImageButton) findViewById(R.id.create_new_product_child);
            product.toggleGroup(Constant.CHILD_GROUP, button.getColorFilter() == null);

            product.setValidGroup(validity_spinner.getSelectedItemPosition());
            product.setDetail(detail_picker.getValue());
            product.setNote(note_layout.getEditText().getText().toString());

            manager.update(product);
        } else {
            product = manager.createProduct(classType, name, price, detail_picker.getValue());

            if (valid_for_layout.getEditText().getText().length() != 0) {
                long valid_for = Long.valueOf(valid_for_layout.getEditText().getText().toString());
                product.setValidTime(valid_for);
            } else {
                product.setValidTime(-1);
            }

            button = (ImageButton) findViewById(R.id.create_new_product_adult);
            product.toggleGroup(Constant.ADULT_GROUP, button.getColorFilter() == null);
            button = (ImageButton) findViewById(R.id.create_new_product_youngster);
            product.toggleGroup(Constant.YOUNGSTER_GROUP, button.getColorFilter() == null);
            button = (ImageButton) findViewById(R.id.create_new_product_junior);
            product.toggleGroup(Constant.JUNIOR_GROUP, button.getColorFilter() == null);
            button = (ImageButton) findViewById(R.id.create_new_product_child);
            product.toggleGroup(Constant.CHILD_GROUP, button.getColorFilter() == null);

            product.setValidGroup(validity_spinner.getSelectedItemPosition());
            product.setDetail(detail_picker.getValue());
            product.setNote(note_layout.getEditText().getText().toString());

            manager.addProduct(product);
        }

        setResult(0);
        finish();

        return false;
    }

    public String verifyName(TextInputLayout layout) {
        EditText text = layout.getEditText();
        if (text != null && text.getText().length() != 0)
            return capitalize(text.getText().toString());
        else
            layout.setError(getString(R.string.required_error));
        return null;
    }

    public int verifyPrice(TextInputLayout layout) {
        EditText text = layout.getEditText();
        if (text != null && text.getText().length() != 0) {
            int price = Integer.valueOf(text.getText().toString());
            if (price >= 0)
                return price;
        } else
            layout.setError(getString(R.string.required_error));
        return -1;
    }

    private String capitalize(String str) {
        String capitalized = str.substring(0, 1).toUpperCase();
        capitalized += str.substring(1);

        return capitalized;
    }

    @Override
    public void onClick(View view) {
        ImageButton button = (ImageButton) view;

        toggleButton(button, button.getColorFilter() != null);
    }

    private void toggleButton(ImageButton button, boolean toggle) {
        TextView textView = (TextView) findViewById(R.id.create_new_product_detail);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.create_new_product_detail_number_picker);

        if (toggle) {
            if (button.getId() == R.id.create_new_product_periodic) {
                textView.setText("Times a week");
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(7);
            }

            button.setColorFilter(null);
        } else {
            if (button.getId() == R.id.create_new_product_periodic) {
                textView.setText("In stock");
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(1000);
            }

            button.setColorFilter(Color.rgb(200, 200, 200));
        }
    }
}
