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


import android.os.Bundle;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

public class CreateNewProductActivity extends CreateNewEntityActivity {

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
    }

    @Override
    public boolean save() {
        return false;
    }
}
