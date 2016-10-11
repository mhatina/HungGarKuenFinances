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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.OneTimeOnly;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Periodic;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;

public class ProductsAdapter extends ArrayAdapter<Product> {

    public ProductsAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.layout_product, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product product = getItem(position);

        if (product != null) {
            viewHolder.name.setText(product.getName());
            viewHolder.price.setText(getContext().getString(R.string.currency, String.valueOf(product.getPrice())));
            if (product instanceof Periodic) {
                String perWeek = String.valueOf(((Periodic) product).getPerWeek());
                perWeek += getContext().getString(R.string.week);
                viewHolder.detail.setText(perWeek);
            } else {
                String stock = getContext().getString(R.string.in_stock, String.valueOf(((OneTimeOnly) product).getStock()));
                viewHolder.detail.setText(stock);
            }
        }

        return convertView;
    }

    @Override
    public void sort(Comparator<? super Product> comparator) {
        super.sort(comparator);
    }

    public class ViewHolder {
        public TextView name;
        public TextView price;
        public TextView detail;

        ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.product_layout_name);
            price = (TextView) view.findViewById(R.id.product_layout_price);
            detail = (TextView) view.findViewById(R.id.product_layout_detail);
        }
    }
}
