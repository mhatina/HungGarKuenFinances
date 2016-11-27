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
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

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
        setTitle(getString(R.string.edit_product_title));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageButton button = (ImageButton) findViewById(R.id.create_new_product_periodic);
        button.setOnClickListener(this);

        button = (ImageButton) findViewById(R.id.create_new_product_adult);
        button.setOnClickListener(this);

        button = (ImageButton) findViewById(R.id.create_new_product_youngster);
        button.setOnClickListener(this);

        button = (ImageButton) findViewById(R.id.create_new_product_junior);
        button.setOnClickListener(this);

        button = (ImageButton) findViewById(R.id.create_new_product_child);
        button.setOnClickListener(this);

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.create_new_product_detail_number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(7);
        numberPicker.setValue(2);

        Spinner validityPeriod = (Spinner) findViewById(R.id.create_new_product_validity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.validity_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        validityPeriod.setAdapter(adapter);
        validityPeriod.setSelection(2);
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public void onClick(View view) {
        ImageButton button = (ImageButton) view;
        TextView textView = (TextView) findViewById(R.id.create_new_product_detail);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.create_new_product_detail_number_picker);

        if (button.getColorFilter() == null) {
            if (button.getId() == R.id.create_new_product_periodic) {
                textView.setText("In stock");
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(1000);
            }

            button.setColorFilter(Color.rgb(200, 200, 200));
        } else {
            if (button.getId() == R.id.create_new_product_periodic) {
                textView.setText("Times a week");
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(7);
            }

            button.setColorFilter(null);
        }
    }
}
