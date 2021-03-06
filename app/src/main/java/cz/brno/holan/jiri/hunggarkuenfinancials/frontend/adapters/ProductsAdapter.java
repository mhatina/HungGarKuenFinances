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
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Youngster;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.OneTimeOnly;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Periodic;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;

public class ProductsAdapter extends ArrayAdapter<Product> {

    public ProductsAdapter(Context context, List<Product> objects) {
        super(context, R.layout.layout_product, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
                perWeek += "/" + getContext().getString(R.string.week).toLowerCase();
                viewHolder.detail.setText(perWeek);
            } else {
                String stock = getContext().getString(R.string.in_stock, String.valueOf(((OneTimeOnly) product).getStock()));
                viewHolder.detail.setText(stock);
            }

            viewHolder.groups.removeAllViews();
            int group = Constant.ADULT_GROUP;
            int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                    convertView.getResources().getDisplayMetrics());
            for (int i = 0; i < Constant.NUMBER_OF_GROUPS; i++) {
                if ((product.getGroup() & group) != 0) {
                    ImageView image = new ImageView(convertView.getContext());
                    image.setImageResource(getGroupResource(group));

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimensionInDp, dimensionInDp);
                    image.setLayoutParams(layoutParams);

                    viewHolder.groups.addView(image);
                }

                group <<= 1;
            }
        }

        return convertView;
    }

    // TODO move to utils
    private int getGroupResource(int group) {
        switch (group) {
            case Constant.ADULT_GROUP:
                return Adult.ICON_PATH;
            case Constant.YOUNGSTER_GROUP:
                return Youngster.ICON_PATH;
            case Constant.JUNIOR_GROUP:
                return Junior.ICON_PATH;
            case Constant.CHILD_GROUP:
                return Child.ICON_PATH;
            default:
                return 0;
        }
    }

    @Override
    public void sort(@NonNull Comparator<? super Product> comparator) {
        super.sort(comparator);
    }

    public class ViewHolder {
        public final TextView name;
        public final TextView price;
        public final TextView detail;
        public final LinearLayout groups;

        ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.product_layout_name);
            price = (TextView) view.findViewById(R.id.product_layout_price);
            detail = (TextView) view.findViewById(R.id.product_layout_detail);
            groups = (LinearLayout) view.findViewById(R.id.product_layout_groups);
        }
    }
}
